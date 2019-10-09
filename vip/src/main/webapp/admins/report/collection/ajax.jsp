<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<style type="text/css">
	.btn2 a:hover {
		background: none repeat scroll 0 0 #6DC03C;
		color: #FFFFFF;
	}
	.btn2 a {
		background: none repeat scroll 0 0 #8DC03C;
		border-radius: 3px 3px 3px 3px;
		color: #FFFFFF;
		height: 31px;
		line-height: 32px;
		padding: 5px 10px;
	}
	.btn2 a.red {
		background: none repeat scroll 0 0 #E55E48;
	}
	.btn2 a.red:hover {
		background: none repeat scroll 0 0 #C55E48;
	}
</style>
<table class="tb-list2"  style="width:2075px;align:center;cellspacing:0;" >
	<thead>
	<tr>
		<th width="26px">序号</th>
		<th>资金类型</th>

		<th>冷存入</th>
		<th>冷转出</th>
		<th>冷手续费</th>
		<th>冷余额</th>

		<th>热提存入</th>
		<th>热提转出</th>
		<th>热提手续费</th>
		<th>热提余额</th>

		<th>热充存入</th>
		<th>热充转出</th>
		<th>热充手续费</th>
		<th>热充余额</th>
	</tr>
	</thead>
	<c:choose>
		<c:when test="${listFinAccWalDailyAcc!=null}">
			<tbody>
			<tr class="space">
				<td colspan="8">
				</td>
			</tr>
			</tbody>

			<c:forEach items="${listFinAccWalDailyAcc}" var="finAccWalDailyAcc" varStatus="statu">
				<tbody class="item_list" >
				<tr>
					<td>
						<div style="text-align: left;">
								${statu.index + 1}
						</div>
					</td>
					<td>
						<div style="text-align: left;">
								${finAccWalDailyAcc.fundTypeName}
						</div>
					</td>
					<td>
						<div style="text-align: right;">
							<font><fmt:formatNumber value="${finAccWalDailyAcc.amount4+finAccWalDailyAcc.amount6}" pattern="0.00000####"/></font>
						</div>
					</td>
					<td>
						<div style="text-align: right;">
							<font><fmt:formatNumber value="${finAccWalDailyAcc.amount3+finAccWalDailyAcc.amount7}" pattern="0.00000####"/></font>
						</div>
					</td>
					<td>
						<div style="text-align: right;">
							<font><fmt:formatNumber value="${finAccWalDailyAcc.fee3+finAccWalDailyAcc.fee7 - finAccWalDailyAcc.sumSameFee37}" pattern="0.00000####"/></font>
						</div>
					</td>
					<td>
						<div style="text-align: right;">
							<font><fmt:formatNumber value="${finAccWalDailyAcc.amount4+finAccWalDailyAcc.amount6-finAccWalDailyAcc.amount3-finAccWalDailyAcc.amount7-finAccWalDailyAcc.fee3-finAccWalDailyAcc.fee7 + finAccWalDailyAcc.sumSameFee37}" pattern="0.00000####"/></font>
						</div>
					</td>
					<td>
						<div style="text-align: right;">
							<font><fmt:formatNumber value="${finAccWalDailyAcc.amount3 + finAccWalDailyAcc.amount5}" pattern="0.00000####"/></font>
						</div>
					</td>
					<td>
						<div style="text-align: right;">
							<font><fmt:formatNumber value="${finAccWalDailyAcc.amount2+finAccWalDailyAcc.amount8}" pattern="0.00000####"/></font>
						</div>
					</td>
					<td>
						<div style="text-align: right;">
							<font><fmt:formatNumber value="${finAccWalDailyAcc.fee2+finAccWalDailyAcc.fee8}" pattern="0.00000####"/></font>
						</div>
					</td>
					<td>
						<div style="text-align: right;">
							<font><fmt:formatNumber value="${finAccWalDailyAcc.amount3+finAccWalDailyAcc.amount5-finAccWalDailyAcc.amount2-finAccWalDailyAcc.amount8-finAccWalDailyAcc.fee2-finAccWalDailyAcc.fee8}" pattern="0.00000####"/></font>
						</div>
					</td>
					<td>
						<div style="text-align: right;">
							<font><fmt:formatNumber value="${finAccWalDailyAcc.amount1}" pattern="0.00000####"/></font>
						</div>
					</td>
					<td>
						<div style="text-align: right;">
							<font><fmt:formatNumber value="${finAccWalDailyAcc.amount4}" pattern="0.00000####"/></font>
						</div>
					</td>
					<td>
						<div style="text-align: right;">
							<font><fmt:formatNumber value="${finAccWalDailyAcc.fee4}" pattern="0.00000####"/></font>
						</div>
					</td>
					<td>
						<div style="text-align: right;">
							<font><fmt:formatNumber value="${finAccWalDailyAcc.amount1-finAccWalDailyAcc.amount4-finAccWalDailyAcc.fee4}" pattern="0.00000####"/></font>
						</div>
					</td>

				</tr>
				</tbody>
			</c:forEach>
			<tfoot>
			<tr>
				<td colspan="12">
					<div class="page_nav" id="pagin">
						<div class="con">
							<c:if test="${pager=='' }">共${itemCount }项</c:if>
							<c:if test="${pager!=null}">${pager}</c:if>
						</div>
					</div>
				</td>
			</tr>
			</tfoot>
		</c:when>
		<c:otherwise>
			<tbody class="air-tips">
			<tr>
				<td colspan="12">
					<p id="exportFlag">没有符合要求的记录!</p>
				</td>
			</tr>
			</tbody>
		</c:otherwise>
	</c:choose>

</table>