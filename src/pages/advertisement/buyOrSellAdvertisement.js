import React from 'react';
import Form from '../../decorator/form'
import { withRouter } from 'react-router';
import { connect } from 'react-redux';
import { FormattedMessage, injectIntl } from 'react-intl';
import '../../assets/style/advertisement/buyOrSellAdvertisement.less';
import SelectHistory from '../../components/selectHistory';
import payTypeChoose from "../../assets/image/pay_type_choose.png";
import { publishAdvertisement, init, getInitialize, getTips, getPublishNum } from "./buyOrSellAdvertisement.model";
import { isBusiness } from '../advertisement/index.model';
import { fetchPublicKey, optPop, separator, formatDecimalNoZero, add_one, isEmptyObject } from '../../utils';
import { USERID } from '../../conf';
import Confirm from '../../components/confirm';
import ALIPAY from '../../assets/image/ad/Alipay.png';
import UNIONPAY from '../../assets/image/ad/UnionPay.png';
import cookie from 'js-cookie'

const BigNumber = require('big.js');
// 获取模型数据

//coinList: state.account.detail,
@connect(
    state => ({
        userInfor: state.session.userInfor,
        coinData: state.session.coinData
    })
)
@Form
class BuyOrSellAdvertisement extends React.Component {
    constructor(props) {
        super(props);
        this.base = {
            price: '',// 固定单价
            buyNum: '', // 数量
            minLimit: '',// 最小限额
            maxLimit: '',// 最大限额
            fundpassword: '', // 资金密码,
            tips: '', // 温馨提示
            noPayment: false, // 未设置收款方式弹框
        };
        this.state = {
            type: 1, // 广告类型
            ...this.base,
            customerInfo: false,// 去用户信息的页面
            customerDetail: '',
            second: false,
            identityConfirm: false, // 身份认证
            coinId: '',// 币种id
            currency: '', // 币种名称
            currencyList: [], // 币种列表
            serviceCharge: 0, // 手续费
            expected: 0, // 实际到账量 || 实际冻结量
            tradeTotal: 0,//交易总价,
            payType: '0', // 支付方式
            bankPayType: '2',
            aliPayType: '0',
            publishNum: 0, // 可发布数量
            limitNum: 0, // 广告备注输入字数
            remarks: '', // 广告备注字段
            configInfo: {
                rate: '0.2%', // 手续费率
                limitDays: 7, // 有效期
                coinBixDian: 5,// 虚拟货币小数点位数
                legalBixDian: 2, // 法币小数点位数
                platformMin: 100, //平台最低限额
                platformMax: 500, //平台最大限额
                accountMoney: 20, //账户余额
                coinBixBalance: 0,
                adValidTimeConf: 7,
                buyFeeFull: '0%',
                sellFeeFull: '0%',

            },
            isSubmit: true,
        };
        this.toRate = this.toRate.bind(this);
        this.minOrMaxBlur = this.minOrMaxBlur.bind(this);
        this.hasEmpty = this.hasEmpty.bind(this);
        this.buyNumBlur = this.buyNumBlur.bind(this);
        this.submit = this.submit.bind(this)
    }

    componentWillMount() {
        !USERID && (window.location.href = '/bw/login')
    }

    componentDidMount() {
        if (localStorage.getItem('tempState')) {
            let tempState = localStorage.getItem('tempState');
            tempState = JSON.parse(tempState);
            this.setState({
                ...tempState
            }, () => {
                localStorage.removeItem('tempState');
                const { coinId } = this.state;
                this.setState({ coinId }) // 解决 出售货币的时候，跳到费率说明再返回下拉框值不变
            });
            // 需要重新获取下温馨提示的信息因为在费率说明那里会切换语言
            getTips(tempState.type).then(res => {
                this.setState({ tips: res });
            })
        } else {
            let type = this.props.match.params.type || 1;
            this.setState({ type });
            this.getInit();
        }
    }

    getInit = () => {
        // 请求初始化数据
        let { type, coinId, currency } = this.state;
        init(type).then(res => {
            let list = res.currencyList;
            if (list.length > 0) {
                coinId = list[0].val;
                currency = list[0].key;
            }
            const decimals = (res && res.getAdInitialize && res.getAdInitialize.coinBixDian) || 0;
            this.getPublishNumApi(`${currency}/CNY`, list[0].val, decimals);
            this.setState({
                coinId,
                currency,
                currencyList: res.currencyList,
                configInfo: res.getAdInitialize,
                tips: res.tips,
            })
        });
    };

