<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>编辑API状态</title>
		<jsp:include page="/admins/top.jsp" />
		<style type="text/css">
			.form-tit{width: 130px;}
			label.checked{margin-top: 7px;}
			label.checkbox{margin-top: 7px;}
		</style>

		<script type="text/javascript">
			$(function(){
				$("#add_or_update").Ui();
			});
			
			function ok(){
				var actionUrl = "/admin/api/doaoru";
				vip.ajax( {
					formId : "add_or_update",
					url : actionUrl,
					div : "add_or_update",
					dataType : "json",
					suc : function(xml) {
						parent.Right(xml.des, {callback:"reload2()"});
					},
					err : function(xml){
						Wrong(xml.des);
					}
				});
			}
		</script>
	</head>
	
	<body>
		<div id="add_or_update" class="main-bd">
			<div class="form-line">
				<div class="form-tit" >用户名：</div>
				<div class="form-con">
					${record.userName }
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">激活状态：</div>
				<div class="form-con">
					<input type="radio" name="isAct" <c:if test="${record.isAct eq 1}">checked="checked"</c:if> value="1" id="isAct1"/>
					<label for="isAct">开启</label>
				</div>
				<div class="form-line">
					<input type="radio" name="isAct" <c:if test="${record.isAct eq 0}">checked="checked"</c:if> value="0" id="isAct0"/>
					<label for="isAct0">关闭</label>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">锁定状态：</div>
				<div class="form-con">
					<input type="radio" name="isLock" <c:if test="${record.isLock eq 1}">checked="checked"</c:if> value="1" id="isLock1"/>
					<label for="isLock">开启</label>
				</div>
				<div class="form-line">
					<input type="radio" name="isLock" <c:if test="${record.isLock eq 0}">checked="checked"</c:if> value="0" id="isLock0"/>
					<label for="isLock0">关闭</label>
				</div>
			</div>
			<div class="form-btn" style="padding: 15px 0 0 80px;">
				<input type="hidden" id="id" name="id" value="${record.id }"/>
				<a class="btn" href="javascript:ok();"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a> 
				<a href="javascript:parent.Close();" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>
			</div>
		</div>
	</body>
</html>
