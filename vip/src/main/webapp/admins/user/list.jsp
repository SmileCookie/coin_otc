<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>用户</title>
<jsp:include page="/admins/top.jsp" />

<style type="text/css">
label.checkbox{  margin: 3px 6px 0 7px;}
label.checkbox em{ padding-left:18px; line-height:15px; float:left; font-style:normal;}
.page_nav{ margin-top:10px;}

.form-search .formline{float:left;}
.form-search p{float:none;}

.operation { height: 20px; line-height: 20px; text-align: left;margin-top: 10px;padding-left: 10px;}
tbody.operations  td{ padding:0; border:0 none;}
tbody.operations  td label.checkbox{ margin-top:10px; width:55px;}
</style>
</head>
<body >
<div class="mains">
	<div class="col-main">
		<div class="form-search" id="searchContainer">
				<form autocomplete="off" name="searchForm" id="searchContaint">
					<input type="hidden" id="tab" name="tab" value="${tab }" />
					
					<div class="formline">
						<span class="formtit">用户Id：</span> 
						<span class="formcon">
							<input id="userId" mytitle="用户名要求填写一个长度小于50的字符串" name="userId" pattern="limit(0,50)" size="20" type="text"/>
						</span>
						
						<span class="formtit">用户名：</span> 
						<span class="formcon">
							<input id="userName" mytitle="用户名要求填写一个长度小于50的字符串" name="userName" pattern="limit(0,50)" size="20" type="text"/>
						</span>

						<span class="formtit">登录ip：</span> 
						<span class="formcon">
							<input id="loginIp" mytitle="登录ip要求填写一个长度小于50的字符串" name="loginIp" pattern="limit(0,50)" size="20" type="text"/>
						</span>
					
						<span class="formtit">推荐人：</span> 
						<span class="formcon">
							<input id="recommendName" mytitle="推荐人要求填写一个长度小于50的字符串" name="recommendName" pattern="limit(0,50)" size="20" type="text"/>
						</span>
					</div>
					<div style="clear: both;"></div>
					<div class="formline">
						<span class="formtit">手机号码：</span> 
						<span class="formcon">
							<input id="mobile" mytitle="推荐人要求填写一个长度小于50的字符串" name="mobile" pattern="limit(0,50)" size="20" type="text"/>
						</span>

						<span class="formtit">邮箱：</span> 
						<span class="formcon">
							<input id="email" mytitle="推荐人要求填写一个长度小于50的字符串" name="email" pattern="limit(0,50)" size="20" type="text"/>
						</span>

						<span class="formtit">用户类型：</span>
						<span style="float:left;margin: 2px 20px 0 0px;" class="formcon">
							<select name="customerType" id="customerType" style="width:110px;display: none;">
								<option style="width:100px;" value="">全部</option>
								<c:forEach items="${map}" var="entry">
									<option style="width:100px;" value=${entry.key}>${entry.value}</option>
								</c:forEach>
							 </select>
						</span>
						<span class="formtit">用户操作类型：</span>
						<span style="float:left;margin: 2px 20px 0 0px;" class="formcon">
							<select name="customerOperation" id="customerOperation" style="width:110px;display: none;">
								<option style="width:100px;" value="">全部</option>
								<c:forEach items="${operationMap}" var="entry">
									<option style="width:100px;" value=${entry.key}>${entry.value}</option>
								</c:forEach>
							 </select>
						</span>
						<span class="formtit">是否冻结：</span>
						<span class="formcon">
							<select id="freez" name="freez">
								<option value="">全部</option>
								<option value="1">已冻结</option>
							</select>
						</span>
					</div>
					<p>
						<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
						<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
						<a class="search-submit" href="javascript:clearIp();">清理IP</a>
					</p>
