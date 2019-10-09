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
<table class="tb-list2" style="table-layout:fixed;" >
	<thead>
		<tr>
			<th width="26px">序号</th>
			<th>交易平台流水号</th>
			<th width="70px">提现金额</th>
			<th width="36px">状态</th>
			<th width="56px">区块高度</th>
			<th width="65px">确认时间</th>
			
			<th>支付中心流水号</th>
			<th width="70px">提现金额</th>
			<th width="36px">状态</th>
			<th width="56px">区块高度</th>
			<th width="65px">确认时间</th>
			<th width="46px">是否一致</th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${listDownloadAccount!=null}">
				<tbody>
					<tr class="space">
						<td colspan="12">
						</td>
					</tr>
				</tbody>
			
				<c:forEach items="${listDownloadAccount}" var="downloadAccount" varStatus="statu">
					<tbody class="item_list" >
						<tr>
							<td>
								<div style="text-align: left;">
									${statu.index + 1}
								</div>
							</td>
							<td style="word-wrap:break-word;">
								<div style="text-align: left;">
									${downloadAccount.bgId}
								</div>
							</td>
							<td>
								<div style="text-align: right;">
									<c:if test="${downloadAccount.bgTxAmount==downloadAccount.pmTxAmount}">
										<font><fmt:formatNumber value="${downloadAccount.bgTxAmount}" pattern="0.00000####"/></font>
									</c:if>
									<c:if test="${downloadAccount.bgTxAmount!=downloadAccount.pmTxAmount}">
										<font color="red"><fmt:formatNumber value="${downloadAccount.bgTxAmount}" pattern="0.00000####"/></font>
									</c:if>
								</div>
							</td>
							<td>
								<div style="text-align: left;">
									<c:if test="${downloadAccount.bgStatus==2}">
										<c:if test="${downloadAccount.bgStatus==downloadAccount.pmStatus}">
										<font>成功</font>
										</c:if>
										<c:if test="${downloadAccount.bgStatus!=downloadAccount.pmStatus}">
										<font color="red">成功</font>
										</c:if>
									</c:if>
									<c:if test="${downloadAccount.bgStatus==1}">
										<font color="red">失败</font>
									</c:if>
								</div>
							</td>
							<td>
								<div style="text-align: left;">
									<c:if test="${downloadAccount.bgBlockHeight>0}">
										<font>${downloadAccount.bgBlockHeight}</font>
									</c:if>
								</div>
							</td>
							<td>
								<div style="text-align: left;">
									${downloadAccount.bgConfigTime}
								</div>
							</td>
							
							<td style="word-wrap:break-word;">
								<div style="text-align: left;">
									${downloadAccount.pmId}
								</div>
							</td>
							<td>
								<div style="text-align: right;">
									<c:if test="${downloadAccount.bgTxAmount==downloadAccount.pmTxAmount}">
										<font><fmt:formatNumber value="${downloadAccount.pmTxAmount}" pattern="0.00000####"/></font>
									</c:if>
									<c:if test="${downloadAccount.bgTxAmount!=downloadAccount.pmTxAmount}">
										<font color="red"><fmt:formatNumber value="${downloadAccount.pmTxAmount}" pattern="0.00000####"/></font>
									</c:if>
								</div>
								
								
							</td>
							<td>
								<div style="text-align: left;">
									<c:if test="${downloadAccount.pmStatus==2}">
										<c:if test="${downloadAccount.bgStatus==downloadAccount.pmStatus}">
										<font>成功</font>
										</c:if>
										<c:if test="${downloadAccount.bgStatus!=downloadAccount.pmStatus}">
										<font color="red">成功</font>
										</c:if>
									</c:if>
									<c:if test="${downloadAccount.pmStatus==1}">
										<font color="red">失败</font>
									</c:if>
								</div>
							</td>
							<td>
								<div style="text-align: left;">
									<c:if test="${downloadAccount.pmBlockHeight>0}">
										<font>${downloadAccount.pmBlockHeight}</font>
									</c:if>
								</div>
							</td>
							<td>
								<div style="text-align: left;">
									${downloadAccount.pmConfigTime}
								</div>
							</td>
							<td>
								<div style="text-align: left;">
									<c:if test="${downloadAccount.amountFlag==0&&downloadAccount.pmStatus==downloadAccount.bgStatus}">
										<font>一致</font>
									</c:if>
									<c:if test="${downloadAccount.amountFlag!=0||downloadAccount.pmStatus!=downloadAccount.bgStatus}"><font color="red">不一致</font></c:if>
								</div>
							</td>
						</tr>
					</tbody>
				</c:forEach>
				<tfoot>
					<tr>
						<td colspan="12">
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
						<td colspan="12">
							<p id="exportFlag">没有符合要求的记录!</p>
						</td>
					</tr>
				</tbody>
			</c:otherwise>
		</c:choose>

</table>