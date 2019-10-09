<%@ page session="false" language="java" import="java.util.*"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>

<div class="modal fade" id="loanOut">
    <form action="">
	<input type="text" class="hid-input"/>
	<input type="password" class="hid-input"/>
    <div class="modal-content">
        <div class="close hide" data-dismiss="modal">&times;</div>
        <div class="modal-header">
            <h3>${L:l(lan,'融资融币借出-申请投资侧滑上部-标题-1')}</h3>
			<p>${L:l(lan,'融资融币借出-申请投资侧滑上部-副标题-1')}</p>
        </div>
        <div class="modal-body">
            <div class="form-line">
                <label>${L:l(lan,'融资融币借出-申请投资侧滑中部-标签-1')}</label>
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
							<ul class="dropdown-menu animated bk-secd-menu fadeIn loan-dropdown-menu" id="loanDropMod" data-animation="fadeIn" role="menu" style="left:auto;right:0">
								<c:if test="${userLoadMaps!=null}">
									<c:forEach items="${userLoadMaps}" var="coin"  varStatus="varStatus" >
										<li>
											<a href="###" onClick="javascript:cointChange('${coin.key}',this);" data-fullname="${coin.value.coint.propEnName}" data-netAssets="${coin.value.surplusLoan}" data-rate="${coin.value.rate}">
												<i class="loan-coin loan-${coin.key}"></i>
												${coin.value.coint.propTag}<br/>
												<b>${coin.value.coint.propEnName}</b>
											</a>
										</li>
									</c:forEach>
								</c:if>
							</ul>
						</li>
					</ul>
				</div>
				<p class="loan-rate" id="msg">${L:l(lan,'融资融币借出-申请投资侧滑中部-标签-2')}${L:l(lan,'：')}${btcCoinInfo.rate}%</p>
                <input type="hidden" id="coint" name="coint" value="btc"/>
                <input type="hidden" id="rate" name="rate" value="${btcCoinInfo.rate}"/>
                <input type="hidden" id="rateForm" name="rateForm" value="1"/>  
				<input type="hidden" name="risk" value="1" />	
            </div>
            <div class="form-line">
                <label id="showName">${L:l(lan,'融资融币借出-申请投资侧滑中部-标签-3')}</label>
                <input type="text" class="form-control" onkeyup="xswCheck(this , 2)" placeholder="${L:l2(lan,'融资融币借出-申请投资侧滑中部-水印-1',btcCoinInfo.surplusLoan,'BTC')}" id="amount" name="amount" autocomplete="off" mytitle="${L:l(lan,'请填写您要投资的资金额度') }" />
                <span class="lo-unit" id="lo-unit">BTC</span>
            </div>
			
            <div class="form-line">
                <label>${L:l(lan,'融资融币借出-申请投资侧滑中部-标签-5')}${L:l(lan,'：')}</label>
                <input class="form-control" autocomplete="off" id="password" name="password" type="password" pattern="limit(4,18)" errormsg="${L:l(lan,'请输入合法的资金安全密码') }" mytitle="${L:l(lan,'请输入您的资金安全密码') }">
                <p class="company"><a href="/ac/safepwd_find" target="_blank">${L:l(lan,'融资融币借出-申请投资侧滑中部-标签-6')}</a>
                </p>
            </div>
            <p class="tongyi">
                <input class="text-bottom" type="checkbox" checked id="shengming" name="shengming">${L:l(lan,'融资融币借出-申请投资侧滑中部-标签-7')}</a>
            </p>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-blue2" onclick="ok();">${L:l(lan,'融资融币借出-申请投资侧滑底部-按钮-1')}</button>
            <button type="button" class="btn btn-gray2" data-dismiss="modal">${L:l(lan,'融资融币借出-申请投资侧滑底部-按钮-2')}</button>
        </div>
    </div>
    </form>
</div>

<script type="text/javascript">
$(function(){
	$('#coint').val('btc');
	$('#rate').val(${btcCoinInfo.rate});
})
function ok() {
	if(!validateForm()){
		return;
	}
	var actionUrl = "/manage/loan/doOut";
	vip.ajax( {
		formId : "loanOut",
		url : actionUrl,
		div : "loanOut",
		dataType : "json",
		suc : function(json) {
			JuaBox.sure(json.des, {
				closeFun: function(){
					window.top.location.reload();
				}
			});
		},
		err : function(json){
			JuaBox.sure(json.des);
		}
	});
}

/**
 * 提交参数验证
 */
function validateForm(){
	var password =	$("#password").val();
	var amount = $("#amount").val();
	//var shengming = $("#shengming").attr("checked");
	var hasChk = $('#shengming').is(':checked');
	if(amount=="" ){
		JuaBox.sure("${L:l(lan,'请输入投资币数量')}");
		return false;
	}else if(!$.isNumeric(amount)){
		JuaBox.sure("${L:l(lan,'请输入投资币数量')}");
		return false;
	}

	if(!password){
		JuaBox.sure("${L:l(lan,'请输入资金密码')}");
		return false;
	}
	
	if(!hasChk){
		JuaBox.sure("${L:l(lan,'请阅读并同意《融资融币交易风险申明》')}。");
		return false;
	}
	return true;
}

function  cointChange(coint,obj){
    var netAssets = $(obj).data("netassets");
    var coinUp = coint.toUpperCase();
	var selCon = "<i class='loan-coin loan-"+coint+"'></i><span>"+coint.toUpperCase()+"<br/><b>"+$(obj).data("fullname")+"</b></span>";
	$('#selCoin').html(selCon);
	$('#coint').val(coint);
    $('#lo-unit').html(coinUp);
    $('#amount').attr('placeholder',bitbank.L("融资融币借出-申请投资侧滑中部-水印-1",netAssets,coinUp));
	var actionUrl = "/manage/loan/getDefaultValue?coint="+coint;
	vip.ajax( {
		url : actionUrl,
		dataType : "json",
		suc : function(json) {
			if(json.datas!="0"){
				$("#msg").html("${L:l(lan,'每日费率')}${L:l(lan,'：')}"+json.datas+"%");
				$('#rate').val(json.datas);
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