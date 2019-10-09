<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>

<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>${L:l(lan,"资金安全密码身份验证")}</title>
<jsp:include page="/common/head.jsp" />
<style type="text/css">
.privilege .tb-list2 b { color:#f00;}
.privilege .tb-list2 th {border:1px solid #f3da91; background-color:#fffbef ; text-align:right; height:28px; line-height:28px; padding-right:15px;}
.privilege .tb-list2 td {border:1px solid #f3da91; background-color:#fff; text-align:left; height:28px; line-height:28px; padding-left:15px;}
.privilege .do a { width:140px;}
.privilege .do a h4 { width:125px;}
</style>
</head>

<body>
<!-- 头部 -->
<div class="main-bd" style="margin:10px; padding:10px; border:none;">
  <div class="mention">
    <div class="privilege">
	<div class="ctips"><p><span>${L:l(lan,"温馨提示")}：</span>${L:l(lan,"取消比特币转出需要您输入资金安全密码")} </p></div>
     <div class="clear" style="height:20px;"></div>

	  <div class="form-line" id="hideDiv1" style="padding-left: 20px;">
		 <div class="form-tit">${L:l(lan,"资金安全密码")}：</div>
		 <div class="form-con">
			<input type="password" class="input" style="width:200px;" name="safePwd" id="safePwd" value="" position="s" mytitle="${L:l(lan,'请输入发送到您资金安全密码')}" errormsg="${L:l(lan,'资金安全密码错误')}" errorName="${L:l(lan,'资金安全密码')}"/>
		 </div>
	  </div>
      <div id="showDiv" class="cardkey">
	   </div>
      <div class="do">
	      <a role="button" onclick="window.top.JuaBox.closeAll();" class="alibtn_orange35">${L:l(lan,"返 回")}</a>
	      <a role="button" onclick="confirmCancel();" class="alibtn_orange35" style="margin-left:36px;">${L:l(lan,"确 定")}</a>
      </div>
    </div>
  </div>
</div>
<!--页面中部内容结束-->
<script type="text/javascript">
	document.domain = "vip.com";
	function confirmCancel(did){
		var actionUrl = vip.vipDomain + "/u/btc/downrecord/confirmCancel?did="+${did}+"&safePwd="+encodeURIComponent($("#safePwd").val());
		vip.ajax({
			url : actionUrl ,
			dataType : "xml",
			suc : function(xml){
				//parent.Right($(xml).find("Des").text(),{callback:"reload2();"});
				JuaBox.info($(xml).find("Des").text(),{
					btnFun1:function(JuaId){
						window.top.location.reload();
					}
				});
			},
			err : function(xml){
				//parent.Wrong($(xml).find("Des").text());
				JuaBox.info($(xml).find("Des").text());
			}
		});
	}
</script>
</body>
</html>