<!-- 					<a id="" class="search-submit" style="width:100px;" href="javascript:exportUser();">确定导出</a> -->
<!-- 					<a id="" class="search-submit" style="width:100px;" href="javascript:sendSms();">发送短信</a> -->
<!-- 					<a id="" class="search-submit" style="width:100px;" href="javascript:seniorExports();">高级导出</a> -->
				</form>
	
		</div>
		<div class="tab_head" id="userTab">
			<a href="javascript:vip.list.search({tab:'real'})" class="current" id="real"><span>真实用户</span></a>
			<a href="javascript:vip.list.search({tab:'del'})" id="del"><span>已删除</span></a>
			<a href="javascript:vip.list.search({tab:'noreg'})" id="noreg"><span>未注册</span></a>
		</div>
		<div class="tab-body" id="shopslist">
			<jsp:include page="ajax.jsp" />
		</div>
	</div>
</div>	

<script type="text/javascript">
$(function(){ 
 	vip.list.ui();
	vip.list.funcName = "用户";
	vip.list.basePath = "/admin/user/";
});

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

function sendMsg(userName){
	var url = "/admin/letter/send-";
	if(userName)
		url += "-"+encodeURI(userName);
	
	Iframe({
	    Url:url,
        zoomSpeedIn		: 200,
        zoomSpeedOut	: 200,
        Width:680,
        Height:500,
        scrolling:'auto',
        isIframeAutoHeight:false,
        isShowIframeTitle: true,
        Title:"发送站内信"
	});
}

function seniorExports(){
	Iframe({
	    Url:'/admin/user/seniorExport',
        zoomSpeedIn		: 200,
        zoomSpeedOut	: 200,
        Width:500,
        Height:300,
        scrolling:'auto',
        isIframeAutoHeight:false,
        isShowIframeTitle: true,
        Title:"高级导出"
	});
}

function exports(){
	var area = $("#area option:selected").val();
	location.href = "/admin/user/exportMobile?area="+encodeURI(encodeURI(area , "urf-8") , "urf-8");
}

