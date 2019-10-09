<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<jsp:include page="/admins/top.jsp" />
<script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>
<style type="text/css">
</style>
</head>
<body>
   <div id="add_or_update" class="main-bd">
      <div class="form-line">
         <div class="form-tit">转出用户名：</div>
         <div class="form-con">
            <input type="text" name="outUserName" id="outUserName" pattern="limit(3,30)" value="${userName }"/>
         </div>
      </div>
      <div class="form-line">
         <div class="form-tit">转入用户名：</div>
         <div class="form-con">
            <input type="text" name="inUserName" id="inUserName" pattern="limit(3,30)" value=""/>
         </div>
      </div>
      <div class="form-line">
         <div class="form-tit">扣除数量：</div>
         <div class="form-con">
            <input type="text" name="amount" id="amount" pattern="limit(1,15);num()" value="${amount }"/>
         </div>
      </div>
      <div class="form-line">
         <div class="form-tit">备注：</div>
         <div class="form-con">
            <textarea name="reason" id="reason" rows="3" cols="50"></textarea>
         </div>
      </div>
      <div class="form-line">
		<div class="form-tit" style="float:left;">谷歌验证码：</div>
		<div class="form-con">
			<input type="text" class="input" name="mCode" id="mCode" value="" pattern="limit(4,10)"/>
		</div>
	  </div>		
      <div class="form-btn">
         <a class="btn" href="javascript:ok();"><span class="cont">确定</span></a>
         <a href="javascript:parent.Close();" class="btn btn-gray"><span class="cont">取消</span></a>
      </div>
   </div>
<script type="text/javascript">
$(function(){
	$("#add_or_update").Ui();
});

function ok(){
    
	var actionUrl = "/admin/pay/user/DotransferFinance";
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
</body>
</html>
