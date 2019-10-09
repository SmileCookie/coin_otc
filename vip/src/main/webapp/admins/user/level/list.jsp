<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page isELIgnored="false"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>积分管理</title>
<jsp:include page="/admins/top.jsp" />

<script type="text/javascript">
$(function(){
	vip.list.ui();
	vip.list.basePath = "/admin/user/level/";
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
							<span>用户ID：</span>
							<input id="userId" name="userId" type="text" style="width: 80px;">
						</p>
						<p>
							<span>积分类型：</span>
							<select name="type" id="type">
                              <option value="">全部</option>
                              <c:forEach items="${jifentypes }" var="type">
                              	<option value="${type.key }">${type.value }</option>
                              </c:forEach>
                            </select>
						</p>
						<p>
							<span>时间范围：</span>
							<input type="text" id="timeS" name="timeS" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',lang : 'cn'})" style="width:120px;" class="">
							至
							<input type="text" id="timeE" name="timeE" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',lang : 'cn'})" style="width:120px;" class="">
						</p>
						
						<p>
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
							<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
							<a class="search-submit" href="javascript:vip.list.aoru({id : 0 , width : 600 , height : 400, title:'添加积分', otherParam:'&addOrDel=0'});">添加</a>
							<a class="search-submit" href="javascript:vip.list.aoru({id : 0 , width : 600 , height : 400, title:'扣除积分', otherParam:'&addOrDel=1'});">扣除</a>
						</p>
					</div>
					<div style="clear: both;"></div>
				</form>
			</div>						
						
				<div class="tab_head" id="userTab">			
					<a href="javascript:vip.list.search({tab : 'all'});" id="all" class="current"><span>全部</span></a>
					<a href="javascript:vip.list.search({tab : 'in'});" id="in" ><span>积分获得</span></a>
					<a href="javascript:vip.list.search({tab : 'out'});" id="out"><span>积分消费</span></a>
					<!-- <a href="javascript:vip.list.search({tab : 'vip'});" id="vip"><span>VIP6用户列表</span></a> -->
				</div>
				<div class="tab-body"  id="shopslist">
					<jsp:include page="ajax.jsp" />
				</div>
			</div>
			
		</div>
	</body>
</html>
