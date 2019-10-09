var isNewName = false;
var isNewEmail = false;
var registerTarVal = "";

var inAjaxing=false;
function reg(){
	if(inAjaxing){
		return;
	}
	$("#registerTar").val(registerTarVal);
    var datas = FormToStr("stepTwo");
    if(datas == null)
    	return;

    if(!$("#agreeNote").prop("checked")){ 
    	Wrong(jsLan[0]);
        return;
    }
    
	inAjaxing = true;
    $.ajax({
    	   type:"POST",
    	   url:"/user/doRegister",
    	   data:datas,
    	   dataType:"xml",
    	   error:function(){Wrong(jsLan[1]);inAjaxing=false;},
    	   success:function(xml){
    		  inAjaxing=false;
    	      if($(xml).find("State").text()=="true"){
    		       var username = $("#userName").val();
    		       var id=$(xml).find("MainData").text();
    		       regSuss();
    		  }else{
    	       	   getCode(); 
    	           Wrong($(xml).find("Des").text());
    	      }
    	  }
	});    
}

function postEmail(){
	vip.ajax({
		formId : "stepOneEmail",
		url : "/user/postEmail",
		dataType : "json",
		suc : function(json){
			emailRemind(json.des);
		},
		err : function(json){
			getCode(); 
			Wrong(json.des);
		}
	});
}

function sendCode() {
	var phonenumber = $('#phonenumber').val();
	var code = $('#code').val();
	$.ajax({
		type:"POST",
		url:"/user/sendCode",
		data:{
			phonenumber: phonenumber,
			code: code,
			codeType: 1
		},
		dataType:"json",
		error:function(){Wrong(jsLan[1]);inAjaxing=false;},
		success:function(json) {
			inAjaxing=false;
			if (json.isSuc) {
				settime($('#sendCodeBtn'));
			} else {
				getCode(); 
				Wrong(json.des);
 	      }
		}
	});
}

var countdown=60; 
function settime(obj) {
	if (countdown == 0) { 
		$(obj).removeAttr("disabled");    
		$(obj).val("点击获取"); 
		countdown = 60;
	} else { 
		$(obj).attr("disabled", true); 
		$(obj).val("已发送(" + countdown + ")"); 
		countdown--; 
		setTimeout(function() { 
			settime($(obj)) 
		},1000)
	} 
} 

function emailRemind(nid){
	Iframe({
		Url:"/user/emailRemind?nid="+nid+"&email="+encodeURIComponent($("#email").val()),
		Width:580,
		Height:345,
		isShowClose:false,
		isIframeAutoHeight:true,
		Title:"验证邮箱",
		scrolling:"no"
	});
}

/*
中文判断函数，允许生僻字用英文“*”代替
返回true表示是符合条件，返回false表示不符合
*/
function f_check_ZhOrNumOrLett(obj){    //判断是否是汉字、字母、数字组成   
    var regu = "^[0-9a-zA-Z\u4e00-\u9fa5]+$";      
    var re = new RegExp(regu);   
    if (re.test( obj )) {   
       return true;   
    } else{  
      
     return false;   
 }
}  


function checkNick(s,id){ 
	 if (!f_check_ZhOrNumOrLett(s))
		 return false;
	 
	 return checkuserName();
}

function regSuss(){
	$(".divUsername").text($("#nick").val());
	$("#divMarket").html($("#sample dt a").html());
	$("#divLanguage").html($("#sample2 dt a").html());
	
	$("#divEmail").text($("#email").val());
	$("#infomation").show();
	$(".Login-tips a").remove();
	moveTo('-800px');
	
	
}

var lastNameJson = {v : "" , suc : false , tips : ""};
var lastEmailJson = {v : "" , suc : false};
var isFormToStr = false;//当前是否处于提交表单事件中

