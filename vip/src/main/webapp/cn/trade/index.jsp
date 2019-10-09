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
<title>${L:l(lan,'交易中心')}-${WEB_NAME }-${WEB_TITLE }</title>
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<link rel="stylesheet" href="${static_domain }/statics/css/web.trans.css?V${CH_VERSON }">
</head>
<body class="trade-page" data-symbol="${market}">
<div class="wrap">
    <jsp:include page="/common/top.jsp" />
    <div class="trade-page-wrap clearfix">
        <input type="hidden" value="${coinType}" id="hidden_cointype" />
        <input type="hidden" value="${moneyType}" id="hidden_moneytype" />
        <div class="col-m">
            <div class="m-wrap">
                <div class="trade-item kline-box">  
                    <div class="trade-page-title clearfix">
                        <div class="trade-symbol">
                            <b>${numberBiFullName} ${L:l(lan,'交易-货币数据-货币标题-1')}</b>
                            <em>${coinType}/${moneyType}</em>
                        </div>
                        <div class="trade-price ${lan == 'en'? 'small-select' :''}">
                            <div class="price-type">
                                <h5>
                                    <span>
                                        <c:if test="${moneyType =='USDT'}">
                                            ${L:l(lan,'USD')}
                                        </c:if>
                                        <c:if test="${moneyType =='BTC'}">
                                            ${L:l(lan,'BTC')}
                                        </c:if>
                                    </span><i></i>
                                </h5>
                                <ul id="basePriceType">
                                    <c:if test="${moneyType =='BTC'}">
                                        <li>${L:l(lan,'BTC')}</li>
                                    </c:if>
                                    <li>${L:l(lan,'USD')}</li>
                                    <li>${L:l(lan,'CNY')}</li>
                                    <li>${L:l(lan,'EUR')}</li>
                                    <li>${L:l(lan,'GBP')}</li>
                                    <li>${L:l(lan,'AUD')}</li>
                                </ul>
                            </div>
                            <ul class="price-ul clearfix">
                                <li>
                                    <h6>${L:l(lan,'交易-货币数据-数据项-1')}</h6>
                                    <p id="curPrice">--</p>
                                </li>
                                <!-- <li>
                                    <h6>${L:l(lan,'24H涨跌')}</h6>
                                    <p id="changeOfDay">--</p>
                                </li> -->
                                <li>
                                    <h6>${L:l(lan,'交易-货币数据-数据项-2')}</h6>
                                    <p id="maxPrice">--</p>
                                </li>
                                <li>
                                    <h6>${L:l(lan,'交易-货币数据-数据项-3')}</h6>
                                    <p id="minPrice">--</p>
                                </li>
                                <li>
                                    <h6>${L:l(lan,'交易-货币数据-数据项-4')}</h6>
                                    <p id="dayVolume">--</p>
                                    <div class="info">
                                        <p>${L:l(lan,'交易-货币数据-数据项鼠标提示-1')}</p>
                                        <p> <span id="dayVolumeInfo_a">-- ${coinType}</span> / <span id="dayVolumeInfo_b"> -- ${moneyType} </span></p>
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <div class="kline">
                        <div id="kline-wrap"></div>
                    </div>
                </div>
                <div class="order clearfix">
                    <div class="trade-item mine-orders" id="bkEntrustTab">
                        <div class="trade-title">
                            <div class="trade-title-right">
                                <span class="btn">${L:l(lan,'交易-委托列表上部-页签-1')}</span><span class="btn">${L:l(lan,'交易-委托列表上部-页签-2')}</span><span class="btn">${L:l(lan,'24小时内已成交')}</span><a href="/entrust/list" target="_blank">${L:l(lan,'历史委托')} ></a>
                            </div>
                            <h4>${L:l(lan,'交易-委托列表上部-标题-1')}<em></em><i class="open"></i></h4>
                        </div>
                        <div class="trade-content bk-entrust">
                            <div class="mine-orders-bd">
                                <table>
                                    <col width="11%"></col><col width="16%"></col><col width="16%"></col><col width="16%"></col><col width="16%"></col><col width="18%"></col><col width="7%"></col>
                                    <thead>
                                        <tr>
                                            <th>${L:l(lan,'交易-委托列表中部-限价委托表头-1')}</th>
                                            <th>${L:l(lan,'交易-委托列表中部-限价委托表头-2')}(${moneyType})</th>
                                            <th>${L:l(lan,'交易-委托列表中部-限价委托表头-3')}(${moneyType})</th>
                                            <th>${L:l(lan,'交易-委托列表中部-限价委托表头-4')}(${coinType})</th>
                                            <th>${L:l(lan,'交易-委托列表中部-限价委托表头-5')}(${moneyType })</th>
                                            <th>${L:l(lan,'交易-委托列表中部-限价委托表头-6')}</th>
                                            <th class="text-center">${L:l(lan,'交易-委托列表中部-限价委托表头-7')}</th>
                                        </tr>
                                    </thead>
                                </table>
                                <div class="bk-entrust-table">
                                    <table>
                                        <col width="11%"></col><col width="16%"></col><col width="16%"></col><col width="16%"></col><col width="16%"></col><col width="18%"></col><col width="7%"></col>
                                        <tbody id="entrustRecord"></tbody>
                                    </table>
                                </div>
                            </div>
                            <div class="mine-orders-bd">
                                <table>
                                    <col width="11%"></col><col width="14%"></col><col width="14%"></col><col width="14%"></col><col width="14%"></col><col width="17%"></col><col width="9%"></col><col width="7%"></col>
                                    <thead>
                                        <tr>
                                            <th>${L:l(lan,'交易-委托列表中部-计划委托表头-1')}</th>
                                            <th>${L:l(lan,'交易-委托列表中部-计划委托表头-2')}(${moneyType})</th>
                                            <th>${L:l(lan,'交易-委托列表中部-计划委托表头-3')}(${moneyType})</th>
                                            <th>${L:l(lan,'交易-委托列表中部-计划委托表头-4')}(${coinType})</th>
                                            <th>${L:l(lan,'交易-委托列表中部-计划委托表头-5')}(${moneyType })</th>
                                            <th>${L:l(lan,'交易-委托列表中部-计划委托表头-6')}</th>
                                            <th>${L:l(lan,'交易-委托列表中部-计划委托表头-7')}</th>
                                            <th class="text-center">${L:l(lan,'交易-委托列表中部-计划委托表头-8')}</th>
                                        </tr>
                                    </thead>
                                </table>
                                <div class="bk-entrust-table">
                                    <table>
                                        <col width="11%"></col><col width="14%"></col><col width="14%"></col><col width="14%"></col><col width="14%"></col><col width="17%"></col><col width="9%"></col><col width="7%"></col>
                                        <tbody id="readyRecord"></tbody>
                                    </table>
                                </div>
                            </div>
                            <div class="mine-orders-bd">
                                <table>
                                    <col width="10%"></col><col width="20%"></col><col width="20%"></col><col width="10%"></col><col width="10%"></col><col width="20%"></col><col width="10%"></col>
                                    <thead>
                                        <tr>
                                            <th>${L:l(lan,'交易-委托列表中部-历史委托表头-1')}</th>
                                            <th>${L:l(lan,'交易-委托列表中部-历史委托表头-2')}(${moneyType})</th>
                                            <th>${L:l(lan,'交易-委托列表中部-历史委托表头-3')}(${moneyType})</th>
                                            <th>${L:l(lan,'交易-委托列表中部-历史委托表头-4')}(${coinType})</th>
                                            <th>${L:l(lan,'交易-委托列表中部-历史委托表头-5')}(${moneyType})</th>
                                            <th>${L:l(lan,'交易-委托列表中部-历史委托表头-6')}</th>
                                            <th class="text-center">${L:l(lan,'交易-委托列表中部-历史委托表头-7')}</th>
                                        </tr>
                                    </thead>
                                    
                                </table>
                                <div class="bk-entrust-table">
                                    <table>
                                        <col width="10%"></col><col width="20%"></col><col width="20%"></col><col width="10%"></col><col width="10%"></col><col width="20%"></col><col width="10%"></col>
                                        <tbody id="historyRecord"></tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="trade clearfix">
                    <div class="bk-trans-form">
                        <div class="g-u  trade-item">
                            <div class="trade-title">
                                <div class="bk-length trade-type pull-right">
                                    <ul>
                                        <li role="button" data-type="depth" <c:if test="${moneyType =='BTC' and coinType== 'ABCDEF'}">class="button-disabled"</c:if> id="buyType">
                                            <a role="button"><span>${L:l(lan,'交易-买卖表单上部-下拉菜单-1')}</span><i class="caret"></i></a>
                                            <dl>
                                                <dd role="button">${L:l(lan,'交易-买卖表单上部-下拉菜单-1')}</dd>
                                                <dd role="button">${L:l(lan,'交易-买卖表单上部-下拉菜单-2')}</dd>
                                                <dd role="button">${L:l(lan,'交易-买卖表单上部-下拉菜单-3')}</dd>
                                            </dl>
                                        </li>
                                    </ul>
                                </div>
                                <h4>${L:l(lan,'交易-买卖表单上部-标题-1')} ${coinType}</h4>
                                <p style="visibility: hidden;">${L:l(lan,'交易-买卖表单上部-标签-1')} <span id="canUseMoney">--</span> ${moneyType}</p>
                            </div>
                            <div class="trade-content">
                                <form role="form" id="buyForm" class="form-horizontal" method="post" action="" autocomplete="off">
                                    <input type="hidden" name="buyType" id="buyType" value="0">
                                    <input type="hidden" name="moneyType" id="moneyType" value="${moneyType_s=='cny'? 0 : moneyType_s=='btc' ? 1 : moneyType_s=='ltc' ? 2 : moneyType_s=='eth' ? 3 : moneyType_s=='etc' ? 4 : ''  }">
                                    <input type="hidden" name="coinType" id="coinType" value="${coinType_s=='cny'? 0 : coinType_s=='btc' ? 1 : coinType_s=='ltc' ? 2 : coinType_s=='eth' ? 3 : coinType_s=='etc' ? 4 : ''  }">

                                    <!-- 限价/批量买入委托开始 -->
                                    <div id="buyDefaultForm">
                                        <div class="form-row">
                                            <label>
                                                <span class="buyBatLabel">${L:l(lan,'交易-买卖表单中部-批量委托表单标签-1')}</span>
                                                <span class="buyDefaultLabel">${L:l(lan,'交易-买卖表单中部-限价委托表单标签-1')}</span>
                                            </label>
                                            <div class="form-group">
                                                <input type="text" id="buyUnitPrice" name="buyUnitPrice" maxlength="15" <c:if test="${moneyType =='BTC' and coinType== 'ABCDEF'}">disabled</c:if> />
                                                <span class="trade-input-desc">${moneyType}</span>
                                            </div>
                                        </div>
                                        <div class="form-row buyBatLabel">
                                            <label>${L:l(lan,'交易-买卖表单中部-批量委托表单标签-2')}</label>
                                            <div class="form-group">
                                                <input type="text" id="buyMaxPrice" name="buyMaxPrice" maxlength="15" <c:if test="${moneyType =='BTC' and coinType== 'ABCDEF'}">disabled</c:if> />
                                                <span class="trade-input-desc">${moneyType}</span>
                                            </div>
                                        </div>
                                        <div class="form-row">
                                            <label>${L:l(lan,'交易-买卖表单中部-计划委托表单标签-2')}</label>
                                            <div class="form-group">
                                                <input type="text" id="buyNumber" name="buyNumber" maxlength="15" <c:if test="${moneyType =='BTC' and coinType== 'ABCDEF'}">disabled</c:if> />
                                                <span class="trade-input-desc">${coinType}</span>
                                            </div>
                                        </div>
                                        <div class="form-row buyDefaultLabel">
                                            <label>${L:l(lan,'交易-买卖表单中部-计划委托表单标签-3')}</label>
                                            <div class="form-group">
                                                <input type="text" name="realBuyAccount" class="money-amount-text" id="realBuyAccount" maxlength="15" <c:if test="${moneyType =='BTC' and coinType== 'ABCDEF'}">disabled</c:if> />
                                                <span class="trade-input-desc" id="">${moneyType}</span>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- 限价/批量买入委托结束 -->
                                    <!-- 计划委托买入开始 -->
                                    <div id="buyPlanForm">
                                        <div class="form-row">
                                            <label>${L:l(lan,'交易-买卖表单中部-计划委托表单标签-1')}</label>
                                            <div class="form-group form-group-trigger">
                                                <span><input type="text" name="buyTriggerPrice" class="money-amount-text" id="buyTriggerPrice" placeholder="${L:l(lan,'交易-买卖表单中部-计划委托表单水印-1')}" maxlength="15" /></span>
                                                <span><input type="text" name="buyPlanPrice" class="money-amount-text" id="buyPlanPrice" placeholder="${L:l(lan,'交易-买卖表单中部-计划委托表单水印-2')}" maxlength="15" /></span>
                                            </div>
                                        </div>
                                        <div class="form-row">
                                            <label>${L:l(lan,'交易-买卖表单中部-批量委托表单标签-3')}</label>
                                            <div class="form-group">
                                                <input type="text" id="buyPlanNumber" name="buyPlanNumber" maxlength="15" />
                                                <span class="trade-input-desc">${coinType}</span>
                                            </div>
                                        </div>
                                        <div class="form-row">
                                            <label>${L:l(lan,'交易-买卖表单中部-计划委托表单标签-3')}</label>
                                            <div class="form-group">
                                                <input type="text" name="buyPlanMoney" class="money-amount-text" id="buyPlanMoney" maxlength="15" />
                                                <span class="trade-input-desc">${moneyType}</span>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- 计划买入委托结束 -->
                                    </form>
                            </div>
                            <div class="form-row form-row-btn">
                                <c:choose>
                                    <c:when test="${moneyType =='BTC' and coinType== 'ABCDEF'}">
                                        <button class="btn-buy btn-disabled" id="buyBtn" disabled>${L:l(lan,'交易-买卖表单下部-按钮-1-disabled')}</button>
                                    </c:when>
                                    <c:otherwise>
                                        <button class="btn-buy" id="buyBtn" >${L:l(lan,'交易-买卖表单下部-按钮-1')}</button>
                                    </c:otherwise>
                                </c:choose>
                                <span class="trade-fee"><a href="${vip_domain}/terms/termsExpenseTable"  target= "_blank">${L:l(lan,'交易-买卖表单下部-标签-1')}${feeRate}</a></span>
                            </div>
                        </div>  
                        <div class="g-u trade-item">
                            <div class="trade-title">
                                <div class="bk-length trade-type pull-right">
                                    <ul class="">
                                        <li role="button" data-type="depth" <c:if test="${moneyType =='BTC' and coinType== 'ABCDEF'}">class="button-disabled"</c:if> id="sellType">
                                            <a role="button"><span>${L:l(lan,'交易-买卖表单上部-下拉菜单-1')}</span><i class="caret"></i></a>
                                            <dl>
                                                <dd role="button">${L:l(lan,'交易-买卖表单上部-下拉菜单-1')}</dd>
                                                <dd role="button">${L:l(lan,'交易-买卖表单上部-下拉菜单-2')}</dd>
                                                <dd role="button">${L:l(lan,'交易-买卖表单上部-下拉菜单-3')}</dd>
                                            </dl>
                                        </li>
                                    </ul>
                                </div>
                                <h4>${L:l(lan,'交易-买卖表单上部-标题-2')} ${coinType}</h4>
                                <p style="visibility: hidden;">${L:l(lan,'交易-买卖表单上部-标签-1')} <span id="canUseCoin">--</span> ${coinType}</p>
                            </div>
                            <div class="trade-content">
                                <form role="form" id="sellForm" class="form-horizontal" method="post" action="" autocomplete="off">
                                    <input type="hidden" name="sellType" id="sellType" value="0">
                                    <!-- 限价/批量委托卖出开始 -->
                                    <div id="sellDefaultForm">
                                        <div class="form-row">
                                            <label>
                                                <span class="sellBatLabel">${L:l(lan,'交易-买卖表单中部-批量委托表单标签-1')}</span>
                                                <span class="sellDefaultLabel">${L:l(lan,'交易-买卖表单中部-限价委托表单标签-1')}</span>
                                            </label>
                                            <div class="form-group">
                                                <input type="text" id="sellUnitPrice" name="sellUnitPrice" maxlength="15" <c:if test="${moneyType =='BTC' and coinType== 'ABCDEF'}">disabled</c:if> />
                                                <span class="trade-input-desc">${moneyType}</span>
                                            </div>
                                        </div>
                                        <div class="form-row sellBatLabel">
                                            <label>${L:l(lan,'交易-买卖表单中部-批量委托表单标签-2')}</label>
                                            <div class="form-group">
                                                <input type="text" id="sellMaxPrice" name="sellMaxPrice" maxlength="15" <c:if test="${moneyType =='BTC' and coinType== 'ABCDEF'}">disabled</c:if> />
                                                <span class="trade-input-desc">${moneyType}</span>
                                            </div>
                                        </div>
                                        <div class="form-row">
                                            <label>${L:l(lan,'交易-买卖表单中部-批量委托表单标签-3')}</label>
                                            <div class="form-group">
                                                <input type="text" id="sellNumber" name="sellNumber" maxlength="15" <c:if test="${moneyType =='BTC' and coinType== 'ABCDEF'}">disabled</c:if> />
                                                <span class="trade-input-desc">${coinType}</span>
                                            </div>
                                        </div>
                                        <div class="form-row sellDefaultLabel">
                                            <label>${L:l(lan,'交易-买卖表单中部-计划委托表单标签-3')}</label>
                                            <div class="form-group">
                                                <input type="text" class="money-amount-text" id="realSellAccount" maxlength="15" <c:if test="${moneyType =='BTC' and coinType== 'ABCDEF'}">disabled</c:if> />
                                                <span class="trade-input-desc">${moneyType}</span>
                                            </div>
                                        </div>
                                    </div>
                                        <!-- 限价/批量委托卖出结束 -->
                                        <!-- 计划委托卖出开始 -->
                                    <div id="sellPlanForm">
                                        <div class="form-row">
                                            <label>${L:l(lan,'交易-买卖表单中部-计划委托表单标签-1')}</label>
                                            <div class="form-group form-group-trigger">
                                                <span><input type="text" id="sellTriggerPrice" name="sellTriggerPrice" placeholder="${L:l(lan,'交易-买卖表单中部-计划委托表单水印-1')}" maxlength="10" /></span>
                                                <span><input type="text" id="sellPlanPrice" name="sellPlanPrice" placeholder="${L:l(lan,'交易-买卖表单中部-计划委托表单水印-2')}" maxlength="10" /></span>
                                                
                                            </div>
                                        </div>
                                        <div class="form-row">
                                            <label>${L:l(lan,'交易-买卖表单中部-批量委托表单标签-3')}</label>
                                            <div class="form-group">
                                                <input type="text" id="sellPlanNumber" name="sellPlanNumber" maxlength="15" />
                                                <span class="trade-input-desc">${coinType}</span>
                                            </div>
                                        </div>
                                        <div class="form-row">
                                            <label>${L:l(lan,'交易-买卖表单中部-计划委托表单标签-3')}</label>
                                            <div class="form-group">
                                                <input type="text" class="money-amount-text" id="sellPlanMoney" maxlength="15" />
                                                <span class="trade-input-desc">${moneyType}</span>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- 计划卖出委托结束 -->
                                </form>
                            </div>
                            <div class="form-row form-row-btn">
                                <c:choose>
                                    <c:when test="${moneyType =='BTC' and coinType== 'ABCDEF'}">
                                        <button class="btn-sell btn-disabled" id="sellBtn" disabled>${L:l(lan,'交易-买卖表单下部-按钮-2-disabled')}</button>
                                    </c:when>
                                    <c:otherwise>
                                        <button class="btn-sell" id="sellBtn">${L:l(lan,'交易-买卖表单下部-按钮-2')}</button>
                                    </c:otherwise>
                                </c:choose>
                                
                                <span class="trade-fee"><a href="${vip_domain}/terms/termsExpenseTable" target= "_blank">${L:l(lan,'交易-买卖表单下部-标签-1')}${feeRate}</a></span>
                                <input type="hidden" name="sellType" id="sellType" value="0">
                            </div>
                        </div>
                        <div class="g-u  trade-item">
                            <div class="trade-title">
                                <h4>${L:l(lan,'交易-交易摘要上部-标题-1')}</h4>
                            </div>
                            <div class="trade-content summaryDefault" id="summaryDefault">
                                <div class="def-box clearfix">
                                    <div class="buy-sum">
                                        <p><b>${L:l(lan,'交易-交易摘要中部-买入标签-1')} ${coinType}</b></p>
                                        <p>${L:l(lan,'交易-交易摘要中部-买入标签-2')}<span class="netValue">0</span></p>
                                        <p>${L:l(lan,'交易-交易摘要中部-买入标签-3')}<span class="costPrice">0.0</span></p>
                                        <p>${L:l(lan,'交易-交易摘要中部-买入标签-4')}<span class="lastPrice">0.0</span></p>
                                        <p>${L:l(lan,'交易-交易摘要中部-买入标签-5')}<span class="marketValue">0.0</span></p>
                                    </div>
                                    <div class="sell-sum">
                                        <p><b>${L:l(lan,'交易-交易摘要中部-卖出标签-1')} ${coinType}</b></p>
                                        <p>${L:l(lan,'交易-交易摘要中部-卖出标签-2')}<span class="netValue">0</span></p>
                                        <p>${L:l(lan,'交易-交易摘要中部-卖出标签-3')}<span class="costPrice">0.0</span></p>
                                        <p>${L:l(lan,'交易-交易摘要中部-卖出标签-4')}<span class="lastPrice">0.0</span></p>
                                        <p>${L:l(lan,'交易-交易摘要中部-卖出标签-5')}<span class="marketValue">0.0</span></p>
                                    </div>
                                </div>
                                <p>${coinType} ${L:l(lan,'交易-交易摘要下部-标签-1')}<span class="" id="profitOrLoss"><b>0.0</b> ${moneyType}</span></p>
                                <p>${coinType} ${L:l(lan,'交易-交易摘要下部-标签-2')}<span id="summaryTotal"><b>0.0</b> ${coinType} </span></p>
                                <div class="re-summary clearfix"><!-- <a href="###" target="_blank">${L:l(lan,'交易-交易摘要下部-超链接-1')}</a> --><button id="summaryRecount">${L:l(lan,'交易-交易摘要下部-按钮-1')}</button></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="order clearfix">
                    <div class="g-u trade-item trade-history">
                        <div class="trade-title">
                            
                            <div class="trade-title-right">
                                <span class="active">${L:l(lan,'交易-站点成交记录-页签-1')}</span><span id="gbc-repo">${L:l(lan,'回购')}</span><span>${L:l(lan,'交易-站点成交记录-页签-2')}</span>

                            </div>
                            
                            <h4>${L:l(lan,'交易-站点成交记录-标题-1')}</h4>
                        </div>
                        <div class="trade-content">
                            <div class="trade-history-bd">
                                <table>
                                    <thead>
                                        <tr>
                                            <th>${L:l(lan,'交易-站点成交记录-表头-1')}</th>
                                            <th>${L:l(lan,'交易-站点成交记录-表头-2')}</th>
                                            <th>${L:l(lan,'交易-站点成交记录-表头-3')}(${moneyType})</th>
                                            <th>${L:l(lan,'交易-站点成交记录-表头-4')}(${coinType})</th>
                                            <!-- <th class="text-right">${L:l(lan,'总计')}(${moneyType})</th> -->
                                        </tr>
                                    </thead>
                                    <tbody id="newTradesRecord">
                                    </tbody>
                                </table>
                            </div>

                            <div class="trade-history-bd">
                                <div class="repo_top">
                                    <div class="repo_top_right">
                                        <div class="circle_box" id="circleBox">
                                            <svg width="70" height="70" viewbox="0 0 70 70">
                                                <circle cx="35" cy="35" r="32" stroke-width="6" stroke="#EFEFEF" fill="none"></circle>
                                                <circle cx="35" cy="35" r="32" stroke-width="6" stroke="#57C4A7" fill="none" transform="rotate(-90,35 35)" stroke-dasharray="100 100"></circle>
                                            </svg>
                                            <em></em>
                                        </div>
                                        <div class="histogram_box clearfix" id="histogram_box">
                                            
                                        </div>
                                    </div>
                                    <p>${L:l(lan,'回购频率')}</p>
                                    <h4><b id="repo-rate"></b>${L:l(lan,'回购频率秒/次')}</h4>
                                    <p>${L:l(lan,'最近一次回购资金')}</p>
                                    <h4><b id="last-repo"></b>USDT</h4>
                                </div>
                                <div class="repo_body">
                                    <div class="repo_list_box">
                                        <table>
                                            <thead>
                                                <tr>
                                                    <th>${L:l(lan,'日期')}</th>
                                                    <th>${L:l(lan,'资金量比')}</th>
                                                    <th>${L:l(lan,'回购资金')}</th>
                                                    <th>=></th>
                                                    <th>${L:l(lan,'回购量')}</th>
                                                </tr>
                                            </thead>
                                        </table>
                                        <div class="list_cover" id="repo_cover">
                                            <div class="list_table">
                                                <table>
                                                    <tbody id="repoList">
                                                        
                                                    </tbody>
                                                </table>
                                                <p class="moretwenty"><b>${L:l(lan,'再显示20条')}<i class="iconfont">&#xe635;</i></b></p>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="related_list_box">
                                        <table>
                                            <thead>
                                                <tr>
                                                    <th>${L:l(lan,'日期')}</th>
                                                    <th>${L:l(lan,'资金量比')}</th>
                                                    <th>${L:l(lan,'回购资金')}</th>
                                                    <th>=></th>
                                                    <th>${L:l(lan,'回购量')}</th>
                                                </tr>
                                            </thead>
                                        </table>
                                        <div class="list_cover"  id="related_cover">
                                            <div class="list_table">
                                                <table>
                                                    <tbody id="relatedList">
                                                        
                                                    </tbody>
                                                </table>
                                                <p class="moretwenty"><b>${L:l(lan,'再显示20条')}<i class="iconfont">&#xe635;</i></b></p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="repo_footer">
                                    <a href="####">${L:l(lan,'回购是什么')}</a>
                                    <span id="related">${L:l(lan,'与我相关的回购')}</span>
                                </div>
                            </div>

                            <div class="trade-history-bd">
                                <table>
                                    <thead>
                                        <tr>
                                            <th>${L:l(lan,'交易-站点成交记录-表头-1')}</th>
                                            <th>${L:l(lan,'交易-站点成交记录-表头-2')}</th>
                                            <th>${L:l(lan,'交易-站点成交记录-表头-3')}(${moneyType})</th>
                                            <th>${L:l(lan,'交易-站点成交记录-表头-4')}(${coinType})</th>
                                            <!-- <th>${L:l(lan,'总计')}(${moneyType})</th> -->
                                        </tr>
                                    </thead>
                                    <tbody id="mineTradesRecord">
                                    </tbody>
                                </table>
                            </div>
                            
                        </div>
                    </div>
                    <div class="item2 trans-practice">
                        <div class="g-u trade-item sell-item">
                            <div class="trade-title">
                                <h4>${L:l(lan,'交易-买卖盘口上部-标题-2')}</h4>
                            </div>
                            <div class="trade-content">
                                <table>
                                    <thead>
                                        <tr>
                                            <th>#</th>
                                            <th>${L:l(lan,'交易-买卖盘口中部-买入表头-2')}(${moneyType})</th>
                                            <th class="text-right">${L:l(lan,'交易-买卖盘口中部-买入表头-3')}(${coinType})</th>
                                            <th class="text-right">${L:l(lan,'交易-委托列表中部-限价委托表头-5')}(${moneyType })</th>
                                            <th></th>
                                        </tr>
                                    </thead>
                                    <tbody id="sellMarket">
                                    </tbody>
                                </table>
                            </div>
                        </div>
                        <div class="g-u trade-item buy-item">
                            <div class="trade-title">
                                <h4>${L:l(lan,'交易-买卖盘口上部-标题-1')}</h4>
                            </div>
                            <div class="trade-content">
                                <table>
                                    <thead>
                                        <tr>
                                            <th>#</th>
                                            <th>${L:l(lan,'交易-买卖盘口中部-卖出表头-2')}(${moneyType})</th>
                                            <th class="text-right">${L:l(lan,'交易-买卖盘口中部-卖出表头-3')}(${coinType})</th>
                                            <th class="text-right">${L:l(lan,'交易-委托列表中部-限价委托表头-5')}(${moneyType })</th>
                                            <th></th>
                                        </tr>
                                    </thead>
                                    <tbody id="buyMarket">
                                    </tbody>
                                </table>
                            </div>
                        </div>
                        <div class="bk-length">
                            <ul class="">
                                <li role="button" data-type="depth" class="" id="bkDepth">
                                    <a role="button"><span>${L:l(lan,'交易-买卖盘口上部-下拉菜单-1')}</span><i class="caret"></i></a>
                                    <dl>
                                        <dd role="button" data-depth="" class="active">${L:l(lan,'交易-买卖盘口上部-下拉菜单-1')}</dd>
                                        <c:forEach items="${mergeDepth}" var="depth">
                                            <dd role="button" data-depth="${depth}" >${L:l(lan,'交易-买卖盘口上部-下拉菜单-2')} ${depth}</dd>
                                        </c:forEach>
                                    <%--  <dd role="button" data-depth="0.000001" class="active">${L:l(lan,'合并')}0.000001</dd>
                                     <dd role="button" data-depth="0.0001">${L:l(lan,'合并')}0.0001</dd>
                                     <dd role="button" data-depth="0.0003">${L:l(lan,'合并')}0.0003</dd>
                                     <dd role="button" data-depth="0.0005">${L:l(lan,'合并')}0.0005</dd> --%>
                                      </dl>
                                </li>
                            </ul>
                        </div>
                        <i class="iconfont transposal" id="ordersTransposal">&#xe619;</i>
                    </div>
                    <div class="item2">
                        <div class="g-u trade-item depth-item">
                            <div class="trade-title">
                                <h4>${L:l(lan,'交易-市场深度-标题-1')}</h4>
                            </div>
                            <div class="trade-content" id="graphbox_depth">
                                
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-s">
            <div class="menu-left">
                <div class="sidebar-title">
                    <div id="tagTab" class="trade-title-right"><span>USDT</span><span>BTC</span></div>
                    <!-- ${L:l(lan,'交易-货币列表-标题-1')} -->
                </div>
                <div class="symbol-list" id="topAllMarket">
                    
                </div>
            </div>
        </div>
    </div>
