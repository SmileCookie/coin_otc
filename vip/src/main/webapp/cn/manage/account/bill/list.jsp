<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>${L:l(lan,"资产明细")}</title>
<script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
	$(function() {
		//vip.list.ui();
		vip.list.basePath = "/manage/account/bill/";
	});
	
	function exportUser(){
		BwModal.alert("${L:l(lan, '确定要导出Excel吗？')}", {btnNum:2, fn:"exportE()"});
	}
	
	function exportE(){
		var actionUrl = "/manage/account/bill/exportUser";
		var datas = FormToStr("searchContaint");
		location.href = actionUrl+"?"+datas;
	}
</script>
</head>
<body>
<div class="user-content">
<div class="user-panel">
<div class="user-header"><h2>${L:l(lan,"资产明细")}</h2></div>
<div class="user-body">
	<form name="searchForm" id="searchContaint">
		<input type="hidden" name="${coint.coinParam}" value="${coint.propTag }" />
		<div class="formSearchContainer" id="formSearchContainer">
			<span>${L:l(lan,"时间")}：${L:l(lan,"从")}</span>
            <input class="form-control" type="text" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : '${lan}'})" name="startDate" id="startDate" />
            <span>${L:l(lan,"到")}</span>
            <input class="form-control" type="text" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : '${lan}'})" name="endDate" id="endDate" />
            <span>${L:l(lan,"备注") }：</span>
			<input class="form-control" type="text" id="memo" name="memo" pattern="limit(0,50)"/>
			<a class="btn" id="idSearch" href="javascript:vip.list.search();">${L:l(lan,"搜索")}</a>
			<a class="btn" id="idReset" href="javascript:vip.list.resetForm();">${L:l(lan,"重置")}</a>
			<a class="btn" id="idExport" href="javascript:exportUser();">${L:l(lan,"导出Excel")}</a>
		</div>
	</form>
							
	<div class="lk_tab">
		<ul>
			<li class="on"><a href="javascript:vip.list.search({tab:'all'})" id="all">${L:l(lan,"所有")}</a></li>
			<li><a href="javascript:vip.list.search({tab:'upload'})" id="upload">${L:l(lan,"充值")}</a></li>
			<li><a href="javascript:vip.list.search({tab:'download'})" id="download">${L:l(lan,"提币")}</a></li>
			<li><a href="javascript:vip.list.search({tab:'other'})" id="other">${L:l(lan,"其他")}</a></li>
		</ul>
	</div>

	<div class="lk_list" id="shopslist">
		<jsp:include page="ajax.jsp" />
	</div>
</div>
</div>
</div>
</body>
</html>
