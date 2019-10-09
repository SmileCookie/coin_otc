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
			var actionUrl = "/admin/iplimit/doaoru";
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
		
		<div class="form-line">
			<div class="form-tit">IP地址：</div>
			<div class="form-con">
				<textarea style="width:400px;height: 100px;" name="ip" mytitle="请输入IP地址，多个用空格分隔。"></textarea>
			</div>
		</div>
		<div class="form-line">
			<div class="form-tit">黑白设置：</div>
			<div class="form-con">
				<select name="type" id="type" pattern="true" errormsg="请选择类型。" style="width:140px;display: none;" selectid="select_24962645">
	             	<option value="">--请选择--</option>
	             	<option value="0" <c:if test="${type==0 }">selected="selected"</c:if>>白名单</option>
	             	<option value="1" <c:if test="${type==1 }">selected="selected"</c:if>>黑名单</option>
		         </select>
		         <div class="SelectGray" id="select_24962645"><span><i style="width: 111px;">全部</i></span></div>
			</div>
		</div>

		<div class="form-btn" style="padding: 15px 0 0 80px;">
			<a class="btn" href="javascript:ok();"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a>
			<a href="javascript:parent.Close();" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>
		</div>
	</div>
</body>
</html>
