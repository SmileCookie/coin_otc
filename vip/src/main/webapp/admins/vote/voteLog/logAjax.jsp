<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" id="ListTable" style="width: 100%">
    <tr>
        <th >序号</th>
        <th >用户ID</th>
        <th >登陆IP</th>
        <th >投票时间</th>
        <th >选项</th>
    </tr>
    <c:choose>
        <c:when test="${activityLogVoList!=null}">
            <c:forEach items="${activityLogVoList}" var="list" varStatus="statu">
                <tbody id="row${1}">
                <tr class="item_list_bd item_list_bgcolor">
                    <td>${statu.index + 1}</td>
                    <td>${list.userId}</td>
                    <td>${list.voteIp}</td>
                    <td><fmt:formatDate value="${list.voteTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td>${list.voteName }</td>
                </tr>
                </tbody>
            </c:forEach>
            <tbody class="operations">
            <tr>
                <td colspan="9">
                    <div id="page_navA" class="page_nav">
                        <div class="con">
                            <c:if test="${pager==''}">共${fn:length(activityLogVoList)}项</c:if>
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