</div>
<div id="tradeList" style="display:none;">
    <div class="bk-entrust">
        <div class="bk-entrust-info">
            <table class="table table-striped table-bordered table-hover">
                <tbody id="tradeRecordInfo"></tbody>
            </table>
        </div>
        <table class="table table-striped table-bordered table-hover table-nopadding">
            <col width="25%"></col><col width="25%"></col><col width="25%"></col><col width="25%"></col>
            <thead>
            <tr>
                <th>${L:l(lan,'交易-委托详情模式窗-委托详情表头-4')}</th>
                <th style="text-align:left;">${L:l(lan,'交易-委托详情模式窗-委托详情表头-5')}(${coinType })</th>
                <th style="text-align:left;">${L:l(lan,'交易-委托详情模式窗-委托详情表头-6')}(${moneyType })</th>
                <th style="text-align:left;">${L:l(lan,'交易-委托详情模式窗-委托详情表头-7')}(${moneyType })</th>
            </tr>
            </thead>
            
        </table>
        <div style="max-height:350px; min-height:100px;overflow-x:hidden;overflow-y:auto;">
            <table class="table table-striped table-bordered table-hover table-nopadding">
                <col width="25%"></col><col width="25%"></col><col width="25%"></col><col width="25%"></col>
                <tbody id="tradeRecord"></tbody>
            </table>
        </div>
    </div>
