import axios from 'axios'
import qs from 'qs'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE } from '../../conf'

const RECEIVE_MANAGE_TRANSFER_HISTORY = 'btcwinex/manage/RECEIVE_MANAGE_TRANSFER_HISTORY';;
export const receivetransferRecord = (json) => {
    return {
        type:RECEIVE_MANAGE_TRANSFER_HISTORY,
        payload:{
            data:json
        }
    }
}
export const transferRecord = (conf) => (dispatch,getState)=> {
    // dispatch(requestRocord())
    // DOMAIN_VIP+"/manage/account/chargeDownHistory/getDownloadRecordList"
    return axios.post(DOMAIN_VIP+"/manage/account/chargeDownHistory/getFundTrandferLogList", qs.stringify({
        pageIndex:conf.pageIndex||PAGEINDEX,
        pageSize:conf.pageSize||PAGESIZEFIVE,
        fundsType:conf.fundsType,
        src:conf.src,
        timeTab:conf.timeTab,
    })).then(res => {
        let result = res.data
        if(result.isSuc){
            dispatch(receivetransferRecord(result.datas))
        }
    })
}

const reducer = (state ={
    record:{
        isloading:false,
        isloaded: false,
        data:{
            list:[],
            totalCount:0
        }
    }
},action) => {
    switch (action.type){
        case RECEIVE_MANAGE_TRANSFER_HISTORY:
            return Object.assign({},state,{
                record:Object.assign({},state.record,{
                    isloading:false,
                    isloaded:true,
                    data:action.payload
                })
            })
        default:
        return state;
   }
}
export default reducer
