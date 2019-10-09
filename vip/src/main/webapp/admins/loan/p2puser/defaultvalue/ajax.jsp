<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%-- <input type="hidden" id="tab" name="tab" value="${tab }" /> --%>
<table class="tb-list2" style="width: 100%; table-layout: fixed;">
	<thead>
		<tr>
			<th>币种</th>
			<th>类别名称</th>
			<th>值</th>
			<th>备注</th>
			<th>操作</th>
		</tr>
	</thead>
	<c:choose>
		<c:when test="${dataList != null}">
			<c:forEach items="${dataList}" var="list" varStatus="statu">
				<tbody>
					<tr class="space">
						<td colspan="4">
						
						</td>
					</tr>
				</tbody>
					<tbody>
						<tr class="hd">
							<td colspan="5">
								<span>编号：${list.id} </span>
							</td>
						</tr>
					</tbody>
				<tbody class="item_list" id="line_${list.id}">
					<tr>
						<td>${list.keyName }</td>
						<td>${list.typeName }</td>
						<td style="color: orange;">${list.valueName }</td>
						<td>${list.reMarks }</td>
						<td>
							<a href="javascript:updateDeFaLimit(${list.id },'${list.typeName }','${list.keyName }');"> 修改</a>
							<a href="javascript:deleteDeFaLimit(${list.id },'${list.typeName }','${list.keyName }');"> 删除</a>
						</td>
					</tr>
				</tbody>
			</c:forEach>
			<tfoot>
				<tr>
					<td colspan="5">
						<div class="page_nav" id="pagin">
							<div class="con">
								<c:if test="${pager==''}">共${itemCount}项</c:if>
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
					<td colspan="5">
						<p>没有符合要求的记录！</p>
					</td>
				</tr>
			</tbody>
		</c:otherwise>
	</c:choose>
</table>