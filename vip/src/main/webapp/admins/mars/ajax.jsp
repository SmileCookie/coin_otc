<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" id="ListTable" style="width: 100%">

	<tr class="hd">
		<%--<h1 style="line-height: 60px;background-color: #96aeb8;margin-bottom: 20px; text-align: center;">统计报表(
			<jsp:useBean id="now" class="java.util.Date" scope="page"/>
			<fmt:formatDate value="${beginTime}" pattern="yyyy-MM-dd " />至
			<fmt:formatDate value="${endTime}" pattern="yyyy-MM-dd " />
			)
		</h1>--%>
	</tr>
	<tr>
		<th >序号</th>
		<th >交易日期</th>
		<th >币种类型</th>
		<th >摘要</th>
		<th >收入</th>
		<th >支出</th>
		<th >变动位置</th>
		<th >变动类型</th>
		<th >公司资金变动类型</th>
		<th >统计类型</th>
		<th >录入人</th>
		<th >审核人</th>
		<th >审核时间</th>
		<th >备注</th>
		<th >操作</th>
	</tr>
	<c:choose>

		<c:when test="${dataList!=null}">
			<c:forEach items="${dataList}" var="list" varStatus="statu">
				<tr class="item_list_bd item_list_bgcolor">
					<td>${statu.index + 1}</td>
					<td><fmt:formatDate value="${list.transDate}" pattern="yyyy-MM-dd HH:mm"/></td>
					<td>${list.fundsTypeName }</td>
					<td>${list.summary }</td>
					<td><fmt:formatNumber value="${list.income }" pattern="0.000######"/></td>
					<td><fmt:formatNumber value="${list.expense }" pattern="0.000######"/></td>
					<td>
						<c:if test="${list.changePosition==1}">
							平台
						</c:if>
						<c:if test="${list.changePosition==2}">
							钱包
						</c:if>
					</td>
					<td>
						<c:if test="${list.changeType==1}">
							冷到其他
						</c:if>

						<c:if test="${list.changeType==2}">
							其他到热提
						</c:if>

						<c:if test="${list.changeType==3}">
							后台充值
						</c:if>

						<c:if test="${list.changeType==4}">
							其他到冷
						</c:if>
						<c:if test="${list.changeType==5}">
							资金划转
						</c:if>
						<c:if test="${list.changeType==6}">
							后台扣除
						</c:if>
						<c:if test="${list.changeType==7}">
							用户充值
						</c:if>
						<c:if test="${list.changeType==8}">
							用户提现
						</c:if>
						<c:if test="${list.changeType==9}">
							热提到其他
						</c:if>
						<c:if test="${list.changeType==10}">
							系统分发
						</c:if>
						<c:if test="${list.changeType==11}">
							ABCDEF发行
						</c:if>
						<c:if test="${list.changeType==12}">
							兑换ABCDEF
						</c:if>
						<c:if test="${list.changeType==13}">
							其他
						</c:if>
					</td>
					<td>
						<c:if test="${list.companyChangeType==1}">
							公司资金减少（T)
						</c:if>
						<c:if test="${list.companyChangeType==2}">
							公司资金增加（T）
						</c:if>
						<c:if test="${list.companyChangeType==3}">
							公司资金减少（F）
						</c:if>
						<c:if test="${list.companyChangeType==4}">
							公司资金增加（F）
						</c:if>
						<c:if test="${list.companyChangeType==5}">
							不影响公司资金
						</c:if>
					</td>
					<td>
						<c:if test="${list.accountingType==1}">
							资金减少（T)
						</c:if>
						<c:if test="${list.accountingType==2}">
							资金增加（T）
						</c:if>
						<c:if test="${list.accountingType==3}">
							资金减少（F）
						</c:if>
						<c:if test="${list.accountingType==4}">
							资金增加（F）
						</c:if>
						<c:if test="${list.accountingType==5}">
							不影响本表
						</c:if>
					</td>
					<td>${list.operator }</td>
					<td>${list.auditior }</td>
					<td><fmt:formatDate value="${list.checkDate}" pattern="yyyy-MM-dd HH:mm"/></td>
					<td>${list.comment }</td>
					<td>
						<c:if test="${list.checkState==1}">
							<font color="blue">已审核</font>
							<br/>
						</c:if>
						<c:if test="${list.checkState==0 }">
							<a href="javascript:check(${list.id});">审核</a>
							<a href="javascript:setUpv(${list.id});">修改</a>
							<br/>
						</c:if>
					</td>
				</tr>
				</tbody>
			</c:forEach>
			<tbody class="operations">
			<tr>
				<td colspan="15" >
					<div id="page_navA" class="page_nav" style="text-align: right">
						<div class="con">
							<c:if test="${pager==''}">共${fn:length(dataList)}项</c:if>
							<c:if test="${pager!=null}">${pager}</c:if>
						</div>
					</div>
				</td>
			</tr>
			</tbody>
		</c:when>
		<c:otherwise>
			<tbody class="air-tips">
			<tr>
				<td colspan="15">
					<p>暂时没有符合要求的记录！</p>
				</td>
			</tr>
			</tbody>
		</c:otherwise>
	</c:choose>
</table>