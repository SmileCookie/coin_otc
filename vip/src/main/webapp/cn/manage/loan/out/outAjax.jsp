<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<c:if test="${fundType==1 }"><h4>${L:l(lan,'可立即投资的人民币')}</h4></c:if>
<c:if test="${fundType==2 }"><h4>${L:l(lan,'可立即投资的比特币')}</h4></c:if>
<c:if test="${fundType==3 }"><h4>${L:l(lan,'可立即投资的莱特币')}</h4></c:if>
<c:if test="${fundType==4 }"><h4>${L:l(lan,'可立即投资的以太币')}</h4></c:if>
<c:if test="${fundType==5 }"><h4>${L:l(lan,'可立即投资的ETC')}</h4></c:if>
<style type="text/css">
#shopslist .page_nav .con_ a.num, #shopslist .page_nav .con_ .go_page, #shopslist .page_nav .con_ .ellipsis{display: none;}
#shopslist .page_nav {
    margin: 6px 26px 0;
}
</style>
<dl class="ch_table">
	<dt><span class="t1"><a href="javascript:;" ${amountCode } title="${L:l(lan,'根据可投金额排序')}">${L:l(lan,'可投金额')}</a></span><span class="t2"><a href="javascript:;" ${rateCode } title="根据利率排序">利率</a></span><span class="t3">利率形式</span><span class="t4">操作</span></dt>
<c:choose>
    <c:when test="${fn:length(lists)>0}">
   	<c:forEach items="${lists }" var="item" varStatus="stat">
	<dd <c:if test="${(stat.index + 1) % 2 == 0}">class="double"</c:if>>
   		<span class="t1">${item.fundsType.tag }<fmt:formatNumber value="${item.sourceAmount-item.hasAmount }" pattern="0.0##" /></span>
   		<span class="t2"><fmt:formatNumber value="${item.rateOfDayShow }" pattern="0.0##" />%</span>
   		<span class="t3">${item.interestRateFormShow }</span>
   		<span class="t4">
			<a href="javascript:vip.p2p.doTrans(${item.id } , true)">${L:l(lan,'立即投资')}</a>
   		</span>
   	</dd>
	</c:forEach>
  </c:when>
  <c:otherwise>
  <div class="air-tips"><p>${L:l(lan,'暂时没有符合要求的记录')}</p></div>
  </c:otherwise>
</c:choose>

</dl>
<div id="page_navA" class="page_nav">
	<div class="conqwe" style="float: right;">
		<c:if test="${pager!=null}">${pager}</c:if>
	</div>
</div>