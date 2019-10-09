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
<title>${L:l(lan,'消息设置')}-${WEB_NAME }-${WEB_TITLE }</title>
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<link rel="stylesheet" href="${static_domain }/statics/css/web.user.css?V${CH_VERSON }">
</head>
<body class="">

<div class="bk-body">
	<!-- Common TopMenu Begin -->
	<jsp:include page="/common/top.jsp" />
	<!-- Common TopMenu End -->
	<!-- Body From mainPage Begin -->
	<div class="mainer">
		<div class="container">
			<jsp:include page="/common/trend.jsp" />
			<div class="cont-row">
			<div class="user-panel">
			<jsp:include page="/cn/manage/auth/menu.jsp"/>
			<div class="content">
				<h2>${L:l(lan,'消息提醒')}</h2>
				  <ul id="tipList" class="user-info-list mt30">
                        <li>
                            <p class="p1">${L:l(lan, "网页登录APP提醒")}</p>
                            <c:choose>
                                <c:when test="${curUser.webLoginAppNotice}">
                                    <!--<p class="p2"><span class="text-second">${L:l(lan, "已开启")}</span></p>-->
                                    <p class="p3 mr20">
                                    	   <em class="eu-off">OFF</em>
                                    	   <a id="appLoginTip" class="switch on" data-type="isWebLoginAppNotice" data-status="1" role="button"><i></i></a>
                                       <em class="eu-on">ON</em>
                                    </p>
                                </c:when>
                                <c:otherwise>
                                    <!--<p class="p2"><span class="text-third">${L:l(lan, "已关闭")}</span></p>-->
                                    <p class="p3 mr20">
                                     	<em class="eu-off">OFF</em>
                                     	<a id="appLoginTip" class="switch" data-type="isWebLoginAppNotice" data-status="0" role="button"><i></i></a>
                                        <em class="eu-on">ON</em>
                                    </p>
                                </c:otherwise>
                            </c:choose>
                        </li>

                       <%-- <li>
                            <p class="p1">${L:l(lan, "人民币充值/提现短信提醒")}</p>
                            <c:choose>
                                <c:when test="${curUser.receiveDealSms}">
                                    <p><span class="text-second">${L:l(lan, "已开启")}</span></p>
                                    <p class="p3"><a id="inOutTip" class="switch on" data-type="receiveDealSms" data-status="1" role="button"><i></i></a></p>
                                </c:when>
                                <c:otherwise>
                                    <p><span class="text-third">${L:l(lan, "已关闭")}</span></p>
                                    <p class="p3"><a id="inOutTip" class="switch" data-type="receiveDealSms" data-status="0" role="button"><i></i></a></p>
                                </c:otherwise>
                            </c:choose>
                        </li> --%>
                        
                        <%--<li>
                            <p class="p1">${L:l(lan, "充值/提现邮件提醒")}</b></div>
                            <c:choose>
                                <c:when test="${curUser.receiveDealEmail}">
                                    <p><span class="text-second">${L:l(lan, "已开启")}</span></p>
                                    <p class="p3"><a id="inOutEmailTip" class="switch on" data-type="receiveDealEmail" data-status="1" role="button"><i></i></a></p>
                                </c:when>
                                <c:otherwise>
                                    <p><span class="text-third">${L:l(lan, "已关闭")}</span></p>
                                    <p class="p3"><a id="inOutEmailTip" class="switch" data-type="receiveDealEmail" data-status="0" role="button"><i></i></a></p>
                                </c:otherwise>
                            </c:choose>
                        </li>--%>
                        <li>
                            <p class="p1">${L:l(lan, "充值/提现APP推送提醒")}</p>
                            <c:choose>
                                <c:when test="${curUser.receiveDealPush}">
                                    <!--<p class="p2"><span class="text-second">${L:l(lan, "已开启")}</span></p>-->
                                    <p class="p3 mr20">
                                      	<em class="eu-off">OFF</em>
                                      	<a id="inOutPushTip" class="switch on" data-type="receiveDealPush" data-status="1" role="button"><i></i></a>
                                        <em class="eu-on">ON</em>
                                    </p>
                                </c:when>
                                <c:otherwise>
                                    <!--<p class="p2"><span class="text-third">${L:l(lan, "已关闭")}</span></p>-->
                                    <p class="p3 mr20">
                                     	<em class="eu-off">OFF</em>
                                      	<a id="inOutPushTip" class="switch" data-type="receiveDealPush" data-status="0" role="button"><i></i></a>
                                        <em class="eu-on">ON</em>
                                    </p>
                                </c:otherwise>
                            </c:choose>
                        </li>
                    </ul>
			    </div>
			  </div>
		   </div>
		</div>
	</div>
		
<script type="text/javascript">

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

</body>
</html>
