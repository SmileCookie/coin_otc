<%@ page import="com.world.web.action.Action" %>
<%@ page import="java.util.Date" %>
<%@ page session="false" language="java"
         pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!doctype html>
<html>
<head>
    <title>btcwinex</title>
    <%--<jsp:include page="/common/head.jsp"/>--%>
    <%!
        private static long CH_VERSON = 0;
    %>
    <%
        String static_domain= Action.STATIC_DOMAIN;
        if(CH_VERSON==0){
            CH_VERSON = new Date().getTime();
        }
        request.setAttribute("CH_VERSON", CH_VERSON);
    %>
    <link rel=stylesheet type=text/css href="${static_domain }/statics/css/cnbtc2014.css?V${CH_VERSON }" />
    <link rel="stylesheet" href="${static_domain }/statics/css/v2/module.base.css?V${CH_VERSON }">
    <link rel="stylesheet" href="${static_domain }/statics/css/v2/module.common.css?V${CH_VERSON }">
    <link rel="stylesheet" href="${static_domain }/statics/css/v2/module.user.css?V${CH_VERSON }">
    <script type="text/javascript" src="${static_domain }/statics/js/admin/jquery.js"></script>
    <style>
        .form-line {
            height: 50px;
            margin: 20px auto;
        }

        .form-line .textright {
            text-align: right;
            font-size: 14px;
            line-height: 46px;
            padding: 0px;
            color: #666;
        }

        .btnclose {
            background: #ccc;
            color: #FFF;
        }

        .btnclose:hover {
            background: #ddd;
        }

        .btn-ok {
            background: #20b0de;
            border-color: #20b0de;
            color: #FFF;
        }

        .btn-ok:hover {
            background: #0093c1;
        }

        .btn-ok, .btnclose {
            font-size: 16px;
            width: 120px;
            height: 40px;
            padding: 0px;
            line-height: 40px;
            margin: 0px 15px;
            min-width: 80px !important;
        }
    </style>
</head>
<body>
<div class="container" style="width: 500px!important; min-width:500px!important;">
    <div id="subForm" class="bk-onekey-form " style="padding: 20px 0px;">
        <form id="frmEnterprise">
            <input type="hidden" name="userId" value="${auth.userId}" />
            <div class="form-line row">
                <div class="col-xs-3 textright">${L:l(lan,'企业名称')}：</div>
                <div class="col-xs-9">
                    <input type="text"
                           class="form-control form-second pull-left inputlong ft16"
                           name="realName" id="realName" value="${auth.realName}" maxlength="120"/>
                </div>
            </div>

            <div class="form-line row">
                <div class="col-xs-3 textright">${L:l(lan,'法人')}：</div>
                <div class="col-xs-9">
                    <input type="text"
                           class="form-control form-second pull-left inputlong ft16"
                           name="legalPersonName" id="legalPersonName" value="${auth.legalPersonName}"/>
                </div>
            </div>

            <div class="form-line row">
                <div class="col-xs-3 textright">${L:l(lan,'企业注册号')}：</div>
                <div class="col-xs-9">
                    <input type="text"
                           class="form-control form-second pull-left inputlong ft16"
                           name="enterpriseRegisterNo" id="enterpriseRegisterNo" value="${auth.enterpriseRegisterNo}" />
                </div>
            </div>

            <div class="form-line row">
                <div class="col-xs-3 textright">${L:l(lan,'组织机构代码')}：</div>
                <div class="col-xs-9">
                    <input type="text"
                           class="form-control form-second pull-left inputlong ft16"
                           name="organizationCode" id="organizationCode" value="${auth.organizationCode}"/>
                </div>
            </div>

            <div class="form-line row">
                <div class="col-xs-3 textright">${L:l(lan,'注册日期')}：</div>
                <div class="col-xs-9">
                    <input type="text" data-picker="true" readonly
                           class="form-control form-second pull-left inputlong ft16"
                           name="enterpriseRegisterDate" id="enterpriseRegisterDate"
                           value="<fmt:formatDate value="${auth.enterpriseRegisterDate}" pattern="yyyy-MM-dd"/>"/>
                </div>
            </div>

            <div class="form-line row">
                <div class="col-xs-3 textright">${L:l(lan,'注册地址')}：</div>
                <div class="col-xs-9">
                    <input type="text"
                           class="form-control form-second pull-left inputlong ft16"
                           name="enterpriseRegisterAddr" id="enterpriseRegisterAddr"
                           value="${auth.enterpriseRegisterAddr}" maxlength="30" />
                </div>
            </div>

            <div class="do row">

                <a href="javascript:simpleSave()" class="btn btn-blue btn-send btn-ok ">
                    <span>√</span>&nbsp; ${L:l(lan,'保存')}
                </a>

                <a href="javascript:parent.Close();" class="btn btn-blue btn-send btn-default btnclose">
                    <span>×</span> &nbsp; ${L:l(lan,'取消')}
                </a>

            </div>
        </form>
    </div>
</div>

<script type="text/javascript">

    function simpleSave() {
        var data = $('#frmEnterprise').serialize();
        $.ajax({
            type: 'POST',
            url: '/admin/user/authen/enterpriseSave',
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
