<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<style type="text/css">
.btn2 a:hover {
    background: none repeat scroll 0 0 #6DC03C;
    color: #FFFFFF;
}
.btn2 a {
    background: none repeat scroll 0 0 #8DC03C;
    border-radius: 3px 3px 3px 3px;
    color: #FFFFFF;
    height: 31px;
    line-height: 32px;
    padding: 5px 10px;
}
.btn2 a.red {
    background: none repeat scroll 0 0 #E55E48;
}
.btn2 a.red:hover {
    background: none repeat scroll 0 0 #C55E48;
}
</style>
<table class="tb-list2" style="width:100%;table-layout: fixed;">
	<thead>
		<tr>
			<th>账户名称</th>
			<th>资金余额</th>
			<th>结算类型</th>
			<th>备注</th>
			<th>操作</th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${dataList!=null}">
				<tbody>
					<tr class="space">
						<td colspan="5">
						</td>
					</tr>
				</tbody>
			
				<c:forEach items="${dataList}" var="list" varStatus="statu">
					<tbody>
						<tr class="space">
							<td colspan="5"></td>
						</tr>
					</tbody>
					<tbody>
						<tr class="hd">
							<td colspan="5">
								<span>编号：${list.id} </span>
								<span>创建时间：<fmt:formatDate value="${list.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
								<span>创建管理员：【${list.aUser.id}，${list.aUser.admName }】</span>
							</td>
						</tr>
					</tbody>
				
					<tbody class="item_list" id="line_${list.id}">
						<tr>
							<td>
								${list.account.name }
							</td>
							<td>
								<c:if test="${list.amount > 0 }">
									<font color="red">
										<fmt:formatNumber value="${list.funds }" pattern="#,000.0000##"/>
									</font>
								</c:if>
								<c:if test="${list.amount <= 0 }">-</c:if>
							</td>
							<td>
								<c:if test="${fn:endsWith(list.groupTime, '00') }"><span style="color: green;">上班结算</span></c:if>
								<c:if test="${fn:endsWith(list.groupTime, '000') }"><span style="color: green;">上班结算</span></c:if>
								<c:if test="${fn:endsWith(list.groupTime, '24') }"><span style="color: red;">下班结算</span></c:if>
							</td>
							<td style="word-break: break-all; WORD-WRAP: break-word;">
								${fn:length(list.memo)>0?list.memo:"-"}
							</td>
							<td>
								<a href="javascript:memo(${list.id });">添加备注</a>
							</td>
						</tr>
					</tbody>
				</c:forEach>
				<tfoot>
					<tr>
						<td colspan="5">
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
						<td colspan="5">
							<p>没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</c:otherwise>
		</c:choose>

</table>