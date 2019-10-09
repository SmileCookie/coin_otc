<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>修改客户端设置</title>
		<jsp:include page="/admins/top.jsp" />
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
				width: 106px;
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
		</style>

		<script type="text/javascript">
			$(function(){
				$("#add_or_update").Ui();
				document.domain = "${baseDomain}";
			});
		
			document.domain = "${baseDomain}";
			function ok(){
				var actionUrl = "/admin/app/appsetup/doaoru";
				vip.ajax( {
					formId : "add_or_update",
					url : actionUrl,
					div : "add_or_update",
					suc : function(xml) {
						parent.vip.list.reload();
						parent.Right($(xml).find("MainData").text());
					}
				});
			}
		</script>
	</head>
	
	<body>
		<input type="hidden" value="${baseDomain}" id="curBaseDomain" />
		<div id="add_or_update" class="main-bd">
			<div class="form-line">
				<div class="form-tit">提现手续费：</div>
				<div class="form-con">
					<input type="radio" value="0" style="width:200px;" name="isFreeFee" <c:if test="${empty curData.freeFee or !curData.freeFee}">checked="checked"</c:if> />收费
					<br/>
					<input type="radio" value="1" style="width:200px;" name="isFreeFee" <c:if test="${curData.freeFee}">checked="checked"</c:if>/>免费
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">地区版本号：</div>
				<div class="form-con">
					<input type="text" name="areaVersion" value="${curData.areaVersion}" pattern="limit(1,3)" style="width:200px;"/>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">充值银行版本号：</div>
				<div class="form-con">
					<input type="text" name="rechargeBankVersion" value="${curData.rechargeBankVersion}" pattern="limit(1,3)" style="width:200px;"/>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">国家信息版本号：</div>
				<div class="form-con">
					<input type="text" name="countryInfoVersion" value="${curData.countryInfoVersion}" pattern="limit(1,3)" style="width:200px;"/>
				</div>
			</div>
			<div class="form-btn" style="padding: 15px 0 30px 80px;">
				<input id="id" name="id" type="hidden" value="${curData.id}" /> 
				<a class="btn" href="javascript:ok();"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a> 
				<a href="javascript:parent.Close();" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>
			</div>
		</div>
	</body>
</html>
