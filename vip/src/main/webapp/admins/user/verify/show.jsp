<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>添加媒体</title>
		<jsp:include page="/admins/top.jsp" />
		<script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js"></script>
		<style type="text/css">
			.header {
				border-bottom: 1px #e2e2e2 solid;
			}
			
			.Toolbar {
				width: 800px;
			}
			
			.ClassifySelect {
				background: none;
			}
			
			.Login-tips {
				float: right;
				font-family: 微软雅黑;
				margin-top: 5px;
			}
			
			.Login-tips span {
				color: #666666;
				float: left;
				line-height: 32px;
				margin-right: 10px;
			}
			
			.Login-tips a {
				float: left;
				background: url(${static_domain }/statics/img/dl.png) no-repeat;
				width: 79px;
				height: 32px;
				line-height: 32px;
				text-align: center;
				color: #fff;
				text-decoration: none;
				font-size: 14px;
			}
			
			.Register-fill {
				border-top: 1px #fff solid;
				font-family: 微软雅黑;
			}
			
			.Register-con {
				margin: 0 auto;
				padding-top: 25px;
			}
			
			.form-line {
				overflow: hidden;
				zoom: 1;
				padding-bottom: 10px;
			}
			
			.form-tit {
				float: left;
				line-height: 28px;
				color: #666666;
				margin-right: 3px;
				width: 66px;
				text-align: right;
			}
			
			.form-con {
				float: left;
				line-height: 30px;
			}
			
			.form-tips {
				float: left;
				line-height: 40px;
				color: #999999;
				padding-left: 10px;
			}
			
			.form-con .txt {
				height: 40px;
				background: #fff;
				border: 1px #dbdbdb solid;
				border-left: 1px #cccccc solid;
				border-top: 1px #cccccc solid;
				width: 240px;
				padding: 0 5px;
			}
			
			.rules {
				padding: 5px 0 15px 214px;
				color: #999999;
			}
			
			.rules a {
				color: #999999;
			}
			
			.rules a:hover {
				color: #333333;
			}
			
			.rules label.checkbox {
				margin: 2px 3px 0 0;
			}
			
			.submit {
				height: 38px;
				padding-left: 214px;
			}
			
			.submit a {
				background: url(${static_domain }/statics/img/zc.png) no-repeat;
				width: 242px;
				height: 38px;
				color: #fff;
				text-align: center;
				line-height: 38px;
				font-size: 14px;
				text-decoration: none;
				display: block;
			}
			
			.color {
				color: red;
				font-wei
			}
			
			.form-con .jqTransformRadioWrapper {
				margin: 0px 0 5px 0px;
			}
			
			.main-bd {
				padding: 20px;
			}
			
			.pm-itemcont .item .preview a {
				font-size: 14px;
			}
			label.checked,label.checkbox{margin: 7px 0 0;}
		</style>

		<script type="text/javascript">
			$(function(){
				$("#add_or_update").Ui();
				document.domain = "${baseDomain}";
			});
			
			function ok(){
				var actionUrl = "/admin/user/verify/doaoru";
				vip.ajax( {
					formId : "add_or_update",
					url : actionUrl,
					div : "add_or_update",
					dataType : "json",
					suc : function(xml) {
						parent.vip.list.reload();
						parent.Right(xml.des);
					}
				});
			}
		</script>
	</head>
	
	<body>
		<input type="hidden" value="${baseDomain}" id="curBaseDomain" />
		<div id="add_or_update" class="main-bd">
			<div class="form-line">
				<div class="form-tit" style="width:100px;">申请时间：</div>
				<div class="form-con">${info.addTimeShow }</div>
			</div>
			<div class="form-line">
				<div class="form-tit" style="width:100px;">用户名：</div>
				<div class="form-con">${info.userName }</div>
			</div>
			<div class="form-line">
				<div class="form-tit" style="width:100px;">申请操作：</div>
				<div class="form-con">
					<c:choose>
						<c:when test="${info.type eq 1 }">更改手机</c:when>
						<c:otherwise>更改Google</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit" style="width:100px;">申请修改：</div>
				<div class="form-con">
					<c:choose>
						<c:when test="${info.type eq 1 }">${info.info }</c:when>
						<c:otherwise>${info.infoShow }</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit" style="width:100px;">修改前：</div>
				<div class="form-con">
					<c:choose>
						<c:when test="${info.type eq 1 }">${info.beforeInfo }</c:when>
						<c:otherwise>${info.beforeInfoShow }</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit" style="width:100px;">状态：</div>
				<div class="form-con">
					<c:choose>
						<c:when test="${info.status eq 0 }">待审核</c:when>
						<c:when test="${info.status eq 1 }">已拒绝</c:when>
						<c:when test="${info.status eq 2 }">已通过</c:when>
						<c:when test="${info.status eq 3 }">已撤消</c:when>
					</c:choose>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit" style="width:100px;">备注：</div>
				<div class="form-con">
					${info.memo }
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit" style="width:100px;">审核时间：</div>
				<div class="form-con">
					${info.verifyTimeShow }
				</div>
			</div>
		</div>
	</body>
</html>
