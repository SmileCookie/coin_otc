<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
String type=request.getParameter("type");
%>
<div class="bk-amountMenu" id="bk-amountMenu">
          <dl>
             <dd><a role="button"><span class="btc"></span>${L:l(lan,'比特币交易') }<i class="fa fa-angle-up bk-animated pull-right"></i></a>
            	<ul role="navigation">
		            <li><a href="/v2/btc" target="_self">${L:l(lan,'买入/卖出') }</a></li>
		            <li><a href="" target="_self">${L:l(lan,'K线行情') }</a></li>
		            <li><a href="" target="_self">${L:l(lan,'交易记录') }</a></li>
		          </ul>
              </dd> 
             <dd><a role="button"><span class="ltc"></span>${L:l(lan,'莱特币交易') }<i class="fa fa-angle-up bk-animated pull-right"></i></a>
            	<ul role="navigation">
		            <li><a href="" target="_self">${L:l(lan,'买入/卖出') }</a></li>
		            <li><a href="" target="_self">${L:l(lan,'K线行情') }</a></li>
		            <li><a href="" target="_self">${L:l(lan,'交易记录') }</a></li>
		          </ul>
              </dd>
             <dd><a role="button"><span class="btq"></span>${L:l(lan,'比特权交易') }<i class="fa fa-angle-up bk-animated pull-right"></i></a>
            	<ul role="navigation">
		            <li><a href="" target="_self">${L:l(lan,'买入/卖出') }</a></li>
		            <li><a href="" target="_self">${L:l(lan,'交易记录') }</a></li>
		          </ul>
              </dd>                             
             <dd><a role="button"><span class="p2p"></span>${L:l(lan,'杠杆') }<i class="fa fa-angle-up bk-animated pull-right"></i></a>
            	<ul role="navigation">
		            <li><a href="" target="_self">${L:l(lan,'杠杆申请') }</a></li>
		            <li><a href="" target="_self">${L:l(lan,'杠杆管理') }</a></li>
		            <li><a href="" target="_self">${L:l(lan,'免息券') }</a></li>
		            <li><a href="" target="_self">${L:l(lan,'杠杆规则') }</a></li>
		          </ul>
              </dd> 
             <dd><a href="" target="_self" role="button"><span class="api"></span>${L:l(lan,'交易') } API</a></dd>               
          </dl>
</div>
<script type="text/javascript">
seajs.use(['module_lang', 'module_base', 'module_common'], function(a,b,c) {
	  //JuaBox.info(000);
	;(function(){
		$("#bk-amountMenu dd > a").each(function(){
		    $(this).click(function(){
		    	$(this).next().slideToggle(150);
		    	$(this).find(".fa").toggleClass("fa-rotate-180");
		    })
		});
		
		var url=window.location.href;
		$("#bk-amountMenu li a").each(function(){
			var href = $(this).attr("href").split("/");
			var urlSeg = url.split("/");  
			if (url.search($(this).attr("href"))>0 && urlSeg[urlSeg.length-1]==href[href.length-1]) {
				$(this).addClass("active");
			}else{
				$(this).removeClass("active");
			}
		});
	})();
});	
</script>
