<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<table class="tb-list2" style="width: 100%">
	<thead>
	<tr>
		<th>买家Id/委托Id</th>
		<th>卖家Id/委托Id</th>
		<th>成交单价</th>
		<th>成交数量</th>
		<th>成交总金额</th>
		<th>类型</th>
		<th>状态</th>
		<th style="width: 100px;">操作</th>
	</tr>
	</thead>
	<c:choose>
		<c:when test="${dataList!=null}">
			<c:forEach items="${dataList}" var="list">
				<tbody>
				<tr class="space">
					<td colspan="4"></td>
				</tr>
				</tbody>
				<tbody class="item_list" id="line_${list.transRecordId}">
				<tr class="hd">
					<td colspan="10">
						<span>成交编号:${list.transRecordId}</span><span>成交时间：${list.timesView} </span>
					</td>
				</tr>
				<tr>

					<td>
						<c:choose>
							<c:when test="${list.userBuyType == '-'}">
								${list.userIdBuy}
							</c:when>
							<c:otherwise>
								<font color="blue">${list.userIdBuy}</font>
							</c:otherwise>
						</c:choose>
						<br/>${list.entrustIdBuy}
					</td>
					<td>
						<c:choose>
							<c:when test="${list.userSellType == '-'}">
								${list.userIdSell}
							</c:when>
							<c:otherwise>
								<font color="blue">${list.userIdSell}</font>
							</c:otherwise>
						</c:choose>
						<br/>${list.entrustIdSell}
					</td>


					<%--<td>${list.userIdBuy}<br/>${list.entrustIdBuy}</td>--%>
					<%--<td>${list.userIdSell}<br/>${list.entrustIdSell}</td>--%>
					<td><fmt:formatNumber value ="${list.unitPrice.doubleValue()}" pattern="####.##########"/></td>
					<td><fmt:formatNumber value ="${list.numbers.doubleValue()}" pattern="####.##########"/></td>
					<td><fmt:formatNumber value ="${list.totalPrice.doubleValue()}" pattern="####.##########"/></td>
					<td>${list.typesView}</td>
					<td>${list.statusView}</td>
					<td>
						<c:choose>
							<c:when test="${list.status eq 1}">
								<a href="javascript:udpateRecordStatus({id :${list.transRecordId}});">更新</a>
							</c:when>
							<c:otherwise>
								-
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				</tbody>
			</c:forEach>
			<tfoot>
			<tr>
				<td colspan="4">
					<div class="page_nav" id="pagin">
						<div class="con">
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
				<td colspan="4">
					<p>暂时没有符合要求的记录！</p>
				</td>
			</tr>
			</tbody>
		</c:otherwise>
	</c:choose>
</table>