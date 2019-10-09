<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<jsp:include page="/admins/top.jsp" />
<script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>

<style type="text/css">
.form-tit {width: 100px;}
.form-line {margin-left:20px;}
.col-main .prompt.b_yellow.remind2.pl_35 {
	padding:0 10px 20px 10px;
}
.form-btn {
    padding: 15px 0 0 121px;
}
.select_wrap select {
    color: #6D6D6D;
    float: left;
 
    padding: 5px;
}

.bankbox{ padding:15px;}
.bankbox .bd {
	padding-right: 20px;
	padding-left: 20px;
}
.formlist .formline {
	overflow:hidden;
	padding-bottom:8px;
	clear: both;
}
span.txt{float:left;margin-right: 5px;}
</style>

<script type="text/javascript">
$(function(){
	$("#bankBox").Ui();
	
});

function ok() {
	var actionUrl = "/admin/financial/settlementdetail/doAoru";
	vip.ajax( {
		formId : "bankBox",
		url : actionUrl,
		div : "bankBox",
		suc : function(xml) {
			parent.Right($(xml).find("MainData").text(), {
				callback : "reload2()"
			});
		}
	});
}

</script>
</head>

<body >
<div class="bankbox" id="bankBox">

<div class="bankbox_bd">
	<div class="form-line">
		<div class="form-tit">币种：</div>
		<div class="form-con">
			<select name="currency" id="currency">
				<c:forEach items="${coinMap}" var="map">
					<option value="${map.key}" ${coinType == map.key ? "selected" : ""}>${map.value.propTag}</option>
				</c:forEach>
			</select>
		</div>
	</div>
	<div class="form-line">
		<div class="form-tit">类型：</div>
		<div class="form-con">
			<select name="isIn" id="isIn">
				<option value="1" ${isIn == 1 ? "selected" : ""}>充值</option>
				<option value="0" ${isIn == 0 ? "selected" : ""}>提现</option>
			</select>
		</div>
	</div>
	<div class="form-line">
    	<div class="form-tit">时间：</div>
    	<div class="form-con">
    		<input class="text" type="text" value="${startTime}" name="startTime" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" id="startTime"/>
			-
			<input class="text" type="text" value="<fmt:formatDate value="${endTime}" pattern="yyyy-MM-dd HH:mm:ss"/>" name="endTime" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" id="endTime"/>
		</div>
	</div>
	

	<div class="form-btn">
		<a class="btn" href="javascript:ok();"><span class="cont">确定</span></a>
		<a href="javascript:parent.Close();" class="btn btn-gray"><span class="cont">取消</span></a>
	</div>
</div>
</div>

</body>
</html>
