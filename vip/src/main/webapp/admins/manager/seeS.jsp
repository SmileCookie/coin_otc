<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
   <jsp:include page="/admins/top.jsp" />
<style type="text/css">
.bankbox{padding: 10px 0 0 126px;}
.bankbox_bd{margin-left: 22px;}
</style>

<script type="text/javascript">
$(function(){
	$("#bankBox").Ui();
});

function ok() {
	var actionUrl = "/admin/btc/recharge/doCharge?mCode="+mCode;
	vip.ajax( {
		formId : "bankBox",
		url : actionUrl,
		div : "bankBox",
		suc : function(xml) {
			parent.Right($(xml).find("Des").text(), {
				callback : "reload2()"
			});
		},
		err : function(xml){
			Wrong($(xml).find("Des").text());
		}
	});
}
</script>
</head>

<body >
<div class="bankbox" id="bankBox">
	<c:if test="${errmsg!=null }"><font color="#ff0000;">${errmsg }</font></c:if>
	<c:if test="${errmsg==null }">
		<div class="bankbox_bd">
			<img src="${url}" width="150px" height="150px"/>
		</div>
		<input type="text" id="secret" name="secret" style="margin-top: 10px;width: 202px;height:26px;" value="${secret}"/>
	</c:if>
</div>

</body>
</html>
