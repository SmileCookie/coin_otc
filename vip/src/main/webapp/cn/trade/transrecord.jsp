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
    <title>${L:l(lan,'历史成交')}-${WEB_NAME }-${WEB_TITLE }</title>
    <meta name="keywords" content="${WEB_KEYWORD }" />
    <meta name="description" content="${WEB_DESC }" />
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
                <h2 class="entrust-title">${L:l(lan,'历史成交')}</h2>
                <div class="entrust-head">
                    <div class="entrust-head-market left">
                        <h5 class="left padl10">${L:l(lan,'历史成交-市场')}</h5>
                        <ul class="beaSelect left" data-type="mType">
                            <li role="button" id="entrustMarketCoin">
                                <a href="javascript:void(0);" role="button">
                                    <span class="select-title"></span><i class="caret"></i>
                                </a>
                                <dl></dl>
                            </li>
                        </ul>
                        <h5 class="left pad10">/</h5>
                        <ul class="beaSelect left" data-type="mNormType">
                            <li role="button">
                                <a href="javascript:void(0);" role="button"><span>USDT</span><i class="caret"></i></a>
                                <dl id="normCoinTab">
                                    <dd role="button">USDT</dd>
                                    <dd role="button">BTC</dd>
                                </dl>
                            </li>
                        </ul>
                    </div>
                    <div class="entrust-head-type left">
                        <h5 class="left padl10">${L:l(lan,'历史成交-类型')}</h5>
                        <ul class="beaSelect left" data-type="mFinds">
                            <li role="button">
                                <a href="javascript:void(0);" role="button"><span>${L:l(lan,'历史成交-不限')}</span><i class="caret"></i></a>
                                <dl>
                                    <dd role="button" data-value="-1">${L:l(lan,'历史成交-不限')}</dd>                                        
                                    <dd role="button" data-value="1">${L:l(lan,'历史成交-买入')}</dd>
                                    <dd role="button" data-value="0">${L:l(lan,'历史成交-卖出')}</dd>
                                </dl>
                            </li>
                        </ul>
                    </div>
                    <div class="entrust-time right">
                        <h5 class="padl10">${L:l(lan,'历史成交-时间')}</h5>
                        <ul class="tab-time">
                            <li class="active bor-left">${L:l(lan,'历史成交-24小时')}</li>
                            <li>${L:l(lan,'历史成交-历史')}</li>
                        </ul>
                    </div>
                </div>
                <div class="entrust-con">
                    <table class="table-entrust">
                        <thead>
                            <tr>
                                <th>${L:l(lan,'历史成交-日期')}</th>
                                <th>${L:l(lan,'历史成交-列表-类型')}</th>
                                <th>${L:l(lan,'历史成交-成交价')}</th>
                                <th class='text-right'>${L:l(lan,'历史成交-流出')}</th>
                                <th class='text-right borright'>${L:l(lan,'历史成交-流入')}</th>                            
                            </tr>
                        </thead>
                        <tbody id="historyEntrustList">
                        </tbody>
                    </table>
                </div>
                <div class="historyEntrustList-page" id="historyEntrustList_Page"></div>                
            </div>
        </div>
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
            <td>{%=rs[i].price%}</td>
            <td class='text-right'>{%=rs[i].outAmount%}</td>
            <td class='text-right'>{%=rs[i].intAmount%}</td>         
          </tr>
        {% } %}
    </script>  
</body>
</html>
