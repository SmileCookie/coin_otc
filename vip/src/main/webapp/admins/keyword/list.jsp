<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>关键字</title>
   <jsp:include page="/admins/top.jsp" />
</head>
<body >
<div class="mains">
	<div class="col-main">
		<div class="form-search">
			<form autocomplete="off" name="searchForm" id="searchContaint">
				<div id="formSearchContainer">
					<div class="formline">
						<span class="formtit">关键字：</span> 
						<span class="formcon">
							<input id="word" mytitle="字段要求填写一个长度小于50的字符串" name="word"
								pattern="limit(0,50)" size="20" type="text"/>
						</span>
						
						<span style="float:left;" class="formtit">类型：</span> 
						<span style="float:left;margin: 2px 20px 0 0px;" class="formcon">
							<select name="kType" id="kType" style="width:140px;display: none;" selectid="select_24962645">
				             	<option value="">全部</option>
				             	<c:forEach var="type" items="${types }">
				             		<option value="${type.key }">${type.value }</option>
				             	</c:forEach>
					         </select>
					         <div class="SelectGray" id="select_24962645"><span><i style="width: 111px;">全部</i></span></div>
						</span>
					</div>
					<p>
						<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
						<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
						<a id="idReset" class="search-submit" href="javascript:vip.list.aoru();">添加</a>
					</p>
				</div>
		
			</form>
		</div>
		
		<div class="tab-body" id="shopslist">
			<jsp:include page="ajax.jsp" />
		</div>
	</div>
</div>	

<script type="text/javascript">
$(function(){ 
 	vip.list.ui();
	vip.list.funcName = "关键字";
	vip.list.basePath = "/admin/keywordsm/";
});

function reload2(){
	Close();
	vip.list.reload();
}

</script>

</body>
</html>
