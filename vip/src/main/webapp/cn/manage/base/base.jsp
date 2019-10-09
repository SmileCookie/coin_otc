<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ page session="false"%>
<div class="content user_manage">
	<h2>${L:l(lan,"帐户信息-左侧子导航-标签-1")}</h2>
	<ul class="mangen_index_head clearfix">
		<li class="clearfix">
			<span class="left">
				<svg class="icon id_icon" aria-hidden="true">
					<use xlink:href="#icon-IDcard"></use>
				</svg>
			</span>
			<span class="left">${L:l(lan,"帐户信息-账户信息中部-标签-1")}：${curUser.userName}</span>
			<c:choose>
				<c:when test="${authStatus eq 6}">
					<span class="user_status user_status_2">${L:l(lan,"已认证")}</span>
				</c:when>
				<c:otherwise>
					<span class="user_status user_status_1">${L:l(lan,"未认证")}</span>
					<a class="user_href" href="/manage/auth/authentication">${L:l(lan,"完成身份认证")}</a>
				</c:otherwise>
			</c:choose>
		</li>
		<li class="clearfix">
			<span>${L:l(lan,"安全等级：") }</span>
			<div class="clearfix">
				<span class="${ userSafeLevel == 1 ? 'low' : userSafeLevel == 2 ? 'in' : 'high' }">
					<svg class="icon" aria-hidden="true">
						<use xlink:href="#icon-anquancopy"></use>
					</svg>
				</span>
					<c:if test="${ userSafeLevel == 1 }"><span class="low pwdlevel_text">${L:l(lan,"低") }</span></c:if>
					<c:if test="${ userSafeLevel == 2 }"><span class="in pwdlevel_text">${L:l(lan,"中") }</span></c:if>
					<c:if test="${ userSafeLevel == 3 }"><span class="high pwdlevel_text">${L:l(lan,"高") }</span></c:if>
				<span class="user_help help_hover">
					<span class="help_hover">
						<svg class="icon help help_hover" aria-hidden="true">
							<use xlink:href="#icon-bangzhu"></use>
						</svg>
					</span>
					<div class="help_div ${lan}">
						<p class="clearfix">
							<span class="low tips_icons">
								<svg class="icon" aria-hidden="true">
									<use xlink:href="#icon-anquancopy"></use>
								</svg>
							</span>
							<span class="low pwdlevel_text">${L:l(lan,"低") }</span>
							<span class="help_text">${L:l(lan,"绑定手机或邮箱") }</span>
						</p>
						<p class="clearfix">
							<span class="in tips_icons">
								<svg class="icon" aria-hidden="true">
									<use xlink:href="#icon-anquancopy"></use>
								</svg>
							</span>
							<span class="in pwdlevel_text">${L:l(lan,"中") }</span>
							<span class="help_text">${L:l(lan,"开启资金密码或谷歌验证") }</span>
						</p>
						<p class="clearfix">
							<span class="high tips_icons">
								<svg class="icon" aria-hidden="true">
									<use xlink:href="#icon-anquancopy"></use>
								</svg>
							</span>
							<span class="high pwdlevel_text">${L:l(lan,"高") }</span>
							<span class="help_text">${L:l(lan,"同时开启资金密码、谷歌验证") }</span>
						</p>
						<span class="xia"></span>
					</div>
				</span>
			</div>
		</li>
		<li><span>${L:l(lan,"帐户信息-账户信息中部-标签-3") }：</span><span><fmt:formatDate value="${curUser.previousLogin}" pattern="${lan == 'en'? 'MM-dd-yyyy HH:mm:ss':'yyyy-MM-dd HH:mm:ss'}"/></span>&nbsp;&nbsp;&nbsp;<span>IP：</span><span>${curUser.loginIp }</span></li>
	</ul>
	<ul class="user_list clearfix">
		<li class="user_head_li"> <span class="user_border"></span>${L:l(lan,"基础设置1") } </li>
		<li class="user_body_li left clearfix">
			<p class="p1">
				<svg class="icon" aria-hidden="true">
					<use xlink:href="#icon-icon_mail"></use>
				</svg>
			</p>
			<p class="p2 relative_10">
				<span class="user_list_name">
					 ${L:l(lan,"帐户信息-账户信息中部-安全认证项-1")}
					 <c:if test="${emailStatu eq 2}">
							<span class="user_mobile_emil_title">(${email})</span>
					</c:if>
				</span>
				<span class="user_list_text">
					${L:l(lan,"登录，提现，及安全设置时用以收取验证邮件")}
				</span>
			</p>
			<c:if test="${emailStatu ne 2}">
				<p class="p3">
					<a role="button" class="btn btn-sm ${emailStatu eq 2?'btn-primary':'btn-set'}" href="/manage/auth/email">
						${emailStatu eq 2 ? L:l(lan,"帐户信息-账户信息中部-按钮-2"):L:l(lan,"帐户信息-账户信息中部-按钮-1")}
					</a>
				</p>
			</c:if>
		</li>
		<li class="user_body_li right mb40">
			<p class="p1">
				<svg class="icon iphone_icon" aria-hidden="true">
					<use xlink:href="#icon-icon_phone"></use>
				</svg>
			</p>
			<p class="p2 relative_10">
				<span class="user_list_name">
					 ${L:l(lan,"帐户信息-账户信息中部-安全认证项-2")}
					 <c:if test="${mobileStatu eq 2}">
							<span class="user_mobile_emil_title">(${mobile})</span>
					</c:if>
				</span>
				<span class="user_list_text">
					${L:l(lan,"登录，提现，及安全设置时用以收取验证短信")}
				</span>
			</p>
			<p class="p3">
				<a role="button" class="btn btn-sm ${mobileStatu eq 2?'btn-primary':'btn-set'}" href="/manage/auth/mobile">${mobileStatu eq 2 ? L:l(lan,"帐户信息-账户信息中部-按钮-2"):L:l(lan,"帐户信息-账户信息中部-按钮-1")}</a>
			</p>
		</li>
		<li class="user_head_li"><span class="user_border"></span>${L:l(lan,"安全设置1") } </li>
		<li class="user_body_li left clearfix mb10">
			<p class="p1">
				<svg class="icon iphone_icon" aria-hidden="true">
					<use xlink:href="#icon-icon_mima"></use>
				</svg>
			</p>
			<p class="p2">
				<span class="user_list_name">
					${L:l(lan,"帐户信息-账户信息中部-安全认证项-4")}
				</span>
				<span class="user_list_text">
					${L:l(lan,"登录时使用")}
					<!-- <c:choose>
						<c:when test="${curUser.pwdLevel <= 40 }"><em class="low">${L:l(lan,"弱") }</em></c:when>
						<c:when test="${curUser.pwdLevel <= 60 }"><em class="in">${L:l(lan,"中") }</em></c:when>
						<c:otherwise><em class="high">${L:l(lan,"强") }</em></c:otherwise>
					</c:choose> -->
				</span>
			</p>
			<p class="p3"><a role="button" class="btn btn-primary btn-sm" href="/manage/auth/pwd/log">${L:l(lan,"帐户信息-账户信息中部-按钮-2") }</a></p>
		</li>
		<li class="user_body_li right mb10">
			<p class="p1">
				<svg class="icon iphone_icon" aria-hidden="true">
					<use xlink:href="#icon-zijinmima"></use>
				</svg>
			</p>
			<p class="p2">
				<span class="user_list_name">
					${L:l(lan,"帐户信息-账户信息中部-安全认证项-5")}
				</span>
				<span class="user_list_text">
					${L:l(lan,"交易时使用")}
					<!--  <c:choose>
						<c:when test="${hasSafe}">
							${L:l(lan,"帐户信息-账户信息中部-安全认证标识-4")}
							<c:choose>
								<c:when test="${curUser.safeLevel <= 40 }"><em class="low">${L:l(lan,"弱") }</em></c:when>
								<c:when test="${curUser.safeLevel <= 60 }"><em class="in">${L:l(lan,"中") }</em></c:when>
								<c:otherwise><em class="high">${L:l(lan,"强") }</em></c:otherwise>
							</c:choose> 
						</c:when>
						<c:otherwise>
							${L:l(lan,"帐户信息-账户信息中部-安全认证标识-5")}
						</c:otherwise>
					</c:choose> -->
				</span>
			</p>
			<p class="p3">
				<a role="button" class="btn btn-sm ${hasSafe?'btn-primary':'btn-set'}" href="/manage/auth/pwd/safe">${hasSafe?L:l(lan,"帐户信息-账户信息中部-按钮-2"):L:l(lan, "帐户信息-账户信息中部-按钮-1")}</a>
			</p>
		</li>
		<li class="user_body_li left clearfix">
			<p class="p1">
				<svg class="icon iphone_icon" aria-hidden="true">
					<use xlink:href="#icon-anquan1"></use>
				</svg>
			</p>
			<p class="p2 relative_10">
				<span class="user_list_name">
					${L:l(lan,"帐户信息-账户信息中部-安全认证项-3")}
				</span>
				<span class="user_list_text">
					${L:l(lan,"登录，提现，安全设置时用以验证谷歌二次验证")}
					<!-- <c:choose>
						<c:when test="${googleAuth eq 2}">
							${L:l(lan,"Google验证码已验证")}
						</c:when>
						<c:otherwise>
							${L:l(lan,"帐户信息-账户信息中部-安全认证标识-3")}
						</c:otherwise>
					</c:choose> -->
				</span>
			</p>
			<p class="p3"><a role="button" class="btn  btn-sm ${googleAuth eq 2?'btn-primary':'btn-set'}" href="/manage/auth/google">${googleAuth eq 2 ? L:l(lan,"帐户信息-账户信息中部-按钮-2"):L:l(lan,"帐户信息-账户信息中部-按钮-1")}</a></p>
		</li>
	</ul>
</div>
