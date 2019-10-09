<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<div class="content" id="mainForm">
	<h2>${L:l(lan,"关闭Google认证") }</h2>
	<div class="fill-form mt30">
		<div class="fill-form-bd">
			<div class="fill-group">
				<em class="name">${L:l(lan,"Google验证码") }：</em>
				<input type="text" class="fill-control" id="gCode" name="gCode">
			</div>
			
			<div class="fill-group clearfix">
				<em class="name">${mobileStatu==2?L:l(lan,'短信验证码'):L:l(lan,'邮件验证码')}：</em>
				<div class="fill-flex">
					<input type="text" class="fill-control" id="mobileCode" name="mobileCode">
				    <a href="javascript:sendCode();" id="sendCodeBtn" class="hq btn btn-primary btn-sm wid80">${L:l(lan,"获取验证码") }</a>
				</div>
			</div>
		</div>
		<div class="fill-group">
			<c:if test="${googleAuth==2}">
				<em class="name"></em>
				<a href="javascript:closeGoogleAuth();" class="btn btn-submit ${mobileStatu!=2?'hide':'' }">
					${L:l(lan,'关闭认证')}
				</a>
			</c:if>
		</div>
	</div>
</div>

<script type="text/javascript">
function closeGoogleAuth() {
	var secret = $('#secret').val();
	var gCode = $('#gCode').val();
	var mobileCode = $('#mobileCode').val();
	var data = {
		gCode : gCode,
		mobileCode : mobileCode
	};
	$.ajax( {
		type: 'POST',
		url : '/manage/auth/doCloseGoogleAuth',
		dataType : 'json',
		data: data,
		success : function(json) {
			if (json.isSuc) {
				JuaBox.sure(json.des, {btnFun1:function(JuaId) {
					 window.top.JuaBox.close(JuaId);
					 window.location.href = "/manage";
				}});
			} else {
				JuaBox.sure(json.des);
			}
		},
		error :  function(json) {
// 			JuaBox.sure('网络访问出错，请稍后重试');
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
			settime($(obj));
		}, 1000)
	}
}
</script>

</body>
</html>
