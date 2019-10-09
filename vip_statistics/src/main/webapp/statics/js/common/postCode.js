$(function(){
	$("#ajax_phone_get").bind("click", postCode);
	$("#ajax_audio_phone_get").bind("click", getAudioCode);
});

/**
 * @param {Object} needMobile
 * @param {Object} needPwd
 * @param {Object} type
 * @return {TypeName} 
 */
function postCode(){
	var needMobile = $("#needMobile").val();
	var needPwd = $("#needPwd").val();
	var needUserName = $("#needUserName").val();
	var type = $("#type").val();
	var postParam = "";
	if(needMobile=="true"){
		var $this = $("#mobile");
		if ($this.val() == null || $this.val() == "" || $this.val().length<8){
			vip.form.error = vip.L("请输入手机号码。");
			CheckTextBox($("#mobile"));
			return;
		}
		if ($this.attr("errorstyle")){
			return;
		}
		var mobile = $.trim($this.val());
		var mCode = $("#mCode").val();
		if(mCode && mCode!="undefined")
			mobile = mCode + " " + mobile;
		
		postParam += "?mobile="+encodeURIComponent(mobile);
	}
	
	if(needPwd=="true"){
		var payPass = $("#payPass").val();
		if(payPass.length < 6){
			vip.form.error = vip.L("请输入您的安全密码。");
			//Wrong("请输入您的安全密码。");
			CheckTextBox($("#payPass"));
			return;
		}
		postParam += "&payPass="+encodeURIComponent(payPass);
	}
	if(needUserName=="true"){
		var userName = $("#userName").val();
		if(userName.length <= 0){
			vip.form.error = vip.L("请输入您的用户名。");
			CheckTextBox($("#userName"));
			return;
		}
		postParam += "&needUserName=true&userName="+encodeURIComponent(userName);
	}
	
//	var nullId = $("#nullId").val();
//	if($("#"+nullId) && $("#"+nullId).val().length == 0){
//		Wrong("请先输入表单内容。");
//		return;
//	}
	
	postParam += "&type="+type;
	
	postParam = postParam.substring(1);
	var actionUrl = "/u/safe/approve/mobilecode?"+postParam;
	vip.ajax( {
		url : actionUrl,
		dataType : "json",
		suc : function(json) {
			if (json.isSuc) {
				waitTime();
				$("#ajax_phone_get").attr("disabled", "disabled");
				$("#ajax_audio_phone_get").hide();
			} else {
				Wrong(json.des);
			}
			//Right(json.des);
		},
		err : function(json){
			if($("#errormsg").length > 0){
				errTo(json.des);
			}else{
				Wrong(json.des);
			}
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

var timeInterval = "";
window.clearInterval(timeInterval);
function waitTime(){
	var count = 120;
	window.clearInterval(timeInterval);
	var timeInterval = window.setInterval(function(){
		count --;
		if(count == 0){
			window.clearInterval(timeInterval);
			$("#ajax_phone_get").text(vip.L("获取验证码")).removeAttr("disabled");	
			$("#ajax_audio_phone_get").show();
			$("#ajax_audio_phone_get").removeAttr("disabled");		
		}else{
			$("#ajax_phone_get").text(vip.L("x1秒后重新获取" , [{k : "x1",v : count}]));								
		}
	}, 1000);
}

function getAudioCode(){
	var needMobile = $("#needMobile").val();
	var needPwd = $("#needPwd").val();
	var type = $("#type").val();
	var postParam = "";
	if(needMobile=="true"){
		var $this = $("#mobile");
		if ($this.val() == null || $this.val() == "" || $this.val().length<8){
			Wrong(vip.L("请输入手机号码。"));			
			return;
		}
		if ($this.attr("errorstyle")){
			return;
		}
		var mobile = $.trim($this.val());
		var mCode = $("#mCode").val();
		if(mCode && mCode!="undefined")
			mobile = mCode + " " + mobile;
		
		postParam = "?mobile="+encodeURIComponent(mobile);
	}
	
	if(needPwd=="true"){
		var payPass = $("#payPass").val();
		if(payPass.length < 6){
			vip.form.error = vip.L("请输入您的安全密码。");
			//Wrong("请输入您的安全密码。");
			CheckTextBox($("#payPass"));
		}
		postParam = "&payPass="+encodeURIComponent(payPass);
	}
	
	postParam += "&type="+type;
	
	postParam = postParam.substring(1);
	var actionUrl = "/u/safe/approve/mobilecode/audioCode?"+postParam;
	vip.ajax( {
		url : actionUrl,
		dataType : "json",
		suc : function(json) {
			waitTime2();
			$("#ajax_audio_phone_get").attr("disabled", "disabled");
			$("#ajax_phone_get").attr("disabled", "disabled");
			//Right(json.des);
		},
		err : function(json){
			if($("#errormsg").length > 0){
				errTo(json.des);
			}else{
				Wrong(json.des);
			}
		}
	});
}

var timeInterval2 = "";
window.clearInterval(timeInterval2);
function waitTime2(){
	var count = 180;
	window.clearInterval(timeInterval2);
	timeInterval2 = window.setInterval(function(){
		count --;
		if(count == 0){
			window.clearInterval(timeInterval2);
			$("#ajax_audio_phone_get").text(vip.L("重新获取语音验证码")).removeAttr("disabled");
			$("#ajax_phone_get").removeAttr("disabled");
		}else{
			$("#ajax_audio_phone_get").text(vip.L("x1秒后重新获取语音验证码" , [{k : "x1",v : count}]));								
		}
	}, 1000);
}