/**
 * tools
 * @author luchao.ding
 */
import { WEBSOCKETURL,EOSTYPE,SECRET } from '../conf'
import axios from 'axios'
import BaseConfig from '../conf'
import { JSEncrypt } from 'jsencrypt'
import cookie from 'js-cookie'
const Reconnectingwebsocket  = require("reconnectingwebsocket")
const encrypt = new JSEncrypt();
const BigNumber = require('big.js');
import pako from 'pako';
import confs from 'conf';
const { defaultData } = confs;

/**
 * @function 获取类型
 * @param   [String] str 传入值
 * @returns [String] 返回类型
 */
const getType = (str = '') => {
    return Object.prototype.toString.call(str).slice(8, -1);
};
/**
 * @function 是否是波尔类型值
 * @param {any} ay
 * @returns [Boolean] 传入值是否是 Array类型
 */
const isArray = (ay = '') => {
    return 'Array' === getType(ay);
};
/**
 * @function 是否是对象
 * @param {any} obj
 * @returns [Boolean] 传入值是否是 Object类型
 */
const isObj = (obj = '') => {
    return 'Object' === getType(obj);
};

/**
 * @function 是否是空对象
 * @param {any} obj
 * @returns [Boolean] 传入值是否是空对象
 */
export const isEmptyObject = (obj) => {
    return typeof obj === 'undefined' ? 'undefined' :  obj === null ? 'null' : Object.keys(obj).length === 0;
};

/**
 * @function 判断一个对象的key是否全部存在于另一个对象，如果存在不匹配直接抛出异常。
 * @param {Object} keys 需要匹配的类
 * @param {Object} obj  被匹配的类
 * @throws not contain
 */
const containsObj = (keys = {}, obj = {}) => {
   if(isObj(keys) && isObj(obj)){
    const getKeys = Object.keys(keys);
    getKeys.forEach((v,i) => {
       if(void 0 === obj[v]){
        throw new Error("not contain")
       }
    })
   } else {
       throw new Error("not contain")
   }
}
// 显示pop信息
let _selfOptPopTimer = null;
export const optPop = (fn = () => {}, msg = '', opt = {timer: 3000}, showSuccess = false) => {
    const popObj = document.getElementById('popout'),
        txtObj = document.getElementById('poptxt');

    if(showSuccess){
        popObj.getElementsByTagName("svg")[0].style.display = 'none';
    } else {
        popObj.getElementsByTagName("svg")[0].removeAttribute("style");
    }

    popObj.style.display = 'block';
    txtObj.innerText = msg;
    clearTimeout(_selfOptPopTimer);
    _selfOptPopTimer = setTimeout(() => {
        popObj.style.display = 'none';
        fn();
    }, opt.timer);
};
// 交易全局吐司
export const trade_pop = ({fn = () => {},msg = '',style = 0,timer = 3000,} = {}) => {
    const rPopObj = document.getElementById('r-pop'),
        upRPopMsgObj = document.getElementById('up-r-pop-msg'),
        svg = rPopObj.getElementsByTagName("svg")[0];

    clearTimeout(trade_pop.timer);

    svg.style.display = style ? 'none' : 'inline-block';

    rPopObj.style.display = 'block';
    rPopObj.setAttribute('class', 'ac'+style);
    upRPopMsgObj.innerText = msg;

    trade_pop.timer = setTimeout(()  => {
        rPopObj.removeAttribute('style');
        fn();
    }, timer);
}
//判断邮箱
export const isEmail = (str) => {
    return  /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/.test(str);
}
//判断是否是数字
export const isFloat = (num) => {
    let reg = /^[0-9]*\.?[0-9]*$/g;
    return reg.test(num)
}
//截取小数位数  默认COIN_KEEP_POINT = 6
export const cutDigits = (num,few=COIN_KEEP_POINT) => {
    BigNumber.RM = 0;
    const nums = new BigNumber(num).toFixed(few);
    return nums;
}
//时间格式化
export const formatDate = (timestamp,format='yyyy-MM-dd hh:mm:ss') => {
    const lang = cookie.get("zlan")
    if (/(y+)/.test(format)) {
        format = lang=='en'?'MM-dd-yyyy':format
    }
    Date.prototype.format = function (format) {
        let o = {
            "M+": this.getMonth() + 1,
            "d+": this.getDate(),
            "h+": this.getHours(),
            "m+": this.getMinutes(),
            "s+": this.getSeconds(),
            "q+": Math.floor((this.getMonth() + 3) / 3),
            "S": this.getMilliseconds()
        }
        if (/(y+)/.test(format)) {
            format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        }
        for (let k in o) {
            if (new RegExp("(" + k + ")").test(format)) {
                format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
            }
        }
        return format;
    }

    return new Date(timestamp).format(format);
}

