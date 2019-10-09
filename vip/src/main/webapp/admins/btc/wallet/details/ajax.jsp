<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" id="ListTable">
								<tr>
									<th>编号/钱包号</th>
									<th>交易金额</th>
					                <th>钱包余额</th>
					                <th>时间</th>
					                <th>备注说明</th>
					                <th class="commodity_action">操作</th>
								</tr>
							<c:choose>
								<c:when test="${dataList!=null}">
									<c:forEach items="${dataList}" var="list">
									<tbody id="row${list.id }">
										<tr class="item_list_bd item_list_bgcolor">
											<td class="commodity_info">
												${list.id }/${list.walletId }
											</td>
											<td class="commodity_price b_gray">
												<c:if test="${list.ioType == 1 }">
													+
												</c:if>
												<c:if test="${list.ioType == 2 }">
													-
												</c:if>
												<fmt:formatNumber value="${list.amount / 100000000 }" pattern="0.000000##" />
												<c:if test="${list.fee>0 }">
													<br/>
													<font color="orange">手续费：-<fmt:formatNumber value="${list.fee / 100000000 }" pattern="0.000000##" /></font>
												</c:if>
											</td>
											<td>
												<fmt:formatNumber value="${list.walletAmount / 100000000 }" pattern="0.000000##" />
											</td>
											<td class="b_gray" >
												<fmt:formatDate value="${list.addDate }" pattern="yyyy-MM-dd HH:mm:ss"/>
											</td>
											<td>${list.des }</td>
											<td>
												—
											</td>
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