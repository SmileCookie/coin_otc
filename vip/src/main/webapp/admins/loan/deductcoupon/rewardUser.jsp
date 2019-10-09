<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
		<title>赠送抵扣券给用户</title>
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
			vip.ajax({
				formId : "form1" , 
				url : "/admin/loan/deductcoupon/creates" ,
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
		
		function xswCheck(obj , max , type){//小数位验证
			var m = max + 1;
			var iv = $(obj).val();
			if(iv.indexOf(".") > 0 && iv.substring(iv.indexOf(".")).length > m){
				$(obj).val(iv.substring(0 , iv.indexOf(".") + m));
			}
		}
		
</script>
	</head>
	<body>
		<div id="form1" class="main-bd">
			<div class="form-line">
				<div class="form-tit">赠送用户：</div>
				<div class="form-con">
				<input id="userName" name="userName" pattern="limit(0,20)" size="20"
					type="text" mytitle="请填写本次赠送抵扣券用户名" errormsg="请填写本次赠送抵扣券用户名" />
			</div>
			</div>
			
			<div class="form-line">
				<div class="form-tit">抵扣券标题：</div>
				<div class="form-con">
				<input id="title" name="title" value="系统赠送" pattern="limit(0,20)"
					size="20" type="text" mytitle="请填写本次抵扣券标题" errormsg="请填写本次抵扣券标题" />
			</div>
			</div>
			
			<div class="form-line">
				<div class="form-tit">抵扣券额度：</div>
				<div class="form-con">
				<input type="text" name="amountDeg" id="amountDeg" onkeyup="xswCheck(this ,8 , 1)" 
					pattern="num();limit(0,11)" class="dai_input" mytitle="请填写本次抵扣券额度" errormsg="抵扣券额度只能为数字"/>
				</div>
			</div>
				
			<div class="form-line">
				<div class="form-tit">过期时间：</div>
				<div class="form-con">
				<input id="endTime" name="endTime" size="20" type="text" 
					onfocus="WdatePicker({dateFmt:'yyyy-MM-dd 23:59:59',lang : 'cn'})" 
					mytitle="请填写本次抵扣券过期时间" errormsg="请填写本次抵扣券过期时间"/>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">获取途径：</div>
				<div class="form-con">
					<input type="radio" name="getWay" value="1"/>活动发放
					<input type="radio" name="getWay" value="2" checked/>系统赠送
				</div>
			</div>
			
			<div class="form-line">
				<div class="form-tit">抵扣券类型：</div>
				<div class="form-con">
					<input type="radio" name="couponType" value="1" checked/>抵扣券
					<!-- <input type="radio" name="couponType" value="2" />打折券 -->
					<input type="radio" name="couponType" value="3" />限额抵扣券
					<!-- <input type="radio" name="couponType" value="4" />限额打折券 -->
				</div>
			</div>
			
<!-- 			<div class="form-line"> -->
<!-- 				<div class="form-tit">抵扣币种：</div> -->
<!-- 				<div class="form-con"> -->
<!-- 				<select id="fundsType" name="fundsType" style="display: block; width: 160px; height: 30px;"> -->
<!-- 					<option value="0" >--请选择币种--</option> -->
<%-- 					<c:forEach items="${fundlist }" var="ft"> --%>
<%-- 					<option value="${ft.key }" >${ft.value }</option> --%>
<%-- 					</c:forEach> --%>
<!-- 				</select> -->
<!-- 				</div> -->
<!-- 			</div> -->
			
			<div class="form-line">
				<div class="form-tit">描述信息：</div>
				<div class="form-con">
				<textarea type="text" id="useCondition" name="useCondition"
					pattern="limit(0,50)" size="50" cols="30" rows="3"
					mytitle="可以添加一些描述性文字" errormsg="长度太长，不能超过50个字符">系统赠送
				</textarea>
			</div>
			</div>
			
			<div class="form-line">
				<div class="form-tit">生成数量：</div>
				<div class="form-con">
					<input type="text" name="count" id="count" pattern="limit(0,11)" class="dai_input"
						mytitle="请填写本次抵扣券生成数量" errormsg="请填写本次抵扣券生成数量"/>
				</div>
			</div>
			
<!-- 			<div class="form-line"> -->
<!-- 				<div class="form-tit"></div> -->
<!-- 				<div class="form-con"> -->
<!-- 					<input type="hidden" name="batchMark" id="batchMark" class="dai_input"/> -->
<!-- 				</div> -->
<!-- 			</div> -->
			<div class="form-btn" styler="text-align:center;">
				<a class="btn" href="javascript:save();"><span class="cont">确定</span></a>
				<a href="javascript:parent.Close();" class="btn btn-gray"><span class="cont">取消</span></a>
			</div>
		</div>
	</body>
</html>
