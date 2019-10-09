<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>快照</title>
   <jsp:include page="/admins/top.jsp" />

<style type="text/css">
label.checkbox{  margin: 3px 6px 0 7px;}
label.checkbox em{ padding-left:18px; line-height:15px; float:left; font-style:normal;}
.page_nav{ margin-top:10px;}
.form-search .formline{float:left;}
.form-search p{float:none;}
.tb-list2 td .m{text-align: right;padding-right: 20px;}
.operation { height: 20px; line-height: 20px; text-align: left;margin-top: 10px;padding-left: 10px;}
tbody.operations  td{ padding:0; border:0 none;}
tbody.operations  td label.checkbox{ margin-top:10px; width:55px;}
</style>
</head>
<body >
<div style="text-align: right;">
本记录创建时间：<fmt:formatDate value="${charts[0].createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
<span style="margin-left: 10px;">创建人：${charts[0].aUser.admName }</span>
</div>
<table class="tb-list2" style="width:100%;table-layout: fixed;">
	<thead>
		<tr>
			<th width="200px;">账户名</th>
			<c:choose>
				<c:when test="${fundType==1}">
					<th>人民币</th>
				</c:when>
				<c:when test="${fundType==2}">
					<th style="text-align: right;padding-right: 40px;">比特币</th>
				</c:when>
				<c:when test="${fundType==3}">
					<th style="text-align: right;padding-right: 40px;">莱特币</th>
				</c:when>
			</c:choose>
			<c:if test="${fundType == 1 }">
				<th>未确认金额</th>
				<th>总金额</th>
			</c:if>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${charts!=null}">
				<c:set var="column" value="${fundType==1?4:2 }"/>
				<tbody>
					<tr class="space">
						<td colspan="${column }">
						</td>
					</tr>
				</tbody>
				
				<c:forEach items="${charts}" var="list" varStatus="statu">
					<tbody class="item_list" id="line_${list.id}">
						<tr>
							<td>
								${list.account.name}
								(<c:choose>
									<c:when test="${list.account.type==1 }"><font color="#D75A46">充值</font></c:when>
									<c:when test="${list.account.type==2 }"><font color="#8DC03C">储备</font></c:when>
									<c:when test="${list.account.type==3 }"><font color="red">提现</font></c:when>
									<c:when test="${list.account.type==4 }"><font color="orange">日常</font></c:when>
								</c:choose>)
							</td>
							<td>
								<div class="m">
							 		<font color="green"><fmt:formatNumber value="${list.funds }" pattern="0.0000####"/></font>
								</div>
							</td>
							<c:if test="${fundType == 1 }">
								<td>
									<div class="m">
										<font color="red"><fmt:formatNumber value="${list.exceptAmount }" pattern="0.00##"/></font>
									</div>
								</td>
								<td class="m">
									<div class="m">
										<font color="#D75A46"><fmt:formatNumber value="${list.totalBalance }" pattern="0.00##" /></font>
									</div>
								</td>
							</c:if>
						</tr>
					</tbody>
				</c:forEach>
				<tr>
					<td style="text-align: right;">
						总计 ：
					</td>
					<td class="m">
						<div class="m">
							<font color="green"><fmt:formatNumber value="${fcDao.confirmBalance }" pattern="0.00######"/></font>
						</div>
					</td>
					<c:if test="${fundType == 1 }">
						<td class="m">
							<div class="m">
								<font color="red"><fmt:formatNumber value="${fcDao.errorBalance }" pattern="0.00######"/></font>
							</div>
						</td>
						<td class="m">
							<div class="m">
								<font color="#D75A46"><fmt:formatNumber value="${fcDao.totalBalance }" pattern="0.00######" /></font>
							</div>
						</td>
					</c:if>
				</tr>
				<tfoot>
					<tr>
						<td colspan="${column }">
							<div class="page_nav" id="pagin">
								<div class="con">
									共${fn:length(charts) }项
								</div>
							</div>
						 </td>
					</tr>
				 </tfoot>
			</c:when>
			<c:otherwise>
				<tbody class="air-tips">
					<tr>
						<td colspan="${column }">
							<p>没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</c:otherwise>
		</c:choose>

</table>
</body>
</html>