//委托时间格式化
export const TradeFormatDate = (timestamp,format='yyyy-MM-dd hh:mm:ss') => {
    if(new Date().getFullYear() == timestamp.getFullYear()){
        format = 'MM-dd hh:mm:ss'
    }else{
        const lang = cookie.get("zlan")
        if (/(y+)/.test(format)) {
            format = lang=='en'?'MM-dd-yyyy hh:mm:ss':format
        }
    }

    Date.prototype.format = function (format) {
        let o = {
            "M+": this.getMonth() + 1,
            "d+": this.getDate(),
            "h+": this.getHours(),
            "m+": this.getMinutes(),
            "s+": this.getSeconds(),
            "q+": Math.floor((this.getMonth() + 3) / 3),
            "S": this.getMilliseconds()
        }
        if (/(y+)/.test(format)) {
            format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        }
        for (let k in o) {
            if (new RegExp("(" + k + ")").test(format)) {
                format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
            }
        }
        return format;
    }

    return new Date(timestamp).format(format);
}


//委托时间格式化
export const RepreFormatDate = (timestamp,format='yyyy-MM-dd') => {
    // if(new Date().getFullYear() == timestamp.getFullYear()){
    //     format = 'MM-dd hh:mm:ss'
    // }else{
    const lang = cookie.get("zlan")
    if (/(y+)/.test(format)) {
        format = lang=='en'?'MM-dd-yyyy':format
    }
    // }

    Date.prototype.format = function (format) {
        let o = {
            "M+": this.getMonth() + 1,
            "d+": this.getDate(),
            "h+": this.getHours(),
            "m+": this.getMinutes(),
            "s+": this.getSeconds(),
            "q+": Math.floor((this.getMonth() + 3) / 3),
            "S": this.getMilliseconds()
        }
        if (/(y+)/.test(format)) {
            format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        }
        for (let k in o) {
            if (new RegExp("(" + k + ")").test(format)) {
                format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
            }
        }
        return format;
    }

    return new Date(timestamp).format(format);
}


