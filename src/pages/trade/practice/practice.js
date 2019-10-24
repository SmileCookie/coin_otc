import React from 'react';
import { FormattedMessage } from 'react-intl';
import { withRouter } from 'react-router'
const BigNumber = require('big.js'); 
import Practicelist from './practicelist';
import SelectList from '../../../components/selectList'
import { requsetWebsocket } from '../../../utils'
import { PRACTICE_LIST_NUM,DEFAULT_MARKETCOIN_TYPE } from '../../../conf/index' 
import { connect } from 'react-redux';
import { doSubscribeMsg } from '../../../redux/modules/socket'

class Practice extends React.Component {
    constructor(props) {
        super(props)

        this.fstData = {};
        
        this.state = {
            exchange:true,
            depth: "",
            options:[],
            depthStep:0,
            changeLoading:false,
            listnum:PRACTICE_LIST_NUM,
            dataError:false,
            selectPk: 0,
            pktj: 0,
        }
        
        this.updatBtn = false
        this.updateExchange = this.updateExchange.bind(this)
        this.initDepth = this.initDepth.bind(this)
        this.updateDepth = this.updateDepth.bind(this)
        this.startWebsocket = this.startWebsocket.bind(this)
        this.countPracticeNum = this.countPracticeNum.bind(this)
        this.pic = 0
        this.picType = ''
        this.picColor = ''
        this.isIE = (!!window.ActiveXObject || "ActiveXObject" in window)
        this.selectPk = this.selectPk.bind(this)
        this.getWs();
        this.in = 0;

        this.smarketinfoData = null;
    }

    getWs(newprops){
        this.ws = (!newprops ? this.props : newprops).socket
        this.ws = this.ws ? this.ws : {send:()=>{}, readyState : 1, isNull: window.isvisibilitychange ? 0 : 1, }
    }

    selectPk(opt = 0){
        this.setState({
            selectPk: opt,
            pktj: opt,
        })
    }

    componentDidMount(){
        this.startWebsocket()
        this.initDepth(this.props)
        window.addEventListener('resize',()=>{
            this.setState({
                listnum:this.countPracticeNum()
            })
        })

        // 轮训获取外部顶部实时价格，砍掉redux实现同步顶部价格。
        
    }

    componentWillReceiveProps(nextProps){
        this.getWs(nextProps);

        const {marketsConf} = nextProps;
        if(this.props.socket !== nextProps.socket || nextProps.marketinfo.currentMarket != this.props.marketinfo.currentMarket || nextProps.language != this.props.language ){
            this.initDepth(nextProps);
            this.setState({
                depthStep:0
            })
        }
        if(this.props.socket !== nextProps.socket || nextProps.marketinfo.currentMarket != this.props.marketinfo.currentMarket){
            this.props.cleanMarket()
            if(this.ws&&this.ws.readyState&&nextProps.marketinfo.currentMarket){
                // this.ws.close()
                this.updatBtn = true
                this.in = 0;
                this.saveBadPackageCount = 0;
                //this.ws.send(JSON.stringify({ "event": 'unsub',"channel":`market.${this.props.marketinfo.currentMarket}.depth.step${this.state.depthStep}`}))
            }
        }

        // 当socket句柄不同重新开启websocket
        if(this.props.socket !== nextProps.socket){
            this.startWebsocket();
        }
        
        if(nextProps.marketHistorydata[0]){
            this.pic = nextProps.marketHistorydata[0].price;
            this.picColor = nextProps.marketHistorydata[0].type === 'buy' ? 'true' : 'false';
            this.picColor = sessionStorage.isBuy === 'true' ? 'now-price-green':'now-price-red';
            
            // if(this.sPic != this.pic){
            //     if(this.sPic){
            //         this.picType = this.pic > this.sPic ? 'now-price-green':'now-price-red';
            //     }
            //     this.forceUpdate();
            // }
            // this.sPic = this.pic;
        }
    }
    
    shouldComponentUpdate(n,np){
        setTimeout(()=>{
            //console.log(np,n);
            this.asyncflg = true;
        }, 2000)
        //return !!(!this.isIE || this.asyncflg);
        return !!this.asyncflg;
    }

    componentWillUnmount() {
        clearInterval(this.picTimer);
        //this.ws.close()
        window.removeEventListener('resize',()=>{
            this.listnum = this.countPracticeNum();
        })
    }

