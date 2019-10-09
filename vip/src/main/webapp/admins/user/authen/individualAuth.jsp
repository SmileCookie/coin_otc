<%@ page import="com.world.config.GlobalConfig" %>
<%@ page import="com.world.web.action.Action" %>
<%@ page import="java.util.Date" %>
<%@ page session="false" language="java"
         pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<!doctype html>
<html>
<head>
    <title>btcwinex</title>

    <%!
        private static long CH_VERSON = 0;
    %>
    <%
        String header = GlobalConfig.session;
        String vip_domain= Action.VIP_DOMAIN;
        String static_domain=Action.STATIC_DOMAIN;
        String main_domain=Action.MAIN_DOMAIN;
        String p2p_domain=Action.P2P_DOMAIN;
        String trans_domain=Action.TRANS_DOMAIN;
//        if(CH_VERSON==0){
            CH_VERSON = new Date().getTime();
//        }
        request.setAttribute("CH_VERSON", CH_VERSON);
    %>
    <script type="text/javascript">
        var JsCommon={uon:"<%=header%>uon",uname:"<%=header%>uname",uid:"<%=header%>uid",aid:"<%=header%>aid",rid:"<%=header%>rid",vip:"<%=header%>vip",aname:"<%=header%>aname",note:"<%=header%>note",lan:"<%=header%>lan",other:"<%=header%>other",mainDomain:"<%=main_domain%>",vipDomain:"<%=vip_domain%>",p2pDomain:"<%=p2p_domain%>",transDomain:"<%=trans_domain%>",staticDomain:"<%=static_domain%>"};
    </script>

    <script type="text/javascript">
        var GLOBAL             = {},
                VERSION            = GLOBAL['VERSION']           = '${CH_VERSON }',
                ZNAME              = GLOBAL['ZNAME']              = '<%=header%>',
                DOMAIN_BASE        = GLOBAL['DOMAIN_BASE']       = '${baseDomain }',
                <%--DOMAIN_MAIN        = GLOBAL['DOMAIN_MAIN']       = '<%=main_domain%>',--%>
                <%--DOMAIN_VIP         = GLOBAL['DOMAIN_VIP']        = '<%=vip_domain%>',--%>
                DOMAIN_MAIN        = GLOBAL['DOMAIN_MAIN']       = '${main_domain}',
                DOMAIN_VIP         = GLOBAL['DOMAIN_VIP']        = '${vip_domain}',
                DOMAIN_STATIC      = GLOBAL['DOMAIN_STATIC']     = '<%=static_domain%>',
                DOMAIN_P2P         = GLOBAL['DOMAIN_P2P']        = '<%=p2p_domain%>',
                DOMAIN_TRANS       = GLOBAL['DOMAIN_TRANS']      = '<%=trans_domain%>',
                LANG               = GLOBAL['LANG']              = '${lan }',
                UON                = GLOBAL['UON']               = '<%=header%>uon',
                UID                = GLOBAL['UID']               = '<%=header%>uid',
                AID                = GLOBAL['AID']               = '<%=header%>aid',
                RID                = GLOBAL['RID']               = '<%=header%>rid',
                VIP                = GLOBAL['VIP']               = '<%=header%>vip',
                UNAME              = GLOBAL['UNAME']             = '<%=header%>uname',
                ANAME              = GLOBAL['ANAME']             = '<%=header%>aname',
                NOTE               = GLOBAL['NOTE']              = '<%=header%>note';

    </script>
    <link rel="stylesheet" href="${static_domain }/statics/css/web.base.css">
    <link rel="stylesheet" href="${static_domain }/statics/css/web.common.css">
    <script src="${static_domain }/statics/js/web.base.js" charset="UTF-8"></script>
    <script src="${static_domain }/statics/js/web.lang.js" charset="UTF-8"></script>


    <link rel="stylesheet" href="${static_domain }/statics/css/web.user.css">
    <style>
        .form-line { height:50px;   margin: 20px auto; }
        .form-line .textright { text-align: right; font-size: 14px; line-height: 46px; padding: 0px; color: #666; }
        .btnclose { background: #ccc; color:#FFF;  }
        .btnclose:hover { background:#ddd; }
        .btn-ok { background: #20b0de;  border-color: #20b0de;  color:#FFF; }
        .btn-ok:hover { background:#0093c1; }
        .btn-ok, .btnclose { font-size: 16px; width:120px; height:40px; padding: 0px;  line-height:40px; margin:0px 15px; min-width:80px!important;  }
    </style>
</head>
<body>
<div class="container" style="width: 500px!important; min-width:500px!important;">
    <div id="subForm" class="bk-onekey-form " style="padding: 20px 0px;">
        <div class="form-line row">
            <div class="col-xs-3 textright">${L:l(lan,'证件所在区域')}：</div>
            <div class="col-xs-9">
                <div class="drop-group dropdown" id="countryGroup">
                    <div class="dropdown-toggle clearfix" data-toggle="dropdown" aria-haspopup="true"
                         aria-expanded="false">
                        <input name="areaInfo" id="areaInfo" type="text"
                               placeholder="${L:l(lan,'请选择证件所在区域') }"
                               pattern="limit(1,40)" value=""
                               class="form-control form-second smallfont" readonly/>
                        <input id="areaInfoHid" type="hidden" value="${auth.areaInfo}">
                        <input id="countryCodeHid" type="hidden" value="${auth.countryCode}">
                    </div>
                    <div class="input-drop dropdown-menu" aria-labelledby="countryGroup"
                         style="max-height:300px;">
                        <ul id="areaInfoList">
                            <li data-value="+86">+86 <span>[${lan=='cn' ? '中国' : 'China'}][China]</span></li>
                            <li data-value="+852">+852 <span>[${lan=='cn' ? '香港' : 'Hongkong'}][香港]</span></li>
                            <li data-value="+853">+853 <span>[${lan=='cn' ? '澳门' : 'Macau'} ][澳門]</span></li>
                            <li data-value="+886">+886 <span>[${lan=='cn' ? '台湾' : 'Taiwan'} ][台灣]</span></li>
                            <div class="bk-divider">--------------------------</div>
                            <c:forEach items="${country}" var="coun">
                                <li data-value="${coun.code}"
                                    <c:if test="${coun.code eq auth.countryCode}">class="active"</c:if> >${coun.code}
                                    <span>[${lan=='cn' ? coun.name : coun.des}][${coun.des}]</span></li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <div class="form-line row" >
            <div class="col-xs-3 textright">${L:l(lan,'姓名')}：</div>
            <div class="col-xs-9">
                <input type="text"
                       class="form-control form-second pull-left inputlong ft16"
                       name="realName" id="realName" value="${auth.realName}" position="s"
                       mytitle="${L:l(lan,'请填写您的真实姓名,以便为您提供更好的服务')}" errormsg="${L:l(lan,'真实姓名错误')}"
                       pattern="limit(4,30)"/>
            </div>
        </div>
        <div class="form-line row">
            <div class="col-xs-3 textright">${L:l(lan,'身份证号')}：</div>
            <div class="col-xs-9">
                <input type="text"
                       class="form-control form-second pull-left inputlong ft16"
                       name="cardId" id="cardId" value="${auth.cardId}" position="s"
                       mytitle="${L:l(lan,'请填写有效的身份证号码')}" errormsg="${L:l(lan,'身份证号错误')}"
                       pattern="limit(4,18)"/>
            </div>
        </div>

        <div class="do row">

            <a href="javascript:simpleSave()" class="btn btn-blue btn-send btn-ok ">
                 <span>√</span>&nbsp; ${L:l(lan,'保存')}
            </a>

            <a href="javascript:parent.Close();" class="btn btn-blue btn-send btn-default btnclose" >
                <span>×</span> &nbsp; ${L:l(lan,'取消')}
            </a>

        </div>
    </div>
</div>


<script type="text/javascript">

    $(function () {
        $('#areaInfoList li').on('click', function () {
            var areaType = 1;
            if ($(this).data('value') == "+86") {
                areaType = 1;
            } else if ($(this).data('value') == "+852" || $(this).data('value') == "+853") {
                areaType = 2;
            } else if ($(this).data('value') == "+886") {
                areaType = 3;
            } else {
                areaType = 4;
            }
            $('#areaInfo').val($(this).find("span").text());
            $('#areaInfoHid').val(areaType);

            if (areaType != 1) {
                $("#bankAuthForm").hide();
                $("#overAuthForm").show();
            } else {
                $("#bankAuthForm").show();
                $("#overAuthForm").hide();
            }
        });

        var hidCountryCode = $('#countryCodeHid').val();
        var countryName = $('#areaInfoList').find('li[data-value="' + hidCountryCode + '"] span').html();
        $('#areaInfo').val(countryName);

        $('#areaInfoList li').on('click', function () {
            $('#countryCodeHid').val($(this).data('value'));
        });
    });

    function simpleSave() {
        var realName = $('#realName').val();
        var cardId = $('#cardId').val();
        var area = $('#areaInfoHid').val();
        var country = $('#countryCodeHid').val();
        var data = {
            userId: '${auth.userId}',
            realName: realName,
            cardId: cardId,
            area: area,
            country: country
        };
        if (!country) {
            parent.Wrong("${L:l(lan, "请选择证件所在区域")}");
            return;
        }

        $.ajax({
            type: 'POST',
            url: '/admin/user/authen/individualSave',
            data: data,
            dataType: 'json',
            success: function (json) {
                if (json.isSuc) {
                    parent.Right(json.des, {callback:"reload2()"});
                } else {
                    parent.Wrong(json.des);
                }
            },
            error: function () {
                parent.Wrong('${L:l(lan, "网络访问出错，请稍后重试")}');
            }
        });
    }
</script>
</body>
</html>
