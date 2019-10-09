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
			
		});
		function ok() {
			var actionUrl = "/admin/level/doaoru";
			vip.ajax( {
				formId : "add_or_update",
				url : actionUrl,
				div : "add_or_update",
				suc : function(xml) {
					parent.Right($(xml).find("Des").text(), {callback:"reload2()"});
				}
			});
		}
		
		
		
		
	</script>
</head>
<body>
	<div id="add_or_update" class="main-bd">
		<input type="hidden" name="id" id="id" value="${userVipLevel.id}" />
		
		<div class="form-line">
			<div class="form-tit">用户等级：</div>
			<div class="form-con">
				<select name="vipRate" id="vipRate" pattern="true" errormsg="请选用户等级" >
	             	<option value="">--请选择--</option>
	             	<c:forEach items="${vipRatesType}" var="vipRate">
							<option value="${vipRate.id }"<c:if test="${vipRate.id==userVipLevel.vipRate }">selected</c:if> >${vipRate.name }</option>
					</c:forEach>
		         </select>
		         
			</div>
		</div>
		
		<div class="form-line">
			<div class="form-tit">所需积分：</div>
			<div class="form-con">
				<input type="text" id="jifen" name ="jifen" value="${userVipLevel.jifen}" pattern="limit(1,10);num()" errormsg="请输入所需积分"  />
			</div>
		</div>
		
		<div class="form-line">
			<div class="form-tit">费率折扣：</div>
			<div class="form-con">
				<input type="text" id="discount" name ="discount" value="${userVipLevel.discount}" pattern="limit(1,10);num()" errormsg="请输入费率折扣"/>%（0为免费）
			</div>
		</div>
		
		<div class="form-line">
			<div class="form-tit">备注：</div>
			<div class="form-con">
				<textarea rows="5" cols="40" id="memo" name ="memo">${userVipLevel.memo}</textarea>
			</div>
		</div>

		<div class="form-btn" style="padding: 15px 0 0 80px;">
			<a class="btn" href="javascript:ok();"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a>
			<a href="javascript:parent.Close();" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>
		</div>
	</div>
</body>
</html>
