<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<jsp:include page="/admins/top.jsp" />
<style type="text/css">
input{padding:8px 5px;}
.form-con .txt{float:left;margin:0 5px 0 3px;}
</style>
</head>
<body>
   <div id="add_or_update" class="main-bd">
      
      <div class="form-line">
         <div class="form-tit">申请额度：</div>
         <div class="form-con">
            ${fc.freeCash }
         </div>
      </div>
      <div class="form-line">
         <div class="form-tit">修改额度：</div>
         <div class="form-con">
         	<fmt:formatNumber var="freeC" value="${fc.freeCash }" pattern="0.00###"/>
            <input class="txt" style="width: 100px" type="text" value="${freeC }" name="freeCash" id="freeCash" mytitle="请输入您要修改的额度。" pattern="limit(1, 10);num()" errormsg="请输入正确的额度。"/>
         </div>
      </div>
      <div class="form-line">
	    	<div  class="form-tit">谷歌验证码:</div>
		    <div class="form-con">
		    	<input type="password" class="input" style="width:100px;" name="mCode" id="mCode" value="" mytitle="请输入移动设备上生成的验证码。" errormsg="验证码错误" pattern="limit(4,10)"/>
		    </div>
	  </div>
      <div class="form-btn">
      	 <input type="hidden" name="${coint.coinParam }" value="${coint.tag }"  />
      	 <input type="hidden" id="vid" name="vid" value="${fc.id }"/>
         <a class="btn" href="javascript:ok();"><span class="cont">修改并通过</span></a>
      </div>
   </div>
<script type="text/javascript">
$(function(){
	$("#add_or_update").Ui();
});

function ok(id){
	var actionUrl = "/admin/btc/freecash/pass";
	vip.ajax( {
		formId : "add_or_update",
		url : actionUrl,
		div : "add_or_update",
		dataType : "json",
		suc : function(json) {
			parent.Right(json.des, {callback:"reload2()"});
		}
	});
}
</script>                
</body>
</html>
