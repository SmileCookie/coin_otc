<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>下班结算</title>
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
    </style>
</head>
<body>
<div class="mains">
    <div class="col-main">
        <div class="form-search">
            <form autocomplete="off" name="searchForm" id="searchContaint">
                <div class="form-search" id="searchContaint">
                    <div class="formline">
                   
                        <span style="float:left;" class="formtit">币种：</span>
						<span style="float:left;margin: 2px 20px 0 0px;" class="formcon">
							<select name="coinType" id="coinType" style="width:140px;">
					           	<option value="">全部</option>
								<c:forEach items="${coinMap}" var="coin">
                                    <option value="${coin.key}">${coin.value.propTag}</option>
                                </c:forEach>
					         </select>
						</span>
					
						<span id="howDay" style="display: none;">
							<span class="formtit">统计日期：</span>
							<span class="formcon mr_5">
								<input type="text" class="inputW2 Wdate" id="startDate" name="startDate"
                                       onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',lang : 'cn'})" style="width:120px;"/>
							</span>
						</span>

                        <p>
                            <a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a>
                            <a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
                            <a class="search-submit" href="javascript:location.reload(true);">刷新</a>
                        </p>
                    </div>

                </div>

            </form>
        </div>

        <div class="tab-body" id="shopslist">
            <jsp:include page="ajax.jsp"/>
        </div>
    </div>
</div>

<script type="text/javascript">
    $(function () {
        vip.list.ui();
        vip.list.funcName = "下班结算";
        vip.list.basePath = "/admin/financial/settlement/";

        $(".item_list").each(function (i) {
            $(this).mouseover(function () {
                $(this).css("background", "#fff8e1");
            }).mouseout(function () {
                $(this).css("background", "#ffffff");
            });
        });

        $("#group").change(function () {
            if ($(this).find("option:selected").val() == "day") {
                $("#howDay").show();
            } else {
                $("#howDay").hide();
            }
        });
    });

    function memo(id) {
        Iframe({
            Url: "/admin/financial/settlement/memo?id=" + id,
            zoomSpeedIn: 200,
            zoomSpeedOut: 200,
            Width: 660,
            Height: 600,
            scrolling: "no",
            isIframeAutoHeight: false,
            isShowIframeTitle: true,
            Title: "添加备注"
        });
    }


    function jiesuan(coinType) {
        Iframe({
            Url: "/admin/financial/settlement/aoru?coinType=" + coinType,
            zoomSpeedIn: 200,
            zoomSpeedOut: 200,
            Width: 660,
            Height: 560,
            scrolling: "no",
            isIframeAutoHeight: false,
            isShowIframeTitle: true,
            Title: "结算"
        });
    }

    function reload2() {
        Close();
        vip.list.reload();
    }
</script>

</body>
</html>
