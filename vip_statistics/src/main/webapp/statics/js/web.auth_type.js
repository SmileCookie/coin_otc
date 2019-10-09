define(function (require, exports, module) {
    "require:nomunge,exports:nomunge,module:nomunge";
    var authType = {};
    var laydate = require("./common/laydate/laydate");
    var plupload = require("./common/plupload/plupload.full.min");
    var qiniu = require("./common/plupload/qiniu.min");
    var up_img = require("./common/up_img_file");
    var submit_btn = $(".user_auth_submit"); 
    authType.countryCode = sessionStorage.getItem("countryCode");  //国家号
    authType.countName = sessionStorage.getItem("countName")//中文国家名
    authType.lastName = ""; //姓
    authType.firstName = ""; //名
    authType.frontalImg = false; //正面证件照
    authType.backImg = false;   //证件背面照
    authType.loadImg = false;   //手持证件照
    authType.startDate = "";    //开始时间
    authType.endDate = "";      //结束时间
    authType.uptoken = "";

    authType.init = function(){
        console.log(this.countName);
        var _this = this;
        this.toUpperCase();
        var my_lang = LANG;
        if (LANG == "hk" ){
            my_lang = "en"
        }
        laydate.render({
            elem: '#layDate_1',
            lang: my_lang
        }); 
        laydate.render({
            elem: '#layDate_2',
            lang: my_lang
        });
        $('input[name="file"]').hover(function(){
            $(this).next().css("background","rgba(18, 143, 220, 0.20)");
        },function(){
            $(this).next().css("background", "rgba(18, 143, 220, 0.07)");
        });
        
        
    }
    authType.idCardInit = function (){   //身份证验证
        var _this = this; 
        var upfile_1 = new upfile({ //	frontalImg    //正面证件照
            iuput_class: ".file_img_1",
            img_id: "#file_img_1",
            file_text:".file_text_1",
            input_id: "file_input_1",
            qiniu_id: "qiniu_1",
            loading: "loading_1",
            callbacks: function (cbdata) {
                _this.frontalImg = cbdata;
            }
        })
        var upfile_2 = new upfile({ //	backImg    //证件背面照
            iuput_class: ".file_img_2",
            img_id: "#file_img_2",
            file_text: ".file_text_2",
            input_id: "file_input_2",
            qiniu_id: "qiniu_2",
            loading: "loading_2",
            callbacks: function (cbdata) {
                _this.backImg = cbdata;
            }
        })
        var upfile_3 = new upfile({ //  loadImg    //手持证件照
            iuput_class: ".file_img_3",
            img_id: "#file_img_3",
            file_text: ".file_text_3",
            input_id: "file_input_3",
            qiniu_id: "qiniu_3",
            loading: "loading_3",
            callbacks: function (cbdata) {
                _this.loadImg = cbdata;
            }
        })
        this.idCardClick();//身份证点击
        
    }
    authType.idCardClick = function () { //身份证点击
        var _this = this;
        submit_btn.on("click", function () {
            if (!_this.verification()) return;
            var idCard = $.trim($("#idCard").val()); //身份证号码    bitbank.L("")
            if (idCard == ""){
                JuaBox.showWrong(bitbank.L("身份证号码不得为空"));
                return;
            }
            if (_this.countryCode == "+86" && !isIdCardNo(idCard)){  //国内身份证验证
                JuaBox.showWrong(bitbank.L("身份证号码错误"));
                return;
            }
            else{  //国际身份证验证
                var regs = /^[a-zA-Z0-9]{5,21}$/;
                if (!regs.test(idCard)) {
                    JuaBox.showWrong(bitbank.L("身份证号码错误"));
                    return;
                }
            }
            if ( !_this.validityFun() ) return; //有效期验证
            if ( _this.frontalImg == "" ) {
                JuaBox.showWrong(bitbank.L("请上传身份证正面照片"));
                return;
            }
            if ( _this.backImg == "" ) {
                JuaBox.showWrong(bitbank.L("请上传身份证背面照片"));
                return;
            }
            if ( _this.loadImg == "" ) {
                JuaBox.showWrong(bitbank.L("请上传手持身份证照片"));
                return;
            }
            _this.upFile({
                lastName: _this.lastName,
                firstName: _this.firstName,
                cardId: idCard,
                cardType: 1,
                startDate: _this.startDate,
                endDate: _this.endDate,
                countryCode: _this.countryCode,
                frontalImg: _this.frontalImg,
                backImg: _this.backImg,
                loadImg: _this.loadImg,
                countryName: _this.countName
            });
        })
    }
    authType.passportInit = function(){   //护照验证
        var _this = this;
        var upfile_1 = new upfile({ //	frontalImg    //正面证件照
            iuput_class: ".file_img_1",
            img_id: "#file_img_1",
            file_text: ".file_text_1",
            input_id: "file_input_1",
            qiniu_id: "qiniu_1",
            loading: "loading_1",
            callbacks: function (cbdata) {
                _this.frontalImg = cbdata;
            }
        });
        var upfile_2 = new upfile({ //	frontalImg    //正面证件照
            iuput_class: ".file_img_2",
            img_id: "#file_img_2",
            file_text: ".file_text_2",
            input_id: "file_input_2",
            qiniu_id: "qiniu_2",
            loading: "loading_2",
            callbacks: function (cbdata) {
                _this.loadImg = cbdata;
            }
        })
       _this.passportClick()
        
    }
    authType.passportClick = function () { //护照验证点击
        var _this = this;
        submit_btn.on("click", function () {
            if ( !_this.verification() ) return;  //姓，名 验证
            var passport = $("#passport").val(); //护照号  
            if (passport == ""){
                JuaBox.showWrong(bitbank.L("护照号码不得为空"));
                return;
            }
            var regs = /^[a-zA-Z0-9]{5,17}$/;
            if (!regs.test(passport)){
                JuaBox.showWrong(bitbank.L("护照号码错误"));
                return;
            }
            if(!_this.validityFun()) return;  //有效期验证
            if ( _this.frontalImg == "" ){
                JuaBox.showWrong(bitbank.L("请上传护照照片"));
                return;
            }
            if ( _this.loadImg == "" ) {
                JuaBox.showWrong(bitbank.L("请上传手持护照照片"));
                return;
            }
            _this.upFile({
                lastName: _this.lastName,
                firstName: _this.firstName,
                cardId: passport,
                cardType:2,
                startDate:_this.startDate,
                endDate:_this.endDate,
                countryCode: _this.countryCode,
                frontalImg: _this.frontalImg,
                loadImg: _this.loadImg,
                countryName: _this.countName
            });
        })
    }
    authType.verification = function(){  //  姓，名  验证
        var _this = this;
        _this.lastName = $.trim( $("#lastName").val() ); //姓
        _this.firstName = $.trim( $("#firstName").val() ); //名  
        if (_this.lastName == ""){
            JuaBox.showWrong(bitbank.L("姓氏不得为空"));
            return false;
        }
        // if (_this.stripscript(_this.lastName)){
        //     JuaBox.showWrong(bitbank.L("姓氏中不得包含特殊字符"));
        //     return false;
        // }
        if (_this.firstName == "") {
            JuaBox.showWrong(bitbank.L("名字不得为空"));
            return false;
        }
        // if (_this.stripscript(_this.firstName)) {
        //     JuaBox.showWrong(bitbank.L("名字中不得包含特殊字符"));
        //     return false;
        // }
        return true;
    }
    authType.validityFun = function(){
        var _this = this;
        _this.startDate = $("#layDate_1").val();
        _this.endDate = $("#layDate_2").val();
        if( _this.startDate == "" || _this.endDate == "" ){
            JuaBox.showWrong(bitbank.L('证件有效期不得为空'));
            return false;
        }
        if( _this.startDate >= _this.endDate ){
            JuaBox.showWrong(bitbank.L('证件有效期的截止时间不得早于开始时间。'));
            return false;
        }
        return true;
    }
    authType.upFile = function(data){
        $.ajax({
            url: DOMAIN_VIP + '/manage/auth/AuthSave',
            type: "POST",
            data: data,
            dataType: "json",
            success: function (rest) {
                if (rest.isSuc){
                    sessionStorage.removeItem("countryCode");
                    sessionStorage.removeItem("countryname");
                    sessionStorage.removeItem("countName");
                    JuaBox.showWrong(rest.des);
                    setTimeout(function() {
                         window.location.href = DOMAIN_VIP + "/manage/auth/authentication";
                    }, 1500);
                }
                else{
                    JuaBox.showWrong(rest.des);
                }
            },
            error: function (err) {
                console.log(err)
            }
        });
    }
    authType.toUpperCase = function(){
        $(".name_toUpCase").blur(function(){
            var value = $(this).val();
            if (value != ""){
                var up_value = value.charAt(0);
                var str = /^[A-Za-z][A-Za-z\s]*[A-Za-z]$/;
                var str_1 = /^[a-z]+$/;
                if (str.test(value) && str_1.test(up_value)) {
                    up_value = up_value.toUpperCase();
                    value = value.replace(value.charAt(0), up_value);
                    $(this).val(value);
                }
            }
            
            
        })
    }
    // authType.stripscript = function(value) {  //验证姓名 --
    //     var pattern = new RegExp("[0-9]");        //new RegExp("[`~!@#$^&*%()=|{}'-:;',\\[\\]0-9.<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]");
    //    if (pattern.test(value)){
    //        return true;
    //     }
    //     else{
    //        return false;
    //     };
    // }
    module.exports = authType;
});

/*验证身份证号码*/
function isIdCardNo(num) {  //验证身份证号码
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
