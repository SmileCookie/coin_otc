<!doctype html>
<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>${L:l(lan,'新增比特币接收地址-${WEB_NAME }-${WEB_TITLE }')}</title>
<jsp:include page="/common/head.jsp" />

<script type="text/javascript">
	
	function confirm() {
		var datas = FormToStr("addForm");
		console.log(datas);
		if (datas == null)
			return;
		$.ajax({
			async : true,
			cache : false,
			type : "POST",
			dataType : "xml",
			data : datas,
			url : "/manage/account/download/doAdd/${coint.stag}",
			error : function(xml) {
			},
			timeout : 60000,
			success : function(xml) {
				$("#bankBox").Loadding({
					IsShow : false
				});
				if ($(xml).find("State").text() == "true") {
					window.top.JuaBox.sure("${L:l(lan,'添加成功!')}", {btnFun1:function(JuaId) {
						//window.top.JuaBox.close(JuaId);
// 						var frames = $(window.top.document.getElementsByTagName('iframe'));
// 						$(frames[1]).attr('src', $(frames[1]).attr('src') + '&v='+ new Date().getTime());
// 						window.top.JuaBox.close(JuaId-1);
						window.top.location.reload();
					}});
				} else {
					if($(xml).find("Des").text()=="1026"){
						window.top.location.href = "/manage/auth/pwd/safe";					
					}else{
						window.top.JuaBox.sure($(xml).find("MainData").text());
					}
				}
			}//ajax调用成功处理函数结束
		});//ajax结束
	}
	//发送短信验证码
	function sendCode() {
		$.ajax({
			type : "POST",
			url : "/userSendCode",
			data : {
				codeType : 10,
				currency : "${coint.stag}"
			},
			dataType : "json",
			error : function() {
				JuaBox.info(jsLan[1]);
				inAjaxing = false;
			},
			success : function(json) {
				inAjaxing = false;
				if (json.isSuc) {
					if (json.datas.isEmail) {
						JuaBox.info(json.des);
					}
					settime($('#sendCodeBtn'));
				} else if ('needMobileAuth' == json.des) {
					JuaBox.info(bitbank.L("您未进行手机认证，请先进行手机认证"));
				} else {
					JuaBox.info(json.des);
				}
			}
		});
	}

	var countdown = 60;
	function settime(obj) {
		if (countdown == 0) {
			$(obj).removeAttr("disabled");
			$(obj).html("${L:l(lan,'点击获取')}");
			countdown = 60;
		} else {
			$(obj).attr("disabled", true);
			$(obj).html("${L:l(lan,'已发送')}"+"(" + countdown + ")");
			countdown--;
			setTimeout(function() {
				settime($(obj))
			}, 1000)
		}
	}
</script>
<style type="text/css">
.bk-tabList .form-control,
.bk-tabList .control-label,
.bk-tabList .form-control-static,
.bk-tabList .btn-group .btn { display:inline-block;}
.bk-tabList .control-label { min-width:150px; text-align:right;}
.bk-tabList .form-control { width:300px; text-align:left;}
.bk-tabList .btn-group .btn { font-size:14px; font-weight:bold; padding:15px 30px;}

</style>
</head>

<body>

	<div class="bk-body">
		<!--页面中部内容开始-->
		<div class="bk-tabList">
			<div class="bk-tabList-hd clearfix">
				<div class="btn-group bk-btn-group" role="group">
					<a class="btn active" role="button"><c:if test="${receive==null }">${L:l1(lan,'添加%%地址',coint.tag)}</c:if><c:if test="${receive!=null }">${L:l1(lan,'修改%%地址',coint.tag)}</c:if> </a>
				</div>
			</div>

			<div class="bk-tabList-bd text-left clearfix" style="padding: 10px 20px 0px 20px;">
				<form role="form" id="addForm" class="form-inline" method="post" autocomplete="off">
					<div class="form-group" style="width: 100%; font-size: 12px; margin-bottom: 10px;">
						<label for="amount" class="control-label">${L:l(lan,'接收地址')}：</label>
						<input type="text" class="form-control" id="address" name="address" value="${receive.address }" placeholder="${L:l(lan,'填写正确的接收地址')}">
					</div>
					<div class="form-group" style="width: 100%; font-size: 12px; margin-bottom: 10px;">
						<label for="amount" class="control-label">${L:l(lan,'标签')}：</label>
						<input type="text" class="form-control" id="memo" name="memo" value="${receive.memo }" placeholder="${L:l(lan,'请输入标签')}">
					</div>
					<div class="form-group" style="width: 100%; font-size: 12px; margin-bottom: 10px;">
						<label for="safepwd" class="control-label">${L:l(lan,'资金安全密码')}：</label>
						<input type="password" class="form-control" id="safepwd" name="safepwd" placeholder="${L:l(lan,'请输入资金安全密码')}">
					</div>
					<div class="form-group ${mobileStatu!=2?'hide':''}" style="width: 100%; font-size: 12px; margin-bottom: 10px;">
						<label for="mobileCode" class="control-label">${L:l(lan,'短信验证码')}：</label>
						<input type="text" class="form-control" id="mobileCode" name="mobileCode" style="width:150px;" placeholder="${L:l(lan,'短信验证码')}">
						<a id="sendCodeBtn" class="btn btn-primary" role="button" onclick="sendCode()" style="min-width: 50px;">${L:l(lan,'获取验证码')}</a>
					</div>
					<c:if test='${googleAuth==2}'>
					<div id="googleDiv" class="form-group" style="width: 100%; font-size: 12px; margin-bottom: 10px;">
						<label for="googleCode" class="control-label">${L:l(lan,'Google验证码')}：</label>
						<input type="text" class="form-control" id="googleCode" name="googleCode" placeholder="${L:l(lan,'Google验证码')}">
					</div>
					</c:if>
					<div class="form-group" style="width: 100%; font-size: 12px; margin-bottom: 10px;display:none;">
						<label for="googleCode" class="control-label">${L:l(lan,'认证')}：</label>
						<input type="checkbox" id="auth" name="auth" ${receive.auth==1?"checked":"" } placeholder="${L:l(lan,'认证')}">
<!-- 						<div style="width:490px;padding-left:150px;"> -->
<!-- 							${L:l(lan,'认证后该地址可以通过api调用提现说明')} -->
<!-- 						</div> -->
					</div>
					<div class="form-group" style="width: 100%; font-size: 12px; margin-bottom: 10px;">
						<label for="" class="control-label"></label>
						<input type="hidden" id="receiveId" name="receiveId" value="${receiveId }"/>
						<a class="btn btn-primary" role="button" onclick="confirm()" style="min-width: 200px; margin-bottom: 30px;">${L:l(lan,'添加')}</a>
					</div>
				</form>
			</div>
		</div>
		<!--页面中部内容结束-->
	</div>

</body>
</html>