</div>
<div id="repoDetail" style="display:none;">
    <div class="bk-repo">
        <h3>${L:l(lan,'平台会将成交产生的手续费用作回购ABCDEF')} <a href="####">${L:l(lan,'了解详情')}</a></h3>
        <div class="bk-repo-info clearfix">
            <div class="tit">${L:l(lan,'回购成功')}</div>
            <div class="repo-id">${L:l(lan,'回购ID')}:<b></b></div>
            <div class="repo-date">
                <p><em></em></p>
                <p><b></b></p>
            </div>
            <div class="content">
                <p><span>${L:l(lan,'回购资金')}</span><i>${L:l(lan,'买入')}</i><span>${L:l(lan,'回购量')}</span></p>
                <p><b class="repo-total-money"></b><i class="iconfont">&#xe633;</i><b class="repo-amount"></b></p>
            </div>
        </div>
        <div class="bk-repo-inscape">
            <h4>${L:l(lan,'回购成交记录')}</h4>
            <table>
                <thead>
                    <tr>
                        <th>${L:l(lan,'日期')}</th>
                        <th>${L:l(lan,'价格')}</th>
                        <th>${L:l(lan,'数量')}</th>
                        <th>${L:l(lan,'总额')}</th>
                    </tr>
                </thead>
                <tbody id="repoRecordInfo">
                    
                </tbody>
            </table>
        </div>
        <div class="bk-repo-detail">
            <h4>${L:l(lan,'回购资金构成')}</h4>
            <table>
                <thead>
                    <tr>
                        <th>${L:l(lan,'日期')}</th>
                        <th>${L:l(lan,'占比')}</th>
                        <th>${L:l(lan,'成交ID')}</th>
                        <th>${L:l(lan,'市场')}</th>
                        <th>${L:l(lan,'手续费折算')}</th>
                    </tr>
                </thead>
                <tbody id="repoRecordDetail">
                    
                </tbody>
            </table>
        </div>
    </div>
