<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
<jsp:include page="/admins/top.jsp" />
<link rel=stylesheet type=text/css href="${static_domain }/statics/css/admin/info.css" />
<style type="text/css">
.details {
	text-align: left;
    padding-left: 15px;
    line-height: 28px;
    font-size: 14px;
}
.details .num-info .s2{width: 266px;float: left;}
.details a{
	color: #4775A9;
}
.MessageControl a.ok, .MessageControl a.noback {
    background-color: none;
    width: 66px;
}
.b_zichan .etinfo ul.ltc {
    background-position: -305px -256px;
}
</style>
<script>
$(function(){
	${userArray};
});
</script>
</head>
<body>
<input type="hidden" id="userId" value="${user.id}" />
<div class="" style="background-color: #FBFAF8;">
  	<div class="main-box user-information" style="text-align:left; border:1px solid #F2F2F2;border-top:solid 10px #e0e0e0;background: none;padding:10px 10px; ">
        <c:forEach items="${records}" var="record">
			<fmt:formatDate value="${record.createTime }" pattern="yyyy-MM-dd HH:mm"/>管理员：${record.aUser.admName}添加备注，内容：${record.memo}<br/>
	 	</c:forEach>
	 	<a style="font-weight: bold;font-size: 14px;color: red;margin-left: 15px;" href="javascript:addMemo('${user.id}')">添加备注</a>
	</div>
	<div class="side_r ld">
    	<div class="b_renzhen clearfloat" id="step1">
            <a name="step1"></a>
	        <dl class="ld" style="padding: 15px;">
	        <dd class="d1">
	        	<b>${user.userName }</b>
	        	<span class="op" style="margin-left: 15px;">
					<font>登录他的账户</font>
		            <%--<a style="color: #ff0000;" href="javascript:loginuser('${user.id}')">登录他的账户</a>--%>
		        </span>
		        <span class="op">
		            <c:if test="${isDiffAreaLoginNoCheck}"><a href="javascript:loginCheck('${user.id}')">开启用户异地登陆限制</a></c:if>
		        </span>
		        <span class="op">
		            <c:if test="${!isDiffAreaLoginNoCheck}"><a href="javascript:loginCheck('${user.id}')">关闭用户异地登陆限制</a></c:if>
		        </span>
            </dd>
	        </dl>
	     </div>
	
		<div class="details">
	       	<span class="op">
	          	<c:if test="${user.lockPwd}"><a href="javascript:unLockLoginPwd('${user.id}')">解锁登录密码</a></c:if>
	        </span>
	        <span class="op">
	            <c:if test="${user.lockSafePwd}"><a href="javascript:unLockSafePwd('${user.id}')">解锁资金密码</a></c:if>
	        </span>
	        <span class="op">
	            <c:if test="${isLockSafePwd}"><a href="javascript:unLock24HSafePwd('${user.id}')">解除资金密码24小时锁定</a></c:if>
	        </span>
	        
	       	<span class="op">
	          	<c:if test="${user.lockMobile}"><a href="javascript:unLockMobile('${user.id}')">解除手机锁定</a></c:if>
	        </span>
	        <span class="op">
	            <c:if test="${user.lockGoogle}"><a href="javascript:unLockGoogle('${user.id}')">解除谷歌验证锁定</a></c:if>
	        </span>
	        <span class="op"><a href="javascript:unlockPhoneCode('${user.id}')">解除当天短信锁定</a></span>
	        
	        <c:if test="${failAuthTimes > 5 }">
	        	<span class="op">
                    <a href="javascript:clearFailAuthTimes('${user.id}')">清除认证失败次数</a>
                </span> 
            </c:if>
            <c:if test="${cacheAuthTimes >= 2}">
            	<span class="op">
                    <a href="javascript:clearAuthTimes('${user.id}')">清除缓存认证次数</a>
                </span> 
            </c:if>
	        
	        <p>
	        	<span>
	        		<c:choose>
	        			<c:when test="${user.userContact.emailStatu==2}">认证邮箱：${user.userContact.safeEmail} 
						<c:if test="${logAdmin.rid==1}">
							<font>修改邮箱</font>
							<%--<a href="javascript:email();">修改邮箱</a>--%>
						</c:if>
	        			</c:when>
	        			<c:otherwise>未认证
	        				<c:if test="${logAdmin.rid==1}">
								<font>手动认证</font>
	        					<%--<a href="javascript:email();">手动认证</a>--%>
	        				</c:if>
	        			</c:otherwise>
	        		</c:choose>
	        			<font color="#ff0000" style="margin-left: 20px;">注册邮箱：${user.email}</font>
	        	</span>
	        </p>
	        <p>
	        	<span>认证手机：
	        		<c:choose>
	        			<c:when test="${user.userContact.mobileStatu==2}">${user.userContact.safeMobile}(<i class="talk-flag" style="background-position: 0 ${country.position};display:inline-block;"></i>${country.name })
	        			<c:if test="${logAdmin.rid==1}">
							<font>修改手机</font>
							<%--<a href="javascript:mobile();">修改手机</a> --%>
							&nbsp;&nbsp;
							<a href="javascript:cancelMobile('${user.id}');">取消认证</a></c:if>
	        			</c:when>
	        			<c:otherwise>未认证<c:if test="${logAdmin.rid==1}">
							<font>手动认证</font>
	        			<%--<a href="javascript:mobile();">手动认证</a>--%>
	        			</c:if></c:otherwise>
	        		</c:choose>
	        	</span>
	        </p>
	       	<c:set var="googleAu" value="${user.userContact.googleAu}"></c:set>
	        <c:if test="${googleAu > 0}">
		        <p>
		        	<span>谷歌认证：
		        		<c:choose>
		        			<c:when test="${googleAu==2}">(<font color="green">已开启</font>)</c:when>
		        			<c:otherwise>(<font color="red">未开启</font>)</c:otherwise>
		        		</c:choose>&nbsp;&nbsp;&nbsp;&nbsp;
		        		<c:if test="${logAdmin.rid==1 || logAdmin.rid==3}">
			        		<a href="javascript:cancelGoogle('${user.id}')">取消谷歌认证</a>
		        		</c:if>
				        <c:if test="${user.loginGoogleAuth}">
				        	<span>登录谷歌认证：
				        	<c:choose>
			        			<c:when test="${user.loginGoogleAuth}">(<font color="green">已开启</font>)</c:when>
			        			<c:otherwise>(<font color="red">未开启</font>)</c:otherwise>
			        		</c:choose>&nbsp;&nbsp;&nbsp;&nbsp;
				        		<a href="javascript:releaseLoginGoogle('${user.id}')">取消登录谷歌认证</a>
				        	</span>
				        </c:if>
		        	</span>
		        </p>
	        </c:if>
	        
	        <c:if test="${logAdmin.rid==1}">
				<span class="op">
			        <c:if test="${pwdLog==nul}"><font>重置登录密码</font></c:if>
					<%--<c:if test="${pwdLog==null}"><a href="javascript:resetPwd('${user.id}')">重置登录密码</a></c:if>/--%>
					<c:if test="${pwdLog!=null}"><a style="color: green;" href="javascript:restorePwd('${user.id}')">还原登录密码</a></c:if>
		        </span>		
				<span>
					<c:if test="${safeLog==null}"><font>重置资金密码</font></c:if>
			        <%--<c:if test="${safeLog==null}"><a href="javascript:resetSafePwd('${user.id}')">重置资金密码</a></c:if>--%>
					<c:if test="${safeLog!=null}"><a style="color: green;" href="javascript:restoreSafePwd('${user.id}')">还原资金密码</a></c:if>
		        </span>		
		        
		        <br/>        	
	        </c:if>
	    </div>
		
		<div style="clear: both;"></div>
		
		<c:forEach var="userA" items="${userArray }" varStatus="stat">
	        <!-- 提现额度 -->
	        <div class="bd" style="padding-top: 0px;color: #ff0000;">
	        	<div class="num-info">
			        <span class="s2">
		        		${userA.coint }免审额度：<font color="green" class="btcDayFreeCash">${userA.dayFreeCash }</font>
							<font>修改</font>
							<%--<a href="javascript:dayCash('${user.id}', 'dayFreeCash', '${userA.coint }')">修改</a>--%>
			        </span>
			        <span class="s2">
		        		今日已免审提：<font color="green" class="btcTodayFreeCash">${userA.hasUse }</font>
			        </span>
			        <span class="s2">
		        		每次提现额度：<font color="green" class="btcTimesCash">${userA.timesCash }</font>
						<font>修改</font>
						<%--<a href="javascript:dayCash('${user.id}', 'timesCash', '${userA.coint }')">修改</a>--%>
			        </span>
			        <span class="s2">
		        		每日提现额度：<font color="green" class="btcDayCash">${userA.dayCash }</font>
						<font>修改</font>
						<%--<a href="javascript:dayCash('${user.id}', 'timesCash', '${userA.coint }')">修改</a>--%>
			        </span>
		        </div>
	        </div>
	        <div class="b_zichan">
		        <div class="etinfo" id="UC_asset">
		          <ul class="btc clearfloat">
		            <li class="d1">总资产：<span>${userA.unitTag } ${userA.total }</span></li>
		            <li class="d2">可用：<span>${userA.unitTag } ${userA.balance }</span></li>
		            <li class="d3">冻结：<span>${userA.unitTag } ${userA.freez }</span></li>
		            <li class="d4">充值：<span>${userA.unitTag } ${userA.chargeTotal }</span></li>
		            <li class="d5">提现：<span>${userA.unitTag } ${userA.cashTotal }</span></li>
		          </ul>
		        </div>  
	      	</div>
        </c:forEach>
		
		
		<c:forEach var="userA" items="${userArray }">
	      	<div style="width:900px;float:left;text-align:left;padding: 10px;font-size: 14px;line-height:26px; ">
			<c:if test="${userA.cashAddr != '[]'}">
			        <p>${userA.coint }提币地址<br/>
			        <c:forEach items="${userA.cashAddr}" var="btcKey">
				        <c:if test="${btcKey.auth==1 }">[<font color="green">已认证</font>]</c:if>
				        <a href="${userA.url }${btcKey.address}" target="_blank">${btcKey.address}</a>
				        <c:if test="${btcKey.isDeleted==1 }">[<font color="grey">已删除</font>]</c:if> <br/>
			        </c:forEach>
			        </p>
		    </c:if>
		    <c:if test="${userA.chargeAddr != '[]'}">
	        	<h3>${userA.coint }充值地址</h3>
	        	<table style="width:70%;">
	        		<tr>
	        			<td>地址</td>
	        			<td>使用次数</td>
	        			<td>钱包</td>
	        		</tr>
	        		<c:forEach items="${userA.chargeAddr}" var="key" varStatus="stat">
	        			<tr>
		        			<td>
		        				<a href="${userA.url }${key.keyPre }" target="_blank">${key.keyPre}</a>
		        				<c:if test="${logAdmin.rid==1}">
		        				<c:choose>
		        					<c:when test="${key.usedTimes eq 0}">
		        						&nbsp;&nbsp;
										<font>标记已使用</font>
										<%--<a href="javascript:tagBtcAddr('${key.keyId}','${userA.coint }')" style='color:red;'>标记已使用</a>--%>
		        					</c:when>
		        					<c:otherwise>
		        						&nbsp;&nbsp;<c:if test="${key.tag eq 1}"><font color="green">[已标记]</font></c:if>
		        					</c:otherwise>
		        				</c:choose>
		        				</c:if>
		        			</td>
		        			<td>${key.usedTimes}</td>
		        			<td>${key.wallet}</td>
		        		</tr>
	        		</c:forEach>
	        	</table>
	        </c:if>
	        </div>
		    
		</c:forEach>
        
      <div style="width:600px;float:left;padding: 10px;margin-bottom: 60px;font-size: 14px;">
	       	<table style="width:100%;">
	       		<tr>
	       			<td>编号</td>
	       			<td>登录时间</td>
	       			<td>登录IP</td>
	       		</tr>
	       		<c:forEach items="${ips}" var="ip" varStatus="stat">
	       			<tr>
	        			<td>${stat.index + 1}</td>
	        			<td>${ip.date}</td>
	        			<td><a href="http://www.ip138.com/ips138.asp?ip=${ip.ip}&action=2" target="_blank">${ip.ip}</a><font style="color:#ff0000;">[${ip.city }]</font></td>
	        		</tr>
	       		</c:forEach>
	       	</table>
        </div>
        
        <div style="width:668px;float:left;text-align:left;padding: 10px;margin-bottom: 60px;font-size: 14px;">
	        <font color="#ff0000">用户日志信息：</font><br/>
	        <table style="width:100%;">
	       		<tr>
	       			<td>时间</td>
	       			<td>描述</td>
	       			<td>IP</td>
	       		</tr>
	       		<c:forEach items="${logs}" var="log">
	       			<tr>
	        			<td><fmt:formatDate value="${log.time }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
	        			<td>${log.des }</td>
	        			<td><a href="http://www.ip138.com/ips138.asp?ip=${log.ip}&action=2" target="_blank">${log.ip}</a></td>
	        		</tr>
	       		</c:forEach>
	       	</table>
        </div>
	</div>
