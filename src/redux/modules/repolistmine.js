import axios from 'axios';
import { DOMAIN_BASE, DOMAIN_VIP, DOMAIN_TRANS } from '../../conf';

const REQUEST_REPOLISTMINE_DATA = 'btcwinex/markets/REQUEST_REPOLISTMINE_DATA';
const RECIEVE_REPOLISTMINE_DATA = 'btcwinex/markets/RECIEVE_REPOLISTMINE_DATA';

export const requestRepoListMine = () => {
    return {
        type: REQUEST_REPOLISTMINE_DATA
    }
}
export const recieveRepoListMine = (data) => {
    
    return {
        type: RECIEVE_REPOLISTMINE_DATA,
        payload: data
    }
}
export const fetchRepoListMineData = (lastEntrustId,callback) => {
    return dispatch => {
        dispatch(requestRepoListMine());
        axios.get(DOMAIN_VIP + "/backcapital/getEntrustsRelatedMe?callback=&lastEntrustId="+lastEntrustId)
            .then(res => {
                let data = eval(res["data"]).datas;
                if(data && data.length<=0||data=={}){
                    data = [];
                }
                console.log(data);
                dispatch(recieveRepoListMine(data));
                if(callback){callback(data)}
            });
    };
}

const initialRepoListMineData = {
    isLoading: false,
    isLoaded: false,
    data: []
}

const reducer = (state = initialRepoListMineData, action) => {
    switch(action.type) {
        case REQUEST_REPOLISTMINE_DATA:
            return Object.assign({}, state, {
                isLoading: true
            });
        case RECIEVE_REPOLISTMINE_DATA:
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