// 手机号添加*
export const mobileFormat = (mobile = '') => {

    // mobile && (mobile = mobile.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2'));
    mobile && (mobile = mobile.replace(/\d{7}(\d{4})/, '****$1'));

    return mobile;
}
// 隐藏部分
export const hideString = (str = '', sflg = 3, rp = '*', rt = 3) => {
    let reg = new RegExp("(.{"+sflg+"}).*?(.{"+sflg+"})\$");

    return str.replace(reg, '$1'+rp.repeat(rt)+'$2');
};
// 邮箱添加*
export const emailFormat = (email = '') => {
    email && (email = email.replace(/(.{4}).*?(@+)/, '$1****$2'));
    return email;
}
// 根据现在语言翻译可以跳转到帮助中心的语言
export const languageFormat = (fm = '') => {
    let rt = '';

    switch(fm){
        case 'en':rt='en-us';break;
        case 'zh':rt='zh-cn';break;
        case 'zh-hant-hk':rt='zh-tw';break;
    }
    return rt;
}
//判断是否是手机
export const isMobiles = (mb = '') => {
    return /([0-9\s\-]{7,})(?:\s*(?:#|x\.?|ext\.?|extension)\s*(\d+))?$/.test(mb);
};
export const isMobile = () => {
    let sUserAgent = navigator.userAgent.toLowerCase();
    let bIsTv = sUserAgent.match(/tv/i) == "tv";
    let bIsIpad = sUserAgent.match(/pad/i) == "pad";
    let bIsIphoneOs = sUserAgent.match(/iphone os/i) == "iphone os";
    let bIsMidp = sUserAgent.match(/midp/i) == "midp";
    let bIsUc7 = sUserAgent.match(/rv:1.2.3.4/i) == "rv:1.2.3.4";
    let bIsUc = sUserAgent.match(/ucweb/i) == "ucweb";
    let bIsAndroid = sUserAgent.match(/android/i) == "android";
    let bIsCE = sUserAgent.match(/windows ce/i) == "windows ce";
    let bIsWM = sUserAgent.match(/windows mobile/i) == "windows mobile";
    if(bIsTv || bIsIpad){
        return false;
    }
    else if (bIsIphoneOs || bIsMidp || bIsUc7 || bIsUc || bIsAndroid || bIsCE || bIsWM) {
        return true;
    } else {
        return false;
    }
};
//数字千位分割符 “10,000,000”格式化
export const separator = (num)=>{
    let result = [ ], counter = 0, reduce = 0,leaveObj = '',_num;
    if(num.toString().indexOf('.') > 0){
        _num = num.slice(0 , num.toString().indexOf('.'))
         let index = num.toString().lastIndexOf(".");
         leaveObj=num.substring(index,num.toString().length);
          leaveObj  = leaveObj.split(",")
    }
    //console.log(leaveObj);

    _num = (_num || 0).toString().split('');
    for (let i = _num.length - 1 ; i >= 0; i--) {
        counter++;
        result.unshift(_num[i]);
        if (!(counter % 3) && i != 0) { result.unshift(','); }
    }
    result = result.concat(leaveObj)
    return result.join('');
}
//是否包含汉字
export const hasChinese = (str) => {
    var regExp = /[\u4E00-\u9FA5\uF900-\uFA2D]/;
    return regExp.test(str);
}
// 是否包含特殊字符
export const hasOther = (str) => {
    var regExp = new RegExp("[`~!@#$%^&*()=|{}':;',\\[\\].<>/?~！@#￥……％&*（）——|{}【】‘；：”“'。，、？]");
    return regExp.test(str);
};
// 验证护照号
export const isPassport = (str) => {
    var regExp = new RegExp("^[a-zA-Z0-9]{1,20}$");
    return regExp.test(str);
}
// 是否包含英文字母
export const hasLetter = (str) => {
    var regExp = new RegExp("[a-zA-Z]");
    return regExp.test(str);
};
/*验证身份证号码*/
export function isIdCardNo(num) {  //验证身份证号码
    num = num.toUpperCase();           //身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X。
    if (!(/(^\d{15}$)|(^\d{17}([0-9]|X)$)/.test(num))) {
        return false;
    }
    //验证前2位，城市符合
    var aCity = { 11: "北京", 12: "天津", 13: "河北", 14: "山西", 15: "内蒙古", 21: "辽宁", 22: "吉林", 23: "黑龙江 ", 31: "上海", 32: "江苏", 33: "浙江", 34: "安徽", 35: "福建", 36: "江西", 37: "山东", 41: "河南", 42: "湖北", 43: "湖南", 44: "广东", 45: "广西", 46: "海南", 50: "重庆", 51: "四川", 52: "贵州", 53: "云南", 54: "西藏", 61: "陕西", 62: "甘肃", 63: "青海", 64: "宁夏", 65: "新疆", 71: "台湾", 81: "香港", 82: "澳门", 91: "国外" };
    if (aCity[parseInt(num.substr(0, 2))] == null) {
        return false;
    }
    //下面分别分析出生日期和校验位
    var len, re; len = num.length;
    if (len == 15) {
        re = new RegExp(/^(\d{6})(\d{2})(\d{2})(\d{2})(\d{3})$/);
        var arrSplit = num.match(re);  //检查生日日期是否正确
        var dtmBirth = new Date('19' + arrSplit[2] + '/' + arrSplit[3] + '/' + arrSplit[4]);
        var bGoodDay; bGoodDay = (dtmBirth.getYear() == Number(arrSplit[2])) && ((dtmBirth.getMonth() + 1) == Number(arrSplit[3])) && (dtmBirth.getDate() == Number(arrSplit[4]));
        if (!bGoodDay) {
            return false;
        } else { //将15位身份证转成18位 //校验位按照ISO 7064:1983.MOD 11-2的规定生成，X可以认为是数字10。
            var arrInt = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2);
            var arrCh = new Array('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2');
            var nTemp = 0, i;
            num = num.substr(0, 6) + '19' + num.substr(6, num.length - 6);
            for (i = 0; i < 17; i++) {
                nTemp += num.substr(i, 1) * arrInt[i];
            }
            num += arrCh[nTemp % 11];
            return true;
        }
    }
    if (len == 18) {
        re = new RegExp(/^(\d{6})(\d{4})(\d{2})(\d{2})(\d{3})([0-9]|X)$/);
        var arrSplit = num.match(re);  //检查生日日期是否正确
        var dtmBirth = new Date(arrSplit[2] + "/" + arrSplit[3] + "/" + arrSplit[4]);
        var bGoodDay; bGoodDay = (dtmBirth.getFullYear() == Number(arrSplit[2])) && ((dtmBirth.getMonth() + 1) == Number(arrSplit[3])) && (dtmBirth.getDate() == Number(arrSplit[4]));
        if (!bGoodDay) {
            return false;
        }
        else { //检验18位身份证的校验码是否正确。 //校验位按照ISO 7064:1983.MOD 11-2的规定生成，X可以认为是数字10。
            var valnum;
            var arrInt = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2);
            var arrCh = new Array('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2');
            var nTemp = 0, i;
            for (i = 0; i < 17; i++) {
                nTemp += num.substr(i, 1) * arrInt[i];
            }
            valnum = arrCh[nTemp % 11];
            if (valnum != num.substr(17, 1)) {
                return false;
            }
            return true;
        }
    }
    return false;
}

