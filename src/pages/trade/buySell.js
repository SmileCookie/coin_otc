import React from 'react';
import { post, get } from 'nets';
import { withRouter } from 'react-router'
import { connect } from 'react-redux'
import { FormattedMessage, injectIntl } from 'react-intl';
import '../../assets/style/trade/buySell.less'
import ReactModal from '../../components/popBox';
import UserCenter from '../../components/user/userCenter'
import { separator, checkNumber, getFirstStr } from "../../utils";
import { DATA_TIME_FORMAT, USERID } from 'conf';
import moment from 'moment';
import { isValEmpty } from "../../utils/index";

const BigNumber = require('big.js');

import {optPop, getCurrency, formatDecimal} from '../../utils';
import {ThemeFactory, Styles} from '../../components/transition';
import {pageClick} from "../../redux/module/session";
import TitleSet from "../../components/setTitle";

// 获取模型数据
import {init, getAdvertiseInfo, saveOrder} from './buySell.model';
//coinList: state.account.detail,
@connect(
    state => ({
        userInfor: state.session.userInfor
    }),
    {
        pageClick
    }
)
class BuySell extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            id: '',
            tips: '', // 温馨提示
            userId: '',
            buyNum: {
                val: '',
                error: ''
            },
            buyPrice: {
                val: '',
                error: ''
            },
            tradeInfo: {},
            checkInfo: {},
            adInfo: {},
            isSubmit: true,
        };
        this.showUserCenter = this.showUserCenter.bind(this)
    }

    componentWillMount() {
        if (this.checkUserInfo()) {
            window.location.href = '/otc/trade';
            return;
        }
        !USERID && (window.location.href = '/bw/login')
    }

    componentDidMount() {
        let id = this.props.match.params.id;
        this.setState({ id });
        this.getInit(id);
        this.props.pageClick(0);
    }

    getInit = (id) => {
        init(this, id).then(res => {
            this.setState({
                tradeInfo: res.advertiseInfo,
                adInfo: res.checkInfo.adInfo,
                checkInfo: res.checkInfo.configInfo,
                tips: res.tips,
            })
        })
    };

    /**
     * @desc 监听输入框改变
     * @param e
     */
    onInputChange = e => {
        let { checkInfo } = this.state;
        let name = e.target.name;
        let targetVal = e.target.value;
        let keepNum = name === 'buyNum' ? checkInfo.coinBixDian : checkInfo.legalBixDian;
        if (name === 'buyPrice' && this.checkValDian(targetVal, keepNum)) {
            targetVal = new BigNumber(this.checkValDian(targetVal, keepNum) || 0).toFixed(keepNum)
        }
        let val = checkNumber(targetVal, keepNum);
        this.tradeNumValid(val, name);
    };

    /**
     * @desc 校验 val 的小数点位数是不是超过设定的值
     * @param val 输入框的值
     * @param dian 允许的小数位数
     */
    checkValDian(val, dian) {
        if (!val || !dian) {
            return false;
        }
        val = String(val);
        let fenmu = '1';
        for (let i = 0; i < dian; i++) {
            fenmu += '0';
        }
        if (val.indexOf('.') > -1) {
            if (val.split('.')[1]) {
                if (val.split('.')[1].length > dian) { // 如果超过设定值，则最后一位+1
                    return parseFloat(val.slice(0, val.length - 1)) + 1 / parseInt(fenmu);
                }
            }
        }
    };

    // input 框的校验
    tradeNumValid = (val, name, type) => {
        // type 存在说明是失去焦点时候的
        let { buyNum, buyPrice, tradeInfo, checkInfo } = this.state;
        if (name === 'buyNum') {
            if (val != "") {
                if (!type) {
                    buyPrice.val = getCurrency(val, tradeInfo.coinPrice, checkInfo.legalBixDian);
                }
                // 数量的校验
                if (parseFloat(val) > parseFloat(tradeInfo.coinTotalNumber)) {
                    buyPrice.error = this.getInternationalInfo();
                } else if (parseFloat(buyPrice.val) < parseFloat(tradeInfo.minNumber) || parseFloat(buyPrice.val) > parseFloat(tradeInfo.maxNumber)) {// 最小限额的校验 最大限额的校验
                    buyPrice.error = this.getInternationalInfo();
                } else if (tradeInfo.orderType == 0) {// 出售的余额的校验
                    let aocount = new BigNumber(checkInfo.coinBixBalance || 0).times(tradeInfo.coinPrice ? tradeInfo.coinPrice : 0);
                    if (buyPrice.val > parseFloat(aocount)) {
                        buyNum.error = "";
                        buyPrice.error = "";
                    } else {
                        buyNum.error = "";
                        buyPrice.error = "";
                    }
                } else {
                    buyNum.error = "";
                    buyPrice.error = "";
                }
            } else {
                buyPrice.val = "";
                // 在input获取焦点的时候如果值为空也不显示提示信息但是失去焦点时候不能清空error
                if (!type) {
                    buyNum.error = "";
                }
            }
            buyNum.val = val;
        } else if (name === 'buyPrice') {
            if (val != "") {
                if (!type) {
                    if (tradeInfo.price != 0) {
                        buyNum.val = formatDecimal((val || 0) / tradeInfo.coinPrice, checkInfo.coinBixDian)
                    }
                }
                // 数量的校验
                if (parseFloat(buyNum.val) > parseFloat(tradeInfo.coinTotalNumber)) {
                    buyPrice.error = this.getInternationalInfo();
                } else if (parseFloat(val) < parseFloat(tradeInfo.minNumber) || parseFloat(val) > parseFloat(tradeInfo.maxNumber)) {// 最小限额的校验 最大限额的校验
                    buyPrice.error = this.getInternationalInfo();
                    buyNum.error = "";
                } else if (tradeInfo.orderType == 0) {// 出售的余额的校验
                    let aocount = new BigNumber(checkInfo.coinBixBalance || 0).times(tradeInfo.coinPrice ? tradeInfo.coinPrice : 0);
                    if (val > parseFloat(aocount)) {
                        buyNum.error = "";
                        buyNum.error = "";
                    } else {
                        buyPrice.error = "";
                        buyNum.error = "";
                    }
                } else {
                    buyNum.error = "";
                    buyPrice.error = "";
                }
            } else {
                buyNum.val = "";
                // 在input获取焦点的时候如果值为空也不显示提示信息但是失去焦点时候不能清空error
                if (!type) {
                    buyPrice.error = "";
                }
            }
            buyPrice.val = val;
        }
        this.setState({
            buyNum,
            buyPrice
        })
    };
    // 获取焦点的时候
    onInputFocus = e => {
        let name = e.target.name;
        let { buyNum, buyPrice } = this.state;
        let val = e.target.value;
        if (name === 'buyNum') {
            buyNum.error = "";
        } else if (name === 'buyPrice') {
            buyPrice.error = "";
        }
        this.tradeNumValid(val, name, 1);
    };
    // 失去焦点的时候
    onInputBlur = e => {
        let { buyNum, buyPrice } = this.state;
        let name = e.target.name;
        let val = event.target.value;
        if (name === 'buyNum') {
            if (val == "") {
                buyPrice.error = this.getInternationalInfo();
            }
        } else if (name === 'buyPrice') {
            if (val == "") {
                buyPrice.error = this.getInternationalInfo();
            }
        }
        this.tradeNumValid(val, name, 1);
    };
    // 点击购买按钮
    sumbit = () => {
        if (!this.state.isSubmit) {
            return;
        }
        this.setState({ isSubmit: false });
        let _this = this;
        const { buyNum, buyPrice, tradeInfo, checkInfo, id } = this.state;
        const { intl } = this.props;
        // 再进行一次校验
        this.tradeNumValid(buyNum.val, 'buyNum', 1);
        if (buyNum.val == "") {
            buyPrice.error = this.getInternationalInfo();
            this.setState({ isSubmit: true });
        }
        if (buyPrice.val == "") {
            buyPrice.error = this.getInternationalInfo();
            this.setState({ isSubmit: true });
        }
        if (buyNum.error != "" || buyPrice.error != "") {
            this.setState({ isSubmit: true });
        }
        if (buyNum.val != "" && buyNum.error == "" && buyPrice.val != "" && buyPrice.error == "") {
            //校验广告状态<>已下架
            //广告有效期<=7天
            //广告未完成订单数量<x
            //广告数量不足关闭吐司后刷新页面，更新数量
            let createDate = tradeInfo.orderTime != '--' ? moment(tradeInfo.orderTime).format(DATA_TIME_FORMAT) : '0000-00-00 00:00:00';
            let endDate = moment().subtract(checkInfo.adValidTimeConf, "days").format(DATA_TIME_FORMAT);
            if (endDate > createDate) {
                optPop(() => {
                    getAdvertiseInfo(id).then(res => {
                        _this.setState({ tradeInfo: res });
                    });
                }, intl.formatMessage({ id: "广告已失效" }));
                this.setState({ isSubmit: true });
            } else {
                // 保留几位小数的形式
                // let val = new BigNumber(buyNum.val).toFixed(checkInfo.coinBixDian);
                saveOrder(_this, tradeInfo, buyNum.val, tradeInfo.userId, buyPrice.val).then(res => {
                    if (res.code == 200) {
                        let id = res.data && res.data.recordId;
                        window.location.href = `/otc/orderDetail/${id}`;
                        setTimeout(() => {
                            this.setState({ isSubmit: true });
                        }, 500);
                    } else {
                        setTimeout(() => {
                            this.setState({ isSubmit: true });
                        }, 500);
                    }
                });
            }
        }
        this.setState({
            buyNum,
            buyPrice,
        })
    };
    // 获取多语言的提示
    getInternationalInfo = () => {
        let { tradeInfo } = this.state;
        let { intl } = this.props;
        let total = tradeInfo.coinTotalNumber * tradeInfo.coinPrice;
        let max = total < tradeInfo.maxNumber ? total : tradeInfo.maxNumber;
        let customerText = intl.formatMessage({ id: '下单金额应该在{num1}CNY到{num2}CNY之间' }, {
            num1: separator(new BigNumber(tradeInfo.minNumber || 0).toFixed(2)),
            num2: separator(new BigNumber(max || 0).toFixed(2))
        });
        return customerText;
    };

    async showUserCenter() {
        let userId = USERID || "";
        const { tradeInfo } = this.state;
        let res = await post('/web/common/getAvgPassTime', { targetUserId: tradeInfo.userId, userId });
        if (res.code == 200) {
            this.setState({
                targetId: tradeInfo.userId,
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

    checkUserInfo() {
        let { userInfor, intl } = this.props;
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
            return true;
        } else if (res.cardStatus == 4) {// 校验实名认证
            this.setState({
                identityConfirm: true,
                second: false,
            });
            return true;
        } else if (res.cardStatus == 7) { // 实名认证失败，重新认证
            this.setState({
                identityConfirm: true,
                second: true,
            });
            return true;
        } else if (res.cardStatus == 5) {
            let message = intl.formatMessage({ id: '身份认证审核中，请稍后再试' });
            optPop(() => {
            }, message, undefined, true);
            return true;
        }
        return false;
    }

    render() {
        const { formatMessage } = this.props.intl;
        const { tradeInfo, buyNum, buyPrice, checkInfo, tips, loading, adInfo } = this.state;
        return (
            <div className="buySell">
                {loading ?
                    ThemeFactory.getThemeInstance(Styles.ThemeB)
                    :
                    <div>
                        <div className="page_title margin_b20 buySell_top">
                            {tradeInfo.orderType == 1 ? formatMessage({ id: "购买xxx" }).replace('xxx', tradeInfo.coinTypeName) : formatMessage({ id: "出售xxx" }).replace('xxx', tradeInfo.coinTypeName)}
                            {
                                tradeInfo.orderType == 0 &&
                                <div className="account">
                                    <FormattedMessage id="法币账户可用余额"/>
                                    <span>：</span>
                                    <span
                                        className="count_num">{new BigNumber(checkInfo.coinBixBalance || 0).toFixed(checkInfo.coinBixDian)}{tradeInfo.coinTypeName}</span>
                                </div>
                            }
                        </div>
                        <div className="buySell_main">
                            <div className="buySell_input_box">
                                <div className={`form_val ${buyNum.error.length > 0 && 'err' }`}>
                                    <div className="input_box">
                                        <input className="form_input" autoComplete="off" name="buyNum"
                                               onBlur={this.onInputBlur} onFocus={this.onInputFocus}
                                               onChange={this.onInputChange} value={buyNum.val}/>
                                        <span>{tradeInfo.coinTypeName}</span>
                                    </div>
                                    <span className="ew">{buyNum.error}</span>
                                </div>
                                <div className="exchange">
                                    <svg className="exchange_svg" aria-hidden="true">
                                        <use xlinkHref="#icon-bizhongduihuan"/>
                                    </svg>
                                </div>
                                <div className={`form_val ${buyPrice.error.length > 0 && 'err' }`}>
                                    <div className="input_box">
                                        <input className="form_input" autoComplete="off" name="buyPrice"
                                               onBlur={this.onInputBlur} onFocus={this.onInputFocus}
                                               onChange={this.onInputChange} value={buyPrice.val}/>
                                        <span>CNY</span>
                                    </div>
                                    <span className="ew">{buyPrice.error}</span>
                                </div>
                            </div>
                            {/* 订单备注 */}
                            <div
                                className="buySell_ad_remarks"
                                style={{ display: isValEmpty(adInfo.remarks) ? 'none' : 'block'}}
                            >
                                <p>{formatMessage({id:"广告商备注"})}:</p>
                                <pre>{adInfo.remarks}</pre>
                            </div>

                            <div
                                className="buySell_details_line"
                                style={{ marginTop: isValEmpty(adInfo.remarks) ? '60px' : '0'}}
                            >
                                <div className="info">
                                    <div className="info_left">
                                        <span style={{ backgroundColor: tradeInfo.color, cursor: 'pointer' }}
                                              onClick={this.showUserCenter}>{getFirstStr(tradeInfo.userName)}</span>
                                        <i style={{ backgroundColor: tradeInfo.onlineStatus == 0 ? '#B9B9B9' : '#47CD85' }}/>
                                    </div>
                                    <div className="right" style={{ width: '110px' }}>
                                        <span className="name" onClick={this.showUserCenter}>{tradeInfo.userName}</span>
                                        <div>
                                            <FormattedMessage id="成交笔数"/>
                                            <span>{tradeInfo.orderTotal}</span>
                                        </div>
                                    </div>
                                </div>
                                <div className="right_table">
                                    <div className="buySell_th">
                                        <span className="text-right">{formatMessage({ id: '数量' })}</span>
                                        <span className="text-right limit">{formatMessage({ id: '限额' })}</span>
                                        <span className="text-right">{formatMessage({ id: '单价' })}</span>
                                        <span className="text-center">{formatMessage({ id: '支付方式' })}</span>
                                        <span className="text-center ">{formatMessage({ id: '付款时间' })}</span>
                                    </div>
                                    <div className="buySell_details_val">
                                        <span
                                            className="text-right">{new BigNumber(tradeInfo.coinTotalNumber || 0).toFixed(checkInfo.coinBixDian)}{tradeInfo.coinTypeName}</span>
                                        <span
                                            className="text-right limit">{`${separator(new BigNumber(tradeInfo.minNumber || 0).toFixed(2))}CNY ~ ${separator(new BigNumber(tradeInfo.maxNumber || 0).toFixed(2))}CNY`}</span>
                                        <span
                                            className="text-right">{`${separator(new BigNumber(tradeInfo.coinPrice || 0).toFixed(2))}CNY`}</span>
                                        <span className="text-center">
                                       {
                                           tradeInfo.paymentTypes && tradeInfo.paymentTypes.indexOf('1') != -1 &&
                                           <svg className="icon" aria-hidden="true">
                                               <use xlinkHref="#icon-ico_alipay"/>
                                           </svg>
                                       }
                                            {
                                                tradeInfo.paymentTypes && tradeInfo.paymentTypes.indexOf('2') != -1 &&
                                                <svg className="bank_icon" aria-hidden="true">
                                                    <use xlinkHref="#icon-ico_unionpay"/>
                                                </svg>
                                            }
                                   </span>
                                        <span
                                            className="text-center">{formatMessage({ id: '%%分钟' }).replace('%%', checkInfo.orderOverTime)}</span>
                                    </div>
                                </div>
                            </div>
                            <div className="btn_box">
                                <input type="button"
                                       className={`btn submit ${tradeInfo.orderType == 0 ? 'marginMore' : ''}`}
                                       value={tradeInfo.orderType == 1 ? formatMessage({ id: "立即购买" }) : formatMessage({ id: "立即出售" })}
                                       onClick={this.sumbit}/>
                                {
                                    tradeInfo.orderType == 0 && <a href="/bw/manage/account/fcurrency"
                                                                   style={{ cursor: 'pointer' }}>{formatMessage({ id: "余额不足？去划转" })}</a>
                                }
                            </div>
                            {
                                tradeInfo.orderType == 1 ?
                                    <div className="tip_message">
                                        <p><FormattedMessage id="温馨提示："/></p>
                                        <div style={{ textAlign: 'justify' }}
                                             dangerouslySetInnerHTML={{ __html: tips.replace('{x}', checkInfo.userCancleNumConf) }}
                                        />
                                    </div>
                                    :
                                    <div className="tip_message">
                                        <p><FormattedMessage id="温馨提示："/></p>
                                        <div style={{ textAlign: 'justify' }}
                                             dangerouslySetInnerHTML={{ __html: tips.replace('{x}', checkInfo.userCancleNumConf) }}
                                        />
                                    </div>
                            }
                            <ReactModal ref={modal => this.modalUser = modal}>
                                <UserCenter modal={this.modalUser} hoemPage={this.state.homePage}
                                            targetId={this.state.targetId} uid={this.state.uid}
                                />
                            </ReactModal>
                        </div>
                    </div>
                }
                 {
                    typeof(tradeInfo.coinTypeName)!='undefined'?<TitleSet titlemoney={`${tradeInfo.coinTypeName}`} titleval={tradeInfo.orderType == 1?"购买":"出售"} />:null
                }


            </div>
        )
    }
}

export default withRouter(injectIntl(BuySell));
