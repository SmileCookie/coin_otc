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
	vip.list.funcName = "新闻咨询";
	vip.list.basePath = "/admin/msg/";
});

function reload2(){
	Close();
	vip.list.reload();
}

function toTop(tid){
	Ask({Msg:"确定要置顶该项吗？", callback:"doTop("+tid+")"});
}

function doTop(tid){
	vip.ajax({url : "/admin/msg/doTop?id="+tid, suc : function(xml){
    	Right($(xml).find("Des").text(), {callback:"reload2()"});
	}});
}

function toCancel(tid){
	Ask({Msg:"确定要取消置顶操作吗？", callback:"doCancelTop("+tid+")"});
}

function doCancelTop(tid){
	vip.ajax({url : "/admin/msg/cancelTop?id="+tid, suc : function(xml){
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
					<span>内容：</span>
					<input id="content" mytitle="内容要求填写一个长度小于50的字符串" name="content" style=" width:80px;" pattern="limit(0,50)" size="20" type="text"/>
				</p>
				<p class="formCloumn">
					<span>公告类型：</span>
					<select name="noticeType" id="noticeType">
						<option value="">--请选择--</option>
						<c:forEach var="ft" items="${noticeType }">
							<option value="${ft.key}">${ft.value}</option>
						</c:forEach>
					</select>
				</p>



				<p>
					<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
					<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
					<a id="idReset1" class="search-submit" target="_blank"  href="/admin/msg/aoru">发布</a>
				</p>


			</div>
	
		</form>
	</div>
	<div class="tab_head" id="userTab">
		<a href="javascript:vip.list.search({tab:'notice'})" class="current" id="notice"><span>公告</span></a>
		<a href="javascript:vip.list.search({tab:'news'})" id="news"><span>新闻</span></a>
		<a href="javascript:vip.list.search({tab:'all'})" id="all"><span>所有公告</span></a>
	</div>
	<div class="tab-body" id="shopslist">
		<jsp:include page="ajax.jsp" />
	</div>
</div>
</div>	
</body>
</html>
