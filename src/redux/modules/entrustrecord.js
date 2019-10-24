import axios from 'axios';
import { DOMAIN_BASE, DOMAIN_VIP, DOMAIN_TRANS } from '../../conf';
const FETCH_ENTRUST_RECORD = 'btcwinex/entrustrecord/FETCH_ENTRUST_RECORD';
const REQUEST_LIMIT_ENTRUST_RECORD = 'btcwinex/entrustrecord/REQUEST_LIMIT_ENTRUST_RECORD';
const REQUEST_PLAN_ENTRUST_RECORD = 'btcwinex/entrustrecord/REQUEST_PLAN_ENTRUST_RECORD';
const REQUEST_HISTORY_ENTRUST_RECORD = 'btcwinex/entrustrecord/REQUEST_HISTORY_ENTRUST_RECORD';
const RECIEVE_LIMIT_ENTRUST_RECORD = 'btcwinex/entrustrecord/RECIEVE_LIMIT_ENTRUST_RECORD';
const RECIEVE_PLAN_ENTRUST_RECORD = 'btcwinex/entrustrecord/RECIEVE_PLAN_ENTRUST_RECORD';
const RECIEVE_HISTORY_ENTRUST_RECORD = 'btcwinex/entrustrecord/RECIEVE_HISTORY_ENTRUST_RECORD';

const CANCEL_ENTRUST = 'btcwinex/entrustrecord/CANCEL_ENTRUST';

export const fetchEntrustRecord = (market,entrustType, type, lastTime, pageSize=30, pageIndex,fun) => {
    return (dispatch, getState) => {
        dispatch(requestEntrustRecord(type));
        // let dataOr ='([{"lastTime":1508145512970,"record":[["603",247.2,0.01,0.01,2.472,0,1505531003942,2,0.00200000,0.0,8],["602",247.2,0.1,0.1,24.72,0,1505530987383,2,0.00200000,0.0,8],["601",247.2,0.01,0.01,2.472,0,1505529883633,2,0.00200000,0.0,8]],"precord" : []}])';
        // let data = eval(dataOr)[0];
        // dispatch(recieveEntrustRecord(type, data));
        axios.get(DOMAIN_TRANS + "/Record/Get-"+market+"?entrustType="+entrustType+"&lastTime="+lastTime+'&status='+type+'&pageSize='+pageSize+'&pageIndex='+pageIndex+'&jsoncallback=',{withCredentials:true})
            .then(res => {
                try{
                let data = eval(res['data'])[0];
                // console.log(data);
                dispatch(recieveEntrustRecord(type, data));
                }catch(e){}
                if(fun){
                    fun()
                }
            });
    };
}
export const requestEntrustRecord = (type) => {
    if(3 == type){
        return {
            type: REQUEST_LIMIT_ENTRUST_RECORD
        }
    }else if(2 == type){
        return {
            type: REQUEST_HISTORY_ENTRUST_RECORD
        }
    }else{
        return {
            type: REQUEST_PLAN_ENTRUST_RECORD
        }
    }
}
export const recieveEntrustRecord = (type, data) => {
    if(3 == type){
        return {
            type: RECIEVE_LIMIT_ENTRUST_RECORD,
            payload: {
                lastTime: data.lastTime,
                record: data.record,
                count: data.count
            }
        }
    }else if(2 == type){
        return {
            type: RECIEVE_HISTORY_ENTRUST_RECORD,
            payload: {
                lastTime: data.lastTime,
                record: data.record,
                count: data.count
            }
        }
    }else{
        let pRecordNew = [];
        let pRecord = data.precord;
        if(pRecord){
            for (let i = 0; i < pRecord.length; i++) {
                if (pRecord[i][7] == "-1") {
                    pRecordNew.push(pRecord[i]);
                }
            };
        }
        return {
            type: RECIEVE_PLAN_ENTRUST_RECORD,
            payload: {
                record: pRecordNew
            }
        }
    } 
}

export const cancelEntrust = (market,id,plantype) => {
    return (dispatch, getState) => {
        return axios.get(DOMAIN_TRANS +  "/Entrust/cancle-" + market + "-" + id + "-" + plantype + "?jsoncallback=",{withCredentials:true});
    }
}
export const batchCancelEntrust = (market,plantype,types,minPrice,maxPrice) => {
    return (dispatch, getState) => {
        return axios.get(DOMAIN_TRANS +  "/entrust/cancleMore-" + market + "-" + plantype + "?jsoncallback="+ "&types=" + types+ "&minPrice=" + minPrice+ "&maxPrice=" + maxPrice,{withCredentials:true});
    }
}
export const cancelAllStop = (market) => {
    return (dispatch, getState) => {
        return axios.get(DOMAIN_TRANS + "/entrust/cancelmorePlanEntrust-" + market + "?jsoncallback=",{withCredentials:true});
    }
}
const initialEntrustRecordState = {
    limit: {
        isFetching: false,
        lastTime: 0,
        data: []
    },
    plan: {
        isFetching: false,
        lastTime: 0,
        data: []
    },
    history: {
        isFetching: false,
        lastTime: 0,
        data: []
    }
}

const reducer = (state = initialEntrustRecordState, action) => {
    switch(action.type) {
        case REQUEST_LIMIT_ENTRUST_RECORD:
            return Object.assign({}, state, {
                limit: Object.assign({}, state.limit, {
                    isFetching: true
                })
            });
        case RECIEVE_LIMIT_ENTRUST_RECORD:
            return Object.assign({}, state, {
                limit: Object.assign({}, state.limit, {
                    isFetching: false,
                    data: action.payload.record
                })
            });
        case REQUEST_HISTORY_ENTRUST_RECORD:
            return Object.assign({}, state, {
                history: Object.assign({}, state.history, {
                    isFetching: true
                })
            });
        case RECIEVE_HISTORY_ENTRUST_RECORD:
            return Object.assign({}, state, {
                history: Object.assign({}, state.history, {
                    isFetching: false,
                    data: action.payload.record
                })
            });
        case REQUEST_PLAN_ENTRUST_RECORD:
            return Object.assign({}, state, {
                plan: Object.assign({}, state.plan, {
                    isFetching: true
                })
            });
        case RECIEVE_PLAN_ENTRUST_RECORD:
            return Object.assign({}, state, {
                plan: Object.assign({}, state.plan, {
                    isFetching: false,
                    data: action.payload.record
                })
            });
        default:
            return state;
    }
}

export default reducer;