import axios from 'axios'
import {DOMAIN_VIP, COIN_KEEP_POINT, OTC,OTC_UIR} from '../../conf'
import { TH } from '../../utils'
const REQUEST_MANAGE_ACCOUNT = 'btcwinex/manage/REQUEST_MANAGE_ACCOUNT';
const RECIEVE_MANAGE_ACCOUNT = 'btcwinex/manage/RECIEVE_MANAGE_ACCOUNT';
const JUMP_MANAGE_CHARGE = 'btcwinex/manage/JUMP_MANAGE_CHARGE';
const JUMP_MANAGE_DOWNLOAD = 'btcwinex/manage/JUMP_MANAGE_DOWNLOAD';
const REQUEST_MANAGE_ACCOUNT_RECORD = 'btcwinex/manage/REQUEST_MANAGE_ACCOUNT_RECORD';
const RECIEVE_MANAGE_ACCOUNT_RECORD = 'btcwinex/manage/RECIEVE_MANAGE_ACCOUNT_RECORD';
const ALI_PAY_SETTTING = 'btcwinex/manage/ALI_PAY_SETTTING';
const CARD_PAY_SETTTING = 'btcwinex/manage/CARD_PAY_SETTTING';
const USER_PARY_INFO = 'btcwinex/manage/USER_PARY_INFO'
const UESR_HOME_PAGE = 'btcwinex/manage/BLACK_LIST'
const BLACK_LIST = 'btcwinex/manage/BLACK_LIST'
const ADD_BLACK_LIST = 'btcwinex/manage/ADD_BLACK_LIST'
const REMOVE_BALCK_LIST = 'btcwinex/manage/REMOVE_BALCK_LIST'
const CHECKUSERVIP = "shangjia/CHECKUSERVIP"
const GOTOP = 'btcwinex/goTop'


// otc lk 黑名单列表
export const receiveAddBlackList = (json) => {
    return {
        type:ADD_BLACK_LIST,
        payload: json
    }
}
//获取商家认证信息的状态

const checkVip = (data) =>{
    return {
        type:CHECKUSERVIP,
        payload: data
    }
}

export const checkVipEvent = () => (dispatch) =>{
    return axios.get(DOMAIN_VIP + '/manage/auth/authenticationJson').then(res =>{
            if(res.status == 200){
                let data = res.data.datas
                dispatch(checkVip(data))
        }

    })
}


export const fetchAddBlackList= (id) => (dispatch) => {
    // dispatch(requestManageInfo())
    return axios.get(OTC_UIR+OTC +"/web/common/getAvgPassTime",)
        .then(res => {
            console.log(res.data)
            if (res.data.code == 200 ){
                dispatch(receiveAddBlackList(res.data.data))
            }
        })
}






// otc 个人主页
export const receiveUesrHomePage = (json) => {
    return {
        type:UESR_HOME_PAGE,
        payload: json
    }
}


export const fetchUesrHomePage= (id) => (dispatch) => {
    // dispatch(requestManageInfo())
    let data = new FormData(),
        targetId = parseInt(id);

    data.append('targetUserId',targetId)
    return axios.post(OTC_UIR+OTC +"/web/common/getAvgPassTime",data)
        .then(res => {
            console.log(res.data)
            if (res.data.code == 200 && !!res.data.data.cardName){
                dispatch(receiveUesrHomePage(res.data.data))
            }
        })
}

// otc 黑名单列表
export const receiveBlackList = (json) => {
    return {
        type:BLACK_LIST,
        payload: json
    }
}


export const fetchBlackList= () => (dispatch) => {
    // dispatch(requestManageInfo())
    return axios.post(OTC_UIR + OTC + "/web/blacklist/query",)
        .then(res => {
            console.log(res.data)
            if (res.data.code == 200){
                dispatch(receiveBlackList(res.data.data))
            }
        })
}

// otc 支付设置 信息获取


export const receiveUserPayInfo = (json) => {
    return {
        type:USER_PARY_INFO,
        payload: json
    }
}

// 获取otc 支付设置信息
export const fetchUserPayInfo= () => (dispatch) => {
    // dispatch(requestManageInfo())
    return axios.get(OTC_UIR+OTC +"/api/v1/user/info",)
        .then(res => {
            console.log(res.data)
            if (res.data.code == 200 && !!res.data.data.cardName){
                dispatch(receiveUserPayInfo(res.data.data))
            }
        })
}


export const receiveAliPaySetting = (json) => {
    return {
        type:ALI_PAY_SETTTING,
        payload: json
    }
}

export const receiveCardPaySetting = (json) => {
    return {
        type:CARD_PAY_SETTTING,
        payload: json
    }
}

// otc 支付设置 card ali 信息 type =1 ali ,type =2 card
export const fetchPaySetting = (type = '') => (dispatch) => {
    // dispatch(requestManageInfo())
    return axios.get(OTC_UIR+ OTC +"/web/payment/PaymentDetails",{params:{type:type}})
        .then(res => {
            console.log(res.data)
            if (res.data.code == 200 && !!res.data.data){
                if(type == 1){
                    dispatch(receiveAliPaySetting(res.data.data));
                }else {
                    dispatch(receiveCardPaySetting(res.data.data));
                }
            }
        })
}