</div>

<script type="text/javascript">
    sessionStorage.setItem("market","${market}");
</script>
<script type="text/x-tmpl" id="tmpl-sellMarket">
    {% for (var i = 0; i <= rs.length -1; i++) { %}
        <tr>
            <td width="10%">{%=i+1%}</td>
            <td width="20%">{%=rs[i][0]%}</td>
            <td width="20%" class="text-right">{%=rs[i][1].split(".")[0]%}.<g>{%=rs[i][1].split(".")[1]%}</g></td>
            <td width="20%" class="text-right">{%=rs[i][3]%}</td>
            <td width="30%" class="text-left"><div class="percent-cover"><div class="percent text-second" style="width:{%=rs[i][2]%}%"></div></div></td>
        </tr>
    {% } %}
</script>
<script type="text/x-tmpl" id="tmpl-buyMarket">
    {% for (var i = 0; i <= rs.length -1; i++) { %}
        <tr>
            <td width="10%">{%=i+1%}</td>           
            <td width="20%">{%=rs[i][0]%}</td>
            <td width="20%" class="text-right">{%=rs[i][1].split(".")[0]%}.<g>{%=rs[i][1].split(".")[1]%}</g></td>
            <td width="20%" class="text-right">{%=rs[i][3]%}</td>
            <td width="30%" class="text-left"><div class="percent-cover"><div class="percent text-primary" style="width:{%=rs[i][2]%}%"></div></div></td>
        </tr>
    {% } %}
