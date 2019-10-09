<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ page session="false"%>
<meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0;" name="viewport" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<meta content="yes" name="apple-moble-web-app-capable"/>
<meta name="viewport" content="target-densitydpi=device-dpi, width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0"/>
<!DOCTYPE >
<html>
	<head>
		<title></title>
		<meta name="viewport" content="width=device-width,height=device-height,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
		<link rel="stylesheet" id="css" href="${static_domain }/statics/css/icocss/m.css">
		<script type="text/javascript" src="${static_domain }/statics/js/admin/jquery.js"></script>
	</head>

<body>
<div class="con">
	<div class="c_top">
		<h2>${curData.title }</h2>
		<p>${curData.pubTimeStr }</p>
	</div>
	<div class="c_bot">
		${curData.content }
	</div>
</div>
<script>
    $(function () {
        $("table").attr("border","1")
    })
</script>
</body>
</html>

