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
			integTypeChange();
		});

		function ok() {
			var actionUrl = "/admin/level/rule/doaoru";
			vip.ajax( {
				formId : "add_or_update",
				url : actionUrl,
				div : "add_or_update",
				suc : function(xml) {
					parent.Right($(xml).find("Des").text(), {callback:"reload2()"});
				}
			});
		}
		function integTypeChange(obj) {
			var integType = $(obj).val();
			if(integType == 2){
				$("#periodDiv").show();
			}else{
				$("#periodDiv").hide();
			}

		}
		
		
		
		
	</script>
</head>
<body>
	<div id="add_or_update" class="main-bd">
		<input type="hidden" name="id" id="id" value="${integralRule.id}" />
		
		<div class="form-line">
			<div class="form-tit">序号：</div>
			<div class="form-con">
				<input type="text" id="seqNo" name ="seqNo" value="${integralRule.seqNo}" pattern="limit(1,10);num()" errormsg="请输入序号"  />
			</div>
		</div>
		
		<div class="form-line">
			<div class="form-tit">类型：</div>
			<div class="form-con">
				<input type="text" id="type" name ="type" value="${integralRule.type}" pattern="limit(2,200)" errormsg="请输入规则类型"  />
			</div>
		</div>
		<!--add by xwz 20170729-->
		<div class="form-line">
			<div class="form-tit">类型代码：</div>
			<div class="form-con">
				<input type="text" id="typeCode" name ="typeCode" value="${integralRule.typeCode}" pattern="limit(2,200)" errormsg="请输入规则类型代码"  />
			</div>
		</div>
		<div class="form-line">
			<div class="form-tit">积分：</div>
			<div class="form-con">
				<input type="text" id="score" name ="score" value="${integralRule.score}" pattern="limit(2,200);num()" errormsg="请输入积分"  />
			</div>
		</div>

		<div class="form-line">
			<div class="form-tit">积分类型：</div>
			<div class="form-con">

				<select id="integType" name="integType" style="width:100px;" onchange="integTypeChange(this)">
					<c:forEach items="${integType}" var="type">
						<option value="${type.key }">${type.value }</option>
					</c:forEach>
				</select>
				<input type="hidden" id="integTypeValueId" value="${integralRule.integType}">
				<script>
					var integType = $("#integTypeValueId").val();
					$($("#integType")[0]).find("option[value='" + integType + "']").attr("selected","selected");
				</script>
			</div>
		</div>

		<div class="form-line" id="periodDiv">
			<div class="form-tit">周期：</div>
			<div class="form-con">
				<input type="text" id="period" name ="period" value="${integralRule.period }" pattern="" errormsg="请输入积分周期"  />
			</div>
		</div>

		<!--end-->

		<div class="form-line">
			<div class="form-tit">规则：</div>
			<div class="form-con">
				<input type="text" id="rule" name ="rule" value="${integralRule.rule}" pattern="limit(2,200)" errormsg="请输入积分规则"/>
			</div>
		</div>
		
		<div class="form-line">
			<div class="form-tit">说明：</div>
			<div class="form-con">
				<textarea rows="5" cols="40" id="memo" name ="memo">${integralRule.memo}</textarea>
			</div>
		</div>

		<div class="form-btn" style="padding: 15px 0 0 80px;">
			<a class="btn" href="javascript:ok();"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a>
			<a href="javascript:parent.Close();" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>
		</div>
	</div>
</body>
</html>
