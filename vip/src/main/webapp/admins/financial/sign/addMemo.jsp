<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
<jsp:include page="/admins/top.jsp" />
<style type="text/css">
.pm-itemcont .item .preview a {
    font-size: 13px;
}
</style>

<script type="text/javascript">
$(function(){
	$("#add_or_update").Ui();
	//$("#photoA").initFileUpload({initShowNum:1 , needAdd : false , isProcess : false, pics : "", isAuth : true, userType : 2, savePicSize : false , picsPathHiddenName : "loadImg", isPicFile : true});
});

function ok(){
	var actionUrl = "/admin/financial/sign/saveMemo";
	vip.ajax({
		formId : "add_or_update",
		url : actionUrl,
		div : "add_or_update",
		suc : function(xml) {
			parent.Right($(xml).find("Des").text(), {callback:"reload2()"});
		}
	});
}

</script>
<style type="text/css">
input{padding:8px 5px;}
.main-bd {
    padding: 30px;
}
</style>
</head>

<body>
	<div id="add_or_update" class="main-bd">
		<input type="hidden" name="id" value="${curData.id }"/>
		<div class="form-line">
			<div class="form-tit">备注信息：</div>
			<div class="form-con">
			 	<textarea name="memo" rows="3" cols="50">${curData.memo }</textarea>
			</div>
		</div>
		<div class="form-btn">
			<a class="btn" href="javascript:ok();"><span class="cont">确定</span></a>
			<a href="javascript:parent.Close();" class="btn btn-gray"><span class="cont">取消</span></a>
		</div>
	</div>
</body>
</html>
