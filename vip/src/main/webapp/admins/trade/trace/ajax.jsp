<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<style>
    .green{
        color:green !important;
    }

    .red{
        color:red !important;;
    }
</style>
<table class="tb-list2" style="width: 100%">
	<thead>
		<tr>
			<th>委托单号</th>
			<th>委托用户ID</th>
			<th>委托类型</th>
			<th>委托单价</th>
			<th>委托数量</th>
			<th>委托总金额</th>
			<th>已成交数量</th>
			<th>已成交金额</th>
			<th>状态</th>
			<th>委托来源</th>
			<th>委托时间</th>
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
				
				
					<tbody class="item_list" id="line_${list.entrustId}">
						<tr><td>${list.entrustId}</td>
							<td>

								g

							</td>
							<td>
								<c:if test="${list.types==1}"><font class="green">买</font></c:if>
								<c:if test="${list.types==0}"><font class="red">卖</font></c:if>
							</td>
							<td>${list.unitPrice} </td>
							<td>${list.numbers} </td>
							<td>${list.totalMoney} </td>
							<td><fmt:formatNumber value ="${list.completeNumber.doubleValue()}" pattern="###0.00000000#"/></td>
							<td> <fmt:formatNumber value ="${list.completeTotalMoney.doubleValue()}" pattern="###0.00000000#"/></td>
							<td>
								<c:if test="${list.status==3 || list.status==0}">待成交</c:if>
								<c:if test="${list.status==2}"><font class="green">已成交</font></c:if>
								<c:if test="${list.status==1}"><font class="red">取消</font></c:if>
							</td>
							<td> 
								<c:if test="${list.webId==5}">APP</c:if>
								<c:if test="${list.webId==6}">API</c:if>
								<c:if test="${list.webId==8}">网站</c:if>
							</td>
							<td> 
							<jsp:useBean id="dateValue" class="java.util.Date"/>
							<jsp:setProperty name="dateValue" property="time" value="${list.submitTime}"/>
							
							<fmt:formatDate value="${dateValue}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
							<td>
								<a href="javascript:vip.list.aoru({id : ${list.entrustId },otherParam:'&tab=${tab }' , width : 800 , height : 660});">明细</a>
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