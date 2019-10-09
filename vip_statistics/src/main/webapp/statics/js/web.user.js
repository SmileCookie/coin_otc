define(function(require, exports, module) {
		"require:nomunge,exports:nomunge,module:nomunge";
        var user = {};
        var M = require("module_method");
        var RSA ;

                var loginType = 0; 
        var userType = 2 ;
        var lockRequest = false;
        var needImgCode = true, 
        	takeMsgCode = false, 
        	logRememberMe = false, 
        	codeCountTime = 60, 
        	loginGoogleAuth = $.cookie(ZNAME+'googleauth') === 'true', 
        	loginIpAuth = $.cookie(ZNAME+'ipauth') === 'true', 
        	submitBtn = "#submitBtn",
        	getMsgBtn = "#sendMsgCode",
        	getImgBtn = "img[role=imgCode]",
        	userName,
            returnTo,
        	countryCode,
        	logPassword,
        	setPassword ,
        	repeatPassword ,
        	pwdLevel ,
        	imgCode ,
        	msgCode ,
        	googleCode,
        	recommendId ,
        	cardId ,
        	rsaPublicKey ,
        	regAgreement ;
            user.vipRate = 0 ;
            user.totalPoints = null ;
            user.simpleAuth = null ;
            user.depthAuth = null ;
            user.emailAuth = null ;
            user.mobileAuth = null ;
            user.googleAuth = null ;
            user.pwdStatus = null ;
            user.safePwdStatus = null ;

        user.init = function() {
        	var $this = this ;
        	var cruLink = document.location.href;
        	var curPath = document.location.pathname;
        	if(JuaBox.isMySelf() && curPath.indexOf("/manage") == -1 && curPath.indexOf("/login") == -1 && curPath.indexOf("/regi") == -1 && curPath.indexOf("/ac/") == -1){
        		$.cookie(ZNAME+'fromurl', cruLink, { expires: 7, path: '/', domain: DOMAIN_BASE});
			}
			// $("#lanSelectA").removeClass("en");
			$("#lanSelectA,html").removeClass("cn en hk").addClass(LANG);
			$(".lanWord").addClass(LANG);
			var lanText;
			switch (LANG){
                case 'cn':
                    lanText = '简体中文';
                    break;
				case 'en':
                    lanText = 'English';
					break;
				case 'hk':
                    lanText = '繁體中文';
					break;
				case 'jp':
                    lanText = '日本語';
                    break;
				case 'kr':
                    lanText = '한국어';
                    break;
                default:
                    lanText = 'English';
                    LANG = 'en';
			}
			// let lanText = LANG =='en'?'English':LANG =='hk'?'繁體中文':日本語'简体中文'
			$("#lantxt").html(lanText);

			var gq = {
				'繁體中文':'<i class="g-ico hk"></i>',
				'简体中文':'<i class="g-ico cn"></i>',
				'English':'<i class="g-ico en"></i>',
                '한국어' :  '<i class="g-ico kr"></i>',
                '日本語':  '<i class="g-ico jp"></i>'
			}

			$("#gq").html(gq[lanText]);
			if($this.isLogin()){
    			if(loginGoogleAuth || loginIpAuth){
        			$this.loginAuth();
				}
				$(".login_hide").hide();//立即注册按钮
				$(".btn_line").hide();
        		$(".nologin").hide();
				$(".logined").show();
				//调整访问交易按钮
				// if (JuaBox.isMobile()) {
				// 		$(".btn_trade").css("top","0.5rem");
				// 	}
			    $("#M_userName").text($.cookie(UNAME));
				$("#M_userName_1").text($.cookie(UNAME));
				var isUlRight = $(".right_logined").width()/2;
				$(".right_logined .mony-shang").css("right",isUlRight);
			    $("#tipList").on("click",".switch",function () {
	                $this = $(this);
	                user.useOrCloseTip($this.data("type"),$this.data("status"),function(){
	                    $this.toggleClass("on");
	                    $this.data("status", $this.hasClass("on") ? 1 : 0);
	                    $this.parents("li").find("span")
	                            .text($this.hasClass("on") ? bitbank.L("已开启") :  bitbank.L("已关闭"))
	                            .removeClass($this.hasClass("on") ? "text-third" :  "text-second")
	                            .addClass($this.hasClass("on") ? "text-second" :  "text-third")
	                });
	            });
			    $this.getUserInfo(function(){
    				$("#D_userTotal").html($this.totalPoints);
					$("#topMobileStatus").addClass($this.mobileAuth ? "pass" : "");
					$("#topEmailStatus").addClass($this.emailAuth ? "pass" : "");
					$("#topAuthStatus").addClass($this.simpleAuth || $this.depthAuth ? "pass" : "");
					$("#topAuthDepth").addClass($this.depthAuth ? "pass" : "");
					$("#topGoogleStatus").addClass($this.googleAuth ? "pass" : "");
        		});
        	}else{
        		$(".logined").hide();
				$(".nologin").show();
				$(".login_hide").css("display","inline-block");
				//调整访问交易按钮
				if (JuaBox.isMobile()) {
					$(".btn_trade").css("top","1.1rem");
				}
        	}
        };
        user.commonPageInit = function(){
        	var $this = this ;
    		userName = $.cookie(ZNAME+'logusername'),
    		countryCode = $.cookie(ZNAME+'logcountrycode');
    		if(userName != "" && userName != null){
    			$("#nike").val(userName).focus();
    			$("#countryCode").val(countryCode);
    			$("#countryText").text(countryCode);
    			$("#countryGroup .dropdown-menu li").each(function(){
    				if($(this).data("value") == countryCode){
    					$(this).addClass("active");
    				}else{
    					$(this).removeClass("active");
    				}
    			});
    		}
    		$("#clearCookie").on("click",function(){
    			$("#nike").val("").focus();
    			$.cookie(ZNAME+'logusername', null, { expires: 7, path: '/', domain: DOMAIN_BASE});
    			$.cookie(ZNAME+'logcountrycode', null, { expires: 7, path: '/', domain: DOMAIN_BASE});
    			JuaBox.showTip(bitbank.L("成功清除cookies记录"));
    		});
        	$('#countryGroup').on('show.bs.dropdown', function () {
        	    $(this).find(".dropdown-menu").width($(".bk-sign-form").width() - 2);
        	});
        	$("#countryGroup .dropdown-menu").on("click","li",function(){
        		$("#countryCode").val($(this).data("value"));
        		$("#countryText").text($(this).data("value"));
        		$("#countryGroup .dropdown-menu li").removeClass("active");
        		$(this).addClass("active");
        	});
        	$("input[name=nike]").on({
        		"keyup" : function(){ $this.checkUserNike($(this).val())},
        		"blur" : function(){ $this.checkUserNike($(this).val())},
        		"change" : function(){ $this.checkUserNike($(this).val())}
        	});
        	$("input[name=password]").on({
        		"focus" : function(){ $("#pwdStrength").fadeIn(); },
        		"keyup" : function(){  $this.checkPwdStrength($(this).val())},
        		"blur" : function(){   
        			$this.checkPwdStrength($(this).val()); 
        			if($("#pwdLevel").val() > 20){
        				$("#pwdStrength").fadeOut();
        			}
        		},
        		"change" : function(){ $this.checkPwdStrength($(this).val())}
        	});
        	$(getImgBtn).on("click",function(){
        		$(this).attr("src", DOMAIN_VIP + "/imagecode/get-28-100-50-"+new Date().getTime());
        	});
        }
        user.logPageInit = function(){
        	var $this = this ;
        	$this.commonPageInit();

        	if(user.isLogin()) {
    			if ($.cookie(ZNAME+"fromurl")) {
    				window.top.location.href = $.cookie(ZNAME+"fromurl");
    			}else{
    				location.href = "/u/asset";
    			}
    		}
        	RSA = require("module_encrypt");
        	$this.getPublicKey();

        	$("body").keypress(function(e) {
    			if (e.keyCode == 13) {
    				$this.login();
    			}
    		});
    		if($.cookie("LoginCode") == "1") {
    			$("#imgCodeForm").show();
    		};
        	$(submitBtn).on("click",function(event){
        		if(event.originalEvent) {
        			$this.login();
            	}else {
                	return JuaBox.showWrong(bitbank.L("请勿使用非法脚本"));
            	}
        	});
        	$(".bk-sign-swift .iconfont").on("click",function(){
        		$(".bk-sign-swift .iconfont, #quickLogin, #vipLogin").toggle();
        		loginType = loginType == 0 ? 1 : 0;
        	});
        };
        user.regPageInit = function(){
        	var $this = this ;
        	$this.commonPageInit();

        	        	if(user.isLogin()) {
    			if ($.cookie(ZNAME+"fromurl")) {
    				window.top.location.href = $.cookie(ZNAME+"fromurl");
    			}else{
    				location.href = "/u/asset";
    			}
    		}
        	$("body").keypress(function(e) {
    			if (e.keyCode == 13) {
    				$this.register();
    			}
    		});
        	$(submitBtn).on("click",function(event){
        		if(event.originalEvent) {
        			$this.register();
            	}else {
                	return JuaBox.showWrong(bitbank.L("请勿使用非法脚本"));
            	}
        	});
        	$(getMsgBtn).on("click",function(event){
        		if(event.originalEvent) {
        			$this.getMsgCode("register", function(){
        				$("input[name=nike]").attr("readonly","readonly");
        				$("#countryGroup .dropdown-toggle").on("click",function(){return false});
        			});
            	}else {
                	return JuaBox.showWrong(bitbank.L("请勿使用非法脚本"));
            	}
        	});
        };
        user.pageIndexInit = function(){
        	var $this = this ;
        	$this.getUserInfo(function(){
        		$("#safeMobileStatus").addClass($this.mobileAuth ? "pass" : "");
        		$("#safeEmailStatus").addClass($this.emailAuth ? "pass" : "");
        		$("#safeAuthStatus").addClass($this.simpleAuth || $this.depthAuth ? "pass" : "");
        		$("#safeGoogleStatus").addClass($this.googleAuth ? "pass" : "");
        		$("#safePwdStatus").addClass($this.pwdStatus ? "pass" : "");
        		$("#safeSafePwdStatus").addClass($this.safePwdStatus ? "pass" : "");
        		if($this.mobileAuth){
        			$("#safeMobileStatus").find(".btn").addClass("disabled").text(bitbank.L("已认证"));
        		}
        		if($this.emailAuth){
        			$("#safeEmailStatus").find(".btn").addClass("disabled").text(bitbank.L("已认证"));
        		}
        		if($this.simpleAuth || $this.depthAuth){
        			$("#safeAuthStatus").find(".btn").addClass("disabled").text(bitbank.L("已认证"));
        		}
        		if($this.googleAuth){
        			$("#safeGoogleStatus").find(".btn").addClass("disabled").text(bitbank.L("已认证"));
        		}
        		if($this.pwdStatus){
        			$("#safePwdStatus").find(".btn").addClass("disabled").text(bitbank.L("已设置"));
        		}
        		if($this.safePwdStatus){
        			$("#safeSafePwdStatus").find(".btn").addClass("disabled").text(bitbank.L("已设置"));
        		}
        	});
			$("#openSafePwd").on("click",function(){
    			$this.openSafePwd(function(){
    				window.top.location.reload();
    			});
    		});
        	$("#closeSafePwd").on("click",function(){
    			$this.closeSafePwd(function(){
    				window.top.location.reload();
    			});
    		});
			$this.accountIndexTipsHover();//账户等级提示信息
			// 首次注册进入此页面时，弹窗提示
			var sigup_first = $.cookie('sigup_first');
			if (sigup_first === "first"){
				$.cookie('sigup_first', '', { expires: -1, path: '/' });
				$this.sigupTips();
			}
        };
        user.findLoginPwdPageInit = function(){
        	var $this = this ;
        	$this.commonPageInit();

        	        	$("body").keypress(function(e) {
    			if (e.keyCode == 13) {
    				$this.findLoginPwd();
    			}
    		});
        	$(submitBtn).on("click",function(event){
        		if(event.originalEvent) {
        			$this.findLoginPwd();
            	}else {
                	return JuaBox.showWrong(bitbank.L("请勿使用非法脚本"));
            	}
        	});
        	$(getMsgBtn).on("click",function(event){
        		if(event.originalEvent) {
        			$this.getMsgCode("findLoginPwd", function(){
        				$("input[name=nike]").attr("readonly","readonly");
        				$("#countryGroup .dropdown-toggle").on("click",function(){return false});
        			});
            	}else {
                	return JuaBox.showWrong(bitbank.L("请勿使用非法脚本"));
            	}
        	});
        };
        user.resetLoginPwdPageInit = function(){
        	var $this = this ;
        	$this.commonPageInit();

        	RSA = require("module_encrypt");
        	$this.getPublicKey();

        	        	$("body").keypress(function(e) {
    			if (e.keyCode == 13) {
    				$this.resetLoginPwd();
    			}
    		});
        	$(submitBtn).on("click",function(event){
        		if(event.originalEvent) {
        			$this.resetLoginPwd();
            	}else {
                	return JuaBox.showWrong(bitbank.L("请勿使用非法脚本"));
            	}
        	});
        };
        user.findSafePwdPageInit = function(){
        	var $this = this ;
        	$this.commonPageInit();

        	$("body").keypress(function(e) {
    			if (e.keyCode == 13) {
    				$this.findSafePwd();
    			}
    		});
        	$(submitBtn).on("click",function(event){
        		if(event.originalEvent) {
        			$this.findSafePwd();
            	}else {
                	return JuaBox.showWrong(bitbank.L("请勿使用非法脚本"));
            	}
        	});
        	$(getMsgBtn).on("click",function(event){
        		if(event.originalEvent) {
        			$this.getMsgCode("findSafePwd", function(){
        				$("input[name=nike]").attr("readonly","readonly");
        				$("#countryGroup .dropdown-toggle").on("click",function(){return false});
        			});
            	}else {
                	return JuaBox.showWrong(bitbank.L("请勿使用非法脚本"));
            	}
        	});
        };
        user.resetSafePwdPageInit = function(){
        	var $this = this ;
        	$this.commonPageInit();

        	RSA = require("module_encrypt");
        	$this.getPublicKey();

        	        	$("body").keypress(function(e) {
    			if (e.keyCode == 13) {
    				$this.resetSafePwd();
    			}
    		});
        	$(submitBtn).on("click",function(event){
        		if(event.originalEvent) {
        			$this.resetSafePwd();
            	}else {
                	return JuaBox.showWrong(bitbank.L("请勿使用非法脚本"));
            	}
        	});
        };
        user.formDataInit = function(){
	        	userName = $.trim($("#nike").val());
	        	countryCode = $.trim($("#countryCode").val());
	        	logPassword = $("#password").val();
	        	setPassword = $("#password").val();
            	returnTo = $.trim($("#returnTo").val());
	        	repeatPassword = $("#confirmPwd").val();
	        	pwdLevel = $("#pwdLevel").val();
	        	cardId = $("#cardId").val();
	        	needImgCode = $("#isSafe").val() || true ;
	        	imgCode = $.trim($("#imgCode").val());
	        	msgCode = $.trim($("#msgCode").val());
	        	googleCode = $.trim($("#googleCode").val());
	        	logRememberMe = $("#rememberMe").is(":checked");
	        	recommendId = $("#recommendId").val();
				regAgreement = $("#agreement").is(":checked");
				prevUrl = $.cookie("prevhref");
        };
        user.isLogin = function(){
        	return $.cookie(UON) == "1" && $.cookie(UNAME) != null && $.cookie(UID) != null && $.cookie('zloginStatus')!=4;
        };
		user.needAuth = function(){
        	return $.cookie(LOGIN_STATUS) == 2;
        };
        user.login = function(){
        	if(lockRequest) return JuaBox.showWrong(bitbank.L("您有未完成的提交申请，请等待后重试"));
        	var $this = this ;
        	$this.formDataInit();

			if(!rsaPublicKey) return false;
        	if(userName == "") return JuaBox.showWrong(bitbank.L("请输入用户名"));
        	if(logPassword.length < 8) return JuaBox.showWrong(bitbank.L("登录密码最少8位字符，请重新输入"));
        	if(imgCode.length < 4 && needImgCode == 0) return JuaBox.showWrong(bitbank.L("图形验证码最少4位字符，请重新输入"));

    		var encrypt = new RSA();
    			encrypt.setPublicKey(rsaPublicKey);
    			logPassword = encrypt.encrypt(logPassword);

				lockRequest = true ;
        	    // $(submitBtn).button().html("loading");
                
        	    $.post(DOMAIN_VIP + "/login/doLogin?callback=?", {
					nike: userName,
					pwd: logPassword,
                    returnTo: returnTo,
					code: imgCode,
					countryCode: countryCode,
					safe: needImgCode,
					pubTag: rsaPublicKey,
					fromUrl:prevUrl
				}, function (json) {
					lockRequest = false;
					if (logRememberMe) {
						$.cookie(ZNAME + 'logusername', userName, { expires: 7, path: '/', domain: DOMAIN_BASE });
						$.cookie(ZNAME + 'logcountrycode', countryCode, { expires: 7, path: '/', domain: DOMAIN_BASE });
					}
					var mainData = json.des;
					if (json.isSuc) {
						$.cookie('prevhref', '', { expires: -1,path: '/' });
						// var zgoogleauth = $.cookie(ZNAME+'googleauth') === 'true';
						if (json.datas && (json.datas.diffIpAuthen || json.datas.loginGoogleAuth || json.datas.goSafe)) {
								// window.top.location.href = mainData;
								$this.loginAuth();
						} else {
							if ($.cookie(ZNAME + "fromurl")) {
								window.top.location.href = mainData;
							} else {
								window.top.location.href = mainData;
							}
						}
					} else if (json.datas.emailTips) {
						window.top.location.href = mainData;
					} else {
						$("#imgCode").val("");
						$(getImgBtn).click();
						JuaBox.showWrong(mainData);
						if (json.datas && json.datas.status == 0) {
							$('#isSafe').val(0);
							$('#imgCodeForm').show();
						}
						$(submitBtn).text(bitbank.L("立即登录"));
					}
				}, "json");

        	        };
        user.register = function(){	//注册
        	if(lockRequest) return JuaBox.showWrong(bitbank.L("您有未完成的提交申请，请等待后重试"));
        	var $this = this ;
        	$this.formDataInit();
			//if(userType == 0) return JuaBox.showWrong(bitbank.L("请输入正确的邮箱或者手机号码"));
			if(userName == "") return JuaBox.showWrong(bitbank.L("请输入手机号码/电子邮箱"));
        	if(userName.length < 6) return JuaBox.showWrong(bitbank.L("用户名最少6位字符，请重新输入"));
        	if(userType == 2 && !M.isEmail(userName)) return JuaBox.showWrong(bitbank.L("邮箱格式错误，请重新输入"));
        	if(userType == 1 && !M.isAllNumber(userName.replace(/[+,-]/g,""))) return JuaBox.showWrong(bitbank.L("手机号码格式错误，请重新输入"));
        	if(pwdLevel < 40) return JuaBox.showWrong(bitbank.L("密码8~20位字符，且为字母、数字、符号等任意2种以上组合。"));
        	if(setPassword != repeatPassword) return JuaBox.showWrong(bitbank.L("两次密码输入不一致，请重新输入"));
        	if(imgCode.length < 4 && needImgCode) return JuaBox.showWrong(bitbank.L("图形验证码最少4位字符，请重新输入"));
        	if(userType == 1 && takeMsgCode == false) return JuaBox.showWrong(bitbank.L("请先获取短信验证码"));
        	if(userType == 1 && msgCode.length < 6) return JuaBox.showWrong(bitbank.L("短信验证码最少6位字符，请重新输入"));
        	if(regAgreement == false) return JuaBox.showWrong(bitbank.L("请您同意用户服务协议"));
			lockRequest = true ;
        	$.ajax({
        		url : DOMAIN_VIP +"/register/"+ (userType == 2 ? "emailReg" : "mobileReg"),
        		type : "POST",
        		data : {
        			phonenumber: userName,
        			email: userName,
        			password: setPassword,
            	    code: imgCode,
            	    mobileCode: msgCode,
            	    pwdLevel: pwdLevel,
            	    tuijianId: recommendId,
            	    countryCode: countryCode,
            	    regAgreement: regAgreement
        		},
        		complete:function(){
        			lockRequest = false;
    				$(submitBtn).button("reset");
    				$("#imgCode").val("");
    				$(getImgBtn).click();
        		},
        		dataType : "json",
        		success : function(json){
        			if(json.isSuc) {
						$.cookie('sigup_first', 'first', { expires: 1, path: '/' });
        				if(userType == 2){   //邮箱注册成功
        					window.top.location.href = DOMAIN_VIP + "/register/emailTips?type=1&nid="+json.des;
        				}else{			//手机注册成功
							JuaBox.showTip(bitbank.L("恭喜您，注册成功!"));
							setTimeout(function(){
								window.top.location.href = DOMAIN_VIP + "/bw/manage/";
							}, 1500);
        				}
        			}else{	//注册失败
        				JuaBox.info(json.des);
        			}
        		}
        	});
        };
        user.findLoginPwd = function(){
        	if(lockRequest) return JuaBox.showWrong(bitbank.L("您有未完成的提交申请，请等待后重试"));
        	var $this = this ;
        	$this.formDataInit();

			// if(userType == 0) return JuaBox.showWrong(bitbank.L("请输入正确的邮箱或者手机号码"));
			if(userName == "") return JuaBox.showWrong(bitbank.L("请输入手机号码/电子邮箱"));
        	if(userName.length < 6) return JuaBox.showWrong(bitbank.L("用户名最少6位字符，请重新输入"));
        	if(userType == 2 && !M.isEmail(userName)) return JuaBox.showWrong(bitbank.L("邮箱格式错误，请重新输入"));
        	if(userType == 1 && !M.isAllNumber(userName.replace(/[+,-]/g,""))) return JuaBox.showWrong(bitbank.L("手机号码格式错误，请重新输入"));
        	if(imgCode.length < 4 && needImgCode) return JuaBox.showWrong(bitbank.L("图形验证码最少4位字符，请重新输入"));
        	if(userType == 1 && takeMsgCode == false) return JuaBox.showWrong(bitbank.L("请先获取短信验证码"));
        	if(userType == 1 && msgCode.length < 6) return JuaBox.showWrong(bitbank.L("短信验证码最少6位字符，请重新输入"));

        	        	lockRequest = true ;
        	// $(submitBtn).button().html("loading");

        	        	$.ajax({
        		url : DOMAIN_VIP +"/ac/password_dofind",
        		type : "POST",
        		data : {
        			userName:userName,
        			method:userType == 1 ? "mobile" : "email",
        			email:userName,
        			mobile:userName,
        			mobileCode:msgCode,
        			countryCode:countryCode,
        			code:imgCode,
        			cardId:cardId
        		},
        		complete:function(){
        			lockRequest = false;
    				$(submitBtn).button("reset");
    				$("#imgCode").val("");
    				$(getImgBtn).click();
        		},
        		dataType : "json",
        		success : function(json){
        			if(json.isSuc) {
        				if(userType == 2){
        					JuaBox.info(bitbank.L("邮箱找回登录密码成功提示", "http://mail." + userName.split("@")[1]));
        				}else{
        					window.location.href = json.datas.url;
        				}
        			}else{
        				JuaBox.info(json.des);
        			}
        		}
        	});
        };
        user.resetLoginPwd = function(){
        	if(lockRequest) return JuaBox.showWrong(bitbank.L("您有未完成的提交申请，请等待后重试"));
        	var $this = this ;
        	$this.formDataInit();

        	        	if(pwdLevel < 40) return JuaBox.showWrong(bitbank.L("密码8~20位字符，且为字母、数字、符号等任意2种以上组合。"));
        	if(setPassword != repeatPassword) return JuaBox.showWrong(bitbank.L("两次密码输入不一致，请重新输入"));

    		var encrypt = new RSA();
    			encrypt.setPublicKey(rsaPublicKey);
    			logPassword = encrypt.encrypt(repeatPassword);

        	        	lockRequest = true ;
        	// $(submitBtn).button().html("loading");

        	        	$.ajax({
        		url : DOMAIN_VIP +"/ac/password_doreset",
        		type : "POST",
        		data : {
        			pwd: logPassword,
    				reNewPwd: logPassword,
    				userId: $('#userId').val(),
    				code: $('#code').val(),
    				pwdLevel: pwdLevel
        		},
        		complete:function(){
        			lockRequest = false;
    				$(submitBtn).button("reset");
        		},
        		dataType : "json",
        		success : function(json){
        			if(json.isSuc) {
        				JuaBox.sure(json.des, {
    						closeFun:function() {
    							window.location.href = DOMAIN_VIP +"/u/";
    						}
    					});
        			}else{
        				JuaBox.sure(json.des);
        			}
        		}
        	});
        };
        user.findSafePwd = function(){
        	if(!user.isLogin()) return JuaBox.showWrong(bitbank.L("请您登录后再进行操作"));
        	if(lockRequest) return JuaBox.showWrong(bitbank.L("您有未完成的提交申请，请等待后重试"));
        	var $this = this ;
        	$this.formDataInit();

        	        	if(userType == 0) return JuaBox.showWrong(bitbank.L("请输入正确的邮箱或者手机号码"));
        	if(userName.length < 6) return JuaBox.showWrong(bitbank.L("用户名最少6位字符，请重新输入"));
        	if(logPassword.length < 8) return JuaBox.showWrong(bitbank.L("登录密码最少8位字符，请重新输入"));
        	if(userType == 2 && !M.isEmail(userName)) return JuaBox.showWrong(bitbank.L("邮箱格式错误，请重新输入"));
        	if(userType == 1 && !M.isAllNumber(userName.replace(/[+,-]/g,""))) return JuaBox.showWrong(bitbank.L("手机号码格式错误，请重新输入"));
        	if(imgCode.length < 4 && needImgCode) return JuaBox.showWrong(bitbank.L("图形验证码最少4位字符，请重新输入"));
        	if(userType == 1 && takeMsgCode == false) return JuaBox.showWrong(bitbank.L("请先获取短信验证码"));
        	if(userType == 1 && msgCode.length < 6) return JuaBox.showWrong(bitbank.L("短信验证码最少6位字符，请重新输入"));

        	        	lockRequest = true ;
        	// $(submitBtn).button().html("loading");

        	$.ajax({
        		url : DOMAIN_VIP +"/ac/safepwd_dofind",
        		type : "POST",
        		data : {
        			userName:userName,
        			passWord:logPassword,
        			method:userType == 1 ? "mobile" : "email",
        			email:userName,
        			mobile:userName,
        			mobileCode:msgCode,
        			countryCode:countryCode,
        			code:imgCode
        		},
        		complete:function(){
        			lockRequest = false;
    				$(submitBtn).button("reset");
    				$("#imgCode").val("");
    				$(getImgBtn).click();
        		},
        		dataType : "json",
        		success : function(json){
        			if(json.isSuc) {
        				if(userType == 2){
        					JuaBox.info(bitbank.L("邮箱找回登录密码成功提示", "http://mail." + userName.split("@")[1]));
        				}else{
        					window.location.href = json.datas.url;
        				}
        			}else{
        				JuaBox.info(json.des);
        			}
        		}
        	});
        };
        user.resetSafePwd = function(){
        	if(lockRequest) return JuaBox.showWrong(bitbank.L("您有未完成的提交申请，请等待后重试"));
        	var $this = this ;
        	$this.formDataInit();

        	        	if(pwdLevel < 40) return JuaBox.showWrong(bitbank.L("密码8~20位字符，且为字母、数字、符号等任意2种以上组合。"));
        	if(setPassword != repeatPassword) return JuaBox.showWrong(bitbank.L("两次密码输入不一致，请重新输入"));
        	if($("#googleCode").is(":hidden") == false && googleCode.length < 4) return JuaBox.showWrong(bitbank.L("请输入Google验证码"));

    		var encrypt = new RSA();
    			encrypt.setPublicKey(rsaPublicKey);
        	lockRequest = true ;
        	// $(submitBtn).button().html("loading");

        	$.ajax({
        		url : DOMAIN_VIP +"/ac/safepwd_doreset",
        		type : "POST",
        		data : {
        			safePwd: setPassword,
        			reNewPwd: setPassword,
        			googleCode: googleCode,
    				userId: $('#userId').val(),
    				code: $('#code').val(),
    				pwdLevel: pwdLevel
        		},
        		complete:function(){
        			lockRequest = false;
    				$(submitBtn).button("reset");
        		},
        		dataType : "json",
        		success : function(json){
        			if(json.isSuc) {
        				JuaBox.sure(json.des, {
    						closeFun:function() {
    							window.location.href = DOMAIN_VIP +"/u/";
    						}
    					});
        			}else{
        				JuaBox.sure(json.des);
        			}
        		}
        	});
        };
        user.checkUserNike = function(nike){
        	if(typeof nike == "undefined" || nike.length == 0 ){
        		return $("#countryGroup,#msgCodeForm").hide(),$("#countryGroup,.msgCodeForm_text").hide();
        	}
        	var nike = $.trim(nike);
        	if(M.hasLetter(nike) || M.hasOther(nike) || M.hasChinese(nike)){
				if(M.isEmail(nike)){
					$("#countryGroup,#msgCodeForm").hide();
					$("#countryGroup,.msgCodeForm_text").hide();
					userType = 2 ;
				}else{
					$("#countryGroup,#msgCodeForm").hide();
					$("#countryGroup,.msgCodeForm_text").hide();
					userType = 0 ;
				}
			}else{
				$("#countryGroup,#msgCodeForm").show();
				$("#countryGroup,.msgCodeForm_text").show();
				userType = 1 ;
			}
        };
        user.checkPwdStrength = function(pwd,div){

		    var level = 0, index = 1, div = div || ".bk-pwdcheck";
	        if (pwd.length >= 8 && pwd.length <= 20){
			    if (/\d/.test(pwd)) level++; 
			    if (/[a-z]/.test(pwd)) level++; 
			    if (/[A-Z]/.test(pwd)) level++; 
			    if (/\W/.test(pwd)) level++; 
			    if (level > 1 && pwd.length > 12) level++;
	        }
			$(div).find(".strength").removeClass("open_active_1 open_active_2 open_active_3 open_active_2_2 open_active_3_2");
	        if(level <= 2){
				$(div).find(".strength:nth-child(1)").addClass("open_active_1");
				$('#pwdStr').text(bitbank.L('注册-右侧内容-标签-4')).removeClass("med strong")			
			}
	        if(level == 3) {
				$(div).find(".strength:nth-child(1)").removeClass("open_active_1").addClass("open_active_2_2");
				$(div).find(".strength:nth-child(2)").addClass("open_active_2");
				$('#pwdStr').text(bitbank.L('注册-右侧内容-标签-5')).removeClass("strong").addClass("med")
			}
	        if(level > 3) {
				$(div).find(".strength:lt(2)").removeClass("open_active_2_2 open_active_2").addClass("open_active_3_2");
				$(div).find(".strength:nth-child(3)").addClass("open_active_3");
				$('#pwdStr').text(bitbank.L("注册-右侧内容-标签-6")).removeClass("med").addClass("strong")
			}
	        $(div).find("input[name='pwdLevel']").val(level * 20);
        };
        user.getMsgCode = function(type, callback){
        	if(lockRequest) return JuaBox.showWrong(bitbank.L("您有未完成的提交申请，请等待后重试"));
        	var $this = this ;
        	$this.formDataInit();

        	if(userName.length < 6) return JuaBox.showWrong(bitbank.L("用户名最少6位字符，请重新输入"));
        	if(userType == 1 && !M.isAllNumber(userName.replace(/[+,-]/g,""))) return JuaBox.showWrong(bitbank.L("手机号码格式错误，请重新输入"));
        	if(imgCode.length < 4 && needImgCode) return JuaBox.showWrong(bitbank.L("图形验证码最少4位字符，请重新输入"));
        	var data = {
    				phonenumber: userName,
        			countryCode: countryCode,
        			code: imgCode,
    				codeType: 1
        		}
	    	if(type == "register"){
	    		data.codeType = 1;
	    	}
	    	if(type == "findLoginPwd"){
	    		data.codeType = 16;
	    		data.cardId = cardId;
	    	}
	    	if(type == "findSafePwd"){
	    		data.codeType = 17;
	    	}
        	lockRequest = true ;

        	$.ajax({
        		url : DOMAIN_VIP + "/register/sendCode",
        		type : "POST",
        		data : data,
        		complete:function(){
        			lockRequest = false;
        			takeMsgCode = true ;
        		},
        		dataType : "json",
        		success : function(json){
        			if (json.isSuc) {
        				JuaBox.showRight(json.des);
        				if($.isFunction(callback)){ callback();};
        				return (function(getMsgBtn){
        					var _arguments = arguments;
        		        	if(codeCountTime == 0) {
        						$(getMsgBtn).removeAttr("disabled").text(bitbank.L("点击获取"));
        						codeCountTime = 60;
        					}else{ 
        						$(getMsgBtn).attr("disabled", true).text(bitbank.L("n秒后重试", codeCountTime));
        						codeCountTime -- ; 
        						return setTimeout(function(){
        							_arguments.callee(getMsgBtn)
        						},1000);
        					}
        		        })(getMsgBtn);
        			} else {
        				JuaBox.info(json.des);
        				$("#imgCode").val("");
        				$(getImgBtn).click();
        			}
        		}
        	});
        };
        user.getCommonMsgCode = function(datas, callback){
        	var $this = this ;
        	if(!datas) return false;
        	if(lockRequest) return JuaBox.showWrong(bitbank.L("您有未完成的提交申请，请等待后重试"));
        	lockRequest = true ;

        	$.ajax({
        		url : DOMAIN_VIP + "/userSendCode",
        		type : "POST",
        		data : datas,
        		complete:function(){
        			lockRequest = false;
        		},
        		dataType : "json",
        		success : function(json){
        			if(json.isSuc){
    					if(json.datas.isEmail){
    						JuaBox.sure(json.des);
    					}
    					if($.isFunction(callback)){ callback();};
        				return (function(getMsgBtn){
        					var _arguments = arguments;
        		        	if(codeCountTime == 0) {
        						$(getMsgBtn).removeAttr("disabled").text(bitbank.L("点击获取"));
        						codeCountTime = 60;
        					}else{ 
        						$(getMsgBtn).attr("disabled", true).text(bitbank.L("n秒后重试", codeCountTime));
        						codeCountTime -- ; 
        						return setTimeout(function(){
        							_arguments.callee(getMsgBtn)
        						},1000);
        					}
        		        })(getMsgBtn);
    				}else if('needMobileAuth' == json.des) {
    					JuaBox.sure(bitbank.L("您未进行手机认证，请先进行手机认证"));
    				}else{
    					JuaBox.sure(json.des);
    				}
        		}
        	});
        };
        user.getPublicKey = function(callback){
        	var $this = this ;
        	$.getJSON(DOMAIN_VIP + "/login/getPubTag?d="+new Date().getTime(), function(json) {
        		$('input[name="publicKey"]').val(json.datas.pubTag);
        		rsaPublicKey = json.datas.pubTag;

        		        		if($.isFunction(callback)){
        			callback();
        		};
        	});
        }; 
        user.getUserInfo = function(callback){
        	if(!user.isLogin() || user.needAuth) return false;
        	var $this = this ;
        	$.getJSON(DOMAIN_VIP + "/manage/getUserInfo?callback=?", function(result) {
        		$this.vipRate       =  result.vipRate ;
        		$this.totalPoints   =  result.totalPoints ;
                $this.simpleAuth    =  result.simpleAuth ;
                $this.depthAuth     =  result.depthAuth ;
                $this.emailAuth     =  result.emailAuth ;
                $this.mobileAuth    =  result.mobileAuth ;
                $this.googleAuth    =  result.googleAuth ;
                $this.pwdStatus     =  result.pwdStatus ;
                $this.safePwdStatus =  result.safePwdStatus ;
        	    if($.isFunction(callback)){
    				callback();
    			};
        	});
        }; 
        user.safePwdForm = function(){
        	var safePwdHtml =  "";
	        	safePwdHtml += "<div id='safeWordForm'>";
	        	safePwdHtml += "    <div class='form-group clearfix' style=''>";
	        	safePwdHtml += "      <label for='safePwd' class='control-label sr-only'>"+bitbank.L("请输入您的安全密码")+"</label>";
	        	safePwdHtml += "      <input type='password' placeholder='"+bitbank.L("请输入您的安全密码")+"' class='form-control' id='safePwd' name='safePwd' style=''>";
	        	safePwdHtml += "      <a class='pull-right' target='_blank' href='"+DOMAIN_VIP+"/ac/safepwd_find'>"+bitbank.L("忘记安全密码")+"？</a>";
	        	safePwdHtml += "    </div>";
	        	safePwdHtml += "    <div class='form-group clearfix mb20' style='width:100%; font-size:12px; '>";
	        	safePwdHtml += "      <label class='radio-inline'>";
	        	safePwdHtml += "        <input type='radio' name='closeStatu' value='' checked>"+bitbank.L("一直开启");
	        	safePwdHtml += "      </label>";
	        	safePwdHtml += "      <label class='radio-inline'>";
	        	safePwdHtml += "        <input type='radio' name='closeStatu' value='6'>"+bitbank.L("关闭6小时");
	        	safePwdHtml += "      </label>";
	        	safePwdHtml += "      <label class='radio-inline'>";
	        	safePwdHtml += "        <input type='radio' name='closeStatu' value='1'>"+bitbank.L("永久关闭");
	        	safePwdHtml += "      </label>";
	        	safePwdHtml += "    </div>";
	        	safePwdHtml += "    <input type='hidden' id='needMobile' name='needMobile' value='false'/>";
	        	safePwdHtml += "    <input type='hidden' id='needPwd' name='needPwd' value='true'/>";
	        	safePwdHtml += "    <p style='font-size:12px;'>"+bitbank.L("温馨提示：您可以在此处设置是否启用交易安全密码。")+"</p>";
	        	safePwdHtml += "</div>";
	        return safePwdHtml ;
        };
        user.closeSafePwd = function(callback){
        	if(!user.isLogin()) return false;
        	var $this = this ;
        	JuaBox.info($this.safePwdForm(),{
        		title:bitbank.L("交易安全密码状态设置"),
        		btnFun1:function(JuaId){
        			if($("#safePwd").val() == "" || $("#safePwd").val().length < 6){
        				return JuaBox.info(bitbank.L("资金安全密码不能少于6位数，也不能为空。"))
        			};
        			$.post(DOMAIN_VIP +"/manage/useOrCloseSafePwd?callback=?", {
                	    payPass: $("#safePwd").val(),
                	    closeStatu: $('input[name="closeStatu"]:checked').val()
                	}, function(json) {
                	    var des = json.des;
                	        JuaBox.close(JuaId,function(){
                	    		JuaBox.info(des,{
                	    			closeFun:function(){
                        	    		if($.isFunction(callback)){
                        	    			callback();
                        	    		}
                	    			}
                	    		});
                	    	});
                	}, "json");
        		},
        		endFun:function(JuaId){
                	$("#JuaBox_"+JuaId).keypress(function(e) {
            			if (e.keyCode == 13) {
            				$("#JuaBtn_"+JuaId+"_1").click();
            			}
            		});
        		}
        	});
        }
        user.openSafePwd = function(callback){
        	if(!user.isLogin()) return false;
        	JuaBox.info(bitbank.L("您确定要开启交易安全密码吗？"),{
        		btnFun1 : function(JuaId){
        			$.getJSON(DOMAIN_VIP + "/manage/useOrCloseSafePwd?callback=?", function(json) {
            			JuaBox.close(JuaId,function(){
            				JuaBox.showTip(json.des,{
            					closeFun:function(){
                    	    		if($.isFunction(callback)){
                    	    			callback();
                    	    		}
            					}
            				});
            			});
            		});
        		}
        	});
        }
		user.useOrCloseTip = function(type,status,callback){
			if(!user.isLogin()) return false;
			$.getJSON(DOMAIN_VIP + "/manage/useOrCloseFun?attr="+type+"&closeStatu="+status+"&callback=?", function(json) {
				JuaBox.showTip(json.des);
				if($.isFunction(callback)){
					callback();
				}
			});
		}
        user.loginAuthForm = function(){
        	var safePwdHtml =  "";
	        	safePwdHtml += "<div id='loginAuthForm'>";
	        	safePwdHtml += "    <div class='form-group' id='loginGoogleAuth'>";
	        	safePwdHtml += "      <label for='vercode' class='control-label'>"+bitbank.L("本次登录需要Google验证码：")+"</label>";
				safePwdHtml += "      <input type='text' placeholder="+bitbank.L('Google验证码')+" class='form-control' id='vercode' name='vercode' maxlength='6'>";
				safePwdHtml += "      <p>&nbsp;</p>";
	        	safePwdHtml += "    </div>";
	        	safePwdHtml += "    <div class='form-group' id='loginIpAuth' style='display:none'>";
	        	safePwdHtml += "      <label for='vercode' class='control-label'>"+bitbank.L("本次登录需要进行异地登录验证：")+"</label>";
	        	safePwdHtml += "      <div class='input-group'>";
	        	safePwdHtml += "        <input type='text' placeholder="+bitbank.L("短信/邮件验证码")+" class='form-control' id='mobileCode' name='mobileCode'>";
	        	safePwdHtml += "        <div class='input-group-btn'>";
	        	safePwdHtml += "          <div class='btn-group'>";
	        	safePwdHtml += "            <button type='button' role='msgCode' id='sendMsgCode' class='btn text-nowrap' style='border-left:none;'>"+bitbank.L("点击获取")+"</button>";
	        	safePwdHtml += "          </div>";
				safePwdHtml += "        </div>";
				safePwdHtml += "        <p>&nbsp;</p>";
	        	safePwdHtml += "      </div>";
	        	safePwdHtml += "    </div>";
	        	safePwdHtml += "    <p style='font-size:12px;'>"+bitbank.L("温馨提示：登录成功后可以在安全设置中修改登录验证方式。")+"</p>";
	        	safePwdHtml += "</div>";
	        return safePwdHtml ;
        };
        user.loginAuth = function(callback){
        	if(!user.isLogin()) return false;
			var $this = this ;
			loginGoogleAuth = $.cookie(ZNAME+'googleauth') === 'true';
        	JuaBox.info($this.loginAuthForm(),{
				title:bitbank.L("登录安全验证"),
				tipClass:"loginGoogleAuthJuaBox",
        		btnName1:bitbank.L("提交"),
				btnNum:2,
				// btnName2:bitbank.L("[切换账号]"),
        		// maskFun:function(JuaId){
				// 	// console.log(JuaId)
				// },
        		btnFun1:function(JuaId){
        			user.googleAuthPush(JuaId);
				},
				btnFun2:function(){
        			window.location.href="/login/logout/";
        		},
        		endFun:function(JuaId){
        			if(loginGoogleAuth){
						$("#loginGoogleAuth").show();
						var vercode = $("#vercode");
						vercode.on("keyup",function(){
							var cup=$(this);  
							if(/[^\d]/.test(cup.val())){//替换非数字字符  
							  var temp_amount=cup.val().replace(/[^\d]/g,'');  
							  $(this).val(temp_amount);  
							}
							var cupNumber = cup.val();
							if(cupNumber.length == 6 && !loginIpAuth){
								user.googleAuthPush(JuaId);
							}else if(cupNumber.length < 6){
								$("#JuaBox_"+JuaId).removeClass("shake");
								vercode.parent().removeClass("red");
								vercode.parent().find("p").html("&nbsp;");
							}
						})
						console.log(JuaId);
        			}else{
						$("#loginGoogleAuth").hide();
					}
        			if(loginIpAuth){
        				$("#loginIpAuth").show();
        	        	$(getMsgBtn).on("click",function(event){
        	        		if(event.originalEvent) {
        	        			$this.getCommonMsgCode({
        	        				codeType : 65
        	        			});
        	            	}else {
        	                	return JuaBox.showWrong(bitbank.L("请勿使用非法脚本"));
        	            	}
        	        	});
        			}else{
						$("#loginIpAuth").hide();
					}
                	// $("#JuaBox_"+JuaId).keypress(function(e) {
            		// 	if (e.keyCode == 13) {
          			// 	$("#JuaBtn_"+JuaId+"_1").click();
            		// 	}
            		// });
                	JuaBox.position();
				},
				closeFun:function(JuaId){
					window.location.href="/login/logout/";
				}
        	});
        };
		user.googleAuthPush = function(JuaId){
			var datas = {};
			var vercodeElm = $("#vercode");
			var vercode = vercodeElm.val(), 
			mobileCode = $("#mobileCode").val();
			if(loginGoogleAuth){
				if(vercode.length < 6) {
					$("#JuaBox_"+JuaId).addClass("shake");
					setTimeout(function(){$("#JuaBox_"+JuaId).removeClass("shake");},200)
					vercodeElm.parent().addClass("red");//JuaBox.showWrong(bitbank.L("请输入合法的验证码"));
					vercodeElm.parent().find("p").text(bitbank.L("请输入合法的验证码"));
				}else{
					datas.vercode = vercode;
					if(!loginIpAuth){
						$("#JuaBox_"+JuaId).find("#JuaBtn_"+JuaId+"_1").addClass("disabled").html(" &#xe60d; ");
						pushdata(datas);
					}
				}
			}
			if(loginIpAuth){
				if(mobileCode.length < 6) return JuaBox.showWrong(bitbank.L("请输入合法的验证码"));
				datas.mobileCode = mobileCode;
			}

			// if(lockRequest) return JuaBox.showWrong(bitbank.L("您有未完成的提交申请，请等待后重试"));
			lockRequest = true ;
			function pushdata(data){
				$.ajax({
					type: "get",
					url: DOMAIN_VIP +"/login/doLoginAuthen?r=" + new Date().getTime(),
					data: data,
					dataType: "json",
					complete:function(){
						lockRequest = false;
					},
					success: function(json) {
						if(json.isSuc){
							// JuaBox.close(JuaId,function(){
								var toUrl = DOMAIN_VIP + '/bw/trade/'
								if($.cookie(ZNAME+'fromurl')){
									toUrl = $.cookie(ZNAME+'fromurl');
								}
								window.top.location.href = toUrl;	
							// })
						}else{
							$("#JuaBox_"+JuaId).addClass("shake");
							setTimeout(function(){
								$("#JuaBox_"+JuaId).removeClass("shake");
								vercodeElm.val("");
							},200)
							vercodeElm.parent().addClass("red");
							vercodeElm.parent().find("p").text(json.des);
							$("#JuaBox_"+JuaId).find("#JuaBtn_"+JuaId+"_1").removeClass("disabled").html(bitbank.L("提交"));
							// JuaBox.info(json.des,{
							// 		btnFun2:function(){
							// 				window.top.location.href = DOMAIN_VIP + '/login/logout';
							// 		}
							// });
						}
					}
				});
			}
		};
        user.hasSafePwd = function(){
        	if(!user.isLogin()) return false;
        	var $this = this;
        	$.ajax({
        	    url: DOMAIN_VIP +"/manage/isTransSafe?callback=?", 
        	    type: 'post', 
        	    dataType: 'json', 
        	    success: function(json) {
        	    	if(json.des=="false"){
        				return false ;
        			}else{
        				return true ;
        			}
        	    }
        	});
        };

        user.loginLogsInit = function(){
        	   	var $this = this ;
	        	var pageIndex   =    1,
	        		pageSize    =    10,
	        		listDiv     =    "#loginLogsDetail";
	        $this.loginLogsDetail(listDiv, pageIndex, pageSize);	
        }
        user.loginLogsDetail = function(listDiv,pageIndex,pageSize){
        	    if(!user.isLogin()) return false;
        	    var $this = this ;
        	    var url = '/manage/queryUserLoginHistroy',
        	        listDiv = listDiv;
            var htmlNoRecord = "<tr><td colspan='7'>"+bitbank.L("通用没有任何记录")+"</td><tr>";
	        	$.ajax({
	    		 		type:"POST",
	    		 		url:"/manage/queryUserLoginHistroy",
	    		 		data: {
		    				pageIndex : pageIndex,
		    				pageSize:pageSize
		    			},
	    		 		dataType: 'json',
	    		 		success:function(data){
	    		 			if (data.isSuc) {
		    					if(data.datas.list == null || data.datas.list.length == 0 ){
			    	        			$(listDiv).html(htmlNoRecord);
				    	        		if(pageIndex == 1){
				            				$(listDiv+"_Page").html("");
				            		}else{
				            		  $this.showLogPage(listDiv, pageIndex, 0, pageSize);
				            		}
			    	    			}else{
			    	    				$(listDiv).html(tmpl("tmpl-"+(listDiv.replace(/[#,.]/g,"")), $this.formatloginLogs(data.datas.list))); 
	    		 			        $this.showLogPage(listDiv, pageIndex, data.datas.totalCount, pageSize);
			    	    			}
		    				} 
	    		 		}
	    		 	});

        	            }
        user.showLogPage = function(listDiv, pageIndex, rsCount, pageSize){
        		var $this = this;
        	    var $pageDiv = $(listDiv+"_Page");
	        	if(rsCount < pageSize && pageIndex == 1) {
	        		$pageDiv.html("");
	        		return false;
	        	}
	        	var pageCount = rsCount % pageSize == 0 ? parseInt(rsCount / pageSize) : parseInt(rsCount / pageSize) + 1 ;
        	    $pageDiv.createPage({
	    			noPage:false,
	    			pageSize:pageSize,
			        rsCount:rsCount,
			        pageCount:pageCount,
			        current: pageIndex || 1,
			        backFn:function(pageNum){
			            $this.loginLogsDetail(listDiv,pageNum,pageSize);
			        }
		    });
        }
        user.formatloginLogs = function(json){
        	    	var record = [];
        	    for(var i = 0;i<json.length;i++){
        	    	    var time = json[i].date;
        	    	    var type = json[i].terminal == 1? bitbank.L("网页登录") : bitbank.L("App登录");
        	    	    var ip = json[i].ip;
	    			record[i] = {};
	    			record[i].Time = LANG == 'en'? M.formatDate(time, "MM-dd-yyyy hh:mm:ss") : M.formatDate(time, "yyyy-MM-dd hh:mm:ss");
	    			record[i].type = type;
	    			record[i].ip = ip;
	    			record[i].status = bitbank.L("成功");
        	    }
        	    return record;
        }
		user.accountIndexTipsHover = function (){
			$(".help_hover").mouseover(function(){
				$(".help_div").show();
			})
			$(".help_hover").hover(function () {
				$(".help_div").show();
			},function(){
				$(".help_div").hide();
			})
		}
		user.sigupTips = function(){
			var html = '<div class="sigup_tips_one">'+
						'<div class="tips_bg"></div>'+
						'<div class="tips_body '+LANG+'">'+
							'<img src="' + DOMAIN_STATIC + '/statics/img/common/sigup_tips.png">' +
							'<div class="tips_title">'+
								bitbank.L("您当前帐户安全等级:")+
								'<span>'+
									'<svg class="icon" aria-hidden="true">'+
										'<use xlink:href="#icon-anquancopy"></use>'+
									'</svg>'+
									bitbank.L("低")+
								'</span>'+
							'</div>'+
							'<div class="tips_text">'+ bitbank.L("为了您的帐户安全，我们强烈建议您提升帐户安全等级。") +'</div>'+
							'<section class="tips_tab clearfix">'+
								'<a href="' + DOMAIN_VIP + '/manage/auth/pwd/safe">' +
									'<svg class="icon" aria-hidden="true">'+
										'<use xlink:href="#icon-zijinmima"></use>'+
									'</svg>'+
									'<div class="tip_tab_title">'+
											bitbank.L("资金密码")+
									'</div>'+
									'<div class="tip_tab_text">'+
										bitbank.L("用于保护您的资金安全。充值、提现、绑定手机邮箱时需要验证资金密码")+
									'</div>'+
								'</a>'+
								'<a href="' + DOMAIN_VIP + '/manage/auth/google">' +
									'<svg class="icon" aria-hidden="true">'+
										'<use xlink:href="#icon-anquan1"></use>'+
									'</svg>'+
									'<div class="tip_tab_title">'+
										bitbank.L("Google验证")+
									'</div>'+
									'<div class="tip_tab_text">'+
										bitbank.L("开启Google验证，可以进一步提升您的帐户安全性。") +
									'</div>'+
								'</a>'+
							'</section>'+
							'<div class="close_tips_warp">'+
								'<span class="close_tips">'+
									bitbank.L("稍后设置")+'<i> > </i>' +
								'</span>'+
							'</div>	'+
						'</div>'+
					'</div>';
				$("body").append(html);
				$(".close_tips").on("click",function(){
					$.cookie('sigup_first', '', { expires: -1, path: '/' });
					$(".sigup_tips_one").remove();
				})
		}
        module.exports = user;
        (function(){ return this || (0,eval)('this'); }()).user = user;
});