    //启动 websocket
    startWebsocket(){
       
        const { marketinfo,params,marketsConf } = this.props
        const marketName =  params.paramName || DEFAULT_MARKETCOIN_TYPE;
        let depthStep = this.state.depthStep
        const MK = this.props.marketinfo.currentMarket;
        const DP = 1;
        depthStep = +depthStep ? depthStep : DP;
        let saveBadPackageCount = 0;
//console.log(depthStep, '====---->');
        this.mkName = marketName
        this.dpStep = depthStep

        this.ws.send(JSON.stringify({ "event": "sub","channel": `market.${marketName}.depth.step${depthStep}`}));

        this.props.doSubscribeMsg(
            (res) => {
                // 不关心的数据包和自己业务没关系的不理会
                if(!res.channel.includes("depth")){
                    return;
                }
                
                try{
                //console.log(res);
                let mkName = this.props.params.paramName,
                    dpStep = +this.state.depthStep ? this.state.depthStep : DP;
                    mkName = mkName ? mkName : MK;
// console.log(dpStep,'@@====--->@@@');
// console.log(MK, '=====--->>', this.props.marketinfo.currentMarket);                
                if(res.channel && res.channel.includes(mkName) && res.channel.includes(dpStep)){
                    if(res.datas&&res.market == this.props.marketinfo.currentMarket){
                        this.props.recieveMarketinfo(eval(`(${res.datas})`))
                        this.setState({
                            changeLoading:false
                        })
                        this.fstData = eval(`(${res.datas})`);
                  //      console.log(this.fstData, '------>>>>');
                        this.forceUpdate();
                        // console.log(this.fstData);
                        window.isvisibilitychange = false;
                    }
                    this.mkName = mkName;
                    this.dpStep = dpStep;
                    

                }else{
                    // if(res.msg&&res.msg.includes('unsub')){
                    //     const depthStep = this.state.depthStep
                    //     const marketName = this.props.marketinfo.currentMarket
                    //     this.ws.send(JSON.stringify({ "event": 'sub',"channel":`market.${marketName}.depth.step${depthStep}`}))  
                    // }if(res.msg&&res.msg.includes('sub')){
                    //     this.updatBtn = false
                    // }
                    
                    // if(res.msg&&res.msg.includes('unsub')){
                    //     // 回复新订阅
                    //     this.ws.send(JSON.stringify({ "event": 'sub',"channel":`market.${mkName}.depth.step${dpStep}`})) 
                    // } else{
                    //     // 取消订阅
                    //     this.ws.send(JSON.stringify({ "event": 'unsub',"channel":`market.${this.mkName}.depth.step${this.dpStep}`})) 
                    // }
// console.log(res.channel,
//     mkName,
//     dpStep,
//     res.msg,
//     this.mkName,
//     this.in);                    
                    if(res.channel && (!res.channel.includes(mkName) || !res.channel.includes(dpStep)) && (!res.msg || res.msg && !res.msg.includes('unsub'))){
                        // 直接unsub掉
                        //console.log(this.dpStep,'===---->');
                        this.ws.send(JSON.stringify({ "event": 'unsub',"channel": res.channel})) 
                    }
//console.log(this.dpStep,'===>><<', dpStep,'@@@@', this.mkName, mkName, res.msg);
                    // 关心的数据包并且没有被订阅过。
                    if((!res.channel.includes(mkName) || !res.channel.includes(dpStep))){
                        this.saveBadPackageCount ++;
                        if(this.saveBadPackageCount > 10 || !this.in){
                            this.ws.send(JSON.stringify({ "event": 'sub',"channel":`market.${mkName}.depth.step${dpStep}`}))
                            this.in = 1;
                        }

                        // 连续10个都不是自己订阅的包重新订阅后清零
                        if(this.saveBadPackageCount > 10){
                            this.saveBadPackageCount = 0;
                        }
                    }

                }
            }catch(e){

            }
        })
            
        
    }

    //委托条数
    countPracticeNum(){
        const screenHei = window.screen.height
        const bodyHei = document.body.clientHeight
            if(bodyHei >= 1000){
                return 8 
            }else if(bodyHei >= 960){
                return 7
            }else if(bodyHei >= 910){
                return 6
            }else if(bodyHei >= 862){
                return 5
            }else if(bodyHei >= 780){
                return 4
            }if(bodyHei >= 730){
                return 3
            }if(bodyHei >= 680){
                return 2
            }
    }

