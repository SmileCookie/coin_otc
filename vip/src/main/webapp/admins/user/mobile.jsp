<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
<jsp:include page="/admins/top.jsp" />

<script type="text/javascript">
$(function(){
	$("#add_or_update").Ui();
});

function ok(){
	var actionUrl = "/admin/user/passmobile?userId="+parent.$("#userId").val();
	vip.ajax( {
		formId : "add_or_update",
		url : actionUrl,
		div : "add_or_update",
		suc : function(xml) {
			parent.Right($(xml).find("MainData").text(), {callback:"reload2()"});
		}
	});
}

</script>
<style type="text/css">
.form-tit{ width:100px;float:left;}
input{padding:8px 5px;}
.main-bd {
    padding: 30px;
}
</style>
</head>

<body>
	<div id="add_or_update" class="main-bd">
		
		<div class="form-line">
			<div class="form-tit">国家编码：</div>
			<div class="form-con">
				<input class="txt" type="text" name="mCode1" id="mCode1" value="${mCode==null||mCode==''?'+86':mCode}" mytitle="请输入手机的国家编码。比如：+86" pattern="limit(1, 6)" errormsg="请输入手机的国家编码。比如：+86"/> 
			</div>
		</div>
		
		<div class="form-line">
			<div class="form-tit">手机号码：</div>
			<div class="form-con">
			 	<input class="txt" type="text" name="mobile" id="mobile" value="${mobile}" mytitle="请输入要认证的手机号码。" pattern="limit(1, 20)" errormsg="请输入要认证的手机号码。"/>
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
		
		<div class="form-btn">
			<a class="btn" href="javascript:ok();"><span class="cont">确定</span></a>
			<a href="javascript:parent.Close();" class="btn btn-gray"><span class="cont">取消</span></a>
		</div>
	</div>
	
</body>
</html>
