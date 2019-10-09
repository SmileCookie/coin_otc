<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%--<h1 style="margin: 0px; padding: 0px 15px; height: 48px; overflow: hidden;">--%>
	<%--<a title='${L:l(lan,"用户中心")}' href="${main_domain }" target="_blank" swaped="true"><img style="border-width: 0px; margin: 0px; padding: 0px;" src="${static_domain }/statics/img/common/vip_logo.png" /> </a>--%>
<%--</h1>--%>
<%--<div style="padding: 0px 20px; height: 50px; text-align: right; line-height: 40px; overflow: hidden;"></div>--%>
<div style="padding: 2px 20px 30px;">
	<p>
		${L:l1(lan,"%%,您好！",curUser.userName)}<br/>
	</p>

    <p>${L:l(lan, emailInfo)}<br/></p>
    <p>${L:l1(lan, "验证码：%%",emailCode)}<br/></p>
	<p>${L:l(lan, "出于安全原因，该验证码将于10分钟后失效。切勿将验证码告知他人。")}<br/>
	</p>
    <p>
        <br />
        <font color='#CCCCCC'>
        ${L:l(lan,"如果此活动不是您本人所为，请尽快联系XXXX客服人员。") }
        <br />
        ${L:l(lan,"此为自动发送邮件，请勿直接回复！") }
        <br />
        ${L:l(lan,"如您有任何疑问，请发送邮件到XXXXX。") }
        </font>
    </p>
</div>
