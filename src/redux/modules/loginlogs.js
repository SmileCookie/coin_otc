
import axios from 'axios';
import { DOMAIN_VIP } from '../../conf';
import qs from 'qs';
//logs
const LOGINLOGS = "btcwinex/manage/LOGINLOGS";
const REQUESTLOGINLOGSINFO = "btcwinex/manage/REQUESTLOGINLOGSINFO";
const REQUESTFAILURE = "btcwinex/manage/REQUESTFAILURE";

//获取安全验证点击操作
const SALFCLICK = "btcwinex/manage/SALFCLICK";
const salfClickPop = () =>({
    type:SALFCLICK
})

export const salfClick = () => (dispatch) => {
    dispatch(salfClickPop());
}

const requestLoginLogs = (values) => (dispatch) => {
        dispatch(requestLoginLogsInfo());
        return axios.post(DOMAIN_VIP + "/manage/queryUserLoginHistroy", qs.stringify({
            pageIndex : values.pageIndex,
            pageSize : values.pageSize
        }));
}

export const requestLoginLogsInfo = () => ({
    type:REQUESTLOGINLOGSINFO
})

export const requestFailure = () => ({
    type:REQUESTFAILURE
})

export const fetchLoginLogs = (values) => {
    return(dispatch, getState) => {
        return dispatch(requestLoginLogs(values));
    }
}

export const receiveLoginLogsInfo = (values) => {
    return {
        type:LOGINLOGS,
        payload : {
            datas:values
        }
    }
}

const initLogsDataState = {
        datas:{
            list:[]
        },
        isFetching:false,
        isLoading:false,
        //是否点击安全验证
        salfClick:false

}

 const reducer = (state = initLogsDataState, action) => {
    switch (action.type) {
        case REQUESTLOGINLOGSINFO:
            return Object.assign({},state,{
                isFetching:true
            });
        case REQUESTFAILURE:
            return Object.assign({},state,{
                isLoading:true
            });
        case LOGINLOGS:
            return Object.assign( {}, state,{
                isFetching:false,
                isLoading:false,
                datas:action.payload.datas
            });
        case SALFCLICK:
        return Object.assign( {}, state,{
            salfClick:true
        });
        default:
            return state

    }
}

export default reducer;