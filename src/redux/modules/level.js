import axios from 'axios'
import { DOMAIN_VIP,COIN_KEEP_POINT } from '../../conf';
const qs = require('querystring');

const REQUEST_MANAGE_LEVELLOGS = 'btcwinex/manage/REQUEST_MANAGE_LEVELLOGS';
const RECIEVE_MANAGE_LEVELLOGS = 'btcwinex/manage/RECIEVE_MANAGE_LEVELLOGS';
const RECIEVE_MANAGE_INTEGARL = 'btcwinex/manage/RECIEVE_MANAGE_INTEGARL';
const requestLevelLogsInfo = () => ({
    type:REQUEST_MANAGE_LEVELLOGS
})

const receiveLevelLogsInfo = (logs) => {
    return {
        type:RECIEVE_MANAGE_LEVELLOGS,
        payload:{
            logs:logs
        }
    }
}
const receiveIntegralInfo = (data) => {
    console.log(data)
    let integral  = data.currentPoints;
    let level = data.currentRate;
    let levelBeginPoint = data.currentRateBeginPoint;
    let nextLevel = data.nextRate;
    let nextLevelBeginPoint = data.nextRateBeginPoint;
    return{
        type:RECIEVE_MANAGE_INTEGARL,
        payload:{
            integral:integral,
            level:level,
            levelBeginPoint:levelBeginPoint,
            nextLevel:nextLevel,
            nextLevelBeginPoint:nextLevelBeginPoint
        }
    }
}
export const fetchIntegral = () => dispatch =>{
     return axios.get(DOMAIN_VIP+'/manage/level/getLevelInfo')
                .then(res => {
                    console.log(eval(res["data"].datas))

                    return dispatch(receiveIntegralInfo(eval(res["data"].datas)))
                })
    
}
export const fetchLevelInfo = () => dispatch => {
    dispatch(requestLevelLogsInfo())
    axios.get(DOMAIN_VIP+'/manage/level/ajax')
                .then(res => {
                    console.log(res);
                    //return dispatch(receiveLevelLogsInfo(eval(res["data"].datas.list)))
                })
    let res = [
        {
            date:1504171212,
            typeShowNew:"登录",
            ioType:0,
            memo:"加积分",
            value:10
        },
        {
            date:1505171212,
            typeShowNew:"账户留存资金",
            ioType:1,
            memo:"加积分",
            value:20
        }
    ]
    return dispatch(receiveLevelLogsInfo(eval(res)))
}

const levelInfo = {
    integralLogs:{
        isFetching: false,
        data:null
    },
    integral:{
        value:null,
        level:null,
        levelBeginPoint:null,
        nextLevel:null,
        nextLevelBeginPoint:null
    }
}

const reducer = (state = levelInfo,action) => {
    switch (action.type){
        case REQUEST_MANAGE_LEVELLOGS:
            return Object.assign({},state,{
                integralLogs:Object.assign({},state.integralLogs,{
                    isFetching:true
                })
            })
        case RECIEVE_MANAGE_LEVELLOGS:
            return Object.assign({},state,{
                integralLogs:Object.assign({},state.integralLogs,{
                    isFetching:false,
                    data:action.payload.logs
                })
            })
        case RECIEVE_MANAGE_INTEGARL:
            return Object.assign({},state,{
                integral:Object.assign({},state.integral,{
                    value:action.payload.integral,
                    level:action.payload.level,
                    levelBeginPoint:action.payload.levelBeginPoint,
                    nextLevel:action.payload.nextLevel,
                    nextLevelBeginPoint:action.payload.nextLevelBeginPoint
                    // value:1000000,
                    // level:8,
                    // levelBeginPoint:800000,
                    // nextLevel:9,
                    // nextLevelBeginPoint:1200000
                })
            })
        default:
            return state;
    }
}

export default reducer;