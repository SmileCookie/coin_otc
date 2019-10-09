<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
<jsp:include page="/admins/top.jsp" />

<script type="text/javascript">
$(function(){
	$("#add_or_update").Ui();
});

function ok(){
	var actionUrl = "/admin/user/modifyCustomerOperation";
	vip.ajax( {
		formId : "add_or_update",
		url : actionUrl,
		div : "add_or_update",
		suc : function(xml) {
			parent.Right($(xml).find("MainData").text(), {callback:"reload2()"});
		}
	});
}

</script>
<style type="text/css">
.form-tit{ width:100px;float:left;}
input{padding:8px 5px;}
.main-bd {
    padding: 30px;
}
</style>
</head>

<body>
	<div id="add_or_update" class="main-bd">
			<div class="form-line" style = "display:block;width:100%;"}>
				<span style="float:left;" class="formtit">用户操作类型：</span>
				<span style="float:left;margin: 2px 20px 0 0px;" class="formcon">
					<select name="customerOperation" id="customerOperation" style="width:110px;display: none;">
						<c:forEach items="${operationMap}" var="entry">
							<option style="width:100px;" value=${entry.key} <c:if test="${entry.key==user.customerOperation}">selected="selected"</c:if>>${entry.value}</option>
						</c:forEach>
					 </select>
				</span>
				<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>
				<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>
				<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>
				<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>
				<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>
		</div>
		<div class="form-btn">
			<input type="hidden" id="userId" name="userId" value="${user.id}" />
			<a class="btn" href="javascript:ok();"><span class="cont">确定</span></a>
			<a href="javascript:parent.Close();" class="btn btn-gray"><span class="cont">取消</span></a>
		</div>
	</div>
</body>
</html>