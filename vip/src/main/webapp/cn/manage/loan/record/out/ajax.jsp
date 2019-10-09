<%@ page session="false" language="java" import="java.util.*"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<style>
.ch_table dd {
	height: 45px;
	line-height: 45px;
	overflow: hidden;
}

.ch_table .u1 {
	width: 12%;
	text-align: center;
}

.ch_table .u2 {
	width: 9%;
	text-align: center;
}

.ch_table .u3 {
	width: 15%;
	text-align: center;
}

.ch_table .u4 {
	width: 10%;
	text-align: center;
}

.ch_table .u5 {
	width: 10%;
	text-align: center;
}

.ch_table .u6 {
	width: 10%;
	text-align: center;
}

.ch_table .u7 {
	width: 10%;
	text-align: center;
}

.ch_table .u8 {
	width: 14%;
	text-align: center;
}

.ch_table .u9 {
	width: 10%;
	text-align: center;
}

dt span {
	font-size: 14px;
}
</style>
<script type="text/javascript">
$(function(){
	vip.list.basePath = "/manage/loan/record/";
});
</script>
<!-- 已成功投资 -->

<div class="table-responsive">
	<div class="bk-search text-left" style="padding: 0px 15px 15px;">
		<form autocomplete="off" class="form-inline" name="searchForm"
			id="formSearch2">
			<div class="form-group form-group-sm mr10">
				<label for="fundsType">${L:l(lan,'投资类型')}：</label> <input type="hidden"
					id="fundsType" name="fundsType" value="${param.fundsType }">
				<div class="input-group">
					<div class="dropdown">
						<button class="btn btn-default dropdown-toggle btn-sm"
							type="button" data-toggle="dropdown" aria-haspopup="true"
							aria-expanded="true" style="min-width: 100px;">
							<span class="text-g">${L:l(lan,'不限')}</span> <span class="caret"></span>
						</button>
						<ul class="dropdown-menu" data-bind="fundsType">
							<li data-value=""><a role="button">${L:l(lan,'不限')}</a></li>
							<c:if test="${coinMaps!=null}">
								<c:forEach items="${coinMaps}" var="coin">
									<li data-value="${coin.value.fundsType}"><a role="button">${coin.value.propCnName}
											${coin.value.propTag}</a></li>
								</c:forEach>
							</c:if>
						</ul>
					</div>
				</div>
			</div>

			<div class="form-group form-group-sm mr10">
				<label for="riskType">${L:l(lan,'风险控制')}：</label> <input
					type="hidden" id="riskType" name="riskType"
					value="${param.riskType }">
				<div class="input-group">
					<div class="dropdown">
						<button class="btn btn-default dropdown-toggle btn-sm"
							type="button" data-toggle="dropdown" aria-haspopup="true"
							aria-expanded="true" style="min-width: 100px;">
							<span class="text-g">${L:l(lan,'不限')}</span> <span class="caret"></span>
						</button>
						<ul class="dropdown-menu" data-bind="riskType">
							<li data-value=""><a role="button">${L:l(lan,'不限')}</a></li>
							<li data-value="1"><a role="button">${L:l(lan,'自担风险')}</a></li>
							<li data-value="2"><a role="button">${L:l(lan,'只要本金')}</a></li>
						</ul>
					</div>
				</div>
			</div>



			<div class="form-group form-group-sm mr10">
				<label for="status">${L:l(lan,'状态')}：</label> <input type="hidden"
					id="status" name="status" value="${param.status }">
				<div class="input-group">
					<div class="dropdown">
						<button class="btn btn-default dropdown-toggle btn-sm"
							type="button" data-toggle="dropdown" aria-haspopup="true"
							aria-expanded="true" style="min-width: 100px;">
							<span class="text-g">${L:l(lan,'不限')}</span> <span class="caret"></span>
						</button>
						<ul class="dropdown-menu" data-bind="status">
							<li data-value=""><a role="button">${L:l(lan,'不限')}</a></li>
							<li data-value="1"><a role="button">${L:l(lan,'还款中')}</a></li>
							<li data-value="2"><a role="button">${L:l(lan,'已还款')}</a></li>
							<li data-value="3"><a role="button">${L:l(lan,'需要平仓')}</a></li>
							<li data-value="4"><a role="button">${L:l(lan,'平仓还款')}</a></li>
						</ul>
					</div>
				</div>
			</div>

			<button type="button" class="btn btn-primary btn-sm mr10"
				id="doSearchBtn" onclick="vip.list.search({formId:'formSearch2'})"
				style="min-width: 100px;">
				<i class="fa fa-search mr5"></i>${L:l(lan,'筛选')}
			</button>
			<button type="reset" class="btn btn-default btn-sm" id="reSetBtn"
				style="min-width: 100px;"
				onclick="vip.list.resetForm({formId:'formSearch2'})">
				<i class="fa fa-repeat mr5"></i>${L:l(lan,'重置')}
			</button>
		</form>
	</div>

	<table class="table table-striped table-bordered table-hover table-2x">
		<thead>
			<tr>
				<th width="12%">${L:l(lan,'时间')}</th>
				<th width="9%">${L:l(lan,'类型')}</th>
				<th width="15%">${L:l(lan,'投资/已还')}</th>
				<th width="10%">${L:l(lan,'利率')}</th>
				<th width="10%">${L:l(lan,'利率形式')}</th>
				<th width="10%">${L:l(lan,'风险控制')}</th>
				<th width="10%">${L:l(lan,'状态')}</th>
				<th width="10%">${L:l(lan,'操作')}</th>
			</tr>
		</thead>
		<tbody>
			<c:choose>
				<c:when test="${fn:length(lists)>0}">
					<c:forEach items="${lists }" var="item" varStatus="stat">
						<tr>
							<c:if test="${item.transferStartDate == null}">
								<td
									title="<fmt:formatDate value="${item.createTime }" pattern="yyyy-MM-dd HH:mm"/>">
									<fmt:formatDate value="${item.createTime }"
										pattern="yyyy-MM-dd HH:mm" />
								</td>
							</c:if>
							<c:if test="${item.transferStartDate != null}">
								<td
									title="<fmt:formatDate value="${item.transferStartDate }" pattern="yyyy-MM-dd HH:mm"/>">
									<fmt:formatDate value="${item.transferStartDate }"
										pattern="yyyy-MM-dd HH:mm" />
								</td>
							</c:if>
							<td>${item.getFt().propTag }</td>
							<fmt:formatNumber var="hasRepay" value="${item.hasRepay }"
								pattern="0.00####" />
							<fmt:formatNumber var="amount" value="${hasRepay+item.amount }"
								pattern="0.00####" />
							<td>${item.getFt().propTag } ${amount }/<font color="green">${hasRepay }</font></td>
							<td><fmt:formatNumber value="${item.rateShow }"	pattern="0.0##" />%</td>
							<td>${L:l(lan,item.interestRateFormShow)}</td>
							<td><p
								title="${L:l(lan,'网站收取手续费') }${item.riskType.fwfScale * 100 }%">
									${L:l(lan,item.riskType.value)} </p> <c:if
									test="${item.riskType.value == null}">-</c:if></td>
							<td><font style="color: ${item.recordStatus.color}">
								<c:choose>
									<c:when test="${item.recordStatus.value  eq '已还款'}">
				  	 	  				 	${L:l(lan,'已还款') }
				  	 				</c:when>
									<c:otherwise>
				  	 					${item.recordStatus.value }
				  	 				</c:otherwise>
								</c:choose> 
								</font>
							</td>
							<td><c:if test="${isIn }">
									<c:choose>
										<c:when test="${item.status == 1}">
											<a href="javascript:;" onclick="vip.p2p.repay(${item.id }, ${pageNo })">${L:l(lan,'还款')}</a>
											<a href="javascript:;" onclick="JuaBox.frame('/manage/loan/repay?id=${item.id }',{width:680});">${L:l(lan,'查看')}</a>
										</c:when>
										<c:when test="${item.status== 2 || item.status == 4}">
											<a href="javascript:;"
												onclick="JuaBox.frame('/manage/loan/repay?id=${item.id }',{width:680});">${L:l(lan,'查看')}</a>
										</c:when>
										<c:otherwise>-</c:otherwise>
									</c:choose>
								</c:if> 
								<c:if test="${!isIn }">
									<a href="javascript:;"
										onclick="JuaBox.frame('/manage/loan/repay?id=${item.id }&date=${item.transferStartDate}&host=1',{width:680});">${L:l(lan,'查看')}</a>
									<c:if test="${item.status == 1}">
										<c:choose>
											<c:when test="${item.tstatus == 0}">
											</c:when>
											<c:otherwise>
											</c:otherwise>
										</c:choose>

									</c:if>
								</c:if>
							</td>
						</tr>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<tr>
						<td colspan="9"><div class="bk-norecord">
								<p>
									<i class="bk-ico info"></i>${L:l(lan,'暂时没有相关记录。')}</p>
							</div>
						</td>
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
//绑定下拉取值事件
$("#formSearch2 .dropdown-menu").on("click","li",function(){
	var dropMenu = $(this).parents(".dropdown");
	var bindInput = dropMenu.find(".dropdown-menu").data("bind");
	
	$("#formSearch2 #" + bindInput).val($(this).data("value"));
	$(dropMenu).find(".text-g").text($(this).text());
});


$(function(){
	$("#formSearch2 .dropdown").each(function(){
	    var _this = $(this).find(".dropdown-menu").data("bind");
		$(this).find(".dropdown-menu").find("li").each(function(){
		  if($(this).data("value")+"" == $("#formSearch2 #" + _this).val()){
		    $(this).parents(".dropdown").find(".text-g").text($(this).text());
		  }
		})
	});

});

</script>

