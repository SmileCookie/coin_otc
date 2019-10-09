import React from 'react';
import { post, get } from 'nets';
import cookie from 'js-cookie';
import moment from 'moment';
import { withRouter } from 'react-router';
import { connect } from 'react-redux';
import { FormattedMessage, injectIntl } from 'react-intl';
import ScrollArea from 'react-scrollbar';
import Confirm from '../../components/confirm';
import ReactModal from '../../components/popBox';
import UserCenter from '../../components/user/userCenter';
import '../../assets/style/advertisement/advertiseDetails.less';
import { separator, getCoinNum, optPop, formatDecimalNoZero, isValEmpty } from '../../utils';
import { USERID, DATA_TIME_FORMAT, DATA_TIEM_EN } from 'conf';
import { Styles, ThemeFactory } from "../../components/transition";
import { getAdvertiseInfo, downFramAd, upFramAd, hideAd } from "./advertiseDetails.model";

const BigNumber = require('big.js');

//coinList: state.account.detail,
@connect(
    state => ({
        coinData: state.session.coinData
    })
)
class AdvertiseDetails extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            loading: false, // 加载动画
            confirmType: 1, // 下架是1 隐藏是2
            advertiseDetails: {},// 详情的数据
            downConfirm: false, // 下架的confirm
            id: '',
            errorPage: false,
        };
        this.showUserCenter = this.showUserCenter.bind(this);
    }

    componentWillMount() {
        !USERID && (window.location.href = '/bw/login')
    }

    componentDidMount() {
        // 请求初始化数据
        let id = this.props.match.params.id;
        this.setState({ id });
        // 发请求
        this.getAdvertiseInformation(id);
    }

    getAdvertiseInformation = (id) => {
        getAdvertiseInfo(this, id).then(res => {
            if (typeof(res) === 'string') {
                // optPop(() => {}, res);
            } else {
                let market = res.coinTypeName;
                let _obj = getCoinNum(this.props.coinData, market);
                res.reason && (res = this.replaceReason(res));
                this.setState({
                    advertiseDetails: res,
                    marketL: _obj.marketL,
                    payL: _obj.payL,
                })
            }
        })
    };

    replaceReason(item) {
        const { formatMessage } = this.props.intl;
        item.reason = item.reason.replace('下架原因：', '').replace('隐藏原因：', '').replace('。', '');
        if (item.reason.indexOf('取消订单') > -1) {
            item.reasonCancel = true;
            item.cancelNum = item.reason.match(/\d+/g)[0]; // 取消次数
            item.cancelTime = item.reason.match(/\d+/g)[1]; // 多少时间之内
            item.reason = item.reason.replace(item.cancelNum, 'N').replace(item.cancelTime, 'T');
            item.reason = formatMessage({ id: item.reason }).replace('N', item.cancelNum).replace('T', item.cancelTime);
        } else {
            item.reason = formatMessage({ id: item.reason });
        }

        return item;
    }

    componentWillReceiveProps() {

    }

    // 上架， 下架，隐藏，发布广告的按钮点击
    // 1 是下架 2 是隐藏 3 是上架
    btnChoose = async type => {
        const { id } = this.state;
        if (type == 1) {
            // 请求下架接口
            this.setState({
                downConfirm: true,
                confirmType: 1,
            })
        } else if (type == 2) {
            this.setState({
                downConfirm: true,
                confirmType: 2,
            })
        } else if (type == 3) {
            // 请求下架上架
            await upFramAd(id);
            this.getAdvertiseInformation(id);
        }
    };
    // 下架的确认框的回调函数
    callBack = async type => {
        const { id, confirmType } = this.state;
        if (type === 'sure') {
            if (confirmType == 1) {
                await downFramAd(id);
            } else {
                await hideAd(id);
            }
            this.getAdvertiseInformation(id);
        }
        this.setState({ downConfirm: false });
    };
    // 跳转详情
    goUrl = (key) => {
        window.orderId = key;
        window.location.href = `/otc/orderDetail/${key}`;
    };

    async showUserCenter(targetId) {
        let userId = USERID || "";
        let res = await post('/web/common/getAvgPassTime', { targetUserId: targetId, userId });
        if (res.code == 200) {
            this.setState({
                targetId: targetId,
                uid: USERID,
                homePage: res.data,
            }, () => {
                this.modalUser.openModal();
            })
        } else {
            optPop(() => {
            }, res.data.msg, { timer: 1500 })
        }
    }

    render() {
        const { formatMessage } = this.props.intl;
        const { loading, advertiseDetails, downConfirm, confirmType, payL, marketL, errorPage } = this.state;
        const LAN = cookie.get('zlan');
        return (
            <div className="advertisement_details container_main">
                {
                    loading ?
                        ThemeFactory.getThemeInstance(Styles.ThemeB)
                        :
                        !errorPage ?
                            <React.Fragment>
                                <div className="page_title margin_b20"><FormattedMessage id="广告详情"/></div>
                                <div className="advertisement_details_content">
                                    <div className="first_line">
                                        {/*<span className="first_label">{advertiseDetails.orderType  == 0 ? '购买':'出售'}{advertiseDetails.coinTypeName}</span>*/}
                                        <span
                                            className="first_label">{advertiseDetails.orderType == 0 ? formatMessage({ id: "购买xxx" }).replace('xxx', advertiseDetails.coinTypeName) : formatMessage({ id: "出售xxx" }).replace('xxx', advertiseDetails.coinTypeName)}</span>
                                        <span className="label">{formatMessage({ id: "广告编号" })}：</span>
                                        <span className="label_val">{advertiseDetails.orderNo}</span>
                                        <span className="label">{formatMessage({ id: "广告状态" })}：</span>
                                        <span
                                            className="label_val">{advertiseDetails.orderStatus == 0 ? formatMessage({ id: "已上架" }) : advertiseDetails.orderStatus == 1 ? formatMessage({ id: "已下架" }) : formatMessage({ id: "已隐藏" })}</span>
                                        {
                                            advertiseDetails.orderStatus != 0 &&
                                            <div>
                                                <span
                                                    className="label">{advertiseDetails.orderStatus == 1 ? formatMessage({ id: "下架原因：" }) : formatMessage({ id: "隐藏原因：" })}</span>
                                                <span className="label"
                                                      style={{ marginLeft: '0' }}>{advertiseDetails.reason}</span>
                                            </div>
                                        }
                                        {
                                            advertiseDetails.orderStatus == 0 ?
                                                <div className="advertisement_details_btn">
                                                    <input type="button" className="btn small_button"
                                                           value={formatMessage({ id: "下架" })} onClick={() => {
                                                        this.btnChoose(1)
                                                    }}/>
                                                    {/* <input type="button" className="btn small_button" value={formatMessage({id:"隐藏"})} onClick={()=> {this.btnChoose(2)}} /> */}
                                                </div>
                                                :
                                                advertiseDetails.orderStatus == 1 ?
                                                    advertiseDetails.typeOperation == 0 ?
                                                        <div className="advertisement_details_btn">
                                                            <input type="button" className="btn small_button"
                                                                   value={formatMessage({ id: "上架" })} onClick={() => {
                                                                this.btnChoose(3)
                                                            }}/>
                                                        </div>
                                                        :
                                                        ""
                                                    :
                                                    ""
                                        }
                                    </div>
                                    <div className="advertisement_details_line">
                                        <div className="advertisement_th">
                                            <span
                                                className="text-left create_time">{formatMessage({ id: "创建时间" })}</span>
                                            <span className="text-right"
                                                  style={{ width: '12%' }}>{formatMessage({ id: "数量" })}</span>
                                            <span className="text-right"
                                                  style={{ width: '12%' }}>{formatMessage({ id: "单价" })}</span>
                                            <span className="text-right limit">{formatMessage({ id: "限额" })}</span>
                                            <span className="text-right">{formatMessage({ id: "已成交数量" })}</span>
                                            <span className="text-right"
                                                  style={{ width: '15%' }}>{formatMessage({ id: "已扣除手续费" })}</span>
                                            <span className="text-right">{formatMessage({ id: "剩余数量" })}</span>
                                        </div>
                                        <div className="advertisement_details_val">
                                            <span
                                                className="text-left create_time">{moment(advertiseDetails.orderTime).format(LAN === 'en' ? DATA_TIEM_EN : DATA_TIME_FORMAT)}</span>
                                            <span className="text-right"
                                                  style={{ width: '12%' }}>{new BigNumber(advertiseDetails.coinTotal || 0).toFixed(marketL)}{advertiseDetails.coinTypeName}</span>
                                            <span className="text-right"
                                                  style={{ width: '12%' }}>{`${separator(new BigNumber(advertiseDetails.coinPrice || 0).toFixed(payL))}CNY`}</span>
                                            <span
                                                className="text-right limit">{`${separator(new BigNumber(advertiseDetails.minNumber || 0).toFixed(payL))}CNY ~ ${separator(new BigNumber(advertiseDetails.maxNumber || 0).toFixed(payL))}CNY`}</span>
                                            <span
                                                className="text-right">{new BigNumber(advertiseDetails.coinComplateNumber || 0).toFixed(marketL)}{advertiseDetails.coinTypeName}</span>
                                            <span className="text-right"
                                                  style={{ width: '15%' }}>{formatDecimalNoZero(advertiseDetails.deductFeeSumNew || 0, 8)}{advertiseDetails.coinTypeName}</span>
                                            <span
                                                className="text-right">{new BigNumber(advertiseDetails.lastNumber || 0).toFixed(marketL)}{advertiseDetails.coinTypeName}</span>
                                        </div>
                                    </div>
                                    <div className="order_content">
                                        <p className="order_title">{formatMessage({ id: "订单数量" })}{formatMessage({ id: "x笔" }).replace('x', advertiseDetails.otrList ? advertiseDetails.otrList.length : 0)}</p>
                                        <ScrollArea ref="scrollerBar" style={{
                                            maxHeight: '485px',
                                            borderTop: '1px solid #414654',
                                            borderBottom: '1px solid #414654',
                                            marginTop: '10px'
                                        }}>
                                            <div className="table_box" style={{ marginTop: '0' }}>
                                                <table className="table_content">
                                                    <thead>
                                                    <tr>
                                                        <th width="19%" className="text-center"><FormattedMessage
                                                            id="交易数量"/></th>
                                                        <th width="60%" className="text-center"><FormattedMessage
                                                            id="交易金额"/></th>
                                                        <th width="20%" className="text-center"><FormattedMessage
                                                            id="订单状态"/></th>
                                                    </tr>
                                                    </thead>
                                                    <tbody id="historyEntrustList">
                                                    {
                                                        advertiseDetails.otrList && advertiseDetails.otrList.length > 0 ? advertiseDetails.otrList.map((item, index) => {
                                                                let backCardName = '';

                                                                if (advertiseDetails.orderType == 0) {
                                                                    backCardName = isValEmpty(item.sellerCardName) ? '' : `(${item.sellerCardName})`
                                                                } else {
                                                                    backCardName = isValEmpty(item.buyerCardName) ? '' : `(${item.buyerCardName})`
                                                                }
                                                                return (
                                                                    [
                                                                        <tr key={(index + 1) * 2 - 1} className="first_tr">
                                                                            <td colSpan='8'>
                                                                                <span
                                                                                    className="tr_title">{formatMessage({ id: '订单编号：' })}</span>
                                                                                <span
                                                                                    className="tr_title advertiseVal">{item.recordNo}</span>
                                                                                <span
                                                                                    className="tr_title margin_l40">{formatMessage({ id: '交易时间：' })}</span>
                                                                                <span
                                                                                    className="tr_title">{moment(item.coinTime).format(LAN === 'en' ? DATA_TIEM_EN : DATA_TIME_FORMAT)}</span>
                                                                                <span
                                                                                    className="tr_title margin_l40">{advertiseDetails.orderType == 0 ? formatMessage({ id: '卖家：' }) : formatMessage({ id: '买家：' })}</span>
                                                                                <span className="blue" onClick={() => {
                                                                                    this.showUserCenter(advertiseDetails.orderType == 0
                                                                                        ? item.sellUserId : item.buyUserId)
                                                                                }}>
                                                                                    {advertiseDetails.orderType == 0 ? item.sellUserName + backCardName : item.buyUserName + backCardName}
                                                                                </span>
                                                                                {/*<span className="charts tr_title iconfont icon-Shape-message">*/}
                                                                                {/*<i>3</i>*/}
                                                                                {/*</span>*/}
                                                                            </td>
                                                                        </tr>,
                                                                        <tr className="second_tr" key={(index + 1) * 2}>
                                                                            <td className="text-center">{new BigNumber(item.coinNumber || 0).toFixed(marketL)}{advertiseDetails.coinTypeName}</td>
                                                                            <td className="text-center">{`${separator(new BigNumber(item.tradeTotal || 0).toFixed(payL))}CNY`}</td>
                                                                            <td className="text-center">
                                                                                <div className="margin_b10">
                                                                                    {item.status == 1 ? formatMessage({ id: "待付款" }) : item.status == 2 ? formatMessage({ id: "待放币" }) : item.status == 3 ? formatMessage({ id: "交易完成" }) :
                                                                                        item.status == 4 ? formatMessage({ id: "交易取消" }) : item.status == 5 ? formatMessage({ id: "异常订单" }) : formatMessage({ id: "申诉中" })}
                                                                                </div>
                                                                                <a href="javascript:void(0)"
                                                                                   onClick={() => this.goUrl(item.id)}>{formatMessage({ id: "查看详情" })}</a>
                                                                            </td>
                                                                        </tr>
                                                                    ]
                                                                )
                                                            }) :
                                                            (
                                                                <tr className="nodata">
                                                                    <td className="billDetail_no_list" colSpan="3">
                                                                        <p className="entrust-norecord">
                                                                            <svg className="icon" aria-hidden="true">
                                                                                <use xlinkHref="#icon-tongchang-tishi"/>
                                                                            </svg>
                                                                            <FormattedMessage id="暂无订单信息"/>
                                                                        </p>
                                                                    </td>
                                                                </tr>
                                                            )
                                                    }
                                                    </tbody>
                                                </table>
                                            </div>
                                        </ScrollArea>
                                    </div>
                                </div>
                                {
                                    downConfirm &&
                                    <Confirm
                                        title={confirmType == 1 ? formatMessage({ id: "下架确认" }) : formatMessage({ id: "隐藏确认" })}
                                        content={confirmType == 1 ? formatMessage({ id: "广告一旦下架将不可复原，您确认要下架广告吗？" }) : formatMessage({ id: "确认要进行广告隐藏吗？" })}
                                        cb={type => this.callBack(type)}/>
                                }
                                <ReactModal ref={modal => this.modalUser = modal}>
                                    <UserCenter modal={this.modalUser} hoemPage={this.state.homePage}
                                                targetId={this.state.targetId} uid={this.state.uid}/>
                                </ReactModal>
                            </React.Fragment>
                            :
                            <div className="erroPage">
                                <h3>
                                    <span
                                        style={{ color: '#3E85A2', paddingRight: '10px' }}
                                        className="iconfont icon-gonggao-moren"
                                    />
                                    <FormattedMessage id="无效广告"/>
                                </h3>
                            </div>
                }
            </div>
        )
    }
}

export default withRouter(injectIntl(AdvertiseDetails));
