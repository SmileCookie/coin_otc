<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<input type="hidden" id="tab" name="tab" value="${tab }" />
<input type="hidden" id="page" name="page" value="${page }" />
<table class="tb-list2" style="width:100%; " id="ListTable">
	<thead>
		<tr>
			<th>标识</th>
			<th>行情URL</th>
			<th>最新价格</th>
			<th>币种</th>
			<th>操作</th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${dataList!=null}">
				<c:forEach items="${dataList}" var="list" varStatus="statu">
					<tbody>
						<tr class="hd">
							<td colspan="7">
								<span>编号：${list.id} </span>
								<c:if test="${list.lastTime != null }">
									<span>最后更新时间：<fmt:formatDate value="${list.lastTime }" pattern="yyyy-MM-dd HH:mm:ss"/></span>
								</c:if>
							</td>
						</tr>
					</tbody>
					<tbody class="item_list" id="line_${list.id}">
						<tr>
							<td>${list.symbol }</td>
							<td>${list.tickerUrl }</td>
							<td>${list.lastPrice }</td>
							<td>${list.ft.tag }</td>
							<td>
								<a href="javascript:vip.list.aoru({id : '${list.id }',width : 650 , height : 520});">编辑</a>
								<c:if test="${list.isDeleted eq 0 }">
									<a href="javascript:vip.list.del({id : '${list.id }'});">删除</a>
								</c:if>
							</td>
						</tr>
					</tbody>
				</c:forEach>
				<tfoot>
					<tr>
						<td colspan="7">
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
						<td colspan="7">
							<p>没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</c:otherwise>
		</c:choose>
</table>