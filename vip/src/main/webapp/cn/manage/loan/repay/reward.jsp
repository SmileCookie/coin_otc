<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<style>
<!--
#bb {
	font-size: 18px;
}

.ltt {
	text-align: center;
	font-size: initial;
}
-->
</style>


<!-- 我发起的投资页 -->
<div class="table-responsive">
	<div class="bk-search text-left" style="padding: 0px 15px 15px;">
		<form autocomplete="off" class="form-inline" name="searchForm" id="formSearch_2">
			<input type="hidden" id="fundsType2" name="fundsType" value="${param.fundsType }">
			<div class="form-group form-group-sm mr10">
				<div class="dropdown">
					<div class="drop-manu" data-bind="fundsType">
						<button type="button" id="bb" data-value="">${L:l(lan,'全部')}</button>
					</div>
				</div>
			</div>
			<div class="form-group form-group-sm mr10">
				<div class="dropdown">
					<div class="drop-manu" data-bind="fundsType">
						<button type="button" id="bb" data-value="1">${L:l(lan,'人民币RMB')}</button>
					</div>
				</div>
			</div>
			<div class="form-group form-group-sm mr10">
				<div class="dropdown">
					<div class="drop-manu" data-bind="fundsType">
						<button type="button" id="bb" data-value="2">${L:l(lan,'比特币BTC')}</button>
					</div>
				</div>
			</div>
			<div class="form-group form-group-sm mr10">
				<div class="dropdown">
					<div class="drop-manu" data-bind="fundsType">
						<button type="button" id="bb" data-value="3">${L:l(lan,'莱特币LTC')}</button>
					</div>
				</div>
			</div>
			<div class="form-group form-group-sm mr10">
				<div class="dropdown">
					<div class="drop-manu" data-bind="fundsType">
						<button type="button" class="bb" id="bb" data-value="4">${L:l(lan,'以太币ETH')}</button>
					</div>
				</div>
			</div>
		</form>
	</div>

	<table class="table table-striped table-bordered table-hover table-2x">
		<thead>
			<tr>
				<th width="6%">${L:l(lan,'时间')}</th>
				<th width="8%">${L:l(lan,'类型')}</th>
				<th width="8%">${L:l(lan,'日收入')}</th>
				<th width="8%">${L:l(lan,'日收入(折合BTC)')}</th>
				<th width="8%">${L:l(lan,'进度')}</th>
			</tr>
		</thead>
		<tbody>
			
			<c:choose>
				<c:when test="${fn:length(lists)>0}">
					<c:forEach items="${lists }" var="list" varStatus="stat">
						<tr>
							<td class="ltt">${list.date }</td>
							<td class="ltt">${list.allFt }</td>
							<td class="ltt"><span style="width: 80px; display: inline-block; text-align: right; margin-right: 30px;">
									<fmt:formatNumber value="${list.liXi}" pattern="0.00" />
									${list.onrFt }
							</span></td>
							<td class="ltt"><span style="width: 80px; display: inline-block; text-align: right;">
									<fmt:formatNumber value="${list.convertRMB }" pattern="0.00" /> ¥
							</span></td>
							<td class="ltt"><span class="schedule"> 
 							<i>
								<u style="width:<fmt:formatNumber value='${max>0?(list.convertRMB / max):0}'  type='percent'  />"></u>
								</i>
							</span>
							<fmt:formatNumber value='${max>0?(list.convertRMB / max):0}'  type='percent' /></td>
						</tr>

					</c:forEach>
				</c:when>
				<c:otherwise>
					<tr>
						<td colspan="9"><div class="bk-norecord">
								<p>
									<i class="bk-ico info"></i>${L:l(lan,'暂时没有相关记录')}。
								</p>
							</div></td>
					</tr>
				</c:otherwise>
			</c:choose>
		</tbody>
	</table>
	<c:if test="${pager!=''}">
		<div id="page_navA" class="page_nav">
			<div class="con_" style="float: right;">${pager}</div>
		</div>
	</c:if>
</div>
<script type="text/javascript">

vip.list.basePath = "/manage/loan/repay/reward/";
// 绑定筛选值事件
$('#formSearch_2').on('click', 'button', function() {
	console.log("on");
	$("#fundsType2").val($(this).data("value"));
	// 	单击#bb触发##but
	vip.list.search({formId:'formSearch_2'})
});

</script>

