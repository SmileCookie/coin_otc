import cookie from 'js-cookie';
import axios from 'axios';
import { DOMAIN_VIP,DOMAIN_TRANS,DEFAULT_MONEY,CONF_MONEY, COOKIE_MONEY, DOMAIN_COOKIE, COOKIE_EXPIRED_DAYS } from '../../conf'

const SET_MONEY = 'btcwinex/language/SET_MONEY';
const SET_RATE = 'btcwinex/language/SET_RATE';
export const setMoney = (name) => {
    cookie.set(COOKIE_MONEY, name, {
        expires: COOKIE_EXPIRED_DAYS,
        domain: DOMAIN_COOKIE,
        path: '/'
    })
    let data = {}
    data.name=name;
    data.logo=CONF_MONEY[name];
    return {
        type: SET_MONEY,
        payload: data
    };
   
}
export const fetchRate = (market) => {
    return dispatch => {
        axios.get(DOMAIN_TRANS + '/getExchangeRate')
            .then(res => {
                try{
                let data = eval(res).data.datas;
                // console.log(data);
                dispatch(recieveRate(data));
                }catch(e){}
            });
    };
}
export const fetchDefaultMoney = () => {
    return dispatch => {
        axios.get(DOMAIN_VIP + '/setCurrency')
            .then(res => {
                try{
                let data = eval(res).data;
                // console.log(data);
                if(data.isSuc){
                    dispatch(setMoney(data.datas));
                }
            }catch(e){}
            });
    };
}
export const recieveRate = (data) => {
    return {
        type: SET_RATE,
        payload: data
    }
}
const getLocale = () => {
    let localeCookie = cookie.get(COOKIE_MONEY);
    let locale=localeCookie?localeCookie:DEFAULT_MONEY;
    let data = {}
    data.name=locale;
    data.logo=CONF_MONEY[locale];
    return data;
}
const initialLanguageState = {
    locale:getLocale(),
    rate:{}
}
const reducer = (state = initialLanguageState, action) => {
    switch(action.type) {
        case SET_MONEY:
            return Object.assign({}, state, {
                locale: action.payload
            });
        case SET_RATE:
            return Object.assign({}, state, {
                rate: action.payload
            });
        default:
            return state;
    }
}

export default reducer;