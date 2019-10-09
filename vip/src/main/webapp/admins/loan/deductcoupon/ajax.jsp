<%@ page session="false" language="java" import="java.util.*"	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<table class="tb-list2" style="width: 100%; table-layout: fixed;">
	<thead>
		<tr>
			<th style="width: 40px;"></th>
			<th>用户名</th>
			<th>标题</th>
			<th>抵扣券类型</th>
			<th>抵扣券额度</th>
<!-- 			<th>抵扣币种</th> -->
			<th>抵扣券标识</th>
			<th>使用状态</th>
			<th>操作</th>
		</tr>
	</thead>
	<c:choose>
		<c:when test="${dataList!=null}">
		
			<tbody>
				<tr class="space">
					<td colspan="7">
						<div class="operation">
							<input type="checkbox" style="display: none;" id="delAll" class="DeleAllSel" name="checkbox" />
								<label style="width: 50px;" onclick="selectAll()" class="checkbox" id="ck_delAll">
								<em>全选</em>
							</label>
							<a class="btn" href="javascript:shanChu();">
							<span class="cont">删除</span></a>
							<a class="btn" href="javascript:jinZhi();"><span class="cont">禁用</span></a>
							<a class="btn" href="javascript:qiDong();"><span class="cont">启用</span></a>
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
						<td colspan="8">
							<span>编号：${list.id} </span>
							<span>密钥：${list.secretkey }</span>
							<span>创建时间：<fmt:formatDate value="${list.startTime }" pattern="yyyy-MM-dd HH:mm:ss" /></span>
							<span>激活时间：<fmt:formatDate value="${list.actTime }" pattern="yyyy-MM-dd HH:mm:ss" /></span>
							<span>过期时间：${list.endFormatTime }</span>
						</td>
					</tr>
				</tbody>
				<tbody class="item_list" id="line_${list.id}">
					<tr>
						<td><input type="checkbox" name="boxs" class="checkItem" value="${list.secretkey }" /></td>
						
						<td>${list.userName}</td>
						
						<td>${list.title }</td>
						
						<td>
							<c:choose>
								<c:when test="${list.couponType eq 0}">--</c:when>
								<c:when test="${list.couponType eq 1}">抵扣券</c:when>
								<c:when test="${list.couponType eq 2}">打折券</c:when>
								<c:when test="${list.couponType eq 3}">限额抵扣券</c:when>
								<c:when test="${list.couponType eq 4}">限额打折券</c:when>
							</c:choose>
						</td>
						
						<td>
						<c:choose>
<%-- 								<c:when test="${list.fundsType eq 0}">--</c:when> --%>
<%-- 								<c:when test="${list.fundsType eq 1}">¥</c:when> --%>
<%-- 								<c:when test="${list.fundsType eq 2}">฿</c:when> --%>
<%-- 								<c:when test="${list.fundsType eq 3}">Ł</c:when> --%>
<%-- 								<c:when test="${list.fundsType eq 4}">E</c:when> --%>
<%-- 								<c:when test="${list.fundsType eq 5}">e</c:when> --%>
								<c:when test="${list.fundsType eq 0}"></c:when>
							</c:choose>
							฿
						<fmt:formatNumber value="${list.amountDeg }" pattern="#,##0.0#######" />
						</td>
						
						<%-- <td>￥ <fmt:formatNumber value="${list.hasUsedAmount }" pattern="#,#00.0####"/></td> --%>
						<%-- <td>${list.usedays }</td> --%>
						
<!-- 						<td> -->
<%-- 							<c:choose> --%>
<%-- 								<c:when test="${list.fundsType eq 0}">--</c:when> --%>
<%-- 								<c:when test="${list.fundsType eq 1}">RMB</c:when> --%>
<%-- 								<c:when test="${list.fundsType eq 2}">BTC</c:when> --%>
<%-- 								<c:when test="${list.fundsType eq 3}">LTC</c:when> --%>
<%-- 								<c:when test="${list.fundsType eq 4}">ETH</c:when> --%>
<%-- 								<c:when test="${list.fundsType eq 5}">ETC</c:when> --%>
<%-- 							</c:choose> --%>
<!-- 						</td> -->
						
						<td> ${list.batchMark } </td>
						
						<td>
							<c:choose>
								<c:when test="${list.useState eq 0}"><font color="blue">未激活</font></c:when>
								<c:when test="${list.useState eq 1}"><font style="color: #339900;">未使用</font></c:when>
								<c:when test="${list.useState eq 2}"><font style="color: red;">已使用</font></c:when>
								<c:when test="${list.useState eq 3}"><font style="color: red;">已过期</font></c:when>
								<c:when test="${list.useState eq 4}"><font style="color: #c0c0c0;">禁止使用</font></c:when>
								<c:when test="${list.useState eq 5}"><font style="color: orange;">使用中</font></c:when>
							</c:choose>
						</td>
						
						<td>
							<c:choose>
								<c:when test="${list.useState eq 0 }">
								<a class="btn" href="javascript:reUser('${list.secretkey }');" >
									<span class="cont">赠送用户</span>
									</a>
								</c:when>
								<c:when test="${list.useState eq 0 || list.useState eq 1 }">
									<a class="btn" href="javascript:unjinZhi('${list.secretkey }');">
										<span class="cont">禁用</span>
									</a>
								</c:when>
								<c:when test="${list.useState eq 4 }">
									<a class="btn" href="javascript:startUp('${list.secretkey }');">
										<span class="cont">激活</span>
									</a>
								</c:when>
							</c:choose>
							
							<a class="btn" href="javascript:deClear('${list.secretkey }','${list.useState }');" class="btn btn-gray">
								<span class="cont">删除</span>
							</a>
						</td>
					</tr>
				</tbody>
			</c:forEach>
			<tfoot>
				<tr>
					<td colspan="7">
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
					<td colspan="7">
						<p>没有符合要求的记录！</p>
					</td>
				</tr>
			</tbody>
		</c:otherwise>
	</c:choose>
</table>

