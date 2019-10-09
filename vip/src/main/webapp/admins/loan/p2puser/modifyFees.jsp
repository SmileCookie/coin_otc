<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<jsp:include page="/admins/top.jsp" />
<style type="text/css">

.form-tit {
float:left;
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
	changeStat("${payUserBean.withdrawRateStat}");
});

function changeStat(val) {
	if(val==1) {
		$("#div_withdrawRate").show();
	} else {
		$("#div_withdrawRate").hide();
	}
}

function ok(mCode) {
	var isSetFees = $("#isSetFees").val();
	var fees = $("#fees").val();
	if(isSetFees==1 && (fees==null || $.trim(isSetFees)=="" || !vip.tool.isFloat(fees))) {
		Wrong("请输入正确的费率！");
		return;
	}
	
	if(!couldPass){
		googleCode("ok", true);
		return;
	}
	couldPass = false;
	var actionUrl = "/admin/loan/p2puser/doModifyFees?mCode="+mCode;
	vip.ajax( {
		formId : "bankBox",
		url : actionUrl,
		div : "bankBox",
		suc : function(xml) {
			parent.Right($(xml).find("Des").text(), {
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
    	<div class="form-tit">服务费状态：</div>
    	<div class="form-con">
    		<select name="isSetFees" id="isSetFees" style="width:200px;" onchange="changeStat(this.value)"> 
				<option value="0" <c:if test="${user.isSetFees==0}">selected="selected"</c:if>>默认费率</option>
				<option value="1" <c:if test="${user.isSetFees==1}">selected="selected"</c:if>>设置费率</option>
            </select>&nbsp;&nbsp;&nbsp;&nbsp;
  		</div>
	</div>
	
	<div class="form-line">
	    <div class="form-tit">用户名：</div>
	    <div class="form-con">
	    	<input id="userName" name="userName" size="15" type="text" value="${user.userName }" readonly="readonly"/>
	  	</div>
	</div>	
 	<div id="div_withdrawRate" class="form-line" style="display: none">
	    <div class="form-tit">服务费率：</div>
	    <div class="form-con">
	    	<input id="fees" errormsg="请正确填写税率。" mytitle="请正确填写税率。" name="fees" pattern="limit(1,40);num()" 
	    	size="15" class="input" type="text" value="<fmt:formatNumber value="${fees}" pattern="0.00##"/>"/>
	  	</div>
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