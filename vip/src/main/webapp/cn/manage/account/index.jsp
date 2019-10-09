<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ page session="false"%>
<!doctype html>
<html>

<head>
	<jsp:include page="/common/head.jsp" />
	<title>${L:l(lan,'资金-资金上部-标题-1')}-${WEB_NAME }-${WEB_TITLE }</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0 maximum-scale=1, user-scalable=no">
	<meta name="keywords" content="${WEB_KEYWORD }" />
	<meta name="description" content="${WEB_DESC }" />
	<link rel="stylesheet" href="${static_domain }/statics/css/web.user.css?V${CH_VERSON }">
	<link rel="stylesheet" href="${static_domain }/statics/css/web.asset.css?V${CH_VERSON }">	
	<script>
		if(JuaBox.isMobile()){
				JuaBox.mobileFontSize();
				$("<link>").attr({ rel: "stylesheet",type: "text/css",href: "${static_domain }/statics/css/mobile/mobile.user.css?V${CH_VERSON }"}).appendTo("head");
				$("<link>").attr({ rel: "stylesheet",type: "text/css",href: "${static_domain }/statics/css/mobile/top_foot_mobile.css?V${CH_VERSON }"}).appendTo("head");
		}
	</script>
</head>

	<body class="">
		<div class="bk-body">
			<!-- Common TopMenu Begin -->
			<jsp:include page="/common/top.jsp" />
			<!-- Common TopMenu End -->
			<!-- Body From mainPage Begin -->
			<div class="mainer">
			<div class="container2">
				<div class="content">
					<div class="bk-assets">
						<div class="cont-row">
							<h2 class="assets-title">${L:l(lan,'充值&提现')}</h2>
							<div class="assets-detail">
								<div class="assets-detail-left">
									<div class="input-box">
										<input type="text" name="checkcoin" id="checkcoin" />
										<button id="btnCheckCoin" class="iconfont2 icon-Shape-search"></button>
									</div>
									<label for="hideCoin">
										<input type="checkbox" name="hideCoin" id="hideCoin" />
										${L:l(lan,'隐藏0余额')}
									</label>
								</div>
								<div class="assets-detail-right">
									<div class="assets-detail-value">
										<div class="val-con">
											<i id="cerrencyUnit"></i> <span id="cerrencyTotal"></span> / <em id="totalBtc"></em> BTC
										</div>
										<b>${L:l(lan,'资金-资金中部-标签-2')}</b>
									</div>
									<div class="assets-detail-perfunds">
										<c:if test="${authResult == 0}">
											<a href="${vip_domain}/manage/auth/authentication">${L:l(lan,'提升额度')}</a>												
										</c:if>
										<span>
											<b>${L:l(lan,'24H提现额度：')}</b>${downloadLimit} BTC											
										</span>
										<span class="mar10">
											<b>${L:l(lan,'可用：')}<span id="availableDownload"></span></b> BTC
										</span>
									</div>
								</div>
							</div>
							
							<div class="data-sheets">
								<table width="100%" class="norm-table">
									<thead id="tableSort">
										<tr>
											<th data-sortName="propTag"><span class="more-box">${L:l(lan,'币种')} <i></i></span></th>
											<th data-sortName="coinFullName" class="text-center"><span class="more-box">${L:l(lan,'全称')} <i></i></span></th>
											<th data-sortName="total" class="text-right"><span class="more-box">${L:l(lan,'总额')} <i></i></span></th>
											<th data-sortName="balance" class="text-right"><span class="more-box">${L:l(lan,'可用余额')} <i></i></span></th>
											<th data-sortName="freeze" class="text-right"><span class="more-box">${L:l(lan,'冻结资金')} <i></i></span></th>
											<th data-sortName="valuation" class="text-right"><span class="more-box"><b id="valName"></b>${L:l(lan,'估值')} <i></i></span></th>
											<th class="text-center">${L:l(lan,'操作')}</th>
										</tr>
									</thead>
									<tbody id="fundsDetail">
										
									</tbody>
								</table>
							</div>
						</div>
					</div>
					</div>
				</div>
			</div>
			</div>
        <script type="text/x-tmpl" id="tmpl-fundsDetail">
		     {% for (var i = 0; i <= rs.length -1; i++) { %}
	 			 <tr class="clearfix">
					      <td>
					      	<img src="${static_domain}/statics/img/common/{%=rs[i].stag%}.png">
					      	<span class="ftbold">{%=rs[i].propTag%}</span>
						  </td>
						  <td class="text-center">{%=rs[i].coinFullName%}</td>
					      <td class="text-right">
					      	{%=rs[i].total6%}
					      </td>
					      <td class="text-right">
					      	{%=rs[i].balance6%}
					      </td>
					      <td class="text-right item_1_3">
								{% if(rs[i].propTag == 'ABCDEF' && rs[i].eventFreez > 0){ %}
									<strong class="hover_text">
										<div class="text_divcon">
											<div class="text_div tag ${lan}">${L:l(lan,"活动冻结资金，需完成")}<a href="${vip_domain}/manage/auth/authentication">${L:l(lan,"身份认证超链接")}</a>${L:l(lan,"后方可解冻")}</div>
										</div>
									</strong>
								{% } %}
					      	{%=rs[i].freeze6%}
						  </td>
						  <td class="text-right">
							{% if(rs[i].valuation != 0){ %}
							  {%=rs[i].valuation%}
							{% } %}
							{% if(rs[i].valuation == 0){ %}
							   --
						    {% } %}
						  </td>
					      <td class="text-center">
							{% if(rs[i].canCharge){ %}
								<a href="${vip_domain}/manage/account/charge?coint={%=rs[i].propTag%}" class="assets-link" target="_blank">${L:l(lan,'资金-资金中部-按钮-1')}</a>
							{% } %}
							{% if(!rs[i].canCharge){ %}
								<a href="javascript:void(0)" class="assets-link color-gray curDefault">${L:l(lan,'暂停')}</a>
							{% } %}

							{% if(rs[i].canWithdraw){ %}
								<a href="${vip_domain}/manage/account/download?coint={%=rs[i].propTag%}" class="assets-link" target="_blank">${L:l(lan,'资金-资金中部-按钮-2')}</a>
							{% } %}
							{% if(!rs[i].canWithdraw){ %}
								<a href="javascript:void(0)" class="assets-link color-gray curDefault">${L:l(lan,'暂停')}</a>
							{% } %}
					      </td>
					    </tr>
				{% } %}
			</script>
			<script type="text/javascript">
				require(['module_asset'],function(bill){
					var preIndex = 0;
					$("#tableSort > tr > th").on('click',function(){
						if(preIndex == $(this).index()){
							++bill.clickNum;
							bill.clickNum = bill.clickNum==3? 0:bill.clickNum;
						}else{
							$(this).siblings().find("i").removeClass("more").hide();
							bill.clickNum = 1 
						}
						if(bill.clickNum == 1){
							$(this).find("i").show()
						}else if(bill.clickNum == 2){
							$(this).find("i").addClass("more");
						}else{
							$(this).find("i").removeClass("more").hide()
						}
						bill.thIndex=$(this).index();
						bill.sortName = $(this).data("sortname");
						if(bill.sortName){
							bill.getUserAsset();
						}
						preIndex = $(this).index()
					})
					$("#btnCheckCoin").on("click",function(){
						$(this).removeClass("icon-guanbiguanggao");
						$("#checkcoin").val("");
						bill.filterVal = "";
						bill.getUserAsset();
					})
					$("#checkcoin").on("keyup",function(){
						bill.filterVal = $.trim($("#checkcoin").val()).toUpperCase();
						if(bill.filterVal){
							$("#btnCheckCoin").addClass("icon-guanbiguanggao")
						}else{
							$("#btnCheckCoin").removeClass("icon-guanbiguanggao")
						}
						bill.getUserAsset();
					})
					$("#hideCoin").on("click",function(){
						bill.isHideZerobalance = $(this).is(":checked")? 1:0; 
						bill.getUserAsset();
					})
				});
				$('#totalBtc').html(formatNumber(${assetsBtc}))
				$('#availableDownload').html(formatNumber(${availableDownload}))				
				function formatNumber(num){
					var numArr = num.toString().split('.');
					if(numArr[1]){
						if(numArr[1].length<8){
							return num+'0'
						}else{
							Big.RM = 0
							return new Big(num).toFixed(8)
						}
						
					}else{
						return num+'.0'
					}
						
				}
			</script>
			<!-- Body From mainPage End -->
			<!-- Common FootMain Begin -->
			<jsp:include page="/common/foot.jsp" />
			<!-- Common FootMain End -->
		</div>
	</body>

</html>