import React from 'react';
import { IndexRoute, Route, IndexRedirect, Redirect ,hashHistory} from 'react-router';
import axios from 'axios';
import { DOMAIN_VIP } from './conf';
const qs = require('qs');
import App from './pages/app';
import { fetchUserinfo, getUserBaseInfo as callGetUserBaseInfo, logout, } from './redux/modules/session.js';
import {goTops} from './redux/modules/account.js'
import cookie from 'js-cookie';
import { browserHistory } from 'react-router';
import { formatURL, optPop } from './utils';
import { LOGINR, FGPWD, NOTS, NTRADE } from './conf/';

const Home = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/home').default);
    }, 'home');
};

const Trade  = (location, cb) => {
    require.ensure([], require => {
        if (cookie.get('zloginStatus') == 4) {
            logout();
        }
        if (!!window.ActiveXObject || "ActiveXObject" in window){
            // console.log('IE')
            cb(null, require('./pages/ie/ietrade').default);
        } else{
            // console.log('not IE')
            cb(null, require('./pages/trade/notietrade').default);
        }

    }, 'trade');
};

const Login = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/login').default);
    }, 'login');
};
const Signup = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/signup').default);
    }, 'signup');
};

//Money
const Money = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/money').default);
    }, 'money');
};

const Efl = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/money/efl').default);
    }, 'efl');
};

//ADMoney
const ADMoney = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/money/admoney').default);
    }, 'ADMoney');
};

//Cmoney
const Cmoney = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/cmoney').default);
    }, 'Cmoney');
};

const CmoneyTree = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/cmoneytree').default);
    }, 'CmoneyTree');
};

const Account = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/mange/account').default);
    }, 'account');
};
const GoogleOne = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/mange/googleOne').default);
    }, 'googleOne');
};
const GoogleTwo = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/mange/googleTwo').default);
    }, 'googleTwo');
};
const GoogleClo = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/mange/googleClo').default);
    }, 'googleClo');
};
const Grade = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/mange/grade').default);
    }, 'grade');
};
const LoginLog = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/mange/loginLog').default);
    }, 'loginLog');
};

const ContactUs = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/mange/contactUs').default);
    }, 'contactUs');
};
const AuthenFail = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/mange/authenFail').default);
    }, 'authenFail');
};
const AuthenSuccess = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/mange/authSuccess').default);
    }, 'authSuccess');
};
const AuthenOne = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/mange/authenOne').default);
    }, 'authenOne');
};
const AuthenTwo = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/mange/authenTwo').default);
    }, 'authenTwo');
};

const AuthenThree = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/mange/authenThree').default);
    }, 'authenThree');
};

const AuthenFour = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/mange/authenFour').default);
    }, 'authenFour');
};

const Authening = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/authening').default);
    }, 'authening');
};

const AuthenHacker = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/authenHacker').default);
    }, 'authenHacker');
};

const Asset = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/asset').default);
    }, 'manage');
};
const Manage = (location, cb) => {
    require.ensure([], require => {
        if (cookie.get('zloginStatus') == 4) {
            logout();
            window.location.href = LOGINR;
        } else {
            cb(null, require('./pages/manage').default);
        }

    }, 'manage');
};
const NotFound = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/notfound').default);
    }, 'notfound');
};
// const NotFound = (location, cb) => {
//     require.ensure([], require => {
//         cb(null, require('./pages/notfound').default);
//     }, 'network');
// };
const Balance = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/balances').default);
    }, 'balance');
};
const Currency = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/currency').default);
    }, 'currency');
};
const LCurrency = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/lcurrency').default);
    }, 'currency');
};
const FCurrency = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/fcurrency').default);
    }, 'fcurrency');
};
const LegalTender = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/legalTender').default);
    }, 'legalTender');
};
const Transfer = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/transfer').default);
    }, 'transfer');
}
const Deposits = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/deposits').default);
    }, 'deposits');
};
const Withdraw = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/withdraw').default);
    }, 'withdraw');
};
const WithdrawAdderss = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/withdraw/addressModal').default);
    }, 'addressModal');
};
const WithdrawDown = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/withdraw/withdrawForm').default);
    }, 'withdrawForm');
};
const ChargeDownHistory = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/chargeDownHistory').default);
    }, 'chargeDownHistory');
};
const FbChargeDownHistory = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/fbchargeDownHistory').default);
    }, 'fbchargeDownHistory');
};
const CapitalTabSwitch = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/capitalTabSwitch').default);
    }, 'capitalTabSwitch');
};

