
import axios from 'axios';
import { DOMAIN_TRANS } from '../../conf';
const qs = require('qs');
import { TH, Types } from '../../utils';
//logs
const CURRENTHISTORY = "btcwinex/Record/CURRENTHISTORY";
const CURRENTHISTORYINFO = "btcwinex/Record/CURRENTHISTORYINFO";

const axiosCurrentHistory = (tab,market,includeCancel,timeType,type,pageIndex,pageSize,lastTime) => (dispatch) => {
        dispatch(requestCurrentHistory());
        return axios.get(DOMAIN_TRANS + "/Record/getTransRecordNow?jsoncallback=&"+"market="+market+"&types="+type+"&tab="+tab+"&pageIndex="+pageIndex+"&pageSize="+pageSize+"&_="+lastTime,{withCredentials:true})
        .then(res => {
            try{
            let data = eval(res['data'].datas);

            const {pageIndex, totalCount, list} = data;

            TH(pageIndex);
            TH(totalCount);
            TH(list);
            if(Types.isArray(list)){
                for(const item of list){
                    TH(item.completeNumber);
                    TH(item.completeTotalMoney);
                    TH(item.id);
                    TH(item.numbers);
                    TH(item.submitTime);
                    TH(item.types);
                    TH(item.unitPrice);
                }
            }
            
            dispatch(receiveLoginLogsInfo(data));
            
            }catch(e){}
        });
}

export const requestCurrentHistory = () => ({
    type:CURRENTHISTORYINFO
})


export const fetchCurrentHistory = (tab,market,includeCancel,timeType,type,pageIndex,pageSize,lastTime) => {
    return(dispatch, getState) => {
        return dispatch(axiosCurrentHistory(tab,market,includeCancel,timeType,type,pageIndex,pageSize,lastTime));
    }
}

export const receiveLoginLogsInfo = (values) => {
    return {
        type:CURRENTHISTORY,
        payload : {
            datas:values
        }
    }
}

const initCurrentHistoryState = {
        datas:{
            list:[]
        },
        isLoaded:false,
        isLoading:false
}

 const reducer = (state = initCurrentHistoryState, action) => {
    switch (action.type) {
        case CURRENTHISTORYINFO:
            return Object.assign({},state,{
                isLoading:true
            });
        case CURRENTHISTORY:
            return Object.assign({}, state,{
                isLoaded:true,
                isLoading:false,
                datas:action.payload.datas
            });
        default:
            return state
    }
}
export default reducer;