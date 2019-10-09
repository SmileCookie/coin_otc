<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ page session="false"%>
<div class="content">
	<h2>API设置</h2>
        		
        		<div class="vip-tip">
							<dl>
								<c:if test="${apiStatus eq 0  }">
								<dd>当前API交易状态：<em class="text-success">${L:l(lan,"已关闭")}</em></dd>
								<dd><a role="button" class="btn btn-primary" onclick="openApi()">${L:l(lan,'开启API')}</a></dd>
								</c:if>
		       			<c:if test="${apiStatus eq 1 }">
								<dd>当前API交易状态：<em class="text-danger">${L:l(lan,"已开启")}</em></dd>
								<dd><a role="button" class="btn btn-primary" onclick="closeApi()">${L:l(lan,'关闭API')}</a></dd>
							</c:if>
							</dl>
						</div>
					
					<div class="fill-form" id="toggle_api_div_group" style="display:none;">
						<div class="fill-form-bd">
						<c:if test="${apiStatus!=1}">
							<div class="fill-group">
								<em class="name">${L:l(lan,"绑定API交易IP")}：</em>
								<input type="text" class="fill-control" id="ipaddrs" placeholder="可以添加多个，为空则不限制用，“，”（逗号）分隔多个IP" name="ipaddrs"  value="${ipaddrs }"/><br/>
							</div>
						</c:if>
						<c:if test="${mobileStatu==2}">	
							<div class="fill-group">
								<em class="name">${mobileStatu==2?L:l(lan,'短信验证码'):L:l(lan,'邮件验证码')}：</em>
								<input type="text" class="fill-control" id="mobileCode" name="mobileCode">
								<a href="javascript:sendCode();" id="sendCodeBtn" class="hq btn btn-five btn-sm">获取验证码</a>
							</div>
						</c:if>
						<c:if test="${googleAuth==2}">
							<div class="fill-group">
								<em class="name">Google验证码：</em>
								<input type="text" class="fill-control" id="googleCode" name="googleCode">
							</div>
						</c:if>
						
					</div>
					<div class="fill-form-fd text-center">
						<c:if test="${apiStatus!=1}">
							<a href="javascript:openOrClose('open');" class="btn btn-primary btn-block">
								${L:l(lan,'开启')}
							</a>
						</c:if>
						<c:if test="${apiStatus==1}">
							<a href="javascript:openOrClose('close');" class="btn btn-primary btn-block">
								${L:l(lan,'关闭')}
							</a>
						</c:if>
					</div>
				</div>
		    	
		    	<c:if test="${apiStatus eq 1 }">
		    	<h2>API密钥管理</h2>
		    		<div class="vip-tip">
							<dl>
								<dd>${L:l(lan,'API访问密钥')}(Access Key)： <b>${accessKey }</b> </dd>
								<dd>${L:l(lan,'API私有密钥')}(Secret Key)：<b>
										<c:if test="${not empty secretKey  }">${secretKey }</c:if>
	          				<c:if test="${empty secretKey }">*******************************************</c:if></b>
								</dd>
								<dd class="text-danger">${L:l(lan,'API私有密钥')}${L:l(lan,'仅显示一次，若遗忘请重新生成')}</dd>
								<dd><a role="button" class="btn btn-primary" href="/manage/api/createKey" target="_self">${L:l(lan,'生成密钥')}</a></dd>
							</dl>
						</div>
		    	
		    	<h2>API绑定IP</h2>
		    	<div class="vip-tip">
							<dl>
								<dd><b>温馨提示：</b>${L:l(lan,"只有绑定的IP才能够通过API进行访问，如果不希望限制则留空。变更IP五分钟后生效。")}</dd>
								<dd>当前绑定IP：
										<c:choose>
											<c:when test="${not empty ipaddrs}"><b class="">${ipaddrs }</b></c:when>
											<c:otherwise><b class="">${L:l(lan, '不绑定ip，不限制')}</b></c:otherwise>
										</c:choose>
								</dd>
								<dd><a role="button" class="btn btn-primary" href="/manage/api/updateIp" target="_self">${L:l(lan,'设置IP')}</a></dd>
							</dl>
						</div>
		    	
		    	</c:if>
				<input type="hidden" id="needMobile" name="needMobile" value="false"/>
				<input type="hidden" id="needPwd" name="needPwd" value="false"/>
	
</div>
<script type="text/javascript">
	$(function(){
		$("#google").addClass("current");
		$("#mainForm").Ui();
		$("#b_41").addClass("active");
	});
	
	function openApi(){
		$("#open_api_tips_div").hide();
		$("#toggle_api_div_group").toggle();
	}
	
	function closeApi(){
		$("#toggle_api_div_group").toggle();
		$(".bind_ip_div").hide();
		$("#api_info").hide();
	}
	
	function openOrClose(oper){
		var mobileCode = $('#mobileCode').val();
		var googleCode = $('#googleCode').val();
		var ipaddrs = $('#ipaddrs').val();
		var data = {
			mobileCode : mobileCode,
			googleCode:googleCode,
			ipaddrs:ipaddrs
		};
		
		
		
		$.ajax({
			type: 'POST',
			url : "/manage/api/openOrClose?oper="+oper,
			dataType : 'json',
			data:data,
			success : function(json) {
				if (json.isSuc) {
					JuaBox.sure(json.des, {btnFun1:function(JuaId) {
						 window.top.JuaBox.close(JuaId);
						 window.location.href = "/manage/api";
					}});
				} else {
					JuaBox.sure(json.des);
				}
			},
			error :  function(json) {
				JuaBox.sure('网络访问出错，请稍后重试');
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