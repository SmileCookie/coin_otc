<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>借贷用户管理</title>
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
.infunds{color: #0088CC;font-size: 14px;font-weight: bold;}
.outfunds{color: #B94A48;font-size: 14px;font-weight: bold;}

#towDiv {
float: none;
}

#towDiv #divlbeKey {
	font-size: 14px;
	height: 30%;
/* 	width: 50%; */
 	width: 99.2%;
	min-width: 182px;
	margin-bottom:8px;
	padding-left: 8px;
	padding-top: 0px;
	color: #666666;
}

#divlbeKey #lbeKey {
	padding-right: 30px;
	color: #33cc00;
}

#input_MoRen {
	height: 28px;
	background: #336699;
	color: white;
	}
.disableCss{
	pointer-events:none;
	color:#afafaf;
	cursor:default
}
	</style>
</head>
<body >
<div class="mains">
	<div class="col-main">
		<div class="form-search">
			<form autocomplete="off" name="searchForm" id="searchContaint">
				<div class="form-search" id="searchContainer">
					
					<div class="formline">
						<span class="formtit">用户Id：</span> 
						<span class="formcon">
							<input id="userId" mytitle="字段要求填写一个长度小于50的字符串" name="userId" pattern="limit(0,50)" size="20" type="text"/>
						</span>

						<span class="formtit">用户名：</span> 
						<span class="formcon">
							<input id="userName" mytitle="用户名要求填写一个长度小于50的字符串" name="userName" pattern="limit(0,50)" size="20" type="text"/>
						</span>
						
						<span class="formtit">杠杆级别：</span> 
						<span class="formcon">
							<select name="level" id="level" style="width:100px;display: none;" selectid="select_24962655">
					        	<option value="">全部</option>
					        	<c:forEach var="level" items="${levers}">
					             	<option value="${level.key}" <c:if test="${user.level==level.key}">selected="selected"</c:if>>${level.value}</option>
				           		</c:forEach>
					        </select>
					        <div class="SelectGray" id="select_24962655"><span><i style="width: 111px;">全部</i></span></div>
						</span>

<!-- 						<span class="formtit">开启状态：</span>  -->
<!-- 						<span class="formcon"> -->
<!-- 							<select name="status" id="status" style="width:100px;display: none;" selectid="select_24962647"> -->
<!-- 					        	<option value="">全部</option> -->
<!-- 					        	<option value="1">开启</option> -->
<!-- 			             		<option value="0">关闭</option> -->
<!-- 					        </select> -->
<!-- 					        <div class="SelectGray" id="select_24962647"><span><i style="width: 111px;">全部</i></span></div> -->
<!-- 						</span> -->
							
						<span class="formtit" style="  clear: left;">放贷状态：</span> 
						<span class="formcon">
							<select name="loanOutStatus" id="loanOutStatus" style="width:100px;display: none;" selectid="select_24962648">
					        	<option value="">全部</option>
					        	<option value="1">开启</option>
			             		<option value="0">关闭</option>
					        </select>
					        <div class="SelectGray" id="select_24962648"><span><i style="width: 111px;">全部</i></span></div>
						</span>
							
<!-- 						<span class="formtit" >可借状态：</span>  -->
<!-- 						<span class="formcon"> -->
<!-- 							<select name="loanInStatus" id="loanInStatus" style="width:100px;display: none;" selectid="select_24962659"> -->
<!-- 					        	<option value="">全部</option> -->
<!-- 					        	<option value="1">开启</option> -->
<!-- 			             		<option value="0">关闭</option> -->
<!-- 					        </select> -->
<!-- 					        <div class="SelectGray" id="select_24962659"><span><i style="width: 111px;">全部</i></span></div> -->
<!-- 						</span> -->
							
						<span class="formtit">平仓状态：</span> 
						<span class="formcon">
							<select name="sysForce" id="sysForce" style="width:100px;display: none;" selectid="select_24962649">
					        	<option value="">全部</option>
					        	<option value="1">可平仓</option>
			             		<option value="0">禁止平仓</option>
					        </select>
					        <div class="SelectGray" id="select_24962649"><span><i style="width: 111px;">全部</i></span></div>
						</span>
						
						<span class="formtit">排序：</span> 
						<span class="formcon">
							<select name="order" id="order" style="width:100px;display: none;" selectid="select_24962646">
					        	<option value="repayLevel">按平仓级别</option>
					        	<option value="btc">按BTC平仓价</option>
					        	<option value="ltc">按LTC平仓价</option>
					        	<option value="eth">按ETH平仓价</option>
					        	<option value="etc">按ETC平仓价</option>
