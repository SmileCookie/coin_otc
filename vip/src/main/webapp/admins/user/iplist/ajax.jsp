<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<table class="tb-list2" style="width: 100%">
	<thead>
		<tr>
			<th width="360">ip</th>
			<th>每分钟限制次数</th>
			<th>创建时间</th>
			<th>最后修改用户</th>
			<th>操作</th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${dataList!=null}">
				<c:forEach items="${dataList}" var="list">
					<tbody>
						<tr class="space">
							<td colspan="4"></td>
						</tr>
					</tbody>
				
				
					<tbody class="item_list" id="line_${list.id}">
						<tr>
							<td>${list.ip}</td>
							<td>${list.limit}</td>
							<td>${list.createTime} </td>
							<td>${list.opUserName} </td>
							<td>
								<a href="javascript:vip.list.aoru({id : ${list.id } , width : 600 , height : 660});">修改</a>
								<br/>
								<a href="javascript:vip.list.del({id : '${list.id }'});">删除</a>
								<br/>
							</td>
						</tr>
					</tbody>
				</c:forEach>
				<tfoot>
					<tr>
						<td colspan="4">
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
						<td colspan="4">
							<p>暂时没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</c:otherwise>
		</c:choose>

</table>