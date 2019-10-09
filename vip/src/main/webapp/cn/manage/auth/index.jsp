<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ page session="false"%>
<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${L:l(lan,'安全设置')}-${WEB_NAME}-${WEB_TITLE }</title>
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<link rel="stylesheet" href="${static_domain }/statics/css/web.user.css?V${CH_VERSON }">
<style type="text/css">
	html,body{
		height:auto;
	}
</style>
</head>
<body class="">

<div class="bk-body">
	<!-- Common TopMenu Begin -->
	<jsp:include page="/common/top.jsp" />
	<!-- Common TopMenu End -->
	<!-- Body From mainPage Begin -->
	<div class="mainer2">
		<div class="container">
			<jsp:include page="/common/trend.jsp" />
			<div class="user-panel">
				<jsp:include page="/cn/manage/auth/menu.jsp"/>
				<c:if test="${func_des=='安全设置' }">
					<jsp:include page="base.jsp"/>
				</c:if>
				<c:if test="${func_des=='异地登录验证' }">
					<jsp:include page="login/smsLoginCheck.jsp"/>
				</c:if>
				<c:if test="${func_des=='登录Google验证' }">
					<jsp:include page="login/loginGoogleAuth.jsp"/>
				</c:if>
				<c:if test="${func_des=='提币手机验证' }">
					<jsp:include page="pay/payMobileAuth.jsp"/>
				</c:if>
				<c:if test="${func_des=='提币Google验证' }">
					<jsp:include page="pay/payGoogleAuth.jsp"/>
				</c:if>
				<c:if test="${func_des=='提币邮箱验证' }">
					<jsp:include page="pay/payEmailAuth.jsp"/>
				</c:if>
			</div>
	    </div>
	</div>
	
  
<script type="text/javascript">

function loginGoogleAuth(ope) {
// 	JuaBox.frame('/manage/auth/loginGoogleAuth?ope='+ope, {width:450});
	location.href = "/manage/auth/loginGoogleAuth?ope="+ope;
}
function payGoogleAuth(ope) {
// 	JuaBox.frame('/manage/auth/payGoogleAuth?ope='+ope, {width:450});
	location.href = "/manage/auth/payGoogleAuth?ope="+ope;
}
function payMobileAuth(ope) {
// 	JuaBox.frame('/manage/auth/payMobileAuth?ope='+ope, {width:450});
	location.href = "/manage/auth/payMobileAuth?ope="+ope;
}
function payEmailAuth(ope) {
// 	JuaBox.frame('/manage/auth/payEmailAuth?ope='+ope, {width:450});
	location.href = "/manage/auth/payEmailAuth?ope="+ope;
}
function smsLoginCheck(ope) {
	//JuaBox.frame('/manage/auth/smsLoginCheck?ope='+ope, {width:450});
	location.href = "/manage/auth/smsLoginCheck?ope="+ope;
}
</script>
      
	<!-- Body From mainPage End -->
	<!-- Common FootMain Begin -->
	<jsp:include page="/common/foot.jsp" />
	<!-- Common FootMain End -->
</div>

<script type="text/javascript">
		require(['module_user'],function(user){
			user.pageIndexInit();
		});	
</script>


<script type="text/javascript">
    /*seajs.use('module_user', function (user) {
        user.pageIndexInit();
    });*/
	require(['module_method'],function(method){
			var href_name = method.GetQueryString("address");
			if(href_name == "address_withdraw"){
				
				var $that = $(".listtop_address").find('a');
				if($that.hasClass('rotate-90')){
						$that.removeClass('rotate-90');
				}else{
						$that.addClass('rotate-90');
				}
				$(".listtop_address").next().slideToggle();
			}
	});	

    $(function () {
        $(".smallpaybtn").click(function () {
            $(this).toggleClass("on");
        });

        $(".stratecont .listtop").click(function () {
            //alert($(this).html());
            var $that = $(this).find('a');
            if($that.hasClass('rotate-90')){
            	     $that.removeClass('rotate-90');
            }else{
            	     $that.addClass('rotate-90');
            }
            $(this).next().slideToggle();
            //$(this).slidetoggle();
        })
    });

    function switchAuth(category, type){
        JuaBox.frame('/manage/auth/switchAuth?category=' + category + "&type=" + type, {width: 450});
    }
    function openSafePwd(category, type){
        $.ajax({
            type: "POST",
            url: "/manage/auth/changeAuth",
            data: {
                category: category,
                type: type
            },
            dataType: "json",
            error: function () {
                JuaBox.sure("net error!");
            },
            success: function (json) {
                if (json.isSuc) {
                    JuaBox.closeAll(function () {
                        JuaBox.sure(json.des, {
                            btnFun1: function () {
                                window.top.location.reload();
                            }
                        });
                    });
                } else {
                    JuaBox.sure(json.des);
                }
            }
        });
    }
	function authenType(category, type,nums) {
		if(type == 2 && nums == 0 ){
			JuaBox.info('${L:l(lan, "您正在开启安全模式，开启后，添加新地址时将进行安全验证，并锁定该地址24小时。您是否要继续？")}', {
				btnFun1: function () {
					authenType_post(category, type);
				},
				btnName1:'${L:l(lan, "继续开启")}'
			});
		}
		else if(type == 2 && nums == 1 ){
			JuaBox.info('${L:l(lan, "您正在开启安全模式，开启后，您的账户将锁定24小时，在此期间不支持提现操作，添加新地址时将进行安全验证，并锁定该地址24小时。您是否要继续？")}', {
				btnFun1: function () {
					authenType_post(category, type);
				},
				btnName1:'${L:l(lan, "继续开启")}'
			});
		}
		else{
			JuaBox.info('${L:l(lan, "您正在开启初级模式，开启后，添加新地址时将不会进行安全验证。并且您的账户将被锁定24小时，在此期间不支持提现操作，可正常交易。您是否要继续？")}', {
				btnFun1: function () {
					authenType_post(category, type);
				},
				btnName1:'${L:l(lan, "继续开启")}'
			});
		}
		
	}
	function authenType_post(category, type) {
		 $.ajax({
            type: "POST",
            url: "/manage/auth/changeAuth",
            data: {
                category: category,
                type: type
            },
            dataType: "json",
            error: function () {
                JuaBox.sure("net error!");
            },
            success: function (json) {
                if (json.isSuc) {
                     window.top.location.reload();
                } else {
                    JuaBox.sure(json.des);
                }
            }
        });
	}
</script>

</body>
</html>
