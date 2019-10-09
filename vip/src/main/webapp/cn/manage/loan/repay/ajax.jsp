<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<div class="detailBox">
<div class="detail-top clearfix">
	<h2>${L:l(lan,"融资融币借出-还币详情模式窗-标题-1")}</h2>
	<div class="ps1-close" onClick="Close(this);">×</div> 
	<p>${L:l(lan,"融资融币借出-还币详情模式窗-副标题-1")}</p>
</div>
<table class="table table-bordered table-list">
	<thead>
		<tr>
			<th style="text-align: center;">${L:l(lan,'融资融币借出-还币详情模式窗-还币详情表头-1')}</th>
			<th style="text-align: center;">${L:l(lan,'融资融币借出-还币详情模式窗-还币详情表头-2')}</th>
			<th class="dikouyc" style="text-align: center;">${L:l(lan,'融资融币借出-还币详情模式窗-还币详情表头-3')}</th>			
			<th style="text-align: center;">${L:l(lan,'融资融币借出-还币详情模式窗-还币详情表头-4')}</th>
			<th style="text-align: center;">${L:l(lan,'融资融币借出-还币详情模式窗-还币详情表头-5')}</th>
			<th style="text-align: center;">${L:l(lan,'融资融币借出-还币详情模式窗-还币详情表头-6')}</th>			
		</tr>
		<c:if test="${isIn eq 1}">
			<tr class="space" id="batch" style="display: none;">
				<td colspan="6" style="text-align: left; padding-top: 8px; padding-left: 3px;">
					<a class="common-small-button_style" style="color: #0088CC;" href="javascript:batchRepay();">${L:l(lan,'批量还款')}</a>
				</td>
			</tr>
		</c:if>
	</thead>
	<tbody>
	<c:choose>
		<c:when test="${lists != null}">
			<c:forEach items="${lists}" var="list" varStatus="status">
					<tr class="bd">
						<td>
							${status.index+1}
						</td>
						<td>
							<c:choose>
								<c:when test="${list.actureDate != null}">
									<fmt:formatDate value="${list.actureDate }" pattern="${lan == 'en'? 'MM-dd-yyyy HH:mm':'yyyy-MM-dd HH:mm'}" />
								</c:when>
								<c:otherwise>
											--
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${list.liXi le 0}">
									${L:l(lan,'0')}
								</c:when>
								<c:otherwise>
									<fmt:formatNumber value="${list.liXi}" pattern="0.00####" />
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${list.benJin le 0}">
									${L:l(lan,'0')}
								</c:when>
								<c:otherwise>
									<fmt:formatNumber value="${list.benJin }" pattern="0.00####" />
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<fmt:formatNumber value="${list.liXi-0+list.benJin}" pattern="0.00####" />
						</td>
						<td>
							<fmt:formatNumber value="${list.accruedRepay/list.amount*100}" pattern="###" />%
						</td>
					</tr>
				
			</c:forEach>
		</c:when>
		<c:otherwise>
				<tr>
					<td colspan="6">
						<div class="bk-norecord bk-norecordIn">
							<p><i class='iconfont2 mr5'>&#xe653;</i>${L:l(lan,'暂时没有相关记录')}</p>
						</div>
					</td>
				</tr>
		</c:otherwise>
	</c:choose>
	</tbody>
</table>
<div id="page_navA" class="page_nav">
	<div class="con">
		<c:if test="${pager!=null}">${pager}</c:if>
	</div>
</div>
</div>