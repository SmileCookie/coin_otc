<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<div class="content" id="mainForm">
<form id="myForm">
	<input type="text" class="hid-input"/>
    <input type="password" class="hid-input"/>
	<h2>${L:l(lan,'修改手机')}</h2>
	<input type="hidden" id="method" value="${method}" />
	<div class="vip-tip">
		<dl>
			<dt>${L:l(lan,'温馨提示') }${L:l(lan,'：')}</dt>
			<dd>${L:l(lan,'发起修改手机申请后，我们的客服人员会尽快与您联系进行视频认证，如有疑问请联系在线客服。')}</dd>
		</dl>
	</div>
	<div class="fill-form" id="stepOne" <c:if test="${method==2}">style="display:none;"</c:if>>
		<div class="fill-form-bd">
			<div class="fill-group">
				<em class="name">${L:l(lan,'原手机号码')}${L:l(lan,'：')}</em>
				<input type="text" class="fill-control" readonly value="${mobile}">
			</div>
			<div class="fill-group clearfix">
				<em class="name">${L:l(lan,'原手机验证码')}${L:l(lan,'：')}</em>
				<div class="fill-flex">
					<input type="text" class="fill-control" id="mobileCode" name="mobileCode">
				    <a href="javascript:sendCode();" id="sendCodeBtn" class="hq btn btn-primary btn-sm wid80">${L:l(lan,'获取验证码')}</a>
				</div>
			</div>
		</div>
		<div class="fill-group">
			<em class="name"></em>
			<a href="javascript:setpOne()" class="btn btn-submit">
				${L:l(lan,'下一步')}
			</a>
			<a class="mbr15" href="javascript:skipStepOne()">${L:l(lan,'原手机已遗失')}</a>
		</div>
	</div>
	
	<div class="fill-form" id="stepTwo"  <c:if test="${method==1}">style="display:none;"</c:if>>
		<div class="fill-form-bd">
			<div class="fill-group">
				<em class="name">${L:l(lan,'新手机号码')}${L:l(lan,'：')}</em>
				<input type="text" class="fill-control" name="mobile" id="mobile" style="padding-left:95px;">
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
									<span class="talk-select-country-name"><c:if test="${coun.des!=''}">(${coun.des })</c:if>
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
				<em class="name">${L:l(lan,'新手机验证码')}${L:l(lan,'：')}</em>
				<div class="fill-flex">
					<input type="text" class="fill-control" id="newMobileCode" name="newMobileCode">
				    <a href="javascript:sendNewCode()" id="sendNewCodeBtn" class="hq btn btn-primary btn-sm wid80">${L:l(lan,'获取验证码')}</a>
				</div>
			</div>
			
			<div class="fill-group">
				<em class="name">${L:l(lan,'资金密码')}${L:l(lan,'：')}</em>
				<input type="password" class="fill-control" id="safePwd" name="safePwd">
			</div>
			<div class="fill-group ${googleAuth != 2?'hide':'' }">
				<em class="name">${L:l(lan,'Google验证码')}${L:l(lan,'：')}</em>
				<input type="text" class="fill-control" id="googleCode" name="googleCode">
			</div>

			<input type="hidden" id="codeType" name="codeType" value="3" />
			
		</div>
		<div class="fill-group">
			<em class="name"></em>
			<a href="javascript:save()" class="btn btn-submit">
				${L:l(lan,'提交')}
			</a>
			<a class="mbr15" href="/ac/safepwd_find" target="_blank">${L:l(lan,'忘记资金密码')}</a>
		</div>
	</div>
</form>	
</div>

<script type="text/javascript">
$(function(){
// 	showCode(true) ;
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
		$("#mCode").val(code);
		$("#mobile").focus();
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

function getCode(){
	$('#idMobCode').attr("src","/imagecode/get-28-80-32-"+new Date().getTime());
}

function setpOne() {
	var mobileCode = $('#mobileCode').val();
	$.ajax({
		type : "POST",
		url : "/manage/auth/doMobileModifyStepOne",
		data : {
			mobileCode : mobileCode,
		},
		dataType : "json",
		error : function() {
			JuaBox.sure(json.des);
			inAjaxing = false;
		},
		success : function(json) {
			inAjaxing = false;
			if (json.isSuc) {
				$('#stepOne').hide();
				$('#stepTwo').show();
			} else {
				JuaBox.sure(json.des);
			}
		}
	});
}

function skipStepOne() {
	$('#method').val(2);
	$('#stepOne').hide();
	$('#stepTwo').show();
}

function save() {
	var newMobile = $('#mobile').val();
	var newMobileCode = $('#newMobileCode').val();
	var safePwd = $('#safePwd').val();
	var method = $('#method').val();
	var mCode = $('#mCode').val();
	var googleCode = $('#googleCode').val();
	var codeType = $('#codeType').val();
	if(!safePwd){
		JuaBox.showTip(bitbank.L("请填写资金密码"));
		return false;
	}
	$.ajax({
		type : "POST",
		url : "/manage/auth/doMobileModify",
		data : {
			countryCode : mCode,
			newMobileNumber : newMobile,
			newMobileCode : newMobileCode,
			safePwd : safePwd,
			googleCode:googleCode,
			method : method,
			codeType : codeType
		},
		dataType : "json",
		error : function() {
			JuaBox.sure(json.des);
			inAjaxing = false;
		},
		success : function(json) {
			inAjaxing = false;
			if (json.isSuc) {
// 				JuaBox.sure(json.des, {btnFun1:function(JuaId) {
// 					 window.top.JuaBox.close(JuaId);
					 window.location.href="/manage/auth/mobile";
// 				}});
			} else if (json.datas.needStepOne == 1) {
				JuaBox.sure(json.des, {closeFun:function(JuaId) {
					$('#myForm')[0].reset();
					$('#stepOne').show();
					$('#stepTwo').hide();
				}});
			} else {
				JuaBox.sure(json.des);
			}
		}
	});
}
// function showCode(isShow) {
// 	var googleAuth = $('#googleAuth').val();
// 	var payGoogleAuth = $('#payGoogleAuth').val();
// 	if (isShow) {
//  		if ('2' == googleAuth && payGoogleAuth == 'true') {
// 			$('#validGoogleCode').removeClass('hide');
//  		}
// 	}
// }

function sendCode() {
	$.ajax({
		type : "POST",
		url : "/userSendCode",
		data : {
			codeType : 3
		},
		dataType : "json",
		error : function() {
			JuaBox.sure(jsLan[1]);
			inAjaxing = false;
		},
		success : function(json) {
			inAjaxing = false;
			if (json.isSuc) {
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
			codeType : 3
		},
		dataType : "json",
		error : function() {
			JuaBox.sure(jsLan[1]);
			inAjaxing = false;
		},
		success : function(json) {
			inAjaxing = false;
			if (json.isSuc) {
				settime2($('#sendNewCodeBtn'));
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

var countdown2 = 60;
function settime2(obj) {
	if (countdown2 == 0) {
		$(obj).removeClass("disabled");
		$(obj).text("${L:l(lan,'点击获取')}");
		countdown2 = 60;
	} else {
		$(obj).addClass("disabled");
		$(obj).text("${L:l(lan,'已发送')}"+"(" + countdown2 + ")");
		countdown2--;
		setTimeout(function() {
			settime2($(obj))
		}, 1000)
	}
}
</script>
