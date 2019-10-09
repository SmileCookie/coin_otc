<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="com.world.web.action.Action"%>
<%@page import="com.world.config.GlobalConfig"%>
<%
	String header = GlobalConfig.session;
	String vip_domain=Action.VIP_DOMAIN;
	String static_domain=Action.STATIC_DOMAIN;
	String main_domain=Action.MAIN_DOMAIN;
	String p2p_domain=Action.P2P_DOMAIN;
	String trans_domain=Action.TRANS_DOMAIN;
 %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>${L:l(lan,'用户登录')}-${WEB_NAME }-${WEB_TITLE }</title>
<link href="${static_domain }/statics/css/cn/user/logF.css" rel="stylesheet" type="text/css" />
<script type="text/javascript">
var JsCommon={uon:"<%=header%>uon",uname:"<%=header%>uname",uid:"<%=header%>uid",aid:"<%=header%>aid",rid:"<%=header%>rid",aname:"<%=header%>aname",note:"<%=header%>note",lan:"<%=header%>lan",mainDomain:"<%=main_domain%>",vipDomain:"<%=vip_domain%>",p2pDomain:"<%=p2p_domain%>",transDomain:"<%=trans_domain%>",staticDomain:"<%=static_domain%>"};
  try{
    var oldDomain=document.domain;
    var numm1=oldDomain.indexOf('${baseDomain}');
    document.domain = oldDomain.substring(numm1,oldDomain.length)
  } catch(msg) {
    document.domain = '${baseDomain}';  
  }
</script>
<script type="text/javascript" src="${static_domain }/statics/js/common/jquery.js"></script>
<script type="text/javascript" src="${static_domain }/statics/js/common/webcommon.js"></script>
<script type="text/javascript">
$(function(){
  $("body").bind("click",function(){$("#message").hide()});
  $("#nick").val("");
   $("#psw").val("");
   $("#code").val("");
 $("input").each(function(){
    $(this).focus(function(){
      $(this).addClass("focus");
  }).blur(function(){
  	if($(this).val().length==0){
	    $(this).removeClass("focus");
	  }else{
     	$(this).addClass("focus");
	  }
  });
	});
$("#do").bind("mouseenter",function(){
   $(this).addClass("on");
 }).bind("mouseleave",function(){
     $(this).removeClass("on");
}); 

 var LoginCode=$.cookie("LoginCode");
if(LoginCode=="1")
{
	$("#codeContainer").show();
	parent.NetBox.resize(550,400);
	getCode();
}

      $("body").bind("keyup", function(event){
         	if (event.keyCode=="13"){
         		$("#doLogins").trigger('click');
         	}
      });

  });
  
   function numberID(){
   return Math.round(Math.random()*10000)*Math.round(Math.random()*10000);
 }
 
  function singin(){
    if($("#nike").val().length<2){
      error('${L:l(lan,"请填写用户名或邮箱")}');
    }else if($("#pwd").val().length<6){
      error('${L:l(lan,"请输入登录密码")}');
    }
    else{
        $("#topbar").animate({"margin-left":"-550px"}, 200,function(){ $("#code").focus();
        });
       
      }
  }
   function pre(){
     $("#topbar").animate({"margin-left":"0px"}, 200);
  }
  function Go1(){
   $("#remember").val("2");
    $("#check").css({"background-position":"0 -1px"});
  }
   function Go2(){
    $("#remember").val("12");
    $("#check").css({"background-position":"0 -40px"});
  }
   function Go3(){
    $("#remember").val("24");
    $("#check").css({"background-position":"0 -81px"});
  }
   function Go4(){
    $("#remember").val("168");
    $("#check").css({"background-position":"0 -120px"}); 
  }

function getCode(){
   var id=numberID();
   $("#idCode").attr("src","/imagecode/get-28-85-39-"+id);
} 

function FormToStr(){
	var str="";
	if($("#nike").val().length<2||$("#nike").val().length>50){
	    $("#topbar").animate({"margin-left":"0px"}, 200);
	    $("#nike").focus();
	   error('${L:l(lan,"请填写用户名或邮箱")}');
	   return null;
	}else
	   str+="&nike="+encodeURIComponent($("#nike").val());
        str+="&returnTo="+encodeURIComponent($("#returnTo").val());
	   
	if($("#pwd").val().length<6||$("#pwd").val().length>50){
	    $("#topbar").animate({"margin-left":"0px"}, 200);
	      $("#pwd").focus();
	   error('${L:l(lan,"请输入正确的密码")}');
	   return null;
	}else
	   str+="&pwd="+encodeURIComponent($("#pwd").val());
	  
	   str+="&remember="+encodeURIComponent($("#remember").val());
	  
	     
	    if($.cookie("LoginCode")=="1"){
			if($("#code").val().length!=4){
			    $("#topbar").animate({"margin-left":"-440px"}, 200);
			      $("#code").focus();
			   error('${L:l(lan,"请输入验证码")}');
			   return null;
			}else
		   		str+="&code="+encodeURIComponent($("#code").val());
		   
		}
	    if($.cookie("mobileCode")=="1"){
			if($("#mobileCode").val().length!=6){
			    $("#topbar").animate({"margin-left":"-440px"}, 200);
			      $("#mobileCode").focus();
			   error('${L:l(lan,"请输入手机验证码")}');
			   return null;
			}else
		   		str+="&mobileCode="+encodeURIComponent($("#mobileCode").val());
		   
		}
	  return str.substring(1,str.length);
}


function getQueryString(name) {
var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
var r = window.location.search.substr(1).match(reg);
if (r != null) return unescape(r[2]); return null;
}


