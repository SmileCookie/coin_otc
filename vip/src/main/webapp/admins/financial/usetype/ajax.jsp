<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" style="width:100%">
	<thead>
		<tr>
			<th style="width:300px;">收支用途</th>
			<th>收支类型</th>
			<th>是否周转</th>
			<th>支出类型</th>
			<th>备注</th>
			<th>操作</th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${dataList!=null}">
				<tbody>
					<tr class="space">
						<td colspan="6">
						</td>
					</tr>
				</tbody>
			
				<c:forEach items="${dataList}" var="list" varStatus="statu">
					<tbody>
						<tr class="space">
							<td colspan="6"></td>
						</tr>
					</tbody>
					<tbody>
						<tr class="hd">
							<td colspan="6">
								<span>编号：${list.id} </span>
								<span>创建时间：<fmt:formatDate value="${list.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
								<span>创建管理员：【${list.aUser.id}，${list.aUser.admName }】</span>
							</td>
						</tr>
					</tbody>
				
					<tbody class="item_list" id="line_${list.id}">
						<tr>
							<td>
								${list.name }
							</td>
							<td>
								${list.isIn==1?"收入":"支出" }
							</td>
							<td>
								${list.turnRound==1?"周转账户":"-" }
							</td>
							<td>${list.type==1?"充值":list.type==2?"提现":list.type==3?"其他":"无" }</td>
							<td>
								${list.memo}
							</td>
							<td>
								<a href="javascript:vip.list.aoru({id:${list.id },height:425});">编辑</a>
								<a href="javascript:vip.list.del({id:${list.id }});">删除</a>
							</td>
						</tr>
					</tbody>
				</c:forEach>
				<tfoot>
					<tr>
						<td colspan="6">
							<div class="page_nav" id="pagin">
								<div class="con">
									<c:if test="${pager=='' }">共${itemCount }项</c:if>
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