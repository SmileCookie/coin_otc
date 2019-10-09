import React from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';
import axios from 'axios';
import moment from 'moment';
import cookie from 'js-cookie';
import { post, get } from 'nets';
import { connect } from 'react-redux';
import Pages from "../page";
import confs from '../../conf';
import { ThemeFactory, Styles } from '../transition';
import { pageClick } from "../../redux/module/session";
import { optPop, fetchPublicKey, separator } from '../../utils';
import { resetTimeDiff, getPayTypeInfo } from '../../pages/business/index.model';
import { PAGESIZE, USERID, DATA_TIME_FORMAT, DATA_TIEM_EN } from '../../conf';
import ReactModal from '../../components/popBox';
import './pendingList.less';

const BigNumber = require('big.js');

@connect(
    state => ({
        userInfor: state.session.userInfor,
    }),
    {
        pageClick
    }
)
class PendingList extends React.Component {
    constructor(props) {
        super(props);
        let propsData = this.props.tabData || {};
        this.state = {
            tabList: propsData.list || [],
            totalPage: propsData.totalPage || 0,
            pageSize: propsData.pageSize || PAGESIZE,
            count: propsData.totalCount || 0,
            pageIndex: propsData.currPage || 0,
            confrimPay: false,
            payRemark: "",
            leavNum: 0,
            isCofrim: false,
            sellCoin: '',
            isLockPwd: false,
            payTypeInfo: {},
        };
        this.currentPageClick = this.currentPageClick.bind(this);
        this.checkModalBoxChange = this.checkModalBoxChange.bind(this);
        this.setInput = this.setInput.bind(this);
        this.confrimPay = this.confrimPay.bind(this);
        this.checkoutNeedPwd = this.checkoutNeedPwd.bind(this);
        this.operate = this.operate.bind(this);
        this.handlePayTypeMouseOver = this.handlePayTypeMouseOver.bind(this);
        this.startInterval()
    }

    startInterval() {
        this.time && (clearInterval(this.time));
        if (this.state.tabList.length > 0) {
            this.time = setInterval(() => {
                this.resetListTimeDiff(this.state.tabList)
            }, 1000)
        }
    }

    resetListTimeDiff(list) {
        list.map((item) => {
            item.timeDiffDesc = resetTimeDiff(item.timeDiff);
            item.timeDiff--;
            if (item.timeDiff == 0) {
                item.timeDiffDesc = '超时';
                item.canotOperate = false
            }
        });
        this.setState({
            tabList: list
        })
    }

    componentWillReceiveProps(props) {
        let propsData = props.tabData || {};
        this.setState({
            tabList: propsData.list || [],
            totalPage: propsData.totalPage || 0,
            pageSize: propsData.pageSize || PAGESIZE,
            count: propsData.totalCount || 0,
            pageIndex: propsData.currPage || 0,
        }, () => {
            this.startInterval()
        });

    }

    currentPageClick(values) {
        // this.props.pageClick(0);
        this.setState({
            pageIndex: values
        }, () => {
            this.props.pendingPageClick(values)
        });
    }

    setInput(e, type) {
        let name = e.target.name;
        let value = e.target.value;
        let leavNum = value.length >= 50 ? 50 : value.length;
        this.setState({
            [name]: value,
            leavNum,
        }, () => {
            this[type]()
        })

    }

    checkModalBoxChange(checkType) {
        this.setState({
                [checkType]: !this.state[checkType],
            },
            () => {
                if (checkType !== 'sellCoinBox') {
                    this[checkType]()
                } else {
                    this.sellCoin()
                }
            }
        )
    };

