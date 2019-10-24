import cookie from 'js-cookie';
import axios from 'axios';
import { DOMAIN_VIP,DEFAULT_LOCALE, COOKIE_LAN, DOMAIN_COOKIE, COOKIE_EXPIRED_DAYS } from '../../conf'
import { random } from 'node-forge';

const SET_LANG = 'btcwinex/language/SET_LANG';

export const setLang = (locale) => {
    console.log(locale)
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
  axios.get(DOMAIN_VIP+"/setLan?lan="+localeForCookie);
    cookie.set(COOKIE_LAN, localeForCookie, {
        expires: COOKIE_EXPIRED_DAYS,
        domain: DOMAIN_COOKIE,
        path: '/'
    })
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