</script>
          
<script type="text/x-tmpl" id="tmpl-entrustRecord">
    {% for (var i = 0; i <= rs.length -1; i++) { %}
    <tr>
        <td><span class="label {%=rs[i].nameClass%}">{%=rs[i].nameType%}</span></td>
        <td>{%=rs[i].unitPrice%}</td>
        <td>{%=rs[i].averagePrice%}</td>
        <td>{%=rs[i].numbers%} / {%=rs[i].completeNumber%}</td>
        <td>{%=rs[i].completeTotalMoney%}</td>
        <td>{%=rs[i].submitTime%}</td>
        <td class="text-center">
            <a class="cancelEntrust" role="button" data-id="{%=rs[i].entrustId%}" data-plantype="{%=rs[i].plantype%}" data-type="{%=rs[i].types%}">{%=rs[i].operat%}</a>
        </td>
    </tr>
    {% } %}
</script>

<script type="text/x-tmpl" id="tmpl-readyRecord">
            {% for (var i = 0; i <= rs.length -1; i++) { %}
            <tr>
                <td><span class="label {%=rs[i].nameClass%}">{%=rs[i].nameType%}</span></td>
                <td ><span class="">{% if(!isNaN(rs[i].triggerPrice)){ %} {%=rs[i].triggerPrice%}  {% }else{ %} {%=rs[i].triggerPriceProfit%} {% } %} </span>       
                </td>
                <td ><span class="">{% if(!isNaN(rs[i].unitPrice)){ %} {%=rs[i].unitPrice%} {% }else{ %} {%=rs[i].unitPriceProfit%} {% } %}  </span>    
                </td>
                <td>
                    <span>{% if(!isNaN(rs[i].numbers)){ %} {%=rs[i].numbers%} {% }else{ %} {%=rs[i].stopAmount%} {% } %} </span>
                </td>
                <td><span class="ft14">{% if(rs[i].totalMoney!= 0 ){ %} {%=rs[i].totalMoney%} {% } 
                else if(!isNaN(rs[i].unitPrice) && !isNaN(rs[i].numbers)){ %} {%=(rs[i].unitPrice*rs[i].numbers).toFixed(5)%} {% }
                else if(isNaN(rs[i].unitPrice) && !isNaN(rs[i].numbers)){ %} {%=(rs[i].unitPriceProfit*rs[i].numbers).toFixed(5)%} {% } 
                else if(!isNaN(rs[i].unitPrice) && isNaN(rs[i].numbers)){ %} {%=(rs[i].unitPrice*rs[i].stopAmount).toFixed(5)%} {% }
                else if(isNaN(rs[i].unitPrice) && isNaN(rs[i].numbers)){ %} {%=(rs[i].unitPriceProfit*rs[i].stopAmount).toFixed(5)%} {% } 
                %} </span>
                </td>
                
                <td>{%=rs[i].submitTime%}</td>
                <td>{%=rs[i].nameStatus%}</td>
                <td class="text-center">
                    <a class="cancelEntrust" role="button" data-id="{%=rs[i].entrustId%}" data-plantype="{%=rs[i].plantype%}" data-type="{%=rs[i].types%}">{%=rs[i].operat%}</a>
                    {% if(rs[i].nameStatus=='已委托'){ %}              
                        <a class="detailEntrust" role="button" data-id="{%=rs[i].formalEntrustId%}"> ${L:l(lan,'明细')} </a>
                    {% }%}
                </td>
            </tr>
            {% } %}
