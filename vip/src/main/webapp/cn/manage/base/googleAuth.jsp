<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="content" id="mainForm">
	<h2>${L:l(lan,"Google验证") }</h2>
	<input type="hidden" id="mobileStatu" value="${mobileStatu}" />
	<fmt:formatDate value="${verifyUserInfo.addTimeShow}" pattern="${lan == 'en'? 'MM-dd-yyyy HH:mm:ss':'yyyy-MM-dd HH:mm:ss'}" var="addTimeShow" />
	<c:if test="${empty method && method!=1 && googleAuth==2}">
		<div class="vip-tip">
			<dl>
				<dt>${L:l(lan,'温馨提示')}${L:l(lan,'：')}</dt>
				<dd>
				<c:choose>
					<c:when test="${verifyUserInfo.status == 0}">
						${L:l1(lan,'您于%%提交了修改Google验证申请，我们会尽快为您审核。',addTimeShow)}
					</c:when>
					<c:otherwise>
						<c:if test="${verifyUserInfo.status==2 }">
							${L:l(lan,'您提交的修改谷歌认证申请已经通过审核。')}<span class="text-primary">${mobile}</span>
						</c:if>
						<c:if test="${verifyUserInfo.status==1}">
							${L:l(lan,'您提交的修改谷歌认证申请没有被通过审核。')}
						</c:if>
						<c:if test="${verifyUserInfo.status==3}">
							${L:l(lan,'您提交的修改谷歌认证申请已经撤回。')}
						</c:if>
						${L:l(lan,'您已开启Google认证。')}
						<a class="btn btn-primary ml15 wid80" href="${vip_domain }/manage/auth/google?method=1">${L:l(lan,"修改")}</a>
						<a class="btn btn-set ml15 ${mobileStatu!=2?'hide':'' } wid80" href="${vip_domain }/manage/auth/closeGoogleAuth">${L:l(lan,"关闭")}</a>
					</c:otherwise>
				</c:choose>
				</dd>
			</dl>
		</div>
	</c:if>
	
	<c:if test="${method==1 || googleAuth!=2 }">
		<c:if test="${verifyUserInfo.status == 0}">
			<div class="vip-tip">
				<dl>
					<dt>${L:l(lan,'温馨提示') }：</dt>
					<dd>${L:l1(lan,'您于%%提交了修改Google验证申请，我们会尽快为您审核。',addTimeShow)}</dd>
				</dl>
			</div>
		</c:if>
		
		<c:if test="${empty verifyUserInfo || verifyUserInfo.status ne 0}">
			<div class="vip-tip dl-l-pad">
				<dl class="clearfix" >
					<img src="/manage/getGoogleAuthQr?secret=${secret }" class="pull-left mr15" style="width:120px; height:auto;"/>
					<!--<dt>${L:l(lan,'使用帮助') }：</dt>-->
					<dd style="margin-top:-5px">1.&nbsp;${L:l(lan,'下载并安装Google身份验证器。')}</dd>
					<dd>2.&nbsp;${L:l(lan,'扫描左侧二维码添加密钥，或手动输入密钥：')}&nbsp;<font color="#ff0000;">${secret}</font></dd>
					<dd>3.&nbsp;${L:l(lan,'输入Google验证码和短信验证码（选择邮件注册，且尚未绑定手机的用户，需要输入邮件验证码）。')}</dd>
					<dd>4.&nbsp;${L:l(lan,'为了防止安装Google身份验证器的设备丢失给您带来的不便，建议您保存好btcwinex帐户名和Google密钥，或者下载Google验证设置页面的二维码图片。')}</dd>
					<dd>5.&nbsp;${L:l(lan,'更多详情请参看')}&nbsp;<a class="google_text_a" href="${vip_domain}/login/zendesk/?viewFlag=googleauth" target="_blank" >${L:l(lan,'帮助中心_1')}</a> ${L:l(lan,'。')}</dd>
					<!--<dd>5.&nbsp;${L:l(lan,'请勿删除此双重验证密码账户，否则会导致您无法进行账户操作；如果误删，您可通过重置密钥重新获取。')}</dd>-->
				</dl>
			</div>
		
			<div class="fill-form">
				<div class="fill-form-bd">
					<div class="fill-group">
						<em class="name">${L:l(lan,'密钥')}：</em>
						<input type="text" class="fill-control bg-gray" id="secret" name="secret" value="${secret}" readonly>
					</div>
					
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
					<em class="name"></em>
					<c:if test="${googleAuth!=2}">
						<a href="javascript:openGoogleAuth();" class="btn btn-set btn-submit">
							${L:l(lan,'验证并开启')}
						</a>
					</c:if>
					<c:if test="${googleAuth==2}">
						<a href="javascript:openGoogleAuth();" class="btn btn-set btn-submit">
							${L:l(lan,'修改认证')}
						</a>
					</c:if>
				</div>
			</div>
		</c:if>
	</c:if>
	
</div>



<script type="text/javascript">
function loginGoogleAuth(ope) {
	JuaBox.frame('/manage/auth/loginGoogleAuth?ope='+ope);
}
function payGoogleAuth(ope) {
	JuaBox.frame('/manage/auth/payGoogleAuth?ope='+ope);
}

function closeGoogleAuth() {
	var secret = $('#secret').val();
	var gCode = $('#gCode').val();
	var mobileCode = $('#mobileCode').val();
	mobileCode = $.trim(mobileCode);
	var data = {
		secret : secret,
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
// 				JuaBox.sure(json.des, {btnFun1:function(JuaId) {
// 					 window.top.JuaBox.close(JuaId);
					 window.location.href = "/manage/auth/google";
// 				}});
			} else {
				JuaBox.sure(json.des);
			}
		}
	});
}



function openGoogleAuth() {
	var secret = $('#secret').val();
	var gCode = $('#gCode').val();
	var mobileCode = $('#mobileCode').val();
	mobileCode = $.trim(mobileCode);
	var data = {
		secret : secret,
		gCode : gCode,
		mobileCode : mobileCode
	};
	$.ajax( {
		type: 'POST',
		url : '/manage/auth/openGoogleAuth',
		dataType : 'json',
		data: data,
		success : function(json) {
			if (json.isSuc) {
// 				JuaBox.sure(json.des, {btnFun1:function(JuaId) {
// 					 window.top.JuaBox.close(JuaId);
					 window.location.href = "/manage/auth/google";
// 				}});
			} else {
				JuaBox.sure(json.des);
			}
		}
	});
}
$(function() { 
	showCode(true);
	}); 
function showCode(isShow) {
	var mobileStatu = $('#mobileStatu').val();
	if (isShow) {
		if ('2' == mobileStatu)  {
			$('#validPhoneCode').removeClass('hide');
		}else{
			$('#validEmailCode').removeClass('hide');
		}
	}
}

function sendCode() {
	$.ajax({
		type : "POST",
		url : "/userSendCode",
		data : {
			codeType : 5
			//codeType : 14
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
		$(obj).removeClass("disabled");
		$(obj).text("${L:l(lan,'点击获取')}");
		countdown = 60;
	} else {
		$(obj).addClass("disabled");
		$(obj).text("${L:l(lan,'已发送')}"+"(" + countdown + ")");
		countdown--;
		setTimeout(function() {
			settime($(obj))
		}, 1000)
	}
}

</script>
