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
            <h2>免责声明</h2>
            <h5></h5>
            <p class="mb40">
                本网站（btcwinex.com）是依现况现状提供您使用，并不保证本站所提供的信息和服务能够充分满足用户的需求，btcwinex 保留随时修改各项服务的部分或全部功能的权利。 Btcwinex对于本网站服务不作任何明示、暗示或法定的担保、主张或声明，包括但不限于对于质量、效能、无侵权、或特定用途适用性的担保，或因交易过程、按惯例或行业常规而衍生的担保。
            </p>
            <h3 class="mb10">1.信息准确性</h3>
            <p class="mb40"> 我们努力确保本网站信息的准确性，本站上的信息和内容仅用于协助用户作出独立决定，如有更改，不会另行通知，请定期查看。我们已采取合理措施确保本站信息的准确性，但是我们不保证通过本网站提供的任何服务或产品的内容的准确性、适用性、可靠性、完整性，并且不对任何直接或间接引起的任何损失或损害承担责任，我们对此类信息的使用或解释不承担任何责任。</p>
            <h3> 2.服务有效性 </h3>
            <p>
                我们会尽最大努力向用户提供服务，确保服务的连贯性和安全性。但我们不能随时预见和防范法律、技术以及其他风险，基于互联网的特殊性，btcwinex不保证提供的本网站服务、本网站或其中所含功能，不会中断、丢失、拦截、更改、安全可靠、正确、完整或无错误；不保证本网站、本网站服务器没有任何病毒、蠕虫、黑客攻击、木马、路由、系统不稳定或任何其他有害的指令、程序或元件，对服务的及时性、安全性都不作担保。
            </p>
            <h3> 3.收费服务 </h3>
            <p>
                Btcwinex为用户提供的部分服务是以收费方式提供的。您同意根据我们网站上发布的收费标准向公司支付适当的费用。我们可能根据实际需要对收费服务的收费标准、方式进行修改和变更，我们也可能会对部分免费服务开始收费。我们会努力在费用标准的变更生效前通过我们的网站或其他渠道对相关变更进行公告。如果您不同意上述修改、变更或付费内容，则应停止使用该服务。
            </p>
            <h3>4.账户安全</h3>
            <p>
                Btcwinex不会向任何用户索取密码，不会让用户向任何非本站交易中心里提供的帐户、数字资产充值地址转账或打款。往非交易平台提供的账户、数字资产地址里打款或币所造成的损失本站不负责任。
            </p>
            <h3>5.服务变更</h3>
            <p>
                Btcwinex可以随时暂时停止提供或者限制本服务部分功能或提供新的功能。只要用户仍然使用本服务，表示用户仍然同意本协议或者变更后的协议，btcwinex对您和任何第三人均无需承担任何责任。
            </p>
            <h3>6.用户信息</h3>
            <p>
                我们有权了解用户使用我们产品或服务的真实交易背景及目的，用户应随时确认所上传或刊载的信息是否正确、并自行采取备份存档等保护措施。如果我们有合理理由怀疑用户提供虚假交易信息的，我们有权暂时或永久限制用户所使用的产品或服务的部分或全部功能。
            </p>
            <h3>7.交易异常</h3>
            <p>
                用户在进行交易时同意并认可，可能由于数字资产网络连线问题或其他不可抗力因素，造成本服务无法提供。如果因用户提供错误资料信息造成本站于上述异常状况发生时，无法及时通知用户相关交易后续处理方式的，btcwinex不承担任何损害赔偿责任。如果处理您的交易时发生错误，btcwinex有权修正、移除、撤回或删除受影响的交易，btcwinex对于您因此造成的损害均不负赔偿责任。
            </p>
            <h3>8.用户言论</h3>
            <p>
                用户对本站发布的用户评论仅代表用户个人观点。这并不意味着本站同意他们的观点或确认他们的描述。本站不承担任何用户评论引起的任何法律责任。
            </p>
            <h3>9.不可抗力</h3>
            <p>
                除了上述适用的免责声明外，btcwinex在本条款下的表现如果由于超出其合理控制的原因而导致的中断或延迟，包括但不限于：台风、地震、海啸、洪水、停电、战争、恐怖袭击等不可抗力的因素；由于黑客攻击、计算机病毒侵入或发作、电力故障、网站升级、银行方面的问题、因政府管制而造成的暂时性关闭等影响网络正常经营的原因而造成的服务中断或者延迟；因行业现有技术力量无法预测或无法解决的技术问题而造成的损失；任何第三方数据提供者的行为疏忽，或其他第三方信息提供者行为疏忽，第三方软件或通信方式中断。Btcwinex对不可抗力情况下产生的任何类型的任何损害不承担任何责任。
            </p>
        </div>
    </section>
</section>
<jsp:include page="/common/foot.jsp" />
</body>
</html>