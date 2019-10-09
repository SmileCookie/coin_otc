<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<table class="tb-list2" id="ListTable">
								<tr>
								
									<th style="text-align: center;">${L:l(lan,"用户ID")}</th>
									<th style="text-align: center;">${L:l(lan,"用户名")}</th>
									<th style="text-align: center;">${L:l(lan,"购买时间")}</th>
									<th style="text-align: center;">${L:l(lan,"到期时间")}</th>
									<th style="text-align: center;">${L:l(lan,"描述")}</th>
								</tr>
							<c:choose>
								<c:when test="${not empty dataList}">
									<c:forEach items="${dataList}" var="item">
									<tbody >
										<tr class="item_list_bd item_list_bgcolor">
											<td >${item[0] }</td>
											<td >${item[1] }</td>
											<td ><fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${item[2]}"/></td>
											<td ><fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${item[3]}"/></td>
											<td >${item[4] }</td>
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