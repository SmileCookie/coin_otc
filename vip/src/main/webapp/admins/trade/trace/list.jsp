<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>
<jsp:include page="/admins/top.jsp" />
<script type="text/javascript">
$(function(){ 
 	vip.list.ui();
	vip.list.funcName = "委托追踪管理";
	vip.list.basePath = "/admin/trade/trace/";
});

function reload2(){
	Close();
	vip.list.reload();
}





</script>	
</head>
<body >
<div class="mains">
<div class="col-main">
	<div class="form-search">
		<form autocomplete="off" name="searchForm" id="searchContaint">
			<div id="formSearchContainer">
				<input type="hidden" name="tab" id="tab" value="${tab }" />
				<p>
					<span>委托单号：</span>
					<input id="entrustId"  name="entrustId" style=" width:80px;" pattern="limit(0,50)" size="20" type="text"/>
				</p>
				<p>
					<span>委托用户ID：</span>
					<input id="userId"  name="userId" style=" width:80px;" pattern="limit(0,50)" size="20" type="text"/>
				</p>
				
				<p>
					<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
					<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
					
				</p>
			</div>
	
		</form>
	</div>
		<div class="tab_head">
				<c:forEach items="${markets}" var="market">
					<a href="/admin/trade/trace?tab=${market.key}" class="${market.key == tab ? 'current' : ''}">${market.key.toUpperCase()}</a>
				</c:forEach>
			</div>
			
	
	<div class="tab-body" id="shopslist">
		<jsp:include page="ajax.jsp" />
	</div>
</div>
</div>	
</body>
</html>