    /**
     * @desc 广告类型选择
     * @param val 1 广告类型 出售 0 购买
     */
    handleChangeTime = (val) => {
        getTips(val).then(res => {
            this.setState({
                type: val,
                tips: res,
            });
            this.setThreeValue();
        })
    };

    /**
     * @desc 获取可发布数量接口
     * @param market 市场
     * @param coinTypeId 币种
     * @param decimals 要保留的小数位
     */
    getPublishNumApi = (market, coinTypeId, decimals) => {
        const params = {
            market,
            coinTypeId,
        };
        getPublishNum(params).then(res => {
            const publishNum = new BigNumber(res.data || 0).toFixed(decimals) || 0;
            this.setState({ publishNum });
        });
    };

    /**
     * @desc 币种下拉框选择
     * @param item 当前选择项
     */
    getCodeType = (item = {}) => {
        this.setState({
            coinId: item.val,
            currency: item.key
        });
        let market = `${item.key}/CNY`;
        getInitialize(market).then(res => {
            this.getPublishNumApi(market, item.val, res.coinBixDian);
            this.setState({ configInfo: res });
        });
    };
    payTypeChoose = (type) => {
        let val = this.state[type];
        if (type === 'bankPayType') {
            val == '2' ? (val = '0') : (val = '2')
        } else {
            val == '1' ? (val = '0') : (val = '1')
        }
        const { errors } = this.state;
        errors.paymentType = [];
        this.setState({
            [type]: val,
            errors
        })
    };
    onChange = e => {
        let { type, price, buyNum, tradeTotal, configInfo } = this.state;
        let name = e.target.name;
        let val = e.target.value;
        let rate = type == 0 ? configInfo.buyFee : configInfo.sellFee;
        let regLegal = new RegExp("^\\d+(?:\\.\\d{0," + configInfo.legalBixDian + "})?");
        let regCoin = new RegExp("^\\d+(?:\\.\\d{0," + configInfo.coinBixDian + "})?");
        let _val = 0;
        if (name === 'price') {
            if (val) {
                val = val.toString().match(regLegal);
                if (val === null) {
                    val = ''
                } else {
                    _val = (val[0].substr(val[0].length - 1, 1) == '.') ? parseInt(val[0]) : parseFloat(val[0]);
                    // 整数位数不能超过8位
                    let str = _val + "";
                    if (str.indexOf('.') != -1) {
                        if (str.split('.')[0].length > 8) {
                            return;
                        }
                    } else {
                        if (str.length > 8) {
                            return;
                        }
                    }
                    tradeTotal = _val.mul(buyNum || 0);
                    this.setState({ tradeTotal: add_one(tradeTotal, 2) });
                }
            }
        } else if (name === 'buyNum') {
            if (val) {
                val = val.toString().match(regCoin);
                if (val === null) {
                    val = ''
                } else {
                    _val = (val[0].substr(val[0].length - 1, 1) == '.') ? parseInt(val[0]) : parseFloat(val[0])
                    // 整数位数不能超过8位
                    let str = _val + "";
                    if (str.indexOf('.') != -1) {
                        if (str.split('.')[0].length > 8) {
                            return;
                        }
                    } else {
                        if (str.length > 8) {
                            return;
                        }
                    }
                    this.setThreeValue(_val);
                }
            }
        } else if (name === 'minLimit' || name === 'maxLimit') {
            if (val) {
                val = val.toString().match(regLegal);
                if (val === null) {
                    val = '';
                }
            }
        }
        this.setState({ [name]: val });
        // 广告备注限制字数（200）
        let limitNum = val.length >= 200 ? 200 : val.length;
        if (name === 'remarks') {
            this.setState({ limitNum });
        }
    };

    /**
     * 设置手续费，冻结量/到账量，交易总价
     * 参数为 购买/出售数量
     */
    setThreeValue(val) {
        let { type, price, buyNum, tradeTotal, expected, serviceCharge, configInfo } = this.state;
        let rate = type == 0 ? configInfo.buyFee : configInfo.sellFee;
        if (typeof val === 'undefined') {
            val = buyNum;
        }
        if (typeof(val) === 'object') {
            val = parseFloat(val[0])
        }
        tradeTotal = val.mul(price || 0);
        serviceCharge = val.mul(parseFloat(rate));
        if (type == 0) {
            expected = val - serviceCharge;
        } else if (type == 1) {
            expected = val + serviceCharge;
        }
        this.setState({
            tradeTotal: add_one(tradeTotal, 2),
            expected,
            serviceCharge,
        })
    }

