<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" style="width:100%;table-layout: fixed;">
	<thead>
		<tr>
			<th>编号</th>
			<th>创建管理员</th>
			<th>资金类型</th>
			<th>创建时间</th>
			<th>详情</th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${dataList!=null}">
				<tbody>
					<tr class="space">
						<td colspan="5">
						</td>
					</tr>
				</tbody>
				
				<c:forEach items="${dataList}" var="list" varStatus="statu">
				
					<tbody class="item_list" id="line_${list.id}">
						<tr>
							<td>
								${list.groupTime}
							</td>
							<td>
								${list.aUser.admId } / ${list.aUser.admName}
							</td>
							<td>
							 	${list.fundType}
							</td>
							<td>
							 	<fmt:formatDate value="${list.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
							</td>
							<td>
							 	<a href="/admin/financial/chart/see?groupTime=${list.groupTime}&fundType=${list.fundType}" target="_blank">查看详情</a>
							</td>
						</tr>
					</tbody>
				</c:forEach>
				<tfoot>
					<tr>
						<td colspan="5">
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
						<td colspan="5">
							<p>没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</c:otherwise>
		</c:choose>

</table>