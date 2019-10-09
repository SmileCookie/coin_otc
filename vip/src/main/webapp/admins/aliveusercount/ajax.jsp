<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" id="ListTable" style="width: 100%">

	<tr class="hd">
		<h1 style="line-height: 60px;background-color: #96aeb8;margin-bottom: 20px; text-align: center;">统计报表(
			<jsp:useBean id="now" class="java.util.Date" scope="page"/>
			<fmt:formatDate value="${beginTime}" pattern="yyyy-MM-dd " />至
			<fmt:formatDate value="${endTime}" pattern="yyyy-MM-dd " />
			)
		</h1>
	</tr>
	<tr>
		<th >序号</th>
		<th >报表时间</th>
		<th >PV</th>
		<th >UV</th>
		<th>访问IP</th>
		<th >当日登陆IP</th>
		<th >注册总量</th>
		<th >当日注册数量</th>
		<th >每日登陆人数</th>
		<th >用户登录率</th>
		<th >注册转化率</th>
		<th>操作</th>
	</tr>
	<c:choose>

		<c:when test="${dataList!=null}">
			<c:forEach items="${dataList}" var="list" varStatus="statu">
				<tr class="item_list_bd item_list_bgcolor">
					<td>${statu.index + 1}</td>
					<td><fmt:formatDate value="${list.countDate}" pattern="yyyy-MM-dd "/></td>
					<td>${list.pvCount }</td>
					<td>${list.uvCount }</td>
					<td>${list.accessingIp }</td>
					<td>${list.ipCount }</td>
					<td>${list.allRegisterCount }</td>
					<td>${list.registerCount }</td>
					<td>${list.loginCount }</td>
					<td>${list.loginRate }%</td>
					<td>${list.registrationConversionRate }%</td>
					<td><a href="javascript:setUpv('${list.id }');">修改PV，UV</a></td>
				</tr>
				</tbody>
			</c:forEach>
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