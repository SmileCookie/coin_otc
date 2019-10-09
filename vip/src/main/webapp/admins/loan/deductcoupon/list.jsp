<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>抵扣券</title>
<jsp:include page="/admins/top.jsp" />

<script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js"></script>

<style type="text/css">
label.checkbox { margin: 3px 6px 0 7px; }

label.checkbox em { padding-left: 18px;	line-height: 15px; float: left;	font-style: normal; }

.page_nav {	margin-top: 10px; }

.form-search .formline { float: left; }

.form-search p { float: none; }

.operation { height: 20px; line-height: 20px;	text-align: left;	margin-top: 10px;	padding-left: 10px; }

tbody.operations  td {	padding: 0;	border: 0 none; }

tbody.operations  td label.checkbox {	margin-top: 10px;	width: 55px; }
</style>

</head>
<body>
	<div class="mains">
		<div class="col-main">
			<div class="form-search">
				<form autocomplete="off" name="searchForm" id="searchContaint">
					<div class="form-search" id="searchContainer">
						<input type="hidden" id="tab" name="tab" value="${tab }" />

						<div class="formline">
							<span class="formtit">用户Id：</span>
							<span class="formcon">
								<input id="userId" mytitle="字段要求填写一个长度小于50的字符串" name="userId" pattern="limit(0,50)" size="20" type="text" />
							</span>
							
							<span class="formtit">用户名：</span>
							<span class="formcon">
								<input id="userName" mytitle="用户名要求填写一个长度小于50的字符串" name="userName" pattern="limit(0,50)" size="20" type="text" />
							</span>
							
							<span class="formtit">抵扣券标题：</span>
							<span class="formcon">
								<input id="title" mytitle="抵扣券标题要求填写一个长度小于50的字符串" name="title" pattern="limit(0,50)" size="20" type="text" />
							</span>
							
