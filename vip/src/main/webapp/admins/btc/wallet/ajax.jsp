<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" id="ListTable">
								<tr>
									<th>编号/文件名</th>
									<c:if test="${fn:indexOf(coint.stag, 'et')>=0}">
										<th>钱包地址、端口</th>
									</c:if>
									<th>接收地址数/已生成数</th>
					                <th>余额</th>
					                <c:if test="${fn:indexOf(coint.stag, 'et')>=0}">
						                <th>钱包地址</th>
						                <th>确认次数</th>
						                <th>交易最大确认次数</th>
					                </c:if>
					                <th>创建时间</th>
					                <th>最大地址量</th>
					                 <th>钱包用途</th>
					                <th class="commodity_action">操作</th>
								</tr>
							<c:choose>
								<c:when test="${dataList!=null}">
									<c:forEach items="${dataList}" var="list">
									<tbody id="row${list.walletId }">
										<tr class="item_list_bd item_list_bgcolor">
											<td class="commodity_info">
												${list.walletId }/${list.name }
											</td>
											<c:if test="${fn:indexOf(coint.stag, 'et')>=0}">
												<td>
													地址：${list.rpcIp}<br/>
													端口：${list.rpcPort}
												</td>
											</c:if>
												<td class="commodity_price b_gray">
													${list.keysNumber }/${list.hasUsedNums }
												</td>
												<td><fmt:formatNumber value="${list.btcs}" pattern="0.000000##" /></td>
											<c:if test="${fn:indexOf(coint.stag, 'et')>=0}">
												<td>${list.sendAddress}</td>
												<td>${list.confirmTimes}</td>
												<td>${list.targetTimes}</td>
											</c:if>
											<td class="b_gray" style="text-align:left;">
												<fmt:formatDate value="${list.createDate }" pattern="yyyy-MM-dd HH:mm:ss"/>
											</td> 
											<td>${list.maxKeyNums }</td>
											<td>
												<c:if test="${!list.withdraw }"><font color="green">收币</font></c:if>
												<c:if test="${list.withdraw }"><font color="red">打币</font></c:if>
											</td>
											<td>
												<a href="javascript:vip.list.aoru({url : '/admin/btc/wallet/aoru?id=${list.walletId }&coint=${coint.stag }', id : ${list.walletId } , width : 600 , height : 700});">修改</a><br/>
												<a href="javascript:vip.list.aoru({url : '/admin/btc/wallet/add?id=${list.walletId }&coint=${coint.stag }' , id : ${list.walletId } , width : 600 , height : 360});">添加地址</a><br/>
												<a href="javascript:vip.list.aoru({url : '/admin/btc/wallet/out?id=${list.walletId }&coint=${coint.stag }' , id : ${list.walletId } , width : 600 , height : 360 , title : '钱包转账'});">转出</a>
											</td>
										</tr>
									</tbody>
									
									
									</c:forEach>
									<tbody class="operations">
										<tr>
											<td colspan="${fn:indexOf(coint.stag,'et')>=0 ? 13 : 7}">
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
											<td colspan="${fn:indexOf(coint.stag,'et')>=0 ? 13 : 7}">
												<p>暂时没有符合要求的记录！</p>
											</td>
										</tr>
									</tbody>
								</c:otherwise>
							</c:choose>
						</table>