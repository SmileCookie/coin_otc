import axios from 'axios';
const qs = require('qs');
import cookie from 'js-cookie';
import { JSEncrypt } from 'jsencrypt';
import { browserHistory } from 'react-router';
import { formatURL, optPop } from '../../utils'

import { DEFAULT_LOCALE, COOKIE_UID, COOKIE_IS_LOGGIN, COOKIE_UNAME, COOKIE_LOGIN_STATUS, COOKIE_LAN, COOKIE_IP_AUTH, COOKIE_GOOGLE_AUTH, DOMAIN_BASE, DOMAIN_VIP, URL_IMG_CODE,COOKIE_PREFIX, TRADE, IDX, LOGINR, KF, SAUTH, SETPASSWORD } from '../../conf';

const SET_LANGUAGE = 'btcwinex/user/SET_LANGUAGE';
//get user info from cookie
const FETCH_USERINFO = 'btcwinex/user/FETCH_USERINFO';
const CHANGE_IMG_CODE = 'btcwinex/user/CHANGE_IMG_CODE';
const SHOW_IMG_CODE = 'btcwinex/user/SHOW_IMG_CODE';
const HIDE_IMG_CODE = 'btcwinex/user/HIDE_IMG_CODE';
//get public key
const FETCH_PUBLIC_KEY = 'btcwinex/user/FETCH_PUBLIC_KEY';
const RECIEVE_PUBLICK_KEY = 'btcwinex/user/RECIEVE_PUBLICK_KEY';
//user login
const LOGIN = 'btcwinex/user/LOGIN';
const LOGIN_SUCCESS = 'btcwinex/user/LOGIN_SUCCESS';
const LOGIN_FAIL = 'btcwinex/user/LOGIN_FAIL';
//user logout
const LOGOUT = 'btcwinex/user/LOGOUT';

//user sign up
const SIGNUP = 'btcwinex/user/SIGN_UP';
const SIGNUP_SUCCESS = 'btcwinex/user/SIGNUP_SUCCESS';
const SIGNUP_FAIL = 'btcwinex/user/SIGNUP_FAIL';
const REMEMBER = 'btcwinex/user/REMEMBER';

// Referee
const REFEREE = 'REFEREE';

// use base info
const REQUEST_USER_BASE_INFO = 'btcwinex/user/REQUEST_USER_BASE_INFO';

// second certification
const SECONDCERTIFICATION = 'second_certification';

// second auth commit
const SECONDCM = 'btcwinex/user/loginAuthGOne';

//controll scroll
// const CONTROLLSCROLL = 'CONTROLLSCROLL'

const OTCFLAG = 'btcwinex/user/otcflag'



//rsa encrypt
const encrypt = new JSEncrypt();

export const secondCertification = (status = 0) => {
    return {
        type: SECONDCERTIFICATION,
        payload: status,
    }
};




export const setOtcFlag = (data) => {
    return {
        type: OTCFLAG,
        payload: data,
    }
};





export const fetchSecondCertification = () => (dispatch) => {

    axios.post(DOMAIN_VIP+'/login/userState', qs.stringify({})).then(res => {
        res = res.data;
        // console.log('asdasdasdasd ' + res);
        let flg = -1;
        if(res.isSmsOpen && res.isGoogleOpen){
            flg = 2;
        } else if(res.isGoogleOpen){
            flg = 0;
        } else if(res.isSmsOpen) {
            flg = 1;
        }else{
            let otcUrl = localStorage.getItem('otcLoginBefore'); // otc 跳转
            if (!!otcUrl) {
                console.log('url =====' + otcUrl)
                localStorage.removeItem('otcLoginBefore');
                window.location.href = otcUrl;
                return false
            }
        }

        dispatch(secondCertification(flg));
    });
};

export const getReferee = (id = '') => {
    return {
        type: REFEREE,
        payload: id
    }
}

export const fetchGetReferee = () => (dispatch) => {
    // axios
    dispatch(getReferee(7858568));
};

//保存注册填写信息
export const rememberSiginInfor = (obj) => (dispatch) => {
    // axios
    dispatch(remembers(obj));
};

const remembers = (obj) =>{

    return {
        type: REMEMBER,
        payload: obj
    }
}

export const setLang = (language) => {
    let lan = language||cookie.get(COOKIE_LAN)||DEFAULT_LOCALE;
    return {
        type: SET_LANGUAGE,
        payload: lan
    }
}

export const fetchLanguageInfo = () => {
    let user = null;
    if(cookie.get(COOKIE_LAN)){
        dispatch(setLang(cookie.get(COOKIE_LAN)));
    };
}

