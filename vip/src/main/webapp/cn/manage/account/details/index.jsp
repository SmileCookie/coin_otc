<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>

<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${L:l(lan,"充值-充值上部-标题-1")}-${WEB_NAME }-${WEB_TITLE }</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0 maximum-scale=1, user-scalable=no">
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<link rel="stylesheet" href="${static_domain }/statics/css/web.pay.css?V${CH_VERSON }">
<link rel="stylesheet" href="${static_domain }/statics/css/web.asset.css?V${CH_VERSON }">
<script>
	if(JuaBox.isMobile()){
			JuaBox.mobileFontSize();
			$("<link>").attr({ rel: "stylesheet",type: "text/css",href: "${static_domain }/statics/css/mobile/mobile.user.css?V${CH_VERSON }"}).appendTo("head");
			$("<link>").attr({ rel: "stylesheet",type: "text/css",href: "${static_domain }/statics/css/mobile/top_foot_mobile.css?V${CH_VERSON }"}).appendTo("head");
	}
</script>
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
											<span class="search">${L:l(lan,"充值-充值上部-标题-1")} ${coint.propTag}<i class="jiantou"></i></span>
											<div id="search_warp" class="${lan}_cong">
												<div class="input_warp">
													<input type="text" class="search_input" id="checkcoin" />
													<button id="btnCheckCoin" class="iconfont2 icon-Shape-search"></button>
												</div>
												<section class="btc_list" id="btc_list">
												</section>
											</div>
										</h2>
										<c:if test="${allowWithdraw}">
											<ul class="tab-link">
												<li class="active"><a href="javascript:void(0);">${L:l(lan,"充值")}</a></li>
												<li>
													<a href="${vip_domain}/manage/account/download?coint=${coint.propTag}">${L:l(lan,"提现")}</a>														
												</li>
											</ul>	
										</c:if>						
										
									<section class="<c:if test="${!isSafePwd}">tablist-tips-warp</c:if> clearfix">
										<div class="<c:if test="${!isSafePwd}">left</c:if> bk-tabList-bd">
											<div id="needPhoneGoogleDiv" class="<c:if test="${isSafePwd}">hide</c:if>" >
												<div class="shimingpoper">
													<div class="safepwd-tip">
														${L:l(lan,'系统检测到您没有设置资金密码，暂时不能进行充值业务，为了您的账号安全，请设置资金密码。')}
														<p class="mt10 mb0">
															<a href="${vip_domain}/manage/auth/pwd/safe" class="btn btn-set mr15" target="_self">${L:l(lan,"设置资金密码")}</a>
														</p>
													</div>
												</div>
											</div>
											<div class="deposit-box <c:if test="${!isSafePwd}">hide</c:if>">
												<div class="key_wrap wid60">
													<h4 class="assets-sub-title">${L:l1(lan,'充值-充值中部-标签-2',coint.propTag)}</h4>
													<div class="key-box clearfix">
														<div class="keyPreImg">
															<img class="pc_block keyPreImg_s" id="keyPreImg" src="/ac/qrcode?code=${btcKey.keyPre}&width=115&height=115" />
														</div>
														<div class="keyPreCopy">
															<span class="text-done" id="keyPreCopy">${btcKey.keyPre}</span>
															<p class="ft12">${L:l(lan,'充值-充值中部-二维码提示-1')}</p>
														</div>
														<a class="btn btn-skip" id="copy"  data-clipboard-target="keyPreCopy">${L:l(lan,'充值-充值中部-按钮-1')}</a>														
													</div>
												</div>
												<div class="pay-tip wid40">
													<h4>${L:l(lan,'充值-充值中部-充值提示-1')}</h4>
													<p>1. ${L:l2(lan,'充值-充值中部-充值提示-2',coint.propTag,coint.propTag)}</p>
													<p>2. ${L:l1(lan,'充值-充值中部-充值提示-3',coint.inConfirmTimes)}</p>
													<p>3. ${L:l(lan,'充值-充值中部-充值提示-4')}</p>
												</div>
											</div>
										</div>
										<div class="vip-tip left <c:if test='${isSafePwd}'>hide</c:if>">
											<dl class="">
												<dt>${L:l(lan,'充值-充值中部-充值提示-1')}</dt>
												<dd>1. ${L:l2(lan,'充值-充值中部-充值提示-2',coint.propTag,coint.propTag)}</dd>
												<dd>2. ${L:l1(lan,'充值-充值中部-充值提示-3',coint.inConfirmTimes)}</dd>
												<dd>3. ${L:l(lan,'充值-充值中部-充值提示-4')}</dd>												
											</dl>
										</div>
									</div>
								<section>		
									<div class="bk-new-tabList">
										<h2>${L:l(lan,'充值-充值下部-充值记录标题-1')}</h2>
										<div class="bk-new-tabList-bd">
											<table class="table-history">
												<thead>
													<tr>
														<th width="15%">${L:l(lan,'状态（确认）')}</th>
														<th width="15%" class="text-center">${L:l(lan,'币种')}</th>
														<th width="15%" class="text-center">${L:l(lan,'数量')}</th>
														<th width="55%" class="text-center borright">${L:l(lan,'地址')}</th>
													</tr>
												</thead>
												<tbody id="chargeRecordDetail">
												</tbody>
											</table>
										</div>
										<div class="bk-tabList-fd bk-pageNav" id="chargeRecordDetail_Page">
											<!-- I'm page -->
										</div>
									</div>
								</div>
							</div>
						</div>
				</div>
			</div>
		</div>
		<jsp:include page="/common/foot.jsp" />
	</div>
	<script type="text/x-tmpl" id="tmpl-chargeRecordDetail">
      {% for (var i = 0; i <= rs.length -1; i++) { %}
			<tr class="bk_payInOut_tr">
				<td>
					<p class="confirm-detail {%=rs[i].status==0? 'orange':''%} {%=rs[i].status==1? 'red':''%}">
					  {%=rs[i].showStatus%}
					  {% if(rs[i].status == 0){ %}
						<span>（{%=rs[i].confirmTimes%}/{%=rs[i].totalConfirmTimes%}）</span>
					  {% } %}
					</p>
					{%=rs[i].recentTime%}
				</td>
				<td class="text-center">{%=rs[i].coinName%}</td>
				<td class="text-center">{%=rs[i].amount%}</td>
				<td>
					{%=rs[i].toAddress%}
					<p class="txid-detail">
						<span class="txid">Txid: <a href="{%=rs[i].webUrl%}" class="btn-check" target="_blank">{%=rs[i].txId%}</a></span>
					</p>							
				</td>
			</tr>
	  {% } %}
	</script>

