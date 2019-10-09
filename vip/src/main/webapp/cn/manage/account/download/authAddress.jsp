<!doctype html>
<%@ page session="false" language="java" import="java.util.*"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>认证地址-${WEB_NAME }-${WEB_TITLE }</title>
<jsp:include page="/common/head.jsp" />

<script type="text/javascript" charset="utf-8">
	try {
		var oldDomain = document.domain;
		var ind = oldDomain.indexOf('vip');
		document.domain = oldDomain.substring(ind, oldDomain.length)
	} catch (msg) {
		document.domain = 'vip.com';
	}
</script>
</head>

<body>
<div class="ctips" style="width:500px;margin:30px auto;">
	<p style="text-shadow:none;"><span>${L:l(lan,'重要提示')}：</span><br>
		<c:if test="${isSuc}">
			<em>您的接收地址已认证成功，请返回<a href="${main_domain}">${title }</a>查看。</em><br>
		</c:if>
		<c:if test="${!isSuc}">
			<em>链接已失效。</em><br>
		</c:if>
	</p>
</div>
</body>
</html>
