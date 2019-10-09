<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 
<div class="content" id="mainForm">
	<h2>${func_des }</h2>
	<c:if test="${needAuth}">
		<div class="vip-tip">
			<dl>
				<dt>${L:l(lan,'重要提示') }${L:l(lan,'：') }</dt>
				<dd>${L:l(lan,'Google认证提示') }</dd>
			</dl>
		</div>
	</c:if>
	<c:if test="${!needAuth}">
		<p>${L:l(lan,"更改安全设置需要身份验证后才能继续，请输入以下验证码") }</p>
		<div class="fill-form">
			<div class="fill-form-bd">
				<div class="fill-group">
					<em class="name">${L:l(lan,'短信验证码') }${L:l(lan,'：') }</em>
					<input type="text" class="fill-control" id="mobileCode" name="mobileCode">
					<a href="javascript:sendCode();" id="sendCodeBtn" class="hq btn btn-five btn-sm">${L:l(lan,'获取短信验证码') }</a>
				</div>
				<div class="fill-group">
					<em class="name">${L:l(lan,'Google验证码') }${L:l(lan,'：') }</em>
					<input type="text" class="fill-control" id="googleCode" name="googleCode">
				</div>
			</div>
			<div class="fill-form-fd">
				<input type="hidden" id="ope" name="ope" value="${oper }"/>
				<a href="javascript:save();" class="btn btn-primary btn-lg btn-block">
					<c:if test="${oper == 1}">${L:l(lan,'开启登录Google验证')}</c:if>
					<c:if test="${oper == 0}">${L:l(lan,'关闭登录Google验证')}</c:if>
				</a>
			</div>
		</div>
	</c:if>
</div>

<script type="text/javascript">
$(function(){
});

function save(){
	var mobileCode = $("#mobileCode").val();
	var googleCode = $("#googleCode").val();
	if(mobileCode.length < 6){
		return JuaBox.info("请输入6位短信验证码");
	}
	if(googleCode.length < 6){
		return JuaBox.info("请输入6位google验证码");
	}
	$.ajax({
		type : "POST",
		url : "/manage/auth/changeLoginGoogleAuth",
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
				if (json.datas.isEmail) {
					JuaBox.info(json.des);
				}
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
