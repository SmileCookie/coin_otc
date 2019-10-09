<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<table class="tb-list2" id="ListTable">
								<tr>
								
									<th style="text-align: center;">${L:l(lan,"时间")}</th>
									<th style="text-align: center;">${L:l(lan,"用户ID")}</th>
									<th style="text-align: center;">${L:l(lan,"类型")}</th>
						            <th style="text-align: center;">${L:l(lan,"积分")}</th>
						            <th style="text-align: center;">${L:l(lan,"描述")}</th>
								</tr>
							<c:choose>
								<c:when test="${not empty dataList}">
									<c:forEach items="${dataList}" var="item">
									<tbody >
										<tr class="item_list_bd item_list_bgcolor">
											<td ><fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${item.addTime}"/></td>
											<td >${item.userId }</td>
											<td >${item.typeShowNew }</td>
											<td >
												<c:if test="${item.ioType==0}">
							                 		<font class="green">+<fmt:formatNumber value="${item.jifen }" maxFractionDigits="2"  /></font>
							                 	</c:if>
							               		<c:if test="${item.ioType==1}">
							               			<font class="orange">-<fmt:formatNumber value="${item.jifen }" maxFractionDigits="2" /></font>
							               		</c:if>
											</td> 
											<td>${item.memo }</td>
										</tr>
									</tbody>
									
									
									</c:forEach>
									<tbody class="operations">
										<tr>
											<td colspan="7">
												<div id="page_navA" class="page_nav">
													<div class="con">
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
											<td colspan="7">
												<p>暂时没有符合要求的记录！</p>
											</td>
										</tr>
									</tbody>
								</c:otherwise>
							</c:choose>
						</table>