const CoinDownHistory = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/coinDownHistory').default);
    }, 'coinDownHistory');
};

const CurrentList = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/entrust/currentList').default);
    }, 'currentList');
};


const EntrustList = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/entrust/entrustList').default);
    }, 'entrustList');
};
const TransRecord = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/entrust/transrecord').default);
    }, 'transrecord');
};
const ReadService = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/auth/readService').default);
    }, 'ReadService');
};
const ForgotPwdThree = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/auth/forgotPwdThree').default);
    }, 'ForgotPwdThree');
};
const FinishedInfo = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/auth/finishedInfo').default);
    }, 'FinishedInfo');
};
const LoginAuthSmsOne = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/auth/loginAuthSmsOne').default);
    }, 'LoginAuthSmsOne');
};
const LoginAuthGOne = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/auth/loginAuthGOne').default);
    }, 'LoginAuthGOne');
};
const NotGCode = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/auth/notGCode').default);
    }, 'NotGCode');
};
const ForgotPwd = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/auth/forgotPwd').default);
    }, 'ForgotPwd');
};
const NotSmsGCode = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/auth/notSmsGCode').default);
    }, 'NotSmsGCode');
};
const AuthOne = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/auth/authOne').default);
    }, 'AuthOne');
};
const AuthThree = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/auth/authThree').default);
    }, 'AuthThree');
};
const AuthTwo = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/auth/authTwo').default);
    }, 'AuthTwo');
};
const FgPwdOne = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/auth/fgPwdOne').default);
    }, 'FgPwdOne');
};
const ForgotPwdTwo = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/auth/forgotPwdTwo').default);
    }, 'ForgotPwdTwo');
};
const FinishedReg = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/auth/finishedReg').default);
    }, 'FinishedReg');
};
const Active = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/auth/active').default);
    }, 'Active');
};
const LoginAuthRoute = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/auth/loginAuthRoute').default);
    }, 'LoginAuthRoute');
};
const isSessionLoaded = (globalState) => {
    return globalState.session && global.session.loaded;
};
const Test = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/auth/test').default);
    }, 'Test');
};
const MG = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/idx').default);
    }, 'MG');
};
const CkTel = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/ckTel').default);
    }, 'CkTel');
};
const SetMobile = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/setMobile').default);
    }, 'SetMobile');
};
const UpMobile = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/upMobile').default);
    }, 'UpMobile');
};
const DUpMobile = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/dUpMobile').default);
    }, 'DUpMobile');
};
const EbMobile = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/ebMobile').default);
    }, 'EbMobile');
};
const DEbUpMobile = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/dEbUpMobile').default);
    }, 'DEbUpMobile');
};
const CloseMobileCk = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/closeMobileCk').default);
    }, 'CloseMobileCk');
};
const SetGCkCode = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/setGCkCode').default);
    }, 'SetGCkCode');
};
const CloseG = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/closeG').default);
    }, 'CloseG');
};
const UpPwd = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/upPwd').default);
    }, 'UpPwd');
};
const AliPaySetting = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/aliPaySetting').default);
    }, 'AliPaySetting');
};
const CardPaySetting = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/cardPaySetting').default);
    }, 'CardPaySetting');
};
const MdPwd = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/mdPwd').default);
    }, 'MdPwd');
};
const SetPayPwd = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/setPayPwd').default);
    }, 'SetPayPwd');
};
const ResetPayPwd = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/resetPayPwd').default);
    }, 'ResetPayPwd');
};
const SPayPwd = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/sPayPwd').default);
    }, 'SPayPwd');
};
const EnMobile = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/enmobile').default);
    }, 'EnMobile');
};

