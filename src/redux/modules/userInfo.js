import axios from 'axios'
import { DOMAIN_VIP,COIN_KEEP_POINT,MOBILE_IMG_CODE,MOBILE_AUTH_CODETYPE,MOBILE_MODIFY_CODETYPE,MONEY_MODIFY_CODETYPE } from '../../conf'
import { JSEncrypt } from 'jsencrypt';

const qs = require('qs');
// const REQUEST_USER_LOGIN_PASSWORD = 'btcwinex/user/REQUEST_USER_LOGIN_PASSWORD';
// const RECEIVE_USER_LOGIN_PASSWORD = 'btcwinex/user/RECEIVE_USER_LOGIN_PASSWORD';
const REQUEST_USER_SAFEPWD = 'btcwinex/user/REQUEST_USER_SAFEPWD';
const RECEIVE_USER_SAFEPWD = 'btcwinex/user/RECEIVE_USER_SAFEPWD';
const REQUEST_USER_EMAIL_AUTH = 'btcwinex/user/REQUEST_USER_EMAIL_AUTH';
const RECEIVE_USER_EMAIL_AUTH = 'btcwinex/user/RECEIVE_USER_EMAIL_AUTH';
const RECIEVE_PUBLICK_KEY_MODIFY = 'btcwinex/user/RECIEVE_PUBLICK_KEY_MODIFY'
const REQUEST_USER_GOOGLEAUTH = 'btcwinex/user/REQUEST_USER_GOOGLEAUTH';
const RECEIVE_USER_GOOGLEAUTH = 'btcwinex/user/RECEIVE_USER_GOOGLEAUTH';
const MODIFY_USER_GOOGLEAUTH = 'btcwinex/user/MODIFY_USER_GOOGLEAUTH';
const REQUEST_USER_MOBILE = 'btcwinex/user/REQUEST_USER_MOBILE';
const RECEIVE_USER_MOBILE = 'btcwinex/user/RECEIVE_USER_MOBILE';
const CHANGE_MOBILE_IMG_CODE = 'btcwinex/user/CHANGE_MOBILE_IMG_CODE';
const REQUEST_USER_UPLOAD_TOKEN = 'btcwinex/user/REQUEST_USER_UPLOAD_TOKEN';
const REQUEST_USER_AUTH_INFO = 'btcwinex/user/REQUEST_USER_AUTH_INFO';
const REQUEST_USER_AUTH_TYPE = 'btcwinex/user/REQUEST_USER_AUTH_TYPE';

//修改手机号
export const sendModifyCode = () => dispatch =>{
    return axios.post(DOMAIN_VIP+"/userSendCode",qs.stringify({
        codeType:MOBILE_MODIFY_CODETYPE
    }))
}

export const sendNewCode = (values) => dispatch => {
    return axios.post(DOMAIN_VIP+"/manage/auth/authMobileSendCode/true",qs.stringify({
        code : values.code,
        mCode : values.mCode,
        mobile : values.mobile,
        codeType : !values.cid ? MOBILE_MODIFY_CODETYPE : values.cid
    }))
}

export const setpOne = (mobileCode) => dispatch => {
    return axios.post(DOMAIN_VIP+"/manage/auth/doMobileModifyStepOne",qs.stringify({
        mobileCode:mobileCode
    }))
}

export const saveModifyMobile = (values) => dispatch => {
    return axios.post(DOMAIN_VIP+"/manage/auth/doMobileModify",qs.stringify({
        countryCode : values.countryCode,
        newMobileNumber : values.newMobileNumber,
        newMobileCode : values.newMobileCode,
        safePwd : values.safePwd,
        googleCode:values.googleCode,
        method : values.method,
        codeType : MOBILE_MODIFY_CODETYPE
    }))
}



//获取用户 Mobile 配置信息
export const requestMobile = () => ({
    type:REQUEST_USER_MOBILE
})

export const receiveMobile = (json) =>{
    return {
        type:RECEIVE_USER_MOBILE,
        payload:{
            data:json
        }
    }
}

export const fetchMobile = () => dispatch => {
    dispatch(requestMobile())
    return axios.get(DOMAIN_VIP + '/manage/auth/mobileJson').then(res => {
        dispatch(receiveMobile(res.data.datas))
    });
}

export const ChangeImgCode = (imgcode) => {
    return {
        type:CHANGE_MOBILE_IMG_CODE,
        payload:imgcode
    }
}

export const submitMobileAuth = (values) => dispatch => {
    return axios.post(DOMAIN_VIP+"/manage/auth/authMobile",qs.stringify({
        mobile:values.mobile,
        mCode:values.mCode,
        code:values.code,
        mobileCode:values.mobileCode,
        emailCode:values.emailCode,
        googleCode:values.googleCode,
        codeType:MOBILE_AUTH_CODETYPE
    }))
}

export const sendMobileCode = (values) => {
    return axios.post(DOMAIN_VIP+"/manage/auth/authMobileSendCode/true",qs.stringify({
        code : values.code,
        mCode : values.mCode,
        mobile : values.mobile,
        codeType : MOBILE_AUTH_CODETYPE
    }))
}

