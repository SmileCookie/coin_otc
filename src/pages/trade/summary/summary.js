import React from 'react';
import {FETCH_SUMMARY_DATA} from '../../../conf';
import { FormattedMessage, injectIntl,FormattedTime ,FormattedDate} from 'react-intl';
import ReactModal from '../../../components/popBox';
const BigNumber = require('big.js');

class Summary extends React.Component {
    constructor(props) {
        super(props);
        this.summaryRecount = this.summaryRecount.bind(this);
        this.moadlRecount = this.moadlRecount.bind(this);
        this.state = {
            modalHTML:""
        }
    }
    componentDidMount() {
        const { user} = this.props;
        if(user){
            this.interval = setInterval(()=>{
                const {currentMarket,money} = this.props;
                if(currentMarket&&money){
                    // console.log(currentMarket);
                    this.props.fetchSummaryData(currentMarket,money.locale.name);
                }
            },FETCH_SUMMARY_DATA)
        }
    }
    componentWillReceiveProps(nextProps){
        const { user} = this.props;
        if(user){
            if( nextProps.currentMarket != this.props.currentMarket){
                const {currentMarket,money} = nextProps;
                // console.log(currentMarket);
                this.props.fetchSummaryData(currentMarket,money.locale.name);
            }
        }
    }
    componentWillUnmount() {
        if(this.interval){
            clearInterval(this.interval)
        }
    }
    moadlRecount(){
        const { user } = this.props;
        if(!user){
            this.props.notifSend(<FormattedMessage id="trade.operateAfterLogin"/>, "warning");
            return;
        }
        let str = <div className="modal-btn">
                <p><FormattedMessage id="sureRecount"/></p>
                <div className="modal-foot">
                    <a className="btn ml10" onClick={() => this.modal.closeModal()}><FormattedMessage id="cancel" /></a>
                    <a className="btn ml10" onClick={() => this.summaryRecount()}><FormattedMessage id="sure" /></a>
                </div>
            </div>;
        this.setState({modalHTML:str},()=>{
            this.modal.openModal();
        })
        
    }
    summaryRecount(){
        const {currentMarket,money} = this.props;
        this.props.summaryRecount(currentMarket,
            res=>{
                this.props.notifSend(<FormattedMessage id="RecountSuccess"/>);
                this.modal.closeModal();
                this.props.fetchSummaryData(currentMarket,money.locale.name);
            }
        );
    }
    render() {
        const { user ,isLoaded ,data ,assets ,currentMarket,marketsConfData,money} = this.props;
        let dataList = {
            "netValue": 0,
            "isProfit": 1,
            "costPrice": 0,
            "lastPrice": 0,
            "marketValue": 0,
            "profitOrLoss": 0,
        }
        let balance = 0,balanceMoney=0,costPrice=0,lastPrice=0,marketValue=0,profitOrLoss=0;
        let coinNameArry,coinName,markerName;
        let exchangeBixDian = 6 ;
        let numberBixDian = 5;
        BigNumber.RM = 0;
        if(currentMarket){
            coinNameArry = currentMarket.split("_");
            coinName = coinNameArry[0].toUpperCase();
            markerName = coinNameArry[1].toUpperCase();
            try{
                exchangeBixDian = marketsConfData[currentMarket].exchangeBixDian;
            } catch(e){
                
            }
            numberBixDian = marketsConfData[currentMarket].numberBixDian
        }
        // console.log(coinName);
        if(user&&isLoaded&&assets&&money.rate.exchangeRateBTC&&data.costPrice){
            dataList = data ;
            // console.log(assets[coinName]);
            balance = new BigNumber(assets[coinName].total).toFixed(2);
            costPrice = new BigNumber(dataList.costPrice).toFixed(4);
            lastPrice = new BigNumber(dataList.lastPrice).toFixed(4);
            marketValue = new BigNumber(dataList.marketValue).toFixed(2);
            profitOrLoss = new BigNumber(dataList.profitOrLoss).toFixed(2);
            // console.log(coinName);
            // console.log(assets[coinName]);
            // console.log(money);
            // console.log(money.rate["exchangeRate"+markerName.substr(0,3)][money.locale.name.toUpperCase()]);
            // balanceMoney = new BigNumber(assets[coinName].total).times(assets[coinName].usdExchange).times(money.rate["exchangeRate"+markerName.substr(0,3)][money.locale.name.toUpperCase()]).toFixed(2);
            balanceMoney = new BigNumber(assets[coinName].total).times(assets[coinName].usdExchange).times(money.rate["exchangeRateUSD"][money.locale.name.toUpperCase()]).toFixed(2);
        }
        // console.log(coinName+":"+balance);
        return (
            <div className="trade_summary">
                <div className="trade-item-title" >
                    <div className="trade-item-title-right">
                        <div className="btnRecount" onClick={this.moadlRecount}>
                            <i>&#xe693;</i>
                            <div><FormattedMessage id="Recount"/></div>
                        </div>
                    </div>
                    <h4><FormattedMessage id="SUMMARY"/></h4>
                </div>
                <div className="trade-content summaryDefault">
                    <div className="def-box clearfix">
                        <div className="buy-sum">
                            <p><b><FormattedMessage id="Buying"/> {coinName}</b></p>
                            <p><FormattedMessage id="buying.NetValue"/><em className="netValue">{dataList.isProfit > 0?dataList.netValue:0}</em></p>
                            <p><FormattedMessage id="CostPrice"/>({money.locale.logo})<em className="costPrice">{dataList.isProfit > 0?costPrice:0}</em></p>
                            <p><FormattedMessage id="LastPrice"/>({money.locale.logo})<em className="lastPrice">{lastPrice}</em></p>
                            <p><FormattedMessage id="MarketValue"/>({money.locale.logo})<em className="marketValue">{dataList.isProfit > 0?marketValue:0}</em></p>
                        </div>
                        <div className="sell-sum">
                            <p><b><FormattedMessage id="Selling"/> {coinName}</b></p>
                            <p><FormattedMessage id="selling.NetValue"/><em className="netValue">{dataList.isProfit <= 0?dataList.netValue:0}</em></p>
                            <p><FormattedMessage id="CostPrice"/>({money.locale.logo})<em className="costPrice">{dataList.isProfit <= 0?costPrice:0}</em></p>
                            <p><FormattedMessage id="LastPrice"/>({money.locale.logo})<em className="lastPrice">{lastPrice}</em></p>
                            <p><FormattedMessage id="MarketValue"/>({money.locale.logo})<em className="marketValue">{dataList.isProfit <= 0?marketValue:0}</em></p>
                        </div>
                    </div>
                    <p>{coinName} <FormattedMessage id="ProfitorLoss"/>({money.locale.logo})
                        {profitOrLoss < 0?
                            <em  className="red"><b>{profitOrLoss}</b> </em>
                        :""}
                        {profitOrLoss > 0?
                            <em  className="green"><b>+{profitOrLoss}</b> </em>
                        :""}
                        {profitOrLoss == 0?
                            <em><b>0</b> </em>
                        :""}
                    </p>
                    <p>{coinName} <FormattedMessage id="AccountTotalAssets"/>({money.locale.logo})<em ><b>{balanceMoney}</b></em></p>
                    {/* <div className="re-summary clearfix">
                        <a href="###" target="_blank">这是什么？</a>
                        <button onClick={this.summaryRecount}><FormattedMessage id="Recount"/></button>
                    </div> */}
                </div>
                <ReactModal ref={modal => this.modal = modal}>
                    {this.state.modalHTML}
                </ReactModal>
            </div>
        )
    }
}

export default Summary;