<!-- 					        	<option value="beOut">有待借出资金</option> -->
<!-- 			             		<option value="outing">有借出中资金</option> -->
<!-- 			             		<option value="beIn">有待借入资金</option> -->
<!-- 			             		<option value="ining">有借入中资金</option> -->
					        </select>
					        <div class="SelectGray" id="select_24962646"><span><i style="width: 111px;">全部</i></span></div>
						</span>
						<span class="formtit">多/空：</span> 
						<span class="formcon">
							<select name="duokong" id="duokong" style="width:100px;display: none;" selectid="select_24962650">
					        	<option value="">全部</option>
					        	<option value="1">做多</option>
			             		<option value="2">做空</option>
					        </select>
					        <div class="SelectGray" id="select_24962650"><span><i style="width: 111px;">全部</i></span></div>
						</span>
						
						<span class="formtit">放贷范围状态：</span> 
						<span class="formcon">
							<select name="userLend" id="userLend" style="width:100px;display: none;" selectid="select_24962651">
					        	<option value="">全部</option>
					        	<option value="0">默认范围</option>
			             		<option value="1">设置范围</option>
					        </select>
					        <div class="SelectGray" id="select_24962651"><span><i style="width: 111px;">全部</i></span></div>
						</span>
						
						<p>
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
							<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
						</p>
					</div>
					<div style="clear: both;"></div>
					<div class="formline">
					</div>
	
				</div>
		
			</form>
		</div>

			<div id="towDiv">
				<div id="divlbeKey" style="color: #666666">
					默认放贷上限：
				
					<c:forEach var="defaultLimit" items="${defaultLimits}">
		             	${defaultLimit.key}:<span style="margin-right: 10px;">${defaultLimit.value}</span>
	           		</c:forEach>
					
				<%-- 	<span style="margin-right: 10px;" >${btclist}</span>
					<span style="margin-right: 10px;" >${ltclist}</span>
					<span style="margin-right: 10px;" >${ethlist}</span>
					<span style="margin-right: 10px;" >${etclist}</span> --%>
					<input type="button" value="修改默认值 " id="input_MoRen" onclick="deFuLimit()" />
				</div>
			</div>

			<div class="tab_head" id="userTab">
			<a href="javascript:vip.list.search({tab:'all'})" class="current" id="all"><span>借贷用户</span></a>
			<a href="javascript:vip.list.search({tab:'level100'})" id="level100"><span>已平仓</span></a>
<%--			<a href="javascript:vip.list.search({tab:'locked'})" id="locked"><span>已锁定</span></a>--%>
		</div>
		<div class="tab-body" id="shopslist">
			<jsp:include page="ajax.jsp" />
		</div>
	</div>
</div>

