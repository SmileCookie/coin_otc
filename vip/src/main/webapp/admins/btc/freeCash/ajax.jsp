<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" style="width:100%">
	<thead>
		<tr>
			<th style="width:300px;">用户信息</th>
			<th>之前免审额度</th>
			<th>此次申请额度</th>
			<th>状态</th>
			<th>操作</th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${dataList!=null}">
				<tbody>
					<tr class="space">
						<td colspan="5">
							<div class="operation">
								<input type="checkbox" style="display:none;" id="delAll" class="DeleAllSel" name="checkbox"/><label style="width: 50px;" onclick="selectAll()" class="checkbox" id="ck_delAll"><em>全选</em></label>
						       	<c:if test="${tab=='wait'}">
							        <a title="批量通过" href="javascript:passmore()" id="del_Btn" class="Abtn delete">批量通过</a>
						       	</c:if>
							</div>
						</td>
					</tr>
				</tbody>
			
				<c:forEach items="${dataList}" var="list" varStatus="statu">
					<tbody>
						<tr class="space">
							<td colspan="5"></td>
						</tr>
					</tbody>
					<tbody>
						<tr class="hd">
							<td colspan="5">
								<input type="checkbox" style="display:none;" value="${list.id}" id="${statu.index}" class="checkItem"/><label id="ck_${statu.index}" class="checkbox" onclick="changeCheckBox('${statu.index}')"></label>
								<span>编号：${list.id} </span>
								<span>提交时间：<fmt:formatDate value="${list.adate}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
								<c:if test="${list.checkTime!=null }">
									<span>审核时间：<fmt:formatDate value="${list.checkTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
									<span>管理员：【${list.aUser.id },${list.aUser.admName }】</span>
								</c:if>	
							</td>
						</tr>
					</tbody>
				
					<tbody class="item_list" id="line_${list.id}">
						<tr>
							<td>
								<div class="pic_info">
									<div class="txt"><a href="javascript:showUser('${list.user.id}')" style="font-weight: bold;color:green;" id="text_${list.user.id }">${list.user.userName}</a></div>
								</div>
							</td>
							<td>${list.oldFreeCash}</td>
							<td>
								${list.freeCash }
							</td>
							<td>
								<c:choose>
									<c:when test="${list.status==1 }">等待审核</c:when>
									<c:when test="${list.status==2 }">通过</c:when>
									<c:when test="${list.status==3 }">不通过<br/><a href="javascript:Alert('${list.reason }')">查看原因</a></c:when>
									<c:otherwise>不需审核</c:otherwise>
								</c:choose>
							</td>
							<td>
								<c:if test="${list.status==0 }">
									-
								</c:if>
								<c:if test="${list.status==1 }">
									<a href="javascript:agree('${list.id }')">通过</a>
									<a href="javascript:reason('${list.id }')">不通过</a>
								</c:if>
								<c:if test="${list.status==2 }">
									<a href="javascript:;" style="cursor: point;color: grey;">通过</a>
									<a href="javascript:reason('${list.id }')">不通过</a>
								</c:if>
								<c:if test="${list.status==3 }">
									<a href="javascript:agree('${list.id }')">通过</a>
									<a href="javascript:;" style="cursor: point;color: grey;">不通过</a>
								</c:if>
							</td>
						</tr>
					</tbody>
				</c:forEach>
				<tfoot>
					<tr>
						<td colspan="5">
							<div class="page_nav" id="pagin">
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
						<td colspan="5">
							<p>没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</c:otherwise>
		</c:choose>

</table>