<script type="text/javascript" src="${static_domain}/statics/js/common/zeroclipboard/ZeroClipboard.min.js"></script>
<script type="text/javascript">

$(function() {
	$('.btn-case').on('click touchend', function(e) {
      var el = $(this);
	  var link = el.attr('href');
      window.location = link;
   })
	$('#keyList li').on('click', function() {
		$('#keyPre').val($(this).html());
		$('#keyPreCopy').html($(this).html());
		$('.keyPreImg_s').attr('src', '/ac/qrcode?code='+$(this).html()+'&width=160&height=160');
	});

	ZeroClipboard.config({swfPath: "${static_domain}/statics/js/common/zeroclipboard/ZeroClipboard.swf"});
	var client = new ZeroClipboard($("#copy"));
    client.on("copy", function (event) {
		var clipboard = event.clipboardData;
		clipboard.setData( "text/plain", $("#keyPreCopy").html());
		JuaBox.sure("${L:l(lan,'复制成功！')}");
	});
});

function newAddr() {
	$.ajax({
		type:'POST',
		url:'/manage/account/charge/btcUsenew',
		data:'',
		dataType:'json',
		success:function(json) {
			if (json.isSuc) {
				Right(json.des, {call: function() {
					window.location.reload();
				}});
			} else {
				JuaBox.showWrong(json.des);
			}
		},
		error: function() {
			JuaBox.showWrong('网络访问出错，请稍后重试');
		}
	});
}

</script>
<script type="text/javascript">
	require(['module_asset'],function(asset){
		asset.charge_init("${coint.propTag}");
	});
</script>
<script type="text/x-tmpl" id="tmpl-btc_list">
		{% for (var i = 0; i <= rs.length -1; i++) { %}
			{% if(rs[i].canCharge){ %}
				<a href="/manage/account/charge?coint={%=rs[i].propTag%}" class="item clearfix">
					<span class="left color_font"> {%=rs[i].propTag%} </span>
					<span class="right">{%=rs[i].balance6%}</span>
				</a>
			{% } %}
		{% } %}
</script>
</body>
</html>