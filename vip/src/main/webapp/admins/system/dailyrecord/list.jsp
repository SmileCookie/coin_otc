<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>系统日志</title>
      <jsp:include page="/admins/top.jsp" />

<style type="text/css">
label.checkbox{  margin: 3px 6px 0 7px;}
label.checkbox em{ padding-left:18px; line-height:15px; float:left; font-style:normal;}
.page_nav{ margin-top:10px;}


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
				<div id="formSearchContainer">
					<p>
						<span>管理员：</span>
						<select name="userId" id="userId" style="width:140px;">
				           <option value="">全部</option>
				           <c:forEach items="${admins}" var="admin">
				           		<option value="${admin.id }">${admin.admName }</option>
				           </c:forEach>
				        </select>
					</p>
					<p>
						<span>日志类型：</span>
						<select name="type" id="type" style="width:180px;">
				           <option value="">全部</option>
				           <c:forEach items="${types}" var="type">
				           		<option value="${type.key }">${type.value }</option>
				           </c:forEach>
				        </select>
					</p>
					<p>
						<span>日志信息：</span>
						<input id="memo" mytitle="搜索日志信息记录的内容。比如汇款单号..." name="memo" position="n" pattern="limit(0,50)" size="30" type="text"/>
					</p>
      	              <div style="clear: both;"></div>
                     <span class="formtit" style="margin-left: 10px;">谷歌码：</span> 
                     <span class="formcon">
                        <input type="text" name="mCode" id="mCode" value="" pattern="limit(0,10)"/>
                     </span>
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
	vip.list.funcName = "系统日志";
	vip.list.basePath = "/admin/system/dailyrecord/";
});
</script>

</body>
</html>
