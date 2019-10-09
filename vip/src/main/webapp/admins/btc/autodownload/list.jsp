<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>小额自动打币记录</title>
    <jsp:include page="/admins/top.jsp"/>
    <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js"></script>

    <style type="text/css">
        label.checkbox {
            margin: 3px 6px 0 7px;
        }

        label.checkbox em {
            padding-left: 18px;
            line-height: 15px;
            float: left;
            font-style: normal;
        }

        .form-search .formline {
            float: left;
        }

        .form-search p {
            float: none;
        }

        tbody.operations td {
            padding: 0;
            border: 0 none;
        }

        tbody.operations td label.checkbox {
            margin-top: 10px;
            width: 55px;
        }

        .tab_head .search-submit, .tab_head .search-submit:hover {
            background: none repeat scroll 0 0 #2f7fc2;
            border: 1px solid #226eac;
            color: #fff;
            display: inline;
            float: right;
            font-family: 微软雅黑;
            height: 26px;
            line-height: 26px;
            margin: 0 0 0 5px;
            text-align: center;
            text-decoration: none;
            width: 55px;
        }

        .tab_head .search-submit.red {
            background: none repeat scroll 0 0 #db2222;
            border: 1px solid #c11010;
        }
    </style>
    <script type="text/javascript">
        $(function () {
            vip.list.ui();
            vip.list.funcName = "${coint.propCnName }自动打币";
            vip.list.basePath = "/admin/btc/autodownload/";
        });

        function reload2() {
            Close();
            vip.list.reload();
        }
    </script>
</head>
<body>
<jsp:include page="/admins/top.jsp"/>
<div class="mains">
    <div class="col-main">
        <form autocomplete="off" name="searchForm" id="searchContaint">
            <div class="form-search" id="searchContaint">

                <div class="formline">
                    <span class="formtit">提交时间：</span>
                    <span class="spacing">从</span>
						<span class="formcon mr_5">
							<input type="text" class="inputW2 Wdate" id="startDate" name="startDate"
                                   onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : 'cn'})"
                                   style="width:140px;"/>
						</span>
                    <span class="spacing">到</span>
						<span class="formcon">
							<input type="text" class="inputW2 Wdate" id="endDate" name="endDate"
                                   onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : 'cn'})"
                                   style="width:140px;"/>
						</span>
                    <span class="formtit">打币时间：</span>
                    <span class="spacing">从</span>
						<span class="formcon mr_5">
							<input type="text" class="inputW2 Wdate" id="confirmStartDate" name="confirmStartDate"
                                   onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : 'cn'})"
                                   style="width:140px;"/>
						</span>
                    <span class="spacing">到</span>
						<span class="formcon">
							<input type="text" class="inputW2 Wdate" id="confirmEndDate" name="confirmEndDate"
                                   onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : 'cn'})"
                                   style="width:140px;"/>
						</span>
                    <span class="formtit">提现金额：</span>
						<span class="formcon mr_5">
							<input type="text" name="moneyMin" id="moneyMin" size="10"/>
						</span>
                    <span class="spacing">到</span>
						<span class="formcon">
							<input type="text" name="moneyMax" id="moneyMax" size="10"/>
						</span>
                </div>

                <div style="clear: both;"></div>

                <span style="float:left;" class="formtit">资金类型：</span>
                    <span style="float:left;margin: 2px 20px 0 0px;" class="formcon">
                        <select name="fundType" id="fundType" style="width:130px;">
                            <option value="">--请选择--</option>
                            <c:forEach var="ft" items="${ft }">
                                <option value="${ft.value.fundsType}">${ft.value.propCnName }</option>
                            </c:forEach>
                        </select>
                    </span>

                <div class="formline">
                    <span class="formtit" style="margin-left: 10px;">打币状态：</span>
                    <span class="formcon">
                        <select name="status" id="status" style="width:100px;">
                            <option value="">--请选择--</option>
                            <option value="confirm">已确认</option>
                            <option value="success">已成功</option>
                            <option value="fail">已失败</option>
                            <option value="sendding">发送中</option>
                        </select>
                    </span>
                    <p>
                        <input type="hidden" name="${coint.coinParam }" value="${coint.propTag }"/>
                        <a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a>
                        <a href="javascript:vip.list.resetForm();" id="idReset" class="search-submit">重置</a>
                        <%--<a class="search-submit" href="javascript:exportUser('');">导出excel</a>--%>
                    </p>
                </div>

            </div>
        </form>
        <%--<div class="tab_head" id="userTab">--%>
            <%--<a href="javascript:vip.list.search({tab : 'confirm'});" id="confirm"><span>已确认</span></a>--%>
            <%--<a href="javascript:vip.list.search({tab : 'success'});" id="success"><span>已成功</span></a>--%>
            <%--<a href="javascript:vip.list.search({tab : 'fail'});" id="fail"><span>已失败</span></a>--%>
        <%--</div>--%>
        <div class="tab-body" id="shopslist">
            <jsp:include page="ajax.jsp"/>
        </div>
    </div>
</div>
</body>
</html>
