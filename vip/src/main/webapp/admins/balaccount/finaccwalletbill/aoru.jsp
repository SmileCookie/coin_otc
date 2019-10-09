<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<jsp:include page="/admins/top.jsp" />
	<script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>

	<style type="text/css">
		.form-tit {width: 100px;}
		.form-line {margin-left:20px;}
		.col-main .prompt.b_yellow.remind2.pl_35 {
			padding:0 10px 20px 10px;
		}
		.form-btn {
			padding: 15px 0 0 121px;
		}
		.select_wrap select {
			color: #6D6D6D;
			float: left;

			padding: 5px;
		}

		.bankbox{ padding:15px;}
		.bankbox .bd {
			padding-right: 20px;
			padding-left: 20px;
		}
		.formlist .formline {
			overflow:hidden;
			padding-bottom:8px;
			clear: both;
		}
		span.txt{float:left;margin-right: 5px;}
	</style>

	<script type="text/javascript">
		$(function(){
			$("#bankBox").Ui();

		});
	</script>
</head>

<body >
<div class="bankbox" id="bankBox">

	<div class="bankbox_bd">
		<table class="tb-list2"  >
			<thead>
			<tr>
				<th width="26px">序号</th>
				<th>交易流水号</th>
				<th>地址</th>
				<th>金额</th>
				<th>资金类型</th>
				<th>钱包名称</th>
				<th>钱包类型</th>
				<th>交易金额</th>
				<th>网络费</th>
				<th>钱包余额</th>
				<th>交易类型</th>
				<th>区块高度</th>
				<th>确认时间</th>
			</tr>
			</thead>
			<c:choose>
				<c:when test="${finAccWalletBillDetails!=null}">
					<tbody>
					<tr class="space">
						<td colspan="8">
						</td>
					</tr>
					</tbody>

					<c:forEach items="${finAccWalletBillDetails}" var="finAccWalletBillDetail" varStatus="statu">
						<tbody class="item_list" >
						<tr>
							<td>
								<div style="text-align: left;">
										${statu.index + 1}
								</div>
							</td>
							<td style="word-wrap:break-word;">
								<div style="text-align: left;">
										${finAccWalletBillDetail.txIdN}
								</div>
							</td>
							<td style="word-wrap:break-word;">
								<div style="text-align: left;">
										${finAccWalletBillDetail.toAddress}
								</div>
							</td>
							<td style="word-wrap:break-word;">
								<div style="text-align: left;width: 100px;">
										<font><fmt:formatNumber value="${finAccWalletBillDetail.txNAmount}" pattern="0.00000####"/></font>
								</div>
							</td>
							<td style="word-wrap:break-word;">
								<div style="text-align: left;width: 100px;">
										${finAccWalletBillDetail.fundsTypeName}
								</div>
							</td>
							<td>
								<div style="text-align: left;width: 120px;">
										${finAccWalletBillDetail.walName}
								</div>
							</td>
							<td>
								<div style="text-align: left;width: 120px;">
										${finAccWalletBillDetail.walTypeName}
								</div>
							</td>
							<td>
								<div style="text-align: right;">
									<font><fmt:formatNumber value="${finAccWalletBillDetail.txAmount}" pattern="0.00000####"/></font>
								</div>
							</td>
							<td>
								<div style="text-align: right;">
									<font><fmt:formatNumber value="${finAccWalletBillDetail.fee}" pattern="0.00000####"/></font>
								</div>
							</td>
							<td>
								<div style="text-align: right;">
									<font><fmt:formatNumber value="${finAccWalletBillDetail.walBalance}" pattern="0.00000####"/></font>
								</div>
							</td>
							<td>
								<div style="text-align: left;width: 120px;">
										${finAccWalletBillDetail.dealTypeName}
								</div>
							</td>
							<td>
								<div style="text-align: left;">
										${finAccWalletBillDetail.blockHeight}
								</div>
							</td>

							<td style="word-wrap:break-word;">
								<div style="text-align: left;width: 100px;">
									<fmt:formatDate value="${finAccWalletBillDetail.configTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
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

	</div>
</div>

</body>
</html>
