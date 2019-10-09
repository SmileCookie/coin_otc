<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<!doctype html>
<html>
<head>
    <title>
        ${L:l(lan,'选择') }
    </title>
    <jsp:include page="/common/head.jsp"/>
	<link rel="stylesheet" href="${static_domain }/statics/css/web.user.css?V${CH_VERSON }">
</head>

<body style="background-color:#fff;">
<div class="form-horizontal" autocomplete="off" style="padding:30px;">
    <c:choose>
        <c:when test="${category == 2}">
            <div class="form-group">
                <input type="password" class="form-control" name="safePwd" id="safePwd" placeholder="${L:l(lan,'资金密码') }"/>
            </div>
        </c:when>
        <c:otherwise>
            <div class="form-group mb30">
                <div class="input-group">
                	   <div class="fill-flex fill-flex2">
                      <input type="text" maxlength="6" class="form-control" name="mobileCode" id="mobileCode" placeholder="${L:l(lan, '短信/邮件验证码')}">
				      <a href="javascript:sendCode();" class="btn btn-primary wid80" id="sendCodeBtn" role="button">${L:l(lan,"获取")}</a>
                	   </div>
                	</div>
            </div>

            <c:if test="${hasGoogleAtuh}">
                <div class="form-group mb30">
                    <input type="text" class="form-control" name="googleCode" id="googleCode" placeholder="${L:l(lan,'Google验证码')}"/>
                </div>
            </c:if>
        </c:otherwise>
    </c:choose>

    <div class="form-group clearfix">
        <a href="javascript:save();" class="btn btn-lg btn-block">
            ${L:l(lan, "提交")}
        </a>
    </div>

    <input type="hidden" id="category" name="category" value="${category }"/>
    <input type="hidden" id="type" name="type" value="${type }"/>
</div>
<script type="text/javascript" charset="utf-8">
    try {
        var oldDomain = document.domain;
        var ind = oldDomain.indexOf('${baseDomain}');
        document.domain = oldDomain.substring(ind, oldDomain.length)
    } catch (msg) {
        document.domain = '${baseDomain}';
    }
</script>
<script type="text/javascript">
    $(function () {
    });

    function save() {
        <c:choose>
        <c:when test="${category == 2}">
        var safePwd = $("#safePwd").val();
        if (safePwd.trim().length == 0) {
            return JuaBox.sure('${L:l(lan, "请输入资金密码")}');
        }
        </c:when>
        <c:otherwise>
        var mobileCode = $("#mobileCode").val();
        if (mobileCode.length < 6) {
            return JuaBox.sure('${L:l(lan, "请输入6位短信/邮件验证码")}');
        }

        <c:if test="${hasGoogleAtuh}">
        var googleCode = $("#googleCode").val();
        if (googleCode.length < 6) {
            return JuaBox.sure('${L:l(lan, "请输入6位google验证码")}');
        }
        </c:if>
        </c:otherwise>
        </c:choose>

        $.ajax({
            type: "POST",
            url: "/manage/auth/changeAuth",
            data: {
            <c:choose>
                <c:when test="${category == 2}">
                    safePwd: safePwd,
                </c:when>
                <c:otherwise>
                    mobileCode: mobileCode,
                    <c:if test="${hasGoogleAtuh}">
                        googleCode: googleCode,
                    </c:if>
                </c:otherwise>
            </c:choose>
                category: $("#category").val(),
                type: $("#type").val()
            },
            dataType: "json",
            error: function () {
                JuaBox.sure("net error!");
            },
            success: function (json) {
                if (json.isSuc) {
                    JuaBox.closeAll(function () {
                        JuaBox.sure(json.des, {
                            btnFun1: function () {
                                window.top.location.reload();
                            }
                        });
                    });
                } else {
                    JuaBox.sure(json.des);
                }
            }
        });
    }

    function sendCode() {
        $.ajax({
            type: "POST",
            url: "/userSendCode",
            data: {
                codeType: 14
            },
            dataType: "json",
            error: function () {
                JuaBox.sure(jsLan[1]);
                inAjaxing = false;
            },
            success: function (json) {
                inAjaxing = false;
                if (json.isSuc) {
                    if (json.datas.isEmail) {
                        JuaBox.sure(json.des);
                    }
                    settime($('#sendCodeBtn'));
                } else if ('needMobileAuth' == json.des) {
                    JuaBox.sure(bitbank.L("您未进行手机认证，请先进行手机认证"));
                } else {
                    JuaBox.sure(json.des);
                }
            }
        });
    }

    var countdown = 60;
    function settime(obj) {
        if (countdown == 0) {
            $(obj).removeAttr("disabled");
            $(obj).html("${L:l(lan,'点击获取')}");
            countdown = 60;
        } else {
            $(obj).attr("disabled", true);
            $(obj).html("${L:l(lan,'已发送')}" + "(" + countdown + ")");
            countdown--;
            setTimeout(function () {
                settime($(obj))
            }, 1000)
        }
    }
</script>
</body>
</html>
