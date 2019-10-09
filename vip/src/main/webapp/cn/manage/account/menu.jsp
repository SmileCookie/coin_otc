<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ page session="false"%>

<div class="menu">
	<ul class="nav-a">
		<li>
			<a class="info" href="/manage/account">${L:l(lan, '资金-左侧子导航-标签-1')}</a>
		</li>
		<li>
			<a class="safe" href="/manage/account/charge">${L:l(lan, '充值-左侧子导航-标签-1')}</a>
		</li>
		<li>
			<a class="msgsetting" href="/manage/account/download">${L:l(lan, '提现-左侧子导航-标签-1')}</a>
		</li>
	</ul>
</div>

<script>
	$(function() {
		var url = window.location.pathname;
		var $bitAcn = $('#bitAccount > li:not(.navFirst) > a');
		var $thisIndex;
		$(".nav-a a").each(function() {
			$this = $(this);
			$thisIndex = $this.parent().index();
			$thisurl = $(this).attr("href");
			if(url.indexOf($thisurl) >= 0) {
				$this.addClass("active").parents().siblings("li").find("a").removeClass("active");
				//$bitAcn.eq($thisIndex).addClass("active").parents().siblings("li").find("a").removeClass("active");
			}
		})
	})
</script>