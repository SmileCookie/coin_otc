<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html; charset=UTF-8" language="java"
	import="java.sql.*" errorPage=""%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<jsp:include page="/admins/top.jsp" />

<style type="text/css">

/* input最外围div  Strat */
#divhead #divinput {
	padding-left: 106px;
}
/* input最外围div End */

/* input Strat */
/* 输入框大小 */
#divin #lim {
	width: 160px;
	height: 28px;
	font-size: 16px;
}

#divinput #divin {
	font-size: 14px;
	margin: 10px;
	clear: both;
}

.user_span {
	float: left;
	padding-left: 12px;
	margin-bottom: 14px;
}

#userLend {
	width: 130px;
}

.divName {
	padding-left: 54px;
}
/* input End */
</style>

<script type="text/javascript">

	$(function() {
		$("#divhead").Ui();
	});
	//确定提交
	function okLend() {
		var okActionUrl = "/admin/loan/p2puser/upadelend";
		vip.ajax({
			formId : "divhead",
			url : okActionUrl,
			div : "divhead",
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
	//显示、隐藏
	function disNoed(value) {
		if (value == 1) {
			$('#divnone').show();
		} else {
			$('#divnone').hide();
		}
	}
	//最小值 
	function min(s, value, id) {
		var textValue = parseFloat(s);
		if (textValue == 0 || textValue >= value) {
			return true;
		} else {
			return false;
		}
	}

	function xswCheck(obj, max, type) {
		var m = max + 1;
		var iv = $(obj).val();
		if (iv.indexOf(".") > 0 && iv.substring(iv.indexOf(".")).length > m) {
			$(obj).val(iv.substring(0, iv.indexOf(".") + m));
		}
	}

</script>

</head>

<body>
	<div id="divhead">

		<!-- value Strat -->
		<div id="divinput">
			<div id="divin">
				<span class="user_span">放贷范围状态：</span> <select id="userLend"
					name="userLend" onchange="disNoed(this.value)">
					<option value="0"
						<c:if test="${curUser.userLend==0 }">selected="selected"</c:if>>默认范围</option>
					<option value="1"
						<c:if test="${curUser.userLend==1 }">selected="selected"</c:if>>设置范围</option>
				</select>
			</div>
			<div id="divin" class="divName">
				用户名：<label id="lim">${curUser.userName }</label>
			</div>
			<div id="divnone"
				style="display: ${curUser.userLend==1? 'block' : 'none'};">
				<!--modify by xwz 20170701-->
				<%--<div id="divin" style="color: red; margin-left: 84px;">--%>
					<%--(注：设置为0时,加载默认。)</div>--%>
				<c:if test="${payUserList !=null}">
					<c:forEach items="${payUserList}" var="payUser">
						<input type="hidden" name="${payUser.coint.propTag}FundsType" value="${payUser.fundsType}"  />
						<div id="divin">
							放贷范围[${payUser.coint.propTag}]：
							<input type="text" id="lim" name="${payUser.coint.propTag}Limit"
								value='<fmt:formatNumber value="${payUser.loanLimit }" pattern="0.00"/>'
								pattern="num();limit(0 , 10);min(10)"
								errormsg="输入资金范围,起步 ¥1000,只允许数字" />
						</div>

					</c:forEach>
				</c:if>
				<%-- <div id="divin">
					放贷范围[RMB]：<input type="text" id="lim" name="rmbLimit"
						value='<fmt:formatNumber value="${curUser.rmbLimit }" pattern="0.00"/>'
						pattern="num();limit(0 , 10);min(1000)"
						errormsg="输入资金范围,起步 ¥1000,只允许数字" />
				</div>
				<div id="divin">
					放贷范围[BTC]：  <input type="text" id="lim" name="btcLimit"
						value='<fmt:formatNumber value="${curUser.btcLimit }" pattern="0.00"/>'
						pattern="num();limit(0 , 10);min(10)"
						errormsg="输入资金范围,起步 ฿10,只允许数字" />
				</div>
				<div id="divin">
					放贷范围[LTC]：  <input type="text" id="lim" name="ltcLimit"
						value='<fmt:formatNumber value="${curUser.ltcLimit }" pattern="0.00"/>'
						pattern="num();limit(0 , 10);min(100)"
						errormsg="输入资金范围,起步 Ł100,只允许数字" />
				</div>
				<div id="divin">
					放贷范围[ETH]：  <input type="text" id="lim" name="ethLimit"
						value='<fmt:formatNumber value="${curUser.ethLimit }" pattern="0.00"/>'
						pattern="num();limit(0 , 10);min(100)"
						errormsg="输入资金范围,起步 E100,只允许数字" />
				</div>
				<div id="divin">
					放贷范围[ETC]：  <input type="text" id="lim" name="etcLimit"
						value='<fmt:formatNumber value="${curUser.etcLimit }" pattern="0.00"/>'
						pattern="num();limit(0 , 10);min(100)"
						errormsg="输入资金范围,起步 e100,只允许数字类型" />
				</div> --%>

			</div>
		</div>
		<!-- value End -->

		<div class="form-btn">
			<input type="hidden" id="userId" name="userId"
				value="${curUser.userId }" /> <a class="btn"
				href="javascript:okLend()"> <i class="left"></i> <span
				class="cont">确定</span> <i class="right"></i>
			</a> <a href="javascript:parent.Close()" class="btn btn-gray"> <i
				class="left"></i> <span class="cont">取消</span> <i class="right"></i>
			</a>
		</div>
	</div>
</body>
</html>