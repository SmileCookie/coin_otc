/**
 * user center
 * @author luchao.ding
 * @date 08-01-2018
 */
import axios from 'axios';
import { browserHistory } from 'react-router';
import { formatURL, optPop,GetRequest } from '../../utils'
const qs = require('qs');
import { ACCOUNT, DOMAIN_VIP, UPMOBILE } from '../../conf';
import { JSEncrypt } from 'jsencrypt';
import cookie from 'js-cookie';
//rsa encrypt
const encrypt = new JSEncrypt();

const PAYKEY='payKey';
/**
 * default state
 */

/**
 * action
 */

/**
 * do
 */
const doCkMobile = (data = {}, callError = () => {}, key = '') => dispatch => {
    console.log(data, callError, key, self._form);
    self._form.callError(key, '错误了')
};

const doSetMobile = (data = {}, callError = () => {}, key = '',type = 1) => dispatch => {
    
    axios.post(DOMAIN_VIP+'/manage/auth/setMobile', qs.stringify(data)).then(r => {
        r = r.data;
        r.type= type
        doRunRes(r);
    });

    // const rt = {
    //     isSuc: true,
    //     errors: [{
    //         key: 'mobile',
    //         msg: '手机号错误',
    //     },{
    //         key: 'smscode',
    //         msg: '短信验证码',
    //     }],
    // };

    //doRunRes(rt);
};

const doGetMobileInfo = () => async (dispatch, getState) => {
    const baseUserInfo = getState().session.baseUserInfo;
    return {
        code: '',
        mobile: baseUserInfo.mobile,
    }
};

const doAuthMobile = (data = {}, callError = () => {}, key = '') => dispatch => {
    axios.post(DOMAIN_VIP+'/manage/auth/updMobileCheck', qs.stringify(data)).then(r => {
         
        const rt = r.data;

        const fm = self._form;
        if(rt.isSuc){
            localStorage.setItem('token', rt.des);
            
            browserHistory.push(formatURL(UPMOBILE));
        } else {
            fm.makeResult(rt, true);
        }

    });
};

const doUpMobile = (data = {}, callError = () => {}, key = '',type = 2) => dispatch => {
    // console.log(data, callError, key);
    axios.post(DOMAIN_VIP+'/manage/auth/updMobile', qs.stringify(data)).then(r => {
        // const rt = {
        //     isSuc: false,
        //     errors: [{
        //         key: 'mobile',
        //         msg: '手机号错误',
        //     },{
        //         key: 'smscode',
        //         msg: '短信验证码',
        //     }],
        // };

        const rt = r.data;

        rt.rmToken = 1;
        rt.type = type;
        doRunRes(rt);

    });
};

const isCanJump = (data = {}) => async dispatch => {
    const r = await axios.post(DOMAIN_VIP+'/manage/auth/pwd/updVerify', qs.stringify(data));
    return rtData(r);
};

const rtData = (rt = {}) => {
    return rt.data;
}

const doEbAuthMobile = (data = {}, callError = () => {}, key = '') => dispatch => {
    axios.post(DOMAIN_VIP+'/manage/auth/openMobileVerify', qs.stringify(data)).then(r => {
        // const rt = {
        //     isSuc: true,
        //     errors: [{
        //         key: 'mobile',
        //         msg: '手机号错误',
        //     },{
        //         key: 'smscode',
        //         msg: '短信验证码',
        //     }],
        // };
        // doRunRes(rt);
        console.log(r)
        const rt = r.data;

        doRunRes(rt);
    });
};

