import axios from 'axios'
import qs from 'qs'
import { DOMAIN_VIP,COIN_KEEP_POINT,DEFAULT_COIN_TYPE,PAGEINDEX,PAGESIZEFIVE } from '../../conf'
import { TH, Types } from '../../utils'
const SET_COINLIST_CURRENTCOIN = 'btcwinex/manage/set_coinlist_currentcoin'
const REQUEST_MANAGE_DEPOSIT = 'btcwinex/manage/REQUEST_MANAGE_DEPOSIT';
const RECIEVE_MANAGE_DEPOSIT = 'btcwinex/manage/RECIEVE_MANAGE_DEPOSIT';
const RECIEVE_MANAGE_USDTLIST = 'btcwinex/manage/RECIEVE_MANAGE_USDTLIST';
const REQUEST_MANAGE_DEPOSIT_RECORD = 'btcwinex/manage/REQUEST_MANAGE_DEPOSIT_RECORD';
const RECIEVE_MANAGE_DEPOSIT_RECORD = 'btcwinex/manage/RECIEVE_MANAGE_DEPOSIT_RECORD';
const MODIFY_MANAGE_DEPOSIT_COINTYPE = 'btcwinex/manage/MODIFY_MANAGE_DEPOSIT_COINTYPE';

//当 Unmount 时 ，充值默认的 cointype
export const modifyDepositCoin = () => ({
    type:MODIFY_MANAGE_DEPOSIT_COINTYPE
})


export const requestManageCoinInfo = () => ({
    type:REQUEST_MANAGE_DEPOSIT
})
export const setCoinListCurrentCoin = (coin) => {
    return {
        type:SET_COINLIST_CURRENTCOIN,
        payload:{
            coin:coin
        }
    }
}
export const receiveManageCoinInfo = (list,noPhoneNoGoogle) => {
    return {
        type:RECIEVE_MANAGE_DEPOSIT,
        payload:{
            record: list,
            noPhoneNoGoogle: noPhoneNoGoogle
        }
    }
}

export const receiveUsdtList = (list) => {
    return {
        type:RECIEVE_MANAGE_USDTLIST,
        payload:{
            record: list
        }
    }
}


export const fetchManageCoinInfo = () => dispatch => {
    dispatch(requestManageCoinInfo())
    return axios.get(DOMAIN_VIP + "/manage/account/charge/rechargecoininfo")
        .then(res => {
            try{

            const {datas:{list:lists},datas, des, isSuc,datas:{usdtlist:usdtlist}} = res.data;
            TH(datas);
            TH(des);
            TH(isSuc);
            TH(lists);

            if(Types.isArray(lists)){
                for(const item of lists){
                    TH(item.address);
                    TH(item.addressTag);
                    TH(item.coinName);
                    TH(item.confirmTimes);
                }
            }
            var list = []
            if(!res["data"]['isSuc']){
                return dispatch(receiveManageCoinInfo(list,false))
            }

            if(res["data"]['datas'] && res["data"]['datas']['list'].length > 0){
                list = res["data"]['datas']['list']
            }

            if(res['data']['datas']['noPhoneNoGoogle']){
                return dispatch(receiveManageCoinInfo(list,true))
            }else{
                return dispatch(receiveManageCoinInfo(list,false))
            }
        }catch(e){}
        })
}


export const requestManageCoinRecord = () => ({
    type:REQUEST_MANAGE_DEPOSIT_RECORD
})

export const receiveManageCoinRecord = (record) => {
    return {
        type:RECIEVE_MANAGE_DEPOSIT_RECORD,
        payload:{
            list:record.list,
            totalCount:record.totalCount
        }
    }
}

export const fetchManageCoinRecord = (conf) => dispatch => {
    //  dispatch(requestManageCoinRecord())
    // DOMAIN_VIP + "/manage/account/chargeDownHistory/getChargeRecordList"
    return axios.post(DOMAIN_VIP + "/manage/account/ChargeDownHistory/getChargeRecordList",qs.stringify({
        pageIndex:conf.pageIndex||PAGEINDEX,
        pageSize:conf.pageSize||PAGESIZEFIVE,
        coint:conf.coint,
        timeTab:conf.timeType,
    })).then(res => {
        // if(res.data.isSuc){
            dispatch(receiveManageCoinRecord(res.data.datas))

        // }
    })
}



const initialManageCoinInfo = {
    record:{
        isloading:false,
        isloaded: false,
        data:{
            list:[],
            totalCount:0
        }
    },
    coinList:{
        isloading:false,
        isloaded: false,
        noPhoneNoGoogle:false,
        data:[]
    },
    usdtList:[],
    currentCoin: DEFAULT_COIN_TYPE
}

const reducer = (state = initialManageCoinInfo,action) => {
    switch (action.type){
        case REQUEST_MANAGE_DEPOSIT:
            return Object.assign({},state,{
                coinList:Object.assign({},state.coinList,{
                    isloading:true
                })
            })
        case RECIEVE_MANAGE_DEPOSIT:
            return Object.assign({},state,{
                coinList:{
                    isloading: false,
                    isloaded:true,
                    noPhoneNoGoogle: action.payload.noPhoneNoGoogle,
                    data: action.payload.record
                }
            })
        case REQUEST_MANAGE_DEPOSIT_RECORD:
             return Object.assign({},state,{
                record:Object.assign({},state.record,{
                    isloading:true
                })

             })
        case RECIEVE_MANAGE_DEPOSIT_RECORD:
            return Object.assign({},state,{
                record:Object.assign({},state.record,{
                    isloading:false,
                    isloaded:true,
                    data:action.payload
                })
            })
        case SET_COINLIST_CURRENTCOIN:
            return Object.assign({},state,{
                currentCoin:action.payload.coin
            })
        case MODIFY_MANAGE_DEPOSIT_COINTYPE:
            return Object.assign({},state,{
                currentCoin:DEFAULT_COIN_TYPE
            })
        default:
             return state;
    }
}


export default reducer;

















