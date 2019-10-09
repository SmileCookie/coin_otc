<%@ page session="false" language="java" import="java.util.*"
	pageEncoding="utf-8"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!doctype html>
<html>
<head>
<title>${L:l(lan,"领取以太币")}-${WEB_NAME }-${WEB_TITLE }</title>

<jsp:include page="/common/head.jsp" />
<link href="${static_domain }/statics/css/v2/module.log.css?V${CH_VERSON }" rel="stylesheet" type="text/css" />
<style>
.apriltwo { width:100%; }
.apriltwo.div01 { background:url(${static_domain }/statics/img/activity/apriltwo/downluck01.jpg) top center no-repeat; height:418px; margin-top:-2px; }
.apriltwo.div02 { background:url(${static_domain }/statics/img/activity/apriltwo/downluck02.jpg) top center no-repeat; height:367px; }
.apriltwo.div03 { background:url(${static_domain }/statics/img/activity/apriltwo/downluck03.jpg) top center no-repeat; height:688px; }
.apriltwo.div04 { background:url(${static_domain }/statics/img/activity/apriltwo/downluck04.jpg) top center no-repeat; height:401px; }
.apriltwo.div05 { background:url(${static_domain }/statics/img/activity/apriltwo/downluck05.jpg) top center no-repeat; height:538px; }
.apriltwo.div06 { background:url(${static_domain }/statics/img/activity/apriltwo/downluck06.jpg) top center no-repeat; height:416px; }
.apriltwo.div07 { background:url(${static_domain }/statics/img/activity/apriltwo/downluck07.jpg) top center no-repeat; height:567px; }
.apriltwo.div08 { background:url(${static_domain }/statics/img/activity/apriltwo/downluck08.jpg) top center no-repeat; height:531px; }
.gobackhome { width:900px; margin:0px auto; position:relative; display:none;  }
.gobackhome a { width:148px; height:93px; position:absolute;   background:url(${static_domain }/statics/img/activity/apriltwo/gobackhome.png) top center no-repeat; display:block; cursor:pointer; top:100px; right:150px;  }
/* .apriltwo.div04 .apcontain { position:relative; width:940px; margin:0px auto; } */
/* .apriltwo.div04 .apcontain a.abtn { width:332px; height:60px; display:block; position:absolute; font-size:25px; font-weight:bold; text-align:center; color:#cb2e1b; font-family: "微软雅黑"; background:#FFF; border-radius:10px; top:90px; left:10px; line-height:60px; cursor:pointer;  } */
/* .apriltwo.div04 .apcontain a.abtn:hover { color:#af2110; } */
/* .apriltwo.div04 .apcontain span {  width:332px; height:60px; display:block; position:absolute; font-size:18px; font-weight:bold; text-align:center; color:#cb2e1b; font-family: "微软雅黑"; background:#FFF; border-radius:10px; top:90px; left:10px; line-height:60px; } */
.navbar { margin-bottom:0px;}
</style>
</head>
<body>

<jsp:include page="/common/top.jsp" />
<div class="apriltwo div01"><div class="gobackhome"><a href="${main_domain}/"></a></div></div>
<div class="apriltwo div02"></div>
<div class="apriltwo div03"></div>
<div class="apriltwo div04"></div>
<div class="apriltwo div05"></div>
<div class="apriltwo div06"></div>
<div class="apriltwo div07"></div>
<div class="apriltwo div08"></div>



<%-- 	<c:if test="${canReceive}"> --%>
<!-- 		<input type="button" id="receiveBtn" onclick="reward()" value="领取" /> -->
<%-- 	</c:if> --%>
<%-- 	<c:if test="${!canReceive}"> --%>
<%-- 		${msg} --%>
<%-- 	</c:if> --%>
<script type="text/javascript">
function reward() {
	$.ajax({
		type : 'POST',
		url : '/u/doEthReward',
		dataType : 'json',
		success : function(json) {
			alert(json.des);
			window.location.reload(true);
		},
		error : function() {
			alert('网络访问出错，请稍后重试');
		}
	});
}
</script>
</body>

</html>