// 国外的身份证验证
// ticket 465
export const smpIsIdCardNo = (num = '') => {
    return /^[\dA-Za-z]{1,20}$/.test(num);
};

// 验证指定表单数据是否为空，或者指定规则是否匹配
export const checkForm = (source = {}, dictionaries = {}, language = '', rules = {}, msgObj = null, status = {}) => {

    let rt = 1;
    // console.log(source, dictionaries, language, rules, msgObj, msg);
    const P = 'passportNumber';
    const ID = 'cardId';
    const HP = ['handheldPassport', 'passport', 'frontalImg', 'backImg', 'loadImg'];
    const NT = [P, ID];

    Object.keys(dictionaries).forEach((v, k) => {
        if(!HP.includes(v)){
            dictionaries[v] = dictionaries[v].replace(/:|：|\./, '');
            if(language == 'en'){
                if(NT.includes(v) && dictionaries[v].indexOf("The") == -1){
                    dictionaries[v] = 'The ' + dictionaries[v];
                }

                let sp = ' ';
                let ay = dictionaries[v].split(sp);
                dictionaries[v] = ay.shift() + sp + (ID == v ? (ay.shift().toUpperCase() + sp) : '') + (ay.length > 0 ? ay.join(sp).toLowerCase() + sp : '');
            }
        }
    });

    out:for(let k in source){
        if(!source[k]){
            showMsg(HP.includes(k) ?  dictionaries[k] : dictionaries[k] + status.empty);
            break;
        }else if(rules[k]){
            for(let i in rules[k]){
                if(!rules[k][i](source[k])){
                    showMsg((NT.includes(k) ? dictionaries['_' + k] : dictionaries[k]) + status.error);
                    break out;
                }
            }
        } else if('endDate' == k){
            if(source['startDate'] > source['endDate']){
                showMsg(dictionaries['startDateEndDate']);
                break;
            }
        }
    }

    function showMsg(msg){
        rt = 0;
        if(msgObj){
            msgObj({
                message: msg,
                kind: 'info',
                dismissAfter: status.timer
            })
        } else {
            optPop(() => {}, msg);
        }
    }

    return rt;
}
// email隐藏头几位
export const hideStr = (str = '', num = 3, rp = '***') => {
    let reg = new RegExp("(.{"+num+"}).*?(@.*)");
    let rt = '';

    try{
        let spstr = str.split('@')[0];
        if(spstr.length > num){
            rt = str.replace(reg, '$1' + rp + '$2');
        } else {
            rt = str;
        }
    } catch(e){
        rt = str;
    }

    return rt;
};
// 格式化url
export const formatURL = (url = '') => {
    let rt = '';
    let sp = '/';

    if(url.indexOf(sp) != 0){
        let pathname = window.location.pathname,
            len = pathname.length - 1;

        pathname.lastIndexOf(sp) == len && (pathname = pathname.substring(0, len));
        rt = pathname.split(sp);
        rt.pop();
        rt.push(url);
        rt = rt.join(sp);
        // console.log(rt);
    }else{
        rt = BASE_UIR + url.substring(1);
    }

    return rt;
};
//截取Url里面的参数
export const GetRequest = () =>{
    var url = location.search; //获取url中"?"符后的字串
    var theRequest = new Object();
    if (url.indexOf("?") != -1) {
        var str = url.substr(1);
        //alert(str);
        let strs = str.split("&");
        for (var i = 0; i < strs.length; i++) {
            theRequest[strs[i].split("=")[0]] = decodeURI(strs[i].split("=")[1]);//获取中文参数转码<span style="font-family: Arial, Helvetica, sans-serif;">decodeURI</span>，（unescape只针对数字，中文乱码)
        }
    }
    return theRequest;
}

