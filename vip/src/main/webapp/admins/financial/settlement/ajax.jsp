<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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

    .error-bg {
        background-color: #FFDADA;
    }
</style>


<div class="tab-body">
    <table class="tb-list2" style="width:100%;table-layout: fixed;">
        <thead>
        <tr>
            <th>币种</th>
            <th>周期</th>
            <th>btcwinex充值总额</th>
            <th>商户平台充值总额</th>
            <th>btcwinex提现金额</th>
            <th>商户平台提现金额</th>
            <th>手续费</th>
            <th>系统扣除</th>
            <th>操作</th>
        </tr>
        </thead>

        <c:forEach items="${list }" var="entity" varStatus="stat">
           	<c:forEach items="${coinMap }" var="coin">
       		<c:if test="${coin.value.fundsType == entity.coinType && coin.value.coin}">
            <c:set var="isChargeBalance" value="${entity.totalCharge == entity.totalChargeMer}"/>
            <c:set var="isWithdrawBalance" value="${entity.totalWithdraw == entity.totalWithdrawMer}"/>
            <tr><c:set var="numFormat" value="${entity.coinType == 1 ? '#,##0.00' : '#,##0.0########'}"/>
                <td style="font-weight: bold;color: #000000;">
                		${coin.key} 
                </td>
                <td>
                    始：<fmt:formatDate value="${entity.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/><br/>
                    末：<font color="red"><fmt:formatDate value="${entity.endTime }" pattern="yyyy-MM-dd HH:mm:ss"/></font>
                </td>

                <td style="color:green;" class="${isChargeBalance ? "" : "error-bg"}">
                    +<fmt:formatNumber pattern="${numFormat}" value="${entity.totalCharge}"/>
                </td>
                <td style="color:green;" class="${isChargeBalance ? "" : "error-bg"}">
                    +<fmt:formatNumber pattern="${numFormat}" value="${entity.totalChargeMer}"/>
                </td>

                <td style="color:orange;" class="${isWithdrawBalance ? "" : "error-bg"}">
                    -<fmt:formatNumber pattern="${numFormat}" value="${entity.totalWithdraw}"/>
                </td>
                <td style="color:orange;" class="${isWithdrawBalance ? "" : "error-bg"}">
                    -<fmt:formatNumber pattern="${numFormat}" value="${entity.totalWithdrawMer}"/>
                </td>

                <td style="color:green;">
                    +<fmt:formatNumber pattern="${numFormat}" value="${entity.totalFees }"/>
                </td>

                <td style="color:orange;">-<fmt:formatNumber pattern="${numFormat}" value="${entity.sysReduce}"/></td>

                <td>
                    <a href="javascript:;" onclick="jiesuan('${entity.coinType}')">结算</a>
                </td>
            </tr>
            </c:if>
           	</c:forEach>
        </c:forEach>
    </table>
</div>

<table class="tb-list2" style="width:100%;table-layout: fixed;">
    <thead>
    <tr>
        <th>币种</th>
        <th>结算日期</th>
        <th>结算人</th>
        <th>btcwinex收入总额</th>
        <th>商户平台收入总额</th>
        <th>btcwinex支出总额</th>
        <th>商户平台支出总额</th>
        <th>对账结果</th>
        <th>财务余额</th>
        <th>备注</th>
    </tr>
    </thead>
    <c:choose>
        <c:when test="${dataList!=null}">
            <c:forEach items="${dataList}" var="list" varStatus="statu">
                <c:set var="numFormat" value="${list.coinType == 1 ? '#,##0.00' : '#,##0.0########'}"/>
                <tbody class="item_list" id="line_${list.id}">
                <tr>
                    <td style="font-weight: bold;color: #000000;">
                          <c:forEach items="${coinMap }" var="coin">
		                		<c:if test="${coin.value.fundsType == list.coinType }">
		                			${coin.key} 
		                		</c:if>
		                	</c:forEach>
                    </td>
                    <td style="font-weight: bold;color: #000000;">
                        <fmt:formatDate value="${list.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                    </td>
                    <td>
                        ${list.userName }
                    </td>

                    <td style="color:green;">
                        <fmt:formatNumber value="${list.totalCharge }" pattern="${numFormat}"/>
                    </td>
                    <td style="color:green;">
                        <fmt:formatNumber value="${list.totalChargeMer }" pattern="${numFormat}"/>
                    </td>

                    <td style="color:orange;">
                        <fmt:formatNumber value="${list.totalWithdraw }" pattern="${numFormat}"/>
                    </td>
                    <td style="color:orange;">
                        <fmt:formatNumber value="${list.totalWithdrawMer }" pattern="${numFormat}"/>
                    </td>

                    <td style="word-break: break-all; WORD-WRAP: break-word;color:${list.status == '正常' ? 'green' : 'red'}">
                            ${fn:length(list.status)>0?list.status:"-"}
                    </td>

                    <td>
                        <fmt:formatNumber value="${list.ftotalBalance }" pattern="${numFormat}"/>
                    </td>

                    <td>
                        <a href="javascript:memo('${list.id }');">详细</a>
                    </td>
                </tr>
                </tbody>
            </c:forEach>
            <tfoot>
            <tr>
                <td colspan="9">
                    <div class="page_nav" id="pagin">
                        <div class="con">
                            <c:if test="${pager=='' }">共${itemCount }项</c:if>
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
                <td colspan="9">
                    <p>没有符合要求的记录！</p>
                </td>
            </tr>
            </tbody>
        </c:otherwise>
    </c:choose>

</table>