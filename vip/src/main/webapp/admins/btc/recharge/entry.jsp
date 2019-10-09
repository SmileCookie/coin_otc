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

.form-tit {
    width: 120px;
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
	
	var actionUrl = "/admin/btc/recharge/doCharge?mCode="+mCode+"&coint=${coint.propTag }";
	if(parseInt($("#connId").val())>0){
		actionUrl = "/admin/btc/recharge/doaoru?mCode="+mCode+"&coint=${coint.propTag }";
	}
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

function changeFundType(){
	$("#fundType").change(function(){
		var fundType = $(this).find("option:selected").val();
		getAccount(fundType);
	});
}
</script>
</head>

<body >
<div class="bankbox" id="bankBox">

<div class="bankbox_bd">

	<div class="form-line">
	    <div class="form-tit">充值地址：</div>
	    <div class="form-con">
	    	<input id="btcKey" errormsg="请输入正确的地址。" mytitle="请输入需要补单的地址，可以为空。" name="btcKey" pattern="limit(0,60)" size="45" class="input" type="text" value="${btcTo}"/>
	  	</div>
	</div>	

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
	  	</div>
	</div>	
	
 	<div class="form-line">
	    <div class="form-tit">充值${coint.propTag }数量：</div>
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
<script type="text/javascript">
/**
 * 根据地址获取钱包账户
 */
function getBtcWallet(btcKey) {
	if(!btcKey || btcKey.length == 0){
		return;
	}
	var actionUrl = "/admin/btc/recharge/getWalletAccount?btcKey="+btcKey+"&coint=${coint.propTag }";
	vip.ajax({
		url : actionUrl,
		dataType : "json",
		suc : function(json) {
			if(json.des=="no"){
				return;
			}
			var wallet = json.datas;
			$("#accountId option").each(function(){
				if($(this).attr("wallet")==wallet.walletId){
					$(this).attr("selected","selected");
					$("#accountId").UiSelect();
				}
			});
		}
	});
}

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
       		$.each(array, function(i, fa){
				htmlS+= '<option value="'+fa.id+'">'+fa.name+'</option>';
			});
			
         	htmlS+= '</select>';
         	$("#finanAccDiv").html(htmlS);
         	$("#accountId").UiSelect();
         	
         	changeMoney();
         	changeToMoney();
		}
	});
}
</script>
</body>
</html>