const doRunRes = (rt = {}, successMsg = '设置成功' , url = '') => {
    const fm = self._form;
    let ffurl = new Object();
    let successMsg_1 =  '设置成功';
    let successMsg_2 = '修改成功';
    const {type} = rt;
    ffurl = GetRequest();
    // const resturl = ffurl['router']
    // const addressId = ffurl['addressId']
    // const queryCoin = ffurl['coint']
    let resturl = '';
    let addressId = '';
    let queryCoin = '';
    let pathname = ''
    try{
        pathname = window.location['pathname']
        resturl = ffurl['router']
        addressId = ffurl['addressId']
        queryCoin = ffurl['coint']
        // resturl = this.props.location.query.router;
        // addressId = this.props.location.query.addressId;
        // queryCoin = this.props.location.query.queryCoin;
        
    } catch(e){
        resturl = '';
        addressId = '';
        queryCoin = '';
        pathname = '';
    }

    if(rt.isSuc){
        rt.des && localStorage.setItem('token', rt.des);
        if(rt.rmToken){
            localStorage.removeItem('token');
        }
        if(resturl == 'widthdraw'&&pathname =="/bw/mg/setPayPwd"){
            browserHistory.push("/bw/manage/account/download/downloadDetails?coint="+queryCoin+'&addressId='+addressId);
        }else if(resturl == 'widthdraw'&&pathname =="/bw/mg/sPayPwd"){
            browserHistory.push("/bw/manage/account/download/downloadDetails?coint="+queryCoin+'&addressId='+addressId);
        }else if(resturl == 'widthdraw'&&pathname =="/bw/mg/resetPayPwd"){
            browserHistory.push("/bw/mg/sPayPwd?router=widthdraw&"+'coint='+queryCoin+'&addressId='+addressId);
        }else if(resturl == 'charge'&&pathname =="/bw/mg/setPayPwd"){
            setTimeout(()=>{
                browserHistory.push("/bw/manage/account/charge?coint="+ queryCoin);
            },100)
           
        }else if(pathname =="/bw/mg/dEbUpMobile"&&cookie.get("tz")!=undefined&&cookie.get("tz")!=""){
            optPop(() => {
                browserHistory.push(cookie.get("tz"));
                cookie.set("tz","")
            }, fm.intl.formatMessage({id: successMsg_1}));
           
        }else if(pathname =="/bw/mg/ebMobile"&&cookie.get("tz")!=undefined&&cookie.get("tz")!=""){
            optPop(() => {
                browserHistory.push(cookie.get("tz"));
                cookie.set("tz","")
            }, fm.intl.formatMessage({id: successMsg_1}));
           
        }else if(pathname =="/bw/mg/setGCkCode"&&cookie.get("tz")!=undefined&&cookie.get("tz")!=""){
            optPop(() => {
                browserHistory.push(cookie.get("tz"));
                cookie.set("tz","")
            }, fm.intl.formatMessage({id: successMsg_1}));
           
        }else if(!url && type == 1){
            optPop(() => {
                browserHistory.push(formatURL(ACCOUNT));
            }, fm.intl.formatMessage({id: successMsg_1}));
        }else if(!url && type == 2){
            optPop(() => {
                browserHistory.push(formatURL(ACCOUNT));
            }, fm.intl.formatMessage({id: successMsg_2}));
        }else {
            if(['Success','成功','success','성공','完了','変更'].some(v => rt.des.indexOf(v) > -1)){
                optPop(() => {
                    browserHistory.push(formatURL(ACCOUNT));
                }, rt.des);
            } else{
                browserHistory.push(formatURL(url));
            }
        }
    } else {
        fm.makeResult(rt, true);
    }
};

const doDEbUpMobile = (data = {}, callError = () => {}, key = '') => dispatch => {
    axios.post(DOMAIN_VIP+'/login/userState', qs.stringify(data)).then(r => {
        const rt = {
            isSuc: true,
            errors: [{
                key: 'mobile',
                msg: '手机号错误',
            },{
                key: 'smscode',
                msg: '短信验证码',
            }],
        };

        doRunRes(rt);
    });
};

const doCloseMobile = (data = {}, callError = () => {}, key = '') => dispatch => {
    axios.post(DOMAIN_VIP+'/manage/auth/closeMobileVerify', qs.stringify(data)).then(r => {
        const rt = r.data;
        doRunRes(rt, '关闭成功');
    });
};

