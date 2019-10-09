<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ page session="false"%>
<div class="content" id="mainForm">
	<h2><c:if test="${hasSafePwd}">${L:l(lan,'修改资金密码')}</c:if><c:if test="${!hasSafePwd}">${L:l(lan,'设置资金密码')}</c:if></h2>
	<div class="vip-tip">
		<p><c:if test="${hasSafePwd}">${L:l(lan,'为了用户资金安全，修改安全密码后24小时内不可用1。')}</c:if><c:if test="${!hasSafePwd}">${L:l(lan,'设置资金密码提示')}</c:if></p>
	</div>
	<input type="hidden" id="googleAuth" value="${googleAuth}" />
	<div class="fill-form calm-up-this">
		<form autocomplete="off">
		<input type="password" class="hid-input">
		<div class="fill-form-bd">
			<c:if test="${hasSafePwd}">
			<input type="hidden" name="type" value="1" />
			<div class="fill-group">
				<em class="name">${L:l(lan,'当前密码')}${L:l(lan,'：')}</em>
				<input type="password" class="fill-control" id="currentPwd" name="currentPwd" autocomplete="off"/>
			</div>
			</c:if>
			<div class="fill-group bk-pwdcheck">
				<em class="name">${hasSafePwd ? L:l(lan,'新密码'):L:l(lan,'密码')}${L:l(lan,'：')}</em>
				<input type="password" class="fill-control" id="safePwd" name="safePwd" />
				<input id="safeLevel" type="hidden" name="safeLevel" value="0"/>
				<ul class="bk-table pwdstr-box">
					<li class="bk-cell strength"></li>
					<li class="bk-cell strength"></li>
					<li class="bk-cell strength"></li>
					<li class="bk-cell strength" id="pwdStr">${L:l(lan,'注册-右侧内容-标签-4')}</li>										
				</ul>
			</div>
			<div class="fill-group">
				<em class="name">${L:l(lan,'确认新密码')}${L:l(lan,'：')}</em>
				<input type="password" class="fill-control" id="newPwd" name="newPwd">
			</div>
			<div class="fill-group clearfix" id="validPhoneCode">
				<em class="name">${mobileStatu==2?L:l(lan,'短信验证码'):L:l(lan,'邮件验证码')}${L:l(lan,'：')}</em>
				<div class="fill-flex">
					<input type="text" class="fill-control" id="mobileCode" name="mobileCode">
				    <a href="javascript:sendCode();" id="sendCodeBtn" class="hq btn btn-primary btn-sm wid80">${L:l(lan,'获取')}</a>
				</div>
			</div>
			<div class="fill-group ${googleAuth ne 2?'hide':'' }">
				<em class="name">${L:l(lan,'Google验证码')}${L:l(lan,'：')}</em>
				<input type="text" class="fill-control" id="googleCode" name="googleCode">
			</div>
		</div>
		<div class="fill-group">
			<em class="name"></em>
			<a href="javascript:save();" class="btn btn-submit">
				${L:l(lan,'提交')}
			</a>
			<a class="mbr15" href="/ac/safepwd_find" target="_blank">${L:l(lan,'忘记资金密码')}</a>
		</div>
		</form>
	</div>
</div>


<script type="text/javascript" src="${static_domain }/statics/js/cn/user/pwd.js?V${CH_VERSON }"></script>
	<script type="text/javascript">
		$(function(){
			// $("#safePwd").keyup(qiangdu2).bind("focus", qiangdu2).bind("blur", qiangdu2);
			$("input[name=safePwd]").on({
				"keyup" : function(){
					if($(this).val() != ""){
						$(".pwdstr-box").show()
					}else{
						$(".pwdstr-box").hide()
					}
					checkPwdStrength($(this).val());
				},
				"blur" : function(){   
					$(".pwdstr-box").hide()
					checkPwdStrength($(this).val()); 
				},
				"change" : function(){ 
					checkPwdStrength($(this).val())
				}
			});
			
		});
		function checkPwdStrength(pwd,div){
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
			$(div).find("input[name='safeLevel']").val(level * 20);
		}
		
		function save() {
			var pwd = $("#safePwd").val();
		    var rePwd = $("#newPwd").val();
		    var pwdLevel = $("#safeLevel").val();
			if(pwd != rePwd) return JuaBox.showWrong(bitbank.L("两次密码输入不一致，请重新输入"));
			var level = 0;
			if (pwd.length >= 8 && pwd.length <= 20){
				    if (/\d/.test(pwd)) level++;
				    if (/[a-z]/.test(pwd)) level++;
				    if (/[A-Z]/.test(pwd)) level++; 
				    if (/\W/.test(pwd)) level++;
				 if(level<2){
					 return JuaBox.showWrong(bitbank.L("密码8~20位字符，且为字母、数字、符号等任意2种以上组合。"));
				 }
		     }else{
			    return JuaBox.showWrong(bitbank.L("密码8~20位字符，且为字母、数字、符号等任意2种以上组合。"));
		     }
			
			var actionUrl = "/manage/auth/pwd/safeUpdate";
			vip.ajax( {
				formId : "mainForm",
				url : actionUrl,
				suc : function(xml) {
					JuaBox.showWrong($(xml).find("MainData").text(),{
						closeFun:function(){
							location.href = "/manage";
						}
					})
				},
				err : function(xml) {
					JuaBox.sure($(xml).find("MainData").text());
				}
			});
		}

		function sendCode() {
			var pwd = $("#safePwd").val();
		    var rePwd = $("#newPwd").val();
		    var pwdLevel = $("#safeLevel").val();
			if(pwd != rePwd) return JuaBox.showWrong(bitbank.L("两次密码输入不一致，请重新输入"));
			var codeType = ${hasSafePwd ? 21:18};
			$.ajax({
				type : "POST",
				url : "/userSendCode",
				data : {
					codeType : codeType
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