function checkuserName(){
	var curVal = $("#nick").val();
	var ok = false;
	
	if((lastNameJson.v != "") && (lastNameJson.v == curVal)){
		if(!lastNameJson.suc){
			vip.form.error = lastNameJson.tips;
		}
		ok = lastNameJson.suc;
	}else{
		vip.ajax({ 
			async : false,
			url : "/user/nickValidated?userName=" + encodeURIComponent(curVal),
			err : function(xml){
				lastNameJson.v = curVal;
				lastNameJson.suc = false;
				if($(xml).find("MainData").text()=="1"){
					vip.form.error = jsLan[7];
					lastNameJson.tips = jsLan[7];
				}else{
					vip.form.error = jsLan[3];
					lastNameJson.tips = jsLan[3];
				}
			},
			suc : function(xml){
				lastNameJson.v = curVal;
				lastNameJson.suc = true;
				registerTarVal = $(xml).find("Des").text();
				isNewName=true;
				ok = true;
			}
		});
	}
	//
	if(document.activeElement.id == "stepOneButton"){
		if(!isFormToStr && ok){
			stepOne();
		}
	}
	return ok;
}

function checkEmail(){
	var curVal = $("#email").val();
	var ok = false;
	
	if((lastEmailJson.v != "") && (lastEmailJson.v == curVal)){
		if(!lastEmailJson.suc){
			vip.form.error =jsLan[4];
		}
		return lastEmailJson.suc;
	}else{
		vip.ajax({
			async : false,
			url : "/user/emailValidated?email=" + encodeURIComponent(curVal),
			err : function(xml){
				lastEmailJson.v = curVal;
				lastEmailJson.suc = false;
				vip.form.error =jsLan[4];
			},
			suc : function(xml){
				lastEmailJson.v = curVal;
				lastEmailJson.suc = true;
				ok = true;
			}
		});
	}
	
	//
	if(document.activeElement.id == "stepOneButton"){
		if(!isFormToStr && ok){
			stepOne();
		}
	}
	return ok;
}

function checkMobile() {
    return true;
}

String.prototype.startWith=function(str){     
  var reg=new RegExp("^"+str);     
  return reg.test(this);        
}  

String.prototype.endWith=function(str){     
  var reg=new RegExp(str+"$");     
  return reg.test(this);        
}

//重新获取验证码
function getCode(obj){
   $(obj).attr("src","/imagecode/get-28-85-39-"+new Date().getTime());
} 


var move=false;
$(function(){
	if($("#emailSuc").val().length > 0){
		$("#pagename .topright").css({"background-position":"0 -50px"});
	}
	
	language=$("#language").val();
	//moveTo('-1600px');
$(".btn_red").each(function(){
$(this).bind("mouseenter",function(){
    $(this).animate({"backgroundColor":"#E89E44"}, 100); $("#iconRight").animate({"margin-left":"25px"}, 200);
  }).bind("mouseleave",function(){
    $(this).animate({"backgroundColor":"#D75A46"}, 100); $("#iconRight").animate({"margin-left":"25px"}, 200);
  });
});
 
$(".btn_blue").each(function(){
$(this).bind("mouseenter",function(){
    $(this).animate({"backgroundColor":"#E89E44"}, 100); $("#iconRight").animate({"margin-left":"25px"}, 200);
  }).bind("mouseleave",function(){
    $(this).animate({"backgroundColor":"#5FAAE3"}, 100); $("#iconRight").animate({"margin-left":"25px"}, 200);
  });
});
//select country
$(".dropdown img.flag").addClass("flagvisibility");

    $(".dropdown dt a").click(function() {
        //$(".dropdown dd ul").toggle();
    	$(this).parent("dt").next("dd").find("ul").toggle();
    });
  //这里决定市场和货币（市场是固定的）
    $("#selectCountryUl li a").click(function() {
        var text = $(this).html();
        $("#sample dt a span").html(text);
        $("#sample dd ul").hide();
        market=getSelectedValue("sample");
      
        $("#market").val(market);
       
    });
    
    $("#selectCountryUl2 li a").click(function() {
        var text = $(this).html();
        $("#sample2 dt a span").html(text);
        $("#sample2 dd ul").hide();
        language=getSelectedValue("sample2");
        $("#language").val(language);
    
    });
   
    
                
    function getSelectedValue(id) {
       return $("#" + id).find("dt a span.value").text();
    }

    $(document).bind('click', function(e) {
        var $clicked = $(e.target);
        if (! $clicked.parents().hasClass("dropdown"))
            $(".dropdown dd ul").hide();
            });

 
            $("#flagSwitcher").click(function() {
        $(".dropdown img.flag").toggleClass("flagvisibility");
    });
    
     //  moveTo("-1600px"); 

});
 

