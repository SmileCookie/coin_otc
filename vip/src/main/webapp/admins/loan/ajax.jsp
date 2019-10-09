<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<table class="tb-list2" style="width:100%;table-layout: fixed;">
	<thead>
		<tr>
			<th style="width:200px;">用户名</th>
			<th>借贷类型</th>
			<th>投资标识</th>
			<th>借贷资金</th>
			<th>借贷利率</th>
			<th>已成功的款项</th>
			<th>已还本息</th>
			<th>风险控制</th>
			<th>免息券类型</th>
			<th>状态</th>
			<th style="width: 100px;">操作</th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${dataList!=null}">
				<tbody>
					<tr class="space">
						<td colspan="11">
							<div class="operation">
								<input type="checkbox" style="display:none;" id="delAll" class="DeleAllSel" name="checkbox"/>
								<label style="width: 50px;" onclick="selectAll()" class="checkbox" id="ck_delAll"><em>全选</em></label>
								<a class="AButton yellow_button manyJisuan" isAll="false" href="javascript:tongji(false)">统计选中金额</a>
								|
								<a class="AButton yellow_button manyJisuan" href="javascript:tongji(true);">统计全部金额</a>
								<span style="margin-left: 30px;font-size: 18px;color: #ff0000;display: none;">总金额：<font id="totalM"></font></span>
								<span style="margin-left: 20px;font-size: 18px;color: #ff0000;display: none;">已成功：<font id="totalM2"></font></span>
								<span style="margin-left: 30px;font-size: 18px;color: #ff0000;display: none;">已还本息：<font id="totalM3"></font></span>
							</div>
						</td>
					</tr>
				</tbody>
				<c:forEach items="${dataList}" var="list" varStatus="statu">
					<tbody>
						<tr class="space">
							<td colspan="11">
							</td>
						</tr>
					</tbody>
					<tbody>
						<tr class="hd">
							<td colspan="11">
								<input type="checkbox" style="display:none;" value="${list.id}" id="${statu.index+2017}" class="checkItem"/><label id="ck_${statu.index+2017}" class="checkbox" onclick="changeCheckBox('${statu.index+2017}')"></label>
								<span>编号：${list.id} </span>
								<span>时间：<fmt:formatDate value="${list.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/></span>
							</td>
						</tr>
					</tbody>
				
					<tbody class="item_list" id="line_${list.id}">
						<tr>
							<td>
								<div class="pic_info">
									<div class="pic">
										<a href="javascript:showUser('${list.userId}')"><img src=""/></a>
									</div>
									<div class="txt">
										${list.userName}
										<%--<a href="javascript:showUser('${list.userId}')" style="font-weight: bold;color:green;" id="text_${list.userId }">${list.userName}</a>--%>
										<br/>
										<font style="">
										</font>
									</div>
								</div>
							</td>
							<td>
								${list.isIn?"借入":"投资" }
							</td>
							<td>
								<c:if test="${list.investMark==0 }"><font color="green">手动投资</font></c:if>
								<c:if test="${list.investMark==1 }"><font color="orange">自动投资</font></c:if>
							</td>
							<td>
								<font color="">${list.getFt().propTag }：<fmt:formatNumber value="${list.amount }" pattern="0.00######"/></font>
							</td>
							<td>
								<fmt:formatNumber value="${list.rateOfDayShow }" pattern="0.00##"/> %
							</td>
							<td>
								${list.getFt().unitTag }：<fmt:formatNumber value="${list.hasAmount }" pattern="0.00######"/>
							</td>
							<td>
								${list.getFt().unitTag }：<fmt:formatNumber value="${list.hasRepayment }" pattern="0.00######"/>
							</td>
							<td>
								${list.riskManage==1?"自担风险":"只要本金币种" }
							</td>
							<td>
								<c:if test="${list.withoutLx==false }">没有免息券</c:if>
			  	 				<c:if test="${list.withoutLx==true }"><font color="red">使用免息券</font></c:if>
							</td>
							<td>
								<c:if test="${list.status==0 }">未有借入</c:if>
								<c:if test="${list.status==1 }"><font color="#F06000">部分借入</font></c:if>
			  	 				<c:if test="${list.status==2 }"><font color="red">已取消</font></c:if>
			  	 				<c:if test="${list.status==3 }"><font color="green">已成功</font></c:if>
			  	 				<c:if test="${list.status>3 }">-</c:if>
							</td>
							<td>
								<span class="t4">
									<%-- <c:if test="${list.status lt 2 }">
										<c:choose>
												<c:when test="${list.isIn }">
													<a href="javascript:doTrans(${list.id } , true)">立即投资</a>
												</c:when>
												<c:otherwise>
													<a href="javascript:doTrans(${list.id } , false)">立即借入</a>
												</c:otherwise>
										</c:choose>
									</c:if> zhanglinbo 后台怎么立即借入？--%>
									<c:if test="${list.status ge 2 }">-</c:if>
								</span>
							</td>
						</tr>
					</tbody>
				</c:forEach>
				<tfoot>
					<tr>
						<td colspan="11">
							<div class="page_nav" id="pagin">
								<div class="con">
									<c:if test="${pager==''}">共${itemCount}项</c:if>
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
						<td colspan="11">
							<p>没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</c:otherwise>
		</c:choose>

</table>