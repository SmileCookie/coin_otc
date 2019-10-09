<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<!doctype html>
<html ${lan=="en" ? "class=\"en\"" : ""}>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
<jsp:include page="/common/head.jsp" />

<style type="text/css">
.loan-info {
	width:100%;
	margin: 0 auto;
	position: relative;
	zoom: 1;
}
.loan-info h1 {
	font-size: 14px;
	font-weight: 500;
	height: 30px;
	line-height: 30px;
	padding: 0 0 0 1px;
}
.loan-info-table{
    box-sizing:border-box;
	width:400px;
	padding:30px 20px 0px;
}

.loan-info-table li {
	width:100%;
	color: #484C4B;;
	text-align: right;
	font-size: 13px;
	overflow:hidden;
}
.loan-info-table li p{
   float:right;
}
.loan-info-table li .tit {
	float:left;
	text-align: left;
}
.lispace{
	margin:10px 0 20px;
	border-bottom: 1px solid #EEE;
}
.loan-info-table li.paid {
	font-size:15px;
}
.loan-info-table li.paid p span{
   	color: #128FDC;
}
.loan-info-table li span {
    font-weight:500;
}

.loan-info-table td span.schedule {
	display: inline-block;
	overflow: hidden;
	vertical-align: -2px;
}

.loan-info-table td span.schedule i {
	background: #ddd;
	border-radius: 3px 3px 3px 3px;
	display: inline-block;
	height: 12px;
	width: 100px;
}

.loan-info-table td span.schedule i u {
	background: #F06000;
	border-radius: 3px 3px 3px 3px;
	display: inline-block;
	height: 12px;
	width: 50px;
}

.loan-info-list {
	position:relative;
	padding-top: 15px;
}

.loan-info-list li {
	position:relative;
	line-height: 34px;
	padding-bottom: 10px;
	color: #666666;
	text-align: left;
}

.loan-info-list li .radioSpan {
	background-color:#ddd;
	padding:5px 15px;
	line-height:1;
	margin-top:6px;
	margin-left:3px;
}
.loan-info-list li .radioSpan.active {
	background-color:#10a8da;
	color:#fff;
}


.loan-info-list li .red {
	font-size: 20px;
}

.loan-info-list li span.tit {
	width: 105px;
	font-size:13px;
	float:left;
	text-align: left;
	display: inline-block;
	color: #484C4B;
}

.loan-info-list li input {
	padding: 0px 10px;
	height: 34px;
	line-height: 34px;
	font-size: 13px;
	color: #484C4B;
}

.loan-info-list li span.prompt {
	color: #666666;
}

.loan-info-list li span.red {
	color: #CC0000;
}
/* .loan-info-list li a.btn { background: #F9F9F9; border: 1px solid #D9D9D9; border-radius: 5px 5px 5px 5px; box-shadow: 0 1px 0 rgba(255, 255, 255, 0.3) inset, 0 1px 1px rgba(50, 50, 50, 0.05); color: #666666; display: inline-block; line-height: 24px; padding: 0 5px; text-align: center; width: 60px; text-decoration: none; } */
.loan-info-list li a {
	color: #3366CC;
}

.loan-info-button {
	padding-top: 10px;
	text-align: center;
}

.loan-info-button a {
	background: #B61D00;
	border: 1px solid #AA1800;
	border-radius: 5px 5px 5px 5px;
	box-shadow: 0 1px 0 rgba(255, 255, 255, 0.3) inset, 0 1px 1px rgba(50, 50, 50, 0.05);
	color: #FFFFFF;
	display: inline-block;
	font-size: 18px;
	font-weight: 500;
	line-height: 38px;
	padding: 0 10px;
	text-align: center;
	text-decoration: none;
	text-shadow: 0 1px 1px rgba(0, 0, 0, 0.2);
	width: 150px;
}

.loan-info-caption {
	background: #FFFCF0;
	position: absolute;
	bottom: -10px;
	right: 0;
	padding: 10px;
	color: #666666;
}

.loan-info-caption strong {
	display: block;
	color: #333333;
	padding: 10px 0;
}

.loan-info-caption table {
	width: 300px;
}

.loan-info-caption td {
	border: 1px #eeeeee solid;
	background: #fff;
	padding: 5px;
	text-align: center;
}

.loan-info-caption .txt td {
	color: #999999;
}
/*******control.css end****/
.header {
	
}

.Toolbar {
	width: 100%;
	text-align: left;
	padding:10px 20px;
    box-shadow: 0 1px 2px 0 rgba(0,0,0,0.10);	
}

.Toolbar h2 {
	font-weight: bold;
	font-size: 18px;
	color: #484C4B;
	line-height: 32px;
	border-bottom:none;
	margin: 0;
	padding:0;
}

