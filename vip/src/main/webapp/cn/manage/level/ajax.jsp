<%@ page session="false" language="java" import="java.util.*"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<table class="table table-striped table-bordered text-left table-level" id="">
	<thead>
		<tr>
			<th>${L:l(lan,"帐户等级-积分明细-积分明细表头-1")}</th>
			<th>${L:l(lan,"帐户等级-积分明细-积分明细表头-2")}</th>
			<th>${L:l(lan,"帐户等级-积分明细-积分明细表头-3")}</th>
			<th>${L:l(lan,"帐户等级-积分明细-积分明细表头-4")}</th>
		</tr>
	</thead>

	<c:choose>
		<c:when test="${fn:length(dataList)>0}">
			<tbody>
				<c:forEach items="${dataList}" var="item">
					<c:if test="${item.jifen > 0}">
					<tr>
						<td><fmt:formatDate pattern="${lan == 'en'? 'MM-dd-yyyy HH:mm:ss':'yyyy-MM-dd HH:mm:ss'}" value="${item.addTime}" /></td>
						<td>${L:l(lan,item.typeShowNew)}</td>
						<td>
							<c:if test="${item.ioType==0}">
								 <font class="green">+<fmt:formatNumber value="${item.jifen}" maxFractionDigits="2" /></font>
							</c:if> 
							<c:if test="${item.ioType==1}">
								<font class="orange">-<fmt:formatNumber value="${item.jifen}" maxFractionDigits="2" /> </font>
							</c:if>
						</td>
						<td>${L:l(lan,item.memo)}</td>
					</tr>
					</c:if>
				</c:forEach>
			</tbody>

			<tfoot>
				<tr>
					<td colspan="7">
						<div id="page_navA" class="page_nav">
							<div class="con">
								<c:if test="${pager!=null}">${pager}</c:if>
							</div>
						</div></td>
				</tr>
			</tfoot>
		</c:when>
		<c:otherwise>
			<tbody>
				<tr>
					<td colspan="7">
						<div class='bk-norecord'><p><i class='iconfont2 mr5'>&#xe653;</i>${L:l(lan,'暂时没有相关记录')}</p></div>
					</td>
				</tr>
			</tbody>
		</c:otherwise>
	</c:choose>
</table>