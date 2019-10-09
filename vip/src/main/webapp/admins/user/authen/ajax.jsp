<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" style="width:100%">
	<thead>
		<tr>
			<th style="width:300px;">用户名</th>
			<th>真实姓名</th>
			<th>认证地区</th>
			<th>证件类型</th>
			<th>身份证/护照</th>
			<th>认证时间</th>
			<th>状态</th>
			<th>审核时间</th>
			<th>操作</th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${dataList!=null}">
				<tbody>
					<tr class="space">
						<td colspan="7">
							<div class="operation">
								<%--<input type="checkbox" style="display:none;" id="delAll" class="DeleAllSel" name="checkbox"/><label style="width: 50px;" onclick="selectAll()" class="checkbox" id="ck_delAll"><em>全选</em></label>--%>
						       	<%--<c:if test="${tab=='wait'}">
							        <a title="批量通过" href="javascript:passmore()" id="del_Btn" class="Abtn delete">批量通过</a>
						       	</c:if>--%>
							</div>
						</td>
					</tr>
				</tbody>
			
				<c:forEach items="${dataList}" var="list" varStatus="statu">
					<%--<tbody>
						<tr class="space">
							<td colspan="7"></td>
						</tr>
					</tbody>--%>
					<%--<tbody>
						<tr class="hd">
							<td colspan="7">
								<input type="checkbox" style="display:none;" value="${list.id}" id="${statu.index}" class="checkItem"/><label id="ck_${statu.index}" class="checkbox" onclick="changeCheckBox('${statu.index}')"></label>
								<span>编号：${list.id} </span>
								<span>提交时间：<fmt:formatDate value="${list.submitTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
								<c:if test="${list.checkTime!=null }">
									<span>审核时间：<fmt:formatDate value="${list.checkTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
									<span>管理员：【${list.aUser.id },${list.aUser.admName }】</span>
								</c:if>
							</td>
						</tr>
					</tbody>--%>

					<c:set var="isIndividualUser" value="${list.type != 2}" />
					<tbody class="item_list" id="line_${list.id}">
						<tr>
							<td>
								<div class="pic_info">
									<%--<div class="pic"><a href="javascript:showUser('${list.user.id}')" style="background: url(${list.user.defaultPhoto}) no-repeat 50% 50%"></a></div>--%>
									<div >${list.user.userName}</div>
								</div>
							</td>
							<td>${list.realName}</td>
							<td>
								<c:if test="${list.areaInfo == 1}"> 大陆地区</c:if>
								<c:if test="${list.areaInfo == 2}"> 港澳台地区</c:if>
								<c:if test="${list.areaInfo == 3}"> 海外地区</c:if>
							</td>
							<td>
								<c:if test="${list.cardType eq '1'}"> 身份证</c:if>
								<c:if test="${list.cardType eq '2'}"> 护照</c:if>
							</td>
							<td>${list.cardId}</td>
							<td><fmt:formatDate value="${list.submitTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
								<%--<div class="pic_info">
									&lt;%&ndash;<div class="pic"><a href="javascript:;" style="background: url(${list.defaultLoadImg}) no-repeat 50% 50%"></a></div>&ndash;%&gt;
									<div class="txt">${list.realName}</div>
									<div class="txt">
										${list.cardId}
									</div>
									&lt;%&ndash;<a style="color: #ff0000;" target="_blank" href="/admin/user/authen/see?id=${list.id}">查看详情</a>
									<a style="color: green;" href="javascript:vip.list.aoru({url: '/admin/user/authen/preEdit?userId=${list.userId}', id: ${list.userId}, width : 700 , height : 600, title:'修改资料'});">修改资料</a>&ndash;%&gt;
								</div>--%>

							<%--<td>
								${list.gegisterCity}
							</td>--%>
							<td>
								<c:choose>
									<c:when test="${list.status==5 }">待审核</c:when>
									<c:when test="${list.status==6 }">通过审核</c:when>
									<c:when test="${list.status==7 }">未通过审核</c:when>
									<%--<a href="javascript:Alert('${list.reason }')">查看原因</a>--%>
									<c:otherwise>-</c:otherwise>
								</c:choose>
							</td>
							<td>
								<c:if test="${list.checkTime != null }">
									<fmt:formatDate value="${list.checkTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
								</c:if>
								<c:if test="${list.checkTime == null }">
									-
								</c:if>


							</td>
							<td>
								<c:if test="${list.status==5 }">
									<a style="color:blue;"  href="/admin/user/authen/see?id=${list.id}">审核</a>
									<%--<a href="javascript:agree('${list.id }',1)">审核</a>--%>
									<%--<a href="javascript:reason('${list.id }',1)">不通过</a>--%>
								</c:if>
								<c:if test="${list.status==6 }">
									<a href="/admin/user/authen/onlySee?id=${list.id}" style="cursor: pointer;color: blue;">查看</a>
									<%--<a href="javascript:reason('${list.id }',1)">不通过</a>
									<a href="javascript:cancel('${list.id }',1)">取消</a>--%>
								</c:if>
								<c:if test="${list.status==7 }">
									<a href="/admin/user/authen/onlySee?id=${list.id}" style="cursor: pointer;color: blue;">查看</a>
									<%--<a href="javascript:reason('${list.id }',1)">不通过</a>
									<a href="javascript:cancel('${list.id }',1)">取消</a>--%>
								</c:if>

								<br/>
								<%--<c:if test="${isIndividualUser}">
									<a href="javascript:bankinfo('${list.id}')">校验银行卡信息</a>
								</c:if>--%>
								<%-- <a class="search-submit" href="javascript:vip.list.aoru({id : ${list.userId } , width : 500 , height : 650, title:'${list.realName }-认证额度设置', otherParam:'&type=${list.type}'});">用户认证额度设置</a>
 --%>
								<%--<c:if test="${logAdmin.rid==1 && isIndividualUser}">
								<br/><a href="javascript:setAuthName('${list.userId}')">设置证件信息</a>
								</c:if>--%>
							</td>
						</tr>
					</tbody>
				</c:forEach>
				<tfoot>
					<tr>
						<td colspan="7">
							<div class="page_nav" id="pagin">
								<div class="con">
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
						<td colspan="7">
							<p>没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</c:otherwise>
		</c:choose>

</table>