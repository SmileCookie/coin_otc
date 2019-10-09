<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ page session="false"%>
<div class="content" id="mainForm">
	<h2>${L:l(lan,'登录密码设置')}</h2>
	<div class="vip-tip">
		<p>${L:l(lan,'登录密码长度8~20位，且为字母、数字、符号等任意2种以上组合。')}</p>
	</div>
	<div class="fill-form">
		<form autocomplete="off">
		<input type="hidden" class="hid-input">
		<div class="fill-form-bd">
			<div class="fill-group">
				<em class="name">${L:l(lan,'当前密码')}${L:l(lan,'：')}</em>
				<input type="password" class="fill-control" id="currentPassword" name="currentPassword" autocomplete="off" />
			</div>
			<div class="fill-group bk-pwdcheck">
				<!--<c:if test="${!noPwd }">${L:l(lan,'新')}</c:if>-->
				<em class="name">${L:l(lan,'新密码')}${L:l(lan,'：')}</em>
				<input type="password" class="fill-control" id="pwd" name="pwd" />
				<input id="pwdLevel" type="hidden" name="pwdLevel" value="0"/>
				<ul class="bk-table pwdstr-box">
					<li class="bk-cell strength"></li>
					<li class="bk-cell strength"></li>
					<li class="bk-cell strength"></li>
					<li class="bk-cell strength" id="pwdStr">${L:l(lan,'注册-右侧内容-标签-4')}</li>										
				</ul>
			</div>
			
			<div class="fill-group">
				<em class="name">${L:l(lan,'确认新密码')}${L:l(lan,'：')}</em>
				<input type="password" class="fill-control" id="repassWord" name="repassWord" />
			</div>
			<%-- <div class="fill-group">
				<em class="name">${mobileStatu==2?L:l(lan,'短信验证码'):L:l(lan,'邮件验证码')}：</em>
				<input type="text" class="fill-control" id="mobileCode" name="mobileCode">
				<a href="javascript:sendCode();" id="sendCodeBtn" class="hq btn btn-five btn-sm">获取验证码</a>
			</div>
			<div class="fill-group ${googleAuth ne 2?'hide':'' }">
				<em class="name">Google验证码：</em>
				<input type="text" class="fill-control" id="googleCode" name="googleCode">
			</div> --%>
		</div>
		<div class="fill-group">
		    <em class="name"></em>
			<input type="hidden" name="pubTag" value=""/>
			<a href="javascript:save();" class="btn btn-submit">
				${L:l(lan,'提交')}
			</a>
			<a class="mbr15" href="/ac/password_find" target="_blank">${L:l(lan,'忘记登录密码')}</a>
		</div>
	   </form>
	</div>
</div>


<script type="text/javascript">
$(function(){
	$("input[name=pwd]").on({
		"keyup" : function(){
			if($(this).val() != ""){
				$(".pwdstr-box").show()
			}else{
				$(".pwdstr-box").hide()
			}
			user.checkPwdStrength($(this).val());
		},
		"blur" : function(){   
			$(".pwdstr-box").hide()
			user.checkPwdStrength($(this).val()); 
		},
		"change" : function(){ 
			user.checkPwdStrength($(this).val())
		}
	});
	
	$.getJSON("/login/getPubTag?d="+new Date().getTime(),  function(json) {
		$('input[name="pubTag"]').val(json.datas.pubTag);
	});
});


var user = {
	checkPwdStrength : function(pwd,div){
		var level = 0, index = 1, div = div || ".bk-pwdcheck";
		if (pwd.length >= 8 && pwd.length <= 20){
			if (/\d/.test(pwd)) level++; 
			if (/[a-z]/.test(pwd)) level++; 
			if (/[A-Z]/.test(pwd)) level++; 
			if (/\W/.test(pwd)) level++; 
			if (level > 1 && pwd.length > 12) level++;
		}
		$(div).find(".strength").removeClass("open_active_1 open_active_2 open_active_3 open_active_2_2 open_active_3_2");
		if(level <= 2){
			$(div).find(".strength:nth-child(1)").addClass("open_active_1");
			$('#pwdStr').text(bitbank.L("注册-右侧内容-标签-4")).removeClass("med strong")			
		}
		if(level == 3) {
			$(div).find(".strength:nth-child(1)").removeClass("open_active_1").addClass("open_active_2_2");
			$(div).find(".strength:nth-child(2)").addClass("open_active_2");
			$('#pwdStr').text(bitbank.L('注册-右侧内容-标签-5')).removeClass("strong").addClass("med")
		}
		if(level > 3) {
			$(div).find(".strength:lt(2)").removeClass("open_active_2_2 open_active_2").addClass("open_active_3_2");
			$(div).find(".strength:nth-child(3)").addClass("open_active_3");
			$('#pwdStr').text(bitbank.L("注册-右侧内容-标签-6")).removeClass("med").addClass("strong")
		}
		$(div).find("input[name='pwdLevel']").val(level * 20);
	}
}

function save() {
	var datas = FormToStr("mainForm");
	if(datas==null){return;}
	
	var encrypt = new JSEncrypt({default_public_exponent : '65537'});
    var pubTag = $("input[name='pubTag']").val();
	encrypt.setPublicKey(pubTag);
    var currentPassword = $('#currentPassword').val();
    if(currentPassword.length == 0) return JuaBox.showWrong(bitbank.L("当前密码不能为空。"));
    currentPassword = encrypt.encrypt(currentPassword);
    var pwd = $("#pwd").val();
    var rePwd = $("#repassWord").val();
    var pwdLevel = $("#pwdLevel").val();
    console.log(pwdLevel);
    if(pwdLevel < 40) return JuaBox.showWrong(bitbank.L("密码8~20位字符，且为字母、数字、符号等任意2种以上组合。"));
	if(pwd != rePwd) return JuaBox.showWrong(bitbank.L("两次密码输入不一致，请重新输入"));

	pwd = encrypt.encrypt(pwd);
	
    var mobileCode = $('#mobileCode').val();
    var googleCode = $('#googleCode').val();
    var pwdLevel = $('#pwdLevel').val();
	var actionUrl = "/manage/auth/pwd/logupdate";
	
	$.ajax({
		type : "POST",
		url : actionUrl,
		data : {
			currentPassword: currentPassword,
			pwd: pwd,
// 			mobileCode: mobileCode,
// 			googleCode: googleCode,
			pwdLevel: pwdLevel
		},
		dataType : "json",
		error : function() {
			JuaBox.sure(jsLan[1]);
			inAjaxing = false;
		},
		success : function(json) {
			inAjaxing = false;
			if (json.isSuc) {
				JuaBox.sure(json.des, {
					closeFun:function() {
						location.href = '/manage';
					}
				});
			} else {
				JuaBox.sure(json.des);
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
