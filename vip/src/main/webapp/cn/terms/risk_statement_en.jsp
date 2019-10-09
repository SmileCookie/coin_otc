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
            <h2>Margin Trading Risk Disclosure Statement</h2>
            <h5></h5>
            <h3>1. Risks of the Transmission of Information over the Internet</h3>
            <p>
                The transmission of data or information (including communications by e-mail) over the Internet or other publicly accessible networks is not one hundred percent secure, and is subject to possible loss, interception, or alteration while in transit. Accordingly, btcwinex does not assume any liability for any damage you may experience or costs you may incur as a result of any transmissions over the Internet or other publicly accessible networks, including but not limited to transmissions involving the Platform or e-mail with btcwinex containing your personal information.
            </p>
            <h3>2. Funding Risks</h3>
            <p>
                Margin trading on btcwinex is HIGH RISK. The high volatility and substantial risk of illiquidity in market means that you may not always be able to liquidate your position. It may cause loss of funds.<br/>
                If the value of assets in your Account falls below the maintenance margin requirement or btcwinex determines, that your Account appears to be in danger of defaulting on a loan, btcwinex may seize and liquidate any or all of your positions and assets in your Account in order to settle your debt to lenders. If your account still contains insufficient funds to settle your debts to lenders after your positions and assets are liquidated, you will be responsible for any additional funds owed. Intentionally defaulting on a loan may result in legal action brought to you by btcwinex.
            </p>
            <h3>3. Investment Risks</h3>
            <p class="mb40">
                Although btcwinex takes several precautions to prevent users from defaulting on a loan, the high volatility and substantial risk of illiquidity in market means that btcwinex cannot make any guarantees against default. When you lend to other users by the lending system of platform, you risk the loss of an unpaid circumstance if the borrower defaults on a loan and liquidation fails to raise sufficient funds to cover the debt.
            </p>
            <h3>
                All margin trading including the loan and lending is conducted by Users, and btcwinex does not take any responsibility for any loss or damage incurred as a result of the use or services provided by btcwinex, or your failure to understand the risks involved in the use of such services.
            </h3>
        </div>
    </section>
</section>
<jsp:include page="/common/foot.jsp" />
</body>
</html>
