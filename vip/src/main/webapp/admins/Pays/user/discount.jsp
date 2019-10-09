<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>设置折扣用户</title>
  <jsp:include page="/admins/top.jsp" />
<style type="text/css">

.form-tit {
float:left;
width:100px;
line-height:32px;
padding-right: 10px;
text-align: right;
}

.form-con{float:left;}

.form-line {margin-left:20px;}
.col-main .prompt.b_yellow.remind2.pl_35 {
	padding:0 10px 20px 10px;
}
.form-btn {
    padding: 15px 0 0 100px;
}
.select_wrap select {
    color: #6D6D6D;
    float: left;
 
    padding: 5px;
}

.bankbox{ padding:15px;}
.bankbox .bd {
	padding-right: 20px;
	padding-left: 20px;
}
.formlist .formline {
	overflow:hidden;
	padding-bottom:8px;
	clear: both;
}
.formlist .formtxt {
float:left;
line-height:24px;
text-align:right;
width:130px;
}
.formlist .formcon {
	overflow:hidden;
	font-weight: bold;
	font-size: 12px;
}
.formlist span.tips {
	color:#666666;
	float:left;
	line-height:24px;
	white-space:nowrap;
	font-size: 12px;
	font-weight: normal;
}
.formbtn {
	height:44px;
	clear: both;
	padding: 15px 0;
}

.formbtn a:hover {
	
	background-position:0 -33px;
	
}
select{ display:inline;}
.form-btn{ padding-left:96px;}

.form-line {
    line-height: 30px;
  
    }
.form-line{ overflow:hidden; clear:none; float:none;}
.form-con{ float:none; overflow:hidden;zoom:1;}
span.txt{float:left;margin-right: 5px;}
.form-con font{font-family: inherit;font-weight: bold;font-size: 16px;}
</style>

<script type="text/javascript">
$(function(){
	$("#bankBox").Ui();
});

function changeStat(val) {
	if(val==1) {
		$("#div_withdrawRate").show();
	} else {
		$("#div_withdrawRate").hide();
	}
}

function ok() {
	var actionUrl = "/admin/pay/user/doSetFees";
	vip.ajax( {
		formId : "bankBox",
		url : actionUrl,
		div : "bankBox",
		dataType : "json",
		suc : function(json) {
			parent.Right(json.des, {
				callback : "reload2()"
			});
		}
	});
}
</script>
</head>

<body >
<div class="bankbox" id="bankBox">

<div class="bankbox_bd">

	<div class="form-line">
	    <div class="form-tit">折扣给用户名：</div>
	    <div class="form-con">
	    	<input id="disUserName" name="disUserName" size="15" type="text" value="${user.disUserName }"/>
	  	</div>
	</div>	
 	<div class="form-line">
	    <div class="form-tit">收益折扣：</div>
	    <div class="form-con">
	    	<input id="discount" errormsg="请正确填写税率。" mytitle="请正确填写税率。" name="discount" pattern="limit(1,40);num()" size="15" class="input" type="text" value="${user.discount}"/>
	  		[0.96]
	  	</div>
	</div>
 	<div class="form-line">
		<div class="form-tit">Google验证码：</div>
		<div class="form-con"><input type="text" value="" style="width:200px;" pattern="limit(6,6)" errormsg="请输入正确的google验证码" name="mCode" id="mCode" /></div>
	</div>

   <div class="form-btn">
      <input type="hidden" name="userId" value="${user.userId}"/>
      <a class="btn" href="javascript:ok();"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a> 
      <a href="javascript:parent.Close()" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>
    </div>  	
</div>
</div>

</body>
</html>
