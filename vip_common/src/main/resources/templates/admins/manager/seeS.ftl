<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <#include "/admins/top.ftl" />
<style type="text/css">
.bankbox{padding: 10px 0 0 126px;}
.bankbox_bd{margin-left: 22px;}
</style>

<script type="text/javascript">
$(function(){
	$("#bankBox").Ui();
});

function save() {
	var actionUrl = "/admin/manager/saveSecret";
	vip.ajax( {
		formId : "bankBox",
		url : actionUrl,
		div : "bankBox",
		dataType : "json",
		suc : function(json) {
			parent.Right(json.des, {
				callback : "Close()"
			});
		},
		err : function(json){
			Wrong(json.des);
		}
	});
}
</script>
</head>

<body >
<div class="bankbox" id="bankBox">
	<#if errmsg??><font color="#ff0000;">${errmsg }</font></#if>
	<#if !errmsg??>
		<div class="bankbox_bd">
			<img src="${url?if_exists}" width="150px" height="150px"/>
		</div>
		<input type="text" id="secret" name="secret" style="margin-top: 10px;width: 202px;height:26px;" value="${secret}"/>
		<div class="bankbox_bd" style="margin:10px 0 0;">
			输入验证码：
		</div>
		<input type="text" id="aCode" name="aCode" value="" style="margin-top: 10px;height:26px;" mytitle="请输入验证码。" errormsg="输入验证码。" pattern="limit(0,10)"/>
		<div class="form-btn">
			<a class="btn" href="javascript:save();"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a>
		</div>
	</#if>
</div>

</body>
</html>
