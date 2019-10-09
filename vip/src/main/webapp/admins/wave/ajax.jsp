<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" id="ListTable">
								<tr>
									<th class="commodity_checkbox"></th>
									<th>编号</th>
					                <th>波动值</th>
					                <th>设置者</th>
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
											
										<tr class="hd">
											<td colspan="6">
												<input type="checkbox" class="deleteck" name="singleDelSel" value="${list.id }" />
												<span>编号:${list.id} </span><span>设置时间：<fmt:formatDate value="${list.date }" pattern="yyyy-MM-dd HH:mm:ss"/> </span>
											</td>
										</tr>								
										<tr class="item_list_bd item_list_bgcolor">
											<td class="commodity_checkbox bl_color"
												style="vertical-align: middle;">
											</td>
											<td class="commodity_info">
												${list.id }
											</td>
											<td class="commodity_price b_gray">
												${list.waveVal }
											</td>
											<td class="b_gray">
												${list.adminId }
											</td>
											<td class="b_gray" style="text-align: left;">
												${list.des }
											</td> 
											<td>
												<a href="javascript:vip.list.aoru({id : '${list.id }', width : 600 , height : 370});">修改</a>
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