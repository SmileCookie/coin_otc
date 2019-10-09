<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 
<input type="hidden" id="currentPage" value="${currentPage }"/>
<input type="hidden" id="currentTab" value="${currentTab }"/>

<table class="table table-striped table-bordered table-hover" id="ListTable">
	<thead>
		<tr>
			<th width="170px">${L:l(lan,"时间")}</th>
			<th width="240px">${L:l(lan,"类型")}</th>
			<th width="78px">${L:l(lan,"金额")}(฿)</th>
			<th width="185px">${L:l(lan,"处理时间")}</th>
			<th width="80px">${L:l(lan,"确认次数")}</th>
			<th width="150px">${L:l(lan,"余额")}(฿)</th>
			<th width="105px">${L:l(lan,"状态")}</th>
		</tr>
	</thead>
	
	<c:choose>
	<c:when test="${dataList != null&&fn:length(dataList)>0}">
	<tbody>
	<c:forEach items="${dataList}" var="pm" varStatus="status">

		<tr class="bd" >
			<td style="text-align: left;">
				<div class="pic_info">
					<div class="txt2" >
						${fn:substring(pm.sendimeTime,0,19)}
					</div>
				</div>
			</td>
			<td style="text-align: left;">
				<p class="text-muted ft12">
						<a title='${L:l(lan,"点击查看状态")}(BlockChain)' style="color: #006699;" href="https://blockchain.info/zh-cn/address/${pm.btcTo }" target="_blank">${pm.btcTo }</a>
						<c:if test="${pm.isIn==7 or pm.isIn==40 or pm.isIn==42 or pm.isIn==44}">${L:l(lan,pm.remark)}</c:if>
				</p>
			</td>
			<td><span class="bkNum"><fmt:formatNumber value="${pm.number/100000000 }"  pattern="#0.000000##" /></span></td>
			<td><c:choose>
			   <c:when test="${pm.status==0}">
			   	<span class="txt2">—</span>
			   </c:when>
			   <c:when test="${pm.status==1}">
			   		<c:choose>
			   			<c:when test="${pm.isIn==7 or pm.isIn==40 or pm.isIn==42 or pm.isIn==44}">
						   	 <span class="co_red">${fn:substring(pm.sendimeTime,0,19)}</span>
			   			</c:when>
			   			<c:otherwise>
						   	 <span class="co_red">${fn:substring(pm.configTime,0,19)}</span>
			   			</c:otherwise>
			   		</c:choose>
			   </c:when>
			    <c:when test="${pm.status==2}">
			    	<c:choose>
			   			<c:when test="${pm.isIn==7 or pm.isIn==40 or pm.isIn==42 or pm.isIn==44}">
						   	 <span style="color:green;">${fn:substring(pm.sendimeTime,0,19)}</span>
			   			</c:when>
			   			<c:otherwise>
						   	 <span style="color:green;">${fn:substring(pm.configTime,0,19)}</span>
			   			</c:otherwise>
			   		</c:choose>
			   </c:when>
			</c:choose>
				
			</td>
			<td>
				<c:if test="${pm.confirmTimes >= 3 }">
					<font class="green">>=${pm.confirmTimes }</font>
				</c:if>
				
				<c:if test="${pm.confirmTimes < 3 }">
					<font class="green">${pm.confirmTimes }</font>
				</c:if>
				
			</td>
			<td class="admin">
				<span class="bkNum">
				<c:if test="${pm.banlance == 0}">
					—
				</c:if>
				<c:if test="${pm.banlance != 0}">
					<fmt:formatNumber value="${pm.banlance/100000000 }"  pattern="#0.000000##" />
				</c:if>
				</span>
			</td>
			<td>
			<c:choose>
			   <c:when test="${pm.status==0}">
			   	${L:l(lan,"网络确认中")}
			   </c:when>
			   <c:when test="${pm.status==1}">
			   	 <span>${L:l(lan,"失败")}</span>
			   </c:when>
			    <c:when test="${pm.status==2}">
			   	 <span>${L:l(lan,"成功")}</span>
			   </c:when>
			</c:choose>
			</td>
		</tr>
	</c:forEach>
	</tbody>
	
		<tfoot>
	        <tr>
                <td colspan="7">
	                <div id="page_navA" class="page_nav">
						<div class="con">
							 <c:if test="${pager!=null}">${pager}</c:if>
						</div>
					</div>
                </td>
               </tr>
             	</tfoot>
	</c:when>
	<c:otherwise>
		<tbody>
              <tr>
               <td colspan="7"><div class="bk-norecord " style="margin:-15px -9px;"><p><i class="bk-ico info"></i>${L:l(lan,'暂时没有相关记录。')}</p></div></td>
              </tr>
             </tbody>
	</c:otherwise>
	</c:choose>
</table>
<script type="text/javascript">
formatNum();
</script>