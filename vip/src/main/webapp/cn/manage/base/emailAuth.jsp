<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<form autocomplete="off">
	<input type="hidden" name="email" class="hid-input"/>
	<input type="hidden" class="hid-input" name="payPwd"/>
	<div class="content" id="mainForm">
	<h2>${L:l(lan,"邮箱认证") }</h2>
	<input type="hidden" id="step" value="${step}"/>
	<input type="hidden" id="mobileStatu" value="${mobileStatu}" />
	<c:if test="${safeAuth && emailStatu != 2}">
		<div class="vip-tip">
			<dl>
				<dt>${L:l(lan,'温馨提示')}</dt>
				<dd>
					${L:l(lan,'请设置资金安全密码后再进行邮箱认证操作。')}
					<a href="/manage/auth/pwd/safe"><strong>${L:l(lan,'资金安全密码设置') }</strong></a>
				</dd>
			</dl>
		</div>
	</c:if>
	
	<c:if test="${emailStatu == 2}">
		<div class="vip-tip">
			<dl>
				<dt>${L:l(lan,'温馨提示')}${L:l(lan,'：')}</dt>
				<dd>${L:l(lan,'您的邮箱已经通过认证')}<b style="color:#f00000;">${email}</b></dd>
			</dl>
		</div>
	</c:if>
	
	<c:if test="${emailStatu != 2}">
  	<c:choose>
  		<c:when test="${step == 'one'}">
	  		<div class="fill-form">
					<div class="fill-form-bd">
		  			<div class="fill-group">
							<em class="name">${L:l(lan,'原邮箱地址')}${L:l(lan,'：')}</em>
							<input type="text" class="fill-control" id="email" name="email" value="${email}" readonly="readonly" />
						</div>
						<div class="fill-group">
							<em class="name">${L:l(lan,'资金密码')}${L:l(lan,'：')}</em>
							<input type="password" class="fill-control" id="payPwd" name="payPwd">
						</div>
						<div class="fill-group">
							<em class="name"></em>
							<a href="javascript:sendemail();" class="btn btn-submit">
								${L:l(lan,'提交')}
							</a>
							<a class="mbr15" href="/ac/safepwd_find" target="_blank">${L:l(lan,'忘记资金密码')}</a>
						</div>
					</div>
				</div>
			</c:when>
			<c:when test="${step == 'next'}">
				<div class="vip-tip">
					<dl>
						<dt>${L:l(lan,'温馨提示') }${L:l(lan,'：')}</dt>
						<dd>
							<span>${email}</span> ${L:l(lan,'您的邮箱将收到一封邮件')}
							<a target="_blank" href="http://mail.${fn:split(source, '@')[1]}">${L:l(lan,'登录到邮箱')}</a>
							<input type="hidden" id="email" name="email" value="${source}" />
						</dd>
						<dd>${L:l(lan,'没有收到请重新发送')}<input type="button" class="hq btn btn-primary btn-sm wid80 ml20 bornone" onclick="sendemail('repost')" value="${L:l(lan,'重新发送')}"/></dd>
					</dl>
				</div>
			</c:when>
			<c:otherwise>
				<div class="fill-form">
					<div class="fill-form-bd">
		  			<div class="fill-group">
							<em class="name">${step=="third" ? L:l(lan,'新') : ""}${L:l(lan,'邮箱地址')}${L:l(lan,'：')}</em>
							<input type="text" class="fill-control" id="email" name="email" autocomplete="off" />
						</div>
						<div class="fill-group">
							<em class="name">${L:l(lan,'资金密码')}${L:l(lan,'：')}</em>
							<input type="password" class="fill-control" id="payPwd" name="payPwd" autocomplete="off" />
						</div>
						<div class="fill-group ${mobileStatu!=2?'hide':'' } clearfix" id="validPhoneCode">
							<em class="name">${mobileStatu==2?L:l(lan,'短信验证码'):L:l(lan,'邮件验证码')}${L:l(lan,'：')}</em>
							<div class="fill-flex">
								<input type="text" class="fill-control" id="mobileCode" name="mobileCode">
							    <a href="javascript:sendCode();" id="sendCodeBtn" class="hq btn btn-primary btn-sm wid80">${L:l(lan,'获取验证码')}</a>
							</div>
						</div>
						<div class="fill-group">
							<em class="name"></em>
							<a onClick="javascript:sendemail('hassend');" href="javascript:void(0);" class="btn btn-submit">
								${L:l(lan,'提交')}
							</a>
							<a class="mbr15" href="/ac/safepwd_find" target="_blank">${L:l(lan,'忘记资金密码')}</a>
						</div>
					</div>
				</div>
			
			
			</c:otherwise>
		</c:choose>
	</c:if>
   </div>
</form>

<script type="text/javascript">
var timeInterval = "";
$(function(){
});
function sendCode() {
	$.ajax({
		type : "POST",
		url : "/userSendCode",
		data : {
			codeType : 7
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
		$(obj).text("${L:l(lan,'点击获取')}");
		countdown = 60;
	} else {
		$(obj).attr("disabled", true);
		$(obj).text("${L:l(lan,'已发送')}"+"(" + countdown + ")");
		countdown--;
		setTimeout(function() {
			settime($(obj))
		}, 1000)
	}
}
function sendemail(key) {
	var mobileCode = $('#mobileCode').val();
	var emailAddress = $('#email').val();
	var payPwd = $('#payPwd').val();
	var valid = '0';
	if (key != "repost" && (!emailAddress || !method.isEmail(emailAddress))){
	    JuaBox.showTip(bitbank.L("请填写您邮箱地址"));
		return false;
	}
	if (key != "repost" && !payPwd){
	    JuaBox.showTip(bitbank.L("请填写资金密码"));
		return false;
	}
	if (key != "repost" && !$('#validPhoneCode').hasClass('hide')) {
 		valid = '1';
		if (!mobileCode) {
			JuaBox.showTip(bitbank.L("请填写您收到的验证码"));
 			return false;
 		}
 	}
	var step = $("#step").val();
	if(key){
		step = key;
	}
	var actionUrl = "/manage/auth/logic/postemail?step="+step;
	vip.ajax( {
		formId : "mainForm",
		url : actionUrl,
		dataType : "json",
		suc : function(json) {
			if(step == "repost"){
				JuaBox.showTip(json.des);
			}else{
				JuaBox.sure(json.des, {
					closeFun :function(){
						location.href = "/manage/auth/email?edit=true&step=next";
					}
				});
			}
		},
		err : function(json){
				JuaBox.sure(json.des);
		}
	});
}
var lastEmailJson = {v : "" , suc : false , tips : ""};
function checkEamil(){
	var isOk = false;
	var $this = $("#email");
	if ($this.val() == null || $this.val() == "")
		return false;
	if ($this.attr("errorstyle")){
		return false;
	}
	var curVal = $this.val();
	if((lastEmailJson.v != "") && (lastEmailJson.v == curVal)){
		if(!lastEmailJson.suc){
			vip.form.error = lastEmailJson.tips;
		}
		return lastEmailJson.suc;
	}else{
		vip.ajax({
			url : "/manage/auth/logic/emailUnique?val="+ encodeURIComponent(curVal),
			async: false,
			dataType : "json",
			suc : function(json) {
				lastEmailJson.v = curVal;
				lastEmailJson.suc = true;
				isOk = true;
			},
			err : function(json){
				var msg = json.des;
				vip.form.error = msg;
				lastEmailJson.v = curVal;
				lastEmailJson.suc = false;
				lastEmailJson.tips = msg;
			}
		});
	}
	return isOk;
}
</script>
