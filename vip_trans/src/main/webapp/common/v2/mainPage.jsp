<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ page import="com.world.web.action.Action"%>
<%@ page import="com.world.config.GlobalConfig"%>
<%
boolean frame = (Boolean)request.getAttribute("isFrame");
%>
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
		if(!frame){
	%>
	<!-- Common TopMenu Begin -->
	<jsp:include page="/common/v2/topMain.jsp" />
	<!-- Common TopMenu End -->
	<%
		}
	%>
	<!-- Body From mainPage Begin -->
	<decorator:body />
	<!-- Body From mainPage End -->
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