<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 
<table class="tb-list2" id="ListTable" style="width: 100%">
	<tr>
		<th class="commodity_checkbox">流水号／时间</th>
		<th >用户编号</th>
        <th >类型</th>
        <th >资金类型</th>
        <th >金额</th>
        <th >备注</th>
        <th class="commodity_action">操作</th>
	</tr>
	<c:choose>
		<c:when test="${dataList!=null}">
			<tbody>
				<tr class="space">
					<td colspan="9">
						<div class="operation">
							<input type="checkbox" style="display:none;" id="delAll" class="DeleAllSel" name="checkbox"/><label style="width: 50px;" onclick="selectAll()" class="checkbox" id="ck_delAll"><em>全选</em></label>
							<a class="AButton yellow_button manyJisuan" href="javascript:tongji(false)">统计选中金额</a>
							|
							<a class="AButton yellow_button manyJisuan" href="javascript:tongji(true)">统计全部金额</a>
							<span style="margin-left: 30px;font-size: 18px;color: #ff0000;display: none;">总额：<font id="totalM"></font></span>
						</div>
					</td>
				</tr>
			</tbody>
			<c:forEach items="${dataList}" var="list" varStatus="statu">
			<tbody id="row${list.id }">
				<tr class="item_list_bd item_list_bgcolor">
					<td class="commodity_checkbox bl_color" style="vertical-align: middle;">
					<input type="checkbox" style="display:none;" value="${list.id}" id="${statu.index}" class="checkItem"/><label id="ck_${statu.index}" class="checkbox" onclick="changeCheckBox('${statu.index}')"></label>
					${list.id } / 
					<fmt:formatDate value="${list.sendTime }" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
					<td class="commodity_info">
						<div class="pic_info" style="text-align: left;">
							${list.userId}
						</div>
					</td>
					<td class="b_gray">
						${list.showType }
					</td>
					<td class="b_gray">
						${list.coinName }
					</td>
					<td class="b_gray" style="font-weight: bold;color: #F37800;">
						<c:if test="${list.bt.inout == 0}">
                 		…
	                 	</c:if>
	               		<c:if test="${list.bt.inout == 1}">
	               			<font class="green">+<fmt:formatNumber value="${list.amount }" pattern="#0.000#####" />=<fmt:formatNumber value="${list.balance }" pattern="#0.0000####" /></font>
	               		</c:if>
	               		<c:if test="${list.bt.inout == 2}">
	               			<font class="orange">-<fmt:formatNumber value="${list.amount }" pattern="#0.000#####" />=<fmt:formatNumber value="${list.balance }" pattern="#0.0000####" /></font>
	               		</c:if>
					</td>
					<td>${list.remark }</td>
					<td class="commodity_action br_color">
						-
					</td>
				</tr>
			</tbody>
			</c:forEach>
			<tbody class="operations">
				<tr>
					<td colspan="6">
						<div id="page_navA" class="page_nav">
							<div class="con">
								<c:if test="${pager==''}">共${fn:length(dataList)}项</c:if>
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