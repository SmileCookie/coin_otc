<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 
<link rel=stylesheet type=text/css href="${static_domain }/statics/css/cnbtc2014.css" />
<link rel=stylesheet type=text/css href="${static_domain }/statics/css/fast.css" />
<style>
.tb-list2 td{font-size: 12px;}
</style>
    <input type="hidden" id="currentPage" value="${currentPage }"/>
			<input type="hidden" id="currentTab" value="${currentTab }"/>
<div style="padding: 10px;">		
			<table class="tb-list2" id="ListTable">
				<thead>
					<tr>
						<th width="260px">${L:l(lan,"时间")}</th>
						<th width="140px">${L:l(lan,"类别")}</th>
						<th width="100px">${L:l(lan,"价格")}</th>
						<th width="90px">${L:l(lan,"数量")}</th>
						<th width="90px">${L:l(lan,"金额")}</th>
						<th width="100px">${L:l(lan,"现金手续费")}</th>
						<th width="100px">${L:l(lan,"比特币手续费")}</th>
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
								
								<div class="txt"><a id="text_" target="_blank" href="https://blockchain.info/zh-cn/address/${pm.btcTo }">${pm.btcTo}</a></div>
								<div class="txt2">${fn:substring(pm.sendimeTime,0,19)}</div>
								
							</div>
						</td>
						
						<td>
							<c:if test="${pm.isIn == 1 || pm.isIn == 2 || pm.isIn == 4}">买入</c:if>
							<c:if test="${pm.isIn == 0 || pm.isIn == 3 || pm.isIn == 5}">卖出</c:if>
						</td>
						<td>
							${pm.price }						
						</td>
						<td>
							<fmt:formatNumber value=" ${pm.number/100000000}"  pattern="#0.00000#" />
						</td>
						
						<td>
							${pm.amount }
						</td>
						<td >
							<c:if test="${pm.isIn == 1 || pm.isIn == 2 || pm.isIn == 4}">—</c:if>
							<c:if test="${pm.isIn == 0 || pm.isIn == 3 || pm.isIn == 5}">${pm.fees }</c:if>
						</td>
						<td>
							<c:if test="${pm.isIn == 1 || pm.isIn == 2 || pm.isIn == 4}">
								<fmt:formatNumber value="${pm.fees}"  pattern="#0.00000#" />
							</c:if>
							<c:if test="${pm.isIn == 0 || pm.isIn == 3 || pm.isIn == 5}">—</c:if>
						</td>
					</tr>
				</tbody>
				</c:forEach>
				
					<tfoot>
				        <tr>
			                <td colspan="6">
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
	</div>		