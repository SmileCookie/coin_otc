import axios from 'axios'
import { TH } from '../../utils'
import { DOMAIN_VIP,COIN_KEEP_POINT } from '../../conf'
const REQUEST_TRANS_ACCOUNT = 'btcwinex/manage/REQUEST_TRANS_ACCOUNT';
const RECIEVE_TRANS_ACCOUNT = 'btcwinex/manage/RECIEVE_TRANS_ACCOUNT';
const JUMP_TRANS_CHARGE = 'btcwinex/manage/JUMP_TRANS_CHARGE';
const JUMP_TRANS_DOWNLOAD = 'btcwinex/manage/JUMP_TRANS_DOWNLOAD';
const REQUEST_TRANS_ACCOUNT_RECORD = 'btcwinex/manage/REQUEST_TRANS_ACCOUNT_RECORD';
const REQUEST_TRANS_TOTAL = 'btcwinex/manage/REQUEST_TRANS_TOTAL';
// const RECIEVE_WALLET_ACCOUNT_RECORD = 'btcwinex/manage/RECIEVE_WALLET_ACCOUNT_RECORD';

export const fetchTransAssetsTotal = () => (dispatch,getState) => {
    let langs = getState().language.locale;
    // if(langs == "en"){
        langs = "USD";
    // }else{
    //     langs = "CNY";
    // }
    dispatch(requestTransInfo);
    return axios.get(DOMAIN_VIP + "/manage/account/getUserFinancialTotalAssest?legal_tender="+langs)
           .then(res => {
               try{
              let data = res["data"]["datas"];
              TH(data.legal_tender_unit);
              TH(data.total_btc);
              TH(data.total_legal_tender);
              TH(data.total_usdt);
              dispatch(recieveTransAssetsTotal(data));
              return data;
               }catch(e){

               }
              
            });
}

const recieveTransAssetsTotal = (asstes) => {
    return {
        type: REQUEST_TRANS_TOTAL,
        payload: asstes
    }
}

export const requestTransInfo = () => ({
    type:REQUEST_TRANS_ACCOUNT
})

export const receiveTransInfo = (record) => {
    return {
        type:RECIEVE_TRANS_ACCOUNT,
        payload:{
            record:record
        }
    }
}

export const fetchTransInfo = () => (dispatch) => {
    dispatch(requestTransInfo())
    return axios.get(DOMAIN_VIP+"/manage/getFinancialDetail")
                .then(res => {
                    dispatch(receiveTransInfo(eval(res["data"])))
                })
}

export const requestTransRecord = () => ({
    type:REQUEST_TRANS_ACCOUNT_RECORD
})

// export const receiveTransRecord = (record) => {
//     return {
//         type:RECIEVE_WALLET_ACCOUNT_RECORD,
//         payload:{
//             manageList:record.list
//         }
//     }
// }

// export const fetchTransRecord = () => dispatch => {
//      dispatch(requestTransRecord())
//      return axios.get(DOMAIN_VIP+"/manage/account/billDetail").
//                   then(res => {
//                       dispatch(receiveTransRecord(res.data.datas))
//                   })
// }

const initialTransInfo = {
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

const reducer = (state = initialTransInfo,action) => {
    switch (action.type){
        case REQUEST_TRANS_ACCOUNT:
            return Object.assign({},state,{
                detail:Object.assign({},state.detail,{
                    isloading:true
                })
            })
        case RECIEVE_TRANS_ACCOUNT:
            return Object.assign({},state,{
                detail:Object.assign({},state.detail,{
                    isloading:false,
                    isloaded:true,
                    data:action.payload.record
                })
            })
        case REQUEST_TRANS_ACCOUNT_RECORD:
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
        case REQUEST_TRANS_TOTAL:
            return Object.assign({}, state, {
                total: action.payload
            });break;
        default:
            return state;
    }
}


export default reducer;

















