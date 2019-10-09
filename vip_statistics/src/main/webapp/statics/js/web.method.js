define(function(require, exports, module) {
	"require:nomunge,exports:nomunge,module:nomunge";
	var method = {};
	
	method.fixNumber = function(value,unit){
    	var value = parseFloat(value).toFixed(unit);
    	var unit = unit || 0 ; 
    	var isInt = value.indexOf(".") == -1 ? true : false ;
    	var intNum = value.split(".")[0];
    	var floatNum = !isInt ? value.split(".")[1] : "0";
    	var floatArry = floatNum.split("");
    	var newFloatNum = ".";
		for(var i = 0; i < unit ; i++){
			if(!floatArry[i]){
				newFloatNum += "0";
			}else{
				newFloatNum += floatArry[i] ;
			}
		}
		return parseFloat(intNum +  newFloatNum).toFixed(unit);
    };
    method.fixDecimal = function(value,unit){
    	var $this = this;
    	var result = $this.fixNumber(value, unit);
    	if(unit > 0){
    		result = parseFloat(result);
    	}else{
    		result = parseInt(result);
    	}
    	if(result > 0 && result < 0.000001){
    		result = $this.fixNumber(value, unit);
    	}
    	return result ;
    }
    method.fixFloat = function(value,unit){
    	var $this = this;
    	var result = $this.fixNumber(value, unit);
    	if(unit > 0){
    		result = parseFloat(result);
    	}else{
    		result = parseInt(result);
    	}
    	return result ;
    }
    method.comptime = function(beginTime,endTime) {
    		var beginTime = beginTime;
    		var endTime =  endTime;
        var beginTimes = beginTime.substring(0, 10).split('-');
        var endTimes = endTime.substring(0, 10).split('-');

                beginTime = beginTimes[1] + '-' + beginTimes[2] + '-' + beginTimes[0] + ' ' + beginTime.substring(10, 19);
        endTime = endTimes[1] + '-' + endTimes[2] + '-' + endTimes[0] + ' ' + endTime.substring(10, 19);

        var a = (Date.parse(endTime) - Date.parse(beginTime)) / 3600 / 1000;
        if (a < 0) {
            return ">";
        } else if (a > 0) {
            return "<";
        } else if (a == 0) {
            return "=";
        } else {
            return 'exception';
        }
    };
    method.ceilNumber = function(value,unit){
		var result = Math.ceil(value*Math.pow(10,unit))/Math.pow(10,unit);
		return result;
    };
    method.floorNumber = function(value,unit){
		Big.RM = 0;
		return new Big(value).toFixed(unit);
    };
	method.divNumber = function(s, n) {
		n = n > 0 && n <= 20 ? n : 2;  
		s = parseFloat((s + "").replace(/[^\d\.-]/g, "")).toFixed(n) + "";  
		var l = s.split(".")[0].split("").reverse();  
			r = s.split(".")[1];  
			t = "";  
		for(i = 0; i < l.length; i ++ ) {  
			t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");  
		}  
		return t.split("").reverse().join("") + "." + r;  
	} 
	method.divInteger = function(s) {
		var l = (s + "").split("").reverse();  
			t = "";  
		for(i = 0; i < l.length; i ++ ) {  
			t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");  
		}  
		return t.split("").reverse().join("");  
	}
	method.isEmail = function(str){
		var regExp = new RegExp("^([a-z0-9A-Z]+[-|_|\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\.)+[a-zA-Z]{2,}$");
		return regExp.test(str);
	};
	method.isPhone = function(str){
		var regExp = new RegExp("((\d{11})|^((\d{7,8})|(\d{4}|\d{3})-(\d{7,8})|(\d{4}|\d{3})-(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1})|(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1}))$)");
		return regExp.test(str);
	};
	method.isAllNumber = function(str){
		var regExp = new RegExp("^[0-9]*$");
		return regExp.test(str);
	};
	method.hasLetter = function(str){
		var regExp = new RegExp("[a-zA-Z]");
		return regExp.test(str);
	};
	method.hasNumber = function(str){
		var regExp = new RegExp("[0-9]");
		return regExp.test(str);
	};
	method.hasChinese = function(str){
		var regExp = /[\u4E00-\u9FA5\uF900-\uFA2D]/;
		return regExp.test(str);
	};
	method.hasOther = function(str){
		var regExp = new RegExp("[`~!@#$%^&*()=|{}':;',\\[\\].<>/?~！@#￥……％&*（）——|{}【】‘；：”“'。，、？]");
		return regExp.test(str);
	};
	method.hasEmailOther = function(str){
		var regExp = new RegExp("[`~!#$%^&*()=|{}':;',\\[\\].<>/?~！#￥……％&*（）——|{}【】‘；：”“'。，、？]");
		return regExp.test(str);
	}
	method.checkStrong = function (sValue){
		var modes = 0;
		if(sValue.length < 6) return modes;
		if(/\d/.test(sValue)) modes++; 
		if(/[a-z]/.test(sValue)) modes++; 
		if(/[A-Z]/.test(sValue)) modes++; 
		if(/\W/.test(sValue)) modes++; 
		if(modes < 4){
			return modes ;
		}else{
			return sValue.length < 12 ? 3 : 4 ;
		}
	}
	method.formatDate = function(timestamp,format){
		Date.prototype.format = function (format) {
		    var o = {
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
		    for (var k in o) {
		        if (new RegExp("(" + k + ")").test(format)) {
		            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
		        }
		    }
		    return format;
		}
		return new Date(timestamp).format(format);
	}


	method.stringByte = function(str, charset){
	    var total = 0,
	        charCode,
	        i,
	        len;
	    charset = charset ? charset.toLowerCase() : '';
	    if(charset === 'utf-16' || charset === 'utf16'){
	        for(i = 0, len = str.length; i < len; i++){
	            charCode = str.charCodeAt(i);
	            if(charCode <= 0xffff){
	                total += 2;
	            }else{
	                total += 4;
	            }
	        }
	    }else{
	        for(i = 0, len = str.length; i < len; i++){
	            charCode = str.charCodeAt(i);
	            if(charCode <= 0x007f) {
	                total += 1;
	            }else if(charCode <= 0x07ff){
	                total += 2;
	            }else if(charCode <= 0xffff){
	                total += 3;
	            }else{
	                total += 4;
	            }
	        }
	    }
	    return total;
	}
	method.preAddSubtract = function (n1, n2) { 
     var oT = this.preHandle(n1, n2);
     return !(oT === oT) ? oT : (function () {
     if (oT.d1 - oT.d2 > 0) {
     oT.s2 = oT.s2 + (new Array(oT.d1 - oT.d2 + 1)).join('0');
     }
     else {
     oT.s1 = oT.s1 + (new Array(oT.d2 - oT.d1 + 1)).join('0');
     }
     return {
     s1: oT.s1,
     s2: oT.s2,
     n1: parseInt(oT.s1, 10),
     n2: parseInt(oT.s2, 10),
     d1: oT.d1,
     d2: oT.d2
     };
     })();
     },

     method.add = function (n1, n2) {
     var oT = this.preAddSubtract(n1, n2),
     out = !(oT === oT) ? oT : ((oT.n1 + oT.n2) / Math.pow(10, Math.max(oT.d1,
     oT.d2))).toFixed(Math.max(oT.d1, oT.d2));
     return parseFloat(out);
     }
     method.subtract = function (n1, n2) {
     var oT = this.preAddSubtract(n1, n2),
         out = !(oT === oT) ? oT : ((oT.n1 - oT.n2) / Math.pow(10, Math.max(oT.d1,oT.d2))).toFixed(Math.max(oT.d1, oT.d2));
     return parseFloat(out);
     }
     method.preHandle = function (n1, n2) {
		function scientificToNumber(num) {
            var str = num.toString();
            var reg = /^(\d+)(e)([\-]?\d+)$/;
            var arr, len,
                zero = '';

            /*6e7或6e+7 都会自动转换数值*/
            if (!reg.test(str)) {
                return num;
            } else {
                /*6e-7 需要手动转换*/
                arr = reg.exec(str);
                len = Math.abs(arr[3]) - 1;
                for (var i = 0; i < len; i++) {
                    zero += '0';
                }

                return '0.' + zero + arr[1];
            }
        }
		if (!(( typeof n1 === 'number' || ( typeof n1 === 'string' && /^[\d]+(\.[\d]+)?$/g.test(n1))) &&
		( typeof n2 === 'number' || ( typeof n2 === 'string' && /^[\d]+(\.[\d]+)?$/g.test(n2)))
		)) {
		return NaN;
		}
		var s1 = scientificToNumber(n1.toString()), s2 = scientificToNumber(n2.toString()), a1 = s1.split("."), a2 = s2.split(".");
		s1 = s1.replace(".", "");
		s2 = s2.replace(".", "");
		return {
			s1: s1,
			s2: s2,
			n1: parseInt(s1, 10),
			n2: parseInt(s2, 10),
			d1: a1.length > 1 ? a1[1].length : 0, 
			d2: a2.length > 1 ? a2[1].length : 0
		};
     }
     method.multiply = function (n1, n2) { 
		var oT = this.preHandle(n1, n2);
		var	out = !(oT === oT) ? oT : (oT.n1 * oT.n2 / Math.pow(10, oT.d1 + oT.d2)).toFixed(oT.d1 + oT.d2);
		return parseFloat(out);
     }
     method.divide = function (n1, n2) {
		var oT = this.preHandle(n1, n2),
		out = !(oT === oT) ? oT : (oT.n1 / oT.n2 / Math.pow(10, oT.d1 - oT.d2));
		return parseFloat(out);
     }
     method.insertFlg = function (str,flg,sn){
	    var newstr="";
	    for(var i=0;i<str.length;i+=sn){
	        var tmp=str.substring(i, i+sn);
	        newstr+=tmp+flg;
	    }
	    return newstr;
	}
	method.GetQueryString = function(name){
		var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
		var r = window.location.search.substr(1).match(reg);
		if(r!=null)return  unescape(r[2]); return null;
	}
	method.numFm = function (num) {
        Big.RM = 0;

        var nums = new Big(num).toFixed(2);
        var unit = '';

        // if(100000000 <= num){
        //     nums = new Big(num).div(100000000).toFixed(2);
        //     unit = 'B';
        // }
        if(1000000 <= num){
            nums = new Big(num).div(1000000).toFixed(2);
            unit = 'M';
        } else if(1000 <= num){
            nums = new Big(num).div(1000).toFixed(2);
            unit = 'K';
        }

        return nums + unit
    }
	method.decimal = function (dayVolume_1){
		Big.RM = 0;
		var priceBtc = "";
		var dayVolume = new Big(dayVolume_1);
		var k_1 = new Big(1000);
		var wan = new Big(10000);
		var m_1 = new Big(1000000); 
		if (parseFloat(dayVolume_1) < 1000) {
			priceBtc = dayVolume.toFixed(2);
		} else {
			if (LANG == 'cn') {
				priceBtc = dayVolume.div(wan).toFixed(2) + "万";
				// this.fixNumber(parseFloat(dayVolume) / 10000, 2) + "万";
			} else {
				if (parseFloat(dayVolume_1) >= 10000000) {
					priceBtc = dayVolume.div(m_1).toFixed(2) + "M";
					// this.fixNumber(parseFloat(dayVolume) / 1000000, 2) + "M";
				} else {
					priceBtc = dayVolume.div(k_1).toFixed(2)  + "K";
					// this.fixNumber(parseFloat(dayVolume) / 1000, 2) + "K";
				}
			}
		}
		return priceBtc;
	}

    module.exports = method;
    (function(){ return this || (0,eval)('this'); }()).method = method;
});



