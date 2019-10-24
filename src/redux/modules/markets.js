import axios from 'axios';
import cookie from 'js-cookie';
import { TH, Types } from '../../utils';
import { DOMAIN_BASE, DOMAIN_VIP, DOMAIN_TRANS,COOKIE_MARKETS_FAV,COOKIE_EXPIRED_DAYS,DOMAIN_COOKIE} from '../../conf';
const BigNumber = require('big.js');
//get all coins info(name, fullname, price, price change, volume)
const FETCH_MARKETS_DATA = 'btcwinex/markets/FETCH_MARKETS_DATA';
const REQUEST_MARKETS_DATA = 'btcwinex/markets/REQUEST_MARKETS_DATA';
const RECIEVE_MARKETS_DATA = 'btcwinex/markets/RECIEVE_MARKETS_DATA';

export const requestMarketsinfo = () => {
    return {
        type: REQUEST_MARKETS_DATA
    }
}
export const recieveMarketsinfo = (markets) => {
    let marketlist = {
        USDT:{},
        BTC:{},
        FAV:{}
    }
    let localeCookie = cookie.get(COOKIE_MARKETS_FAV);
    if(!localeCookie||localeCookie==undefined){
        localeCookie = "";
    }
    let marketsFavArr = localeCookie.split("-");
    let rateBtc = markets.btc_usdt_hotdata_Bitcoin[0];
    Object.keys(markets).map(
        (key,index)=>{
            //console.log(key+":"+markets[key][1]);
            let keyP = key.replace(/ /g, "+");
            let coin = key.split("_");
            let marketName = coin[1].toUpperCase();
            //console.log(marketName);
            marketlist[marketName][keyP] = markets[key];
            marketlist[marketName][keyP][10]=false;
            if(key=="ABCDE_btc_hotdata_ABCDE"){
                marketlist[marketName][keyP][11]=false;
            }else{
                marketlist[marketName][keyP][11]=true;
            }
            // console.log(markets[key][9]);
            if(!isNaN(markets[key][9])){
                // console.log(markets[key][9]);
                if(marketName=="USDT"){
                    marketlist[marketName][keyP][12]=new BigNumber(markets[key][9]).toFixed(6);
                }else if(marketName == "BTC"){
                    marketlist[marketName][keyP][12]=new BigNumber(markets[key][9]).times(rateBtc).toFixed(6);
                }
            }
            marketsFavArr.map(
                (k,i)=>{
                    if(k==keyP){
                        marketlist.FAV[keyP] = markets[key];
                        marketlist[marketName][keyP][10]=true;
                    }
                }
            )
        }
    )
    //console.log(marketlist);
    return {
        type: RECIEVE_MARKETS_DATA,
        payload: marketlist
    }
}
export const addFavMarkets = (name) => {
    let localeForCookie;
    let localeCookie = cookie.get(COOKIE_MARKETS_FAV)||"";
    localeForCookie = localeCookie+"-"+name;
    cookie.set(COOKIE_MARKETS_FAV, localeForCookie, {
        expires: COOKIE_EXPIRED_DAYS,
        domain: DOMAIN_COOKIE,
        path: '/'
    })
}
export const removeFavMarkets = (name) => {
    let localeForCookie;
    let localeCookie = cookie.get(COOKIE_MARKETS_FAV);
    let marketsFavArr = localeCookie.split("-");
    for(let e = 0;e < marketsFavArr.length;e++){
        if(marketsFavArr[e]==name){
            marketsFavArr.splice(e,1);
            break;
        }
    }
    
    localeForCookie = marketsFavArr.join("-");
    cookie.set(COOKIE_MARKETS_FAV, localeForCookie, {
        expires: COOKIE_EXPIRED_DAYS,
        domain: DOMAIN_COOKIE,
        path: '/'
    })
}
export const fetchMarketsData = () => {
    return dispatch => {
        dispatch(requestMarketsinfo());
        axios.get(DOMAIN_TRANS + '/line/topall?jsoncallback=')
            .then(res => {
                try{
                    let markets = eval(res['data'])[0];
                    
                    for(const item in markets){
                        Types.isArray(markets[item]);
                    }

                    dispatch(recieveMarketsinfo(markets));
                    
                }catch(e){

                }
            });
    };
}

const initialMaketsState = {
    isLoading: false,
    isLoaded: false,
    marketsData: null
}

const reducer = (state = initialMaketsState, action) => {
    switch(action.type) {
        case REQUEST_MARKETS_DATA:
            return Object.assign({}, state, {
                isLoading: true
            });
        case RECIEVE_MARKETS_DATA:
            return Object.assign({}, state, {
                marketsData: action.payload,
                isLoading: false,
                isLoaded: true
            });
        default:
            return state;
    }
}

export default reducer;
