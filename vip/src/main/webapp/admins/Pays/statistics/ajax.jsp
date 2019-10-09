<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<script type="text/javascript">
$(function(){ 
	$("#shopslist").Ui();
});
</script>
<table class="tb-list2" id="ListTable" style="width: 100%">
	<tr>
		<th style="width:26px;">序号</th>
		<th style="width: 13%;">用户</th>
        <th style="width: 13%;">总额(${tab })</th>
        <th style="width: 13%;"><select name="balance" onchange="reload2(this);"><option value="">可用余额默认排序</option><option value="asc" <c:if test="${balance=='asc' }">selected="selected"</c:if>>可用余额升序</option><option value="desc" <c:if test="${balance=='desc' }">selected="selected"</c:if>>可用余额降序</option></select></th>
		<th style="width: 13%;">融资融币借入金额</th>
        <th style="width: 13%;"><select name="freez" onchange="reload2(this);"><option value="">冻结余额默认排序</option><option value="asc" <c:if test="${freez=='asc' }">selected="selected"</c:if>>冻结余额升序</option><option value="desc" <c:if test="${freez=='desc' }">selected="selected"</c:if>>冻结余额降序</option></select></th>
		<th style="width: 13%;">提现冻结金额</th>
		<th style="width: 13%;">融资融币放贷冻结金额</th>
		<th style="width: 13%;">挂单委托冻结金额</th>
       <%--  <th style="width: 13%;"><select name="storage" onchange="reload2(this);"><option value="">活期余额默认排序</option><option value="asc" <c:if test="${storage=='asc' }">selected="selected"</c:if>>活期余额升序</option><option value="desc" <c:if test="${storage=='desc' }">selected="selected"</c:if>>活期余额降序</option></select></th>
         <th style="width: 10%;">利息总额</th>--%>
        <th style="width: 10%;" class="commodity_action">操作</th>
	</tr>
	<c:choose>
		<c:when test="${dataList!=null}">
			<tbody>
				<tr class="space">
					<td colspan="7">
						<div class="operation">
							<input type="checkbox" style="display:none;" id="delAll" class="DeleAllSel" name="checkbox"/><label style="width: 50px;" onclick="selectAll()" class="checkbox" id="ck_delAll"><em>全选</em></label>
							<a class="AButton yellow_button manyJisuan" href="javascript:tongji(false)">统计选中金额</a>
							|
							<a class="AButton yellow_button manyJisuan" href="javascript:tongji(true)">统计全部金额</a>
							<span style="margin-left: 30px;font-size: 14px;color: #ff0000;display: none;">
								总金额：<font id="totalM"></font>
								可用总额：<font id="totalM2"></font>
								冻结总额：<font id="totalM3"></font>
								<!-- 存款总额：<font id="totalM4"></font>
								利息总额：<font id="totalM5"></font> -->
							</span>
						</div>
					</td>
				</tr>
			</tbody>
			<c:forEach items="${dataList}" var="list" varStatus="statu">
			  <tbody id="row${list.user.id}">
				<tr class="item_list_bd item_list_bgcolor">
					<td>
						<div style="text-align: left;">
								${statu.index + 1}
						</div>
						<input type="checkbox" style="display:none;" value="${list.userId}" id="${statu.index+2017}" class="checkItem"/>
					</td>
					<td class="commodity_info">
						<div class="pic_info">
								${list.userId }
							<%--<a href="javascript:showUser('${list.userId }')" style="font-weight: bold;color:green;" id="text_${list.userId }">${list.userName }</a>--%>
						</div>
					</td>
					<td class="b_gray" style="text-align: right;">
						<fmt:formatNumber pattern="0.00######" value="${list.total }"/>
					</td>
					<td class="b_gray" style="text-align: right;">
						<fmt:formatNumber pattern="0.00######" value="${list.balance }"/>
					</td>
					<td class="b_gray" style="text-align: right;">
						<fmt:formatNumber pattern="0.00######" value="${list.inSuccess }"/>
					</td>
					<td class="b_gray" style="text-align: right;">
						<fmt:formatNumber pattern="0.00######" value="${list.freez }"/>
					</td>
					<td class="b_gray" style="text-align: right;">
						<fmt:formatNumber pattern="0.00######" value="${list.withdrawFreeze }"/>
					</td>
					<td class="b_gray" style="text-align: right;">
						<fmt:formatNumber pattern="0.00######" value="${list.outWait }"/>
					</td>
					<td class="b_gray" style="text-align: right;">
						<fmt:formatNumber pattern="0.00######" value="${list.entrustFreeze }"/>
					</td>
				<%-- 	<td class="b_gray" style="text-align: right;">
						<fmt:formatNumber pattern="0.00######" value="${list.storageBtc }"/>
					</td>
					<td class="b_gray" style="text-align: right;">
						<fmt:formatNumber pattern="0.00######" value="${list.storageBtcLixi }"/>
					</td> --%>
					<td class="commodity_action br_color">
                       -
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
