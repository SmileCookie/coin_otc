import React from 'react';
import { post, get } from 'nets';
import cookie from 'js-cookie';
import { withRouter } from 'react-router';
import { connect } from 'react-redux';
import { FormattedMessage, injectIntl } from 'react-intl';
import Pages from "../../components/page";
import ReactModal from '../../components/popBox';
import Confirm from '../../components/confirm';
import Tab from '../../components/tab';
import UserCenter from '../../components/user/userCenter';
import '../../assets/style/trade/index.less';
import '../../assets/style/modal/index.css';
import { DOMAIN_VIP, PAGEINDEX, PAGESIZE, USERID } from '../../conf';
import { optPop, separator, getFirstStr, getCoinNum } from '../../utils'
import { Styles, ThemeFactory } from "../../components/transition";
import { pageClick } from "../../redux/module/session";

const BigNumber = require('big.js');
// 获取模型数据
import { init, getTableList, FirstEquivalence, getAdCheckInfo } from './index.model';
import { isBusiness } from '../advertisement/index.model';
//设置title 标题
import TitleSet from "../../components/setTitle";
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
class Trade extends React.Component {
    constructor(props) {
        super(props);
        // 从 localStorage 中获取 筛选项
        let trStorage = {};
        //0 即 TYPE_NAVIGATENEXT 正常进入的页面（非刷新、非重定向等）
        //1 即 TYPE_RELOAD 通过 window.location.reload() 刷新的页面
        //2 即 TYPE_BACK_FORWARD 通过浏览器的前进后退按钮进入的页面（历史记录）
        //255 即 TYPE_UNDEFINED 非以上方式进入的页面
        const performance = window.performance || window.msPerformance || window.webkitPerformance;
        const { type } = (performance && performance.navigation) || '';
        if (type === 1) {
            trStorage = JSON.parse(window.localStorage.getItem('tradeStorage')) || {};
        } else {
            window.localStorage.removeItem('tradeStorage');
        }
        this.state = {
            modalHTML: '', // 去登录
            TabList: ['购买', '出售'],
            tabIndex: 0, // 默认购买
            loading: true, // 加载动画
            pageIndex: PAGEINDEX, // 当前页
            pageSize: PAGESIZE, // 每页多少条
            count: 4,   // 总页数
            currencyList: [], // 币种
            payTypeList: [
                {
                    key: '',
                    value: '全部'
                },
                {
                    key: 1,
                    value: '支付宝'
                },
                {
                    key: 2,
                    value: '银行卡'
                },
            ],// 支付方式
            currency: '', //币种的选择
            payType: '', // 支付方式的选择
            legalCurrency: 'CNY',
            customerText: "", // 哪些信息未设置
            customerModalText: '',//modal里的customerText
            tableList: [],// table 的数据
            payTypeConfirm: false, // 收款方式设置
            customerInfo: false,// 去用户信息的页面
            identityConfirm: false, // 身份认证
            isBusinessModalOpen: false, // 去商家认证页面 Modal
            isNotHasPayTypeModalOpen: false, // 验证收款方式 Modal
            second: false,
            modalTxt: '',
            ...trStorage,
        };
        this.showUserCenter = this.showUserCenter.bind(this)
    }

    componentDidMount() {
        //防止子页面滚动之后返回
        this.props.pageClick(0);
        // 请求初始化数据
        this.showInfoSetting();
        this.initData();
        if (cookie.get("zuid")) {
            FirstEquivalence(cookie.get("zuid"));
        }
        this.startInterval();
    }

    componentWillUnmount() {
        if (this.interval) {
            clearInterval(this.interval);
        }
        window.localStorage.removeItem('tradeStorage');
    }

    startInterval() {
        if (this.interval) {
            clearInterval(this.interval);
        }
        this.interval = setInterval(() => {
            this.getTradeList('', '', true);
        }, 3000)
    }

