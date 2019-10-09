<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>

<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${L:l1(lan,"%%提币",coint.propTag)}-${WEB_NAME }-${WEB_TITLE }</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0 maximum-scale=1, user-scalable=no">
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<link rel="stylesheet" href="${static_domain }/statics/css/web.pay.css?V${CH_VERSON }">
<link rel="stylesheet" href="${static_domain }/statics/css/web.asset.css?V${CH_VERSON }">

</head>
<body>
<div class="bk-body">
  <jsp:include page="/common/top.jsp" />
    <div class="mainer">
        <div class="container2">
            <div class="content">
                <div class="bk-payInOut">
                    <div class="cont-row">
                        <div class="row"> 
                        <section class="new_address">
                            <h2>${L:l1(lan, '新增%%提现地址',coint.propTag)} </h2> 
                            <c:if test="${curUser.withdrawAddressAuthenType != 2}">
                                <div class="item mb20 head_text_tips">${L:l(lan, '您当前的提现地址验证为”初级模式“，为了您的资金安全，建议您开启')}"<a class="hover_color" href="/manage/auth?address=address_withdraw">${L:l(lan, '安全模式_1')}</a>"${L:l(lan, '。')}</div>
                            </c:if>
                            <div class="item mb10">
                                <h5>${L:l(lan,"地址标签")}:</h5>
                                <div class="input_warp">
                                    <input type="text" id="memo" class="input_item"/>
                                </div>
                            </div>
                            <div class="item ${curUser.withdrawAddressAuthenType == 2 ? 'mb10' : 'mb40'}">
                                <h5>${L:l(lan,"提现地址")}:</h5>
                                <div class="input_warp">
                                    <input type="text"  id="address" class="input_item"/>
                                </div>
                            </div>
                            <c:if test="${curUser.withdrawAddressAuthenType == 0}">
                                <div class="item mb5">
                                    <h5>
                                    ${L:l(lan, mobileStatu == 2 ?"提现-提现中部-标签-6":"邮件验证码") }                                                
                                    </h5>
                                    <div class="input_warp input_warp_bor clearfix">
                                        <input type="text" id="sendCode" class="input_item input_send" placeholder="${L:l(lan, '短信/邮件验证码')}"/>
                                        <div class="send_btn hover_color" id="sendCodeBtn">${L:l(lan,"获取")}</div>
                                    </div>
                                </div>
                                <div class="item text_bottom_tips mb40">${L:l(lan,"温馨提示_1")}${L:l(lan,"您当前为“安全模式”，新增提现地址后将被锁定24小时。")}</div>
                            </c:if>
                            <div class="address_btn" id="submit">${L:l(lan,"创建")}</div>
                        </section>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/common/foot.jsp" />
<script>
    $("#sendCodeBtn").on("click",function(){
       var memo = $.trim($("#memo").val());
       var address = $.trim($("#address").val());
        if(memo.length > 20 ){
            JuaBox.showWrong("${L:l(lan,"标签不得超过20个字符")}");
        }
        else if(address == ""){
            JuaBox.showWrong("${L:l(lan,"请输入提现地址")}");
        }
        else{
            sendCode();
        }
        
    })
    function sendCode() {
        $.ajax({
            type: "POST",
            url: "/userSendCode",
            data: {
                codeType: 10,
                currency:"${coint.propTag}"
            },
            dataType: "json",
            error: function (err) {
                console.log(err)
                inAjaxing = false;
            },
            success: function (json) {
                inAjaxing = false;
                if (json.isSuc) {
                    // if (json.datas.isEmail) {
                    //     JuaBox.sure(json.des);
                    // }
                    settime($('#sendCodeBtn'));
                } else if ('needMobileAuth' == json.des) {
                    JuaBox.sure(bitbank.L("您未进行手机认证，请先进行手机认证"));
                } else {
                    JuaBox.sure(json.des);
                }
            }
        });
    }

    var countdown = 60;
    function settime(obj) {
        if (countdown == 0) {
            $(obj).removeAttr("disabled");
            $(obj).html("${L:l(lan,'获取')}");
            countdown = 60;
        } else {
            $(obj).attr("disabled", true);
            $(obj).html("${L:l(lan,'已发送')}" + "(" + countdown + ")");
            countdown--;
            setTimeout(function () {
                settime($(obj))
            }, 1000)
        }
    }
    var submitStatus = 1;
    $("#submit").on("click",function(){
        var memo = $.trim($("#memo").val());
        var address = $.trim($("#address").val());
        var sendCode = $.trim($("#sendCode").val());
        if(memo.length > 20 ){
            JuaBox.showWrong("${L:l(lan,"标签不得超过20个字符")}");
            return;
        }
        if(address == ""){
            JuaBox.showWrong("${L:l(lan,"请输入提现地址")}");
            return;
        }
        if(${curUser.withdrawAddressAuthenType == 2} && sendCode == ""){
            JuaBox.showWrong("${L:l(lan,"请输入验证码")}");
            return;
        }
        if(submitStatus==-1){
			 JuaBox.showWrong("${L:l(lan,"请勿重复提交！")}");
			return;
		}
        submitStatus = -1;
        $.ajax({
            url:"/manage/account/download/addAddress/${coint.propTag}",
            type:"post",
            dataType:"json",
            data:{
                address:address,
                memo:memo,
                mobileCode:sendCode
            },
            success:function(json){
                submitStatus = 1;
                if(json.isSuc){
                    JuaBox.showWrong(json.des);
                    setTimeout(function(){
                        window.top.location.href="/manage/account/download?coint=${coint.propTag}";
                    }, 1500);
                }
                else{
                    JuaBox.sure(json.des);
                }
            },
            error:function(err){
                submitStatus = 1;
                console.log(err)
            }
        })
        
    })
</script>
</body>
</html>