export const requestManageInfo = () => ({
    type:REQUEST_MANAGE_ACCOUNT
})

export const receiveManageInfo = (record) => {
    return {
        type:RECIEVE_MANAGE_ACCOUNT,
        payload:{
            record:record
        }
    }
}

export const fetchManageInfo = (cb =() =>{}) => (dispatch) => {
    dispatch(requestManageInfo())
    return axios.get(DOMAIN_VIP+"/manage/getAssetsDetail")
                .then(res => {
                    const rs = eval(res["data"]);
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
                            //throw new Error("参数必须是数字")
                        }
                        dispatch(receiveManageInfo(rs));
                        cb();
                    } catch(e){

                    }
                })
}

export const requestManageRecord = () => ({
    type:REQUEST_MANAGE_ACCOUNT_RECORD
})

export const receiveManageRecord = (record) => {
    return {
        type:RECIEVE_MANAGE_ACCOUNT_RECORD,
        payload:{
            manageList:record.list
        }
    }
}

export const fetchManageRecord = () => dispatch => {
     dispatch(requestManageRecord())
     return axios.get(DOMAIN_VIP+"/manage/account/billDetail").
                  then(res => {

                      dispatch(receiveManageRecord(res.data.datas))
                  })
}

//全局scroll跳到顶部位置
export const goTops = (data = 0) =>{
    return {
        type: GOTOP,
        payload: data,
    }
}

//计算点击量和距离
// export const goTopsClick = (num = 0) => (dispatch) =>{
//     dispatch(goTops(num))
// }

const initialManageInfo = {
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
    aliPay:{
        accountName:'',
        accountNumber:'',
        enable:'',
        id:'',
        paymentType:'',
        bankOpeningBranch:'',
        bankOpeningBank:'',
        qrcodeUrl:'',
        userId:'',
    },
    cardPay:{
        accountName:'',
        accountNumber:'',
        enable:'',
        id:'',
        paymentType:'',
        bankOpeningBranch:'',
        bankOpeningBank:'',
        qrcodeUrl:'',
        userId:'',
    },
    userPayInfo:{
        cardName:''
    },
    blackList:[],
    homePage:{
        avgPassTime:'',
        blackListFlg:'',
        cardStatus:'',
        sumSecond:'',
        tradeVolume:'',
        nickname:'',
        firstRegTime:'',
        firstVisit:'',
        stageVolume:'',
        stageVolumeRate:'',
        stageDate:''
    },
    checkVipObj:{
        storeStatus:null,
        storeType:1,
        loading:true
    },
    goTopCount:0,
    goTopNum:0,
}

const reducer = (state = initialManageInfo,action) => {
    switch (action.type){
        case REQUEST_MANAGE_ACCOUNT:
            return Object.assign({},state,{
                detail:Object.assign({},state.detail,{
                    isloading:true
                })
            })
        case RECIEVE_MANAGE_ACCOUNT:
            return Object.assign({},state,{
                detail:Object.assign({},state.detail,{
                    isloading:false,
                    isloaded:true,
                    data:action.payload.record
                })
            })
        case REQUEST_MANAGE_ACCOUNT_RECORD:
             return Object.assign({},state,{
                record:Object.assign({},state.record,{
                    isloading:true
                })
             })
        case RECIEVE_MANAGE_ACCOUNT_RECORD:
            return Object.assign({},state,{
                record:Object.assign({},state.record,{
                    isloading:false,
                    isloaded:true,
                    data:action.payload.manageList
                })
            })
        case ALI_PAY_SETTTING:
            return Object.assign({},state,{
                aliPay:Object.assign({},state.aliPay,{
                    ...action.payload
                })
            })
        case CARD_PAY_SETTTING:
            return Object.assign({},state,{
                cardPay:Object.assign({},state.cardPay,{
                    ...action.payload
                })
            })
        case USER_PARY_INFO:
            return Object.assign({},state,{
                userPayInfo:Object.assign({},state.userPayInfo,{
                    ...action.payload
                })
            })
        case BLACK_LIST:
            // console.log("bl ====" + action.payload)
            return Object.assign({},state,{
                blackList:[...action.payload]
            })
        case UESR_HOME_PAGE:
            return Object.assign({},state,{
                homePage:Object.assign({},state.homePage,{
                    ...action.payload
                })
            })
        case CHECKUSERVIP:
            return Object.assign({},state,{
                checkVipObj : Object.assign({},state.checkVipObj,{
                    ...action.payload,
                    loading:false
                })
            })
        case GOTOP:
                return Object.assign({},state,{
                    goTopCount:state.goTopCount + 1,
                    goTopNum:action.payload
                })
        default:
            return state;
    }
}


export default reducer;

















