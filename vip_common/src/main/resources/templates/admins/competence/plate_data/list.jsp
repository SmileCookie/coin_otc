<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>板块数据</title>
<link href="${static_domain }/statics/css/admin/global.css" rel="stylesheet" type="text/css"/>
<link href="${static_domain }/statics/css/admin/control.css" rel="stylesheet" type="text/css" /> 
<script type="text/javascript" src="${static_domain }/statics/js/admin/jquery.js"></script>
<script type="text/javascript" src="${static_domain }/statics/js/admin/global.js"></script>
<script type="text/javascript">

$(function(){ 
 	vip.list.ui();
	vip.list.funcName = "板块数据";
	vip.list.basePath = "/admin/competence/plate_data/";
});
</script>	
</head>
<body >
<div class="mains">
<div class="col-main">
	<div class="form-search">
		<form autocomplete="off" name="searchForm" id="searchContaint">
			<div id="formSearchContainer">
				<p>
					<span>数据编号：</span>
					<input errormsg="请检查字段AdmRoleId是否是数字类型的,注意,本字段功能如下: 角色编号"
						id="id" mytitle="AdmRoleId要求填写一个数字类型的值" name="id"
						pattern="num()" size="10" type="text"/>
				</p>
				<p>
					<span>描述：</span>
					<input id="dataDes" mytitle="AdmDes要求填写一个长度小于50的字符串" name="dataDes"
						pattern="limit(0,50)" size="20" type="text"/>
				</p>
				<p>
					<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
					<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
					<a class="search-submit" href="javascript:vip.list.aoru({id : 0 , width : 600 , height : 360});">添加</a>
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
