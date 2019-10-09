<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:if test="${array != null }">
	<c:if test="${logAdmin.rid==1 || logAdmin.rid==6 }">
	<div class="tittype" style="font-size: 18px;font-weight: bold;padding: 10px;margin-bottom: -20px;">
<%--		<div class="formcon" style="margin-right: 10px;">--%>
<%--			<select name="fundType" id="fundType" onchange="changeType()" style="width:130px;display: none;" selectid="select_28644876">--%>
<%--	           <option value="1" <c:if test="${fundType==1}">selected="selected"</c:if>>人民币账户</option>--%>
<%--	           <option value="2" <c:if test="${fundType==2}">selected="selected"</c:if>>比特币账户</option>--%>
<%--	           <option value="3" <c:if test="${fundType==3}">selected="selected"</c:if>>莱特币账户</option>--%>
<%--	         </select>--%>
<%--	         <div class="SelectGray" id="select_28644876"><span style="margin-right: 10px;"><i style="width: 171px;">人民币账户</i></span></div>--%>
<%--		</div>--%>
	
		金额汇总
		==》
		<span><font color="#D75A46"><fmt:formatNumber value="${totalBalance }" pattern="0.00######"/></font></span>
		<%-- <span>已确认：<font color="#8DC03C"><fmt:formatNumber value="${confirmBalance }" pattern="0.00######"/></font></span>
		<span>未确认：<font color="red"><fmt:formatNumber value="${errorBalance }" pattern="0.00######"/></font></span>
		<span class="operate" style="clear: both;margin-right: 0">
			<a href="javascript:chart(${fundType });">快照</a>
		</span> --%>
	</div>
	</c:if>
	<c:forEach items="${array}" var="obj">
	<div class="accou">
		<c:if test="${logAdmin.rid==1 || logAdmin.rid==6 }">
		<div class="atype">
			<c:if test="${obj.key==1 }">充值账户</c:if>
			<c:if test="${obj.key==2 }">储备账户</c:if>
			<c:if test="${obj.key==3 }">提现账户</c:if>
			<c:if test="${obj.key==4 }">日常开支</c:if>
			==》
			<span>实际总额：<font color="#D75A46"><fmt:formatNumber value="${obj.totalBalance }" pattern="0.00######"/></font></span>
<!-- 			<span>已确认：<font color="#8DC03C"><fmt:formatNumber value="${obj.confirmBalance }" pattern="0.00######"/></font></span> -->
<!-- 			<span>未确认：<font color="red"><fmt:formatNumber value="${obj.errorBalance }" pattern="0.00######"/></font></span> -->
		</div>
		</c:if>	
		<c:forEach items="${obj.array }" var="list">
		<div class="view" style="cursor: pointer;">
			<table>
				<tr>
					<th colspan="2"><a href="/admin/financial/account?accountId=${list.id }">${list.name}</a></th>
				</tr>
				<tr>
					<td class="tit">
						实际总额：
					</td>
					<td>
						<font color="#D75A46">
							<c:if test="${fundType == 1 }">
								<fmt:formatNumber value="${list.total }" pattern="#,##0.00##"/>
							</c:if>
							<c:if test="${fundType > 1 }">
								<fmt:formatNumber value="${list.funds }" pattern="#,##0.00######"/>
							</c:if>
						</font> ${coint.propTag }
					</td>
				</tr>
				<c:if test="${list.fundType == 1}">
				<tr>
					<td class="tit">
						已确认：
					</td>
					<td>
						<font color="#8DC03C"><fmt:formatNumber value="${list.amount }" pattern="#,000.00##"/></font> RMB
					</td>
				</tr>
				<tr>
					<td class="tit">
						未确认：
					</td>
					<td>
						<font color="red"><fmt:formatNumber value="${list.exceptAmount }" pattern="#,000.00##"/></font> RMB
					</td>
				</tr>
				</c:if>
			</table>
			<div class="operate">
				<c:if test="${list.dayTag<dayTag0||list.dayTag==dayTag24 }">
					<a href="javascript:balance(${list.id }, ${dayTag0 });">上班结算</a>
				</c:if>
				<c:if test="${list.dayTag==dayTag0 && list.dayTag<dayTag24 }">
					<a class="red" href="javascript:balance(${list.id }, ${dayTag24 });">下班结算</a>
				</c:if>
			
				<a href="javascript:vip.list.aoru({id:${list.id },height:618});">编辑</a>
				<a href="javascript:vip.list.del({id:${list.id }});">删除</a>
				<c:if test="${!list.isDefault }">
					<a href="javascript:setDefault(${list.id }, 1);">设置默认账户</a>
				</c:if>
				<c:if test="${list.isDefault }">
					<a class="red" href="javascript:setDefault(${list.id }, 0);">取消默认</a>
				</c:if>
			</div>
		</div>
		</c:forEach>
	</div>
	</c:forEach>
</c:if>