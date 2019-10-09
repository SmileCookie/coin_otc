<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>

<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${L:l(lan,"融资融币借入-二级导航-标题-1")}-${WEB_NAME }-${WEB_TITLE }</title>
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<link rel="stylesheet" href="${static_domain }/statics/css/web.loan.css?V${CH_VERSON }">
<script type="text/javascript">
var coinFees = {
		<c:forEach items="${defaultLimitList}" var="defaultLimit">
			${defaultLimit.keyName} : ${defaultLimit.valueName},
		</c:forEach>
};
</script>
</head>
<body>
	<div class="bk-body">
		<jsp:include page="/common/top.jsp" />
		<jsp:include page="doLoanIn.jsp" />
		<!--start-->

		<div class="loain-top">
			<div class="container">
                 <ul class="loan-tab">
					 <li class="active"><a href="/manage/loan">${L:l(lan,"融资融币借入-二级导航-标题-1")}</a><i></i></li>
					 <li><a href="/manage/loan/out">${L:l(lan,"融资融币借出-二级导航-标题-1")}</a><i></i></li>
				 </ul>
			</div>
		</div>
		<div class="mainer-phase2">
			<div class="container">
				<div class="loan-overview mar-60 clearfix">
					<div class="lo-view-left mr25">
						<ul class="lo-view-topul clearfix">
							<li class="left">
								<p>${L:l(lan,"融资融币借入-借币详情栏-标签-1")}(BTC)</p>
								<span>${userAssetsInfo.loanInAssets}</span>
							</li>
							<li class="right text-right">
								<p>${L:l(lan,"融资融币借入-借币详情栏-标签-2")}(BTC)</p>
								<span>${userAssetsInfo.surplusLoanIn > 0 ? userAssetsInfo.surplusLoanIn : '0.0000'}</span>
							</li>
						</ul>
						<div class="lo-rang">
							<span style="width:${userAssetsInfo.loanRate*100}%;"></span>
						</div>
						<ul class="lo-view-topul lo-view-botul  clearfix">
							<li class="wid25">
								<p>${L:l(lan,"融资融币借入-借币详情栏-标签-3")}(BTC)</p>
								<span>${userAssetsInfo.netAssets}</span>
							</li>
							<li class="wid12 text-center">
								<p class="vib-hide">${L:l(lan,"乘号")}</p>
								<span class="icon-sign">×</span>
							</li>
							<li class="wid20 text-center">
								<p>${L:l(lan,"融资融币借入-借币详情栏-标签-4")}</p>
								<span>${userAssetsInfo.level}</span>
							</li>
							<li class="wid12 text-center">
								<p class="vib-hide">${L:l(lan,"等于")}</p>
								<span class="icon-sign">=</span>
							</li>
							<li class="wid30 text-right">
								<p>${L:l(lan,"融资融币借入-借币详情栏-标签-5")}(BTC)</p>
								<span>${userAssetsInfo.loanInMax > 0 ? userAssetsInfo.loanInMax : '0.0000'}</span>
							</li>
						</ul>
					</div>
					<div class="lo-view-center">
							<c:set var="percent" value="0"></c:set>
							<c:choose>
								<c:when test="${p2pUser.loanInAssets > 0}">
									<c:set var="percent" value="${(userAssetsInfo.netAssets/userAssetsInfo.loanInAssets)*100}"></c:set>
									<fmt:formatNumber value="${percent}" pattern="###.#" var="percentNum"/>
									<c:if test="${percent < 0}">
										<div class="lo-assets">
											<h4>${L:l(lan,"融资融币借入-风险提示栏-饼图-1")}</h4>
											<p>0.0%</p>
											<img src="${static_domain}/statics/img/common/index/circle-3.png" alt="">
										</div>
										<div class="lo-risk">
											${L:l(lan,"融资融币借入-风险提示栏-标签-1")}${L:l(lan,"：")}<span class="risk-high">${userAssetsInfo.repayLevelShowNew}</span>
										</div>
									</c:if>
									<c:if test="${percent <= 30 && percent >= 0}">
										<div class="lo-assets">
											<h4>${L:l(lan,"融资融币借入-风险提示栏-饼图-1")}</h4>
											<p>${percentNum}%</p>
											<img src="${static_domain}/statics/img/common/index/circle-3.png" alt="">
										</div>
										<div class="lo-risk">
											${L:l(lan,"融资融币借入-风险提示栏-标签-1")}${L:l(lan,"：")}<span class="risk-high">${userAssetsInfo.repayLevelShowNew}</span>
										</div>
									</c:if>
									<c:if test="${percent <= 60 && percent > 30}">
										<div class="lo-assets">
											<h4>${L:l(lan,"融资融币借入-风险提示栏-饼图-1")}</h4>
											<p>${percentNum}%</p>
											<img src="${static_domain}/statics/img/common/index/circle-2.png" alt="">
										</div>
										<div class="lo-risk">
											${L:l(lan,"融资融币借入-风险提示栏-标签-1")}${L:l(lan,"：")}<span class="risk-mid">${userAssetsInfo.repayLevelShowNew}</span>
										</div>
									</c:if>
									<c:if test="${percent <= 100 && percent > 60}">
										<div class="lo-assets">
											<h4>${L:l(lan,"融资融币借入-风险提示栏-饼图-1")}</h4>
											<p>${percentNum}%</p>
											<img src="${static_domain}/statics/img/common/index/circle-1.png" alt="">
										</div>
										<div class="lo-risk">
											${L:l(lan,"融资融币借入-风险提示栏-标签-1")}${L:l(lan,"：")}<span class="risk-low">${userAssetsInfo.repayLevelShowNew}</span>
										</div>
									</c:if>
									<c:if test="${percent > 100}">
										<div class="lo-assets">
											<h4>${L:l(lan,"融资融币借入-风险提示栏-饼图-1")}</h4>
											<p>> 100%</p>
											<img src="${static_domain}/statics/img/common/index/circle-1.png" alt="">
										</div>
										<div class="lo-risk">
											${L:l(lan,"融资融币借入-风险提示栏-标签-1")}${L:l(lan,"：")}<span class="risk-low">${userAssetsInfo.repayLevelShowNew}</span>
										</div>
									</c:if>
								</c:when>
								<c:otherwise>
										<div class="lo-assets">
											<h4>${L:l(lan,"融资融币借入-风险提示栏-饼图-1")}</h4>
											<p>N/A</p>
											<img src="${static_domain}/statics/img/common/index/circle-4.png" alt="">
										</div>
										<div class="lo-risk">
											${L:l(lan,"融资融币借入-风险提示栏-标签-1")}${L:l(lan,"：")}<span class="risk-none">${userAssetsInfo.repayLevelShowNew}</span>
										</div>
								</c:otherwise>
							</c:choose>
					</div>
					<div class="lo-view-right">
						<div class="lo-borrow"></div>
						<p class="text-center">${L:l(lan,"融资融币借入-借币申请栏-提示语-1")}</p>
						<a class="apply-currency" href="###" data-toggle="modal" data-target=".modal">${L:l(lan,"融资融币借入-借币申请栏-按钮-1")}</a>
					</div>
				</div>
				
				<div class="lo-detail" id="loDetail">
					<div class="lo-detail-top hd">
						<ul class="lo-detail-tab btn-group bk-btn-group">
							<li class="active" data-type="1"><a href="###">${L:l(lan,'融资融币借入-借币列表上部-页签-1')}</a></li>
							<li data-type="2"><a href="###">${L:l(lan,'融资融币借入-借币列表上部-页签-2')}</a></li>
						</ul>

						<p class="mon-species">
							${L:l(lan,'融资融币借入-借币列表上部-标签-1')}
							<select class="modal-select ml5">
								<option value="">${L:l(lan,'融资融币借入-借币列表上部-下拉菜单-1')}</option>
								<c:if test="${userLoadMaps!=null}">
									<c:forEach	items="${userLoadMaps}" var="userLoad" varStatus="vstatus">
										<option value="${userLoad.value.coint.propTag}">${userLoad.value.coint.propTag}/BTC</option>
									</c:forEach>
								</c:if>
							</select>
						</p>
					</div>
					<div class="bd">
						<div class="bd-list-con">
							<div class="bd-list">
								<table class="table table-bordered table-loan">
									<thead>
										<tr>
											<th>${L:l(lan,"融资融币借入-借币列表中部-已借入表头-1")}</th>
											<th>${L:l(lan,"融资融币借入-借币列表中部-已借入表头-2")}</th>
											<th>${L:l(lan,"融资融币借入-借币列表中部-已借入表头-3")}</th>
											<th>${L:l(lan,"融资融币借入-借币列表中部-已借入表头-4")}</th>
											<th>${L:l(lan,"融资融币借入-借币列表中部-已借入表头-5")}</th>
											<th>${L:l(lan,"融资融币借入-借币列表中部-已借入表头-6")}</th>
											<th>${L:l(lan,"融资融币借入-借币列表中部-已借入表头-7")}</th>
											<th>${L:l(lan,"融资融币借入-借币列表中部-已借入表头-8")}</th>								
										</tr>
									</thead>
									<tbody id="loanDetail"></tbody>
								</table>
							</div>
							<div class="text-center" id="loanDetail_Page"></div>
						</div>

						<div class="bd-list-con loan-hide">
							<div class="bd-list">
								<table class="table table-bordered table-loan">
									<thead>
										<tr>
											<th>${L:l(lan,"融资融币借入-借币列表中部-已还清表头-1")}</th>
											<th>${L:l(lan,"融资融币借入-借币列表中部-已还清表头-2")}</th>
											<th>${L:l(lan,"融资融币借入-借币列表中部-已还清表头-3")}</th>
											<th>${L:l(lan,"融资融币借入-借币列表中部-已还清表头-4")}</th>
											<th>${L:l(lan,"融资融币借入-借币列表中部-已还清表头-5")}</th>
											<th>${L:l(lan,"融资融币借入-借币列表中部-已还清表头-6")}</th>
											<th>${L:l(lan,"融资融币借入-借币列表中部-已还清表头-7")}</th>
											<th>${L:l(lan,"融资融币借入-借币列表中部-已还清表头-8")}</th>									
										</tr>
									</thead>
									<tbody id="loanDetailAlready">
									</tbody>
								</table>
							</div>
							<div class="text-center" id="loanDetailAlready_Page"></div>
						</div>
					</div>
				</div>
			</div>	
		</div>
	   
		</div>
		<!--end-->
	</div>
	<jsp:include page="/common/foot.jsp" />
	

 <script type="text/javascript">
 $(document).ready(function(){
	  require(['module_asset'],function(asset){
			asset.loanRecordInit();
		});
	  $("#msg").html("${L:l(lan,'每日费率')}${L:l(lan,'：')}"+coinFees.btc+"%");
	  $("#apply").click(function(){
		  JuaBox.info("",{
	    		btnNum : 1,
	    		btnName1 : bitbank.L("申请放贷"),
	    		endFun : function(JuaId){
	    			$("#JuaBox_" + JuaId + " .body").load("/manage/applyForm",function(){
	    				JuaBox.position();
	    			});
	    		},
	    		btnFun1 : function(JuaId){
	    			$.ajax({
	    				url:  "/manage/doInvestorApply",
	    				type: 'post',
	    				dataType:'json',
	    				data: $("#investForm").serializeArray(),
	    				success: function(json) {
	    					if (json.isSuc) {
	    						JuaBox.close(JuaId, function(){
	    							//提交后的操作
	    							JuaBox.sure(json.des);
	    						});
	    					} else {
	    						JuaBox.sure(json.des);
	    					}
	    				}
	    			});
	    		}
	 		 });
	  });
	  
	});

 function reload2(){
	 window.location.href="/manage/loan";
 }