export const fetchUserinfo = () => {
    // console.log('===============> update user');
    let user = null;
    if(cookie.get(COOKIE_IS_LOGGIN ) && cookie.get(COOKIE_UNAME)){

        user = {
            uon: cookie.get(COOKIE_IS_LOGGIN),
            username: cookie.get(COOKIE_UNAME),
            uid: cookie.get(COOKIE_UID),
            loginStatus: cookie.get(COOKIE_LOGIN_STATUS),
            googleAuth: cookie.get(COOKIE_GOOGLE_AUTH),
            ipAuth: cookie.get(COOKIE_IP_AUTH)
        }
    };
    return {
        type: FETCH_USERINFO,
        payload: user
    }
}
export const fetchPublicKey = () => {
    return (dispatch, getState) => {
        return axios.get(DOMAIN_VIP + "/login/getPubTag?t=" + new Date().getTime())
    }
}
export const recievePublicKey = (publicKey) => {
    encrypt.setPublicKey(publicKey);
    return {
        type: RECIEVE_PUBLICK_KEY,
        payload: publicKey
    }
}
export const changeImgCode = (imgCode) =>{
    return {
        type: CHANGE_IMG_CODE,
        payload: imgCode
    }
}
export const showImgCode = () =>{
    return {
        type: SHOW_IMG_CODE
    }
}

export const hideImgCode = () => {
    return {
        type: HIDE_IMG_CODE,
    }
}

const loginAfterFetchPublicKey = (values) => {
    return (dispatch, getState) => {
        return dispatch(fetchPublicKey())
                .then(res => {
                    let result = res.data;
                    if(result && result.isSuc) {
                        dispatch(recievePublicKey(result.datas.pubTag));
                    }
                    return axios.post(DOMAIN_VIP + "/login/doLogin?callback=", qs.stringify({
                        nike: values.nike,
                        pwd: encrypt.encrypt(values.pwd),
                        code: values.code,
                        countryCode: values.countryCode,
                        safe: values.safe,
                        pubTag: getState().session.pubTag,
                        code: values.code
                    }));
                });
    }
}

const CapitalCode = (values) => {
    return (dispatch, getState) => {
        return dispatch(fetchPublicKey())
                .then(res => {
                    let result = res.data;
                    if(result && result.isSuc) {
                        dispatch(recievePublicKey(result.datas.pubTag));
                    }
                    return axios.post(DOMAIN_VIP + '/manage/account/download/submit/'+values.upperName, qs.stringify({
                        receiveAddress: values.receiveAddress,
                        cashAmount: values.code,
                        safePwd: encrypt.encrypt(values.safePwd),
                        mobileCode: values.mobileCode,
                        googleCode: values.googleCode,
                        fees: values.fees,
                        codeType:values.codeType,
                        fundsType: values.fundsType
                    }))
                });
    }
}
const Cashwithdrawals = (values) => {
    return (dispatch, getState) => {
        return dispatch(fetchPublicKey()).then(res => {
                    let result = res.data;
                    if(result && result.isSuc) {
                        dispatch(recievePublicKey(result.datas.pubTag));
                    }
                    return axios.post(DOMAIN_VIP + '/manage/account/download/doSubmit/'+values.value.upperName, qs.stringify({
                        receiveAddress: values.value.receiveAddress,
                        cashAmount: values.value.cashAmount,
                        safePwd: encrypt.encrypt(values.value.safePwd),
                        mobileCode: values.value.mobileCode,
                        googleCode: values.value.googleCode,
                        fees: values.value.fees,
                        fundsType: values.value.fundsType,
                        token:values.value.token
                    }))
                });
            }
        }
// 公钥加密方法 otc
export const paySetPublicKey = (key,cb) =>{
    const K = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJG14+94DEgzyd6G8+Ue+lpLKK9uIftpSZ7wvnX3jtw+6SUKldkvL1mYq9W8qIJD7w5t3YQIkVoWIlm5Eba5NcDYgfDC/QnYyr9zfDthlJECvQ8TC0wjy9cOtCC4FntewsqmGxLjTA17Zn0RJpsqXvNFjZEinR6IawvnlhPKJ/IwIDAQAB";
    console.log(K);
    encrypt.setPublicKey(K);
    let ckey = encrypt.encrypt(key)
    return cb(ckey);
}

