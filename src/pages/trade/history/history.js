import React from 'react';
import { Link,withRouter } from 'react-router';
import { FormattedMessage, injectIntl } from 'react-intl';
const BigNumber = require('big.js');
import Historylist from './historylist';
import { DEFAULT_MARKETCOIN_TYPE,ERRORCONFIG } from '../../../conf';
import { requsetWebsocket } from '../../../utils'
import { connect } from 'react-redux';
import { doSubscribeMsg } from '../../../redux/modules/socket'

class Tradehistory extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            tebName:"market"
        }
        // 不是首次登录，用来处理componentWillReceiveProps 带币种的时候执行一次。
        this.notFstInToPage = 0;
        // 用来处理是否点击了币种列表中的币种用来切换管道
        this.in = 0;
        // 当前币种
        this.current = '';
        // 防止服务端无法自动切换市场当大于10再次sub
        this.tcount = 0;
        // 上一次的币种，用来unsub用
        this.prevCoin = props.params.paramName || DEFAULT_MARKETCOIN_TYPE;
        this.wsIsLoading = true
        // 模型中的websocket
        this.getWs();
        this.startWebsocket = this.startWebsocket.bind(this)
        // 用来判断是否切换了盘口那个瞬间
        this.selectPK = false;
    }
    getWs(newprops){
        this.ws = (!newprops ? this.props : newprops).socket
        this.ws = this.ws ? this.ws : {send:()=>{}, readyState : 1, isNull: window.isvisibilitychangeTradeHistory ? 0 : 1, }
    }
    componentDidMount() {
        this.startWebsocket()
    }

    componentWillReceiveProps(nextProps){
        this.getWs(nextProps);

        if(nextProps.currentMarket != this.props.currentMarket){
            if(this.ws&&this.ws.readyState&&nextProps.currentMarket){

                //console.log(this.prevCoin,nextProps.currentMarket,12345);
                // 如果非首次登录初始化状态
                // if(this.notFstInToPage){
                //     this.in = 0;
                // }
                this.prevCoin = this.props.currentMarket;

                // console.log(this.prevCoin, nextProps.currentMarket, '===----->');

                if(this.prevCoin !== nextProps.currentMarket && !this.notFstInToPage){
                    this.in = 0;
                }
                this.tcount = 0;

                this.notFstInToPage = 0;
                this.wsIsLoading = true
                this.selectPK = true;
                //this.ws.send(JSON.stringify({ "event": 'unsub',"channel":`market.${this.props.currentMarket}.deals`}))
            }
        }

        // 当socket句柄不同重新开启websocket
        if(this.props.socket !== nextProps.socket){
            // console.log(123321);
            this.in = 0;
            this.tcount = 0;
            this.notFstInToPage = 0;
            this.wsIsLoading = false;

            this.startWebsocket();
        }
    }

    //启动 websocket
    startWebsocket(curmarket){
        const marketName = curmarket || this.props.params.paramName || DEFAULT_MARKETCOIN_TYPE

        this.ws.send(JSON.stringify({ "event": 'sub',"channel":`market.${marketName}.deals`}));
        this.in = 1;

        this.notFstInToPage = 1;

        setTimeout(()=>{

            this.notFstInToPage = 0;
        }, 0)

        this.props.doSubscribeMsg(
            (res) => {

                // 不关心的数据包和自己业务没关系的不理会
                if(!res.channel.includes("deals")){
                    return;
                }

                const current = this.props.params.paramName || DEFAULT_MARKETCOIN_TYPE;
//console.log(res.msg, res.channel, res.channel.includes(current), current, this.in, '----->>');
                // 如果没有msg 并且返回的数据不是当前市场的 或者 切换导致的首次加载 进行订阅和取消上次的订阅
                if(!res.msg && res.channel && !res.channel.includes(current) || !this.in){

                    // 如果连续10都不是自己关心的包。
                    if(!res.channel.includes(current)){
                        this.tcount++;
                    }

                    // 如果币种列表点击或者10次都不是自己的包就尝试订阅一次
                    if(!this.in || this.tcount > 10){
                        this.ws.send(JSON.stringify({ "event": 'sub',"channel":`market.${current}.deals`}))
                        // 订阅后清除计数状态重新开始
                        if(this.tcount > 10){
                            this.tcount = 0;
                        }
                        // 订阅过重置in代表不在是点击列表来的了。
                        this.in = 1;
                    }

                    try{
                        // 如果返回的数据和本次没关系，就取消订阅上一次的。
                        if(res.channel && !res.channel.includes(current) && (!res.msg || res.msg && !res.msg.includes('unsub'))){
                            this.ws.send(JSON.stringify({ "event": 'unsub',"channel":res.channel}))
                        }
                    }catch(e){

                    }
                }

                // 渲染视图
                if(!res.msg && res.channel && res.channel.includes(current)){
                    // 记录当前币种，当切换币种列表赋值给上一次好做unsub用。
                    this.current = current;
//console.log(res);
                    if(res.datas){
                        if(this.wsIsLoading){
                            this.wsIsLoading = false
                        }
                        // 接受到数据将切换盘口的那个瞬间的flg清空
                        this.selectPK = false;
                        //if(res.market == this.props.currentMarket){
                            this.props.recieveMarkethistory(res.datas.filter(i => !!i))
                            window.isvisibilitychangeTradeHistory = false;
                        //}
                        if(window.ieFlag){
                            setTimeout( () => {
                                this.ws.close()
                            },2000)
                        }
                    }
                }

            }
        )
    }

    componentWillUnmount() {
        //this.ws.close()
    }
    setTebName(name){
        this.setState({
            tebName: name,
        })
    }
    componentDidCatch(){
        if(window.ERRORCONFIG){
            this.setState({
                dataError:true
            })
            // console.log(err,infor)
        }else{
            // console.log(err,infor)
        }
    }

    render() {
      //  console.log(!this.ws.isNull);
        if(this.state.dataError){
            return (
                <div className="trade-item trade-history height-prop-25" style={{background:'#17191F'}}>
                    <div className="iconfont icon-jiazai new-loading"></div>
                </div>)
        }
        const { user,currentMarket ,marketsConfData} = this.props;
        let coinNameArry,coinName,markerName;
        if(currentMarket){
            coinNameArry = currentMarket.split("_");
            coinName = coinNameArry[0].toUpperCase();
            markerName = coinNameArry[1].toUpperCase();
        }
        return (
            <div className="trade-item trade-history height-prop-25" style={{background:'transparent'}}>
                    <div className="trade-item-title" >
                        <div className="trade-item-title-right">
                            <ul className="clearfix">
                                <li style={{border:0}} className={this.state.tebName=="market"?"react-tabs__tab--selected":""} ><FormattedMessage id="成交记录"/></li>
                            </ul>
                        </div>
                    </div>

                    <div className="trade-content clearfix">
                        {
                            this.state.tebName=="market"?
                                <div className="tabBorBottom">
                                    <ul className="history-list-tit">
                                        <li className="wid30"><FormattedMessage id="时间"/></li>
                                        <li className="wid35"><FormattedMessage id="价格"/>({markerName})</li>
                                        <li className="wid35"><FormattedMessage id="成交量"/>({coinName})</li>
                                    </ul>
                                    <Historylist
                                        selectPK={this.selectPK}
                                        currentMarket={currentMarket}
                                        marketsConfData={marketsConfData}
                                        data={this.props.marketHistorydata}
                                        wsIsLoading={this.wsIsLoading}
                                        mkIsLoading={!this.ws.isNull || window.isvisibilitychangeTradeHistory ? this.props.mkIsLoading : true} />
                                </div>
                            :""
                        }
                    </div>
            </div>
        );
    }
}


export default withRouter(connect(state=>({}),
(dispatch) => {
    return{
        doSubscribeMsg:(fn = ()=>{})=>{
            dispatch(doSubscribeMsg(fn))
        }
    }
})(Tradehistory));