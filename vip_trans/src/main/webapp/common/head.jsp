<%@ page import="com.world.web.action.Action"%>
<%@ page import="com.world.config.GlobalConfig"%>
<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
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
//	if(CH_VERSON==0){
		CH_VERSON = new Date().getTime(); 
//	}
	request.setAttribute("CH_VERSON", CH_VERSON);
	request.setAttribute("WEB_NAME", "比特全球");
	request.setAttribute("WEB_LOGO", static_domain + "/statics/img/common/vip_logo.png");
	request.setAttribute("WEB_TITLE", "BTC/ETH/ETC交易平台，您身边的虚拟货币兑换中心");
	request.setAttribute("WEB_KEYWORD", "比特全球,比特币,比特币行情,以太币,以太币行情,以太币交易平台,以太币兑换,比特币交易平台,比特币兑换,比特币手机客户端,比特币微盘,买比特币,卖比特币,比特币,手机客户端,手机APP,bitcoin,btc,以太币价格,以太币,以太坊,以太坊价格,以太币交易 ,以太坊交易");
	request.setAttribute("WEB_DESC", "比特全球官网");
 %>
<script type="text/javascript">
    document.domain = '${baseDomain}';
</script>

<script type="text/javascript">
var GLOBAL             = {},
    VERSION            = GLOBAL['VERSION']           = '${CH_VERSON }',
    ZNAME              = GLOBAL['ZNAME']              = '<%=header%>',
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
<meta charset="utf-8">
<meta name="renderer" content="webkit">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta http-equiv="Cache-Control" content="no-siteapp">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta content="yes" name="apple-touch-fullscreen">
<meta content="telephone=no" name="format-detection">
<meta content="black" name="apple-mobile-web-app-status-bar-style">
<meta name="author" content="${baseDomain }">
<meta name="copyright"  content="Copyright &copy;${baseDomain } 版权所有">
<meta name="revisit-after"  content="1 days">
<%--<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1, user-scalable=no">--%>
<link rel="shortcut icon" href="${static_domain }/statics/img/favicon/favicon_red.ico">
<link rel="stylesheet" href="${static_domain }/statics/css/web.base.css">
<link rel="stylesheet" href="${static_domain }/statics/css/web.common.css">
<script src="${static_domain }/statics/js/web.base.js" charset="UTF-8"></script>
<script src="${static_domain }/statics/js/web.lang.js" charset="UTF-8"></script>
<script src="${static_domain }/statics/js/common/webcommon.js" charset="UTF-8"></script>
<script src="${static_domain }/statics/js/sea.js" charset="UTF-8"></script>
<script src="${static_domain }/statics/js/sea.config.js" charset="UTF-8"></script>
<!--[if lt IE 9]>
  <script src="${static_domain }/statics/js/html5shiv.min.js" charset="UTF-8"></script>
  <script src="${static_domain }/statics/js/respond.min.js" charset="UTF-8"></script>
<![endif]-->
<script type="text/javascript">
if(JuaBox.isMobile()){
	document.writeln("<style type=\"text/css\">");
	document.writeln(".container, .bk-nav.navbar, .bk-footer, .bk-body, .bg-login, .Jua-showTip {min-width: 980px !important;}  ");
	document.writeln("</style>");
}
</script>
