import axios from 'axios';
import { DOMAIN_BASE, DOMAIN_VIP, URL_IMG_CODE } from '../../conf';
import { TH } from '../../utils'

const FETCH_ASSETS_DETAIL = 'btcwinex/assets/FETCH_ASSETS_DETAIL';
const REQUEST_ASSETS_DETAIL = 'btcwinex/assets/REQUEST_ASSETS_DETAIL';
const RECIEVE_ASSETS_DETAIL = 'btcwinex/assets/RECIEVE_ASSETS_DETAIL';

const FETCH_ASSETS_TOTAL = 'btcwinex/assets/FETCH_ASSETS_TOTAL';
const REQUEST_ASSETS_TOTAL = 'btcwinex/assets/REQUEST_ASSETS_TOTAL';
const RECIEVE_ASSETS_TOTAL = 'btcwinex/assets/RECIEVE_ASSETS_TOTAL';
const RECIEVE_WALLET_TOTAL = 'btcwinex/assets/RECIEVE_WALLET_TOTAL';

export const fetchAssetsDetail = () => {
    return dispatch => {
        dispatch(requestAssetsDetail);
        axios.get(DOMAIN_VIP + "/manage/getAssetsDetail?callback=")
            .then(res => {

                try{
                    const rs = eval(res["data"]);

                    for(let i in rs){
                        TH(rs[i].balance);
                        TH(rs[i].canCharge);
                        TH(rs[i].canWithdraw);
                        TH(rs[i].coinFullNameEn);
                        TH(rs[i].freeze);
                        TH(rs[i].fundsType);
                        TH(rs[i].imgUrl);
                        TH(rs[i].propTag);
                        TH(rs[i].total);
                        TH(rs[i].unitTag);
                        TH(rs[i].usdExchange);
                    }
                    dispatch(recieveAssetsDetail(rs));

                } catch(e){

                }
            });
    }
}

export const requestAssetsDetail = () => {
    return {
        type: REQUEST_ASSETS_DETAIL
    }
}

export const recieveAssetsDetail = (asstes) => {
    return {
        type: RECIEVE_ASSETS_DETAIL,
        payload: asstes
    }
}

export const fetchAssetsTotal = () => (dispatch,getState) => {
    let langs = getState().language.locale;
    // if(langs == "en"){
        langs = "USD";
    // }else{
    //     langs = "CNY";
    // }
    dispatch(requestAssetsDetail);
    return axios.get(DOMAIN_VIP + "/manage/account/getUserTotalAssest?legal_tender="+langs)
           .then(res => {
               try{
              let data = res["data"]["datas"];
              TH(data.legal_tender_unit);
              TH(data.total_btc);
              TH(data.total_legal_tender);
              TH(data.total_usdt);
              dispatch(recieveAssetsTotal(data));
              return data;
               }catch(e){

               }

            });
}
export const fetchrWalletTotal = () => (dispatch,getState) => {
    let langs = getState().language.locale;
    // if(langs == "en"){
        langs = "USD";
    // }else{
    //     langs = "CNY";
    // }
    dispatch(requestAssetsDetail);
    return axios.get(DOMAIN_VIP + "/manage/account/getUserWalletTotalAssest?legal_tender="+langs)
           .then(res => {
               try{
              let data = res["data"]["datas"];
              TH(data.legal_tender_unit);
              TH(data.total_btc);
              TH(data.total_legal_tender);
              TH(data.total_usdt);
              dispatch(recieveWalletTotal(data));
              return data;
               }catch(e){}
            });
}

export const requestAssetsTotal = () => {
    return {
        type: REQUEST_ASSETS_DETAIL
    }
}

export const recieveAssetsTotal = (asstes) => {
    return {
        type: RECIEVE_ASSETS_TOTAL,
        payload: asstes
    }
}
export const recieveWalletTotal = (asstes) => {
    return {
        type: RECIEVE_WALLET_TOTAL,
        payload: asstes
    }
}

const initialAssetsState = {
    detail: {
        isloading: false,
        isloaded:false,
        data:null
    },
    total: null,
    wallet:null
}

const reducer = (state = initialAssetsState, action) => {
    switch(action.type) {
        case REQUEST_ASSETS_DETAIL:
            return Object.assign({},state,{
                detail:Object.assign({},state.detail,{
                    isloading:true
                })
            })
        case RECIEVE_ASSETS_DETAIL:
            return Object.assign({}, state, {
                detail:Object.assign({},state.detail,{
                    isloading:false,
                    isloaded:true,
                    data: action.payload
                })
            });
        case RECIEVE_ASSETS_TOTAL:
            return Object.assign({}, state, {
                total: action.payload
            });
        case RECIEVE_WALLET_TOTAL:
        return Object.assign({}, state, {
                wallet: action.payload
        });
        default:
            return state;
    }
}

export default reducer;
