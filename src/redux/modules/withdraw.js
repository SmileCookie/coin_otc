import axios from 'axios'
import { DOMAIN_VIP,COIN_KEEP_POINT,PAGEINDEX,PAGESIZEFIVE,DEFAULT_COIN_TYPE } from '../../conf'

const qs = require('qs');
const REQUEST_MANAGE_DOWNLOAD_COIN = 'btcwinex/manage/REQUEST_MANAGE_DOWNLOAD_COIN';
const RECEIVE_MANAGE_DOWNLOAD_COIN = 'btcwinex/manage/RECEIVE_MANAGE_DOWNLOAD_COIN';
const JUMP_MANAGE_DOWNLOAD = 'btcwinex/manage/JUMP_MANAGE_DOWNLOAD';
const CHOOSE_MANAGE_DOWNLOAD_COIN = 'btcwinex/manage/CHOOSE_MANAGE_DOWNLOAD_COIN';
const REQUEST_MANAGE_DOWNLOAD_SMS = 'btcwinex/manage/REQUEST_MANAGE_DOWNLOAD_SMS';
const RECEIVE_MANAGE_DOWNLOAD_SMS = 'btcwinex/manage/RECEIVE_MANAGE_DOWNLOAD_SMS';
const REQUEST_MANAGE_DOWNLOAD_WITHDRAW = 'btcwinex/manage/REQUEST_MANAGE_DOWNLOAD_WITHDRAW';
const RECEIVE_MANAGE_DOWNLOAD_WITHDRAW = 'btcwinex/manage/RECEIVE_MANAGE_DOWNLOAD_WITHDRAW';
const REQUEST_MANAGE_DOWNLOAD_RECORD = 'btcwinex/manage/REQUEST_MANAGE_DOWNLOAD_RECORD';
const RECEIVE_MANAGE_DOWNLOAD_RECORD = 'btcwinex/manage/RECEIVE_MANAGE_DOWNLOAD_RECORD';
const REQUEST_MANAGE_DOWNLOAD_ADDRESS = 'btcwinex/manage/REQUEST_MANAGE_DOWNLOAD_ADDRESS';
const RECEIVE_MANAGE_DOWNLOAD_ADDRESS = 'btcwinex/manage/RECEIVE_MANAGE_DOWNLOAD_ADDRESS';
const MODIFY_DEFAULT_DOWNLOAD_COINTYPE = 'btcwinex/manage/MODIFY_DEFAULT_DOWNLOAD_COINTYPE';
const GET_WITHDRAW_ADDRESS_AUTHEN_TYPE  = 'btcwinex/manage/GET_WITHDRAW_ADDRESS_AUTHEN_TYPE';

//组件 Unmount 时 coin变回 btc
export const modifyCoinType = () => ({
    type:MODIFY_DEFAULT_DOWNLOAD_COINTYPE
})
//获取withdrawAddressAuthenType
export const requestType = (data) => {
    return {
        type:GET_WITHDRAW_ADDRESS_AUTHEN_TYPE,
        payload:{
            data
        }
    }
}

export const getWithdrawAddressAuthenType = () => dispatch => {
    return axios.get(DOMAIN_VIP+"/manage/account/download/indexJson").then(res => {
        const result = res.data
        if(result.isSuc){
             dispatch(requestType(result.datas.withdrawAddressAuthenType))
        }
        return res;
    })
    
}

//修改提币地址备注
// export const fetchModifyAddrss = (data) => dispatch => {
//     return axios.post(DOMAIN_VIP+"/manage/account/download/updateReceiveAddr",qs.stringify({
//                 receiveId:data.id,
//                 memo:data.memo
//            }))
// }
export const fetchModifyAddrss = (data) => dispatch => {
    return axios({
        method: 'post',
        url:DOMAIN_VIP+"/manage/account/download/updateReceiveAddr/"+data.coint,
        data: qs.stringify({
                receiveId:data.id,
                memo:data.memo
            }),
        headers: {
            'Content-type': 'application/x-www-form-urlencoded;charset=UTF-8'
        }
    })
}



//提币历史地址
export const requestAddress = () => ({
     type:REQUEST_MANAGE_DOWNLOAD_ADDRESS
 })

export const receiveAddress = (json) => {
     return {
         type:RECEIVE_MANAGE_DOWNLOAD_ADDRESS,
         payload:{
             data:json
         }
     }
}

export const fetchHisAddress = (pageIndex={},pageSize=5) => (dispatch,getState) => {
    const coint = getState().withdraw.curCoin
    dispatch(requestAddress())
    return axios.post(DOMAIN_VIP+`/manage/account/download/getAddressPage/${coint}`,qs.stringify({
        pageIndex:pageIndex||1,
        pageSize
    })).then(res => {
            dispatch(receiveAddress(res.data.datas))
        })
}

// 接受币种信息
export const requestDownCoin = () => ({
      type:REQUEST_MANAGE_DOWNLOAD_COIN
})

export const receiveDownCoin = (json) => {
    return {
        type:RECEIVE_MANAGE_DOWNLOAD_COIN,
        payload:{
            data:json
        }
    }
}

export const fetchDownCoin = () => (dispatch,getState) => {
    dispatch(requestDownCoin())
    const cointType = getState().withdraw.curCoin
    return axios.post(DOMAIN_VIP+"/manage/account/download/downloadCoinInfo",qs.stringify({
       coint:cointType
    })).then(res => {
        let result = res.data
        if(result.isSuc){
            dispatch(receiveDownCoin(result.datas))
        }
    })
}

