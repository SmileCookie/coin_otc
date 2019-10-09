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
<title>${L:l(lan,'交易总览')}-${WEB_NAME }-${WEB_TITLE }</title>
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<link rel="stylesheet" href="${static_domain }/statics/css/web.trans.css?V${CH_VERSON }">
</head>
<body class="">

<input type="hidden" id="market" name="market" value="${market}">
<div class="bk-body">
	<!-- Common TopMenu Begin -->
	<jsp:include page="/common/top.jsp" />
	<!-- Common TopMenu End -->
	<!-- Body From mainPage Begin -->
	<div class="mainer-phase2">
		<div class="container">
			<div class="trade-sum boxshadow" id="tradeSum">
				<div class="jili-top hd clearfix">
					<ul class="jili-nav">
							<li class="active"><a href="javascript:void(0);">${L:l(lan,'正在进行的委托')}</a></li>
							<li><a href="javascript:void(0);">${L:l(lan,'计划委托')}</a></li>
							<li><a href="javascript:void(0);">${L:l(lan,'最近委托')}</a></li>
							<li><a href="javascript:void(0);">${L:l(lan,'历史委托')}</a></li>
					</ul>
					<p>${L:l(lan,'交易市场')}${L:l(lan,'：')}
						<select class="modal-select" id="tradeMarketSel">
						</select>
					</p>
				</div>
				
				<div class="bd">
					<div class="lsju" id="enTrustOn">
						<div class="bk-entrust">
							<h2>${coinType}/${moneyType}</h2>
							<div class="table-box">
								<table class="table table-bordered table-hover text-center table-noline">
									<thead>
										<tr>
											<th>${L:l(lan,'类型')}</th>
											<th>${L:l(lan,'委托价格')}(${moneyType})</th>
											<th>${L:l(lan,'成交价格')}(${moneyType})</th>
											<th>${L:l(lan,'委托数量/成交数量')}(${coinType })</th>
											<th>${L:l(lan,'成交额')}(${moneyType})</th>
											<th>${L:l(lan,'日期')}</th>
											<th style="text-align:center">${L:l(lan,'操作')}<br /><a role="button" data-plantype="false" id="batchCancel">[${L:l(lan,'批量撤销')}]</a></th>
										</tr>
									</thead>
									<tbody id="entrustRecord">
									</tbody>
								</table>								
							</div>
						</div>
						<div class="bk-pageNav mb20" id="entrustRecord_Page"></div>
					</div>
					
					<div class="lsju">
						<div class="bk-entrust">
							<h2>${coinType}/${moneyType}</h2>
							<div class="table-box">
								<table class="table table-bordered table-hover text-center table-noline">
									<thead>
										<tr>
											<th>${L:l(lan,'类型')}</th>
											<th>${L:l(lan,'委托价格')}(${moneyType })</th>
											<th>${L:l(lan,'成交均价')}(${moneyType })</th>
											<th>${L:l(lan,'触发价格')}(${moneyType })</th>										
											<th>${L:l(lan,'委托数量/成交数量')}(${coinType })</th>
											<th>${L:l(lan,'成交额')}(${moneyType })</th>
											<th>${L:l(lan,'日期')}</th>
											<th>${L:l(lan,'操作')}<br/><a role="button" data-plantype="true" id="batchCancelPlan">[${L:l(lan,'全部撤销')}]</a></th>
										</tr>
									</thead>
									<tbody id="readyRecord">
									</tbody>
								</table>
							</div>
						</div>
						<div class="bk-pageNav mb20" id="readyRecord_Page"></div>
					</div>
					
					<div class="lsju">
						<div class="bk-entrust">
							<h2>${coinType}/${moneyType}</h2>
							<div class="table-box">
								<table class="table table-bordered table-hover text-center table-hei table-noline">
									<thead>
										<tr>
											<th>${L:l(lan,'类型')}</th>
											<th>${L:l(lan,'委托价格')}(${moneyType})</th>
											<th>${L:l(lan,'平均价格')}(${moneyType})</th>				
											<th>${L:l(lan,'委托数量/成交数量')}(${coinType})</th>				
											<th>${L:l(lan,'成交额')}(${moneyType })</th>
											<th>${L:l(lan,'状态')}</th>
											<th>${L:l(lan,'日期')}</th>
											<th>${L:l(lan,'订单来源')}</th>
											<th>${L:l(lan,'操作')}</th>
										</tr>
									</thead>
									<tbody id="historyRecord">
									</tbody>
								</table>
							</div>
						</div>
						<div class="bk-pageNav mb20" id="historyRecord_Page"></div>
					</div>
					<div class="lsju">
						<div class="bk-entrust">
							<h2>${coinType}/${moneyType}</h2>
							<div class="table-box">
								<table class="table table-bordered table-hover text-center table-hei table-noline">
									<thead>
										<tr>
											<th>${L:l(lan,'类型')}</th>
											<th>${L:l(lan,'委托价格')}(${moneyType})</th>
											<th>${L:l(lan,'平均价格')}(${moneyType})</th>				
											<th>${L:l(lan,'委托数量/成交数量')}(${coinType})</th>				
											<th>${L:l(lan,'成交额')}(${moneyType })</th>
											<th>${L:l(lan,'状态')}</th>
											<th>${L:l(lan,'日期')}</th>
											<th>${L:l(lan,'订单来源')}</th>
											<th>${L:l(lan,'操作')}</th>
										</tr>
									</thead>
									<tbody id="oldHistoryRecord">
									</tbody>
								</table>
							</div>
						</div>
						<div class="bk-pageNav mb20" id="oldHistoryRecord_Page"></div>
					</div>
				</div>
			</div>
				<!-- part4 -->
				 <div id="tradeList" style="display:none;">
					<div class="bk-entrust">
						<div class="bk-entrust-info">
							<table class="table table-striped table-bordered table-hover">
								<tbody id="tradeRecordInfo"></tbody>
							</table>
						</div>
						<table class="table table-striped table-bordered table-noline">
							<col width="25%"></col><col width="25%"></col><col width="25%"></col><col width="25%"></col>
							<thead>
							<tr>
								<th>${L:l(lan,'成交时间')}</th>
								<th style="text-align:right;">${L:l(lan,'成交数量')}(${coinType })</th>
								<th style="text-align:right;">${L:l(lan,'成交价格')}(${moneyType })</th>
								<th style="text-align:right;">${L:l(lan,'成交额')}(${moneyType })</th>
							</tr>
							</thead>
							
						</table>
						<div style="max-height:350px; min-height:50px;overflow-x:hidden;overflow-y:auto;">
							<table class="table table-striped table-bordered table-notop">
								<col width="25%"></col><col width="25%"></col><col width="25%"></col><col width="25%"></col>
								<tbody id="tradeRecord"></tbody>
							</table>
						</div>
					</div>
				</div>
				<!--part4 end   -->
		</div>	
	</div>
