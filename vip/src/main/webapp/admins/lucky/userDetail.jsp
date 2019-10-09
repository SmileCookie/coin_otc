<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<style type="text/css">
    .btn2 a:hover {
        background: none repeat scroll 0 0 #6DC03C;
        color: #FFFFFF;
    }
    .btn2 a {
        background: none repeat scroll 0 0 #8DC03C;
        border-radius: 3px 3px 3px 3px;
        color: #FFFFFF;
        height: 31px;
        line-height: 32px;
        padding: 5px 10px;
    }
    .btn2 a.red {
        background: none repeat scroll 0 0 #E55E48;
    }
    .btn2 a.red:hover {
        background: none repeat scroll 0 0 #C55E48;
    }
    td{
        border:1px solid #ccc;
    }
</style>
<table class="tb-list2" style="width:100%;table-layout: fixed;">
    <thead>
    <tr>
        <th width="50px">用户ID</th>
        <th width="100px">抽奖时间</th>
        <th width="100px">获得奖励</th>
        <th width="100px">是否到账</th>
    </tr>
    </thead>
    <c:choose>
        <c:when test="${luckyQualifys!=null}">
            <tbody>
            <tr class="space">
                <td colspan="4">
                </td>
            </tr>
            </tbody>
            <c:forEach items="${luckyQualifys}" var="list" varStatus="statu">
                <tbody class="item_list" >
                <tr>
                    <td align="center">
                            ${list.userId}
                    </td>
                    <td align="center">
                        <fmt:formatDate value="${list.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                    </td>
                    <td align="center">
                        <font><fmt:formatNumber value="${list.occurAmount}" pattern="0.000####"/></font>
                    </td>
                    <td align="center">
                        是
                    </td>
                </tr>
                </tbody>
            </c:forEach>
        </c:when>
    </c:choose>

</table>