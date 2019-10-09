<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>提现异常处理</title>
    <jsp:include page="/admins/top.jsp" />
    <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>

    <script type="text/javascript">
        var conitName = "BTC"; //默认为BTC
        $(function(){
            vip.list.ui();
            vip.list.basePath = "/admin/btc/downloadexception/";
        });

        function reload2() {
            Close();
            vip.list.reload();
        }

    </script>
    <style type="text/css">
        .commodity_action a{padding: 0 5px;}
        label.checkbox{  margin: 3px 6px 0 7px;}
        label.checkbox em{ padding-left:18px; line-height:15px; float:left; font-style:normal;}
        .form-search .formline{float:left;}
        .form-search p{float:none;}
        tbody.operations  td{ padding:0; border:0 none;}
        tbody.operations  td label.checkbox{ margin-top:10px; width:55px;}

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

        .tab_head .search-submit.red{
            background: none repeat scroll 0 0 #db2222;
            border: 1px solid #c11010;
        }

    </style>
</head>
<body>
<div class="mains">
    <div class="col-main">
        <div class="form-search" id="listSearch">
            <form autocomplete="off" name="searchForm" id="searchContaint">
                <p class="formCloumn">
                    <span>资金类型：</span>
                    <select name="coint" id="coint">
                        <c:forEach var="ft" items="${ft}">
                            <option value="${ft.value.databaseKey}">${ft.value.propTag}</option>
                        </c:forEach>
                    </select>
                    <input type="hidden" id="cointName" name="conitName"/>
                </p>
                <p class="formCloumn">
                    <span class="formText">
                        提现编号：
                    </span>
                    <span class="formContainer">
                        <input type="text" id="uuid" name="uuid" style="width:200px;"/>
                    </span>
                </p>
                <p>
                    <a class="search-submit" id="idSearch" href="javascript:vip.list.search({coint1:conitName});">查找</a>
                </p>
            </form>
        </div>
        <div class="tab-body" id="shopslist">
            <jsp:include page="ajax.jsp" />
        </div>

    </div>
</div>
</body>
</html>