const doGCkCode = (data = {}, callError = () => {}, key = '') => dispatch => {
    
    data[PAYKEY] = localStorage.getItem(PAYKEY);

    axios.post(DOMAIN_VIP+'/manage/auth/openGoogleVerify', qs.stringify(data)).then(r => {
        r = r.data;
        doRunRes(r);
    });

};
const doCloseG = (data = {}, callError = () => {}, key = '') => dispatch => {
    // axios.post(DOMAIN_VIP+'/login/userState', qs.stringify(data)).then(r => {
    //     const rt = {
    //         isSuc: true,
    //         errors: [{
    //             key: 'emailcode',
    //             msg: '邮箱验证码',
    //         },{
    //             key: 'gcode',
    //             msg: '谷歌验证码',
    //         }],
    //     };

    //     doRunRes(rt, '关闭成功');
    // });
    axios.post(DOMAIN_VIP+'/manage/auth/closeGoogleVerify', qs.stringify(data)).then(r => {
        const rt = r.data;
        doRunRes(rt, '关闭成功');
    });
};
const getPubKey = (data = {}) => {
    return axios.get(DOMAIN_VIP + "/login/getPubTag?t=" + new Date().getTime()).then(rp => {
        encrypt.setPublicKey(rp.data.datas.pubTag);
        const dic = ['gcode', 'emailcode', 'selectedCode', 'smscode', 'token'];
        for(let i in data){
            !dic.includes(i) && (data[i] = encrypt.encrypt(data[i]));
        }
        return data;
    });
};
const doMdPwd = (data = {}, callError = () => {}, key = '') => dispatch => {
    getPubKey(data).then(up => {
        axios.post(DOMAIN_VIP+'/manage/auth/pwd/logUpdate', qs.stringify(up)).then(r => {
            // const rt = {
            //     isSuc: false,
            //     errors: [{
            //         key: 'password',
            //         msg: '新密码',
            //     },{
            //         key: 'confirmPwd',
            //         msg: '确认新密码',
            //     }],
            // };
            const rt = r.data;
            rt.rmToken = 1;
            doRunRes(rt);
        });
    });

};
const doSetPayPwd = (data = {}, callError = () => {}, key = '') => dispatch => {

    getPubKey(data).then(up => {
        axios.post(DOMAIN_VIP+'/manage/auth/pwd/safeUpdate', qs.stringify({...up,type:18})).then(r => {
            // const rt = {
            //     isSuc: true,
            //     errors: [{
            //         key: 'lpwd',
            //         msg: '登录密码',
            //     },{
            //         key: 'password',
            //         msg: '新密码',
            //     },{
            //         key: 'confirmPwd',
            //         msg: '确认新密码',
            //     }],
            // };
            const rt = r.data;
            doRunRes(rt);
        });
    });
};
const doGetAuth = () => async dispatch => {
    return await axios.post(DOMAIN_VIP+'/login/userState', qs.stringify({})).then(res => {
        res = res.data;
        
        const rs  = res;
        
        let rt = -1;
        if(rs.isGoogleOpen && rs.isSmsOpen){
            rt = 2;
        } else if(rs.isGoogleOpen){
            rt = 0;
        } else if(rs.isSmsOpen){
            rt = 1;
        }
    
        return rt;
    });
}
const doAuthCm = (data = {}, callError = () => {}, key = '') => dispatch => {
    getPubKey(data).then(r => {axios.post(DOMAIN_VIP+'/manage/auth/pwd/updLoginPwdCheck', qs.stringify(r)).then(r => {
        // const rt = {
        //     isSuc: true,
        //     errors: [{
        //         key: 'password',
        //         msg: '原登录密码',
        //     },{
        //         key: 'emailcode',
        //         msg: '邮箱验证码',
        //     },{
        //         key: 'gcode',
        //         msg: '谷歌验证码',
        //     }],
        // };

        doRunRes(r.data, '', 'mdPwd');
    })});
};
const doAuthPayCm = (data = {}, callError = () => {}, key = '') => dispatch => {
    getPubKey(data).then(up => {
        axios.post(DOMAIN_VIP+'/manage/auth/pwd/updPayPwdCheck', qs.stringify(up)).then(r => {
            // const rt = {
            //     isSuc: true,
            //     errors: [{
            //         key: 'lpwd',
            //         msg: '登录密码',
            //     },{
            //         key: 'emailcode',
            //         msg: '邮箱验证码',
            //     },{
            //         key: 'gcode',
            //         msg: '谷歌验证码',
            //     },{
            //         key: 'smscode',
            //         msg: '短信验证码',
            //     }],
            // };

            doRunRes(r.data, undefined, 'sPayPwd');
        });
    });
};
const doRsPayPwd = (data = {}, callError = () => {}, key = '') => dispatch => {
    getPubKey(data).then(up => {
        axios.post(DOMAIN_VIP+'/manage/auth/pwd/safeUpdate', qs.stringify({...up})).then(r => {
            r.rmToken = 1;
            doRunRes(r.data, '修改成功，锁定帐户24小时。');
        });
    });
};
const doSelectModel = (data = {}) => async dispatch => {
    return await axios.post(DOMAIN_VIP+"/manage/auth/changeAuth",qs.stringify(data)).then(r => {
        return {
            opt: r.data.isSuc
        };
    });
};
const getModel = () => async dispatch => {
    return await axios.get(`${DOMAIN_VIP}/manage/auth/authInit`).then(r => {
        try{
            return r.data.datas.withdrawAddressAuthenType;
        }catch(e){
            return 0;
        }
    });
};
const getGoogleSInfo = () => async () => {
    return await axios.get(DOMAIN_VIP+'/manage/auth/getGoogleInfo').then(r => {
        const rt = r.data.datas;
        localStorage.setItem(PAYKEY, rt.key);
        return rt;
    });
    
};
// get enable google guide
const getGGuide = () => async dispatch => {
    
};
/**
 * reducer
 */



/**
 * export
 */
export { doCkMobile, 
         doSetMobile,
         doGetMobileInfo,
         doAuthMobile, 
         doUpMobile, 
         doEbAuthMobile, 
         doDEbUpMobile, 
         doCloseMobile,
         doGCkCode,
         doCloseG,
         doGetAuth,
         doAuthCm,
         doMdPwd,
         doSetPayPwd,
         doAuthPayCm,
         doRsPayPwd,
         doSelectModel,
         getModel,
         getGoogleSInfo,
         isCanJump, }; 