<%@ page import="com.world.web.action.Action"%>
<%@ page import="com.world.config.GlobalConfig"%>
<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page import="com.Lan" %>
<%@ page import="com.world.util.SEO" %>
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

	String lan = (String) request.getAttribute("lan");
	request.setAttribute("CH_VERSON", CH_VERSON);
	request.setAttribute("WEB_NAME", Lan.Language(lan, "btcwinex"));
	request.setAttribute("WEB_LOGO", static_domain + "/statics/img/common/vip_logo.png");
	request.setAttribute("WEB_TITLE", Lan.Language(lan,"BTC/LTC/ETH trading platform,your virtual currency exchange center"));
	request.setAttribute("WEB_KEYWORD", Lan.Language(lan,"比特全球关键词"));
	request.setAttribute("WEB_DESC", Lan.Language(lan,"比特全球官网"));
	String TITLE = Lan.Language(lan,SEO.TITLE);
	String KEYWORDS = Lan.Language(lan,SEO.KEYWORDS);
	String DESCRIPTION = Lan.Language(lan,SEO.DESCRIPTION);
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
    LOGIN_STATUS       = GLOBAL['LOGIN_STATUS']      = '<%=header%>loginStatus',
    AID                = GLOBAL['AID']               = '<%=header%>aid',
    RID                = GLOBAL['RID']               = '<%=header%>rid',
    VIP                = GLOBAL['VIP']               = '<%=header%>vip',
    UNAME              = GLOBAL['UNAME']             = '<%=header%>uname',
    ANAME              = GLOBAL['ANAME']             = '<%=header%>aname',
    NOTE               = GLOBAL['NOTE']              = '<%=header%>note';

</script>
<%--<title><%=TITLE%></title>--%>
<%--<meta name="keywords" content="<%=KEYWORDS%>">--%>
<%--<meta name=”description” content=”<%=DESCRIPTION%>”>--%>
<meta charset="utf-8">
<meta name="flexible" content="initial-dpr=1" />
<meta name="renderer" content="webkit">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta http-equiv="Cache-Control" content="no-siteapp">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta content="yes" name="apple-touch-fullscreen">
<meta content="telephone=no" name="format-detection">
<meta content="black" name="apple-mobile-web-app-status-bar-style">
<meta name="author" content="${baseDomain }">
<meta name="copyright"  content="Copyright &copy;${baseDomain } copyright">
<meta name="revisit-after"  content="1 days">
<link rel="shortcut icon" href="${static_domain }/statics/img/common/title-pic_new.ico">
<link rel="stylesheet" href="${static_domain }/statics/css/web.base.css">
<link rel="stylesheet" href="${static_domain }/statics/css/web.common.css">
<script src="${static_domain }/statics/js/big.js" charset="UTF-8"></script>
<script src="${static_domain }/statics/js/pack.tmpl.js" charset="UTF-8"></script>
<script src="${static_domain }/statics/js/web.base.js" charset="UTF-8"></script>
<script src="${static_domain }/statics/js/web.lang.js" charset="UTF-8"></script>
<script src="${static_domain}/statics/js/web.mCustomScrollbar.js"></script>
<script src="${static_domain }/statics/js/common/webcommon.js" charset="UTF-8"></script>
<script src="${static_domain }/statics/js/require.js" charset="UTF-8"></script>

<script src="${static_domain }/statics/js/flexible.js" charset="UTF-8"></script>
<style>
    @charset "utf-8";
    html{-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%}
    html *{outline:0;-webkit-text-size-adjust:none;-webkit-tap-highlight-color:rgba(0,0,0,0)}
</style>

<script>
    var ajaxRun = true;
    requirejs.config({
        baseUrl: DOMAIN_STATIC + "/statics/js",
        paths: {
            module_base: "web.base",
            module_method: "web.method",
            module_lang: "web.lang",
            module_common: "web.common",
            module_user: "web.user",
            module_asset: "web.asset",
            module_trans: "web.trans",
            module_market: "web.market",
            module_simChart: "web.simchart",
            module_tmpl: "pack.tmpl",
            module_range: "pack.range",
            module_pako: "pack.pako",
            module_encrypt: "pack.encrypt",
            module_highCharts: "pack.highcharts",
            module_highStock: "pack.highstock",
            module_vote: "web.vote",
            module_authType:"web.auth_type",
            module_draw: "web.draw",
            module_wheeler:"web.wheeler",
            module_vconsole:"vconsole.min",
            module_swiper:"web.swiper",
            // module_echarts:"web.echarts",
            module_mcustom:'web.mCustomScrollbar',
        },
        // urlArgs: "V" + VERSION
    });
    // seajs.config({
    //     alias: {
    //         module_base: "dist/web.base",
    //         module_method: "dist/web.method",
    //         module_lang: "dist/web.lang",
    //         module_common: "dist/web.common",
    //         module_user: "dist/web.user",
    //         module_asset: "dist/web.asset",
    //         module_trans: "dist/web.trans",
    //         module_market: "dist/web.market",
    //         module_simChart: "dist/web.simchart",
    //         module_tmpl: "dist/pack.tmpl",
    //         module_range: "dist/pack.range",
    //         module_pako: "dist/pack.pako",
    //         module_encrypt: "dist/pack.encrypt",
    //         module_highCharts: "dist/pack.highcharts",
    //         module_highStock: "dist/pack.highstock",
    //         module_vote: "dist/web.vote",
    //         module_authType:"dist/web.auth_type",
    //         module_draw: "dist/web.draw",
    //         module_wheeler:"dist/web.wheeler",
    //     },
    //     paths: {
    //         dev: "statics/js",
    //         dist: "statics/js"
    //     },
    //     debug: false,
    //     base: DOMAIN_STATIC,
    //     map: [
    //         [/^(.*\/statics\/.*\.(?:css|js))(?:.*)$/i, "$1?" + VERSION]
    //     ],
    //     charset: "utf-8"
    // });
</script>
<script src="${static_domain }/statics/js/common/iconfont.js" charset="UTF-8"></script>
<!--[if lt IE 9]>
  <script src="${static_domain }/statics/js/html5shiv.min.js" charset="UTF-8"></script>
  <script src="${static_domain }/statics/js/respond.min.js" charset="UTF-8"></script>
<![endif]-->
