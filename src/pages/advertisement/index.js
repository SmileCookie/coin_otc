import React from 'react';
import { Link } from 'react-router-dom';
import cookie from 'js-cookie';
import moment from 'moment';
import { withRouter } from 'react-router';
import { connect } from 'react-redux';
import { FormattedMessage, injectIntl } from 'react-intl';
import Pages from "../../components/page";
import ReactModal from '../../components/popBox';
import Confirm from '../../components/confirm';
import '../../assets/style/advertisement/index.less';
import '../../assets/style/modal/index.css';
import { PAGEINDEX, PAGESIZE, USERID, DATA_TIME_FORMAT, DATA_TIEM_EN, DOMAIN_VIP } from '../../conf';
import { separator, optPop, getCoinNum } from '../../utils'
import { Styles, ThemeFactory } from "../../components/transition";
import SelectHistory from '../../components/selectHistory';
import { pageClick } from '../../redux/module/session';

const BigNumber = require('big.js');
// 获取模型数据
import {
    getFindCoinName,
    getAdvertiseList,
    downFramAd,
    upFramAd,
    hideAd,
    getAdvertiseFlag,
    isBusiness
} from './index.model.js';
import axios from "axios";

//coinList: state.account.detail,
@connect(
    state => ({
        userInfor: state.session.userInfor,
        coinData: state.session.coinData
    }),
    {
        pageClick
    }
)
class Advertisement extends React.Component {
    constructor(props) {
        super(props);
        // 输入框延迟执行
        this.inputDelay = 0;
        // 从 localStorage 中获取 筛选项
        let adStorage = {};
        //0 即 TYPE_NAVIGATENEXT 正常进入的页面（非刷新、非重定向等）
        //1 即 TYPE_RELOAD 通过 window.location.reload() 刷新的页面
        //2 即 TYPE_BACK_FORWARD 通过浏览器的前进后退按钮进入的页面（历史记录）
        //255 即 TYPE_UNDEFINED 非以上方式进入的页面
        const performance = window.performance || window.msPerformance || window.webkitPerformance;
        const { type } = (performance && performance.navigation) || '';
        if (type === 1) {
            adStorage = JSON.parse(window.localStorage.getItem('adStorage')) || {};
        } else {
            window.localStorage.removeItem('adStorage');
        }

        this.state = {
            modalHTML: '', // 去登录
            bordeBlue: false, // 输入搜索框的是不是onFocus
            filterVal: '', // 输入搜索框的内容
            btnStus: 0, // 是显示清空数据还是显示搜索的图标
            loading: true, // 加载动画
            pageIndex: PAGEINDEX, // 当前页
            pageSize: PAGESIZE, // 每页多少条
            count: 0,   // 总页数
            currencyList: [], // 币种
            tradeTypeList: [
                {
                    key: '',
                    value: '全部'
                },
                {
                    key: '0',
                    value: '购买'
                },
                {
                    key: '1',
                    value: '出售'
                },
            ],// 广告类型
            currency: '', //币种的选择
            tadeType: '', // 广告类型的选择
            adStatus: '', // 广告状态
            tableList: [],// table 的数据
            downConfirm: false, // 下架和隐藏的confirm
            confirmType: 1, // 下架是1 隐藏是2
            customerInfo: false,// 去用户信息的页面
            identityConfirm: false, // 身份认证
            isBusinessModalOpen: false, // 去身份商家身份页面
            second: false,
            id: '',// 广告ID
            customerDetail: '',//用户信息的校验
            ...adStorage,
        };
    }

    componentWillMount() {
        !USERID && (window.location.href = '/bw/login');
    }

    componentDidMount() {
        //防止子页面滚动之后返回
        this.props.pageClick(0);
        // 请求初始化数据
        const { intl } = this.props;
        getFindCoinName().then(async coinRes => {
            let currencyList = coinRes;
            currencyList.unshift({ fundsType: '', coinName: intl.formatMessage({ id: "全部" }) });
            this.setState({ currencyList });
            await this.getTableData();
        });
    }

