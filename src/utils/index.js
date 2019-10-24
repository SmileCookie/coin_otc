const BigNumber = require('big.js');
import cookie from 'js-cookie';
import {COIN_KEEP_POINT, COOKIE_LAN, BASE_UIR,WEBSOCKETURL,EOSTYPE,DOMAIN_VIP, OTCWEBSOCKETURL} from '../conf'
const Reconnectingwebsocket  = require("reconnectingwebsocket")
import pako from 'pako';
import axios  from 'axios';

export const formatMarkets = (newMarketsData, oldMarketsData) => {
    let record = [] ;
    let i=0;
    for(let key in newMarketsData){
        let hotdata = newMarketsData[key];
        let propTag = key.split("_")[0].toUpperCase();
        let stag = key.split("_")[0];
        let moneyTag = key.split("_")[1].toUpperCase();
        let market = key.split("_")[0]+"_"+key.split("_")[1];
        let desc = key.split("_")[3].replace('+', ' ');


        if(hotdata instanceof Array){
            let price = hotdata[0];
            let priceBtc = new BigNumber(hotdata[9]||0);
            let volume = new BigNumber(hotdata[5]);
            let last24minPrice = hotdata[6];
            let range = 0;
            let rangeOf24h = 0;
            let unit;
            let trendPrice =[] ;
            if(hotdata.length>6 ){
                trendPrice = hotdata[7];
            }
            if(hotdata.length>7 ){
                rangeOf24h = hotdata[8];
            }

            record[i] = {};
            record[i].key = key;
            record[i].propTag = propTag;
            record[i].stag = stag;
            // record[i].price = M.fixNumber(price,unit);
            record[i].price = price;
            record[i].priceBtc = priceBtc.toFixed(2);
            if(oldMarketsData){
                let oldPrice = oldMarketsData[key][0];
                range = new BigNumber(price).minus(oldPrice).times(100).toFixed(2);
            }
            record[i].range = range;

            record[i].volume = volume.toFixed(2);
            record[i].rangeOf24h = new BigNumber(rangeOf24h).toFixed(2);
            record[i].market = market;
            record[i].symbol = key.split("_")[0].toUpperCase()+"/"+key.split("_")[1].toUpperCase();
            record[i].trendPrice = trendPrice;
            record[i].desc = desc;
            i++;
        }
    }
    return record ;
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
            format = lang=='en'?'MM-dd-yyyy hh:mm:ss':format
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
//移动端rem设置
export const mobileFontSize = () => {
    //根据屏幕宽度自动设置html根节点的font-size值默认1rem=100px;
        let docEl = document.documentElement,
        resizeEvt = 'orientationchange' in window ? 'orientationchange' : 'resize',
        recalc = function () {
            let clientWidth = docEl.clientWidth;
            if (!clientWidth) return;
                docEl.style.fontSize = 100 * (clientWidth / 750) + 'px';
            };
            // Abort if browser does not support addEventListener
        if (!document.addEventListener) return;
        window.addEventListener(resizeEvt, recalc, false);
        document.addEventListener('DOMContentLoaded', recalc, false);
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

//num => language=="EN"?"K/M":"万";
// like:(123456,2)=> language=="EN"?"123.46 K":"12.35 万"
//    (1675432,2)=> language=="EN"?"1.68 M":"167.54 万"
export const numToKiloMillion = (num,pointNum,lan)=>{
    let language = lan?lan:cookie.get(COOKIE_LAN);
    let numstr = "";
    BigNumber.RM = 0;
    switch(language) {
        case 'en':
            if(num>1000000){
                numstr = new BigNumber(num/1000000).toFixed(pointNum)+" M";
            }else if(num>1000){
                numstr = new BigNumber(num/1000).toFixed(pointNum)+" K";
            }else{
                numstr = new BigNumber(num).toFixed(pointNum)
            }
            break;
        default:
            if(num>10000){
                numstr = new BigNumber(num/10000).toFixed(pointNum)+" 万";
            }else{
                numstr = new BigNumber(num).toFixed(pointNum)
            }
    }
    return numstr;
}

//数字千位分割符 “10,000,000”格式化
export const separator = (num)=>{
    let result = [ ], counter = 0;
    num = (num || 0).toString().split('');
    for (let i = num.length - 1; i >= 0; i--) {
        counter++;
        result.unshift(num[i]);
        if (!(counter % 3) && i != 0) { result.unshift(','); }
    }
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
// 显示pop信息
let _selfOptPopTimer = null;
export const optPop = (fn = () => {}, msg = '', opt = {timer: 3000, innerHTML : 0}, showSuccess = false) => {
    const popObj = document.getElementById('popout'),
          txtObj = document.getElementById('poptxt');

    if(showSuccess){
        popObj.getElementsByTagName("svg")[0].style.display = 'none';
    } else {
        popObj.getElementsByTagName("svg")[0].removeAttribute("style");
    }

    popObj.style.display = 'block';
    !opt.innerHTML ? (txtObj.innerText = msg) : (txtObj.innerHTML = msg);
    clearTimeout(_selfOptPopTimer);
    _selfOptPopTimer = setTimeout(() => {
        popObj.style.display = 'none';
        fn();
    }, opt.timer);
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

// 密码判断
export const ckPwd = (str) => {
    var regExps = [new RegExp("[a-zA-Z]"), new RegExp("\\d"), new RegExp("[_\\W]")],
        flg = 0;
        let url=window.location.pathname
    regExps.forEach(v => {
        v.test(str) && flg++;
    });
    return url=="/bw/login"?true:url=="/bw/login/"?true:str.length > 7 && str.length < 21 && flg > 1;
};
export const isAllNumber = (str) => {
    var regExp = new RegExp("^[0-9]*$");
    return regExp.test(str);
};
export const formatSort = (markets,sdicBase)=>{
    let resultMarkets = {};
    const reverse = sdicBase.reverse;
    const key = sdicBase.key;

    if(reverse==0){
        resultMarkets = markets
    }else {
        let sdic;
        if(reverse==1){
            if(key=="key"){
                sdic=Object.keys(markets).sort();
            }else{
                sdic=Object.keys(markets).sort(function(a,b){return markets[a][key]-markets[b][key]});
            }
        }else if(reverse==-1){
            if(key=="key"){
                sdic=Object.keys(markets).sort().reverse();
            }else{
                sdic=Object.keys(markets).sort(function(a,b){return -(markets[a][key]-markets[b][key])});
            }
        }
        // console.log(sdic);
        for(let ki in sdic){
            // console.log(ki);
            let name = sdic[ki];
            resultMarkets[name] = markets[name];
        }
    }
    return resultMarkets;
}

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
        if(pathname=="/bw/manage/account/download/downloadDetails"){
            rt = BASE_UIR+"mg/"+url
        }else{
            pathname.lastIndexOf(sp) == len && (pathname = pathname.substring(0, len));
            rt = pathname.split(sp);
            rt.pop();
            rt.push(url);
            rt = rt.join(sp);
        }

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
    };

    ws.onmessage = function (evt) {
        // let result = JSON.parse(evt.data);
        // cb&&cb(result)

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

//验证是否为EOS类
export const checkEosIcoin = (type) =>{
    let _isEos = false;
    EOSTYPE.map((item,index) =>{
        if(item == type){
            _isEos = true
        }
    })

    return _isEos
}


// 是否是IE浏览器
export const isIE = () => {
    return !!window.ActiveXObject || "ActiveXObject" in window
}


export const TH = (item) => {
    if(item === undefined){
        throw new Error("data err");
    }
}

// types
export const Types = {
    isArray(data){
        let rt = false;

        if('Array' === Object.prototype.toString.call(data).slice(8,-1)){
            rt = true;
        } else {
            TH(undefined);
        }

        return rt;
    },

    isObject(data){
        let rt = false;

        if('Object' === Object.prototype.toString.call(data).slice(8,-1)){
            rt = true;
        } else {
            TH(undefined);
        }

        return rt;
    }
};

// 埋点统计
export const pointCount = (market) => {
    clearTimeout(pointCount.tmr);

    pointCount.tmr = setTimeout(()=>{
        let mk = window.location.href.split('/');
        mk = mk[mk.length -1];
       if(market!="1"){
        axios.get(DOMAIN_VIP + '/browseTrade?market='+(market?market:mk));
       }
    }, 300)
};
export const getType = (o) => {
    return Object.prototype.toString.call(o).slice(8, -1);
};

export const isFunc = (func = () => {}) => {
    return 'Function' === getType(func);
}