.ps1-close {
	color:#484C4B;
	cursor: pointer;
	font-size:28px;
	height: 16px;
	line-height: 16px;
	position: absolute;
	right: 15px;
	text-align: center;
	top: 14px;
	width: 16px;
}

.ps1-close:hover {
	color: #666666;
	text-decoration: none;
}

.jqTransformRadioWrapper {
	margin-top: 10px;
	margin-right: 5px;
}
/* .jqcheckboxWrapper { margin: 10px 5px 12px 3px; } */
.do {
	padding: 45px 0px 20px;
	text-align: center;
}

.loan-info-list li .d_select {
	font-size: 16px;
	margin: 12px 2px -10px 0px;
	display: inline-block;
	color: #999999;
}

.loan-info-list li #diKouAmou {
	height: 22px;
	width: 118px;
	line-height: 22px;
	font-size: 20px;
	color: #CC0000;
}

.loan-info-list li #edu {
	height: 22px;
	width: 110px;
	line-height: 22px;
	font-size: 20px;
	color: #CC0000;
}

#tips {
	height: auto;
}

.loan-info-list li {
	overflow: hidden;
}
.wid100{
	width:100%;
}
.loan-info-list li .paidall input{
	height:17px;
    margin:0;
}
.paidall label{
	display:none;
}
.do .btn-blue2,.do .btn-gray2{
	width:177px;
}
.msg{
	position:absolute;
	right:10px;
	top:38px;
	font-size: 13px;
	color: #484C4B;
}
.loan-info-list li.tips{
	position:absolute;
	top:88px;
}
.loan-info-list li.tips p{
	color:#ccc;
	font-size:12px;
	padding-bottom:0px;
	line-height:16px;
}
.hideRadio{
	display:none;
}
</style>
</head>
<body style="background: #FFFFFF;">
	<div class="header">
		<div class="Toolbar">
			<h2>${L:l(lan,'融资融币借入-还币模式窗-标题-1')}</h2>
			<div class="ps1-close" onclick="Close();" style="font-weight: bold;" >×</div> 
		</div>
	</div>

	<div class="loan-info" id="uimain">
		<ul class="loan-info-table">
			<li>
				<p class="tit">${L:l(lan,'融资融币借入-还币模式窗-标签-1')}${L:l(lan,'：')}</p>
				<p><span><fmt:formatNumber value="${curLoan.amount}" pattern="0.######" /></span> ${curLoan.getFt().propTag }</p>
			</li>
			<li>
				<p class="tit">${L:l(lan,'融资融币借入-还币模式窗-标签-2')}${L:l(lan,'：')}</p>
				<p>  
					<input type="hidden" name="lixi" value="${curLoan.needLx}" />
					<span><fmt:formatNumber value="${curLoan.needLx }" pattern="0.######" /></span> 
					${curLoan.getFt().propTag }
				</p>
			</li>
			<li class="lispace"></li>
			<li class="paid">
				<p class="tit">${L:l(lan,'融资融币借入-还币模式窗-标签-3')}${L:l(lan,'：')}</p>
				<p><span id="aa"><fmt:formatNumber value="${curLoan.couldRepay + curLoan.needLx}" pattern="0.######" /></span> ${curLoan.getFt().propTag}</p>
			</li>
			<li style="display:none;">
				<p class="tit">${L:l(lan,'融资融币借入-还币模式窗-标签-5')}${L:l(lan,'：')}</p>
				<p><span><fmt:formatNumber value="${userAvailable}" pattern="0.######" /></span> ${curLoan.getFt().propTag }</p>
			</li>
			<font class="dikouyc"> <c:if test="${curLoan.deDuctCouponId > 0 }">
					<li>
						<p class="tit">${L:l(lan,'抵扣券')}${L:l(lan,'：')}</p>
						<p>${L:l(lan,'共')}
								<span id="aa"><fmt:formatNumber value="${zhelx }" pattern="0.0#######" /></span> 
								${curLoan.getFt().propTag} ，${L:l(lan,'已抵扣')} 
								<c:choose>
									<c:when test="${yidikou != null}">
										<span><fmt:formatNumber value="${yidikou }" pattern="0.0#######" /></span>
									</c:when>
									<c:otherwise>
										<span>0</span>
									</c:otherwise>
								</c:choose>
						${curLoan.getFt().propTag}</p>
					</li>
				</c:if>
			</font>
		</table>
		<ul class="loan-info-list">
			<li>
				<p class="msg">${curLoan.getFt().propTag }</p>
				<span class="tit">${L:l(lan,'融资融币借入-还币模式窗-标签-5')}</span>
			　　<c:if test="${curLoan.status == 3}">
					<input name="repay"  class="form-control wid100" style="display:inline-block;margin-top:5px;"
					position="s"  onkeyup="xswCheck(this , 6 , 1)"
					placeholder="${L:l2(lan,'账户%%余额：%%',curLoan.getFt().propTag,userAvailable)}"
					id="repay" type="text" size="30" />
					<input type="hidden" id="checkRepayType" class="ld" value="1" name="repayType" />	
				</c:if>
				<c:if test="${curLoan.status != 3}">
					<span class="paidall" style="float: right;">
						<input class="text-bottom" id="checkType" type="checkbox" checked="checked"  />
						${L:l(lan,'融资融币借入-还币模式窗-标签-4')}
					</span>
					<input name="repay"  class="form-control wid100" style="display:inline-block;margin-top:5px;"
					position="s" disabled="disabled"
					value='<fmt:formatNumber value="${curLoan.couldRepay + curLoan.needLx }" pattern="0.######" />'
					onkeyup="xswCheck(this , 3 , 1)"
					placeholder="${L:l2(lan,'账户%%余额：%%',curLoan.getFt().propTag,userAvailable)}"
					id="repay" type="text" size="30" />
					<input type="hidden" id="checkRepayType" class="ld" value="0" name="repayType" />	
				</c:if>
			</li>
			<li id="tips" class="tips clearfix ${curLoan.status == 3? '' : 'hideRadio'}" style="margin-bottom: 0;">
				<p class="mb0">${L:l(lan,'融资融币借入-还币模式窗-标签-7') }</p>
			</li>
			<font class="dikouyc">
				<c:if test="${curLoan.deDuctCouponId > 0 }">
					<li id="lidis" style="margin-left: 10px; margin-bottom: 0; padding-bottom: 0;">
						${L:l(lan,'实际扣款')}： 
						<span id='diKouAmou' name="diKouAmou" style="color: #CC0000; font-size: 20px;"></span>
						(${L:l(lan,'抵扣额度')}： 
						<c:choose>
							<c:when test="${yidikou != null}">
								<span id="dked" name="dked" style="color: #CC0000; font-size: 20px;"></span>
							</c:when>
							<c:otherwise> 0 </c:otherwise>
						</c:choose>)
					</li>
				</c:if>
			</font>
		</ul>
		<!--<div class="bk-divider"></div>-->
		<div class="do">
			<input type="hidden" name="id" value="${curLoan.id}" />
			<a class="btn btn-blue2" href="javascript:dosubmit();">${L:l(lan,'融资融币借入-还币模式窗-按钮-1')}</a>
			<a class="btn btn-gray2" data-dismiss="modal" onClick="Close();">${L:l(lan,'融资融币借入-还币模式窗-按钮-2')}</a>
		</div>
	</div>

	<script type="text/javascript">
	 
		// $(document).ready(function(){
		// 	dikoulixi(0);
		// });
       
		function dikoulixi(repayType){
			var repay=parseFloat($("#repay").val());
			var amount=parseFloat("${curLoan.amount}");//借款金额	
			var huanlixi=parseFloat("${curLoan.arrearsLx}");//还款金额的利息
			if (repayType == 1) {
				amount = repay;
			}
			var p2pOutRate=parseFloat("${p2pOutRate}");// 费率
			huanlixi = accAdd(huanlixi, (amount * p2pOutRate / 100));
			
			var dkId=parseFloat("${curLoan.deDuctCouponId}");//抵扣id
			var zj = 0;// 总金额
			var shenyu=0;
			if (dkId > 0) {
				var useState=parseFloat("${jine.useState}");//抵扣状态
				var zlixi=parseFloat("${zhelx}");/* 折算 */
				var yidikou=parseFloat("${yidikou}");//已抵扣
				var amountDeg=0;
				if (yidikou != null||yidikou!=0) {
					// 剩余抵扣 = 总抵扣券金额-已用抵扣券金额
					var deglixi = accSubtr(zlixi, yidikou);
					if ( amount > 0) {
						if (useState == 5 || deglixi <= 0) {
							amountDeg=deglixi;
							}
						}
					}
				// 如果已使用 ，抵扣券为零
				if (useState == 2) {
					amountDeg=0;
					}
				// 获取剩下的利息
				if (huanlixi > amountDeg) {
					shenyu=amountDeg;
					zj = accAdd(accSubtr(huanlixi, amountDeg), amount);
					}
				if (huanlixi <= amountDeg) {
					shenyu=huanlixi;
					zj = amount;
					}
				} else {
					zj = accAdd(amount, huanlixi);
					}
			if (repay == null || repay == 0) {
				shenyu=0;
				huanlixi=0;
				zj = 0;
				}
			if(shenyu<=0){
				shenyu=0;
			}
			// 	$("#dked").val(shenyu.toFixed(4));parseFloat(shenyu)
// 			if (shenyu.toString().length > 8||shenyu.toString().length > 8) {
				$("#dked").text(parseFloat(shenyu).toFixed(8));
				$("#diKouAmou").text(parseFloat(zj).toFixed(8));
// 			}else{
// 				$("#dked").text(parseFloat(shenyu));
// 				$("#diKouAmou").text(parseFloat(zj));	
// 			}
		}

	</script>

	<script type="text/javascript">
    function Close(){
		  $("div[id^='JuaMask']", parent.document).remove();
		  $("div[id^='JuaBox']", parent.document).remove();
	}

	$("#uimain").Ui();
	$(function(){
			$('#checkType').on('click',function(){
				var repayType = $(this).is(':checked')? 0 : 1;
				$('#checkRepayType').val(repayType);
				if(repayType == 0){
					$("#tips").hide();
					$("#repay").attr("disabled","disabled").val('<fmt:formatNumber value="${curLoan.couldRepay + curLoan.needLx }" pattern="0.0#######" />');
				}else{
					if(${curLoan.couldRepay<=minUnit}){
						JuaBox.sure("${L:l(lan,'本次还款额度已低于最小还款额度，不能选择部分还款。')}",{
							btnNum : 1,
							closeFun:function(){
									//console.info(index);
									$('#checkType').click();
								}
						});
					}else{
						$("#repay").removeAttr("disabled").val("");
					}
					$("#tips").show();
				};
				// dikoulixi(repayType);
			});
		
		$(".jqTransformRadioWrapper").next("span").each(function(){
			$(this).css({"cursor" : "pointer"});
			$(this).click(function(){
				$(".jqTransformRadioWrapper").next("span").removeClass("active");
				$(this).addClass("active");
				$(this).prev(".jqTransformRadioWrapper").find(".jqTransformRadio").trigger("click");
				JuaBox.reSetHeight();
			});
		});
	});
	function dosubmit(){
		if(!vip.user.checkLogin()){
			return;
		}
		var datas = FormToStr("uimain"); 
		if(datas == null){return;}
		vip.ajax({
			 needLogin : true, //需要登录
			 formId : "uimain",//表单ID
			 div : "uimain",//lodding的div
			 url : "/manage/loan/repay/huanNew" ,
			 dataType : "json",
			 suc : function(json){
				 JuaBox.showTip(json.des , {
					 btnNum : 1,
					 btnFun1:function(){
							//console.info(index);
							window.top.location.reload();
						}
				 });
			 },
			 err : function(json){
				 JuaBox.sure(json.des);
			 }
		});
	} 
	
	function amoutCheck(v , min , max){
		var amount = parseFloat($("input[name='repay']").val());
		if(amount < min){
			return false;
		}
		if(amount > max){
			return false;
		}
		return true;
	}
	
	function xswCheck(obj , max , type){//小数位验证
		var m = max + 1;
		var iv = $(obj).val();
		if(iv.indexOf(".") > 0 && iv.substring(iv.indexOf(".")).length > m){
				$(obj).val(iv.substring(0 , iv.indexOf(".") + m));
		}
	}
	$("body").click(function(){
		$(".tipsr").remove();
	});
	
	function accSubtr(arg1, arg2) {
        var t1 = 0, t2 = 0, m, n;
        try {
            t1 = arg1.toString().split(".")[1].length;
        } catch (e) {
            t1 = 0;
        }
        try {
            t2 = arg2.toString().split(".")[1].length;
        } catch (e) {
            t2 = 0;
        }
        with (Math) {
            //动态控制精度长度
            n = Math.max(t1, t2);
            m = Math.pow(10, n);
            //return (arg1  * m - arg2 * m) / m;
            return ((arg1 * m - arg2 * m) / m).toFixed(n);
        }
    }

    function accAdd(arg1, arg2) {
        var t1 = 0, t2 = 0, m, n;
        try {
            t1 = arg1.toString().split(".")[1].length;
        } catch (e) {
            t1 = 0;
        }
        try {
            t2 = arg2.toString().split(".")[1].length;
        } catch (e) {
            t2 = 0;
        }
        with (Math) {
            //动态控制精度长度
            n = Math.max(t1, t2);
            m = Math.pow(10, n);
            //return (arg1  * m - arg2 * m) / m;
            return ((arg1 * m + arg2 * m) / m).toFixed(n);
        }
    }
	
	
</script>




</body>
</html>
