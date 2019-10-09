<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
String type=request.getParameter("type");
%>
<div class="bk-amountMenu" id="bk-amountMenu">
  <%
		if("asset".equals(type)){
	%>
	 <dl>
      <dd><a role="button"><span class="cny"></span>人民币<i class="fa fa-angle-up bk-animated pull-right"></i></a>
     	<ul role="navigation">
       <li><a href="${vip_domain }/u/pay/recharge" target="_self">人民币充值</a></li>
         <li><a href="${vip_domain }/u/pay/cash" target="_self">人民币提现</a></li>
<%--          <li><a href="${vip_domain }/u/pay/fundsdetails" target="_self">人民币明细</a></li> --%>
     </ul>
       </dd> 
      <dd><a role="button"><span class="btc"></span>比特币<i class="fa fa-angle-up bk-animated pull-right"></i></a>
     	<ul role="navigation">
       <li><a href="${vip_domain }/u/btc/uprecord" target="_self">比特币充值</a></li>
         <li><a href="${vip_domain }/u/btc/download" target="_self">比特币提现</a></li>
<%--          <li><a href="${vip_domain }/u/btc/details" target="_self">比特币明细</a></li> --%>
     </ul>
       </dd>
      <dd><a role="button"><span class="ltc"></span>莱特币<i class="fa fa-angle-up bk-animated pull-right"></i></a>
     	<ul role="navigation">
       <li><a href="${vip_domain }/u/ltc/uprecord" target="_self">莱特币充值</a></li>
         <li><a href="${vip_domain }/u/ltc/download" target="_self">莱特币提现</a></li>
<%--          <li><a href="${vip_domain }/u/ltc/details" target="_self">莱特币明细</a></li> --%>
     </ul>
       </dd>
       <dd><a role="button"><span class="p2p"></span>P2P借贷<i class="fa fa-angle-up bk-animated pull-right"></i></a>
     	<ul role="navigation">
       <li><a href="${p2p_domain }/u/loan" target="_self">${L:l(lan,'我要借入') }</a></li>
       <li><a href="${p2p_domain }/u/loan/out" target="_self">${L:l(lan,'我要投资') }</a></li>
       <li><a href="${p2p_domain }/u/rule" target="_self">${L:l(lan,'借贷规则') }</a></li>
     </ul>
       </dd>
      <dd><a role="button"><span class="asset"></span>${L:l(lan,'资产明细') }</a>
        <ul role="navigation">
       <li><a href="${vip_domain }/u/bill" target="_self">${L:l(lan,'综合账单') }</a></li>
     </ul>
      </dd>               
   </dl>
	<%
		}
	%>
	
	<%
		if("trans".equals(type)||type==null){
	%>
          <dl>
             <dd><a role="button"><span class="btc"></span>${L:l(lan,'比特币交易') }<i class="fa fa-angle-up bk-animated pull-right"></i></a>
            	<ul role="navigation">
		            <li><a href="${trans_domain }/btc" target="_self">${L:l(lan,'买入/卖出') }</a></li>
		            <%-- <li><a href="${trans_domain }/markets/BTC" target="_blank">${L:l(lan,'K线行情') }</a></li> --%>
<%-- 		            <li><a href="${trans_domain }/record/moretrans/btc" target="_self">${L:l(lan,'交易记录') }</a></li> --%>
		          </ul>
              </dd> 
             <dd><a role="button"><span class="ltc"></span>${L:l(lan,'莱特币交易') }<i class="fa fa-angle-up bk-animated pull-right"></i></a>
            	<ul role="navigation">
		            <li><a href="${trans_domain }/ltc" target="_self">${L:l(lan,'买入/卖出') }</a></li>
		            <%-- <li><a href="${trans_domain }/markets/BTC" target="_blank">${L:l(lan,'K线行情') }</a></li> --%>
<%-- 		            <li><a href="${trans_domain }/record/moretrans/ltc" target="_self">${L:l(lan,'交易记录') }</a></li> --%>
		          </ul>
              </dd>
             <dd><a role="button"><span class="btq"></span>${L:l(lan,'比特权交易') }<i class="fa fa-angle-up bk-animated pull-right fa-rotate-180"></i></a>
            	<ul role="navigation" style="display:none;">
		            <li><a href="${trans_domain }/btq" target="_self">${L:l(lan,'买入/卖出') }</a></li>
		            <%-- <li><a href="${trans_domain }/markets/BTQ" target="_blank">${L:l(lan,'K线行情') }</a></li> --%>
<%-- 		            <li><a href="${trans_domain }/record/moretrans/btq" target="_self">${L:l(lan,'交易记录') }</a></li> --%>
		          </ul>
              </dd>
              <dd><a role="button"><span class="p2p"></span>P2P借贷<i class="fa fa-angle-up bk-animated pull-right"></i></a>
				     	<ul role="navigation">
				       <li><a href="${p2p_domain }/u/loan" target="_self">${L:l(lan,'我要借入') }</a></li>
				       <li class="p2pLoanOut" style="display:none"><a href="${p2p_domain }/u/loan/out" target="_self">${L:l(lan,'我要投资') }</a></li>
				       <li><a href="${p2p_domain }/u/rule" target="_self">${L:l(lan,'借贷规则') }</a></li>
				     </ul>
				       </dd>                         
             <dd><a role="button"><span class="api"></span>${L:l(lan,'交易') } API<i class="fa fa-angle-up bk-animated pull-right"></i></a>
               <ul role="navigation">
		            <li><a href="${main_domain }/i/developer/restApi" target="_self">REST API</a></li>
		            <li><a href="${main_domain }/i/developer/websocketApi" target="_self">WebSocket API</a></li>
		          </ul>
             </dd>               
          </dl>
	<%
		}
	%>
	
  <%
		if("user".equals(type) ){
	%>
	 <dl>
      <dd><a role="button"><span class="user"></span>用户中心<i class="fa fa-angle-up bk-animated pull-right"></i></a>
     	<ul role="navigation">
       <li><a href="${vip_domain }/u/safe" target="_self">基本资料</a></li>
         <li><a href="${vip_domain }/u/level" target="_self">积分等级</a></li>
     </ul>
       </dd> 
     <dd><a role="button"><span class="safe"></span>安全设置<i class="fa fa-angle-up bk-animated pull-right"></i></a>
     	<ul role="navigation">
         <li><a href="${vip_domain }/u/safe/safepwd/loginset" target="_self">登录密码</a></li>
         <li><a href="${vip_domain }/u/safe/safepwd/payset" target="_self">资金安全密码</a></li>
       <li><a href="${vip_domain }/u/safe/approve/mobile" target="_self">安全手机</a></li>
       <li><a href="${vip_domain }/u/safe/approve/email" target="_self">安全邮箱</a></li>
         <li><a href="${vip_domain }/u/safe/auth" target="_self">实名认证</a></li>
         <li><a href="${vip_domain }/u/safe/safeAuth?oper=0&dealType=googleAuth&dealVal=99" target="_self">Google认证</a></li>
         <li><a href="${vip_domain }/u/safe#userTab" target="_self">登录历史</a></li>
     </ul>
       </dd>
       <dd><a href="${vip_domain }/user/logout" target="_self" role="button"><span class="quit"></span>${L:l(lan,'退出登录') }</a></dd>  
   </dl>
	<%
		}
	%>
    
</div>
<script type="text/javascript">
!(function(){
		$("#bk-amountMenu dd > a").each(function(){
		    $(this).click(function(){
		    	$(this).next().slideToggle(150);
		    	$(this).find(".fa").toggleClass("fa-rotate-180");
		    })
		});
		
		$("#bk-amountMenu li a").each(function(){
			var url=window.location.href;
			var href = $(this).attr("href");
			if(url == href){
				$(this).addClass("active");
			}
			/* var href = $(this).attr("href").split("/");
			var urlSeg = url.split("/");  
			
			if (url.search($(this).attr("href"))>0 && urlSeg[urlSeg.length-1]==href[href.length-1]) {
				$(this).addClass("active");
			}else{
				$(this).removeClass("active");
			} */
		});
	})();

</script>
