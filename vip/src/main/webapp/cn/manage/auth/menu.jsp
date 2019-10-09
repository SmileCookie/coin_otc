<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ page session="false"%>

<div class="menu">
	<ul class="nav-a">
		<li>
			<a class="info" href="/manage">${L:l(lan, '帐户信息-左侧子导航-标签-1')}</a>
		</li>
		<!-- <li>
			<a class="safe" href="/manage/auth/userAuthIndex">${L:l(lan, '提现额度')}</a>
		</li> -->
		<li>
			<a class="safe" href="/manage/auth">${L:l(lan, '安全设置-左侧子导航-标签-1')}</a>
		</li>
		<!--<li>
			<a class="msgsetting" href="/manage/msgsetting">${L:l(lan, '消息提醒')}</a>
		</li>-->
		
		<li>
			<a class="level" href="/manage/level">${L:l(lan, '帐户等级-左侧子导航-标签-1')}</a>
		</li>
		<li>
			<a class="level" href="/manage/toUserLoginHistroy">${L:l(lan, '登录日志-左侧子导航-标签-1')}</a>
		</li>
	</ul>
</div>

<script>

$(function () {
    var url = window.location.pathname;
    var $broNav = $('#bitManage > li:not(.navFirst) > a');
    var $thisIndex;
        $(".nav-a a").each(function () {
            $this = $(this);
            $thisIndex = $(this).parent().index();
            $thisurl = $this.attr("href");
            
			if(url.indexOf("/manage/auth/") >= 0 && url.indexOf("/manage/auth/userAuthIndex") < 0){
            	 	$this = $(".info");
            	 	$broNav.eq(0).addClass("active").parent().siblings("li").find("a").removeClass("active");
            	 	$this.addClass("active").parents().siblings("li").find("a").removeClass("active");
            }else if(url == $thisurl){
            		$this.addClass("active").parents().siblings("li").find("a").removeClass("active");
            	 	$broNav.eq($thisIndex).addClass("active").parent().siblings("li").find("a").removeClass("active");
            }
        })
})
</script>