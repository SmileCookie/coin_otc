import axios from 'axios'
import { DOMAIN_VIP,COIN_KEEP_POINT } from '../../conf'
import { TH } from '../../utils/'
const REQUEST_WALLET_ACCOUNT = 'btcwinex/manage/REQUEST_WALLET_ACCOUNT';
const RECIEVE_WALLET_ACCOUNT = 'btcwinex/manage/RECIEVE_WALLET_ACCOUNT';
const JUMP_WALLET_CHARGE = 'btcwinex/manage/JUMP_WALLET_CHARGE';
const JUMP_WALLET_DOWNLOAD = 'btcwinex/manage/JUMP_WALLET_DOWNLOAD';
const REQUEST_WALLET_ACCOUNT_RECORD = 'btcwinex/manage/REQUEST_WALLET_ACCOUNT_RECORD';
// const RECIEVE_WALLET_ACCOUNT_RECORD = 'btcwinex/manage/RECIEVE_WALLET_ACCOUNT_RECORD';

export const requestWalletInfo = () => ({
    type:REQUEST_WALLET_ACCOUNT
})

export const receiveWalletInfo = (record) => {
    return {
        type:RECIEVE_WALLET_ACCOUNT,
        payload:{
            record:record
        }
    }
}



export const fetchWalletInfo = () => (dispatch) => {
    dispatch(requestWalletInfo())
    return axios.get(DOMAIN_VIP+"/manage/getWalletDetail")
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
                        dispatch(receiveWalletInfo(rs))
                    } catch(e){

                    }
                })
}

export const requestWalletRecord = () => ({
    type:REQUEST_WALLET_ACCOUNT_RECORD
})

// export const receiveWalletRecord = (record) => {
//     return {
//         type:RECIEVE_WALLET_ACCOUNT_RECORD,
//         payload:{
//             manageList:record.list
//         }
//     }
// }

// export const fetchWalletRecord = () => dispatch => {
//      dispatch(requestWalletRecord())
//      return axios.get(DOMAIN_VIP+"/manage/account/billDetail").
//                   then(res => {
//                       dispatch(receiveWalletRecord(res.data.datas))
//                   })
// }

const initialWalletInfo = {
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
}

const reducer = (state = initialWalletInfo,action) => {
    switch (action.type){
        case REQUEST_WALLET_ACCOUNT:
            return Object.assign({},state,{
                detail:Object.assign({},state.detail,{
                    isloading:true
                })
            })
        case RECIEVE_WALLET_ACCOUNT:
            return Object.assign({},state,{
                detail:Object.assign({},state.detail,{
                    isloading:false,
                    isloaded:true,
                    data:action.payload.record
                })
            })
        case REQUEST_WALLET_ACCOUNT_RECORD:
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
        default:
            return state;
    }
}


export default reducer;

















