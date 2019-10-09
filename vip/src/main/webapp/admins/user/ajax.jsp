<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<script type="text/javascript">
$(function(){
	$("a[mytitle]").each(function(){
		  $(this).UiTitle(); 
	});
});
function updateUserAuth(id, title, type){
	Iframe({
        Url : "/admin/user/authen/aoru?id="+id + "&type=" + type,
        Width : 500,
        Height : 800,
        scrolling : 'no',
        isShowIframeTitle: true,
        Title : title
    });
}
</script>
<table class="tb-list2" id="ListTable" style="width: 100%">
	<thead>
		<tr>
			<th style="width:300px;">用户名</th>
			<th>手机号</th>
			<th>邮箱</th>
			<th>用户类型</th>
			<th>用户操作类型</th>
			<th>推荐人</th>
			<th>登录ip</th>
			<th style="width: 100px;">操作</th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${dataList!=null}">
				<c:if test="${logAdmin.rid==1 }">
				<tbody>
					<tr class="space">
						<td colspan="6">
							<div class="operation">
								<input type="checkbox" style="display:none;" id="delAll" class="DeleAllSel" name="checkbox"/><label style="width: 50px;" onclick="selectAll()" class="checkbox" id="ck_delAll"><em>全选</em></label>
						       	<c:if test="${tab!='del'}">
							        <a title="批量删除" href="javascript:deletes()" id="del_Btn" class="Abtn delete">批量删除</a>
						       	</c:if>
						       	<c:if test="${tab=='del'}">
							        <a title="批量还原" href="javascript:returns()" id="del_Btn" class="Abtn delete">批量还原</a>
						       	</c:if>
						       	<c:if test="${itemCount>0}"><span style="color: red;font-weight: bold;padding: 0 20px;">共${itemCount}项</span></c:if>
							</div>
						</td>
					</tr>
				</tbody>
				</c:if>
				<c:forEach items="${dataList}" var="list" varStatus="statu">
					<tbody>
						<tr class="space">
							<td colspan="7">
							</td>
						</tr>
					</tbody>
					<tbody>
						<tr class="hd">
							<td colspan="7">
								<c:if test="${logAdmin.rid==1 }">
								<input type="checkbox" style="display:none;" value="${list.id}" id="${statu.index}" class="checkItem"/><label id="ck_${statu.index}" class="checkbox" onclick="changeCheckBox('${statu.index}')"></label>
								</c:if>
								<span>用户编号：${list.id} </span>
								<span>注册时间：<fmt:formatDate value="${list.registerTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
								<c:if test="${list.lastLoginTime!=null }"><span>最后登陆时间：<fmt:formatDate value="${list.lastLoginTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span></c:if>	
								<c:if test="${list.freez }"><a style="color: green;font-weight: bold;" href="javascript:freez_user('${list.id }',false)">解冻</a></c:if>
								<c:if test="${!list.freez }"><a style="color: red;font-weight: bold;" href="javascript:freez_user('${list.id }',true)">冻结</a></c:if>
							</td>
						</tr>
					</tbody>
				
					<tbody class="item_list" id="line_${list.id}">
						<tr>
							<td>
								<div class="pic_info">
									<div class="txt">
										<a href="javascript:showUser('${list.id}')" style="font-weight: bold;color:green;" id="text_${list.id }">${list.userName}</a>
									</div>
								</div>
							</td>
							<td>
								${fn:length(list.userContact.safeMobile)>0?list.userContact.safeMobile:"-" }
								<c:if test="${fn:length(list.userContact.mobileCode)>0}">
									<c:if test="${fn:length(list.userContact.checkMobile)>0}">
										<br/>接收短信手机号码：${list.userContact.checkMobile}
									</c:if>
									<br/>
<!-- 									<font><a style="color: #ff0000;font-weight: bold;" href="javascript:sendEmail('${list.id}');">发送验证码邮件</a></font> -->
								</c:if>
							</td>
							<td>
								<c:if test="${fn:length(list.userContact.safeEmail)==0}">
								 	<font color="#ff0000">注册邮箱：<br/>${list.email}</font>
								</c:if>
								<c:if test="${fn:length(list.userContact.safeEmail)>0}">
									认证邮箱：<br/>${list.userContact.safeEmail}
								</c:if>
							</td>
							<td>
								<a href="javascript:setCustomerType('${list.id }');">${list.customerTypeView}</a>
							</td>
							<td>
								<a href="javascript:setCustomerOperation('${list.id }');">${list.customerOperationView}</a>
							</td>
							<td>${fn:length(list.recommendName)>0?list.recommendName:"<font color='green'>自己来的</font>"}</td>
							<td>
								<c:if test="${fn:length(list.loginIp) > 0 }">
									<a href="http://www.ip138.com/ips138.asp?ip=${list.loginIp }&action=2" target="_blank">${list.loginIp}</a>
								</c:if> ${fn:length(list.loginIp)>0?"":"-"}
							</td>
							
							
							<td>
								<c:if test="${logAdmin.rid==1 }">
									<a href="javascript:setTuijian('${list.id }');">修改推荐人</a>
								</c:if>
								<c:if test="${!list.deleted }">
									<br/>
									<a href="javascript:doDel('${list.id}')">删除</a>
								</c:if>
								<c:if test="${list.deleted }">
									<br/>
									<a href="javascript:doReturn('${list.id}')">还原</a>
								</c:if>
                                <c:if test="${list.userName==null }"><a href="javascript:clearNoReg('${list.id}')">清除</a></c:if>
								<br/> 
								<a mytitle="${list.memo }" href="javascript:addMemo('${list.id}')">备注</a>
							</td>
						</tr>
					</tbody>
				</c:forEach>
				<tfoot>
					<tr>
						<td colspan="6">
							<div class="page_nav" id="pagin">
								<div class="con">
									<c:if test="${pager==''}">共${fn:length(dataList)}项</c:if>
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
						<td colspan="6">
							<p>没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</c:otherwise>
		</c:choose>

</table>