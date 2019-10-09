<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!doctype html>
<html>
<head>
	<jsp:include page="/common/head.jsp" />
	<title>${WEB_NAME }-${WEB_TITLE }</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0 maximum-scale=1, user-scalable=no">
	<meta name="keywords" content="${WEB_KEYWORD }" />
	<meta name="description" content="${WEB_DESC }" />
	<link rel="stylesheet" href="http://at.alicdn.com/t/font_744069_76qz3hgj8v4.css">
	<link href="${static_domain }/statics/css/web.user.css?V${CH_VERSON }" rel="stylesheet" type="text/css" />
    <link href="${static_domain}/statics/css/iconfont_new.css" rel="stylesheet" type="text/css" >
	<script>
        if(JuaBox.isMobile()){
            JuaBox.mobileFontSize();
            $("<link>").attr({ rel: "stylesheet",type: "text/css",href: "${static_domain }/statics/css/mobile/mobile.user.css?V${CH_VERSON }"}).appendTo("head");
            $("<link>").attr({ rel: "stylesheet",type: "text/css",href: "${static_domain }/statics/css/mobile/top_foot_mobile.css?V${CH_VERSON }"}).appendTo("head");
        }
	</script>
	<script type="text/javascript">
        $(function(){
            if(JuaBox.isMobile()){
                $(".bk-app").slideDown();
                var showMenuInter ;
                $("input,textarea").focus(function(){
                    clearTimeout(showMenuInter);
                    $(".bk-app").hide();
                });
                $("input,textarea").click(function(){
                    clearTimeout(showMenuInter);
                    $(".bk-app").hide();
                });
                $("input,textarea").blur(function(){
                    showMenuInter = setTimeout(function(){
                        $(".bk-app").slideDown();
                    },1500);
                });
            }

        });
	</script>
	<style>
		.text-p{
			font-size: 16px;
			margin-bottom: 60px;
			margin-top:60px;
			color:#9199af;
		}
		.bk-sign-form .form-group .text-primary{
			color: #999999;
		}
		.bk-sign-form .form-group .text-primary-a{
			display: block;
			text-align: center;
			line-height: 48px;
			width: 180px;
			height: 48px;
			color: #FFFFFF;
			background: #3E85A2;
			border-radius: 5px;
			margin-right: 40px;
			font-size: 16px;
		}
		.bk-sign-form .form-group .text-primary-a:hover{
			color: #FFFFFF;
			text-decoration: none;
			background: #479EC2;
		}
		.bk-sign-main .logoutbox .text-h4{
			color: #d4dcf0;
			border: none
		}
		.bk-sign-main .logoutbox .text-h4 .iconfont{
			font-size: 26px;
			font-weight: 400;
			margin-right: 4px;
			line-height: 30px
		}
		.logoutbox_new_d{
			margin-bottom: 380px;
		}
	</style>
</head>

