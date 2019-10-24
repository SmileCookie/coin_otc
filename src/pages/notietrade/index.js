import React from 'react';
import cookie from 'js-cookie';
import { connect } from 'react-redux';
import { FormattedMessage } from 'react-intl';
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
const { notifSend } = notifActions;
import { recieveMarket } from '../../redux/modules/marketinfo';
import MarketinfoContainer from '../trade/marketinfo/marketinfoContainer';
import EntrustrecordContainer from '../trade/entrustrecord/entrustrecordContainer';
import Practice from '../trade/practice/practiceContainer';
import KlineAndDepthChart from '../trade/klineOrDepthChart'
import { DOMAIN_VIP,DISMISS_TIME,COOKIE_MONEY,HEADER_HEIGHT,PAGEMINHEIGHT,ERRORCONFIG } from '../../conf/index'
import SellBuy from '../trade/sellbuy/sellBuyFormContainer'
import Tradehistory from '../trade/history/historyContainer';
import {setIsHasAuthGoogleAndIp} from '../../redux/modules/session';
import ReactModal from '../../components/popBox';
import { formatDate } from '../../utils';
import axios from 'axios';
import ScrollArea from 'react-scrollbar'
import '../../assets/css/trade'
import ErrorComponent from '../common/ErrorComponent'
import { doSubscribeMsg } from '../../redux/modules/socket'


class Trade extends React.Component {
    constructor(props){
        super(props)
        this.state = {
            buyPrice:0,
            buyAmount:0,
            sellPrice:0,
            sellAmount:0,
            ipAuth:'',
            googleAuth:'',
            modalHTML:'',
        }
        this.btnStart = false;
        this.updateSellPrice = this.updateSellPrice.bind(this)
        this.updateBuyPrice = this.updateBuyPrice.bind(this)
        this.takeVoteTrade = this.takeVoteTrade.bind(this)
    }
    componentDidMount() {
        const defaultCoinName = this.props.routeParams.paramName || "btc_usdt"
        this.props.recieveMarket(defaultCoinName)
        this.takeVoteTrade()

    }

    //公告弹窗
    takeVoteTrade(){
        axios.get(DOMAIN_VIP + "/msg/getUserUnReadNotice")
            .then(res => {
                try{
                    let result = res.data;
                    if (!result.isSuc || !result.datas || result.datas.length == 0) return false;
                    let notice = result.datas[0];
                    let html = <div className="modal trade-notice-modal">
                        <div className="modal-body">
                            <div className="notice-dialog-close">
                                <i onClick={() => this.updateNoticeStatus(notice['id'])} className="iconfont icon-guanbi-yiru"></i>
                            </div>
                            <h4>{notice["title"]}</h4>
                            <div className="notice-dialog-time"><i className="iconfont icon-msnui-time"></i>{formatDate(notice["publishTime"])}</div>
                            <ScrollArea className="trade-scrollarea">
                                <div className="notice-dialog-content"
                                     dangerouslySetInnerHTML={{__html:notice['content']}}>
                                </div>
                            </ScrollArea>
                        </div>
                        <div className="modal-foot">
                            <a className="btn ml10" onClick={() => this.updateNoticeStatus(notice['id'])}><FormattedMessage id="sure"/></a>
                        </div>
                    </div>;

                    const lan = cookie.get("zlan");
                    const ISSHOW = 'isshow_' + lan;
                    const zuid = cookie.get("zuid");
                    // console.log('okokkokokokokokooko')
                    if((!zuid && localStorage.getItem(ISSHOW) !== notice.id || zuid)){
                        this.setState({modalHTML:html},()=>{
                            this.modal.openModal();
                        })
                        if(!zuid){
                            localStorage.setItem(ISSHOW, notice.id);
                        }
                    }
                }catch(e){

                }
            });
    }

    updateNoticeStatus(lastNoticeId) {
        axios.get(DOMAIN_VIP + "/msg/readNotice?maxNoticeId=" + lastNoticeId)
            .then(res => {
                this.modal.closeModal();
            });
    }

    updateSellPrice(price,amount, type){
        this.setState({
            buyPrice:price,
            sellPrice:price,
            buyAmount:amount,
            sellAmount:amount
        })
    }
    updateBuyPrice(price,amount, type){
        this.setState({
            sellPrice:price,
            buyPrice:price,
            sellAmount:amount,
            buyAmount:amount
        })
    }


    render() {
        return (
            <ErrorComponent divStyles={{width:'100%'}} >
                <React.Fragment>
                    <div className="col-m">
                        {/*头部 和 K线图 height-prop-74*/}
                        <div className="kline-box">
                            <MarketinfoContainer />
                            {/* <Depthhighchart/> */}
                            <KlineAndDepthChart
                                pathName={this.props.routeParams.paramName}
                                skin = {this.props.skin}
                                lang = {this.props.lang}
                                currentMarket = {this.props.currentMarket}
                            />
                        </div>
                        {/*委托记录*/}
                        <div className="trade-item height-prop-25">
                            <EntrustrecordContainer cancelEntrust={this.props.cancelEntrust}/>
                        </div>
                    </div>
                    <div className="col-a up-tr">

                        <div className="up-mp clearfix">
                            <div className="trade-top-con item">
                                {/*买入，卖出委托，成交记录，市场深度*/}
                                {
                                    <Practice socket={this.props.socket.ws} updateSellPrice={this.updateSellPrice} updateBuyPrice={this.updateBuyPrice}/>
                                }
                            </div>
                            <div className="item fr">
                                {
                                    <Tradehistory socket={this.props.socket.ws} />
                                }
                            </div>
                        </div>

                        {/*买入- 卖出 - 摘要*/}
                        {
                            <div className="height-prop-48">
                                <div className="trade-item sell-buy box-allhei">
                                    <SellBuy
                                        updateSellPrice={this.updateSellPrice}
                                        price ={this.state.buyPrice}
                                        amount = {this.state.buyAmount}/>
                                </div>
                            </div>
                        }

                    </div>
                    <ReactModal ref={modal => this.modal = modal}>
                        {this.state.modalHTML}
                    </ReactModal>
                </React.Fragment>
            </ErrorComponent>
        )
    }
}

const mapStateToProps = (state, ownProps) => {
    return {
        socket: state.socket,
        marketinfo: state.marketinfo,
        user:state.session.user,
        skin:state.trade.skin,
        lang:state.language.locale,
        currentMarket:state.marketinfo.currentMarket
    };
}
const mapDispatchToProps = (dispatch) => {
    return {
        recieveMarket:(market) => {
            dispatch(recieveMarket(market));
        },
        setIsHasAuthGoogleAndIp:()=>{
            dispatch(setIsHasAuthGoogleAndIp());
        },
        notifSend: (msg) => {
            dispatch(notifSend({
                message: msg,
                kind: 'info',
                dismissAfter: DISMISS_TIME
            }));
        }
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(Trade);