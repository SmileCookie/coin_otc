import React from 'react';
import axios from 'axios';
import { FormattedMessage, injectIntl } from 'react-intl';
import { Link,withRouter } from 'react-router';
import {numToKiloMillion,formatSort} from '../../../utils';
import { removeFavMarkets,addFavMarkets } from '../../../redux/modules/markets';
import {DOMAIN_VIP} from '../../../conf';
const BigNumber = require('big.js');
import SidebarItem from './sidebarItem'
import ScrollArea from 'react-scrollbar'

class SidebarListDnd extends React.Component {
    constructor(props) {
        super(props);
        this.addFav = this.addFav.bind(this);
        this.removeFav = this.removeFav.bind(this);
    }
    componentDidMount() {
        //console.log("render sidebar list");
        //this.props.changeMarketinfo("btc_usdt","Bitcoin");
    }

    chooseMarket(name,allName){
        const {money} = this.props;
        window.scrollTo(0,0);
        this.props.changeMarketinfo(name,allName,money.locale.name);
    }
    removeFav(key,e){
        try{
        const { user } = this.props;
        // console.log(key);
        if(user){
            axios.get(DOMAIN_VIP + "/manage/closeCollect?"+"market=" + key,{withCredentials:true})
            .then(res => {
                console.log(res.data.isSuc);
                if(res.data.isSuc){
                    removeFavMarkets(key);
                    this.props.fetchMarketsData();
                }
            })
        }else{
            removeFavMarkets(key);
            this.props.fetchMarketsData();
        }
        e.stopPropagation();
    }catch(e){}
    }
    addFav(key,e){
        try{
        const { user } = this.props;
        // console.log(key)
        if(user){
            axios.get(DOMAIN_VIP + "/manage/userCollect?"+"market=" + key,{withCredentials:true})
            .then(res => {
                if(res.data.isSuc){
                    addFavMarkets(key);
                    this.props.fetchMarketsData();
                }
            })
        }else{
            addFavMarkets(key);
            this.props.fetchMarketsData();
        }
        e.stopPropagation();
    }catch(e){}
    }
    render() {
        //throw new Error(2222)
        const { marketType, markets, currentMarket,marketsConfData,showEstimated,volBase,rate,sdicBase,money,coinSearch} = this.props;
        const marketsFormated = formatSort(markets,sdicBase);
        return(
            <ScrollArea className="trade-scrollarea">
                <div className="sidebar-list">
                
                        {Object.keys(marketsFormated).length>0?
                            Object.keys(marketsFormated).filter((item, index, arr) => {
                                const itemCoin = item.split("_")[0]
                                return itemCoin.includes(coinSearch.toLowerCase())
                            }).map(
                                (key, index) => {
                                    let coin = key.split("_");
                                    let imageName = coin[0];
                                    let markName = coin[1].toUpperCase();
                                    let coinName = imageName.toUpperCase();
                                    let coinFullname = coin[3].replace("+", " ");
                                    let coinSymbol = coin[0] + "_" +coin[1];
                                    let exchangeFixedNum = 6 ;
                                    try{
                                    if(marketsConfData[coinSymbol] && marketsConfData[coinSymbol].exchangeBixDian){
                                        exchangeFixedNum = marketsConfData[coinSymbol].exchangeBixDian;
                                    }
                                }catch(e){}
                                    //console.log(coinSymbol);
                                    //console.log(exchangeFixedNum);
                                    let activeClassName = ""; 
                                    let range = marketsFormated[key][8];
                                    
                                    let rangeHtml = <span className='coin-price-range'>0.00%</span>
                                    if(range>=0){
                                        rangeHtml = <span className='coin-price-range green'>+{range.toFixed(2)}%</span>;
                                    }else if(range<0){
                                        rangeHtml = <span className='coin-price-range red'>-{Math.abs(range).toFixed(2)}%</span>;
                                    }
                                    if(currentMarket==coinSymbol){
                                        activeClassName = "active";
                                    }
                                    let rangeOf24h = marketsFormated[key][9];
                                    if(rangeOf24h == 0 || rangeOf24h == null){
                                        rangeOf24h = "0.00";
                                    }else{
                                        if(markName==volBase){                            
                                            rangeOf24h = new BigNumber(marketsFormated[key][9]).toFixed(2);
                                        }else {                          
                                            if(volBase=="SELF"){
                                                rangeOf24h = new BigNumber(marketsFormated[key][5]).toFixed(2);
                                            }else if(markName=="USDT"){
                                                rangeOf24h = rate != 0?new BigNumber(marketsFormated[key][9]).div(rate).toFixed(2):0;
                                            }else if(markName=="BTC"){
                                                rangeOf24h = new BigNumber(marketsFormated[key][9]).times(rate).toFixed(2);
                                                // console.log(marketsFormated[key][9]+"*"+rate+"="+rangeOf24h);                                       
                                            }
                                        }
                                    }
                                    // console.log(rangeOf24h);
                                    rangeOf24h = numToKiloMillion(rangeOf24h,2)
                                    let price = marketsFormated[key][0];
                                    if(price == 0 || price == null){
                                        let zero = 0
                                        price = zero.toFixed(exchangeFixedNum);
                                    }else{
                                        price = new BigNumber(price).toFixed(exchangeFixedNum);
                                    }
                                    let fav = marketsFormated[key][10];
                                    let open = marketsFormated[key][11];
                                    let tareMoney = 0;
                                    if(showEstimated && money.rate.exchangeRateBTC){
                                        // console.log(money)
                                        let rate = money.rate["exchangeRate"+markName.substr(0,3)][money.locale.name.toUpperCase()];
                                        // console.log(markName.substr(0,3))
                                        // console.log(rate)
                                        tareMoney = new BigNumber(price).times(rate).toFixed(2);
                                    }
                                    return (
                                        <SidebarItem 
                                            key={key}
                                            coinKey={key}
                                            fav={fav}
                                            coinName={coinName}
                                            markName={markName}
                                            showEstimated={showEstimated}
                                            price={price}
                                            tareMoney={tareMoney}
                                            rangeHtml = {rangeHtml}
                                            range = { range }
                                            removeFav = {this.removeFav}
                                            addFav = {this.addFav}
                                            logo={money.locale.logo}
                                            activeClassName={activeClassName}
                                        />
                                    )
                                }
                            ):
                            <div className="haveNoFav">
                                <i className="iconfont icon-tongchang-tishi norecord"></i> 
                                &nbsp;<FormattedMessage id="收藏列表为空"/>
                            </div>
                            
                        }
                
                </div>
            </ScrollArea>
        )
    }
}
export default withRouter(SidebarListDnd);