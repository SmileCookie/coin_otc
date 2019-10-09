<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="b_usertips clearfloat">
    <c:set value="${curUser.userContact.maoLink }" var="mao"/>
	<div class="ld"><p><span>${helloWord }</span>，<i>${curUser.userName}</i>&nbsp;&nbsp;上次登录时间:<i><fmt:formatDate value="${curUser.previousLogin}" pattern="yyyy-MM-dd HH:mm:ss"/></i>&nbsp;&nbsp;登录IP:<i>${curUser.trueIp}</i></p></div>
	<div class="rd"><span class="ld">您当前安全认证等级：</span><div class="jindu ld"><div style="width:${curUser.userContact.approveLevel}%;" title="提高安全等级能有效保证资金安全"></div></div><span class="ld"><c:if test="${not empty mao }"><a href="javascript:void(0);" onclick="upgrade('${mao}')">升级</a></c:if></span></div>
</div>
<div class="b_usertab clearfloat">
  <ul>
    <li class="t1"><a href="${vip_domain }/u" target="_self"><h2>财务概况</h2><p>您的资产概况，以及便捷充值</p></a></li>
    <li class="t2"><a href="${vip_domain }/u/safe" target="_self"><h2>认证与安全</h2><p>用户基本信息与安全认证</p></a></li>
    <li class="t3"><a href="${vip_domain }/u/level" target="_self"><h2>用户等级</h2><p>系统级别及权限</p></a></li>
  </ul>
<script type="text/javascript">
$(function(){
	var curUrl = document.location.pathname;
   	var payType = ["/u/pay","/u/btc","/u/ltc","/u/score","/u/bill"];
   	var isPay = false;
   	for(var i=0;i<payType.length;i++){
		if(curUrl.indexOf(payType[i]) >= 0){
   			isPay=true;
   			break;
   		}
   	}
   	if(isPay){
   		$(".b_usertab li").removeClass("on");
   		$(".b_usertab li.t1").addClass("on");
   	}else{
   		$(".b_usertab li").removeClass("on");
   		$(".b_usertab li.t2").addClass("on");
   	}
});
</script>
</div>
