<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<jsp:include page="/admins/top.jsp" />
<style type="text/css">

.form-con{float:left;}

.form-line {margin-left:20px;}
.col-main .prompt.b_yellow.remind2.pl_35 {
	padding:0 10px 20px 10px;
}
.form-btn {
    padding: 15px 0 0 100px;
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
.formlist .formtxt {
float:left;
line-height:24px;
text-align:right;
width:130px;
}
.formlist .formcon {
	overflow:hidden;
	font-weight: bold;
	font-size: 12px;
}
.formlist span.tips {
	color:#666666;
	float:left;
	line-height:24px;
	white-space:nowrap;
	font-size: 12px;
	font-weight: normal;
}
.formbtn {
	height:44px;
	clear: both;
	padding: 15px 0;
}

.formbtn a:hover {
	
	background-position:0 -33px;
	
}
select{ display:inline;}
.form-btn{ padding-left:96px;}

.form-line {
    line-height: 30px;
  
    }
.form-line{ overflow:hidden; clear:none; float:none;}
.form-con{ float:none; overflow:hidden;zoom:1;}
span.txt{float:left;margin-right: 5px;}
.form-con font{font-family: inherit;font-weight: bold;font-size: 16px;}
</style>

<script type="text/javascript">
$(function(){
	$("#bankBox").Ui();
	var connId = $("#connId").val();
	if(parseInt(connId) > 0){
		var obj = parent.$("#row"+connId);
		$("#userName").val(obj.attr("uname"));
		$("#funds").val(obj.attr("money"));
		$("#fundsComm").val(obj.attr("fee"));
	}
	changeMoney();
});

function ok(mCode) {
	if(!couldPass){
		googleCode("ok", true);
		return;
	}
	couldPass = false;
	var actionUrl = "/admin/btc/download/confirmSuc?mCode="+mCode+"&coint=${coint.propTag }";
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

function changeMoney(){
	$("#accountId").change(function(){
		var fundType = $(this).find("option:selected").attr("fundtype");
		
		if($.cookie(vip.cookiKeys.rid)==1||$.cookie(vip.cookiKeys.rid)==6){
			var money = $(this).find("option:selected").attr("money");
			if(money){
				$("#balanceM").text("余额："+parseFloat(money));
			}else{
			}
		}
	});
	$("#accountId").trigger("change");
}
</script>
</head>

<body >
<div class="bankbox" id="bankBox">

<div class="bankbox_bd">

	<div class="form-line">
    	<div class="form-tit">账户类型：</div>
    	<div class="form-con">
    		<select name="accountId" id="accountId" pattern="true" style="width:200px;"  errormsg="请选择账户类型"> 
           		<option value="">--请选择--</option>
           		<c:forEach var="account" items="${accounts}"><%--网站比特币 --%>
	             	<option value="${account.id}" <c:if test="${logAdmin.rid==1||logAdmin.rid==6}">money="${account.funds}"</c:if> <c:if test="${logAdmin.rid==1&&account.id==20 || account.adminId==cookie.AdminID.value&&account.isDefault}">selected="selected"</c:if>>${account.name}</option>
           		</c:forEach>
            </select>&nbsp;&nbsp;&nbsp;&nbsp;
           	<font color="red" id="balanceM"></font>
  		</div>
	</div>
	
	<div class="form-line">
	    <div class="form-tit">用户名：</div>
	    <div class="form-con">
	    	<input id="userName" errormsg="请输入用户名。" mytitle="请输入用户名，可以为空。" name="userName" pattern="limit(0,40)" size="15" class="input" type="text" value="${userName }"/>
	  	</div>
	</div>	
	
 	<div class="form-line">
	    <div class="form-tit">金额：</div>
	    <div class="form-con">
	    	<input id="funds" errormsg="请正确填写资金的数额。" mytitle="请正确填写资金的数额。" name="funds" pattern="limit(1,40);num()" size="15" class="input" type="text" value=""/>
	  	</div>
	</div>

 	<div class="form-line">
	    <div class="form-tit">手续费：</div>
	    <div class="form-con">
	    	<input id="fundsComm" errormsg="请正确填写该账户产生的手续费。" mytitle="请正确填写该账户产生的手续费。" name="fundsComm" pattern="limit(1,40);num()" size="15" class="input" type="text" value="0"/>
	    	<p style="margin: 0;line-height: 20px;color: #ff0000;">正值代表网站收入，负值代表网站支出。</p>
	  	</div>
	</div>

	<div class="form-line">
	    <div class="form-tit">描述：</div>
	    <div class="form-con">
	    	<textarea rows="3" cols="50" id="memo" errormsg="请正确填写备注信息" mytitle="请正确填写备注信息" name="memo" pattern="limit(0,500)">${entry.memo}</textarea>
	  	</div>
	</div>

	<div class="form-btn" id="FormButton">
       <input type="hidden" id="connId" name="connId" value="${connId }"/>
       <input type="hidden" id="useTypeId" name="useTypeId" value="${useTypeId }"/><%--用户提现比特币 --%>
       <input type="hidden" id="fundType" name="fundType" value="${fundType }"/><%--比特币 --%>
       <a class="btn" href="javascript:ok();"><span class="cont">确定</span></a>
	   <a href="javascript:parent.Close();" class="btn btn-gray"><span class="cont">取消</span></a>
    </div>
</div>
</div>

</body>
</html>
