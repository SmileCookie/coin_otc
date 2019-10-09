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
				
			});
			function ok() {
				
				var actionUrl = "/admin/countrym/doaoru";
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
				<div class="form-tit">国家名称：</div>
				<div class="form-con">
					<input type="text" name="name" mytitle="请输入国家的名称。" value="${country.name}"/>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">简称：</div>
				<div class="form-con">
					<input type="text" name="des" mytitle="请输入简称。" value="${country.des}"/>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">编码：</div>
				<div class="form-con">
					<input type="text" name="code" mytitle="请输入编码。" value="${country.code}"/>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">位置：</div>
				<div class="form-con">
					<input type="text" name="position" mytitle="请输入位置。" value="${country.position}"/>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">顺序值：</div>
				<div class="form-con">
					<input type="text" name="seq" mytitle="请输入从1开始的数字顺序值。" value="${country.seq}"/>
				</div>
			</div>

			<div class="form-btn" style="padding: 15px 0 0 80px;">
				<input id="id" name="id" type="hidden" value="${country.id}" />
				<a class="btn" href="javascript:ok();"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a>
				<a href="javascript:parent.Close();" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>
			</div>
		</div>
	</body>
</html>
