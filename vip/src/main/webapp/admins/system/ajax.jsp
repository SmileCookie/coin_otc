<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<script type="text/javascript">
$(function(){
   if($("#sysshow").val()){
      $("#sysuser").show();
   }else{
      $("#sysuser").hide();
   }
});
</script>
<c:if test="${funds==null }">
请输入谷歌验证码搜索。
</c:if>
<c:if test="${funds!=null }">
<table class="tb-list2" style="width: 50%">
	<thead>
		<tr class="hd">
			<td colspan="3">
				<span>
					系统时间：<fmt:formatDate value="${funds.now}" pattern="yyyy-MM-dd HH:mm:ss" />
				</span>
			</td>
		</tr>
		<tr>
			<th>
				类型
			</th>
			<th>
				系统统计数据
			</th>
			<th>
				财务账户总额 	
			</th>
		</tr>
		<tr class="rmb">
			<td>
				用户RMB总余额
			</td>
			<td>
				<fmt:formatNumber value="${funds.rmbBalance }" pattern="0.00##"/> 	
			</td>
			<td>
				<fmt:formatNumber value="${rmbObj.totalBalance }" pattern="0.00##"/> 	
			</td>
		</tr>
		<tr class="btc">
			<td>
				用户BTC总余额
			</td>
			<td>
				<fmt:formatNumber value="${funds.btcBalance }" pattern="0.000#####"/>
			</td>
			<td>
				<fmt:formatNumber value="${btcObj.totalBalance }" pattern="0.000#####"/>
			</td>
		</tr>
		<tr class="ltc">
			<td>
				用户LTC总余额
			</td>
			<td>
				<fmt:formatNumber value="${funds.ltcBalance }" pattern="0.000#####"/> 	
			</td>
			<td>
				<fmt:formatNumber value="${ltcObj.totalBalance }" pattern="0.000#####"/> 	
			</td>
		</tr>
		<tr class="btq">
			<td>
				用户BTQ总余额
			</td>
			<td>
				<fmt:formatNumber value="${funds.btqBalance }" pattern="0.00"/> 	
			</td>
			<td>
				-
			</td>
		</tr>
	</thead>
</table>
<div style="height: 30px;"></div>
<table class="tb-list2" style="width: 80%">
		<tr class="hd">
			<td colspan="5">
			<form autocomplete="off" name="searchForm" id="searchContaint">
				<span>
					用户充值提现搜索
					<span class="spacing" style="margin-left: 30px;">从</span><span class="formcon mr_5">
					<input type="text" class="Wdate" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd'})" value="${fn:substring(startTime,0,11)}" name="startDate" id="startDate" size="15"/></span>
					<span class="spacing">到</span> 
					<input type="text" class="Wdate" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd'})" value="${fn:substring(endTime,0,11)}" name="endDate" id="endDate" size="15" />
				</span>
				<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">搜索</a> 
				(总额不变化)
			</form>
			</td>
		</tr>
		<tr>
			<th>
				类型
			</th>
			<th>
				已发起
			</th>
			<th>
				已成功
			</th>
			<th>
				-
			</th>
			<th>
				总额
			</th>
		</tr>
		<tr class="rmb">
			<td>
				${startTime!=null?"搜索":"今日" }充值RMB
			</td>
			<td>
				<fmt:formatNumber value="${funds.rmbRechargeSearch }" pattern="0.00##"/>
			</td>
			<td>
				-
			</td>
			<td>
				用户充值RMB总额
			</td>
			<td>
				<fmt:formatNumber value="${funds.rmbRecharge }" pattern="0.00##"/>	
			</td>
		</tr>
		<tr class="rmb">
			<td>
				${startTime!=null?"搜索":"今日" }提现RMB
			</td>
			<td>
				<fmt:formatNumber value="${funds.rmbWithdrawSearch }" pattern="0.00##"/>
			</td>
			<td>
				<fmt:formatNumber value="${funds.rmbWithdrawSearchSuc }" pattern="0.00##"/>
			</td>
			<td>
				用户提现RMB总额
			</td>
			<td>
				<fmt:formatNumber value="${funds.rmbWithdraw }" pattern="0.00##"/>
			</td>
		</tr>
		<tr class="btc">
			<td>
				${startTime!=null?"搜索":"今日" }充值BTC
			</td>
			<td>
				<fmt:formatNumber value="${funds.btcRechargeSearch }" pattern="0.000#####"/>
			</td>
			<td>
				-
			</td>
			<td>
				用户充值BTC总额
			</td>
			<td>
				<fmt:formatNumber value="${funds.btcRecharge }" pattern="0.000#####"/>
			</td>
		</tr>
		<tr class="btc">
			<td>
				${startTime!=null?"搜索":"今日" }提现BTC
			</td>
			<td>
				<fmt:formatNumber value="${funds.btcWithdrawSearch }" pattern="0.000#####"/>
			</td>
			<td>
				<fmt:formatNumber value="${funds.btcWithdrawSearchSuc }" pattern="0.000#####"/>
			</td>
			<td>
				用户提现BTC总额
			</td>
			<td>
				<fmt:formatNumber value="${funds.btcWithdraw }" pattern="0.000#####"/>
			</td>
		</tr>
		<tr class="ltc">
			<td>
				${startTime!=null?"搜索":"今日" }充值LTC
			</td>
			<td>
				<fmt:formatNumber value="${funds.ltcRechargeSearch }" pattern="0.000#####"/>
			</td>
			<td>
				-
			</td>
			<td>
				用户充值LTC总额
			</td>
			<td>
				<fmt:formatNumber value="${funds.ltcRecharge }" pattern="0.000#####"/>
			</td>
		</tr>
		<tr class="ltc">
			<td>
				${startTime!=null?"搜索":"今日" }提现LTC
			</td>
			<td>
				<fmt:formatNumber value="${funds.ltcWithdrawSearch }" pattern="0.000#####"/>
			</td>
			<td>
				<fmt:formatNumber value="${funds.ltcWithdrawSearchSuc }" pattern="0.000#####"/>
			</td>
			<td>
				用户提现LTC总额
			</td>
			<td>
				<fmt:formatNumber value="${funds.ltcWithdraw }" pattern="0.000#####"/>
			</td>
		</tr>
</table>
</c:if>