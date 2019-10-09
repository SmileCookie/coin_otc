<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>

<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${WEB_NAME }-${WEB_TITLE }</title>
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
</head>
<body>
<jsp:include page="/common/top.jsp" />

<!--页面中部内容开始-->
<div class="container">
	<div style="height:500px; text-align:center; line-height:500px; margin:15px auto; font-size:24px; border:1px solid #eee; background-color:#f9f9f9;">
     <p>页面建设中</p>
     </div>
</div>
<!--页面中部内容结束-->
<jsp:include page="/common/foot.jsp" />
</body>
</html>
