<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>

<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${WEB_NAME }-${WEB_TITLE }</title>
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
</head>
<body class="room minWidth">
<jsp:include page="/common/top.jsp" />
<section class="news_mainer">
    <section class="news_container">
        <div class="terms_main clearfix">
            <h2>联系我们</h2>
            <h5></h5>
            <h3 class="mb10">一般支持：</h3>
            <p class="mb40">
                您在btcwinex遇到任何疑问或异常，都可以通过以下邮箱联系我们。<br/>
                <span>support@btcwinex.com</span>
            </p>
            <h3 class="mb10">意见建议：</h3>
            <p>
                您对btcwinex有任何意见或者建议，都可以通过以下邮箱联系我们。<br/>
                <span>feedback@btcwinex.com</span>
            </p>
        </div>
    </section>
</section>
<jsp:include page="/common/foot.jsp" />
</body>
</html>
