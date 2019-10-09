<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>

<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${L:l1(lan,"%%提币",coint.propTag)}-${WEB_NAME }-${WEB_TITLE }</title>
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<link rel="stylesheet" href="${static_domain }/statics/css/web.pay.css?V${CH_VERSON }">
<link rel="stylesheet" href="${static_domain }/statics/css/web.asset.css?V${CH_VERSON }">
</head>

<body>
<div class="bk-body">
  <jsp:include page="/common/top.jsp" />
  <div class="mainer">
  	  <div class="container2">
		<div class="content">
			<div class="bk-payInOut">
				<div class="cont-row">
				<div class="row">
					<div class="bk-assets bk_pay_asset">
						<h2 class="assets-title assets-title-search">
							<span class="search">${L:l(lan,"提现-提现上部-标题-1")} ${coint.propTag}<i class="jiantou"></i></span>
							<div id="search_warp" class="${lan}">
								<div class="input_warp">
									<input type="text" class="search_input" id="checkcoin" />
									<button id="btnCheckCoin" class="iconfont2 icon-Shape-search"></button>
								</div>
								<section class="btc_list" id="btc_list">
								</section>
							</div>
						</h2>
						<c:if test="${allowCharge}">
							<ul class="tab-link">
								<li><a href="${vip_domain}/manage/account/charge?coint=${coint.propTag}">${L:l(lan,"充值")}</a></li>
								<li class="active"><a href="javascript:void(0);">${L:l(lan,"提现")}</a></li>							
							</ul>
						</c:if>
					</div>
					<article class="bk_assets_with_tips">
						<p class="with_tips_title">${L:l(lan,"温馨提示_1")}</p>
						<p class="mb0">${L:l(lan,"提现之前请您确认提现地址正确，一旦发送到区块链网络便不可撤回。")}</p>
					</article>
					<section class="asset_address">
						<h2 class="clearifx">
							${L:l(lan,"提现地址")}
							<c:choose>  
								<c:when test="${curUser.withdrawAddressAuthenType == 2}">
								<strong>(${L:l(lan,"安全模式")})</strong>
								</c:when>  
								<c:otherwise>
									<span>${L:l(lan,"您当前的提现地址验证为”初级模式“，为了您的资金安全，建议您开启")} "<a class="hover_color" href="/manage/auth?address=address_withdraw">${L:l(lan,"安全模式_1")}</a>"${L:l(lan,"。")}</span>
								</c:otherwise>  
							</c:choose>  
							<a class="address hover_background" href="/manage/account/download/address?coint=${coint.propTag}">${L:l(lan,"新增提现地址")}</a>
						</h2>
						<div class="address_list">
							<div class="address_list_title clearfix">
								<div class="item width272">${L:l(lan,"标签")}</div>
								<div class="item width585">${L:l(lan,"地址")}</div>
								<div class="item width340">${L:l(lan,"操作")}</div>
							</div>
							<section class="address_list_body" id="address_list_body">
								<!-- 提现地址 -->
							</section>
							<div class="bk-new-tabList-fd" id="address_list_body_Page">
								<!-- I'm page -->
							</div>
						</div>
					</section>
					<div class="bk-new-tabList">
							<h2>${L:l(lan,'提现-提现下部-提现记录标题-1')}</h2>
							<div class="bk-new-tabList-bd">
								<table class="table-history">
									<thead>
										<tr>
											<th>${L:l(lan,'状态（确认）')}</th>
											<th>${L:l(lan,'币种')}</th>
											<th class="text-center">${L:l(lan,'数量')}</th>
											<th class="text-center">${L:l(lan,"标签")}</th>
											<th class="text-center border-right">${L:l(lan,'地址')}</th>
										</tr>
									</thead>
									<tbody id="downloadRecordDetail">

									</tbody>
								</table>
							</div>
							<div class="bk-new-tabList-fd" id="downloadRecordDetail_Page">
								<!-- I'm page -->
							</div>
						</div>
					</div>
					</div>
				</div>
			</div>
		</div>
  	  </div>
  </div>
</div>
<script>
	

function cancelOut(did) {
	JuaBox.info("${L:l(lan,'确定要取消吗？')}", {
		btnFun1:function(JuaId){
			window.top.JuaBox.close(JuaId,function(){
				confirmCancel(did);
			});
		}
	});
}


