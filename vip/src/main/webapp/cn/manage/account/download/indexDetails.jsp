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
</script>
<body>
<div class="bk-body">
  <jsp:include page="/common/top.jsp" />
  <div class="mainer">
  	  <div class="container2">
		<div class="content">
			<div class="bk-payInOut">
				<div class="cont-row">
					<div class="row">
						<section class="withdrawal_details ${canWithdraw}">
						<fmt:formatNumber value="${(balance-0.0000000049)<=0?0:(balance-0.0000000049)}"  pattern="0.00000000" var="balance2"/>
							<h2 class="mb40">${L:l(lan,'提现')} ${coint.propTag}</h2>
							<div class="withdrawal_details_head mb20">
                                <c:if test="${lockStatus == 1}"> 
									<div class="posi_top10">${L:l(lan,'提示：')}${lockTips} </div>
								</c:if>
								<div class="address_div">
									<div class="item clearfix mb10">
										<span class="width_item_1 left">${L:l(lan,"标签")}:</span>
										<span class="width_item_1 right">${memo}</span>
									</div>
									<div class="item clearfix mb10">
										<span class="width_item_2 left">${L:l(lan,"提现地址")}:</span>
										<span class="width_item_2 right">${address}</span>
									</div>	
									<div class="item clearfix">
										<span class="width_item_3 left">${L:l(lan,"提现-提现中部-标签-7")}</span>
										<span class="width_item_3 right">-<fmt:formatNumber value="${fees}" pattern="##0.0000####"></fmt:formatNumber></span>
									</div>	
								</div>
							</div>
							
							<section class="from_withdrawal">
								<div class="input_div">
									<h5 class="clearfix">${L:l(lan,"提现-提现中部-标签-4") }
										<div class="limit_24">
											<span>
												<b>${L:l(lan,'24H提现额度_1')}</b>${downloadLimit} BTC
											</span>
											<span class="mar10 mal20">
												<b>${L:l(lan,'可用：')}</b>${availableDownload} BTC
											</span>
											<c:if test="${authResult == 0}"> 
												<a href="${vip_domain}/manage/auth/authentication">${L:l(lan,'提升额度_1')}</a>												
											</c:if>
										</div>
									</h5>
									<div class="input_div_1 input_div_2">
										<input type="text" autocomplete="off" id="cashAmount" class="input_1 input_2"/>
										<div class="bitunit">
												${L:l1(lan,"提现-提现中部-标签-2",balance2)} 
												<a href="###" class="btn-maxdraw" id="maxDraw">${L:l(lan,"全部提现")}</a>
										</div>
									</div>
								</div>
								<!-- <div class="input_div">
									<h5>${L:l(lan,"备注_1")}:</h5>
									<div class="input_div_1">
										<input type="text" autocomplete="off" id="remarks" placeholder='${L:l(lan,"如您被要求填写地址标签")}' class="input_1"/>
									</div>
								</div> -->
								<div class="input_div">
									<h5>${L:l(lan,"提现-提现中部-标签-5") }</h5>
									<div class="input_div_1">
										<input type="password" class="input_1 safePwd" autocomplete="off" />
									</div>
								</div>
								<div class="${payGoogleAuth ? "input_div" : "input_div mb20"}">
									<c:if test="${payMobileAuth || payEmailAuth}">
										<h5>${L:l(lan, payMobileAuth?"提现-提现中部-标签-6":"邮件验证码") }</h5>
										<div class="input_div_1 input_div_2">
											<input type="text" class="input_1 input_2" id="mobileCode" name="mobileCode">
											<a href="javascript:sendCode();" id="sendCodeBtn" class="">${L:l(lan, payMobileAuth?"提现-提现中部-按钮-1":"提现-提现中部-按钮-1") }</a>
										</div>
									</c:if>
								</div>
								<c:if test="${payGoogleAuth}">
									<div class="input_div mb20">
										<h5>${L:l(lan,"Google验证码") }${L:l(lan,"：")}</h5>
										<div class="input_div_1">
											<input type="text" id="googleCode" name="googleCode" autocomplete="off" class="input_1">
										</div>
									</div>
								</c:if>
								<div class="pay-tip">
										<h4>${L:l1(lan,'提现-提现中部-提现提示-1',coint.propTag)}</h4>
										<p>1. ${L:l3(lan,'提现-提现中部-提现提示-2',coint.propTag,fees,coint.propTag)}</p>
										<c:choose>
											<c:when test="${lan == 'en'}">
												<p>2. ${L:l4(lan,'提现-提现中部-提现提示-3',everyTimeCash,coint.propTag,everyTimeCash,coint.propTag)}</p>
												<p>3. ${L:l1(lan,'提现-提现中部-提现提示-4',coint.outConfirmTimes)}</p>											
											</c:when>
											<c:otherwise >
												<p>2. ${L:l3(lan,'提现-提现中部-提现提示-3',coint.propTag,everyTimeCash,everyTimeCash)}</p>
												<p>3. ${L:l2(lan,'提现-提现中部-提现提示-4',coint.propTag,coint.outConfirmTimes)}</p>											
											</c:otherwise>
										</c:choose>
								</div>
								<c:if test="${lockStatus == 0}">  <!-- 0 不锁定、1 锁定 -->
									<div class="submit hover_background" id="submit">${L:l(lan,"提现-提现中部-按钮-2")}</div>
								</c:if>
								<c:if test="${lockStatus == 1}"> <!-- 0 不锁定、1 锁定 -->
									<div class="submit locked">${L:l(lan,"提现-提现中部-按钮-2")}</div>
								</c:if>
							</section>
						</section>
					</div>
				</div>
			</div>
		</div>
		</div>
  	  </div>
  </div>
