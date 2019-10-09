<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>用户认证日志</title>
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
				<div class="formline">
					<span class="formtit">用户ID：</span> 
					<span class="formcon">
						<input id="userId" mytitle="用户ID要求填写一个长度小于50的字符串" name="userId" pattern="limit(0,10);num()" size="10" type="text"/>
					</span>
				


					<span class="formtit">类型：</span> 
					<span class="formcon" style="line-height: 40px;">
						<select id="type" name="type" style="display: none;" selectid="select_46344474">
							<option value="">--请选择--</option>
							<c:forEach var="at" items="${authType }">
								<option value="${at.key }">${at.value }</option>
							</c:forEach>
						</select>
						<div class="SelectGray" id="select_46344474"><span><i style="width: 58px;">--请选择--</i></span></div>
					</span>
				</div>
				
				<div class="formline">
					<p>
						<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
						<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
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
	vip.list.funcName = "认证日志";
	vip.list.basePath = "/admin/user/authlog/";
});
</script>

</body>
</html>