</div>
<script type="text/javascript">
$(function() {
});
var showname="";
function loginuser(userName){
	if(!couldPass){
		showname = userName;
		googleCode("loginuser", true);
		return;
	}
	
	Info("验证通过，请点击<a href='/admin/user/loginuser?uid="+encodeURIComponent(showname)+"&mCode="+userName+"' target='_blank'>登录</a>");
	couldPass = false;
	//Close();
	//var url = "/admin/user/loginuser?uid="+encodeURIComponent(showname)+"&mCode="+userName;
	//openwin(url);
}

function openwin(url) {
    var a = document.createElement("a");
    a.setAttribute("href", url);
    a.setAttribute("target", "_blank");
    a.setAttribute("id", "openwin");
    document.body.appendChild(a);
    a.click();
}

//解锁登录密码
function unLockLoginPwd(ids) {
	if(!couldPass){
		showname = ids;
		googleCode("unLockLoginPwd", true);
		return;
	}
	Ask2( {
		Msg : "确定要解锁该用户的登录密码吗？",
		call : function() {
			ajaxUrl("/admin/user/clearlock-" + showname+"?mCode="+ids, "xml");
		}
	});
}

function unLockSafePwd(ids) {
	if(!couldPass){
		showname = ids;
		googleCode("unLockSafePwd", true);
		return;
	}
	Ask2( {
		Msg : "确定要解锁该用户的资金密码吗？",
		call : function() {
			ajaxUrl("/admin/user/clearsafelock-" + showname+"?mCode="+ids, "xml");
		}
	});
}

