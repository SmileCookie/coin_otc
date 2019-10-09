<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>

<style type="text/css">

.into_box6 {
font-family: "微软雅黑";	
}

.into_box6 .fabup2p li h3 {
	width: 480px;
}

#autoForm {
	font-size: 25px;
	padding-left: inherit;
	width: 90px;
	height: 38px;
}

a h2 {
	margin-top: 11px;
}

.into_box6 .fabup2p li.d6 a {
	margin-left: 365px;
}


/*-- xianshi Start --*/
.xianshi {
	width: 1160px;
	height: 90px;
	margin: -2px auto;
	border: hidden;
	text-align: center;
}
.xianshi ul li {
	width: 230px;
	height: 71px;
	border: hidden;
	float: left;
	margin: auto;
}
.xianshi ul li .boo {
	width: 220px;
	height: 70px;
	border: 1px solid #ddd;
	margin: auto;
}
.xianshi ul li .boo h4 {
	margin-top: 8%;
}
/*-- xianshi End --*/

/*-- tz Start --*/
.d5 {
	font-size: 15px;
}
i.tz {
	color: red;
	margin-left: 10px;
}
/*-- tz End --*/

/*-- free Start --*/
.freesw {
	padding-right: 10%;
	font-size: 13px;
	text-align: center;
}
#freeSwitchs {
	width: 165px;
	height: 30px;
	font-size: 18px;
	padding-left: 53px;
	align: center;
	margin: auto;
}
/*-- free End --*/
</style>

 <div class="xianshi">
	<ul>
		<c:if test="${p2pUser!=null && p2pUser.funds!=null }">
			<c:forEach	items="${p2pUser.funds}" var="fund">
				<li>
					<div class="boo">
						<h4> ${L:l(lan,'可用') } ${fund.value.getFt().propTag} <fmt:formatNumber value="${fund.value.balance}" pattern="0.00##" /> </h4>
					</div>
				</li>
			</c:forEach>
		</c:if>
	</ul>
</div>

