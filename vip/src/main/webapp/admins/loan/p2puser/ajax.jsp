<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<input type="hidden" id="tab" name="tab" value="${tab }" />
<input type="hidden" id="page" name="page" value="${page }" />
<table class="tb-list2" style="width:100%;table-layout: fixed;">
	<thead>
		<tr>
			<th style="width:240px;">用户名</th>
			<th style="">平仓级别</th>
			<th>锁定用户</th>
			<th>平仓设置</th>
<!-- 			<th>待借入资金</th> -->
			<th>借贷服务费</th>
			<th>杠杆级别</th>
		
<!-- 			 -->
<!-- 			<th>开启状态</th> -->
			 <!-- <th style="width: 120px;">放贷额度</th>  -->
			<th style="width: 55px;">放贷状态</th>
<!-- 			<th style="width: 55px;">可借入</th> -->
			<th style="width: 80px;">操作</th>
		</tr>
	</thead>
		<c:choose>
			<c:when test="${dataList!=null}">
				<!-- <tbody>
					<tr class="space">
						<td colspan="9">
							<div class="operation">
								<input type="checkbox" style="display:none;" id="delAll" class="DeleAllSel" name="checkbox"/><label style="width: 50px;" onclick="selectAll()" class="checkbox" id="ck_delAll"><em>全选</em></label>
								<a class="AButton yellow_button manyJisuan" href="javascript:tongji(0)">统计选中</a>|
								<a class="AButton yellow_button manyJisuan" href="javascript:tongji(true)">统计查询结果</a>
							</div>
						</td>
					</tr>
				</tbody> -->
				<tbody id="staticInfo" style="display:none;" class="item_list item_list_bd item_list_bgcolor">
					<tr>
						<td colspan="2">
							统计结果：
						</td>
						<td>
							<font class="infunds">RMB：<span id="beOutRmb"></span></font><br/>
							<font class="infunds">BTC：<span id="beOutBtc"></span></font><br/>
							<font class="infunds">LTC：<span id="beOutLtc"></span></font><br/>
							<font class="infunds">ETH：<span id="beOutEth"></span></font><br/>
							<font class="infunds">ETC：<span id="beOutEtc"></span></font>
						</td>
						<td>
							<font class="infunds">RMB：<span id="outingRmb"></span></font><br/>
							<font class="infunds">BTC：<span id="outingBtc"></span></font><br/>
							<font class="infunds">LTC：<span id="outingLtc"></span></font><br/>
							<font class="infunds">ETH：<span id="outingEth"></span></font><br/>
							<font class="infunds">ETC：<span id="outingEtc"></span></font>
						</td>
						<td>
							<font class="infunds">RMB：<span id="iningRmb"></span></font><br/>
							<font class="infunds">BTC：<span id="iningBtc"></span></font><br/>
							<font class="infunds">LTC：<span id="iningLtc"></span></font><br/>
							<font class="infunds">ETH：<span id="iningEth"></span></font><br/>
							<font class="infunds">ETC：<span id="iningEtc"></span></font>
						</td>
						<td>
							<font class="infunds">RMB：<span id="overdraftRmb"></span></font><br/>
							<font class="infunds">BTC：<span id="overdraftBtc"></span></font><br/>
							<font class="infunds">LTC：<span id="overdraftLtc"></span></font><br/>
							<font class="infunds">ETH：<span id="overdraftEth"></span></font><br/>
							<font class="infunds">ETC：<span id="overdraftEtc"></span></font>
						</td>
						<td colspan="3">
							--
						</td>
					</tr>
				</tbody>
				
				<c:forEach items="${dataList}" var="list" varStatus="statu">
					<tbody>
						<tr class="space">
							<td colspan="8">
							</td>
						</tr>
					</tbody>
					<tbody>
						<tr class="hd">
							<td colspan="8">
								<input type="checkbox" style="display:none;" value="${list.userId}" id="${statu.index}" class="checkItem"/><label id="ck_${statu.index}" class="checkbox" onclick="changeCheckBox('${statu.index}')"></label>
								<span>用户编号：${list.userId} </span>
								<%-- <c:if test="${not empty list.lastRecord }">
									<span style="color: ${list.lastRecord.createTime>within1day? 'red' : list.lastRecord.createTime>within3day? 'orange' : list.lastRecord.createTime>within5day? '#ffc107' :'#cccccc' };">
									最新备注： 管理员：${list.lastRecord.aUser.admName} 在 <fmt:formatDate value="${list.lastRecord.createTime }" pattern="yyyy-MM-dd HH:mm"/> 添加备注：${list.lastRecord.memo} </span>
								</c:if> --%>
							</td>
						</tr>
					</tbody>
				
					<tbody class="item_list" id="line_${list.userId}">
						<tr>
							<td>
								<div >
									<%-- <div class="pic">
										<a href="javascript:showUser('${list.userId}')"><img src=""/></a>
									</div> --%>
									<div class="txt">
										${list.userName}
										<%--<a href="javascript:showUser('${list.userId}')" style="font-weight: bold;color:green;" id="text_${list.userId }">${list.userName}</a>--%>
									</div>
								</div>
							</td>
							<td> ${list.repayLevel }(
										<font style="color: ${list.repayLevel > 60?'red':''}">
											${list.repayLevelShow }
										</font> 
										)
							</td>
							<td>
								<c:if test="${list.repayLock }">
									<br/>
									<font style="color:red">已锁定</font> <span style="color: green;"><a href="javascript:unlockUser('${list.userId}');">解锁</a></span>
								</c:if>
							</td>
							<td>
								<c:if test="${list.sysForce==0 }"><font style="color:red">禁止系统平仓</font></c:if>
								<c:if test="${list.sysForce==1 }">系统可平仓</c:if>
							</td>
							<td>
								<font color="red"><c:if test="${list.isSetFees==1 }"><fmt:formatNumber value="${list.fees }" pattern="0.00####"/></c:if><c:if test="${list.isSetFees==0 }">默认</c:if></font>
							</td>
							<td>
								${list.loanLever.value } 
							</td>
							<%-- <td>
								<br/>${list.coint.propTag}：${list.unwindPrice  >list.currPrice ? '空▲' : '多▽'}
						       	<c:if test="${list.unwindPrice > 0 && list.currPrice > 0 }">	
						       		<font style="color: ${list.unwindPrice  > list.currPrice ? '#de211d' : '#3dc18e'};">
						       		<fmt:formatNumber value="${list.unwindPrice}" pattern="0.000000##"/>
									(${list.unwindPrice  > list.currPrice ? '+' : ''}<fmt:formatNumber value="${list.unwindPrice -list.currPrice}" pattern="0.0"/>,
									<fmt:formatNumber value="${(list.unwindPrice - list.currPrice)/list.currPrice*100}" pattern="0.0"/>%) 
									</font>
						       	</c:if>
						       
							</td> 
							<td>
							 	<c:if test="${list.outWait.doubleValue()>0 }">
									<font class="infunds">${list.coint.propTag}：<fmt:formatNumber value="${list.outWait.doubleValue() }" pattern="0.000000##"/></font>
									<br/>
								</c:if>
								<c:if test="${list.outWait.doubleValue()==0 }">--</c:if>
 							</td>
							<td>
								<c:if test="${list.outSuccess.doubleValue()>0 }">
									<font class="infunds">${list.coint.propTag}：<fmt:formatNumber value="${list.list.outSuccess.doubleValue() }" pattern="0.000000#"/></font>
									<br/>
								</c:if>
								<c:if test="${list.outSuccess.doubleValue()==0 }">--</c:if>
							</td>
							<td>
								<c:if test="${list.inSuccess.doubleValue()>0 }">
									<font class="outfunds">${list.coint.propTag}：<fmt:formatNumber value="${list.inSuccess.doubleValue() }" pattern="0.000000#"/></font>
									<br/>
								</c:if>
								
								<c:if test="${list.inSuccess.doubleValue()==0 }">--</c:if>
							</td> 
							
							<td>
							 	<c:if test="${list.overdraft.doubleValue()>0 }">
									<font class="">RMB：<fmt:formatNumber value="${list.overdraft.doubleValue()>0 }" pattern="0.000000##"/></font>
									<br/>
								</c:if>
								
								<c:if test="${list.overdraft.doubleValue()==0 }">--</c:if>
							</td>--%>
							<%-- <td>
								${list.loanLever.value }
							</td> --%>
							<%-- <td>
								<c:if test="${list.status==0 }">${L:l(lan,'已关闭')}</c:if>
								<c:if test="${list.status==1 }">${L:l(lan,'已开启')}</c:if>
							</td> --%>
							<!-- 放贷范围	Start -->
							
						<%-- 	<td>
							 	<c:if test="${list.userLend==0 }"> --</c:if>
								<c:if test="${list.userLend==1 }"> <span style="color: orange;">指定额度 </span><br/>
									<c:forEach items="${defaultLimits}" var="defaultLimit">
										<font class="">${defaultLimit.key }：<fmt:formatNumber value="${defaultLimit.value}" pattern="0.00"/></font>
									<br/>
									</c:forEach>
								</c:if>
								
							</td> --%>
							<!-- 放贷范围	End -->
							<td>
								<c:if test="${list.loanOutStatus==0 }">${L:l(lan,'已关闭')}</c:if>
								<c:if test="${list.loanOutStatus==1 }">${L:l(lan,'已开启')}</c:if>
							</td>
							<%-- <td>
								<c:if test="${list.loanInStatus==0 }">${L:l(lan,'禁止')}</c:if>
								<c:if test="${list.loanInStatus==1 }">${L:l(lan,'允许')}</c:if>
							</td> --%>
							<td>
							
								
								<a href="javascript:viewLoanDetail('${list.userId }')">${L:l(lan,'查看借贷明细')}</a>
								<br/>
								<a href="javascript:level('${list.userId }');">${L:l(lan,'修改杠杆')}</a>
								<%-- <br/><a href="javascript:modifyStatus('${list.userId}',${list.status});">
									<c:if test="${list.status==0 }">${L:l(lan,'开启')}</c:if>
									<c:if test="${list.status==1 }">${L:l(lan,'关闭')}</c:if>
								</a> --%>
								
								<br>
								<a href="javascript:modifyForce('${list.userId}',${list.sysForce});">
									<c:if test="${list.sysForce==0 }">系统自动平仓</c:if>
									<c:if test="${list.sysForce==1 }">禁止自动平仓</c:if>
								</a>
								<br/>
								<a href="javascript:modifyFees('${list.userId }')" class="disableCss">${L:l(lan,'修改服务费率')}</a>
								<br/>
								<a href="javascript:modifyLoanOutStatus('${list.userId}',${list.loanOutStatus});">
									<c:if test="${list.loanOutStatus==0 }">${L:l(lan,'开启放贷')}</c:if>
									<c:if test="${list.loanOutStatus==1 }">${L:l(lan,'关闭放贷')}</c:if>
								</a>
								<%--<br/>
								 <a href="javascript:modifyLoanInStatus('${list.userId}',${list.loanInStatus});">
									<c:if test="${list.loanInStatus==0 }">${L:l(lan,'允许被借入')}</c:if>
									<c:if test="${list.loanInStatus==1 }">${L:l(lan,'禁止被借入')}</c:if>
								</a> --%>
								<%-- <br/>
								<a href="javascript:modifyFreeMasterSwitch('${list.userId }',${list.freeMasterSwitch });">
									<c:if test="${list.freeMasterSwitch==0 }"><button>${L:l(lan,'开启免息')}</button></c:if>
									<c:if test="${list.freeMasterSwitch==1 }"><button>${L:l(lan,'关闭免息')}</button></c:if>
								</a> --%>
								
								  <br/>
								<a href="javascript:userLend('${list.userId }',${list.userLend });"> ${L:l(lan,'修改放贷限额 ')}</a> 
						</td>
						</tr>
					</tbody>
				</c:forEach>
				<tfoot>
					<tr>
						<td colspan="8">
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
						<td colspan="9">
							<p>没有符合要求的记录！</p>
						</td>
					</tr>
				</tbody>
			</c:otherwise>
		</c:choose>
</table>