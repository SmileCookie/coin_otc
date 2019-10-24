import React from 'react';
import { FormattedMessage } from 'react-intl';
import { numToKiloMillion, numFm, }from '../../../utils';
import {ERRORCONFIG} from '../../../conf'
const BigNumber = require('big.js');
import {withRouter} from 'react-router'
import cookie from 'js-cookie';
import ErrorCompont from '../../common/ErrorComponent'

import TitleSet from "../../../pages/common/titleSet";


class Marketinfo extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            exchangeMoney: this.props.money.locale.name.toUpperCase(),
            dataError:false,
            res:"",
            ress:""
        }
    }


    componentWillReceiveProps(nextProps){
        if(nextProps.money!=this.props.money){
            this.updateValue(nextProps.money.locale.name.toUpperCase());
        }
        var url = window.location.href;
        var indes = url.lastIndexOf("\/");
        var str = url.substring(indes + 1, url.length);
        if(str!="multitrade"){
            this.setState({
                res: "enws",
                ress:"enwss"
            })
        }
    }
    // componentDidMount() {
    //     throw new Error(2222)
    //     // this.interval = setInterval(()=>{
    //     //     const {marketinfo} = this.props;
    //     //     // console.log("FETCH_MARKETS_INFO"+FETCH_MARKETS_INFO+"  -------"+marketinfo.currentMarket+":"+marketinfo.currentMarketAllName)
    //     //     this.props.fetchMarketInfo(marketinfo.currentMarket,marketinfo.currentMarketAllName);
    //     // },FETCH_MARKETS_INFO)
    // }
    componentWillMount() {
        if(this.props.marketinfo.currentMarket){
            let coin = this.props.marketinfo.currentMarket.split("_");
            let moneyName = coin[1].toUpperCase();
            this.updateValue(moneyName);
        }
    }
    updateValue(money) {
        this.setState({
            exchangeMoney: money
        })
    }

    render() {
        //throw new Error(222)
        const { marketinfo, marketsConf,money,markets,klineCoin, marketHistorydata, moneyLocale } = this.props;
        console.log(this.props)
        let pathname = this.props.location.pathname;
        let isLoaded = markets.isLoaded;
        let currentMarket = klineCoin || marketinfo.currentMarket;
        let currentMarkets=this.props.currentMarket
        if(currentMarkets!=undefined && !currentMarkets.toUpperCase().includes("USDC")){
            currentMarket=currentMarkets
        }
        BigNumber.RM = 0;
        if( !isLoaded|| !currentMarket || !marketsConf || this.state.exchangeMoney == ""){
            return (
                <div>Loading...</div>
            )
        }
        let coin = currentMarket.split("_");
        let coinName = coin[0].toUpperCase();
        let moneyName = coin[1].toUpperCase();
        let conf = marketsConf[currentMarket];
        let exchangeBixDian = 2;   // 法币 暂时写死
        // 虚拟货币 小数
        // if (currentMarket.toUpperCase().includes("_USDT")){
        //     exchangeBixDian = 2;
        // }else{
        //     exchangeBixDian = conf["exchangeBixDian"] ? conf["exchangeBixDian"] : 0;
        // }

        let numberBixDian = conf["numberBixDian"];
        let rateDate = money.rate
        let marketData;
        let marketAllData = markets.marketsData[moneyName]

        // 获取折算率
        const currentFB = moneyLocale.name;
        const marketCoin = coin[1]=="usdt"?"usd":coin[1];
        const legalMon = rateDate[`exchangeRate${marketCoin.toUpperCase()}`][currentFB];


        for(let curKey in marketAllData){
            if(curKey.includes(currentMarket)){
                marketData = marketAllData[curKey]
            }
        }
        let by = 0;
        if(marketHistorydata[0]){
            marketData[0] = marketHistorydata[0].price;
            by = marketHistorydata[0].type;
        }
        let titlePrice = new BigNumber(marketData[0]) // title 价格
        let currentPrice = new BigNumber(marketData[0]).toFixed(exchangeBixDian);
        //console.log(marketData[0], legalMon,'#$%%')
        const cpShow = new BigNumber(marketData[0]).times(legalMon).toFixed(exchangeBixDian)
        //可以作为title 实时变化的数


        // 顶部价格发生改变，改变委托价格。
        try{
            this.pic != currentPrice && (sessionStorage.currentPrice = currentPrice, sessionStorage.isBuy = (marketData[8] >= 0), this.pic = currentMarket);
        } catch(e){

        }

        let lastPrice = marketData[0];
        let highPrice = marketData[3];
        let lowPrice = marketData[4];
        let volume = marketData[5];
        let volumeMarkets = +(new BigNumber(marketData[9]).times(legalMon));
        let exchangeRate = 1;
        let rangeHtml =  <span>0.00%</span>;
        let range = marketData[8];
        if(range>=0){
            rangeHtml = <span className='green'>+{range.toFixed(2)}%</span>;
        }else if(range<0){
            rangeHtml = <span className='red'>-{Math.abs(range).toFixed(2)}%</span>;
        }
        highPrice = new BigNumber(highPrice).times(legalMon).toFixed(exchangeBixDian);
        lowPrice = new BigNumber(lowPrice).times(legalMon).toFixed(exchangeBixDian);
        //volumeMarkets = numToKiloMillion(new BigNumber(volumeMarkets).toFixed(2),2);

        volumeMarkets = numFm(volumeMarkets);
        const moneyNamed = volumeMarkets.unit;

        volumeMarkets = volumeMarkets.num;

        if(volume <= 1000){
            volume = new BigNumber(volume).toFixed(numberBixDian);
        }else{
            volume = numToKiloMillion(new BigNumber(volume).toFixed(numberBixDian),2);
        }


        return (
            <div className="coininfo">
                <div className={`coininfo-title ${this.state.ress}`}>
                    {coinName}/{moneyName}
                </div>
                <div className="coininfo-price">
                    <div className="coininfo-price-data">
                        <ul className={this.state.res}>
                            <li>
                                <h6><FormattedMessage id="最新价"/></h6>
                                <p>≈{moneyLocale.logo} {cpShow}</p>
                            </li>
                            <li>
                                <h6><FormattedMessage id="涨跌幅"/></h6>
                                <p>{rangeHtml}</p>
                            </li>
                            <li>
                                <h6><FormattedMessage id="24小时最高价"/></h6>
                                <p>≈{moneyLocale.logo} {highPrice}</p>
                            </li>
                            <li>
                                <h6><FormattedMessage id="24小时最低价"/></h6>
                                <p>≈{moneyLocale.logo} {lowPrice} </p>
                            </li>
                            <li>
                                <h6><FormattedMessage id="24小时成交量"/></h6>
                                <p>≈{moneyLocale.logo} {volumeMarkets}{ moneyNamed } </p>
                            </li>
                        </ul>
                    </div>
                </div>
                {pathname!="/bw/multitrade"?(<TitleSet titlemoney={`${titlePrice} ${coinName}/${moneyName}`} titleval={'币币交易'}/>):null}

            </div>
        );
    }
}

export default withRouter(Marketinfo);
