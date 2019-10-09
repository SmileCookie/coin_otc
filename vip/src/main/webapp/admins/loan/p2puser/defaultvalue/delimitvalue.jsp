<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html; charset=UTF-8" language="java" import="java.sql.*" errorPage=""%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<jsp:include page="/admins/top.jsp" />

<style type="text/css">

/* 第一个div  Strat */
#divh #divinput {
	padding-left: 130px;
}
/* 第一个div End */

/* 输入框大小 Strat */
#divin #limn {
	width: 160px;
	height: 28px;
}

#divinput #divin {
	font-size: 18px;
	margin: 30px;
	clear: both;
}
/* 输入框大小 End */

#divin .limn {
	border-color: #6CF;
	max-width: 300px;
	max-height: 100px;
}
			
</style>
<script type="text/javascript">
	$(function() {
		$("#divh").Ui();
	})

	/* 确定提交	Start */
	function oklimits() {
		var okActionUrl = "/admin/loan/p2puser/defaultvalue/updateMoRenValue";
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
	/* 确定提交	End */

	/* 小数位验证	Start */
	function xswCheck(obj, max, type) {
		var m = max + 1;
		var iv = $(obj).val();
		if (iv.indexOf(".") > 0 && iv.substring(iv.indexOf(".")).length > m) {
			$(obj).val(iv.substring(0, iv.indexOf(".") + m));
		}
	}
	/* 小数位验证	End */
	/* 最小值 	Start */
	function min(s, value, id) {
		var textValue = parseFloat(s);
		if (textValue == 0 || textValue >= value) {
			return true;
		} else {
			return false;
		}
	}
	/* 最小值	End */
</script>
</head>
<body>
	<div id="divh">
		<input type="hidden" id="limn" name="ids" value="${param.ids }" />
		<input type="hidden" id="limn" name="typeName" value="${param.fundsType }" />
		<input type="hidden" id="limn" name="keyName" value="${param.fkey }" />
		
		<!-- value Strat -->
		<div id="divinput">
			<div id="divin">
				币种：<label id="limn">${dataList.keyName }</label>
			</div>
			
			<div id="divin">
				名称：<label id="limn">${dataList.typeName }</label>
			</div>
			
			<div id="divin">
				值： <input type="text" id="limn" name="valueName"
					value='<fmt:formatNumber value="${dataList.valueName }" pattern="0.00"/>'
					pattern="num()" errormsg="输入资金范围，只允许数字" onkeyup="xswCheck(this , 2 , 1)" />
			</div>

			<div id="divin">
				备注：<br>
				<textarea class="limn" name="reMark" rows="4" cols="36">${dataList.reMarks }</textarea>
			</div>

		</div>
		<!-- value End -->

		<div class="form-btn">
			<a class="btn" href="javascript:oklimits()"> <i class="left"></i>
				<span class="cont">确定</span> <i class="right"></i>
			</a>
			<a href="javascript:parent.Close()" class="btn btn-gray">
			<i class="left"></i> <span class="cont">取消</span> <i class="right"></i>
			</a>
		</div>
	</div>
</body>
</html>