</div>

<script type="text/x-tmpl" id="tmpl-entrustRecord">
{% for (var i = 0; i <= rs.length -1; i++) { %}
	<tr>
		<td><span class="label {%=rs[i].nameClass%}">{%=rs[i].nameType%}</span></td>
		<td>{%=rs[i].unitPrice%}</td>
		<td>{%=rs[i].averagePrice%}</td>
		<td>{%=rs[i].numbers%} / {%=rs[i].completeNumber%}</td>
		<td>{%=rs[i].completeTotalMoney%}</td>
		<td>{%=rs[i].submitTime%}</td>
		<td>
			<a class="cancelEntrust" role="button" data-id="{%=rs[i].entrustId%}" data-plantype="{%=rs[i].plantype%}" data-type="{%=rs[i].types%}">{%=rs[i].operat%}</a>
		</td>
	</tr>
	{% } %}
</script>

<script type="text/x-tmpl" id="tmpl-historyRecord">
{% for (var i = 0; i <= rs.length -1; i++) { %}
<tr>
<td><span class="label {%=rs[i].nameClass%}">{%=rs[i].nameType%}</span></td>
<td><span class="">{%=rs[i].unitPrice%}</span></td>
<td><span class="">{%=rs[i].averagePrice%}</span></td>
<td>
	<span class="">{%=rs[i].numbers%}</span> / 
	<span class="">{%=rs[i].completeNumber%}</span>
</td>
<td>{%=rs[i].completeTotalMoney%}</td>
<td>{%=rs[i].nameStatus%}</td>
<td>{%=rs[i].submitTime%}</td>
<td>{%=rs[i].source%}</td>
<td>
	<a class="detailEntrust" role="button" data-id="{%=rs[i].entrustId%}" data-types="{%=rs[i].types%}"  data-numbers="{%=rs[i].numbers%}">${L:l(lan,'明细')}</a>
</td>
</tr>
{% } %}
</script>
<script type="text/x-tmpl" id="tmpl-oldHistoryRecord">
{% for (var i = 0; i <= rs.length -1; i++) { %}
<tr>
<td><span class="label {%=rs[i].nameClass%}">{%=rs[i].nameType%}</span></td>
<td><span class="">{%=rs[i].unitPrice%}</span></td>
<td><span class="">{%=rs[i].averagePrice%}</span></td>
<td>
	<span class="">{%=rs[i].numbers%}</span> / 
	<span class="">{%=rs[i].completeNumber%}</span>
</td>
<td>{%=rs[i].completeTotalMoney%}</td>
<td>{%=rs[i].nameStatus%}</td>
<td>{%=rs[i].submitTime%}</td>
<td>{%=rs[i].source%}</td>
<td>
	<a class="detailEntrust" role="button" data-id="{%=rs[i].entrustId%}" data-types="{%=rs[i].types%}" data-numbers="{%=rs[i].numbers%}">${L:l(lan,'明细')}</a>
</td>
</tr>
{% } %}
</script>
<script type="text/x-tmpl" id="tmpl-readyRecord">
			{% for (var i = 0; i <= rs.length -1; i++) { %}
			<tr>
				<td><span class="label {%=rs[i].nameClass%}">{%=rs[i].nameType%}</span></td>
				<td>{% if(rs[i].unitPrice!='--'){ %} {%=rs[i].unitPrice%}  {% }else{ %} {%=rs[i].unitPriceProfit%} {% } %}</td>
				<td><span class="">{%=rs[i].averagePrice%}</span></td>
				<td>{% if(rs[i].triggerPrice!='--'){ %} {%=rs[i].triggerPrice%}  {% }else{ %} {%=rs[i].triggerPriceProfit%} {% } %}</td>
				<td>
                  	<span>{% if(rs[i].numbers!="--"){ %} {%=rs[i].numbers%} {% }else{ %} {%=rs[i].stopAmount%} {% } %} </span> / 
					<span class="">{%=rs[i].completeNumber%}</span>
				</td>
				<td>{%=rs[i].totalMoney%}</td>
				<td>{%=rs[i].submitTime%}</td>				
				<td>
					<a class="cancelEntrust" role="button" data-id="{%=rs[i].entrustId%}" data-plantype="{%=rs[i].plantype%}" data-type="{%=rs[i].types%}">{%=rs[i].operat%}</a>
				</td>
			</tr>
			{% } %}