    async submit() {
        if (!this.state.isSubmit) {
            return;
        }
        this.setState({ isSubmit: false });

        const { intl } = this.props;
        let _this = this;
        let { type, price, buyNum, minLimit, maxLimit, fundpassword, serviceCharge, currency, coinId, bankPayType, aliPayType, remarks } = this.state;
        let key = fetchPublicKey();
        typeof(price) === 'object' && (price = parseFloat(price[0]));
        typeof(buyNum) === 'object' && (buyNum = parseFloat(buyNum[0]));
        typeof(minLimit) === 'object' && (minLimit = parseFloat(minLimit[0]));
        typeof(maxLimit) === 'object' && (maxLimit = parseFloat(maxLimit[0]));
        let paymentType = '';
        if (aliPayType != '0' && bankPayType != '0') {
            paymentType = `${aliPayType},${bankPayType}`;
        } else if (aliPayType != '0') {
            paymentType = aliPayType;
        } else {
            paymentType = bankPayType;
        }
        let params = {
            remarks,
            orderType: type,
            coinTypeId: coinId,
            legalTypeId: 2,
            coinPrice: price,
            coinTotalNumber: buyNum,
            minNumber: minLimit,
            maxNumber: maxLimit,
            market: `${currency}/CNY`,
            fee: formatDecimalNoZero(serviceCharge || 0, 8),
            transPwd: key.encrypt(fundpassword),
            paymentType: paymentType,
        };
        if (this.hasEmpty()) { // 校验是否有空信息
            this.setState({ isSubmit: true });
            return;
        }
        if (this.checkInfo()) { // 未设置信息，浏览器输入url访问
            this.setState({ isSubmit: true });
            return;
        }
        // 校验当前用户是否设置了收款方式
        const { otcPaymentTypes } = this.props.userInfor.data;
        const isBindPayment = Array.isArray(otcPaymentTypes)
            && otcPaymentTypes.length > 0
            && otcPaymentTypes.every(item => item.enable === false);
        if (type == 1 && isBindPayment) { // 校验支付
            this.setState({
                noPayment: true,
                isSubmit: true,
            });
            return;
        }
        // 校验是否是商家
        isBusiness().then(res => {
            switch (res) {
                case 'business':
                    publishAdvertisement(params).then(res => {
                        if (res.code == 200) {
                            optPop(() => {
                                _this.props.history.go(-1);
                            }, res.msg);
                            setTimeout(() => {
                                this.setState({ isSubmit: true });
                            }, 500);
                        } else {
                            optPop(() => {
                            }, res.msg);
                            setTimeout(() => {
                                this.setState({ isSubmit: true });
                            }, 500);
                        }
                    });
                    break;
                case 'common':
                    optPop(() => {
                    }, intl.formatMessage({ id: '您没有发布广告权限，请先认证商家身份' }));
                    break;
                case 'approving':
                    optPop(() => {
                    }, intl.formatMessage({ id: '商家认证审核中，请稍后再试' }));
                    break;
                default:
                    this.setState({ isSubmit: true });
                    console.log('error');
            }
        });
    }

    hasEmpty() {
        const { intl } = this.props;
        const { type, errors, price, buyNum, minLimit, maxLimit, fundpassword, bankPayType, aliPayType } = this.state;
        if (!price) {
            errors.price = [intl.formatMessage({ id: "请输入固定单价" })];
        }
        if (!buyNum) {
            type == 0 ? (errors.buyNum = [intl.formatMessage({ id: "请输入购买数量" })]) : (errors.buyNum = [intl.formatMessage({ id: "请输入出售数量" })]);
        }
        if (!minLimit) {
            errors.minLimit = [intl.formatMessage({ id: "请输入最小限额" })];
        }
        if (!maxLimit) {
            errors.maxLimit = [intl.formatMessage({ id: "请输入最大限额" })];
        }
        if (!fundpassword) {
            errors.fundpassword = [intl.formatMessage({ id: "请输入资金密码" })];
        }
        if (bankPayType == '0' && aliPayType == '0') {
            errors.paymentType = [intl.formatMessage({ id: "至少选择一种支付方式" })];
        }
        // 如果errors是{} 说明没有错误提示
        if (isEmptyObject(errors)) {
            return false;
        }
        // 如果errors的某个属性，不是[]说明有错误提示
        let res = Object.keys(errors).every((key) => {
            return errors[key].length === 0;
        });
        if (!res) {
            this.setState({ errors });
            return true;
        } else {
            return false;
        }
    }

