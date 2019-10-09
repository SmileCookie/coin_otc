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
            <h2>btcwinex融资融币业务风险声明</h2>
            <h5></h5>
            <h3>1. 互联网信息传输风险</h3>
            <p>
                通过互联网或其他可公开访问的网络传输数据或信息（包括通过电子邮件进行的通信）不是百分百安全的，并且在运输过程中可能会丢失，拦截或更改。因此，btcwinex不对您可能遇到的任何损害或因互联网或其他可公开访问的网络进行任何传输而导致的费用承担任何责任，包括但不限于涉及平台或电子邮件的传输。
            </p>
            <h3>2. 借款风险</h3>
            <p>
                btcwinex的融资融币交易是高风险交易。市场高流动性和流动性不足的巨大风险意味着系统不一定能够清算您的持仓，您可能会损失资金。如果您的账户中的资产价值低于维护保证金要求，或者btcwinex认为您的账户似乎有违约贷款的风险，btcwinex可能会清算您的持仓，清算您账户中的任何余额的资产，以偿还投资人的债务。如果您的持仓和资产清算后，您的账户仍然没有足够的资金来偿还投资人的债务，您将承担任何额外的资金用于偿还债务。有意拖欠贷款可能导致btcwinex向您提起法律诉讼。
            </p>
            <h3>3. 投资风险</h3>
            <p class="mb40">
                虽然btcwinex采取了预防措施来防止用户违约贷款，但市场流动性高的波动性和巨大风险意味着btcwinex不能对违约提供任何保证。当您使用用平台的借贷系统向其他用户提供借款时，如果借款人违约贷款，借款人账户的清算未能提供足够的资金来偿付其债务，则可能会造成无法偿还您所有出借资金的损失。
            </p>
            <h3>所有的融资融币交易，借款或投资都由用户自己进行，btcwinex对您使用btcwinex提供的任何服务或您未能了解所涉及的风险产生的任何损失或损害不承担任何责任。</h3>
        </div>
    </section>
</section>
<jsp:include page="/common/foot.jsp" />
</body>
</html>
