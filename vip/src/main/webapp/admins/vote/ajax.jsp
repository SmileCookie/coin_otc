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
								<span>活动编号：${list.activityId} </span>
								<span>创建时间：<fmt:formatDate value="${list.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
							</td>
						</tr>
					</tbody>
				
					<tbody class="item_list" id="line_${list.activityId}">
						<tr>
							<td>
								<a target="_blank" href="/vote?preview_state=${list.activityId }">${list.activityNameJson }</a>
							</td>
							<td >
								<fmt:formatDate value="${list.startTime }" pattern="yyyy-MM-dd HH:mm:ss"/>至<fmt:formatDate value="${list.endTime }" pattern="yyyy-MM-dd HH:mm:ss"/>

							</td>
							<td >
								<c:if test="${list.state==1}">
									<br/>
									<span >进行中</span>
								</c:if>
								<c:if test="${list.state==0}">
									<br/>
									<span>未开始</span>
								</c:if>
								<c:if test="${list.state==2}">
									<br/>
									<span>暂停中</span>
								</c:if>
								<c:if test="${list.state==3}">
									<br/>
									<span>已结束</span>
								</c:if>
							</td>

							<td>
								<c:if test="${list.state==1}">
									<a href="javascript:changeState('${list.activityId}',2);">暂停</a>
									<a href="javascript:changeState('${list.activityId}',3);">停止</a>
									<a target="_blank" href="/admin/vote/voteReult?id=${list.activityId }">查看结果</a>
									<a target="_blank" href="/admin/vote/aoru?id=${list.activityId }">修改</a>
									<br/>
								</c:if>
								<c:if test="${list.state==0}">
									<a href="javascript:changeState('${list.activityId}',1);">开启</a>
									<a target="_blank" href="/admin/vote/aoru?id=${list.activityId }">修改</a>
									<a href="javascript:changeState('${list.activityId}',4);">删除</a>
									<br/>
								</c:if>
								<c:if test="${list.state==2}">
									<a href="javascript:changeState('${list.activityId}',1);">开始</a>
									<a href="javascript:changeState('${list.activityId}',3);">停止</a>
									<a target="_blank" href="/admin/vote/voteReult?id=${list.activityId }">查看结果</a>
									<a target="_blank" href="/admin/vote/aoru?id=${list.activityId }">修改</a>
									<br/>
								</c:if>
								<c:if test="${list.state==3}">
									<a target="_blank" href="/admin/vote/voteReult?id=${list.activityId }">查看结果</a>
									<a target="_blank" href="/admin/vote/aoru?id=${list.activityId }">修改</a>
									<a href="javascript:changeState('${list.activityId}',4);">删除</a>
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