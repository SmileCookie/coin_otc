import axios from 'axios';
import { DOMAIN_BASE, DOMAIN_VIP, DOMAIN_TRANS } from '../../conf';

const REQUEST_SUMMARY_DATA = 'btcwinex/marketsconf/REQUEST_SUMMARY_DATA';
const RECIEVE_SUMMARY_DATA = 'btcwinex/marketsconf/RECIEVE_SUMMARY_DATA';

export const fetchSummaryData = (market,money) => {
    return dispatch => {
        dispatch(requestSummaryData);
        axios.get(DOMAIN_TRANS + "/entrust/transactionSummary?callback=&currency=" + market+"&legalTender="+money,{withCredentials:true})
            .then(res => {
                let data = res["data"]["datas"];
                dispatch(recieveSummaryData(data));
            });
    }
}
export const summaryRecount = (market) => {
    return dispatch => {
        return axios.get(DOMAIN_TRANS + "/entrust/transactionSummaryResum?callback=?&currency=" + market,{withCredentials:true});
    }
}
export const requestSummaryData = () => {
    return {
        type: REQUEST_SUMMARY_DATA
    }
}

export const recieveSummaryData = (data) => {
    return {
        type: RECIEVE_SUMMARY_DATA,
        payload: data
    }
}

const initialSummaryDataState = {
    isLoading: false,
    isLoaded: false,
    data: null
}

const reducer = (state = initialSummaryDataState, action) => {
    switch(action.type) {
        case REQUEST_SUMMARY_DATA:
            return Object.assign({}, state, {
                isLoading: true
            });
        case RECIEVE_SUMMARY_DATA:
            return Object.assign({}, state, {
                isLoading: false,
                isLoaded: true,
                data: action.payload
            });
        default:
            return state;
    }
}

export default reducer;