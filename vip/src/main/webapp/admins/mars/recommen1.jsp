<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title></title>
    <jsp:include page="/admins/top.jsp" />
    <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>
    <script type="text/javascript">
        $(function(){
            $("#add_or_update").Ui();
        });

        function ok(){
            var actionUrl = "/admin/mars/saveMars";
            vip.ajax( {
                formId : "add_or_update",
                url : actionUrl,
                div : "add_or_update",
                suc : function(xml) {
                    parent.Right($(xml).find("MainData").text(), {callback:"reload2()"});
                }
            });
        }

    </script>
    <style type="text/css">
        .form-tit{ width:100px;float:left;}
        input{padding:8px 5px;}
        .main-bd {
            padding: 30px;
        }
    </style>
</head>

<body>
<div id="add_or_update" class="main-bd">

    <div class="form-line">
        <div class="form-tit">交易日期：</div>
        <div class="form-con">
            <input class="txt Wdate" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd ',lang : '${lan}'})" type="text"
                   name="transDate" id="transDate" mytitle="请选择活动的开始时间"
                   value="<fmt:formatDate value='${user.transDate }' pattern='yyyy-MM-dd '/>"/>
        </div>
    </div>

    <div class="form-line">
        <div class="form-tit">摘要：</div>
        <div class="form-con">
            <input class="txt" type="text" style="width: 200px;" name="summary" id="summary" value="${user.summary}" pattern="limit(0, 200)"/>
        </div>
    </div>


    <div class="form-line">
        <div class="form-tit">币种类型：</div>
        <div class="form-con">
            <select name="fundsType" id="fundsType">
                <option value="">--请选择--</option>
                <c:forEach var="ft" items="${ft }">
                    <option id="fundT_${ft.value.fundsType }" value="${ft.value.fundsType }" <c:if test="${ft.value.fundsType==user.fundsType }">selected="selected"</c:if>>${ft.value.propTag }</option>
                </c:forEach>
            </select>
        </div>
    </div>

    <div class="form-line">
        <div class="form-tit">收入：</div>
        <div class="form-con">
            <input class="txt" type="text" style="width: 200px;" name="income" id="income" value="${user.income}"/>
        </div>
    </div>

    <div class="form-line">
        <div class="form-tit">支出：</div>
        <div class="form-con">
            <input class="txt" type="text" style="width: 200px;" name="expense" id="expense" value="${user.expense}"/>
        </div>
    </div>


    <div class="form-line">
        <div class="form-tit">变动位置：</div>
        <div class="form-con">
            <select name="changePosition" id="changePosition">
                <option value="">-请选择-</option>
                <option value="1" <c:if test="${user.changePosition == '1'}">selected</c:if> >平台</option>
                <option value="2" <c:if test="${user.changePosition == '2'}">selected</c:if> >钱包</option>
            </select>
        </div>
    </div>

    <div class="form-line">
        <div class="form-tit">变动类型：</div>
        <div class="form-con">
            <select name="changeType" id="changeType">
                <option value="">-请选择-</option>
                <option value="1" <c:if test="${user.changeType == '1'}">selected</c:if> >冷到其他</option>
                <option value="2" <c:if test="${user.changeType == '2'}">selected</c:if> >其他到热提</option>
                <option value="3" <c:if test="${user.changeType == '3'}">selected</c:if> >后台充值</option>
                <option value="4" <c:if test="${user.changeType == '4'}">selected</c:if> >其他到冷</option>
                <option value="5" <c:if test="${user.changeType == '5'}">selected</c:if> >资金划转</option>
                <option value="6" <c:if test="${user.changeType == '6'}">selected</c:if> >后台扣除</option>
                <option value="7" <c:if test="${user.changeType == '7'}">selected</c:if> >用户充值</option>
                <option value="8" <c:if test="${user.changeType == '8'}">selected</c:if> >用户提现</option>
                <option value="9" <c:if test="${user.changeType == '9'}">selected</c:if> >热提到其他</option>
                <option value="10" <c:if test="${user.changeType == '10'}">selected</c:if> >系统分发</option>
                <option value="11" <c:if test="${user.changeType == '11'}">selected</c:if> >ABCDEF发行</option>
                <option value="12" <c:if test="${user.changeType == '12'}">selected</c:if> >兑换ABCDEF</option>
                <option value="13" <c:if test="${user.changeType == '13'}">selected</c:if> >其他</option>
            </select>
        </div>
    </div>
    <div class="form-line">
        <div class="form-tit">公司资金变动类型：</div>
        <div class="form-con">
            <select name="companyChangeType" id="companyChangeType">
                <option value="">-请选择-</option>
                <option value="1" <c:if test="${user.accountingType == '1'}">selected</c:if> >公司资金减少（T)</option>
                <option value="2" <c:if test="${user.accountingType == '2'}">selected</c:if> >公司资金增加（T）</option>
                <option value="3" <c:if test="${user.accountingType == '3'}">selected</c:if> >公司资金减少（F）</option>
                <option value="4" <c:if test="${user.accountingType == '4'}">selected</c:if> >公司资金增加（F）</option>
                <option value="5" <c:if test="${user.accountingType == '5'}">selected</c:if> >不影响公司资金</option>
            </select>
        </div>
    </div>
    <div class="form-line">
        <div class="form-tit">统计类别：</div>
        <div class="form-con">
            <select name="accountingType" id="accountingType">
                <option value="">-请选择-</option>
                <option value="1" <c:if test="${user.accountingType == '1'}">selected</c:if> >资金减少（T)</option>
                <option value="2" <c:if test="${user.accountingType == '2'}">selected</c:if> >资金增加（T）</option>
                <option value="3" <c:if test="${user.accountingType == '3'}">selected</c:if> >资金减少（F）</option>
                <option value="4" <c:if test="${user.accountingType == '4'}">selected</c:if> >资金增加（F）</option>
                <option value="5" <c:if test="${user.accountingType == '5'}">selected</c:if> >不影响本表</option>
            </select>
        </div>
    </div>

    <div class="form-line">
        <div class="form-tit">备注：</div>
        <div class="form-con">
            <input class="txt" type="text" style="width: 200px;" name="comment" id="comment" value="${user.comment}"/>
        </div>
    </div>






    <div class="form-btn" style="padding-top: 55px">
        <input type="hidden" id="id" name="id" value="${user.id}" />

        <a class="btn" href="javascript:ok();"><span class="cont">确定</span></a>
        <a href="javascript:parent.Close();" class="btn btn-gray"><span class="cont">取消</span></a>
    </div>
</div>
</body>
</html>
