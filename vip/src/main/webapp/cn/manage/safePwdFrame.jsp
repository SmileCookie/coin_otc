<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 
<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
<jsp:include page="/common/head.jsp" />
<link rel=stylesheet type=text/css href="${static_domain }/statics/css/fast.css" />
<!-- <script type="text/javascript" charset="utf-8">
  try{
    var oldDomain=document.domain;
    var ind=oldDomain.indexOf('vip');
    document.domain = oldDomain.substring(ind,oldDomain.length)
  } catch(msg) {
    document.domain = 'vip.com';  
  }
</script> -->
<script type="text/javascript">
$(function(){
	$("#mainForm").Ui();
})
function submit(){
	vip.ajax({
		url : "/manage/useOrCloseSafePwd?callback=?", 
		dataType : "jsonp", 
		formId : "mainForm",
		suc:function(json){
			parent.Right(json.des, {callback:"window.location.reload()"});
		},
		err : function(json){
			errTo(json.des);
		}
	});
}

function errTo(msg){
	$("#errormsg").text(msg);
	Pause(this, 5000);
   	this.NextStep = function(){
   		$("#errormsg").text("");
   	}
}
</script>
<style type="text/css">
.main-bd{border: none;}
.main-bd .form-line{padding-left:0px !important;}
hr { margin:0 auto; width:100%; clear:both;}
.main-bd .form-line .form-con { *width:330px;}
.form-con i {
    float: left;
}
</style>
</head> 
<body>

<div class="main-bd" id="mainForm">
   <div class="form-line">
      <div class="form-tit" style="width:100px;">${L:l(lan,'资金安全密码')}：</div>
      <div class="form-con">
         <input class="input" style="width:200px;" type="password" id="payPass"  name="payPass" mytitle="${L:l(lan,'请输入您当前的资金安全密码')}" errormsg="${L:l(lan,'确认密码格式是否正确')}" pattern="limit(6,20)" errorName="${L:l(lan,'当前密码')}"/>
         <span><a target="_blank" href="/ac/password_find">${L:l(lan,'忘记登录密码')}</a></span>
      </div>
   </div>
   <div class="form-line" style="height: auto;">
      <div class="form-tit" style="width:100px;">${L:l(lan,'关闭状态')}：</div>
      <div class="form-con">
      	<span class="jqTransformRadioWrapper" style="margin: 12px 6px 0;"><a style="cursor:pointer;" class="jqTransformRadio"></a><input type="radio" checked="checked" value="6" name="closeStatu" class="" style="display: none;"/></span> <i>${L:l(lan,'关闭6小时')}</i> 
        <span class="jqTransformRadioWrapper" style="margin: 12px 6px 0 20px;"><a style="cursor:pointer;" class="jqTransformRadio"></a><input type="radio" value="1" name="closeStatu" class="" style="display: none;"/></span> <i>${L:l(lan,'永久关闭')}</i>
      </div>
   </div>
   <%-- 
   <div class="form-line" style="disply:none;">
      <div  class="form-tit">${L:l(lan,"短信验证码")}：</div>
      <div class="form-con">
         <input type="password" id="mCode" name="mCode" style="width:100px;" class="input" position="n" mytitle="${L:l(lan,'请输入发送到您手机上的验证码')}" errormsg="${L:l(lan,'验证码错误')}" errorName="${L:l(lan,'验证码')}" pattern="limit(0,10)"/>
         <jsp:include page="/en/sms_buttons.jsp"></jsp:include>
      </div>
   </div>
    --%>
   <input type="hidden" id="needMobile" name="needMobile" value="false"/>
   <input type="hidden" id="needPwd" name="needPwd" value="true"/>
 <%-- 
   <c:if test="${googleAuth==2}">
      <div class="form-line">
         <div  class="form-tit">谷歌验证码：</div>
          <div class="form-con">
            <input type="password" class="input" style="width:100px;" name="code" id="code" value="" mytitle="请输入移动设备上生成的验证码。" errormsg="${L:l(lan,'验证码错误')}" pattern="limit(4,10)"/>
          </div>
       </div>
   </c:if>
   --%>
      <div class="form-line" style="height: 20px;margin-bottom: 10px;">
         <div  class="form-tit">&nbsp;</div>
          <div class="form-con" id="errormsg" style="color: #ff0000;"></div>
       </div>
   <hr/>
   
   <div class="do" >
      <a href="javascript:parent.Close()" tabindex="5" class="alibtn_orange35"><h4>${L:l(lan,'取消')}</h4></a> 
      <a href="javascript:submit()" style="margin-left:36px;" tabindex="8" class="alibtn_orange35"><h4>${L:l(lan,'提交')}</h4></a>
   </div>
 
</div>
</body>
</html>
