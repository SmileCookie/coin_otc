<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ page import="com.world.web.action.Action"%>
<%@ page import="com.world.config.GlobalConfig"%>

<!doctype html>
<html>
<head>
<jsp:include page="/common/v2/headFile.jsp" />
<title><decorator:title/> </title>
<decorator:head />

<body>
<div class="bk-body">
	<!-- Common pageInti Begin -->
	<jsp:include page="/common/v2/pageInti.jsp" />
	<!-- Common pageInti End -->
	<%
		boolean frame = (Boolean)request.getAttribute("isFrame");
		if(!frame){
	%>
	<!-- Common TopMenu Begin -->
	<jsp:include page="/common/v2/topMain.jsp" />
	<!-- Common TopMenu End -->
	<%
		}
	%>
	<!-- Common UserMain Begin -->
	<div class="bk-main">
		<div class="container">
			<div class="row">
				<div class="col-xs-2">
					<div class="bk-left-main">
						<!-- userPage leftMenu Begin -->
						<jsp:include page="/common/v2/leftMenu.jsp" />
						<!-- userPage leftMenu End -->
					</div>
				</div>
				<div class="col-xs-10">
					<div class="bk-right-main">
						<!-- Body From userPage Begin -->
						<decorator:body />
						<!-- Body From userPage End -->
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- Common UserMain End -->
	<%
		if(!frame){
	%>
	<!-- Common FootMain Begin -->
	<jsp:include page="/common/v2/footMain.jsp" />
	<!-- Common FootMain End -->
	<%
		}
	%>
</div>
</body>
</html>