    //确认统一处理
    makeSureBtn = (type) => {
        let { id, recordNo, adUserId, buyUserId, buyUserMoblie, sellUserMoblie } = this.state.currItem
        let { payRemark, confrimPay, isCofrim, sellCoin } = this.state;
        let adNoMoblie = adUserId == buyUserId ? buyUserMoblie : sellUserMoblie;
        if (type == 'confrimPay') {
            let _obj = {
                id,
                recordNo,
                adNoMoblie,
                payRemark
            };
            if (confrimPay) {
                post('/web/v1/trade/commit', _obj).then((res) => {
                    optPop(() => {
                    }, res.msg);
                    this.modal.closeModal();
                    if (res.code == '200') {
                        this.props.refreshAll()
                    }
                })
            }
        }
        if (type == 'sellCoin') {
            let key = fetchPublicKey();
            let _obj = {
                recordId: id,
                transPwd: isCofrim ? '' : key.encrypt(sellCoin),
                adNoMoblie,
            };
            post('/web/v1/trade/releaseCoin', _obj).then(res => {
                optPop(() => {
                }, res.msg);
                this.modal.closeModal();
                if (res.code == '200') {
                    this.props.refreshAll()
                } else {
                    this.setState({
                        sellCoin: '',
                        sellCoinBox: false
                    })
                }
            })

        }
    };

    /**
     * @desc 操作栏事件
     * @param item 当前点击项
     */
    operate(item) {
        if (!item.canotOperate) {
            return;
        }
        if (item.dealType == 'sell') {
            this.checkoutNeedPwd(item);
        } else {
            this.confrimPay(item);
        }
    }

    //确认交易
    confrimPay(item) {
        const { confrimPay, payRemark, leavNum } = this.state;
        const { formatMessage } = this.props.intl;

        let Mstr = <div className="Jua-table-inner Jua-table-main ">
            <div className="head react-safe-box-head">
                <h3 className="tc f-18 color-fff"><FormattedMessage id="付款确认"/></h3>
                <p className="text-p color-fff"><FormattedMessage id="1.一单标记已付款，订单不可取消。"/></p>
                <p className="text-p color-fff"><FormattedMessage
                    id="2.请勿在未付款的情况下点击“已付款”按钮，该行为属于恶意点击，当此类订单发生申诉时，系统将冻结买方平台账户。"/></p>
                <div className="textArea_p">
                    <p><FormattedMessage id="备注："/></p>
                    <div className="area_wrap">
                        <textarea
                            maxLength="50" name="payRemark" id="" cols="30" rows="10" value={payRemark}
                            onChange={(e) => this.setInput(e, 'confrimPay')} className="area_dom"
                            placeholder={formatMessage({ id: '建议您输入转账时间和转账方式便于卖家账单核对，快速释放货币。' })}
                        />
                        <span className="limit_num">{leavNum}/50</span>
                    </div>
                </div>
                <div className="entrust-head-box" onClick={() => this.checkModalBoxChange('confrimPay')}>
                    <div className={`${confrimPay ? "bg-white" : ""} checkboxitem`}>
                        <i className={confrimPay ? "iconfont icon-xuanze-yixuan" : "iconfont icon-xuanze-weixuan "}/>
                    </div>
                    <span><FormattedMessage id="我确认已付款"/></span>
                </div>
            </div>
            <div className="foot">
                <a id="JuaBtn_8_2" style={{ marginLeft: 0 }} role="button" className="btn btn-outgray btn-sm"
                   onClick={() => this.modal.closeModal()}><FormattedMessage id="取消"/></a>
                <a id="JuaBtn_8_1" role="button" className={`btn btn-primary btn-sm ${confrimPay ? null : 'stop'}`}
                   onClick={() => this.makeSureBtn('confrimPay')}><FormattedMessage id="确定"/></a>
            </div>
        </div>;

        item = item ? item : this.state.currItem;
        this.setState({
            modalHTML: Mstr,
            currItem: item,
        }, () => {
            this.modal.openModal();
        })
    }

