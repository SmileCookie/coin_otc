<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>${L:l(lan,"手机挂失-${WEB_NAME }-${WEB_TITLE }")}</title>
<jsp:include page="/common/head.jsp" />
<link rel=stylesheet type=text/css href="${static_domain}/statics/css/fast.css" />
<style>
body { background-color: #f9f9f9; }
#userTab { height:52px; line-height:50px; border-bottom:1px solid #ddd; margin:10px auto; text-align:center;}
#userTab a { width:120px; height:51px; border:1px solid #ddd; display:block; float:left; margin-left:30px; font-size:14px; font-weight:bold; color:#999; margin-top:1px;  background-color:#f9f9f9; box-shadow:0px -1px 1px #f0f0f0; }
#userTab a.current { background: url(${static_domain}/statics/img/tab_bg_f9.png) left top repeat-x; color:#e60000; border-bottom:1px solid #f9f9f9; text-shadow:0px 0px 1px #fff; }
#userTab a:hover { text-decoration:none; color:#e60000; background: url(${static_domain}/statics/img/tab_bg_f9.png) left top repeat-x;}
.form-con {*width:650px;}

.m_mothod { margin-right:15px !important; cursor:pointer;}
.m_mothod.active { font-weight:bold; color:red;}
</style>
<script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js"></script>
</head>

<body>
<jsp:include page="/common/top.jsp" />
<div class="wrap">
  <form name="" autocomplete="off">
    <div id="main" class="main">
      <div id="userTab" class="tab">
        <a hidefocus="true" href="/ac/password_find">${L:l(lan,"找回登录密码")}</a>
        <a hidefocus="true" href="/ac/safepwd_find">${L:l(lan,"找回资金安全密码")}</a>
        <a hidefocus="true" class="current" href="/ac/lossMobile">${L:l(lan,"手机挂失")}</a>
      </div>
      <div class="ctips" style="margin-bottom: 10px;">
         <p style="text-shadow:none;"><span>${L:l(lan,"温馨提示")}：</span><em>${L:l(lan,"详尽的信息有助于更快为您挂失手机号码噢!")}</em></p>
      </div>
      <div id="safePassword" class="bd" style="margin:15px auto;">
        <div class="main-bd">
<!--           <div class="form-line"> -->
<%--             <div class="form-tit">${L:l(lan,"您的用户名")}：</div> --%>
<%--             <div class="form-con"><input type="text" class="input" size="35px;" pattern="limit(4,40)" errormsg="${L:l(lan,'请输入正确的用户名')}" mytitle="${L:l(lan,'请输入正确的用户名')}" value="" id="userName" name="userName"/></div> --%>
<!--             </div> -->

			<div class="form-line">
              <div class="form-tit">*${L:l(lan,"您的资金安全密码")}：</div>
              <div class="form-con"><input type="password" class="input" size="35px;" pattern="limit(6,30)" errormsg="${L:l(lan,'请输入您的资金安全密码')}" mytitle="${L:l(lan,'请输入您的资金安全密码')}" value="" id="safePwd" name="safePwd"/></div>
            </div>
			<div class="form-line">
              <div class="form-tit">*${L:l(lan,"挂失手机号码")}：</div>
              <div class="form-con"><input type="text" class="input" size="35px;" pattern="limit(6,30)" errormsg="${L:l(lan,'请输入您的挂失手机号码')}" mytitle="${L:l(lan,'请输入您的挂失手机号码')}" value="" id="mobileNumber" name="mobileNumber"/></div>
            </div>
			<div class="form-line">
              <div class="form-tit">*${L:l(lan,"认证姓名")}：</div>
              <div class="form-con"><input type="text" class="input" size="35px;" pattern="limit(6,30)" errormsg="${L:l(lan,'请输入您的认证姓名')}" mytitle="${L:l(lan,'请输入您的认证姓名')}" value="" id="realName" name="realName"/></div>
            </div>
			<div class="form-line">
              <div class="form-tit">*${L:l(lan,"认证证件号")}：</div>
              <div class="form-con"><input type="text" class="input" size="35px;" pattern="limit(6,30)" errormsg="${L:l(lan,'请输入您的认证证件号')}" mytitle="${L:l(lan,'请输入您的认证证件号')}" value="" id="cardId" name="cardId"/></div>
            </div>
			<div class="form-line">
              <div class="form-tit">${L:l(lan,"注册年月")}：</div>
              <div class="form-con">
              	<input type="text" id="regYearMonth" name="regYearMonth"
              		onFocus="WdatePicker({startDate:'%y-%M-01 00:00:00',dateFmt:'yyyy-MM',alwaysUseStartDate:true, readOnly: true})"/>
              </div>
            </div>
			<div class="form-line">
              <div class="form-tit">${L:l(lan,"首次充值币种")}：</div>
              <div class="form-con">
               	<select id="firstDepositCurrency" name="firstDepositCurrency" class="form-control" style="width:250px;">
               		<option value="">--${L:l(lan,'请选择')}--</option>
               		<c:forEach items="${currencyTypes}" var="item">
                		<option value="${item.key}">${item.value}</option>
               		</c:forEach>
               	</select>
              </div>
            </div>
            <div class="form-line">
              <div class="form-tit">${L:l(lan,"首次充值金额")}：</div>
              <div class="form-con">
              	<input type="text" class="input" size="35px;" pattern="num()"
              		mytitle="${L:l(lan,'firstDepositAmount')}" value="" id="firstDepositAmount" name="firstDepositAmount"/></div>
            </div>
			<div class="form-line">
              <div class="form-tit">${L:l(lan,"首次提现币种")}：</div>
              <div class="form-con">
               	<select id="firstWithdrawCurrency" name="firstWithdrawCurrency" class="form-control" style="width:250px;">
               		<option value="">--${L:l(lan,'请选择')}--</option>
               		<c:forEach items="${currencyTypes}" var="item">
                		<option value="${item.key}">${item.value}</option>
               		</c:forEach>
               	</select>
               	
              </div>
            </div>
            <div class="form-line">
              <div class="form-tit">${L:l(lan,"首次提现金额")}：</div>
              <div class="form-con">
              	<input type="text" class="input" size="35px;" pattern="num()"
              		mytitle="${L:l(lan,'firstWithdrawAmount')}" value="" id="firstWithdrawAmount" name="firstWithdrawAmount"/></div>
            </div>

 			<div class="form-line" id="mobileDiv" style="height:100px;">
              <div class="form-tit">*${L:l(lan,"您的临时手机")}：</div>
              <div class="form-con">
              	<select id="countryCode" name="countryCode" class="form-control" style="width:250px;">
               		<c:forEach items="${country}" var="coun">
                		<option value="${coun.code}" <c:if test="${coun.code=='+86'}">selected</c:if>>${coun.code} （${coun.name}）</option>
               		</c:forEach>
               	</select><br/>
                <input type="text" class="input" size="35px;" pattern="limit(4,40);mobile()" errormsg="${L:l(lan,'请输入您的临时手机号码')}" mytitle="${L:l(lan,'请输入您的临时手机号码')}" value="" id="newMobile" name="newMobile" style="margin-right: 10px;" />
              </div>
            </div>

            <div class="form-line">
	            <div class="form-tit">${L:l(lan,"验证码")}：</div>
	            <div class="form-con" style="width:228px;">
	              <input class="txt" type="text" name="code" errmsg='${L:l(lan,"请填写图像验证码")}' id="code"  pattern="limit(4,4)" position="s" style="width:190px;"/>
	            </div>
	            <div class="form-code"> <img alt='${L:l(lan,"请填写至少4位的验证码")}' onclick="getCode()" src="/imagecode/get-28-85-39" id="idCode" /></div>
           </div>

            <div class="form-line" id="mCodeDiv" style=";">
				<div class="form-tit">${L:l(lan,"短信验证码")}：</div>
				<div class="form-con" style="width:228px;">
					<input class="txt" type="text" name="newMobileCode" errmsg='${L:l(lan,"请填写短信验证码")}' id="newMobileCode"  pattern="limit(6,6)" position="s" style="width:190px;"/>
				</div>
				<div class="form-code"><input type="button" name="sendCodeBtn" id="sendCodeBtn" style="padding:0 10px;" value="${L:l(lan,'点击获取')}" onclick="sendCode()" /></div>
			</div>
			
			<div class="form-line">
              <div class="form-tit">${L:l(lan,"Google验证码")}：</div>
              <div class="form-con">
                <input class="input" size="20px;" type="text" name="googleCode" id="googleCode" errormsg="${L:l(lan,'请输入Google验证码')}" mytitle="${L:l(lan,'请输入Google验证码')}" errorName="${L:l(lan,'Google验证码')}" pattern=""/>
              </div>
            </div>

          <hr/>
          <div class="do"><a tabindex="5" href="javascript:Redirect('/u/')" class="alibtn_orange35"><h4>${L:l(lan,"取消")}</h4></a> <a tabindex="8" style="margin-left:36px;" href="javascript:send()" class="alibtn_orange35"><h4>${L:l(lan,"提交")}</h4></a></div>

          </div>
        </div>
      </div>
    </form>
</div>

<jsp:include page="/common/foot.jsp" />
<script type="text/javascript">
	$(function(){
		$("#main").Ui();
	});
	
	function send() {
		var actionUrl = "/ac/reportMobileLoss";
		var safePwd = $('#safePwd').val();
		var mobileNumber = $('#mobileNumber').val();
		var realName = $('#realName').val();
		var cardId = $('#cardId').val();
		var regYearMonth = $('#regYearMonth').val();
		var firstDepositCurrency = $('#firstDepositCurrency').val();
		var firstDepositAmount = $('#firstDepositAmount').val();
		var firstWithdrawCurrency = $('#firstWithdrawCurrency').val();
		var firstWithdrawAmount = $('#firstWithdrawAmount').val();
		
		var newMobile = $('#newMobile').val();
		var newMobileCode = $('#newMobileCode').val();
		var countryCode = $('#countryCode').val();
		var code = $('#code').val();
		var googleCode = $('#googleCode').val();
		$.ajax({
			type:"POST",
			url: actionUrl,
			data:{
				safePwd:safePwd,
				mobileNumber:mobileNumber,
				realName:realName,
				cardId:cardId,
				regYearMonth:regYearMonth,
				firstDepositCurrency:firstDepositCurrency,
				firstDepositAmount:firstDepositAmount,
				firstWithdrawCurrency:firstWithdrawCurrency,
				firstWithdrawAmount:firstWithdrawAmount,
				
				newMobileNumber:newMobile,
				newMobileCode:newMobileCode,
				countryCode:countryCode,
				code:code,
				googleCode:googleCode
			},
			dataType:"json",
// 			error:function(){Wrong(jsLan[1]);inAjaxing=false;},
			success:function(json) {
				inAjaxing=false;
				if (json.isSuc) {
					Right(json.des, {callback:"reload2()"});
				} else {
					getCode();
					Wrong(json.des);
	 	      }
			}
		});
	}
	function reload2(){
		Close();
		document.location.reload();
	}
	
	/* function send(){
		var actionUrl = "/ac/reportMobileLoss";
		var safePwd = $('#safePwd').val();
		var mobileNumber = $('#mobileNumber').val();
		var realName = $('#realName').val();
		var cardId = $('#cardId').val();
		var regYearMonth = $('#regYearMonth').val();
		var firstDepositCurrency = $('#firstDepositCurrency').val();
		var firstDepositAmount = $('#firstDepositAmount').val();
		var firstWithdrawCurrency = $('#firstWithdrawCurrency').val();
		var firstWithdrawAmount = $('#firstWithdrawAmount').val();
		
		var newMobile = $('#newMobile').val();
		var newMobileCode = $('#newMobileCode').val();
		var countryCode = $('#countryCode').val();
		var code = $('#code').val();
		var googleCode = $('#googleCode').val();
		
		var data = {
			safePwd:safePwd,
			mobileNumber:mobileNumber,
			realName:realName,
			cardId:cardId,
			regYearMonth:regYearMonth,
			firstDepositCurrency:firstDepositCurrency,
			firstDepositAmount:firstDepositAmount,
			firstWithdrawCurrency:firstWithdrawCurrency,
			firstWithdrawAmount:firstWithdrawAmount,
			
			newMobileNumber:newMobile,
			newMobileCode:newMobileCode,
			countryCode:countryCode,
			code:code,
			googleCode:googleCode
		};
		$.post(actionUrl, data, function(json){
			if(json.isSuc){
				Right(json.des);
			}else{
				Wrong(json.des, {callback:"Close();getCode();"});
			}
		},"json");

	} */

function getCode(){
   var id=numberID();
   $("#idCode").attr("src","/imagecode/get-28-85-39-"+id);
}

function sendCode() {
	var phonenumber = $('#newMobile').val();
	var countryCode = $('#countryCode').val();
	var code = $('#code').val();
	$.ajax({
		type:"POST",
		url:"/register/sendCode",
		data:{
			phonenumber: phonenumber,
			countryCode: countryCode,
			code: code,
			codeType: 11
		},
		dataType:"json",
		error:function(){Wrong(jsLan[1]);inAjaxing=false;},
		success:function(json) {
			inAjaxing=false;
			if (json.isSuc) {
				settime($('#sendCodeBtn'));
			} else {
				getCode();
				Wrong(json.des);
 	      }
		}
	});
}

var countdown=60;
function settime(obj) {
	if (countdown == 0) {
		$(obj).removeAttr("disabled");
		$(obj).val("${L:l(lan,'点击获取')}");
		countdown = 60;
	} else {
		$(obj).attr("disabled", true);
		$(obj).val("${L:l(lan,'已发送')}"+"(" + countdown + ")");
		countdown--;
		setTimeout(function() {
			settime($(obj))
		},1000)
	}
}

</script>
</body>
</html>
