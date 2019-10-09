<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <jsp:include page="/admins/top.jsp"/>
    <style type="text/css">

        .form-tit {
            float: left;
            line-height: 32px;
            width: 100px;
            padding-right: 10px;
            text-align: right;
        }

        .form-con {
            float: left;
        }

        .form-line {
            margin-left: 20px;
        }

        .col-main .prompt.b_yellow.remind2.pl_35 {
            padding: 0 10px 20px 10px;
        }

        .form-btn {
            padding: 15px 0 0 100px;
        }

        .select_wrap select {
            color: #6D6D6D;
            float: left;

            padding: 5px;
        }

        .bankbox {
            padding: 15px;
        }

        .bankbox .bd {
            padding-right: 20px;
            padding-left: 20px;
        }

        .formlist .formline {
            overflow: hidden;
            padding-bottom: 8px;
            clear: both;
        }

        .formlist .formtxt {
            float: left;
            line-height: 24px;
            text-align: right;
            width: 130px;
        }

        .formlist .formcon {
            overflow: hidden;
            font-weight: bold;
            font-size: 12px;
        }

        .formlist span.tips {
            color: #666666;
            float: left;
            line-height: 24px;
            white-space: nowrap;
            font-size: 12px;
            font-weight: normal;
        }

        .formbtn {
            height: 44px;
            clear: both;
            padding: 15px 0;
        }

        .formbtn a:hover {

            background-position: 0 -33px;

        }

        select {
            display: inline;
        }

        .form-btn {
            padding-left: 96px;
        }

        .form-line {
            line-height: 30px;

            margin-bottom: 10px;
        }

        .form-line {
            overflow: hidden;
            clear: none;
            float: none;
        }

        .form-con {
            float: none;
            overflow: hidden;
            zoom: 1;
        }

        .form-con font {
            font-family: inherit;
            font-weight: bold;
            font-size: 16px;
        }

        span.txt {
            float: left;
            margin-right: 5px;
        }

        .wrong {
            color: red;
        }

        .right{
            color: green;
        }
    </style>

    <script type="text/javascript">
        $(function () {
            $("#bankBox").Ui();
        });

        function ok() {
            var actionUrl = "/admin/financial/settlement/domemo";
            vip.ajax({
                formId: "bankBox",
                url: actionUrl,
                div: "bankBox",
                suc: function (xml) {
                    parent.Right($(xml).find("MainData").text(), {
                        callback: "reload2()"
                    });
                }
            });
        }
    </script>
</head>

<body>
<div class="bankbox" id="bankBox">
    <c:set var="numFormat" value="${entity.coinType == 1 ? '#,##0.00' : '#,##0.0########'}"/>
    <div class="bankbox_bd">
        <div class="form-line">
            <div class="form-tit">币种：</div>
            <div class="form-con">
                ${coinTypeMap[entity.coinType]} 账单[<fmt:formatDate value="${entity.startTime}" pattern="yyMMddHHmm"/> -
                <fmt:formatDate value="${entity.endTime}" pattern="yyMMddHHmm"/>]
            </div>
        </div>

        <div class="form-line">
            <div class="form-tit">充值对比：</div>
            <div class="form-con">
                ${entity.totalCharge}(bitglobal) ： ${entity.totalChargeMer}(商户平台) => ${entity.totalCharge == entity.totalChargeMer ? "<span class='right'>平衡</span>" : "<span class='wrong'>不平衡</span>"}
            </div>
        </div>

        <div class="form-line">
            <div class="form-tit">提现对比：</div>
            <div class="form-con">
                ${entity.totalWithdraw}(bitglobal) ： ${entity.totalWithdrawMer}(商户平台) => ${entity.totalWithdraw == entity.totalWithdrawMer ? "<span class='right'>平衡</span>" : "<span class='wrong'>不平衡</span>"}
            </div>
        </div>

        <div class="form-line">
            <div class="form-tit">结算信息：</div>
            <div class="form-con">
                <fmt:formatNumber value="${entity.totalCharge }" pattern="${numFormat}"/>
                -
                <fmt:formatNumber value="${entity.totalWithdraw }" pattern="${numFormat}"/>
                =
                <fmt:formatNumber value="${entity.ftotalBalance }" pattern="${numFormat}"/>
                -
                <fmt:formatNumber value="${entity.prevFtotalBalance }" pattern="${numFormat}"/>
            </div>
        </div>

        <div class="form-line">
            <div class="form-tit">系统营收：</div>
            <div class="form-con" style="color: red;">
                <c:set var="total1" value="${entity.totalCharge - entity.totalWithdraw + entity.totalFees}"></c:set>
                ${total1 > 0 ? "+" : ""}
                <fmt:formatNumber value="${total1}" pattern="${numFormat}"/>
               <%-- =
                <c:set var="total2" value="${entity.ftotalBalance - entity.prevFtotalBalance}"></c:set>
                ${total2 > 0 ? "+" : ""}
                <fmt:formatNumber value="${total2}" pattern="${numFormat}"/>--%>
            </div>
        </div>

        <div class="form-line">
            <div class="form-tit">审核人员：</div>
            <div class="form-con">
                ${entity.userName }
            </div>
        </div>

        <div class="form-line">
            <div class="form-tit">审核结果：</div>
            <div class="form-con">
                ${entity.status}
            </div>
        </div>

        <div class="form-line">
            <div class="form-tit">备注：</div>
            <div class="form-con">
                <textarea rows="3" cols="50" id="memo" errormsg="请正确填写备注信息" mytitle="请正确填写备注信息" name="memo"
                          pattern="limit(0,500)">${entity.memo}</textarea>
            </div>
        </div>

        <div class="form-btn">
            <input type="hidden" name="id" value="${entity.id }"/>
            <a class="btn" href="javascript:ok();"><span class="cont">确定</span></a>
            <a href="javascript:parent.Close();" class="btn btn-gray"><span class="cont">取消</span></a>
        </div>
    </div>
</div>

</body>
</html>
