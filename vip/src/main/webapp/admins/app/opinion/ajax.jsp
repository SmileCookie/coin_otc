<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<input type="hidden" id="tab" name="tab" value="${tab }" />
<input type="hidden" id="page" name="page" value="${page }" />
<table class="tb-list2" style="width:100%;table-layout: fixed;">
	<thead>
		<tr>
			<th>内容</th>
			<th>用户</th>
			<th>提交时间</th>
			<th>回复</th>
			<th>回复时间</th>
			<th style="width: 100px;">操作</th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${dataList!=null}">
				<c:forEach items="${dataList}" var="data" varStatus="statu">
					<tbody>
						<tr class="space">
							<td colspan="6">
							</td>
						</tr>
					</tbody>
					<tbody class="item_list" id="line_${data.id}">
						<tr>
							<td>${data.content}</td>
							<td>${data.userId}</td>
							<td><fmt:formatDate value="${data.submitDate }" pattern="yyyy-MM-dd" /></td>
							<td>${data.reply}</td>
							<td><fmt:formatDate value="${data.replyDate }" pattern="yyyy-MM-dd" /></td>
							<td>
								<a href="javascript:vip.list.aoru({id : '${data.id }',width : 600 , height : 500});">回复</a>
								<a href="javascript:vip.list.del({id : '${data.id }'});">删除</a><br />
							</td>
						</tr>
					</tbody>
				</c:forEach>
				<tfoot>
					<tr>
						<td colspan="6">
							<div class="page_nav" id="pagin">
								<div class="con">
									<c:if test="${pager==''}">共${itemCount}项</c:if>
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
						<td colspan="6">
							<p>没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</c:otherwise>
		</c:choose>

</table>