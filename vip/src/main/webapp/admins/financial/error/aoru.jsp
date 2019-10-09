<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 

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
span.txt{float:left;margin-right: 5px;}
.form-con font{font-family: inherit;font-weight: bold;font-size: 16px;}
</style>

<script type="text/javascript">
$(function(){
	$("#bankBox").Ui();
});

function ok() {
	var actionUrl = "/admin/financial/errcord/doAoru";
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
    		<select name="accountId" id="accountId" pattern="true" style="width:200px;"  errormsg="请选择账户类型"> 
           		<option value="">--请选择--</option>
           		<c:forEach var="account" items="${accounts}">
<%--           			<c:if test="${logAdmin.rid==1}">--%>
	             		<option fundtype="${account.fundType}" value="${account.id}">${account.name}</option>
<%--           			</c:if>--%>
           		</c:forEach>
            </select>&nbsp;&nbsp;&nbsp;&nbsp;
           	<font color="red" id="balanceM"></font>
  		</div>
	</div>
	
	<div class="form-line" style="display: none;">
    	<div class="form-tit">资金类型：</div>
    	<div class="form-con">
    		<select name="fundType" id="fundType" pattern="true" style="width:140px;"  errormsg="请选择资金类型"> 
           		<option value="">--请选择--</option>
             	<option id="fundT_1" value="1" selected="selected">人民币</option>
             	<option id="fundT_2" value="2">比特币</option>
             	<option id="fundT_3" value="3">莱特币</option>
            </select>
  		</div>
	</div>
	
	<div class="form-line">
    	<div class="form-tit">资金用途：</div>
    	<div class="form-con">
    		<select name="useTypeId" id="useTypeId" pattern="true" style="width:180px;"  errormsg="请选择资金用途"> 
           		<option value="">--请选择--</option>
             	<c:forEach var="usetype" items="${useTypes}">
	             	<option value="${usetype.id}" <c:if test="${usetype.id==5 }">selected="selected"</c:if>>${usetype.name}</option>
           		</c:forEach>
            </select>
  		</div>
	</div>
	
 	<div class="form-line">
	    <div class="form-tit">金额：</div>
	    <div class="form-con">
	    	<input id="funds" errormsg="请正确填写资金的数额。" mytitle="请正确填写资金的数额。" name="funds" pattern="limit(1,40);num()" size="15" class="input" type="text" value="${entry.money }"/>
	  	</div>
	</div>

 	<div class="form-line" style="display: none;">
	    <div class="form-tit">手续费：</div>
	    <div class="form-con">
	    	<input id="fundsComm" errormsg="请正确填写该账户产生的手续费。" mytitle="请正确填写该账户产生的手续费。" name="fundsComm" pattern="limit(1,40);num()" size="15" class="input" type="text" value="0"/>
	    	<p style="margin: 0;line-height: 20px;color: #ff0000;">正值代表网站收入，负值代表网站支出。</p>
	  	</div>
	</div>

	<div class="form-line">
	    <div class="form-tit">描述：</div>
	    <div class="form-con">
	    	<textarea style="width: 295px;height: 105px;" rows="3" cols="50" id="memo" errormsg="请正确填写备注信息" mytitle="请正确填写备注信息" name="memo" pattern="limit(0,500)">${entry.memo}</textarea>
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
