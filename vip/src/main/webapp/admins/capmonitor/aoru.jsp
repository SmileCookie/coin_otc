<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <jsp:include page="/admins/top.jsp"/>

    <script type="text/javascript">
        $(function () {
            $("#add_or_update").Ui();
        });
        //处理备注
        dealMonRemark = function(obj) {
            $(obj).parents("td").prev().find("textarea").attr("disabled",false).focus();
        }

        //保存备注
        saveMonRemark = function(obj,id) {
            var remark = $(obj).parents("td").prev().find("textarea").val();
            $(obj).parents("td").prev().find("textarea").attr("disabled",true);
            var actionUrl = "/admin/capmonitor/saveMonRemark?id=" + id+ "&dealreamark=" + remark;
            vip.ajax({
                url : actionUrl,
                dataType : "json",
                suc : function(json) {
                    Right(json.des);
                },
                err : function(json){
                    Right(json.des);
                }
            });
        }
    </script>
</head>
<body>
<div id="add_or_update" class="main-bd">

    <table class="tb-list2" id="ListTable" style="width: 100%"> 
        <tr> 
            <th>序号</th>
            <th>资金类型</th>
            <th>检查时点余额</th>
            <th>流水合计金额</th>
            <th>账单号</th>
            <th>处理备注</th>
            <th>操作</th>
        </tr>
        <c:choose>
            <c:when test="${dataList!=null}">
                <c:forEach items="${dataList}" var="list" varStatus="statu">
                    <tbody id="row${list.id }">
                    <tr class="item_list_bd item_list_bgcolor">
                        <td>${statu.index + 1}</td>
                        <td>
                            ${list.coinName }
                        </td>
                        <td>${list.checkBillAmount }</td>
                        <td>${list.billTotalAmount }</td>
                        <td>
                                ${list.billId }
                        </td>
                        <td>
                            <textarea style="width:100%; " disabled>${list.dealRemark}</textarea>
                        </td>
                        <td>
                            <a href="#" onclick="dealMonRemark(this)">处理</a>
                            <a href="#" onclick="saveMonRemark(this,'${list.id}')">保存</a>
                        </td>
                    </tr>
                    </tbody>
                </c:forEach>
                <%--<tbody class="operations">--%>
                    <%--<tr>--%>
                        <%--<td colspan="6">--%>
                            <%--<div id="page_navA" class="page_nav">--%>
                                <%--<div class="con">--%>
                                    <%--<c:if test="${pager==''}">共${fn:length(dataList)}项</c:if>--%>
                                    <%--<c:if test="${pager!=null}">${pager}</c:if>--%>
                                <%--</div>--%>
                            <%--</div>--%>
                        <%--</td>--%>
                    <%--</tr>--%>
                <%--</tbody>--%>
            </c:when>
            <c:otherwise>
                <tbody class="air-tips">
                <tr>
                    <td colspan="9">
                        <p>暂时没有符合要求的记录！</p>
                    </td>
                </tr>
                </tbody>
            </c:otherwise>
        </c:choose>
    </table>
</div>
</body>
</html>
