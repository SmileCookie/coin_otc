<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" style="width:100%;table-layout: fixed;">
	<thead>
		<tr>
			<th>借入用户</th>
			<th>借出用户</th>
			<th>借贷类型</th>
			<th>借贷资金/已还</th>
			<th>借贷利率</th>
			<th>状态</th>
			<th>免息券</th>
			<th>转让状态</th>
			<th style="width: 100px;">操作</th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${dataList!=null}">
				<tbody>
					<tr class="space">
						<td colspan="9">
							<div class="operation">
								<input type="checkbox" style="display:none;" id="delAll" class="DeleAllSel" name="checkbox"/>
								<label style="width: 50px;" onclick="selectAll()" class="checkbox" id="ck_delAll"><em>全选</em></label>
								<a class="AButton yellow_button manyJisuan" isAll="false" href="javascript:tongji(false)">统计选中金额</a>
								|
								<a class="AButton yellow_button manyJisuan" href="javascript:tongji(true);">统计全部金额</a>
								<span style="margin-left: 30px;font-size: 18px;color: #ff0000;display: none;">总金额：<font id="totalM"></font></span>
								<span style="margin-left: 20px;font-size: 18px;color: #ff0000;display: none;">已还金额：<font id="totalM2"></font></span>
							</div>
						</td>
					</tr>
				</tbody>
				<c:forEach items="${dataList}" var="list" varStatus="statu">
					<tbody>
						<tr class="space">
							<td colspan="9">
							</td>
						</tr>
					</tbody>
					<tbody>
						<tr class="hd">
							<td colspan="9">
								<input type="checkbox" style="display:none;" value="${list.id}" id="${statu.index+2017}" class="checkItem"/><label id="ck_${statu.index+2017}" class="checkbox" onclick="changeCheckBox('${statu.index+2017}')"></label>
								<span>记录编号：${list.id} </span>
								<span>借款时间：<fmt:formatDate value="${list.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/></span>
								<span>还款时间：<fmt:formatDate value="${list.repayDate }" pattern="yyyy-MM-dd HH:mm:ss"/></span>
							</td>
						</tr>
					</tbody>
				
					<tbody class="item_list" id="line_${list.id}">
						<tr>
							<td>
								<div class="">
									<div class="pic">
										<a href="javascript:showUser('${list.inUserId}')"></a>
									</div>
									<div class="txt">
										${list.inUserName}
										<%--<a href="javascript:showUser('${list.inUserId}')" style="font-weight: bold;color:green;" id="text_${list.inUserId }">${list.inUserName}</a>--%>
										<br/>
										<font style="">
										</font>
									</div>
								</div>
							</td>
							<td>
								<div class="">
									<div class="pic">
										<a href="javascript:showUser('${list.outUserId}')"></a>
									</div>
									<div class="txt">
										${list.outUserName}
										<%--<a href="javascript:showUser('${list.outUserId}')" style="font-weight: bold;color:green;" id="text_${list.outUserId }">${list.outUserName}</a>--%>
										<br/>
										<font style="">
										</font>
									</div>
								</div>
							</td>
							<td>
								${list.isIn?"借入":"投资" }
							</td>
							<td>
								<font color="">${list.getFt().unitTag }：<fmt:formatNumber value="${list.hasRepay+list.amount }" pattern="0.00######"/></font>
								/
								<font color="green"><fmt:formatNumber value="${list.hasRepay }" pattern="0.00######"/></font>
							</td>
							<td>
								<fmt:formatNumber value="${list.rate*100 }" pattern="0.00##"/> %
							</td>
							<td>
								<font style="color: ${list.recordStatus.color}">${list.recordStatus.value }</font>
								<%--需要平仓的时候显示按钮 --%>
								<c:if test="${list.status==3 }"><a href="javascript:force(${list.inUserId })">强平</a></c:if>
								<c:if test="${list.status==3 && !list.p2pUser.repayLock}"><a href="javascript:continueLoan(${list.id })">续借</a></c:if>
							</td>
							<td>
								<c:choose>
				            		<c:when test="${list.withoutLxDays gt 0}">
				            			<table style="width:100%;height:45px;border:none;">
				            				<tr>
				            					<td style=" height:20px; line-height:20px; border:none;border-bottom:1px solid #ddd">
				            						<i class="green">${list.balanceWithoutLxDays }</i>/<i class="red">${list.withoutLxDays }</i></div>
				            					</td>
				            				</tr>
				            				<tr>
				            					<td style=" height:20px; line-height:20px; border:none">
				            						<%-- <c:if test="${list.fundType eq 1 }">￥</c:if>
				            						<c:if test="${list.fundType eq 2 }">฿</c:if>
				            						<c:if test="${list.fundType eq 3 }">Ł</c:if>
				            						<c:if test="${list.fundType eq 4 }">E</c:if>
				            						<c:if test="${list.fundType eq 5 }">e</c:if> --%>
				            						${list.getFt().unitTag }
				            						<fmt:formatNumber value="${list.withoutLxAmount }" pattern="0.0##"/>
				            					</td>
				            				</tr>
				            			</table>
				            		</c:when>
				            		<c:otherwise>
				            			-
				            		</c:otherwise>
				            	</c:choose>
							</td>
							<td>
								<c:choose>
				 					<c:when test="${list.tstatus == 0}">
				 						-
				 					</c:when>
				 					<c:otherwise>
				 						转让中
				 					</c:otherwise>
				 				</c:choose>
				 				<c:choose>
				 					<c:when test="${list.transferStartDate != null}">
				 						<fmt:formatDate value="${list.transferStartDate }" pattern="yyyy-MM-dd HH:mm"/>
				 					</c:when>
				 				</c:choose>
							</td>
							<td>
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
							<p>没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</c:otherwise>
		</c:choose>

</table>