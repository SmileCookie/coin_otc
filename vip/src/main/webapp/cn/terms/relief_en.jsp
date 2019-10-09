<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>

<!doctype html>
<html>
<head>
    <jsp:include page="/common/head.jsp" />
    <title>${L:l(lan,'免责声明')}-${WEB_NAME }</title>
    <meta name="keywords" content="${WEB_KEYWORD }" />
    <meta name="description" content="${WEB_DESC }" />
</head>
<body class="room minWidth">
<jsp:include page="/common/top.jsp" />
<section class="news_mainer bbyh-dk">
    <section class="news_container">
        <div class="terms_main clearfix">
            <h2>Disclaimer</h2>
            <h5></h5>
            <p class="mb40">
                This website (btcwinex.com) is to provide you with the use based on current situation, not guarantee that the information and services provided can fully meet the needs of users. Btcwinex reserves the right to modify some or all functions of the services at any time. Btcwinex does not make any express, implied or statutory warranties, claims or representations regarding the services, including but not limited to warranties of quality, effectiveness, non-infringement, or fitness for a particular purpose, or warranties derived from the trading process, conventions or industry practices.
            </p>
            <h3 class="mb10">1. Information accuracy</h3>
            <p class="mb40"> We strive to ensure the accuracy of the information on this website. The information and content are only used to assist users to make independent decisions. We will not inform you if there is any change, so please check it regularly. We have taken reasonable steps to ensure the accuracy of the information, but we do not guarantee the accuracy, applicability, reliability and integrity of any service or product content provided by this website. We shall not be liable for any loss or damage caused directly or indirectly, including the use or interpretation of such information.</p>
            <h3> 2. Effectiveness of services</h3>
            <p>
                We will do our best to provide services and ensure continuity and security for users. However, we cannot foresee and prevent laws, technologies and other risks at any time. Based on the particularity of the Internet, btcwinex does not guarantee that the services provided on this website or the functions contained therein will not be interrupted, lost, intercepted, changed, or always be safe, reliable, correct, complete or error-free. We do not guarantee that this website and servers have no virus, worm, hacker attack, or Trojans, including any other harmful instruction, program, or component caused by routing or system instability. Even we do not guarantee the timeliness or security of the services.
            </p>
            <h3> 3. Charging services</h3>
            <p>
                Btcwinex provides users with some services for a fee. You agree to pay the company an appropriate fee based on the rates posted on our website. We may modify and change the charging standards and methods of the charging services according to actual needs. We may also start charging for some free services. We will try to announce the changes through our website or other channels before the changes take effect. If you do not agree to the above modifications, changes or paid content, you should stop using the services.
            </p>
            <h3>4. Account security</h3>
            <p>
                Btcwinex will not ask any user for a password and will not allow any user to transfer or make a payment to any account or digital assets deposit address provided by non-local trading center of the site. The website shall not be responsible for any loss caused by paying coins to the account or digital assets address provided by the non-local trading platform.
            </p>
            <h3>5. Service changes</h3>
            <p>
                Btcwinex can temporarily stop providing or restricting some functions of the services or providing new functions at any time. Btcwinex is not responsible for you or any third party as long as the user still uses the services, which means that the user still agrees to this agreement or the changed agreement.
            </p>
            <h3>6. User information</h3>
            <p>
                We have the right to know the real trading background and purpose of using our products or services. So you should confirm whether the information uploaded or published is correct at any time, and take backup archiving and other protective measures by yourselves. If we are reasonable to suspect you provide false trading information, we have the right to temporarily or permanently restrict some or all functions of the products or services you used.
            </p>
            <h3>7. Abnormal trading</h3>
            <p>
                Users agree and acknowledge that the services may not be available due to digital assets network connection problems or other force majeure factors when conducting trading. Btcwinex shall not be liable for any damage caused by the failure of the website notifying the user about the subsequent processing of relevant transactions when meeting the abnormal situation, which caused by the user providing wrong information. Btcwinex reserves the right to correct, remove, cancel or delete the affected transactions in the event of any error when handling transactions, and btcwinex shall not be liable for any damage caused thereby.
            </p>
            <h3>8. User comments</h3>
            <p>
                Users' comments posted on this site only represent their personal views. This does not mean that this site agrees with their points of views or confirms their descriptions. This site does not assume any user comments caused by any legal responsibility.
            </p>
            <h3>9. Force majeure</h3>
            <p>
                In addition to the above applicable disclaimer, btcwinex's performance under this agreement may be interrupted or delayed for reasons beyond its reasonable control, including but not limited to: typhoon, earthquake, tsunami, flood, power failure, war, terrorist attack and other force majeure factors; service interruption or delay caused by hacker attack, computer virus invasion or attack, power failure, website upgrade, temporary shutdown caused by government regulation and other reasons affecting normal operation of the network; losses caused by technical problems that cannot be predicted or solved by the existing technical forces of the industry; negligence of any third party data provider or from others, interruption of third party software or communication mode. Btcwinex is not responsible for any damage of any kind caused by force majeure.
            </p>
        </div>
    </section>
</section>
<jsp:include page="/common/foot.jsp" />
</body>
</html>
