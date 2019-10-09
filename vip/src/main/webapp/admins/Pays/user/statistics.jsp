<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage=""%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title></title>
   <jsp:include page="/admins/top.jsp" />
   <link href="${static_domain }/statics/css/old_uadmin.css" rel="stylesheet" type="text/css" /> 
   <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>
   
   <script type="text/javascript">
   		function getInfo() {
   			var startDate = $("#startDate").val();
   			var endtDate = $("#endtDate").val();
   			if((startDate==null||startDate=="") && (endtDate==null||endtDate=="")) {
   				Wrong("请输入时间段");
   				return;
   			}
   			var userId = '${userId}';
   			$.getJSON("/admin/pay/user/getInfo?userId="+userId+"&startDate="+startDate+"&endtDate="+endtDate,function(result){
   				if(result.isSuc) {
   					$("#in").text(result.datas.in1);
	        		$("#out").text(result.datas.out1);
	        		$("#totalLixi").text(result.datas.totalLixi);
   				}
   			});
   		}
   </script>
<style type="text/css">
.usercontent {
	width: 690px;
}
body{background: none;}
.main-bd {
	margin: 10px 0 0 10px;
	overflow: hidden;
	padding: 20px;
	width: 628px;
}

.user-info-content li.op {
	width: 220px;
	float: left;
}

.user-information .bd .details .op {
	margin-right: 30px;
}

.user-information .ft .num-info .s1 {
    margin-left: 20px;
}
.user-information .ft .num-info .s2 {
    padding-right: 0;
    float: left;
    width: 210px;
}
.daycash .s2 {
    padding-right: 0;
    float: left;
    width: 200px;
}

.user-information .ft {
    font-size: 12px;
    font-weight: bold;
    line-height: 30px;
}
.user-information .ft .unit {
    line-height: 60px;
}
.user-information .ft .num-info {
    width: 630px;
    padding-left: 10px;
}

.user-information .bd .details {
    float: left;
    padding-left: 15px;
}
</style>
</head>
<body>
	<div class="col-main" style="float:right;">
		<div class="main-box user-information" style="border:1px solid #F2F2F2;border-top:solid 10px #e0e0e0;background: none;">
	        <div class="ft" style="height:98px;">
	        	<div class="num-info">
	        		<span class="s2">总资产：<em><fmt:formatNumber value="${user.balance+user.freez}" pattern="0.000#####"/></em></span>
	        		<span class="s2">可用：<em><fmt:formatNumber value="${user.balance}" pattern="0.000#####"/></em></span>
	        		<span class="s2">冻结：<em><fmt:formatNumber value="${user.freez}" pattern="0.000#####"/></em></span>
	        	</div>
	        	<div class="num-info">
	        		<span class="s2">借入：<em><fmt:formatNumber value="${user.iningBtc}" pattern="0.000#####"/></em></span>
	        		<span class="s2">借出：<em><fmt:formatNumber value="${user.outingBtc}" pattern="0.000#####"/></em></span>
	        		<span class="s2">利息：<em><fmt:formatNumber value="${totalLixi }" pattern="0.000#####"/></em></span>
	        	</div>
	        	<div class="num-info">
	        		<span class="s2">用户充值：<em><fmt:formatNumber value="${in}" pattern="0.000#####"/></em></span>
	        		<span class="s2">用户提现：<em><fmt:formatNumber value="${out}" pattern="0.000#####"/></em></span>
	        		<span class="s2">&nbsp;</span>
	        	</div>
	        	<div class="num-info">
	        		<span class="s2">时间从：<input id="startDate" class="inputW2 Wdate" type="text" style="width:140px;" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : 'cn'})" name="startDate"></span>
	        		<span class="s2">到：<input id="endDate" class="inputW2 Wdate" type="text" style="width:140px;" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : 'cn'})" name="endDate"></span>
	        		<span class="s2"><a href="javascript:getInfo();">查询</a></span>
	        	</div>
	        	<div class="num-info">
	        		<span class="s2">借入：<em id="in"><fmt:formatNumber value="${user.iningBtc}" pattern="0.000#####"/></em></span>
	        		<span class="s2">借出：<em id="out"><fmt:formatNumber value="${user.outingBtc}" pattern="0.000#####"/></em></span>
	        		<span class="s2">总利息：<em id="totalLixi"><fmt:formatNumber value="${totalLixi }" pattern="0.000#####"/></em></span>
	        	</div>
	        </div>
        </div>
	</div>
</body>
</html>