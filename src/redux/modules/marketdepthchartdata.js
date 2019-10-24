import axios from 'axios';
import { DOMAIN_BASE, DOMAIN_VIP, DOMAIN_TRANS } from '../../conf';
const REQUEST_MARKET_CHART_INFO = 'btcwinex/marketinfo/REQUEST_MARKET_CHART_INFO';
const RECIEVE_MARKET_CHART_INFO = 'btcwinex/marketinfo/RECIEVE_MARKET_CHART_INFO';
export const fetchMarketDepthChartData = (currentMarket) => {
    return (dispatch, getState) => {
        dispatch(requestMarketinfo());
        axios.get(DOMAIN_TRANS + '/Line/getMarketDepth-'+currentMarket+'?&jsoncallback=')
            .then(res => {
                try{
                let marketdepth = eval(res['data'])[0];
                // console.log(marketdepth);
                dispatch(recieveMarketinfo(marketdepth));
                }catch(e){}
            });
    };
}

export const requestMarketinfo = () => {
    return {
        type: REQUEST_MARKET_CHART_INFO
    }
}

export const recieveMarketinfo = (marketdepth) => {
    return {
        type: RECIEVE_MARKET_CHART_INFO,
        payload: {
            marketdepth: marketdepth,
        }
    }
}

const initialMarketDepthDataState = {
    isLoading: false,
    isLoaded: false,
    data: null
}

const reducer = (state = initialMarketDepthDataState, action) => {
    switch(action.type) {
        case REQUEST_MARKET_CHART_INFO:
            return Object.assign({}, state, {
                isLoading: true
            });
        case RECIEVE_MARKET_CHART_INFO:
            return Object.assign({}, state, {
                isLoading: false,
                isLoaded: true,
                data: action.payload.marketdepth
            });
        default:
            return state;
    }
}

export default reducer;