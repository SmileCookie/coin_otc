<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${L:l(lan,'温馨提示')}-${WEB_NAME }-${WEB_TITLE }</title>
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
	  
	  var topMargin = $(window).height() > 670 ? $(window).height()/2 - 280 : 60 ;
		$('#logBox').css({marginTop:topMargin}).fadeIn(200);
	  
		$(window).resize(function(){
			var topMargin = $(window).height() > 670 ? $(window).height()/2 - 280 : 60 ;
			$('#logBox').css({marginTop:topMargin});
		});
		 
	});
</script>
</head>

<body class="body-bg">
	<div class="bk-body">
		<jsp:include page="/common/top.jsp" />
		 <div class="logon-box blank">
		  <div class="container">
		    <div class="bk-sign-main" id="logBox" style="display:none">
		      <div class="logoutbox blank_logoutbox">
		      <div class="bk-sign-tab">
		        <h4 class="text-h4">${L:l(lan,'温馨提示')}</h4>
		      </div>
		      <div class="bk-sign-form">
		        <form role="form" id="logform" class="form-horizontal" method="post" action="" autocomplete="off">
		          <div class="form-group">
			          <div>
          				<c:choose>
          					<c:when test="${lan eq 'cn' }">
								<p>${tips}</p>
          					</c:when>
          					<c:otherwise>
								<p>${tips}</p>
          					</c:otherwise>
          				</c:choose>
			          </div>
			        </div>
		          
		        </form>
		      </div>
		    </div>
		  </div>
		 </div>
		</div>
	</div>
	<jsp:include page="/common/foot.jsp" />
	<script type="text/javascript">
			require(["module_user","module_common"],function(user){
				  user.init();
		  });
	  </script>
</body>
</html>