<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<script>
	var dateFormatStr = <fmt:formatDate value="${curUser.lastLoginTime }" pattern="yyyy-MM-dd HH:mm:ss"/>;
</script>
<%--<h1 style="margin: 0px; padding: 0px 15px; height: 48px; overflow: hidden;">--%>
	<%--<a title='${L:l(lan,"用户中心")}' href="${main_domain }" target="_blank" swaped="true"><img style="border-width: 0px; margin: 0px; padding: 0px;" src="${static_domain }/statics/img/common/vip_logo.png" /> </a>--%>
<%--</h1>--%>
<%--<div style="padding: 0px 20px; height: 50px; text-align: right; line-height: 40px; overflow: hidden;"></div>--%>
<div style="padding: 2px 20px 30px;">
	<p>
		${L:l1(lan,"%%,您好！",curUser.userName) }
	</p>
	<%--<p>${L:l(lan, emailInfo)}<br/></p>--%>
	<p>${L:l2(lan, "您的账号已成功登录XX，登录IP地址：%%，登录时间：%%。", ip, time)}<br/></p>
    <p>
        <br />
        <font color='#CCCCCC'>
            ${L:l(lan,"如果这不是您，为保障您的电子邮件地址安全，请及时更改您的密码并设置二次验证。")}
            <br />
            ${L:l(lan,"此为自动发送邮件，请勿直接回复！") }
            <br />
            ${L:l(lan,"如您有任何疑问，请发送邮件到XXXXX。") }
        </font>
    </p>
</div>