    updateDepth(value,index){
        //console.log(index, '-------->>>>');
        if(value != this.state.depth){
            const prevDepth = this.state.depthStep
            const { currentMarket } = this.props.marketinfo
            this.setState({
                depth: value,
                depthStep:index + 1,
                changeLoading:true
            },() => {
                // 触发盘口的重发机制。
                this.in = 0;
                this.updatBtn = true
                this.ws.send(JSON.stringify({
                    "event": 'unsub',
                    "channel":`market.${currentMarket}.depth.step${prevDepth}`
                }))
            })
        }
        
    }

    initDepth(pop){
        const {marketsConf} = this.props;
        let dp = "";
        if(pop.marketinfo.currentMarket){
            let mergePrice =  marketsConf[pop.marketinfo.currentMarket].mergePrice;
            let mergePriceArry = mergePrice.split(",");
            let mergePriceOptions = [
                
            ]
            for(let key in mergePriceArry){
                let thisOption = {
                    val: parseFloat(mergePriceArry[key] + 1),
                    key: <FormattedMessage id="Group" values={{value:mergePriceArry[key]}}/>,
                }
                mergePriceOptions.push(thisOption)

                //!this.setDfDepth && (dp = parseFloat(mergePriceArry[key]),this.setDfDepth = 1);
                //console.log(this.setDfDepth)
            }
            //console.log(mergePriceOptions)
            this.setState({
                depth: mergePriceOptions[0].val,
                options:mergePriceOptions
            })
        }
    }
    