<script type="text/javascript">
$(function(){
 	vip.list.ui();
	vip.list.funcName = "借贷用户管理";
	vip.list.basePath = "/admin/loan/p2puser/";
});

	function reload2() {
		Close();
		vip.list.reload();
	}

	function level(id) {
		Iframe({
			Url : "/admin/loan/p2puser/level?userId=" + id,
			zoomSpeedIn : 200,
			zoomSpeedOut : 200,
			Width : 560,
			Height : 330,
            isShowIframeTitle: true,
			Title : "修改杠杆级别"
		});
	}

	//全选按钮的方法
	function selectAll() {

		changeCheckBox('delAll');
		$(".hd .checkbox").trigger("click");
		//$("#ck_0,#ck_1,#ck_2,#ck_3,#ck_4,#ck_5,#ck_6,#ck_7,#ck_8,#ck_9").trigger('click');
	}

	function deletes() {
		var ids = "";
		$(".checkItem").each(function() {
			var id = $(this).val();
			if ($(this).attr("checked") == true) {
				ids += id + ",";
			}
		});
		var list = ids.split(",");
		if (list.length == 1) {
			Wrong("请选择一项");
			return;
		}
		doDelMore(ids);
	}
	var commids = "";
	function doDelMore(ids) {
		if (!couldPass) {
			commids = ids;
			googleCode("doDelMore", true);
			return;
		}
		couldPass = false;

		vip.list.reloadAsk({
			title : "确定要删除选中的项吗？",
			url : "/admin/user/dodel?id=" + commids + "&mCode=" + ids
		});
	}

	function modifyStatus(userId, stat) {
		if (userId == null || $.trim(userId) == "" || stat == null
				|| $.trim(stat) == "") {
			Wrong("用户编号或者状态不能为空");
			return;
		}
		var status = 0;
		var msg = "";
		if (stat == 0) {
			status = 1;
			msg = "确定开启p2p借贷吗？";
		} else if (stat == 1) {
			status = 0;
			msg = "确定关闭p2p借贷吗？";
		}
		vip.list.reloadAsk({
			"title" : msg,
			url : "/admin/loan/p2puser/modifyStatus?userId=" + userId + "&status=" + status
		});
	}

	function modifyLoanOutStatus(userId, stat) {
		if (userId == null || $.trim(userId) == "" || stat == null
				|| $.trim(stat) == "") {
			Wrong("用户编号或者状态不能为空");
			return;
		}
		var status = 0;
		var msg = "";
		if (stat == 0) {
			status = 1;
			msg = "确定为该用户开启p2p放贷吗？";
		} else if (stat == 1) {
			status = 0;
			msg = "确定为该用户关闭p2p放贷吗？";
		}
		vip.list.reloadAsk({
			"title" : msg,
			url : "/admin/loan/p2puser/modifyLoanOutStatus?userId=" + userId + "&status=" + status
		});
	}

	function modifyLoanInStatus(userId, stat) {
		if (userId == null || $.trim(userId) == "" || stat == null
				|| $.trim(stat) == "") {
			Wrong("用户编号或者状态不能为空");
			return;
		}
		var status = 0;
		var msg = "";
		if (stat == 0) {
			status = 1;
			msg = "确定允许该用户被借入吗？";
		} else if (stat == 1) {
			status = 0;
			msg = "确定禁止该用户被借入吗？";
		}
		vip.list.reloadAsk({
			"title" : msg,
			url : "/admin/loan/p2puser/modifyLoanInStatus?userId=" + userId + "&status=" + status
		});
	}

	/* 自动投资免息多个账户	Strat */
	// 	 function modifyFreeMasterSwitch(userId, fMS) {
	// 		if (userId == null || $.trim(userId) == "" || fMS == null || $.trim(fMS) == "") {
	// 			Wrong("用户编号或者状态不能为空");
	// 			return;
	// 		}
	// 		var fMS1 = 0;
	// 		var msg = "";
	// 		if (fMS == 0) {
	// 			fMS1 = 1;
	// 			msg = "确定开启免息总开关吗？";
	// 		} else if (fMS == 1) {
	// 			fMS1 = 0;
	// 			msg = "确定关闭免息总开关，并强制关闭免息开关吗？";
	// 		}
	// 		vip.list.reloadAsk({
	// 			"title" : msg,
	// 			url : "/admin/p2puser/modifyFreeMasterSwitch?userId=" + userId + "&freeMasterSwitch=" + fMS1}
	// 		);
	// 	} 
	/* 自动投资免息多个账户	End */

	/* userLend	Start */
	function userLend(userId, lends) {
		if (userId == null || $.trim(userId) == "" || lends == null || $.trim(lends) == "") {
			Wrong("用户编号不能为空！");
			return;
		}
		var userLend = 0;
		Iframe({
			Url : "/admin/loan/p2puser/lend?userId=" + userId + "&userLend=" + lends,
			zoomSpeedIn : 200,
			zoomSpeedOut : 200,
			Width : 560,
			Height : 450,
            isShowIframeTitle: true,
			Title : "修改用户投资金额"
		});
	}
	/* userLend End */

	function modifyForce(userId, stat) {
		if (userId == null || $.trim(userId) == "" || stat == null || $.trim(stat) == "") {
			Wrong("用户编号或者状态不能为空");
			return;
		}
		var status = 0;
		var msg = "确定关闭系统平仓吗？";
		if (stat == 0) {
			status = 1;
			msg = "确定开启系统平仓吗？";
		} else if (stat == 1) {
			status = 0;
			msg = "确定关闭系统平仓吗？";
		}
		vip.list.reloadAsk({
			"title" : msg,
			url : "/admin/loan/p2puser/modifyForce?userId=" + userId + "&status=" + status
		});
	}

	function unlockUser(userId) {
		if (!couldPass) {
			_userId = userId;
			googleCode("unlockUser", true);
			return;
		}
		couldPass = false;
		if (userId == null) {
			Wrong("用户编号不能为空");
			return;
		}
		var msg = "确定解除用户锁定吗？";
		vip.list.reloadAsk({
			"title" : msg,
			url : "/admin/loan/p2puser/unlockUser?userId=" + _userId + "&mCode=" + userId
		});
	}

	function modifyFees(id) {
		Iframe({
			Url : "/admin/loan/p2puser/modifyFees?userId=" + id,
			Width : 600,
			Height : 456,
			Title : "设置放贷服务费率",
            isShowIframeTitle: true,
			scrolling : "no"
		});
	}
	
	
	/**
	* 查看用户借贷明细
	*/
	function viewLoanDetail(userId){
		Iframe({
			Url : "/admin/loan/p2puser/viewLoanDetail?userId=" + userId,
			Width : 1060,
			Height : 650,
			Title : "查看用户借贷明细",
            isShowIframeTitle: true,
			scrolling : "no"
		});
	}
	
	/* 修改默投资认值 	Start*/
	function deFuLimit() {
		 Iframe({
				Url : "/admin/loan/p2puser/deLimit",
				zoomSpeedIn : 200,
				zoomSpeedOut : 200,
				Width : 530,
				Height : 350,
             	isShowIframeTitle: true,
				Title : "修改投资默认值"
			});
		}
		/* 修改系统默认值	End */
		