    //判断是否可以免密发送
    checkoutNeedPwd = async (item) => {
        item = item ? item : this.state.currItem;
        let { sellUserMoblie, buyUserMoblie, buyUserId, adUserId } = item;
        let adNoMoblie = adUserId == buyUserId ? buyUserMoblie : sellUserMoblie;
        let isLockPwd = await this.checkoutPwdLock();
        axios.post(confs.BBAPI + "/manage/isTransSafe").then(res => {
            let needSafeWord = eval(res["data"]).des;
            if (needSafeWord != 'false') {
                this.setState({
                    isCofrim: false,
                    currItem: item,
                    isLockPwd
                }, () => this.sellCoin()) //不可以免密

            } else {
                this.setState({
                    isCofrim: true,
                    currItem: item,
                    isLockPwd
                }, () => this.sellCoin()) //不可以免密
            }
        })
    };
    //判断密码是否锁定24h
    checkoutPwdLock = () => {
        return new Promise((resolve) => {
            axios.get(confs.BBAPI + '/manage/account/download/resetPayPwdAsh').then(res => {
                if (res.status == '200') {
                    resolve(res.data.datas.ashLockStatus);
                } else {
                    resolve(false);
                }
            })
        })
    };

    /**
     * 支付信息图标移入事件
     * @param id 当前列表项 id
     */
    handlePayTypeMouseOver = async id => {
        const res = await getPayTypeInfo(id);
        this.setState({ payTypeInfo: res });
    };

    //释放货币
    sellCoin(item) {
        item = this.state.currItem;
        const { formatMessage } = this.props.intl;
        const icon = item.market;
        const { sellCoin, sellCoinBox, isCofrim, isLockPwd } = this.state;
        let Mstr = <div className="Jua-table-inner Jua-table-main">
            <div className="head react-safe-box-head">
                <h3 className="tc f-18 color-fff"><FormattedMessage id="确认释放{coin}" values={{ coin: icon }}/></h3>
                {
                    !isCofrim ?
                        <React.Fragment>
                            <input type="password" maxLength="20" name="sellCoin" onPaste={(e) => {
                                e.preventDefault()
                            }} className="form_input" onChange={(e) => this.setInput(e, "sellCoin")} value={sellCoin}
                                   placeholder={formatMessage({ id: '请输入资金密码' })}/>
                            <div className="entrust-head-box" onClick={() => this.checkModalBoxChange('sellCoinBox')}>
                                <div className={`${sellCoinBox ? "bg-white" : ""} checkboxitem`}>
                                    <i className={sellCoinBox ? "iconfont icon-xuanze-yixuan" : "iconfont icon-xuanze-weixuan "}/>
                                </div>
                                <span><FormattedMessage id="我已收到款项"/></span>
                            </div>
                            <div className="ar" style={{ paddingTop: '20px' }}>
                                <a className={`baseColor  ${isLockPwd ? 'lockSetPwd' : 'baseCoHover'}`}
                                   href={isLockPwd ? `javascript:void(0);` : `/bw/mg/resetPayPwd`}>{formatMessage({ id: '忘记资金密码？' })}</a>
                            </div>
                        </React.Fragment>
                        :
                        <div className="entrust-head-box" onClick={() => this.checkModalBoxChange('sellCoinBox')}>
                            <div className={`${sellCoinBox ? "bg-white" : ""} checkboxitem`}>
                                <i className={sellCoinBox ? "iconfont icon-xuanze-yixuan" : "iconfont icon-xuanze-weixuan "}/>
                            </div>
                            <span><FormattedMessage id="我已收到款项"/></span>
                        </div>
                }
            </div>
            <div className="foot">
                <a id="JuaBtn_8_2" style={{ marginLeft: 0 }} role="button" className="btn btn-outgray btn-sm"
                   onClick={() => this.modal.closeModal()}><FormattedMessage id="取消"/></a>
                {
                    !isCofrim ?
                        (sellCoin && sellCoinBox) ?
                            <a id="JuaBtn_8_1" onClick={() => this.makeSureBtn('sellCoin')} role="button"
                               className="btn btn-primary btn-sm"><FormattedMessage id="确定"/></a>
                            : <a id="JuaBtn_8_1" role="button" style={{
                                backgroundColor: '#737A8D',
                                color: '#9199AF',
                                cursor: 'default',
                                border: 'none'
                            }} className="btn btn-primary btn-sm"><FormattedMessage id="确定"/></a>
                        :
                        sellCoinBox ?
                            <a id="JuaBtn_8_1" onClick={() => this.makeSureBtn('sellCoin', isCofrim)} role="button"
                               className="btn btn-primary btn-sm"><FormattedMessage id="确定"/></a>
                            : <a id="JuaBtn_8_1" role="button" style={{
                                backgroundColor: '#737A8D',
                                color: '#9199AF',
                                cursor: 'default',
                                border: 'none'
                            }} className="btn btn-primary btn-sm"><FormattedMessage id="确定"/></a>

                }
            </div>
        </div>;


        this.setState({
            modalHTML: Mstr,
            currItem: item,
        }, () => {
            this.modal.openModal();
        })
    }

