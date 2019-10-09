<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>

<!-- style Start -->
<style>
<!--
#btn_chk {
	zoom: 130%;
}

#chkid {
	zoom: 130%;
}
-->
</style>
<!-- style End -->

<!-- 我发起的投资页 -->
<div class="table-responsive">
	<div id="myupdate" class="bk-search text-left"
		style="padding: 0px 15px 15px;">
		<form autocomplete="off" class="form-inline" name="searchForm"
			id="formSearch1">
			<div class="form-group form-group-sm mr10">
				<label for="fundsType">${L:l(lan,'投资类型：') }</label> <input type="hidden"
					id="fundsType" name="fundsType" value="${param.fundsType }">
				<div class="input-group">
					<div class="dropdown">
						<button class="btn btn-default dropdown-toggle btn-sm"
							type="button" data-toggle="dropdown" aria-haspopup="true"
							aria-expanded="true" style="min-width: 100px;">
							<span class="text-g">${L:l(lan,'不限') }</span> <span class="caret"></span>
						</button>
						<ul class="dropdown-menu" data-bind="fundsType">
							<li data-value=""><a role="button">${L:l(lan,'不限') }</a></li>
							<c:if test="${coinMaps!=null}">
								<c:forEach items="${coinMaps}" var ="coin">
									<li data-value="${coin.value.fundsType}"><a role="button">${coin.value.propCnName} ${coin.value.propTag}</a></li>
								</c:forEach>
							</c:if>
						</ul>
					</div>
				</div>
			</div>

			<div class="form-group form-group-sm mr10">
				<label for="riskType">${L:l(lan,'风险控制：') }</label> <input
					type="hidden" id="riskType" name="riskType"
					value="${param.riskType }">
				<div class="input-group">
					<div class="dropdown">
						<button class="btn btn-default dropdown-toggle btn-sm"
							type="button" data-toggle="dropdown" aria-haspopup="true"
							aria-expanded="true" style="min-width: 100px;">
							<span class="text-g">${L:l(lan,'不限') }</span> <span class="caret"></span>
						</button>
						<ul class="dropdown-menu" data-bind="riskType">
							<li data-value=""><a role="button">${L:l(lan,'不限') }</a></li>
							<li data-value="1"><a role="button">${L:l(lan,"自担风险") }</a></li>
							<li data-value="2"><a role="button">${L:l(lan,'只要本金') }</a></li>
						</ul>
					</div>
				</div>
			</div>


			<!-- but Start -->
			<button type="button" class="btn btn-primary btn-sm mr10"
				id="doSearchBtn" onclick="vip.list.search({formId:'formSearch1'})"
				style="min-width: 100px;">
				<i class="fa fa-search mr5"></i>${L:l(lan,'筛选') }
			</button>
			<button type="reset" class="btn btn-default btn-sm" id="reSetBtn"
				style="min-width: 100px;"
				onclick="vip.list.resetForm({formId:'formSearch1'})">
				<i class="fa fa-repeat mr5"></i>${L:l(lan,'重置') }
			</button>
			<!--<button type="button" class="btn btn-default btn-sm"
				style="background: white; color: red; min-width: 100px;"
				onclick="getCheckboxValue2()">${L:l(lan,'一键取消') }</button>
			 but End -->

		</form>
	</div>

	<table class="table table-striped table-bordered table-hover table-2x">
		<thead>
			<tr>
				<th width="12%"><a class="sort">${L:l(lan,'时间') }</a></th>
				<th width="9%">${L:l(lan,'类型') }</th>
				<th width="12%">${L:l(lan,'投资金额') }</th>
				<th width="11%">${L:l(lan,'利率') }</th>
				<th width="10%">${L:l(lan,'利率形式') }</th>
				<th width="10%">${L:l(lan,'风险控制') }</th>
				<th width="16%" class="text-left">${L:l(lan,'进度') }</th>
				<th width="10%">${L:l(lan,'操作') }</th>
			</tr>
		</thead>
		<tbody>
			<c:choose>
				<c:when test="${fn:length(lists)>0}">
					<c:forEach items="${lists }" var="item" varStatus="stat">
						<script type="text/javascript">
						<!--
							function getCheckboxValue2() {
								var uids = $
								{
									item.userId
								}
								;
								var pageno = $
								{
									pageNo
								}
								;
								javascript: vip.p2p.doCancels(uids, pageno);
							}
						//-->
						</script>

						<tr>
							<td
								title="<fmt:formatDate value="${item.createTime }" pattern="yyyy-MM-dd HH:mm"/>">
								<fmt:formatDate value="${item.createTime }"
									pattern="yyyy-MM-dd HH:mm" />
							</td>
							<td>${item.getFt().propTag }</td>
							<td><span style="cursor: pointer;"
								<c:if test='${item.isLoop }'>onclick="JuaBox.info('循：代表该笔借贷记录为循环借贷，有还款时会在初始借贷金额上累加该笔还款的金额，达到循环放贷的目的。该值表示已累积放货的金额。')"</c:if>>
									${item.getFt().unitTag }<fmt:formatNumber
										value="${item.amount }" pattern="0.0##" /> <c:if
										test="${item.isLoop }">
										<img src="${static_domain }/statics/img/xun.png"
											title="${L:l(lan,'可自动循环投资') }" class="mian" />
									</c:if>
							</span></td>
							<td><fmt:formatNumber value="${item.rateOfDayShow }"
									pattern="0.0##" />%</td>
							<td>
							 ${L:l(lan,item.interestRateFormShow) } 
							</td>
							<td><p title="${L:l(lan,'网站收取手续费') }${item.riskType.fwfScale * 100 }%">${L:l(lan,item.riskType.value)}</p></td>
							<td class="text-left"><span class="schedule"> <i><u
										style="width:${item.hasBidRate}px"></u></i>
							</span>${item.hasBidRate}%</td>

							<td id="t7_${item.id }"><c:choose>
									<c:when test="${inUserId!='' }">
										<c:if test="${item.status<=1 }">
											<a
												href="javascript:vip.p2p.doCancel(${item.id }, ${pageNo })">${L:l(lan,'取消') }</a>
										</c:if>
										<c:if test="${item.status==2 }">
											<font color="red">${L:l(lan,'已取消') }</font>
										</c:if>
										<%-- <c:if test="${item.status==3 }"><a href="javascript:;" onclick="JuaBox.frame('/u/loan/record/records?lid=${item.id }&isIn=${item.isIn }' ,{width:680});">查看</a></c:if> --%>
										<c:if test="${item.status>3 }">-</c:if>
										<c:if test="${item.hasBidRate>0}">
											<a href="javascript:;"
												onclick="JuaBox.frame('/manage/loan/record/records?lid=${item.id }&isIn=${item.isIn }' ,{width:680});">${L:l(lan,'查看') }</a>
										</c:if>
										<c:if test="${item.status!=2 && item.isLoop}">
											<a
												href="javascript:vip.p2p.doCancelLoop(${item.id }, ${pageNo })">${L:l(lan,'不循环') }</a>
										</c:if>
									</c:when>
									<c:otherwise>
										<a href="javascript:vip.p2p.doTrans(${item.id } , false)">${L:l(lan,'立即借入') }</a>
									</c:otherwise>
								</c:choose></td>

						</tr>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<tr>
						<td colspan="9"><div class="bk-norecord">
								<p>
									<i class="bk-ico info"></i>${L:l(lan,'暂时没有相关记录。') }
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
	vip.list.basePath = "/manage/loan/";
	//绑定下拉取值事件
	$("#formSearch1 .dropdown-menu").on("click", "li", function() {
		var dropMenu = $(this).parents(".dropdown");
		var bindInput = dropMenu.find(".dropdown-menu").data("bind");

		$("#formSearch1 #" + bindInput).val($(this).data("value"));
		$(dropMenu).find(".text-g").text($(this).text());
	});

	$(function() {
		$("#formSearch1 .dropdown").each(
				function() {
					var _this = $(this).find(".dropdown-menu").data("bind");
					$(this).find(".dropdown-menu").find("li").each(
							function() {
								if ($(this).data("value") + "" == $(
								   "#formSearch1 #" + _this).val()) {
									$(this).parents(".dropdown")
											.find(".text-g").text(
													$(this).text());
								}
							})
				});
	});
	
/* 	function doCancel(loanId){
		if(window.confirm("您确定要取消该借款信息吗？")){
			vip.ajax( {
				url : "/manage/loan/doCancel?id=" + loanId,
				dataType : "json",
				suc : function(json) {
					JuaBox.sure(json.des, {
						closeFun : window.top.location.reload()
					});
				},
				err : function(json){
					JuaBox.sure(json.des);
				}
			});
		}
	} */
	
</script>

