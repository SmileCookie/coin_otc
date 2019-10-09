import {get,post} from 'nets';
import axios from 'axios'
import conf from '../../conf'
const USER = 'USER';
const PAGENUM = "PAGENUM"
const COINMARK = 'COINMARK'
const RONGTOKEN = 'RONGTOKEN'
const CHECKUSERVIP = "shangjia/CHECKUSERVIP"
const SOKETEVENT = 'tips/SOKETEVENT'

const init = {
    loading:true,
    userInfor:{
        data:null,
        code:''
    },
    total: null,
    pageNum:0,
    topNum:0,
    coinData:[],
    rongToken:null,
    checkVipObj:{
        storeStatus:null,
        storeType:1,
        load:true
    },
    //socket 方法
    webScoketEvent:{}
}

//获取商家认证信息的状态

const checkVip = (data) =>{
    return {
        type:CHECKUSERVIP,
        payload: data
    }
}
//判断是否登陆成功
// const isloginSuc = () =>{
//     let 
// }

const checkVipEvent = () => (dispatch) =>{
    return axios.get(conf.BBAPI + '/manage/auth/authenticationJson').then(res =>{
            if(res.status == 200){
                let data = res.data.datas
                dispatch(checkVip(data))
        }
        
    })
}

const saveUserinfor = (data) => {
    return {
        type: USER,
        preload: data,
    }
}

const pageCount = (data) =>{
    return {
        type: PAGENUM,
        preload: data,
    }
}

const getCoinMark = (data) =>{
    return {
        type: COINMARK,
        preload: data,
    }
}

//存储scoket方法
const saveSocket = (data) =>{
    return {
        type: SOKETEVENT,
        preload: data,
    }
}

//获取用户信息
const getUserBaseInfo = () =>  async(dispatch) => {

    return await get('/api/v1/user/info').then((res) => {
        //console.log(res);
        dispatch(saveUserinfor(res))
    }).catch(err =>{
        window.location.href = '/error'
    });
}

//注册融云Token
const getRongToken =  () => async(dispatch) => {
    return await post('/api/v1/user/registerRongCloud').then((res) =>{
        if(res == 200){
             dispatch(saveRongToken(res.data.rongCloudToken))
        }
    }).catch(err =>{
        window.location.href = '/error'
    });
}

//保存融云Token
const saveRongToken = (data) =>{
    return {
        type: RONGTOKEN,
        preload: data,
    }
}

//获取币种市场
const getCoinMarkInfor  = () => async(dispatch) =>{
    return await get('/web/common/market/coins').then((res) => {
       if(res.code == '200'){

            dispatch(getCoinMark(res.data.marks))
       }
        
    }).catch(err =>{
        
    });
}


//计算page点击量
const pageClick = (num = 0) => (dispatch) =>{
    dispatch(pageCount(num))
}


//save socket
const saveSocketEvent = (data) => (dispatch) =>{
    dispatch(saveSocket(data))
}

export default (state = init, action = {}) => {
    switch(action.type){
        case USER:
            return Object.assign({}, state, {
                userInfor:action.preload,
                loading:false
            });
            break;
        case PAGENUM:
            return Object.assign({}, state, {
                pageNum : state.pageNum  + 1,
                topNum  :  action.preload
            });
            break;
        case COINMARK:
                return Object.assign({}, state, {
                    coinData: action.preload
                });
            break;
        case RONGTOKEN:
                return Object.assign({}, state, {
                    rongToken: action.preload
                });
             break;
        case CHECKUSERVIP:
            return Object.assign({},state,{
                checkVipObj : Object.assign({},state.checkVipObj,{
                    ...action.payload,
                    load:false
                })
            });
        case SOKETEVENT:
            //debugger
            return Object.assign({},state,{
                webScoketEvent : Object.assign({},state.webScoketEvent,{
                    ...action.preload,
                })
            });
        default:
            return state;
            break;
    }
};

export const changeImgCode = (imgCode) =>{
    return {
        type: CHANGE_IMG_CODE,
        payload: imgCode
    }
}



export {getUserBaseInfo,pageClick,getCoinMarkInfor,getRongToken,checkVipEvent,saveSocketEvent}