//js 实现简单切换
export const radioSwitch = (classname) => {
    let radioLabel = document.querySelectorAll('.'+classname)
    for(let i=0 ; i<radioLabel.length ; i++){
        radioLabel[i].index = i;
        radioLabel[i].onclick = function( ){
            for(var i =0; i < radioLabel.length; i++){
                radioLabel[i].className = classname;
            }
            this.className = classname + " active";
        }
    }
}

//websocket
export const requsetWebsocket = (params,cb,nc) => {
    let ws = nc.ws = new Reconnectingwebsocket(WEBSOCKETURL);
    // 建立 web socket 连接成功触发事件${this.props.params.paramName}
    ws.onopen = function () {
        ws.send(JSON.stringify(params));
        // nc.otcSetTime = setInterval(() =>{
        //     //console.log(params);
        //     ws.send(params);
        // },20000)
    };

    ws.onmessage = function (evt) {
        // let result = JSON.parse(evt.data);
        // cb&&cb(result)
        //console.log(evt);
        if(evt.data instanceof Blob){
            var reader = new FileReader();
            reader.onload = () => {
                let result = JSON.parse(pako.inflate(reader.result, {to:'string'}));
                cb&&cb(result)
            }
            reader.readAsArrayBuffer(evt.data);
        }
    };

    ws.onclose = function () {
    };
}

//在线人数 转换
export const exChangeTradeper = (num,type) => {
    BigNumber.RM = 0
    if(num >= 1000000&&!type){
        const numStr = new BigNumber(num).div(1000000).toFixed(2)
        return {num:numStr,unit:'M'}
    }else if(num >= 10000&&type){
        const numStr = new BigNumber(num).div(10000).toFixed(2)
        return {num:numStr,unit:'W'}
    }else if(num >= 1000){
        const numStr = new BigNumber(num).div(1000).toFixed(2)
        return {num:numStr,unit:'K'}
    }
    return {num:num,unit:''}
}

// 位数转换
export const numFm = (num) => {
    BigNumber.RM = 0;

    let nums = new BigNumber(num).toFixed(2);
    let unit = '';

    /*if(100000000 <= num){
        nums = new BigNumber(num).div(100000000).toFixed(2);
        unit = 'B';
    } else */
    if(1000000 <= num){
        nums = new BigNumber(num).div(1000000).toFixed(2);
        unit = 'M';
    } else if(1000 <= num){
        nums = new BigNumber(num).div(1000).toFixed(2);
        unit = 'K';
    }

    return {num:nums,unit:unit}
}

//输入整数
export const inputInteger = (e) => {
    if((e.keyCode>=48 && e.keyCode<=57)||(e.keyCode>=96 && e.keyCode<=105)){
        return true;
    }
    return false;
}
//格式化国际化语言
export const formateLocale = (lang) => {
    if(lang == 'zh'){
        return 'zh-cn'
    }else if(lang == 'zh-hant-hk'){
        return 'zh-hk'
    }
    return lang
}


