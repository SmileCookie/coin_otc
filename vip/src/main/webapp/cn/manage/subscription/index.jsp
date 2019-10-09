<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<!doctype html>
<html>
<head>
	<jsp:include page="/common/head.jsp" />
	<title>${L:l(lan,'ABCDEF申购-左侧子导航-标签1-ABCDEF申购')}-${WEB_NAME }-${WEB_TITLE }</title>
	<meta name="keywords" content="${WEB_KEYWORD }" />
	<meta name="description" content="${WEB_DESC }" />
	<link rel="stylesheet" href="${static_domain }/statics/css/web.subscription.css?V${CH_VERSON }">
	<link rel="stylesheet" href="${static_domain }/statics/css/web.loan.css?V${CH_VERSON }">
</head>
<script type="text/javascript" >
	var isloading = false;

	var preGbcAmount = ""; //上次输入的gbc数量
	var preSgAmount = ""; //上次输入的兑换币数量

	var currentCoin = 'BTC';//当前选择的币种
	var currentCoinAmount = '${btcBalance}'//当前选择币种的余额

	var totalNum = '${exchangeNumInfo.totalNum}';//总的允许购买GBC数量
	var exchangeNum = '${exchangeNumInfo.exchangeNum}';//已购买的GBC数量

	var coinRatioList = {
		<c:forEach items="${exchangeRate}" var="item">
		'${item.name}' : '${item.value}',
		</c:forEach>
	};
	var coinAmountList = {
		<c:forEach items="${coinAndBalance}" var="coin"  varStatus="varStatus" >
		'${coin.propTag}':'${coin.balance}',
		</c:forEach>
	}

	$(function(){

		$("#sgAmount input").attr("placeholder",bitbank.L('余额')+' '+currentCoinAmount);
		$("#tableList .tablist-success").html(bitbank.L('成功'));

		$('#gbcAmount input').on('keyup',function(){
			var thatVal = $(this).val();
			if(!/^[0-9]*\.?[0-9]+(eE?[0-9]+)?$/.test(thatVal)){
				if(thatVal!=""){
					$(this).val(preGbcAmount);

				}else{
					$(this).val("");
					preGbcAmount = thatVal;

					$("#sgAmount input").val("");
					preSgAmount = thatVal
				}
			}else{
				preGbcAmount = thatVal;
				//计算sg的值
				console.log(thatVal)
				setSg(thatVal);
			}

		});


		$('#sgAmount input').on('keyup',function(){
			var thatVal = $(this).val();
			if(!/^[\d\.]+$/.test(thatVal)){
				if(thatVal!=""){
					$(this).val(preSgAmount);
				}else{
					$(this).val("");
					preSgAmount = thatVal;

					$("#gbcAmount input").val("");
					preGbcAmount = thatVal
				}
			}else{
				preSgAmount =thatVal;
				//计算gbc的值
				console.log(thatVal)
				setGbc(thatVal);
			}
		});
	})

	//设置gbc
	function setGbc(val){
		// var ce = totalNum - exchangeNum
		// if(val > ce){
		// 	$("#gbcAmount input").val(ce);
		// 	preGbcAmount = ce;

		// }else{
		// 	var ye = 0;
		// 	for(var i in coinRatioList){
		// 		if(i == currentCoin){
		// 			ye = 1/coinRatioList[i]*val
		// 		}
		// 	}
		// 	ye = method.fixNumber(ye,6)
		// 	$("#sgAmount input").val(ye);
		// 	preSgAmount = ye;
		// }
		var ce = totalNum - exchangeNum
		var ye = sgToGbc(val);

		if(ye > ce){
			$("#gbcAmount input").val(ce);
			preGbcAmount = ce

			var sg = gbcToSg(ce);
			$("#sgAmount input").val(sg);
			preSgAmount = sg;
		}else{
			$("#gbcAmount input").val(ye);
			preGbcAmount = ye

		}
	}
	//设置sg
	function setSg(val){
		// var val = $(obj).val();
		// if(val!="" && val >0){
		// 	var ye = 0;
		// 	for(var i in coinRatioList){
		// 		if(i == currentCoin){
		// 			ye = coinRatioList[i]*val
		// 		}
		// 	}
		// 	ye = method.fixNumber(ye,0)
		// 	$("#gbcAmount input").val(ye);
		// 	preGbcAmount = ye
		// }

		var ce = totalNum - exchangeNum
		//var ye = sgToGbc(val);
		console.log('差额:'+ ce);
		if(val > ce){
			$("#gbcAmount input").val(ce);
			preGbcAmount = ce

			var sg = gbcToSg(ce);
			$("#sgAmount input").val(sg);
			preSgAmount = sg;
		}else{
			val = gbcToSg(val);
			$("#sgAmount input").val(val);
			preSgAmount = val

		}
	}

	function sgToGbc(val){
		var ye = 0;
		for(var i in coinRatioList){
			if(i == currentCoin){
				ye = coinRatioList[i]*val
			}
		}
		ye = method.fixNumber(ye,0)
		return ye;
	}
	function gbcToSg(val){
		var sg = 0;
		for(var i in coinRatioList){
			if(i == currentCoin){
				sg = 1/coinRatioList[i] * val
			}
		}
		sg = method.fixNumber(sg,6);
		return sg;
	}



	//选择币种
	function cointChange(coint,blance,obj){

		$("#gbcAmount input").val('');
		$("#sgAmount input").val('');

		$('#selCoin').html($(obj).html());
		$('#sgAmount input').attr('placeholder',bitbank.L('余额')+' '+blance);
		$('#sgAmount span').html(coint.toUpperCase());
		currentCoin = coint.toUpperCase()
		currentCoinAmount =blance;
	}

	function submitCoint(){
		var saleType = $("#saleType").val();
		var exchangeNum1 = $("#gbcAmount input").val();
		var sourceCurNum = $("#sgAmount input").val();
		var coinName = currentCoin;

		var va = totalNum - exchangeNum;
		if(va < exchangeNum1){
			JuaBox.showTip('剩余配售额度不足');
			return;
		}

		if(!isloading) {
			isloading = true;
			$.ajax({
				url : "/manage/promotion/ico/userExchangeNum",
				data:{
					'saleType':saleType,
					'exchangeNum':exchangeNum1,
					'sourceCurNum':sourceCurNum,
					'coinName':coinName
				},
				dataType : "JSON",
				success : function(json) {
					isloading = false;
					$("#gbcAmount input").val('');
					$("#sgAmount input").val('');
					JuaBox.sure(json.des,{
						closeFun:function(){
							if(json.isSuc){
								// getTableList();
								window.location.reload()
							}
						}
					});
				},
				error : function(json){
					isloading = false;
					JuaBox.sure('error');
					$("#gbcAmount input").val('');
					$("#sgAmount input").val('');
				}
			});
		}

	}

	function getTableList(){
		$.ajax({
			url : "/manage/promotion/ico/getExchangeRecordListJson",
			data:{
			},
			dataType : "JSON",
			success : function(json) {
				var dates = json.datas
				var html =''
				for(var i in dates){
					html+='<tr><td>'+dates[i].sendTime+'</td><td>'+dates[i].coinName+'</td><td>'+dates[i].amount+'</td><td>'+dates[i].remark+'</td><td>success</td></tr>'
				}
				$("#tableList").html(html);
			},
			error : function(json){
			}
		});
	}