    componentWillUnmount() {
        document.getElementsByClassName('scrollarea-content')[0].style.marginTop = '0px';
        window.localStorage.removeItem('adStorage');
    }

    /**
     * @desc  获取 table 数据
     * @param indexPage 当前页
     * @param page 每页多少条
     */
    getTableData = (indexPage, page) => {
        const { filterVal, pageIndex, pageSize, currency, tadeType, adStatus } = this.state;
        let index = indexPage || pageIndex;
        let size = page || pageSize;
        getAdvertiseList(this, filterVal.trim(), adStatus, tadeType, currency, index, size).then(res => {
            let tableList = [];
            res.list.map(item => {
                let market = item['coinTypeName'];
                let _obj = getCoinNum(this.props.coinData, market);
                let _item = Object.assign({}, item, _obj);
                _item.flag = this.getFlag(item);
                _item = this.replaceReason(_item);
                tableList.push(_item);
            });
            this.setState({
                tableList: tableList,
                count: res.totalCount,
                pageIndex: res.currPage,
                totalPage: res.totalPage,
            })
        })
    };

    replaceReason(item) {
        item.reason = item.reason.replace('下架原因：', '').replace('隐藏原因：', '').replace('。', '');
        if (item.reason.indexOf('取消订单') > -1) {
            item.reasonCancel = true;
            item.cancelNum = item.reason.match(/\d+/g)[0]; // 取消次数
            item.cancelTime = item.reason.match(/\d+/g)[1]; // 多少时间之内
            item.reason = item.reason.replace(item.cancelNum, 'N').replace(item.cancelTime, 'T');
        }
        return item;
    }

    getFlag(item) {
        const lan = cookie.get('zlan');
        if (item.orderStatus != 0) {
            if (item.orderStatus == 1) {
                return getAdvertiseFlag('down', lan);
            } else {
                return getAdvertiseFlag('hidden', lan);
            }
        } else {
            return '';
        }
    }

    /**
     * @desc 搜索框边框样式
     */
    changeFocus = () => this.setState({ bordeBlue: true });
    changeBlur = () => this.setState({ bordeBlue: false });

    /**
     * @desc 清空搜索框
     */
    clearFilterVal = () => {
        this.setState({
            filterVal: '',
            btnStus: 0
        }, () => {
            this.getTableData(PAGEINDEX, PAGESIZE);
        });
    };

    /**
     * @desc 输入时 input 设置到 state
     * @param event
     */
    handleInputChange = async event => {
        const _this = this;
        this.inputDelay += 1;
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;

        await this.setState({
            [name]: value,
        });
        setTimeout(async () => {
            _this.inputDelay -= 1;
            if (_this.inputDelay === 0) {
                _this.getTableData(PAGEINDEX, PAGESIZE);
            }
        }, 500);
        if (name === 'filterVal' && value) {
            this.setState(preState => {
                if (preState.btnStus != 1) {
                    return { btnStus: 1 }
                }
            })
        } else {
            this.setState(preState => {
                if (preState.btnStus != 0) {
                    return { btnStus: 0 }
                }
            })
        }
    };

    /**
     * @desc 选择币种和广告类型
     * @param val 所选择的项
     * @param type 存在为币种，否则为广告类型
     */
    chooseItem = (val, type) => {
        if (type) {
            this.setState({
                currency: val
            }, () => {
                this.getTableData(PAGEINDEX, PAGESIZE);
            });
        } else {
            this.setState({
                tadeType: val
            }, () => {
                this.getTableData(PAGEINDEX, PAGESIZE);
            });
        }
    };

    /**
     * @desc 广告状态下拉框选择
     * @param item 当前选择项
     */
    getChangeType = (item = {}) => {
        this.setState({
            adStatus: item.val,
        }, () => {
            this.getTableData(PAGEINDEX, PAGESIZE);
        });
    };

    /**
     * @desc 分页点击
     * @param values 当前点击的页码
     */
    currentPageClick = (values) => {
        this.props.pageClick(0);
        this.getTableData(values, PAGESIZE);
    };


