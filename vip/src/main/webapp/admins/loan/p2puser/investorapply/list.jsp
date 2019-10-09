<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>借贷人申请</title>
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
.infunds{color: #0088CC;font-size: 14px;font-weight: bold;}
.outfunds{color: #B94A48;font-size: 14px;font-weight: bold;}
</style>
</head>
<body >
<div class="mains">
	<div class="col-main">
		<div class="form-search">
			<form autocomplete="off" name="searchForm" id="searchContaint">
				<div class="form-search" id="searchContainer">
					
					<div class="formline">
						<span class="formtit">用户Id：</span> 
						<span class="formcon">
							<input id="userId" mytitle="字段要求填写一个长度小于50的字符串" name="userId" pattern="limit(0,50)" size="20" type="text"/>
						</span>

						<span class="formtit">用户名：</span> 
						<span class="formcon">
							<input id="userName" mytitle="用户名要求填写一个长度小于50的字符串" name="userName" pattern="limit(0,50)" size="20" type="text"/>
						</span>
						
						<p>
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
							<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
						</p>
					</div>
	
				</div>
		
			</form>
		</div>
		<div class="tab_head" id="userTab">
			<a href="javascript:vip.list.search({tab:'untreated'})" class="current" id="untreated"><span>未处理</span></a>
			<a href="javascript:vip.list.search({tab:'open'})" id="open"><span>已开启</span></a>
			<a href="javascript:vip.list.search({tab:'reject'})" id="reject"><span>已拒绝</span></a>
			<a href="javascript:vip.list.search({tab:'all'})" id="all"><span>全部</span></a>
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
	vip.list.basePath = "/admin/loan/p2puser/investorapply/";
});

function reload2(){
	Close();
	vip.list.reload();
}

function modifyStatus(userId,stat) {
	if(userId==null || $.trim(userId)=="" || stat==null || $.trim(stat)=="") {
		Wrong("用户编号或者状态不能为空");
		return;
	}
	var status = 0;
	var msg = "";
	if(stat==0) {
		status = 1;
		msg = "确定开启p2p借贷吗？";
	} else if(stat==1) {
		status = 0;
		msg = "确定关闭p2p借贷吗？";
	}
	vip.list.reloadAsk({
		"title" : msg,
		url:"/admin/loan/p2puser/modifyStatus?userId="+userId+"&status="+status}
	);
}

function modifyLoanOutStatus(userId,stat) {
	if(userId==null || $.trim(userId)=="" || stat==null || $.trim(stat)=="") {
		Wrong("用户编号或者状态不能为空");
		return;
	}
	var status = 0;
	var msg = "";
	if(stat==0) {
		status = 1;
		msg = "确定为该用户开启p2p放贷吗？";
	} else if(stat==1) {
		status = 0;
		msg = "确定为该用户关闭p2p放贷吗？";
	}
	vip.list.reloadAsk({
		"title" : msg,
		url:"/admin/loan/p2puser/modifyLoanOutStatus?userId="+userId+"&status="+status}
	);
}

function modifyLoanInStatus(userId,stat) {
	if(userId==null || $.trim(userId)=="" || stat==null || $.trim(stat)=="") {
		Wrong("用户编号或者状态不能为空");
		return;
	}
	var status = 0;
	var msg = "";
	if(stat==0) {
		status = 1;
		msg = "确定允许该用户被借入吗？";
	} else if(stat==1) {
		status = 0;
		msg = "确定禁止该用户被借入吗？";
	}
	vip.list.reloadAsk({
		"title" : msg,
		url:"/admin/loan/p2puser/modifyLoanInStatus?userId="+userId+"&status="+status}
	);
}

function userLend(userId,lends){
	if (userId == null || $.trim(userId) == "" || lends == null || $.trim(lends) == "") {
		Wrong("用户编号不能为空！");
		return;
	}
	var userLend=0;
	Iframe({
	Url : "/admin/loan/p2puser/lend?userId=" + userId + "&userLend=" + lends,
		zoomSpeedIn : 200,
		zoomSpeedOut : 200,
		Width : 560,
		Height : 400,
        isShowIframeTitle: true,
		Title : "修改用户投资金额"
	});
}

function rejectInvStatus(userId) {
	if(userId==null || $.trim(userId)=="") {
		Wrong("用户编号不能为空");
		return;
	}
	msg = "确定拒绝该用户的申请吗？";
	vip.list.reloadAsk({
		"title" : msg,
		url:"/admin/loan/p2puser/investorapply/rejectInvStatus?userId="+userId}
	);
}

</script>

</body>
</html>
