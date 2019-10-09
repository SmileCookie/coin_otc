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
<script type="text/javascript">
/*
	var toggle = function(a, id, type){
		if(type == 0){
			$("tbody[name="+id+"]").each(function(){
				$(this).hide();
			});
			$("#" + a).text("展开查看明细");
			$("#" + a).attr("href", "javascript:toggle('"+a+"','"+id+"',1)");
		}else{
			$("tbody[name="+id+"]").each(function(){
				$(this).show();
			});
			$("#" + a).text("点击关闭明细");
			$("#" + a).attr("href", "javascript:toggle('"+a+"','"+id+"',0)");
		}
	}
*/
</script>
<table class="tb-list2" style="width:100%;table-layout: fixed;">
	<thead>
		<tr>
			<th><b>统计日期</b></th>
			<th><b>货币类型</b></th>
			<th><b>费用类型</b></th>
			<th><b>统计金额</b></th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${dataList!=null}">
				<tbody>
					<tr class="space">
						<td colspan="4">
						</td>
					</tr>
				</tbody>
				<c:forEach items="${dateTime }" var="time" varStatus="status">
					<tbody>
						<tr style="background-color:#cffffe">
							<td><b>${time }</b></td>
							<td colspan="3">
							</td>
						</tr>
					</tbody>
					<c:forEach items="${dataList}" var="list">
						<c:if test="${fn:indexOf(time, list.timestr) eq 0 }">
							<tbody  name="tbody${status.count }">
								<tr>
									<td></td>
									<td>${list.currency }</td>
									<td>
										<c:choose>
											<c:when test="${feeType eq 0}">全部</c:when>
											<c:when test="${feeType eq 1}">交易手续费</c:when>
											<c:when test="${feeType eq 2}">借贷手续费</c:when>
											<c:when test="${feeType eq 3}">提现手续费</c:when>
										</c:choose>
									</td>
									<td>
										<%--<c:choose>
											<c:when test="${'cny' eq fn:toLowerCase(list.currency)}">￥</c:when>
											<c:when test="${'btc' eq fn:toLowerCase(list.currency)}">฿</c:when>
											<c:when test="${'ltc' eq fn:toLowerCase(list.currency)}">Ł</c:when>
										</c:choose>--%>
										<fmt:formatNumber value="${list.amount }" pattern="0.000000##"/>
									</td>
								</tr>
							</tbody>
						</c:if>
					</c:forEach>
				</c:forEach>
			</c:when>
		</c:choose>
	<tbody>
		<tr class="space">
			<td colspan="4">
			</td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td><b>合计</b></td>
			<c:forEach items="${totalAmount}" var="amount">
				<td><b>${amount.key}：<fmt:formatNumber value="${amount.value }" pattern="0.000000##"/></b></td>
			</c:forEach>
		</tr>
	</tfoot>
</table>