function unLock24HSafePwd(ids) {
	if(!couldPass){
		showname = ids;
		googleCode("unLock24HSafePwd", true);
		return;
	}
	Ask2( {
		Msg : "您确定要解除该用户的资金密码24小时锁定吗？",
		call : function() {
			ajaxUrl("/admin/user/doUnlockSafePwd?userId=" + showname + "&mCode=" + ids, "xml");
		}
	});
}

function unLockMobile(ids) {
	if(!couldPass){
		showname = ids;
		googleCode("unLockMobile", true);
		return;
	}
	Ask2( {
		Msg : "确定要解除该用户的手机锁定吗？",
		call : function() {
			ajaxUrl("/admin/user/clearmobile-" + showname+"?mCode="+ids, "xml");
		}
	});
}
function unLockGoogle(ids) {
	if(!couldPass){
		showname = ids;
		googleCode("unLockGoogle", true);
		return;
	}
	Ask2( {
		Msg : "确定要解除该用户的谷歌认证锁定吗？",
		call : function() {
			ajaxUrl("/admin/user/cleargoogle-" + showname+"?mCode="+ids, "xml");
		}
	});
}
function cancelGoogle(ids) {
	if(!couldPass){
		showname = ids;
		googleCode("cancelGoogle", true);
		return;
	}
	Ask2( {
		Msg : "确定要取消该用户的谷歌认证吗？",
		call : function() {
			ajaxUrl("/admin/user/cancelgoogle?userId=" + showname+"&mCode="+ids, "xml");
		}
	});
}

