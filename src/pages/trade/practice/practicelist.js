import React from 'react';
import ScrollArea from 'react-scrollbar'
import { FormattedMessage, injectIntl } from 'react-intl';
import PropTypes from 'prop-types';
const BigNumber = require('big.js');

class practicelist extends React.Component {
    constructor(props){
        super(props)
        this.state = {

        }
        this.tmH = 0;
        this.ct = null;
        this.isRunSellH = 1;
        this.sbRefs = React.createRef();
        this.sbCdRef = React.createRef();

        this.tableKey = "_key" + Math.random();
    }
    componentWillReceiveProps(npros,nstates){
        if(!isNaN(this.selectPk) && this.selectPk !== npros.selectPk){
            this.rl();
        }
        this.selectPk = npros.selectPk;

        this.tableKey = npros.currentMarket + "_" + npros.theme + '_' + npros.paramName;
       // console.log(this.tableKey);
    }

    componentWillUnmount(){
        window.removeEventListener('resize', this.rz);
    }

    componentDidMount() {
        this.pk = document.getElementById("pk").getElementsByClassName("now-price")[0];
        //this.pk.setAttribute("style","visibility:hidden")

        this.ct = document.getElementsByClassName("sell")[0];
        
        // 初始化盘口
        this.rl();
        window.addEventListener('resize', this.rz);
    }
    rz=()=>{
        clearTimeout(this.tmr);
        this.pk.removeAttribute('style')
        this.tmr = setTimeout(()=>{
            this.rl();
        },300)
    }
    rl=()=>{
        try{
            // console.log('kkk');
            const scr = this.sbCdRef.current,
            tmH = this.refs.tmH;

            const cH = tmH.clientHeight,
            ITEM = 18,
            PKH = 20;

            const surplus = Number(0 + '.' + ((cH / ITEM) + '').split(".")[1]);
            let jg = 0;

            if(10 < cH / ITEM && 0.75 > surplus){
                const smp = Math.ceil(ITEM * surplus) * (!this.props.selectPk ? 2 : 1);
                jg = smp;
            }
            
            jg && this.pk.setAttribute('style', "flex-basis:"+(PKH+jg)+'px;line-height:'+(PKH+jg-12)+'px');
            //scr.style.top = jg + 'px';  
        }catch(e){
            
        }       
    }
    updatePrice(p, n, type){
        this.props.updatePrice(p, n, type)
    }
    formatFundsDetail(result,num){
        const {marketsConf,currentMarket,maxBuyNumber} = this.props;
        let Lister = [];
        let s = 0;
        let i = 0;
        let exchangeBixDian = 0;
        try{
            exchangeBixDian = marketsConf[currentMarket].exchangeBixDian
        } catch(e){

        }
        let numberBixDian = marketsConf[currentMarket].numberBixDian
        let accumulatedAmount = 0;
        for(let key in result){
            Lister[i] = {};
            accumulatedAmount += result[key][1];
            Lister[i].price  = new BigNumber(result[key][0]).toFixed(exchangeBixDian);
            Lister[i].amount = new BigNumber(result[key][1]).toFixed(numberBixDian);
            Lister[i].accumulatedAmount = new BigNumber(accumulatedAmount).toFixed(numberBixDian);
            Lister[i].total = new BigNumber(result[key][0]).times(result[key][1]).toFixed(exchangeBixDian);
            Lister[i].width = new BigNumber(Lister[i].total).div(maxBuyNumber).times(100).toFixed(numberBixDian)+"%";
            Lister[i].isuse = this.isString(result[key][2])?result[key][2]:result[key][3];
            i++;
            // 盘口增加num改为50
            if(i>=50){
                break;
            }
        }
        return Lister;
    }
    isString(str){ 
        return (typeof str=='string')&&str.constructor==String; 
    } 
    componentDidUpdate(){
        //this.isRunSellH && this.pk.removeAttribute("style");
    }
    render() {
        const {pics} = this.props;
        //const pics = [];
        
        try{
        try{
            
            !this.tmH && (this.tmH = this.refs.tmH.offsetHeight);
            this.isRunSellH = 1;
            
        }catch(e){
           
        }

        const { data,theme,currentMarket,num,paramName } = this.props;
        let tmQueue = [];
        const SH = 20;

        // if( paramName&&(paramName != currentMarket) ){
        //     return null
        // }
        const formateData = this.formatFundsDetail(data,num);
        let dataList = theme=="sell"?formateData.reverse():formateData;

        if(this.tmH && theme === 'sell'){
            try{
                tmQueue = new Array(Math.floor((this.tmH - dataList.length * SH) / SH)).fill(0);
            } catch(e){
                tmQueue = [];
            }
        }
 //console.log(dataList, data,theme,dataList.length);
        return(
            <div className={"trade-moudle "+theme} ref="tmH">
            <div style={{height:'100%',position:'absolute',left:0,top:0,right:0,bottom:0}} ref={this.sbCdRef}>
                {this.isRunSellH && <ul className="table-bg" style={theme === 'sell' ? {bottom:'1px',top:'auto'} : {}}>
                    {
                        tmQueue.map((v, k) => <li className="percent-cover" key={k}></li>)
                    }
                    {
                        dataList.map((item,index)=>{
                            const colorType = theme=="sell"?"red":"green";
                            return(
                                <li key={item.amount+"_"+item.price} className="percent-cover">
                                    <span className={`${colorType} practice_${colorType}`} style={{width: item.width}}></span>
                                </li>
                            )
                        })
                    }
                    
                </ul>}
                {this.isRunSellH ? <table key={this.tableKey} className={`${theme}-practice-table`} style={theme === 'sell' ? {position:'absolute',bottom:0} : {}}>
                    <tbody>
                        {
                            tmQueue.map((v, k) => <tr className="nohover" key={k}><td width="30%" className="text-right"></td></tr>)
                        }
                        {
                            dataList.length?
                            dataList.map((item,index)=>{
                                    return(
                                        <tr className={pics.includes(item.price) ? 'ac' : ''} key={item.amount+"_"+item.price+"_"} onClick={this.updatePrice.bind(this, item.price, item.accumulatedAmount, theme)}>
                                            <td width="30%" className={theme=="sell"?"red":"green"}>{item.price}</td>
                                            <td width="30%" className="text-right">{item.amount}</td>
                                            <td width="40%" className="text-right">{item.total}</td>
                                        </tr>
                                    )
                                }
                            ):
                            (()=>{
                                let i = 0;
                                let zws = [];
                                while(i < 100){
                                    zws.push(
                                        <tr key={i}>
                                            <td width="30%">--</td>
                                            <td className="text-right" width="30%">--</td>
                                            <td className="text-right" width="40%">--</td>
                                        </tr>
                                    );
                                    i++;
                                }
                                return zws;
                            })()
                        }
                    </tbody>
                </table>:null}
                {
                    theme=="sell" && this.ct && false &&
                    <Sp />
                }
                </div>
            </div>
        )
    }catch(e){
        return null;
    }

    }
}

class Sp extends React.Component{
    componentWillReceiveProps(){
        !this.pc && this.context.scrollArea.scrollBottom();
        this.pc = 1;
    }
    render(){
        return null;
    }
}
Sp.contextTypes = {
    scrollArea: PropTypes.object
}
export default practicelist;