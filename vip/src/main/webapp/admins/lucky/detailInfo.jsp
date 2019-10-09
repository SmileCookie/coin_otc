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
</style>
<table class="tb-list2" style="width:100%;table-layout: fixed;">
<thead>
<tr>
    <th>序号</th>
    <th>用户编号</th>
    <th>抽奖次数</th>
    <th>获得奖励</th>
</tr>
</thead>
<c:choose>
    <c:when test="${luckyQualifys!=null}">
        <tbody>
        <tr class="space">
            <td colspan="5">
            </td>
        </tr>
        </tbody>

        <c:forEach items="${luckyQualifys}" var="list" varStatus="statu">
            <tbody class="item_list">
            <tr>
                <td>
                        ${statu.index + 1}
                </td>
                <td>
                        ${list.userId}
                </td>
                <td>
                        <a href="javascript:showDetail('${luckyEvent.luckyId}','${list.userId}')">${list.userCount}</a>
                </td>
                <td>
                    <font><fmt:formatNumber value="${list.occurAmount}" pattern="0.000####"/></font>
                </td>
            </tr>
            </tbody>
        </c:forEach>
        <tfoot>
        <tr>
            <td colspan="4">
                <div class="page_nav" id="pagin">
                    <div class="con">
                        <c:if test="${pager==''}">共${itemCount}项</c:if>
                        <c:if test="${pager!=null}">${pager}</c:if>
                    </div>
                </div>
            </td>
        </tr>
        </tfoot>
    </c:when>
    <c:otherwise>
        <tbody class="air-tips">
        <tr>
            <td colspan="5">
                <p>没有符合要求的记录！</p>
            </td>
        </tr>
        </tbody>
    </c:otherwise>
</c:choose>

</table>