// 暂时不用 公钥加密方法 otc
export const paySetPublicKey1 = (key,cb) =>{
    // console.log('lllllllll ', key)
    return (dispatch) => {
        return dispatch(fetchPublicKey()).then((res) => {
            let r = res.data
            if(r && r.isSuc) {
                // const K = r.datas.pubTag;
                const K = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJG14+94DEgzyd6G8+Ue+lpLKK9uIftpSZ7wvnX3jtw+6SUKldkvL1mYq9W8qIJD7w5t3YQIkVoWIlm5Eba5NcDYgfDC/QnYyr9zfDthlJECvQ8TC0wjy9cOtCC4FntewsqmGxLjTA17Zn0RJpsqXvNFjZEinR6IawvnlhPKJ/IwIDAQAB";
                console.log(K);

                encrypt.setPublicKey(K);
                let ckey = encrypt.encrypt(key)
                return cb(ckey);
            }
        })
    }
}
const doLogin = (values) => {
    return (dispatch, getState) => {
        return axios.post(DOMAIN_VIP + "/login/doLogin?callback=", qs.stringify({
            nike: values.nike,
            pwd: encrypt.encrypt(values.pwd),
            code: values.code,
            countryCode: values.countryCode,
            safe: values.safe,
            pubTag: getState().session.pubTag,
            code: values.code
        }));
    };
}

export const login = (values) => {
    return (dispatch, getState) => {
        if(!getState().session.pubTag) {
            return dispatch(loginAfterFetchPublicKey(values));
        }else{
            return dispatch(doLogin(values));
        }
    };
}
export const withdrawal = (values) => {
    return (dispatch, getState) => {
        if(!getState().session.pubTag) {
            return dispatch(CapitalCode(values));
        }else{
            return dispatch(CapitalCode(values));
        }
    };
}
export const Cashwithdrawal = (values) => {
    return  (doLogins, getState) => {
        if(!getState().session.pubTag) {
            return doLogins(Cashwithdrawals(values));
        }else{
            return doLogins(Cashwithdrawals(values));
        }
}
}
export const loginSuccess = (that = {}) => {
    return (dispatch, getState) => {
        dispatch(fetchUserinfo());
        dispatch(getUserBaseInfo());
        //optPop(() => {
            // certification

                let url = '/',
                    flg = true;
                flg && (url = 'loginAuthRoute');
                window.location.href = formatURL(url);

            //browserHistory.push(formatURL(url));
        //},that.intl.formatMessage({id: "登录成功_w"}));
    };     
}

export const logout = () => {
    cookie.remove(COOKIE_IS_LOGGIN, {domain: '.'+ DOMAIN_BASE});
    cookie.remove(COOKIE_UNAME, {domain: '.'+ DOMAIN_BASE});
    return {
        type: LOGOUT
    }
}

// 获取用户的基础信息
export const requestUserBaseInfo = json => {
    return {
        type: REQUEST_USER_BASE_INFO,
        payload: json
    }
}
export const getUserBaseInfo = () => async (dispatch, getState) => {
    const baseUserInfo = getState().session.baseUserInfo;

    // if(!baseUserInfo.isLoaded){
        // get baseInfo
    return await axios.get(DOMAIN_VIP + '/manage/user').then((res) => {
            //console.log('>>>>>><<<<<<<<<<<');
            //console.log(res.data.isSuc);
            // save to state
            dispatch(requestUserBaseInfo(Object.assign({}, {isLoaded:res.data.isSuc}, res.data.datas)));
            return res.data.datas;
        });
    // }
}

export const setSecondCm = (status = false) => {
    return {
        type: SECONDCM,
        payload: status,
    };
};

export const emCodeCheck = (data = {}, callError = () => {}, key = '') => dispatch => {

    data.type = 1;
    // data.secondVerify = 1;
    axios.post(DOMAIN_VIP+'/login/checkCode', qs.stringify(data)).then((res)=>{
        res = res.data;
        if(!res.isSuc){
            // callError(key, res.des);
            callError.makeResult(res, true);
        } else {
            localStorage.setItem("email", data.email);
            localStorage.setItem("uid", res.datas);
            localStorage.setItem("token", res.des);
            browserHistory.push(formatURL('notGCode'));
        }
    });

    // console.log(data);
    // setTimeout(() => {
    //     callError(key, '有问题');
    // });

    // browserHistory.push(formatURL('notGCode'));
};

export const notGSms = (data = {}) => async dispatch => {
    let res = await axios.post(DOMAIN_VIP + '/login/userCheckType', qs.stringify(data));
    res = res.data;

    return [res.isRecharge, res.isAuthen, res.isSafePwd];
};

