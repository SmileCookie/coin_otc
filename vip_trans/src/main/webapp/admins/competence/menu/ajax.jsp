<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<table class="tb-list2" id="ListTable">
	<thead>
		<tr>
			<th>编号</th>
			<th>名称</th>
			<th>子菜单</th>
			<th>描述</th>
			<th>操作</th>
		</tr>
	</thead>
	<c:choose>
		<c:when test="${dataList!=null}">
			<c:forEach items="${dataList}" var="list">
				<tbody class="item_list" id="line_${list.id}">
				<tr>
					<td>
						${list.id}
					</td>
					<td>
						${list.name}
					</td>
					<td>
						
					</td>
					<td>
						${list.des}
					</td>
					<td>
						<a href="javascript:vip.list.del({id : '${list.id }'});">删除</a> &nbsp;
						<a href="javascript:vip.list.aoru({id : '${list.id }', width : 600 , height : 300});">修改</a>
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