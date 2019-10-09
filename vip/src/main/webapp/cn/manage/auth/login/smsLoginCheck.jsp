<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 
<div class="content" id="mainForm">
	<h2>${func_des }</h2>
	<p>${L:l(lan,"更改安全设置需要身份验证后才能继续，请输入以下验证码") }</p>
	<div class="fill-form">
		<div class="fill-form-bd">
				<c:choose>
		        <c:when test="${curUser.userContact.mobileStatu==2 }">
							<div class="fill-group">
								<em class="name">${L:l(lan,'短信验证码') }${L:l(lan,'：') }</em>
								<input type="text" class="fill-control" id="mobileCode" name="mobileCode">
								<a href="javascript:sendCode();" id="sendCodeBtn" class="hq btn btn-five btn-sm">${L:l(lan,'获取短信验证码') }</a>
							</div>
						</c:when>
		        <c:otherwise>
							<div class="fill-group">
								<em class="name">${L:l(lan,'Google验证码$') }{L:l(lan,'：') }</em>
								<input type="text" class="fill-control" id="googleCode" name="googleCode">
							</div>
						</c:otherwise>
		     </c:choose>
		</div>
		<div class="fill-form-fd">
			<input type="hidden" id="ope" name="ope" value="${oper }"/>
			<a href="javascript:save();" class="btn btn-primary btn-lg btn-block">
				<c:if test="${oper == 1}">${L:l(lan,'开启异地登录验证')}</c:if>
				<c:if test="${oper == 0}">${L:l(lan,'关闭异地登录验证')}</c:if>
			</a>
		</div>
	</div>
</div>
<script type="text/javascript">
$(function(){
});

function save(){
	var mobileCode = $("#mobileCode").val();
	var googleCode = $("#googleCode").val();
// 	if(mobileCode.length < 6){
// 		return JuaBox.info("${L:l(lan,'请输入6位短信验证码')}");
// 	}
	$.ajax({
		type : "POST",
		url : "/manage/auth/changeSmsLoginCheck",
		data : {
			mobileCode: mobileCode,
			googleCode: googleCode,
			ope: $("#ope").val()
		},
		dataType : "json",
		error : function() {
			JuaBox.info("net error!");
		},
		success : function(json) {
			if (json.isSuc) {
				JuaBox.closeAll(function(){
		    		JuaBox.info(json.des,{btnFun1:function(){
		    			window.top.location.href = "/manage/auth";
		    			}
		    		});
		    });
			} else {
				JuaBox.info(json.des);
			}
		}
	});
	
}

function sendCode() {
	$.ajax({
		type : "POST",
		url : "/userSendCode",
		data : {
			codeType : 14
		},
		dataType : "json",
		error : function() {
			JuaBox.info(jsLan[1]);
			inAjaxing = false;
		},
		success : function(json) {
			inAjaxing = false;
			if (json.isSuc) {
				settime($('#sendCodeBtn'));
			} else if ('needMobileAuth' == json.des) {
				JuaBox.info(bitbank.L("您未进行手机认证，请先进行手机认证"));
			} else {
				JuaBox.info(json.des);
			}
		}
	});
}

var countdown = 60;
function settime(obj) {
	if (countdown == 0) {
		$(obj).removeAttr("disabled");
		$(obj).html("${L:l(lan,'点击获取')}");
		countdown = 60;
	} else {
		$(obj).attr("disabled", true);
		$(obj).html("${L:l(lan,'已发送')}"+"(" + countdown + ")");
		countdown--;
		setTimeout(function() {
			settime($(obj))
		}, 1000)
	}
}
	
</script>
