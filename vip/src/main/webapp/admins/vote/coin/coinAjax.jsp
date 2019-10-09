<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" id="ListTable" style="width: 100%">
    <tr>
        <th >选项名称</th>
        <th >币种全称</th>
        <th >链接</th>

    </tr>
    <c:choose>
        <c:when test="${coinList!=null}">
            <c:forEach items="${coinList}" var="list" varStatus="statu">
                <tbody>
                <tr class="item_list_bd item_list_bgcolor">
                    <td>${list.coinNameJson}</td>
                    <td>${list.coinFullNameJson }</td>
                    <td><a target="_Blank" href="${list.urlJson}">${list.urlJson}</a></td>
                </tr>
                </tbody>
            </c:forEach>
            <tbody class="operations">
            <tr>
                <td colspan="9">
                    <div id="page_navA" class="page_nav">
                        <div class="con">
                            <c:if test="${pager==''}">共${fn:length(coinList)}项</c:if>
                            <c:if test="${pager!=null}">${pager}</c:if>
                        </div>
                    </div>
                </td>
            </tr>
            </tbody>
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