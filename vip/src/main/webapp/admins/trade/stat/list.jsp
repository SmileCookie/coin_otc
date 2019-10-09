<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>盘口交易统计</title>
<jsp:include page="/admins/top.jsp" />
		<style type="text/css">
			.translist { width:100%; overflow:hidden; }
			.translist .tab_head { margin-top:10px;  }
			.bdlist .tablelist { width:32%; display:inline-block; vertical-align: top; }
			.bdlist .tablelist:nth-child(2) { margin:0px 1.5%; }
			.bdlist td.depth { width:40%; text-align:left; padding-left:10px; vertical-align: middle; }
			.bdlist .depth  span{ display:inline-block; height:16px; margin-top:2px;   }
			.bdlist  span.buyspan { background:#3dc18e; }
			.bdlist  span.sellspan { background:#de211d; }
			.bdlist .tb-list2 th, .tb-list2 td { padding:3px; }
			.bdlist .tablelist h3 { display:block; height:30px; }
			.bdlist .tb-list2 td  { border-left:none; border-right:none; height:25px; border-top:none; }
			.bdlist .tb-list2 tr:nth-child(2n) { background:#F3F3F3;  }
			.bdlist thead tr { background:#E1E1E1; height:32px; border-bottom:1px solid #ddd; }
			.buytr th { color:#D00; }
			.selltr th { color:green; }
		</style>
	</head>
	<body> 
	
	<div class="mains ">
		<div class="translist col-main">
			<div class="tab_head">
				<c:forEach items="${markets}" var="market">
					<a href="/admin/trade/stat${merge}?tab=${market.key}" class="${market.key == tab ? 'current' : ''}">${market.key.toUpperCase() }</a>
				</c:forEach>
			</div>
			
			<div class="tab-body">
				<div class="bdlist btc" id="shopslist">
					<jsp:include page="ajax.jsp" />
				</div>
			</div>
		</div>
	</div>
	
	
<script type="text/javascript">
$(function(){ 
 	vip.list.ui();
	vip.list.funcName = "盘口交易统计";
	vip.list.basePath = "/admin/trade/stat/";
});
</script>
</body>
</html>
