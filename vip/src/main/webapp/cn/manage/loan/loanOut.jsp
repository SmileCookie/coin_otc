<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>

<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${L:l(lan,"融资融币借出-二级导航-标题-1")}-${WEB_NAME }-${WEB_TITLE }</title>
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<link rel="stylesheet" href="${static_domain }/statics/css/web.loan.css?V${CH_VERSON }">
<script type="text/javascript" src="${static_domain }/statics/js/common/radialIndicator.js?V${CH_VERSON }"></script>
<script type="text/javascript">
//币种借贷费率
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
		<jsp:include page="doLoanOut.jsp" />
		<!--start-->
        <div class="loain-top">
			<div class="container">
                 <ul class="loan-tab">
					 <li><a href="/manage/loan">${L:l(lan,"融资融币借入-二级导航-标题-1")}</a><i></i></li>
					 <li class="active"><a href="/manage/loan/out">${L:l(lan,"融资融币借出-二级导航-标题-1")}</a><i></i></li>
				 </ul>
			</div>
		</div>
		<div class="mainer-phase2">
		<div class="container">
			<div class="loan-overview mar-60 clearfix">
				<div class="lo-view-left">
					<div class="loan-left">
                         <h4>${L:l(lan,"融资融币借出-投资详情栏-标签-1")}</h4>
                         <div aria-expanded="false">
							<ul class="loan-secd" style="margin-right:0;">
								<li class="dropdown">
									<a class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
										<div class="selcoin" id="selCoinOut">
											<i class="loan-coin loan-btc"></i>	
											<span>BTC<br/><b>Bitcoin</b></span>
										</div>
										<i class="iconfont2 ft14 right loan-down">&#xe600;</i>
									</a>
									<ul class="dropdown-menu animated bk-secd-menu fadeIn loan-dropdown-menu" id="loanDrop" data-animation="fadeIn" role="menu" style="left:auto;right:0">
										<c:if test="${userLoadMaps!=null}">
											<c:forEach items="${userLoadMaps}" var="userLoad"  varStatus="varStatus" >
												<li>
													<a href="###" class="line40" onClick="javascript:loadChange('${userLoad.key}',this);" index="${varStatus.index}" data-outSuccess="${userLoad.value.totalLoan}"  data-loanLimit="${userLoad.value.loanLimit}" data-rate="${userLoad.value.rate}" data-earningsOfDay="${userLoad.value.earningsOfDay}" data-arrivedOfDay="${userLoad.value.arrivedOfDay}" data-totalEarnings="${userLoad.value.totalEarnings}" data-fullname="${userLoad.value.coint.propEnName}">
														<i class="loan-coin loan-${userLoad.key}"></i>
														${userLoad.value.coint.propTag}
														<span class="coinnum">
															<fmt:formatNumber value="${(userLoad.value.totalLoan-0.0049)<=0?0:(userLoad.value.totalLoan-0.0049)}" pattern="0.00" />
														</span>
													</a>
												</li>
											</c:forEach>
										</c:if>
									</ul>
								</li>
							</ul>
						 </div>
						 <p class="mt20"><label>${L:l(lan,"融资融币借出-投资详情栏-标签-2")}</label> <span id="userLoadUnit">${btcCoinInfo.loanLimit} BTC</span></p>
						 <p><label>${L:l(lan,"融资融币借出-投资详情栏-标签-3")}</label> <span id="ratel">${btcCoinInfo.rate} %</span></p>
					</div>
					<div class="loan-right">
                        <div class="radialrange" id="radialRange">
							<div class="loan-remain">
								<h4>${L:l(lan,"融资融币借出-投资详情栏-饼图-1")}</h4>
								<span id="loanRemain">${btcCoinInfo.surplusLoan}</span>
							</div>
						</div>
                        <p class="text-center"><label>${L:l(lan,"融资融币借出-投资详情栏-标签-4")}</label><span id="outSuccess"><fmt:formatNumber value="${(btcCoinInfo.outSuccess + btcCoinInfo.outWait - 0.0049)<=0?0:(btcCoinInfo.outSuccess + btcCoinInfo.outWait - 0.0049)}" pattern="0.00" /> BTC</span></p>
					</div>
				</div>
				<div class="lo-view-center lout-view-center">
                     <h4>${L:l(lan,"融资融币借出-投资详情栏-标签-5")}</h4>
					 <ul class="lo-income clearfix">
						 <li>
							 <span class="slogan">${L:l(lan,"融资融币借出-投资详情栏-标签-6")}</span>
							 <span class="lo-num" id="earningsOfDay">${btcCoinInfo.earningsOfDay} BTC</span>
						 </li>
						 <li>
							 <span class="slogan">${L:l(lan,"融资融币借出-投资详情栏-标签-7")}</span>
							 <span class="lo-num" id="totalEarnings">${btcCoinInfo.totalEarnings} BTC</span>
						 </li>
						 <li>
							 <span class="slogan">${L:l(lan,"融资融币借出-投资详情栏-标签-8")}</span>
							 <span class="lo-num" id="arrivedOfDay">${btcCoinInfo.arrivedOfDay} BTC</span>
						 </li>
					 </ul>
				</div>
				<div class="lo-view-right">
                     <div class="lo-borrow lo-invest"></div>
					 <p class="text-center">${L:l(lan,"融资融币借出-投资申请栏-标签-9")}</p>
					 <a class="apply-currency" href="###" data-toggle="modal" data-target=".modal">${L:l(lan,"融资融币借出-投资申请栏-按钮-1")}</a>
				</div>
			</div>

			<div class="lo-detail" id="loDetail">
				<div class="lo-detail-top hd">
					<ul class="lo-detail-tab btn-group bk-btn-group">
						<li class="active"><a href="###">${L:l(lan,"融资融币借出-投资列表上部-页签-1")}</a></li>
						<li><a href="###">${L:l(lan,"融资融币借出-投资列表上部-页签-2")}</a></li>
					</ul>

					<p class="mon-species">
						${L:l(lan,'融资融币借出-投资列表上部-标签-1')}
						<select class="modal-select ml5">
							<option value="">${L:l(lan,"融资融币借出-投资列表上部-下拉菜单-1")}</option>
							<c:if test="${userLoadMaps!=null}">
								<c:forEach	items="${userLoadMaps}" var="userLoad" varStatus="vstatus">
									<option value="${userLoad.value.coint.propTag }">${userLoad.value.coint.propTag }/BTC</option>
								</c:forEach>
							</c:if>
						</select>
					</p>
				 </div>
				 <div class="bd">
					<div class="bd-list-con">
						<div class="userload-con">
							<table class="table table-bordered table-loan">
								<thead>
									<tr>
										<th>${L:l(lan,"融资融币借出-投资列表中部-投资中表头-1")}</th>
										<th>${L:l(lan,"融资融币借出-投资列表中部-投资中表头-2")}</th>
										<th>${L:l(lan,"融资融币借出-投资列表中部-投资中表头-3")}</th>
										<th>${L:l(lan,"融资融币借出-投资列表中部-投资中表头-4")}</th>
										<th>${L:l(lan,"融资融币借出-投资列表中部-投资中表头-5")}</th>
										<th>${L:l(lan,"融资融币借出-投资列表中部-投资中表头-6")}</th>
										<th>${L:l(lan,"融资融币借出-投资列表中部-投资中表头-7")}</th>
										<th>${L:l(lan,"融资融币借出-投资列表中部-投资中表头-8")}</th>								
									</tr>
								</thead>
								<tbody id="loanOutDetail">
								</tbody>
							</table>
						</div>
						<div class="text-center" id="loanOutDetail_Page"></div>
					</div>
					
					<div class="bd-list-con">
						<div class="userload-con">
							<table class="table table-bordered table-loan">
							<thead>
								<tr>
									<th>${L:l(lan,"融资融币借出-投资列表中部-已收回表头-1")}</th>
									<th>${L:l(lan,"融资融币借出-投资列表中部-已收回表头-2")}</th>
									<th>${L:l(lan,"融资融币借出-投资列表中部-已收回表头-3")}</th>
									<th>${L:l(lan,"融资融币借出-投资列表中部-已收回表头-4")}</th>
									<th>${L:l(lan,"融资融币借出-投资列表中部-已收回表头-5")}</th>
									<th>${L:l(lan,"融资融币借出-投资列表中部-已收回表头-6")}</th>								
									<th>${L:l(lan,"融资融币借出-投资列表中部-已收回表头-7")}</th>
									<th>${L:l(lan,"融资融币借出-投资列表中部-已收回表头-8")}</th>
									<th>${L:l(lan,"融资融币借出-投资列表中部-已收回表头-9")}</th>		
								</tr>
							</thead>
							<tbody id="loanOutDetailAlready">
							</tbody>
							</table>
						</div>
						<div class="text-center" id="loanOutDetailAlready_Page"></div>
					</div>
				 </div>
			</div>
         </div>
	<!--end-->
	</div>
	<jsp:include page="/common/foot.jsp" />
	<script type="text/javascript">	
		require(['module_asset'], function(asset) {
			asset.loanOutRecordInit();
		});
		function reload2() {
			window.location.href = "/manage/loan/out";
		}
		var radialObj =radialIndicator('#radialRange',{
			barColor: '#69BF6C',
			radius: 50,
			barWidth:10,
			displayNumber:true,
			roundCorner:true,
			percentage:true,
			fontColor:'transparent',
			fontSize:'30',
			frameTime:2,
			title:''
		});
		//touzi 200 jishu 1000
        radialObj.animate(${btcCoinInfo.totalLoan}/${btcCoinInfo.loanLimit}*100);
		$("#loDetail").slide({
			trigger : "click",
			titOnClassName : "active",
			startFun : function(i) {
				// $(".tipsy").remove();
				// vip.p2p.contShow(i);
			},
			endFun : function() {
				//$(".Loadding").remove();
			}
		});
        
		function loadChange(coint,obj){
			 var $modSel = $('#loanDropMod > li').eq($(obj).parent().index()).find('a');
             var upperName = coint.toUpperCase();
             var loanlimit = $(obj).data("loanlimit"),rate = $(obj).data("rate"),earningsofday = $(obj).data("earningsofday"),
			 	 arrivedofday = $(obj).data("arrivedofday"),totalearnings = $(obj).data("totalearnings"),fillName = $(obj).data("fullname")
				 outSuccess = $(obj).data("outsuccess");
			 var selectCon = "<i class='loan-coin loan-"+coint+"'></i><span>"+upperName+"<br/><b>"+fillName+"</b></span>";
			 $('#selCoinOut').html(selectCon);
			 $('#userLoadUnit').html(loanlimit+" "+upperName);
             $('#outSuccess').html(outSuccess+" "+upperName);
             $('#ratel').html(rate+"%");
			 $('#earningsOfDay').html(earningsofday +" "+upperName);
             $('#arrivedOfDay').html(arrivedofday +" "+upperName);
			 $('#totalEarnings').html(totalearnings +" "+upperName);
			 $('#loanRemain').html(method.fixDecimal(loanlimit-outSuccess,2));
			 radialObj.animate(outSuccess/loanlimit*100);
			 cointChange(coint,$modSel);
		}
	</script>
	<script type="text/x-tmpl" id="tmpl-loanOutDetail">
		{% for(var i = 0; i <= rs.length -1; i++){  %}
			<tr>
				<td>
					<img src="${static_domain}/statics/img/common/{%=rs[i].coinName%}.png">
					{%=rs[i].coinLowerName%}
				</td>
				<td>{%=rs[i].createTime%}</td>
				<td>{%=rs[i].amount%}</td>
				<td>{%=rs[i].lendAmount%}/{%=rs[i].recoverAmount%}</td>
				<td class="orange">
					<span class="schedule">
						<i>
							<u class="lightBlue" style="width:{%=rs[i].lendRange%}%;"></u>
							<u style="width:{%=rs[i].recoverRange%}%;"></u>
						</i>
					</span>
					<span class="schedpt">{%=rs[i].lendRange%}%/{%=rs[i].recoverRange%}%</span>
				</td>
				<td>{%=rs[i].rate%}%</td>
				<td>{%=rs[i].income%}</td>
				<td>
					{% if(rs[i].status == 0 || rs[i].status==1){ %}
						<a class="mr5 loan-link" href="javascript:vip.p2p.doCancel({%=rs[i].id%},${pageNo})">${L:l(lan,'取消')}</a>
					{% } %}
					{% if(rs[i].status == 1 || rs[i].status==3){ %}
						<a class="mr5 loan-link" href="###" onclick="JuaBox.frame('/manage/loan/record/records?lid={%=rs[i].id%}&isIn=' ,{width:800});">${L:l(lan,'查看') }</a>	
					{% } %}		
				</td>
			</tr>                     
		{% } %}
	</script>
	<script type="text/x-tmpl" id="tmpl-loanOutDetailAlready">
		{% for(var i = 0; i <= rs.length -1; i++){  %}
			<tr>
				<td>
					<img src="${static_domain}/statics/img/common/{%=rs[i].coinName%}.png">
					{%=rs[i].coinLowerName%}
				</td>
				<td>{%=rs[i].createTime%}</td>
				<td>{%=rs[i].recoverTime%}</td>
				<td>{%=rs[i].amount%}</td>				
				<td>{%=rs[i].lendAmount%}</td>
				<td>{%=rs[i].recoverAmount%}</td>
				<td>{%=rs[i].rate%}</td>
				<td>{%=rs[i].income%}</td>
				<td>
					<a class="loan-link" href="###" onclick="JuaBox.frame('/manage/loan/record/records?lid={%=rs[i].id%}&isIn=' ,{width:800});">${L:l(lan,'查看') }</a>
				</td>
			</tr>                     
		{% } %}
	</script>
</body>
</html>