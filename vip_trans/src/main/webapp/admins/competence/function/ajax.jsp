<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<table class="tb-list2" id="ListTable">
	<thead>
		<tr>
			<th>编号</th>
			<th>名称</th>
			<th>路径</th>
			<th>功能描述</th>
			<th>视图</th>
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
				<tbody>
					<tr class="hd">
						<td colspan="6">
							<span>组编号：${stat.index+1} </span>
							<span>功能描述：${list.value.des}</span>
							<span>${list.key}</span>
						</td>
					</tr>
				</tbody>
			
				<c:forEach items="${list.value.functions}" var="fs" varStatus="sstat">
					<tbody class="item_list">
					<tr>
						<td>
							${sstat.index + 1}
						</td>
						<td>
							${fs.vc.name}
						</td>
						<td>
							${fs.vc.path}
						</td>
						<td>
							${fs.des}
						</td>
						<td>
							${fs.vc.viewerPath}
						</td>
						<td>
							<a href="javascript:vip.list.aoru({id : '${fs.vc.name }', width : 600 , height : 660});">修改</a>
						</td>
					</tr>
					</tbody>
				</c:forEach>
			
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