<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<table class="tb-list2" id="ListTable" style="width: 100%">
		<tr>
			<th >流水号</th>
               <th >提交时间</th>
               <th >提现地址</th>
               <th >提取个数（个）</th>
               <th >实际个数</th>
               <th >状态</th>
               <th >备注</th>
               <th class="commodity_action">操作</th>
		</tr>
	<c:choose>
		<c:when test="${dataList!=null}">
			<tbody>
				<tr class="space">
					<td colspan="8">
						<div class="operation" style="padding-left: 18px;">
							<input type="checkbox" style="display:none;" id="delAll" class="DeleAllSel" name="checkbox"/><label style="width: 50px;" onclick="selectAll()" class="checkbox" id="ck_delAll"><em>全选</em></label>
							<a class="AButton yellow_button manyJisuan" href="javascript:tongji(false)">统计选中金额</a>
							|
							<a class="AButton yellow_button manyJisuan" href="javascript:tongji(true)">统计全部金额</a>
							<span style="margin-left: 30px;font-size: 18px;color: #ff0000;display: none;">提取个数：<font id="totalM"></font></span>
							<span style="margin-left: 30px;font-size: 18px;color: #ff0000;display: none;">实际个数：<font id="totalM2"></font></span>
						</div>
					</td>
				</tr>
			</tbody>
			<c:forEach items="${dataList}" var="list" varStatus="statu">
			<tbody id="row${list.id }" uname="${list.userId}" money="<fmt:formatNumber value="${list.amount}" pattern="0.000000##"/>" fee="<fmt:formatNumber value="${list.fees}" pattern="0.000000##"/>" after="<fmt:formatNumber value="${list.afterAmount }" pattern="0.000000##"/><br/>">
					
				<tr class="hd">
					<td colspan="8">
						<input type="checkbox" style="display:none;" value="${list.id}" id="${statu.index}" class="checkItem"/><label id="ck_${statu.index}" class="checkbox" onclick="changeCheckBox('${statu.index}')"></label>
						<span>提现编号:${list.id} </span><span>提交时间：<fmt:formatDate value="${list.submitTime }" pattern="yyyy-MM-dd HH:mm:ss"/> </span>
						<span>确认时间：<fmt:formatDate value="${list.manageTime }" pattern="yyyy-MM-dd HH:mm:ss"/> </span>

						<%--modify by xwz 20171019 小额自动打币start--%>
						<c:if test="${list.autoDownloadId > 0 && list.commandId > 0}"><span style="color: red;">自动</span></c:if>
						<c:if test="${list.autoDownloadId == 0 && list.commandId > 0}"><span style="color: red;">人工</span></c:if>
						<%--end--%>

						<%--<c:if test="${list.commandId>0 }"><span style="color: red;">自动</span></c:if>--%>
						<%--<c:if test="${list.managerId==1 }"><span style="color: blue;">(免审)</span></c:if>--%>
					</td>
				</tr>								
				<tr class="item_list_bd item_list_bgcolor">
					
					<td class="commodity_info">
						<div class="pic_info">
							${list.userId}
							<%--<a href="javascript:showUser('${list.user.id}')" style="font-weight: bold;color:green;" id="text_${list.user.id }">${list.user.userName}</a>--%>
							<c:if test="${list.status<0 && list.commandId==0}">
								<br/>
								<a style="color: red;font-weight: bold;" href="javascript:firstCheck(${list.id},'${list.user.id }')" class="confirmTrue">审核通过</a>
							</c:if>
							<c:if test="${list.status==0 && list.commandId==0}">
								<br/>
								<a style="color: red;font-weight: bold;" href="javascript:confirm(${list.id})" class="confirmTrue">确认</a>
								(非管理员勿点)
							</c:if>
							<c:if test="${list.hasFail == 1 && list.status == 2 && currentTab != 'success'}">
							 <%-- <a style="color: red;font-weight: bold;" href="javascript:retrySend(${list.id})" class="confirmTrue">重新打币</a> --%>
							</c:if>
						</div>
					</td>
					<td class="commodity_price b_gray">
						<fmt:formatDate value="${list.submitTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
					<td class="b_gray" id="evaluate_td${list.id}">
						<a style="color: #006699;" href="${coint.web }${list.toAddress }" target="_blank">${list.toAddress }</a>
					</td>
					<td class="b_gray">
					    <fmt:formatNumber value="${list.amount}" pattern="0.000000##"/><br/>
					</td>
					<td class="b_gray">
					    <font style="color:red; font-size: 14px;font-weight: bold;">
					   		<fmt:formatNumber value="${list.afterAmount }" pattern="0.000000##"/><br/>
						</font>
					</td>
					<td>
						${list.showStat }
					</td>
					<td class="b_gray">
						${list.remark }
					</td>
					<td class="commodity_action br_color">
						<c:choose>
							<c:when test="${list.status==0 && list.commandId<=0}"><%--待确认 --%>
								<a href="javascript:confirmCancel(${list.id})" mytitle="如果此提现记录提现存在问题，请点击关闭，提现金额将返还给用户" setStat="5" class="failState" Shadow="true">取消</a><br>
								<a href="javascript:confirmSuc(${list.id})" class="confirmTrue">成功</a>
							</c:when>
							<c:when test="${list.status==1}"><%-- 已失败 --%>
								<c:if test="${list.commandId > 0 && !list.confirm}">
									<a style="color: green;" href="javascript:failEntry(${list.id})">账户返还</a>
								</c:if>
							</c:when>
							<c:when test="${list.status==2}">
								<c:if test="${list.hasFail == 0 }">
									<a href="javascript:confirmFail(${list.id})" mytitle="如果此提现记录提现存在问题，请点击关闭，不返还用户资金，需要重新打币">失败不返还</a>
									<br/>
								</c:if>
								<c:if test="${list.hasFail == 1 && currentTab != 'success'}">
									<a href="javascript:failToSuc(${list.id})" class="confirmTrue">成功</a>
									<br/>
								</c:if>
								<c:if test="${list.commandId > 0 && !list.confirm}">
									<a style="color: green;" href="javascript:fees(${list.id})">录入手续费</a><br>
								</c:if>
<!-- 								<a href="javascript:sucFail(${list.id})" class="sucFail" ids="${list.id}" style="color:red;">确认失败并返还用户资金</a> -->
							</c:when>
							<c:otherwise>
							<%-- 
								<a href="javascript:void(0)" ids="${list.id}" class="del AButton gray_button" Shadow="true">删除</a>
							--%>
							-
							</c:otherwise>
						</c:choose> 
						
						<br/>
					</td>
				</tr>
			</tbody>
			</c:forEach>
			<tbody class="operations">
				<tr>
					<td colspan="8">
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
					<td colspan="8">
						<p>暂时没有符合要求的记录！</p>
					</td>
				</tr>
			</tbody>
		</c:otherwise>
	</c:choose>
</table>