// 是否是IE浏览器
export const isIE = () => {
    return !!window.ActiveXObject || "ActiveXObject" in window
}

export const isFunc = (func = () => {}) => {
    return 'Function' === getType(func);
}
// 获取某一范围内的随机数并且是正整数
export const getRandom = (num = 0) => {
    let span = num + 1;
    let result = parseInt(Math.random() * span, 10);
    return result;
};
//非法跳转
export const optJump = (jumpLink, msg = '',btnText='确认') => {
    const popObj = document.getElementById('popoJump'),
        txtObj = document.getElementById('poptxts'),
        jumps  = document.getElementById('JumpLink'),
        confrimJump = document.getElementById('confrimJump');


    popObj.style.display = 'block';
    txtObj.innerText = msg;
    confrimJump.innerText = btnText
    jumps.setAttribute('href',jumpLink);

};

//时间转换
export const timeChange = (value) =>{
    var secondTime = parseInt(value);// 秒
            var minuteTime = 0;// 分
            var hourTime = 0;// 小时
            var _obj = {}
            if(secondTime > 60) {//如果秒数大于60，将秒数转换成整数
                //获取分钟，除以60取整数，得到整数分钟
                minuteTime = parseInt(secondTime / 60);
                //获取秒数，秒数取佘，得到整数秒数
                secondTime = parseInt(secondTime % 60);
                //如果分钟大于60，将分钟转换成小时
                if(minuteTime > 60) {
                    //获取小时，获取分钟除以60，得到整数小时
                    hourTime = parseInt(minuteTime / 60);
                    //获取小时后取佘的分，获取分钟除以60取佘的分
                    minuteTime = parseInt(minuteTime % 60);
                }
            }
            secondTime = parseInt(secondTime);

            if(minuteTime > 0) {
                minuteTime = parseInt(minuteTime) || ''
            }
            if(hourTime > 0) {
                hourTime = parseInt(hourTime) || ''
            }
            _obj = {
                hours:hourTime,
                min:minuteTime,
                sec:secondTime
            }
        return _obj;
}

//生成错误信息
export const creatErrorMsg = (arr =[],_this) =>{
    let erro = {};
    arr.map((item,index) =>{
            erro[item] = {
                value: '',
                errStatus: false,
                errMsg: ''
            }
        })
    //return _obj
    let _obj = Object.assign({},_this.state,{
        ...erro
    })
    _this.setState({
        ..._obj
    })
}

//清除错误信息
export const clearRrroMsg = (key,arr = [],_this) =>{
    arr.map((item,index) =>{
        if(item == key){
            _this.setState({
                [item]:Object.assign({},_this.state[item],{
                    errMsg:''
                })
            })
        }

    })
}


// 交易输入框规则校验
export const checkNumber = (value, unit, type)=> {
    if (value != "") {
        if (isNumber(value)) {
            let valueStr = value + "";
            if (valueStr.indexOf(".") != -1) {
                let newStr,
                    intStr = valueStr.split(".")[0] + "",
                    floatStr = valueStr.split(".")[1] + "";
                if (floatStr.split("").length > unit) {
                    newStr = intStr + "." + floatStr.substr(0, unit);
                    value = newStr;
                }
            }
        }else{
            if (isNaN(parseFloat(value))) {
                value = ''
            }else {
                value = parseFloat(value)
            }
        }
    }
    return value
}
// 判断字符串是否为数字
export const isNumber = (val) => {
    let re = /^[0-9]+\.?[0-9]*$/;  //判断正整数 /^[1-9]+\.?[0-9]*]*$/
    if (!re.test(val)) {
        return false
    }
    return true
}
// 截取第一个字符
export const getFirstStr = (val) => {
    let str = "";
    if(val) {
        str = val.substring(0,1).toUpperCase();
    }
    return str
};

//获取交易秘钥
export const fetchPublicKey =  () =>{
        encrypt.setPublicKey(SECRET);
        return encrypt
}

