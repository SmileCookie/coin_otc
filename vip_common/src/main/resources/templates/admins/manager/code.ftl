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
	
	#main{width: 660px;}
	.right{width: 643px;}
	
	.do{float: none;}
	
	.tip .tipss{padding-left: 35px;}
	.tip .tipss p{padding-left: 0;}
	</style>
</head>

<body>

<!-- 主体内容 -->
<div  id="main" class="Register-fill">
	
	<div class="right" id="mainForm">
        <div id="rightInner">
		
		<div class="main-bd">    
   		
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
               <a class="btn" id="doGo" href="javascript:;" onclick="go()"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a> 
               <a href="javascript:parent.Close()" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>
             </div>
		
		</div>
		
		
		</div>
	</div>
</div>
<!-- 主体内容结束 -->
<script type="text/javascript">
	$(function(){
		$("#mainForm").Ui();

        setDomain();
		$("body").bind("keyup", function(event){
			if (event.keyCode=="13"){
				$("#doGo").trigger('click');
			}
		});
	});
	
	function go(){
		var mCode = $("#mCode").val();
		if(mCode.length < 6){
			errTo("请输入验证码。");
			return;
		}
		var callback = "${callback}";
		var actionUrl = "/admin/manager/checkCode";
		vip.ajax({
			formId : "mainForm",
			div : "mainForm",
			url : actionUrl,
			dataType : "json",
			suc : function(json) {
				parent.couldPass = true;
				eval("parent."+callback+"("+mCode+")");
			},
			err : function(json){
				parent.couldPass = false;
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

