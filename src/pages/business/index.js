import React from 'react';
import { post, get } from 'nets';
import { connect } from 'react-redux';
import { DOMAIN_VIP, USERID } from '../../conf';
import { FormattedMessage, injectIntl } from 'react-intl';
import { withRouter } from 'react-router';
import SelectHistory from '../../components/selectHistory';
import Confirm from '../../components/confirm';
import ReactModal from '../../components/popBox';
import PendingList from '../../components/authen/pendingList';
import RecentList from '../../components/authen/recentList';
import {
    getOrederList,
    initData,
    getAuthen,
    getTopall,
    getTodayData,
    getTopallData,
    getLockStatus,
    linkUrl,
    isEnoughBail,
    getDelayData,
} from './index.model'
import Form from '../../decorator/form'
import { fetchPublicKey, optPop, formatDecimal } from '../../utils';
import cookie from 'js-cookie'
import { ThemeFactory, Styles } from '../../components/transition';
import UserCenter from '../../components/user/userCenter'
import {pageClick} from "../../redux/module/session";
import TitleSet from "../../components/setTitle";

import '../../assets/style/business/index.less';
import IMG_VIP from '../../assets/image/business/vip.png';
import IMG_TEL from '../../assets/image/business/tele.png';
import IMG_STAR from '../../assets/image/business/star.png';
import IMG_REFRESH from '../../assets/image/business/refresh.png';
import IMG_REFRESH_ON from '../../assets/image/business/refresh_on.png';
import TIPS from '../../assets/image/tips.png'
import axios from "axios";

const BigNumber = require('big.js');

