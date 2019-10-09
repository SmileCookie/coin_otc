<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<script type="text/javascript">
$(function(){
	$("a[mytitle]").each(function(){
		  $(this).UiTitle(); 
	});
});
</script>
<table class="tb-list2" id="ListTable" style="width: 100%">
	<thead>
		<tr>
			<th>申请时间</th>
			<th>用户ID</th>
			<th>申请操作</th>
			<th>申请修改</th>
			<th>修改前</th>
			<th>验证旧手机/Google</th>
			<th>状态</th>
			<th>操作</th>
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
							<td>${list.addTimeShow }</td>
							<td>
								${list.userId}
								<%--<a href="javascript:showUser('${list.userId}')" style="font-weight: bold;color:green;" id="text_${list.id }">${list.userName}</a>--%>
							</td>
							<td>
								<c:choose>
									<c:when test="${list.type eq 1 }">更改手机</c:when>
									<c:when test="${list.type eq 2 }">更改Google</c:when>
									<c:otherwise>挂失手机</c:otherwise>
								</c:choose>
							</td>
							<td>
								<c:choose>
									<c:when test="${list.type eq 1 }">${list.info }</c:when>
									<c:otherwise>${list.infoShow }</c:otherwise>
								</c:choose>
							</td>
							<td>
								<c:choose>
									<c:when test="${list.type eq 1 }">${list.beforeInfo }</c:when>
									<c:otherwise>${list.beforeInfoShow }</c:otherwise>
								</c:choose>
							</td>
							<td>
								<c:choose>
									<c:when test="${list.verifyOldInfo eq 0 }">否</c:when>
									<c:when test="${list.verifyOldInfo eq 1 }">是</c:when>
								</c:choose>
							</td>
							<td>
								<c:choose>
									<c:when test="${list.status eq 0 }">待审核</c:when>
									<c:when test="${list.status eq 1 }">已拒绝</c:when>
									<c:when test="${list.status eq 2 }">已通过</c:when>
									<c:when test="${list.status eq 3 }">已撤消</c:when>
								</c:choose>
							</td>
							<td>
								<c:if test="${list.status eq 0 }"><a href="javascript:verify('${list.id}')">我要审核</a></c:if>
								<c:if test="${list.status > 0 }"><a href="javascript:show('${list.id}')">查看详情</a></c:if>
								<c:if test="${list.status eq 0 and list.type eq 3 and list.type eq 4}"><a href="javascript:compare('${list.id}')">对比</a></c:if>
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