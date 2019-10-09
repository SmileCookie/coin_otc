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
<title>${L:l(lan,func_des)}-${WEB_NAME }-${WEB_TITLE }</title>
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<link rel="stylesheet" href="${static_domain }/statics/css/web.user.css?V${CH_VERSON }">
<script type="text/javascript" src="${static_domain }/statics/js/common/jsencrypt.js?V${CH_VERSON }"></script>
<script type="text/javascript" src="${static_domain }/statics/js/bootstrap-datepicker.js?V${CH_VERSON }"></script>

</head>
<body class="">
<div class="bk-body">
	<!-- Common TopMenu Begin -->
	<jsp:include page="/common/top.jsp" />
	<!-- Common TopMenu End -->
	<!-- Body From mainPage Begin -->
	<div class="mainer2">
		<div class="container">
			<jsp:include page="/common/trend.jsp" />
			<div class="zh-trade">
				<div class="cont-row">
					<div class="user-panel">
						<jsp:include page="/cn/manage/auth/menu.jsp"/>
						<c:if test="${func_des=='基本信息' }">
							<jsp:include page="base/base.jsp"/>
						</c:if>
						<c:if test="${func_des=='登录密码' }">
							<jsp:include page="/cn/manage/base/password.jsp"/>
						</c:if>
						<c:if test="${func_des=='资金密码' }">
							<jsp:include page="/cn/manage/base/safeword.jsp"/>
						</c:if>
						<c:if test="${func_des=='手机认证' }">
							<jsp:include page="/cn/manage/base/mobileAuth.jsp"/>
						</c:if>
						<c:if test="${func_des=='邮箱认证' }">
							<jsp:include page="/cn/manage/base/emailAuth.jsp"/>
						</c:if>
						<c:if test="${func_des=='Google认证' }">
							<jsp:include page="/cn/manage/base/googleAuth.jsp"/>
						</c:if>
						<c:if test="${func_des=='关闭Google认证' }">
							<jsp:include page="/cn/manage/base/googleAuthClose.jsp"/>
						</c:if>
						<c:if test="${func_des=='修改手机' }">
							<jsp:include page="/cn/manage/base/mobileModify.jsp"/>
						</c:if>
						<c:if test="${func_des=='高级实名认证' }">
							<jsp:include page="/cn/manage/auth/depthAuth.jsp"/>
						</c:if>			
						<c:if test="${func_des=='实名认证' }">
							<c:choose>
								<c:when test="${pagefward=='switchType'}">
									<jsp:include page="/cn/manage/auth/switchType.jsp"/>
								</c:when>
								<c:when test="${pagefward=='enterpriseAuth'}">
									<jsp:include page="/cn/manage/auth/enterpriseAuth.jsp"/>
								</c:when>
								<c:otherwise >
									<jsp:include page="/cn/manage/auth/simpleAuth.jsp"/>
								</c:otherwise>
							</c:choose>
						</c:if>
						<c:if test="${func_des=='身份认证' }">	<!-- 身份认证状态（ 未认证/审核中/认证失败/认证成功 ） -->
							<jsp:include page="/cn/manage/userAuth/index.jsp"/>
						</c:if>
						<c:if test="${func_des=='认证类型'}"> <!-- 认证国籍和类型（ 身份证/护照 ） -->
							<jsp:include page="/cn/manage/userAuth/authType.jsp"/>
						</c:if>
						<c:if test="${func_des=='身份证认证' }"> <!-- 身份证认证 -->
							<jsp:include page="/cn/manage/userAuth/IdCard.jsp"/>
						</c:if>
						<c:if test="${func_des=='护照认证' }"> <!-- 护照认证 -->
							<jsp:include page="/cn/manage/userAuth/passport.jsp"/>
						</c:if>
					</div>
				
				</div>
			</div>
		</div>
	</div>
	
  
<script type="text/javascript">
 $(document).ready(function() {
	require(['module_user'],function(user){
		user.pageIndexInit();
	});
 });
 
</script>
      
	<!-- Body From mainPage End -->
	<!-- Common FootMain Begin -->
	<jsp:include page="/common/foot.jsp" />
	<!-- Common FootMain End -->
</div>

</body>
</html>
