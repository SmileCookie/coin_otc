<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 
<div class="content" id="mainForm">
	<h2>${func_des }</h2>
	<c:if test="${needAuth}">
		<div class="vip-tip">
			<dl>
				<dt>${L:l(lan,'重要提示') }：</dt>
				<dd>${L:l(lan,'邮件认证提示') }</dd>
			</dl>
		</div>
	</c:if>
	<c:if test="${!needAuth}">
		<p>${L:l(lan,"更改安全设置需要身份验证后才能继续，请输入以下验证码") }</p>
		<div class="fill-form">
			<div class="fill-form-bd">
				<div class="fill-group">
					<em class="name">${mobileStatu==2?L:l(lan,'短信验证码'):L:l(lan,'邮件验证码')}${L:l(lan,'：') }</em>
					<input type="text" class="fill-control" id="mobileCode" name="mobileCode">
					<a href="javascript:sendCode();" id="sendCodeBtn" class="hq btn btn-five btn-sm">${L:l(lan,'获取验证码') }</a>
				</div>
			</div>
			<div class="fill-form-fd">
				<input type="hidden" id="type" name="type" value="${codeType.key}"/>
				<input type="hidden" id="ope" name="ope" value="${oper }"/>
				<a href="javascript:save();" class="btn btn-primary btn-lg btn-block">
					<c:if test="${oper == 1}">${L:l(lan,'开启支付邮件验证')}</c:if>
					<c:if test="${oper == 0}">${L:l(lan,'关闭支付邮件验证')}</c:if>
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
	if(mobileCode.length < 6){
		return JuaBox.sure("${L:l(lan,'请输入6位短信验证码') }");
	}
	$.ajax({
		type : "POST",
		url : "/manage/auth/changePayEmailAuth",
		data : {
			mobileCode: mobileCode,
			ope: $("#ope").val(),
			type: $("#type").val()
		},
		dataType : "json",
		error : function() {
			JuaBox.sure("net error!");
		},
		success : function(json) {
			if (json.isSuc) {
				JuaBox.closeAll(function(){
		    		JuaBox.sure(json.des,{btnFun1:function(){
		    			window.top.location.href = "/manage/auth";
		    			}
		    		});
		    });
			} else {
				JuaBox.sure(json.des);
			}
		}
	});
	
}

//发送短信验证码
function sendCode() {
	$.ajax({
		type : "POST",
		url : "/userSendCode",
		data : {
			codeType : 14
		},
		dataType : "json",
		error : function() {
			JuaBox.sure(jsLan[1]);
			inAjaxing = false;
		},
		success : function(json) {
			inAjaxing = false;
			if (json.isSuc) {
				if (json.datas.isEmail) {
					JuaBox.sure(json.des);
				}
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
