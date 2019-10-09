<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${L:l(lan,'注册新用户')}-${WEB_NAME }-${WEB_TITLE }</title>
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
      <div class="logon-box clearfix">
		  <div class="container open_up">
		    <div class="bk-sign-main sig_box clearfix" id="logBox">
		    	<div class="logoutbox fixed-left">
		      <div class="bk-sign-tab">
					<%--${L:l(lan,'注册新用户')}--%>
		        <h4 class="text-h4">${L:l(lan,'注册-右侧内容-标题-1')}</h4>
		      </div>
		      <div class="bk-sign-form">
		        <form role="form" id="regForm" class="form-horizontal" method="post" action="" autocomplete="off">
							<div class="sig_text">${L:l(lan,'注册-右侧内容-标签-1')}</div>
		          <div class="form-group line-bot" id="nikeForm">
		            <label class="sr-only" for="nike">${L:l(lan,'注册-右侧内容-标签-1')}</label>
		          	<div class="input-group">
							    <div class="input-group-btn">
							       <button type="button" class="btn"><i class="iconfont2">&#xe609;</i></button>
							    </div>
							    <input type="text" name="nike" id="nike" class="form-control" tabindex="10">
							    <div class="input-group-btn dropdown" id="countryGroup" style="display:none">
							      <div class="btn-group dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
							        <input type="hidden" id="countryCode" name="countryCode" value="+86">
							        <button type="button" class="btn text-nowrap" id="countryText" style="border-right:none;border-left:none;">+86</button>
								      <button type="button" class="btn" style="border-left:none;"><i class="iconfont2 ft12">&#xe600;</i></button>
								  	</div>
								  	<ul class="dropdown-menu" aria-labelledby="countryGroup" style="left:auto;right:0; width:350px; height:400px; overflow-x:hidden; overflow-y:auto;">
									  	<c:forEach items="${country}" var="coun">
	                    	<li data-value="${coun.code}" <c:if test="${coun.code=='+86'}">class="active"</c:if>><a>${coun.code} [<span>${coun.des}</span>]</a></li>
	                    </c:forEach>
	                  </ul>
					    		</div>
							  </div>
		          </div>
		          <div class="sig_text">${L:l(lan,'注册-右侧内容-标签-2')}</div>
		          <div class="form-group" id="passwordForm">
		            <label class="sr-only" for="password">${L:l(lan,'注册-右侧内容-标签-2')}</label>
		            <div class="input-group line-bot">
		              <div class="input-group-btn">
		                 <button type="button" class="btn text-nowrap"><i class="iconfont2">&#xe60a;</i></button>
		              </div>
		              <input type="password" class="form-control" id="password" name="password" tabindex="11">
		            </div>
		            <div class="bk-pwdcheck" id="pwdStrength" style="display:none">
		            	<input type="hidden" id="pwdLevel" name="pwdLevel" value="0" />
		            	<p class="text-left ft12" style="display:none"><span class="jingbao_icon"></span>&nbsp;${L:l(lan,'注册-右侧内容-标签-3')}</p>
			            <ul class="bk-table">
			              <li class="bk-cell strength"></li>
			              <li class="bk-cell strength"></li>
										<li class="bk-cell strength"></li>
			              <li class="bk-cell strength" id="pwdStr">${L:l(lan,'注册-右侧内容-标签-4')}</li>										
			            </ul>
		            </div>
		          </div>
		          <div class="sig_text">${L:l(lan,'注册-右侧内容-标签-7')}</div>
		          <div class="form-group line-bot" id="confirmPwdForm">
		            <label class="sr-only" for="confirmPwd">${L:l(lan,'注册-右侧内容-标签-7')}</label>
		            <div class="input-group">
		              <div class="input-group-btn">
		                 <button type="button" class="btn text-nowrap"><i class="iconfont">&#xe60a;</i></button>
		              </div>
		              <input type="password" class="form-control" id="confirmPwd" name="confirmPwd"  tabindex="12">
		            </div>
		          </div>
		          <div class="sig_text">${L:l(lan,'注册-右侧内容-标签-8')}</div>
		          <div class="form-group line-bot" id="imgCodeForm">
		            <label class="sr-only" for="imgCode">${L:l(lan,'注册-右侧内容-标签-8')}</label>
		            <div class="input-group">
		              <div class="input-group-btn">
		                 <button type="button" class="btn"><i class="iconfont">&#xe60b;</i></button>
		              </div>
		              <input type="text" name="imgCode" id="imgCode" class="form-control"  tabindex="13">
		              <div class="input-group-btn">
		                <img src="/imagecode/get-28-100-50" alt="${L:l(lan,'点击刷新验证码')}" role="imgCode" />
		              </div>
		            </div>
		          </div>
		           <div class="sig_text msgCodeForm_text"style="display:none;">${L:l(lan,'注册-右侧内容-标签-9')}</div>
		          <div class="form-group line-bot" id="msgCodeForm" style="display:none;">
		            <label class="sr-only" for="msgCode">${L:l(lan,'注册-右侧内容-标签-9')}</label>
		          	<div class="input-group">
					    <div class="input-group-btn">
					       <button type="button" class="btn text-nowrap"><i class="iconfont">&#xe623;</i></button>
					    </div>
					    <input type="text" name="msgCode" id="msgCode" class="form-control" tabindex="14">
					    <div class="input-group-btn">
					    <div class="btn-group">
					      <button type="button" role="msgCode" id="sendMsgCode" class="btn text-nowrap line-left">${L:l(lan,'注册-右侧内容-按钮-1')}</button>
					  	</div>
				    		</div>
					</div>
		          </div>
		          <div class="agreement">
			          <div class="checkbox text-left">
											<input type="checkbox" checked="checked" name="agreement" id="agreement" >
				            	<label class="check_label" for="agreement">${L:l(lan,'我已阅读并同意')}</label><span class="text-blue">&nbsp;<a href="${vip_domain}/terms/service" target="_blank">${L:l(lan,'《服务条款》')}</a></span>
			          </div>
			        </div>
		          
		          <div class="form-group btn_wrap">
		          	<!-- <input type="hidden" id="pwdLevel" name="pwdLevel" value="40" /> -->
		            <input type="hidden" id="recommendId" name="recommendId" value="${recommId}" />
		            <button id="submitBtn" type="button" class="btn btn-primary btn-login" tabindex="15">${L:l(lan,'注册-右侧内容-按钮-2')}</button>
		          </div>
		         
		        </form>
		      </div>
		    </div>
				<section class="sig_leftbox">
						<div class="open_top_img"></div>
						<h3 class="sigleft_h3 open_h3">${L:l(lan,'注册-左侧内容-标题-1')}</h3>
						<p class="sigleft_p open_p">
								${L:l(lan,'注册-左侧内容-标签-1')}
						</p>
						<div class="open_text clearfix"><span class="open_okicon"></span><p class="text_p">${L:l(lan,'注册-左侧内容-标签-2')}</p></div>
						<div class="open_text clearfix"><span class="open_okicon"></span><p class="text_p">${L:l(lan,'注册-左侧内容-标签-4')}</p></div>
						<div class="open_text clearfix"><span class="open_okicon"></span><p class="text_p">${L:l(lan,'注册-左侧内容-标签-3')}</p></div>
				</section>
		    </div>
		  </div>
			
        </div>
				<jsp:include page="/common/foot.jsp" />
<script type="text/javascript">
	  require(["module_market","module_asset","module_user","module_common"],function(market,asset,user){
		  market.init();
		  asset.init();
		  user.regPageInit();
	  });
</script>
</div>
</body>
</html>

