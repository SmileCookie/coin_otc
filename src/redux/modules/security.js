import axios from 'axios'
const qs = require('qs');
import { DOMAIN_VIP,COIN_KEEP_POINT } from '../../conf'
const REQUEST_USER_SECURITY = 'btcwinex/manage/REQUEST_USER_SECURITY';
const RECIEVE_USER_SECURITY = 'btcwinex/manage/RECIEVE_USER_SECURITY';
const requestSecurityInfo = () => ({
    type:REQUEST_USER_SECURITY
})

const receiveSecurityInfo = (data) => {
    return {
        type:RECIEVE_USER_SECURITY,
        payload:{
            data:data
        }
    }
}
export const saveChange = (data) => dispatch => {
    return axios.post(DOMAIN_VIP+'/manage/auth/changeAuth',qs.stringify(data))
}
export const userSendCode = () => dispatch => {
    let data = {codeType:14}
    return axios.post(DOMAIN_VIP+'/userSendCode',qs.stringify(data))
}
export const fetchSecurityInfo = () => dispatch => {
    dispatch(requestSecurityInfo())
    return axios.get(`${DOMAIN_VIP}/manage/auth/authInit`)
                .then(res => {
                    try{
                    const result = res.data
                    dispatch(receiveSecurityInfo(result.datas))
                    }catch(e){}
                })
}

const securityInfo = {
    security:{
        isFetching: false,
        data:null
    }
}

const reducer = (state = securityInfo,action) => {
    switch (action.type){
        case REQUEST_USER_SECURITY:
            return Object.assign({},state,{
                security:Object.assign({},state.security,{
                    isFetching:true
                })
            })
        case RECIEVE_USER_SECURITY:
            return Object.assign({},state,{
                security:Object.assign({},state.security,{
                    isFetching:false,
                    data:action.payload.data
                })
            })
        default:
            return state;
    }
}

export default reducer;