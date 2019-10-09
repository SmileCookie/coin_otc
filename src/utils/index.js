const Big = require('big.js')
import { TIMEFORMAT_ss, DAYFORMAT, USERLIMIT } from '../conf'
import moment from 'moment'
import React from 'react'
export const ucfirst = (url) => {
    let urlArr = url.split('/');
    let str = urlArr[urlArr.length - 1]
    // let strFfig = str.substring(0,1).toUpperCase()+str.substring(1);
    return str;
}
//金额格式化
//第二个参数是否去掉小数点后面的0
export const toThousands = (numa, zero) => {
    Big.RM = 0;
    // numa=String(numa)
    if (numa == '0' || numa == 0) {
        return '0'
    }
    else if (numa == '' || numa == null) {
        return '--'
    } else if (isNaN(numa)) {
        return 'NaN'
    } else {
        let num = new Big(numa).toFixed()
        // (numa||'0').toString().split('.')[1]&&(numa||'0').toString().split('.')[1].length>8?num = new Big(numa).toFixed(8):num = new Big(numa).toFixed()//判断小数点后位数，大于8位则保留八位，
        const regexp = /(?:\.0*|(\.\d+?)0+)$/
        zero ? num = num.replace(regexp, '$1') : num
        let numarr = (num || '0').toString().split('.')[0], result = '';
        // let h = numarr.charAt(0)
        // // console.log(numarr,typeof numarr,numarr.charAt(0))
        // if(numarr.charAt(0) == '-'){
        //     numarr = numarr.substring(1);
        //     // console.log(numarr)                
        // }
        while (numarr.length > 3 && !(numarr.length == 4 && (numarr.indexOf('-') != -1))) {
            result = ',' + numarr.slice(-3) + result;
            numarr = numarr.slice(0, numarr.length - 3);
        }
        if (numarr) {
            let numdian = num.toString().split('.')[1] ? '.' + num.toString().split('.')[1] : ''
            result = numarr + result + numdian;
            // result = h + numarr + result + numdian; 
        }
        return result;
    }
}
//只去掉小数点后面的0
export const removeZero = (num) => {
    if (num == '') {
        return '--'
    } else {
        return parseFloat(num);
    }

}

//判断权限
export const pageLimit = (pagename, limitList) => {

    let array = []
    for (let i = 0; i < limitList.length; i++) {
        let pagIndex = limitList[i].indexOf(':')
        let pgeIndex = limitList[i].lastIndexOf(':')
        let pgeName = limitList[i].slice(pagIndex + 1, pgeIndex)
        if (pgeName == pagename) {
            array.push(limitList[i].slice(pgeIndex + 1, limitList[i].length))
        }
    }
    return array
}

//判断字符长度（汉字算两个字符，字母数字算一个）
export const getByteLen = (val) => {
    var len = 0;
    for (var i = 0; i < val.length; i++) {
        var a = val.charAt(i);
        if (a.match(/[^\x00-\xff]/ig) != null) {
            len += 2;
        }
        else {
            len += 1;
        }
    }
    return len;
}
//转二进制流
export const dataURItoBlob = (dataURI) => {
    var byteString = atob(dataURI.split(',')[1]);
    var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
    var ab = new ArrayBuffer(byteString.length);
    var ia = new Uint8Array(ab);
    for (var i = 0; i < byteString.length; i++) {
        ia[i] = byteString.charCodeAt(i);
    }
    return new Blob([ab], { type: mimeString });
}

//tableScroll
export const tableScroll = (table, type, panel, cb) => {

    if (cb) {
        var panelHeight = document.querySelector(panel).offsetHeight;
        cb(panelHeight)
    }
    var tableCont = document.querySelector(table);
    const scrollHandle = () => {
        var scrollTop = tableCont.scrollTop;
        tableCont.querySelector('thead').style.transform = `translateY(${scrollTop || 0}px)`;
        if (tableCont.querySelectorAll('thead').length > 1) {
            tableCont.querySelector('thead').nextSibling.style.transform = `translateY(${scrollTop}px)`;
        }
    }
    scrollHandle()
    if (!type) {
        tableCont.removeEventListener('scroll', scrollHandle);
        return false;
    }
    tableCont.addEventListener('scroll', scrollHandle);
}

//table 文字换行

export const toWordWrap = text => {
    let br = <br></br>, arr = [], result;
    arr = text.split('')
    for (let i = 0; i < arr.length; i++) {
        if (i == 0) {
            result = arr[i]
        } else if (i % 7 == 0) {
            result = <span>{result}{br}{arr[i]}</span>
        } else {
            result = <span>{result}{arr[i]}</span>
        }
    }
    // console.log(result)
    return <div>{result}</div>
}

// keypress enter inquiry
export const kpEventLisInquiry = (...args) => {
    switch (args.length) {
        case 1:
            window.removeEventListener('keypress', args[args.length - 1])
            break;
        case 3:
            if (args[0] == args[1]) {
                window.addEventListener('keypress', args[args.length - 1])
            } else {
                window.removeEventListener('keypress', args[args.length - 1])
            }
            break;
        default:
            break;
    }
}

