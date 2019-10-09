<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${L:l(lan,'注册成功')}-${WEB_NAME }-${WEB_TITLE }</title>
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

<body>
<div class="bk-body">
  <jsp:include page="/common/top.jsp" />
       <div class="logon-box">
		  <div class="container">
		    <div class="bk-sign-success" id="logBox" >
		      <div class="bk-sign-tab">
		      	<svg class="icon" aria-hidden="true">
	                <use xlink:href="#icon-checked"></use>
	            </svg>
		        <h4 class="text-h4 text-center mb10">${L:l(lan,'恭喜您，注册成功!')}</h4>
		        <p class="mb40 text-center">${L:l(lan,'为了您的资金安全和正常操作，建议您立即设置资金安全密码。')}</p>
		        <p class="clearfix">
			        <a class="btn btn-lg btn-over mb10 mr15 ml15" href="${vip_domain }/manage/auth/pwd/safe" target="_self" style="min-width:200px">${L:l(lan,'立即设置')}</a>
			        <!--<a class="btn btn-lg btn-over text-dep  ml15 mb10 mr15" href="${vip_domain}/trade" target="_self" style="min-width:200px">${L:l(lan,'以后设置')}</a>-->
							 <a class="btn btn-lg btn-over text-dep  ml15 mb10 mr15" href="${vip_domain}/trade" target="_self" style="min-width:200px">${L:l(lan,'以后设置')}</a>
		        </p>
		      </div>
		    </div>
		  </div>
		</div>
		<jsp:include page="/common/foot.jsp" />
<script type="text/javascript">
	 	require(["module_market","module_asset","module_user","module_common"],function(market,asset,user){
		  market.init();
		  asset.init();
		  user.pageIndexInit();
	  });
</script>	
</div>
</body>
</html>

