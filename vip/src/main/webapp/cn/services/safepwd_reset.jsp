<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${L:l(lan,'重置资金安全密码')}-${WEB_NAME }-${WEB_TITLE }</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0 maximum-scale=1, user-scalable=no">
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<link href="${static_domain }/statics/css/web.user.css?V${CH_VERSON }" rel="stylesheet" type="text/css" />
    <script>
        if(JuaBox.isMobile()){
						JuaBox.mobileFontSize();
						$("<link>").attr({ rel: "stylesheet",type: "text/css",href: "${static_domain }/statics/css/mobile/mobile.user.css?V${CH_VERSON }"}).appendTo("head");
            $("<link>").attr({ rel: "stylesheet",type: "text/css",href: "${static_domain }/statics/css/mobile/top_foot_mobile.css?V${CH_VERSON }"}).appendTo("head");
        }
    </script>
<script type="text/javascript">
$(function(){
	  if(JuaBox.isMobile()){
		  $(".bk-app").slideDown();
			var showMenuInter ;
			$("input,textarea").focus(function(){
				clearTimeout(showMenuInter);
				$(".bk-app").hide();
			});
			$("input,textarea").click(function(){
				clearTimeout(showMenuInter);
				$(".bk-app").hide();
			});
			$("input,textarea").blur(function(){
				showMenuInter = setTimeout(function(){
					$(".bk-app").slideDown();
				},1500);
			});	
		}
		 
	});
</script>
</head>

<body class="body-bg">
<div class="bk-body">
  <jsp:include page="/common/top.jsp" />
      <div class="logon-box">
		  <div class="container pass_finf_box">
		    <div class="bk-sign-main" id="logBox">
		      <div class="logoutbox sig_box sig_box_pwd">
		      <div class="bk-sign-tab">
		        <h4 class="text-h4">${L:l(lan,'重设资金安全密码')}</h4>
		      </div>
		      <div class="bk-sign-form">
		        <form role="form" id="regForm" class="form-horizontal" method="post" action="" autocomplete="off">
		          <div class="sig_text">${L:l(lan,'新的资金安全密码')}</div>
		          <div class="form-group line-bot" id="passwordForm">
		            <label class="sr-only" for="password">${L:l(lan,'新的资金安全密码')}</label>
		            <div class="input-group line-bot">
		              <div class="input-group-btn">
		                 <button type="button" class="btn text-nowrap"><i class="iconfont2">&#xe60a;</i></button>
		              </div>
		              <input type="password" class="form-control" id="password" name="password" tabindex="11">
		            </div>
		            <div class="bk-pwdcheck" id="pwdStrength" style="display:none">
		            	<input type="hidden" id="pwdLevel" name="pwdLevel" value="0" />
		            	<p class="text-left ft12"><span class="jingbao_icon"></span>&nbsp;${L:l(lan,'注册-右侧内容-标签-3')}</p>
			            <ul class="bk-table">
										<li class="bk-cell strength"></li>
										<li class="bk-cell strength"></li>
										<li class="bk-cell strength"></li>
										<li class="bk-cell strength" id="pwdStr">${L:l(lan,'注册-右侧内容-标签-4')}</li>										
									</ul>
		            </div>
		          </div>
		          <div class="sig_text">${L:l(lan,'重复输入密码')}</div>
		          <div class="form-group line-bot" id="confirmPwdForm">
		            <label class="sr-only" for="confirmPwd">${L:l(lan,'重复输入密码')}</label>
		            <div class="input-group">
		              <div class="input-group-btn">
		                 <button type="button" class="btn text-nowrap"><i class="iconfont2">&#xe60a;</i></button>
		              </div>
		              <input type="password" class="form-control" id="confirmPwd" name="confirmPwd" tabindex="12">
		            </div>
		          </div>
		          <div class="sig_text ${googleAu?'':'hide'}">${L:l(lan,'Google验证码')}</div>
		          <div class="form-group ${googleAu?'':'hide'} line-bot" id="googleCodeForm">
		            <label class="sr-only" for="googleCode">${L:l(lan,'Google验证码')}</label>
		            <div class="input-group">
		              <div class="input-group-btn">
		                 <button type="button" class="btn text-nowrap"><i class="iconfont2">&#xe60a;</i></button>
		              </div>
		              <input type="text" class="form-control" id="googleCode" name="googleCode" tabindex="13">
		            </div>
		          </div>
		          <div class="form-group btn_wrap">
		          	<input type="hidden" name="publicKey" value="" />
		          	<input type="hidden" id="userId" name="userId" value="${userId}"/>
                <input type="hidden" id="code" name="code" value="${code}"/>
		            <button id="submitBtn" type="button" class="btn btn-login" tabindex="15">${L:l(lan,'提交')}</button>
		          </div>
		        </form>
		      </div>
		    </div>
		  </div>
		 </div>
		 <jsp:include page="/common/foot.jsp" />
		</div>

<script type="text/javascript">
	  require(["module_market","module_asset","module_user","module_common"],function(market,asset,user){
		  market.init();
		  asset.init();
		  user.resetSafePwdPageInit();
	  });
</script>
</div>
</body>
</html>