//time change
export const timeChange = (mss) => {
    // var days = parseInt(mss / (1000 * 60 * 60 * 24));
    // var hours = parseInt((mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    // var minutes = parseInt((mss % (1000 * 60 * 60)) / (1000 * 60));
    // var seconds = (mss % (1000 * 60)) / 1000;

    var days = parseInt(mss / (60 * 60 * 24));
    var hours = parseInt((mss % (60 * 60 * 24)) / (60 * 60));
    var minutes = parseInt((mss % (60 * 60)) / 60);
    var seconds = (mss % 60) / 1000;

    if (days > 0) {
        return days + "天"
    }
    if (hours > 0 && days == 0) {
        return hours + "时"
    }
    if (days == 0 && hours == 0) {
        if (minutes == 0) {
            return 1 + "分"
        }
        return minutes + "分"
    }
}

//市场推广判断类型
export const showChooseType = (data) => {
    let _data;
    if (data) {
        _data = data.split(',');
    } else {
        _data = []
    }

    let _type = [];
    console.log(_data)
    _data.map((item, index) => {
        switch (item) {
            case "1":
                _type.push('首页广告')
                break;
            case "2":
                _type.push('用户引流')
                break;
            case "3":
                _type.push('媒体支持')
                break;
            case "4":
                _type.push('友情链接')
                break;
            case "5":
                _type.push('其他')
                break;
            case "6":
                _type.push('战略合作')
                break;
        }
    })

    return _type;
}

/**
 * @author Oliver
 * 
 * @function 获取类型
 * @param   [String] str 传入值
 * @returns [String] 返回类型
 */
export const getType = (str = '') => {
    return Object.prototype.toString.call(str).slice(8, -1);
};


/**
 * @author Oliver
 * 
 * @param {any} func 传入值
 * @requires [Boolean] 返回类型
 * 
 */
export const isFunc = (func = () => { }) => {
    return 'Function' === getType(func);
}

/**
 * @function 是否是波尔类型值
 * @param {any} ay 
 * @returns [Boolean] 传入值是否是 Array类型
 */
export const isArray = (ay = '') => {
    return 'Array' === getType(ay);
};

/**
 * @function 是否是对象
 * @param {any} obj 
 * @returns [Boolean] 传入值是否是 Object类型
 */
export const isObj = (obj = '') => {
    return 'Object' === getType(obj);
};

/**
 * @function 是否是字典
 * @param {*} map 
 * @returns [Boolean] 传入值是否是 Map类型
 */
export const isMap = (map = '') => 'Map' === getType(map)

/**
 * @function 判断一个对象的key是否全部存在于另一个对象，如果存在不匹配直接抛出异常。
 * @param {Object} keys 需要匹配的类
 * @param {Object} obj  被匹配的类
 * @throws not contain
 */
export const containsObj = (keys = {}, obj = {}) => {
    if (isObj(keys) && isObj(obj)) {
        const getKeys = Object.keys(keys);
        getKeys.forEach((v, i) => {
            if (void 0 === obj[v]) {
                throw new Error("not contain")
            }
        })
    } else {
        throw new Error("not contain")
    }
}

/**
 * 
 * @param {*} item 
 * @throws data err
 */
export const TE = v => {
    if (v === void 0) {
        throw new Error("data err");
    }
    return v
}

/**
 * @function Map取值
 * @param {*} map 
 * @param {*} k 
 * @returns value
 */
export const mapGet = (map = new Map(), k) => {
    if (!isMap(map)) throw new Error('data is not Map')
    if (!map.has(k)) {
        return ''
    }
    return map.get(k)
}

/**
 * 
 * @param {*} v 
 * @description 请求列表判断后端是数组
 */
export const ckd = v => {
    if (v === null) {
        return []
    } else if (isArray(v)) {
        return v
    } else {
        throw new Error('data is not Array')
    }
}

/**
 * @author Oliver
 * 
 * @param {Number} d 
 * @param {String} type 
 * @returns {String} type类型的时间 2018-01-10 09:00:00
 */
export const dateToFormat = (d, type = TIMEFORMAT_ss) => d ? moment(d).format(type) : '--'

/**
 * @author Oliver
 * 
 * @param {Number} d 
 * @param {String} type 
 * @returns {String} type类型的时间 2018-01-10 
 */
export const dateToFormat_ymd = (d, type = DAYFORMAT) => d ? moment(d).format(type) : '--'

/**
 * 
 * @param  {array || Object} time
 * @param  {Number} num
 * @returns [String] 2019-07-07 || ''
 */
const _dateToFormat_ymd = (d, type = DAYFORMAT) => d ? moment(d).format(type) : ''
export const arrayTimeToStr_ymd = (time = [], num = 0) => {
    const _obj = {
        Array: _dateToFormat_ymd(time[num]),
        Object: _dateToFormat_ymd(time)
    }
    try {
        return _obj[getType(time)]
    } catch (error) {
        console.error('arrayTimeToStr_ymd:' + error)
        return ''
    }
}

