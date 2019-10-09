<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<style>
    .green{
        color:green !important;
    }

    .red{
        color:red !important;;
    }
</style>
<div class="tablelist buytable">
    <h3 style="color:#D00;">买入委托信息</h3>
    <table class="tb-list2">
        <thead>
        <tr class="buytr">
            <th>用户ID</th>
            <th>用户类型</th>
            <th>买入</th>
            <th>买入均价(￥)</th>
            <th>委单量(${unit})</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${buyList}" var="entity" varStatus="sta">
            <tr>
                <td>
                <c:choose>
                    <c:when test="${entity.userType == '-'}">
                        ${entity.userId}
                    </c:when>
                    <c:otherwise>
                        <font color="blue">${entity.userId}</font>
                    </c:otherwise>
                </c:choose>
                </td>
                <td>
                    ${entity.userType}
                </td>
                <td>买(${sta.index+1})</td>
                <td><fmt:formatNumber value="${entity.avgPrice }" pattern="0.000000##"/><br/></td>
                <td><fmt:formatNumber value="${entity.numbers }" pattern="0.000#####"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<div class="tablelist selltable">
    <h3 class="green">卖出委托信息</h3>
    <table class="tb-list2">
        <thead>
        <tr class="selltr">
            <th>用户ID</th>
            <th>用户类型</th>
            <th>卖出</th>
            <th>卖出价(￥)</th>
            <th>委单量(฿)</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${sellList}" var="entity" varStatus="sta">
            <tr>
                <td>
                    <c:choose>
                        <c:when test="${entity.userType == '-'}">
                            ${entity.userId}
                        </c:when>
                        <c:otherwise>
                            <font color="blue">${entity.userId}</font>
                        </c:otherwise>
                    </c:choose>
                    <%--<a href="javascript:showUser('${entity.userId}')" style="font-weight: bold;" class="green">${entity.userName}</a>--%>
                </td>
                <td>
                    ${entity.userType}
                </td>
                <td>卖(${sta.index+1})</td>
                <td><fmt:formatNumber value="${entity.avgPrice }" pattern="0.000000##"/><br/></td>
                <td><fmt:formatNumber value="${entity.numbers }" pattern="0.000#####"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<div class="tablelist selltable">
    <h3>最新成交</h3>
    <table class="tb-list2">
        <thead>
        <tr>
            <th>交易时间</th>
            <th>成交价格(￥)</th>
            <th>成交量(${unit})</th>
            <th>买方用户ID</th>
            <th>卖方用户ID</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${tradeList}" var="entity">
            <tr>
                <td><fmt:formatDate value="${entity.timeDate}" pattern="HH:mm:ss"></fmt:formatDate></td>
                <td><span class="${entity.isBuy == 1 ? 'red' : 'green'}"><fmt:formatNumber value="${entity.unitPrice}" pattern="0.000000#"/></span></td>
                <td><fmt:formatNumber value="${entity.numbers}" pattern="0.000##"/></td>
                <td>
                    ${entity.userIdBuy}
                    <%--<a href="javascript:showUser('${entity.userIdBuy}')"--%>
                       <%--style="font-weight: bold;" class="red">${entity.userNameBuy}</a>--%>
                </td>
                <td>
                    ${entity.userIdSell}
                    <%--<a href="javascript:showUser('${entity.userIdSell}')"--%>
                       <%--style="font-weight: bold;" class="green">${entity.userNameSell}</a>--%>
                </td>
            </tr>
        </c:forEach>

        </tbody>
    </table>
</div>