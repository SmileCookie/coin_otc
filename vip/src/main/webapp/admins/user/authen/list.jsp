<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page import="com.world.model.entity.user.authen.AuditType" %>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>实名认证</title>
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
		<div class="form-search">
			<form autocomplete="off" name="searchForm" id="searchContaint">
				<input type="hidden" id="tab" name="tab" value="${tab }" />
				<div class="formline">
					<span class="formtit">用户名：</span> 
					<span class="formcon">
						<input id="userName" mytitle="用户名要求填写一个长度小于50的字符串" name="userName" pattern="limit(0,50)" size="20" type="text"/>
					</span>

					<span class="formtit">真实姓名：</span> 
					<span class="formcon">
						<input id="realName" mytitle="真实姓名要求填写一个长度小于50的字符串" name="realName" pattern="limit(0,50)" size="20" type="text"/>
					</span>
					<span class="formtit">证件类型：</span>
					<span class="formcon" style="line-height: 40px;">
						<select id="cardType" name="cardType" style="display: none;" selectid="select_463444741">
							<option value="">--请选择--</option>
							<option value="1">身份证</option>
							<option value="2">护照</option>
						</select>
						<div class="SelectGray" id="select_463444741"><span><i style="width: 58px;">--请选择--</i></span></div>
					</span>
					<span class="formtit">证件号码：</span> 
					<span class="formcon">
						<input id="cardId" mytitle="身份证号要求填写一个长度小于50的字符串" name="cardId" pattern="limit(0,50)" size="30" type="text"/>
					</span>
					<span class="formtit">认证地区：</span>
					<span class="formcon" style="line-height: 40px;">
						<select id="areaInfo" name="areaInfo" style="display: none;" selectid="select_46344474">
							<option value="">--请选择--</option>
							<option value="1">大陆地区</option>
							<option value="2">港澳台地区</option>
							<option value="3">海外地区</option>
						</select>
						<div class="SelectGray" id="select_46344474"><span><i style="width: 58px;">--请选择--</i></span></div>
					</span>


					<p>
						<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a>
						<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
						<%-- <a class="search-submit" style="width: 130px;" href="javascript:vip.list.aoru({id : 0 , width : 500 , height : 600, title:'个人用户认证额度设置', otherParam: '&type=<%=AuditType.individual.getKey()%>'});">个人用户认证额度设置</a>
						<a class="search-submit" style="width: 130px;" href="javascript:vip.list.aoru({id : 0 , width : 500 , height : 600, title:'企业用户认证额度设置', otherParam: '&type=<%=AuditType.corporate.getKey()%>'});">企业用户认证额度设置</a>
 --%>					</p>
				</div>
				<div class="formline">

				</div>
	
			</form>
		</div>
		<%--<div class="tab_head" id="userTab">
			<a href="javascript:vip.list.search({tab:'a1wait'})" class="current" id="a1wait"><span>初级等待审核</span></a>
			<a href="javascript:vip.list.search({tab:'a1pass'})" id="a1pass"><span>初级审核通过</span></a>
			<a href="javascript:vip.list.search({tab:'a1unpass'})" id="a1unpass"><span>初级审核不通过</span></a>

			<a href="javascript:vip.list.search({tab:'wait'})" id="wait"><span>高级等待审核</span></a>
			<a href="javascript:vip.list.search({tab:'pass'})" id="pass"><span>高级审核通过</span></a>
			<a href="javascript:vip.list.search({tab:'unpass'})" id="unpass"><span>高级审核不通过</span></a>

			<a href="javascript:vip.list.search({tab:'c3audite'})" id="c3audite"><span>C3等待审核</span></a>
			<a href="javascript:vip.list.search({tab:'c3pass'})" id="c3pass"><span>C3审核通过</span></a>
			<a href="javascript:vip.list.search({tab:'c3unpass'})" id="c3unpass"><span>C3审核不通过</span></a>

			<a href="javascript:vip.list.search({tab:'nosub'})" id="nosub"><span>不需审核</span></a>
			<a href="javascript:vip.list.search({tab:'blackList'})" id="blackList"><span>实名认证黑名单</span></a>
			<a href="javascript:vip.list.search({tab:'all'})" id="all"><span>所有记录</span></a>
		</div>--%>
		<div class="tab-body" id="shopslist">
			<jsp:include page="ajax.jsp" />
		</div>
	</div>
