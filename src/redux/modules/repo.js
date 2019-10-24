import axios from 'axios';
import { DOMAIN_VIP,DOMAIN_TRANS} from '../../conf'

const REQUEST_REPO_DATA = 'btcwinex/REQUEST_REPO_DATA';
const SET_REPO_DATA = 'btcwinex/SET_REPO_DATA';
export const fetchCountDown = (id,callback) => {
    return dispatch => {
        dispatch(requestRrpoData());
        let lastEntrustId = id?id:0;
        axios.get(DOMAIN_VIP + "/backcapital/countDown?lastEntrustId="+lastEntrustId+"&&callback=")
            .then(res => {
                let data = eval(res).data.datas;
                dispatch(recieveRrpoData(data));
                if(callback){callback(data)}
            });
    };
}
export const requestRrpoData = () => {
    return {
        type: REQUEST_REPO_DATA
    }
}
export const recieveRrpoData = (data) => {
    return {
        type: SET_REPO_DATA,
        payload: data
    }
}
const initialRepoState = {
    isLoading: false,
    isLoaded: false,
    repoData: null
}
const reducer = (state = initialRepoState, action) => {
    switch(action.type) {
        case REQUEST_REPO_DATA:
            return Object.assign({}, state, {
                isLoading: true,
                // isLoaded: false,
            });
        case SET_REPO_DATA:
            return Object.assign({}, state, {
                isLoading: false,
                isLoaded: true,
                repoData: action.payload
            });
        default:
            return state;
    }
}

export default reducer;