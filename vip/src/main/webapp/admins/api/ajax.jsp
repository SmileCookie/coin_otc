<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" style="width:100%;table-layout: fixed;">
	<thead>
		<tr>
			<th>用户名</th>
			<th>绑定IP地址</th>
			<th>accesskey</th>
			<th>secretkey</th>
			<th>激活状态</th>
			<th>锁定状态</th>
			<th style="width: 100px;">操作</th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${dataList!=null}">
				<c:forEach items="${dataList}" var="list" varStatus="statu">
					<tbody>
						<tr class="space">
							<td colspan="7">
							</td>
						</tr>
					</tbody>
				
					<tbody class="item_list" id="line_${list.id}">
						<tr>
							<td>
								<a href="javascript:showUser('${list.userId}')" style="font-weight: bold;color:green;" id="text_${list.userId }">${list.userName}</a>
							</td>
							<td>${list.ipaddrs }</td>
							<td>${list.accesskey }</td>
							<td>${list.secretkey }</td>
							<td>
								<c:if test="${list.isAct eq 1 }">已激活</c:if>
								<c:if test="${list.isAct eq 0 }">未激活</c:if>
							</td>
							<td>
								<c:if test="${list.isLock eq 1 }">已锁定</c:if>
								<c:if test="${list.isLock eq 0 }">未锁定</c:if>
							</td>
							<td>
								<a href="javascript:setStatus('${list.id }','${list.userName }');">修改状态</a>
								<a href="javascript:vip.list.del({id : '${list.id }'});" style="display: none;">删除</a>
							</td>
						</tr>
					</tbody>
				</c:forEach>
				<tfoot>
					<tr>
						<td colspan="7">
							<div class="page_nav" id="pagin">
								<div class="con">
									<c:if test="${pager==''}">共${fn:length(dataList)}项</c:if>
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