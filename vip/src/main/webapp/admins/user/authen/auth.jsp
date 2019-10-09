<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<jsp:include page="/admins/top.jsp" />		
		<style type="text/css">

.form-tit {
float:left;
line-height:32px;
width:100px;
padding-right: 10px;
text-align: right;
}

.form-con{float:left;}

.form-line {margin-left:20px;}
.col-main .prompt.b_yellow.remind2.pl_35 {
	padding:0 10px 20px 10px;
}
.form-btn {
    padding: 15px 0 0 100px;
}
.select_wrap select {
    color: #6D6D6D;
    float: left;
 
    padding: 5px;
}

.bankbox{ padding:15px;}
.bankbox .bd {
	padding-right: 20px;
	padding-left: 20px;
}
.formlist .formline {
	overflow:hidden;
	padding-bottom:8px;
	clear: both;
}
.formlist .formtxt {
float:left;
line-height:24px;
text-align:right;
width:130px;
}
.formlist .formcon {
	overflow:hidden;
	font-weight: bold;
	font-size: 12px;
}
.formlist span.tips {
	color:#666666;
	float:left;
	line-height:24px;
	white-space:nowrap;
	font-size: 12px;
	font-weight: normal;
}
.formbtn {
	height:44px;
	clear: both;
	padding: 15px 0;
}

.formbtn a:hover {
	
	background-position:0 -33px;
	
}
</style>
		<script type="text/javascript">
			$(function() {
				$("#bankBox").Ui();
				setDomain();
			});
			function ok() {
				
				var actionUrl = "/admin/user/authen/doHandPass";
				vip.ajax( {
					formId : "bankBox",
					url : actionUrl,
					div : "bankBox",
					dataType:"json",
					suc : function(json) {
						parent.Right(json.des, {callback:"reload2()"});
					}
				});
			}
		</script>
	</head>
<body>
	<div class="bankbox" id="bankBox">

		<div class="bankbox_bd">
			
			<div class="form-line">
				<div class="form-tit">真实姓名：</div>
				<div class="form-con">
					<input type="text" name="realName" class="input" mytitle="请输入真实姓名。" value="${au.realName}"/>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">身份证号：</div>
				<div class="form-con">
					<input type="text" name="cardId" class="input" mytitle="请输入身份证号。" value="${au.cardId}"/>
				</div>
			</div>
			<div class="form-line">
		    	<div  class="form-tit">您的短信验证码:</div>
			    <div class="form-con">
			    	<input type="password" id="code" name="code" style="width:100px;" class="input" position="n" mytitle="请输入发送到您手机上的验证码" errormsg="验证码错误" errorName="验证码" pattern="limit(4,10)"/>
			    	<button type="button" id="ajax_phone_get">获取验证码</button>	
			    </div>
		    </div>
			
			<div class="form-line">
		    	<div  class="form-tit">您的谷歌验证码:</div>
			    <div class="form-con">
			    	<input type="password" class="input" style="width:100px;" name="mCode" id="mCode" value="" mytitle="请输入移动设备上生成的验证码。" errormsg="验证码错误" pattern="limit(4,10)"/>
			    </div>
		    </div>

			<div class="form-btn" >
				<input id="userId" name="userId" type="hidden" value="${userId}" />
				<input id="vid" name="vid" type="hidden" value="${au.id}" />
         		<a class="btn" href="javascript:ok();"><span class="cont">确定</span></a>
         		<a href="javascript:parent.Close();" class="btn btn-gray"><span class="cont">取消</span></a>
			</div>
		</div>
	</div>
<script type="text/javascript">
$(function(){
	$("#ajax_phone_get").bind("click", postCode);
});

function postCode(){
	
	var actionUrl = "/admin/postcode";
	vip.ajax( {
		url : actionUrl,
		dataType : "json",
		suc : function(json) {
			waitTime();
			$("#ajax_phone_get").attr("disabled", "disabled");
			//Right(json.des);
		}
	});
}

var timeInterval = "";
window.clearInterval(timeInterval);
function waitTime(){
	var count = 180;
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
</body>
	
</html>
