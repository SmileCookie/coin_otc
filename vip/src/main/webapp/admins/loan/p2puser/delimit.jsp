<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html; charset=UTF-8" language="java" import="java.sql.*" errorPage=""%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<jsp:include page="/admins/top.jsp" />

<style type="text/css">

/* 第一个div  Strat */
#divh #divinput {
	padding-right: 106px;
}
/* input最外围div End */

/* 输入框大小 Strat */
#divinput .divin {
	font-size: 14px;
	margin: 10px;
	clear: both;
}

.divin .limn {
	width: 160px;
	height: 28px;
	font-size: 16px;
}
/* 输入框大小 End */
</style>

<script type="text/javascript">

	$(function() {
		$("#divh").Ui();
	});
	//确定提交
	function oklimits() {
		var okActionUrl = "/admin/loan/p2puser/updateMoRen";
		vip.ajax({
			formId : "divh",
			url : okActionUrl,
			div : "divh",
			dataType : "json",
			suc : function(json) {
				parent.Right(json.des, {
					callback : "reload2()"
				});
				
			},
			err : function(json) {
				Wrong(json.des);
			}
		});
	};

	//最小值 
	function min(s, value, id) {
		var textValue = parseFloat(s);
		if (textValue == 0 || textValue >= value) {
			return true;
		} else {
			return false;
		}
	}

	function xswCheck(obj, max, type) {//小数位验证
		var m = max + 1;
		var iv = $(obj).val();
		if (iv.indexOf(".") > 0 && iv.substring(iv.indexOf(".")).length > m) {
			$(obj).val(iv.substring(0, iv.indexOf(".") + m));
		}
	}
	
</script>

</head>
<body>
	<div id="divh">

		<!-- value Strat -->
		<div id="divinput">
					<span style="float: left; width: 150px; padding-left: 202px; margin:12px; font-size: 18px; color: red;">修改默认配置</span>
			</div>
			<input type="hidden" class="limn" name="typeName" value="${typeName }" />
			<div id="divinput">
			<c:if test="${defaultLimitList != null }">
				<c:forEach items="${defaultLimitList }" var="defaultLimit">
					<div class="divin">
						<input type="hidden" class="limn" name="${defaultLimit.keyName }id" value="${defaultLimit.id }" />
						<input type="hidden" class="limn" name="${defaultLimit.keyName }key" value="${defaultLimit.keyName }" />
						<span style="float: left; width: 60px; padding-left: 148px; margin-top: 3px;">[${defaultLimit.keyName }]：</span>
						<input type="text" class="limn" name="${defaultLimit.keyName }Value"
						value='<fmt:formatNumber value="${defaultLimit.valueName }" pattern="0.##"/>'
						pattern="num();limit(0 , 10)" errormsg="输入资金范围,只允许整数"
						onkeyup="xswCheck(this , -1 , 1)" />
					</div>
				</c:forEach>
			
			</c:if>
			
			<%-- <div class="divin">
					<input type="hidden" class="limn" name="btcid" value="${btcid }" />
					<input type="hidden" class="limn" name="btckey" value="${btc }" />
					<span style="float: left; width: 60px; padding-left: 148px; margin-top: 3px;">[${btc } ]：</span>
					<input type="text" class="limn" name="btcValue"
					value='<fmt:formatNumber value="${btcValue }" pattern="0.##"/>'
					pattern="num();limit(0 , 10)" errormsg="输入资金范围,只允许整数"
					onkeyup="xswCheck(this , -1 , 1)" />
			</div>
			<div class="divin">
					<input type="hidden" class="limn" name="ltcid" value="${ltcid }" />
					<input type="hidden" class="limn" name="ltcType" value="${ltcType }" />
					<input type="hidden" class="limn" name="ltckey" value="${ltc }" />
					<span style="float: left; width: 60px; padding-left: 148px; margin-top: 3px;">[${ltc }  ]：</span>
					<input type="text" class="limn" name="ltcValue"
					value='<fmt:formatNumber value="${ltcValue }" pattern="0.##"/>'
					pattern="num();limit(0 , 10)" errormsg="输入资金范围,只允许整数"
					onkeyup="xswCheck(this , -1 , 1)" />
			</div>
			<div class="divin">
					<input type="hidden" class="limn" name="ethid" value="${ethid }" />
					<input type="hidden" class="limn" name="ethType" value="${ethType }" />
						<input type="hidden" class="limn" name="ethkey" value="${eth }" />
					<span style="float: left; width: 60px; padding-left: 148px; margin-top: 3px;">[${eth } ]：</span>
					<input type="text" class="limn" name="ethValue"
					value='<fmt:formatNumber value="${ethValue }" pattern="0.##"/>'
					pattern="num();limit(0 , 10)" errormsg="输入资金范围,只允许整数"
					onkeyup="xswCheck(this , -1 , 1)" />
			</div>
			<div class="divin">
					<input type="hidden" class="limn" name="etcid" value="${etcid }" />
					<input type="hidden" class="limn" name="etcType" value="${etcType }" />
					<input type="hidden" class="limn" name="etckey" value="${etc }" />
					<span style="float: left; width: 60px; padding-left: 148px; margin-top: 3px;">[${etc } ]：</span>
					<input type="text" class="limn" name="etcValue"
					value='<fmt:formatNumber value="${etcValue }" pattern="0.##"/>'
					pattern="num();limit(0 , 10)" errormsg="输入资金范围,只允许整数"
					onkeyup="xswCheck(this , -1 , 1)" />
			</div> --%>
		</div>
		<!-- value End -->

		<div class="form-btn">
			<a class="btn" href="javascript:oklimits()"><i class="left"></i>
				<span class="cont">确定</span> <i class="right"></i>
			</a>
			<a href="javascript:parent.Close()" class="btn btn-gray">
			<i class="left"></i> <span class="cont">取消</span><i class="right"></i>
			</a>
		</div>
	</div>
</body>
</html>