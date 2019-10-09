import cookie from 'js-cookie';
import confs from '../../conf';
import axios from 'nets';
import {COOKIE_LAN,DOMAIN_COOKIE,COOKIE_EXPIRED_DAYS,DEFAULT_LOCALE} from '../../conf/index'
//import config from '../../conf'

const SET_LANG = 'btcwinex/language/SET_LANG';


export const setLang =  (locale) => {
    //console.log(locale)
    //window.location.reload()
   let localeForCookie;
   switch(locale) {
       case 'zh-hant-hk':
           localeForCookie = "hk";
           break;
       case 'zh':
           localeForCookie = "cn";
           break;
       case 'en':
           localeForCookie = "en";
           break;
       case 'ko':
           localeForCookie = "kr";
            break;
       case 'ja':
           localeForCookie = "jp";
           break;
       default:
           localeForCookie = "en";
           locale = 'en';
   }
   
    axios.get(confs.BBAPI + "/setLan?lan="+localeForCookie);
    cookie.set(COOKIE_LAN, localeForCookie, {
        expires: COOKIE_EXPIRED_DAYS,
        domain: DOMAIN_COOKIE,
        path: '/'
    })
    let isReloadHref = window.location.href;
    if(isReloadHref.indexOf('orderDetail') > 0){
        window.location.reload()
    }
    
   return {
       type: SET_LANG,
       payload: locale
   };
}

const getLocale = () => {
    let localeCookie = cookie.get(COOKIE_LAN);
    let locale;
    switch(localeCookie) {
        case 'cn':
            locale = "zh";
            break;
        case 'en':
            locale = "en";
            break;
        case 'hk':
            locale = "zh-hant-hk";
            break;
        case 'kr':
            locale = "ko";
             break;
        case 'jp':
            locale = "ja";
            break;
        default:
            locale = DEFAULT_LOCALE;
    }
    return locale;
}
const initialLanguageState = {
    locale: getLocale()
}
const reducer = (state = initialLanguageState, action) => {
   switch(action.type) {
       case SET_LANG:
           return Object.assign({}, state, {
               locale: action.payload
           });
       default:
           return state;
   }
}

export default reducer;