    /**
     * @desc 上架， 下架，隐藏，发布广告的按钮点击
     * @param type 1 是下架 2 是隐藏 3 是上架 4 是发布广告
     * @param id 当前点击列表项 id
     */
    btnChoose = async (type, id) => {
        let { userInfor, intl } = this.props;
        if (type == 1) {
            // 请求下架接口
            this.setState({
                id,
                downConfirm: true,
                confirmType: 1,
            })
        } else if (type == 2) {
            this.setState({
                id,
                downConfirm: true,
                confirmType: 2,
            })
        } else if (type == 3) {
            await upFramAd(id);
            this.getTableData();
            this.setState({ id });
        } else {
            // 发布广告 判断是否登录
            if (userInfor.code != 200) {
                // this.openModalInfo();
                window.location.href = '/bw/login';
            } else {
                // 校验个人信息
                let res = userInfor.data;
                let str = '';
                if (!res.nickname) {
                    str += intl.formatMessage({ id: '昵称' });
                }
                if (!res.transPwd) {
                    if (str.length > 0) {
                        str += '、';
                    }
                    str += intl.formatMessage({ id: '资金密码' });
                }
                if (!res.mobile) {
                    if (str.length > 0) {
                        str += '、';
                    }
                    str += intl.formatMessage({ id: '绑定手机号' });
                }
                if (str.length > 0) {
                    this.setState({
                        customerInfo: true,
                        customerDetail: str
                    });
                } else if (res.cardStatus == 4) {// 校验实名认证
                    this.setState({
                        identityConfirm: true,
                        second: false,
                    });
                } else if (res.cardStatus == 7) { // 实名认证失败，重新认证
                    this.setState({
                        identityConfirm: true,
                        second: true,
                    });
                } else if (res.cardStatus == 5) {
                    let message = intl.formatMessage({ id: '身份认证审核中，请稍后再试' });
                    optPop(() => {
                    }, message, undefined, true);
                } else {
                    isBusiness().then(res => {
                        switch (res) {
                            case 'business':
                                // 跳转发布页面
                                let orderType = 1; // 默认出售
                                this.props.history.push(`/otc/buyOrSellAdvertisement/${orderType}`);
                                break;
                            case 'common':
                                this.setState({ isBusinessModalOpen: true });
                                break;
                            case 'approving':
                                optPop(() => {
                                }, intl.formatMessage({ id: '商家认证审核中，请稍后再试' }));
                                break;
                            default:
                                console.log('error');
                        }
                    });
                }
            }
        }
    };
    // 登录的弹窗
    // openModalInfo = () => {
    //     //confirm demo
    //     let str = <div className="Jua-table-inner Jua-table-main ">
    //         <div className="head react-safe-box-head">
    //             <h3 className="tc"><FormattedMessage id="发布广告前请先登录"/></h3>
    //         </div>
    //         <div className="foot">
    //             <a id="JuaBtn_8_2" role="button" className="btn btn-outgray btn-sm"
    //                onClick={() => this.modal.closeModal()}><FormattedMessage id="取消"/></a>
    //             <a id="JuaBtn_8_1" role="button" className="btn btn-primary btn-sm" onClick={() => {
    //                 this.modal.closeModal();
    //                 window.location.href = '/bw/login'
    //             }}><FormattedMessage id="去登录"/></a>
    //         </div>
    //         <div className="zoom"></div>
    //     </div>;
    //
    //     this.setState({ modalHTML: str }, () => {
    //         this.modal.openModal();
    //     });
    // };

    /**
     * @desc 下架的确认框的回调函数
     * @param type 取消 or 确认
     */
    callBack = async type => {
        const { id, confirmType } = this.state;
        if (type === 'sure') {
            if (confirmType == 1) {
                await downFramAd(id);
            } else {
                await hideAd(id);
            }
            this.getTableData();
        }
        this.setState({ downConfirm: false });
    };

