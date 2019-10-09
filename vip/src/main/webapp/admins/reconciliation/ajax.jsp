<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" id="ListTable" style="width: 100%">

	<tr class="hd">
		<h1 style="line-height: 60px;background-color: #96aeb8;margin-bottom: 20px; text-align: center;">交易平台资金总账表(
			<jsp:useBean id="now" class="java.util.Date" scope="page"/>
			<fmt:formatDate value="${beginTime}" pattern="yyyy-MM-dd " />至
			<fmt:formatDate value="${endTime}" pattern="yyyy-MM-dd " />
			)
		</h1>
	</tr>
	<tr>
		<th >序号</th>
		<th >报表日期</th>
		<th >资金类型</th>
		<th >交易平台金额(11+4+9+10-3+6-N+W)</th>
		<th >外部调账差额(W)</th>
		<th >内部调账差额(N)</th>
		<th >交易平台内部差额(12-11-9+N)</th>
		<th >充值(1)</th>
		<th >提现(2)</th>
		<th >系统充值(3)</th>
		<th >系统扣除(4)</th>
		<th >系统分发(5)</th>
		<th >ICO兑换(6)</th>
		<th >交易卖出(7)</th>
		<th >交易买入(8)</th>
		<th>交易手续费(9)</th>
		<th >提现手续费(10)</th>
		<th >账面余额(11)</th>
		<th >交易平台类型汇总余额(1-2+3-4+5-6)</th>
	</tr>
	<c:choose>

		<c:when test="${dataList!=null}">
			<c:forEach items="${dataList}" var="list" varStatus="statu">
				<tr class="item_list_bd item_list_bgcolor">
					<td>${statu.index + 1}</td>
					<td><fmt:formatDate value="${list.reportDate }" pattern="yyyy-MM-dd "/></td>
					<td>${list.fundsTypeName }</td>
					<td><fmt:formatNumber value="${list.bookBalance + list.sysDeduction + list.transactionFee + list.withdrawFee - list.sysRecharge + list.icoExchange - list.internalAdjustmentPositive + list.internalAdjustmentNegative + list.externalAdjustmentPositive - list.externalAdjustmentNegative }" pattern="0.000#######"/></td>
					<td><fmt:formatNumber value="${list.externalAdjustmentPositive - list.externalAdjustmentNegative }" pattern="0.000#######"/></td>
					<td><fmt:formatNumber value="${list.internalAdjustmentPositive - list.internalAdjustmentNegative }" pattern="0.000#######"/></td>
					<td><fmt:formatNumber value="${list.recharge - list.withdraw + list.sysRecharge - list.sysDeduction - list.icoExchange +list.sysSort - list.bookBalance - list.transactionFee + list.internalAdjustmentPositive - list.internalAdjustmentNegative}" pattern="0.000#######"/></td>
					<td><fmt:formatNumber value="${list.recharge }" pattern="0.000#######"/></td>
					<td><fmt:formatNumber value="${list.withdraw }" pattern="0.000#######"/></td>
					<td><fmt:formatNumber value="${list.sysRecharge }" pattern="0.000#######"/></td>
					<td><fmt:formatNumber value="${list.sysDeduction }" pattern="0.000#######"/></td>
					<td><fmt:formatNumber value="${list.sysSort }" pattern="0.000#######"/></td>
					<td><fmt:formatNumber value="${list.icoExchange}" pattern="0.000#######"/></td>
					<td><fmt:formatNumber value="${list.sell }" pattern="0.000#######"/></td>
					<td><fmt:formatNumber value="${list.buy }" pattern="0.000#######"/></td>
					<td><fmt:formatNumber value="${list.transactionFee }" pattern="0.000#######"/></td>
					<td><fmt:formatNumber value="${list.withdrawFee }" pattern="0.000#######"/></td>
					<td><fmt:formatNumber value="${list.bookBalance }" pattern="0.000#######"/></td>
					<td><fmt:formatNumber value="${list.recharge - list.withdraw + list.sysRecharge - list.sysDeduction - list.icoExchange + list.sysSort}" pattern="0.000#######"/></td>
				</tr>
				</tr>
				</tbody>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<tbody class="air-tips">
			<tr>
				<td colspan="9">
					<p>暂时没有符合要求的记录！</p>
				</td>
			</tr>
			</tbody>
		</c:otherwise>
	</c:choose>
</table>