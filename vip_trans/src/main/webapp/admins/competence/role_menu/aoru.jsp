<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
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
	
	function save(){
		Ask2({Msg : "确定要保存吗？", call:function(){
			var ids = '';
			$("input[name='has']").each(function(){
				if($(this).attr("checked")){
					ids += ',' + $(this).val();
				}
			});
			if(ids.length > 0){
				$("#ids").val(ids.substring(1));
			}
			vip.ajax({formId : "admin_user_update" , url : "/admin/competence/role_menu/doAoru" , div : "admin_user_update" , suc : function(xml){
		    	Right($(xml).find("Des").text());
			}});
		}});
	}
	</script>
	</head>
	<body>
		<div id="admin_user_update" class="main-bd">
			<div class="win-form-line top">
				请选择是否包含当前菜单：
			</div>
		
			<div class="win-form-line">
				<c:forEach items="${dataList}" var="data">
					<span class="group-input"><input type="checkbox" value="${data.id }" name="has" <c:if test="${data.inRole}">checked="checked"</c:if>/></span> <span class="group-name">${data.name}</span> 
				</c:forEach>
			</div>
			
			<div class="form-btn">
				<input id="id" name="id" type="hidden" value="${curData.id}"/>
				<input id="ids" name="ids" type="hidden" value=""/>
				<a class="btn" href="javascript:save();"><i class="left"></i><span class="cont">保存</span><i class="right"></i></a> 
				<a href="javascript:parent.Close();" class="btn btn-gray"><i class="left"></i><span class="cont">退出</span><i class="right"></i></a>
			</div>
		</div>

	</body>
</html>
