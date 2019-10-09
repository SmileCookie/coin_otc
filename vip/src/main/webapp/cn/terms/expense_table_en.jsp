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
<section class="news_mainer bbyh-dk">
    <section class="news_container">
        <div class="terms_main clearfix">
            <h2>Fees</h2>
            <h5></h5>
            <h6 class="mb20">Transaction Fees</h6>
            <section class="table_box mb20">
                <table>
                    <thead>
                        <th>Account level</th>
                        <th>Points</th>
                        <th>Fees</th>
                    </thead>
                    <tbody>
                        <tr>
                            <td>VIP-0</td>
                            <td><10000</td>
                            <td>0.200%</td>
                        </tr>
                        <tr>
                            <td>VIP-1</td>
                            <td>≥10000</td>
                            <td>0.180%</td>
                        </tr>
                        <tr>
                            <td>VIP-2</td>
                            <td>≥30000</td>
                            <td>0.160%</td>
                        </tr>
                        <tr>
                            <td>VIP-3</td>
                            <td>≥60000</td>
                            <td>0.140%</td>
                        </tr>
                        <tr>
                            <td>VIP-4</td>
                            <td>≥100000</td>
                            <td>0.120%</td>
                        </tr>
                        <tr>
                            <td>VIP-5</td>
                            <td>≥150000</td>
                            <td>0.100%</td>
                        </tr>
                        <tr>
                            <td>VIP-6</td>
                            <td>≥300000</td>
                            <td>0.090%</td>
                        </tr>
                        <tr>
                            <td>VIP-7</td>
                            <td>≥500000</td>
                            <td>0.080%</td>
                        </tr>
                        <tr>
                            <td>VIP-8</td>
                            <td>≥800000</td>
                            <td>0.070%</td>
                        </tr>
                        <tr>
                            <td>VIP-9</td>
                            <td>≥1200000</td>
                            <td>0.060%</td>
                        </tr>
                        <tr>
                            <td>VIP-10</td>
                            <td>≥2000000</td>
                            <td>0%</td>
                        </tr>
                    </tbody>
                </table>
            </section>
            <h6 class="mb20">Withdrawal Fees</h6>
            <section class="table_box table_box_1 mb30">
                <table>
                    <thead>
                        <tr>
                            <th>Coins</th>
                            <th>Network Fees</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>BTC</td>
                            <td>0.0005 BTC</td>
                        </tr>
                        <tr>
                            <td>LTC</td>
                            <td>0.001 LTC</td>
                        </tr>
                        <tr>
                            <td>ETC</td>
                            <td>0.01 ETC</td>
                        </tr>
                        <tr>
                            <td>ETH</td>
                            <td>0.01 ETH</td>
                        </tr>
                        <tr>
                            <td>DASH</td>
                            <td>0.002 DASH</td>
                        </tr>
                        <tr>
                            <td>OMG</td>
                            <td>0.6 OMG</td>
                        </tr>
                        <tr>
                            <td>USDT</td>
                            <td>2 USDT</td>
                        </tr>
                        <tr>
                            <td>QTUM</td>
                            <td>0.01 QTUM</td>
                        </tr>
                        <tr>
                            <td>EOS</td>
                            <td>0.1 EOS</td>
                        </tr>
                        <tr>
                            <td>ELF</td>
                            <td>5 ELF</td>
                        </tr>
                        <tr>
                            <td>SNT</td>
                            <td>40 SNT</td>
                        </tr>
                        <tr>
                            <td>ZRX</td>
                            <td>3 ZRX</td>
                        </tr>
                        <tr>
                            <td>LINK</td>
                            <td>2 LINK</td>
                        </tr>
                        <tr>
                            <td>KNC</td>
                            <td>5 KNC</td>
                        </tr>
                        <tr>
                            <td>MANA</td>
                            <td>15 MANA</td>
                        </tr>
                        <tr>
                            <td>MCO</td>
                            <td>0.5 MCO</td>
                        </tr>
                        <tr>
                            <td>LRC</td>
                            <td>10 LRC</td>
                        </tr>
                        <tr>
                            <td>DGD</td>
                            <td>0.05 DGD</td>
                        </tr>
                        <tr>
                            <td>VDS</td>
                            <td>0.1 VDS</td>
                        </tr>
                    </tbody>
                </table>
            </section>
            <h6 class="lintH32">Notes:</h6>
            <p>
                1. Your account will be upgraded to corresponding levels automatically when you earn certain points.<br/>
                2. We may adjust our account level policy periodically. 
            </p>
        </div>
    </section>
</section>
<jsp:include page="/common/foot.jsp" />
</body>
</html>
