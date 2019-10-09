<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<jsp:include page="/admins/top.jsp" />
		
		<script type="text/javascript">
			$(function() {
				$("#add_or_update").Ui();
				setDomain();
			});
			function ok() {
				var actionUrl = "/admin/user/authen/dobankcheck";
				vip.ajax( {
					formId : "add_or_update",
					url : actionUrl,
					div : "add_or_update",
					suc : function(xml) {
						$('#checkResult span').html($(xml).find("Des").text());
					}
				});
			}
		</script>
	</head>
	<body>
		<div id="add_or_update" class="main-bd">
			<div id="checkResult" class="form-line"><span style="text-align:center;display:block;width:100%;color:red;"></span></div>
			<div class="form-line">
				<div class="form-tit" style="width:150px;">持卡人姓名：</div>
				<div class="form-con">
					${data.realName}
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit" style="width:150px;">持卡人身份证号码：</div>
				<div class="form-con">
					${data.cardId}
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit" style="width:150px;">持卡人银行卡号：</div>
				<div class="form-con">
					${data.bankCard}
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit" style="width:150px;">银行预留手机号：</div>
				<div class="form-con">
					${data.bankTel}
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit" style="width:150px;">银行卡类别：</div>
				<div class="form-con">
					<c:choose>
						<c:when test="${data.bankCardType == 1}">借记卡（储蓄卡）</c:when>
						<c:when test="${data.bankCardType == 2}">贷记卡（信用卡）</c:when>
						<c:when test="${data.bankCardType == 0}">存折</c:when>
					</c:choose>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit" style="width:150px;">信用卡背面后三位数字：</div>
				<div class="form-con">
					${data.bankCvv2}
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit" style="width:150px;">信用卡有效期：</div>
				<div class="form-con">
					${data.bankExpiredate}
				</div>
			</div>

			<div class="form-btn" style="padding: 15px 0 0 80px;">
				<input id="id" name="id" type="hidden" value="${data.id}" />
				<a class="btn" href="javascript:ok();"><i class="left"></i><span class="cont">校验</span><i class="right"></i></a>
				<a href="javascript:parent.Close();" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>
			</div>
		</div>
	</body>
</html>
