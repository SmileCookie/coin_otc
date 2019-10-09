<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<script type="text/javascript">
	$(function() {
		JuaBox.reSetHeight();
	});
</script>
<table>
	<thead>
		<tr>
			<th>${L:l(lan,"时间") }</th>
			<th>${L:l(lan,"类型") }</th>
			<th>${L:l(lan,"收支") }(${coint.tag })</th>
			<th>${L:l(lan,"备注") }</th>
		</tr>
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${fn:length(dataList)>0}">
				<c:forEach items="${dataList}" var="bill">
					<tr>
						<td><fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${bill.sendTime}" /></td>
						<td>
							<c:choose>
								<c:when test="${coint.tag ne 'BTC'}">
									${L:l(lan, fn:replace(bill.showType, "BTC", coint.tag))}
								</c:when>
								<c:otherwise>${L:l(lan, bill.showType)}</c:otherwise>
							</c:choose>
						</td>
						<td><c:if test="${bill.bt.inout == 0}">
								<span class="gray">-</span>
							</c:if> <c:if test="${bill.bt.inout == 1}">
			               		<span class="orange">+<fmt:formatNumber value="${bill.amount }" pattern="#0.000#####" />=<fmt:formatNumber value="${bill.balance }" pattern="#0.0000####" /></span>
							</c:if> <c:if test="${bill.bt.inout == 2}">
			               		<span class="green">-<fmt:formatNumber value="${bill.amount }" pattern="#0.000#####" />=<fmt:formatNumber value="${bill.balance }" pattern="#0.0000####" /></span>
							</c:if></td>
						<td>
							<c:choose>
			              		<c:when test="${fn:contains(bill.remark, '(')}">
			              			<c:set var="recommendT" value="${fn:substring(bill.remark, 0, fn:indexOf(bill.remark, '('))}"/>
			              			<c:set var="numT" value="${fn:substring(bill.remark, fn:indexOf(bill.remark, '('), -1)}"/>
			              			${L:l(lan, recommendT)}${numT}
			              		</c:when>
			              		<c:otherwise>
			              			${L:l(lan, bill.remark)}
			              		</c:otherwise>
			              	</c:choose>
						</td>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan="5">
						<div class="norecord"><i class="fa fa-exclamation-triangle"></i><p>${L:l(lan,"暂时没有相关记录！")}</p></div>
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</tbody>
</table>
<c:if test="${fn:length(dataList)>0}">
	<div class="lk_page">
		<div id="page_navA" class="page_nav">
			<div class="con">
				<c:if test="${pager!=null}">${pager}</c:if>
			</div>
		</div>
	</div>
</c:if>