<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<table class="tb-list2" id="ListTable">
	<thead>
		<tr> 
			<th>编号</th>
			<th>日期</th>
			<th>名称</th>
			<th>地址</th>
			<th>操作</th>
		</tr>
	</thead>
	<c:choose>
	 <c:when test="${fn:length(dataList)>0 }">
		<c:forEach items="${dataList }" var="list">
				<tbody>
					<tr class="space">
						<td colspan="5"></td>
					</tr>
				</tbody>
				<tbody class="item_list">
				<tr>
					<td>
						${list.myId}
					</td>
					<td>
						${list.date}
					</td>
					<td>
						${list.name }		
					</td>
					<td>
						${list.address }
					</td>
					<td>
						<a href="javascript:confirmDel('${list.myId}');">删除</a> &nbsp;
					</td>
				</tr>
				</tbody>
			
			</c:forEach>
			<tfoot>
	        <tr>
	          <td colspan="5">
		         <div class="page_nav" id="pagin">
                     <div class="con">
		                ${pager}
		         	 </div>
                 </div>
			  </td>
			</tr>
	   </tfoot>
	   </c:when>
	   <c:otherwise>
			<tbody>
				<tr>
					<td colspan="5">
						<p>暂时没有符合要求的记录！</p>
					</td>
				</tr>
			</tbody>
	   </c:otherwise>
</c:choose>
</table>