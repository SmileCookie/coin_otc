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
    <title>${L:l(lan,'历史委托')}-${WEB_NAME }-${WEB_TITLE }</title>
    <meta name="keywords" content="${WEB_KEYWORD }" />
    <meta name="description" content="${WEB_DESC }" />
    <link rel="stylesheet" href="${static_domain }/statics/css/web.trans.css?V${CH_VERSON }">    
    <link rel="stylesheet" href="${static_domain }/statics/css/web.entrust.css?V${CH_VERSON }">
</head>
<body class="">
    <div class="bk-body">
        <!-- Common TopMenu Begin -->
        <jsp:include page="/common/top.jsp" />
        <!-- Common TopMenu End -->
        <!-- Body From mainPage Begin -->
        <div class="mainer-phase2">
            <div class="container2">
                <h2 class="entrust-title">${L:l(lan,'历史委托')}</h2>
                <div class="entrust-head">
                    <div class="entrust-head-market left">
                        <h5 class="left padl10">${L:l(lan,'历史委托-市场')}</h5>
                        <ul class="beaSelect left" data-type="mType">
                            <li role="button" id="entrustMarketCoin">
                                <a href="javascript:void(0);" role="button">
                                    <span class="select-title">ABCDEF</span><i class="caret"></i>
                                </a>
                                <dl></dl>
                            </li>
                        </ul>
                        <h5 class="left pad10">/</h5>
                        <ul class="beaSelect left" data-type="mNormType">
                            <li role="button" >
                                <a href="javascript:void(0);" role="button"><span id="mNormTypeCon">USDT</span><i class="caret"></i></a>
                                <dl id="normCoinTab">
                                    <dd role="button">USDT</dd>
                                    <dd role="button">BTC</dd>
                                </dl>
                            </li>
                        </ul>
                    </div>
                    <div class="entrust-head-type left">
                        <h5 class="left padl10">${L:l(lan,'历史委托-类型')}</h5>
                        <ul class="beaSelect left" data-type="mFinds">
                            <li role="button">
                                <a href="javascript:void(0);" role="button"><span>${L:l(lan,'历史委托-不限')}</span><i class="caret"></i></a>
                                <dl>
                                    <dd role="button" data-value="-1">${L:l(lan,'历史委托-不限')}</dd>                                        
                                    <dd role="button" data-value="1">${L:l(lan,'历史委托-买入')}</dd>
                                    <dd role="button" data-value="0">${L:l(lan,'历史委托-卖出')}</dd>
                                </dl>
                            </li>
                        </ul>
                    </div>
                    <div class="entrust-head-box left">
                        <input type="checkbox" name="undoTrade" id="undoTrade" checked="checked" />
                        <label for="undoTrade">${L:l(lan,'历史委托-隐藏已撤销')}</label>
                    </div>

                    <div class="entrust-time right">
                        <h5 class="padl10">${L:l(lan,'历史委托-时间')}</h5>
                        <ul class="tab-time">
                            <li class="active bor-left">${L:l(lan,'历史委托-24小时')}</li>
                            <li>${L:l(lan,'历史委托-历史')}</li>
                        </ul>
                    </div>
                </div>
                <div class="entrust-con bk-entrust">
                    <table class="table-entrust">
                        <thead>
                            <tr>
                                <th>${L:l(lan,'历史委托-日期')}</th>
                                <th>${L:l(lan,'历史委托-列表-类型')}</th>
                                <th class="text-right">${L:l(lan,'历史委托-委托价格')}</th>
                                <th class="text-right">${L:l(lan,'历史委托-委托数量')}</th>
                                <th class="text-right">${L:l(lan,'历史委托-成交均价')}</th>
                                <th class="text-right">${L:l(lan,'历史委托-成交数量')}</th>
                                <th class="text-right">${L:l(lan,'历史委托-成交金额')}</th>
                                <th>${L:l(lan,'历史委托-状态')}</th>
                                <th>${L:l(lan,'历史委托-操作')}</th>
                            </tr>
                        </thead>
                        <tbody id="historyEntrustList">
                        </tbody>
                    </table>
                </div>
                <div class="historyEntrustList-page" id="historyEntrustList_Page"></div>
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
                        <thead id="tradeRecordHead"></thead>
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
        <!-- Body From mainPage End -->
            <!-- Common FootMain Begin -->
            <jsp:include page="/common/foot.jsp">
            <jsp:param value="${market}" name="market"/>
        </jsp:include>
        <!-- Common FootMain End -->
    </div>
    <script type="text/javascript">
        $(document).on('click',function(e){
            e.stopPropagation()
            var $options = $(this).find('dl');
            if($options.hasClass("beaShow")){
                $options.removeClass("beaShow").hide();
            }
        })
    </script>
    <script type="text/x-tmpl" id="tmpl-historyEntrustList">
        {% for (var i = 0; i <= rs.length -1; i++) { %}
          <tr>
            <td>{%=rs[i].date%}</td>
            <td class={%=rs[i].type==1? 'buy' :'sell'%}>{%=rs[i].typeRes%}</td>
            <td style="text-align:right;">{%=rs[i].price%}</td>
            <td style="text-align:right;">{%=rs[i].amount%}</td>
            <td style="text-align:right;">{%=rs[i].averagePrice%}</td>
            <td style="text-align:right;">{%=rs[i].completeNumber%}</td>
            <td style="text-align:right;">{%=rs[i].completeTotalMoney%}</td>
            <td>{%=rs[i].statusRes%}</td>
            <td>
                {% if(rs[i].status == 2){ %}
                    <a class="detailEntrust" role="button" data-id="{%=rs[i].entrustId%}" data-types="{%=rs[i].type%}" data-numbers="{%=rs[i].amount%}">${L:l(lan,'明细')}</a>
                {% } %} 
            </td>            
          </tr>
        {% } %}
    </script>   
    <script type="text/x-tmpl" id="tmpl-tradeRecordInfo">
        <tr>
            <td><span class="bk-entrust-info-type {%=rs.typesClass%}">{%=rs.types%}</span></td>
            <td>
                <h6>${L:l(lan,'成交均价')}({%=rs.moneyType%})</h6>
                <p>{%=rs.allAvrPrice%}</p>
            </td>
            <td>
                <h6>${L:l(lan,'委托数量/成交数量')}({%=rs.coinType%})</h6>
                <p>{%=rs.allNumber%}/{%=rs.allTotalCoin%}</p>
            </td>
            <td>
                <h6>${L:l(lan,'成交额')}({%=rs.moneyType%})</h6>
                <p>{%=rs.allTotalMoney%}</p>
            </td>
        </tr>
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
    <script type="text/x-tmpl" id="tmpl-tradeRecordHead">
        <tr>
            <th>${L:l(lan,'成交时间')}</th>
            <th style="text-align:right;">${L:l(lan,'成交数量')}({%=rs.coinType%})</th>
            <th style="text-align:right;">${L:l(lan,'成交价格')}({%=rs.moneyType%})</th>
            <th style="text-align:right;">${L:l(lan,'成交额')}({%=rs.moneyType%})</th>
        </tr>
    </script>
</body>
</html>
