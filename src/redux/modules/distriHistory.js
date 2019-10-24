import axios from 'axios'
import qs from 'qs'
import { DOMAIN_VIP,COIN_KEEP_POINT,PAGESIZETHIRTY,PAGEINDEX } from '../../conf'


const REQUEST_DISTRI_HISTORY = 'btcwinex/manage/REQUEST_DISTRI_HISTORY';
const RECIEVE_DISTRI_HISTORY = 'btcwinex/manage/RECIEVE_DISTRI_HISTORY';


export const requestDistriHistory = () => ({
    type:REQUEST_DISTRI_HISTORY
})

export const receiveDistriHistory = (data) => ({
    type:RECIEVE_DISTRI_HISTORY,
    payload:data
})

export const fetchDistriHistory = (conf) => dispatch => {
    dispatch(requestDistriHistory())
    return axios.post(DOMAIN_VIP+"/manage/queryUserDistribution",qs.stringify({
        pageIndex:conf.pageIndex,
        pageSize:conf.pageSize,
        type:conf.type
    })).then(res => {
        const result = res.data
        if(result.isSuc){
            dispatch(receiveDistriHistory(result.datas))
        }
    })
}


const initState = {
    isloading:false,
    isloaded:false,
    data:[]
}

const reducer = ( state=initState , action ) => {
    switch (action.type){
        case REQUEST_DISTRI_HISTORY:
            return Object.assign({},state,{
                isloading:true
            })
        case RECIEVE_DISTRI_HISTORY:
            return Object.assign({},state,{
                isloading:false,
                isloaded:true,
                data:action.payload
            })
        default:
            return state
    }
}

export default reducer



















