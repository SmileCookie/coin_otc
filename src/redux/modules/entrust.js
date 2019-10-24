import axios from 'axios'
import qs from 'qs'
import { DOMAIN_TRANS,DOMAIN_VIP,PAGEINDEX,PAGESIZETHIRTY } from '../../conf'

const REQUEST_MARKET_LIST = 'btcwinex/entrust/REQUEST_MARKET_LIST'
const RECEIVE_MARKET_LIST = 'btcwinex/entrust/RECEIVE_MARKET_LIST'
const REQUEST_ENTRUST_LIST = 'btcwinex/entrust/REQUEST_ENTRUST_LIST'
const RECEIVE_ENTRUST_LIST = 'btcwinex/entrust/RECEIVE_ENTRUST_LIST'
const SET_ENTRUST_LIST_COIN = 'btcwinex/entrust/SET_ENTRUST_LIST_COIN'
const SET_ENTRUST_TRANS_COIN = 'btcwinex/entrust/SET_ENTRUST_TRANS_COIN'


export const setEntrustListCoin = (coin) => ({
    type:SET_ENTRUST_LIST_COIN,
    payload:coin
})

export const setEntrustTransCoin = (coin) => ({
    type:SET_ENTRUST_TRANS_COIN,
    payload:coin
})

export const requestMarketList = () => ({
    type:REQUEST_MARKET_LIST
})

export const receiveMarketList = (data) => ({
    type:RECEIVE_MARKET_LIST,
    payload:data
})

export const fetchMarketList = () => dispatch => {
    dispatch(requestMarketList())
    return axios.get(DOMAIN_TRANS+"/getMarketRelate").then(res => {
        const result = res.data
        dispatch(receiveMarketList(result.datas))
    })
}

export const requestEntrustList = () => ({
    type:REQUEST_ENTRUST_LIST
})

export const receiveEntrustList = (data) => ({
    type:RECEIVE_ENTRUST_LIST,
    payload:data
})

export const fetchEntrustList = (market,timeType,type,pageNum,lastTime) => dispatch =>  {
    dispatch(requestEntrustList())
    return axios.get(DOMAIN_TRANS+"/Record/getTransRecordHistory?jsoncallback=&"+"market="+market+"&type="+type+"&timeType="+timeType+"&pageNum="+pageNum+"&_="+lastTime,{withCredentials:true})
    .then(res => {
        try{
        let data = eval(res['data'])[0];
        dispatch(receiveEntrustList(data));
        }catch(e){}
        
    })
}


const initState = {
    coin:{
        isloading:false,
        isloaded:false,
        data:[]
    },
    transRecord:{
        isloading:false,
        isloaded:false,
        data:[] 
    },
    listCoin:'',
    transCoin:''
}

const reducer = (state = initState , action) => {
    switch (action.type) {
        case REQUEST_ENTRUST_LIST:
            return Object.assign({},state,{
                transRecord:Object.assign({},state.transRecord,{
                    isloading:true
                })
            })
        case RECEIVE_ENTRUST_LIST:
            return Object.assign({},state,{
                transRecord:Object.assign({},state.transRecord,{
                    isloading:false,
                    isloaded:true,
                    data:action.payload
                })
            })
        case REQUEST_MARKET_LIST:
            return Object.assign({},state,{
                coin:Object.assign({},state.coin,{
                    isloading:true
                })
            })
        case RECEIVE_MARKET_LIST:
            return Object.assign({},state,{
                coin:Object.assign({},state.coin,{
                    isloading:false,
                    isloaded:true,
                    data:action.payload
                })
            })
        case SET_ENTRUST_LIST_COIN:
            return Object.assign({},state,{
                listCoin:action.payload
            })
        case SET_ENTRUST_TRANS_COIN:
            return Object.assign({},state,{
                transCoin:action.payload
            })
        default:
            return state
    }
}

export default reducer
















