import axios from 'axios';
import { DOMAIN_BASE, DOMAIN_VIP, DOMAIN_TRANS,PERCENTAGE } from '../../conf';
import cookie from 'js-cookie';
import qs from 'qs'
const BigNumber = require('big.js'); 


const MONDIFY_TRADE_BG = 'btcwinex/MONDIFY_TRADE_BG';
const HIDE_FOOTER = 'btcwinex/HIDE_FOOTER';
const RECEIVE_CURRENT_TRADE_VOLUME = 'btcwinex/CURRENT_TRADE_VOLUME'
const RECORD_COIN_DETAIL = 'btcwinex/RECORD_COIN_DETAIL'
const RECEIVE_ONLINE_PERSION = 'btcwinex/RECEIVE_ONLINE_PERSION'
const REQUEST_MULTI_TRADE = 'btcwinex/REQUEST_MULTI_TRADE'


export const modifyTradeBg = (type) => ({
    type:MONDIFY_TRADE_BG,
    payload:{
        skin:type
    }
})

export const modifyFoot = (type) => {
    return {
        type:HIDE_FOOTER,
        payload:{
            footState:type
        }
    }
}

export const fetchCoinDetail = (coin,lang) => dispatch => {
    return axios.post(DOMAIN_VIP+"/coin/getCoinInfo",qs.stringify({
        internationalization:lang,
        coinName:coin
    })).then(res => {
        const result = res.data
        if(result.isSuc){
            dispatch(receiveDetail(result.datas))
        }
    })
}

const receiveDetail = (data) => ({
    type:RECORD_COIN_DETAIL,
    payload:{data}
})

const receiveCurVOl = (data) => {
    return {
        type:RECEIVE_CURRENT_TRADE_VOLUME,
        payload:{
            data
        }
    }
}
//当前交易量
export const fetchTotalTrade = () => dispatch => {
    return axios.get(DOMAIN_VIP+"/chart/getTradingVolume").then((res) => {
        const result = res.data;
        dispatch(receiveCurVOl(result.datas))
    })
}
const receiveOnline = (data) => ({
    type:RECEIVE_ONLINE_PERSION,
    payload:{data}
})
//在线人数
export const fetchOnlinePer = () => dispatch => {
    return axios.get(DOMAIN_VIP+"/report/queryUserOnline/",{'headers': {'Cache-Control': 'no-cache'}}).then(res => {
        const result = res.data
        if(result.isSuc&&result.datas){
            dispatch(receiveOnline(result.datas.initial))
        }
    })
}
//多屏看板 1.查询 2.新增 3.删除 4.替换
export const fetchMultiTrade = ({multiScreen,type=1,multiScreenOld,group}={}) => dispatch => {
    return axios.post(DOMAIN_VIP+"/manage/userscreen/operatingScreens",qs.stringify({
        operationType:type,
        multiScreen,
        groupByScreen:group,
        multiScreenOld
    })).then(res => {
        try{
        const result = res.data
        if(result.isSuc&&type==1){
            dispatch(receiveMutili(result.datas.reverse()))
        }}catch(e){}
    })
}
export const receiveMutili = (data) => ({
    type:REQUEST_MULTI_TRADE,
    payload:{
        data
    }
})

const initialState = {
    skin:cookie.get("skin")||'dark',
    onlineUser:0,
    tradeNum:0,
    footStau:true,
    currMarket:'',
    volume:0,
    coinIntro:null,
    online:0,
    mutli:""
}

const reducer = ( state = initialState, action ) => {
    switch(action.type) {
        case MONDIFY_TRADE_BG:
            return Object.assign({},state,{
                skin:action.payload.skin
            })
        case HIDE_FOOTER:
            return Object.assign({},state,{
                footStau:action.payload.footState
            })
        case RECEIVE_CURRENT_TRADE_VOLUME:
            return Object.assign({},state,{
                volume:action.payload.data
            })
        case RECORD_COIN_DETAIL:
            return Object.assign({},state,{
                coinIntro:action.payload.data
            })
        case RECEIVE_ONLINE_PERSION:
            return Object.assign({},state,{
                online:action.payload.data
            })
        case REQUEST_MULTI_TRADE:
            return Object.assign({},state,{
                mutli:action.payload.data
            })
        default:
            return state;
    }
}

export default reducer
















