<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" style="width:100%;table-layout: fixed;">
	<thead>
		<tr>
			<th style="width:200px;">买家</th>
			<th style="width:200px;">卖家</th>
			<th>成交单价</th>
			<th>成交数量</th>
			<th>成交金额</th>
			<th>成交状态</th>
			<th>状态</th>
			<th style="width: 100px;">操作</th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="ture">
				<tbody>
					<tr class="space">
						<td colspan="8">
							<div class="operation">
							</div>
						</td>
					</tr>
				</tbody>
				
				
				
				<tfoot>
					<tr>
						<td colspan="10">
							<div class="page_nav" id="pagin">
								<div class="con">
									<c:if test="{pager==''}">共{itemCount}项</c:if>
									<c:if test="{pager!=null}">{pager}</c:if>
								</div>
							</div>
						 </td>
					</tr>
				 </tfoot>
			</c:when>
			<c:otherwise>
				<tbody class="air-tips">
					<tr>
						<td colspan="10">
							<p>没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</c:otherwise>
		</c:choose>

</table>