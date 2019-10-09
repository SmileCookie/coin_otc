<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<table class="tb-list2" id="ListTable" style="width: 100%">
    <tr>
        <th>批次号</th>
        <th>打币金额</th>
        <th>提交时间</th>
        <th>打币时间</th>
        <th>唯一标志</th>
    </tr>
    <c:choose>
        <c:when test="${dataList!=null}">
            <c:forEach items="${dataList}" var="list" varStatus="statu">
                <tr>
                    <td>${list.batchId}</td>
                    <td>${list.amount}</td>
                    <td><fmt:formatDate value="${list.submitTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td><fmt:formatDate value="${list.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td>${list.downloadId}</td>
                </tr>
            </c:forEach>
            <tbody class="operations">
            <tr>
                <td colspan="5">
                    <div id="page_navA" class="page_nav">
                        <div class="con">
                            <c:if test="${pager==''}">共${fn:length(dataList)}项</c:if>
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
                <td colspan="5">
                    <p>暂时没有符合要求的记录！</p>
                </td>
            </tr>
            </tbody>
        </c:otherwise>
    </c:choose>
</table>