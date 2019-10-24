import {post,get} from '../utils/axios'
import {URL_IMG_CODE} from '../conf'
const BeasUrl = ''
//const BeasUrl = 'http://192.168.3.18:8089'

//验证码
// export const BeasUrlImg  = 'http://192.168.3.18:8089/imagecode/get-28-100-50?t=';
export const BeasUrlImg  = URL_IMG_CODE + "?t=";

//邀请页面
export function sendInvitation(params){
    return get(BeasUrl + '/register/invitation', params)
}

//钱包页面
export function walletCooperate(params){
    return get(BeasUrl + '/register/walletCooperate', params)
}

//兼职邀请
export function partTimeJob(params){
    return get(BeasUrl + '/register/partTimeJob', params)
}

//注册页面
export function emailReg(params){
    return get(BeasUrl + '/register/emailReg', params)
}


//处理数据
export const controlData = (data) =>{
    let _data = data.toString();
    console.log(_data)
    let _index = _data.indexOf('Error');
    let _newData;
    if(_index > 0){
        _newData = _data.substring(0,_index);
    }else{
        _newData = _data;
    }
    return _newData
 } 