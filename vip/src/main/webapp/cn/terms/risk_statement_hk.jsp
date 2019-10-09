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
            <h2>btcwinex融資融幣業務風險聲明</h2>
            <h5></h5>
            <h3>1. 互聯網信息傳輸風險</h3>
            <p>
                通過互聯網或其他可公開訪問的網絡傳輸數據或信息（包括通過電子郵件進行的通信）不是百分百安全的，並且在運輸過程中可能會丟失，攔截或更改。因此，btcwinex不對您可能遇到的任何損害或因互聯網或其他可公開訪問的網絡進行任何傳輸而導致的費用承擔任何責任，包括但不限於涉及平臺或電子郵件的傳輸。
            </p>
            <h3>2. 借款風險</h3>
            <p>
                btcwinex的融資融幣交易是高風險交易。市場高流動性和流動性不足的巨大風險意味著系統不壹定能夠清算您的持倉，您可能會損失資金。如果您的賬戶中的資產價值低於維護保證金要求，或者btcwinex認為您的賬戶似乎有違約貸款的風險，btcwinex可能會清算您的持倉，清算您賬戶中的任何余額的資產，以償還投資人的債務。如果您的持倉和資產清算後，您的賬戶仍然沒有足夠的資金來償還投資人的債務，您將承擔任何額外的資金用於償還債務。有意拖欠貸款可能導致btcwinex向您提起法律訴訟。
            </p>
            <h3>3. 投資風險</h3>
            <p class="mb40">
                 雖然btcwinex采取了預防措施來防止用戶違約貸款，但市場流動性高的波動性和巨大風險意味著btcwinex不能對違約提供任何保證。當您使用用平臺的借貸系統向其他用戶提供借款時，如果借款人違約貸款，借款人賬戶的清算未能提供足夠的資金來償付其債務，則可能會造成無法償還您所有出借資金的損失。
            </p>
            <h3>所有的融資融幣交易，借款或投資都由用戶自己進行，btcwinex對您使用btcwinex提供的任何服務或您未能了解所涉及的風險產生的任何損失或損害不承擔任何責任。</h3>
        </div>
    </section>
</section>
<jsp:include page="/common/foot.jsp" />
</body>
</html>