    cancel = () => {
        this.props.history.go(-1);
    };

    toRate() {
        localStorage.setItem('tempState', JSON.stringify(this.state));
        this.props.history.push('/otc/rateStatement');
    }

    minOrMaxBlur(flag) {
        const { intl } = this.props;
        let { type, errors, minLimit, maxLimit, configInfo, tradeTotal } = this.state;
        let configMin = type == 0 ? configInfo.buyMinNum : configInfo.sellMinNum;
        let configMax = type == 0 ? configInfo.buyMaxNum : configInfo.sellMaxNum;

        if (flag === 'min') {
            if (!minLimit) {
                errors.minLimit = [intl.formatMessage({ id: "请输入最小限额" })];
            } else {
                // 当前小于平台最小 就填充平台最小
                if (minLimit < configMin) {
                    minLimit = configMin;
                } else {
                    let total = parseFloat(new BigNumber(tradeTotal || 0).toFixed(configInfo.legalBixDian));
                    if (total > configMax) {
                        if (minLimit > configMax) {
                            minLimit = configMin;
                        }
                    } else {
                        if (minLimit > total) {
                            minLimit = configMin;
                        }
                    }
                }
            }
        } else {
            if (!maxLimit) {
                errors.maxLimit = [intl.formatMessage({ id: "请输入最大限额" })];
            } else {
                let total = parseFloat(new BigNumber(tradeTotal || 0).toFixed(configInfo.legalBixDian));
                if (total > configMax) {
                    if (maxLimit > configMax) {
                        maxLimit = configMax;
                    }
                } else {
                    if (maxLimit > total) {
                        maxLimit = total;
                    }
                }
            }
        }
        this.setState({
            errors,
            minLimit,
            maxLimit,
        })
    }

    buyNumBlur() {
        const { intl } = this.props;
        let { type, buyNum, serviceCharge, expected, errors, configInfo } = this.state;
        if (!buyNum) {
            type == '1' && (errors.buyNum = [intl.formatMessage({ id: "请输入出售数量" })]);
            type == '0' && (errors.buyNum = [intl.formatMessage({ id: "请输入购买数量" })]);
        }
        // else if (type == '1') {
        //     let rate = configInfo.sellFee;
        //     if (parseFloat(buyNum[0]) + serviceCharge > configInfo.coinBixBalance) {
        //         // 不联动了要用户自己输入，最后提交的时候给提示
        //         buyNum = configInfo.coinBixBalance / (1 + configInfo.sellFee)
        //         console.log(new BigNumber(buyNum || 0).toFixed(configInfo.coinBixDian))
        //         buyNum = new BigNumber(buyNum || 0).toFixed(configInfo.coinBixDian)
        //         serviceCharge = new BigNumber(configInfo.coinBixBalance ).minus(buyNum).toFixed() ;
        //         expected = parseFloat(buyNum) + serviceCharge;
        //     }
        // }
        this.setState({
            buyNum,
            expected,
            serviceCharge,
        })
    }

    goSetPayment(type) {
        if (type === 'sure') {
            window.location.href = '/bw/mg/account';
        }
        this.setState({ noPayment: false });
    }

