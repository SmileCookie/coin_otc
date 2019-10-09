<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>注册新用户-${WEB_NAME }-${WEB_TITLE }</title>
<jsp:include page="/common/head.jsp" />
<style type="text/css">
	html,body{
		height:100%;
	}
</style>
<script type="text/javascript">
try{
	parent.document.domain;
}catch(e){
	document.domain="${baseDomain}";
}
var timeInterval = "";
function sendemail(nid) {
	var actionUrl = "/user/repostE?nid="+nid;
	vip.ajax( {
		url : actionUrl,
		dataType : "json",
		suc : function(json) {
			parent.Right(json.des, {callback:"emailRemind("+nid+")"});
		},
		err : function(json){
			$(".cx").html("发送成功").attr("disabled","disabled");
			window.clearInterval(timeInterval);
			timeInterval = window.setInterval(function(){
				$(".cx").html("${L:l(lan,'重新发送')}").removeAttr("disabled");
			}, 60000);
		}
	});
}
</script>
</head> 
<body style="background-color:#fff;">
<div>
   <div class="side_r ld">
      <div class="b_comon_box">
         <div class="main-bd" id="mainForm">
            <div>
               <div class="rtips" >
                  <p><span>${email }</span> ${L:l(lan,'您的邮箱将收到一封邮件')}请在24小时内点击链接完成注册。</p>
                  <div class="dlyx"><a target="_blank" href="http://mail.${fn:split(email, '@')[1]}" class="alibtn_orange35" style="width:250px;"><h4 style="width:235px;">${L:l(lan,'登录到邮箱')}</h4></a></div>
               </div>
               <div style="margin-top:25px;" class="ctips">
                  <p>${L:l(lan,'没有收到请重新发送')}<button type="button" class="cx" onclick="sendemail(${nid})">${L:l(lan,'重新发送')}</button></p>
               </div>
            </div>
          </div>
      </div>
   </div>
</div>
</body>
</html>
