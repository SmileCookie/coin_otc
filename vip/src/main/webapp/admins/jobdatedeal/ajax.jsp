<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" id="ListTable" style="width: 100%">
	<tr>
		<th >序号</th>
		<th >作业名称</th>
		<th >开始执行时间</th>
		<th >结束执行时间</th>
		<th >时间间隔秒</th>
		<th >执行类</th>
		<th >状态</th>
		<th >备注</th>
		<th >创建时间</th>
		<th >操作</th>
	</tr>
	<c:choose>
		<c:when test="${dataList!=null}">
			<c:forEach items="${dataList}" var="list" varStatus="statu">
				<tbody id="row${list.id }">
				<tr class="item_list_bd item_list_bgcolor">
					<td>${statu.index + 1}</td>
					<td>${list.jobName }</td>
					<td>${list.jobStartTime }</td>
					<td>${list.jobEndTime }</td>
					<td>${list.jobInterval }</td>
					<td>${list.jobClass }</td>
					<td>${list.statusDes }</td>
					<td>${list.remark }</td>
					<td>${list.createTime }</td>
					<td>
						<%--<a class="search-submit" href="javascript:vip.list.del({id : '${list.id }' , width : 600 , height : 500});">删除</a>--%>
						<a class="search-submit" href="javascript:vip.list.aoru({id : '${list.id }' , width : 600 , height : 500});">修改</a>
					</td>
				</tr>
				</tbody>
			</c:forEach>
			<tbody class="operations">
			<tr>
				<td colspan="10">
					<div id="page_navA" class="page_nav">
						<div class="con">
							<c:if test="${pager==''}">共${fn:length(dataList)}项</c:if>
							<c:if test="${pager!=null}">${pager}</c:if>
						</div>
					</div>
				</td>
			</tr>
			</tbody>
		</c:when>
		<c:otherwise>
			<tbody class="air-tips">
			<tr>
				<td colspan="9">
					<p>暂时没有符合要求的记录！</p>
				</td>
			</tr>
			</tbody>
		</c:otherwise>
	</c:choose>
</table>