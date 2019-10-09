<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${L:l(lan,'帐号登录')}-${WEB_NAME }-${WEB_TITLE }</title>
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
		  // $('#logBox').addClass("mobile");
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

<body class="sig-body-bg">
	<div class="bk-body">
		<jsp:include page="/common/top.jsp" />
		<div class="logon-box">
		  <div class="container">
		     <div class="bk-sign-main sig_box clearfix" id="logBox">
		      <div class="logoutbox">
		       <div id="vipLogin">
						 <!--<div class="sig_top clearfix">
							 Please confirm that you are visiting!
							 <div class="top_right">
								<span class="top_suo"></span>
								<span>I</span>
								https://www.bitglobal.com
							 </div>
						 </div>-->
			      <div class="bk-sign-tab">
			        <h4 class="text-h4">${L:l(lan,'登录-右侧内容-标题-1')}</h4>
			      </div>
			      <div class="bk-sign-form">
			        <form role="form" id="logform" class="form-horizontal" method="post" action="" autocomplete="off">
			          <input type="hidden" name="publicKey" value=""/>
								<div class="sig_text">${L:l(lan,'登录-右侧内容-标签-1')}</div>
			          <div class="form-group line-bot" id="nikeForm">
			            <label class="sr-only" for="nike">${L:l(lan,'登录-右侧内容-标签-1')}</label>
      				<div class="input-group">
				    <div class="input-group-btn">
				       <button type="button" class="btn"><i class="iconfont2">&#xe609;</i></button>
				    </div>
				    <input type="text" name="nike" id="nike" class="form-control" tabindex="10">
						<input type="hidden" id="returnTo" name="returnTo" value="${returnTo}">
				    <div class="input-group-btn dropdown" id="countryGroup" style="display:none;">
				      <div class="btn-group dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
				        <input type="hidden" id="countryCode" name="countryCode" value="+86">
				        <button type="button" class="btn text-nowrap" id="countryText" style="border-right:none;border-left:none;">+86</button>
					    <button type="button" class="btn" style="border-left:none;"><i class="iconfont2 ft12">&#xe600;</i></button>
					  	</div>
					  	<ul class="dropdown-menu" aria-labelledby="countryGroup" style="left:auto;right:0; width:300px; height:400px; overflow-x:auto; overflow-y:auto;">
						  	<c:forEach items="${country}" var="coun">
		                    	<li data-value="${coun.code}" <c:if test="${coun.code=='+86'}">class="active"</c:if>><a>${coun.code}[<span>${coun.des}</span>]</a></li>
		                    </c:forEach>
		                  </ul>
						</div>
					   </div>
			          </div>
			          <div class="sig_text">
									${L:l(lan,'登录-右侧内容-标签-2')}
									<a class="pull-right text-blue" href="${vip_domain }/ac/password_find" target="_self" title="${L:l(lan,'登录-右侧内容-超链接-1')}">${L:l(lan,'登录-右侧内容-超链接-1')}</a>
								</div>
			          <div class="form-group line-bot" id="passwordForm">
			            <label class="sr-only" for="password">${L:l(lan,'登录-右侧内容-标签-2')}</label>
									
			            <div class="input-group">
			              <div class="input-group-btn">
			                 <button type="button" class="btn"><i class="iconfont2">&#xe60a;</i></button>
			              </div>
			              <input type="password" class="form-control" id="password" name="password" tabindex="11">
			            </div>
			          </div>
			          <div class="form-group line-bot" id="imgCodeForm" <c:if test="${isSafe==1}">style="display:none"</c:if> >
									<div class="sig_text sigimg_text" >${L:l(lan,'图形验证码')}</div>
			            <label class="sr-only" for="imgCode">${L:l(lan,'图形验证码')}</label>
			            <div class="input-group">
			              <div class="input-group-btn">
			                 <button type="button" class="btn"><i class="iconfont">&#xe60b;</i></button>
			              </div>
			              <input type="text" name="imgCode" id="imgCode" class="form-control" tabindex="12">
			              <div class="input-group-btn">
			                <img src="/imagecode/get-28-100-50" alt="${L:l(lan,'点击刷新验证码')}" role="imgCode" />
			              </div>
			            </div>
			          </div>
			          
			          <!--<div class="form-group mt90">
				          <div class="checkbox text-left">
					          <!--<label title="${L:l(lan,'请勿在公共场所使用该功能')}"><input type="checkbox" name="rememberMe" id="rememberMe" style="top:5px;">${L:l(lan,'记住用户名')}</label>
					          <a class="pull-right text-blue" href="${vip_domain }/ac/password_find" target="_self" title="${L:l(lan,'忘记密码')}">${L:l(lan,'忘记密码')}</a>
				          </div>
				        </div>-->
			          
			          <div class="form-group btn_wrap">
			              <input type="hidden" id="isSafe" name="isSafe" value="1">
			              <button id="submitBtn" type="button" class="btn btn-primary btn-login" tabindex="13">${L:l(lan,'登录-右侧内容-按钮-1')}</button>
			          </div>
			          <!--<div class="form-group">
       					   <a id="clearCookie" class="text-blue pointer" title="${L:l(lan,'清除cookies数据')}">${L:l(lan,'清除记录')}</a>
			               <span class="pull-right">${L:l(lan,'login.text1')}&nbsp;<a href="/register" class="text-blue" >${L:l(lan,'立即注册')}</a></span>
			          </div>-->
			        </form>
			      </div>
		      </div>
		      </div>
            <section class="sig_leftbox">
                <div class="top_img"></div>
                <h3 class="sigleft_h3">${L:l(lan,'登录-左侧内容-标题-1')}</h3>
                <p class="sigleft_p">
                    ${L:l(lan,'登录-左侧内容-标签-1')}
                </p>
                <a class="sigleft_a" href="${vip_domain}/register">${L:l(lan,'注册-右侧内容-按钮-2')}</a>
            </section>
		    </div>
		  </div>
			
		</div>
		<jsp:include page="/common/foot.jsp" />
	  <script type="text/javascript">
			require(["module_market","module_asset","module_user","module_common"],function(market,asset,user){
				  market.init();
				  asset.init();
			  	user.logPageInit();
			});
			console.log($.cookie("prevhref"))
	  </script>
	</div>
</body>

</html>
