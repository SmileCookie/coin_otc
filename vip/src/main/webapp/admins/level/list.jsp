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
	vip.list.funcName = "用户积分等级";
	vip.list.basePath = "/admin/level/";
});

function update(id){
	Ask2({Msg:"确定要修改吗？", call:function(){
		vip.ajax({
			url : "/admin/level/doaoru?id="+id,
			dataType : "json",
			suc : function(json) {
				Right(json.des, {callback:"reload2()"});
			}
		});
	}});
}

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
				<p>
					<span>等级：</span>
					<select id="vipRate"  name="vipRate">
						<option value="-1">全部</option>
						<c:forEach items="${vipRatesType}" var="vipRate">
							<option value="${vipRate.id }">${vipRate.name }</option>
						</c:forEach>
						
					</select>
				</p>
				<p>
					<span>所需积分：</span>
					<input id="jifen"  name="jifen" style=" width:80px;" pattern="limit(0,50);num()" size="20" type="text"/>
				</p>
				<p>
					<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
					<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
					<a id="idSearch" style="width: 68px" class="search-submit" href="javascript:vip.list.aoru({ width : 600 , height : 660});">添加</a>
				</p>
			</div>
	
		</form>
	</div>
	<div class="tab-body" id="shopslist">
		<jsp:include page="ajax.jsp" />
	</div>
</div>
</div>	
</body>
</html>
