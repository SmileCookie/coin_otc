import axios from 'axios';
import { DOMAIN_BASE, DOMAIN_VIP, DOMAIN_TRANS } from '../../conf';

const REQUEST_MARKET_HISTORY_DATA = 'btcwinex/markets/REQUEST_MARKET_HISTORY_DATA';
const RECIEVE_MARKET_HISTORY_DATA = 'btcwinex/markets/RECIEVE_MARKET_HISTORY_DATA';

export const requestMarkethistory = () => {
    return {
        type: REQUEST_MARKET_HISTORY_DATA
    }
}
export const recieveMarkethistory = (data) => {
    return {
        type: RECIEVE_MARKET_HISTORY_DATA,
        payload: data
    }
}
export const fetchMarketHistoryData = (market,initRecord,last_trade_tid) => {
    return dispatch => {
        dispatch(requestMarkethistory());
        let id = "0";
        if(last_trade_tid){
            id = last_trade_tid
        }
        axios.get(DOMAIN_TRANS + "/getLastTrades?callback=&symbol=" + market + "&last_trade_tid=" + id)
            .then(res => {
                let data = res['data']['datas']||[];
                if(data.length<=0||data=={}){
                    data = [];
                }
                // console.log(data);
                dispatch(recieveMarkethistory(data));
            });
    };
}

const initialMarketHistoryData = {
    isLoading: false,
    isLoaded: false,
    data: []
}

const reducer = (state = initialMarketHistoryData, action) => {
    switch(action.type) {
        case REQUEST_MARKET_HISTORY_DATA:
            return Object.assign({}, state, {
                isLoading: true
            });
        case RECIEVE_MARKET_HISTORY_DATA:
            return Object.assign({}, state, {
                data: action.payload,
                isLoading: false,
                isLoaded: true
            });
        default:
            return state;
    }
}

export default reducer;