<!-- 							<span class="formtit">币种类型：</span> -->
<!-- 							<span class="formcon"> -->
<!-- 								<select id="fundsType" name="fundsType"> -->
<!-- 									<option value="0">-- 全部 --</option> -->
<%-- 									<c:forEach items="${fundlist }" var="ft"> --%>
<%-- 										<option value="${ft.key }">${ft.value }</option> --%>
<%-- 									</c:forEach> --%>
<!-- 							</select> -->
<!-- 							</span> -->
							
							<span class="formtit">获取途径：</span>
							<span class="formcon">
								<select id="getWay" name="getWay">
									<option value="0">-- 全部 --</option>
									<option value="1">活动发放</option>
									<option value="2">系统赠送</option>
							</select>
							</span>
							
							<span class="formtit">抵扣券类型：</span>
							<span class="formcon">
								<select id="couponType" name="couponType">
									<option value="0">-- 全部 --</option>
									<option value="1">抵扣券</option>
									<!-- <option value="2">打折券</option> -->
									<!-- <option value="3">限额抵扣券</option> -->
									<!-- <option value="4">限额打折券</option> -->
							</select>
							</span>
							
							<span class="formtit">失效日期(含前)：</span>
							<span class="formcon">
								<input id="endTime" name="endTime" size="20" type="text" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd 23:59:59',lang : 'cn'})" />
							</span>
							
							<p>
								<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a>
								<a class="search-submit" id="idReset"  href="javascript:vip.list.resetForm();">重置</a>
								<a class="search-submit" id="idSearch" href="javascript:aoru();">生成一批抵扣券</a>
								<a class="search-submit" id="idSearch" href="javascript:rewardUser();">赠送用户抵扣券</a>
								<!-- <a class="search-submit" id="idSearch" href="javascript:input();">录入抵扣券</a> -->
							</p>
						</div>
						<div style="clear: both;"></div>
						<div class="formline"></div>
					</div>
				</form>
			</div>
			<div class="tab_head" id="userTab">
				<a href="javascript:vip.list.search({tab:'allCol'})" id="allCol" class="current"><span>所有抵扣券</span></a>
				<a href="javascript:vip.list.search({tab:'zeroCol'})" id="zeroCol"><span>未激活</span></a>
				<a href="javascript:vip.list.search({tab:'oneCol'})" id="oneCol"><span>未使用</span></a>
				<a href="javascript:vip.list.search({tab:'fivesCol'})" id="fivesCol"><span>使用中</span></a>
				<a href="javascript:vip.list.search({tab:'towCol'})" id="towCol"><span>已使用</span></a>
				<a href="javascript:vip.list.search({tab:'threeCol'})" id="threeCol"><span>已过期</span></a>
				<a href="javascript:vip.list.search({tab:'fourCol'})" id="fourCol"><span>禁止使用</span></a>
				
			</div>
			<div class="tab-body" id="shopslist">
				<jsp:include page="ajax.jsp" />
			</div>
		</div>
	</div>

	<script type="text/javascript">
		$(function() {
			vip.list.ui();
			vip.list.basePath = "/admin/loan/deductcoupon/";
		});

		function reload2() {
			Close();
			vip.list.reload();
		}

		/* 全选按钮	Start */
		function selectAll(obj) {
			changeCheckBox('delAll');
			$(".hd .checkbox").trigger("click");
			if ($("#delAll")[0].checked) {
				$("input[name=boxs]").each(function() {
					$(this)[0].checked = true;
				});
			} else {
				$("input[name=boxs]").each(function() {
					$(this)[0].checked = false;
				});
			}
		}
		/* 全选按钮	End */

		/* 禁用	Start */
		function jinZhi() {
			var boxs = $("input[name=boxs]:checked");
			if (boxs.length == 0) {
				Wrong("至少选择一项!");
				return;
			}
			var secretkey = "";
			boxs.each(function() {
				secretkey += $(this).val() + ",";
			});
			if (secretkey != "")
				secretkey = secretkey.substring(0, secretkey.length - 1);
			unjinZhi(secretkey);
		}
		//跳转禁用
		function unjinZhi(secretkey) {
			vip.ajax({
				url : "/admin/loan/deductcoupon/unjinZhi?secretkey=" + secretkey,
				dataType : "json",
				suc : function(json) {
					if (json.isSuc) {
						Right("更改成功!", {
							callback : "reload2()"
						});
					} else {
						Wrong(json.des);
					}
				}
			});
		}
		/* 禁用	End */

		/* 启用	Start */
		function qiDong() {
			var boxs = $("input[name=boxs]:checked");
			if (boxs.length == 0) {
				Wrong("至少选择一项!");
				return;
			}
			var secretkey = "";
			boxs.each(function() {
				secretkey += $(this).val() + ",";
			});
			if (secretkey != "")
				secretkey = secretkey.substring(0, secretkey.length - 1);
			startUp(secretkey);
		}
		//跳转启动
		function startUp(secretkey) {
			vip.ajax({
				url : "/admin/loan/deductcoupon/startUp?secretkey=" + secretkey,
				dataType : "json",
				suc : function(json) {
					if (json.isSuc) {
						Right("更改成功!", {
							callback : "reload2()"
						});
					} else {
						Wrong(json.des);
					}
				}
			});
		}

		/* 启用	End */

		/* 删除	Start */
		function shanChu() {
			var boxs = $("input[name=boxs]:checked");
			if (boxs.length == 0) {
				Wrong("至少选择一项!");
				return;
			}
			var secretkey = "";
			boxs.each(function() {
				secretkey += $(this).val() + ",";
			});
			if (secretkey != "")
				secretkey = secretkey.substring(0, secretkey.length - 1);
			deClear(secretkey);
		}
		//再次确认是否删除
		function deClear(secretkey) {
			Ask2({
				Msg : "确定要删除选中的项吗？",
				callback : "del('" + secretkey + "')"
			});
		}
		//跳转删除
		function del(secretkey) {
			vip.ajax({
				url : "/admin/loan/deductcoupon/deClear?secretkey=" + secretkey,
				dataType : "json",
				suc : function(json) {
					if (json.isSuc) {
						Right("删除成功!", {
							callback : "reload2()"
						});
					} else {
						Wrong(json.des);
					}
				}
			});
		}
		/* 删除	End */
		
		/* 快速赠送用户	Start */
		function reUser(secretkey) {
			Iframe({
				Url : "/admin/loan/deductcoupon/reUser?secretkey=" + secretkey,
				zoomSpeedIn : 200,
				zoomSpeedOut : 200,
				Width : 500,
				Height : 400,
                isShowIframeTitle: true,
				Title : "快速赠送用户"
			});
		}
		/* 快速赠送用户	Start */
		
		/* 生成一批抵扣券	Start */
		function aoru() {
			Iframe({
				Url : "/admin/loan/deductcoupon/aoru",
				zoomSpeedIn : 200,
				zoomSpeedOut : 200,
				Width : 500,
				Height : 610,
                isShowIframeTitle: true,
				Title : "生成抵扣券"
			});
		}
		/* 生成一批抵扣券	End */
		
		/* 奖励给用户	Start */
		function rewardUser(){
			Iframe({
				Url : "/admin/loan/deductcoupon/rewardUser",
				zoomSpeedIn : 200,
				zoomSpeedOut : 200,
				Width : 500,
				Height: 610,
                isShowIframeTitle: true,
				Title : "赠送用户抵扣券"
			});	  
		}
		/* 奖励给用户	End */
		
// 		function input(){
// 			Iframe({
// 				Url : "/admin/deductcoupon/input",
// 				zoomSpeedIn : 200,
// 				zoomSpeedOut : 200,
// 				Width : 450,
// 				Height: 670,
// 				Title : "录入抵扣券"
// 			});	  
// 		}
	</script>

</body>
</html>