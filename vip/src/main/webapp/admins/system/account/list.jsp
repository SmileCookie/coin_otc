<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>系统功能</title>
<jsp:include page="/admins/top.jsp" />
</head>
<body >

<script type="text/javascript">
$(function(){ 
 	vip.list.ui();
	vip.list.funcName = "邮件账户";
	vip.list.basePath = "/admin/system/account/";
});
</script>
<div class="mains">
<div class="col-main">
<div class="form-search">
				<form autocomplete="off" name="searchForm" id="searchContaint">
					<div id="formSearchContainer">
						<p>
							<span>编号：</span>
							<input errormsg="请检查字段AdmId是否是数字类型的,注意,本字段功能如下: 管理员编号" id="admId"
								mytitle="AdmId要求填写一个数字类型的值" name="admId" pattern="num()"
								size="10" type="text" value="${admId }"/>
						</p>
						
						<p>
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
							<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
							<a class="search-submit" href="javascript:vip.list.aoru({id : 0 , width : 500 , height : 410});">添加</a>
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
