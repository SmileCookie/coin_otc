<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
		<title>快速赠送抵扣券给用户</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<jsp:include page="/admins/top.jsp" />
		<script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>

		<script type="text/javascript">
		$(function(){
		  $("#admin_user_update").Ui();
		});//结束body load部分

		function reload2(){
			parent.Close();
			parent.document.location.reload();
		}
		function save(){
			var se_id=${seid};
			vip.ajax({
				formId : "form1" , 
				url : "/admin/loan/deductcoupon/creates2?se_id=" + se_id,
				div : "form1" ,
				dataType : "json", 
				suc : function(json){
					if(json.isSuc){
						parent.Right("已成功赠送给用户", {callback:"reload2()"});
				    }else{
						parent.Wrong(json.des);
					}
				}
			});
		}
		
</script>
	</head>
	<body>
		<div id="form1" class="main-bd">
			
			<div class="form-line">
				<div class="form-tit">抵扣券额度：</div>
				<label class="lab1" style="font-size: 20px; color: red;"> ${ftype.tag } ${fundlist.amountDeg }</label>
			</div>
			
			<div class="form-line">
				<div class="form-tit">赠送用户：</div>
				<div class="form-con">
				<input id="userName" name="userName" pattern="limit(0,20)" size="20" type="text" mytitle="请填写本次赠送抵扣券用户名" errormsg="请填写本次赠送抵扣券用户名" />
			</div>
			</div>
			
			<div class="form-btn" styler="text-align:center;">
				<a class="btn" href="javascript:save();"><span class="cont">确定</span></a>
				<a href="javascript:parent.Close();" class="btn btn-gray"><span class="cont">取消</span></a>
			</div>
		</div>
	</body>
</html>
