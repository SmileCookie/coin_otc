<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ page session="false"%>
<div class="content">
<h2>${L:l(lan,'安全设置-安全设置上部-标题-1')}</h2>
<div class="basic-infocont stratecont">
	  <ul class="clearfix mt30">
                <li class="listtop">
                    <div class="ld"><b class="text-deepgray">${L:l(lan, "安全设置-登录验证未展开项-主标题-1")}</b><span>${L:l(lan, "安全设置-登录验证未展开项-描述-1")}</span></div>
                    <div class="md">
                        <c:choose>
                            <c:when test="${curUser.loginAuthenType == 0}">${L:l(lan, "未选择")}</c:when>
                            <c:otherwise><span>"${L:l(lan, curUser.loginAuthenTypeName)}"</span></c:otherwise>
                        </c:choose>
                    </div>
                    <div class="rd"><a class="btn-slide-down"><i class="iconfont2 icon-tiaozhuan ft30"></i></a></div>
                </li>
                <div class="listbody">
                    <dl>
                        <dd>
                            <span style="${curUser.loginAuthenType == 1 ? "color: #de211d;" : ""}">${L:l(lan, "安全设置-登录验证展开项-标签-1")}</span>
                            <c:if test="${curUser.loginAuthenType != 1}">
                                <a onclick="switchAuth(1, 1)">${L:l(lan, "选择此项")}</a>
                            </c:if>
                        </dd>
                        <dd>
                            <span style="${curUser.loginAuthenType == 3 ? "color: #de211d;" : ""}">${L:l(lan, "安全设置-登录验证展开项-标签-2")}</span>
                            <c:if test="${curUser.loginAuthenType != 3}">
                                <a onclick="switchAuth(1, 3)">${L:l(lan, "选择此项")}</a>
                            </c:if>
                        </dd>
                        <c:choose>
                            <c:when test="${hasGoogleAtuh}">
                                <dd>
                                    <span style="${curUser.loginAuthenType == 2 ? "color: #de211d;" : ""}">${L:l(lan, "安全设置-登录验证展开项-标签-3")}</span>
                                    <c:if test="${curUser.loginAuthenType != 2}">
                                        <a onclick="switchAuth(1, 2)">${L:l(lan, "选择此项")}</a>
                                    </c:if>
                                </dd>
                                <dd>
                                    <span style="${curUser.loginAuthenType == 4 ? "color: #de211d;" : ""}">${L:l(lan, "安全设置-登录验证展开项-标签-4")}</span>
                                    <c:if test="${curUser.loginAuthenType != 4}">
                                        <a onclick="switchAuth(1, 4)">${L:l(lan, "选择此项")}</a>
                                    </c:if>
                                </dd>
                            </c:when>
                            <c:otherwise>
                                <dd><span>${L:l(lan, "安全设置-登录验证展开项-标签-3")}</span> ${L:l(lan, "（您还未开启Google认证）")}</dd>
                                <dd><span>${L:l(lan, "安全设置-登录验证展开项-标签-4")}</span> ${L:l(lan, "（您还未开启Google认证）")}</dd>
                            </c:otherwise>
                        </c:choose>
                    </dl>
                </div>
                <div class="hei-top"></div>
                <li class="listtop">
                    <div class="ld"><b>${L:l(lan, "安全设置-交易验证未展开项-主标题-2")}</b><span>${L:l(lan, "安全设置-交易验证未展开项-描述-2")}</span></div>
                    <div class="md">
                        <c:choose>
                            <c:when test="${curUser.tradeAuthenType == 0}">${L:l(lan, "未选择")}</c:when>
                            <c:otherwise><span>"${L:l(lan, curUser.tradeAuthenTypeName)}"</span></c:otherwise>
                        </c:choose>
                    </div>
                    <div class="rd"><a class="btn-slide-down"><i class="iconfont2 icon-tiaozhuan ft30"></i></a></div>
                </li>
                <div class="listbody">
                    <dl>
                        <dd>
                            <span style="${curUser.tradeAuthenType == 1 ? "color: #de211d;" : ""}">${L:l(lan, "安全设置-交易验证展开项-标签-1")}</span>
                            <c:if test="${curUser.tradeAuthenType != 1}">
                                <a onclick="switchAuth(2, 1)">${L:l(lan, "选择此项")}</a>
                            </c:if>
                        </dd>
                        <dd>
                            <span style="${curUser.tradeAuthenType == 2 ? "color: #de211d;" : ""}">${L:l(lan, "安全设置-交易验证展开项-标签-2")}</span>
                            <c:if test="${curUser.tradeAuthenType != 2}">
                                <a onclick="switchAuth(2, 2)">${L:l(lan, "选择此项")}</a>
                            </c:if>
                        </dd>
                        <dd>
                            <span style="${curUser.tradeAuthenType == 3 ? "color: #de211d;" : ""}">${L:l(lan, "安全设置-交易验证展开项-标签-3")}</span>
                            <c:if test="${curUser.tradeAuthenType != 3}">
                                <a onclick="openSafePwd(2, 3)">${L:l(lan, "选择此项")}</a>
                            </c:if>
                        </dd>
                    </dl>
                </div>
                <div class="hei-top"></div>
                <li class="listtop">
                    <div class="ld"><b>${L:l(lan, "安全设置-提现验证未展开项-主标题-3")}</b><span>${L:l(lan, "安全设置-提现验证未展开项-描述-3")}</span></div>
                    <div class="md">
                        <c:choose>
                            <c:when test="${curUser.withdrawAuthenType == 0}">${L:l(lan, "未选择")}</c:when>
                            <c:otherwise><span>"${L:l(lan, curUser.withdrawAuthenTypeName)}"</span></c:otherwise>
                        </c:choose>
                    </div>
                    <div class="rd"><a class="btn-slide-down"><i class="iconfont2 icon-tiaozhuan ft30"></i></a></div>
                </li>
                <div class="listbody">
                    <dl>
                        <dd>
                            <span style="${curUser.withdrawAuthenType == 1 ? "color: #de211d;" : ""}">${L:l(lan, "安全设置-提现验证展开项-标签-1")}</span>
                            <c:choose>
                                <c:when test="${hasMobileAtuh || hasEmailAuth}">
                                    <c:if test="${curUser.withdrawAuthenType != 1}">
                                        <a onclick="switchAuth(3, 1)">${L:l(lan, "选择此项")}</a>
                                    </c:if>
                                </c:when>
                                <c:otherwise>（${L:l(lan, "您还未开启手机认证")}）</c:otherwise>
                            </c:choose>

                        </dd>
                        <c:choose>
                            <c:when test="${hasGoogleAtuh}">
                                <dd>
                                    <span style="${curUser.withdrawAuthenType == 2 ? "color: #de211d;" : ""}">${L:l(lan, "安全设置-提现验证展开项-标签-2")}</span>
                                    <c:if test="${curUser.withdrawAuthenType != 2}">
                                        <a onclick="switchAuth(3, 2)">${L:l(lan, "选择此项")}</a>
                                    </c:if>
                                </dd>
                                <dd>
                                    <span style="${curUser.withdrawAuthenType == 3 ? "color: #de211d;" : ""}">${L:l(lan, "安全设置-提现验证展开项-标签-3")}</span>
                                    <c:if test="${curUser.withdrawAuthenType != 3}">
                                        <a onclick="switchAuth(3, 3)">${L:l(lan, "选择此项")}</a>
                                    </c:if>
                                </dd>
                            </c:when>
                            <c:otherwise>
                                <dd><span>${L:l(lan, "安全设置-提现验证展开项-标签-2")}</span> (${L:l(lan, "安全设置-未开启谷歌验证-标签-1")})</dd>
                                <dd><span>${L:l(lan, "安全设置-提现验证展开项-标签-3")}</span> (${L:l(lan, "安全设置-未开启谷歌验证-标签-1")})</dd>
                            </c:otherwise><%--开启/u/auth/google?oper=0&dealType=googleAuth&dealVal=99--%>
                        </c:choose>
                    </dl>
                </div>
                <div class="hei-top"></div>
                    <li class="listtop listtop_address">
                    <div class="ld"><b>${L:l(lan, "提现地址验证")}</b><span>${L:l(lan, "用于数字货币新增提现地址时的安全设置")}</span></div>
                    <div class="md">
                        <c:choose>
                            <c:when test="${curUser.withdrawAddressAuthenType == 0 || curUser.withdrawAddressAuthenType == 1 }"><span>"${L:l(lan, "初级模式")}"</span></c:when>
                            <c:otherwise><span>"${L:l(lan, "安全模式")}"</span></c:otherwise>
                        </c:choose>
                    </div>
                    <div class="rd"><a class="btn-slide-down"><i class="iconfont2 icon-tiaozhuan ft30"></i></a></div>
                </li>
                <div class="listbody">
                    <dl>
                        <dd>
                            <span style="${curUser.withdrawAddressAuthenType == 1 || curUser.withdrawAddressAuthenType == 0 ? " color: #de211d; " : " "}">${L:l(lan, "初级模式")}</span>
                            <c:if test="${curUser.withdrawAddressAuthenType != 1 && curUser.withdrawAddressAuthenType != 0}">
                                <a onclick="authenType(4, 1,${curUser.withdrawAddressAuthenType})">${L:l(lan, "选择此项")}</a>
                            </c:if>
                        </dd>
                        <dd>
                            <span style="${curUser.withdrawAddressAuthenType == 2 ? " color: #de211d; " : " "}">
                                ${L:l(lan, "安全模式")} (${L:l(lan, "开启后，增加新地址时会进行安全验证，并锁定该地址24小时。")})
                            </span>
                            <c:if test="${curUser.withdrawAddressAuthenType != 2}">
                                <a onclick="authenType(4, 2,${curUser.withdrawAddressAuthenType})">${L:l(lan, "选择此项")}</a>
                            </c:if>
                        </dd>
                    </dl>
                </div>
            </ul>
            </div>
</div>