export const sendMobileECode = (values) => {
    return axios.post(DOMAIN_VIP+"/manage/auth/authMobileSendCode",qs.stringify({
        mCode : values.mCode,
        mobile : values.mobile,
        codeType : MOBILE_AUTH_CODETYPE
    }))
}


//获取用户 Google 验证码信息

export const requestGoogle = () => ({
    type:REQUEST_USER_GOOGLEAUTH
})

export const receiveGoogle = (json) => {
    return {
        type:RECEIVE_USER_GOOGLEAUTH,
        payload:{
            data:json
        }
    }
}

export const fetchGoogle = () => dispatch => {
    dispatch(requestGoogle())
    return axios.get(DOMAIN_VIP + '/manage/auth/googleJson').then(res => {
        dispatch(receiveGoogle(res.data.datas));
    });
}

export const submitGoogle = (values) => dispatch => {
    return axios.post(DOMAIN_VIP+"/manage/auth/openGoogleAuth",qs.stringify({
        secret : values.secret,
		gCode : values.gCode,
		mobileCode : values.mobileCode
    }))
}

export const sendGoogleCode = () => dispatch => {
    return axios.post(DOMAIN_VIP+"/userSendCode",qs.stringify({
        codeType :5
    }))
}

export const ModifyGoogle = () => ({
    type:MODIFY_USER_GOOGLEAUTH
})


//获取用户资金密码配置
export const requestSafePwd = (json) => ({
    type:REQUEST_USER_SAFEPWD,
    payload:{
        data:json
    }
})
export const receiveSafePwd = (json) => {
    return {
        type:RECEIVE_USER_SAFEPWD,
        payload:{
            data:json
        }
    }
}
export const fetchSafePwd = () => dispatch => {
    dispatch(requestSafePwd())
    return axios.get(DOMAIN_VIP + '/manage/auth/pwd/safeJson').then(res => {
     dispatch(receiveSafePwd(res.data.datas))
    });
}

export const sendSafePwdCode = (id) => dispatch => {
    return axios.post(DOMAIN_VIP+"/userSendCode",qs.stringify({
        codeType : id ? id : MONEY_MODIFY_CODETYPE
    }))
}

export const submitSafePwd = (values) => dispatch => {
    return axios.post(DOMAIN_VIP+"/manage/auth/pwd/safeUpdate",qs.stringify({
        currentPwd:values.currentPwd,
        safePwd:values.safePwd,
        safeLevel:values.safeLevel,
        newPwd:values.newPwd,
        mobileCode:values.mobileCode,
        googleCode:values.googleCode
    }))
}

// 关闭谷歌认证
export const doCloseGoogleAuth = (values) => dispatch => {
    return axios.post(DOMAIN_VIP + '/manage/auth/doCloseGoogleAuth', qs.stringify(values))
}

//rsa encrypt
//登录密码修改
const encrypt = new JSEncrypt();
export const fetchPublicKey = () => {
    return (dispatch, getState) => {
        return axios.get(DOMAIN_VIP + "/login/getPubTag?t=" + new Date().getTime())
    }
}
export const savePassword = (values) => (dispatch,getState) => {
    return dispatch(fetchPublicKey()).then(res => {
        let result = res.data;
        if(result && result.isSuc) {
            encrypt.setPublicKey(result.datas.pubTag);
        }

        return axios.post(DOMAIN_VIP+"/manage/auth/pwd/logupdate",qs.stringify({
                currentPassword: encrypt.encrypt(values.currentPassword),
                pwd: encrypt.encrypt(values.pwd),
                pwdLevel: values.pwdLevel
            }))

    })
}

//邮箱验证 actions
export const requestEmail = () => ({
    type:REQUEST_USER_EMAIL_AUTH
})

export const receiveEmail = (json) => {
    return {
        type:RECEIVE_USER_EMAIL_AUTH,
        payload:{
            data:json
        }
    }
}
export const fetchMailInfo = () => dispatch => {
    dispatch(requestEmail());
    return axios.get(DOMAIN_VIP + '/manage/auth/emailJson').then(res => {
        res = res.data;
        const send = Object.assign({}, {isError:!!res.datas.tips}, res.datas);
        dispatch(receiveEmail(send))
    });
}

//邮箱验证短信验证码获取
export const sendCode = () => dispatch => {
    return axios.post(DOMAIN_VIP+"/userSendCode",qs.stringify({
        codeType : 7
    }))
}

//邮箱验证提交
export const fetchMailbox = (values) => dispatch => {
    return axios.post(DOMAIN_VIP+"/manage/auth/logic/postemail?step="+values.step,qs.stringify({
        email:values.email,
        payPwd:values.payPwd,
        mobileCode:values.mobileCode
    }))
}

