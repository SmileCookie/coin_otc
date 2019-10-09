<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page isELIgnored="false"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>来源统计</title>
<jsp:include page="/admins/top.jsp" />

<script type="text/javascript">
$(function(){
	vip.list.ui();
	vip.list.basePath = "/admin/user/utm/";
});
function reload2(){
	Close();
	vip.list.reload();
}

</script>

<!-- 		<link href="/en/css/uadmin.css" rel="stylesheet" type="text/css" /> -->
   <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>
		<style type="text/css">
		.col-main{float:left;width:100%;}
		.tb-list2 th{padding:8px 10px;}
		.tb-list2 .hd td span b{font-weight:normal;color:#C92707;}
		.tb-list2 {width: 100%;}
		.tb-list2 a span{color:#C92707;display: inline-block;width: 110px;}
		</style>
	</head>
	<body>
	
		<div class="mains">
		
		<input type="hidden" value="${tab }" id="tab"/>
		
			<div class="col-main">
				<div class="form-search">
				<form autocomplete="off" name="searchForm" id="searchContaint">
					<div id="formSearchContainer">
						<p>
							<span>活动来源：</span>
							<select name="utm_medium" id="utm_medium">
								<option value="">请选择</option>
								<c:forEach var="medium" items="${mediumList }">
									<option value="${medium }">${medium }</option>
								</c:forEach>
							</select>

						</p>
						<p>
							<span>网站来源：</span>
							<select name="utm_source" id="utm_source">
								<option value="">请选择</option>
								<c:forEach var="source" items="${sourceList }">
									<option value="${source }">${source }</option>
								</c:forEach>

							</select>
						</p>
						<p>
							<span>时间范围：</span>
							<input type="text" id="startDate" name="startDate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',lang : 'cn'})" style="width:120px;" class="">
							至
							<input type="text" id="endDate" name="endDate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',lang : 'cn'})" style="width:120px;" class="">
						</p>
						
						<p>
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a>
							<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
						</p>
					</div>
					<div style="clear: both;"></div>
				</form>
			</div>						
				<div class="tab-body"  id="shopslist">
					<jsp:include page="ajax.jsp" />
				</div>
			</div>
			
		</div>
	</body>
</html>