    checkInfo() {
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
                customerDetail: str,
            });
            return true
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

    cusInfoConfirm = (flag, tag) => {
        if (flag === 'sure') {
            if (tag == 1) {
                // 去用户中心
                window.location.href = '/bw/mg/account';
            } else {
                // 去身份认证
                const { cardStatus } = this.props.userInfor && this.props.userInfor.data;
                const { type } = this.state;

                if (cardStatus == 5 || cardStatus == 7) {
                    window.location.href = '/bw/mg/authenOne';
                } else {
                    window.location.href = '/bw/mgs/authenOnes';
                }
                localStorage.setItem('redirectUrl', '/otc/buyOrSellAdvertisement/' + type);
            }
        }
        this.setState({
            customerInfo: false, // 去用户信息的页面
            identityConfirm: false, // 身份认证
        })
    };

    render() {
        const { formatMessage } = this.props.intl;
        const {
            configInfo, errors, type, currency, currencyList, serviceCharge, expected, tradeTotal,
            price, buyNum, minLimit, maxLimit, fundpassword, bankPayType, aliPayType, coinId, tips, noPayment,
            customerInfo, identityConfirm, second, customerDetail, remarks, limitNum, publishNum
        } = this.state;
        const { fIn, bOut } = this; // 从form中获取的方法
        const { price: eprice = [], buyNum: ebuyNum = [], minLimit: eminLimit = [], maxLimit: emaxLimit = [], fundpassword: efundpassword = [], paymentType: epaymentType } = errors;
        const LAN = cookie.get('zlan');
        const rateFlag = type == 0 ? configInfo.buyFee : configInfo.sellFee;

        return (
            <div className="buy_or_sell_content" style={{ minHeight: '100vh' }}>
                <div className="page_title margin_b20"><FormattedMessage id="发布广告"/></div>
                <div className="content_box">
                    <div className="explain_message">
                        <div className="explain"><FormattedMessage id="说明："/></div>
                        <div>
                            {rateFlag != 0 &&
                            <div>
                                <FormattedMessage id="1.平台会对广告主收取每笔成交金额的{rate}作为手续费"
                                                  values={{ rate: type == 0 ? configInfo.buyFeeFull : configInfo.sellFeeFull }}/>
                                <i className="iconfont icon-bangzhuzhongxin"/>
                                <a onClick={this.toRate}>{formatMessage({ id: "费率说明" })}</a>
                            </div>
                            }
                            {rateFlag == 0 && <FormattedMessage id="1.目前平台不收取任何手续费"/>}
                        </div>
                        <div>
                            <FormattedMessage id="2.广告有效期为{days}天" values={{ days: configInfo.adValidTimeConf }}/>
                        </div>
                        {/*
                            type == '1' &&<p> <FormattedMessage id="3.账户余额大于或等于平台最低出售数量{x}广告才可发布成功" values={{x: parseFloat(configInfo.sellMinNum) + parseFloat(configInfo.sellMinNum) * parseFloat(configInfo.sellFee)}} /> </p>
                        */}
                    </div>
                    <div className="form">
                        <div className="entrust-time radio clearfix">
                            <h5 className="padl40 padl10" style={{ fontWeight: '400', marginTop: '3px' }}>
                                <FormattedMessage id="广告类型"/></h5>
                            <ul className="tab-time">
                                <li onClick={() => this.handleChangeTime(1)}>
                                    <label
                                        className={type == 1 ? "iconfont icon-danxuan-yixuan" : 'iconfont icon-danxuan-moren'}
                                    />
                                    <FormattedMessage id="出售货币"/>
                                </li>
                                <li onClick={() => this.handleChangeTime(0)}>
                                    <label
                                        className={type == 0 ? "iconfont icon-danxuan-yixuan" : 'iconfont icon-danxuan-moren'}
                                    />
                                    <FormattedMessage id="购买货币"/>
                                </li>
                            </ul>
                        </div>
                        <div className="form_item">
                            <div className="form_label"><FormattedMessage id="币种"/></div>
                            <div className="form_val clearfix">
                                <div className="record-head entrust-selcet">
                                    <SelectHistory
                                        options={currencyList}
                                        defaultValue={coinId}
                                        Cb={this.getCodeType}
                                    />
                                </div>
                            </div>
                        </div>
                        <div className="form_item">
                            <div className="form_label"><FormattedMessage id="市场"/></div>
                            <div className="form_val clearfix">
                                <div className="record-head entrust-selcet">
                                    <div className="select-history" style={{ cursor: 'default' }}>
                                        <div className="select-val">CNY<i className="per-icon-jiao"/></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div className={`form_item ${eprice && eprice[0] && 'err' }`}>
                            <div className="form_label"><FormattedMessage id="固定单价"/></div>
                            <div className="form_val">
                                <input type="text" className="lj"/>
                                <div className="input_box">
                                    <input className="form_input" autoComplete="off" maxLength="30" onFocus={fIn}
                                           onBlur={bOut} name="price" value={price}
                                           placeholder={formatMessage({ id: "请输入固定单价" })} onChange={this.onChange}/>
                                    <span>CNY</span>
                                </div>
                                <input type="text" className="lj"/>
                                <span className="ew">{eprice && eprice[0]}</span>
                            </div>
                        </div>
                        <div className={`form_item ${ebuyNum && ebuyNum[0] && 'err' }`}>
                            <div className="form_label">
                                <span>{type == 0 ? formatMessage({ id: "购买数量" }) : formatMessage({ id: "出售数量" })}</span>
                            </div>
                            <div className="form_val">
                                <input type="text" className="lj"/>
                                <div className="input_box">
                                    <input className="form_input" autoComplete="off" maxLength="30" onFocus={fIn}
                                           onBlur={this.buyNumBlur} name="buyNum" value={buyNum}
                                           placeholder={type == 0 ? formatMessage({ id: "请输入购买数量" }) : formatMessage({ id: "请输入出售数量" })}
                                           onChange={this.onChange}/>
                                    <span>{currency}</span>
                                </div>
                                <input type="text" className="lj"/>
                                <span className="ew">{ebuyNum && ebuyNum[0]}</span>
                            </div>
                        </div>
                        <div className="trade_info">
                            {
                                type == 1
                                &&
                                <div className="info_line">
                                    <span/>
                                    <div className="account_money">
                                        <i className="iconfont icon-bangzhuzhongxin publish_icon"/>
                                        <div className="tip_msg">
                                            <div>
                                                <p>
                                                    <FormattedMessage
                                                        id="情况一：当法币账户余额 ≥（保证金*80% - 在途广告数量）"/>
                                                </p>
                                                <p>
                                                    <FormattedMessage
                                                        id="可发布数量=保证金*80% - 在途广告数量"/>
                                                </p>
                                                <p>
                                                    <FormattedMessage id="情况二：当法币账户余额 <（保证金*80% - 在途广告数量）"/>
                                                </p>
                                                <p>
                                                    <FormattedMessage
                                                        id="可发布数量=法币账户余额"/>
                                                </p>
                                            </div>
                                        </div>
                                        <span>{formatMessage({ id: "可发布数量" })}：</span>
                                        <span>{publishNum}{currency}</span>
                                    </div>
                                </div>
                            }
                            <div className="info_line">
                                <span>{formatMessage({ id: "总手续费" })}({type == 0 ? configInfo.buyFeeFull : configInfo.sellFeeFull})</span>
                                <span>{formatDecimalNoZero(serviceCharge || 0, 8)}{currency}</span>
                            </div>
                            <div className="info_line">
                                <span>{type == 0 ? formatMessage({ id: "预计到账量" }) : formatMessage({ id: "实际冻结量" })}</span>
                                <span>{formatDecimalNoZero(expected || 0, 8)}{currency}</span>
                            </div>
                            <div className="info_line">
                                <FormattedMessage id="交易总价"/>
                                <span>{separator(new BigNumber(tradeTotal || 0).toFixed(configInfo.legalBixDian))}CNY</span>
                            </div>
                        </div>
                        <div className="form_item bottom_line">
                            <div className="form_label"><FormattedMessage id="单笔限额"/></div>
                            <div className="limit_money">
                                <div className={`form_val ${eminLimit && eminLimit[0] && 'err' }`}>
                                    <div className="limit_title"><FormattedMessage id="最小限额（一次交易的最低交易限制）"/></div>
                                    <input type="text" className="lj"/>
                                    <div className="input_box">
                                        <input className="form_input" autoComplete="off" onFocus={fIn} onBlur={() => {
                                            this.minOrMaxBlur('min')
                                        }} name="minLimit" value={minLimit}
                                               placeholder={type == 0 ? configInfo.buyMinNum : configInfo.sellMinNum}
                                               onChange={this.onChange}/>
                                        <span>CNY</span>
                                    </div>
                                    <input type="text" className="lj"/>
                                    <span className="ew">{eminLimit && eminLimit[0]}</span>
                                </div>
                                <div className="line"/>
                                <div className={`form_val ${emaxLimit && emaxLimit[0] && 'err' }`}>
                                    <div className="limit_title"><FormattedMessage id="最大限额（一次交易的最高交易限制）"/></div>
                                    <input type="text" className="lj"/>
                                    <div className="input_box">
                                        <input className="form_input" autoComplete="off" onFocus={fIn} onBlur={() => {
                                            this.minOrMaxBlur('max')
                                        }} name="maxLimit" value={maxLimit}
                                               placeholder={type == 0 ? configInfo.buyMaxNum : configInfo.sellMaxNum}
                                               onChange={this.onChange}/>
                                        <span>CNY</span>
                                    </div>
                                    <input type="text" className="lj"/>
                                    <span className="ew">{emaxLimit && emaxLimit[0]}</span>
                                </div>
                            </div>
                        </div>
                        {
                            type == 0
                            &&
                            <div className="form_item">
                                <div className="form_label"><FormattedMessage id="付款方式"/></div>
                                <div className={`pay_type_box form_val ${epaymentType && epaymentType[0] && 'err'}`}>
                                    <div className={`pay_type_item ${bankPayType == '2' ? 'choose_item' : ''}`}
                                         onClick={() => this.payTypeChoose('bankPayType')}>
                                        {LAN === 'cn' && <svg className="pay_type" aria-hidden="true">
                                            <use xlinkHref="#icon-shoukuanfangshiyinlian"/>
                                        </svg>}
                                        {LAN !== 'cn' && <div style={{
                                            width: '80px',
                                            height: '18px',
                                            backgroundImage: `url(${UNIONPAY})`,
                                            backgroundSize: '100% 100%'
                                        }}/>}
                                        {bankPayType == '2' && <img src={payTypeChoose} alt=""/>}
                                    </div>
                                    <div className={`pay_type_item ${aliPayType == '1' ? 'choose_item' : ''}`}
                                         onClick={() => this.payTypeChoose('aliPayType')}>
                                        {LAN === 'cn' && <svg className="pay_type" aria-hidden="true">
                                            <use xlinkHref="#icon-shoukuanfangshizhifubao"></use>
                                        </svg>}
                                        {LAN !== 'cn' && <div style={{
                                            width: '80px',
                                            height: '18px',
                                            backgroundImage: `url(${ALIPAY})`,
                                            backgroundSize: '100% 100%'
                                        }}/>}
                                        {aliPayType == '1' && <img src={payTypeChoose} alt=""/>}
                                    </div>
                                    <span className="ew">{epaymentType && epaymentType[0]}</span>
                                </div>
                            </div>
                        }
                        <div className={`form_item ${efundpassword && efundpassword[0] && 'err' }`}>
                            <div className="form_label"><FormattedMessage id="资金密码"/></div>
                            <div className="form_val">
                                <input type="text" className="lj"/>
                                <div className="input_box">
                                    <input type="password" style={{ position: 'absolute', left: '-99999px' }}/>
                                    <input className="form_input" autoComplete="off" onFocus={fIn} onBlur={bOut}
                                           type="password" name="fundpassword" value={fundpassword}
                                           placeholder={formatMessage({ id: "请输入资金密码" })} onChange={this.onChange}/>
                                </div>
                                <input type="text" className="lj"/>
                                <span className="ew">{efundpassword && efundpassword[0]}</span>
                            </div>
                        </div>
                        <div className="form_item">
                            <div style={{ color: '#fff' }}><FormattedMessage id="广告备注"/></div>
                            <div className="form_val">
                                <div className="area_wrap">
                                    <textarea
                                        maxLength="200" name="remarks" value={remarks}
                                        className="buy_or_sell_area"
                                        autoComplete="off" onChange={this.onChange}
                                        placeholder={formatMessage({ id: '广告备注将显示在广告详情中，请谨慎填写' })}
                                    />
                                    <span className="limit_num">{limitNum}/200</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    {
                        type == 0 ?
                            <div className="tip_message">
                                <p><FormattedMessage id="温馨提示："/></p>
                                <div style={{ textAlign: 'justify' }} dangerouslySetInnerHTML={{ __html: tips }}/>
                            </div>
                            :
                            <div className="tip_message">
                                <p><FormattedMessage id="温馨提示："/></p>
                                <div style={{ textAlign: 'justify' }} dangerouslySetInnerHTML={{ __html: tips }}/>
                            </div>
                    }
                    <div className="btn_box">
                        <input type="button" className="btn cancel" value={formatMessage({ id: "取消" })}
                               onClick={this.cancel}/>
                        <input type="button" className="btn submit" value={formatMessage({ id: "确定" })} onClick={() => {
                            this.submit()
                        }}/>
                    </div>
                </div>
                {
                    noPayment &&
                    <Confirm safeIcon={true} okText={formatMessage({ id: "前往设置" })}
                             content={formatMessage({ id: "您未设置收款方式" })} cb={type => this.goSetPayment(type)}/>
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
            </div>
        )
    }
}

export default withRouter(injectIntl(BuyOrSellAdvertisement));
