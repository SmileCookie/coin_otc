<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<table class="tb-list2" id="ListTable" style="width: 100%">
    <tr>
        <th >流水号</th>
        <th >提交时间</th>
        <th >提现地址</th>
        <th >提取个数（个）</th>
        <th >实际个数</th>
        <th >状态</th>
        <th >备注</th>
        <th class="commodity_action">操作</th>
    </tr>
    <c:choose>
        <c:when test="${dataList!=null}">
            <tbody>
            <tr class="space">
                <td colspan="8">
                    <div class="operation" style="padding-left: 18px;">
                        <span style="margin-left: 30px;font-size: 18px;color: #ff0000;display: none;">提取个数：<font id="totalM"></font></span>
                        <span style="margin-left: 30px;font-size: 18px;color: #ff0000;display: none;">实际个数：<font id="totalM2"></font></span>
                    </div>
                </td>
            </tr>
            </tbody>
            <c:forEach items="${dataList}" var="list" varStatus="statu">
                <tbody id="row${list.id }" uname="${list.user.userName }" money="<fmt:formatNumber value="${list.amount}" pattern="0.000000##"/>" fee="<fmt:formatNumber value="${list.fees}" pattern="0.000000##"/>" after="<fmt:formatNumber value="${list.afterAmount }" pattern="0.000000##"/><br/>">

                <tr class="hd">
                    <td colspan="8">
                        <span>提现标志:${list.uuid} </span>
                        <span>提交时间：<fmt:formatDate value="${list.submitTime }" pattern="yyyy-MM-dd HH:mm:ss"/> </span>
                        <span>确认时间：<fmt:formatDate value="${list.manageTime }" pattern="yyyy-MM-dd HH:mm:ss"/> </span>
                    </td>
                </tr>
                <tr class="item_list_bd item_list_bgcolor">

                    <td class="commodity_info">
                        ${list.id}
                    </td>
                    <td class="commodity_price b_gray">
                        <fmt:formatDate value="${list.submitTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                    </td>
                    <td class="b_gray" id="evaluate_td${list.id}">
                        <a style="color: #006699;" href="${coint.web }${list.toAddress }" target="_blank">${list.toAddress }</a>
                    </td>
                    <td class="b_gray">
                        <fmt:formatNumber value="${list.amount}" pattern="0.000000##"/><br/>
                    </td>
                    <td class="b_gray">
                        <font style="color:red; font-size: 14px;font-weight: bold;">
                            <fmt:formatNumber value="${list.afterAmount }" pattern="0.000000##"/><br/>
                        </font>
                    </td>
                    <td>
                            ${list.showStat }
                    </td>
                    <td class="b_gray">
                            ${list.remark }
                    </td>
                    <td class="commodity_action br_color">
                        <a href="javascript:vip.list.aoru({id:'${list.uuid}'+ '_' + $('#coint').find('option:selected').text(), width : 600 , height : 660});">处理异常</a>
                    </td>
                </tr>
                </tbody>
            </c:forEach>
            <tbody class="operations">
            <tr>
                <td colspan="8">
                    <div id="page_navA" class="page_nav">
                        <div class="con">
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
                <td colspan="8">
                    <p>暂时没有符合要求的记录！</p>
                </td>
            </tr>
            </tbody>
        </c:otherwise>
    </c:choose>
</table>
