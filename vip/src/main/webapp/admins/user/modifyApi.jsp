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

<script type="text/javascript">
$(function(){
	$("#add_or_update").Ui();
});

function ok(mCode){
	if(!couldPass){
		googleCode("ok", true);
		return;
	}
	couldPass = false;
	var actionUrl = "/admin/user/doModifyApi?mCode="+mCode;
	vip.ajax( {
		formId : "add_or_update",
		url : actionUrl,
		div : "add_or_update",
		suc : function(xml) {
			parent.Right($(xml).find("Des").text(), {callback:"reload2()"});
		}
	});
}

function apikey(){
	var actionUrl = "/admin/user/apiKey";
	vip.ajax( {
		url : actionUrl,
		suc : function(xml) {
			if($(xml).find("State").text() == "true"){
				var json  = eval("(" +$(xml).find("Des").text() +")");
				$("#accesskey").val(json.accesskey);
				$("#secretkey").val(json.secret);
				$("#showAccesskey").text(json.accesskey);
				$("#showSecretkey").text(json.secretkey);
				$("#showDiv").show();
			}
		}
	});
}
</script>
<style type="text/css">
.form-tit{ width:100px;float:left;}
input{padding:8px 5px;}
.main-bd {
    padding: 30px;
}
</style>
</head>

<body>
	<div id="add_or_update" class="main-bd">
		<div class="form-line">
			<div class="form-tit">用户名：</div>
			<div class="form-con">${user.userName }</div>
		</div>
		<div class="form-line">
			<div class="form-tit">Api访问公钥：</div>
			<div class="form-con"><input type="text" name="accesskey" id="accesskey" style="width:300px;" value="${user.apiKey }"/></div>
		</div>
		<div class="form-line">
			<div class="form-tit">Api访问密钥：</div>
			<div class="form-con"><input type="text" name="secretkey" id="secretkey" style="width:300px;" value="${user.apiSecret }"/></div>
		</div>
		<div class="form-line">
			<div class="form-tit">Api状态：</div>
			<div class="form-con">
				<select id="status" name="status">
					<option value="2" <c:if test="${user.apiStatus eq 2 }">selected</c:if>>已开启</option>
					<option value="1" <c:if test="${user.apiStatus eq 1 }">selected</c:if>>已关闭</option>
				</select>
			</div>
		</div>
		<div class="form-line" id="showDiv" style="display:none;">
			<div class="form-tit">复制使用：</div>
			<div class="form-con">
				<p id="showAccesskey">-</p>
				<p id="showSecretkey">-</p>
			</div>
		</div>
		<div class="form-btn">
			<input type="hidden" id="userId" name="userId" value="${user.userId }"/>
			<a class="btn" href="javascript:apikey();"><span class="cont">更换Api密钥</span></a>
			<a class="btn" href="javascript:ok();"><span class="cont">确定</span></a>
			<a href="javascript:parent.Close();" class="btn btn-gray"><span class="cont">取消</span></a>
		</div>
	</div>
</body>
</html>
