<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" style="width:100%;table-layout: fixed;">
	<thead>
		<tr>
			<th>账户</th>
			<th>收支类型</th>
			<th>金额</th>
			<th>用途来源</th>
			<th>用户ID</th>
			<th>备注</th>
			<th>余额</th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${dataList!=null}">
				<tbody>
					<tr class="space">
						<td colspan="7">
						</td>
					</tr>
				</tbody>
				
				<tbody class="operations">
					<tr>
						<td colspan="7">
							<div class="operation">
								<input type="checkbox" name="checkbox" class="DeleAllSel" id="delAll" style="display:none;">
								<label id="ck_delAll" class="checkbox" onclick="selectAll()" style="width: 50px;"><em>全选</em></label>
								<c:if test="${logAdmin.rid==1 || logAdmin.rid==6}">
									<a class="AButton yellow_button manyJisuan" isAll="false" href="javascript:tongji(false)">统计选中金额</a>
									
									<span style="margin-left: 30px;font-size: 18px;color: #ff0000;display: none;">收入：<font id="inMoney"></font></span>
									<span style="margin-left: 20px;font-size: 18px;color: #ff0000;display: none;">支出：<font id="outMoney"></font></span>
								</c:if>
							</div>
						</td>
					</tr>
				</tbody>
			
				<c:forEach items="${dataList}" var="list" varStatus="statu">
					<tbody>
						<tr class="space">
							<td colspan="7"></td>
						</tr>
					</tbody>
					<tbody>
						<tr class="hd">
							<td colspan="7">
								<input type="checkbox" style="display:none;" value="${list.id}" id="${statu.index}" class="checkItem"/><label id="ck_${statu.index}" class="checkbox" onclick="changeCheckBox('${statu.index}')"></label>
								<span>编号：${list.id} </span>
								<span>创建时间：<fmt:formatDate value="${list.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
								<span>创建管理员：【${list.aUser.id}，${list.aUser.admName }】</span>
								<span>IP：<a href="http://www.ip138.com/ips138.asp?ip=${list.ip }&action=2" target="_blank">${list.ip}</a></span>
							</td>
						</tr>
					</tbody>
				
					<tbody class="item_list" id="line_${list.id}">
						<tr>
							<td>
								${list.account.name}
							</td>
							<td>
								${list.isIn }
							</td>
							<td>
							 	<c:if test="${list.funds>0 }">
									<c:if test="${list.isIn=='收入' }">
									 	<font color="green">+<fmt:formatNumber value="${list.funds }" pattern="##,##0.00######"/></font>
									</c:if>
									<c:if test="${list.isIn=='支出' }">
									 	<font color="orange">-<fmt:formatNumber value="${list.funds }" pattern="##,##0.00######"/></font>
									</c:if>
							 	</c:if>
							 	<c:if test="${list.funds==0 }">-</c:if>
							 	<c:if test="${list.funds<0 }">
							 		<font color="orange"><fmt:formatNumber value="${list.funds }" pattern="##,##0.00######"/></font>
							 	</c:if>
							 	<c:if test="${list.fundsComm!=0}">
						 			<font color="${list.fundsComm>0?'green':list.fundsComm<0?'orange':'' }"><br/>手续费：<fmt:formatNumber value="${list.fundsComm }" pattern="##,##0.00######" /></font>
						 		</c:if>
							</td>
							<td>
								${list.useType.name}
								<c:if test="${list.connectionId>0 }">
									<br/>
									编号：${list.connectionId }
								</c:if>
							</td>
							<td>
								<%--${fn:length(list.userId)>0?list.userId:"-"}--%>
								${list.userId}
							</td>
							<td style="word-break: break-all; WORD-WRAP: break-word;">
								${fn:length(list.memo)>0?list.memo:"-"}
							</td>
							<td>
								<fmt:formatNumber value="${list.balance }" pattern="##,##0.00######" />
							</td>
						</tr>
					</tbody>
				</c:forEach>
				<tfoot>
					<tr>
						<td colspan="7">
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
						<td colspan="7">
							<p>没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</c:otherwise>
		</c:choose>

</table>