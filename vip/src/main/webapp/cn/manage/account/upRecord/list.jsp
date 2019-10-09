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
<title>${L:l(lan,'充值记录')}-${WEB_NAME }-${WEB_TITLE }</title>
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<link rel="stylesheet" href="${static_domain }/statics/css/web.trans.css?V${CH_VERSON }">
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
				<p>${L:l(lan,"选择币种")}：
					<select id="setAssetSel"></select>
				</p>
				<h2>${coint.propTag }${L:l(lan,"充值记录")}</h2>
			</div>
			<div class="lsju">
				<table class="table table-striped table-bordered table-hover text-center">
				  <thead>
				    <tr>
				      <th>${L:l(lan,"时间")}</th>
				      <th>${L:l(lan,"类型")}</th>
				      <th>${L:l(lan,"充值金额")}(${coint.propTag })</th>
				      <th>${L:l(lan,"处理时间")}</th>
				      <th>${L:l(lan,"确认次数")}</th>
				      <th style="text-align:center">${L:l(lan,"状态")}</th>
				    </tr>
				  </thead>
				  <tbody id="chargeRecordDetail">
				  </tbody>
				</table>
				<div class="bk-pageNav mb20" id="chargeRecordDetail_Page"></div>
			</div>
			
		</div>	
		
	</div>


<script type="text/x-tmpl" id="tmpl-chargeRecordDetail">
      {% for (var i = 0; i <= rs.length -1; i++) { %}
					<tr>
						<td>{%=rs[i].submitTime%}</td>
						<td>{%=rs[i].coinName%}</td>
						<td>{%=rs[i].amount%} </td>
						<td></td>
						<td></td>
						<td>{%=rs[i].status%} </td>
					</tr>
       {% } %}
</script>
      
<script type="text/javascript">
			require(['module_asset'],function(asset){
				asset.chargeRecordInit("${coint.stag }");
			});
</script>
      
	<!-- Body From mainPage End -->
	<!-- Common FootMain Begin -->
	<jsp:include page="/common/foot.jsp" />
	<!-- Common FootMain End -->
</div>

</body>
</html>