function tongji(isAll){
	var ids="";
	if(!isAll){
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
	}
	vip.ajax({
		url : vip.list.basePath+"statistic?userIds=" + ids+"&isAll="+isAll,
		formId : "searchContaint",
		dataType : "json",
		suc : function(json) {
			var obj = json.datas;
			
			$("#staticInfo").show();
			$("#beOutRmb").html(obj.beOutRmb);
			$("#beOutBtc").html(obj.beOutBtc);
			$("#beOutLtc").html(obj.beOutLtc);
			$("#beOutEth").html(obj.beOutEth);
			$("#beOutEtc").html(obj.beOutEtc);
			
			$("#outingRmb").html(obj.outingRmb);
			$("#outingBtc").html(obj.outingBtc);
			$("#outingLtc").html(obj.outingLtc);
			$("#outingEth").html(obj.outingEth);
			$("#outingEtc").html(obj.outingEtc);
			
			$("#iningRmb").html(obj.iningRmb);
			$("#iningBtc").html(obj.iningBtc);
			$("#iningLtc").html(obj.iningLtc);
			$("#iningEth").html(obj.iningEth);
			$("#iningEtc").html(obj.iningEtc);
			
			$("#overdraftRmb").html(obj.overdraftRmb);
			$("#overdraftBtc").html(obj.overdraftBtc);
			$("#overdraftLtc").html(obj.overdraftLtc);
			$("#overdraftEth").html(obj.overdraftEth);
			$("#overdraftEtc").html(obj.overdraftEtc);
			$("#iningEtc").html(obj.iningEtc);
			
			$("#overdraftRmb").html(obj.overdraftRmb);
			$("#overdraftBtc").html(obj.overdraftBtc);
			$("#overdraftLtc").html(obj.overdraftLtc);
			$("#overdraftEth").html(obj.overdraftEth);
			$("#overdraftEtc").html(obj.overdraftEtc);
			
			
		}
	});
}
</script>

</body>
</html>
