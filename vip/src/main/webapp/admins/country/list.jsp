<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>国家</title>
<jsp:include page="/admins/top.jsp" />
</head>
<body >
<div class="mains">
	<div class="col-main">
		<div class="form-search">
			<form autocomplete="off" name="searchForm" id="searchContaint">
				<div id="formSearchContainer">
					<p>
						<span>名称：</span>
						<input id="name" mytitle="字段要求填写一个长度小于50的字符串" name="name"
								pattern="limit(0,50)" size="20" type="text"/>
					</p>
					<p>
						<span>简称：</span>
						<input id="des" mytitle="字段要求填写一个长度小于50的字符串" name="des"
								pattern="limit(0,50)" size="20" type="text"/>
					</p>
					<p>
						<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
						<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
						<a id="idSearch" class="search-submit" href="javascript:vip.list.aoru();">添加</a>
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
	vip.list.funcName = "国家";
	vip.list.basePath = "/admin/countrym/";
});

function reload2(){
	Close();
	vip.list.reload();
}

</script>

</body>
</html>
