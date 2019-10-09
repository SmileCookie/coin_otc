<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${n.title}-${WEB_NAME }-${WEB_TITLE }</title>
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<link rel="stylesheet" href="${static_domain }/statics/css/web.news.css?V${CH_VERSON }">
<!-- 新闻资讯页面自适应处理开始 -->
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1, user-scalable=no">
<style>
@media (max-width: 768px) {
	.bk-footer-hd { display:none !important;}
	.bk-nav .navbar-header { padding-bottom:10px;}
	.bk-nav.navbar { margin-bottom:0px;}
	.b_contain { padding:0px 0px; background-color:transparent; border:none;}
	
	.b_contain .hd, .b_contain .bd { padding:0;}
	.b_contain .hd h2 { line-height:1.4; font-size:24px; padding-bottom:10px;}
}
</style>
<!-- 新闻资讯页面自适应处理结束 -->
</head>
<body>
<jsp:include page="/common/top.jsp" />
<!--页面中部内容开始-->
<div class="container">
     <div class="b_contain">
       <div class="hd">
       <h2>${n.title}</h2>
       <div class="tt"><b>${L:l(lan,'发表于')}：</b><i><fmt:formatDate value="${n.pubTime}" pattern="yyyy/MM/dd HH:mm" /> </i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
       		<b>${L:l(lan,'关键字')}：</b>${n.keyword }</div>
       </div>
       <div class="bd">
       <p align="center"></p>
       <p align="left">${n.content}</p>
       </div>
     </div>
</div>
<!--页面中部内容结束-->

<jsp:include page="/common/foot.jsp" />
</body>
</html>
