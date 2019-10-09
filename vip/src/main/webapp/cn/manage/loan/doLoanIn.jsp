<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>

<div class="modal fade" id="loanIn">
    <div class="modal-content">
        <div class="close hide" data-dismiss="modal">&times;</div>
        <div class="modal-header">
			<h3>${L:l(lan,'融资融币借入-申请借币侧滑上部-标题-1')}</h3>
			<p>${L:l(lan,'融资融币借入-申请借币侧滑上部-副标题-1')}</p>
		</div>
        <div class="modal-body">
            <div class="form-line">
                <label>${L:l(lan,'融资融币借入-申请借币侧滑中部-标签-1')}</label>
				<div aria-expanded="false">
					<ul class="loan-secd" style="margin-right:0;">
						<li class="dropdown">
							<a class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
								<div class="selcoin" id="selCoin">
									<i class="loan-coin loan-btc"></i>	
									<span>BTC<br/><b>Bitcoin</b></span>
								</div>
								<i class="iconfont2 ft14 right loan-down">&#xe600;</i>
							</a>
							<ul class="dropdown-menu animated bk-secd-menu fadeIn loan-dropdown-menu" id="loanDrop" data-animation="fadeIn" role="menu" style="left:auto;right:0">
								<c:forEach items="${userLoadMaps}" var="coin"  varStatus="varStatus" >
									<li>
										<a href="###" onClick="javascript:cointChange('${coin.key}',this);" data-canLoan="<fmt:formatNumber value="${(coin.value.canLoan-0.00049)<=0? 0 : (coin.value.canLoan-0.00049)}" pattern="0.000"/>">
											<i class="loan-coin loan-${coin.key}"></i>
											${coin.value.coint.propTag}<br/>
											<b>${coin.value.coint.propEnName}</b>
										</a>
									</li>
								</c:forEach>
							</ul>
						</li>
					</ul>
				</div>
			   <p class="loan-rate" id="msg">${L:l(lan,'融资融币借入-申请借币侧滑中部-标签-2')}${btcCoinInfo.rate}%</p>
               <input type="hidden" id="coint" name="coint" value="btc"/>				
               <input type="hidden" id="index" name="index" value="0"/>
               <input type="hidden" id="confirm" name="confirm" value="0"/>
            </div>
            <div class="form-line">
                <label>${L:l(lan,'融资融币借入-申请借币侧滑中部-标签-3')}</label>
				<fmt:formatNumber type="number" value="${(btcAssetsInfo.canLoan-0.00049)<=0? 0 : (btcAssetsInfo.canLoan-0.00049)}" pattern="0.###" var="btcCanLoan" />
                <input type="text" class="form-control" id="amount" name="amount" onkeyup="xswCheck(this , 3)" placeholder="${L:l2(lan,'当前最高可借',btcCanLoan,'BTC')}" autocomplete="off">
				<span class="lo-unit" id="lo-unit">BTC</span>
            </div>
            <p class="tongyi">
                <input class="text-bottom" type="checkbox" checked id="shengming" name="shengming">${L:l(lan,'融资融币借入-申请借币侧滑中部-标签-5')}</a>
            </p>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-blue2" onclick="ok();">${L:l(lan,'融资融币借入-申请借币侧滑底部-按钮-1')}</button>
            <button type="button" class="btn btn-gray2" data-dismiss="modal">${L:l(lan,'融资融币借入-申请借币侧滑底部-按钮-2')}</button>
        </div>
    </div>
</div>

 <script type="text/javascript">
	$(function(){
		$('#coint').val('btc');
	})
	function ok() {
	if(!validateForm()){
		return;
	}
	var index = $("#index").val();
	var actionUrl = "/manage/loan/doLoan";
	vip.ajax( {
		formId : "loanIn",
		url : actionUrl,
		div : "loanIn",
		dataType : "json",
		suc : function(json) {
			JuaBox.sure(json.des, {
				closeFun:function(){
					window.top.location.reload();
				}
			});
		},
		err : function(json){
			if(json.datas == "needConfirm"){//需要二次确认
				JuaBox.info(json.des,{
					btnFun1 : function() {
						JuaBox.closeAll(function(){
							$("#confirm").val("1");//设置确认值
							ok();
						});
					}
				});
			}else{
				JuaBox.sure(json.des);
			}
		}
	});
}

/**
 * 提交参数验证
 */
function validateForm(){
	var coint =	$("#coint").val();
	var amount = $("#amount").val();
	//var shengming = $("#shengming").attr("checked");
	var hasChk = $('#shengming').is(':checked');
	if(coint==""){
		JuaBox.sure("请选择借入币种。");
		return false;
	}
	//console.log(typeof amount);
	if(amount=="" ){
		JuaBox.sure("${L:l(lan,'请输入借入数量')}");
		return false;
	}else if(!$.isNumeric(amount)){
		JuaBox.sure("${L:l(lan,'请输入借入数量')}");
		return false;
	}
	
	if(!hasChk){
		JuaBox.sure("${L:l(lan,'请阅读并同意《融资融币交易风险申明》')}。");
		return false;
	}
	return true;
}

function cointChange(coint,obj){
	$('#selCoin').html($(obj).html());
	$('#coint').val(coint);
	$('#lo-unit').html(coint.toUpperCase());
	$("#amount").attr("placeholder",bitbank.L("当前最高可借",$(obj).data("canloan"),coint.toUpperCase()));
	var actionUrl = "/manage/loan/getDefaultValue?coint="+coint;

	vip.ajax( {
		url : actionUrl,
		dataType : "json",
		suc : function(json) {
			if(json.datas!="0"){
				$("#msg").html("${L:l(lan,'每日费率')}${L:l(lan,'：')}"+json.datas+"%");
			}else{
				$("#msg").html("");
			}
		},
		err : function(json){
			JuaBox.sure(json.des);
		}
	});
}
   function xswCheck(obj , max){//小数位验证
		var m = max + 1;
		var iv = $(obj).val();
		if(iv.indexOf(".") > 0 && iv.substring(iv.indexOf(".")).length > m){
				$(obj).val(iv.substring(0 , iv.indexOf(".") + m));
		}
	}

</script>