function exportUser(mCode){
	if(!couldPass){
		googleCode("exportUser", true);
		return;
	}
	couldPass = false;
	var assets = $("#assets").val();
	var nt = $("#nt option:selected").val();
	if(isNaN(assets)){
		Info("参数错误");
		return;
	}
	Ask2({
		Msg : "确定要导出资产大于等于“"+assets+"”的用户吗？",
		call : function(){
			Close();
         	location.href = "/admin/user/exportUser?assets=" + assets+"&nt="+nt+"&mCode="+mCode;
		}
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

function addBrushFram(ids){
	if(!couldPass){
		commids = ids;
		googleCode("addBrushFram", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({
		title : "确定要该用户添加到刷量用户吗？",
		url : "/admin/user/doAddBrushFram?userId="+commids+"&mCode="+ids
	});
}

function sendSms(){
	var assets = $("#assets").val();
	var nt = $("#nt option:selected").val();
	if(isNaN(assets)){
		Info("参数错误");
		return;
	}
	Iframe({
	    Url:"/admin/user/sendSms?assets=" + assets+"&nt="+nt,
        zoomSpeedIn		: 200,
        zoomSpeedOut	: 200,
        Width:750,
        Height:500,
        scrolling:'no',
        isShowIframeTitle: true,
        Title:"发送消息"
	});
}

function reload2(){
	Close();
	vip.list.reload();
}

//分配客服
function addAuditor(id){
	Iframe({
		Url : "/admin/user/addAuditor?userId=" + id,
		zoomSpeedIn : 200,
		zoomSpeedOut : 200,
		Width : 590,
		Height: 430,
        isShowIframeTitle: true,
		Title : "分配客服"
	});	  
}

function setVersion(userId) {
	Iframe({
		Url : "/admin/user/versionPage?userId=" + userId,
		Width : 550,
		Height : 330,
		isShowIframeTitle: true,
		Title : "设置用户 【"+$("#text_"+userId).text()+"】 的版本"
	});
}

function randomUser(id , set){
	var title = "确定当前用户使用随机代号吗？";
	if(!set){
		title = "确定取消当前用户使用随机代号吗？";
	}
	vip.list.reloadAsk({
		title : title,
		url : "/admin/user/randomFunc?userId="+id+"&useRandom="+set
	});
}

function setTuijian(id) {
	Iframe({
		Url : "/admin/user/updateTuijian?userId="+id,
		zoomSpeedIn : 200,
		zoomSpeedOut : 200,
		Width : 550,
		Height : 340,
		scrolling : 'no',
		isIframeAutoHeight : false,
		isShowIframeTitle:true,
		Title : "修改推荐人"
	});
}

function setCustomerType(id) {
    Iframe({
        Url : "/admin/user/showCustomerType?userId="+id,
        zoomSpeedIn : 200,
        zoomSpeedOut : 200,
        Width : 400,
        Height : 400,
        scrolling : 'no',
        isIframeAutoHeight : false,
        isShowIframeTitle:true,
        Title : "修改客户类型"
    });
}
function setCustomerOperation(id) {
    Iframe({
        Url : "/admin/user/showCustomerOperation?userId="+id,
        zoomSpeedIn : 200,
        zoomSpeedOut : 200,
        Width : 400,
        Height : 400,
        scrolling : 'no',
        isIframeAutoHeight : false,
        isShowIframeTitle:true,
        Title : "修改客户操作类型"
    });
}

function modifyApi(id) {
	Iframe({
		Url : "/admin/user/modifyApi?userId="+id,
		zoomSpeedIn : 200,
		zoomSpeedOut : 200,
		Width : 550,
		Height : 410,
		scrolling : 'no',
		isIframeAutoHeight : false,
		isShowIframeTitle:true,
		Title : "修改Api密钥"
	});
}

function limitAccessApi(id) {
	Iframe({
		Url : "/admin/user/limitAccessApi?userId="+id,
		zoomSpeedIn : 200,
		zoomSpeedOut : 200,
		Width : 550,
		Height : 410,
		scrolling : 'no',
		isIframeAutoHeight : false,
		isShowIframeTitle:true,
		Title : "Api访问限制"
	});
}

//全选按钮的方法
function selectAll(){
	
	changeCheckBox('delAll'); 
	$(".hd .checkbox").trigger("click");
	//$("#ck_0,#ck_1,#ck_2,#ck_3,#ck_4,#ck_5,#ck_6,#ck_7,#ck_8,#ck_9").trigger('click');
}

function doDel(ids){
	if(!couldPass){
		commids = ids;
		googleCode("doDel", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({
		title : "确定要删除该用户吗？",
		url : "/admin/user/dodel?id="+commids+"&mCode="+ids
	});
}

function bannedService(ids){
	if(!couldPass){
		commids = ids;
		googleCode("bannedService", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({
		title : "确定要禁止电话拜访该用户吗？",
		url : "/admin/user/bannedService?id="+commids+"&mCode="+ids
	});
}

function deletes(){
	var ids="";
	$(".checkItem").each(function(){
		var id=$(this).val();
		if($(this).attr("checked")==true){
			ids+=id+",";
		}
	});
	var list=ids.split(",");
	if(list.length==1){ 
		Wrong("请选择一项"); 
		return;
	}
	doDelMore(ids);
}
var commids = "";
function doDelMore(ids){
	if(!couldPass){
		commids = ids;
		googleCode("doDelMore", true);
		return;
	}
	couldPass = false;
	
	vip.list.reloadAsk({
		title : "确定要删除选中的项吗？",
		url : "/admin/user/dodel?id="+commids+"&mCode="+ids
	});
}

function doReturn(ids){
	if(!couldPass){
		commids = ids;
		googleCode("doReturn", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({
		title : "确定要恢复该用户吗？",
		url : "/admin/user/doreturn?id="+commids+"&mCode="+ids
	});
}

function returns(){
	var ids="";
	$(".checkItem").each(function(){
		var id=$(this).val();
		if($(this).attr("checked")==true){
			ids+=id+",";
		}
	});
	var list=ids.split(",");
	if(list.length==1){ 
		Wrong("请选择一项"); 
		return;
	}
	doReturnMore(ids);
}

function doReturnMore(ids){
	if(!couldPass){
		commids = ids;
		googleCode("doReturnMore", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({
		title : "确定要还原选中的项吗？",
		url : "/admin/user/doreturn?id="+commids+"&mCode="+ids
	});
}

function clearIp() {
	Iframe({
		Url : "/admin/user/clearIp",
		zoomSpeedIn : 200,
		zoomSpeedOut : 200,
		Width : 550,
		Height : 310,
		scrolling : 'no',
		isIframeAutoHeight : false,
		isShowIframeTitle:true,
		Title : "清理IP"
	});
}

function sendEmail(ids){
	if(!couldPass){
		commids = ids;
		googleCode("sendEmail", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({
		title : "确定要给用户发送短信验证码到邮箱吗，请先确认用户没有重新发送验证码？",
		url : "/admin/user/sendEmail?userId="+commids+"&mCode="+ids
	});
}

function clearNoReg(ids){
	if(!couldPass){
		if(ids)
			commids = ids;
		else
			commids = "";
		googleCode("clearNoReg", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({
		title : "确定要清除吗？",
		url : "/admin/user/doClearNoreg?userId="+commids+"&mCode="+ids
	});
}

function visit(id) {
	Iframe({
		Url : "/admin/user/visit?userId=" + id,
		Width : 550,
		Height : 330,
		isShowIframeTitle: true,
		Title : "添加用户 【"+$("#text_"+id).text()+"】 的拜访信息"
	});
}
var isFreez=true;
function freez_user(ids, isTrue){
	if(!couldPass){
		commids = ids;
		isFreez=isTrue;
		googleCode("freez_user", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({
		title : "确定要"+(isFreez?"冻结":"解冻")+"该用户吗？",
		url : "/admin/user/freez?id="+commids+"&mCode="+ids
	});
}

function forbid_user(ids, isTrue){
	if(!couldPass){
		commids = ids;
		isFreez=isTrue;
		googleCode("forbid_user", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({
		title : "确定要"+(isFreez?"禁止":"解除禁止")+"该用户资金划出吗？",
		url : "/admin/user/forbidOut?id="+commids+"&mCode="+ids
	});
}

function allow_bwIn(ids, isTrue){
	if(!couldPass){
		commids = ids;
		isFreez=isTrue;
		googleCode("allow_bwIn", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({
		title : "确定要"+(isFreez?"禁止":"允许")+"该用户资金从BW转入吗？",
		url : "/admin/user/allowIn?id="+commids+"&mCode="+ids
	});
}

function juaUnBind(ids){
	if(!couldPass){
		commids = ids;
		googleCode("juaUnBind", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({
		title : "确定要解除该用户与Bitbank账户的绑定吗？",
		url : "/admin/user/juaunbind?id="+commids+"&mCode="+ids
	});
}
function changeFlowRight(ids,yon){
	var title = "确定要禁止账户["+commids+"]资金转出吗？";
	if(!whetherAllow){
		title = "确定允许账户["+commids+"]资金转出吗？";
	}
	vip.list.reloadAsk({
		title : title,
		url : "/admin/user/setCapitalFlow?id="+commids+"&yon="+whetherAllow+"&mCode="+ids
	});
}
function service(userId){
	var url = "/admin/user/waitservice/service?userId=" + userId;
	Iframe({
	    Url:url,
        zoomSpeedIn		: 200,
        zoomSpeedOut	: 200,
        Width:680,
        Height:500,
        scrolling:'auto',
        isIframeAutoHeight:false,
        isShowIframeTitle: true,
        Title:"电话拜访"
	});
}
</script>

</body>
</html>