    // 初始化数据
    initData = () => {
        init(this).then(res => {
            let tableList = [];
            res.tradeList.list.map(item => {
                let market = item['coinTypeName'];
                let _obj = getCoinNum(this.props.coinData, market);
                let _item = Object.assign({}, item, _obj);
                tableList.push(_item);
            });

            this.setState({
                currencyList: res.currencyList,
                tableList: tableList,
                count: res.tradeList.totalCount,
                pageIndex: res.tradeList.currPage,
                totalPage: res.tradeList.totalPage,
            });
            // 如果缓存中没有币种筛选项则设置默认值
            const { currency } = this.state;
            if (currency === '') {
                this.setState({ currency: res.currencyList[0].coinName });
            }
        })
    };
    // 获取列表数据
    getTradeList = (indexPage, page, noLoading) => {
        const { tabIndex, pageIndex, pageSize, currency, payType, legalCurrency } = this.state;
        // 和广告是反着的
        let type = tabIndex == 0 ? '1' : '0';
        let index = indexPage || pageIndex;
        let size = page || pageSize;
        getTableList(this, type, payType, currency, legalCurrency, index, size, noLoading).then(res => {
            let tableList = [];
            res.list.map(item => {
                let market = item['coinTypeName'];
                let _obj = getCoinNum(this.props.coinData, market);
                let _item = Object.assign({}, item, _obj);
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
    // 获取那些用户信息没填写
    showInfoSetting = () => {
        const { userInfor } = this.props;
        // 用户信息接口返回的
        if (userInfor.code == 200) {
            this.getCustomerInfo(2);
        }
    };
    getCustomerInfo = (type, id, userId, paymentTypes) => {
        // type == 1是购买和出售的  type == 2 是跳转用户中心的
        let { intl, userInfor } = this.props;
        const { tabIndex } = this.state;
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
        if (type == 2) {
            if (res.cardStatus == 0 || res.cardStatus == 4 || res.cardStatus == 5 || res.cardStatus == 7) {
                if (str.length > 0) {
                    str += '、';
                }
                str += intl.formatMessage({ id: '身份认证' });
            }
            this.setState({
                customerText: str
            })
        } else {
            if (str.length > 0) {
                this.setState({
                    customerInfo: true,
                    customerModalText: str
                });
            } else if (res.cardStatus == 4) {// 校验实名认证
                this.setState({
                    identityConfirm: true,
                    second: false,
                });
            } else if (res.cardStatus == 7) {
                this.setState({
                    identityConfirm: true,
                    second: true,
                });
            } else if (res.cardStatus == 5) {
                let message = intl.formatMessage({ id: '身份认证审核中，请稍后再试' });
                optPop(() => {
                }, message, undefined, true);
            } else if (type == 1) {

                // tabIndex 0 购买， 1 出售
                // 购买  当前用户订单取消次数<3、当前用户未完成订单数量<x、校验广告主是否是当前用户、是否被对方拉黑或者拉黑对方
                // 出售  当前用户未完成订单数量<x、/校验广告主是当前用户、校验有效收款方式>0、是否被对方拉黑或者拉黑对方
                getAdCheckInfo(id, userId).then(res => {
                    if (typeof(res) !== 'boolean') {
                        let message = '';
                        if (tabIndex == 0 && res.configInfo.userCancelNum >= res.configInfo.userCancleNumConf) {
                            message = intl.formatMessage({ id: '您今日取消订单次数已达N次，24小时内购买功能不可使用' }).replace('N', res.configInfo.userCancleNumConf);
                        } else if (res.configInfo.userUnFinishTotal >= res.configInfo.userOrderNumMax) {
                            message = intl.formatMessage({ id: '您有未处理完的订单交易' });
                        } else if (USERID == res.adInfo.userId) {
                            message = intl.formatMessage({ id: '不能交易自己创建的广告' });
                        }
                        if (message != '') {
                            optPop(() => {
                            }, message);
                            return;
                        }
                        // 出售校验收款方式
                        let returnFlag = true;
                        if (tabIndex == 1) {
                            const { otcPaymentTypes } = this.props.userInfor.data;
                            // 当前用户是否设置了收款方式
                            const isBindPayment = Array.isArray(otcPaymentTypes)
                                && otcPaymentTypes.length > 0
                                && otcPaymentTypes.every(item => item.enable === false);
                            // 未设置收款方式弹窗提示
                            if (isBindPayment) {
                                this.setState({ payTypeConfirm: true });
                                returnFlag = false;
                            } else {
                                // paymentTypes 卖家设置的收款方式 1支付宝 2银行
                                const sellerArr = paymentTypes.split(',');
                                const buyerArr = [];
                                otcPaymentTypes.forEach(item => {
                                    if (item.enable) {
                                        buyerArr.push(item.paymentType.toString());
                                    }
                                });
                                // 如果卖家设置的收款方式中不包含当前用户的收款方式则弹出 Modal 提示
                                if (buyerArr.length < 2) {
                                    buyerArr.forEach(item => {
                                        if (!sellerArr.includes(item)) {
                                            if (item == '2') { // 买家为“银行卡”，则需设置“支付宝”
                                                this.setState({
                                                    isNotHasPayTypeModalOpen: true,
                                                    modalTxt: intl.formatMessage({ id: '此用户付款方式为“支付宝”，请设置并开启“支付宝”' })
                                                });
                                            } else {
                                                this.setState({
                                                    isNotHasPayTypeModalOpen: true,
                                                    modalTxt: intl.formatMessage({ id: '此用户付款方式为“银行卡”，请设置并开启“银行卡”' })
                                                });
                                            }
                                            returnFlag = false;
                                        }
                                    });
                                }
                            }
                        }
                        if (!returnFlag) {
                            return;
                        }
                        window.oderInfo = {
                            id,
                            userId
                        };
                        this.props.history.push(`/otc/buySell/${id}`);
                    }
                });
            } else {
                isBusiness().then(res => {
                    switch (res) {
                        case 'business':
                            // 跳转发布页面
                            let type = tabIndex == 0 ? 1 : 0;
                            this.props.history.push(`/otc/buyOrSellAdvertisement/${type}`);
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
    };
    // 币种和支付类型
    chooseItem = (val, type) => {
        if (type) {
            this.setState({
                currency: val
            }, () => {
                this.getTradeList(PAGEINDEX, PAGESIZE);
            })
        } else {
            this.setState({
                payType: val
            }, () => {
                this.getTradeList(PAGEINDEX, PAGESIZE);
            })
        }
    };
    //分页
    currentPageClick = (values) => {
        this.props.pageClick(0);
        this.getTradeList(values, PAGESIZE);
    };
    // 购买， 出售，发布广告
    btnChoose = (type, id, userId, paymentTypes) => {
        // 有type 就指的是购买和出售
        let { userInfor } = this.props;
        // 发布广告 判断是否登录
        if (userInfor.code != 200) {
            if (type) {
                window.location.href = '/bw/login';
                localStorage.setItem('otcLoginBefore', '/otc/trade');
            } else {
                // this.openModalInfo();
                window.location.href = '/bw/login';
                localStorage.setItem('otcLoginBefore', '/otc/trade');
            }
        } else {
            // 校验个人信息
            this.getCustomerInfo(type, id, userId, paymentTypes);
        }
    };
    // 登录的弹窗
    // openModalInfo = () => {
    //     //confirm demo
    //     let str = <div className="Jua-table-inner Jua-table-main ">
    //         <div className="head react-safe-box-head">
    //             <h3 className="tc"><FormattedMessage id="发布广告前请先登录"/></h3>
    //         </div>
    //
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
    // 收款方式
    callBack = type => {
        if (type === 'sure') {
            // 去设置收款方式
            window.location.href = '/bw/mg/authenOne';
            localStorage.setItem('redirectUrl', '/otc/trade');
        }
        this.setState({ payTypeConfirm: false });
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
                    localStorage.setItem('redirectUrl', '/otc/trade');
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
            isBusinessModalOpen: false, // 去身份商家身份页面
            isNotHasPayTypeModalOpen: false, // 去身份商家身份页面
        })
    };

    /**
     * @desc tab 切换
     * @param val tab 循环下标
     */
    tabChange = val => {
        this.setState({
            tabIndex: val,
        }, () => {
            this.getTradeList(PAGEINDEX, PAGESIZE);
        });
        // 将筛选值存入 localStorage
        const { localStorage } = window;
        let trStorage = JSON.parse(localStorage.getItem('tradeStorage')) || {};
        trStorage = {
            ...trStorage,
            tabIndex: val,
        };
        localStorage.setItem('tradeStorage', JSON.stringify(trStorage));
    };

    async showUserCenter(target) {
        let userId = USERID || "";
        let res = await post('/web/common/getAvgPassTime', { targetUserId: target.userId, userId });
        if (res.code == 200) {
            this.setState({
                targetId: target.userId,
                uid: USERID,
                homePage: res.data,
            }, () => {
                this.modalUser.openModal();
            })
        } else {
            optPop(() => {
            }, res.data.msg, { timer: 1500 });
        }
    }

    render() {
        const { userInfor } = this.props;
        const { formatMessage } = this.props.intl;
        const {
            tabIndex, TabList, currencyList, payTypeList, currency, payType, loading, pageIndex,
            pageSize, count, payTypeConfirm, customerInfo, identityConfirm, customerText, tableList,
            customerModalText, totalPage, second, isBusinessModalOpen, isNotHasPayTypeModalOpen, modalTxt
        } = this.state;

        return (
            <div className="tradeMain container_main">
                <div className="tap_content">
                    <Tab list={TabList} index={tabIndex} className="big_tab" onChange={this.tabChange}/>
                </div>
                {
                    userInfor.code == 200 && customerText.length > 0 &&
                    <div className="customer_info">
                        <svg className="icon" aria-hidden="true">
                            <use xlinkHref="#icon-tongchang-tishi"/>
                        </svg>
                        <FormattedMessage id="在开始交易前，需要完善必要的交易信息，您还有{userInfo}未进行设置。请在{userCenter}进行设置。" values={{
                            userInfo: customerText,
                            userCenter: <a href="/bw/mg/account"> {formatMessage({ id: "用户中心" })}</a>
                        }}/>
                    </div>
                }
                <div className="entrust-head clearfix" style={{ fontWeight: '400' }}>
                    <div className="left">
                        <h5 className="left padd5"><FormattedMessage id="币种："/></h5>
                        {
                            currencyList.map((item, index) => {
                                return (
                                    <span key={index}
                                          className={`currency_label ${currency == item.coinName ? 'curency_choose' : ''}`}
                                          onClick={() => this.chooseItem(item.coinName, 1)}>{item.coinName}</span>
                                )
                            })
                        }
                    </div>
                    <div className="entrust-head-type left">
                        <h5 className="left padd5"><FormattedMessage id="法币："/></h5>
                        <span className="currency_label curency_choose">CNY</span>
                    </div>
                    <div className="entrust-head-type left">
                        <h5 className="left padd5"><FormattedMessage id="支付方式："/></h5>
                        {
                            payTypeList.map((item, index) => {
                                return (
                                    <span key={index}
                                          className={`currency_label ${payType == item.key ? 'curency_choose' : ''}`}
                                          onClick={() => this.chooseItem(item.key)}>{formatMessage({ id: item.value })}</span>
                                )
                            })
                        }
                    </div>
                    <input type="button" className="btn cancel add_btn" value={formatMessage({ id: "发布广告" })}
                           onClick={() => {
                               this.btnChoose()
                           }}/>
                </div>
                <div className="table_box">
                    <table className="table_content">
                        <thead>
                        <tr>
                            <th width="14%" className="text-left"><FormattedMessage id="商家"/></th>
                            <th width="14%" className="text-right"><FormattedMessage id="数量"/></th>
                            <th width="22%" className="text-right"><FormattedMessage id="限额"/></th>
                            <th width="16%" className="text-right"><FormattedMessage id="单价"/></th>
                            <th width="18%" className="text-center"><FormattedMessage id="支付方式"/></th>
                            <th width="16%" className="text-center"><FormattedMessage id="交易"/><span
                                className="pay_money"><FormattedMessage id="手续费："/>0</span></th>
                        </tr>
                        </thead>
                        <tbody id="historyEntrustList">
                        {
                            !loading
                                ?
                                (tableList.length > 0
                                    ? tableList.map((item, index) => {
                                        if (USERID == item.userId) {
                                            item.onlineStatus = 1
                                        }
                                        return (
                                            <tr key={index}>
                                                <td className="text-left first_td">
                                                    <div className="info">
                                                        <div className="info_left" style={{ cursor: 'pointer' }}
                                                             onClick={() => this.showUserCenter(item)}>
                                                        <span
                                                            style={{ backgroundColor: item.color }}>{getFirstStr(item.userName)}</span>
                                                            <i style={{ backgroundColor: item.onlineStatus == 0 ? '#B9B9B9' : '#47CD85' }}/>
                                                        </div>
                                                        <div className="right">
                                                        <span className="name"
                                                              onClick={() => this.showUserCenter(item)}>{item.userName}</span>
                                                            <div style={{ whiteSpace: 'nowrap' }}>
                                                                <FormattedMessage id="成交笔数"/>
                                                                &nbsp;
                                                                <span>{item.orderTotal}</span>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td className="text-right">{new BigNumber(item.coinTotalNumber || 0).toFixed(item.marketL)}{item.coinTypeName}</td>
                                                <td className="text-right">{`${separator(new BigNumber(item.minNumber || 0).toFixed(item.payL))}CNY ~ ${separator(new BigNumber(item.maxNumber || 0).toFixed(item.payL))}CNY`}</td>
                                                <td className="text-right">{`${separator(new BigNumber(item.coinPrice || 0).toFixed(item.payL))}CNY`}</td>
                                                <td className="text-center">
                                                    {
                                                        item.paymentTypes.indexOf('1') != -1 &&
                                                        <svg className="icon" aria-hidden="true">
                                                            <use xlinkHref="#icon-ico_alipay"/>
                                                        </svg>
                                                    }
                                                    {
                                                        item.paymentTypes.indexOf('2') != -1 &&
                                                        <svg className="bank_icon" aria-hidden="true">
                                                            <use xlinkHref="#icon-ico_unionpay"/>
                                                        </svg>
                                                    }
                                                </td>
                                                <td className="text-center">
                                                    <div className="trade_btn">
                                                        <input type="button" className="btn small_button"
                                                               value={tabIndex == 0 ? formatMessage({ id: "购买xxx" }).replace('xxx', item.coinTypeName) : formatMessage({ id: "出售xxx" }).replace('xxx', item.coinTypeName)}
                                                               onClick={() => this.btnChoose(1, item.id, item.userId, item.paymentTypes)}/>
                                                    </div>
                                                </td>
                                            </tr>
                                        )
                                    })
                                    :
                                    <tr className="nodata">
                                        <td className="billDetail_no_list" colSpan="6">
                                            <p className="entrust-norecord">
                                                <svg className="icon" aria-hidden="true">
                                                    <use xlinkHref="#icon-tongchang-tishi"/>
                                                </svg>
                                                <FormattedMessage id="暂无更多广告"/>
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
                <ReactModal ref={modal => this.modalUser = modal}>
                    <UserCenter modal={this.modalUser} hoemPage={this.state.homePage} targetId={this.state.targetId}
                                uid={this.state.uid}/>
                </ReactModal>
                {
                    payTypeConfirm &&
                    <Confirm safeIcon={true} okText={formatMessage({ id: "去设置" })}
                             content={formatMessage({ id: "请设置收款方式" })} cb={type => this.callBack(type)}/>
                }
                {
                    customerInfo &&
                    <Confirm safeIcon={true} isNotCancel={true} okText={formatMessage({ id: "确定" })}
                             content={formatMessage({ id: "请设置xxx" }).replace('xxx', customerModalText)}
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
                {
                    isNotHasPayTypeModalOpen &&
                    <Confirm safeIcon={true} okText={formatMessage({ id: "去开启" })} content={modalTxt}
                             cb={type => this.cusInfoConfirm(type, 1)}/>
                }
                 <TitleSet titlemoney={`${this.state.currency}`} titleval={this.state.tabIndex == 0 ? "购买" : "出售"}/>
            </div>
        )
    }
}

export default withRouter(injectIntl(Trade));