export const fetchSecondCm = (data = {}, callError = () => {}, key = '', formatMessage = () => {}) => dispatch => {
    // axios
    // console.log(data);
    // console.log(data, callError, key);
    // dispatch(setSecondCm(false));
    // setTimeout(() => {
    //     callError()(key, '3次不对');
    // });

    axios.post(DOMAIN_VIP + '/login/doLoginAuthen', qs.stringify(data)).then(res => {
        // dispatch(fetchUserinfo());
        // dispatch(getUserBaseInfo());
        res = res.data;
        if(res.isSuc){
            // dispatch(fetchUserinfo());
            // debugger
            // optPop(() => {
            //     window.location.href = res.des;
            // }, formatMessage({id: "登录成功"}));
            let otcUrl = localStorage.getItem('otcLoginBefore'); //otc 跳转
            if (!!otcUrl) {
                // console.log('url =====' + otcUrl)
                // setTimeout(() =>{
                //     localStorage.removeItem('otcLoginBefore');
                //     window.location.href = otcUrl;
                //     return false
                // },1000)
                dispatch(setOtcFlag(true))
                console.log ('waiting back to otc ')
            }else{
                window.location.href = res.des;
            }
        } else {
            callError().makeResult(res, true);
        }
    });
};
// 跳转回otc
export const backToOtc = () => dispatch => {
    let otcUrl = localStorage.getItem('otcLoginBefore'); //otc 跳转
    if (!!otcUrl) {
        console.log('url =====' + otcUrl)
        dispatch(setOtcFlag(false))
        setTimeout(() => {
            localStorage.removeItem('otcLoginBefore');
            window.location.href = otcUrl;
            return false
        }, 500)

    }else{
        return false
    }

}

export const fetchCoinList = () => async dispatch => {
    let res = await axios.post(DOMAIN_VIP + '/coin/getCoin');
    res = res.data.datas;
    return res;
};

export const fetchFrontalImg = (data = {}, callError = () => {}, key = '', formatMessage = () => {},callErrors = () => {},) => dispatch => {
    //console.log(data);
    //callError(key, '错了');
    //browserHistory.push(formatURL('/'));

    axios.post(DOMAIN_VIP+'/login/idCardAuth', qs.stringify(data)).then((res)=>{
        res = res.data;

        if(res.isSuc){
            optPop(() => {
                localStorage.removeItem("token");
                window.location.href = res.des;
             }, formatMessage({id: "您已上传证件照片，请等待客服人员审核。"}));
        } else {
            callErrors.makeResult(res, true)
        }
    });
};

export const fetchCoinCm = (data = {}, callError = () => {}, key = '', formatMessage = () => {}) => dispatch => {
    axios.post(DOMAIN_VIP+'/login/addressAuth', qs.stringify(data)).then((res)=>{
        res = res.data;

        if(res.isSuc){
            optPop(() => {
                localStorage.removeItem("token");
                window.location.href = res.des;
               //browserHistory.push(formatURL(''));
            }, formatMessage({id: "关闭谷歌/短信验证成功"}));
        } else {
            callError().makeResult(res, true);
        }
    });
};

export const fetchPay = (data = {}, callError = () => {}, key = '', formatMessage = () => {}) => dispatch => {
    axios.post(DOMAIN_VIP+'/login/checkSafe', qs.stringify(data)).then((res)=>{
        res = res.data;
        if(res.isSuc){
            optPop(() => {
                localStorage.removeItem("token");
                window.location.href = res.des;
                // browserHistory.push(formatURL(''));
            }, formatMessage({id: "关闭谷歌/短信验证成功"}));
        } else {
            callError.makeResult(res, true);
        }
    });
};

export const fetchFgAuth = (data = {}, callError = () => {}, key = '') => dispatch => {
    data.type = 1;
    axios.post(DOMAIN_VIP+'/login/checkCode', qs.stringify(data)).then((res)=>{
        res = res.data;
        if(!res.isSuc){
            // callError(key, res.des);
            callError.makeResult(res, true);
        } else {
            localStorage.setItem("email", data.email);
            localStorage.setItem("uid", res.datas);
            localStorage.setItem("token", res.des);
            // 开启后跳转安全认证页面 -- forgotPwdTwo
            // 未开启跳转至设置新密码页面 -- forgotPwdThree
            axios.post(DOMAIN_VIP+'/login/userStateCheck', qs.stringify(data)).then(res => {
                res = res.data;
                let canGoAuth = false;
                for(let i in res){
                    canGoAuth = res[i];
                    if(canGoAuth){
                        break;
                    }
                }
                if(canGoAuth){
                    browserHistory.push(formatURL('forgotPwdTwo'));
                } else {
                    browserHistory.push(formatURL('forgotPwdThree'));
                }
            });
        }
    });
};

