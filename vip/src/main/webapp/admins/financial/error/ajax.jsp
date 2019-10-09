<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" style="width:100%;table-layout: fixed;">
	<thead>
		<tr>
			<th>账户</th>
			<th>收支类型</th>
			<th>人民币</th>
			<th>用途来源</th>
			<th>用户名</th>
			<th>备注</th>
			<th>状态</th>
			<th>操作</th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${dataList!=null}">
				<tbody>
					<tr class="space">
						<td colspan="8">
						</td>
					</tr>
				</tbody>
			
				<c:forEach items="${dataList}" var="list" varStatus="statu">
					<tbody>
						<tr class="space">
							<td colspan="8"></td>
						</tr>
					</tbody>
					<tbody>
						<tr class="hd">
							<td colspan="8">
								<span>编号：${list.id} </span>
								<span>创建时间：<fmt:formatDate value="${list.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
								<span>创建管理员：【${list.aUser.id}，${list.aUser.admName }】</span>
								<span>IP：<a href="http://www.ip138.com/ips138.asp?ip=${list.ip }&action=2" target="_blank">${list.ip}</a></span>
							</td>
						</tr>
					</tbody>
				
					<tbody class="item_list" id="line_${list.id}">
						<tr>
							<td>
								${list.account.name}
							</td>
							<td>
								收入
							</td>
							<td>
							 	<c:if test="${list.money>0 }">
								 	<font color="green">+<fmt:formatNumber value="${list.money }" pattern="##,##0.00##"/></font>
							 		<br/>
							 		<font color="${list.commission>0?'green':list.commission<0?'orange':'' }">手续费：<fmt:formatNumber value="${list.commission }" pattern="0.0000" /></font>
							 	</c:if>
							 	<c:if test="${list.money==0 }">-</c:if>
							</td>
							<td>
								${list.useType.name}
								<c:if test="${list.connectionId>0 }">
									<br/>
									编号：${list.connectionId }
								</c:if>
							</td>
							<td>
								${fn:length(list.userName)>0?list.userName:"-"}
							</td>
							<td style="word-break: break-all; WORD-WRAP: break-word;">
								${fn:length(list.memo)>0?list.memo:"-"}
							</td>
							<td>
								<c:if test="${list.status==0 }">系统未录入</c:if>
								<c:if test="${list.status==1 }"><font color="green">已处理</font></c:if>
								<c:if test="${list.status==2 }"><font color="red">已取消</font></c:if>
							</td>
							<td>
								<c:if test="${list.status==0 }">
									<a href="javascript:deal(${list.id})">处理</a><br/>
									<a href="javascript:cancel(${list.id})">取消</a>
								</c:if>
								<c:if test="${list.status>0 }">-</c:if>
							</td>
						</tr>
					</tbody>
				</c:forEach>
				<tfoot>
					<tr>
						<td colspan="8">
							<div class="page_nav" id="pagin">
								<div class="con">
									<c:if test="${pager=='' }">共${itemCount }项</c:if>
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
						<td colspan="8">
							<p>没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</c:otherwise>
		</c:choose>

</table>