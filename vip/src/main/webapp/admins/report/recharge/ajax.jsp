<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" id="ListTable" style="width: 100%">
	<tr>
		<th class="commodity_checkbox"></th>
		<th >用户信息</th>
        <th >交易类型</th>
        <th >金额（${coint.propTag }）</th>
        <th >备注</th>
        <th >状态</th>
	</tr>
	<c:choose>
		<c:when test="${dataList!=null}">
			<c:if test="${logAdmin.rid==1 || logAdmin.rid==6}">
				<input type="checkbox" style="display:none;" id="delAll" class="DeleAllSel" name="checkbox"/><label style="width: 50px;" onclick="selectAll()" class="checkbox" id="ck_delAll"><em>全选</em></label>
				<a class="AButton yellow_button manyJisuan" href="javascript:tongji(false)">统计选中金额</a>
				|
				<a class="AButton yellow_button manyJisuan" href="javascript:tongji(true)">统计全部金额</a>
				<span style="margin-left: 30px;font-size: 18px;color: #ff0000;display: none;">总额：<font id="totalM"></font></span>
			</c:if>
			<c:forEach items="${dataList}" var="list" varStatus="statu">
			<tbody id="row${list.detailsId }" uname="${list.userName }" money="${list.amount }">
				<tr class="hd">
					<td colspan="7" style="background: #FBFDFF;padding: 3px 10px;">
						<input type="checkbox" style="display:none;" value="${list.detailsId}" id="${statu.index}" class="checkItem"/><label id="ck_${statu.index}" class="checkbox" onclick="changeCheckBox('${statu.index}')"></label>
						<span>充值编号:${list.detailsId} </span><span>充值时间：<fmt:formatDate value="${list.sendTime }" pattern="yyyy-MM-dd HH:mm:ss"/> </span>
						<span>确认时间：<fmt:formatDate value="${list.configTime }" pattern="yyyy-MM-dd HH:mm:ss"/> </span>
						<c:if test="${list.fromAddr != null }">从${list.fromAddr }</c:if>
						<c:if test="${list.toAddr != null }">到${list.toAddr }</c:if>
						<c:if test="${list.adminId > 0 }"><span>管理员：【${list.aUser.id},${list.aUser.admName }】 </span></c:if>
					</td>
				</tr>								
				<tr class="item_list_bd item_list_bgcolor">
					<td class="commodity_checkbox bl_color" style="vertical-align: middle;">
					</td>
					<td class="commodity_info">
						<div class="pic_info" style="text-align: left;">
							${list.userId}<br>
							<%--<a href="javascript:showUser('${list.userId}')" style="font-weight: bold;color:green;" id="text_${list.userId }">${list.userName}</a>--%>
						</div>
					</td>
					<td class="b_gray">
						${list.inType }
					</td>
					<td class="b_gray" style="font-weight: bold;color: #F37800;">
						<fmt:formatNumber value="${list.amount }" pattern="0.00######"/><br/>
					</td>
					<td>${list.remark }${list.type==1?('（确认次数：'.concat(list.confirmTimes).concat('）')):'' }</td>
					<td>
						${list.showStatu }
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
						<p id="exportFlag">没有符合要求的记录!</p>
					</td>
				</tr>
			</tbody>
		</c:otherwise>
	</c:choose>
</table>