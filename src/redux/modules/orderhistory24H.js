
import axios from 'axios';
import { TH, Types } from '../../utils';
import { DOMAIN_TRANS } from '../../conf';
const qs = require('qs');
//logs
const ORDERHISTORY = "btcwinex/Record/ORDERHISTORY";
const REQUESTORDERHISTORYINFO = "btcwinex/Record/REQUESTORDERHISTORYINFO";

const axiosOrderHistory24H = (market,includeCancel,timeType,type,pageNum,pageSize,lastTime,fun) => (dispatch) => {
        dispatch(requestOrderHistory());
        return axios.get(DOMAIN_TRANS + "/Record/getEntrustHistoryFor24?jsoncallback=&"+"market="+market+"&type="+type+"&includeCancel="+includeCancel+"&timeType="+timeType+"&pageNum="+pageNum+"&pageSize="+pageSize+"&_="+lastTime,{withCredentials:true})
        .then(res => {
            try{
            let data = eval(res['data'])[0];
            
            const ay = data.records;

            if(Types.isArray(ay)){
                for(const item of ay){
                    TH(item.amount);
                    TH(item.completeNumber);
                    TH(item.completeTotalMoney);
                    TH(item.date);
                    TH(item.entrustId);
                    TH(item.market);
                    TH(item.price);
                    TH(item.status);
                    TH(item.type);
                }
            }

            dispatch(receiveLoginLogsInfo(data));
            
            if(fun){
                fun();
            }}catch(e){}
        });
}

export const requestOrderHistory = () => ({
    type:REQUESTORDERHISTORYINFO
})


export const fetchOrderHistory24H = (market,includeCancel,timeType,type,pageNum,pageSize,lastTime,fun) => {
    return(dispatch, getState) => {
        return dispatch(axiosOrderHistory24H(market,includeCancel,timeType,type,pageNum,pageSize,lastTime,fun));
    }
}

export const receiveLoginLogsInfo = (values) => {
    return {
        type:ORDERHISTORY,
        payload : {
            datas:values
        }
    }
}

const initOrderHistoryState = {
        datas:{
            list:[]
        },
        isLoaded:false,
        isLoading:false
}

 const reducer = (state = initOrderHistoryState, action) => {
    switch (action.type) {
        case REQUESTORDERHISTORYINFO:
            return Object.assign({},state,{
                isLoading:true
            });
        case ORDERHISTORY:
            return Object.assign( {}, state,{
                isLoaded:false,
                isLoading:false,
                datas:action.payload.datas
            });
        default:
            return state

    }
}
export default reducer;