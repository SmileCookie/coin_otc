import axios from 'axios'
import { TH } from '../../utils'
import { DOMAIN_VIP,COIN_KEEP_POINT } from '../../conf'
const REQUEST_OTC_ACCOUNT = 'btcwinex/manage/REQUEST_OTC_ACCOUNT';
const RECIEVE_OTC_ACCOUNT = 'btcwinex/manage/RECIEVE_OTC_ACCOUNT';
const JUMP_OTC_CHARGE = 'btcwinex/manage/JUMP_OTC_CHARGE';
const JUMP_OTC_DOWNLOAD = 'btcwinex/manage/JUMP_OTC_DOWNLOAD';
const REQUEST_OTC_ACCOUNT_RECORD = 'btcwinex/manage/REQUEST_OTC_ACCOUNT_RECORD';
const REQUEST_OTC_TOTAL = 'btcwinex/manage/REQUEST_OTC_TOTAL';
// const RECIEVE_WALLET_ACCOUNT_RECORD = 'btcwinex/manage/RECIEVE_WALLET_ACCOUNT_RECORD';

export const fetchOtcAssetsTotal = () => (dispatch,getState) => {
    let langs = getState().language.locale;
    // if(langs == "en"){
        langs = "USD";
    // }else{
    //     langs = "CNY";
    // }
    dispatch(requestOtcInfo);
    return axios.get(DOMAIN_VIP + "/manage/account/getUserOtcTotalAssest?legal_tender="+langs)
           .then(res => {
               try{
              let data = res["data"]["datas"];
              TH(data.legal_tender_unit);
              TH(data.total_btc);
              TH(data.total_legal_tender);
              TH(data.total_usdt);
              dispatch(recieveOtcAssetsTotal(data));
              return data;
               }catch(e){

               }
              
            });
}

const recieveOtcAssetsTotal = (asstes) => {
    return {
        type: REQUEST_OTC_TOTAL,
        payload: asstes
    }
}

export const requestOtcInfo = () => ({
    type:REQUEST_OTC_ACCOUNT
})

export const receiveOtcInfo = (record) => {
    return {
        type:RECIEVE_OTC_ACCOUNT,
        payload:{
            record:record
        }
    }
}

export const fetchOtcInfo = () => (dispatch) => {
    dispatch(requestOtcInfo())
    return axios.get(DOMAIN_VIP+"/manage/getAssetsOtcDetail")
                .then(res => {
                    dispatch(receiveOtcInfo(eval(res["data"])))
                })
}

export const requestOtcRecord = () => ({
    type:REQUEST_OTC_ACCOUNT_RECORD
})

// export const receiveOtcRecord = (record) => {
//     return {
//         type:RECIEVE_WALLET_ACCOUNT_RECORD,
//         payload:{
//             manageList:record.list
//         }
//     }
// }

// export const fetchOtcRecord = () => dispatch => {
//      dispatch(requestOtcRecord())
//      return axios.get(DOMAIN_VIP+"/manage/account/billDetail").
//                   then(res => {
//                       dispatch(receiveOtcRecord(res.data.datas))
//                   })
// }

const initialOtcInfo = {
    record:{
        isloading: false,
        isloaded:false,
        data:null
    },
    detail:{
        isloading: false,
        isloaded:false,
        data:null
    },
    total: null,
}

const reducer = (state = initialOtcInfo,action) => {
    switch (action.type){
        case REQUEST_OTC_ACCOUNT:
            return Object.assign({},state,{
                detail:Object.assign({},state.detail,{
                    isloading:true
                })
            })
        case RECIEVE_OTC_ACCOUNT:
            return Object.assign({},state,{
                detail:Object.assign({},state.detail,{
                    isloading:false,
                    isloaded:true,
                    data:action.payload.record
                })
            })
        case REQUEST_OTC_ACCOUNT_RECORD:
             return Object.assign({},state,{
                record:Object.assign({},state.record,{
                    isloading:true
                })
             })
        // case RECIEVE_WALLET_ACCOUNT_RECORD:
        //     return Object.assign({},state,{
        //         record:Object.assign({},state.record,{
        //             isloading:false,
        //             isloaded:true,
        //             data:action.payload.manageList
        //         })
        //     })
        case REQUEST_OTC_TOTAL:
            return Object.assign({}, state, {
                total: action.payload
            });break;
        default:
            return state;
    }
}


export default reducer;

















