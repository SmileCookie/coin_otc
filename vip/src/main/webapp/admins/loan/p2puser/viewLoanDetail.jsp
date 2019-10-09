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
.form-con font{font-family: inherit;font-weight: bold;font-size: 16px;}
</style>

<script type="text/javascript">
$(function(){
	$("#bankBox").Ui();
	changeStat("${payUserBean.withdrawRateStat}");
});

function changeStat(val) {
	if(val==1) {
		$("#div_withdrawRate").show();
	} else {
		$("#div_withdrawRate").hide();
	}
}

function ok(mCode) {
	var isSetFees = $("#isSetFees").val();
	var fees = $("#fees").val();
	if(isSetFees==1 && (fees==null || $.trim(isSetFees)=="" || !vip.tool.isFloat(fees))) {
		Wrong("请输入正确的费率！");
		return;
	}
	
	if(!couldPass){
		googleCode("ok", true);
		return;
	}
	couldPass = false;
	var actionUrl = "/admin/loan/p2puser/doModifyFees?mCode="+mCode;
	vip.ajax( {
		formId : "bankBox",
		url : actionUrl,
		div : "bankBox",
		suc : function(xml) {
			parent.Right($(xml).find("Des").text(), {
				callback : "reload2()"
			});
		}
	});
}
</script>
</head>

<body >
<div class="mains">
	<div class="col-main">
		<div class="tab-body">
		
			<table class="tb-list2" style="width:100%;table-layout: fixed;">
				<thead>
					<tr>
						<th>币种类型</th>
						<th>可用资产</th>
						<th>冻结资产</th>
						<th>借入资产</th>
						<th>贷出资产</th>
						<th>放贷额度</th>
						<th>拖欠金额</th>
						<th>日利息</th>
						<th>提现冻结中的资产</th>
					</tr>
				</thead>
				<c:forEach items="${payUserList}" var="list" varStatus="statu">
						<tbody class="item_list" id="line_${list.userId}">
							<tr>
								<td>
								 <c:if test="${list.coint!=null}">
								 	${list.coint.propCnName }
								 </c:if>
								 <c:if test="${list.coint ==null}">
								 	未知
								 </c:if>	
								</td>
								<td> 
									<fmt:formatNumber value="${list.balance.doubleValue()}" pattern="0.0000##"/>
								</td>
								<td> 
									<fmt:formatNumber value="${list.freez.doubleValue()}" pattern="0.0000##"/>
								</td>
								<td> 
									成功借入金额：<fmt:formatNumber value="${list.inSuccess.doubleValue()}" pattern="0.0#####"/><br/>
									等待借入金额：<fmt:formatNumber value="${list.inWait.doubleValue()}" pattern="0.0#####"/>
								</td>
								<td> 
									成功贷出金额：<fmt:formatNumber value="${list.outSuccess.doubleValue()}" pattern="0.0#####"/><br/>
									等待贷出金额：<fmt:formatNumber value="${list.outWait.doubleValue()}" pattern="0.0#####"/>
								</td>
								<td> 
									<fmt:formatNumber value="${list.loanLimit.doubleValue()}" pattern="0.0#####"/>
								</td>
								<td> 
									<fmt:formatNumber value="${list.overdraft.doubleValue()}" pattern="0.0#####"/>
								</td>
								<td> 
									<fmt:formatNumber value="${list.interestOfDay.doubleValue()}" pattern="0.0#####"/>
								</td>
								<td> 
									<fmt:formatNumber value="${list.withdrawFreeze.doubleValue()}" pattern="0.0#####"/>
								</td>
							</tr>
						</tbody>
					</c:forEach>
			</table>
		</div>
	</div>
</div>
</body>
</html>