</div>
	<!-- Body From mainPage End -->
	<!-- Common FootMain Begin -->
	<jsp:include page="/common/foot.jsp" />
	<!-- Common FootMain End -->
	<script>
	vip.addTips('cashAmount','${L:l1(lan,"请正确填写%%的数量", coint.propTag)}');
	vip.addTips('minD','${L:l2(lan,"提%%的数量不能小于%%", coint.propTag, minD)}');
	vip.addTips('receiveAddr','${L:l1(lan,"请正确填写%%的接收地址", coint.propTag)}');
	vip.addTips('safePwd','${L:l(lan,"请填写资金密码")}');
	vip.addTips('canWithdraw','${L:l2(lan,"单笔限额最多为%% %%", canWithdraw, coint.propTag)}');
	vip.addTips('请填写您收到的验证码！','${L:l(lan,"请填写您收到的验证码！")}');
	vip.addTips('请输入Google验证码！','${L:l(lan,"请输入Google验证码！")}');
	$("#maxDraw").on("click",function(){
		var availableNum= "${balance2}";		
		$("#cashAmount").val(availableNum)
	})
	function sendCode(){
		$.ajax({
			type : "POST",
			url : "/userSendCode",
			data : {
				codeType : 8,
				currency : "${coint.propTag}",
			},
			dataType : "json",
			error : function() {
				JuaBox.sure(jsLan[1]);
				inAjaxing = false;
			},
			success : function(json) {
				inAjaxing = false;
				if (json.isSuc) {
					if (json.datas.isEmail) {
						JuaBox.info(json.des);
					}
					settime($('#sendCodeBtn'));
				} else if ('needMobileAuth' == json.des) {
					JuaBox.sure(bitbank.L("您未进行手机认证，请先进行手机认证"));
				} else {
					JuaBox.sure(json.des);
				}
			}
		});
	}

	var countdown = 60;
	function settime(obj) {
		if (countdown == 0) {
			$(obj).removeAttr("disabled");
			$(obj).text("${L:l(lan,'提现-提现中部-按钮-1')}");
			countdown = 60;
		} else {
			$(obj).attr("disabled", true);
			$(obj).text("${L:l(lan,'已发送')}"+"(" + countdown + ")");
			countdown--;
			setTimeout(function() {
				settime($(obj))
			}, 1000)
		}
	}

	$('#cashAmount').on('keyup',function(e){  //数量
		if((e.keyCode>=48&&e.keyCode<=57)||(e.keyCode>=96&&e.keyCode<=105)){
			Big.RM = 0;
			var $thatVal = $(this).val() || 0;
			var $balance = ${balance};
			var $fees = ${fees};
			if($thatVal > $balance){
				$(this).val(new Big($balance).toFixed(8));
			}else{
				if($thatVal.toString().split(".")[1]&&$thatVal.toString().split(".")[1].length>8){
					$thatVal = 	new Big($thatVal).toFixed(8)
					$(this).val($thatVal)
				}
			}
			
		}
	});
	var submitStatus = 1;
	$("#submit").on("click",function(){
		var cashAmount = $.trim($("#cashAmount").val()); //提现数量
		var remarks = $.trim($("#remarks").val()) || "";  //备注
		var safePwd = $.trim($(".safePwd").val());  //资金密码
		var mobileCode = $.trim($("#mobileCode").val());  //验证码
		var googleCode = $.trim($("#googleCode").val()); //谷歌验证码 ${payGoogleAuth}
		var minD = ${minD}; //最小提现数量 
		var canWithdraw = ${canWithdraw};//最大提现数量
		if(!cashAmount || !vip.tool.isFloat(cashAmount)){
			JuaBox.showTip(vip.L("cashAmount"));
			$("#cashAmount").val('');
			$("#cashAmount").focus();
			return;
		}
		// if(remarks.length > 10){
		// 	JuaBox.showWrong("${L:l(lan,'备注不得超过10个字符')}")
		// 	return;
		// }
		if(parseFloat(cashAmount) < parseFloat(minD)){
			JuaBox.showTip(vip.L("minD"));
			return;
		}
		
		// if (parseFloat(cashAmount) > parseFloat(canWithdraw)) {
		// 	JuaBox.showTip(vip.L("canWithdraw"));
		// 	return;
		// }
		if (!safePwd){
			JuaBox.showTip(vip.L("safePwd"));
			return;
		}
		if (( ${payMobileAuth || payEmailAuth} ) && !mobileCode) {
			JuaBox.showTip(vip.L("请填写您收到的验证码！"));
			return;
		}
		if (${payGoogleAuth}) {
			if (!googleCode) {
				JuaBox.showTip(vip.L("请输入Google验证码！"));
				return;
			}
		}
		if(submitStatus==-1){
			JuaBox.showWrong("${L:l(lan,"请勿重复提交！")}");
			return;
		}
		submitStatus = -1;
		var alertboxs = new alertBox({
			cashAmount:cashAmount,  //数量
			fees:"${fees}",			//手续费
			memo:"${memo}",			//标签
			address:"${address}",		//地址
			remarks:remarks,			//备注
			safePwd:safePwd,
			mobileCode:mobileCode,
			googleCode:googleCode
		});
	})
		function alertBox(data){
			var $this = this;
			this.docW = $(document).width();
			this.docH = $(document).height();
			this.winW = $(window).width();
			this.winH = $(window).height();
			var title = "";
			var html_body = "";
			var foot_btn = "";
			data.remarks = data.remarks || "--";
				title = '<div class="tiltes mb40 mt35">${L:l(lan,'提现确认')}</div>';
				html_body = '<div class="alertBox_text">'+
								'<div class="mb10">${L:l(lan,'提现-提现中部-标签-4')} '+data.cashAmount+'</div>'+
								'<div class="mb10">${L:l(lan,'提现-提现中部-标签-7')} '+data.fees+'</div>'+
								'<div class="mb10">${L:l(lan,'标签')}: '+data.memo+'</div>'+
								'<div class="mb10">${L:l(lan,'提现-提现中部-标签-3')} '+data.address+'</div>'+
								// '<div>${L:l(lan,'备注_1')}: '+data.remarks+'</div>'+
							'</div>';
				foot_btn = '<div class="btns_div mt20">' +
								'<span class="btn close_alertBox">${L:l(lan,'取消')}</span>'+
								'<span class="btn submit" id="alert_submit">${L:l(lan,'确定')}</span>'+
							'</div>';
			var html = '<div class="alertBox">'+
							'<div class="alertBox_back"></div>'+
							'<div class="alertBox_body">'+
								title +
								html_body+
								foot_btn+
							'</div>'+
						'</div>';
			$("body").append(html);
			$(".alertBox").css({
				"width": Math.max(this.docW, this.winW),
				"min-width": "320px",
				"height": Math.max(this.docH, this.winH),
				"z-index": 100
			});
			$(".alertBox_body").height("280px").width("520px");
			$(".close_alertBox").on("click",function(){
				$(".alertBox").remove();
			})
			$("#alert_submit").on("click",function(){
				submit_ajax(data);
			})
		}
		function submit_ajax(json){
				$.ajax({
					url : "/manage/account/download/doSubmit/${coint.propTag}",
					type:"post",
					data :{
						cashAmount: json.cashAmount,// 提现金额
						receiveAddress: json.address,  //地址
						fees:json.fees,// 手续费
						safePwd:json.safePwd,// 资金密码
						mobileCode: json.mobileCode,//验证码
						googleCode:json.googleCode,
						memo:json.remarks// 备注
					},
					dataType : "JSON",
					success : function(json) {
						submitStatus = 1;
						JuaBox.sure(json.des,{
							closeFun:function(){
								if(json.isSuc){
									window.top.location.href = "/manage/account/download?coint=${coint.propTag}"
								}
								else{
									$(".alertBox").remove();
								}
							}
						});
					},
					error : function(json){
						submitStatus = 1;
						if(json.des=="1026"){
							window.top.location.href = "/manage/auth/pwd/safe";					
						}
					}
				});
		}
		
	</script>
</body>
</html>