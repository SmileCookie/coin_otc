<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8" contentType="text/html;charset=utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="cn" lang="cn">
<head>
<title>Btcwinex Administrator Center</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<jsp:include page="/admins/top.jsp" />
	<script type="text/javascript">
	$(function(){//span_timer
	 	$('body').Ui();

	 	  $("body").keydown(function(event){
	 		 event.keyCode==13 && doLogin(); 
	 	});
	});
	function doLogin()
	{
		if($("#UserName").val()=="UserName"){
			Wrong("请输入用户名。");
			return;
		}
		if($("#Password").val()=="password"){
			Wrong("请输入密码。");
			return;
		}
<%--		if($("#mCode").val()=="Valid Code"){--%>
<%--			Wrong("请输入验证码。");--%>
<%--			return;--%>
<%--		}--%>
		
		var datas=FormToStr("loginFRM");
  		if(datas==null)
  		return;
		$.ajax({
			   async:true,
			   cache:false,
			   type:"POST",
			   dataType:"xml",
			   data:datas,
			   url:"/ad_admin/admin_login/dologin",
			   error:function(xml){},
			   timeout:60000,
			   contentType : "application/x-www-form-urlencoded; charset=UTF-8",
			   success:function(xml){
			   Pause(this,300); //0.3秒后执行后续代码，等待lodding结束,不然网络很快的情况就会导致效果顺序得当
	           this.NextStep = function(){
	            if($(xml).find("State").text()=="true")
	            {
	             //新添加的id
	             var id=$(xml).find("MainData").text();
	           //  Right($(xml).find("Des").text()+",FullTestId号为:"+id);
	           //  $.cookie("CurrentState",null);
	             Redirect("/ad_admin/admin_manage");
	             	//跳转
	             //做些什么..,比如Right里面加入回调函数,删除指定div的id等等
	            }else{
	              Wrong($(xml).find("MainData").text());
	              // Alert("数据提交成功+"+xml);
	            }
	          };
			 }
			});
	}

	</script>
	<style type="text/css">
	 
    body
    {
		overflow-x:hidden;overflow-y:hidden; 
	background-color:#0787ac;
	margin: 0px;
	padding: 0px;
    }
    .login {
	padding-top: 300px;
	padding-left:240px;

    }
.main
    {
		background:url(${static_domain }/statics/img/admin/adminbg.jpg) no-repeat 50% 50%;
	height:600px;
	width:1000px;
	 margin:0 auto;
    }
	  .login-main {
	height: 210px;
    }
    .messages div
    { padding-bottom:18px;padding-left:7px;}
	
	.messages input{ border: none; background-image:none; background-color:#fff; height:36px; line-height:36px; width:215px;background:none;}
       .messages .left
       {
	float:left;
	width: 140px;
	margin-top: 81px;
       }
       .messages .left input
       {
       background:#000;
	color:#142446;
	font-family:arial;
	font-size:14px;
	font-weight:bold;
	height:18px;
	width:140px;

	line-height: 18px;
	margin: 0px;
	padding: 0px;
	border-top-width: 0px;
	border-right-width: 0px;
	border-bottom-width: 0px;
	border-left-width: 0px;
	border-top-style: solid;
	border-right-style: solid;
	border-bottom-style: solid;
	border-left-style: solid;
       }
       .messages .right
       {
	color:#283B4E;
	text-shadow:0 1px 0 #E0ECFF;
	font-family: "宋体";
	font-size: 12px;
	float: left;
	width: 114px;
	margin-left: 15px;
	line-height: 18px;
	margin-top: 75px;
 }
 .botton
 {
	 overflow:hidden;zoom:1; padding-top:20px;}
       .botton a{
		   float:left; width:84px; height:27px;margin-right:4px; display:inline;
       }
      .botton a#A1{ width:27px;}
       #loginFRM
       {
       width:100%;
       }
 
 input{height:30px;line-height:30px;}

#onLogin{ display: block;
    float: left;
    font-size: 30px;
    height: 35px;
    line-height: 35px;
    margin-left: 218px;
    text-align: center;
    width: 110px;}
    
    
    
.form-code img {
    border: 1px solid #006684;
    cursor: pointer;
    float: left;
}

.form-code a{float: none;}

*html .form-code{padding:-30px 0 0 -20px;}

    </style>
</head>

<body>
	<form name="loginFRM" id="loginFRM">
<div class="main">
	<div class="login">
		<div class="login-main">
			<div class="messages">
				<div style="width:220px;float:left;">
					<input type="text" position="s" name="UserName" valueDemo="用户名" id="UserName" mytitle="" />
				</div>
				<div  style="width:220px;margin-left:15px;float:left;">
					<input position="s" valueDemo="密码" type="password" name="Password" id="Password" mytitle="" />
				</div>
			
				<div style="clear:both;padding-top:8px;float:left;width: 700px;">
			
					<input position="n" valueDemo="谷歌验证码" style="width:140px;float:left;color:#96aeb8;" type="text" name="mCode" id="mCode" mytitle="please input google code"  />
			
					<a href="javascript:doLogin();" id="onLogin">登录</a>
				</div>
										
			</div>
		          
		</div>
	</div>
</div>
	</form>

  </body>
</html>