var inAjaxing=false;
function login(isSafe){

	if(inAjaxing){
		return;
	}
    var datas = FormToStr();
    if(datas == null){
    	inAjaxing = false;
    	return;
    }

    if(isSafe)
        datas=datas+"&safe=1";
     else
        datas=datas+"&safe=0"; 
	inAjaxing = true;
	datas += "&domain=${domain}"
	$.getJSON("${vip_domain}/user/doLogin?callback=?", datas ,function(json) {
		var maindata=json.des;
		if(json.isSuc){
	      	if($.cookie(ZNAME+"fromurl")){
								window.top.location.href = $.cookie(ZNAME+"fromurl");
			 		 }else{
			 				Redirect(mainData);
			 		 }
			  /* if(maindata.indexOf("/u/security") > 0 ){
	        	  parent.Redirect(maindata);
	          } else if(maindata.indexOf("jua.com")>0){
	          	  window.parent.location = maindata;
	          } else{
  	          	  parent.Redirect(maindata);
	          } */
		      // Redirect(maindata);
		}else{
			  $("#code").val("");
              if(maindata=="验证码错误，请重新输入。"){
	       	      error(maindata);
	       	      $("#codeContainer").slideDown();
	       	      parent.NetBox.resize(550,400);
              }else{
              	   if(maindata.indexOf("手机")>=0){
              	   	parent.Redirect(vip.vipDomain+"/user/login");
              	   	return;
	           	   }
            	  $("#pwd").val("").focus();
            	  error(maindata);
              }
              getCode();
			  inAjaxing = false;
		}
	});
}

function loginTwo(){
 	var datas = FormToStr("topbarQ");
    if(datas == null)
    	return;
	parent.Iframe({
		Url : "${vip_domain}/user/mobileLogin?"+datas,
		zoomSpeedIn : 200,
		zoomSpeedOut : 200,
		Width : 450,
		Height: 300,
		isShowIframeTitle:false,
		Title : "异地登录验证"
	});	  
}
function Redirect(url){
	self.location=url;
}
 function error(msg){
  $("#message").html(msg).fadeIn(300).delay(1000).fadeOut(300);
  }
  
function showMobileCode(){
	var mobileCode=$.cookie("mobileCode");
	if(mobileCode!=null){
		$("#nick").val(mobileCode).focus();
		$("#mobileCodeShow").show();
		parent.NetBox.resize(550,500);
		$("#ajax_phone_get").bind("click", getMobileCode);
		waitTime();
		return true;
	}
	return false;
}
  
function getMobileCode(){
	var mobileCode=$.cookie("mobileCode");
	var actionUrl = "${vip_domain}/user/postcode?uid="+encodeURIComponent(mobileCode);
	vip.ajax({
		url : actionUrl,
		dataType : "json",
		suc : function(json) {
			waitTime();
		}
	});
}

var timeInterval = "";
window.clearInterval(timeInterval);
function waitTime(){
	$("#ajax_phone_get").attr("disabled", "disabled");
	var count = 120;
	window.clearInterval(timeInterval);
	var timeInterval = window.setInterval(function(){
		count --;
		if(count == 0){
			window.clearInterval(timeInterval);
			$("#ajax_phone_get").text("获取验证码").removeAttr("disabled");	
		}else{
			$("#ajax_phone_get").text(count+"秒后重新获取");								
		}
	}, 1000);
} 
</script>
</head>
<body>
<!-- top line1 -->

<div id="message">username or password error！</div>
    <div class="stepOne" id="topbarQ">
      <div id="username">
        <input class="txt" tabindex="1" size="50" type="text" name="nike" id="nike" />
      </div>
      <div id="password">
        <input tabindex="2" class="txt" size="35" type="password" name="pwd" id="pwd" />
      </div>
      
		<%-- <div class="form-line" id="mobileCodeShow" style="margin-top:5px; width:100%;display: none;">
	       	<div class="form-tit">${L:l(lan,"手机验证码")}：</div>
	       	<div class="form-con" style="width:340px;float:left;">
	         		<input class="txt" style="width:100px;height:40px; padding:0px 10px;" position="n" type="text" name="mobileCode" mytitle='${L:l(lan,"请输入手机验证码")}' errormsg="手机验证码错误，请重新输入。" id="mobileCode" pattern="limit(6,6)" tabindex="3" />
	        	<button type="button" id="ajax_phone_get">${L:l(lan,"获取验证码")}</button>
	        </div>
     	</div> --%>
      
      <div class="form-line" id="codeContainer" style="display: none;">
      <!--<div class="form-tit">${L:l(lan,"验证码")}：</div>-->
      <div class="form-con" style="width:250px;float:left;">
        <input class="input" tabindex="3" type="text" name="code" errmsg='${L:l(lan,"请输入验证码")}' id="code"  pattern="limit(4,4)" />
      </div>
      <div class="form-code" style="margin-top:10px;"><img title="点击刷新验证码" onclick="getCode()" src="/imagecode/get-28-85-39" id="idCode" /> </div>
    </div>
    <div id="do" class="do" style="margin-top:10px;"><a href="/user/register" tabindex="5" target="_blank">${L:l(lan,"注册")}</a> <a href="javascript:;" id="doLogins" onclick="login(true)" tabindex="4" style="margin-left:36px;">${L:l(lan,"登录")}</a></div>
    <div id="forgot" style="text-align:right;"><a href="/ac/password_find"  tabindex="5" target="_blank">${L:l(lan,"忘记密码")}?</a> &nbsp;&nbsp; <a href="javascript:parent.Close()">${L:l(lan,"取消")}</a></div>
</div>
</body>
</html>
