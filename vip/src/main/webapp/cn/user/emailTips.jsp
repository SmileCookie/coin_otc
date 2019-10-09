<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${L:l(lan,'帐号激活')}-${WEB_NAME}-${WEB_TITLE }</title>
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

<body class="">
	<div class="bk-body">
		<jsp:include page="/common/top.jsp" />
      <div class="logon-box clearfix">
		  <div class="container">
		    <div class="bk-sign-success email_tips" id="logBox">
		      <div class="bk-sign-tab">
		        <h4 class="text-h4">${L:l(lan,'帐号激活')}</h4>
		      </div>
		      <div class="bk-sign-form">
		        <form role="form" id="logform" class="form-horizontal" method="post" action="" autocomplete="off">
		          <div class="form-group">
			          <div class="text-left">
				          <c:if test="${type==1}">
		          				<c:choose>
		          					<c:when test="${lan eq 'cn' }">
													<p>验证邮件已发送至<span class="text-primary">${email}</span>，请登录邮箱点击激活链接，完成帐号激活。</p>
		          					</c:when>
												<c:when test="${lan eq 'hk' }">
													<p>驗證郵件已發送至<span class="text-primary">${email}</span>，請登錄郵箱點擊激活鏈接，完成帳號激活。</p>
		          					</c:when>
		          					<c:otherwise>
													<p>We've sent an email to <span class="text-primary">${email}</span>, please login your email account and click the link to active your renew account.</p>
		          					</c:otherwise>
		          				</c:choose>
									</c:if>
									<c:if test="${type==2}">
										<c:choose>
														<c:when test="${lan eq 'cn' }">
															<p>系统检测到您的邮箱账号未激活，请登录您的注册邮箱<span class="text-primary">${email}</span>，点击收件箱中的激活链接完成帐号激活。</p>
														</c:when>
														<c:when test="${lan eq 'hk' }">
															<p>系統檢測到您的郵箱賬號未激活，請登錄您的註冊郵箱<span class="text-primary">${email}</span>，點擊收件箱中的激活鏈接完成帳號激活。</p>
														</c:when>
														<c:otherwise>
															<p>We found that you have not active your account, please login <span class="text-primary">${email}</span>, please login your email account and click the link to active your renew account.</p>
														</c:otherwise>
													</c:choose>
									</c:if>
			          </div>
			        </div>
		          
		          <div class="form-group logBtn_form_group">
		              <button id="logBtn" type="button" onclick="reSendEmail()" class="btn btn-login ft16">${L:l(lan,'重新发送激活邮件')}</button>
		          </div>
		          <div class="form-group text-center">
		           	 <a class="nav-left text-depblue" href="${vip_domain }/register" target="_self" title="${L:l(lan,'注册帐号')}">${L:l(lan,'注册帐号')}</a> 
					 <a class="nav-right text-depblue" href="${vip_domain }/login" target="_self" title="${L:l(lan,'帐号登录')}">${L:l(lan,'帐号登录')}</a>
		          </div>
		        </form>
		      </div>
		    </div>
		  </div>
		 </div>
		 <jsp:include page="/common/foot.jsp" />
	  <script type="text/javascript">
		function reSendEmail() {
			$.ajax({
				type : "POST",
				url : "/register/reSendEmail",
				data : {
					nid : ${nid}
				},
				dataType : "json",
				error : function() {
					JuaBox.info(jsLan[1]);
					inAjaxing = false;
				},
				success : function(json) {
					inAjaxing = false;
					if (json.isSuc) {
						settime($('#logBtn'));
					} else {
						JuaBox.info(json.des);
					}
				}
			});
		}
		var countdown = 60;
		function settime(obj) {
			if (countdown == 0) {
				$(obj).removeAttr("disabled");
				$(obj).html("${L:l(lan,'重新发送激活邮件')}");
				countdown = 60;
			} else {
				$(obj).attr("disabled", true);
				$(obj).html('${L:l(lan,"已发送")}(' + countdown + ')');
				countdown--;
				setTimeout(function() {
					settime($(obj))
				}, 1000)
			}
		}
		require(["module_market","module_asset","module_common"],function(market,asset){
			 market.init();
			 asset.init();
		});
	  </script>
	</div>
</body>
</html>