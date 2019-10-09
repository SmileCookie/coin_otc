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
		<th>活动标题</th>
		<th>活动时间</th>
		<th>状态</th>
		<th>操作</th>
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
				<tbody>
				<tr class="space">
					<td colspan="5"></td>
				</tr>
				</tbody>
				<tbody>
				<tr class="hd">
					<td colspan="5">
						<span>活动编号：${list.eventId} </span>
						<span>创建时间：<fmt:formatDate value="${list.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
						<span>创建人：${list.createUserName} </span>
					</td>
				</tr>
				</tbody>

				<tbody class="item_list" id="line_${list.eventId}">
				<tr>
					<td>
						<a target="_blank" href="/admin/lucky/getView?eventId=${list.eventId }">${list.eventTitleJson}</a>
					</td>
					<td >
						<fmt:formatDate value="${list.startTime }" pattern="yyyy-MM-dd HH:mm:ss"/>至<fmt:formatDate value="${list.endTime }" pattern="yyyy-MM-dd HH:mm:ss"/>

					</td>
					<td >
						<span>${list.statusView }</span>
					</td>
					<td>
						<c:if test="${list.statusView=='未开始'}">
							<a target="_blank" href="/admin/lucky/aoru?eventId=${list.eventId }">修改</a>
							<br/>
							<a href="javascript:changeState('${list.eventId}','05','${list.eventTitleJson}');">删除</a>
							<br/>
							<a href="javascript:changeState('${list.eventId}','01','${list.eventTitleJson}');">开启</a>
							<br/>
						</c:if>
						<c:if test="${list.statusView=='进行中'}">
							<a target="_blank" href="/admin/lucky/aoru?eventId=${list.eventId }">修改</a>
							<br/>
							<a href="javascript:changeState('${list.eventId}','04','${list.eventTitleJson}');">停止</a>
							<br/>
							<a href="javascript:changeState('${list.eventId}','03','${list.eventTitleJson}');">暂停</a>
							<br/>
							<a target="_blank" href="/admin/lucky/getResultInfo?eventId=${list.eventId }">查看结果</a>
							<br/>
						</c:if>
						<c:if test="${list.statusView=='暂停'}">
							<a target="_blank" href="/admin/lucky/aoru?eventId=${list.eventId }">修改</a>
							<br/>
							<a href="javascript:changeState('${list.eventId}','04','${list.eventTitleJson}');">停止</a>
							<br/>
							<a href="javascript:changeState('${list.eventId}','02','${list.eventTitleJson}');">开始</a>
							<br/>
							<a target="_blank" href="/admin/lucky/getResultInfo?eventId=${list.eventId }">查看结果</a>
							<br/>
						</c:if>
						<c:if test="${list.statusView=='结束'}">
							<a target="_blank" href="/admin/lucky/aoru?eventId=${list.eventId }">修改</a>
							<br/>
							<a href="javascript:changeState('${list.eventId}','05','${list.eventTitleJson}');">删除</a>
							<br/>
							<a target="_blank" href="/admin/lucky/getResultInfo?eventId=${list.eventId }">查看结果</a>
							<br/>
						</c:if>
					</td>
				</tr>
				</tbody>
			</c:forEach>

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