<div class="into_box1">
	<div id="autop2ps" class="fabup2p">
		<input type="hidden" id="isIn" value="${isIn}" />
		<form action="" method="get" id="loanOut1">
		
			<p>
				<strong style="font-size: 31px;">${L:l(lan,'委托设置：') }</strong> 
				<select id="autoForm" name="autoForm">
					<option value="1" <c:if test="${p2pUser.switchs==1 }">selected="selected"</c:if>>${L:l(lan,'开启') }</option>
					<option value="0" <c:if test="${p2pUser.switchs==0 }">selected="selected"</c:if>>${L:l(lan,'关闭') }</option>
				</select>
			</p>
			<h5 style="color: red; padding-right: 3%;">${L:l(lan,'（注：没输入或0将不委托）') }</h5>
			
			<%--这个是固定一个用户id --%>
 			<c:if test="${p2pUser.userId==140490 }">
				<div class="freesw">
					<strong>${L:l(lan,'是&nbsp;否&nbsp;免&nbsp;息：') }</strong> 
					<select id="freeSwitchs" name="freeSwitchs">
						<option value="0" <c:if test="${p2pUser.freeSwitchs==0 }">selected="selected"</c:if>>${L:l(lan,'关闭') }</option>
						<option value="1" <c:if test="${p2pUser.freeSwitchs==1 }">selected="selected"</c:if>>${L:l(lan,'开启') }</option>
					</select>
				</div>
			</c:if>
			
		
			
			<ul >
				<c:choose>
					<c:when test="${p2pUser.loanOutStatus==1 }">
						<c:if test="${p2pUser.funds!=null}">
							<c:forEach items="${p2pUser.funds}" var="fund">
								<li class="d5">
									<h3>${L:l(lan,'委托低价') }[${fund.value.getFt().propTag}]：</h3> 
									<input type="text" name="value${fund.value.getFt().propTag}" id="value${fund.value.getFt().propTag}" value='<fmt:formatNumber value="${fund.value.entrustThreshold}" pattern="0.00"/>' 
									pattern="num();limit(0 , 22);min(1)" errormsg="${L:l1(lan,'请输入您的委托界限，只允许数字类型，投资最小值%%','>=fund.value.loanLimit') }" 
									style="width: 165px; height: 30px;" placeholder="${L:l(lan,'可用') } ${fund.value.getFt().unitTag} ${fund.value.balance }"
									onkeyup="vip.p2p.xswCheck(this , 2 , 1)" /><%-- <i class="tz">${L:l(lan,'投资低价为') }>=100</i> --%>
								</li>	
							</c:forEach>
						
						</c:if>
						
						<%-- 
						<li class="d5">
							<h3>${L:l(lan,'委托低价') }[RMB]：</h3> 
							<input type="text" name="valueRmb" id="valueRmb" value='<fmt:formatNumber value="${p2pUser.rmbThreshold  }" pattern="0.00"/>' 
							pattern="num();limit(0 , 22);min(100)" errormsg="${L:l1(lan,'请输入您的委托界限，只允许数字类型，投资最小值%%','>=100') }" style="width: 165px; height: 30px;" placeholder="${L:l(lan,'可用') }￥${p2pUser.loansOut[0] }"
							onkeyup="vip.p2p.xswCheck(this , 2 , 1)" /><%-- <i class="tz">${L:l(lan,'投资低价为') }>=100</i> 
						</li>
						<li class="d5">
							<h3>${L:l(lan,'委托低价') }[BTC]：</h3> 
							<input type="text" name="valueBtc" id="valueBtc" value='<fmt:formatNumber value="${p2pUser.btcThreshold  }" pattern="0.00"/>'
							pattern="num();limit(0 , 22);min(1)" errormsg="${L:l1(lan,'请输入您的委托界限，只允许数字类型，投资最小值%%','>=1') }" style="width: 165px; height: 30px;" placeholder="${L:l(lan,'可用') }฿${p2pUser.loansOut[1] }"
							onkeyup="vip.p2p.xswCheck(this , 2 , 1)" /><i class="tz">${L:l(lan,'投资低价为') }>=1</i>
							</li>
						<li class="d5">
							<h3>${L:l(lan,'委托低价') }[LTC]：</h3> 
							<input type="text" name="valueLtc" id="valueLtc" value='<fmt:formatNumber value="${p2pUser.ltcThreshold  }" pattern="0.00"/>' 
							pattern="num();limit(0 , 22);min(10)" errormsg="${L:l1(lan,'请输入您的委托界限，只允许数字类型，投资最小值%%','>=10') }" style="width: 165px; height: 30px;" placeholder="${L:l(lan,'可用 ') }Ł${p2pUser.loansOut[2] }"
							onkeyup="vip.p2p.xswCheck(this , 2 , 1)" /><i class="tz">${L:l(lan,'投资低价为') }>=10</i>
								</li>
						<li class="d5">
							<h3>${L:l(lan,'委托低价') }[ETH]：</h3> 
							<input type="text" name="valueEth" id="valueEth" value='<fmt:formatNumber value="${p2pUser.ethThreshold  }" pattern="0.00"/>'
							pattern="num();limit(0 , 22);min(10)" errormsg="${L:l1(lan,'请输入您的委托界限，只允许数字类型，投资最小值%%','>=10') }" style="width: 165px; height: 30px;" placeholder="${L:l(lan,'可用') }E${p2pUser.loansOut[3] }"
							onkeyup="vip.p2p.xswCheck(this , 2 , 1)" /><i class="tz">${L:l(lan,'投资低价为') }>=10</i>
							</li>
						<li class="d5 etcDisplay">
							<h3>${L:l(lan,'委托低价') }[ETC]：</h3> 
							<input type="text" name="valueEtc" id="valueEtc" value='<fmt:formatNumber value="${p2pUser.etcThreshold  }" pattern="0.00"/>'
							pattern="num();limit(0 , 22);min(10)" errormsg="${L:l1(lan,'请输入您的委托界限，只允许数字类型，投资最小值%%','>=10') }" style="width: 165px; height: 30px;" placeholder="${L:l(lan,'可用') }e${p2pUser.loansOut[4] }"
							onkeyup="vip.p2p.xswCheck(this , 2 , 1)" /><i class="tz">${L:l(lan,'投资低价为') }>=10</i>
							</li> --%>
						<li class="d6">
<!-- 							<input type="hidden" name="autoForm" id="autoForm" value="1" />  -->
							<input type="hidden" name="riskManage" id="riskManage" value="1" /> 
							<a href="javascript:vip.p2p.doAutoOut(false);"><h2>${L:l(lan,'保存信息') }</h2></a>
							</li>
					</c:when>
					<c:otherwise>
						<li class="d1"><h4>${L:l(lan,'停止个人发布P2P借贷公告：') }</h4></li>
						<div class="ctips" style="margin-top: 15px;">
							<p style="font-size: 14px; line-height: 25px; text-align: left;">${L:l(lan,'自即日起，停止个人发布新的P2P借贷信息，之前发布的借贷信息仍然有效。') }</p>
							<p style="font-size: 14px; line-height: 25px; text-align: right; margin-top: 30px;">
								${L:l(lan,'比特全球(vip)') }<br />2015年4月1日
							</p>
						</div>
					</c:otherwise>
				</c:choose>
			</ul>
		</form>
		<div class="d_quick_loan" id="shopslist" style="display: none"></div>
	</div>
</div>
<script type="text/javascript">
	function min(s, value, id) {
		var textValue = parseFloat(s);
		if (textValue == 0 || textValue >= value) {
			return true;
		} else {

			return false;
		}
	}
</script>
