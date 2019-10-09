<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<div class="content" id="mainForm">
	<h2>${L:l(lan,"手机认证") }</h2>
	<c:choose>
	<c:when test="${verifyUserInfo.status == 0}">
		<div class="vip-tip">
			<dl>
				<dt>${L:l(lan,'温馨提示')}${L:l(lan,'：')}</dt>
				<fmt:formatDate value="${verifyUserInfo.addTimeShow}" pattern="${lan == 'en'? 'MM-dd-yyyy HH:mm:ss':'yyyy-MM-dd HH:mm:ss'}" var="addTimeShow" />
				<dd>${L:l1(lan,"您于%%提交了修改手机认证申请，我们会尽快为您审核。",addTimeShow)}</dd>
			</dl>
		</div>
	</c:when>
	<c:otherwise>
	<c:if test="${mobileStatu == 2}">
		<div class="vip-tip">
			<dl>
				<dt>${L:l(lan,'温馨提示')}${L:l(lan,'：')}</dt>
				<dd>
				<c:if test="${empty verifyUserInfo }">
					${L:l(lan,'您的手机已经通过认证')}<span class="text-primary">${mobile}</span>
				</c:if>
				<c:if test="${verifyUserInfo.status==2 }">
					${L:l(lan,'您提交的新手机号码已经通过审核。当前认证的手机为')}${L:l(lan,'：')}<span class="text-primary">${mobile}</span>
				</c:if>
				<c:if test="${verifyUserInfo.status==1}">
					${L:l(lan,'您提交的修改手机申请没有被通过审核。当前认证的手机为')}${L:l(lan,'：')}<span class="text-primary">${mobile}</span>
				</c:if>
				<c:if test="${verifyUserInfo.status==3}">
					${L:l(lan,'您提交的修改手机申请已经撤回，修改手机失败。当前认证的手机为')}${L:l(lan,'：')}<span class="text-primary">${mobile}</span>
				</c:if>
				<a class="btn btn-primary btn-sm ml15 wid80"	href="${vip_domain }/manage/auth/mobileModify?method=1">${L:l(lan,"修改")}</a>
				</dd>
			</dl>
		</div>
	</c:if>
	
	<c:if test="${mobileStatu != 2}">
		<div class="fill-form mt30">
			<div class="fill-form-bd">
				<div class="fill-group">
					<em class="name">${L:l(lan,'手机号码')}${L:l(lan,'：')}</em>
					<input type="text" class="fill-control" name="mobile" id="mobile" style="padding-left:90px;">
					
					<div id="country" class="goog-inline-block talk-select-country">
						<div class="goog-inline-block talk-select-country-caption">
							<div class="talk-flag" style="background-position: 0px -825px;"></div>
						</div>
						<input type="text" name="mCode"	id="mCode" class="fill-control fill-borno" value="+86" />
						<div class="country-icon">
							<span class="arrow"></span>
						</div>
					</div>
												
					<div class="goog-menu talk-select-country-menu" id="countryDownload" style="display: none;">
						<c:forEach items="${country}" var="coun" varStatus="st">
							<div class="goog-menuitem">
								<div class="goog-menuitem-content">
									<div>
										<div class="goog-inline-block">
											<div class="talk-flag" style="background-position: 0px ${coun.position};"></div>
										</div>
										<span class="talk-select-country-name"><c:if test="${coun.des!=''}">(${coun.des})</c:if>
										</span> <span class="talk-select-country-code" dir="ltr">${coun.code}</span>
									</div>
								</div>
							</div>
							<c:if test="${st.index==0}">
								<div class="goog-menuseparator"></div>
							</c:if>
						</c:forEach>
					</div>
					
				</div>
				<div class="fill-group clearfix">
					<em class="name">${L:l(lan,'图形验证码')}${L:l(lan,'：')}</em>
					<div class="fill-flex">
						<input type="text" class="fill-control" id="code" name="code">
					    <a class="hq"><img onclick="getCode()" src="/imagecode/get-28-80-32" id="idMobCode" /></a>
					</div>
				</div>
				<div class="fill-group clearfix">
					<em class="name">${L:l(lan,'短信验证码')}${L:l(lan,'：')}</em>
					<div class="fill-flex">
						<input type="text" class="fill-control" id="mobileCode" name="mobileCode">
						<a href="javascript:sendNewCode();" id="sendNewCodeBtn" class="hq btn btn-primary btn-sm wid80">${L:l(lan,'获取验证码')}</a>
					</div>
				</div>
				
				<div class="fill-group clearfix">
					<em class="name">${L:l(lan,'邮件验证码')}${L:l(lan,'：')}</em>
					<div class="fill-flex">
						<input type="text" class="fill-control" id="emailCode" name="emailCode">
						<a href="javascript:sendEmailCode();" id="sendEmailCodeBtn" class="hq btn btn-primary btn-sm wid80">${L:l(lan,'获取验证码')}</a>
					</div>
				</div>
				
				<div class="fill-group ${googleAuth != 2?'hide':'' } clearfix">
					<em class="name">${L:l(lan,'Google验证码')}${L:l(lan,'：')}</em>
					<input type="text" class="fill-control" id="googleCode" name="googleCode">
				</div>

				<input type="hidden" name="codeType" value="2" />
			</div>
			<div class="fill-group clearfix">
				<em class="name"></em>
				<a href="javascript:save()" class="btn btn-submit">
					${L:l(lan,'提交')}
				</a>
			</div>
		</div>
	</c:if>
	</c:otherwise>
	</c:choose>
