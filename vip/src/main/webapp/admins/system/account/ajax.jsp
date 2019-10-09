<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<table class="tb-list2" id="ListTable">
	<thead>
		<tr>
			<th>编号</th>
			<th>发送邮箱</th>
			<th>HOST/端口</th>
			<th>账号/密码</th>
			<th>状态</th>
			<th>操作</th>
		</tr>
	</thead>
	<c:choose>
		<c:when test="${dataList!=null}">
			<c:forEach items="${dataList}" var="list" varStatus="stat">
				<tbody>
					<tr class="space">
						<td colspan="6"></td>
					</tr>
				</tbody>
				<tbody class="item_list">
				<tr>
					<td>
						${list.myId}
					</td>
					<td>
						${list.fromAddr}
					</td>
					<td>
						${list.mailServerHost} / ${list.mailServerHost}
					</td>
					<td>
						${list.emailUserName} / **
					</td>
					<td>
						${list.status}
					</td>
					<td>
						<a href="javascript:vip.list.del({id : '${list.id }'});">删除</a> &nbsp;
						<a href="javascript:vip.list.aoru({id : '${list.myId}', width : 500 , height : 410});">修改</a>
						
					</td>
				</tr>
				</tbody>
			
			</c:forEach>
			<tfoot>
	        <tr>
	          <td colspan="6">
		         <div class="page_nav" id="pagin">
                     <div class="con">
		                <span class="page_num">共${itemCount}项</span>
		                <c:if test="${pager!=null}">${pager}</c:if>
		         	 </div>
                 </div>
			  </td>
			</tr>
	   </tfoot>
		</c:when>
			
		<c:otherwise>
			<tbody>
				<tr>
					<td colspan="6">
						<p>暂时没有符合要求的记录！</p>
					</td>
				</tr>
			</tbody>
		</c:otherwise>
	</c:choose>
</table>