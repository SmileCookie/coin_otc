import {cooperArr} from '../../conf/index'

//判断是否是数字
export const isFloat = (num) => {
    let reg = /^[0-9]*\.?[0-9]*$/g;
    return reg.test(num)
}
//判断邮箱
export const isEmail = (str) => {
    let regExp = new RegExp("^([a-z0-9A-Z]+[-|_|\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\.)+[a-zA-Z]{2,}$");
    return regExp.test(str);
}
//判断是否是手机
export const isMobiles = (mb = '') => {
    return /([0-9\s\-]{7,})(?:\s*(?:#|x\.?|ext\.?|extension)\s*(\d+))?$/.test(mb);
}; 

//判断钱包名称
export const isNickName = (name) => {
    let regExp = new RegExp('[\u4e00-\u9fa5_a-zA-Z0-9_]{0,60}');
    return regExp.test(name);
}

//判断官方URL
export const isURL = (url) => {
    // let regExp = /^([hH][tT]{2}[pP]:\/\/|[hH][tT]{2}[pP][sS]:\/\/)(([A-Za-z0-9-~]+)\.)+([A-Za-z0-9-~\/])+$/;
    return url
}

//判断中国人名
export const isName = (name) => {
    let regExp = new RegExp("^[a-zA-Z\u4e00-\u9fa5]+$");//英文和汉字
    return regExp.test(name);
}

//判断微信号
export const isWeChatNum = (num) => {
    let regExp = new RegExp('^[0-9a-zA-Z]*$');//数字和字母
    return regExp.test(num)
}
//判断特殊字符
export const isSpecial = (data) =>{
    let regExp =  RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？%+_]");
    return regExp.test(data)
}
//判断密码，至少包括字母数字特殊字符中任意2种
export const isSafePassWord = (passwrod) => {
    let regExp = new RegExp('^(?![0-9]+$)(?![a-zA-Z]+$)(?!([^(0-9a-zA-Z)]|[\(\)])+$)([^(0-9a-zA-Z)]|[\(\)]|[a-zA-Z]|[0-9]){8,}$');
    return regExp.test(passwrod);
}
// //判断输入框内容
// export const isTrue = (type) => {
//     switch(type){
//         case: 
//     }
// }

//获取合作列表
export const getCooperInfor = (types) =>{
    let _newCooper =[];
        // console.log(_cooper);
       for(let v in cooperArr){
            types.map((item,index) =>{
                if(item == v){
                    _newCooper.push(cooperArr[v])
                }
            })
       }
       
       console.log(_newCooper)
       return _newCooper
}

