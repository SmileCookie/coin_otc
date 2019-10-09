<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 
    <input type="hidden" id="currentPage" value="${currentPage }"/>
			<input type="hidden" id="currentTab" value="${currentTab }"/>
			<table class="tb-list2" id="ListTable">
				<thead>
					<tr>
						<th width="280px">${L:l(lan,"接收地址")}</th>
						<th width="157px">${L:l(lan,"时间")}</th>
						<th width="100px">${L:l(lan,"状态")}</th>
						<th width="130px">${L:l(lan,"备注")}</th>
						<th width="140px">${L:l(lan,"价格")}</th>
						<th width="140px">${L:l(lan,"金额")}</th>
						<th width="140px">${L:l(lan,"余额")}</th>
					</tr>
				</thead>
				
				<c:choose>
				<c:when test="${dataList != null&&fn:length(dataList)>0}">
				<c:forEach items="${dataList}" var="pm" varStatus="status">
				<tbody>
					<tr class="space">
						<td colspan="7"></td>
					</tr>
				</tbody>
			
				<tbody>
					<tr class="bd" >
						<td>
							<div class="pic_info">
								<c:if test="${fn:length(pm.btcTo)==0 }">-</c:if>
								<div class="txt"><a id="text_" target="_blank" href="https://blockchain.info/zh-cn/address/${pm.btcTo }">${pm.btcTo}</a></div>
							</div>
						</td>
						<td>
							<div class="pic_info">
								<div class="txt2">${fn:substring(pm.sendimeTime,0,19)}</div>
								<c:if test="${tm.isIn == 1 }">
									<div class="txt2">
										<c:choose>
										   <c:when test="${pm.status==0}">
										   	<span class="txt2">—</span>
										   </c:when>
										   <c:when test="${pm.status==1||pm.status==3}">
										   	 <span class="co_red">${pm.showConfigTime}</span>
										   </c:when>
										    <c:when test="${pm.status==2}">
										   	 <span style="color:green;">${pm.showConfigTime}</span>
										   </c:when>
										</c:choose>
									</div>
								</c:if>
							</div>
						</td>
						<td>
						<c:choose>
						   <c:when test="${pm.status==0}">
						   	<span >${L:l(lan,"处理中")}</span>
						   </c:when>
						   <c:when test="${pm.status==1}">
						   	 <span class="co_red">${L:l(lan,"失败")}</span>
						   </c:when>
						    <c:when test="${pm.status==2}">
						   	 <span class="co_red">${L:l(lan,"成功")}</span>
						   </c:when>
						     <c:when test="${pm.status==3}">
						   	 <span class="co_red">${L:l(lan,"已取消")}</span>
						   </c:when>
						</c:choose>
						
						</td>
						<td>
							${pm.remark }
						</td> 
						<td>
							<c:choose>
								<c:when test="${pm.price > 0 && pm.isIn != 10}">
									<font class="red">${fn:substring(pm.priceShow,0,1) }<fmt:formatNumber value="${fn:substring(pm.priceShow,1,-1) }" pattern="0.00####"/> </font>
								</c:when>
								<c:otherwise>-</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:if test="${pm.isIn == 1 || pm.isIn == 2 || pm.isIn == 4 || pm.isIn == 9 || pm.isIn == 10 || pm.isIn == 12 || pm.isIn == 7 || pm.isIn==13|| pm.isIn==40 || pm.isIn==42||pm.isIn==44}">
								<font class="green">+฿<fmt:formatNumber value=" ${pm.number/100000000}"  pattern="#0.000000##" /></font>
							</c:if>
							<c:if test="${pm.isIn == 0 || pm.isIn == 3 || pm.isIn == 5 || pm.isIn == 8 || pm.isIn==11 || pm.isIn==41 || pm.isIn==43 ||pm.isIn==45}">
								<font class="orange">-฿<fmt:formatNumber value=" ${pm.number/100000000}"  pattern="#0.000000##" /></font>
							</c:if>
						</td>
						<td >
							฿<fmt:formatNumber value="${pm.banlance/100000000}"  pattern="#0.000000##" />
						</td>
					</tr>
				</tbody>
				</c:forEach>
				
					<tfoot>
				        <tr>
			                <td colspan="7">
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
				                <td colspan="7">
				                	<p>${L:l(lan,"没有符合要求的记录")}</p>
				                </td>
			                </tr>
		                </tbody>
				</c:otherwise>
				</c:choose>
			</table>
			