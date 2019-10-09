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
    <title>${L:l(lan,"提现记录")}-${WEB_NAME }-${WEB_TITLE}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0 maximum-scale=1, user-scalable=no">
    <meta name="keywords" content="${WEB_KEYWORD }" />
    <meta name="description" content="${WEB_DESC }" />
    <link rel="stylesheet" href="${static_domain }/statics/css/web.pay.css?V${CH_VERSON }">
    <link rel="stylesheet" href="${static_domain }/statics/css/web.asset.css?V${CH_VERSON }">
</head>
<body>
    <div class="bk-body">
        <jsp:include page="/common/top.jsp" />
        <div class="mainer">
            <div class="container2">
                <div class="content"> 
                    <div class="record-head tit-space">
                        <h2 class="assets-title left">${L:l(lan,'充值记录')}</h2>
                        <ul class="beaSelect right">
                            <li role="button">
                                <a href="javascript:void(0);" role="button">
                                    <span class="select-title">${L:l(lan,'币种-全部')}</span><i class="caret"></i>
                                </a>
                                <dl id="cointSelect"></dl>
                            </li>
                        </ul>
                    </div>
                    <div class="record-box">
                        <table class="table-history">
                            <thead>
                                <tr>
                                    <th width="15%">${L:l(lan,'状态（确认）')}</th>
                                    <th width="15%" class="text-center">${L:l(lan,'币种')}</th>
                                    <th width="15%" class="text-center">${L:l(lan,'数量')}</th>
                                    <th width="55%" class="borright text-center">${L:l(lan,'地址')}</th>
                                </tr>
                            </thead>
                            <tbody id="chargeRecordDetailH">
                                
                            </tbody>
                        </table>
                        <div class="pageCon" id="chargeRecordDetailH_Page"></div>
                    </div>
                    <h2 class="assets-title">${L:l(lan,'提现记录')}</h2>
                    <div class="record-box">
                        <table class="table-history">
                            <thead>
                                <tr>
                                    <th>${L:l(lan,'状态（确认）')}</th>
                                    <th class="text-center">${L:l(lan,'币种')}</th>
                                    <th class="text-center">${L:l(lan,'数量')}</th>
                                    <th class="text-center">${L:l(lan,"标签")}</th>
                                    <th class="borright text-center">${L:l(lan,'地址')}</th>
                                </tr>
                            </thead>
                            <tbody id="downloadRecordDetailH">
                                
                            </tbody>
                        </table>
                        <div class="pageCon" id="downloadRecordDetailH_Page"></div>
                    </div>
                </div>
            </div>
        </div>
        <jsp:include page="/common/foot.jsp" />
    </div>

<script type="text/javascript">
    function cancelOut(did,coint) {
        JuaBox.info("${L:l(lan,'确定要取消吗？')}", {
            btnFun1:function(JuaId){
                window.top.JuaBox.close(JuaId,function(){
                    confirmCancel(did,coint);
                });
            }
        });
    }

    function confirmCancel(did,coint){
        var actionUrl = vip.vipDomain + "/manage/account/downrecord/confirmCancel?did="+did+"&coint="+coint;
        vip.ajax({
            url : actionUrl , 
            dataType : "xml",
            suc : function(xml){
                console.log(xml)
                location.reload();
            },
            err : function(xml){
                JuaBox.sure($(xml).find("Des").text());
                // BwModal.alert($(xml).find("Des").text(),{width:300});
            }
        });
    }
    require(['module_asset'],function(asset){
        $(".beaSelect").on("click",function(e){
            e.stopPropagation();
            $(this).find("dl").slideToggle(100);
            $(this).find("dd").off("click")
            $(this).find("dd").on("click",function(e){
                var type = $(this).html();
                $(".select-title").html(type)
                var val = $(this).data("val");
                console.log(val)
                asset.depositRecordInit(val);
                asset.withdrawRecordInit(val);
            })
        })
        $("document,body").on("click",function(e){
            e.stopPropagation()
            var $cointSelect = $("#cointSelect")
            if(!$cointSelect.is(":hidden")){
                $cointSelect.hide()
            }
        })
        asset.cointList();
        asset.depositRecordInit();
        asset.withdrawRecordInit();
    });
</script>
<script type="text/x-tmpl" id="tmpl-downloadRecordDetailH">
        {% for (var i = 0; i <= rs.length -1; i++) { %}
            <tr class="withdraw_tr">
                <td>
                    <p class="confirm-detail clearfix">
                        {% if(rs[i].status == 0 || rs[i].status >3){ %}
                            {% if(rs[i].commandId > 0){ %}
                            <span class="orange">${L:l(lan,"打币中")}</span>
                            {% } %}
                            {% if(rs[i].commandId == 0){ %}
                            <span class="orange">${L:l(lan,"待处理") }</span>
                            {% } %}
                        {% } %}
                        {% if(rs[i].status == 1){ %}<span class="red">${L:l(lan,"失败")}</span>{% } %}
                        {% if(rs[i].status == 2){ %}<span class="green">${L:l(lan,"成功")}</span>{% } %}
                        {% if(rs[i].status == 3){ %}<span class="gray">${L:l(lan,"已取消")}</span>{% } %}
                        {% if(rs[i].status <= 0 && rs[i].commandId <=0){ %}<a class="btn-cancel" href="javascript:cancelOut('{%=rs[i].id%}','{%=rs[i].coinName%}')">${L:l(lan,"取消")}</a>{% } %}						
                    </p>
                    {%=rs[i].recentTime%}
                </td>
                <td class="text-center">{%=rs[i].coinName%}</td>
                <td class="text-center">{%=rs[i].amount%}</td>
                <td class="text-center width120"> {%=rs[i].addressMemo%} </td>
                <td class="{%=rs[i].txId == 0?'pad10':''%} ">
                    <span class="address_span">{%=rs[i].toAddress%}</span>
                    {% if(rs[i].memo != "--"){ %}<span>${L:l(lan,"备注_1")}: {%=rs[i].memo%}</span>{% } %}
                    {% if(rs[i].txId != 0){ %}
                      <p class="txid-detail">
                        <span class="txid">Txid: <a class="btn-check" href="{%=rs[i].webUrl%}" title="{%=rs[i].txId%}" target="_blank">{%=rs[i].txId%}</a></span>
                      </p>
                    {% } %}
                </td>
            </tr>
        {% } %}
  </script>
  <script type="text/x-tmpl" id="tmpl-chargeRecordDetailH">
    {% for (var i = 0; i <= rs.length -1; i++) { %}
          <tr class="bk_payInOut_tr">
              <td>
                <p class="confirm-detail {%=rs[i].status==0? 'orange':''%} {%=rs[i].status==1? 'red':''%}">
                    {%=rs[i].showStatus%}
                    {% if(rs[i].status == 0){ %}
                        <span>（{%=rs[i].confirmTimes%}/{%=rs[i].totalConfirmTimes%}）</span>
                    {% } %}
                </p>
                {%=rs[i].recentTime%}
              </td>
              <td class="text-center">{%=rs[i].coinName%}</td>
              <td class="text-center">{%=rs[i].amount%}</td>
              <td>
                  {%=rs[i].toAddress%}
                  <p class="txid-detail">
                    <span class="txid">Txid: <a  href="{%=rs[i].webUrl%}" class="btn-check" title="{%=rs[i].txId%}" target="_blank">{%=rs[i].txId%}</a></span>
                  </p>				
              </td>
          </tr>
    {% } %}
  </script>



</body>
</html>