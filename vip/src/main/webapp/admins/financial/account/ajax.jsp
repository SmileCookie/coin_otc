<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<style type="text/css">
.btn2 a:hover {
    background: none repeat scroll 0 0 #6DC03C;
    color: #FFFFFF;
}
.btn2 a {
    background: none repeat scroll 0 0 #8DC03C;
    border-radius: 3px 3px 3px 3px;
    color: #FFFFFF;
    height: 31px;
    line-height: 32px;
    padding: 5px 10px;
}
.btn2 a.red {
    background: none repeat scroll 0 0 #E55E48;
}
.btn2 a.red:hover {
    background: none repeat scroll 0 0 #C55E48;
}
</style>
<table class="tb-list2" style="width:100%;table-layout: fixed;">
	<thead>
		<tr>
			<th>账户名称</th>
			<th>账户余额</th>
			<th>费率</th>
			<th>备注</th>
			<th>操作</th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${dataList!=null}">
				<tbody>
					<tr class="space">
						<td colspan="5">
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
								<span>编号：${list.id} </span>
								<span>创建时间：<fmt:formatDate value="${list.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
								<span>所属管理员：【${list.aUser.id}，${list.aUser.admName }】</span>
							</td>
						</tr>
					</tbody>
				
					<tbody class="item_list" id="line_${list.id}">
						<tr>
							<td>
								${list.name }
							</td>
							<td>
								<font color="green">
								 	<fmt:formatNumber value="${list.funds }" pattern="#,##0.00######"/>
								</font>
								<c:if test="${list.exceptAmount>0 }">
								<br/>
								未录入：<font color="red"><fmt:formatNumber value="${list.exceptAmount }" pattern="#,000.0000##"/></font>
								</c:if>
							</td>
							<td>
								<font <c:if test="${list.rate>0||list.rate<0 }">color="red"</c:if>><fmt:formatNumber value="${list.rate }" pattern="0.0000####"/></font>
							</td>
							<td style="word-break: break-all; WORD-WRAP: break-word;">
								${fn:length(list.memo)>0?list.memo:"-"}
							</td>
							<td>
								<c:if test="${list.type==1 || list.type==3}">
								<div class="btn2">
									<c:if test="${list.dayTag<dayTag0||list.dayTag==dayTag24 }">
										<a href="javascript:balance(${list.id }, ${dayTag0 }, ${list.type });">上班结算</a>
									</c:if>
									<c:if test="${list.dayTag==dayTag0 && list.dayTag<dayTag24 }">
										<a class="red" href="javascript:balance(${list.id }, ${dayTag24 }, ${list.type });">下班结算</a>
									</c:if>
								</div>
								</c:if>
							
								<c:if test="${logAdmin.rid==1 || logAdmin.rid==6}">
									<a href="javascript:vip.list.aoru({id:${list.id },height:618});">编辑</a>
									<a href="javascript:vip.list.del({id:${list.id }});">删除</a>
									<br/>
								</c:if>
								<c:if test="${!list.isDefault }">
									<a href="javascript:setDefault(${list.id }, 1);">设置默认账户</a>
								</c:if>
								<c:if test="${list.isDefault }">
									<a class="red" href="javascript:setDefault(${list.id }, 0);">取消默认</a>
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
						<td colspan="5">
							<p>没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</c:otherwise>
		</c:choose>

</table>