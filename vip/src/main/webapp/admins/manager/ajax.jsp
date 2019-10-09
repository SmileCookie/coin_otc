<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<table class="tb-list2" id="ListTable">
	<thead>
		<tr>
			<th>登录名/编号</th>
			<th>真实姓名</th>
			<th>邮箱</th>
			<th>电话</th>
			<th>所属角色</th>
			<th>最后登录时间</th>
			<th>性别</th>
			<th>账户状态</th>
			<th>操作</th>
		</tr>
	</thead>
	<c:choose>
		<c:when test="${dataList!=null}">
			<c:forEach items="${dataList}" var="list">
				<tbody class="item_list" id="line_${list.admId}" >
				<tr>
					<td>
						<div class="pic_info">
							<div class="pic"><img src="${list.admPhoto }"/></div>
							<div class="txt"><a href="#" id="text_${list.admId}">${list.admName} </a></div>
							<div class="txt">编号：${list.admId}</div>
						</div>
					</td>
					<td>
						${list.admUName}
					</td>
					<td>
						${list.email}
					</td>
					<td>
						${list.telphone}
					</td>
					<td>
						${list.ar.roleName}
					</td>
					<td>
						${list.lastLoginTime}
						<br>
						${list.lastLoginIp}
					</td>
					<td>
						<c:choose>
							<c:when test="${list.admSex==1}"> 男</c:when>
							<c:when test="${list.admSex==2}"> 女</c:when>
							<c:otherwise>阴阳人</c:otherwise>
						</c:choose>
					</td>
					<td>
						${list.status}
						<br>
						<c:if test="${list.variablesCustomer == 0}">
							<font style="color:red;">无效客服</font>
						</c:if>
						<c:if test="${list.variablesCustomer == 1}">
							<font  style="color:green;">有效客服</font>
						</c:if>
					</td>
					<td>
						<a href="javascript:doDel('${list.admId }');">删除</a> &nbsp;
						<a href="javascript:vip.list.aoru({id : '${list.admId }', width : 600 , height : 660});">修改</a>
						<br>
						<a href="javascript:biaoZhiKeFu(${list.variablesCustomer == 0 ? 1 : 0 } , ${list.admId });" title="标识是否可列入有效客服队列！">
							标识为${list.variablesCustomer == 0 ? '有效' : '无效' }
						</a>
					</td>
				</tr>
				</tbody>
			</c:forEach>
			<tfoot>
	        <tr>
	          <td colspan="9">
		         <div class="page_nav" id="pagin">
                     <div class="con">
		                <span class="page_num">共${itemCount}项</span>
		                <c:if test="${pager!=null}">${pager}</c:if>
		             </div>
                 </div>
			  </td>
			</tr>
	   </tfoot>
		</c:when>
			
		<c:otherwise>
			<tbody>
				<tr>
					<td colspan="9">
						<p>暂时没有符合要求的记录！</p>
					</td>
				</tr>
			</tbody>
		</c:otherwise>
	</c:choose>
</table>