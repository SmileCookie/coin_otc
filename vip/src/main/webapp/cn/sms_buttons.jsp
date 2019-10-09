<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script type="text/javascript" src="${static_domain }/statics/js/common/postCode.js?id=1"></script>

<button type="button" id="ajax_phone_get">${L:l(lan,"短信验证码")}</button>
<%-- <c:if test="${showAudioButton }"> --%>
<%-- <button type="button" id="ajax_audio_phone_get" style="display:none;">${L:l(lan,"语音验证码")}</button> --%>
<%-- </c:if> --%>
<!-- 发送验证码的类型 -->
<input type="hidden" id="type" name="type" value="${codeType}"/>