const NewsPostCon = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/trade/newsPostCon').default);
    }, 'NewsPostCon');
};
const News = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/trade/news').default);
    }, 'News');
};
const NewsDetail = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/trade/news/newsdetail').default);
    }, 'NewsDetail');
};
const Vote = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/trade/vote').default);
    }, 'Vote');
};
const VoteDetail = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/trade/vote/votedetail').default);
    }, 'VoteDetail');
};
const Multi = (location, cb) => {
    require.ensure([], require => {
        if (!!window.ActiveXObject || "ActiveXObject" in window){
            // console.log('IE');
            cb(null, require('./pages/ie/iemultiTrade').default);
        } else {
            // console.log('not IE');
            cb(null, require('./pages/trade/dnd/multiTrade').default);
        }
    }, 'Multi');
};
const OtcCapitalRecord = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/otc/capital/capitalRecord').default);
    }, 'Capital');
};
const OtcCapitalFrozen = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/otc/capital/capitalFrozen').default);
    }, 'Capital');
};


// 上币
const CoinApply = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/gcoin/apply').default);
    }, 'CoinApply');
};
const Ca = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/gcoin/ca').default);
    }, 'Ca');
};
const GcStepOne = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/gcoin/stepOne').default);
    }, 'GcStepOne');
};

//Cmonerd
const Cmonerd = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/cmonerd').default);
    }, 'Cmonerd');
};

//Cmrd
const Cmrd = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/manage/cmrd').default);
    }, 'Cmrd');
};

/*推广 */
import Market from 'extension/pages/index'
import {userFinCenInfo} from "./pages/money/index.model";
const WalletCooperation  = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('extension/pages/market/walletCooperation').default);
    }, 'walletCooperation');
};
const CelebrityCooperation  = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('extension/pages/market/celebrityCooperation').default);
    }, 'celebrityCooperation');
};
const MediaCooperation  = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('extension/pages/market/mediaCooperation').default);
    }, 'mediaCooperation');
};
const InnerOne  = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('extension/pages/market/innerOne').default);
    }, 'innerOne');
};
const InnerTwo  = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('extension/pages/market/innerTwo').default);
    }, 'innerTwo');
};
const PartTime  = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('extension/pages/market/partTime').default);
    }, 'partTime');
};
const PartTimeSuccess  = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('extension/pages/market/partTimeSuccess').default);
    }, 'partTime');
};

const ChargeList = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/charge/index').default);
    }, 'partTime');
};
const Leve = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/charge/leve').default);
    }, 'partTime');
};
const WithdrawalCharge = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/charge/objects').default);
    }, 'partTime');
};
const TradeCharge = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/charge/tradeCharge').default);
    }, 'partTime');
};

const CancleUserInfor = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/mange/cancleUseConfro').default);
    }, 'cancleUserInfor');
};

const CancleUserInforIng = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/mange/cancleUserInforIng').default);
    }, 'cancleUserInforIng');
};

const CancleUserInforBack = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/mange/cancleUserInforBack').default);
    }, 'cancleUserInforIng');
};

// 判断用户 理财支付状态  1 已支付
const checkAuthPayStatus = (nextState, replace, cb) =>{
    axios.get(DOMAIN_VIP+'/manage/financial/userFinCenInfo').then((res)=>{
        const data = res.data.datas;

        const c = +data.authPayFlag;
        // const c = 2;

        ([2,3].includes(c)) ? cb() : window.location.href = '/bw/money';

    });
}

