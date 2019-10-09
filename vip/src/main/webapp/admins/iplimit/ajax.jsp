<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" style="width:100%">
	<thead>
		<tr>
			<th style="width: 15px"></th>
			<th>编号</th>
			<th>IP地址</th>
			<th>添加时间</th>
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
						        <a title="批量删除" href="javascript:delMore()" id="del_Btn" class="Abtn delete">批量删除</a>
							</div>
						</td>
					</tr>
				</tbody>
			
				<c:forEach items="${dataList}" var="list" varStatus="statu">
					<%--
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
								<span>提交时间：<fmt:formatDate value="${list.submitTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
								<c:if test="${list.checkTime!=null }">
									<span>审核时间：<fmt:formatDate value="${list.checkTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
									<span>管理员：【${list.aUser.id },${list.aUser.admName }】</span>
								</c:if>	
							</td>
						</tr>
					</tbody>
					--%>
					<tbody class="item_list" id="line_${list.id}">
						<tr>
							<td class="hd">
								<input type="checkbox" style="display:none;" value="${list.id}" id="${statu.index}" class="checkItem"/><label id="ck_${statu.index}" class="checkbox" onclick="changeCheckBox('${statu.index}')"></label>
							</td>
							<td>
								${list.id }
							</td>
							<td>${list.ip}</td>
							<td>
								<fmt:formatDate value="${list.addTime }" pattern="yyyy-MM-dd HH:mm:ss"/>
							</td>
							<td>
								<c:if test="${list.type==0 }">
									<a style="color:green;" href="javascript:update('${list.id }')">转黑名单</a>
								</c:if>
								<c:if test="${list.type==1 }">
									<a style="color:red;" href="javascript:update('${list.id }')">转白名单</a>
								</c:if>
								<a href="javascript:vip.list.del({id:'${list.id }'})">删除</a>
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