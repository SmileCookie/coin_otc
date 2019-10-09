<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<script type="text/javascript">

$(function(){
	$(".item_list").each(function(i){
        $(this).mouseover(function(){
            $(this).css("background","#fff8e1");
        }).mouseout(function(){
        	  $(this).css("background","#ffffff");
        });
    });
});
</script>
<table class="tb-list2" style="width:100%">
	<thead>
		<tr>
			<th style="width:300px;">用户信息</th>
			<th>发送状态</th>
			<th>消息标题</th>
			<th>接收手机</th>
			<th>接收邮箱</th>
			<th style="width: 400px;">消息内容</th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${dataList!=null}">
				<tbody>
					<tr class="space">
						<td colspan="6">
						</td>
					</tr>
				</tbody>
			
				<c:forEach items="${dataList}" var="list" varStatus="statu">
					<tbody>
						<tr class="hd">
							<td colspan="6">
								<span>创建时间：<fmt:formatDate value="${list.addDate}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
								<span>创建IP：<a href="http://www.ip138.com/ips138.asp?ip=${list.sendIp }&action=2" target="_blank">${list.sendIp}</a></span>
							</td>
						</tr>
					</tbody>
				
					<tbody class="item_list" id="line_${list.id}">
						<tr>
							<td>
								<div class="txt">${list.userId}<br/>
								<%--	<a href="javascript:;" style="font-weight: bold;color:green;"><font id="text_${list.userId}">${list.userName}</font></a>--%>
								</div>
							</td>
							<td>
								<c:if test="${list.sendStat == 0}"><font color="red">未发送</font></c:if>
								<c:if test="${list.sendStat == 1}"><font color="orange">发送中</font></c:if>
								<c:if test="${list.sendStat == 2}">
									<font color="green">已成功</font>
									
									<br>
									<a href="javascript:reset('${list.id}');">重发</a>
								</c:if>
								<c:if test="${list.sendStat == 3}"><font color="grey">已失败</font></c:if>
							</td>
							<td>
								${list.title}
							</td>
							<td>
								${list.receivePhoneNumber}
							</td>
							<td>
								${list.receiveEmail}
							</td>
							<td>
								${list.cont}
							</td>
						</tr>
					</tbody>
				</c:forEach>
				<tfoot>
					<tr>
						<td colspan="6">
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
						<td colspan="6">
							<p>没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</c:otherwise>
		</c:choose>

</table>