import React from 'react';
import cookie from 'js-cookie';
import { connect } from 'react-redux';
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
const { notifSend } = notifActions;
import { fetchMarketsData } from '../../redux/modules/markets';
import { fetchMarketInfo } from '../../redux/modules/marketinfo';
import { fetchEntrustRecord } from '../../redux/modules/entrustrecord';
import { fetchMarketDepthChartData} from '../../redux/modules/marketdepthchartdata';
import { fetchMineHistoryData } from '../../redux/modules/minehistory';
import SidebarContainer from './sidebar/sidebarContainer';
import { fetchAssetsDetail } from '../../redux/modules/assets'
import { DOMAIN_VIP,DISMISS_TIME,COOKIE_MONEY,HEADER_HEIGHT,HEADER_HEIGHT_MARGIN,PAGEMINHEIGHT } from '../../conf/index'
import { fetchOrderHistory } from '../../redux/modules/orderhistory';
import { fetchDefaultMoney } from '../../redux/modules/money';
import TradeMenu from './menu/menu.js'
import CoinDetail from './coindetail'
import '../../assets/css/trade'
import HTML5Backend from 'react-dnd-html5-backend'
import { DragDropContext } from 'react-dnd'
// import { Chat } from 'oasis-client-dep'
import { hideString, formatURL } from '../../utils'
import { FormattedMessage, injectIntl } from 'react-intl';
import { browserHistory } from 'react-router';
import Debounce from 'lodash-decorators/debounce'
import ErrorComponent from '../common/ErrorComponent'

@DragDropContext(HTML5Backend)
class NewsPsotCon extends React.Component {
    constructor(props){
        super(props)
        this.state = {
            wrapHei:document.body.clientHeight - HEADER_HEIGHT,
            showMenu:true,
            dropMenu:false
        }
        this.menuClickHide = this.menuClickHide.bind(this)
        this.checkPageHeight = this.checkPageHeight.bind(this)
    }
    componentDidMount() {
        this.checkPageHeight()
        window.addEventListener('resize',this.checkPageHeight)
        this.props.fetchMarketsData();
        let localeCookie = cookie.get(COOKIE_MONEY);
        if(!localeCookie){
            this.props.fetchDefaultMoney();
        }
        if(!this.props.user) {
            return false;
        }
        this.props.fetchAssetsDetail();
    }
    componentWillReceiveProps(nextProps){
        const { dropMenu } = this.state
        if(!dropMenu&&nextProps.location.pathname=="/bw/multitrade"){
            this.setState({
                dropMenu:true
            })
        }else if(dropMenu&&nextProps.location.pathname!="/bw/multitrade"){
            this.setState({
                dropMenu:false
            })
        }
    }
    checkPageHeight(){
        const browHei = window.screen.height 
        const pageHei = window.rs ? window.rs : document.body.clientHeight
        const htmlDom = document.getElementsByTagName("html")[0];
        clearTimeout(this.tmr);
        this.tmr = setTimeout(()=>{
            this.setState({
                wrapHei:Math.max(pageHei - HEADER_HEIGHT, 701)
            })
        },100)
        
        if(browHei >= 768&&pageHei>=701){
            htmlDom.setAttribute("style","overflow:hidden")
        } else {
            htmlDom.removeAttribute("style");
        }
        window.rs = 0;
    }
    componentWillUnmount(){
        document.getElementsByTagName("html")[0].removeAttribute("style");
        window.removeEventListener('resize',this.checkPageHeight)
        this.triggerResizeEvent.cancel();
    }
    
    menuClickHide(){
        this.triggerResizeEvent()
        this.setState(preState => ({
            showMenu:!preState.showMenu
        }))
    }

    @Debounce(200)
    triggerResizeEvent() {
        const event = document.createEvent('HTMLEvents');
        event.initEvent('resize', true, false);
        window.dispatchEvent(event);
    }
    
    render() {
        const { showMenu,dropMenu,wrapHei } = this.state
        const { user, intl:{formatMessage:mg} } = this.props
   
        return (
            <div className={`trade-wrap up-trade trade-wrap-${this.props.skin} ${wrapHei==PAGEMINHEIGHT?"small-trade":""}`} style={{height:wrapHei,width:'100%'}}>
                
                    {showMenu&&<div className="col-s up-wsp" id="colSim" >
                        <SidebarContainer pathName={this.props.router.params.paramName} menuClickHide={this.menuClickHide} dropMenu={dropMenu}  changeMarketinfo = {this.props.changeMarketinfo}/>
                        <div className="trade-item balance height-prop-25">
                            <CoinDetail />
                            
                            <div className="trade-talk plv" style={{display: 'none'}}>
                                <div style={{position: 'absolute', left:0, right:0, bottom:0}}>
                                {
                                    false
                                    &&
                                    <Chat uid={user ? user.uid : ''} nickname={user ? hideString(user.username) : ''} pt="BBCLIENT" lang={{tith: mg({id:'聊天室'}), send: mg({id:'发送'}), err: mg({id:'已禁言'})}} delTith={mg({id: '清空聊天消息'})} defaultTith={mg({id:'友情提示：切勿刷屏发推广链接、微信扫描码、虚假消息等，否则将被禁言。'})} msgs={{mContent:mg({id:'不能发送空白信息'}), optMsg:!user ? mg({id:'请先登录'}) : ''}} enableSayFlg={!!user} enableCb={()=>{browserHistory.push(formatURL('login'))}} theme={this.props.skin === 'light' ? 'bright' : 'cc' } />
                                }
                                </div>
                            </div>
                        </div>
                    </div>}
                    <i className={`sidebar-btn cebianlanshouqi-moren ${!showMenu&&"rotate180 left86"}`} onClick={this.menuClickHide}></i>
                <ErrorComponent classNames="col-big" divStyles={{height:'100%',background:this.props.skin == 'dark'?'#17191F':'#fff'}}>
                    <div className="col-big" style={{height:'100%'}}>
                        {this.props.children}
                    </div>
                </ErrorComponent>
            </div>
        )
    }
}

const mapStateToProps = (state, ownProps) => {
    return {
        marketinfo: state.marketinfo,
        user:state.session.user,
        skin:state.trade.skin,
        curMarket:state.marketinfo.currentMarket
    };
}
const mapDispatchToProps = (dispatch) => {
    return {
        fetchMarketsData: () => {
            dispatch(fetchMarketsData());
        },
        changeMarketinfo: (market,fullName,money) => {
                // dispatch(fetchMarket(market,fullName));
                dispatch(fetchMarketInfo(market,fullName,"",0,5,1));
                let lastTime = +new Date;
                dispatch(fetchEntrustRecord(market,1, 3, lastTime, 30, 1));
                dispatch(fetchEntrustRecord(market,2, -1, lastTime, 30, 1));
                // dispatch(fetchEntrustRecord(market,1, 2, lastTime, 0, 1));
                // dispatch(fetchOrderHistory(market,includeCancel,timeType,type,pageNum,pageSize,lastTime))
                dispatch(fetchOrderHistory(market,0,0,-1,1,30,lastTime))
                /* fetchMarketDepthChartData */
                dispatch(fetchMarketDepthChartData(market));

                dispatch(fetchMineHistoryData(market));
                /* summary */
        },
        fetchDefaultMoney:()=>{
            dispatch(fetchDefaultMoney())
        },
        notifSend: (msg) => {
            dispatch(notifSend({
                message: msg,
                kind: 'info',
                dismissAfter: DISMISS_TIME
            }));
        },
        fetchAssetsDetail:() => {
            dispatch(fetchAssetsDetail())
        }
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(NewsPsotCon))