@connect(
    state => ({
        coinData: state.session.coinData,
        userInfor: state.session.userInfor,
        moneylogo: state.money.locale,
    }),
    {
        pageClick
    }
)
@Form
class Business extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            refreshOn: '',
            transPwd: false, // 是否设置资金密码
            includeCancel: false,
            recentData: {},
            pendingData: {},
            identityConfirm: false,
            storeList: {},
            storeStatus: '',
            visibility: 'hidden',
            bail: '',
            errors: [],
            market: [
                { val: 0, key: 'BTC/USDT' }
            ],
            marketFlag: '',
            topallData: {
                lastPrice: '',
                high: '',
                low: '',
                volume: '',
            },
            todayData: {},
            fundpassword: '',
            currencyFlag: 'CNY',
            coins: {},
            submitPengding: false,
            exchangeRate: {
                exchangeRateBTC: {},
                exchangeRateUSD: {},
            },
            topallExchange: 0,
            showFlag:false
        };
        this.checkBoxChange = this.checkBoxChange.bind(this);
        this.iconHover = this.iconHover.bind(this);
        this.mouseLeave = this.mouseLeave.bind(this);
        this.pendingPageClick = this.pendingPageClick.bind(this);
        this.recentPageClick = this.recentPageClick.bind(this);
        this.identityCllback = this.identityCllback.bind(this);
        this.chooseBail = this.chooseBail.bind(this);
        this.onChange = this.onChange.bind(this);
        this.cancel = this.cancel.bind(this);
        this.getChangeType = this.getChangeType.bind(this);
        this.toOtherPage = this.toOtherPage.bind(this);
        this.startAuthen = this.startAuthen.bind(this);
        this.resetExchange = this.resetExchange.bind(this);
        this.checkPwd = this.checkPwd.bind(this);
        this.calcNum = this.calcNum.bind(this);
        this.showUserCenter = this.showUserCenter.bind(this);
        this.refreshAll = this.refreshAll.bind(this);
    }

    componentWillMount() {
        !USERID && (window.location.href = '/bw/login');
    }

    componentDidMount() {
        this.getPendingList(1);
        this.getRecentList(1);
        this.setState({ loading: true });
        initData(this).then(res => {
            this.setState({
                // market: res.coinTypeList,
                // marketFlag: res.coinTypeList[0].key,
                bail: Object.keys(res.storeList)[0],
                visibility: 'inherit',
                transPwd: !!this.props.userInfor.data.transPwd,
                ...res,
            },() =>{
                this.setState({
                    showFlag: true
                })
            });
            getDelayData(this).then(async res1 => {
                await this.setState({
                    market: res1.coinTypeList,
                    marketFlag: res1.coinTypeList[0].key,
                    ...res1,
                });
                this.resetExchange(res1.coinTypeList[0].key, res1.topallData.lastPrice, 'topallExchange');
            });
        });
        this.startInterval();
        this.props.pageClick(0);
    }

    componentWillReceiveProps(nextProps) {
        if (this.props.moneylogo.name != nextProps.moneylogo.name) { // 更改了顶部币种
            const { marketFlag, topallData } = this.state;
            this.resetExchange(marketFlag, topallData.lastPrice, 'topallExchange');
        }
    }

    componentDidUpdate(){

        // this.m = true; //获取title修改的最终变化值
    }

    componentWillUnmount() {
        if (this.interval) {
            clearInterval(this.interval);
        }
        // this.m = null;
    }

    startInterval() {
        if (this.interval) {
            clearInterval(this.interval)
        }
        this.interval = setInterval(async () => {
            const { marketFlag } = this.state;
            // let topall = await getTopall();
            // let topallData = getTopallData(marketFlag, topall);
            // this.setState({
            //     topallData
            // }, () => {
            //     this.resetExchange(this.state.marketFlag, this.state.topallData.lastPrice, 'topallExchange');
            // })
            getTopall().then(res => {
                let topallData = getTopallData(marketFlag, res);
                this.resetExchange(marketFlag, topallData.lastPrice, 'topallExchange');
                this.setState({ topallData });
            });
        }, 3000)
    }

    getChangeType = (item = {}) => {
        let res = getTopallData(item.key, this.state.topall);
        this.resetExchange(item.key, res.lastPrice, 'topallExchange');
        this.setState({
            topallData: res,
            marketFlag: item.key,
        });
    };

    // 汇率计算
    async resetExchange(flag, num, key) {
        let { exchangeRate, currencyFlag } = this.state;
        // let coins = await getMarketCoins(this);
        if (typeof flag === 'undefined') {
            return false;
        }
        flag = flag.split('/')[1].slice(0, 3);
        let rate = 1;
        Object.keys(exchangeRate).map(item => {
            if (item.indexOf(flag) > -1) {
                let currency = this.props.moneylogo.name;
                rate = exchangeRate[item][currency] || 1;
                currencyFlag = this.props.moneylogo.logo;
            }
        });
        let res = num && formatDecimal(num * rate, 2);
        this.setState({
            [key]: res,
            currencyFlag,
            loading: false,
        })
    }

    checkBoxChange = () => {
        this.setState({
            includeCancel: !this.state.includeCancel,
        })
    };

    calcNum(num) {
        if (parseFloat(num) > 1000000000) {
            return (formatDecimal(new BigNumber(num / 1000000000 || 0), 2) + 'B')
        } else if (parseFloat(num) > 1000000) {
            return (formatDecimal(new BigNumber(num / 1000000 || 0), 2) + 'M')
        } else {
            return formatDecimal(new BigNumber(num || 0), 2)
        }
    }

    iconHover(flag) {
        this.setState({ refreshOn: flag });
    }

    mouseLeave() {
        this.setState({ refreshOn: '' });
    }

    pendingPageClick(currPage) {
        this.getPendingList(currPage);
    }

    getPendingList(currPage) {
        getOrederList(currPage, '1,2', this, true).then(res => {
            this.setState({ pendingData: res })
        });
        getOrederList(currPage, '5,6', this, true).then(res => {
            this.setState({ unusual: res.totalCount });
        })
    }

    recentPageClick(currPage) {
        this.getRecentList(currPage);
    }

    getRecentList(currPage) {
        getOrederList(currPage, '3,4', this, true).then(res => {
            this.setState({ recentData: res });
        })
    }

    async identityCllback(type) {
        if (type == 'sure') {
            let authData = await axios.get(DOMAIN_VIP + '/manage/auth/authenticationJson');
            const { authStatus } = authData && authData.data && authData.data.datas;

            if (authStatus == 5 || authStatus == 7) {
                window.location.href = '/bw/mg/authenOne';
            } else {
                window.location.href = '/bw/mgs/authenOnes';
            }
            localStorage.setItem('redirectUrl', '/otc/business');
        }
        this.setState({ identityConfirm: false });
    }

    chooseBail(val) {
        this.setState({
            bail: val
        }, () => {
            this.openBail();
        })
    }

    onChange(e) {
        const { errors } = this.state;
        let name = e.target.name;
        let val = e.target.value;
        this.setState({
            [name]: val,
            errors,
        }, () => {
            this.openBail();
        })
    }

    checkPwd() {
        const { errors, fundpassword } = this.state;
        const { intl } = this.props;
        if (!fundpassword) {
            errors.fundpassword = [intl.formatMessage({ id: "请输入正确的资金密码" })];
        } else {
            errors.fundpassword = [];
        }
        this.setState({
            errors,
        }, () => {
            this.openBail();
        });
    }

    refresh = async type => {
        switch (type) {
            case 'today':
                let todayData = await getTodayData(this, true);
                this.setState({ todayData });
                break;
            case 'pending':
                this.getPendingList(1);
                break;
            case 'recent':
                this.getRecentList(1);
                break;
            default:
        }
        this.setState({ refreshOn: '' });
    };

    refreshAll() {
        this.getPendingList(1);
        this.getRecentList(1);
    }

    async cancel(type) {
        const { formatMessage } = this.props.intl;
        if (type == 'sure') {
            let { storeList, bail, fundpassword, errors, balance, submitPengding } = this.state;
            if (balance < storeList[bail]) {
                optPop(() => {
                }, formatMessage({ id: '保证金金额不足' }));
                return;
            }
            if (!fundpassword) {
                errors.fundpassword = [formatMessage({ id: '请输入正确的资金密码' })];
                this.setState({
                    errors
                }, () => {
                    this.openBail();
                });
                return;
            }
            if (submitPengding) {
                return;
            }
            let key = fetchPublicKey();
            let safePwd = key.encrypt(fundpassword);
            this.setState({
                submitPengding: true
            }, async () => {
                this.openBail();
                let res = await isEnoughBail();
                if (res) {
                    setTimeout(() => {
                        getAuthen(safePwd, parseInt(bail)).then(res => {
                            if (res) {
                                optPop(() => {
                                }, formatMessage({ id: '提交成功，请耐心等待审核' }));
                                this.modal.closeModal();
                                this.setState({
                                    storeStatus: 0,
                                    submitPengding: false,
                                })
                            } else {
                                this.setState({
                                    submitPengding: false
                                }, () => {
                                    this.openBail();
                                })
                            }
                        })
                    }, 1000);
                } else {
                    optPop(() => {
                    }, formatMessage({ id: '保证金金额不足' }));
                    this.setState({
                        submitPengding: false
                    }, () => {
                        this.openBail();
                    })
                }
            })
        } else {
            this.modal.closeModal();
        }
    }

    // 保证金的弹窗
    async openBail(first) {
        if (first) {
            let storeList = await get('/web/v1/store/storeList');
            this.setState({
                balance: storeList.balance,
                errors: {},
                fundpassword: '',
                bail: Object.keys(storeList.data)[0],
            }, () => {
                this.bailModal();
            })
        } else {
            this.bailModal();
        }
    };

    bailModal() {

        const { storeList, bail, fundpassword, errors, balance, transPwd, submitPengding} = this.state;
        const { fundpassword: efundpassword = [] } = errors;
        const { fIn, bOut } = this; // 从form中获取的方法
        const { formatMessage } = this.props.intl;
        let str =
            <div className="Jua-table-inner Jua-table-main bail_modal" style={{ width: '570px' }}>
                <div>
                    <FormattedMessage id='您法币账户中USDT余额为：{xxx}'
                                      values={{ 'xxx': <span style={{ color: '#fff' }}>{balance}</span> }}/>
                    <span style={{ color: '#fff' }}> USDT</span>
                    <a style={{ color: '#3E85A2', cursor: 'pointer' }}
                       href='/bw/manage/account/balance'> {formatMessage({ id: '点击充值' })}</a>
                </div>
                <div className="entrust-head-type clearfix bail-head-type" style={{ marginLeft: '0' }}>
                    <div className="bailTips" style={{ margin: '10px 0', position: 'relative' }}>
                        <span style={{ color: '#ffffff' }}><FormattedMessage id="缴纳保证金"/></span>
                        <span className="bailHelp">
                		<span className="tips_img"><img src={TIPS} style={{ width: '100%', height: '100%' }}/></span>
		                <span className="ew">{formatMessage({ id: '保证金与可发布广告的总量相关联' })}</span>
                	</span>
                    </div>
                    {
                        Object.keys(storeList).map((item, index) => {
                            return (
                                <span key={index} className={`currency_label ${bail == item ? 'curency_choose' : ''}`}
                                      onClick={() => this.chooseBail(item)}>{storeList[item]} USDT</span>
                            )
                        })
                    }
                </div>
                {
                    transPwd ?
                        <div className="test-form" style={{ width: '100%', margin: '10px 0' }}>
                            <div className={efundpassword && efundpassword[0] && 'err'}>
                                <h3 style={{ margin: '10px 0' }}>{formatMessage({ id: "资金密码" })}</h3>
                                <input type="password" className="lj"/>
                                <input type="password" style={{ position: 'absolute', left: '-99999px' }}/>
                                <input className="form_input" autoComplete="off" maxLength='20' onFocus={fIn}
                                       onBlur={this.checkPwd} type="password" name="fundpassword" value={fundpassword}
                                       placeholder={formatMessage({ id: "请输入资金密码" })}
                                       onChange={(e) => this.onChange(e)}/>
                                <input type="password" className="lj"/>
                                <span className="ew">{efundpassword && efundpassword[0]}</span>
                            </div>
                        </div>
                        :
                        <div className="test-form" style={{ width: '100%', margin: '10px 0' }}>
                            <h3 style={{ margin: '10px 0' }}>{formatMessage({ id: "资金密码" })}</h3>
                            <div className="business_setPwd" onClick={() => {
                                window.location.href = '/bw/mg/setPayPwd'
                            }}>
                                {formatMessage({ id: "请设置xxx" }).replace('xxx', formatMessage({ id: '资金密码' }))}
                            </div>
                        </div>
                }
                <div style={{ margin: '10px 0' }}>
                    {formatMessage({ id: "提示：取消商家认证后，保证金会退还至法币账户余额中" })}
                </div>
                <div className="but_box">
                    <input type="button" className="btn cancel" value={formatMessage({ id: "取消认证" })}
                           onClick={() => this.cancel('cancel')}/>
                    <input type="button" className={`btn submit margin_r0 ${submitPengding ? 'stop' : null}`}
                           value={formatMessage({ id: "继续认证" })} onClick={() => this.cancel('sure')}/>
                </div>
            </div>;

        this.setState({
            modalHTML: str
        }, () => {
            this.modal.openModal();
        });
    }

    toOtherPage(url) {
        const { authStatus, storeStatus } = this.state;
        if (url.indexOf('buyOrSellAdvertisement') > -1 && (authStatus != 6 || storeStatus != 1)) {
            return
        }
        this.props.history.push(url);
    }

    startAuthen() {
        const { formatMessage } = this.props.intl;
        const { authStatus, includeCancel, storeStatus } = this.state;
        if (!includeCancel) {
            return;
        }
        if (storeStatus == 0) {
            return;
        }
        if (authStatus == 5) {
            optPop(() => {
            }, formatMessage({ id: '身份认证审核中，请稍后再试' }));
            return
        }
        if (authStatus != 6) {
            this.setState({ identityConfirm: true });
            return;
        }
        getLockStatus().then(res => {
            if (res) {
                optPop(() => {
                }, formatMessage({ id: '您输入资金密码错误次数已达上限，请24小时候尝试' }));
            } else {
                this.openBail(true);
            }
        })
    }

    async showUserCenter(item) {
        let targetUserId = item.dealType == 'sell' ? item.buyUserId : item.sellUserId;
        let userId = USERID || "";
        let res = await post('/web/common/getAvgPassTime', { targetUserId: targetUserId, userId });
        if (res.code == 200) {
            this.setState({
                targetId: targetUserId,
                uid: USERID,
                homePage: res.data,
            }, () => {
                this.modalUser.openModal();
            })
        } else {
            optPop(() => {
            }, res.msg, { timer: 1500 })
        }
    }

    render() {
        const { formatMessage } = this.props.intl;
        const {
            includeCancel, pendingData, recentData, identityConfirm, market, topallData, todayData, unusual,
            authStatus, storeStatus, storeReason, marketFlag, topallExchange, loading, currencyFlag, visibility,
            coins, refreshOn, exchangeRate,showFlag
        } = this.state;

        const LAN = cookie.get('zlan');
        let mFlag = marketFlag ? marketFlag.split('/')[1] : 'USDT';
        // let todayDataCoin = coins['BTC'] || 2

        return (
            <div className="business_main">
                {
                    loading ?
                        ThemeFactory.getThemeInstance(Styles.ThemeB)
                        :
                        <div style={{ visibility: visibility }}>
                            <div className="topall">
                                <div>
                                    <input type="button" style={{ marginTop: '-3px' }}
                                           className={`btn small_button ${(authStatus == 6 && storeStatus == 1) ? null : 'stop'}`}
                                           onClick={() => {
                                               this.toOtherPage('/otc/buyOrSellAdvertisement/1')
                                           }} value={formatMessage({ id: "发布广告" })}/>
                                    <SelectHistory
                                        options={market}
                                        Cb={this.getChangeType}
                                    />
                                    <span
                                        className="topall_item">{formatMessage({ id: "当前价格" })}:{topallData.lastPrice} {mFlag}(≈{currencyFlag} {new BigNumber(topallExchange || 0).toFixed(2)})</span>
                                    <span
                                        className="topall_item">{formatMessage({ id: "24小时成交量" })}:{this.calcNum(topallData.volume)} {mFlag}</span>
                                    <span
                                        className="topall_item">{formatMessage({ id: "24小时最高价" })}:{topallData.high}</span>
                                    <span
                                        className="topall_item">{formatMessage({ id: "24小时最低价" })}:{topallData.low} </span>
                                </div>
                                <svg className="icon" aria-hidden="true">
                                    {/*<use xlinkHref="#icon-Shape-message"></use>*/}
                                </svg>
                            </div>

                            {
                                storeStatus != 1 ?
                                    <div className="authen_wrap center">
                                        <div className="authen_title">{formatMessage({ id: '申请广告商家认证' })}</div>
                                        <div>{formatMessage({ id: '广告商家享有0手续费及更多交易特权' })}</div>
                                        <div className="three_card">
                                            <div className="card">
                                                <div className="card_icon">
                                                    <img src={IMG_VIP}/>
                                                </div>
                                                <div className="card_title">{formatMessage({ id: '商家标识' })}</div>
                                                <div className="card_desc">{formatMessage({ id: '专属标识，增强交易方信任' })}</div>
                                            </div>
                                            <div className="card" style={{ margin: '0 40px' }}>
                                                <div className="card_icon">
                                                    <img src={IMG_STAR}/>
                                                </div>
                                                <div className="card_title">{formatMessage({ id: '优先推荐' })}</div>
                                                <div
                                                    className="card_desc">{formatMessage({ id: '高效匹配，提高交易成单效率' })}</div>
                                            </div>
                                            <div className="card">
                                                <div className="card_icon">
                                                    <img src={IMG_TEL}/>
                                                </div>
                                                <div className="card_title">{formatMessage({ id: '专属客服' })}</div>
                                                <div
                                                    className="card_desc">{formatMessage({ id: '7*24小时，一对一在线服务' })}</div>
                                            </div>
                                        </div>
                                        <div className="entrust-head-box">
                                            <div className={`${includeCancel ? "bg-white" : ""} checkboxitem`}>
                                                <i className={includeCancel ? "iconfont icon-xuanze-yixuan" : "iconfont icon-xuanze-weixuan "}
                                                   onClick={this.checkBoxChange}/>
                                            </div>
                                            <span><FormattedMessage id="我已阅读并同意{xxx}" values={{
                                                xxx: <a href={linkUrl[LAN]}
                                                        className="xieyi">{formatMessage({ id: "《广告商协议》" })}</a>
                                            }}/></span>
                                        </div>
                                        <div className="submit_wrap">
                                            <input type="button"
                                                   className={`btn submit ${(includeCancel && (storeStatus == -1 || storeStatus == 2)) ? null : 'stop'}`}
                                                   onClick={this.startAuthen}
                                                   value={storeStatus == 0 ? formatMessage({ id: "认证中" }) : (storeStatus == 2 ? formatMessage({ id: '重新认证' }) : formatMessage({ id: "立即认证" }))}/>
                                            <div>
                                                {storeStatus == 2 && storeReason && formatMessage({ id: '十分抱歉，您的申请未审核通过，原因为：xxx' }).replace('xxx', storeReason)}
                                            </div>
                                        </div>
                                    </div>
                                    :
                                    <div className="operate_content">
                                        <div className="operate_wrap today_data">
                                            <div className="operate_title">
                                                <div className="operate_title_left">
                                                    {formatMessage({ id: '今日数据' })}
                                                    <i className="operate_title_icon" onClick={() => {
                                                        this.refresh('today')
                                                    }} onMouseLeave={this.mouseLeave} onMouseEnter={() => {
                                                        this.iconHover('today')
                                                    }}>
                                                        <img src={refreshOn == 'today' ? IMG_REFRESH_ON : IMG_REFRESH}
                                                             style={{ width: '100%', height: '100%' }}/>
                                                    </i>
                                                </div>
                                                <div></div>
                                            </div>
                                            <div className="four_wrap">
                                                <div className="four_item four_item_1">
                                                    <p className="num">{todayData.finishOrder}</p>
                                                    <p className="desc">{formatMessage({ id: '已成交订单数' })}</p>
                                                </div>
                                                <div className="four_item four_item_2">
                                                    <p className="num">{new BigNumber(todayData.sellCount || 0).toFixed(2)}
                                                        CNY</p>
                                                    <p className="desc">{formatMessage({ id: '累计出售总额' })}</p>
                                                </div>
                                                <div className="four_item four_item_3">
                                                    <p className="num">{new BigNumber(todayData.buyCount).toFixed(2)}
                                                        CNY</p>
                                                    <p className="desc">{formatMessage({ id: '累计购买总额' })}</p>
                                                </div>
                                                <div className="four_item four_item_4">
                                                    <div>
                                                        <p className="num">{formatDecimal(new BigNumber(todayData.total_btc || 0), 8)}
                                                            BTC</p>
                                                        <p style={{ fontSize: '14px', color: '#fff' }}>
                                                            ≈{formatDecimal(todayData.total_usdt * exchangeRate.exchangeRateUSD.CNY, 2)}CNY</p>
                                                    </div>
                                                    <p className="desc">{formatMessage({ id: '法币账户资产余额' })}</p>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="operate_wrap">
                                            <div className="operate_title">
                                                <div className="operate_title_left">
                                                    {formatMessage({ id: '进行中的订单(x)' }).replace('x', pendingData.totalCount || 0)}
                                                    <i className="operate_title_icon" onClick={() => {
                                                        this.refresh('pending')
                                                    }} onMouseLeave={this.mouseLeave} onMouseEnter={() => {
                                                        this.iconHover('pending')
                                                    }}>
                                                        <img src={refreshOn == 'pending' ? IMG_REFRESH_ON : IMG_REFRESH}
                                                             style={{ width: '100%', height: '100%' }}/>
                                                    </i>
                                                </div>
                                                <div onClick={() => {
                                                    this.toOtherPage('/otc/otcOrder?appeal=4')
                                                }}
                                                     style={{ cursor: 'pointer' }}>{formatMessage({ id: '异常/申诉订单(x)' }).replace('x', unusual || 0)}</div>
                                            </div>
                                            <div>
                                                <PendingList tabData={pendingData}
                                                             pendingPageClick={this.pendingPageClick}
                                                             refreshAll={this.refreshAll}
                                                             showUserCenter={this.showUserCenter}/>
                                            </div>
                                        </div>
                                        <div className="operate_wrap" style={{ marginBottom: '62px' }}>
                                            <div className="operate_title">
                                                <div className="operate_title_left">
                                                    {formatMessage({ id: '近期订单(x)' }).replace('x', recentData.totalCount || 0)}
                                                    <i className="operate_title_icon" onClick={() => {
                                                        this.refresh('recent')
                                                    }} onMouseLeave={this.mouseLeave} onMouseEnter={() => {
                                                        this.iconHover('recent')
                                                    }}>
                                                        <img src={refreshOn == 'recent' ? IMG_REFRESH_ON : IMG_REFRESH}
                                                             style={{ width: '100%', height: '100%' }}/>
                                                    </i>
                                                </div>
                                            </div>
                                            <div>
                                                <RecentList tabData={recentData} recentPageClick={this.recentPageClick}
                                                            refreshAll={this.refreshAll}
                                                            showUserCenter={this.showUserCenter}/>
                                            </div>
                                        </div>
                                    </div>
                            }
                        </div>
                }
                {
                    identityConfirm &&
                    <Confirm isNotCancel={true} okText={formatMessage({ id: "前往认证" })}
                             title={formatMessage({ id: "尚未实名认证" })}
                             content={formatMessage({ id: "请您先完成实名认证后，再申请广告商家认证" })}
                             cb={type => this.identityCllback(type)}/>
                }
                <ReactModal ref={modal => this.modal = modal}>
                    {this.state.modalHTML}
                </ReactModal>
                {/* 设置title */}
                {showFlag ? (storeStatus != 1 ? <TitleSet titleval={"申请商家权限"} /> : <TitleSet titleval={"工作台"} /> ):null}
                 {/* <TitleSet titleval={storeStatus != 1 ?"申请商家权限":"工作台"} /> */}
                <ReactModal ref={modal => this.modalUser = modal}>
                    <UserCenter modal={this.modalUser} hoemPage={this.state.homePage} targetId={this.state.targetId}
                                uid={this.state.uid}/>
                </ReactModal>
            </div>
        )
    }
}

export default withRouter(injectIntl(Business));
