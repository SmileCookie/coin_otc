<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
   <#include "/admins/top.ftl" />
   <script type="text/javascript" src="${static_domain }/statics/js/admin/admin.js"></script>
<style type="text/css">
.bankbox{padding: 30px;}
.form-tit{width: 120px;}
</style>

<script type="text/javascript">
$(function(){
	$("#bankBox").Ui();
});

function ok() {
	var datas = FormToStr("bankBox");
	if(datas == null)
		return;

	location.href = "/admin/manager/seeS?"+datas;
}
</script>
</head>

<body >
<div class="bankbox" id="bankBox">

<div class="bankbox_bd">

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
        <a class="btn" href="javascript:ok();"><span class="cont">确定</span></a>
        <a href="javascript:parent.Close();" class="btn btn-gray"><span class="cont">取消</span></a>
	</div>
	
</div>
</div>
</body>
</html>
