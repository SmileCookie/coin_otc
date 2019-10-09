<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${L:l(lan,'找回登录密码')}-${WEB_NAME }-${WEB_TITLE }</title>
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
		  $('#logBox').addClass("mobile");
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
		    	 <div class="logoutbox sig_box">
		      <div class="bk-sign-tab">
		        <h4 class="text-h4">${L:l(lan,'找回登录密码')}</h4>
		      </div>
		      <div class="bk-sign-form">
		        <form role="form" id="regForm" class="form-horizontal" method="post" action="" autocomplete="off">
		          <div class="sig_text">${L:l(lan,'登录-右侧内容-标签-1')}</div>
		          <div class="form-group line-bot" id="nikeForm">
		            <label class="sr-only" for="nike">${L:l(lan,'登录-右侧内容-标签-1')}</label>
		          	<div class="input-group">
							    <div class="input-group-btn">
							       <button type="button" class="btn"><i class="iconfont2">&#xe609;</i></button>
							    </div>
							    <input type="text" name="nike" id="nike" class="form-control" tabindex="10">
							    <div class="input-group-btn dropdown" id="countryGroup" style="display:none">
							      <div class="btn-group dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
							        <input type="hidden" id="countryCode" name="countryCode" value="+86">
							        <button type="button" class="btn text-nowrap" id="countryText" style="border-right:none;border-left:none;">+86</button>
								      <button type="button" class="btn" style="border-left:none;"><i class="iconfont ft12">&#xe600;</i></button>
								  	</div>
								  	<ul class="dropdown-menu" aria-labelledby="countryGroup" style="left:auto;right:0; width:350px; height:400px; overflow-x:hidden; overflow-y:auto;">
									  	<c:forEach items="${country}" var="coun">
	                    	<li data-value="${coun.code}" <c:if test="${coun.code=='+86'}">class="active"</c:if>><a>${coun.code} [<span>${coun.des}</span>]</a></li>
	                    </c:forEach>
	                  </ul>
					    		</div>
							  </div>
		          </div>
		          
		          <div class="form-group line-bot" id="cardIdForm" style="display: none;">
		            <label class="sr-only" for="cardId">${L:l(lan,'证件号码')}</label>
		          	<div class="input-group">
							    <div class="input-group-btn">
							       <button type="button" class="btn"><i class="iconfont">&#xe6bf;</i></button>
							    </div>
							    <input type="text" name="cardId" id="cardId" class="form-control" placeholder="${L:l(lan,'证件号码')}" tabindex="11">
							    <p></p>
							  </div>
							  <p class="text-left"><i class="iconfont">&#xe698;</i> ${L:l(lan,'如果帐户未实名认证此项可不填写')}</p>
		          </div>
		          
		          <div class="sig_text sigimg_text" >${L:l(lan,'图形验证码')}</div>
		          <div class="form-group line-bot" id="imgCodeForm">
		            <label class="sr-only" for="imgCode">${L:l(lan,'图形验证码')}</label>
		            <div class="input-group">
		              <div class="input-group-btn">
		                 <button type="button" class="btn"><i class="iconfont2">&#xe60b;</i></button>
		              </div>
		              <input type="text" name="imgCode" id="imgCode" class="form-control"tabindex="13">
		              <div class="input-group-btn">
		                <img src="/imagecode/get-28-100-50" alt="${L:l(lan,'点击刷新验证码')}" role="imgCode" />
		              </div>
		            </div>
		          </div>
		          <div class="sig_text msgCodeForm_text"style="display:none;">${L:l(lan,'注册-右侧内容-标签-9')}</div>
		          <div class="form-group line-bot" id="msgCodeForm" style="display:none">
								
		            <label class="sr-only" for="msgCode">${L:l(lan,'短信验证码')}</label>
		          	<div class="input-group">
							    <div class="input-group-btn">
							       <button type="button" class="btn text-nowrap"><i class="iconfont2">&#xe623;</i></button>
							    </div>
							    <input type="text" name="msgCode" id="msgCode" class="form-control"tabindex="14">
							    <div class="input-group-btn">
							      <div class="btn-group">
								      <button type="button" role="msgCode" id="sendMsgCode" class="btn text-nowrap line-left" style="border-left:none;">${L:l(lan,'点击获取')}</button>
								  	</div>
					    		</div>
							  </div>
		          </div>
		          
		          <!--<div class="form-group mt90">
				      	<p class="text-left">${L:l(lan,'温馨提示')}${L:l(lan,'：')}${L:l(lan,'若没有设置过登录密码可通过该方法设置。')}</p>
				      </div>-->
		          
		          <div class="form-group submitBtn_wrap">
		            <button id="submitBtn" type="button" class="btn btn-login" tabindex="15">${L:l(lan,'提交')}</button>
		          </div>
		          <!--<div class="form-group">
		            <a class="text-blue" href="${vip_domain }/ac/safepwd_find" target="_self" title="${L:l(lan,'找回资金安全密码')}">${L:l(lan,'找回资金安全密码')}</a>
 		             | 
							  <a class="" href="${vip_domain }/ac/lossMobile" target="_self" title="${L:l(lan,'手机挂失')}">${L:l(lan,'手机挂失')}</a> 
		          </div>  -->
		        </form>
		      </div>
		    </div>
		  </div>
        </div>
				
       </div>
			 <jsp:include page="/common/foot.jsp" />
<script type="text/javascript">
	  require(["module_market","module_asset","module_user","module_common"],function(market,asset,user){
		  market.init();
		  asset.init();
		  user.findLoginPwdPageInit();
	  });
</script>
</div>
</body>
</html>

