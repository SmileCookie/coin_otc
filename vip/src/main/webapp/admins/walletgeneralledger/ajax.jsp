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
<font color="blue">钱包金额(QA=1-2=3+4+5+6+7)</font>
<table class="tb-list2" style="width:1570px;align:center;cellspacing:1;table-layout:fixed;" >
	<thead>
	<tr>
		<th width="10px">序号</th>
		<th width="10px">资金类型</th>
		<th width="30px">对账差额(JA-QA)</th>
		<th width="30px">内部调账差额</th>
		<th width="30px">交易平台金额(JA)</th>
		<th width="30px">外部调账差额</th>
		<th width="30px">钱包金额(QA=1-2)</th>
		<th width="30px">用户充值(1)</th>
		<th width="30px">用户提现(2)</th>
		
		<th width="5px">空白</th>
		
		<th width="30px">热冲发生额|余额(3)</th>
		<th width="30px">冷钱包发生额|余额(4)</th>
		<th width="30px">热提发生额|余额(5)</th>
		<th width="30px">网络费汇总(6)</th>
		<th width="30px">其他发生额(7)</th>
		
		<th width="33px">对账日期</th>
		
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
						<div style="text-align: left">
								${statu.index + 1}
						</div>
					</td>
					<td>
						<div style="text-align: left;" >
								${finAccWalDailyAcc.fundTypeName}
						</div>
					</td>
					<td> 
						<div style="color:#4169E1;text-align: right" >
							<font><fmt:formatNumber value="${finAccWalDailyAcc.transactionPlatformAmount - finAccWalDailyAcc.amount1 + finAccWalDailyAcc.amount2}" pattern="0.00000####"/></font>
						</div>
					</td>
					<td>
						<div style="text-align: right;width=30px" >
						<font>
							<fmt:formatNumber value="${finAccWalDailyAcc.internalAdjustmentPositive - finAccWalDailyAcc.internalAdjustmentNegative }" pattern="0.00000####"/>
						</font>
						</div>
					</td>
					<td>
						<div style="color:#4169E1;text-align: right" >
							<font><fmt:formatNumber value="${finAccWalDailyAcc.transactionPlatformAmount}" pattern="0.00000####"/></font>
						</div>
					</td>
					<td>
						<div style="text-align: right;">
						<font>
							<fmt:formatNumber value="${finAccWalDailyAcc.externalAdjustmentPositive - finAccWalDailyAcc.externalAdjustmentNegative }" pattern="0.00000####"/>
						</font>
						</div>
					</td>
					<td>
						<div style="color:#4169E1;text-align: right" >
							<font><fmt:formatNumber value="${finAccWalDailyAcc.amount1 - finAccWalDailyAcc.amount2}" pattern="0.00000####"/></font>
						</div>
					</td>
					<td>
						<div style="text-align: right;">
							<font><fmt:formatNumber value="${finAccWalDailyAcc.amount1}" pattern="0.00000####"/></font>
						</div>
					</td>
					<td>
						<div style="text-align: right;">
							<font><fmt:formatNumber value="${finAccWalDailyAcc.amount2}" pattern="0.00000####"/></font>
						</div>
					</td>
					
					<td></td>
					
					 
					<td>
						<!-- 热冲发生额 -->
						<div style="text-align: right;">
							<font><fmt:formatNumber value="${finAccWalDailyAcc.amount1 - finAccWalDailyAcc.amount4 - finAccWalDailyAcc.fee4}" pattern="0.00000####"/></font>
						</div>
					</td>
					
					<td>
						<!-- 冷钱包发生额 -->
						<div style="text-align: right;">
								<%--start by xwz 20171013--%>
								<%--<font><fmt:formatNumber value="${finAccWalDailyAcc.amount4 - finAccWalDailyAcc.amount3 - finAccWalDailyAcc.fee3+finAccWalDailyAcc.amount6 - finAccWalDailyAcc.amount7 - finAccWalDailyAcc.fee7--%>
							<font><fmt:formatNumber value="${finAccWalDailyAcc.amount4 - finAccWalDailyAcc.amount3 - finAccWalDailyAcc.fee3+finAccWalDailyAcc.amount6 - finAccWalDailyAcc.amount7 - finAccWalDailyAcc.fee7 + finAccWalDailyAcc.sumSameFee37}" pattern="0.00000####"/></font>
								<%--end--%>
						</div>
					</td>
					
					<td>
						<!-- 热提发生额 -->
						<div style="text-align: right;">
							<font><fmt:formatNumber value="${finAccWalDailyAcc.amount3 - finAccWalDailyAcc.amount2 - finAccWalDailyAcc.fee2+finAccWalDailyAcc.amount5-finAccWalDailyAcc.amount8-finAccWalDailyAcc.fee8}" pattern="0.00000####"/></font>
						</div>
					</td>
					<td>
						<div style="text-align: right;">
							<font><fmt:formatNumber value="${finAccWalDailyAcc.fee2 + finAccWalDailyAcc.fee8 + finAccWalDailyAcc.fee4 + finAccWalDailyAcc.fee7 - finAccWalDailyAcc.sumSameFee7 + finAccWalDailyAcc.fee3 - finAccWalDailyAcc.sumSameFee3 - finAccWalDailyAcc.sumSameFee37}" pattern="0.00000####"/></font>
						</div>
					</td>
					
					<!-- 
					<font><fmt:formatNumber value="${finAccWalDailyAcc.fee4}" pattern="0.00000####"/></font>
					<font><fmt:formatNumber value="${finAccWalDailyAcc.fee7 - finAccWalDailyAcc.sumSameFee7}" pattern="0.00000####"/></font>
					<font><fmt:formatNumber value="${finAccWalDailyAcc.fee3 - finAccWalDailyAcc.sumSameFee3 - finAccWalDailyAcc.sumSameFee37}" pattern="0.00000####"/></font>
					<font><fmt:formatNumber value="${finAccWalDailyAcc.fee8}" pattern="0.00000####"/></font>
					<font><fmt:formatNumber value="${finAccWalDailyAcc.fee2}" pattern="0.00000####"/></font>
					 -->
					<td>
						<!-- 其他 -->
						<div style="text-align: right;">
							<font><fmt:formatNumber value="${finAccWalDailyAcc.amount7 + finAccWalDailyAcc.amount8 - finAccWalDailyAcc.amount6 - finAccWalDailyAcc.amount5}" pattern="0.00000####"/></font>
						</div>
					</td>
					
	
					<td style="word-wrap:break-word;">
						<div style="text-align: left;">
						<!-- 
							<fmt:formatDate value="${finAccWalDailyAcc.minTime }" pattern="yyyy-MM-dd" />
							到
							<fmt:formatDate value="${finAccWalDailyAcc.maxTime }" pattern="yyyy-MM-dd" />
						</div>
						 -->
						${queryDate }
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