// 交易里法币向上截取对应位数

 export const getCurrency = (val, price, unit)=>{
    let finalVal = "";
     if (val != "" && price != "") {
         let valueStr = val * price + "";
         if (valueStr.indexOf(".") != -1) {
             let newStr,
                 intStr = valueStr.split(".")[0] + "",
                 floatStr = valueStr.split(".")[1] + "";
             if (floatStr.split("").length > unit && (floatStr.substr(unit, 1)) != 0 ) {
                 newStr = intStr + "." + floatStr.substr(0, unit);
                 let littleVal = new BigNumber(1).div(Math.pow(10, unit)).toFixed(unit);
                 finalVal = new BigNumber(newStr).plus(littleVal).toFixed(unit);
             }else {
                 finalVal = new BigNumber(valueStr).toFixed(unit);
             }
         }else {
             finalVal = new BigNumber(valueStr).toFixed(unit);
         }
     }
     return finalVal;
 };

// 根据选择的Key获取value
export const getSelectName = (list, key)=> {
    let name = "";
    if(isArray(list) && list.length > 0) {
        for(let i = 0; i < list.length; i++) {
            if(key = list[i].fundsType) {
                name = list[i].coinName;
                break;
            }
        }
    }
    return name;
};

//获取币种截断保留小数位
export const getCoinNum = (list=[],market = '') =>{
    let _obj = {};
    if(list.length >0){
        for(let i =0 ; i<list.length ; i++){
            if(market == list[i].name.slice(0,-4)){
                _obj = {marketL:list[i].coinBixDian,payL:list[i].legalBixDian}
                return _obj
            }
        }
    }
}
//截取小数,不四舍五入 不足补0
export const formatDecimal = (num, decimal) => {
    num = num.toString();
    var index = num.indexOf('.');
    if (index !== -1) {
        num = num.substring(0, decimal + index + 1)
    } else {
        num = num.substring(0)
    }
    return parseFloat(num).toFixed(decimal)
}
export const formatDecimalNoZero = (num, decimal) => {
    num = num.toString();
    var index = num.indexOf('.');
    if (index !== -1) {
        num = num.substring(0, decimal + index + 1)
    } else {
        num = num.substring(0)
    }
    return num
}
// 超过位数，进1， 该方法暂时只适用decimal为2
export const add_one = (num, decimal) => {
    num = num.toString();
    if (num.indexOf('.') > -1) { // 11.0025, 11.0925, 11.9925, 11.1125, 11.9825
        let head = num.split('.')[0]
        let footer = num.split('.')[1]
        if (footer.length > decimal) { // 小数位数大于 decimal
            footer = footer.slice(0, decimal)
            if (footer[0] === '0' && footer[1] !== '9') { // 00 -- 08
                let footer_1 = parseInt(footer[1])
                footer_1++
                footer = '0' + footer_1
            }else if (footer === '09') { // 09
                footer = '10'
            }else if (footer === '99') { // 99
                head = parseInt(head)
                head ++
                footer = '00'
            }else { // 11-98
                footer = parseInt(footer)
                footer ++
            }
            num = head + '.' + footer
        }
    }
    return num
}


/**
 * @desc 函数防抖 (触发事件后在 n 秒内函数只能执行一次，如果在 n 秒内又触发了事件，则会重新计算函数执行时间)
 * @param { function } func 函数
 * @param { number } delay 延迟执行毫秒数
 * @param { boolean } immediate true 表立即执行，false 表非立即执行
 */
export const debounce = (func, delay, immediate) => {
    let timeout = null;

    return function() {
        const context = this;
        const args = arguments;

        if (timeout) clearTimeout(timeout);

        if (immediate) {
            const callNow = !timeout;
            // 一定时间后清空定时器，即一定时间内定时器存在，callNow 为 false
            timeout = setTimeout(() => {
                timeout = null;
            }, delay);
            if (callNow) func.apply(context, args);
        } else {
            timeout = setTimeout(function() {
                func.apply(context, args);
            }, delay);
        }
    };
};

/**
 * @desc 验证值是否为空
 * @param { any } val 要验证的值
 * @return { boolean }
 */
export const isValEmpty = (val) => {
  return val === defaultData ||val === '' || typeof val === 'undefined' || val === null;
};

export { getType, isArray, isObj, containsObj };
