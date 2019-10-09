<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<table class="tb-list2" id="ListTable">
	<thead>
		<tr> 
			<th>编号</th>
			<th>创建人</th>
			<th>日期/备注</th>
			<th>签名</th>
			<th>状态</th>
			<th>操作</th>
		</tr>
	</thead>
	<c:choose>
	 <c:when test="${fn:length(dataList)>0 }">
		<c:forEach items="${dataList }" var="list">
				<tbody>
					<tr class="space">
						<td colspan="6"></td>
					</tr>
				</tbody>
				<tbody class="item_list">
				<tr>
					<td>
						${list.myId}
					</td>
					<td>
						${list.admName}
					</td>
					<td>
						${list.date}
						<br>
						备注：${list.memo }
					</td>
					<td>
						<c:if test="${fn:length(list.outputs) >= 0 }">
							<c:forEach items="${list.outputs }" var="output">
									<a target="_blank" href="${list.currency=='btc'?'https://block.bitbank.com/address/':'http://qukuai.com/ltc/address/' }${output.key }">${output.key }</a>
									(<fmt:formatNumber value="${output.value / 100000000 }" pattern="0.00######"/>)
									<font color="red">(${output.name })</font><br>
							</c:forEach>
						</c:if>
						<c:if test="${fn:length(list.outputs) <= 0 }">
							<font color="red">无效签名</font>
						</c:if>			
					</td>
					<td>
						<c:if test="${list.status == '未使用' }"><font color="green">未使用</font></c:if>
						<c:if test="${list.status == '已过期' }"><font color="orange">已过期</font></c:if>
						<c:if test="${list.status == '已使用' }"><font color="red">已使用</font></c:if>
					</td>
					<td>
						<a href="javascript:confirmDel('${list.myId}');">删除</a> &nbsp;
						<a href="javascript:useIt('${list.myId}');">使用</a>
						<a href="javascript:addMemo('${list.myId}');">备注</a>
						<br>
						<c:if test="${list.status == '已使用'}"><a style="color: green;" href="javascript:succonfirm('${list.myId}')">账务录入</a></c:if>
					</td>
				</tr>
				</tbody>
			
			</c:forEach>
			<tfoot>
	        <tr>
	          <td colspan="6">
		         <div class="page_nav" id="pagin">
                     <div class="con">
		                ${pager}
		         	 </div>
                 </div>
			  </td>
			</tr>
	   </tfoot>
	   </c:when>
	   <c:otherwise>
			<tbody>
				<tr>
					<td colspan="6">
						<p>暂时没有符合要求的记录！</p>
					</td>
				</tr>
			</tbody>
	   </c:otherwise>
</c:choose>
</table>