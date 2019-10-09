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
				<jsp:include page="base.jsp"/>
			</div>
		
		</div>
	</div>
  


      
	<!-- Body From mainPage End -->
	<!-- Common FootMain Begin -->
	<jsp:include page="/common/foot.jsp" />
	<!-- Common FootMain End -->
</div>

<script type="text/javascript">
// 		seajs.use('module_user',function(user){
// 			user.pageIndexInit();
// 		});	
</script>

</body>
</html>