// 理财超级主节点
const Smn = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/money/smn').default);
    }, 'Smn');
};
const Smnlist = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/money/smnlist').default);
    }, 'Smnlist');
};
// 回本加成排名
const Pm = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/money/pm').default);
    }, 'Pm');
};


const createRoutes = (store) => {
    // 判断登录前 路由
    const checkWhereJumpFrom = (nextState) =>{
        let path = window.location.pathname,
            prevPath = document.referrer;
        // console.log(this.props.history)
        console.log(path)
        console.log(prevPath)
        if ( path.includes('/bw/login') && prevPath.includes('/bw')) {
            localStorage.removeItem('otcLoginBefore');
        }
    }

    const scrollTopEvent = (nextState, replace, cb) =>{
        //初始置顶滚动条
        store.dispatch(goTops(0))
        cb()
    }

    const requireLogin = (nextState, replace, cb) => {
        //初始置顶滚动条
        store.dispatch(goTops(0))
        let checkAuth = () => {
            const { session: { user } } = store.getState();
            if (!user) {
                window.location.href = LOGINR;
            }
            if (nextState.location.pathname.indexOf('/mg/') > -1) {
                // zloginStatus
                if (cookie.get('zloginStatus') == 4) {
                    logout();
                    window.location.href = LOGINR;
                }
            }
            cb();
        }
        if (!isSessionLoaded(store.getState)) {
            store.dispatch(fetchUserinfo());
        }
        checkAuth();
    };

    const isLogin = (nextState, replace, cb) => {
        let checkAuth = () => {
            const { session: { user } } = store.getState();
            if (user && user.loginStatus == 1) {
                window.location.href = NTRADE;
            }
            cb();
        }
        if (!isSessionLoaded(store.getState)) {
            store.dispatch(fetchUserinfo());
        }
        checkAuth();
    };

    const requireFst = (nextState, replace, cb) => {
        // email
        const email = localStorage.getItem("email");
        const uid = localStorage.getItem("uid");
        const path = nextState.location.pathname;

        if (!email || !uid) {
            // go to fst
            // console.log(nextState.location.pathname, email);
            if (path.indexOf('forgot') > -1) {
                window.location.href = FGPWD;
            } else if (path.indexOf('not') > -1 || path.indexOf('auth') > -1) {
                window.location.href = NOTS;
            }
        }
        cb();
    };

    const requireUserInfo = (nextState, replace, cb) => {
        if (sessionStorage.getItem('countryCode') && sessionStorage.getItem('countName')) {
            cb();
        } else {
            replace('/bw/manage/auth/authtype');
            cb();
        }
    };

    const getUserBaseInfo = () => {
        if (!store.getState().session.baseUserInfo.isLoaded) {
            store.dispatch(callGetUserBaseInfo());
        }
        // let path = window.location.pathname,
        //     prevPath = document.referrer;
        // // console.log(this.props.history)
        // console.log(path)
        // console.log(prevPath)
        // if (path.includes('login')) {
        //     setTimeout(()=>{
        //         document.title = 'haha';
        //     },0)
        // }else if(path.includes('trade')) {
        //     setTimeout(()=>{
        //         document.title = 'bababababa';
        //     },0)
        // }
    }


    const jmpurl = (key = '', cr = '') => {
        if (key !== cr) {
            try {
                browserHistory.replace(formatURL(key));
            } catch (e) {
                window.location.href = formatURL(key);
            }
        }
    }

    const UserAuthRoute = (n, rp, c) => {
        console.log('log.................')
        !((rp.location || n.location).query.go) ?
            axios.post(DOMAIN_VIP + '/manage/user', qs.stringify({})).then(r => {
                const { isLock: lk, isBlack: hk, authStatus: rs } = r.data.datas;

                /**
                 * 0 not auth -- authenOne
                 * 1 auth now -- authening
                 * 2 auth fail -- authenFail
                 * 3 hacker -- authenHacker
                 * 4 success -- authenSuccess
                 * 5 free
                 */
                r = {
                    flg: rs === 4 ? 0 : (rs === 5 ? 1 : (rs === 6 ? 4 : (rs === 7 && (hk | lk) ? 3 : 2))),
                }

                const { flg } = r;
                let currentPath = (rp.location || n.location).pathname.split('/');
                currentPath = currentPath[currentPath.length - 1];
                const [authenOne, authenFail, authenSuccess, authening, authenHacker] = ['authenOne', 'authenFail', 'account', 'authening', 'authenHacker'];

                switch (flg) {
                    case 0:
                        if (['authenTwo', 'authenThree', 'authenFour'].every(v => v !== currentPath)) {
                            jmpurl(authenOne, currentPath);
                        }
                        ; break;
                    case 1:
                        jmpurl(authening, currentPath);
                        break;
                    case 2:
                        jmpurl(authenFail, currentPath);
                        ; break;
                    case 3:
                        jmpurl(authenHacker, currentPath);
                        ; break;
                    case 4:
                        jmpurl(authenSuccess, currentPath);
                        ; break;
                }

                c();
            }) : c();
    }

    const checkTo = async (nextState, replace, cb) =>{
        let path  = nextState.location.pathname;
        let datas =  await axios.get(DOMAIN_VIP + '/manage/auth/authenticationJson').then(res =>{
                if(res.status == 200){
                    let data = res.data.datas;
                    return data
                }
            })
             let {storeStatus,storeType} = datas;
               if(storeStatus == 1 && storeType == 1){
                    browserHistory.replace(formatURL('cancleUserInfor'));
                }
                else if(storeStatus == 0 && storeType == 2){
                    path.includes('cancleUserInforIng') ? cb() : browserHistory.replace(formatURL('cancleUserInforIng'));
                }
                else if(storeStatus == 2 && storeType == 2){
                    path.includes('cancleUserInforBack') ? cb() : browserHistory.replace(formatURL('cancleUserInforBack'));
                }
                else{
                    browserHistory.replace(formatURL('account'));
                }
            // cb()

    }
    return (
        <React.Fragment>
            {/*推广的单独路由*/}
            <Route path="/market" component={Market}>
                <Route path="walletCooperation" getComponent={WalletCooperation} />
                <Route path="celebrityCooperation" getComponent={CelebrityCooperation} />
                <Route path="MediaCooperation" getComponent={MediaCooperation} />
                <Route path="PartTime" getComponent={PartTime} />
                <Route path="PartTimeSuccess" getComponent={PartTimeSuccess} />
            </Route>

            <Route path="/bw" component={App} onChange={getUserBaseInfo} onEnter={getUserBaseInfo}>
                <IndexRoute getComponent={Home} />
                {
                    true
                    &&
                    <Route  path="money" getComponent={Money} />
                }

                <Route path="efl" getComponent={Efl} />

                <Route path="smn(/:type)" getComponent={Smn} />
                <Route path="pm" getComponent={Pm} />
                <Route onEnter={requireLogin} path="smnlist" getComponent={Smnlist} />


                {
                    false
                    &&
                    <Route path="admoney" getComponent={ADMoney} />
                }
                <Route onEnter={requireLogin}>
                    <Route path="coinApply" getComponent={CoinApply} />
                    <Route path="ca" getComponent={Ca}>
                        <Route path="stepOne" getComponent={GcStepOne} />
                    </Route>
                    <Route path="manage" getComponent={Manage} >
                        <IndexRedirect to="account" />
                        <Route path="account" getComponent={CapitalTabSwitch} >
                            <IndexRedirect to="currency" />
                            {
                                true
                                &&
                                <Route  path="cmoney" getComponent={Cmoney} />
                            }
                            {
                                true
                                &&
                                <Route onEnter={checkAuthPayStatus} path="cmoneytree" getComponent={CmoneyTree} />
                            }
                            
                            <Route  path="cmonerd" getComponent={Cmonerd} />
                            <Route  path="cmrd" getComponent={Cmrd} />

                            <Route path="currency" getComponent={Currency} />
                            <Route path="fcurrency" getComponent={FCurrency} />
                            {
                                true
                                &&
                                <Route path="lcurrency" getComponent={LCurrency} />
                            }
                            <Route path="balance" getComponent={Balance} />
                            <Route path="legalTender" getComponent={LegalTender} />
                            <Route path="charge" getComponent={Deposits} />
                            <Route path="download">
                                <IndexRoute getComponent={Withdraw} />
                                <Route path="address" getComponent={WithdrawAdderss} />
                                <Route path="downloadDetails" getComponent={WithdrawDown} />
                            </Route>
                            <Route path="chargeDownHistory" getComponent={ChargeDownHistory} />
                            <Route path="fbChargeDownHistory(/:type/:fl/:reward)" getComponent={FbChargeDownHistory} />
                            {/* <Route path="queryDistribution" getComponent={DistriButionHistory} />  */}
                            <Route path='coinDownHistory' getComponent={CoinDownHistory} />
                            <Route path="capitalRecord" getComponent={OtcCapitalRecord} />
                            <Route path="capitalFrozen" getComponent={OtcCapitalFrozen} />
                        </Route>
                    </Route>
                </Route>

                {/* <Route onEnter={userPop}> */}

                <Route path="entrust">
                    <Route path="current" getComponent={CurrentList} />
                    <Route path="list" getComponent={EntrustList} />
                    <Route path="transrecord" getComponent={TransRecord} />
                </Route>
                {/* </Route> */}

                <Route getComponent={NewsPostCon}>

                    <Route path="trade/(:paramName)" getComponent={Trade} />
                    <Route path="multitrade" getComponent={Multi} />
                    <Route path="news">
                        <IndexRoute getComponent={News} />
                        <Route path="newsdetail(/:id)" getComponent={NewsDetail} />
                    </Route>
                    <Route path="announcements">
                        <IndexRoute getComponent={Vote} />
                        <Route path="announcementsDetail" getComponent={VoteDetail} />
                    </Route>
                </Route>


                <Redirect from="trade" to="trade/(:paramName)" />

                <Route onEnter={requireLogin}>
                    <Route path="loginAuthSmsOne" getComponent={LoginAuthSmsOne} />
                    <Route path="loginAuthGOne" getComponent={LoginAuthGOne} />
                    <Route path="loginAuthRoute" getComponent={LoginAuthRoute} />
                </Route>

                <Route onEnter={checkWhereJumpFrom} path="login" getComponent={Login} />
                <Route onEnter={isLogin} path="signup" getComponent={Signup} />
                {/* <Route path="signup" getComponent={Signup} /> */}

                <Route path="asset" getComponent={Asset} />
                <Route path="readService" getComponent={ReadService} />
                <Route path="finishedInfo" getComponent={FinishedInfo} />
                <Route path="notSmsGCode" getComponent={NotSmsGCode} />
                <Route path="forgotPwd" getComponent={ForgotPwd} />
                <Route path="fgPwdOne" getComponent={FgPwdOne} />
                <Route onEnter={requireFst}>
                    <Route path="forgotPwdTwo" getComponent={ForgotPwdTwo} />
                    <Route path="forgotPwdThree" getComponent={ForgotPwdThree} />
                    <Route path="authOne" getComponent={AuthOne} />
                    <Route path="authTwo" getComponent={AuthTwo} />
                    <Route path="authThree" getComponent={AuthThree} />
                    <Route path="notGCode" getComponent={NotGCode} />
                </Route>
                <Route path="finishedReg" getComponent={FinishedReg} />
                <Route path="active" getComponent={Active} />
                {/*test component*/}
                <Route path="tests" getComponent={Test} />
                {/*user center*/}
                <Route onEnter={requireLogin} path="mg" onChange={scrollTopEvent} getComponent={MG} id="账户">
                    <IndexRedirect to="account" />
                    <Route path="account" getComponent={Account} />
                    <Route path="cktel" getComponent={CkTel} id="手机验证" />
                    <Route path="setMobile" getComponent={SetMobile} id="设置手机号" />
                    <Route path="googleOne" getComponent={GoogleOne} id="谷歌验证" />
                    <Route path="googleTwo" getComponent={GoogleTwo} id="谷歌验证" />
                    <Route path="googleClo" getComponent={GoogleClo} id="关闭谷歌验证" />
                    <Route path="grade" getComponent={Grade} />
                    <Route path="loginLog" getComponent={LoginLog} />
                    <Route path="contactUs" getComponent={ContactUs} id="联系我们" />

                    <Route onEnter={UserAuthRoute} onChange={UserAuthRoute}>
                        <Route path="authenFail" getComponent={AuthenFail} id="身份认证失败" />
                        <Route path="authenSuccess" getComponent={AuthenSuccess} id="身份认证" />
                        <Route path="authenOne" getComponent={AuthenOne} id="开启身份认证" />
                        <Route path="authenTwo" getComponent={AuthenTwo} id="身份认证" />
                        <Route path="authenThree" getComponent={AuthenThree} id="身份认证" />
                        <Route path="authenFour" getComponent={AuthenFour} id="身份认证" />
                        <Route path="authening" getComponent={Authening} id="身份认证审核中" />
                        <Route path="authenHacker" getComponent={AuthenHacker} id="身份认证失败" />
                    </Route>

                    <Route path="upMobile" getComponent={UpMobile} id="修改手机号" />
                    <Route path="dUpMobile" getComponent={DUpMobile} id="修改手机号" />
                    <Route path="ebMobile" getComponent={EbMobile} id="手机验证" />
                    <Route path="dEbUpMobile" getComponent={DEbUpMobile} id="手机验证" />
                    <Route path="closeMobileCk" getComponent={CloseMobileCk} id="关闭手机验证" />
                    <Route path="setGCkCode" getComponent={SetGCkCode} id="谷歌验证" />
                    <Route path="closeG" getComponent={CloseG} id="关闭谷歌验证" />
                    <Route path="upPwd" getComponent={UpPwd} id="修改登录密码" />
                    <Route path="cancleUserInfor" getComponent={CancleUserInfor} id="取消认证" />
                    <Route  onEnter={checkTo}>
                        <Route path="cancleUserInforIng" getComponent={CancleUserInforIng} id="取消认证" />
                        <Route path="cancleUserInforBack" getComponent={CancleUserInforBack} id="取消认证" />
                    </Route>
                    <Route path="mdPwd" getComponent={MdPwd} id="修改登录密码" />
                    <Route path="setPayPwd" getComponent={SetPayPwd} id="设置资金密码" />
                    <Route path="resetPayPwd" getComponent={ResetPayPwd} id="重置资金密码" />
                    <Route path="sPayPwd" getComponent={SPayPwd} id="重置资金密码" />
                    <Route path="cardPaySetting" getComponent={CardPaySetting} id="收款设置" />
                    <Route path="aliPaySetting" getComponent={AliPaySetting} id="收款设置" />
                    <Route path="enMobile" getComponent={EnMobile} id="手机验证" />
                </Route>
                <Route path="chargeList" getComponent={ChargeList}>
                    <IndexRedirect to="leve" />
                    <Route path="leve" getComponent={Leve} id="帐户等级" />
                    <Route path="withdrawalCharge" getComponent={WithdrawalCharge} id="交易手续费"  />
                    <Route path="tradsCharge" getComponent={TradeCharge} id="提现手续费" />
                </Route>
                <Route path="*" getComponent={NotFound} />
            </Route>
        </React.Fragment>
    )
};

export default createRoutes;
