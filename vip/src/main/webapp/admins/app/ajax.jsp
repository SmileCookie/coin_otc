<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<input type="hidden" id="tab" name="tab" value="${tab }" />
<input type="hidden" id="page" name="page" value="${page }" />
<table class="tb-list2" style="width:100%;table-layout: fixed;">
	<thead>
		<tr>
			<th>客户端类型</th>
			<th>是否强制更新</th>
			<th>版本名称</th>
			<th>版本号</th>
			<th>下载链接</th>
			<!-- <th>备注</th> -->
			<th>更新时间</th>
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
							<td>${data.type}</td>
							<td>
								<c:if test="${data.enforceUpdate == true}">
									强制更新
								</c:if>
								<c:if test="${data.enforceUpdate == false}">
									非强制更新
								</c:if>
							</td>
							<td>${data.cnName}</td>
							<td>${data.num}</td>
							<td><a href="${data.url}">${data.url}</a></td>
							<%-- <td>${data.remark}</td> --%>
							<td><fmt:formatDate value="${data.updateDatetime }" pattern="yyyy-MM-dd HH:mm:ss" /></td>
							<td>
								<a href="javascript:changeKey('${data.id }');">APPKey</a>
								<a href="javascript:vip.list.aoru({id : '${data.id }',width : 700 , height : 700});">修改</a>
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