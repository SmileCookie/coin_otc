<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <jsp:include page="/admins/top.jsp" />
<style type="text/css">


.form-con{float:left;}

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
.form-tit {
    width: 120px;
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

.form-con{ float:none; overflow:hidden;zoom:1;}
span.txt{float:left;margin-right: 5px;}
</style>

<script type="text/javascript">
$(function(){
	$("#bankBox").Ui();
	var connId = $("#connId").val();
	if(parseInt(connId) > 0){
		var obj = parent.$("#row"+connId);
		$("#userName").val(obj.attr("uname"));
		$("#funds").val(obj.attr("money"));
		//$("#fundsComm").val(obj.attr("fee"));
	}
});

function ok(mCode) {
	if(FormToStr("bankBox")==null){
		return;
	}
	if(!couldPass){
		googleCode("ok", true);
		return;
	}
	$("#funds").val(-Math.abs($("#money").val()));
	var actionUrl = "/admin/btc/recharge/doDeduction?mCode="+mCode+"&coint=${coint.propTag }";
	couldPass = false;
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

<div class="bankbox_bd">

	<div class="form-line">
    	<div class="form-tit">财务账户：</div>
    	<div class="form-con" id="finanAccDiv">
    		<select name="accountId" id="accountId" style="width:200px;"  errormsg="请选择账户类型"> 
           		<c:forEach var="account" items="${accounts}">
	             	<option value="${account.id}">${account.name}</option>
           		</c:forEach>
            </select>
  		</div>
	</div>
	
	<div class="form-line">
	    <div class="form-tit">用户名：</div>
	    <div class="form-con">
	    	<input id="userName" errormsg="请输入用户名。" mytitle="请输入用户名，可以为空。" name="userName" pattern="limit(0,40)" size="15" class="input" type="text" value="${userName }"/>
	  		<font color="red">可扣除：<fmt:formatNumber value="${payUser.balance }" pattern="0.000#####"/> ${coint.propTag }</font>
	  	</div>
	</div>	
	
 	<div class="form-line">
	    <div class="form-tit">扣除${coint.propTag }数量：</div>
	    <div class="form-con">
	    	<input id="money" errormsg="请正确填写资金的数额。" mytitle="请正确填写资金的数额。" name="money" pattern="limit(1,40);num()" size="15" class="input" type="text" value=""/>
	  		<input type="hidden" id="funds" name="funds" value=""/>
	  	</div>
	</div>

	<div class="form-line">
	    <div class="form-tit">描述：</div>
	    <div class="form-con">
	    	<textarea rows="3" cols="50" id="memo" errormsg="请正确填写备注信息" mytitle="请正确填写备注信息" name="memo" pattern="limit(0,500)">${entry.memo}</textarea>
	  	</div>
	</div>
	
	<div class="form-btn">
		<input type="hidden" id="connId" name="connId" value="${connId }"/>
		<input type="hidden" id="useTypeId" name="useTypeId" value="${useTypeId }"/><%--用户充值比特币 --%>
		<input type="hidden" id="fundType" name="fundType" value="${coint.fundsType }"/><%--比特币 --%>
		<input type="hidden" name="${coint.coinParam }" value="${coint.propTag }"  />
      <a class="btn" href="javascript:ok();"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a> 
      <a href="javascript:parent.Close()" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>
    </div>
	
</div>
</div>
</body>
</html>