function moveTo(position){
  $("#form-con").animate({"margin-left":position},200);
  if(position=='0px'){
   $("#pagename .topright").css("background-position","0 0");
   $(".Register-con").css("height","500px");}
  else if(position=='-800px'){
  $("#pagename .topright").css("background-position","0 -50px");
  $(".Register-con").css("height","871px");}
  else  if(position=='-1600px'){
   $("#pagename .topright").css("background-position","0 -100px");
   $(".Register-con").css("height","442px");}
}
function stepOne(){
	  
	isFormToStr = true;
//	var currentTab = $('.reghdlist li.on').attr('data-id');
//	if ('mob' == currentTab) {
		var datas = FormToStr("stepOneMob");
		isFormToStr = false;
		if(datas == null)
			return;
		var phonenumber = $('#phonenumber').val();
		var code = $('#mobileCode').val();
		var recommId = $('recommId').val();
		window.location.href = '/user/postMobileCode?phonenumber=' + phonenumber + '&code=' + code + '&recommId=' + recommId;
//	} else {
//		var datas = FormToStr("stepOneEmail");
//		isFormToStr = false;
//		if(datas == null)
//			return;
//		postEmail();
//	}
}

function stepTwo(){
    var datas = FormToStr("stepTwo");
    if(datas == null)
    	return;
//    if(!isNewName){
//        Alert("Nickname you selected has already been registered, please change another new!");
//        return;
//    	}
//    	if(!isNewEmail){
//    	  Alert("Email you selected has already been registered, please change another new!");
//        return;
//    	}
     moveTo('-1600px'); 
}
//top kik
function upStepTwo(){
    if(currency==""&&market=="")
      moveTo('0px');
    else if(!isNewName||!isNewEmail||$("#nick").val()==""||$("#email").val()=="")
    {
        moveTo('-800px'); 
     }else{
      moveTo('-1600px'); 
     }
}
var market="";
var language=""
	
	
	var ga_reg_password_common = new Array(
			"~!@#$%^&*()",
			"0987654321",
			"1234567890",
			"qwertyuiop",
			"asdfghjkl",
			"zxcvbnm",
			""
		);

		var g_reg_realname = "";
		var g_reg_birthday = "";

		function hash1inhash2(h1, h2)
		{
			for (k in h1)
			{
				if (h2[k] == null)
				{
					return false;
				}
			}
			return true;
		}

		function passwordCheck(password)
		{
			var intInPass=new Array(0,0,0,0);	//num,eng,ENG,others
			var hashInPass=new Array();
			var hashInPassCount=0;
			var hashInName=new Array();
			var hashInNameCount=0;
			
			var numInName=new Array();
			var numInNameCount=0;
			var numInPass=new Array();
			var numInPassCount=0;
			var engInName=new Array();
			var engInNameCount=0;
			var engInPass=new Array();
			var engInPassCount=0;
			var ENGInName=new Array();
			var ENGInNameCount=0;
			var ENGInPass=new Array();
			var ENGInPassCount=0;
			
			var Modes=0;

			for(i=0;i<g_reg_realname.length;i++){
				ch=g_reg_realname.charAt(i);
				cc=g_reg_realname.charCodeAt(i);
				if (hashInName[cc] == null)
				{
					hashInName[cc] = 0;
					hashInNameCount ++;
				}
				else
				{
					hashInName[cc] ++;
				}
				if(ch>='0'&&ch<='9'){//get number in name
					numInName[numInNameCount]=ch;
					numInNameCount++;
				}
				else if(ch>='a' && ch<='z'){//get eng in name
					engInName[engInNameCount]=ch;
					engInNameCount++;
				}
				else if(ch>='A' && ch<='Z'){//get ENG in name
					ENGInName[ENGInNameCount]=ch;
					ENGInNameCount++;
				}
			}
			for(i=0;i<password.length;i++){
				ch=password.charAt(i);
				cc=password.charCodeAt(i);
				if (hashInPass[cc] == null)
				{
					hashInPass[cc] = 0;
					hashInPassCount ++;
				}
				else
				{
					hashInPass[cc] ++;
				}
				if(ch>='0'&&ch<='9'){//get number in pass
					numInPass[numInPassCount]=ch;
					numInPassCount++;
					intInPass[0]++;
					Modes |= 1;
				}
				else if(ch>='a' && ch<='z'){//get eng in pass
					engInPass[engInPassCount]=ch;
					engInPassCount++;
					intInPass[1]++;
					Modes |= 2;
				}
				else if(ch>='A' && ch<='Z'){//get ENG in pass
					ENGInPass[ENGInPassCount]=ch;
					ENGInPassCount++;
					intInPass[2]++;
					Modes |= 4;
				}
				else
				{
					intInPass[3]++;
					Modes |= 8;
				}
			}

			if (hashInPassCount == 1)
			{
				return 4;
			}
			if (isBirth(g_reg_birthday, password))
			{
				return 4;
			}
			if (isTooCommon(password.toLowerCase()))
			{
				return 4;
			}
			if (hashInPassCount
				&& hashInNameCount
				&& (hash1inhash2(hashInPass, hashInName) || hash1inhash2(hashInName, hashInPass)))
			{
				return 4;
			}
			var btotal = bitTotal(Modes);
			if (password.length >= 10)
			{
				btotal++;
			}
			switch (btotal)
			{
			case 1:
				return 4;
			case 2:
				return 3;
			case 3:
				return 2;
			default:
				return 1;
			}
		}

		function bitTotal(num){
			var modes=0;
			for (i=0;i<4;i++){
				if (num & 1) modes++;
				num>>>=1;
			}
			return modes;
		}

		function isBirth(birth, password)
		{
			if (birth.length != 8)
			{
				return false;
			}
			
			var birth1=birth.substring(2,0);//19
			var birth2=birth.substring(4,2);//85
			var birth3=birth.substring(6,4);//01
			var birth4=birth.substring(8,6);//02
			var birth33;
			if(birth3.substring(0,1)=="0"){
				birth33=birth3.substring(1,2);
			}
			else{
				birth33=birth3.substring(0,2);
			}
			var birth44;
			if(birth4.substring(0,1)=="0"){
				birth44=birth4.substring(1,2);
			}
			else{
				birth44=birth4.substring(0,2);
			}

			var testbirth1=birth2+birth3+birth4;
			var testbirth2=birth1+birth2+birth33+birth44;

			var yyyymm=birth1+birth2+birth3;
			var yyyydd=birth1+birth2+birth4;
			var mmyyyy=birth3+birth1+birth2;
			var ddyyyy=birth4+birth1+birth2;
			
			if(password.indexOf(birth)>-1){
				return true;
			}
			else if(password.indexOf(testbirth1)>-1){
				return true;
			}
			else if(password.indexOf(testbirth2)>-1){
				return true;
			}
			else if(password.indexOf(yyyymm)>-1){
				return true;
			}
			else if(password.indexOf(yyyydd)>-1){
				return true;
			}
			else if(password.indexOf(mmyyyy)>-1){
				return true;
			}
			else if(password.indexOf(ddyyyy)>-1){
				return true;
			}
			return false;
		}

		function isTooCommon(password)
		{
			for (var i=0; ga_reg_password_common[i]!=""; i++){
				if(ga_reg_password_common[i].indexOf(password)>-1){
					return true;
				}
			}
			return false;
		}

		 
		function qiangdu(){
		      var pass=$("#pwd").val();
		      var i=passwordCheck(pass); 
		      $("#pwdLevel").val(120-i*20);
		      if($("#mimaqiangdu").attr("src") != vip.staticDomain+"/statics/img/cn/user/level/mmqd_"+i+".gif"){
		    	  $("#mimaqiangdu").attr({"src":vip.staticDomain+"/statics/img/cn/user/level/mmqd_"+i+".gif"});
		      }
		    }
		function qiangdu2(){
		  	var pass=$("#safePwd").val();
		  	var i=passwordCheck(pass); 
		  	$("#safeLevel").val(120-i*20);
		  	if($("#mimaqiangdu").attr("src") != vip.staticDomain+"/statics/img/cn/user/level/mmqd_"+i+".gif"){
			  	$("#mimaqiangdu").attr({"src":vip.staticDomain+"/statics/img/cn/user/level/mmqd_"+i+".gif"});
		  	}
		}
		
		function checkPwd(s, id){
			var pattern = /^(?!\D+$)(?![^a-zA-Z]+$)\S{6,20}$/;
			var re = new RegExp(pattern);   
		    if (re.test(s)) {
		    	return true;
		    }   
		    return false;
		}		