<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ page import="com.world.util.qiniu.QiNiuUtil" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
    String uptoken = QiNiuUtil.getUpToken();
    String domain = QiNiuUtil.getHost();
%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/common/head.jsp" />
        <title>${L:l(lan,'提现额度')}-${WEB_NAME }-${WEB_TITLE }</title>
        <meta name="keywords" content="${WEB_KEYWORD }" />
        <meta name="description" content="${WEB_DESC }" />
        <link rel="stylesheet" href="${static_domain}/statics/css/web.user.css?V${CH_VERSON }">
	</head>
    <body>
    	   <jsp:include page="/common/top.jsp" />
		   <jsp:include page="/cn/manage/withdraw/uploadImg.jsp" />
		<!-- Common TopMenu End -->
		<!-- Body From mainPage Begin -->
		<div class="mainer">
			<div class="container">
				<jsp:include page="/common/trend.jsp" />
				<div class="cont-row">
					<div class="user-panel">
						<jsp:include page="/cn/manage/auth/menu.jsp"/>
						<!--C1-->
						<div class="withdraw-content step1">
						    <span class="title-text">${L:l(lan,'认证')}<span>
							<div class="progress-box">
								<div class="pro progress1 activeing">
									<span class="num">1</span>
									<span class="text">C1${L:l(lan,'认证')}</span>
								</div>
								<div class="pro progress2">
									<span class="num">2</span>
									<span class="text">C2${L:l(lan,'认证')}</span>
								</div>
								<div class="pro progress3">
									<span class="num">3</span>
									<span class="text">C3${L:l(lan,'认证')}</span>
								</div>
							</div>
							<div class="withdraw-box">
								<span class="title">${L:l(lan,'姓名和证件证明')}</span>
								<span class="memo">1.${L:l(lan,'认证后每日提现额度：相等于')} 20 BTC</br>2.${L:l(lan,'请保证填写内容与真实证件信息一致')}</br>3.${L:l(lan,'认证通过后将无法修改身份信息')}</span>
								<table>
									<tr>
										<td class="text">${L:l(lan,'证件所在区域')}</td>
										<td class="input" id="countryList">
											<input type='text' id="countryName" readonly value="中国"/>
											<div class="dropdown" id="countryGroup">
												<div class="toggle" data-toggle="dropdown">
													<input type="hidden" id="countryCode" name="countryCode" value="+86">
													<i class="btn1" id="countryText" style="visibility: hidden">+86</i>
													<i class="iconfont ft12">&#xe600;</i>
												</div>
												<ul class="dropdown-menu" style="left:auto;right:0; width:350px; height:400px; overflow-x:hidden; overflow-y:auto;">
													<c:forEach items="${country}" var="coun">
														<li data-value="${coun.code}" <c:if test="${coun.code=='+86'}">class="active"</c:if>><a>${coun.code} [<span>${coun.des}</span>]</a></li>
													</c:forEach>
												</ul>
											</div>
										</td>
									</tr>
									<tr>
										<td class="text">${L:l(lan,'证件姓名')}</td>
										<td class="input"><input type='text' id="zjName" maxlength="16"/></td>
									</tr>
									<tr>
										<td class="text">${L:l(lan,'证件号码')}</td>
										<td class="input"><input type='text' id="zjNum" maxlength="30" onblur="zjNumBlur()"/></td>
									</tr>
								</table>
								<button class="withdraw-submit" onclick="subC1()">${L:l(lan,'提交审核')}</button>
							</div>
						</div>

						<!--C2-->
						<div class="withdraw-content step2-1">
						    <span class="title-text">${L:l(lan,'认证')}<span>
							<div class="progress-box">
								<div class="pro progress1 actived">
									<span class="num">1</span>
									<img src="${static_domain}/statics/img/user/finish.png">
									<span class="text">C1${L:l(lan,'认证')}</span>
								</div>
								<div class="pro progress2 activeing">
									<span class="num">2</span>
									<img src="${static_domain}/statics/img/user/finish.png">
									<span class="text">C2${L:l(lan,'认证')}</span>
								</div>
								<div class="pro progress3">
									<span class="num">3</span>
									<img src="${static_domain}/statics/img/user/finish.png">
									<span class="text">C3${L:l(lan,'认证')}</span>
								</div>
							</div>
							<div class="withdraw-box" >
								<span class="title">${L:l(lan,'证件照片证明')}:</span>
								<span class="memo">1.${L:l(lan,'认证后每日提现额度：相等于')} 100 BTC;</br>2.${L:l(lan,'请提供真实有效的手持本人身份证照片，虚假认证可能会导致账户被冻结，由虚假认证产生的一切后果由用户负责')}！</span>
								<table>
									<tr>
										<td class="text">${L:l(lan,'证件所在区域')}</td>
										<td class="input"><input type='text' value="${countryName}" readonly /></td>
									</tr>
									<tr>
										<td class="text">${L:l(lan,'证件姓名')}</td>
										<td class="input"><input type='text' value="${auth.realName}" readonly/></td>
									</tr>
									<tr>
										<td class="text">${L:l(lan,'证件号码')}</td>
										<td class="input"><input type='text' value="${auth.cardId}"readonly/></td>
									</tr>
									<tr>
										<td class="text">${L:l(lan,'证件颁发日期')}</td>
										<td class="input"><input type='text' id="step2-1-banfa" readonly /></td>
									</tr>
									<tr>
										<td class="text">${L:l(lan,'证件到期日期')}</td>
										<td class="input"><input type='text' id="step2-1-daoqi" readonly /></td>
									</tr>
								</table>
								<div class="pics-box">
									<div class="item one">
										<div class="img"><img src="${static_domain}/statics/img/user/group-pic-1.png"/></div>
										<div class="shili">${L:l(lan,'示例')}</div>
										<div class="img"><img src="${static_domain}/statics/img/user/user_pic_1.png"/></div>
										<div class="text">${L:l(lan,'证件上文字清晰可识别')}</div>
									</div>
									<div class="uploadpick" id="step2-1-one"><span>${L:l(lan,'点击上传（正面）')}</span></div>
									<div class="item two">
										<div class="img"><img src="${static_domain}/statics/img/user/group-pic-2.png"/></div>
										<div class="shili">${L:l(lan,'示例')}</div>
										<div class="img"><img src="${static_domain}/statics/img/user/user_pic_2.png"/></div>
										<div class="text">${L:l(lan,'证件上文字清晰可识别')}</div>
									</div>
									<div class="uploadpick" id="step2-1-two"><span>${L:l(lan,'点击上传（背面）')}</span></div>
									<div class="item three">
										<div class="img"><img src="${static_domain}/statics/img/user/group-pic-3.png"/></div>
										<div class="shili">${L:l(lan,'示例')}</div>
										<div class="img"><img src="${static_domain}/statics/img/user/user_pic_3.png"/></div>
										<div class="text">${L:l(lan,'五官可见，证件全部信息清晰无遮挡，完全露出双手手臂')}</div>
									</div>
									<div class="uploadpick" id="step2-1-three"><span>${L:l(lan,'点击上传（手持）')}</span></div>
								</div>

								<button class="withdraw-submit" onclick="subC2()">${L:l(lan,'提交审核')}</button>
							</div>
						</div>

						<!--C2-2-->
						<div class="withdraw-content step2-2">
						    <span class="title-text">${L:l(lan,'认证')}<span>
							<div class="progress-box">
								<div class="pro progress1 actived">
									<span class="num">1</span>
									<img src="${static_domain}/statics/img/user/finish.png">
									<span class="text">C1${L:l(lan,'认证')}</span>
								</div>
								<div class="pro progress2">
									<span class="num">2</span>
									<img src="${static_domain}/statics/img/user/finish.png">
									<span class="text">C2${L:l(lan,'认证')}</span>
								</div>
								<div class="pro progress3">
									<span class="num">3</span>
									<img src="${static_domain}/statics/img/user/finish.png">
									<span class="text">C3${L:l(lan,'认证')}</span>
								</div>
							</div>
							<div class="withdraw-box">
								<span class="info">${L:l(lan,'您的审核信息已经提交，我们会在2个工作日内完成审核')}</span>
								<table>
									<tr>
										<td class="text">${L:l(lan,'证件所在区域')}</td>
										<td class="input"><input type='text' value="${countryName}" readonly/></td>
									</tr>
									<tr>
										<td class="text">${L:l(lan,'证件姓名')}</td>
										<td class="input"><input type='text' value="${auth.realName}"  readonly/></td>
									</tr>
									<tr>
										<td class="text">${L:l(lan,'证件号码')}</td>
										<td class="input"><input type='text' value="${auth.cardId}" readonly/></td>
									</tr>
									<tr>
										<td class="text">${L:l(lan,'证件颁发日期')}</td>
										<td class="input"><input type='text' value="${auth.startDate}" readonly/></td>
									</tr>
									<tr>
										<td class="text">${L:l(lan,'证件到期日期')}</td>
										<td class="input"><input type='text' value="${auth.endDate}" readonly/></td>
									</tr>
								</table>
							</div>
						</div>

						<!--C3-->
						<div class="withdraw-content step3-1" >
						    <span class="title-text">${L:l(lan,'认证')}<span>
							<div class="progress-box">
								<div class="pro progress1 actived">
									<span class="num">1</span>
									<img src="${static_domain}/statics/img/user/finish.png">
									<span class="text">C1${L:l(lan,'认证')}</span>
								</div>
								<div class="pro progress2 actived">
									<span class="num">2</span>
									<img src="${static_domain}/statics/img/user/finish.png">
									<span class="text">C2${L:l(lan,'认证')}</span>
								</div>
								<div class="pro progress3 activeing">
									<span class="num">3</span>
									<img src="${static_domain}/statics/img/user/finish.png">
									<span class="text">C3${L:l(lan,'认证')}</span>
								</div>
							</div>
							<div class="withdraw-box" >
								<span class="title">${L:l(lan,'证件照片证明')}:</span>
								<span class="memo">1.${L:l(lan,'认证后每日提现额度：相等于')} 200 BTC</br>2.${L:l(lan,'目前可接受的住址证明文件类型有：3个月内有效的水、电、燃气费及信用卡账单，其中需包括您的姓名和详细居住地址')}</span>
								<table>
									<tr>
										<td class="text">${L:l(lan,'证件所在区域')}</td>
										<td class="input"><input type='text' value="${countryName}" readonly/></td>
									</tr>
									<tr>
										<td class="text">${L:l(lan,'证件姓名')}</td>
										<td class="input"><input type='text' value="${auth.realName}" readonly/></td>
									</tr>
									<tr>
										<td class="text">${L:l(lan,'证件号码')}</td>
										<td class="input"><input type='text' value="${auth.cardId}" readonly/></td>
									</tr>
									<tr>
										<td class="text">${L:l(lan,'证件颁发日期')}</td>
										<td class="input"><input type='text' value="${auth.startDate}" readonly /></td>
									</tr>
									<tr>
										<td class="text">${L:l(lan,'证件到期日期')}</td>
										<td class="input"><input type='text' value="${auth.endDate}" readonly /></td>
									</tr>
								</table>
								<div class="pics-box">
									<div class="item four">
										<div class="img"><img src="${static_domain}/statics/img/user/group-pic-4.png"/></div>
									</div>
									<div class="uploadpick" id="step3-1-one"><span>${L:l(lan,'点击上传')}</span></div>
								</div>

								<input type="hidden" id="domain" value='<%=domain%>'/>
								<input type="hidden" id="uptoken" value='<%=uptoken%>'/>

								<input type="hidden" id="imgOne" value=''/><!--front-->
								<input type="hidden" id="imgTwo" value=''/><!--back-->
								<input type="hidden" id="imgThree" value=''/><!--hand-->
								<input type="hidden" id="imgFour" value=''/>

								<input type="hidden" id="status" value ="${auth.status}" />

								<button class="withdraw-submit" onclick="subC3()">${L:l(lan,'提交审核')}</button>
							</div>
						</div>

					</div>
			    </div>
			</div>
	    </div>
	    <jsp:include page="/common/foot.jsp" />
	 <link rel="stylesheet" href="${static_domain }/statics/js/common/jedate/skin/jedate.css">
	 <script type="text/javascript" src="${static_domain }/statics/js/common/jedate/jquery.jedate.js"></script>
	 <script type="text/javascript">
			  	
				$(function(){
					
					initCountry();
					
					initDate();

					initPage();

				})

				function initPage () {
					var status = parseInt($("#status").val());
					console.log(status)
					switch (status) {
						/* 4 - 7 c1 */
						case 4: 
							$(".step1").css({"display":"block"});
							break;
						case 5: 
							$(".step2-1").css({"display":"block"});
							break;
						case 6: 
							$(".step2-1").css({"display":"block"});
							break;
						case 7: 
							$(".step2-1").css({"display":"block"});
							break;
						
						/* 0 - 3 c2 */
						case 0:
							$(".step2-1").css({"display":"block"});
							break;
						case 1:
							$(".step2-2").css({"display":"block"});
							$(".step2-2 .progress2").addClass('activeing');
							break;
						case 2:
							$(".step3-1").css({"display":"block"});
							break;
						case 3:
							$(".step2-1").css({"display":"block"});
							break;

						/* -1 - -4 c3 */
						case -1:
							$(".step3-1").css({"display":"block"});
							break;
						case -2:
							$(".step2-2").css({"display":"block"});
							$(".step2-2 .progress2").addClass('actived');
							$(".step2-2 .progress3").addClass('activeing');
							break;
						case -3:
							$(".step2-2").css({"display":"block"});
							$(".step2-2 .progress2").addClass('actived');
							$(".step2-2 .progress3").addClass('actived');
							$(".step2-2 .withdraw-box .info").html(bitbank.L('您目前的认证级别是C3，已达到最高'))
							break;
						case -4:
							$(".step3-1").css({"display":"block"});
							break;

						default:
							$(".step1").css({"display":"block"});
							break;
					}
				}

				function subC1() {
					var countryCode = $("#countryCode").val();
					var zjName = $("#zjName").val();
					var zjNum = $("#zjNum").val();
					console.log(zjNum)
					if (verify(zjName,zjNum)){
						$.ajax({
							url : '/manage/auth/simpleSave',
							type : "POST",
							data : {
								country: countryCode,
								realName: zjName,
								cardId: zjNum
							},
							complete:function(json){
								console.log('complete:'+ json)
							},
							dataType : "json",
							success : function(json){
								if(json.isSuc) {
									console.log(json)
									location.reload();
								}else{
									JuaBox.showWrong(json.des);
								}
							}
						});
					}

				}
				function verify (zjName,zjNum) {

					var re =/[`~!@#$%^&*_+<>{}\/'[\]]/im;
					if(zjName.trim() =='' || zjName.trim() == undefined){
						JuaBox.showWrong(bitbank.L('请填写证件名称'))
						return false;
					}
					if (re.test(zjName.trim())){
						JuaBox.showWrong(bitbank.L('我们只能识别文字,数字'));
						return false;
					}

					if(zjNum.trim() =='' || zjNum.trim() ==undefined){
						JuaBox.showWrong(bitbank.L('请填写证件号码'))
						return false;
					}
					if (re.test(zjNum.trim())){
						JuaBox.showWrong(bitbank.L('我们只能识别文字数字'));
						return false;
					}
					return true;
				}
				function zjNumBlur () {
					var zjNum = $("#zjNum").val();
					$.ajax({
						url : '/manage/auth/isExsitIdNumber',
						type : "POST",
						data : {
							cardId: zjNum
						},
						complete:function(){
						},
						dataType : "json",
						success : function(json){
							if(json.isSuc) {
								if(json.datas){
									JuaBox.showWrong(bitbank.L('证件号码已认证过'));
									$("#zjNum").val('');
								}
							}else{
							}
						}
					});
				}


				function subC2() {

					var banfa = $("#step2-1-banfa").val();
					var daoqi = $("#step2-1-daoqi").val();
					var img1 = $("#imgOne").val();
					var img2 = $("#imgTwo").val();
					var img3 = $("#imgThree").val();

					if (verifyC2(banfa,daoqi,img1,img2,img3)){
						$.ajax({
							url : '/manage/auth/depthSave',
							type : "POST",
							data : {
								frontalImg: img1,
								backImg: img2,
								loadImg: img3,
								startDate: banfa,
								endDate: daoqi
							},
							complete:function(json){
								console.log('complete:'+ json)
							},
							dataType : "json",
							success : function(json){
								if(json.isSuc) {
									console.log(json)
									location.reload();
								}else{
									JuaBox.showWrong(json.des);
								}
							}
						});
					}


				}

				function verifyC2(banfa,daoqi,img1,img2,img3) {

					if(banfa =='' || banfa == undefined){
						JuaBox.showWrong(bitbank.L('请填写颁发日期'))
						return false;
					}
					if(daoqi =='' || daoqi == undefined){
						JuaBox.showWrong(bitbank.L('颁发日期应早于到期日期'))
						return false;
					}
					if(new Date(banfa) > new Date(daoqi)){
						JuaBox.showWrong(bitbank.L('颁发日期应早于到期日期'))
						return false;
					}

					if(img1 =='' || img1 == undefined){
						JuaBox.showWrong(bitbank.L('请按要求上传证件照片'))
						return false;
					}
					if(img2 =='' || img2 == undefined){
						JuaBox.showWrong(bitbank.L('请按要求上传证件照片'))
						return false;
					}
					if(img3 =='' || img3 == undefined){
						JuaBox.showWrong(bitbank.L('请按要求上传证件照片'))
						return false;
					}
					
					return true;
				}
				
				function subC3() {
					
					var img4 = $("#imgFour").val();
					if(img4 =='' || img4 == undefined){
						JuaBox.showWrong(bitbank.L('请按要求上传证件照片'))
						return false;
					}
					
					$.ajax({
						url : '/manage/auth/c3AuthSave',
						type : "POST",
						data : {
							addrImg: img4
						},
						complete:function(json){
							console.log('complete:'+ json)
						},
						dataType : "json",
						success : function(json){
							if(json.isSuc) {
								console.log(json)
								location.reload();
							}else{
								JuaBox.showWrong(json.des);
							}
						}
					});


				}
				function initCountry(){
					$(".withdraw-box .dropdown-menu li").on('click',function(){
						$(".withdraw-box .dropdown-menu li").removeClass('active');
						$(this).addClass('active');
						var val = $(this).attr('data-value')
						$("#countryCode").val(val);
						$("#countryText").html(val);
						$("#countryName").val($(this).find('span').html());
					})
				}

				function initDate() {
					var language;
					if(LANG =='en'){
						language = {                            
							name  : "en",
							month : ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"],
							weeks : [ "SUN","MON","TUR","WED","THU","FRI","SAT" ],
							times : ["Hour","Minute","Second"],
							clear : "Clear",
							today : "Today",
							yes   : "Set",
							close : "Close"
						}
					}else{
						language = {
							name  : "cn",
							month : ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"],
							weeks : [ "日", "一", "二", "三", "四", "五", "六" ],
							times : ["小时","分钟","秒数"],
							clear : "清空",
							today : "今天",
							yes   : "确定",
							close : "关闭"
						}
					}
					$("#step2-1-banfa").jeDate({
						format: "YYYY-MM-DD",
						language:language
					});
					$("#step2-1-daoqi").jeDate({
						format: "YYYY-MM-DD",
						language:language
					});
					
				}
		</script>
 	</body>
</html>