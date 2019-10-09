<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<jsp:include page="/admins/top.jsp" />
		<style type="text/css">
			.form-tit{
				width : 120px;
			}
		</style>
		<script type="text/javascript">
			$(function() {
				$("#add_or_update").Ui();
				setDomain();
			});
			function ok() {
				if(!couldPass){
					googleCode("ok", true);
					return;
				}
				couldPass = false;
				var actionUrl = "/admin/user/authen/doSetAuthName";
				vip.ajax( {
					formId : "add_or_update",
					url : actionUrl,
					div : "add_or_update",
					suc : function(xml) {
						parent.Right($(xml).find("Des").text(), {callback:"reload2()"});
					}
				});
			}
		</script>
	</head>
	<body>
		<div id="add_or_update" class="main-bd">
			
			<div class="form-line">
				<div class="form-tit">用户名：</div>
				<div class="form-con">
					${user.userName}
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">身份证件所在地区：</div>
				<div class="form-con">
					<select id="areaInfo" name="areaInfo">
						<option value="1" <c:if test="${auth.areaInfo==1}">selected="selected"</c:if>>大陆地区</option>
						<option value="2" <c:if test="${auth.areaInfo==2}">selected="selected"</c:if>>港澳地区</option>
						<option value="3" <c:if test="${auth.areaInfo==3}">selected="selected"</c:if>>台湾地区</option>
						<option value="4" <c:if test="${auth.areaInfo==4}">selected="selected"</c:if>>海外地区</option>
					</select>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">真实姓名：</div>
				<div class="form-con">
					<input type="text" name="realName" mytitle="请输入真实姓名。" value="${auth.realName}"/>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">证件号：</div>
				<div class="form-con">
					<input type="text" name="idCard" mytitle="请输入证件号。" value="${auth.cardId}"/>
				</div>
			</div>

			<div class="form-btn" style="padding: 15px 0 0 80px;">
				<input id="id" name="id" type="hidden" value="${user.id}" />
				<a class="btn" href="javascript:ok();"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a>
				<a href="javascript:parent.Close();" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>
			</div>
		</div>
	</body>
</html>