    render() {
        const { tabList, loading, totalPage, pageSize, pageIndex, count, payTypeInfo } = this.state;
        const { formatMessage } = this.props.intl;
        const LAN = cookie.get('zlan');

        return (
            <React.Fragment>
                <div className="table_box pendingListMain">
                    <table className="table_content">
                        <thead>
                        <tr>
                            <th className="text-left"><FormattedMessage id="类型"/></th>
                            <th className="text-right"><FormattedMessage id="交易对象"/></th>
                            <th className="text-right"><FormattedMessage id="总价"/></th>
                            <th className="text-right"><FormattedMessage id="货币"/></th>
                            <th className="text-right"><FormattedMessage id="单价"/></th>
                            <th className="text-right"><FormattedMessage id="交易数量"/></th>
                            <th className="text-right"><FormattedMessage id="下单时间"/></th>
                            <th className="text-right"><FormattedMessage id="状态"/></th>
                            <th className="text-right"><FormattedMessage id="订单限时"/></th>
                            <th className="text-right"><FormattedMessage id="支付信息"/></th>
                            <th className="text-center"><FormattedMessage id="操作"/></th>
                        </tr>
                        </thead>
                        <tbody id="historyEntrustList">
                        {
                            tabList.length > 0 ? tabList.map((item, index) => {
                                    return (
                                        <tr key={index}>
                                            <td className={`text-left ${item.dealType === 'sell' ? 'red' : 'green'}`}>
                                                {item.dealType === 'sell' ? formatMessage({ id: "出售" }) : formatMessage({ id: "购买" })}
                                            </td>
                                            <td className="text-right high_light" onClick={() => {
                                                this.props.showUserCenter(item)
                                            }}>{item.buyUserId == USERID ? item.sellUserName : item.buyUserName}
                                            </td>
                                            <td className="text-right">{separator(new BigNumber(item.coinPriceTotal || 0).toFixed(2))}
                                                CNY
                                            </td>
                                            <td className="text-right">{item.market}</td>
                                            <td className="text-right">{separator(new BigNumber(item.coinPrice).toFixed(2))}
                                                CNY
                                            </td>
                                            <td className="text-right">{new BigNumber(item.coinNumber).toFixed(item.marketL)} {item.market}</td>
                                            <td className="text-right">{moment(item.coinTime).format(LAN == 'en' ? DATA_TIEM_EN : DATA_TIME_FORMAT)}</td>
                                            <td className="text-right">{formatMessage({ id: item.statusName })}</td>
                                            <td className="text-right">{item.timeDiffDesc == '超时' ? formatMessage({ id: '超时' }) : item.timeDiffDesc}</td>
                                            <td className="text-center payInfo">
                                                {
                                                    item.dealType === 'buy' && item.paymentTypes.indexOf('2') !== -1 &&
                                                    <div className="bank_icon_wrap">
                                                        <svg
                                                            className="bank_icon" aria-hidden="true"
                                                            onMouseOver={() => {
                                                                this.handlePayTypeMouseOver(item.id)
                                                            }}
                                                        >
                                                            <use xlinkHref="#icon-ico_unionpay"/>
                                                        </svg>
                                                        <div className="bank_tip tip_txt_wrap">
                                                            <div>
                                                                <p><FormattedMessage id="姓名"/></p>
                                                                <p><FormattedMessage id="银行卡号"/></p>
                                                                <p><FormattedMessage id="开户行"/></p>
                                                                <p><FormattedMessage id="开户支行"/></p>
                                                            </div>
                                                            <div>
                                                                <p>{payTypeInfo.bankName}</p>
                                                                <p>{payTypeInfo.bankNumber}</p>
                                                                <p>{payTypeInfo.bankOpeningBank}</p>
                                                                <p>{payTypeInfo.bankOpeningBranch}</p>
                                                            </div>
                                                        </div>
                                                    </div>
                                                }
                                                {
                                                    item.dealType === 'buy' && item.paymentTypes.indexOf('1') !== -1 &&
                                                    <div className="alipay_icon_wrap">
                                                        <svg
                                                            className="alipay_icon" aria-hidden="true"
                                                            onMouseOver={() => {
                                                                this.handlePayTypeMouseOver(item.id)
                                                            }}
                                                        >
                                                            <use xlinkHref="#icon-ico_alipay"/>
                                                        </svg>
                                                        <div className="alipay_tip">
                                                            <div>
                                                                {
                                                                    (payTypeInfo.qrcodeUrl === confs.defaultData
                                                                        || payTypeInfo.qrcodeUrl === ''
                                                                        || payTypeInfo.qrcodeUrl === null
                                                                        || typeof payTypeInfo.qrcodeUrl === 'undefined'
                                                                    ) ?
                                                                        <span><FormattedMessage id="未上传"/></span> :
                                                                        <img src={payTypeInfo.qrcodeUrl} alt=""/>
                                                                }
                                                            </div>
                                                            <div className="tip_txt_wrap">
                                                                <div>
                                                                    <p><FormattedMessage id="姓名"/></p>
                                                                    <p><FormattedMessage id="支付宝号"/></p>
                                                                </div>
                                                                <div>
                                                                    <p>{payTypeInfo.alipayName}</p>
                                                                    <p>{payTypeInfo.alipayNumber}</p>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                }
                                                {
                                                    item.dealType === 'sell' && <span>{item.buyerCardName}</span>
                                                }
                                            </td>
                                            <td className="text-center">
                                                <span className={`${item.canotOperate ? 'canOperate' : 'noOperate'}`}
                                                      style={{ cursor: 'pointer' }} onClick={() => {
                                                    this.operate(item)
                                                }}>{formatMessage({ id: item.operateName })} </span>
                                                <span style={{ color: '#3E85A2', cursor: 'pointer' }} onClick={() => {
                                                    window.location.href = `/otc/orderDetail/${item.id}`
                                                }}>&nbsp;&nbsp; {formatMessage({ id: "查看详情" })}</span>
                                            </td>
                                        </tr>
                                    )
                                }) :
                                loading ?
                                    (
                                        <tr>
                                            <td className="loading_td" colSpan="11">
                                                {ThemeFactory.getThemeInstance(Styles.ThemeA)}
                                            </td>
                                        </tr>
                                    )
                                    :
                                    (
                                        <tr className="nodata">
                                            <td className="billDetail_no_list" colSpan="11">
                                                <p className="entrust-norecord">
                                                    <svg className="icon" aria-hidden="true">
                                                        <use xlinkHref="#icon-tongchang-tishi"/>
                                                    </svg>
                                                    <FormattedMessage id="当前项无相关数据"/>
                                                </p>
                                            </td>
                                        </tr>
                                    )
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
            </React.Fragment>
        )
    }
}

export default injectIntl(PendingList);
