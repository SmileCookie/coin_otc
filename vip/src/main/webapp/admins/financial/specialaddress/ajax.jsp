<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" id="ListTable" style="width: 100%">
    <tr>
        <th >序号</th>
        <th >类型</th>
        <th >类型描述</th>
        <th >详细类型</th>
        <th >详细类型描述</th>
        <th >字典值</th>
        <th >备注</th>
        <th >状态</th>
        <th >操作</th>
    </tr>
    <c:choose>
        <c:when test="${dataList!=null}">
            <c:forEach items="${dataList}" var="list" varStatus="statu">
                <tbody id="row${list.attrId }">
                <tr class="item_list_bd item_list_bgcolor">
                    <td>${statu.index + 1}</td>
                    <td>${list.attrType}</td>
                    <td>${list.attrTypeDesc }</td>
                    <td>${list.paraCode }</td>
                    <td>${list.paraName }</td>
                    <td>${list.paraValue }</td>
                    <td>${list.paraDesc }</td>
                    <td>${list.attrStateDesc }</td>
                    <td>
                        <a href="javascript:vip.list.aoru({id : '${list.attrId }' , width : 600 , height : 660});">修改</a>
                        <br/>
                        <a href="javascript:vip.list.del({id : '${list.attrId }'});">删除</a>
                    </td>
                </tr>
                </tbody>
            </c:forEach>
            <tbody class="operations">
            <tr>
                <td colspan="7">
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
                <td colspan="7">
                    <p>暂时没有符合要求的记录！</p>
                </td>
            </tr>
            </tbody>
        </c:otherwise>
    </c:choose>
</table>