// 选择币种
export const chooseDownCoin = (type) => {
    return {
        type:CHOOSE_MANAGE_DOWNLOAD_COIN,
        payload:{
            coint:type
        }
    }
}

// 短信
export const fetchSms = () => (dispatch,getState) => {
    const curCoin = getState().withdraw.curCoin;
    return axios.post(DOMAIN_VIP+`/userSendCode?codeType=8&currency=${curCoin}`)
}

// 提交
export const fetchWithdraw = (values) => (dispatch,getState) => {
    const coint = getState().withdraw.curCoin.toUpperCase();
    return axios({
        method: 'post',
        url:DOMAIN_VIP + `/manage/account/download/doSubmit/${coint}`,
        data:qs.stringify(values),
        headers:{
            'Content-type':'application/x-www-form-urlencoded;charset=UTF-8'
        }
    })
}

//添加地址

export const addWithDrawAddress = (values) => (dispatch,getState) => {
    const coint = getState().withdraw.curCoin.toUpperCase();
    return axios({
        method: 'post',
        url:DOMAIN_VIP+`/manage/account/download/addAddress/${coint}`,
        data:qs.stringify(values),
        headers:{
            'Content-type':'application/x-www-form-urlencoded;charset=UTF-8'
        }
    })
}

//提币记录
export const requestRocord = () => ({
    type:REQUEST_MANAGE_DOWNLOAD_RECORD
})
export const receiveRecord = (json) => {
    return {
        type:RECEIVE_MANAGE_DOWNLOAD_RECORD,
        payload:{
            data:json
        }
    }
}

export const fetchRecord = (conf) => (dispatch,getState)=> {
    dispatch(requestRocord())
    // DOMAIN_VIP+"/manage/account/chargeDownHistory/getDownloadRecordList"
    return axios.post(DOMAIN_VIP+"/manage/account/chargeDownHistory/getDownloadRecordList", qs.stringify({
        pageIndex:conf.pageIndex||PAGEINDEX,
        pageSize:conf.pageSize||PAGESIZEFIVE,
        coint:conf.coint,
        timeTab:conf.timeType,
    })).then(res => {
        let result = res.data
        if(result.isSuc){
            dispatch(receiveRecord(result.datas))
        }
    })
}
//取消提币

export const fetchCancel = (item = {}) => (dispatch,getState) => {
    // console.log(item)
    // // dispatch(requestCancel())
    const curCoin = item.fundstype
    return axios.post(DOMAIN_VIP+`/manage/account/downrecord/confirmCancel?did=${item.did}&coint=${curCoin}`)
}

//资金页面 提现跳转
export const jumpWithdraw = (coint) => {
    return {
        type:JUMP_MANAGE_DOWNLOAD,
        payload:{
            type:coint
        }
    }
}

const reducer = (state ={
    drawList:{
        isloading:false,
        isloaded:false,
        datas:{}
    },
    coinMap:{},
    curCoin:DEFAULT_COIN_TYPE,
    withdrawAddressAuthenType:0,
    hisRecord:{
        isloading:false,
        isloaded:false,
        data:null,
    },
    hisAddress:{
        isloading:false,
        isloaded:false,
        data:null,
    },
}, action) => {
    switch (action.type) {
        case REQUEST_MANAGE_DOWNLOAD_COIN:
            return Object.assign({},state,{
                drawList:Object.assign({},state.drawList,{
                    isloading:true
                })
            });
        case RECEIVE_MANAGE_DOWNLOAD_COIN:
            return Object.assign({},state,{
                drawList:Object.assign({},state.drawList,{
                    isloading:false,
                    isloaded:true,
                    datas:action.payload.data
                }),
                coinMap:Object.keys(action.payload.data.coinMap),
            })
        case CHOOSE_MANAGE_DOWNLOAD_COIN:
              return Object.assign({},state,{
                    curCoin:action.payload.coint
              })
        case JUMP_MANAGE_DOWNLOAD:
              return Object.assign({},state,{
                 curCoin:action.payload.type
              })
        case REQUEST_MANAGE_DOWNLOAD_RECORD: 
              return Object.assign({},state,{
                hisRecord:Object.assign({},state.hisRecord,{
                    isloading:false
                })
              })
        case RECEIVE_MANAGE_DOWNLOAD_RECORD:
              return Object.assign({},state,{
                hisRecord:Object.assign({},state.hisRecord,{
                    isloading:false,
                    isloaded:true,
                    data:action.payload.data
                })
              })
        case REQUEST_MANAGE_DOWNLOAD_ADDRESS:
              return Object.assign({},state,{
                 hisAddress:Object.assign({},state.hisAddress,{
                    isloading:true
                 })
              })
        case RECEIVE_MANAGE_DOWNLOAD_ADDRESS:
              return Object.assign({},state,{
                 hisAddress:Object.assign({},state.hisAddress,{
                    isloading:false,
                    isloaded:true,
                    data:action.payload.data
                 })
              })
        case MODIFY_DEFAULT_DOWNLOAD_COINTYPE:
              return Object.assign({},state,{
                curCoin:DEFAULT_COIN_TYPE
              })
        case GET_WITHDRAW_ADDRESS_AUTHEN_TYPE:
              return Object.assign({},state,{
                withdrawAddressAuthenType:action.payload.data
              })
        default:
            return state;
    }
};



export default reducer