    updateExchange() {
        this.setState({
            exchange: !this.state.exchange,
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
        
        //throw new Error(333)
        if(this.state.dataError){
            return (
                <div className="trade-item buy-practice box-allhei" style={{height:'245px'}}>
                    <div className="iconfont icon-jiazai new-loading"></div>
                </div>)
        } 
        const { isLoaded,marketinfo,moneyLocale,pics } = this.props
        const { changeLoading,exchange,options,listnum,depth, selectPk, pktj, } = this.state
        let currColor = '',marketCoinArr = [];
        let marketinfoData = marketinfo.data;
        
        marketinfoData && (this.smarketinfoData = marketinfoData);
        
        if(window.isvisibilitychange){
            this.smarketinfoData && (marketinfoData = this.smarketinfoData);
            this.ws.isNull = false;
        }

        let maxBuyNumber = 0;
        let s = 0;
        let legalMonNum = '0.00';
        if(marketinfoData){
            let listDown = marketinfoData.listDown;
            for(let key in listDown){
                const listDownTotal = listDown[key][1]*listDown[key][0];
                if(listDownTotal > maxBuyNumber){
                    maxBuyNumber = listDownTotal;
                }
                s++
                if(s>listnum){
                    s = 0
                    break;
                }
            }
            let listUp = marketinfoData.listUp;
            for(let key in listUp){
                const listUpTotal = listUp[key][1]*listUp[key][0];
                if(listUpTotal > maxBuyNumber){
                    maxBuyNumber = listUpTotal;
                }
                s++
                if(s>listnum){
                    s = 0
                    break;
                }
            }
            marketCoinArr = marketinfo.currentMarket.split('_')
            let marketCoin = marketCoinArr[1]
            if(marketCoin){
                BigNumber.RM = 0;
                marketCoin = marketCoin=="usdt"?"usd":marketCoin
                const legalMon = marketinfoData[`exchangeRate${marketCoin.toUpperCase()}`][moneyLocale.name]
                try{
                    legalMonNum = new BigNumber(/*marketinfoData.currentPrice*/this.pic).times(legalMon).toFixed(2)
                }catch(e){}
            }
            
            currColor = marketinfoData.currentIsBuy?'now-price-green':'now-price-red';
            // !this.picType && (this.picType = currColor); 
        }
        // if(!isLoaded){
        //     return (
        //         <div className="clearfix trans-practice">
        //             <div className="trade-item sell-practice">Loading...</div>
        //             <div className="trade-item buy-practice">Loading...</div>
        //         </div>
        //     )
        // }
        
        const cd = marketinfoData&&!changeLoading || this.fstData.data;
        // console.log(cd, marketinfoData, !this.ws.isNull, window.isvisibilitychange)
        return (
            <div className={exchange?"clearfix trans-practice trans-exchange height-prop-52":"clearfix trans-practice height-prop-52"}>
                <div className="trade-item buy-practice box-allhei">
                    <div className="trade-item-title" >
                        <ul className="up-pk-select clearfix">
                            <li className={0 === pktj ? 'ac' : ''} onClick={() => {this.selectPk(0)}}></li>
                            <li className={1 === pktj ? 'ac' : ''} onClick={() => {this.selectPk(1)}}></li>
                            <li className={2 === pktj ? 'ac' : ''} onClick={() => {this.selectPk(2)}}></li>
                        </ul>
                        <div className="practice-depth">
                            <FormattedMessage id="合并深度"/>:
                            <SelectList 
                                options={options}
                                class="sm right marginleft5"
                                Cb={this.updateDepth}
                                setVal={depth}
                            />
                        </div>
                    </div>
                    {marketinfoData?
                        <div className="practice-table-head">
                            <div className="practice-table-head-item"><FormattedMessage id="价格"/>({marketCoinArr[1].toUpperCase()})</div>
                            <div className="practice-table-head-item"><FormattedMessage id="数量"/>({marketCoinArr[0].toUpperCase()})</div>
                            <div className="practice-table-head-item"><FormattedMessage id="总额"/>({marketCoinArr[1].toUpperCase()})</div>
                        </div>
                        :
                        <div className="practice-table-head">
                            <div className="practice-table-head-item"><FormattedMessage id="价格"/></div>
                            <div className="practice-table-head-item"><FormattedMessage id="数量"/></div>
                            <div className="practice-table-head-item"><FormattedMessage id="总额"/></div>
                        </div>
                    }
                    <div className="trade-content-practice-box" id="pk">
                        {
                            true?
                            <React.Fragment>
                            {
                                0 === selectPk || 1 === selectPk ?
                                <Practicelist 
                                    selectPk={selectPk}
                                    pics={pics}
                                    theme={"sell"} 
                                    user={this.props.user} 
                                    num={listnum} 
                                    maxBuyNumber={maxBuyNumber}  
                                    data = { !this.ws.isNull && cd ? (marketinfoData ? marketinfoData.listUp : this.fstData.data.listUp) : []} 
                                    currentMarket={marketinfo.currentMarket} 
                                    marketsConf={this.props.marketsConf}
                                    updatePrice={this.props.updateSellPrice}
                                    paramName={this.props.params.paramName}
                                    />
                                    :
                                    null
                            }
                            {
                                !this.ws.isNull && cd
                                ?
                                <div className={`now-price ${/*currColor*/this.picColor}`}>
                                    <span>{/*marketinfoData.currentPrice*/this.pic}</span>    
                                    <span>≈ {legalMonNum} {moneyLocale.name}</span>
                                </div> 
                                :
                                <div className={`now-price`}>
                                    --
                                </div>   
                            }                       
                            {
                                0 === selectPk || 2 === selectPk ?
                                <Practicelist 
                                    selectPk={selectPk}
                                    pics={pics}
                                    theme={"buy"} 
                                    user={this.props.user} 
                                    num={listnum} 
                                    maxBuyNumber={maxBuyNumber} 
                                    data = {!this.ws.isNull && cd ? (marketinfoData ? marketinfoData.listDown : this.fstData.data.listDown) : [] } 
                                    currentMarket={marketinfo.currentMarket} 
                                    marketsConf={this.props.marketsConf} 
                                    updatePrice={this.props.updateBuyPrice}
                                    paramName={this.props.params.paramName}
                                    />
                                :
                                null
                            }
                            </React.Fragment>
                            :
                            <div className="iconfont icon-jiazai new-loading"></div>
                        }       
                    </div>
                </div>
            </div>
        );
    }
}

export default connect(state=>({marketHistorydata:state.marketHistoryData.data,pics: state.positioin.prices}),
(dispatch) => {
    return {
        doSubscribeMsg:(fn = ()=>{})=>{
            dispatch(doSubscribeMsg(fn))
        }
    }
}
)(withRouter(Practice));