</script> 

<script type="text/x-tmpl" id="tmpl-loanDetail">
{% for(var i = 0; i <= rs.length -1; i++){  %}
	<tr>
		<td>
			<img src="${static_domain}/statics/img/common/{%=rs[i].coinLowerName%}.png">
			{%=rs[i].coinName%}
		</td>
		<td>{%=rs[i].createTime%}</td>
		<td>{%=rs[i].allAmount%}</td>
		<td>{%=rs[i].rate%}%</td>
		<td>{%=rs[i].lx%}</td>
		<td>{%=rs[i].allAmount%}/{%=rs[i].hasRepay%}</td>
		<td class="orange">
			<span class="schedule">
				<i><u style="width:{%=rs[i].hasBidRate%}%;"></u></i>
			</span>
			<span class="schedpt">{%=rs[i].hasBidRate%}%</span>
		</td>
		<td>
			{% if(rs[i].status==1 || rs[i].showRepayButton==1){ %}
				<a class="loan-link" href="#" onclick="vip.p2p.repay({%=rs[i].id%}, 0);" >${L:l(lan,"融资融币借入-借币列表中部-已借入列表操作-1")}</a>
			{% } %}
		</td>
	</tr>                     
{% } %}
</script>
<script type="text/x-tmpl" id="tmpl-loanDetailAlready">
{% for(var i = 0; i <= rs.length -1; i++){  %}
	<tr>
		<td>
			<img src="${static_domain}/statics/img/common/{%=rs[i].coinLowerName%}.png">
			{%=rs[i].coinName%}
		</td>
		<td>{%=rs[i].createTime%}</td>
		<td>{%=rs[i].repayDate%}</td>
		<td>{%=rs[i].amount-0+rs[i].hasRepay%}</td>
		<td>{%=rs[i].rate*100%}%</td>
		<td>{%=rs[i].hasLx%}</td>
		<td>{%=rs[i].total%}</td>
		<td><span class="loan-label {%=rs[i].statusClass%}">{%=rs[i].statusShow%}</span></td>
	</tr>                     
{% } %}
</script>
</body>
</html>