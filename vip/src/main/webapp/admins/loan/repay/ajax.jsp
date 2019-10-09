<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<table class="tb-list2">
	<thead>
		<tr>
			<th></th>
			<th>投资用户ID</th>
			<th>应还日期</th>
			<th>本息</th>
			<th>服务费</th>
			<th>还款状态</th>
			<th>还款日期</th>
			<th>逾期利息/天数</th>
			<th>操作</th>
		</tr>
	</thead>
	<c:choose>
		<c:when test="${dataList != null}">
			<tbody>
				<tr class="space">
					<td colspan="9">
						<div class="operation">
							<input type="checkbox" name="checkbox" class="DeleAllSel" id="delAll" style="display:none;">
							<label id="ck_delAll" class="checkbox" onclick="selectAll()" style="width: 50px;"><em>全选</em></label>
							<a class="AButton yellow_button manyJisuan" isAll="false" href="javascript:tongji(false)">统计选中金额</a>
							|
							<a class="AButton yellow_button manyJisuan" href="javascript:tongji(true);">统计全部金额</a>
							<span style="margin-left: 30px;font-size: 18px;color: #ff0000;display: none;">本息：<font id="total_1"></font></span>
							<span style="margin-left: 20px;font-size: 18px;color: #ff0000;display: none;">利息：<font id="total_2"></font></span>
							<span style="margin-left: 30px;font-size: 18px;color: #ff0000;display: none;">手续费：<font id="total_3"></font></span>
						</div>
					</td>
				</tr>
			</tbody>
			<c:forEach items="${dataList}" var="list" varStatus="statu">
				<tbody>
					<tr class="space">
						<td colspan="9"></td>
					</tr>
				</tbody>
				<tbody> 
					<tr class="bd">
						<td><input type="checkbox" style="display:none;" value="${list.id}" id="${statu.index}" class="checkItem"/><label id="ck_${statu.index}" class="checkbox" onclick="changeCheckBox('${statu.index}')"></label></td>
						<td>${list.outUserId }</td>
						<td><fmt:formatDate value="${list.forecastDate }" pattern="yyyy-MM-dd HH:mm"/></td>
						<td style="color: #5AAB04;">
							<c:choose>
								<c:when test="${(list.liXi + list.benJin) le 0}">
									免息
								</c:when>
								<c:otherwise>
									${list.ft.tag }<fmt:formatNumber value="${list.liXi + list.benJin }" pattern="0.00####"/>
								</c:otherwise>
							</c:choose>
						</td>
						<td style="color: #5AAB04;">
							${list.ft.tag }<fmt:formatNumber value="${list.lxFwf }" pattern="0.00####"/>
						</td>
						<td>
							${list.repayStatus.value}
						</td>
						<td>
							<c:choose>
								<c:when test="${list.actureDate != null}">
									<fmt:formatDate value="${list.actureDate }" pattern="yyyy-MM-dd HH:mm"/>
								</c:when>
								<c:otherwise>
									-
								</c:otherwise>
							</c:choose>
						</td>
						<td style="color: #F37800;">
							<a class="des" mytitle="滞纳金：借款人逾期还息时，将每天产生利息*0.5%的滞纳金,此处最多显示到小数位第五位。">${list.ft.tag }<fmt:formatNumber pattern="0.0####" value="${list.yuQiLiXi}"/></a> / ${list.yuQiDayShow }
						</td>
						<td id="actionTd">
							-
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
						<p>暂时没有符合要求的记录！</p>
					</td>
				</tr>
			</tbody>
		</c:otherwise>
	</c:choose>
</table>
