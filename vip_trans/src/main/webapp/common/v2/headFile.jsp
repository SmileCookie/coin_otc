<%@page import="com.world.web.action.Action"%>
<%@page import="com.world.config.GlobalConfig"%>
<%@page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%
	String header = GlobalConfig.session;
	String vip_domain=Action.VIP_DOMAIN;
	String static_domain=Action.STATIC_DOMAIN;
	String main_domain=Action.MAIN_DOMAIN;
	String p2p_domain=Action.P2P_DOMAIN;
	String trans_domain=Action.TRANS_DOMAIN;
	
 %>
<script type="text/javascript">
var GLOBAL             = {},
    VERSION            = GLOBAL['VERSION']           = '${CH_VERSON }',
    DOMAIN_BASE        = GLOBAL['DOMAIN_BASE']       = '${baseDomain }',
    <%--DOMAIN_MAIN        = GLOBAL['DOMAIN_MAIN']       = '<%=main_domain%>',--%>
    <%--DOMAIN_VIP         = GLOBAL['DOMAIN_VIP']        = '<%=vip_domain%>',--%>
	DOMAIN_MAIN        = GLOBAL['DOMAIN_MAIN']       = '${main_domain}',
	DOMAIN_VIP         = GLOBAL['DOMAIN_VIP']        = '${vip_domain}',
    DOMAIN_STATIC      = GLOBAL['DOMAIN_STATIC']     = '<%=static_domain%>',
    DOMAIN_P2P         = GLOBAL['DOMAIN_P2P']        = '<%=p2p_domain%>',
    DOMAIN_TRANS       = GLOBAL['DOMAIN_TRANS']      = '<%=trans_domain%>',
    LANG               = GLOBAL['LANG']              = '${lan }',
    UON                = GLOBAL['UON']               = '<%=header%>uon',
    UID                = GLOBAL['UID']               = '<%=header%>uid',
    AID                = GLOBAL['AID']               = '<%=header%>aid',
    RID                = GLOBAL['RID']               = '<%=header%>rid',
    VIP                = GLOBAL['VIP']               = '<%=header%>vip',
    UNAME              = GLOBAL['UNAME']             = '<%=header%>uname',
    ANAME              = GLOBAL['ANAME']             = '<%=header%>aname',
    NOTE               = GLOBAL['NOTE']              = '<%=header%>note';

</script>
<script type="text/javascript">
;(function(){
		try {
		    var oldDomain = document.domain;
		    document.domain = oldDomain.substring(oldDomain.indexOf(DOMAIN_BASE), oldDomain.length);
		} catch (e) {
		    document.domain = DOMAIN_BASE;
		}
})();
</script>

<meta charset="utf-8">
<meta name="renderer" content="webkit">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta http-equiv="Cache-Control" content="no-siteapp">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta content="yes" name="apple-touch-fullscreen">
<meta content="telephone=no" name="format-detection">
<meta content="black" name="apple-mobile-web-app-status-bar-style">
<meta name="author" content="vip.COM">
<meta name="copyright"  content="Copyright &copy;vip.COM 版权所有">
<meta name="revisit-after"  content="1 days">
<%--<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1, user-scalable=no">--%>
<link rel="shortcut icon" href="${static_domain }/statics/img/v2/favicon/favicon.ico?v${CH_VERSON }">
<link rel="apple-touch-icon" sizes="57x57" href="${static_domain }/statics/img/v2/favicon/apple-touch-icon-114.png?v${CH_VERSON }">
<link rel="apple-touch-icon" sizes="114x114" href="${static_domain }/statics/img/v2/favicon/apple-touch-icon-114.png?v${CH_VERSON }">
<link rel="apple-touch-icon" sizes="72x72" href="${static_domain }/statics/img/v2/favicon/apple-touch-icon-144.png?v${CH_VERSON }">
<link rel="apple-touch-icon" sizes="144x144" href="${static_domain }/statics/img/v2/favicon/apple-touch-icon-144.png?v${CH_VERSON }">
<link rel="stylesheet" href="${static_domain }/statics/css/v2/module.base.css?v${CH_VERSON }">
<link rel="stylesheet" href="${static_domain }/statics/css/v2/module.common.css?v${CH_VERSON }">
<link rel="stylesheet" href="//at.alicdn.com/t/font_1447746205_6731396.css?v${CH_VERSON }">
<script src="${static_domain }/statics/js/v2/module.base.js?v${CH_VERSON }" charset="UTF-8"></script>
<script src="${static_domain }/statics/js/v2/module.lang.js?v${CH_VERSON }" charset="UTF-8"></script>
<script src="${static_domain }/statics/js/v2/sea.js?v${CH_VERSON }" charset="UTF-8"></script>
<script src="${static_domain }/statics/js/v2/sea.config.js?v${CH_VERSON }" charset="UTF-8"></script>
<!--[if lt IE 9]>
  <script src="${static_domain }/statics/js/v2/html5shiv.min.js?v${CH_VERSON }"></script>
  <script src="${static_domain }/statics/js/v2/respond.min.js?v${CH_VERSON }"></script>
<![endif]-->



