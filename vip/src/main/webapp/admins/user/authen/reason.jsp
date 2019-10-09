<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>审核不通过填写原因页面</title>
   <jsp:include page="/admins/top.jsp" />

</head>
<body>
<div id="col_main">
	<input type="hidden" id="roleId" value="${roleId }"/>
	<div id="add_or_update" class="main-bd">
		
		<div class="form-line">
			<div class="form-tit">附加理由：</div>
			<div class="form-con">
				<input type="hidden" id="passType" name="passType" value="${passType}" />
				<textarea pattern="limit(6,8000)" mytitle="请输入审核的原因。" errmsg="字符长度应大于5小于500" style="width:350px;height:140px;" position="s" id="reason" name="reason" pattern="limit(0,500)"></textarea>
				<br/>
				<font color="#4488bd">【提示：</font><font color="#CC5600">* 填写审核不通过的原因</font><font color="#4488bd">】</font>
			</div>
		</div>
		<div class="form-line">
			<div class="form-tit">哪些通过了：</div>
			<div class="form-con">
				<input type="checkbox" name="infoPass" value="1"/>基本信息
				<input type="checkbox" name="idImgPass" value="1"/>身份证照片
				<input type="checkbox" name="bankPass" value="1"/>银行信息
				<input type="checkbox" name="proofAddressPass" value="1"/>住址证明
				<br/>
				<font color="#4488bd">【提示：</font><font color="#CC5600">* 勾选通过的信息,不通过不勾选</font><font color="#4488bd">】</font>
			</div>
		</div>
		
		<div class="form-btn">
			<input type="hidden" id="vid" name="vid" value="${beanId }"/>
			<a href="javascript:void(0)" id="do_submit" class="btn"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a> <a href="javascript:parent.Close();" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>
		</div>
	</div>
</div>

<script type="text/javascript">
$(function(){
	setDomain();

	$("#audit_No_Reason").UiText();
	$("#mySaveDes").UiSelect();
	$("#userSaveDes").UiSelect();
	
	/********审核不通过部分*********/
	$("#do_submit").bind("click",function(){
		var actionUrl = "/admin/user/authen/unpass";
		vip.ajax( {
			formId : "add_or_update",
			url : actionUrl,
			div : "add_or_update",
			dataType : "json",
			suc : function(json) {
				parent.Right(json.des, {callback:"reload2()"});
			}
		});
	});
});

</script>
</body>
</html>