// 获取用户上传图片的token和相关状态
export const requestUserUploadToken = (json)=>{
    return {
        type: REQUEST_USER_UPLOAD_TOKEN,
        payload: json
    }
}
export const getUploadToken = (cb = () => {}) => (dispatch) => {
    return axios.post(DOMAIN_VIP+"/manage/auth/uploadToken").then(res => {
        dispatch(requestUserUploadToken(res.data));
        cb();
    });
}

// 开始认证
export const requestUserAuthInfo = json => {
    return {
        type: REQUEST_USER_AUTH_INFO,
        payload: json
    }
}
export const getAuthInfo = () => dispatch => {
    axios.get(DOMAIN_VIP + '/manage/auth/authenticationJson').then((res)=>{
        res.data.isSuc && dispatch(requestUserAuthInfo(res.data.datas));
    });
}

// 身份认证，选择发证国家
export const requestUserAuthType = json => {
    return {
        type: REQUEST_USER_AUTH_TYPE,
        payload: json
    }
}
export const getAuthTypeInfo = () => dispatch => {
    return axios.get(DOMAIN_VIP + '/manage/auth/authTypeJson').then((res)=>{
        res.data.isSuc && dispatch(requestUserAuthType(res.data.datas));
        return res;
    });
}

// 身份证，护照上传。
export const saveUserAuth = (formData) => dispatch => {
    return axios.post(DOMAIN_VIP + '/manage/auth/AuthSave', qs.stringify(formData), {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
        }
    });
}

const reducer = (state={
    email:{
        isloading:false,
        isloaded:false,
        safeAuth:false,
        emailStatus:0,
        step:''
    },
    safePwd:{
        isloading:false,
        isloaded:false,
        data:{
            hasSafePwd:false,
            googleAuth:0,
            mobileStatu:0
        }
    },
    google:{
        isloading:false,
        isloaded:false,
        data:{
            verifyUserInfo:{
                status:-1
            }
        }
    },
    mobile:{
        isloading:false,
        isloaded:false,
        imgCode:MOBILE_IMG_CODE,
        data:{
            phonenum:"",
            mobileStatu:1,
            verifyUserInfo:{
                status:-1
            }
        }
    },
    uploadToken:{
        des: "",
        isSuc: false,
        datas: {}
    },
    authInfo:{
        authStatus: '',
        reason: '',
        isBlack: '',
        isLock: '',
        lockTime: ''
    },
    authType:{
        selectedCode: '-1',
        redirect: ''
    }
},action) => {
    switch(action.type){
        case REQUEST_USER_EMAIL_AUTH:
            return Object.assign({},state,{
                email:Object.assign({},state.email,{
                    isloading:true
                })
            })
        case RECEIVE_USER_EMAIL_AUTH:
            return Object.assign({},state,{
                email:Object.assign({},state.email,{
                    isloading:false,
                    isloaded:true,
                }, action.payload.data)
            })
        case REQUEST_USER_SAFEPWD:
            return Object.assign({},state,{
                safePwd:Object.assign({},state.safePwd,{
                    isloading:true
                }, action.payload.data)
            })
        case RECEIVE_USER_SAFEPWD:
            return Object.assign({},state,{
                safePwd:Object.assign({},state.safePwd,{
                    isloading:false,
                    isloaded:true,
                    data:action.payload.data
                })
            })
        case REQUEST_USER_GOOGLEAUTH:
            return Object.assign({},state,{
                google:Object.assign({},state.google,{
                    isloading:true
                })
            })
        case RECEIVE_USER_GOOGLEAUTH:
            return Object.assign({},state,{
                google:Object.assign({},state.google,{
                    isloading:false,
                    isloaded:true,
                    data:Object.assign({}, state.google.data ,action.payload.data)
                })
            })
        case MODIFY_USER_GOOGLEAUTH:
            return Object.assign({},state,{
                google:Object.assign({},state.google,{
                    data:Object.assign({},state.google.data,{
                        method:1
                    })
                })
            })
        case REQUEST_USER_MOBILE:
            return Object.assign({},state,{
                mobile:Object.assign({},state.mobile,{
                    isloading:true
                })
            })
        case RECEIVE_USER_MOBILE:
            return Object.assign({},state,{
                mobile:Object.assign({},state.mobile,{
                    isloading:false,
                    isloaded:true,
                    data:Object.assign({}, state.mobile.data, action.payload.data)
                })
            })
        case CHANGE_MOBILE_IMG_CODE:
            return Object.assign({},state,{
               mobile:Object.assign({},state.mobile,{
                    imgCode:action.payload
               })
            })
        case REQUEST_USER_UPLOAD_TOKEN:
            return Object.assign({}, state, {
                uploadToken: Object.assign({}, state.uploadToken, action.payload)
            })
        case REQUEST_USER_AUTH_INFO:
            return Object.assign({}, state, {
                authInfo: Object.assign({}, state.authInfo, action.payload)
            })
        case REQUEST_USER_AUTH_TYPE:
            return Object.assign({}, state, {
                authType: Object.assign({}, state.authType, action.payload)
            })
        default:
           return state
    }
}

export default reducer

