export const fetchFgPwdAuth = () => dispatch => {
    // 0 google
    // 1 sms
    // 2 all
    return 2;
}

export const fetchFgPwdAuthCm = (data = {}, callError = () => {}, key = '') => dispatch => {

    let url = DOMAIN_VIP + (key === 'smscode' ? '/login/checkCode' : '/login/checkGoogle');

    data.code = data.gcode || data.smscode;

    axios.post(url, qs.stringify(data)).then((res) => {
        res = res.data;

        if(res.isSuc){
            browserHistory.push(formatURL('forgotPwdThree'));
        } else {
            callError().makeResult(res, true);
        }
    });
};

export const fetchFgPwdUp = (data = {}, callError = () => {}, key = '', formatMessage = () => {}) => dispatch => {


    dispatch(fetchPublicKey()).then(r => {
        r = r.data;

        if(r.isSuc){
            const K = r.datas.pubTag;
            encrypt.setPublicKey(K);
            data.password = encrypt.encrypt(data.password);
            data.pwd = data.password;
            delete data.confirmPwd;
            let uid = localStorage.getItem("uid") || '';
            data.userId = uid;
            data.pwdLevel = 1;

            axios.post(DOMAIN_VIP + '/ac/password_doreset', qs.stringify(data)).then((res) => {
                res = res.data;

                if(res.isSuc){
                    optPop(() => {
                        browserHistory.push(formatURL('login'));
                    }, res.des);

                } else {
                    callError().makeResult(res, true);
                }
            });

            localStorage.removeItem("token");
        } else {
            callError().makeResult(res, true);
        }
    });
};

export const fetchFstSecondCm = () => dispatch => {
    // axios
    dispatch(setSecondCm(false));
};

const initialSessionState = {
    loaded: false,
    user: null,
    pubTag: '',
    imgCode: URL_IMG_CODE,
    needImgCode: 0,
    baseUserInfo: {
        userName: '',
        authStatus: '',
        userSafeLevel: '',
        previousLogin: 0,
        loginIp: '',
        emailSatus: '',
        email: '',
        mobileStatus: '',
        mobile: '',
        mobilec:'',
        hasSafe: '',
        googleAuth: '',
        isLoaded:0
    },
    referee: '',
    certificationUrl: '',
    certificationStatue: 0,//判断二次认证状态
    secondCm: false,
    //remember sigin
    rememberSigin:{
        email:'',
        password:'',
        confirmPwd:'',
        code:'',
    },
    otcFlag: false
}

//user reducer
const reducer = (state = initialSessionState, action) => {

    switch(action.type) {
        case OTCFLAG:
            return Object.assign({}, state, {
                otcFlag: action.payload
            });
        case RECIEVE_PUBLICK_KEY:
            return Object.assign({}, state, {
                pubTag: action.payload
            });
        case FETCH_USERINFO:
            return Object.assign({}, state, {
                    loaded: true,
                    user: action.payload
                });
        case CHANGE_IMG_CODE:
            return Object.assign({}, state, {
                imgCode: action.payload
            });
        case SHOW_IMG_CODE:
            return Object.assign({}, state, {
                needImgCode: 1
            });
        case HIDE_IMG_CODE:
            return Object.assign({}, state, {
                needImgCode: 0,
            });
        case LOGIN:
            return Object.assign({}, state,{
                    user: action.payload.user
                });
        case LOGOUT:
            return Object.assign({}, state,{
                    user: null
                });
        case REQUEST_USER_BASE_INFO:
                return Object.assign({}, state, {
                    baseUserInfo: Object.assign({}, state.baseUserInfo, action.payload)
                });
        case REFEREE:
                return Object.assign({}, state, {referee: action.payload});
        case SECONDCERTIFICATION:
                let certificationUrl = '';
                switch(action.payload){
                    case 0:
                        certificationUrl = 'loginAuthGOne';break;
                    case 1:
                        certificationUrl = 'loginAuthSmsOne';break;
                    case 2:
                        certificationUrl = 'loginAuthGOne?all=1';break;
                    default:
                        certificationUrl = TRADE;break;
                }

                return Object.assign({}, state, {certificationUrl: formatURL(certificationUrl),certificationStatue:action.payload});
        case SECONDCM:
                return Object.assign({}, state, {secondCm: action.payload});
        case REMEMBER:
                console.log(action.payload)
                return Object.assign({}, state, {rememberSigin : action.payload});
        default:
            return state;
    }
}

export default reducer;