<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>用户管理</title>
<jsp:include page="/admins/top.jsp" />

<style type="text/css">
label.checkbox{  margin: 3px 6px 0 7px;}
label.checkbox em{ padding-left:18px; line-height:15px; float:left; font-style:normal;}
.page_nav{ margin-top:10px;}

.form-search .formline{float:left;}
.form-search p{float:none;}

.operation { height: 20px; line-height: 20px; text-align: left;margin-top: 10px;padding-left: 10px;}
tbody.operations  td{ padding:0; border:0 none;}
tbody.operations  td label.checkbox{ margin-top:10px; width:55px;}


.tb-list2 td em {
    color: #0072a3;
}
i, em {
    font-style: normal;
}
</style>
</head>
<body >
<div class="mains">
	<div class="col-main">
		<div class="form-search">
			<form autocomplete="off" name="searchForm" id="searchContaint">
				<div class="form-search" id="searchContainer">
					<input type="hidden" id="tab" name="tab" value="${tab }" />
					
					<div class="formline">
						<span class="formtit">用户ID：</span>
						<span class="formcon">
							<input id="userId" mytitle="用户ID要求填写一个长度小于50的字符串" name="userId" pattern="limit(0,50)" size="20" type="text"/>
						</span>
					</div>
						<p>
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
							<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
						</p>
				</div>
		
			</form>
		</div>
		<div class="tab_head" id="userTab">
			<a href="javascript:vip.list.search({tab:'0'})" class="current" id="0"><span>待审核</span></a>
			<a href="javascript:vip.list.search({tab:'2'})" id="2"><span>已通过</span></a>
			<a href="javascript:vip.list.search({tab:'1'})" id="1"><span>已拒绝</span></a>
			<a href="javascript:vip.list.search({tab:'3'})" id="3"><span>已撤消</span></a>
			<a href="javascript:vip.list.search({tab:'4'})" id="4" style="display: none;"><span>手机挂失</span></a>
		</div>
		<div class="tab-body" id="shopslist">
			<jsp:include page="ajax.jsp" />
		</div>
	</div>
</div>	

<script type="text/javascript">
$(function(){ 
 	vip.list.ui();
	vip.list.funcName = "用户";
	vip.list.basePath = "/admin/user/verify/";
});

function verify(id){
	Iframe({
		Url : "/admin/user/verify/aoru?id="+id,
		zoomSpeedIn : 200,
		zoomSpeedOut : 200,
		Width : 500,
		Height : 580,
		scrolling : 'no',
		isIframeAutoHeight : false,
		isShowIframeTitle:true,
		Title : "我要审核"
	});
}

function compare(id){
	Iframe({
		Url : "/admin/user/verify/compareWithSystemInfo?id="+id,
		zoomSpeedIn : 200,
		zoomSpeedOut : 200,
		Width : 500,
		Height : 580,
		scrolling : 'no',
		isIframeAutoHeight : false,
		isShowIframeTitle:true,
		Title : "对比一下"
	});
}

function show(id){
	Iframe({
		Url : "/admin/user/verify/show?id="+id,
		zoomSpeedIn : 200,
		zoomSpeedOut : 200,
		Width : 500,
		Height : 500,
		scrolling : 'no',
		isIframeAutoHeight : false,
		isShowIframeTitle:true,
		Title : "查看详情"
	});
}
</script>

</body>
</html>
