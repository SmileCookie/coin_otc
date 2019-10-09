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

function ok(){
	var actionUrl = "/admin/user/saveTuijian";
	vip.ajax( {
		formId : "add_or_update",
		url : actionUrl,
		div : "add_or_update",
		suc : function(xml) {
			parent.Right($(xml).find("MainData").text(), {callback:"reload2()"});
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
			<div class="form-tit">推荐人用户名：</div>
			<div class="form-con">
			 	<input class="txt" type="text" style="width: 200px;" name="userName" id="userName" value="${user.recommendName}" pattern="limit(0, 30)"/>
			</div>
		</div>

		<div class="form-line">
			<div class="form-tit">奖励推荐人：</div>
			<div class="form-con">
			 	<input class="txt" type="text" style="width: 200px;" name="reward" id="reward" value="${payUser.rewardPoint}" pattern="limit(0, 30)"/>%
			</div>
		</div>

		<div class="form-line">
			<div class="form-tit">奖励自己：</div>
			<div class="form-con">
			 	<input class="txt" type="text" style="width: 200px;" name="selfPoint" id="selfPoint" value="${payUser.selfPoint}" pattern="limit(0, 30)"/>%
			</div>
		</div>
		
		<div class="form-line">
	    	<div  class="form-tit">谷歌验证码:</div>
		    <div class="form-con">
		    	<input type="password" class="input" style="width:100px;" name="mCode" id="mCode" value="" mytitle="请输入移动设备上生成的验证码。" errormsg="验证码错误" pattern="limit(4,10)"/>
		    </div>
	    </div>
		
		<div class="form-btn">
			<input type="hidden" name="${coint.coinParam }" value="${coint.propTag }"  />
			<input type="hidden" id="userId" name="userId" value="${user.id}" />
			<a class="btn" href="javascript:ok();"><span class="cont">确定</span></a>
			<a href="javascript:parent.Close();" class="btn btn-gray"><span class="cont">取消</span></a>
		</div>
	</div>
</body>
</html>
