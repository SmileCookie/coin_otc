<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%--<h1 style="margin: 0px; padding: 0px 15px; height: 48px; overflow: hidden;">--%>
    <%--<a title='${L:l(lan,"用户中心")}' href="${main_domain }" target="_blank" swaped="true"><img--%>
            <%--style="border-width: 0px; margin: 0px; padding: 0px;"--%>
            <%--src="${static_domain }/statics/img/common/vip_logo.png"/> </a>--%>
<%--</h1>--%>
<div style="padding: 0px 20px; height: 50px; text-align: right; line-height: 40px; overflow: hidden;">
</div>
<div style="padding: 2px 20px 30px;">
    <p>
        ${L:l1(lan,"%%,您好！",curUser.userName) }
    </p>
    <p>
        ${L:l(lan,"您正在找回登录密码，以下是您的新登录密码，请点击下面的链接启用您的新密码。")}
    </p>
    <p style="width: 100%; overflow: hidden; word-wrap: break-word;">
        ${L:l1(lan,"您的新密码为：%%。",newPass) }<br/>
    </p>
    <p style="width: 100%; overflow: hidden; word-wrap: break-word;">
        <a href="${vip_domain }/ac/password_usenew?userId=${curUser.id }&code=${code }" target="_blank" swaped="true">
            ${vip_domain }/ac/password_usenew?userId=${curUser.id }&code=${code }
        </a>
        <br/>
    </p>

    <p>
        <br/>
        ${L:l(lan,"此为自动发送邮件，请勿直接回复！如您有任何疑问，请点发送邮件到support@btcwinex.com。") }
    </p>
</div>





