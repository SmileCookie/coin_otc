import axios from 'axios'
import { DOMAIN_VIP,COIN_KEEP_POINT } from '../../conf'


const REQUEST_COIN_LIST = 'btcwinex/manage/REQUEST_COIN_LIST';
const RECIEVE_COIN_LIST = 'btcwinex/manage/RECIEVE_COIN_LIST';



export const requsetCoinList = () => ({
    type:REQUEST_COIN_LIST
})

export const receiveCoinList = (data) => ({
    type:RECIEVE_COIN_LIST,
    payload:data
}) 

export const fetchCoinList = () => dispatch => {
    dispatch(requsetCoinList())
    return axios.get(DOMAIN_VIP+"/manage/account/chargeDownHistory/getCoins").then(res => {
        const result = res.data
        dispatch(receiveCoinList(result.datas))
    })
}

const initCoinlist = {
    isloading:false,
    isloaded: false,
    data:[]
}


const reducer = (state = initCoinlist, action) => {
    switch (action.type) {
        case REQUEST_COIN_LIST:
            return Object.assign({},state,{
                isloading:true
            })
        case RECIEVE_COIN_LIST:
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












