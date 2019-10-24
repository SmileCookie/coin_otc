import axios from 'axios'
const qs = require('qs');
import { DOMAIN_VIP,COIN_KEEP_POINT,PAGEINDEX,PAGESIZEFIVE} from '../../conf'


const REQUEST_TRANSFER_LIST = 'btcwinex/manage/REQUEST_TRANSFER_LIST';



export const receivetransferRecord = (json) => {
    return {
        type:REQUEST_TRANSFER_LIST,
        payload:{
            data:json
        }
    }
}
export const fetchTransferList =(conf) => async dispatch => {
    // dispatch(requsetTransferList())
    return axios.post(DOMAIN_VIP+'/manage/account/ChargeDownHistory/getFundTrandferLog',qs.stringify({
        pageIndex:conf.pageIndex||PAGEINDEX,
        pageSize:conf.pageSize||PAGESIZEFIVE,
        src:conf.from,
        timeTab:conf.timeTab,
        dst:conf.to,
    })).then(res => {
        let result = res.data
        if(result.isSuc){
            dispatch(receivetransferRecord(result.datas))
        }
    })
}

const inittransferlist = {
    record:{
        isloading:false,
        isloaded: false,
        data:{
            list:[],
            totalCount:0
        }
    }
}


const reducer = (state = inittransferlist, action) => {
        switch (action.type){
            case REQUEST_TRANSFER_LIST:
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












