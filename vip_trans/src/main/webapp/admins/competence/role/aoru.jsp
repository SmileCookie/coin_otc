<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>更新角色</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<link href="${static_domain }/statics/css/admin/global.css" rel="stylesheet" type="text/css"/>
<link href="${static_domain }/statics/css/admin/control.css" rel="stylesheet" type="text/css" /> 
<script type="text/javascript" src="${static_domain }/statics/js/admin/jquery.js"></script>
<script type="text/javascript" src="${static_domain }/statics/js/admin/global.js"></script>
		<script type="text/javascript" src="/body/js/YicuiUpload.js"></script>
<link href="/css/upload.css" rel="stylesheet" type="text/css" />

		<script type="text/javascript">
$(function(){
  $("#admin_user_update").Ui();
});//结束body load部分

function save(){
	vip.ajax({formId : "admin_user_update" , url : "/admin/competence/role/doAoru" , div : "admin_user_update" , suc : function(xml){
		parent.vip.list.reload();
	    parent.Right($(xml).find("Des").text());
	}});
}
</script>
	</head>
	<body>
		<div id="admin_user_update" class="main-bd">
			<div class="form-line">
				<div class="form-tit">
					名称：
				</div>
				<div class="form-con">
					<input
						errormsg="请检查字段RoleName长度小于50的字符串(每个中文算两个字符),注意,本字段功能如下: 角色名称"
						id="roleName" mytitle="RoleName要求填写一个长度小于50的字符串" name="roleName"
						pattern="limit(0,50)" size="20" type="text" value="${curData.roleName}"
						valueDemo="例：参数例子"/>
				</div>
			</div>
			
			<div class="form-line">
				<div class="form-tit">
					备注：
				</div>
				<div class="form-con">
						<textarea errormsg="长度太长，不能超过50个字符"
							id="des" mytitle="可以添加一些描述性文字" name="des"
							pattern="limit(0,50)" size="50" type="text" valueDemo="例：参数例子" cols="30" rows="3">${curData.des}</textarea>
				</div>
				
			</div>
			
			
			<div class="form-line">
				<div class="form-con">
					<input id="id" name="id" type="hidden" value="${curData.id}"/>
				</div>
			</div>
			
			<div class="form-btn">
				<a class="btn" href="javascript:save();"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a> <!--<a href="#" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>-->
			</div>
		</div>

	</body>
</html>
