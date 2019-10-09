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
	vip.list.funcName = "机器人管理";
	vip.list.basePath = "/admin/robot/";
});

function reload2(){
	Close();
	vip.list.reload();
}




function doStart(tid){
	vip.ajax({url : "/admin/robot/start?id="+tid, suc : function(xml){
    	Right($(xml).find("Des").text(), {callback:"reload2()"});
	}});
}

function doStop(tid){
	vip.ajax({url : "/admin/robot/stop?id="+tid, suc : function(xml){
    	Right($(xml).find("Des").text(), {callback:"reload2()"});
	}});
}
</script>	
</head>
<body >
<div class="mains">
<div class="col-main">
	<div class="form-search">
		<form autocomplete="off" name="searchForm" id="searchContaint">
			<div id="formSearchContainer">
				<p>
					<span>标题：</span>
					<input id="title" mytitle="标题要求填写一个长度小于50的字符串" name="title" style=" width:80px;" pattern="limit(0,50)" size="20" type="text"/>
				</p>
				<p>
					<span>账户ID：</span>
					<input id="account" mytitle="内容要求填写一个长度小于50的字符串" name="account" style=" width:80px;" pattern="limit(0,50)" size="20" type="text"/>
				</p>
				<p>
					<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
					<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
					<a class="search-submit" href="javascript:vip.list.aoru({id : 0 , width : 600 , height : 660});">添加</a>
				</p>
			</div>
	
		</form>
	</div>
	<div class="tab_head" id="userTab">
		<a href="javascript:vip.list.search({tab:'etc_btc'})" class="current" id="etc_btc"><span>ETC/BTC</span></a>
		<a href="javascript:vip.list.search({tab:'eth_btc'})" id="eth_btc"><span>ETH/BTC</span></a>
		
	</div>
	<div class="tab-body" id="shopslist">
		<jsp:include page="ajax.jsp" />
	</div>
</div>
</div>	
</body>
</html>
