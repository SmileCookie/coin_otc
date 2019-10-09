<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page isELIgnored="false"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>钱包管理</title>
<jsp:include page="/admins/top.jsp" /></script>

<script type="text/javascript">
$(function(){
	vip.list.ui();
	vip.list.funcName = "钱包管理";
	vip.list.basePath = "/admin/btc/wallet/"; 
});
function reload2(){
	Close();
	vip.list.reload();
}

function add(){
	vip.list.reloadAsk({url : ""})
}

function aoru(){
	Iframe({
		    Url:"/admin/btc/wallet/aoru?coint=${coint.propTag }",//下载
	        zoomSpeedIn		: 200,
	        zoomSpeedOut	: 200,
	        Width:600,
	        Height:700,
	        scrolling:"no",
	        isIframeAutoHeight:false,
        	isShowIframeTitle: true,
	        Title:"添加/编辑"
	});
}
</script>

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
		<jsp:include page="/admins/topTab.jsp" />
		<input type="hidden" value="${currentTab }" id="currentTab"/>
		
			<div class="col-main">
				<div class="form-search" id="searchContaint">
						<p class="formCloumn">
							说明：
							<input type="text" name="userName" mytitle="请输入用户名"  id="userName" size="16" />
						</p>
						
						<p>
							<input type="hidden" name="${coint.coinParam }" value="${coint.propTag }"  />
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
							<a href="javascript:vip.list.resetForm();" id="idReset" class="search-submit">重置</a>
							<a class="search-submit" href="javascript:aoru();">添加</a>
						</p>
				</div>						
						
				<div class="tab_head" id="userTab">			
						<a href="javascript:vip.list.search({tab : 'all'});" id="all" class="current"><span>所有</span></a>
				</div>
				<div class="tab-body"  id="shopslist">
					<jsp:include page="ajax.jsp" />
				</div>
			</div>
			
		</div>
	</body>
</html>