function cancelMobile(ids) {
	if(!couldPass){
		showname = ids;
		googleCode("cancelMobile", true);
		return;
	}
	Ask2( {
		Msg : "确定要取消该用户的手机认证吗？",
		call : function() {
			ajaxUrl("/admin/user/cancelmobile?userId=" + showname+"&mCode="+ids, "xml");
		}
	});
}
function cancelEmail(ids) {
	Ask2( {
		Msg : "确定要取消该用户的邮箱认证吗？",
		call : function() {
			ajaxUrl("/admin/user/cancelemail?userId=" + ids, "xml");
		}
	});
}

function resetPwd(ids) {
	if(!couldPass){
		showname = ids;
		googleCode("resetPwd", true);
		return;
	}
	Ask2( {
		Msg : "确定要重置该用户的登录密码吗？",
		call : function() {
			ajaxUrl("/admin/user/updatePwd-" + showname+"?mCode="+ids, "xml");
		}
	});
}

function restorePwd(ids) {
	Ask2( {
		Msg : "确定要还原该用户的登录密码吗？",
		call : function() {
			ajaxUrl("/admin/user/restorePwd-" + ids, "xml");
		}
	});
}

function resetSafePwd(ids) {
	if(!couldPass){
		showname = ids;
		googleCode("resetSafePwd", true);
		return;
	}
	Ask2( {
		Msg : "确定要重置该用户的资金密码吗？",
		call : function() {
			ajaxUrl("/admin/user/updateSafePwd-" + showname+"?mCode="+ids, "xml");
		}
	});
}

