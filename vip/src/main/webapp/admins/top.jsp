<%@page import="com.world.web.action.Action"%>
<%@page import="com.world.config.GlobalConfig"%>
<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String header = GlobalConfig.session;
	String vip_domain=Action.VIP_DOMAIN;
	String static_domain=Action.STATIC_DOMAIN;
	String main_domain=Action.MAIN_DOMAIN;
	String p2p_domain=Action.P2P_DOMAIN;
	String trans_domain=Action.TRANS_DOMAIN;
 %>
<script type="text/javascript">
var JsCommon={uon:"<%=header%>uon",uname:"<%=header%>uname",uid:"<%=header%>uid",aid:"<%=header%>aid",rid:"<%=header%>rid",aname:"<%=header%>aname",note:"<%=header%>note",lan:"<%=header%>lan",mainDomain:"<%=main_domain%>",vipDomain:"<%=vip_domain%>",p2pDomain:"<%=p2p_domain%>",transDomain:"<%=trans_domain%>",staticDomain:"<%=static_domain%>"};
</script>
<link href="${static_domain }/statics/css/admin/global.css" rel="stylesheet" type="text/css"/>
<link href="${static_domain }/statics/css/admin/control.css" rel="stylesheet" type="text/css" /> 
<script type="text/javascript" src="${static_domain }/statics/js/admin/jquery.js"></script>
<script type="text/javascript" src="${static_domain }/statics/js/admin/global.js"></script>
<script type="text/javascript" src="${static_domain }/statics/js/admin/admin.js"></script>
