import axios from 'axios';
import { DOMAIN_BASE, DOMAIN_VIP, DOMAIN_TRANS } from '../../conf';

const REQUEST_MINE_HISTORY_DATA = 'btcwinex/markets/REQUEST_MINE_HISTORY_DATA';
const RECIEVE_MINE_HISTORY_DATA = 'btcwinex/markets/RECIEVE_MINE_HISTORY_DATA';

export const requestMinehistory = () => {
    return {
        type: REQUEST_MINE_HISTORY_DATA
    }
}
export const recieveMinehistory = (data) => {
    
    return {
        type: RECIEVE_MINE_HISTORY_DATA,
        payload: data
    }
}
export const fetchMineHistoryData = (market) => {
    return dispatch => {
        dispatch(requestMinehistory());
        axios.get(DOMAIN_TRANS + "/Record/traderecord-" + market + "?jsoncallback=&pageIndex=1&dateTo=5",{withCredentials:true})
            .then(res => {
                try{
                let data = eval(res['data'])[0]['record'];
                if(data && data.length<=0||data=={}){
                    data = [];
                }
                // console.log(data);
                dispatch(recieveMinehistory(data));
            }catch(e){}
            });
    };
}

const initialMineHistoryData = {
    isLoading: false,
    isLoaded: false,
    data: []
}

const reducer = (state = initialMineHistoryData, action) => {
    switch(action.type) {
        case REQUEST_MINE_HISTORY_DATA:
            return Object.assign({}, state, {
                isLoading: true
            });
        case RECIEVE_MINE_HISTORY_DATA:
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
