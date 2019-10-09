<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<jsp:include page="/admins/top.jsp" />
	
<style type="text/css">
.bankbox{padding: 30px;}
span.txt{float:left;margin-right: 5px;}
</style>

<script type="text/javascript">
$(function(){
	$("#bankBox").Ui();
});

function ok() {
	
	var actionUrl = "/admin/loan/p2puser/saveLevel";
	vip.ajax( {
		formId : "bankBox",
		url : actionUrl,
		div : "bankBox",
		dataType : "json",
		suc : function(json) {
			parent.Right(json.des, {
				callback : "reload2()"
			});
		},
		err : function(json){
			Wrong(json.des);
		}
	});
}
</script>
</head>

<body >
<div class="bankbox" id="bankBox">

<div class="bankbox_bd">

	<div class="form-line">
    	<div class="form-tit">借贷级别：</div>
    	<div class="form-con">
    		<select name="level" id="level" pattern="true" style="width:200px;"  errormsg="请选择借贷级别"> 
           		<option value="">--请选择--</option>
           		<c:forEach var="level" items="${levers}">
	             	<option value="${level.key}" <c:if test="${user.level==level.key}">selected="selected"</c:if>>${level.value}</option>
           		</c:forEach>
            </select>
  		</div>
	</div>
	
	<div class="form-btn">
		<input type="hidden" id="userId" name="userId" value="${user.userId }"/>
		
      	<a class="btn" href="javascript:ok();"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a> 
      	<a href="javascript:parent.Close()" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>
    </div>
	
</div>
</div>
</body>
</html>
