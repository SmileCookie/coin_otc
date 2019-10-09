<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 
<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${L:l1(lan,"%%提币记录",coint.propTag)}-${WEB_NAME }-${WEB_TITLE }</title>
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<link rel="stylesheet" href="${static_domain }/statics/css/web.trans.css?V${CH_VERSON }">
<script type="text/javascript">
$(function() {
	vip.list.ui();
	vip.list.basePath = "/manage/account/downrecord/";
	vip.list.ajaxPage({url : vip.list.basePath+"ajax/${coint.stag}", suc : function(){}});
});

function reload2() {
	Close();
	vip.list.reload();
}

function cancelOut(did) {
	JuaBox.info("${L:l(lan,'确定要取消吗？')}", {
		btnFun1:function(JuaId){
			window.top.JuaBox.close(JuaId,function(){
				confirmCancel(did);
			});
		}
	});
}


function confirmCancel(did){
	var actionUrl = vip.vipDomain + "/manage/account/downrecord/confirmCancel?did="+did+"&coint=${coint.tag}";
	vip.ajax({
		url : actionUrl , 
		dataType : "xml",
		suc : function(xml){
			location.reload();
		},
		err : function(xml){
			BwModal.alert($(xml).find("Des").text(), {width:300});
		}
	});
}

</script>
</head>
<body>

<div class="bk-body">
	<!-- Common TopMenu Begin -->
	<jsp:include page="/common/top.jsp" />
	<!-- Common TopMenu End -->
	<!-- Body From mainPage Begin -->
	<div class="zh-trade">
		<div class="container">
			<div class="jili-top clearfix">
				<p>${L:l(lan,'选择币种？')}：
					<select id="setAssetSel"></select>
				</p>
				<h2>${coint.propTag }${L:l(lan,'提币记录')}</h2>
			</div>
			<div class="lsju" id="shopslist">
			
			</div>
		</div>	
	</div>
	<!-- Body From mainPage End -->
	<!-- Common FootMain Begin -->
	<jsp:include page="/common/foot.jsp" />
	<!-- Common FootMain End -->
</div>
</body>
</html>