/**
 * 
 * @param  {array || Object} time
 * @param  {Number} num
 * @returns [String] 2019-07-07 23:59:59 || ''
 */
const _dateToFormat = (d, type = TIMEFORMAT_ss) => d ? moment(d).format(type) : ''
export const arrayTimeToStr = (time = [], num = 0) => {
    const _obj = {
        Array: _dateToFormat(time[num]),
        Object: _dateToFormat(time)
    }
    try {
        return _obj[getType(time)]
    } catch (error) {
        console.error('arrayTimeToStr:' + error)
        return ''
    }
}

/**
 * @author Oliver
 * 
 * @param {any} key
 * @param {any} value
 * @param {String} name
 */
const SS = global.sessionStorage || window.sessionStorage
export const saveToSS = (value, key = 'orderNo', name = 'OTC_Trade') => SS.setItem(name, JSON.stringify({ [key]: value }))

export const getFromSS = (key = 'orderNo', name = 'OTC_Trade') => (JSON.parse(SS.getItem(name)) || {})[key]

export const removeFromSS = (name = 'OTC_Trade') => SS.removeItem(name)


/**
 * @author Oliver
 * 
 * @param {String} str 
 * @param {Number} num 
 * @param {String} type 
 * 
 * @returns [String] 返回类型
 */
export const splitArr = (str = '', num = 0, type = ',') => str.split(type)[num] || ''
export const getFromLS = (key, name) => {
    // console.log("getFromLS", name)

    let ls = {};
    if (global.localStorage) {
        try {
            ls = JSON.parse(global.localStorage.getItem(name)) || {};
        } catch (e) {
            /*Ignore*/
        }
    }
    return ls[key];
}

export const saveToLS = (key, value, name) => {
    if (global.localStorage) {
        global.localStorage.setItem(
            name,
            JSON.stringify({
                [key]: value
            })
        );
    }
}


/** 驾驶舱 方法 start */
export const judgeDate = date => moment(date).format(TIMEFORMAT_ss)

// 1554998400000
// 1555084799000



export const getDate = ({ scope, prev, curr, beforeNow } = { scope: 'day', prev: 0, curr: 0, beforeNow: '' }) => {

    let startTime, endTime;

    try {
        if (beforeNow) {
            startTime = moment(beforeNow && beforeNow).startOf(scope).valueOf()
        } else {
            startTime = moment()[scope](moment()[scope]() - prev).startOf(scope).valueOf()
        }
        endTime = moment()[scope](moment()[scope]() - curr).endOf(scope).valueOf()
        // console.log(`${scope}:${prev}>>start`, judgeDate(startTime))
        // console.log(`${scope}:${curr}>>end`, judgeDate(endTime))
        return {
            startTime,
            endTime
        }
    } catch (error) {
        console.log(error)
    }
}


// 格式化时间
export const formatDateStr = (timeStr, type) => {
    // type 1日 2是周 3是月
    if (!timeStr) {
        return '';
    }
    let str = '';
    if (type == 1) {
        str = moment(timeStr).format('YYYY-MM-DD');
    } else {
        timeStr = timeStr + '';
        if (timeStr.length > 4) {
            str = timeStr.substring(0, 4);
            if (type == 2) {
                str += '-' + timeStr.substring(4) + '周';
            } else {
                if (timeStr.length == 5) {
                    let num = timeStr.substring(4, 5);
                    if (parseInt(num) < 10) {
                        str += '-0' + num
                    }
                } else {
                    str += '-' + timeStr.substring(4);
                }
            }
        } else {
            str = timeStr;
        }
    }
    return str;
};
export const formatDateList = (arr, type) => {
    type = type || 1;
    let list;
    if (!arr) {
        return [];
    }
    list = arr.map(item => {
        let str = formatDateStr(item, type);
        return str;
    });
    return list;
};

export const newTableColumns = (columnArr, changeArr, radioCurrency, radioLegal) => {
    for (let i = 0; i < changeArr.length; i++) {
        let item = changeArr[i];
        // 0是货币，1是法币
        if (item.type == 0) {
            columnArr[item.index].title = item.title + '(' + radioCurrency + ')';
        } else {
            columnArr[item.index].title = item.title + '(' + radioLegal + ')';
        }
    }
    return columnArr;
};

/**  驾驶舱 end */

export const islessBodyWidth = () => document.body.clientWidth < 1540


/** 
 * @author Oliver
 * 
 * @description 用户信息判断用户限制
 * 
 */

export const userLimit = (v = '') => {
    try {
        return USERLIMIT[(() => v.split(',').sort((a, b) => a - b).join(','))()] || USERLIMIT.default
    } catch (error) {
        return <span className='red'>错误：不是String类型</span>
    }

}

/**
 * @function 获取随机颜色
 * 
 * @returns {String}
 */
export function getRandomColor() {
    return `#${Math.random().toString(16).substr(2, 6)}`
}