<body class="bg-login body-bg">
<div class="bk-body">
	<jsp:include page="/common/top.jsp" />
	<!--页面中部内容开始-->
	<div class="container container-bg">
		<div class="bk-sign-main container-bk-sign-main" id="logBox">
			<div class="logoutbox logoutbox_new logoutbox_new_d">
				<div class="bk-sign-tab">
					<c:choose>
						<c:when test="${type eq '0'}">
							<h2 class="text-h4 "><i class="bb-iconfont bb-icon-tanhao" style="color: #d4dcf0;font-weight: bold;padding-right: 15px;font-size: 26px;"></i>${L:l(lan,'激活链接已失效')}</h2>
						</c:when>
						<c:when test="${type eq '1'}">
							<h2 class="text-h4 "><i class="bb-iconfont bb-icon-tanhao" style="color: #d4dcf0;font-weight: bold;padding-right: 15px;font-size: 26px;"></i>${L:l(lan,'激活链接已过期')}</h2>
						</c:when>
						<c:otherwise>
							<h2 class="text-h4 "><i class=" iconfont icon-zhucewenanicon"></i>${L:l(lan,'激活成功')}</h2>
						</c:otherwise>
					</c:choose>
				</div>
				<div class="bk-sign-form">
					<c:choose>
						<c:when test="${type eq '0'}">
							<div class="form-group">
								<c:choose>
									<c:when test="${lan eq 'cn'}">
										<p>${tips}</p>
										<p class="text-p">激活链接已失效</p>
										<a  class="text-primary-a" href="${vip_domain }/bw/login" target="_self">立即跳转</a>
									</c:when>
									<c:when test="${lan eq 'hk'}">
										<p>${tips}</p>
										<p class="text-p" >激活鏈接已失效</p>
										<a class="text-primary-a" href="${vip_domain }/bw/login" target="_self">立即跳轉</a>
									</c:when>
									<c:when test="${lan eq 'jp'}">
										<p>${tips}</p>
										<p class="text-p" >リンクの有効化が無効になりました</p>
										<a class="text-primary-a" href="${vip_domain }/bw/login" target="_self">今すぐジャンプします</a>
									</c:when>
									<c:when test="${lan eq 'kr'}">
										<p>${tips}</p>
										<p class="text-p" >활성화 링크가 무호되었습니다</p>
										<a class="text-primary-a" href="${vip_domain }/bw/login" target="_self">당장 이동</a>
									</c:when>
									<c:otherwise>
										<p>${tips}</p>
										<p class="text-p">Invalid Activation link. </p>
										<a class="text-primary-a" href="${vip_domain }/bw/login" target="_self">Jump now</a>
									</c:otherwise>
								</c:choose>
							</div>
						</c:when>
						<c:when test="${type eq '1'}">
							<div class="form-group">
								<c:choose>
									<c:when test="${lan eq 'cn'}">
										<p>${tips}</p>
										<p class="text-p">激活链接已过期，请重新发送</p>
										<a  class="text-primary-a" href="${vip_domain }/bw/signup" target="_self">立即跳转</a>
									</c:when>
									<c:when test="${lan eq 'hk'}">
										<p>${tips}</p>
										<p class="text-p" >激活鏈接已過期，請重新發送</p>
										<a class="text-primary-a" href="${vip_domain }/bw/signup" target="_self">立即跳轉</a>
									</c:when>
									<c:when test="${lan eq 'jp'}">
										<p>${tips}</p>
										<p class="text-p" >アクティベーションリンクが期限切れになりました。再発送してください</p>
										<a class="text-primary-a" href="${vip_domain }/bw/signup" target="_self">今すぐジャンプします</a>
									</c:when>
									<c:when test="${lan eq 'hr'}">
										<p>${tips}</p>
										<p class="text-p" >활성화 링크가 기한이 지냈습니다. 다시 발송하세요</p>
										<a class="text-primary-a" href="${vip_domain }/bw/signup" target="_self">당장 이동</a>
									</c:when>
									<c:otherwise>
										<p>${tips}</p>
										<p class="text-p">Activation link has expired. Please resend it</p>
										<a class="text-primary-a" href="${vip_domain }/bw/signup" target="_self">Jump now</a>
									</c:otherwise>
								</c:choose>
							</div>
						</c:when>
						<c:otherwise>
							<div class="form-group">
								<c:if test="true">
									<c:choose>
										<c:when test="${lan eq 'cn'}">
											<p class="text-p">将在<span class="text-primary " id="autoJump">3</span>秒后跳转至用户中心。</p>
											<a class="text-primary-a" href="${vip_domain }/bw/mg/account" target="_self">立即跳转</a>
										</c:when>
										<c:when test="${lan eq 'hk'}">
											<p class="text-p">將在<span class="text-primary" id="autoJump">3</span>秒後跳轉至用戶中心。</p>
											<a class="text-primary-a" href="${vip_domain }/bw/mg/account" target="_self">立即跳轉</a>
										</c:when>
										<c:when test="${lan eq 'jp'}">
											<p class="text-p"><span class="text-primary" id="autoJump">3</span>秒後ユーザーセンターにジャンプします。</p>
											<a class="text-primary-a" href="${vip_domain }/bw/mg/account" target="_self">今すぐジャンプします</a>
										</c:when>
										<c:when test="${lan eq 'kr'}">
											<p class="text-p"><span class="text-primary" id="autoJump">3</span>초후에 고객선터로 이동하겠습니다.</p>
											<a class="text-primary-a" href="${vip_domain }/bw/mg/account" target="_self">당장 이동</a>
										</c:when>
										<c:otherwise>
											<p class="text-p">Jump to the account center in<span class="text-primary" id="autoJump">3</span> seconds. </p>
											<a class="text-primary-a" href="${vip_domain }/bw/mg/account" target="_self">Jump now</a>
										</c:otherwise>
									</c:choose>
								</c:if>
							</div>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="/common/foot.jsp" />
	<script type="text/javascript">
        $(function(){
            <c:if test="${isSuc }">
            var t = 3;
            var time = document.getElementById("autoJump");
            var inter = setInterval(function(){
                if(t >= 2){
                    t--;
				}else{
                    clearInterval(inter);
				}
                time.innerHTML = t;
                if (t <= 1) {

                    <c:choose>
                    <c:when test="${type eq '1' }">
                    window.top.location.href = DOMAIN_VIP + "/bw/mg/account";
                    </c:when>
                    <c:otherwise>
                    window.top.location.href = DOMAIN_VIP + "/bw/mg/account";
                    </c:otherwise>
                    </c:choose>
                };
            },1000);
            </c:if>

        });

        require(["module_market","module_asset","module_common"],function(market,asset){
            market.init();
            asset.init();
        });
	</script>
	<!--页面中部内容结束-->
</div>
</body>
</html>