<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>

<!doctype html>
<html>
<head>
<title>${L:l(lan,'行情图表-比特全球兑换中心-用心服务每一刻')}</title>
<jsp:include page="/common/head.jsp" />
<link rel="stylesheet" href="${static_domain }/statics/css/web.trans.css?V${CH_VERSON }">
<style type="text/css">
  body {overflow-x: hidden; overflow-y: hidden; background-color:#1F1D1D !important;}
</style>
<style type="text/css">
/*字体引入*/
@font-face {font-family: "iconfont";
  src: url('/common/fonts/iconfont.eot?v=${CH_VERSON }'); /* IE9*/
  src: url('/common/fonts/iconfont.eot?#iefix&v=${CH_VERSON }') format('embedded-opentype'), /* IE6-IE8 */
  url('/common/fonts/iconfont.woff?v=${CH_VERSON }') format('woff'), /* chrome, firefox */
  url('/common/fonts/iconfont.ttf?v=${CH_VERSON }') format('truetype'), /* chrome, firefox, opera, Safari, Android, iOS 4.2+*/
  url('/common/fonts/iconfont.svg?v=${CH_VERSON }#iconfont') format('svg'); /* iOS 4.1- */
}
@font-face{font-family:'FontAwesome';
	src:url('/common/fonts/fontawesome-webfont.eot?v=4.3.0');
	src:url('/common/fonts/fontawesome-webfont.eot?#iefix&v=4.3.0') format('embedded-opentype'),
	   url('/common/fonts/fontawesome-webfont.woff2?v=4.3.0') format('woff2'),
	   url('/common/fonts/fontawesome-webfont.woff?v=4.3.0') format('woff'),
	   url('/common/fonts/fontawesome-webfont.ttf?v=4.3.0') format('truetype'),
	   url('/common/fonts/fontawesome-webfont.svg?v=4.3.0#fontawesomeregular') format('svg');
	   font-weight:normal;font-style:normal;
}
@font-face{font-family:'Glyphicons Halflings';
    src:url('/common/fonts/glyphicons-halflings-regular.eot');
	src:url('/common/fonts/glyphicons-halflings-regular.eot?#iefix') format('embedded-opentype'),
	    url('/common/fonts/glyphicons-halflings-regular.woff2') format('woff2'),
		url('/common/fonts/glyphicons-halflings-regular.woff') format('woff'),
		url('/common/fonts/glyphicons-halflings-regular.ttf') format('truetype'),
		url('/common/fonts/glyphicons-halflings-regular.svg#glyphicons_halflingsregular') format('svg');
}
</style>

<script type="text/javascript">
	var marketData = ${marketData };
</script>
</head>
<body>
<!--[if lt IE 9]>
  <div class="alert alert-warning text-left" role="alert" style="margin-bottom:0; border-radius:0;position:relative;z-index:999999;">
    <p><i class="fa fa-exclamation-triangle fa-fw"></i>你的浏览器不支持${WEB_NAME }的一些新特性，请升级或更换你的浏览器至以下新版本。</p>
    <p>
      <a class='alert-link' href="http://rj.baidu.com/soft/detail/14744.html" target="_blank">(1)、谷歌浏览器(百度下载)</a><br>
      <a class='alert-link' href="https://www.google.com/intl/zh-CN/chrome/browser/" target="_blank">(2)、谷歌浏览器(官方下载)</a><br>
      <a class='alert-link' href="http://www.firefox.com.cn/download/" target="_blank">(3)、火狐浏览器</a><br>
      <a class='alert-link' href="http://chrome.360.cn/" target="_blank">(4)、360极速浏览器</a>
    </p>
    <p>HTML5时代已经来临，是时候换个浏览器了...</p>
  </div>
<![endif]-->
<div class="bk-body">
  <!--页面中部内容开始-->
   <div class="bk-kHeader clearfix">
     <div class="bk-kLogo pull-left">
    <%--  <a href="${main_domain }" title="${lan eq 'cn'?'比特全球':''}BITGLOBAL.COM">
       <img src="${static_domain }/statics/img/v2/common/chbtc_logo_k.png?V${CH_VERSON }" alt="${L:l(lan,'比特全球')}">
     </a> --%>
     </div>
     <div class="bk-kType pull-left">
     	<c:forEach items="${markets}" var ="market">
       	 <a href="${vip_domain }/markets/${market.key}" target="_self" class="<c:if test="${currMarket eq market.key }">active</c:if>">
     	 <img src="${static_domain }/statics/img/common/${market.value.numberBi.toLowerCase()}.png">${market.value.numberBi}/${market.value.exchangeBi} ${L:l(lan,'行情')}</a>
     	</c:forEach>
     </div>
     <div class="bk-kTool pull-right">
       <a onclick="toggleFoot()" roll="button" title="${L:l(lan,'切换底部')}" class="toggle-foot"></a>
       <a onclick="toggleRight()" roll="button" title="${L:l(lan,'切换右侧')}" class="toggle-right"></a>
     </div>
     <div class="bk-kUser pull-right">
       <div class="nologin">
         <a href="${vip_domain }/login" target="_self">${L:l(lan,'登录')}</a> | 
         <a href="${vip_domain }/register" target="_self">${L:l(lan,'注册')}</a> | 
         <a href="${vip_domain }/trade/${market }" target="_self" title="${L:l(lan,'返回专业版')}">${L:l(lan,'返回专业版')}</a>
       </div>
       <div class="logined" style="display: none;">
         <a target="_self" href="${vip_domain }/manage">
                                ${L:l(lan,'您好')}，<span id="M_userName"></span>&nbsp;&nbsp;<i id="M_userLevelIco"></i>
         </a> | 
         <a href="${vip_domain }/login/logout" target="_self" title="${L:l(lan,'退出登录')}">${L:l(lan,'退出登录')}</a> | 
         <a href="${vip_domain }/trade/${market }" target="_self" title="${L:l(lan,'返回专业版')}">${L:l(lan,'返回专业版')}</a>
       </div>
     </div>
   </div>
   <iframe src="${vip_domain }/markets/kline?symbol=${coinType_s }_${moneyType_s!='cny'?moneyType_s:''}" id="marketFrame" class="marketFrame" name="marketFrame" onload="" frameborder="0" width="100%" style="height:500px" hspace="0" scrolling="no"></iframe>
   <div class="bk-kMarket" style="height:215px;">
     <div class="bk-trans-form">
			  <div class="bk-table">
			    <div class="bk-cell list">
			      <div class="bk-tabList" id="bkEntrustTab">
						    <div class="bk-tabList-hd clearfix">
						        <div class="btn-group bk-btn-group" role="group">
						            <a class="btn active" role="button">${L:l(lan,'限价委托')}<span style="display: none;">(<span id="limitEntrustNum">0</span>)</span></a>
						            <a class="btn" role="button">${L:l(lan,'计划委托')}<span style="display: none;">(<span id="readyEntrustNum">0</span>)</span></a>
						            <a class="btn " role="button">${L:l(lan,'历史委托')}</a>
						        </div>
						        <a class="pull-right" href="${main_domain }/entrust/${market }" target="_blank" role="button">${L:l(lan,'更多记录')}>></a>
						    </div>
						    <div class="bk-tabList-bd">
						        <div class="bk-entrust">
									    <table class="table table-striped table-bordered table-hover">
									      <thead>
									        <tr>
									          <th style="">${L:l(lan,'委托时间')}</th>
									          <th style="text-align:left;">${L:l(lan,'委托数量/已成交')}(${coinType })</th>
									          <th style="text-align:left;">${L:l(lan,'委托价格/成交均价')}(${moneyType })</th>
									          <th style="">${L:l(lan,'成交总额')}(${moneyType })</th>
									          <th style="">${L:l(lan,'状态')}</th>
									          <th style="">${L:l(lan,'订单来源')}</th>
									          <th style="width:115px;">${L:l(lan,'操作')}<a role="button" id="batchCancel">[${L:l(lan,'批量撤单')}]</a></th>
									        </tr>
									      </thead>
									      <tbody id="entrustRecord"></tbody>
							        </table>
									  </div>
									  <div class="bk-entrust">
									    <table class="table table-striped table-bordered table-hover">
									    	<thead>
									        <tr>
									          <th style="">${L:l(lan,'委托时间')}</th>
									          <th style="text-align:left;">${L:l(lan,'委托量')}</th>
									          <th style="text-align:left;">${L:l(lan,'触发价格')}(${moneyType })</th>
									          <th style="text-align:left;">${L:l(lan,'委托价格')}(${moneyType })</th>
									          <th style="">${L:l(lan,'状态')}</th>
									          <th style="">${L:l(lan,'订单来源')}</th>
									          <th style="width:115px;">${L:l(lan,'操作')}<a role="button" data-plantype="true" id="batchCancelPlan">[${L:l(lan,'全部撤单')}]</a></th>
									        </tr>
									      </thead>
									      <tbody id="readyRecord"></tbody>
							        </table>
									  </div>
									  <div class="bk-entrust">
									    <table class="table table-striped table-bordered table-hover">
									      <thead>
									        <tr>
									          <th style="">${L:l(lan,'委托时间')}</th>
									          <th style="text-align:left;">${L:l(lan,'委托数量/已成交')}(${coinType })</th>
									          <th style="text-align:left;">${L:l(lan,'委托价格/成交均价')}(${moneyType })</th>
									          <th style="">${L:l(lan,'成交总额')}(${moneyType })</th>
									          <c:if test="${feeRate != null && feeRate>0}">
									          <th style="">${L:l(lan,'交易手续费')}</th>
									          </c:if>
									          <th style="">${L:l(lan,'状态')}</th>
									          <th style="">${L:l(lan,'订单来源')}</th>
									          <th style="width:115px;">${L:l(lan,'操作')}</th>
									        </tr>
									      </thead>
									      <tbody id="historyRecord"></tbody>
							        </table>
									  </div>
						    </div>
						</div>
						<div id="tradeList" style="display:none;">
						  <div class="bk-entrust" style="max-height:500px; min-height:150px;overflow-x:hidden;overflow-y:auto;">
						    <table class="table table-striped table-bordered table-hover">
						      <thead>
						        <tr>
						          <th>${L:l(lan,'成交时间')}</th>
						          <th style="text-align:left;">${L:l(lan,'成交数量')}(${coinType })</th>
						          <th style="text-align:left;">${L:l(lan,'成交价格')}(${moneyType })</th>
						          <th style="text-align:left;">${L:l(lan,'成交额')}(${moneyType })</th>
						          <%--  <c:if test="${feeRate != null && feeRate>0}">
							          <th style="text-align:left;">${L:l(lan,'交易手续费')}</th>
							       </c:if> --%>
						        </tr>
						      </thead>
						      <tbody id="tradeRecord"></tbody>
				        </table>
						  </div>
						</div>
					  <script type="text/x-tmpl" id="tmpl-entrustRecord">
            {% for (var i = 0; i < Math.min(5, rs.length); i++) { %}
  	        <tr>
  	          <td>{%=rs[i].submitTime%}</td>
  	          <td style="text-align:left;"><span class="label {%=rs[i].nameClass%}">{%=rs[i].nameType%}</span><span class="bkNum {%=rs[i].nameClass%}">{%=rs[i].numbers%}</span> / <span class="bkNum {%=rs[i].nameClass%}">{%=rs[i].completeNumber%}</span></td>
							<td style="text-align:left;">
                  <span class="{%=rs[i].nameClass%}">{%=rs[i].unitPrice%}</span> / 
                  <span class="{%=rs[i].nameClass%}">{%=rs[i].averagePrice%}</span>
              </td>
  	          <td>{%=rs[i].completeTotalMoney%}</td>
  	          <td>{%=rs[i].nameStatus%}</td>
 			  <td>{%=rs[i].source%}</td>
  	          <td><a class="cancelEntrust" role="button" data-id="{%=rs[i].entrustId%}" data-type="{%=rs[i].types%}">{%=rs[i].operat%}</a></td>
  	        </tr>
            {% } %}
      			</script>
      			
      			<script type="text/x-tmpl" id="tmpl-readyRecord">
            {% for (var i = 0; i < Math.min(5, rs.length); i++) { %}
  	        <tr>
  	          <td>{%=rs[i].submitTime%}</td>
  	          <td style="text-align:left;">
                  <span class="label {%=rs[i].nameClass%}">{%=rs[i].nameType%}</span>
                  <span class="ft14 {%=rs[i].nameClass%}">{% if(rs[i].types==1){ %} {%=rs[i].totalMoney%} ${moneyType } {% }else{ %} {%=rs[i].numbers%} ${coinType } {% } %}</span>
              </td>
  	          <td style="text-align:left;">
                  <span class="{%=rs[i].nameClass%}">{% if(rs[i].types==1){ %} ${L:l(lan,'追高触发价')} ：{%=rs[i].triggerPrice%}  {% }else{ %} ${L:l(lan,'止盈触发价')}：{%=rs[i].triggerPriceProfit%} {% } %} </span><br/> 
                  <span class="{%=rs[i].nameClass%}">{% if(rs[i].types==1){ %} ${L:l(lan,'抄底触发价')} ：{%=rs[i].triggerPriceProfit%} {% }else{ %} ${L:l(lan,'止损触发价')}：{%=rs[i].triggerPrice%} {% } %} </span>
              </td>
  	          <td style="text-align:left;">
                  <span class="{%=rs[i].nameClass%}">{% if(rs[i].types==1){ %} ${L:l(lan,'追高委托价')} ：{%=rs[i].unitPrice%} {% }else{ %} ${L:l(lan,'止盈委托价')}：{%=rs[i].unitPriceProfit%} {% } %}  </span><br/>
                  <span class="{%=rs[i].nameClass%}">{% if(rs[i].types==1){ %} ${L:l(lan,'抄底委托价')} ：{%=rs[i].unitPriceProfit%} {% }else{ %} ${L:l(lan,'止损委托价')}：{%=rs[i].unitPrice%} {% } %} </span>
              </td>
  	          <td>{%=rs[i].nameStatus%}</td>
			  <td>{%=rs[i].source%}</td>
  	          <td>
                 <a class="cancelEntrust" role="button" data-id="{%=rs[i].entrustId%}" data-plantype="{%=rs[i].plantype%}" data-type="{%=rs[i].types%}">{%=rs[i].operat%}</a>
				{% if(rs[i].nameStatus=='已委托'){ %}				
 					<a class="detailEntrust" role="button" data-id="{%=rs[i].formalEntrustId%}"> ${L:l(lan,'明细')} </a>
				{% }%}
              </td>
  	        </tr>
            {% } %}
      </script>
      			
      			
      			<script type="text/x-tmpl" id="tmpl-historyRecord">
            {% for (var i = 0; i < Math.min(5, rs.length); i++) { %}
  	        <tr>
  	          <td>{%=rs[i].submitTime%}</td>
  	          <td style="text-align:left;">
                 <span class="label {%=rs[i].nameClass%}">{%=rs[i].nameType%}</span>
                 <span class="bkNum {%=rs[i].nameClass%}">{%=rs[i].numbers%}</span> / 
                 <span class="bkNum {%=rs[i].nameClass%}">{%=rs[i].completeNumber%}</span>
              </td>
  	          <td style="text-align:left;">
                  <span class="{%=rs[i].nameClass%}">{%=rs[i].unitPrice%}</span> / 
                  <span class="{%=rs[i].nameClass%}">{%=rs[i].averagePrice%}</span>
              </td>
  	          <td style="text-align:left;">{%=rs[i].completeTotalMoney%}</td>
			<c:if test="${feeRate != null && feeRate>0 }">
			  <td style="text-align:left;">{%=rs[i].tradeFee%} </td>
			</c:if>
  	          <td style="text-align:left;">{%=rs[i].nameStatus%}</td>
			   <td style="text-align:left;">{%=rs[i].source%}</td>
  	          <td><a class="detailEntrust" role="button" data-id="{%=rs[i].entrustId%}">${L:l(lan,'明细')}</a></td>
  	        </tr>
            {% } %}
      </script>
      <script type="text/x-tmpl" id="tmpl-tradeRecord">
            {% for (var i = 0; i <= rs.length -1; i++) { %}
  	        <tr>
  	          <td>{%=rs[i].submitTime%}</td>
  	          <td style="text-align:left;">
                 <%--<span class="label {%=rs[i].nameClass%}">{%=rs[i].nameType%}</span>--%>
                 <span class="bkNum">{%=rs[i].numbers%}</span>
              </td>
              <td style="text-align:left;">{%=rs[i].unitPrice%}</td>
  	          <td>{%=rs[i].totalMoney%}</td>
			<%--<c:if test="${feeRate != null && feeRate>0 }">
			  <td>{%=rs[i].tradeFee%} </td>
			</c:if>--%>
  	        </tr>
            {% } %}
      </script>
      			
      			
      			
			    </div>
			  	<div class="bk-cell item">
			  		<div class="bk-buy-form">
			        <form role="form" id="buyForm" class="form-horizontal" method="post" action="" autocomplete="off">
			        	<input type="hidden" name="buyType" id="buyType" value="0">
			          <input type="hidden" name="moneyType" id="moneyType" value="${moneyType_s=='cny'? 0 : moneyType_s=='btc' ? 1 : moneyType_s=='ltc' ? 2 : moneyType_s=='eth' ? 3 : ''  }">
			          <input type="hidden" name="coinType" id="coinType" value="${coinType_s=='cny'? 0 : coinType_s=='btc' ? 1 : coinType_s=='ltc' ? 2 : coinType_s=='eth' ? 3 : ''  }">
			          <div class="form-group has-feedback" style="padding-top:8px;">
			            <label class="control-label" style="position:static;">${L:l(lan,'可用') }：<b id="canUseMoney" class="text-third">--</b> ${moneyType }</label>
			            <label class="control-label" style="position:static;">${L:l(lan,'可买') }：<b id="canBuyCoin" class="text-gray">--</b> ${coinType }</label>
			           <%--  <c:if test="${coinType != 'BTQ' }">
					  		  <label class="control-label" style="position:static;">${L:l(lan,'可借') }：<b id="canLoan${moneyType }" class="text-gray">--</b> ${moneyType } 
					  		  	<span title="一键杠杆" class="onoffswitch buy pull-right">
									    <input type="checkbox" name="leverSwitchBuy" class="onoffswitch-checkbox" id="leverSwitchBuy">
									    <label class="onoffswitch-label" for="leverSwitchBuy">
									        <span class="onoffswitch-inner"></span>
									        <span class="onoffswitch-switch"></span>
									    </label>
										</span>
					  		  </label>
					  		</c:if> --%>
			          </div>
			          <div id="buyDefaultForm">
				          <div class="form-group has-feedback">
				            <label class="control-label" for="buyUnitPrice"><span class="buyLabel">${L:l(lan,'限定最高')}</span>${L:l1(lan,'买入价%%',moneyType)}</label>
				            <div class="input-group">
				              <input type="text" class="form-control form-second" id="buyUnitPrice" name="buyUnitPrice" pattern="" errormsg="" aria-describedby="buyUnitPrice_error">
				            </div>
				          </div>
				          <div class="form-group has-feedback" style="display:none">
				            <label class="control-label" for="buyMinPrice">${L:l1(lan,'限定最低买入价%%',moneyType)}</label>
				            <div class="input-group">
				              <input type="text" class="form-control form-second" id="buyMinPrice" name="buyMinPrice" pattern="" errormsg="" aria-describedby="buyMinPrice_error">
				            </div>
				          </div>
				          <div class="form-group has-feedback">
				            <label class="control-label" for="buyNumber">${L:l1(lan,'买入量%%',coinType) }</label>
				            <div class="input-group">
				              <input type="text" class="form-control form-second" id="buyNumber" name="buyNumber" pattern="" errormsg="" aria-describedby="buyNumber_error">
				            </div>
				          </div>
				          
				          <div class="range range_buy hide" id="buySlider"><!-- 买单滑动杆 --></div>
				          
				          <div class="form-group has-feedback">
				            <label class="control-label" for="realBuyAccount" style="position:static;">${L:l(lan,'预计交易额')}：<b class="text-third" id="realBuyAccount">0.00</b> ${moneyType }</label>
				            <%-- <c:if test="${coinType != 'BTQ' }">
				            	<span role="button" class="pull-right mr5 bluepopover" style="margin-top:2px; border-bottom:1px dashed #ddd;">${L:l(lan,'使用规则')}
												<div class="popover fade top in" role="tooltip" style="left:auto;right:-5px; ${lan eq 'en'?'top:-140px':' top:-75px' }">
					                <div class="arrow" style="left:auto;right:15px;"></div>
					                <div class="popover-content">
					                	<p>1、最大借入额度为个人净资产的2倍；<br/>2、每天收取借入金额0.1%的手续费；<br/>3、杠杆交易存在平仓风险，请合理使用。</p>
					                </div>
				                </div>
											</span>
				            	<label class="control-label" id="leverAccountBuyLabel" for="leverAccountBuy" style="position:static;">${L:l(lan,'使用杠杆额度')}：<b class="text-primary" id="leverAccountBuy">0.00</b> ${moneyType }</label>
				            </c:if> --%>
				          </div>
				          <div class="form-group">
				            <button id="buyBtn" type="button" data-loading-text="Loading..." class="btn btn-third btn-block btn-hg">
				            	<i class="bk-ico buycart"></i>${L:l(lan,'立即买入')}
				            </button>
				          </div>
			          </div>
			        </form>
            </div>
			  	</div>
			  	<div class="bk-cell item">
			  		<div class="bk-sell-form">
			        <form role="form" id="sellForm" class="form-horizontal" method="post" action="" autocomplete="off">
			          <div class="form-group has-feedback" style="padding-top:8px;">
			            <label class="control-label" style="position:static;">${L:l(lan,'可用') }：<b id="canUseCoin" class="text-primary">--</b> ${coinType }</label>
			            <label class="control-label" style="position:static;">${L:l(lan,'可卖') }：<b id="canSellMoney" class="text-gray">--</b> ${moneyType }</label>
			            <%-- <c:if test="${coinType != 'BTQ'}">
					  		  <label class="control-label" style="position:static; ${coinType == 'DAO'?'visibility: hidden;':'' }">${L:l(lan,'可借') }：<b id="canLoan${coinType }" class="text-gray">--</b> ${coinType } 
					  		  	<span title="一键杠杆" class="onoffswitch sell pull-right">
									    <input type="checkbox" name="leverSwitchSell" class="onoffswitch-checkbox" id="leverSwitchSell">
									    <label class="onoffswitch-label" for="leverSwitchSell">
									        <span class="onoffswitch-inner"></span>
									        <span class="onoffswitch-switch"></span>
									    </label>
										</span>
					  		  </label>
					  		</c:if> --%>
			          </div>
			          <div id="sellDefaultForm">      
				          <div class="form-group has-feedback">
				            <label class="control-label" for="sellUnitPrice"><span class="sellLabel">${L:l(lan,'限定最低')}</span>${L:l1(lan,'卖出价%%',moneyType)}</label>
				            <div class="input-group">
				              <input type="text" class="form-control form-second" id="sellUnitPrice" name="sellUnitPrice" pattern="" errormsg="" aria-describedby="sellUnitPrice_error">
				            </div>
				          </div>
				          <div class="form-group has-feedback" style="display:none">
				            <label class="control-label" for="sellMaxPrice">${L:l1(lan,'限定最高卖出价%%',moneyType) }</label>
				            <div class="input-group">
				              <input type="text" class="form-control form-second" id="sellMaxPrice" name="sellMaxPrice" pattern="" errormsg="" aria-describedby="sellMaxPrice_error">
				            </div>
				          </div>
				          <div class="form-group has-feedback">
				            <label class="control-label" for="sellNumber">${L:l1(lan,'卖出量%%',coinType) }</label>
				            <div class="input-group">
				              <input type="text" class="form-control form-second" id="sellNumber" name="sellNumber" pattern="" errormsg="" aria-describedby="sellNumber_error">
				            </div>
				          </div>
				          
				          <div class="range range_sell hide" id="sellSlider"><!-- 卖单滑动杆 --></div>
				          
				          <div class="form-group has-feedback">
				            <label class="control-label" for="realSellAccount" style="position:static;">${L:l(lan,'预计交易额')}：<b class="text-primary" id="realSellAccount">0.00</b> ${moneyType }</label>
				          <%--   <c:if test="${coinType != 'BTQ' }">
				            	<span role="button" class="pull-right mr5 bluepopover" style="margin-top:2px; border-bottom:1px dashed #ddd;">${L:l(lan,'使用规则')}
												<div class="popover fade top in" role="tooltip" style="left:auto;right:-5px; ${lan eq 'en'?'top:-140px':' top:-75px' }">
					                <div class="arrow" style="left:auto;right:15px;"></div>
					                <div class="popover-content">
					                	<p>1、最大借入额度为个人净资产的2倍；<br/>2、每天收取借入金额0.1%的手续费；<br/>3、杠杆交易存在平仓风险，请合理使用。</p>
					                </div>
				                </div>
											</span>
				            	<label class="control-label" id="leverAccountSellLabel" for="leverAccountSell" style="position:static;">${L:l(lan,'使用杠杆额度')}：<b class="text-second" id="leverAccountSell">0.00</b> ${coinType }</label>
				            </c:if> --%>
				          </div>
				          <div class="form-group">
				            <button id="sellBtn" type="button" data-reset-text="<i class='bk-ico sellhand'></i>${L:l(lan,'立即卖出')}" data-loading-text="Loading..." class="btn btn-primary btn-block btn-hg">
				            	<i class="bk-ico sellhand"></i>${L:l(lan,'立即卖出')}
				           	</button>
				          </div>
			          </div>
			        </form>
            </div>
			  	</div>
			  </div>
		</div>
   </div>
  <!--页面中部内容结束-->
  <script type="text/javascript">
			$(".bk-tabList").slide({
			    mainCell: ".bk-tabList-bd",
			    titCell: ".btn-group .btn",
			    effect: "fade",
			    trigger: "click",
			    titOnClassName: "active"
			});
			
			function toggleFoot(){
				$(".bk-kMarket").toggle();
				var sHeight = $(window).height() - $(".bk-kHeader").height() - $(".bk-kMarket:visible").height() - 10;
				$("#marketFrame").css("height",sHeight);
				$(".toggle-foot").toggleClass("active");
			}
			
			function toggleRight(){
				$(window.frames['marketFrame'].document.body).find("#trade_container").toggle();
				$(window.frames['marketFrame'])[0].on_size();
				$(".toggle-right").toggleClass("active");
			}
	
		  $("#h_import").remove();
		  
		  $(function(){
			  var sHeight = $(window).height() - $(".bk-kHeader").height() - $(".bk-kMarket:visible").height() - 10;
			  $("#marketFrame").css("height",sHeight);
		  });  
		  
		  $(window).resize(function() {
			  var sHeight = $(window).height() - $(".bk-kHeader").height() - $(".bk-kMarket:visible").height() - 10;
			  $("#marketFrame").css("height",sHeight);
			});
		 $(document).ready(function() { //add by zhanglinbo 20160808
			  require(["module_trans","module_market","module_asset","module_common"],function(trans,market,asset){
				  trans.pageIndexInit("${coinType_s }_${moneyType_s!='cny'?moneyType_s:''}",2000);
				  market.init();
				  asset.init();
			  });
		 });
  </script>
</div>
</body>
</html>