</div>

<script type="text/javascript">
$(function() {
	$("#mainForm").Ui();

	$("#country").DropSelecter({
		ControlSeleter : "#countryDownload",
		Top : -1,
		Left : -5,
		StyleHover : ""
	});

	$("#countryDownload .goog-menuitem").click(function() {
		$this = $(this);
		var talkflag = $this.find(".goog-inline-block").html();
		var code = $this.find(".talk-select-country-code").text();

		$("#country").find(".talk-select-country-caption").html(talkflag);
		//if(code != "+86"){
		$("#mCode").val(code);
		$("#mobile").focus();
		//	$("#country").next(".mobi-right").children("input").val(code).focus();
		//}else{
		//	$("#country").next(".mobi-right").children("input").val("").focus();
		//}
		$("#countryDownload").hide();
	});

	$("#mobile").keyup(function() {
		var mobile = $("#mobile").val();
		if (mobile.length == 0) {
			$("#country").find(".talk-flag").css({
				"background-position" : "0px -825px"
			});
		}
	});

	vip.tips["输入手机号码"] = [ "请输入常用的手机号码。",
			"Please enter your mobile phone number commonly used." ];
});
function save() {
	var actionUrl = "/manage/auth/authMobile";
	vip.ajax({
		formId : "mainForm",
		url : actionUrl,
		dataType : "json",
		suc : function(json) {
// 			JuaBox.sure(json.des, {
// 				closeFun:function(){
					window.location.reload();
// 				}
// 			});
		},
		err : function(json) {
			JuaBox.sure(json.des);
		}
	});
}

function getCode(){
	$('#idMobCode').attr("src","/imagecode/get-28-80-32-"+new Date().getTime());
}

function sendCode() {
	var mCode = $('#mCode').val();
	var mobile = $('#mobile').val();
	$.ajax({
		type : "POST",
		url : "/manage/auth/authMobileSendCode",
		data : {
			mCode : mCode,
			mobile : mobile,
			codeType : 2
		},
		dataType : "json",
		error : function() {
			JuaBox.sure("");
			inAjaxing = false;
		},
		success : function(json) {
			inAjaxing = false;
			if (json.isSuc) {
				settime($('#sendCodeBtn'));
			} else {
				JuaBox.sure(json.des);
			}
		}
	});
}

function sendNewCode() {
	var newMobile = $('#mobile').val();
	var code = $('#code').val();
	var mCode = $('#mCode').val();
	if (!newMobile) {
		JuaBox.sure("${L:l(lan,'请输入正确的手机号！')}");
		return;
	}
	if (!code) {
		JuaBox.sure("${L:l(lan,'请输入图形验证码！')}");
		return;
	}
	$.ajax({
		type : "POST",
		url : "/manage/auth/authMobileSendCode/true",
		data : {
			code : code,
			mCode : mCode,
			mobile : newMobile,
			codeType : 2
		},
		dataType : "json",
		error : function() {
			JuaBox.sure(jsLan[1]);
			inAjaxing = false;
		},
		success : function(json) {
			inAjaxing = false;
			if (json.isSuc) {
				settime($('#sendNewCodeBtn'));
			} else if (json.datas.id == 'code') {
				JuaBox.sure(json.des, {btnFun1:function(JuaId) {
					window.top.JuaBox.close(JuaId);
					getCode();
				}});
			} else {
				JuaBox.sure(json.des);
			}
		}
	});
}

function sendEmailCode() {
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
				settime2($('#sendEmailCodeBtn'));
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
		$(obj).addClass("disabled", true);
		$(obj).text("${L:l(lan,'已发送')}"+"(" + countdown + ")");
		countdown--;
		setTimeout(function() {
			settime($(obj))
		}, 1000)
	}
}

var countdown2 = 60;
function settime2(obj) {
	if (countdown2 == 0) {
		$(obj).removeClass("disabled");
		$(obj).text("${L:l(lan,'点击获取')}");
		countdown2 = 60;
	} else {
		$(obj).addClass("disabled", true);
		$(obj).text("${L:l(lan,'已发送')}"+"(" + countdown2 + ")");
		countdown2--;
		setTimeout(function() {
			settime2($(obj))
		}, 1000)
	}
}
</script>
