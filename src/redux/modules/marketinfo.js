import axios from 'axios';
import { DOMAIN_BASE, DOMAIN_VIP, DOMAIN_TRANS } from '../../conf';
const FETCH_MARKET_INFO = 'btcwinex/marketinfo/FETCH_MARKET_INFO';
const REQUEST_MARKET_INFO = 'btcwinex/marketinfo/REQUEST_MARKET_INFO';
const RECIEVE_MARKET_INFO = 'btcwinex/marketinfo/RECIEVE_MARKET_INFO';
const RECIEVE_MARKET = 'btcwinex/marketinfo/RECIEVE_MARKET';
const CLEAN_MARKET = 'btcwinex/marketinfo/CLEAN_MARKET';


// export const fetchMarket=(currentMarket,currentMarketAllName)=>{
//     return (dispatch, getState) => {
//         dispatch(fetchMarketInfo(currentMarket,currentMarketAllName,"",0,5,1));
//     }
// }
export const fetchMarketInfo = (currentMarket,currentMarketAllName,depth,lastTime,length,fun) => {
    return (dispatch, getState) => {
        
        if(fun){
            // console.log(fun);
            dispatch(recieveMarket(currentMarket,currentMarketAllName));
        }
    };
}

export const requestMarketinfo = () => {
    return {
        type: REQUEST_MARKET_INFO
    }
}

export const recieveMarketinfo = (marketinfo) => {
    return {
        type: RECIEVE_MARKET_INFO,
        payload: {
            marketinfo: marketinfo
        }
    }
}
export const recieveMarket = (currentMarket) => {
    return {
        type: RECIEVE_MARKET,
        payload: {
            currentMarket: currentMarket
        }
    }
}

//清空 市场委托
export const cleanMarket = () => ({
        type: CLEAN_MARKET
})

const initialMarketinfoState = {
    isLoading: false,
    isLoaded: false,
    currentMarket: 'btc_usdt',
    currentMarketAllName:'',
    data: null
}

const reducer = (state = initialMarketinfoState, action) => {
    switch(action.type) {
        case REQUEST_MARKET_INFO:
            return Object.assign({}, state, {
                isLoading: true
            });
        case RECIEVE_MARKET_INFO:
            return Object.assign({}, state, {
                isLoading: false,
                isLoaded: true,
                data: action.payload.marketinfo
            });
        case RECIEVE_MARKET:
            return Object.assign({}, state, {
                isLoading: false,
                isLoaded: true,
                currentMarket: action.payload.currentMarket,
                currentMarketAllName:action.payload.currentMarketAllName,
            });
        case CLEAN_MARKET:
            return Object.assign({},state,{
                data:null
            })
        default:
            return state;
    }
}

export default reducer;