</script>
<body>
<div class="bk-body">
	<jsp:include page="/common/top.jsp" />
	<div class="mainer">
		<div class="container">
			<jsp:include page="/common/trend.jsp" />
			<div class="user-panel">
				<jsp:include page="/cn/manage/account/menu.jsp" />
				<div class="content">
					<div class="bk-payInOut">
						<div class="cont-row">
							<div class="row">
								<div class="bk-assets">
									<h2>${L:l(lan,'ABCDEF申购-左侧子导航-标签1-ABCDEF申购')}</h2>
									<div class="gbc-ratio">
										<p class="gbc-title">${L:l(lan,'ABCDEF申购-现阶段ABCDEF兑换比例-标题1-现阶段ABCDEF兑换比例')}</p>
										<p class="gbc-content" id="gbcRatio">
											<c:forEach items="${exchangeRate}" var="item">
												${item.name}=1 : ${item.value} &nbsp;
											</c:forEach>
										</p>
									</div>
									<div class="fill-dizhi clearfix new">
										<div class="pay-form">
											<form autocomplete="off">
												<input type="text" class="hid-input" name="cashAmount" />
												<input type="password" class="hid-input" name="safePwd" />
												<div class="fill-dizhi-con" id="mainForm">
													<fmt:formatNumber value="${canWithdraw}"  pattern="0.000000" var="canWith"/>
													<h3>${L:l(lan,'ABCDEF申购-ABCDEF申购申请表单左侧-标题1-ABCDEF申购申请')}</h3>
													<h4 class="sub-title sub-detail">${exchangeNumInfo.des}</h4>
													<ul>
														<li>
															<em class="name">${L:l(lan,'ABCDEF申购-ABCDEF申购申请表单左侧-标签1-选择币种：')}</em>
															<div class="gbc-changeB-box" id="gbc-changeB-box">
																<div aria-expanded="false" class="first">
																	<ul class="loan-secd" style="margin-right:0;background:#fff;">
																		<li class="dropdown">
																			<a class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
																				<div class="selcoin" id="selCoin">
																					<i class="loan-coin loan-btc"></i>
																					<span>
																					<b class="coin-name">BTC</b>
																					<b>Bitcoin</b>
																				    </span>
																				</div>
																				<i class="iconfont2 ft14 right loan-down">&#xe600;</i>
																			</a>
																			<ul class="dropdown-menu animated bk-secd-menu fadeIn loan-dropdown-menu" id="loanDrop" data-animation="fadeIn" role="menu" style="left:auto;right:0">
																				<c:forEach items="${coinAndBalance}" var="coin"  varStatus="varStatus" >
																					<li>
																						<a href="###" onClick="javascript:cointChange('${coin.key}','${coin.balance}',this);">
																							<i class="loan-coin loan-${coin.key}"></i>
																								<b class="coin-name">${coin.propTag}</b>
																								<b>${coin.propEnName}</b>
																						</a>
																					</li>
																				</c:forEach>
																			</ul>
																		</li>
																	</ul>
																</div>

																<div class="jiantou">></div>

																<div aria-expanded="false" class="two">
																	<ul class="loan-secd" style="margin-right:0;background:#fff;">
																		<li>
																			<a>
																				<div class="selcoin" id="selCoin">
																					<img src="${static_domain }/statics/img/common/gbc.png"/>
																					<span><b class="coin-name">ABCDEF</b><b>Globalcoin</b></span>
																				</div>
																			</a>

																		</li>
																	</ul>
																</div>
															</div>

														</li>
														<li>
															<div class="group-top">
																<em class="name1">${L:l(lan,'ABCDEF申购-ABCDEF申购申请表单左侧-标签2-申购金额：')}</em>
															</div>
															<div class="gbc-sgje-box">
																<div class="first" id="sgAmount">
																	<input type="text" name="sgAmout" id="sgAmountInput"  autocomplete="off"/>
																	<span class="bitunit">${coint.propTag}</span>
																</div>
																<div class="jiantou">></div>
																<div class="two" id="gbcAmount">
																	<input  name="cashAmount" type="text"   autocomplete="off" />
																	<span class="bitunit" style="background:#fff">ABCDEF</span>
																</div>
															</div>
														</li>
													</ul>

													<input type="hidden" value="${saleType}" id="saleType"/>

													<div class="submit-box gbc">
														<a class="btn btn-submit-blue" href="javascript:submitCoint();">${L:l(lan,'ABCDEF申购-ABCDEF申购申请表单左侧-按钮1-立即申购')}</a>
													</div>


												</div>
											</form>
										</div>
										<div class="center-line"><div class="line"></div></div>
										<div class="pay-tip">
											<h4>${L:l(lan,'ABCDEF申购-ABCDEF申购申请表单右侧-标题1-ABCDEF申购注意事项')}</h4>
											<p>${L:l(lan,'ABCDEF申购-ABCDEF申购申请表单右侧-标签1-1.申购兑换比例不会根据市场价格波动')}</p>
											<p>${L:l(lan,'ABCDEF申购-ABCDEF申购申请表单右侧-标签2-2.不同阶段的申购兑换比例不同')}</p>
											<p>${L:l(lan,'ABCDEF申购-ABCDEF申购申请表单右侧-标签3-3.申购最小单位为1ABCDEF')}</p>
											<p>${L:l(lan,'ABCDEF申购-ABCDEF申购申请表单右侧-标签4-4.申购一旦提交不可撤回，平台上线前不可兑换成其他货币')}</p>
											<p>${L:l(lan,'ABCDEF申购-ABCDEF申购申请表单右侧-标签5-5.如果您有任何申购疑问，请发送邮件至support@btcwinex.com')}</p>
										</div>
									</div>
								</div>
								<div class="bk-tabList">
									<div class="cjjl">
										<div class="bk-tabList-hd clearfix">
											<div class="btn-group bk-btn-group" role="group">
												<a class="btn-none" role="button">${L:l(lan,'ABCDEF申购-ABCDEF申购记录-标题1-ABCDEF申购记录')}</a>
											</div>
										</div>
										<div class="bk-tabList-bd table-responsive" style="min-height:400px;">
											<!--<jsp:include page="/cn/manage/account/downRecord/listAjax.jsp" />-->
											<table class="table table-striped table-bordered text-left">
												<thead>
												<tr>
													<th>${L:l(lan,'ABCDEF申购-ABCDEF申购记录-表头1-日期')}</th>
													<th>${L:l(lan,'ABCDEF申购-ABCDEF申购记录-表头2-币种')}</th>
													<th>${L:l(lan,'ABCDEF申购-ABCDEF申购记录-表头3-支出')}</th>
													<th>${L:l(lan,'ABCDEF申购-ABCDEF申购记录-表头4-收到(ABCDEF)')}</th>
													<th>${L:l(lan,'ABCDEF申购-ABCDEF申购记录-表头5-状态')}</th>
												</tr>
												</thead>
												<tbody id="tableList">
												<c:choose>
													<c:when test="${exchangeRecordList.size() > 0}">
														<c:forEach items="${exchangeRecordList}" var="item"  varStatus="varStatus" >
															<tr>
																<td>${item.sendTime}</td>
																<td>${item.coinName}</td>
																<td>${item.amount}</td>
																<td>${item.remark}</td>
																<td class="tablist-success"></td>
															</tr>
														</c:forEach>
													</c:when>
													<c:otherwise>
														<tr>
															<td colspan="5">
																<div class="bk-norecord">
																	<p><i class='iconfont2 mr5'>&#xe653;</i>${L:l(lan,'暂时没有相关记录')}</p>
																</div>
															</td>
														</tr>
													</c:otherwise>
												</c:choose>
												</tbody>
											</table>
										</div>
										<div class="bk-tabList-fd bk-pageNav" id="downloadRecordDetail_Page">
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
</div>
<!-- Body From mainPage End -->
<jsp:include page="/common/foot.jsp" />
<!-- Common FootMain End -->
<script type="text/javascript">
	require(['module_asset'],function(asset,market){
		setTimeout(function(){
			asset.downloadRecordInit("${coint.stag}");
		},100);
	});
</script>
</body>
</html>