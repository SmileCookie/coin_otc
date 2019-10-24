import axios from 'axios';
import { DOMAIN_BASE, DOMAIN_VIP, DOMAIN_TRANS } from '../../conf';

const REQUEST_REPOLIST_DATA = 'btcwinex/markets/REQUEST_REPOLIST_DATA';
const RECIEVE_REPOLIST_DATA = 'btcwinex/markets/RECIEVE_REPOLIST_DATA';

export const requestRepoList = () => {
    return {
        type: REQUEST_REPOLIST_DATA
    }
}
export const recieveRepoList = (data) => {
    
    return {
        type: RECIEVE_REPOLIST_DATA,
        payload: data
    }
}
export const fetchRepoListData = (lastEntrustId,callback) => {
    return dispatch => {
        dispatch(requestRepoList());
        axios.get(DOMAIN_VIP + "/backcapital/getEntrusts?callback=&lastEntrustId="+lastEntrustId)
            .then(res => {
                let data = eval(res["data"]).datas;
                if(data && data.length<=0||data=={}){
                    data = [];
                }
                console.log(data);
                dispatch(recieveRepoList(data));
                if(callback){callback(data)}
            });
    };
}

const initialRepoListData = {
    isLoading: false,
    isLoaded: false,
    data: []
}

const reducer = (state = initialRepoListData, action) => {
    switch(action.type) {
        case REQUEST_REPOLIST_DATA:
            return Object.assign({}, state, {
                isLoading: true
            });
        case RECIEVE_REPOLIST_DATA:
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
