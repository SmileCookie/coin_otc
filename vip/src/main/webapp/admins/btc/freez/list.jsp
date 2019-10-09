<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
	<title>资金冻结管理</title>
   <jsp:include page="/admins/top.jsp" />
   <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script> 
	</head>
<body>
<div class="mains">
	<div class="col-main">
			<div class="form-search" id="listSearch">
			<form autocomplete="off" name="searchForm" id="searchContaint">
				<p class="formCloumn">
					<span class="formText">
						提交时间：
					</span>
					<span class="formContainer">
						<span class="spacing">从</span><span class="formcon mr_5">
  								<input type="text" class="Wdate" onFocus="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})" name="startDate" id="startDate" size="15"/></span>
  								<span class="spacing">到</span> 
  								<input type="text" class="Wdate" onFocus="WdatePicker({readOnly:true,dateFmt:'yyyy-MM-dd'})" name="endDate" id="endDate" size="15" />
					</span>
				</p>
				<p class="formCloumn">
					<span class="formText">
						用户名：
					</span>
					<span class="formContainer">
						<input type="text" id="userName" name="userName" value="${userName }" style="width:80px;"/>
					</span>
				</p>
				
				<p class="formCloumn">
					<span class="formText">
						备注：
					</span>
					<span class="formContainer">
						<input type="text" id="memo" name="memo" style="width:100px;"/>
					</span>
				</p>
				<p>
					<input type="hidden" name="${coint.coinParam }" value="${coint.propTag }"  />
					<a class="search-submit" id="idSearch" href="javascript:chbtc.list.search();">查找</a> 
					<a href="javascript:chbtc.list.resetForm();" id="idReset" class="search-submit">重置</a>
					<a class="search-submit" href="javascript:btcFreez('','${coint.propTag }');" id="sysFreezBtn">冻结${coint.propTag }</a>
					<a class="search-submit" href="javascript:btcUnFreez('','${coint.propTag }');" id="sysUnFreezBtn">解冻${coint.propTag }</a>
				</p>
			</form>
			</div>
			
			<div class="tab_head" id="userTab">
				<a href="javascript:chbtc.list.search({tab : 'all'});" id="all"><span>所有冻结</span></a>
				<a href="javascript:chbtc.list.search({tab : 'manager'});" id="manager"><span>管理员冻结</span></a>
				<a href="javascript:chbtc.list.search({tab : 'system'});" class="current" id="system"><span>系统冻结</span></a>
			</div>
			
			<div class="tab-body" id="shopslist">
				<jsp:include page="ajax.jsp" />	
			</div>
			
	</div>
</div>
<script type="text/javascript">
$(function(){
	chbtc.list.ui();
	chbtc.list.funcName = "冻结管理";
	chbtc.list.basePath = "/admin/btc/freez/"; 
	
});
var commid="";
function unfreez(id){
	if(!couldPass){
		commid = id;
		googleCode("unfreez", true);
		return;
	}
	var urls="/admin/btc/freez/unfreez?fid="+commid+"&mCode="+ids+"&coint=${coint.propTag }";
	Ask2({Msg:"确定要解冻此冻结吗？", call:function(){
		couldPass=false;
		chbtc.ajax({
			url : urls,
			dataType : "xml",
			suc : function(json) {
				Right($(xml).find("Des").text(), {callback:"reload2()"});
			}
		});
	}});
}

function reload2(){
	Close();
	chbtc.list.reload();
}
</script>
</body>
</html>
