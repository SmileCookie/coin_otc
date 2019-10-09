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
</style>

<script type="text/javascript">
$(function(){
	$("#bankBox").Ui();
	
	$("#accountId").change(function(){
		var ltcs = $(this).find("option:selected").attr("ltc");
		var money = $("#money").val();
		if(parseFloat(ltcs) < parseFloat(money)){
			Wrong("打币钱包余额已不足，请及时充值。");
			$("#accountId").val("");
			$("#accountId option:first").attr("selected", true);
			$("#accountId").UiSelect();
			return;
		}
// 		getBtcWallet($(this).val());
	});
	
	$("#accountId").trigger("change");
});

function ok(mCode){
	
	var actionUrl = "/admin/btc/download/homeconfirm/confirm"+"?coint=${coint.propTag }";
	vip.ajax({
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
		<div class="form-tit">转入地址：</div>
		<div class="form-con">
			<a style="color: #006699;" href="https://blockchain.info/zh-cn/address/${curData.toAddress }" target="_blank">${curData.toAddress }</a>
			<font style="color:red;">请核实地址后提现给用户</font>
		</div>
	</div>
	
	<div class="form-line">
		<div class="form-tit">转出数量：</div>
		<div class="form-con">
			<fmt:formatNumber value="${curData.afterAmount }" pattern="0.0000####"/>
			<input type="hidden" id="money" value="${curData.afterAmount}"/>
		</div>
	</div>
	<div style="color: #ff0000;margin-left: 59px;height: 26px;" id="wallet"></div>
	<div class="form-line">
    	<div class="form-tit">账户类型：</div>
    	<div class="form-con">
    		<select name="accountId" id="accountId" pattern="true" style="width:280px;"  errormsg="请选择打款账户">
           		<option value="">--请选择打款账户用于此次提现--</option>
           		<c:forEach var="account" items="${accounts}"><%--网站比特币 --%>
	             	<option value="${account.id}" ltc="${account.funds}" <c:if test="${account.isDefault}">selected="selected"</c:if>>${account.name} | 余额：<fmt:formatNumber value="${account.funds }" pattern="0.0000####" /></option>
           		</c:forEach>
            </select>
  		</div>
	</div>

	<div class="form-line">
		<div class="form-tit" style="float:left;">谷歌验证码：</div>
		<div class="form-con">
			<input type="text" class="input" name="mCode" id="mCode" value="" pattern="limit(4,10)"/>
		</div>
	</div>	

	<div class="form-btn" id="FormButton">
	   <input type="hidden" id="did" name="did" value="${curData.id }"/>
       <a class="btn" href="javascript:ok();" id="setOk"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a>
       <a class="btn" href="javascript:parent.Close();" id="setOk"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>
    </div>
</div>
</div>
<script type="text/javascript">
/**
 * 获取莱特币钱包余额
 */
function getBtcWallet(accountId) {
	var actionUrl = "/admin/btc/download/getWalletBalance?accountId="+accountId+"&coint=${coint.propTag }";
	vip.ajax({
		url : actionUrl,
		dataType : "json",
		suc : function(json) {
			if(json.des == 'notexsit'){
				Wrong("没有打币钱包");
				$("#wallet").html("");
				return;
			}
			var wallets = json.datas;
			var divStr = '已同步到的钱包的余额：'+wallets.wallet;
			if(wallets.hasSync != null){
				var sync = wallets.hasSync;
				divStr += "，还有"+sync.count+"笔手续费没有录入，总共："+sync.fees;
			}
			if(wallets.noSync != null){
				var sync = wallets.noSync;
				divStr += "，"+sync.count+"笔打币成功的记录没有同步，总额："+sync.fees+"。";
			}
			
			$("#wallet").html(divStr);
		}
	});
}
</script>
</body>
</html>