</script>
<script type="text/x-tmpl" id="tmpl-topAllMarket">
{% for (var i = 0; i <= rs.length -1; i++) { %}
<a class="symbol-item{%=rs[i].market=='${market}'?' active':''%}"  moneyTag = "{%=rs[i].moneyTag%}" href="/trade/{%=rs[i].market%}/">
    <div class="symbol-info">
        <div class="clearfix">
            <div class="symbol-img">
                {% if(rs[i].market=='gbc_btc'){ %}
                    <img src="${static_domain }/statics/img/common/{%=rs[i].stag%}-gray.png">
                {% }else{ %}
                    <img src="${static_domain }/statics/img/common/{%=rs[i].stag%}.png">
                {% }%}
            </div>
            <div class="symbol-range" style="background-color:{% if (rs[i].rangeOf24h>0) { %}{%= '#2BB38A' %}{% }else if (rs[i].rangeOf24h< 0){ %}{%= '#E34B51' %}{% }else{ %}{%= '#607D8B' %}{% } %};">{%=rs[i].rangeOf24h>0?'+ '+rs[i].rangeOf24h:"" %}{%= rs[i].rangeOf24h<0? '- '+ -rs[i].rangeOf24h:"" %}{%= rs[i].rangeOf24h==0? rs[i].rangeOf24h:"" %} %</div>
        </div>
        <div class="clearfix">
            <div class="symbol-name">
                <h6>{%=rs[i].propTag%}</h6>
            </div>
            <div class="symbol-price">
                {%=rs[i].price%}
                {%=rs[i].moneyTag%}
            </div>
        </div>
        <div class="clearfix">
            <div class="symbol-name-detail">
                <p>{%=rs[i].desc%}</p>
            </div>
            <div class="symbol-price-btc">
                ${L:l(lan,'交易-货币列表-标签-2')} {%=rs[i].priceBtc=='NaN'||rs[i].priceBtc == 0 ?'0.0':rs[i].priceBtc%} {%=rs[i].moneyTag%}
            </div>
        </div>
    </div>
    
</a>

{% } %}
</script>

