<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>API白名单管理</title>
<jsp:include page="/admins/top.jsp" />
<link href="${static_domain }/statics/css/en/upload.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="${static_domain }/statics/js/common/upload.js"></script>
<script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script> 
<%--onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : '${lan}'})"--%>
<style type="text/css">
.header{ border-bottom:1px #e2e2e2 solid;}
.Toolbar{ width:800px;}
.ClassifySelect{ background:none;}
.Login-tips{ float:right; font-family:微软雅黑; margin-top:5px;}
.Login-tips span{ color:#666666; float:left;line-height:32px; margin-right:10px;}
.Login-tips a{ float:left; background:url(${static_domain }/statics/img/dl.png) no-repeat; width:79px; height:32px; line-height:32px; text-align:center; color:#fff; text-decoration:none; font-size:14px;}
.Register-fill{ border-top:1px #fff solid; font-family:微软雅黑; }
.Register-con{ margin:0 auto; padding-top:25px;}
.form-line{ overflow:hidden;zoom:1; padding-bottom:10px;}
.form-tit{ float:left; line-height:40px;color:#666666; margin-right:3px; width:66px; text-align:right;}
.form-con{ float:left; line-height:40px;}
.form-tips{ float:left; line-height:40px; color:#999999; padding-left:10px;}
.form-con .txt{ height:40px; background:#fff; border:1px #dbdbdb solid;border-left:1px #cccccc solid;border-top:1px #cccccc solid; width:240px; padding:0 5px;}
.rules{ padding:5px 0 15px 214px; color:#999999;}
.rules a{ color:#999999;}
.rules a:hover{ color:#333333;}
.rules label.checkbox{ margin:2px 3px 0 0;}

.submit{ height:38px; padding-left:214px;}
.submit a{ background:url(${static_domain }/statics/img/zc.png) no-repeat; width:242px; height:38px; color:#fff; text-align:center; line-height:38px; font-size:14px; text-decoration:none; display:block;}
.color{
color:red;
font-wei
}
.form-con .jqTransformRadioWrapper{
margin:5px 0 5px 5px; 
}

.main-bd{padding: 20px;}

.pm-itemcont .item .preview a {
    font-size: 14px;
}
</style>

	<script type="text/javascript">
	//document.domain = "${baseDomain}";
$(function(){
	$("#add_or_update").Ui();
 });

function ok(){
	var actionUrl = "/admin/user/iplist/doaoru";
	vip.ajax( {
		formId : "add_or_update",
		url : actionUrl,
		div : "add_or_update",
		suc : function(xml) {
			//alert($(xml).find("Des").text());
			parent.Right($(xml).find("Des").text(), {callback:"reload2()"});
		}
	});
}


</script>

</head>

<body>
<input type="hidden" value="${baseDomain}" id="curBaseDomain"/>
	<div id="add_or_update" class="main-bd">
		
		<div class="form-line">
			<div class="form-tit">ip：</div>
			<div class="form-con"> <input class="txt" type="text" style="width:200px;" name="ip" id="ip" mytitle="请填写ip地址。"
			  value="${whiteIp.ip }" /> </div>
			<div class="form-tips">请输入有效的ip地址</div>
		</div>
			
		<div class="form-line">
			<div class="form-tit">次数限制：</div>
			<div class="form-con">
				<input type="text" class="txt" style="width:200px;" name="limit" id ="limit"  pattern="limit(1, 100);num()" errmsg="请正确填写每分钟访问次数。" value="${whiteIp.limit}" /> 
			
			</div>
		</div>
		
		
		
		<input type="hidden" name="id" value="${whiteIp.id}"/>
		<div class="submit" style="padding-bottom: 10px;"><a id="doLoginSimple" href="javascript:ok();">保存</a></div>
	</div>
</body>
</html>
