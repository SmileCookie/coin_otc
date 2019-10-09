<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<!doctype html>
<html>
<head>
</head>
<body>
	<form id="investForm" class="">
		<div class='bk-tabList mt0 mb0'>
			<div class="bk-tabList-hd clearfix">
        <div class="btn-group bk-btn-group" role="group">
	        <a class="btn active ft18" role="button">${L:l(lan,'申请放贷')}</a>
	      </div>
      </div>
			<div class='bk-tabList-bd clearfix text-left' style='padding:20px 20px 0px 20px; font-size:14px; line-height:1;'>
				<div class="form-group">
					<h3 class="mb10">1、${L:l(lan,'投资币种')}</h3>
					<!-- <label class="checkbox-inline">
					  <input type="checkbox" id="loanCNY" name="loanCoinCNY" value="人民币" checked> CNY
					</label> -->
					<c:forEach items="${coinMap}" var="coin">
						<label class="checkbox-inline">
						  <input type="checkbox" id="loan${coin.value.propTag }" name="loanCoin${coin.value.propTag }" value="${coin.value.propTag }"> ${coin.value.propTag }
						</label>
					</c:forEach>
					
				<!-- 	<label class="checkbox-inline">
					  <input type="checkbox" id="loanETH" name="loanCoinETH" value="ETH"> ETH
					</label>
					<label class="checkbox-inline">
					  <input type="checkbox" id="loanETC" name="loanCoinETC" value="ETC"> ETC
					</label>
					<label class="checkbox-inline">
					  <input type="checkbox" id="loanLTC" name="loanCoinLTC" value="LTC"> LTC
					</label> -->
			  </div>
			  <div class="form-group">
					<h3 class="mb10">2、${L:l(lan,'投资金额')} </h3>
					<label class="radio-inline">
					  <input type="radio" name="loanAmount" value="${L:l(lan,'50-100')}" checked> ${L:l(lan,'50-100')}
					</label>
					<label class="radio-inline">
					  <input type="radio" name="loanAmount" value="${L:l(lan,'100-500')}"> ${L:l(lan,'100-500')}
					</label>
					<label class="radio-inline">
					  <input type="radio" name="loanAmount" value="${L:l(lan,'500以上')}"> ${L:l(lan,'500以上')}
					</label>
			  </div>
			  <div class="form-group">
					<h3 class="mb10">3、${L:l(lan,'投资周期')}</h3>
					<label class="radio-inline">
					  <input type="radio" name="loanPeriod" value="${L:l(lan,'6-12个月')}"> ${L:l(lan,'6-12个月')}
					</label>
					<label class="radio-inline">
					  <input type="radio" name="loanPeriod" value="${L:l(lan,'12个月以上')}" checked> ${L:l(lan,'12个月以上')}
					</label>
			  </div>
			  <div class="form-group">
					<h3 class="mb10">4、${L:l(lan,'预期放贷年化利率')}</h3>
<!-- 					<label class="radio-inline"> -->
<!-- 					  <input type="radio" name="loanRate" value="10%-15%" checked> 10%-15% -->
<!-- 					</label> -->
<!-- 					<label class="radio-inline"> -->
<!-- 					  <input type="radio" name="loanRate" value="15%-20%"> 15%-20% -->
<!-- 					</label> -->
					<label class="radio-inline">
					  <input type="radio" name="loanRate" value="30%以上" checked> ${L:l(lan,'30%以上')}
					</label>
			  </div>
			  <div class="form-group">
					<h3 class="mb10">5、${L:l(lan,'风险担保')}</h3>
					<label class="radio-inline">
					  <input type="radio" name="loanRisk" value="${L:l(lan,'平台保本(50%管理费)')}" checked> ${L:l(lan,'平台保本(50%管理费)')}
					</label>
					<label class="radio-inline">
					  <input type="radio" name="loanRisk" value="${L:l(lan,'自担风险(40%管理费)')}"> ${L:l(lan,'自担风险(40%管理费)')}
					</label>
			  </div>
			 <div class="form-group">
					<h3 class="mb10">6、${L:l(lan,'联系信息')}</h3>
			    <input type="text" class="form-control mb5" id="loanName" name="loanName" placeholder="${L:l(lan,'联系人')}">
			    <input type="text" class="form-control" id="loanPhone" name="loanPhone" placeholder="${L:l(lan,'联系电话')}">
					<p class="help-block text-muted ft12">${L:l(lan,'提示')}：${L:l(lan,'您的申请提交后，我们的专员会尽快与你联系，请保持电话畅通。')}</p>
			  </div>
			</div>
		</div>
	</form>
</body>
</html>