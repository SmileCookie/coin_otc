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
<table class="tb-list2" style="width:100%;table-layout: fixed;">
	<thead>
		<tr>
			<th>商户平台流水号</th>
			<th>商户平台ID</th>
			<th>本平台记录ID</th>
			<th>类型</th>
			<th>金额</th>
			<th>商户平台金额</th>
			<th>手续费</th>
			<th>商户平台手续费</th>
			<th>成功时间</th>
			<th>商户平台成功时间</th>
			<th>操作</th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${dataList!=null}">
				<tbody>
					<tr class="space">
						<td colspan="11">
						</td>
					</tr>
				</tbody>
				<c:forEach items="${dataList}" var="list" varStatus="statu">
					<tbody>
						<tr class="space">
							<td colspan="11"></td>
						</tr>
					</tbody>
					<c:set var="currencyFormat" value="#,##0.00######"/>
					<tbody class="item_list" id="line_${list.merchantOrderNo}">
						<tr>
							<td>${list.merchantOrderNo }</td>
							<td>${list.tradId == 0 ? "" : list.tradId}</td>
							<td>${list.recordId == 0 ? "" : list.recordId}</td>
							<td>
                                <c:choose>
                                 <c:when test="${list.isIn==1 }">充值</c:when>
                                 <c:when test="${list.isIn==0 }">提现</c:when>
                                 <c:otherwise></c:otherwise>
                                </c:choose>
							</td>

							<td ${list.unusually==1?'class="error-bg"':'' }><fmt:formatNumber value="${list.money}" pattern="${currencyFormat}" /> ${list.currency}</td>
							<td ${list.unusually==1?'class="error-bg"':'' }><fmt:formatNumber value="${list.mMoney}" pattern="${currencyFormat}" /> ${list.currency}</td>

							<td ${list.unusually==1?'class="error-bg"':'' }><fmt:formatNumber value="${list.fees}" pattern="${currencyFormat}" /> ${list.currency}</td>
							<td ${list.unusually==1?'class="error-bg"':'' }><fmt:formatNumber value="${list.mFees}" pattern="${currencyFormat}" /> ${list.currency}</td>

							<td ${list.unusually==1?'class="error-bg"':'' }>
								<fmt:formatDate value="${list.date }" pattern="yyyy-MM-dd HH:mm:ss" />
							</td>
							<td ${list.unusually==1?'class="error-bg"':'' }>
								<fmt:formatDate value="${list.mdate }" pattern="yyyy-MM-dd HH:mm:ss" />
							</td>

							<td>
								<a href="javascript:vip.list.del({id:${list.merchantOrderNo }});">删除</a>
							</td>
						</tr>
					</tbody>
				</c:forEach>
				<tfoot>
					<tr>
						<td colspan="11">
							<div class="page_nav" id="pagin">
								<div class="con">
									<c:if test="${pager=='' }">共${itemCount }项</c:if>
									<c:if test="${pager!=null}">${pager}</c:if>

									<c:if test="${total > 0}"><span style="margin: 0 5px; color:blue;">累计金额：<fmt:formatNumber value="${total}" pattern="${currencyFormat}" />${currency}</span></c:if>
									<c:if test="${mtotal > 0}"><span style="margin: 0 5px;color:blue;">商户平台累计金额：<fmt:formatNumber value="${mtotal}" pattern="${currencyFormat}" />${currency}</span></c:if>
								</div>
							</div>
						 </td>
					</tr>
				 </tfoot>
			</c:when>
			<c:otherwise>
				<tbody class="air-tips">
					<tr>
						<td colspan="11">
							<p>没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</c:otherwise>
		</c:choose>
</table>