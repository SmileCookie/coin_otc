<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" style="width:100%">
	<thead>
		<tr>
			<th>关键字</th>
			<th>类型</th>
			<th>添加时间</th>
			<th>操作</th>
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
					<tbody>
						<tr class="hd">
							<td colspan="4"><span>编号：${list.id} </span>
							</td>
						</tr>
					</tbody>
				
					<tbody class="item_list" id="line_${list.id}">
						<tr>
							<td>
								${list.word}
							</td>
							<td>
								${list.kType.value}
							</td>
							<td>
								<fmt:formatDate value="${list.adate}" pattern="yyyy-MM-dd HH:mm"/>
							</td>
							<td>
								<a href="javascript:vip.list.aoru({id:'${list.id}'})">编辑</a>
								<a href="javascript:vip.list.del({id:'${list.id}'})">删除</a>
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
							<p>没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</c:otherwise>
		</c:choose>

</table>