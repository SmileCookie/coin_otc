<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 

<input type="hidden" id="currentPage" value="${currentPage }" /> 
						<input type="hidden" id="currentTab" value="${currentTab }" />
						<table class="tb-list2" id="ListTable">
							<thead>
								<tr>
									<th style="width:150px;">${L:l(lan,"编号")}/${L:l(lan,"时间")}</th>
									<th style="width:225px;">${L:l(lan,"备注")}</th>
									<th style="width:225px;">${L:l(lan,"冻结")}/${L:l(lan,"解冻")}</th>
									<th style="width:180px;">${L:l(lan,"余额")}</th>
								</tr>
							</thead>

							<c:choose>
								<c:when test="${fn:length(dataList)>0}">
									<c:forEach items="${dataList}" var="freez">
										<tbody>
											<tr class="space">
												<td colspan="4"></td>
											</tr>
										</tbody>

										<tbody>
											<tr class="bd">
												<td style="text-align: left;">
													${freez.btcFreezeId }
													<br>
													<fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${freez.freezeTime}" />
												</td>
												<td>${freez.reMark }</td>
												<td>
														<c:choose>
															<c:when test="${freez.statu==0 }">
																<font class="orange">+฿<fmt:formatNumber value="${freez.btcNumber/100000000 }" pattern="#0.00000###" /></font>
															</c:when>
															<c:when test="${freez.statu==1 }">
																<font class="green">-฿<fmt:formatNumber value="${freez.btcNumber/100000000 }" pattern="#0.00000###" /></font>
															</c:when>
															<c:otherwise>-</c:otherwise>
														</c:choose>
												</td>
												<td class="admin"><font color="#000">
													฿<fmt:formatNumber value="${freez.freezeBanlance/100000000 }" pattern="#0.00000###" /></font>
												</td>
											</tr>
										</tbody>
									</c:forEach>
									<tfoot>
										<tr>
											<td colspan="4">
												<div id="page_navA" class="page_nav">
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
											<td colspan="6">
												<p>${L:l(lan,"没有符合要求的记录")}</p></td>
										</tr>
									</tbody>
								</c:otherwise>
							</c:choose>
						</table>
