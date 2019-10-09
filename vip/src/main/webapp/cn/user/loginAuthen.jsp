<%@ page session="false" language="java" import="java.util.*"	pageEncoding="utf-8"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${L:l(lan,'二次登录验证')}-${WEB_NAME }-${WEB_TITLE }</title>
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<link href="${static_domain }/statics/css/web.log.css?V${CH_VERSON }" rel="stylesheet" type="text/css" />
<script type="text/javascript">
	$(function() {
		$("body").bind("keyup", function(event) {
			if (event.keyCode == "13") {
				loginGoogleAuth();
			}
		});

	});
	
	function loginAuth() {
		var datas = FormToStr("commonform");
	    if (datas == null) return;
		$.ajax({
            type: "get",
            url: "/login/doLoginAuthen?r=" + new Date().getTime(),
            data: datas,
            dataType: "json",
            error: function() {
                JuaBox.showWrong(vip.L("认证失败"));
            },
            success: function(json) {
                if (json.isSuc) {
                    if ($.cookie(ZNAME+"fromurl")) {
											window.top.location.href = $.cookie(ZNAME+"fromurl");
										} else {
											window.top.location.href = DOMAIN_VIP;
										}
                } else {
                    JuaBox.showWrong(json.des);
                }
            }
        });
		
		return false;
	}
	
	function sendCode() {
		$.ajax({
			type : "POST",
			url : "/userSendCode",
			data : {
				codeType : 65
			},
			dataType : "json",
			error : function() {
				JuaBox.info(jsLan[1]);
				inAjaxing = false;
			},
			success : function(json) {
				inAjaxing = false;
				if (json.isSuc) {
					if (json.datas.isEmail) {
						JuaBox.info(json.des);
					}
					settime($('#sendCodeBtn'));
				} else if ('needMobileAuth' == json.des) {
					JuaBox.info(bitbank.L("您未进行手机认证，请先进行手机认证"));
				} else {
					JuaBox.info(json.des);
				}
			}
		});
	}

	var countdown = 60;
	function settime(obj) {
		if (countdown == 0) {
			$(obj).removeAttr("disabled");
			$(obj).val("${L:l(lan,'点击获取')}");
			countdown = 60;
		} else {
			$(obj).attr("disabled", true);
			$(obj).val("${L:l(lan,'已发送')}"+"(" + countdown + ")");
			countdown--;
			setTimeout(function() {
				settime($(obj))
			}, 1000)
		}
	}
</script>

</head>
<body class="body-bg">
<div class="bk-body">
<jsp:include page="/common/top.jsp" />
<div class="page-signup ng-scope" ng-controller="authCtrl">

    <div class="wrapper" id="logBox" style="margin-top:60px;">
    
        <div class="main-body">
            <div class="body-inner">
                <div class="card">
                
                	<div class="card-content" id="commonform">
                		<c:if test="${fn:length(ip) > 0 }">
	                        <div class="hd">
	                        	<h2>${L:l(lan,'登录异常') }</h2>
								<p class="text-success" style="padding:5px 0px 5px 0px; color:#F00; ">
								${L:l1(lan,'系统检测到您本次登录IP(%%)为异地登录，需要验证手机。',ip) }
								</p>
	                        </div>
	                        <div class="bd">
	                          <div>
	                          	<div class="form-horizontal ng-pristine ng-valid">
	                            <fieldset>
	                                <div class="form-group">
	                                    <div class="ui-input-group">        
	                                        <input type="text" required="" name="mobileCode" id="mobileCode" class="form-control">
	                                        <span class="input-bar"></span>
	                                        <label>${L:l(lan,'短信/邮件验证码') }</label>
	                                        <div class="input-add"><input type="button" name="sendCodeBtn" class="btn btn-primary btn-sm" id="sendCodeBtn" value="${L:l(lan,'点击获取')}" onclick="sendCode()"></div>
	                                    </div>
	                                </div>
	                            </fieldset>
	                          	</div>
	                          	</div>
		                	</div> 
                		</c:if>
	                	<c:if test="${fn:length(needGoogle) > 0 }">
	                        <div class="hd">
	                        	<h2>Google验证</h2>
								<p class="text-success"  style="padding:15px 0px 10px 0px; color:#F00; ">${L:l(lan,'为了您的账户安全，登录时需要进行Google验证。') }</p>
	                        </div>
	                        <div class="bd">
	                          	<div>
	                            <fieldset>
	                                <div class="form-group">
	                                    <div class="ui-input-group">        
	                                        <input type="password"  name="vercode" id="vercode" class="form-control">
	                                        <span class="input-bar"></span>
	                                        <label>Google验证码</label>
	                                    </div>
	                                </div>
	                                
	                            </fieldset>
	                         	</div>
	                		</div> 
	                	</c:if>
	                    <div class="card-action no-border text-center">
	                        <input type="hidden" id="isSafe" value="1">
	                        <button id="fixedBtn" type="button" onclick="loginAuth()" data-loading-text="Loading..." class="btn btn-outsecond"><i class="bk-ico userlog"></i>验 证</button>
	                    </div>
            		</div>
        		</div>
    		</div>
		</div>
	</div>
</div>
</div>


 
			
			
</body>
</html>
