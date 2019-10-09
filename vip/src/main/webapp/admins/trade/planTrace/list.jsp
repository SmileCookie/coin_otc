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
	<script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>
<script type="text/javascript">
$(function(){
 	vip.list.ui();
	vip.list.funcName = "计划委托追踪";
	vip.list.basePath = "/admin/trade/planTrans/";
});

function reload2(){
	Close();
	vip.list.reload();



}





</script>	
</head>
<body >
<div class="mains">
<div class="col-main">
	<div class="form-search">
		<form autocomplete="off" name="searchForm" id="searchContaint">
			<div id="formSearchContainer">
				<input type="hidden" name="tab" id="tab" value="${tab }" />
				<p>
					<span>委托单号：</span>
					<input id="entrustId"  name="entrustId" style=" width:80px;" pattern="limit(0,50)" size="20" type="text"/>
				</p>
				<p>
					<span>委托用户ID：</span>
					<input id="userId"  name="userId" style=" width:80px;" pattern="limit(0,50)" size="20" type="text"/>
				</p>

				<p class="formCloumn">
						<span class="formText">
							排序：
						</span>
					<span class="formContainer">
							<select id="type" name="type">
								<option value="2">-请选择-</option>
								<option value="0">卖出</option>
								<option value="1">购买</option>
								<option value="1">取消</option>
							</select>
						</span>
				</p>

				<p class="formCloumn">
						<span class="formText">
							状态：
						</span>
					<span class="formContainer">
							<select id="status" name="status">
								<option value="0">-请选择-</option>
								<option value="-1">未完成并且不等于取消</option>
								<option value="1">取消</option>
								<option value="2">交易成功</option>
								<option value="3">交易一部分</option>
							</select>
						</span>
				</p>


				<span class="formtit">开始日期：</span>
				<span class="formcon mr_5">
							<input type="text" class="inputW2 Wdate" id="timeFrom" name="timeFrom"  onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',lang : 'cn'})" style="width:120px;"/>
						</span>
				<span class="formtit">&nbsp;&nbsp;&nbsp;</span>
				<span class="formtit">结束日期：</span>
				<span class="formcon mr_5">
							<input type="text" class="inputW2 Wdate" id="timeTo" name="timeTo" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',lang : 'cn'})" style="width:120px;"/>
						</span>
				<p>
					<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
					<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
					
				</p>
			</div>
	
		</form>
	</div>
		<div class="tab_head">
				<c:forEach items="${markets}" var="market">
					<a href="/admin/trade/planTrans?tab=${market.key}" class="${market.key == tab ? 'current' : ''}">${market.key.toUpperCase()}</a>
				</c:forEach>
			</div>
			
	
	<div class="tab-body" id="shopslist">
		<jsp:include page="ajax.jsp" />
	</div>
</div>
</div>	
</body>
</html>