    /**
     * @desc 跳转页面
     * @param type 取消 or 确认
     * @param tag 跳转标识符
     */
    cusInfoConfirm = async(type, tag) => {
        if (type === 'sure') {
            switch (tag) {
                case 1:
                    // 去用户中心
                    window.location.href = '/bw/mg/account';
                    break;
                case 2:
                    // 去身份认证
                    let authData = await axios.get(DOMAIN_VIP + '/manage/auth/authenticationJson');
                    const { authStatus } = authData && authData.data && authData.data.datas;

                    if (authStatus == 5 || authStatus == 7) {
                        window.location.href = '/bw/mg/authenOne';
                    } else {
                        window.location.href = '/bw/mgs/authenOnes';
                    }
                    localStorage.setItem('redirectUrl', '/otc/advertisement');
                    break;
                case 3:
                    // 去身份商家身份页面
                    window.location.href = '/otc/business';
                    break;
                default:
            }
        }
        this.setState({
            customerInfo: false,// 去用户信息的页面
            identityConfirm: false, // 身份认证
            isBusinessModalOpen: false // 去身份商家身份页面
        })
    };

    render() {
        const { formatMessage } = this.props.intl;
        const {
            bordeBlue, filterVal, btnStus, currencyList, tradeTypeList, currency, tadeType, loading, tableList,
            pageIndex, pageSize, count, downConfirm, customerInfo, identityConfirm, confirmType, customerDetail,
            totalPage, second, isBusinessModalOpen, adStatus
        } = this.state;
        const LAN = cookie.get('zlan');

        return (
            <div className="advertisement container_main">
                <div className="page_title"><FormattedMessage id="广告管理"/></div>
                <div className="entrust-head clearfix">
                    <div className={bordeBlue ? "input-box borde-blue" : 'input-box'}>
                        <input type="text" autoComplete="off" name="filterVal" style={{ width: '195px' }}
                               value={filterVal} placeholder={formatMessage({ id: "输入广告编号" })}
                               onChange={this.handleInputChange} onFocus={this.changeFocus} onBlur={this.changeBlur}/>
                        <button onClick={btnStus == 1 ? this.clearFilterVal : null}
                                className={btnStus == 0 ? "iconfont icon-search-bizhong" : "iconfont icon-shanchu-moren"}/>
                    </div>
                    <div className="entrust-head-type left" style={{ fontWeight: '400' }}>
                        <h5 className="left padd5"><FormattedMessage id="币种："/></h5>
                        {
                            currencyList.map((item, index) => {
                                return (
                                    <span key={index}
                                          className={`currency_label ${currency == item.fundsType ? 'curency_choose' : ''}`}
                                          onClick={() => this.chooseItem(item.fundsType, 1)}>{item.coinName}</span>
                                )
                            })
                        }
                    </div>
                    <div className="entrust-head-type left">
                        <h5 className="left padd5"><FormattedMessage id="广告状态"/></h5>
                        <div className="record-head entrust-selcet">
                            <SelectHistory
                                options={[
                                    { val: "-1", key: formatMessage({ id: "全部" }) },
                                    { val: "0", key: formatMessage({ id: "已上架" }) },
                                    { val: "1", key: formatMessage({ id: "已下架" }) },
                                    { val: "2", key: formatMessage({ id: "已隐藏" }) },
                                ]}
                                Cb={this.getChangeType}
                                defaultValue={adStatus}
                            />
                        </div>
                    </div>
                    <div className="entrust-head-type left">
                        <h5 className="left padd5"><FormattedMessage id="广告类型："/></h5>
                        {
                            tradeTypeList.map((item, index) => {
                                return (
                                    <span key={index}
                                          className={`currency_label ${tadeType == item.key ? 'curency_choose' : ''}`}
                                          onClick={() => this.chooseItem(item.key)}>{formatMessage({ id: item.value })}</span>
                                )
                            })
                        }
                    </div>
                    <input type="button" className="btn cancel add_btn" value={formatMessage({ id: "发布广告" })}
                           onClick={() => {
                               this.btnChoose(4)
                           }}/>
                </div>
                <div className="table_box">
                    <table className="table_content">
                        <thead>
                        <tr>
                            <th width="10%" className="text-center"><FormattedMessage id="币种"/></th>
                            <th width="10%" className="text-center"><FormattedMessage id="广告类型"/></th>
                            <th width="13%" className="text-right"><FormattedMessage id="数量"/></th>
                            <th width="13%" className="text-right"><FormattedMessage id="单价"/></th>
                            <th width="18%" className="text-right"><FormattedMessage id="限额"/></th>
                            <th width="13%" className="text-right"><FormattedMessage id="已成交数量"/></th>
                            <th width="10%" className="text-center"><FormattedMessage id="广告状态"/></th>
                            <th width="13%" className="borright text-center"><FormattedMessage id="操作"/></th>
                        </tr>
                        </thead>
                        <tbody id="historyEntrustList">
                        {
                            !loading
                                ?
                                (tableList.length > 0
                                    ? tableList.map((item, index) =>
                                        <React.Fragment key={index}>
                                            <tr key={(index + 1) * 2 - 1} className="advertisement_line">
                                                <td colSpan='8' style={{ position: 'relative' }}>
                                                    <span className="tr_title"><FormattedMessage id="广告编号："/></span>
                                                    <span className="tr_title advertiseVal"><Link
                                                        to={`/otc/advertiseDetails/${item.id}`}
                                                        style={{ color: '#fff' }}>{item.orderNo}</Link></span>
                                                    <span className="tr_title createTime"><FormattedMessage
                                                        id="创建时间："/></span>
                                                    <span
                                                        className="tr_title">{moment(item.orderTime).format(LAN === 'en' ? DATA_TIEM_EN : DATA_TIME_FORMAT)}</span>
                                                    {
                                                        item.orderStatus != 0 && (
                                                            <div className="reason">
                                                                {<span
                                                                    className="tr_title createTime">{item.orderStatus == 1 ? formatMessage({ id: "下架原因：" }) : formatMessage({ id: "隐藏原因：" })}</span>}
                                                                {!item.reasonCancel &&
                                                                <span className="tr_title createTime"
                                                                      style={{ marginLeft: '0' }}>{formatMessage({ id: item.reason })}</span>}
                                                                {item.reasonCancel &&
                                                                <span className="tr_title createTime"
                                                                      style={{ marginLeft: '0' }}>{formatMessage({ id: item.reason }).replace('N', item.cancelNum).replace('T', item.cancelTime)}</span>}
                                                            </div>
                                                        )
                                                    }
                                                    {item.flag && <img src={item.flag} className="ad_flag"/>}
                                                </td>
                                            </tr>
                                            <tr className="advertisement_tr" key={(index + 1) * 2}>
                                                <td className="text-center">{item.coinTypeName}</td>
                                                <td className="text-center advertise_td_cl">{item.orderType == 0 ? formatMessage({ id: "购买" }) : formatMessage({ id: "出售" })}</td>
                                                <td className="text-right">{new BigNumber(item.coinTotal || 0).toFixed(item.marketL)}{item.coinTypeName}</td>
                                                <td className="text-right">{`${separator(new BigNumber(item.coinPrice || 0).toFixed(item.payL))}CNY`}</td>
                                                <td className="text-right">{`${separator(new BigNumber(item.minNumber || 0).toFixed(item.payL))}CNY ~ ${separator(new BigNumber(item.maxNumber).toFixed(item.payL))}CNY`}</td>
                                                <td className="text-right">{new BigNumber(item.coinComplateNumber || 0).toFixed(item.marketL)}{item.coinTypeName}</td>
                                                <td className="text-center advertise_td_cl">{item.orderStatus == 0 ? formatMessage({ id: "已上架" }) : item.orderStatus == 1 ? formatMessage({ id: "已下架" }) : formatMessage({ id: "已隐藏" })}</td>
                                                <td className="text-center">
                                                    {
                                                        item.orderStatus == 0 ?
                                                            <div className="advertisement_btn">
                                                                <input type="button" className="btn small_button"
                                                                       value={formatMessage({ id: "下架" })}
                                                                       onClick={() => {
                                                                           this.btnChoose(1, item.id)
                                                                       }}/>
                                                                {/* <input type="button" className="btn small_button" value={formatMessage({id:"隐藏"})} onClick={()=> {this.btnChoose(2, item.id)}} /> */}
                                                            </div>
                                                            :
                                                            item.orderStatus == 1 ?
                                                                (item.typeOperation == 0 || item.reason.indexOf('广告已超有效期') > -1) ?
                                                                    <div className="advertisement_btn">
                                                                        <input type="button"
                                                                               className="btn small_button"
                                                                               value={formatMessage({ id: "上架" })}
                                                                               onClick={() => {
                                                                                   this.btnChoose(3, item.id)
                                                                               }}/>
                                                                    </div> : "" : ""}
                                                    {/* <Link to={`/otc/advertiseDetails/${item.id}`}><FormattedMessage id="查看详情" /></Link> */}
                                                    <a href={`/otc/advertiseDetails/${item.id}`}><FormattedMessage
                                                        id="查看详情"/></a>
                                                </td>
                                            </tr>
                                        </React.Fragment>)
                                    :
                                    <tr className="nodata">
                                        <td className="billDetail_no_list" colSpan="8">
                                            <p className="entrust-norecord">
                                                <svg className="icon" aria-hidden="true">
                                                    <use xlinkHref="#icon-tongchang-tishi"/>
                                                </svg>
                                                <FormattedMessage id="暂无广告信息"/>
                                            </p>
                                        </td>
                                    </tr>)
                                :
                                <tr style={{ height: '300px' }}>
                                    <td colSpan="15">
                                        {ThemeFactory.getThemeInstance(Styles.ThemeA)}
                                    </td>
                                </tr>
                        }
                        </tbody>
                    </table>
                </div>
                {
                    totalPage > 1 &&
                    <div className="tablist">
                        <Pages
                            pageIndex={pageIndex}
                            pagesize={pageSize}
                            total={count}
                            ref="pages"
                            currentPageClick={this.currentPageClick}
                        />
                    </div>
                }
                <ReactModal ref={modal => this.modal = modal}>
                    {this.state.modalHTML}
                </ReactModal>
                {
                    downConfirm &&
                    <Confirm title={confirmType == 1 ? formatMessage({ id: "下架确认" }) : formatMessage({ id: "隐藏确认" })}
                             content={confirmType == 1 ? formatMessage({ id: "广告一旦下架将不可复原，您确认要下架广告吗？" }) : formatMessage({ id: "确认要进行广告隐藏吗？" })}
                             cb={type => this.callBack(type)}/>
                }
                {
                    customerInfo &&
                    <Confirm safeIcon={true} isNotCancel={true} okText={formatMessage({ id: "确定" })}
                             content={formatMessage({ id: "请设置xxx" }).replace('xxx', customerDetail)}
                             cb={type => this.cusInfoConfirm(type, 1)}/>
                }
                {
                    identityConfirm && !second &&
                    <Confirm safeIcon={true} okText={formatMessage({ id: "去认证" })}
                             content={formatMessage({ id: "为了您的资金安全，进行交易前请完成身份认证" })}
                             cb={type => this.cusInfoConfirm(type, 2)}/>
                }
                {
                    identityConfirm && second &&
                    <Confirm safeIcon={true} okText={formatMessage({ id: "去认证" })}
                             content={formatMessage({ id: "身份认证失败，请重新进行认证" })}
                             cb={type => this.cusInfoConfirm(type, 2)}/>
                }
                {
                    isBusinessModalOpen &&
                    <Confirm safeIcon={true} okText={formatMessage({ id: "去认证" })}
                             content={formatMessage({ id: "您没有发布广告权限，请先认证商家身份" })}
                             cb={type => this.cusInfoConfirm(type, 3)}/>
                }
            </div>
        )
    }
}

export default withRouter(injectIntl(Advertisement));
