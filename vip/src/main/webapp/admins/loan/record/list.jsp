<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>用户借贷投标记录</title>
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
						<span class="formtit">借入者：</span> 
						<span class="formcon">
							<input id="userId" mytitle="字段要求填写一个长度小于50的字符串" name="inUserName" pattern="limit(0,50)" size="20" type="text"/>
						</span>

						<span class="formtit">投资者：</span> 
						<span class="formcon">
							<input id="userName" mytitle="用户名要求填写一个长度小于50的字符串" name="outUserName" pattern="limit(0,50)" size="20" type="text"/>
						</span>

						<span class="formtit">借贷类型：</span> 
						<span class="formcon">
							<select name="isIn" id="isIn" style="width:100px;display: none;" selectid="select_24962646">
					        	<option value="">全部</option>
					        	<option value="1">借款</option>
			             		<option value="2">投资</option>
					        </select>
					        <div class="SelectGray" id="select_24962646"><span><i style="width: 111px;">全部</i></span></div>
						</span>
						
						<span class="formtit">借贷状态：</span> 
						<span class="formcon">
							<select name="status" id="status" style="width:100px;display: none;" selectid="select_24962655">
					        	<option value="">全部</option>
					        	<option value="1">还款中</option>
					        	<option value="2">已还款</option>
					        	<option value="3">需要平仓</option>
					        	<option value="4">平仓还款</option>
					        </select>
					        <div class="SelectGray" id="select_24962655"><span><i style="width: 111px;">全部</i></span></div>
						</span>
						
						<span class="formtit">排序：</span> 
						<span class="formcon">
							<select name="dir" id="dir" style="width:100px;display: none;" selectid="select_24962656">
					        	<option value="createTime" selected="selected">借款时间倒序</option>
					        	<option value="repayDate">还款时间倒序</option>
					        </select>
					        <div class="SelectGray" id="select_24962656"><span><i style="width: 111px;">借款时间</i></span></div>
						</span>

						<p>
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
							<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
						</p>
					</div>
					<div style="clear: both;"></div>
					<div class="formline">
					</div>
	
				</div>
		
			</form>
		</div>
		<div class="tab_head" id="userTab">
			<a href="javascript:vip.list.search({tab:'0'})" class="current" id="0"><span>所有借贷</span></a>
			<c:if test="${coinMaps!=null }">
				<c:forEach items="${coinMaps}" var="coin" >
					<a href="javascript:vip.list.search({tab:'${coin.value.fundsType}'})" id="${coin.value.fundsType}"><span>${coin.value.propTag}借贷</span></a>
				</c:forEach>
			</c:if>
			<!-- <a href="javascript:vip.list.search({tab:'rmb'})" id="rmb"><span>RMB借贷</span></a>
			<a href="javascript:vip.list.search({tab:'btc'})" id="btc"><span>BTC借贷</span></a>
			<a href="javascript:vip.list.search({tab:'ltc'})" id="ltc"><span>LTC借贷</span></a>
			<a href="javascript:vip.list.search({tab:'eth'})" id="eth"><span>ETH借贷</span></a>
			<a href="javascript:vip.list.search({tab:'etc'})" id="etc"><span>ETC借贷</span></a> -->
		</div>
		<div class="tab-body" id="shopslist">
			<jsp:include page="ajax.jsp" />
		</div>
	</div>
</div>	

<script type="text/javascript">
$(function(){ 
 	vip.list.ui();
	vip.list.funcName = "借贷记录";
	vip.list.basePath = "/admin/loan/record/";
});

function reload2(){
	Close();
	vip.list.reload();
}

//分配客服
function addAuditor(id){
	Iframe({
		Url : "/admin/user/addAuditor?userId=" + id,
		zoomSpeedIn : 200,
		zoomSpeedOut : 200,
		Width : 590,
		Height: 430,
        isShowIframeTitle: true,
		Title : "分配客服"
	});	  
}

//全选按钮的方法
function selectAll(){
	
	changeCheckBox('delAll');
	$(".hd .checkbox").trigger("click");
	//$("#ck_0,#ck_1,#ck_2,#ck_3,#ck_4,#ck_5,#ck_6,#ck_7,#ck_8,#ck_9").trigger('click');
}

function deletes(){
	var ids="";
	$(".checkItem").each(function(){
		var id=$(this).val();
		if($(this).attr("checked")==true){
			ids+=id+",";
		}
	});
	var list=ids.split(",");
	if(list.length==1){ 
		Wrong("请选择一项"); 
		return;
	}
	doDelMore(ids);
}
var commids = "";
function doDelMore(ids){
	if(!couldPass){
		commids = ids;
		googleCode("doDelMore", true);
		return;
	}
	couldPass = false;
	
	vip.list.reloadAsk({
		title : "确定要删除选中的项吗？",
		url : "/admin/user/dodel?id="+commids+"&mCode="+ids
	});
}

function continueLoan(ids){
	if(!couldPass){
		commids = ids;
		googleCode("continueLoan", true);
		return;
	}
	couldPass = false;
	
	vip.list.reloadAsk({
		title : "确定要更改该记录的状态吗，更改之后平仓状态会消失？",
		url : "/admin/loan/record/continueLoan?id="+commids+"&mCode="+ids
	});
}
function force(ids){
	if(!couldPass){
		commids = ids;
		googleCode("force", true);
		return;
	}
	couldPass = false;
	
	vip.list.reloadAsk({
		title : "确定要强制给用户平仓吗，命令发送之后系统会智能判断？",
		url : "/admin/loan/record/force?userId="+commids+"&mCode="+ids
	});
}
</script>

</body>
</html>
