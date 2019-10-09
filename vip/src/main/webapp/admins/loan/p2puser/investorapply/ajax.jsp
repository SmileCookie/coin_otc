<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<input type="hidden" id="tab" name="tab" value="${tab }" />
<input type="hidden" id="page" name="page" value="${page }" />
<table class="tb-list2" style="width:100%;table-layout: fixed;">
	<thead>
		<tr>
			<th style="width:240px;">用户</th>
			<th>姓名</th>
			<th>电话</th>
			<th>投资币种</th>
			<th>投资金额</th>
			<th>投资周期</th>
			<th>预期放贷年化利率</th>
			<th>风险担保</th>
			<th>申请时间</th>
			<th>放贷状态</th>
			<th style="width: 80px;">操作</th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${dataList!=null}">
				<c:forEach items="${dataList}" var="list" varStatus="statu">
					<tbody class="item_list" id="line_${list.userId}">
						<tr>
							<td>
								${list.userName}
								<%--<a href="javascript:showUser('${list.userId}')" style="font-weight: bold;color:green;" id="text_${list.userId }">${list.userName}</a>--%>
							</td>
							<td>${list.name }</td>
							<td>${list.phone }</td>
							<td>${list.investmentCurrency }</td>
							<td>${list.investmentAmount }</td>
							<td>${list.investmentCycle }</td>
							<td>${list.investmentRate }</td>
							<td>${list.guarantee }</td>
							<td><fmt:formatDate value="${list.date }" pattern="yyyy-MM-dd HH:mm:ss" /></td>
							<td>
								<c:if test="${list.p2pUser.loanOutStatus==0 }">已关闭</c:if>
								<c:if test="${list.p2pUser.loanOutStatus==1 }">已开启</c:if>
							</td>
							<td>
<%-- 								<c:if test='${list.p2pUser.loanOutStatus!=1}'> --%>
								<a href="javascript:modifyLoanOutStatus('${list.p2pUser.userId}',${list.p2pUser.loanOutStatus});">
									<c:if test="${list.p2pUser.loanOutStatus==0 }">开启放贷</c:if>
									<c:if test="${list.p2pUser.loanOutStatus==1 }">关闭放贷</c:if>
								</a>
								<c:if test="${list.p2pUser.loanOutStatus==0 && list.status!=3 }">
								<br/>
								<a href="javascript:rejectInvStatus('${list.p2pUser.userId}');">拒绝申请</a>
								</c:if>
								<%-- <br/>
								<a href="javascript:modifyLoanInStatus('${list.p2pUser.userId}',${list.p2pUser.loanInStatus});">
									<c:if test="${list.p2pUser.loanInStatus==0 }">允许被借入</c:if>
									<c:if test="${list.p2pUser.loanInStatus==1 }">禁止被借入</c:if>
								</a> --%>
								
								<br/>
								<a href="javascript:userLend('${list.p2pUser.userId }',${list.p2pUser.userLend });"> 修改投资限额 </a>
<%-- 								</c:if> --%>
							</td>
						</tr>
					</tbody>
				</c:forEach>
				<tfoot>
					<tr>
						<td colspan="9">
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
						<td colspan="9">
							<p>没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</c:otherwise>
		</c:choose>
</table>