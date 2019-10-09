<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<table class="tb-list2" id="ListTable">
    <tr>
        <th style="text-align: center;">活动来源</th>
        <th style="text-align: center;">网站来源</th>
        <th style="text-align: center;">数量</th>
    </tr>
    <tbody>
        <c:forEach var="utmMap" items="${utmMap }">
            <tr class="item_list_bd item_list_bgcolor">
                <td>${fn:substringBefore(utmMap.key, "_")}</td>
                <td>${fn:substringAfter(utmMap.key, "_") }</td>
                <td>${utmMap.value }</td>
            </tr>
        </c:forEach>
    </tbody>
</table>