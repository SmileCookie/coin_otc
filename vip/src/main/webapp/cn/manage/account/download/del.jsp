<!doctype html>
<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>新增比特币接收地址-${WEB_NAME }-${WEB_TITLE }</title>
<jsp:include page="/common/head.jsp" />
<link rel=stylesheet type=text/css href="${static_domain }/statics/css/fast.css" />

<script type="text/javascript">
	$(function(){
	
		$("#bankBox").Ui();
		$("#okSet").bind("click",function(){
			  var datas=FormToStr("bankBox");
			  if(datas==null)
			  return;
			  $("#bankBox").Loadding({OffsetXGIF:270,OffsetYGIF:80});
			    $.ajax({
					   async:true,
					   cache:false,
					   type:"POST",
					   dataType:"xml",
					   data:datas,
					   url:"doDel",
					   error:function(xml){JuaBox.sure("netWork error!");},
					   timeout:60000,
					   success:function(xml){
						   $("#bankBox").Loadding({IsShow:false}); 
				           if($(xml).find("State").text()=="true"){
				        	   		JuaBox.sure($(xml).find("MainData").text(),{
				        	   			closeFun:function(){
				        	   				window.location.reload()
				        	   			}}
				        	   		);
					        }else{
					             JuaBox.sure($(xml).find("MainData").text());
					        }
						}//ajax调用成功处理函数结束
				});//ajax结束
			});//本处理函数结束
	});
</script>
<style type="text/css">
.main-bd .form-line {
    padding-left: 0px;
}
.form-con .txt{float: left;}
</style>
</head>

<body >

<div class="bankbox" id="bankBox">

<div class="main-bd" style="border:none;">
 
	<div class="form-line">
		<div class="form-tit">${L:l(lan,"BTC接收地址：")}</div>
		<div class="form-con">${receive.address}</div>
	</div>
 
	<div class="form-line">
		<div class="form-tit">${L:l(lan,"资金安全密码")}：</div>
		<div class="form-con">
			<input class="input" type="password" style="width:155px;" name="payPass" id="payPass" value="" mytitle="${L:l(lan,'请输入您的资金安全密码')}" errormsg="${L:l(lan,'请确认资金安全密码是否正确')}" errorName="${L:l(lan,'资金安全密码')}" pattern="limit(4,16)"/>
			<span><a href="/ac/safepwd_find" target="_blank">${L:l(lan,'忘记资金安全密码')}</a></span>
		</div>
	</div>	
	
	<div class="form-line">
    	<div  class="form-tit">${L:l(lan,"短信验证码")}：</div>
	    <div class="form-con">
	    	<input type="password" id="mCode" name="mCode" style="width:155px;" class="input" position="n" mytitle="${L:l(lan,'请输入发送到您手机上的验证码')}" errormsg="${L:l(lan,'验证码错误')}" errorName="${L:l(lan,'验证码')}" pattern="limit(4,10)"/>
	    	<jsp:include page="/en/sms_buttons.jsp"></jsp:include>
	    </div>
    </div>
    <input type="hidden" id="needMobile" name="needMobile" value="false"/>
	<input type="hidden" id="needPwd" name="needPwd" value="true"/>
	
	<c:if test="${googleAuth==2}">
		<div class="form-line">
	    	<div  class="form-tit">谷歌验证码：</div>
		    <div class="form-con">
		    	<input type="password" class="input" name="code" id="code" value="" mytitle="请输入移动设备上生成的验证码。" errormsg="${L:l(lan,'验证码错误')}" pattern="limit(4,10)"/>
		    </div>
	    </div>
	</c:if>
	  <div class="form-line" style="height: 20px;">
         <div  class="form-tit">&nbsp;</div>
          <div class="form-con" id="errormsg" style="color: #ff0000;"></div>
       </div>
  	<hr/>
	<input type="hidden" id="receiveId" name="receiveId" value="${receive.btcReciveAddressId}"/>
	<div class="do" ><a href="javascript:parent.Close()" tabindex="5" class="alibtn_orange35"><h4>${L:l(lan,"取消")}</h4></a> <a href="javascript:;" id="okSet" style="margin-left:36px;" tabindex="8" class="alibtn_orange35"><h4>${L:l(lan,"确认")}</h4></a></div>
       
  
</div>
</div>

</body>
</html>
