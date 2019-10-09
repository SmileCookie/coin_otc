<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title></title>
<#include "/admins/top.ftl" />
	<style type="text/css">
	.form-tit {
	    line-height: 32px;
	    width:80px;
	}
	
	.do{float: none;}
	.form-con span.txt{float:left;margin-right:15px;}
	.tip .tipss{padding-left: 35px;}
	.tip .tipss p{padding-left: 0;}
	</style>
</head>

<body>

<!-- 主体内容 -->
<div class="main-bd" id="bankBox">    


	<div class="form-line">
		<div class="form-tit" style="float:left;">谷歌验证码：</div>
		<div class="form-con">
			<input type="text" class="input" name="mCode" id="mCode" value="" pattern="limit(4,10)"/>
		</div>
	</div>		
	<div class="form-line">
		<div style="text-align: center;width:328px; height: 20px;color: #ff0000;" id="errmsg"></div>		
	</div>		
    <div class="form-btn">
       <input type="hidden" name="id" value="${adminId}">
       <a class="btn" id="doGo" href="javascript:;" onclick="go()"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a> 
       <a href="javascript:parent.Close()" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>
     </div>

</div>
		
<!-- 主体内容结束 -->
<script type="text/javascript">
	$(function(){
		$("#bankBox").Ui();
	});
	
	function go(){
		var domain = "";
		$("input[name=domain]").each(function(){
			var id=$(this).val();
			if($(this).attr("checked")==true){
				domain += ","+id;
			}
		});
		if(domain.length > 1){
			domain = domain.substring(1);
		}
		if(domain.length == 0){
			errTo("请选择一个同步的网站。");
			return;
		}
		var mCode = $("#mCode").val();
		if(mCode.length < 6){
			errTo("请输入验证码。");
			return;
		}
		var actionUrl = "/admin/syncAdminToOuter?domains="+domain;
		
		vip.ajax( {
			formId : "bankBox",
			url : actionUrl,
			dataType : "json",
			div : "bankBox",
			suc : function(xml) {
				parent.Right(xml.des, {
					callback : "reload2()"
				});
			},
			err : function(json){
				errTo(json.des);
			}
		});
	}
	
	function errTo(msg){
		$("#errmsg").text(msg);
		Pause(this, 5000);
       	this.NextStep = function(){
       		$("#errmsg").text("");
       	}
	}
</script>
</body>
</html>

