<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" style="width:100%">
	<thead>
		<tr>
			<th style="width:300px;">用户信息</th>
			<th>创建时间</th>
			<th>IP</th>
			<th>城市</th>
			<th>登录描述</th>
			<th>是否授权</th>
			<th>操作</th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${dataList!=null}">
			
				<c:forEach items="${dataList}" var="list" varStatus="statu">
					<tbody class="item_list" id="line_${list.id}">
						<tr>
							<td>
								<div class="pic_info">
									${list.userId}
								<%--<div class="txt">${list.userId }<a href="javascript:showUser('${list.user.id}')" style="font-weight: bold;color:green;margin-left: 20px;" id="text_${list.user.id }">${list.user.userName}</a></div>--%>
								</div>
							</td>
							<td><fmt:formatDate value="${list.date}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
							<td>
								<a href="http://www.ip138.com/ips138.asp?ip=${list.ip }&action=2" target="_blank">${list.ip}</a>
							</td>
							<td>
								${list.city }
							</td>
							<td>
								${list.describe}
							</td>
							<td>
								${list.checked==1?"已授权":"" }
							</td>
							<td>
								-
							</td>
						</tr>
					</tbody>
				</c:forEach>
				<tfoot>
					<tr>
						<td colspan="6">
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
						<td colspan="6">
							<p>没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</c:otherwise>
		</c:choose>

</table>