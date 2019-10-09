<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<link href="${static_domain }/statics/css/admin/global.css" rel="stylesheet" type="text/css"/>
<link href="${static_domain }/statics/css/admin/control.css" rel="stylesheet" type="text/css" /> 
<script type="text/javascript" src="${static_domain }/statics/js/admin/jquery.js"></script>
<script type="text/javascript" src="${static_domain }/statics/js/admin/global.js"></script>

<script type="text/javascript">
$(function(){
  $("#admin_user_update").Ui();
});//结束body load部分

function save(){
	vip.ajax({formId : "admin_user_update" , url : "/admin/competence/plate_data/doAoru" , div : "admin_user_update" , suc : function(xml){
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
					路径：
				</div>
				<div class="form-con">
					<select name="path">
						<c:forEach items="${groups}" var="group">
							<option value="${group.key}">${group.key}</option>
						</c:forEach>
					</select>
				</div>
			</div>
			
			<div class="form-line">
				<div class="form-tit">
					ID：
				</div>
				<div class="form-con">
					<input
						id="dataId" mytitle="请填写数据ID" name="dataId"
						pattern="limit(0,50)" size="20" type="text" value="${curData.dataDes}"
						valueDemo="数据ID"/>
				</div>
			</div>
		
			<div class="form-line">
				<div class="form-tit">
					名称：
				</div>
				<div class="form-con">
					<input
						id="dataDes" name="dataDes"
						pattern="limit(0,50)" size="20" type="text" value="${curData.dataDes}"
						valueDemo="数据描述"/>
				</div>
			</div>
			
			<div class="form-line">
				<div class="form-con">
					<input id="id" name="id" type="hidden" value="${curData.id}"/>
					<input id="mid" name="mid" type="hidden" value="${mid}"/>
				</div>
			</div>
			
			<div class="form-btn">
				<a class="btn" href="javascript:save();"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a> <!--<a href="#" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>-->
			</div>
		</div>

	</body>
</html>
