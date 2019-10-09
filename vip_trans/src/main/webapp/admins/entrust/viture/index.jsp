<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>BTC委托记录</title>
   <link href="${static_domain }/statics/css/admin/global.css" rel="stylesheet" type="text/css"/>
   <link href="${static_domain }/statics/css/admin/control.css" rel="stylesheet" type="text/css" /> 
   <script type="text/javascript" src="${static_domain }/statics/js/admin/jquery.js"></script>
   <script type="text/javascript" src="${static_domain }/statics/js/admin/global.js"></script>
   <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>
	
</head>
<body >
<div class="mains">
	<div class="tab_head" id="userTab">
			<a href="/admin/entrust/viture?tab=btcdefault" id="btcdefault" <c:if test="${tab=='btcdefault' }">class="current"</c:if>><span>比特币</span></a>
			<a href="/admin/entrust/viture?tab=ltcdefault" id="ltcdefault" <c:if test="${tab=='ltcdefault' }">class="current"</c:if>><span>莱特币</span></a>
			<a href="/admin/entrust/viture?tab=ethdefault" id="ethdefault" <c:if test="${tab=='ethdefault' }">class="current"</c:if>><span>以太币/人民币</span></a>
			<a href="/admin/entrust/viture?tab=ethbtcdefault" id="ethbtcdefault" <c:if test="${tab=='ethbtcdefault' }">class="current"</c:if>><span>以太币/比特币</span></a>
	</div>
	<div class="col-main">
		<table class="tb-list2" style="width:100%;table-layout: fixed;">
				<tr>
					<td colspan="3"><h3>所有虚拟委托</h3></td>
				</tr>
				<tr>
					<th>委托单价</th>
					<th>委托数量</th>
					<th>操 作</th>
				</tr>
				<c:choose>
					<c:when test="${length eq 0}">
						<tr>
							<td colspan="3" style="text-align:center;color:red;">暂无虚拟委托数据</td>
						</tr>
					</c:when>
					<c:otherwise>
						<c:forEach items="${result }" var="list">
							<tr>
								<td>${list[0] }</td>
								<td>${list[1] }</td>
								<td>
									<a class="search-submit" id="idSearch" href="javascript:doCancel(${list[0]-1 },${list[0]+1 });">确定取消</a> 
								</td>
							</tr>
						</c:forEach>
					</c:otherwise>
				</c:choose>
		</table>
		<br/>
		<hr style="color:#ccc"/>
		<br/>
		<table class="tb-list2" style="width:100%;table-layout: fixed;">
				<tr>
					<td>委托单价</td>
					<td><input type="text" class="inputW2" id="unitPrice" name="unitPrice" style="width:140px;"/></td>
					<td>委托数量</td>
					<td><input type="text" class="inputW2" id="number" name="number" style="width:140px;"/></td>
					<td><a class="search-submit" id="idSearch" href="javascript:doEntrust();">确定委托</a> </td>
				</tr>
				<tr>
					<td>最小委托单价</td>
					<td><input type="text" class="inputW2" id="minPrice" name="minPrice" style="width:140px;"/></td>
					<td>最大委托单价</td>
					<td><input type="text" class="inputW2" id="maxPrice" name="maxPrice" style="width:140px;"/></td>
					<td><a class="search-submit" id="idSearch" href="javascript:cancel();">确定取消</a> </td>
				</tr>
		</table>
	</div>
</div>	

<script type="text/javascript">
function doEntrust(){
	var unitPrice = $("#unitPrice").val();
	var number = $("#number").val();
	if(unitPrice == "" || unitPrice <= 0){
		Alert("单价不能为空，且必须大于0");
		return;
	}
	if(number == "" || number <= 0){
		Alert("委托数量不能为空，且必须大于0");
		return;
	}
    $.getJSON("/admin/entrust/viture/doVitureEntrust?market=${tab }&unitPrice="+unitPrice+"&number="+number,  function(result) {
    	if(result.isSuc == true){
    		alert("委托成功");
    		document.location.href = "/admin/entrust/viture?tab=${tab}";
    	}else{
    		Alert("未知异常");
    	}
	}); 
}

function cancel(){
	var minPrice = $("#minPrice").val();
	var maxPrice = $("#maxPrice").val();
	if(minPrice == "" || minPrice <= 0){
		Alert("最小价格不能为空，且必须大于0");
		return;
	}
	if(maxPrice == "" || maxPrice <= 0){
		Alert("最大价格不能为空，且必须大于0");
		return;
	}
	if(Number(minPrice) >= Number(maxPrice)){
		Alert("最小价格不能高于最大价格");
		return;
	}
	doCancel(minPrice,maxPrice);
}

function doCancel(minPrice, maxPrice){
	if(!confirm("您确定要取消虚拟委托吗?")) return;
    $.getJSON("/admin/entrust/viture/doCancelVitureEntrust?market=${tab }&minPrice="+minPrice+"&maxPrice="+maxPrice,  function(result) {
    	if(result.isSuc == true){
    		alert("撤消成功");
    		document.location.href = "/admin/entrust/viture?tab=${tab}";
    	}else{
    		Alert("未知异常");
    	}
	}); 
}
</script>

</body>
</html>
