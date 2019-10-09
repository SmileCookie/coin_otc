<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<input type="hidden" id="currentTab" value="${currentTab }"/>
<table class="tb-list2" id="ListTable" style="width: 100%">
	<tr>
		<th style="width:200px">用户ID</th>
        <th>资金类型</th>
        <th>金额</th>
        <th class="commodity_action">操作</th>
	</tr>
	<c:choose>
		<c:when test="${dataList!=null}">
			<c:forEach items="${dataList}" var="list">
			  <tbody id="row${list.user.id}">
				<tr class="hd">
					<td colspan="4">
						<span>注册时间：<fmt:formatDate value="${list.user.registerTime }" pattern="yyyy-MM-dd HH:mm"/> </span>
					</td>
				</tr>								
				<tr class="item_list_bd item_list_bgcolor">
					<td class="commodity_info">
						<div class="pic_info">
							<div class="txt">
								${list.userId }
								<%--<a href="javascript:showUser('${list.user.id }')" style="font-weight: bold;color:green;" id="text_${list.user.id }">${list.userName }</a>--%>
							</div>
						</div>
					</td>
					<td>
						${list.ft.propTag }(${list.ft.unitTag })
					</td>
					<td class="b_gray" style="text-align: left;">
						可用：<fmt:formatNumber pattern="0.000#####" value="${list.balance }"/>
                        <c:if test="${list.balance>0}">
                           <a style="float: right;" href="javascript:btcFreez('${list.userId }','${list.ft.propTag }');">冻结</a>
                        </c:if>
					    <br/>
						冻结：<fmt:formatNumber pattern="0.000#####" value="${list.freez }"/>
                        <c:if test="${list.freez>0}">
                           <a style="float: right;color: red;" href="javascript:btcUnFreez('${list.userId }','${list.ft.propTag }');">解冻</a>
                        </c:if>
					</td>
					<td class="commodity_action br_color">

						<c:choose>
							<c:when test="${isOpenManagement}">
								<a href="javascript:btcCharge('${list.user.id }','${list.ft.propTag }');">充${list.ft.propTag }</a>
								<a href="javascript:btcDeduct('${list.user.id }','${list.ft.propTag }');">扣${list.ft.propTag }</a>
							</c:when>
							<c:otherwise>
								充${list.ft.propTag }
								扣${list.ft.propTag }
							</c:otherwise>
						</c:choose>

					</td>
				</tr>
				</tbody>
			</c:forEach>
			<tbody class="operations">
				<tr>
					<td colspan="4">
						<div id="page_navA" class="page_nav">
							<div class="con">
								<c:if test="${pager==''}">共${itemCount}项</c:if>
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
					<td colspan="4">
						<p>暂时没有符合要求的记录！</p>
					</td>
				</tr>
			</tbody>
		</c:otherwise>
	</c:choose>
</table>
<script type="text/javascript">
	function statistics(id) {
		$.Iframe({
			Url : '/admin/pay/user/statistics?userId='+id,
			Width : 730,
			Height : 500,
            isShowIframeTitle: true,
			Title : "用户资金信息"
		});
	} 
</script>