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

.form-tit {
float:left;
line-height:32px;
width:100px;
padding-right: 10px;
text-align: right;
}

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
span.txt{float:left;margin-right: 5px;}
.form-con font{font-family: inherit;font-weight: bold;font-size: 16px;}
</style>

<script type="text/javascript">
$(function(){
	$("#bankBox").Ui();
});

function ok() {
	var actionUrl = "/admin/financial/errcord/doDeal";
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
    	<div class="form-tit">账户类型：</div>
    	<div class="form-con">
    		${account.name} 充值金额 <font color="red"><fmt:formatNumber value="${error.money}" pattern="0.0000"/></font>
  		</div>
	</div>

	<div class="form-line">
	    <div class="form-tit">相关编号：</div>
	    <div class="form-con">
	    	<input id="connId" errormsg="请输入异常信息的相关编号。" mytitle="请输入异常信息的相关编号。" name="connId" pattern="limit(1,40)" size="15" class="input" type="text" value=""/>
	  	</div>
	</div>	

	<div class="form-line">
	    <div class="form-tit">描述：</div>
	    <div class="form-con">
	    	<textarea style="width: 295px;height: 105px;" id="memo" errormsg="请正确填写备注信息" mytitle="请正确填写备注信息" name="memo" pattern="limit(0,500)">${entry.memo}</textarea>
	  	</div>
	</div>
	
   <div class="form-btn">
		<input type="hidden" name="id" value="${error.id }"/>
      <a class="btn" href="javascript:ok();"><span class="cont">确定</span></a>
      <a href="javascript:parent.Close();" class="btn btn-gray"><span class="cont">取消</span></a>
   </div> 	
</div>
</div>

</body>
</html>
