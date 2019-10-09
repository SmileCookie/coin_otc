<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>

<style>
<!--
.lt {
	text-align: center;
	font-size: initial;
	left: 15px;
}

.schedule {
	position: relative;
	width: 120px;
	display: inline-block;
	height: 20px;
}

.schedule b {
	position: absolute;
	top: 0px;
	left: 0px;
	height: 22px !important;
	width: 100px;
	z-index: 5;
	display: inline-block;
	text-align: left;
	background: none !important;
	padding-left: 12px;
}

.schedule em {
	position: absolute;
	top: 0px;
	left: 0px;
	height: 22px;
	width: 80px;
	z-index: 3;
	display: inline-block;
	background: #eee;
}

.tatop {
	height: 50px;
	float: none;
	background: #eee;
	font-size: large;
	width: 100%;
}

.ulli {
	height: auto;
}

.lis {
	float: left;
	padding: 14px;
}
-->
</style>
<!-- 日收益 -->
<div class="table-responsive">
	<div class="tatop">
		<ul class="ulli">
			<li class="lis">${L:l(lan,'30天平均值：') } ฿ <fmt:formatNumber
					value="${reven.dailyAverage }" pattern="0.00######" /></li>
			<li class="lis">${L:l(lan,'本月汇总：') }  ฿ <fmt:formatNumber
					value="${reven2.thisMonthSum }" pattern="0.00######"  /> 
			<li class="lis">${L:l(lan,'上月汇总：') }  ฿  
				<c:if test="${reven3.lastMonthSum!=null }" >
					<fmt:formatNumber value="${reven3.lastMonthSum }"  pattern="0.00######"/>
				</c:if>				
				<c:if test="${reven3.lastMonthSum==null }">
					0.0
				</c:if>	
			</li>	
		</ul>
	</div>
	<table class="table table-striped table-bordered table-hover table-2x">
		<thead>
			<tr>
				<th >${L:l(lan,'时间') }</th>
				<th >${L:l(lan,'币种') }</th>
				<th >${L:l(lan,'收益') }</th>
				<th >${L:l(lan,'日收入（折合BTC）') }</th>
			</tr>
		</thead>
		<tbody>
			<c:choose>
				<c:when test="${fn:length(listBean)>0}">
					<c:forEach items="${listBean }" var="list" varStatus="stat">
						<c:set var="earnMax"  value="0"/>
						<c:set var="conMax"  value="0"/>
						<tr>
							<td class="lt" title="<fmt:formatDate value="${list.earningTime }" pattern="yyyy-MM-dd" />">
								<fmt:formatDate value="${list.earningTime }" pattern="yyyy-MM-dd" />
							</td>
							<td class="lt"> 
								<span class="schedule">
									<b>
									 <c:if test="${coinMaps!=null }">
										<c:forEach items="${coinMaps }" var="coin">
											<c:if test="${list.fundsType == coin.value.fundsType }">${coin.value.propTag }
												<c:set var="earnMax"  value="${earnMaxMap.get(coin.value.fundsType) }"/>
												<c:set var="conMax"  value="${conMaxMap.get(coin.value.fundsType) }"/>
											</c:if>
										</c:forEach>
									</c:if> 
									</b>
								</span>
							</td>
							
							<td class="lt">
								<span class="schedule">
									<b><fmt:formatNumber value="${list.earnings }" pattern="0.00######" /></b>
									<em style="width:<fmt:formatNumber value="${earnMax>0?(list.earnings/earnMax):0 }" type="percent" />"></em>
								</span>
							</td>
							<td class="lt">
								<span class="schedule">
									<b><fmt:formatNumber value="${list.converts}" pattern="0.00######" /></b>
									<em style="width:<fmt:formatNumber value="${conMax > 0 ? (list.converts / conMax) : 0 }"  type='percent' />"></em>
								</span>
							</td>
							<%-- <td class="lt"> 
								<span class="schedule">
									<b><fmt:formatNumber value="${list.btcEarnings}" pattern="0.00" /></b>
									<em style="width:<fmt:formatNumber value="${btcMax>0?(list.btcEarnings/btcMax):0 }" type="percent" />"></em>
								</span>
							</td>
							<td class="lt">
								<span class="schedule">
									<b><fmt:formatNumber value="${list.ltcEarnings}" pattern="0.00" /></b>
									<em style="width:<fmt:formatNumber value="${ltcMax>0?(list.ltcEarnings/ltcMax):0 }"  type="percent" />"></em>
								</span>
							</td>
							<td class="lt">
								<span class="schedule">
									<b><fmt:formatNumber value="${list.ethEarnings}" pattern="0.00" /></b>
									<em style="width:<fmt:formatNumber value="${ethMax>0?(list.ethEarnings/ethMax):0 }" type="percent" />"></em>
								</span>
							</td>
							<td class="lt etcDisplay">
								<span class="schedule">
									<b><fmt:formatNumber value="${list.etcEarnings}" pattern="0.00" /></b>
									<em style="width:<fmt:formatNumber value="${etcMax>0?(list.etcEarnings/etcMax):0 }" type="percent" />"></em>
								</span>
							</td> --%>
							
						</tr>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<tr>
						<td colspan="4">
							<div class="bk-norecord">
								<p>
									<i class="bk-ico info"></i>${L:l(lan,'暂时没有相关记录。') }
								</p>
							</div>
						</td>
					</tr>
				</c:otherwise>
			</c:choose>
		</tbody>
	</table>
	<c:if test="${pager!=''}">
		<div id="page_navA" class="page_nav">
			<div class="con_" style="float: right;">${pager}</div>
		</div>
	</c:if>
</div>

