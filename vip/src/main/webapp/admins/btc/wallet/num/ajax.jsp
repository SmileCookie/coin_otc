<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" id="ListTable">
								<tr>
									<th class="commodity_checkbox"></th>
									<th>编号/钱包</th>
					                <th>余额</th>
					                <th>时间</th>
					                <th>描述</th>
					                <th class="commodity_action">操作</th>
								</tr>
							<c:choose>
								<c:when test="${dataList!=null}">
									<tbody class="operations">
										<tr>
											<td colspan="6">
												<div class="operation">
													<label><input type="checkbox" name="checkbox" class="DeleAllSel" id="DeleAllSel" />全/反选</label>
												</div>
											</td>
										</tr>
									</tbody>
								
									<c:forEach items="${dataList}" var="list">
									<tbody id="row${list.id }">
										<tr class="item_list_bd item_list_bgcolor">
											<td class="commodity_checkbox bl_color"
												style="vertical-align: middle;">
											</td>
											<td class="commodity_info">
												${list.id }/${list.walletName }
											</td>
											<td class="commodity_price b_gray">
												<fmt:formatNumber value="${list.balance }" pattern="0.000000##"/>
											</td>
											<td><fmt:formatDate value="${list.addDate }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
											<td class="b_gray" style="text-align:left;">
												<c:if test="${list.fromKey != null }">
													从<a style="color: #006699;" href="https://blockchain.info/zh-cn/address/${list.fromKey }" target="_blank">${list.fromKey }</a> <br>
													到<a style="color: #006699;" href="https://blockchain.info/zh-cn/address/${list.toKey }" target="_blank"> ${list.toKey }</a>
													<br>
													金额：<fmt:formatNumber value="${list.amount/100000000 }" pattern="0.000000##"/><br/>
												</c:if>
													${list.des }
											</td> 
											<td>
												—
											</td>
										</tr>
									</tbody>
									
									
									</c:forEach>
									<tbody class="operations">
										<tr>
											<td colspan="6">
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
											<td colspan="6">
												<p>暂时没有符合要求的记录！</p>
											</td>
										</tr>
									</tbody>
								</c:otherwise>
							</c:choose>
						</table>