function confirmCancel(did){
	var actionUrl = vip.vipDomain + "/manage/account/downrecord/confirmCancel?did="+did+"&coint=${coint.tag}";
	vip.ajax({
		url : actionUrl , 
		dataType : "xml",
		suc : function(xml){
			location.reload();
		},
		err : function(xml){
			BwModal.alert($(xml).find("Des").text(), {width:300});
		}
	});
}
</script>

<script type="text/x-tmpl" id="tmpl-downloadRecordDetail">
  {% for (var i = 0; i <= rs.length -1; i++) { %}
			<tr class="withdraw_tr">
				<td>
					<p class="confirm-detail clearfix">
						{% if(rs[i].status == 0 || rs[i].status >3){ %}
							{% if(rs[i].commandId > 0){ %}
							<span class="orange">${L:l(lan,"打币中")}</span>
							{% } %}
							{% if(rs[i].commandId == 0){ %}
							<span class="orange">${L:l(lan,"待处理") }</span>
							{% } %}
						{% } %}
						{% if(rs[i].status == 1){ %}<span class="red">${L:l(lan,"失败")}</span>{% } %}
						{% if(rs[i].status == 2){ %}<span class="green">${L:l(lan,"成功")}</span>{% } %}
						{% if(rs[i].status == 3){ %}<span class="gray">${L:l(lan,"已取消")}</span>{% } %}
						{% if(rs[i].status <= 0 && rs[i].commandId <=0){ %}<a class="btn-cancel" href="javascript:cancelOut('{%=rs[i].id%}')">${L:l(lan,"取消")}</a>{% } %}						
					</p>
					{%=rs[i].recentTime%}
				</td>
				<td>{%=rs[i].coinName%}</td>
				<td class="text-center">{%=rs[i].amount%}</td>
				<td class="text-center width120">{%=rs[i].addressMemo%}</td>
				<td class="{%=rs[i].txId == 0?'pad10':''%}">
					<span class="address_span">{%=rs[i].toAddress%}</span>
					{% if(rs[i].memo != "--"){ %}<span>${L:l(lan,"备注_1")}: {%=rs[i].memo%}</span>{% } %}
					{% if(rs[i].txId != 0){ %}
					  <p class="txid-detail">
						<span class="txid">Txid: <a class="btn-check" href="{%=rs[i].webUrl%}" target="_blank">{%=rs[i].txId%}</a></span>
					  </p>
					{% } %}
				</td>
			</tr>
{% } %}
</script>
<script type="text/javascript">
	require(['module_asset'],function(asset){
		asset.download_init("${coint.tag}");
	});
</script>
<script type="text/x-tmpl" id="tmpl-btc_list">
		{% for (var i = 0; i <= rs.length -1; i++) { %}
			{% if(rs[i].canWithdraw){ %}
				<a href="/manage/account/download?coint={%=rs[i].propTag%}" class="item clearfix">
					<span class="left color_font"> {%=rs[i].propTag%} </span>
					<span class="right">{%=rs[i].balance6%}</span>
				</a>
			{% } %}
		{% } %}
</script>
<script type="text/x-tmpl" id="tmpl-address_list_body">
		{% for (var i = 0; i <= rs.length -1; i++) { %}
			<div class="item clearfix">
				<div class="item_1 width272">
				{% if(rs[i].memo == "") { %}
					--
				{% }else{ %}
					{%=rs[i].memo%}
				{% }%}
				</div>
				<div class="item_1 item_1_2 width585">
					{%=rs[i].address%}
				</div>
				<div class="item_1 item_1_3 width340" data-id="{%=rs[i].id%}" data-memo="{%=rs[i].memo%}" data-address="{%=rs[i].address%}">
					{% if(rs[i].lockStatus == 1) { %}
						<strong class="hover_text">
								<div class="text_div tag ${lan}">${L:l(lan,"您当前为“安全模式”，新增提现地址后将被锁定24小时。")}</div>
							</strong>
							<span class="withdraw_href security_a">${L:l(lan,"锁定")}</span>
					{% }else{ %}   
						<a class="withdraw_href" href="/manage/account/download/downloadDetails?coint=${coint.propTag}&addressId={%=rs[i].id%}">${L:l(lan,"提现")}</a> 
				  	{% }%}
					<span class="withdraw_a address_memo">${L:l(lan,"编辑")}</span>
					<span class="withdraw_a address_delete">${L:l(lan,"删除")}</span>
				</div>
			</div>
		{% } %}
</script>
<jsp:include page="/common/foot.jsp" />
</body>
</html>