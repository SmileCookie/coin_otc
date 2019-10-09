<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>账户管理</title>
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

        .page_nav {
            margin-top: 10px;
        }

        .form-search .formline {
            float: left;
        }

        .form-search p {
            float: none;
        }

        .operation {
            height: 20px;
            line-height: 20px;
            text-align: left;
            margin-top: 10px;
            padding-left: 10px;
        }

        tbody.operations td {
            padding: 0;
            border: 0 none;
        }

        tbody.operations td label.checkbox {
            margin-top: 10px;
            width: 55px;
        }

        .error-bg {
            background-color: #FFDADA;
        }
    </style>
</head>
<body>
<div class="mains">
	<%-- <jsp:include page="/admins/topTab.jsp" /> --%>
	<div class="tab_head" id="topTab" style="margin-top: 10px;">
		<c:forEach items="${coinMap}" var="coin">
			<c:if test="${coin.value.coin }">
				<a href="?currency=${coin.key }" id="${coin.value.propTag }" class="${currency==coin.key?'current':'' }"><span>${coin.value.propTag }</span></a>
			</c:if>
		</c:forEach><%-- 
		<a href="?coint=btc" id="BTC" class="${coint.stag=='btc'?'current':'' }"><span>比特币</span></a>
		<a href="?coint=eth" id="ETH" class="${coint.stag=='eth'?'current':'' }"><span>以太坊</span></a>
		<a href="?coint=etc" id="ETC" class="${coint.stag=='etc'?'current':'' }"><span>以太坊经典</span></a> --%>
	</div>
    <div class="col-main">
        <div class="form-search">
            <form autocomplete="off" name="searchForm" id="searchContaint">
                <div class="form-search" id="searchContaint">
                    <div class="formline">
                        <span style="float:left;" class="formtit">结算时间：</span>
						<span style="float:left;margin: 1px 20px 0 0px;" class="formcon">
							<input class="text" type="text" value="" id="startTime" name="startTime"
                                   onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" id="startTime"/>
							-
							<input class="text" type="text" value="" name="endTime"
                                   onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" id="endTime"/>
						</span>

                        <span style="float:left;" class="formtit">商户平台流水号：</span>
                        <span style="float:left;margin: 1px 20px 0 0px;" class="formcon">
							<input class="text" type="text" value="" name="orderNo"/>
						</span>

                        <span style="float:left;" class="formtit">商户平台ID：</span>
                        <span style="float:left;margin: 1px 20px 0 0px;" class="formcon">
							<input class="text" type="text" value="" name="tradId"/>
						</span>

                        <span style="float:left;" class="formtit">是否异常：</span>
                        <span style="float:left;margin: 1px 20px 0 0px;" class="formcon">
							<select name="unusually">
                                <option value="">全部</option>
                                <option value="1">是</option>
                                <option value="0">否</option>
                            </select>
						</span>

                        <p>
                        	<input type="hidden" name="currency" id="currency" value="${currency}"  /> 
                            <a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a>
                            <a class="search-submit"
                               href="javascript:compareNow();">刷新数据</a>
                        </p>
                    </div>

                </div>

            </form>
            <div class="tab_head" id="userTab">
                <a href="javascript:vip.list.search({tab : 'all'});" id="all" class="current" data-coinType="" data-isIn=""><span>所有</span></a>
                <a href="javascript:vip.list.search({tab : 'charge'});" id="charge" data-isIn="1"><span>充值</span></a>
                <a href="javascript:vip.list.search({tab : 'withdraw'});" id="withdraw" data-isIn="0"><span>提现</span></a>
            </div>
        </div>

        <div class="tab-body" id="shopslist">
            <jsp:include page="ajax.jsp"/>
        </div>
    </div>
</div>

<script type="text/javascript">
    function initTableRow(){
        $(".item_list").each(function (i) {
            $(this).mouseover(function () {
                $(this).css("background", "#fff8e1");
            }).mouseout(function () {
                $(this).css("background", "#ffffff");
            });
        });
    }

    function compareNow(){
        var $currTab = $("#userTab a.current");
        var otherParam = "&coinType=${coint.stag}" +
                        "&isIn=" + $currTab.attr('data-isIn') +
                        "&startTime=" + $('#startTime').val() +
                        "&endTime=" + $('#endTime').val();
        vip.list.aoru({height:300,title:'刷新数据', otherParam: otherParam});
    }

    $(function () {
        vip.list.ui();
        vip.list.funcName = "公司账户";
        vip.list.basePath = "/admin/financial/settlementdetail/";
        vip.list.pageInit = function () {
            initTableRow();
        };

        initTableRow();
    });

    function reload2() {
        Close();
        vip.list.reload();
    }
</script>

</body>
</html>
