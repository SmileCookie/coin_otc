<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page isELIgnored="false"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>提现地址</title>
   <jsp:include page="/admins/top.jsp" />
   <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>
<script type="text/javascript">
$(function(){
	vip.list.ui();
	vip.list.funcName = "提现地址";
	vip.list.basePath = "/admin/btc/downloadkey/"; 
});

function reload2(){
	Close();
	vip.list.reload();
}

function unLock(ids){
	if(!couldPass){
		commids = ids;
		googleCode("unLock", true);
		return;
	}
	couldPass = false;
	
	var actionUrl = "/admin/btc/downloadkey/unLock?id="+commids+"&mCode="+ids+"&coint=${coint.stag}";
	Ask2({Msg:"确定要解除锁定吗？", call:function(){
		vip.ajax( {
			url : actionUrl,
			dataType : "json",
			suc : function(json) {
				Right(json.des, {callback:"reload2()"});
			}
		});
	}});
}
</script>

		<style type="text/css">
		.col-main{float:left;width:100%;}
		.tb-list2 th{padding:8px 10px;}
		.tb-list2 .hd td span b{font-weight:normal;color:#C92707;}
		.tb-list2 {width: 100%;}
		.tb-list2 a span{color:#C92707;display: inline-block;width: 110px;}
		
		.form-search .formline{float:left;}
.form-search p{float:none;}
		</style>
	</head>
	<body>
	
		<div class="mains">
		<jsp:include page="/admins/topTab.jsp" />
		<input type="hidden" value="${currentTab }" id="currentTab"/>
		
			<div class="col-main">
				<form autocomplete="off" name="searchForm" id="searchContaint">
				<div class="form-search" id="searchContainer">
					<input type="hidden" id="tab" name="tab" value="${tab }" />
					
					<div class="formline">
						<span class="formtit">用户ID：</span> 
						<span class="formcon">
							<input id="userId" mytitle="用户Id搜索" name="userId" pattern="limit(0,50)" size="10" type="text"/>
						</span>

						<%--<span class="formtit">用户名：</span>
						<span class="formcon">
							<input id="userName" mytitle="用户名要求填写一个长度小于50的字符串" name="userName" pattern="limit(0,50)" size="20" type="text"/>
						</span>--%>
						
					</div>
					<div class="formline">
						<span class="formtit">提现地址：</span> 
						<span class="formcon">
							<input id="address" mytitle="请输入地址。" name="address" pattern="limit(0,50)" size="45" type="text"/>
						</span>
						<p>
							<input type="hidden" name="${coint.coinParam }" value="${coint.propTag }"  />
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
							<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
						</p>
					</div>
				</div>
				</form>
				<div class="tab_head" id="userTab">			
					<a href="javascript:vip.list.search({tab : 'normal'});" id="normal" class="current"><span>常规地址</span></a>
					<a href="javascript:vip.list.search({tab : 'all'});" id="all"><span>所有</span></a>
				</div>
				<div class="tab-body"  id="shopslist">
					<jsp:include page="ajax.jsp" />
				</div>
			</div>
		</div>
	</body>
</html>
