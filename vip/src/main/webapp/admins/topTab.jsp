<%@page import="com.world.web.action.Action"%>
<%@page import="com.world.config.GlobalConfig"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<div class="tab_head" id="topTab" style="margin-top: 10px;">
	<c:forEach items="${coinMap}" var="coin">
		<a href="?coint=${coin.key}" id="${coin.value.propTag}" class="${coint.stag==coin.key?'current':'' }"><span>${coin.value.propTag}</span></a>
	</c:forEach>
</div>
