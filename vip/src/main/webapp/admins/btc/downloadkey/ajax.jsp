<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" id="ListTable">
	<tr>
		<th >编号</th>
		<th >用户ID</th>
        <th >地址</th>
        <th >创建时间</th>
<!--         <th >是否认证</th> -->
        <th >是否删除</th>
        <th class="commodity_action">操作</th>
	</tr>
	<c:choose>
		<c:when test="${dataList!=null}">
		
			<c:forEach items="${dataList}" var="list">
			<tbody id="row${list.id }">
					
				<tr class="item_list_bd item_list_bgcolor">
					<td class="commodity_info">
						${list.id }
					</td>
					<td class="commodity_info">
						<div class="txt">
							${list.userId}
							<%--<a href="javascript:showUser('${list.userId}')" style="font-weight: bold;color:green;" id="text_${list.userId }">${list.userName}</a>--%>
						</div>
					</td>
					<td class="b_gray">
						<a href="${coint.web }${list.address }" target="_blank">${list.address }</a>
					</td>
					<td class="commodity_price b_gray">
						<fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${list.createTime}" />
					</td>
<!-- 					<td> -->
<!-- 						<c:if test="${list.auth==1 }">已认证</c:if> -->
<!-- 						<c:if test="${list.auth==0 }">-</c:if> -->
<!-- 					</td> -->
					<td>
						<c:if test="${list.isDeleted==1 }"><font color="grey">已删除</font></c:if>
						<c:if test="${list.isDeleted==0 }">-</c:if>
					</td>
					<td class="commodity_action br_color">
						<c:if test="${logAdmin.rid==1}">
							<c:if test="${list.isDeleted == 0 }">
							</c:if>
						</c:if>
					</td>
				</tr>
			</tbody>
			
			
			</c:forEach>
			<tbody class="operations">
				<tr>
					<td colspan="6">
						<div id="page_navA" class="page_nav">
							<div class="con">
								<c:if test="${fn:length(pager)==0}">共${itemCount }项</c:if>
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
					<td colspan="6">
						<p>暂时没有符合要求的记录！</p>
					</td>
				</tr>
			</tbody>
		</c:otherwise>
	</c:choose>
</table>