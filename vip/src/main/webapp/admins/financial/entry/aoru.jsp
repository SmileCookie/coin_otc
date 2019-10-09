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
.form-con font{font-family: inherit;font-size: 12px;}
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
	changeFundType();
});

function ok() {
	var actionUrl = "/admin/financial/entry/doAoru";
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

function turnRound(){
	$("#useTypeId").change(function(){
		if($(this).find("option:selected").attr("turn")==1){
			$("#turnRound").show();
		}else{
			$("#turnRound").hide();
		}
	});
}

function changeFundType(){
	$("#fundType").change(function(){
		var fundType = $(this).find("option:selected").val();
		getAccount(fundType);
// 		getUseType(fundType);
	});
	turnRound();
}
</script>
</head>

<body >
<div class="bankbox" id="bankBox">

<div class="bankbox_bd">

    <div class="form-line">
      <div class="form-tit">资金类型：</div>
      <div class="form-con">
         <select name="fundType" id="fundType" pattern="true" style="width:140px;" errormsg="请选择资金类型"> 
               <option value="">--请选择--</option>
               <c:forEach var="ft" items="${ft }">
		           <option id="fundT_${ft.value.fundsType }" value="${ft.value.fundsType }" <c:if test="${ft.value.fundsType==entry.fundType }">selected="selected"</c:if>>${ft.value.propCnName }</option>
	           </c:forEach>
            </select>
      </div>
    </div>

	<div class="form-line">
    	<div class="form-tit">财务账户：</div>
    	<div class="form-con" id="finanAccDiv">
    		<select name="accountId" id="accountId" pattern="true" style="width:200px;"  errormsg="请选择账户类型"> 
           		<option value="">--请选择--</option>
            </select>&nbsp;&nbsp;&nbsp;&nbsp;
           	<font color="red" id="balanceM"></font>
  		</div>
	</div>
	
	<div class="form-line">
	    <div class="form-tit">用户名：</div>
	    <div class="form-con">
	    	<input id="userName" errormsg="请输入用户名。" mytitle="请输入用户名，可以为空。" name="userName" pattern="limit(0,40)" size="15" class="input" type="text" value="${entry.userName }"/>
	  	</div>
	</div>	
	
	<div class="form-line">
    	<div class="form-tit">资金用途：</div>
    	<div class="form-con" id="useTypeDiv">
    		<select name="useTypeId" id="useTypeId" pattern="true" style="width:180px;"  errormsg="请选择资金用途"> 
           		<option value="">--请选择--</option>
             	<c:forEach var="usetype" items="${useTypes}">
	             	<option turn="${usetype.turnRound}" value="${usetype.id}" <c:if test="${usetype.id==useTypeId }">selected="selected"</c:if>>${usetype.name}</option>
           		</c:forEach>
            </select>
  		</div>
	</div>
	
	<div class="form-line" style="display: none;" id="turnRound">
    	<div class="form-tit">流转账户：</div>
    	<div class="form-con" id="toFinanAccDiv">
    		<select name="toAccountId" id="toAccountId" style="width:200px;"  errormsg="请选择流转到的账户"> 
           		<option value="">--请选择流转到的账户--</option>
            </select>&nbsp;&nbsp;&nbsp;&nbsp;
           	 余额：<font color="red" id="balanceMT">0.0</font>
  		</div>
	</div>

 	<div class="form-line">
	    <div class="form-tit">金额：</div>
	    <div class="form-con">
	    	<input id="funds" errormsg="请正确填写资金的数额。" mytitle="请正确填写资金的数额。" name="funds" pattern="limit(1,40);num()" size="15" class="input" type="text" value="${entry.funds }"/>
	  	</div>
	</div>

 	<div class="form-line">
	    <div class="form-tit">手续费：</div>
	    <div class="form-con">
	    	<input id="fundsComm" errormsg="请正确填写该账户产生的手续费。" mytitle="请正确填写该账户产生的手续费。" name="fundsComm" pattern="limit(1,40);num()" size="15" class="input" type="text" value="${entry.fundsComm }"/>
	    	<p style="margin: 0;line-height: 20px;color: #ff0000;">正值代表网站收入，负值代表网站支出。</p>
	  	</div>
	</div>

	<div class="form-line">
	    <div class="form-tit">描述：</div>
	    <div class="form-con">
	    	<textarea rows="3" cols="50" id="memo" errormsg="请正确填写备注信息" mytitle="请正确填写备注信息" name="memo" pattern="limit(0,500)">${entry.memo}</textarea>
	  	</div>
	</div>
	
	<div class="form-line">
    	<div  class="form-tit">谷歌验证码:</div>
	    <div class="form-con">
	    	<input type="password" class="input" style="width:100px;" name="mCode" id="mCode" value="" mytitle="请输入移动设备上生成的验证码。" errormsg="验证码错误" pattern="limit(4,10)"/>
	    </div>
    </div>
	
   <div class="form-btn">
      <input type="hidden" name="id" value="${entry.id }"/>
	  <input type="hidden" id="connId" name="connId" value="${connId }"/>
      <a class="btn" href="javascript:ok();"><span class="cont">确定</span></a>
      <a href="javascript:parent.Close();" class="btn btn-gray"><span class="cont">取消</span></a>
   </div>	
</div>
</div>
<script type="text/javascript">
/**
 * 根据资金类型获取账户
 */
function getAccount(fundType) {
	var actionUrl = "/admin/financial/entry/getAccount?fundType="+fundType;
	vip.ajax({
		url : actionUrl,
		dataType : "json",
		suc : function(json) {
			var array = json.datas;
			if(array.length == 0){
				Wrong("财务账户不存在。");
				return;
			}
			//账户一
			var htmlS = '<select name="accountId" id="accountId" pattern="true" style="width:200px;"  errormsg="请选择账户类型">';
				htmlS+= '<option value="">--请选择--</option>'; 
			//周转账户
         	var htmlS2 ='<select name="toAccountId" id="toAccountId" style="width:200px;"  errormsg="请选择流转到的账户">';
       			htmlS2+='<option value="">--请选择流转到的账户--</option>';
       		$.each(array, function(i, fa){
       			var money = "";
       			if(fa.money){
       				money = "money="+fa.money;
       			}
				htmlS+= '<option '+money+' value="'+fa.id+'">'+fa.name+'</option>';
				htmlS2+= '<option '+money+' value="'+fa.id+'">'+fa.name+'</option>';
			});
			
         	htmlS+= '</select>&nbsp;&nbsp;&nbsp;&nbsp;<font color="red" id="balanceM"></font>';
         	htmlS2+= '</select>&nbsp;&nbsp;&nbsp;&nbsp;<font color="red" id="balanceMT"></font>';
         	$("#finanAccDiv").html(htmlS);
         	$("#accountId").UiSelect();
         	$("#toFinanAccDiv").html(htmlS2);
         	$("#toAccountId").UiSelect();
         	
         	changeMoney();
         	changeToMoney();
		}
	});
}
function changeMoney(){
	$("#accountId").change(function(){
		var money = $(this).find("option:selected").attr("money");
		if(money){
			$("#balanceM").text("余额："+parseFloat(money));
		}else{
			$("#balanceM").text("");
		}
	});
}

function changeToMoney(){
	$("#toAccountId").change(function(){
		var money = $(this).find("option:selected").attr("money");
		if(money){
			$("#balanceMT").text("余额："+money);
		}else{
			$("#balanceMT").text("");
		}
	});
}

/**
 * 根据资金类型获取资金用途
 */
function getUseType(fundType) {
	var actionUrl = "/admin/financial/entry/getUseType?fundType="+fundType;
	vip.ajax({
		url : actionUrl,
		dataType : "json",
		suc : function(json) {
			var array = json.datas;
			if(array.length == 0){
				Wrong("资金用途不存在。");
				return;
			}
			//账户一
			var htmlS = '<select name="useTypeId" id="useTypeId" pattern="true" style="width:180px;"  errormsg="请选择资金用途"> ';
				htmlS+= '<option value="">--请选择--</option>'; 
       		$.each(array, function(i, obj){
				htmlS+= '<option turn="'+obj.turn+'" value="'+obj.id+'">'+obj.name+'</option>';
			});
			
         	htmlS+= '</select>';
         	$("#useTypeDiv").html(htmlS);
         	$("#useTypeId").UiSelect();
		}
	});
}
</script>
</body>
</html>