</script>

<script type="text/x-tmpl" id="tmpl-tradeRecord">
            {% for (var i = 0; i <= rs.length -1; i++) { %}
  	        <tr>
  	          <td style="text-align:left;">{%=rs[i].submitTime%}</td>
  	          <td style="text-align:right;"><span>{%=rs[i].numbers%}</span></td>
              <td style="text-align:right;">{%=rs[i].unitPrice%}</td>
  	          <td style="text-align:right;">{%=rs[i].totalMoney%}</td>
  	        </tr>
      {% } %}
</script>   
<script type="text/x-tmpl" id="tmpl-tradeRecordInfo">
	<tr>
		<td><span class="bk-entrust-info-type {%=rs.typesClass%}">{%=rs.types%}</span></td>
		<td>
			<h6>${L:l(lan,'成交均价')}(${moneyType})</h6>
			<p>{%=rs.allAvrPrice%}</p>
		</td>
		<td>
			<h6>${L:l(lan,'委托数量/成交数量')}(${coinType})</h6>
			<p>{%=rs.allNumber%}/{%=rs.allTotalCoin%}</p>
		</td>
		<td>
			<h6>${L:l(lan,'成交额')}(${moneyType})</h6>
			<p>{%=rs.allTotalMoney%}</p>
		</td>
	</tr>
</script>
<script type="text/javascript">
			$("#tradeSum").slide({
						titOnClassName:"active",
						effect: "fade",
						trigger:"click",
						startFun:function(i,c){	
						}
			 });
</script>   

      
	<!-- Body From mainPage End -->
	<!-- Common FootMain Begin -->
	<jsp:include page="/common/foot.jsp">
 		<jsp:param value="${market }" name="market"/>
 	</jsp:include>
	<!-- Common FootMain End -->
</div>

</body>
</html>
