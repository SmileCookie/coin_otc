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
</style>

<script type="text/javascript">
$(function(){
	$("#bankBox").Ui();
});

function ok() {
	var actionUrl = "/admin/financial/usetype/doAoru";
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
	    <div class="form-tit">收支用途：</div>
	    <div class="form-con">
	    	<input id="name" errormsg="请正确填写资金的用途。" mytitle="请正确填写资金的用途。" name="name" pattern="limit(2,40)" size="25" class="input" type="text" value="${useType.name }"/>
	  	</div>
	</div>

	<div class="form-line">
	    <div class="form-tit">描述：</div>
	    <div class="form-con">
	    	<textarea rows="3" cols="50" id="memo" errormsg="请正确填写备注信息" mytitle="请正确填写备注信息" name="memo" pattern="limit(0,500)">${useType.memo}</textarea>
	  	</div>
	</div>
	
	<div class="form-line">
    	<div class="form-tit">收支类型：</div>
    	<div class="form-con">
    		<select name="inOut" id="inOut" pattern="true" style="width:140px;"  errormsg="请选择收支类型"> 
           		<option value="">--类型--</option>
             	<option value="1" <c:if test="${useType.isIn==1 }">selected="selected"</c:if>>收入</option>
             	<option value="2" <c:if test="${useType.isIn==2 }">selected="selected"</c:if>>支出</option>
            </select>
  		</div>
	</div>
	
<%-- 	<div class="form-line" style="display: none;">
    	<div class="form-tit">资金类型：</div>
    	<div class="form-con">
    		<select name="fundType" id="fundType" style="width:140px;"> 
           	   <option value="">--请选择--</option>
               <c:forEach var="ft" items="${fundTypes }">
                  <option value="${ft.value.fundsType }" <c:if test="${useType.fundType==ft.value.fundsType }">selected="selected"</c:if>>${ft.value.propCnName }</option>
               </c:forEach>
            </select>
  		</div>
	</div>
 --%>
	<div class="form-line">
    	<div class="form-tit">支出类型：</div>
    	<div class="form-con">
    		<select name="type" id="type" style="width:140px;"> 
           	   <option value="">--请选择--</option>
               <option value="1" <c:if test="${useType.type==1 }">selected="selected"</c:if>>充值</option>
               <option value="2" <c:if test="${useType.type==2 }">selected="selected"</c:if>>提现</option>
               <option value="3" <c:if test="${useType.type==3 }">selected="selected"</c:if>>其他</option>
            </select>
  		</div>
	</div>

	<div class="form-line">
    	<div class="form-tit">流转账户：</div>
    	<div class="form-con">
    		<select name="turnRound" id="turnRound" pattern="true" style="width:140px;"  errormsg="请选择是否流转账户"> 
           		<option value="">--请选择是否流转--</option>
             	<option value="0" <c:if test="${useType.turnRound==0 }">selected="selected"</c:if>>不流转</option>
             	<option value="1" <c:if test="${useType.turnRound==1 }">selected="selected"</c:if>>流转账户</option>
            </select>
  		</div>
	</div>
		
   <div class="form-btn">
		<input type="hidden" name="id" value="${useType.id }"/>
      <a class="btn" href="javascript:ok();"><span class="cont">确定</span></a>
      <a href="javascript:parent.Close();" class="btn btn-gray"><span class="cont">取消</span></a>
   </div>   	
</div>
</div>

</body>
</html>
