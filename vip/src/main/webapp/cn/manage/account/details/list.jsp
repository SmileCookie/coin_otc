<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 
<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>BTC ${L:l(lan,"收支明细")}-${WEB_NAME }-${WEB_TITLE }</title>
<jsp:include page="/common/head.jsp" />
<link rel=stylesheet type=text/css href="${static_domain }/statics/css/fast.css" />
<script type="text/javascript">
$(function() {
	vip.list.ui();
	vip.list.basePath = "/u/btc/details/";
});

function reload2() {
	Close();
	vip.list.reload();
}
</script>
</head>
<body>
<!-- 头部 -->
<jsp:include page="/common/top.jsp" />

	<div class="h_ucenter">
  		<div class="wrap clearfloat">
  			<div class="side_l ld">
  			<%-- 	<jsp:include page="/common/left.jsp"><jsp:param value="asset" name="type"/></jsp:include> --%>
			</div>
  
    		<div class="side_r ld">
            <jsp:include page="/common/tab.jsp" />
    		  <div class="main-bd">
              <div class="tab_head" id="userTab">BTC ${L:l(lan,"收支明细")}</div>
			<div class="col-main">
			<form autocomplete="off" name="searchForm" id="searchContaint">
			<div class="form-search" id="formSearchContainer">
		<div class="formline">
		<span class="formtit">${L:l(lan,"显示")} </span> 
			<select id="pagesize" name="pageSize" size="1" style="display: none;" selectid="select_37173240">
				<option value="10" selected="selected">10</option><option value="20">20</option><option value="30">30</option><option value="50">50</option>
			</select>
			<div class="SelectGray" id="select_37173240"><span><i style="width: 22px;">10</i></span></div>
			
		<span class="formtit"> ${L:l(lan,"条")}</span>
		
		</div>
			<div class="formline"><span class="formtit">${L:l(lan,"备注")}：</span><input class="input" type="text" name="title" id="title" position="s" mytitle='${L:l(lan,"请输入搜索关键字")}' style="width:200px;"/>
				<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">${L:l(lan,"搜索")}</a>
				<a class="search-submit" id="idReset" href="javascript:vip.list.resetForm();">${L:l(lan,"重置")}</a>
			</div>
		</div>
		</form>
		<div class="cztab_head" id="userTab">
							<a href="javascript:vip.list.search({tab:'all'})" id="all" class="current">${L:l(lan,"所有")}</a>
							<a href="javascript:vip.list.search({tab:'upload'})" id="upload">${L:l(lan,"充值")}</a>
							<a href="javascript:vip.list.search({tab:'download'})" id="download">${L:l(lan,"提现")}</a>
							<a href="javascript:vip.list.search({tab:'sell'})" id="sell">${L:l(lan,"卖出")}</a>
							<a href="javascript:vip.list.search({tab:'buy'})" id="buy">${L:l(lan,"买入")}</a>
							<a href="javascript:vip.list.search({tab:'other'})" id="other">${L:l(lan,"其它")}</a>
		</div>

		<div class="tab-body" id="shopslist">
			<input type="hidden" id="currentPage" value="${currentPage }"/>
			<input type="hidden" id="currentTab" value="${currentTab }"/>
			
			<jsp:include page="ajax.jsp" />
		</div>
	</div>
	          </div>
			</div>

		</div>
	</div>

<!-- 头部 -->
<jsp:include page="/common/foot.jsp" />
</body>
</html>