</div>	

<script type="text/javascript">
	$(function(){
		vip.list.ui();
		vip.list.funcName = "实名认证";
		vip.list.basePath = "/admin/user/authen/";
	});

function reload2(){
	Close();
	vip.list.reload();
}

function agree(id,passType){
	var title = "确定要通过用户提交的实名认证审核吗？";
	Ask2({Msg:title, call:function(){
		vip.ajax({
			url : "/admin/user/authen/pass?vid=" + id + "&passType=" + passType,
			dataType : "json",
			suc : function(json) {
				Right(json.des, {callback:"reload2()"});
			}
		});
	}});
}

function reason(id,passType){
	Iframe({
		Url : "/admin/user/authen/reason?id=" + id + "&passType=" + passType,
		zoomSpeedIn : 200,
		zoomSpeedOut : 200,
		Width : 590,
		Height: 430,
        isShowIframeTitle: true,
		Title : "不通过"
	});	  
}

function bankinfo(id){
	Iframe({
		Url : "/admin/user/authen/bankInfo?id=" + id,
		zoomSpeedIn : 200,
		zoomSpeedOut : 200,
		Width : 590,
		Height: 500,
        isShowIframeTitle: true,
		Title : "校验银行卡信息"
	});	  
}

function unpass(id,reason,ope){
	var ajaxUrl = "/admin/user/authen/unpass";
	if(ope && ope == "cancel"){
		ajaxUrl = "/admin/user/authen/cancelAuthen";
	}
	vip.ajax({
		url : ajaxUrl+"?vid=" + id+"&reason="+encodeURIComponent(reason) + "&passType=" + $('#passType').val(),
		dataType : "json",
		suc : function(json) {
			Right(json.des, {callback:"reload2()"});
		}
	});
}

function cancel(id){
	Iframe({
		Url : "/admin/user/authen/cancel?id=" + id,
		zoomSpeedIn : 200,
		zoomSpeedOut : 200,
		Width : 590,
		Height: 430,
        isShowIframeTitle: true,
		Title : "取消实名认证"
	});	  
}

function history(userId){
	Iframe({
		Url : "/admin/user/authen/history?userId=" + userId,
		zoomSpeedIn : 200,
		zoomSpeedOut : 200,
		Width : 590,
		Height: 430,
        isShowIframeTitle: true,
		Title : "认证审核历史"
	});	  
}

function doCancel(id,reason){
	vip.ajax({
		url : "/admin/user/authen/unpass?vid=" + id+"&reason="+encodeURIComponent(reason) + "&passType=" + passType,
		dataType : "json",
		suc : function(json) {
			Right(json.des, {callback:"reload2()"});
		}
	});
}


//全选按钮的方法
function selectAll(){
	
	changeCheckBox('delAll'); 
	$(".hd .checkbox").trigger("click");
	//$("#ck_0,#ck_1,#ck_2,#ck_3,#ck_4,#ck_5,#ck_6,#ck_7,#ck_8,#ck_9").trigger('click');
}

function passmore(){
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

	Ask2({Msg:"确定要通过选中的项吗？", call:function(){
		vip.ajax({
			url : "/admin/user/authen/passMore?&vids="+ids,
			dataType : "json",
			suc : function(json) {
				Right(json.des, {callback:"reload2()"});
			}
		});
	}});
}

function authen(id){
	Iframe({
		Url : "/admin/user/authen/authen?id=" + id,
		zoomSpeedIn : 200,
		zoomSpeedOut : 200,
		Width : 590,
		Height: 430,
        isShowIframeTitle: true,
		Title : "用户认证失败手动通过认证"
	});	  
}

function setAuthName(id){
	Iframe({
		Url : "/admin/user/authen/setAuthName?id=" + id,
		zoomSpeedIn : 200,
		zoomSpeedOut : 300,
		Width : 590,
		Height: 430,
        isShowIframeTitle: true,
		Title : "设置用户身份证信息"
	});	  
}

	function addBlackList(id, cardNo){
		Iframe({
			Url : "/admin/user/authen/idcardBlackListAction/addBlackList?id=" + id + "&cardNo=" + cardNo,
			zoomSpeedIn : 200,
			zoomSpeedOut : 300,
			Width : 480,
			Height: 270,
            isShowIframeTitle: true,
			Title : "加入证件黑名单"
		});
	}
</script>

</body>
</html>