function restoreSafePwd(ids) {
	Ask2( {
		Msg : "确定要还原该用户的资金密码吗？",
		call : function() {
			ajaxUrl("/admin/user/restoreSafePwd-" + ids, "xml");
		}
	});
}

function unLock(urls, id) {
	$.ajax( {
		async : true,
		cache : false,
		type : "POST",
		dataType : "xml",
		data : "",
		url : urls,
		error : function(xml) {
			Wrong("请检查网络，可能是网络过慢导致超时或者远程服务出现故障!");
		},
		timeout : 60000,
		success : function(xml) {
			if ($(xml).find("State").text() == "true") {
				Right($(xml).find("Des").text());
				$("#" + id).attr("href", "javascript:void(0);").css( {
					color : "gray"
				});
			} else {
				Wrong("发生错误:" + $(xml).find("Des").text());
			}
		}
	});
}

function reload2() {
	window.location.reload();
}

//手动通过手机认证
function mobile() {
	Iframe({
		Url : "/admin/user/mobile?userId="+$("#userId").val(),
		zoomSpeedIn : 200,
		zoomSpeedOut : 200,
		Width : 450,
		Height : 410,
		scrolling : 'no',
		isIframeAutoHeight : false,
		isShowIframeTitle:true,
		Title : "手机认证"
	});
}

//手动通过邮箱认证
function email() {
	Iframe({
		Url : "/admin/user/email?userId="+$("#userId").val(),
		zoomSpeedIn : 200,
		zoomSpeedOut : 200,
		Width : 450,
		Height : 350,
		scrolling : 'no',
		isIframeAutoHeight : false,
		isShowIframeTitle:true,
		Title : "邮箱认证"
	});
}

var inAjaxing = false;
function ajaxUrl(url, dataType){
	if(inAjaxing)
		return;
	
	inAjaxing = true;
	$.ajax( {
		async : true,
		cache : true,
		type : "POST",
		dataType : dataType,
		data : "",
		url : url,
		error : function(xml) {
			inAjaxing = false;
		},
		timeout : 60000,
		success : function(xml) {
			inAjaxing = false;
			if ($(xml).find("State").text() == "true") {
				Right($(xml).find("MainData").text(), {callback : "reload2()"});
			} else{
				Wrong($(xml).find("Des").text());
			}
		}
	});
}

