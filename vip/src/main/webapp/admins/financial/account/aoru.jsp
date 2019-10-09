<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<jsp:include page="/admins/top.jsp" />

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
	
// 	chargeAcc();
});

function ok() {
	var actionUrl = "/admin/financial/account/doAoru";
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

function chargeAcc(){
	var fundType = $("#fundType").val();
	var type = $("#type").val();
	
	if(type==1 || type == 3){
		var withdraw = 0;
		if(type==3){
			withdraw = 1;
		}
		if(fundType==1&&type!=3){
			getBankAccount();
		}else if(fundType==2){
			getBtcWallet(withdraw);
		}else if(fundType==5){
			getBtcWallet(withdraw, 'eth');
		}else if(fundType==6){
			getBtcWallet(withdraw, 'etc');
		}else{
			$("#chargeAcc").html("").hide();
		}
	}else{
		$("#chargeAcc").html("").hide();
	}
}
</script>
</head>

<body >
<div class="bankbox" id="bankBox">

<div class="bankbox_bd">
	<div class="form-line">
	    <div class="form-tit">账户名称：</div>
	    <div class="form-con">
	    	<input id="name" errormsg="请正确填写账户名称。" mytitle="请正确填写账户名称。" name="name" pattern="limit(2,40)" size="25" class="input" type="text" value="${fa.name }"/>
	  	</div>
	</div>
	<div class="form-line">
    	<div class="form-tit">资金类型：</div>
    	<div class="form-con">
    		<select name="fundType" id="fundType" pattern="true" style="width:140px;" onchange="chargeAcc()" errormsg="请选择资金类型"> 
           		<option value="">--请选择--</option>
             	<c:forEach var="ft" items="${ft }">
		           <option value="${ft.value.fundsType }" <c:if test="${ft.value.fundsType==fa.fundType }">selected="selected"</c:if>>${ft.value.propCnName }</option>
	            </c:forEach>
            </select>
  		</div>
	</div>
	<div class="form-line">
    	<div class="form-tit">账户类型：</div>
    	<div class="form-con">
    		<select name="type" id="type" pattern="true" style="width:140px;" errormsg="请选择账户类型"> 
           		<option value="">--请选择--</option>
             	<option value="1" <c:if test="${fa.type==1 }">selected="selected"</c:if>>充值账户</option>
             	<option value="2" <c:if test="${fa.type==2 }">selected="selected"</c:if>>储备账户</option>
             	<option value="3" <c:if test="${fa.type==3 }">selected="selected"</c:if>>提现账户</option>
             	<option value="4" <c:if test="${fa.type==4 }">selected="selected"</c:if>>日常开支</option>
             	<option value="5" <c:if test="${fa.type==5 }">selected="selected"</c:if>>其他</option>
            </select>
  		</div>
	</div>
	
	<input type="hidden" name="hiddenBankAccountId" id="hiddenBankAccountId" value="${fa.bankAccountId }"/>
	<div id="chargeAcc" style="display: none;">
	</div>
	
	<div class="form-line">
    	<div class="form-tit">管理员：</div>
    	<div class="form-con">
    		<select name="adminId" id="adminId" pattern="true" style="width:140px;"  errormsg="请选择管理员"> 
           		<option value="">--请选择--</option>
             	<c:forEach items="${admins}" var="admin">
	           		<option value="${admin.id }" <c:if test="${fa.adminId==admin.id}">selected="selected"</c:if>>${admin.admName }</option>
	           </c:forEach>
            </select>
  		</div>
	</div>

	<div class="form-line">
	    <div class="form-tit">余额：</div>
	    <div class="form-con">
	    	<input id="funds" errormsg="请正确填写资金的余额。" mytitle="请正确填写资金的余额。" name="funds" pattern="limit(1,40);num()" size="15" class="input" type="text" value="<fmt:formatNumber value='${fa.funds }' pattern='0.0000####'/>"/>
	  	</div>
	</div>
	
	<div class="form-line" style="display: none;">
	    <div class="form-tit">费率：</div>
	    <div class="form-con">
	    	<input id="rate" errormsg="请正确填写费率。" mytitle="请正确填写费率。" name="rate" pattern="limit(1,40);num()" size="15" class="input" type="text" value="0"/>
	  	</div>
	    <p style="margin: 0;line-height: 20px;color: #ff0000;">正值代表网站收入的费率，负值代表网站支出的费率。</p>
	</div>

	<div class="form-line">
	    <div class="form-tit">描述：</div>
	    <div class="form-con">
	    	<textarea rows="3" cols="50" id="memo" errormsg="请正确填写备注信息" mytitle="请正确填写备注信息" name="memo" pattern="limit(0,500)">${fa.memo}</textarea>
	  	</div>
	</div>
	<div class="form-line">
	    <div class="form-tit">图标：</div>
	    <div class="form-con">
	    	<input class="input" type="text" id="img" errormsg="银行的图标路径，可不写" size="35" mytitle="银行的图标路径，可不写" name="img" pattern="limit(0,50)" value="${fa.img}"/>
	  	</div>
	</div>

	<div class="form-btn">
		<input type="hidden" name="id" value="${fa.id }"/>
		<a class="btn" href="javascript:ok();"><span class="cont">确定</span></a>
		<a href="javascript:parent.Close();" class="btn btn-gray"><span class="cont">取消</span></a>
	</div>
</div>
</div>

<script type="text/javascript">
/**
 * 获取人民币充值账户
 */
function getBankAccount() {
	var actionUrl = "/admin/financial/account/getbankaccount";
	vip.ajax({
		url : actionUrl,
		dataType : "json",
		suc : function(json) {
			var banks = json.datas;
			var divStr = '<div class="form-line">';
    			divStr+= '<div class="form-tit">关联的充值账户：</div>'
    				   + '<div class="form-con">'
    					 + '<select name="bankAccountId" id="bankAccountId" style="width:350px;"  errormsg="请选择关联的充值账户">' 
           				  + '<option value="">--请选择关联的充值账户--</option>';
           			
					for(var i=0; i < banks.length; i ++){
						if($("#hiddenBankAccountId").val() == banks[i].id){
			           		divStr+='<option value="'+banks[i].id+'" selected="selected">'+banks[i].startBank.substring(0,8)+banks[i].account+'</option>';
						}else{
			           		divStr+='<option value="'+banks[i].id+'">'+banks[i].startBank.substring(0,8)+banks[i].account+'</option>';
						}
					}
					
				  divStr+= '</select>'
  						+'</div>'
       	 				+'<p style="float:left; margin: 0;line-height: 20px;color: #ff0000;">不是充值账户，不选</p>'
						+'</div>';

			$("#chargeAcc").html(divStr).show();
			$("#bankAccountId").UiSelect();
		}
	});
}
/**
 * 获取比特币钱包
 */
function getBtcWallet(withdraw,coin) {
	var actionUrl = "/admin/financial/account/getBtcWallet?withdraw="+withdraw+"&coint="+coin;
	vip.ajax({
		url : actionUrl,
		dataType : "json",
		suc : function(json) {
			var wallets = json.datas;
			var divStr = '<div class="form-line">';
    			divStr+= '<div class="form-tit">关联的钱包：</div>'
    				   + '<div class="form-con">'
    					 + '<select name="bankAccountId" id="bankAccountId" style="width:170px;"  errormsg="请选择关联的BTC钱包">' 
           				  + '<option value="">--请选择关联的钱包--</option>';
           			
					for(var i=0; i < wallets.length; i ++){
						if($("#hiddenBankAccountId").val() == wallets[i].walletId){
			           		divStr+='<option value="'+wallets[i].walletId+'" selected="selected">'+wallets[i].name+'</option>';
						}else{
			           		divStr+='<option value="'+wallets[i].walletId+'">'+wallets[i].name+'</option>';
						}
					}
					
				  divStr+= '</select>'
  						+'</div>'
  						+'<p style="float:left; margin: 0;line-height: 20px;color: #ff0000;">没有关联账户，不选</p>'
						+'</div>';
						
			$("#chargeAcc").html(divStr).show();
			$("#bankAccountId").UiSelect();
		}
	});
}
</script>
</body>
</html>
