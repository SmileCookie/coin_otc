import React from 'react';
import axios from 'axios';
import { FormattedMessage, injectIntl } from 'react-intl';
import { Link,withRouter } from 'react-router';
import {numToKiloMillion,formatSort, pointCount} from '../../../utils';
import { removeFavMarkets,addFavMarkets } from '../../../redux/modules/markets';
import {DOMAIN_VIP,DEFAULT_MARKETCOIN_TYPE} from '../../../conf';
const BigNumber = require('big.js');
import Scrollarea from 'react-scrollbar'
import { connect } from 'react-redux';
import { recieveMarkethistory } from '../../../redux/modules/markethistory';
import {reqPic} from '../../../redux/modules/position';

class SidebarList extends React.Component {
    constructor(props) {
        super(props);
        this.addFav = this.addFav.bind(this);
        this.removeFav = this.removeFav.bind(this);
        this.state = {
            dropMenu:false
        }
    }
    componentDidMount() {
        //console.log("render sidebar list");
        //this.props.changeMarketinfo("btc_usdt","Bitcoin");
    }

    chooseMarket(name,allName){
        // 获取当前所在的盘口
        const CurrentPK = this.props.params.paramName || DEFAULT_MARKETCOIN_TYPE;
        if(CurrentPK !== name){
            this.props.recieveMarkethistory([]);
            this.props.reqPic();
            const {money} = this.props;
            window.scrollTo(0,0);
            this.props.changeMarketinfo(name,allName,money.locale.name);
            this.props.router.replace(`/bw/trade/${name}`)
        }
    }
    removeFav(e,key){
        try{
        const { user } = this.props;
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
        e.nativeEvent.stopImmediatePropagation();
        e.stopPropagation();
    }catch(e){}
    }
    addFav(e,key){
        try{
        const { user } = this.props;
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
        e.nativeEvent.stopImmediatePropagation();
        e.stopPropagation();
    }catch(e){}
    }
    //判断语言
    // chooseLan(){
    //     console.log(this.props)
    //     return 123
    // }
    render() {
            //throw new Error(2222)
        const { marketType, markets, currentMarket,marketsConfData,showEstimated,rate,sdicBase,money,coinSearch,marketHistorydata } = this.props;
        const marketsFormated = formatSort(markets,sdicBase);
        const {locale} = this.props;
        // console.log(locale)
        
        return(
            <Scrollarea className="trade-scrollarea">
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
                            }}catch(e){}
                            //console.log(coinSymbol);
                            //console.log(exchangeFixedNum);
                            let activeClassName = ""; 
                            let range = marketsFormated[key][8];
                            const rangColor = range>=0?'green':'red';
                            let rangeHtml = <span className='coin-price-range'>0.00%</span>
                            if(range>=0){
                                rangeHtml = <span className='coin-price-range green'>+{range.toFixed(2)}%</span>;
                            }else if(range<0){
                                rangeHtml = <span className='coin-price-range red'>-{Math.abs(range).toFixed(2)}%</span>;
                            }
                            if(currentMarket==coinSymbol){
                                activeClassName = "active";
                            }
                            const pc = marketHistorydata[0] && activeClassName === "active" ? marketHistorydata[0].price : marketsFormated[key][0];
                            let price = pc;
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
                                    <div key={key} className={`coin ${activeClassName} ${open?"":"gray"} clearfix`}>
                                    <a href='javascript:void(0)' onClick={() => {pointCount();this.props.recieveMarket(coinSymbol)}}>
                                        <div className="sidebar-list-market-box clearfix"  onClick={()=>{this.chooseMarket(coinSymbol,key)}}>
                                            <div className="sidebar-list-market">
                                                {fav?
                                                    <span className="coin-fav coin-fav-true" onClick={(e)=>{this.removeFav(e,key)}}>
                                                        &#xe6ad;
                                                        <span className="tips"><FormattedMessage id="取消自选"/></span>
                                                    </span>:
                                                    <span className="coin-fav" onClick={(e)=>{this.addFav(e,key)}}>
                                                        &#xe6ac;
                                                        <span className="tips"><FormattedMessage id="加入自选"/></span>
                                                    </span>
                                                }
                                                
                                                <span className="coin-name coin-name-fav">{coinName+"/"+markName}</span>
                                            </div>
                                            <div className="sidebar-list-price">
                                                {showEstimated?
                                                    <span className={`coin-current-price coin-current-price-usdt ${rangColor}`}>
                                                        {price}&nbsp;<i>{money.locale.logo} {tareMoney}</i>
                                                    </span>
                                                :
                                                    <span className="coin-current-price">
                                                        {price}
                                                    </span>
                                                }
                                            </div>
                                                {rangeHtml}
                                        </div>
                                    </a>
                                </div>
                            )
                        }
                    ):
                    <div className="haveNoFav">
                        <i className="iconfont icon-tongchang-tishi norecord"></i> 
                        &nbsp;<FormattedMessage id="收藏列表为空"/>
                    </div>
                }
               
                </div>
            </Scrollarea>
        )
    }
}
export default withRouter(connect(state=>({marketHistorydata:state.marketHistoryData.data}),(dispatch) => {
    return {
        reqPic:() => {
            dispatch(reqPic([]));
        },
        recieveMarkethistory:(data)=>{
            dispatch(recieveMarkethistory(data)); 
        }}})(SidebarList));