function del(id) {
	Ask( {
		Title : "您确定要删除当前用户的所有信息吗？",
		callback : "dodelete(" + id + ")"
	});
}

function dodelete(ids) {
	$.ajax( {
		async : true,
		cache : true,
		type : "POST",
		dataType : "xml",
		data : "userId=" + ids,
		url : "dodelete",
		error : function(xml) {
			inAjaxing = false;
		},
		timeout : 60000,
		success : function(xml) {
			inAjaxing = false;
			if ($(xml).find("State").text() == "true") {
				Right($(xml).find("MainData").text(), {
					callback : "reload3()"
				});
			} else
				Wrong("发生错误:" + $(xml).find("Des").text());
		}
	});

}

function reload3() {
	parent.reload();
	parent.Close();
}

function addMemo(id) {
	
	var uName = "${user.userName}";
	Iframe({
		Url : '/admin/user/addMemo?userId=' + id,
		Width : 550,
		Height : 220,
		isShowIframeTitle: true,
		Title : "添加用户"+uName+"备注信息"
	});
}

function tagBtcAddr(btckeyid, coint){
	Ask2( {
		Msg : "确定要标记该"+coint+"充值地址吗，标记后该地址会失效？",
		call : function() {
			ajaxUrl("/admin/user/tagBtcAddr?btckeyid=" + btckeyid+"&coint="+coint, "xml");
		}
	});
}

function loginCheck(ids) {
	if(!couldPass){
		showname = ids;
		googleCode("loginCheck", true);
		return;
	}
	Ask2( {
		Msg : "确定要${!isDiffAreaLoginNoCheck?'关闭':'开启'}用户异地登录手机验证吗？",
		call : function() {
			ajaxUrl("/admin/user/loginCheck?userId=" + showname+"&mCode="+ids, "xml");
		}
	});
}

function dayCash(id, type, coint) {
	var title = "";
	title = type=="dayCash"?coint+"每天额度":type=="timesCash"?coint+"每次额度":type=="dayFreeCash"?coint+"日免审额度":"额度";
	Iframe({
		Url : "/admin/user/dayCash?userId="+id+"&type="+type+"&coint="+coint,
		zoomSpeedIn : 200,
		zoomSpeedOut : 200,
		Width : 480,
		Height : 279,
		scrolling : 'no',
		isIframeAutoHeight : false,
		isShowIframeTitle:true,
		Title : "修改"+title
	});
}

function releaseLoginGoogle(ids) {
	if(!couldPass){
		showname = ids;
		googleCode("releaseLoginGoogle", true);
		return;
	}
	Ask2({
		Msg : "您确定要解除该用户的登录GOOGLE验证吗？",
		call : function() {
			ajaxUrl("/admin/user/releaseLoginGoogle?id=" + showname + "&mCode=" + ids, "xml");
		}
	});
}

function unlockPhoneCode(ids) {
	if(!couldPass){
		showname = ids;
		googleCode("unlockPhoneCode", true);
		return;
	}
	Ask2( {
		Msg : "您确定要解锁该用户的当天短信限制吗？",
		call : function() {
			ajaxUrl("/admin/user/doUnlockPhoneCode?userId=" + showname + "&mCode=" + ids, "xml");
		}
	});
}

function clearFailAuthTimes(ids) {
    if (!couldPass) {
        showname = ids;
        googleCode("clearFailAuthTimes", true);
        return;
    }
    Ask2({
        Msg: "您确定要清除该用户的失败认证次数吗？",
        call: function () {
            ajaxUrl("/admin/user/clearFailAuthTimes?id=" + showname + "&mCode=" + ids, "xml");
        }
    });
}

function clearAuthTimes(ids) {
    if (!couldPass) {
        showname = ids;
        googleCode("clearAuthTimes", true);
        return;
    }
    Ask2({
        Msg: "您确定要清除该用户的缓存认证次数吗？",
        call: function () {
            ajaxUrl("/admin/user/clearAuthTimes?id=" + showname + "&mCode=" + ids, "xml");
        }
    });
}
</script>
</body>
</html>
