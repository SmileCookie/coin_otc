import axios from 'axios';
const qs = require('qs');
import cookie from 'js-cookie';
import { DEFAULT_LOCALE, COOKIE_UID, COOKIE_IS_LOGGIN, COOKIE_UNAME, COOKIE_LOGIN_STATUS, COOKIE_LAN, COOKIE_IP_AUTH, COOKIE_GOOGLE_AUTH, DOMAIN_BASE, DOMAIN_VIP,DOMAIN_TRANS, URL_IMG_CODE,COOKIE_PREFIX } from '../../conf';
import { TH, Types } from '../../utils';
const HAS_SAFE_PASSWORD = 'btcwinex/trade/HAS_SAFE_PASSWORD';
const GET_USERINFO = 'btcwinex/trade/GET_USERINFO';


export const fetchHasSafePwd = () => {
    return (dispatch, getState) => {
        return axios.post(DOMAIN_VIP + "/manage/isTransSafe")
    }
}
// 限价委托
export const fetchLimitPriceEntrust = (safePwd,coinPrice,coinNumber,isBuy,entrustUrlBase,market) => {
    return (dispatch, getState) => {
        return axios.get(DOMAIN_TRANS + "" + entrustUrlBase + "entrust/doEntrust-" + market+"?safePassword="+safePwd+"&unitPrice="+coinPrice+"&number="+coinNumber+"&isBuy="+isBuy,{withCredentials:true})
    }
}
// 计划委托
export const fetchPlanEntrust = (planData,entrustUrlBase,market) => {
    return (dispatch, getState) => {
        return axios.get(DOMAIN_TRANS + "" + entrustUrlBase + "entrust/doPlanEntrust-" + market,{withCredentials:true,params: planData})
    }
}
//批量委托
export const fetchBatchEntrust = (safePwd,coinPriceMin,coinPriceMax,coinNumber,isBuy,entrustUrlBase,market) => {
    return (dispatch, getState) => {
        return axios.get(DOMAIN_TRANS + "" + entrustUrlBase + "entrust/doEntrustMore-" + market,{
            withCredentials:true,
            params:{
                safePassword: safePwd,
                priceLow: coinPriceMin,
                priceHigh: coinPriceMax,
                numbers: coinNumber,
                isbuy: isBuy
            }
        })
    }
}

// 计划委托
export const fetchUserInfo = () => {
    return (dispatch, getState) => {
        return axios.get(DOMAIN_VIP + "/manage/level/getUserInfo").then((res)=>{
            if(res.data.isSuc){
                try{
                    dispatch(receiveUserInfo(res.data.datas))
                }catch(e){}
            }
        })
    }
}

export const receiveHasSafePwd = (des) =>{
    return {
        type: HAS_SAFE_PASSWORD,
        payload: des
    }
}
export const receiveUserInfo = (des) =>{
    return {
        type: GET_USERINFO,
        payload: des
    }
}

const initialBuySellState = {
    isLoading: false,
    userInfo:null

}

const reducer = (state = initialBuySellState, action) => {
    
    switch(action.type) {
        case HAS_SAFE_PASSWORD:
            return Object.assign({}, state, {
                needSafeWord: action.payload
            });
        case GET_USERINFO:
            return Object.assign({}, state, {
                userInfo: action.payload
            });
        default:
            return state;
    }
}

export default reducer;