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
<table class="tb-list2" cellspacing="0" width="1350px" salign="center" >
	<thead>
		<tr>
			<th width="26px">序号</th>
			<th>交易流水号</th>
			<th>交易流水号N</th>
			<th>地址</th>
			<th>金额</th>
			<th>资金类型</th>
			<th>钱包名称</th>
			<th>钱包类型</th>
			<th>交易金额</th>
			<th>网络费</th>
			<th>钱包余额</th>
			<th>交易类型</th>
			<th>区块高度</th>
			<th>确认时间</th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${finAccWalletBillDetail!=null}">
				<tbody>
					<tr class="space">
						<td colspan="8">
						</td>
					</tr>
				</tbody>
			
				<c:forEach items="${finAccWalletBillDetail}" var="finAccWalletBillDetails" varStatus="statu">
					<tbody class="item_list" >
						<tr>
							<td>
								<div style="text-align: left;">
										${statu.index + 1}
								</div>
							</td>
							<td style="word-wrap:break-word;">
								<div style="width:100px;">
										${finAccWalletBillDetails.txId}
								</div>
							</td>
							<td style="word-wrap:break-word;">
								<div style="width:100px;">
										${finAccWalletBillDetails.txIdN}
								</div>
							</td>
							<td style="word-wrap:break-word;">
								<div style="width:100px;">
										${finAccWalletBillDetails.toAddress}
								</div>
							</td>
							<td style="word-wrap:break-word;">
								<div style="text-align: left">
									<font><fmt:formatNumber value="${finAccWalletBillDetails.txNAmount}" pattern="0.00000####"/></font>
								</div>
							</td>
							<td style="word-wrap:break-word;">
								<div style="text-align: left;width: 50px;">
										${finAccWalletBillDetails.fundsTypeName}
								</div>
							</td>
							<td>
								<div style="text-align: left;width: 120px;">
										${finAccWalletBillDetails.walName}
								</div>
							</td>
							<td>
								<div style="text-align: left;width: 80px;">
										${finAccWalletBillDetails.walTypeName}
								</div>
							</td>
							<td>
								<div style="text-align: right;width: 50px;">
									<font><fmt:formatNumber value="${finAccWalletBillDetails.txAmount}" pattern="0.00000####"/></font>
								</div>
							</td>
							<td>
								<div style="text-align: right;">
									<font><fmt:formatNumber value="${finAccWalletBillDetails.fee}" pattern="0.00000####"/></font>
								</div>
							</td>
							<td>
								<div style="text-align: right;">
									<font><fmt:formatNumber value="${finAccWalletBillDetails.walBalance}" pattern="0.00000####"/></font>
								</div>
							</td>
							<td>
								<div style="text-align: left;width: 150px;">
										${finAccWalletBillDetails.dealTypeName}
								</div>
							</td>
							<td>
								<div style="text-align: left;">
										${finAccWalletBillDetails.blockHeight}
								</div>
							</td>

							<td style="word-wrap:break-word;">
								<div style="text-align: left;width: 150px;">
									<fmt:formatDate value="${finAccWalletBillDetails.configTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
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