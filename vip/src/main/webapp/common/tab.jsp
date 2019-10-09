<%@page import="com.world.model.entity.user.User"%>
<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<div class="b_usertips clearfloat">
	<div class="ld"><p><span id="showTimeSpan_11"></span>，<i>${curUser.userName}</i>&nbsp;&nbsp;${L:l(lan,'上次登录时间')}：<i><fmt:formatDate value="${curUser.previousLogin}" pattern="yy-MM-dd HH:mm"/></i>&nbsp;&nbsp;${L:l(lan,'登录IP')}：<i>${curUser.trueIp}</i></p></div>
	<script>$("#showTimeSpan_11").text(vip.user.showTime());</script>
	<div class="rd"><span class="ld">${L:l(lan,'您当前安全认证等级')}：</span><div class="jindu ld"><div style="width:${curUser.userContact.approveLevel}%;" 
      <%
    	User user = (User)request.getAttribute("curUser");
    
        String mao = user.getUserContact().getMaoLink();
        String title = "";
        if(mao.equals("stip1")){
        	
    %>
    		title='${L:l(lan,"非常低")}'
    <%
        }else if(mao.equals("stip2")){
    %>
    		title="${L:l(lan,'低')}"
    <%
        }else if(mao.equals("stip2")){
    %>
    		title="${L:l(lan,'中')}"
    <%
        }else if(mao.equals("stip2")){
    %>
    		title="${L:l(lan,'高')}"
    <%
        }else if(mao.equals("stip2")){
    %>
    		title="${L:l(lan,'非常高')}"
    <%
        }else if(mao.equals("stip2")){
    %>
    		title="${L:l(lan,'提高安全等级能有效保证资金安全')}"
    <%
        }
    %>
      >
    </div></div><span class="ld"><c:if test="${not empty mao }"><a href="javascript:void(0);" onclick="upgrade('${mao}')">${L:l(lan,'升级')}</a></c:if></span></div>
</div>
<script type="text/javascript">
function upgrade(mao){
    if(mao=="step1"){
       Alert("完成邮箱认证即可升级，现在就去<a href='${vip_domain }/manage/auth/logic/email' target='_blank'>认证邮箱</a>。");
       return;
    }else if(mao=="step2"){
       Alert("完成手机绑定或谷歌认证即可升级，现在就去<a href='${vip_domain }/manage/auth/logic/mobile' target='_blank'>绑定手机</a>。");
       return;
    }else if(mao=="step3"){
       Alert("完成实名认证即可升级，现在就去<a href='${vip_domain }/manage/auth' target='_blank'>实名认证</a>。");
       return;
    }else if(mao=="step4"){
       Alert("开启十星宝即可升级，现在就去<a href='${vip_domain }/u/sxb/sxbAuth' target='_blank'>开启十星宝</a>。");
       return;
    }
 }
</script>
