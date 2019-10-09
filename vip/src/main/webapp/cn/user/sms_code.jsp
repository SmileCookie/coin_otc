<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/common/head.jsp" />
<script type="text/javascript">
document.domain = "${baseDomain}";
$(function() {
	$("#uimain").Ui();
	$("#ajax_phone_get").bind("click", getMobileCode);
	waitTime();
});

function getMobileCode(){
	var actionUrl = "/user/postcode?uid="+encodeURIComponent($("#nike").val());
	vip.ajax({
		url : actionUrl,
		dataType : "json",
		suc : function(json) {
			waitTime();
		}
	});
}

var timeInterval = "";
window.clearInterval(timeInterval);
function waitTime(){
	$("#ajax_phone_get").attr("disabled", "disabled");
	var count = 120;
	window.clearInterval(timeInterval);
	var timeInterval = window.setInterval(function(){
		count --;
		if(count == 0){
			window.clearInterval(timeInterval);
			$("#ajax_phone_get").text("获取验证码").removeAttr("disabled");	
		}else{
			$("#ajax_phone_get").text(count+"s");								
		}
	}, 1000);
} 

var inAjaxing=false;
function doLogin(){
	if(inAjaxing){
		return;
	}
	var datas = FormToStr("uimain");
    if(datas == null)
    	return;
    inAjaxing = true;	
	var actionUrl = "/user/doLogin?callback=?&"+datas;
	$.getJSON(actionUrl, function(json){
		inAjaxing = false;
		if(json.isSuc){
			parent.Redirect(json.des);
		}else{
			error(json.des);
		}
	});
}

function error(msg){
  	$("#message").html(msg).fadeIn(300).delay(3000).fadeOut(300);
}
</script>
<style>
#uimain { text-align:center; padding:20px 0;}
#uimain input#mobileCode { width:100px; height: 20px; line-height: 20px; border: 1px solid #ddd; padding: 5px 10px; vertical-align: bottom;}
.uido { padding-top:30px; border-top:1px solid #ddd; width:95%; margin:0 auto; vertical-align: bottom;}
#uimain button { width:80px; height:30px;}
#message{margin-top: 3px;color: red;}
</style>
<div class="popIframeTitle"><div class="popIframeTitle">异地登录验证</div><div class="popIframeCloseC"><a title="关闭" href="javascript:Close()" onfocus="this.blur()" class="popIframeClose">Close</a></div></div>
<div class="ctips" style="width:80%;">
<p>您的账户本次登录的IP所在地与最后一次登录地不一致，需要验证您的手机。</p>
</div>
<div id="uimain">
	短信验证码：<input type="text" class="txt" name="mobileCode" id="mobileCode" value="" position="s" 
	mytitle="请输入发送到您手机上的验证码" errmsg="验证码错误" 
	errorName="验证码" pattern="limit(4,10)"/>
	<button type="button" id="ajax_phone_get">${L:l(lan,"获取验证码")}</button>
	<input type="hidden" name="nike" id="nike" value="${nick }"/>
	<input type="hidden" name="pwd" id="pwd" value="${pwd }"/>
	<div id="message" style="display: none;"></div>
</div>
<div class="uido">
    <a class="alibtn_orange35" href="javascript:;" onclick="parent.Close()"><h4>${L:l(lan,"取消")}</h4></a>
    <a class="alibtn_orange35" href="javascript:;" onclick="doLogin()" style="margin-left:36px;"><h4>${L:l(lan,"登录")}</h4></a>
</div>
