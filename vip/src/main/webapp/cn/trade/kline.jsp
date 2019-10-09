<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ page import="com.world.web.action.Action"%>
<%@ page import="com.world.config.GlobalConfig"%>
<%! 
  private static long CH_VERSON = 0; 
%>
<%
	String header = GlobalConfig.session;
	String vip_domain=Action.VIP_DOMAIN;
	String static_domain=Action.STATIC_DOMAIN;
	String main_domain=Action.MAIN_DOMAIN;
	String p2p_domain=Action.P2P_DOMAIN;
	String trans_domain=Action.TRANS_DOMAIN;
	if(CH_VERSON==0){
		CH_VERSON = new Date().getTime(); 
	}
	request.setAttribute("CH_VERSON", CH_VERSON);
 %>
<!DOCTYPE html>
<html>
<head>
<script type="text/javascript">
var GLOBAL             = {},
    VERSION            = GLOBAL['VERSION']           = '${CH_VERSON }',
    ZNAME              = GLOBAL['ZNAME']              = '<%=header%>',
    DOMAIN_BASE        = GLOBAL['DOMAIN_BASE']       = '${baseDomain }',
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

    if(getParentUrl().indexOf('/bw/')==-1){
        document.domain = '${baseDomain}';
    }
    
    function getParentUrl() { 
        var url = null; 
        if (parent !== window) { 
            try {
                url = parent.location.href; 
            } catch (e) { 
                url = document.referrer; 
            } 
        }
        return url; 
    }
</script>
<meta charset="utf-8">
<title>${L:l(lan,'行情图表')}-${WEB_NAME }-${WEB_TITLE }</title>
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
<link href="${static_domain}/statics/css/site.compiled.css?V${CH_VERSON}" rel="stylesheet" type="text/css">
<link rel="stylesheet" type="text/css" href="${static_domain}/statics/css/customization.css?V${CH_VERSON}">
<style>
.loading-indicator {
    width: 100%;
    height: 100%;
    position: absolute;
    z-index: 150;
    background: none;
}
</style>
</head>

<body class="chart-page on-widget transparentChart">
  <div class="loading-indicator" id="loading-indicator"></div>
    <script src="${static_domain}/statics/js/spin.min.js?V${CH_VERSON}"></script>
    <script>
        var loadingSpinner = new Spinner({
            lines: 17,
            length: 0,
            width: 3,
            radius: 30,
            scale: 1,
            corners: 1,
            color: "#00A2E2",
            opacity: 0.3,
            rotate: 0,
            direction: 1,
            speed: 1.5,
            trail: 60,
            fps: 20,
            zIndex: 2000000000,
            className: "spinner",
            top: "50%",
            left: "50%",
            shadow: false,
            hwaccel: false
        }).spin(document.getElementById("loading-indicator"));
    </script>
  <script type="text/javascript" src="${static_domain}/statics/js/tv-chart.js?V${CH_VERSON}"></script>

</body>
</html>