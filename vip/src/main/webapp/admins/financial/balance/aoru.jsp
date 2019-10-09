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

.op {
float:left;
line-height:32px;
width:100px;
padding-right: 10px;
text-align: right;
}

.op{float:left;}

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
.op{ float:none; overflow:hidden;zoom:1;}
.op font{font-family: inherit;font-weight: bold;font-size: 16px;}
span.txt{float:left;margin-right: 5px;}
</style>

<script type="text/javascript">
$(function(){
	$("#bankBox").Ui();
});

/*结算确认*/
function ok() {
	var maxDetailsId = $("#maxDetailsId").val();
	var fundType = $("#fundType").val();
	var accType = $("#accType").val();
	var perTotalAmount = $("#perTotalAmount").val();
	var balanceFlag = $("#balanceFlag").val();
	var actionUrl = "/admin/financial/balance/doAoru?maxDetailsId="+maxDetailsId+"&fundType="+fundType+"&accType="+accType+"&perTotalAmount="+perTotalAmount+"&balanceFlag="+balanceFlag;
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

/*查看记录明细*/
function rechargeDetails() {
	var maxDetailsId = $("#maxDetailsId").val();
	var fundType = $("#fundType").val();
	var accType = $("#accType").val();
	/*充值明细*/
	var url = "/admin/financial/balance/giveRechargeDetails?maxDetailsId="+maxDetailsId+"&fundType="+fundType;
	var title = "充值记录";
	if(3 == accType) {
		url = "/admin/financial/balance/giveDownload?maxDetailsId="+maxDetailsId+"&fundType="+fundType;
		title = "提现记录";
	}
	Iframe({
		Url : url,
		zoomSpeedIn : 200,
		zoomSpeedOut : 200,
		Width : 1050,
		Height : 800,
		scrolling : 'yes',
		isIframeAutoHeight : false,
        isShowIframeTitle: true,
		Title : title
	});
	
	
}


</script>
</head>

<body >
<div class="bankbox" id="bankBox">

<div class="bankbox_bd">
	<div class="form-line">
	    <span class="op">结算账户：</span>
	    <span class="op">
	    	${account.name},资金编号：${fundType }
	  	</span>
	</div>
	
	<div class="form-line">
		<c:if test="${accType != 3 }">
	    <span class="op">当前余额：</span>
	    <span class="op">
			<font color="blue">
				<fmt:formatNumber value="${account.funds }" pattern="0.00000####"/> 
			</font>
	  	</span>
	  	</c:if>
	  	
	  	<c:if test="${accType == 3 }">
	    <span class="op">当前钱包余额：</span>
	    <span class="op">
			<font color="blue">
				<fmt:formatNumber value="${account.funds }" pattern="0.00000####"/> 
			</font>
	  	</span>
	  	<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	  			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	  	</span>
	  	<span class="op">当前累积金额：</span>
	  	<span class="op">
			<font color="blue">
				<fmt:formatNumber value="${account.curTotalAmount }" pattern="0.00000####"/> 
			</font>
	  	</span>
	  	</c:if>
	  	
	</div>
	
	<div class="form-line">
		<c:if test="${accType != 3 }">
	    <span class="op">充值金额：</span>
	    <span class="op">
			<font color="blue">
				<fmt:formatNumber value="${detailsBean.amount }" pattern="0.00000####"/> 
			</font>
	  	</span>
	  	</c:if>
	  	
	  	<c:if test="${accType == 3 }">
	    <span class="op">提现成功金额：</span>
	    <span class="op">
			<font color="blue">
				<fmt:formatNumber value="${detailsBean.amount }" pattern="0.00000####"/> 
			</font>
	  	</span>
	  	<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	  			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	  	</span>
	  	
	  	<!-- 
	  	<span class="op">实际提现金额：</span>
	  	<span class="op">
			<font color="blue">
				<fmt:formatNumber value="${detailsBean.fees }" pattern="0.00000####"/> 
			</font>
	  	</span>
	  	 -->
	  	</c:if>
	</div>
	
	<div class="form-line">
		<c:if test="${accType != 3 }">
	    <span class="op">上次金额：</span>
	    <span class="op">
			<font color="blue">
				<fmt:formatNumber value="${finanBalance.amount }" pattern="0.00000####"/> 
			</font>
	  	</span>
	  	</c:if>
	  	
	  	<c:if test="${accType == 3 }">
	    <span class="op">上次钱包金额：</span>
	    <span class="op">
			<font color="blue">
				<fmt:formatNumber value="${finanBalance.amount }" pattern="0.00000####"/> 
			</font>
	  	</span>
	  	<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	  			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	  	</span>
	  	<span class="op">上次累积金额：</span>
	  	<span class="op">
			<font color="blue">
				<fmt:formatNumber value="${finanBalance.perTotalAmount }" pattern="0.00000####"/> 
			</font>
	  	</span>
	  	</c:if>
	</div>
	
	<div class="form-line">
	    <span class="op">结算提醒：</span>
	    <span class="op">
	    	<c:if test="${balanceFlag > 0 }">
				<font color="blue">
					无问题
				</font>
			</c:if>
			<c:if test="${balanceFlag == 0 }">
				<font color="red">
					有问题
				</font>
			</c:if>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<a href="javascript:rechargeDetails();">查看详情 </a>
	  	</span>
	</div>
	<div class="form-line">
		<c:if test="${accType == 1 }">
			期初余额（上次金额）+ 发生额（充值金额）= 期末余额（当前余额）。      充值金额不包含手工录入。如有财务手工录入需要在备注中说明。
		</c:if>		
		<c:if test="${accType == 3 }">
			期初余额（上次累积金额）+ 发生额（提现成功金额）= 期末余额（当前累积金额）。
		</c:if>														
	</div>
	<div class="form-line">
	    <span class="op">备注：</span>
	    <span class="op">
	    	<textarea rows="3" cols="70" id="memo" errormsg="请正确填写备注信息" mytitle="请正确填写备注信息" name="memo" pattern="limit(0,500)">
本次结算最后一笔充值记录编号：${detailsBean.detailsId }。
<c:if test="${balanceFlag > 0 }">本次结算账务情况：${finanBalance.amount }+${detailsBean.amount }=${account.amount }</c:if><c:if test="${balanceFlag == 0 }">本次结算账务情况：${finanBalance.amount }+${detailsBean.amount }≠${account.amount }</c:if>
</textarea>
	  	</span>
	</div>
	
   <div class="form-btn">
		<input type="hidden" name="accountId" value="${account.id }"/>
		<input type="hidden" name="dayTag" value="${dayTag }"/>
		<input type="hidden" name="perAmount" value="${finanBalance.amount }"/>
		<input type="hidden" id="perTotalAmount" name="perTotalAmount" value="${account.curTotalAmount }"/>
		<input type="hidden" id="maxDetailsId" name="maxDetailsId" value="${detailsBean.detailsId }"/>
		<input type="hidden" id="fundType" name="fundType" value="${fundType }"/>
		<input type="hidden" id="accType" name="accType" value="${accType }"/>
		<input type="hidden" id="balanceFlag" name="balanceFlag" value="${balanceFlag }"/>
		</br></br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <a class="btn" href="javascript:ok();"><span class="cont">确定</span></a>
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <a href="javascript:parent.Close();" class="btn btn-gray"><span class="cont">取消</span></a>
   </div>	
</div>
</div>

</body>
</html>
