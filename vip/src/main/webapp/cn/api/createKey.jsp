<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ page session="false"%>
<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${L:l(lan,'API设置')}-${WEB_NAME }-${WEB_TITLE }</title>
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<link rel="stylesheet" href="${static_domain }/statics/css/web.user.css?V${CH_VERSON }">
</head>
<body class="">

<div class="bk-body">
	<!-- Common TopMenu Begin -->
	<jsp:include page="/common/top.jsp" />
	<!-- Common TopMenu End -->
	<!-- Body From mainPage Begin -->
	<div class="zh-trade">
		<div class="container">
			<div class="user-panel">
				<jsp:include page="/cn/manage/auth/menu.jsp"/>
				<!-- 内容 start -->
				<div class="content" id="mainForm">
					<h2>${L:l(lan,"API密钥生成")}</h2>
					<div class="fill-form">
					<div class="fill-form-bd">
						
						<c:if test="${mobileStatu==2}">	
							<div class="fill-group">
								<em class="name">${mobileStatu==2?L:l(lan,'短信验证码'):L:l(lan,'邮件验证码')}：</em>
								<input type="text" class="fill-control" id="mobileCode" name="mobileCode">
								<a href="javascript:sendCode();" id="sendCodeBtn" class="hq btn btn-five btn-sm">获取验证码</a>
							</div>
						</c:if>
						
						
						<c:if test="${googleAuth==2}"> 
							<div class="fill-group">
								<em class="name">${L:l(lan,"Google验证码")}：</em>
								<input type="text" class="fill-control" id="googleCode" name="googleCode">
							</div>
						</c:if>
						
					</div>
					<div class="fill-form-fd text-center">
						<a href="javascript:save();"  class="btn btn-primary btn-block mb15">${L:l(lan,'提交')}</a>
					</div>
					
				</div>
				
				
				<!-- 内容end -->
			</div>
			<input type="hidden" id="needMobile" name="needMobile" value="false"/>
			<input type="hidden" id="needPwd" name="needPwd" value="false"/>
		</div>
	</div>
      
	<!-- Body From mainPage End -->
	<!-- Common FootMain Begin -->
	<jsp:include page="/common/foot.jsp" />
	<!-- Common FootMain End -->
</div>

<script type="text/javascript">

/**
 * 保存绑定IP地址
 */
function save(){
	var mobileCode = $('#mobileCode').val();
	var googleCode = $('#googleCode').val();
	var data = {
		mobileCode : mobileCode,
		googleCode:googleCode
	}
	
	
	$.ajax({
		type: 'POST',
		url : "/manage/api/doCreateKey",
		dataType : 'json',
		data:data,
		success : function(json) {
			if (json.isSuc) {
				 window.location.href = "/manage/api";
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

seajs.use('module_user',function(user){
	user.pageIndexInit();
});	
</script>

</body>
</html>