<script type="text/x-tmpl" id="tmpl-historyRecord">
    {% for (var i = 0; i <= rs.length -1; i++) { %}
    <tr>
        <td><span class="label {%=rs[i].nameClass%}">{%=rs[i].nameType%}</span></td>
        <td>{%=rs[i].unitPrice%}</td>
        <td>{%=rs[i].averagePrice%}</td>
        <td>{%=rs[i].completeNumber%}</td>
        <td>{%=rs[i].completeTotalMoney%}</td>
        <td>{%=rs[i].submitTime%}</td>
        <td class="text-center"><a class="detailEntrust" role="button" data-id="{%=rs[i].entrustId%}" data-types="{%=rs[i].types%}" data-numbers="{%=rs[i].numbers%}">${L:l(lan,'明细')}</a></td>
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
 <script type="text/x-tmpl" id="tmpl-tradeRecord">
            {% for (var i = 0; i <= rs.length -1; i++) { %}
            <tr>
              <td>{%=rs[i].submitTime%}</td>
              <td style="text-align:left;">
                 <span class="bkNum">{%=rs[i].numbers%}</span>
              </td>
              <td style="text-align:left;">{%=rs[i].unitPrice%}</td>
              <td>{%=rs[i].totalMoney%}</td>
            </tr>
      {% } %}
</script>
<script type="text/x-tmpl" id="tmpl-summaryDefault">
    
</script>
<script src="${static_domain }/statics/js/jquery.flot-min.js?V${CH_VERSON }" charset="UTF-8"></script>
<script src="${static_domain }/statics/js/charting_library.min.js?V${CH_VERSON }" charset="UTF-8"></script>
<script src="${static_domain }/statics/js/web.wheeler.js" charset="UTF-8"></script>

    <!-- Body From mainPage End -->
    <!-- Common FootMain Begin -->
    <jsp:include page="/common/foot.jsp">
        <jsp:param value="${market }" name="market"/>
    </jsp:include>
    <!-- Common FootMain End -->
</div>

</body>
</html>
