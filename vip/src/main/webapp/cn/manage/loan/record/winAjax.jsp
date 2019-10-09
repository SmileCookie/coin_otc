<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<div class="detailBox">
<div class="detail-top clearfix">
	<h2>${L:l(lan,"融资融币借出-投资详情模式窗-标题-1")}</h2>
	<div class="ps1-close" onClick="Close();">×</div> 
</div>
<table class="table table-bordered table-list table-invest">
			<thead>
				<tr>
					<th>${L:l(lan,"融资融币借出-投资详情模式窗-投资详情表头-1")}</th>
					<th>${L:l(lan,"融资融币借出-投资详情模式窗-投资详情表头-2")}</th>
					<th>${L:l(lan,"融资融币借出-投资详情模式窗-投资详情表头-3")}</th>
					<th>${L:l(lan,"融资融币借出-投资详情模式窗-投资详情表头-4")}</th>
					<th>${L:l(lan,"融资融币借出-投资详情模式窗-投资详情表头-5")}</th>
					<th>${L:l(lan,"融资融币借出-投资详情模式窗-投资详情表头-6")}</th>
				</tr>
			</thead>
			<tbody> 
			<c:choose>
				<c:when test="${lists != null}">
					<c:forEach items="${lists}" var="item" varStatus="status">
						
							<tr class="bd">
								<td>${status.index+1}</td>
								<td id="actionTd" >
									<span class="loan-label ${item.status == 1 ? 'loan-yellow':item.status == 2 ? 'loan-green':'loan-red'}">${item.recordStatusShow}</span>
								</td>
								<td>
									<fmt:formatDate value="${item.createTime }" pattern="${lan == 'en'? 'MM-dd-yyyy HH:mm':'yyyy-MM-dd HH:mm'}"/>
								</td>
								<!--<td>
									${item.getFt().propTag}
									${item.getFt().unitTag}
								</td>-->
								<td>
									<fmt:formatNumber value="${item.amount }" pattern="0.####" />/<fmt:formatNumber value="${item.hasRepay }" pattern="0.####" />
								</td>
								<td>
									<fmt:formatNumber value="${item.hasRepay/item.amount*100}" pattern="###" var="rateRange" />
									<span class="schedule">
										<i><u style="width:${rateRange}%;"></u></i>
									</span>
									<span class="schedpt">${rateRange}%</span>
								</td>
								<td>
						  	 		<c:if test="${!isIn }">
							 			<a href="javascript:;" onclick="parent.JuaBox.frame('/manage/loan/repay?id=${item.id }' ,{width:800});">${L:l(lan,"融资融币借出-还币详情模式窗-标题-1")}</a>
						  	 		</c:if>
								</td>
							</tr>
						
					</c:forEach>
				</c:when>
				<c:otherwise>
					<tr>
						<td colspan="7">
							<div class="bk-norecord">
								<p><i class='iconfont2 mr5'>&#xe653;</i>${L:l(lan,'暂时没有相关记录')}</p>
							</div>
						</td>
					</tr>
				</c:otherwise>
			</c:choose>
			</tbody>
		</table>
	<c:if test="${pager!=''}">
	    <div id="page_navA" class="page_nav">
		  <div class="con">${pager}</div>
		</div>
	</c:if>
</div>