<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" id="ListTable" style="width: 100%">
	<tr>
		<th >序号</th>
		<th >监控时间</th>
		<th >资金类型</th>
		<th >检查用户数</th>
		<th >正常用户数</th>
		<th >异常用户数</th>
		<th >检查结果</th>
		<th >处理备注</th>
		<th >操作</th>
	</tr>
	<c:choose>
		<c:when test="${dataList!=null}">
			<c:forEach items="${dataList}" var="list" varStatus="statu">
				<tbody id="row${list.id }">
				<tr class="item_list_bd item_list_bgcolor">
					<td>${statu.index + 1}</td>
					<td><fmt:formatDate value="${list.monTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td>
						${list.coinName }
					</td>
					<td>${list.checkUserNum }</td>
					<td>${list.correctUserNum }</td>
					<td>
						<c:choose>
						<c:when test="${list.errorUuserNum == 0}">
							${list.errorUuserNum}
						</c:when>
						<c:otherwise>
							<a style="color:red;font-weight: bold;" href="javascript:aoru('${list.ucmId}')" class="confirmTrue">${list.errorUuserNum}</a>
						</c:otherwise>
						</c:choose>
					</td>
					<td>
						<c:if test="${list.checkResult==0 }">${"默认"}</c:if>
						<c:if test="${list.checkResult==1 }">${"正常"}</c:if>
						<c:if test="${list.checkResult==2 }">${"异常"}</c:if>
					</td>
					<td width="200">
						<c:choose>
							<c:when test="${list.errorUuserNum == 0}">
								---
							</c:when>
							<c:otherwise>
								<textarea style="width:100%; " disabled>${list.dealRemark}</textarea>
							</c:otherwise>
						</c:choose>
					</td>
					<td>
						<c:choose>
							<c:when test="${list.errorUuserNum > 0 }">
								<a href="#" onclick="dealRemark(this)">处理</a>
								<a href="#" onclick="saveRemark(this,'${list.id}')">保存</a>
							</c:when>
							<c:otherwise>
								---
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				</tbody>
			</c:forEach>
			<tbody class="operations">
			<tr>
				<td colspan="9">
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
				<td colspan="9">
					<p>暂时没有符合要求的记录！</p>
				</td>
			</tr>
			</tbody>
		</c:otherwise>
	</c:choose>
</table>