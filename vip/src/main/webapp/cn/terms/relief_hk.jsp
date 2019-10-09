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
            <h2>免責聲明</h2>
            <h5></h5>
            <p class="mb40">
                本網站（btcwinex.com）是依現況現狀提供您使用，並不保證本站所提供的信息和服務能夠充分滿足用戶的需求，btcwinex 保留隨時修改各項服務的部分或全部功能的權利。 Btcwinex對于本網站服務不作任何明示、暗示或法定的擔保、主張或聲明，包括但不限于對于質量、效能、無侵權、或特定用途適用性的擔保，或因交易過程、按慣例或行業常規而衍生的擔保。            </p>
            <h3 class="mb10">1.信息准確性</h3>
            <p class="mb40">
                我們努力確保本網站信息的准確性，本站上的信息和內容僅用于協助用戶作出獨立決定，如有更改，不會另行通知，請定期查看。我們已采取合理措施確保本站信息的准確性，但是我們不保證通過本網站提供的任何服務或産品的內容的准確性、適用性、可靠性、完整性，並且不對任何直接或間接引起的任何損失或損害承擔責任，我們對此類信息的使用或解釋不承擔任何責任。            </p>
            <h3>2.服務有效性</h3>
            <p>
                我们会尽最大努力向用户提供服务，确保服务的连贯性和安全性。但我们不能随时预见和防范法律、技术以及其他风险，基于互联网的特殊性，btcwinex不保证提供的本网站服务、本网站或其中所含功能，不会中断、丢失、拦截、更改、安全可靠、正确、完整或无错误；不保证本网站、本网站服务器没有任何病毒、蠕虫、黑客攻击、木马、路由、系统不稳定或任何其他有害的指令、程序或元件，对服务的及时性、安全性都不作担保。
            </p>
            <h3> 3.收费服务 </h3>
            <p>
                Btcwinex爲用戶提供的部分服務是以收費方式提供的。您同意根據我們網站上發布的收費標准向公司支付適當的費用。我們可能根據實際需要對收費服務的收費標准、方式進行修改和變更，我們也可能會對部分免費服務開始收費。我們會努力在費用標准的變更生效前通過我們的網站或其他渠道對相關變更進行公告。如果您不同意上述修改、變更或付費內容，則應停止使用該服務。
            <h3>4.賬戶安全</h3>
            <p>
                Btcwinex不會向任何用戶索取密碼，不會讓用戶向任何非本站交易中心裏提供的帳戶、數字資産充值地址轉賬或打款。往非交易平台提供的賬戶、數字資産地址裏打款或幣所造成的損失本站不負責任。            </p>
            <h3>5.服務變更</h3>
            <p>
                Btcwinex可以隨時暫時停止提供或者限制本服務部分功能或提供新的功能。只要用戶仍然使用本服務，表示用戶仍然同意本協議或者變更後的協議，btcwinex對您和任何第三人均無需承擔任何責任。            </p>
            <h3>6.用戶信息</h3>
            <p>
                我們有權了解用戶使用我們産品或服務的真實交易背景及目的，用戶應隨時確認所上傳或刊載的信息是否正確、並自行采取備份存檔等保護措施。如果我們有合理理由懷疑用戶提供虛假交易信息的，我們有權暫時或永久限制用戶所使用的産品或服務的部分或全部功能。            </p>
            <h3>7.交易異常</h3>
            <p>
                用戶在進行交易時同意並認可，可能由于數字資産網絡連線問題或其他不可抗力因素，造成本服務無法提供。如果因用戶提供錯誤資料信息造成本站于上述異常狀況發生時，無法及時通知用戶相關交易後續處理方式的，btcwinex不承擔任何損害賠償責任。如果處理您的交易時發生錯誤，btcwinex有權修正、移除、撤回或刪除受影響的交易，btcwinex對于您因此造成的損害均不負賠償責任。            </p>
            <h3>8.用戶言論</h3>
            <p>
                用戶對本站發布的用戶評論僅代表用戶個人觀點。這並不意味著本站同意他們的觀點或確認他們的描述。本站不承擔任何用戶評論引起的任何法律責任。
            </p>
            <h3>9.不可抗力</h3>
            <p>
                除了上述適用的免責聲明外，btcwinex在本條款下的表現如果由于超出其合理控制的原因而導致的中斷或延遲，包括但不限于：台風、地震、海嘯、洪水、停電、戰爭、恐怖襲擊等不可抗力的因素；由于黑客攻擊、計算機病毒侵入或發作、電力故障、網站升級、銀行方面的問題、因政府管制而造成的暫時性關閉等影響網絡正常經營的原因而造成的服務中斷或者延遲；因行業現有技術力量無法預測或無法解決的技術問題而造成的損失；任何第三方數據提供者的行爲疏忽，或其他第三方信息提供者行爲疏忽，第三方軟件或通信方式中斷。Btcwinex對不可抗力情況下産生的任何類型的任何損害不承擔任何責任。
            </p>
        </div>
    </section>
</section>
<jsp:include page="/common/foot.jsp" />
</body>
</html>