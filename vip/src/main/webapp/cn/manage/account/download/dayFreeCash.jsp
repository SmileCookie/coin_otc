<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>

<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
<jsp:include page="/common/head.jsp" />
<link rel=stylesheet type=text/css href="${static_domain }/statics/css/fast.css" />

<style type="text/css">
.privilege .tb-list2 b { color:#f00;}
.privilege .tb-list2 th {border:1px solid #f3da91; background-color:#fffbef ; text-align:right; height:28px; line-height:28px; padding-right:15px;}
.privilege .tb-list2 td {border:1px solid #f3da91; background-color:#fff; text-align:left; height:28px; line-height:28px; padding-left:15px;}
.privilege .do a { width:140px;}
.privilege .do a h4 { width:125px;}
.main-bd .form-line{padding-left: 72px;}
</style>
</head>

<body>
<!-- 头部 -->
<div class="main-bd" id="mainForm" style="padding:10px; border:none;">
  <div class="mention">
    <div class="privilege">
	<div class="ctips"><p><span>温馨提示：</span>如果您要调整大额度，需要手机和谷歌双重验证。</p></div>
     <div class="clear" style="height:20px;"></div>
      <div class="form-line" id="hideDiv1">
		 <div class="form-tit">${L:l(lan,"当前额度")}：</div>
		 <div class="form-con">
			${freeCash }
		 </div>
	  </div>
	  <div class="form-line" id="hideDiv1">
		 <div class="form-tit">${L:l(lan,"申请额度")}：</div>
		 <div class="form-con">
			<input type="text" class="input" style="width:200px;" name="dayFreeCash" id="dayFreeCash" value="" pattern="limit(1,10)" mytitle="${L:l(lan,'请输入您的免审核额度')}" errormsg="${L:l(lan,'免审额度输入有误')}" errorName="${L:l(lan,'免审额度')}"/>
		 </div>
	  </div>
	  
      <div id="showDiv" class="cardkey" style="display: none;">
      	 <div class="form-line">
             <div  class="form-tit">${L:l(lan,"短信验证码")}：</div>
             <div class="form-con">
               <input type="password" id="mCode" name="mCode" style="width:100px;" class="input" position="n" mytitle="${L:l(lan,'请输入发送到您手机上的验证码')}" errormsg="${L:l(lan,'验证码错误')}" errorName="${L:l(lan,'验证码')}" pattern="limit(0,10)"/>
              <%--  <jsp:include page="/en/sms_buttons.jsp"></jsp:include> --%>
             </div>
         </div>
         <input type="hidden" id="needMobile" name="needMobile" value="false"/>
         <input type="hidden" id="needPwd" name="needPwd" value="false"/>
            
         <c:if test="${googleAuth==2}">
            <div class="form-line">
               <div  class="form-tit">${L:l(lan,'双重验证码')}：</div>
                <div class="form-con">
                  <input type="password" class="input" style="width:100px;" name="code" id="code" value="" mytitle="${L:l(lan,'请输入移动设备上生成的验证码。')}" errormsg="${L:l(lan,'验证码错误')}" pattern="limit(0,10)"/>
                </div>
             </div>
         </c:if>
	  </div>   
	     
      <div class="do">
	      <a href="javascript:parent.Close();" class="alibtn_orange35">返 回</a>
	      <a href="javascript:saveDayFreeCash();" class="alibtn_orange35" style="margin-left:36px;">确 定</a>
      </div>
    </div>
  </div>
</div>
<script type="text/javascript" charset="utf-8">
  try{
    var oldDomain=document.domain;
    var ind=oldDomain.indexOf('${baseDomain}');
    document.domain = oldDomain.substring(ind,oldDomain.length)
  } catch(msg) {
    document.domain = '${baseDomain}';  
  }
</script> 
<!--页面中部内容结束-->
<script type="text/javascript">
	$(function(){
		$("#dayFreeCash").blur(function(){
			if(parseFloat($(this).val()) > parseFloat(${freeCash})){
				$("#showDiv").show();
			}else{
				$("#showDiv").hide();
			}
		});
		$("#mainForm").Ui();
	});

	function saveDayFreeCash(){
		var actionUrl = vip.vipDomain + "/u/btc/download/saveDayFreeCash";
		vip.ajax({
			url : actionUrl , 
			formId : "mainForm",
			dataType : "json",
			suc : function(xml){
				parent.Right(xml.des,{callback:"window.location.reload();"});
			}
		});
	}
</script>
</body>
</html>
