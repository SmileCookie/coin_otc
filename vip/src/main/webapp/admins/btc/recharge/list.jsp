<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>${coint.tag }管理-交易管理</title>
   <jsp:include page="/admins/top.jsp" />
   <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script> 
		<style type="text/css">
		label.checkbox{  margin: 3px 6px 0 7px;}
		label.checkbox em{ padding-left:18px; line-height:15px; float:left; font-style:normal;}
			.pic_info .pic{width:30px;height:30px;}
			body{background: #FFFFFF;}
			.item_list_bgcolor{background: #FFFFFF;}
			.form-search span{line-height: 30px;}
			.tb-list2 th, .tb-list2 td{padding: 2px 10px;}
			.form-search{padding:3px 0 3px 10px;}
		</style>
		<script type="text/javascript">
			function exportUser(mCode){
				if(!couldPass){
					googleCode("exportUser", true);
					return;
				}
				couldPass = false;
				Close();
				var actionUrl = "/admin/btc/recharge/exportUser?mCode="+mCode;
				var datas = FormToStr("searchContaint");
				location.href = actionUrl+"&"+datas;
			}
		</script>
	</head>
	<body> 
		<div class="mains">
		<jsp:include page="/admins/topTab.jsp" />
			<div class="col-main">
				<div class="form-search" id="listSearch">
					<form autocomplete="off" name="searchForm" id="searchContaint">
						<p class="formCloumn">
							<span class="formText">
								提交时间：
							</span>
							<span class="formContainer">
								<span class="spacing">从</span><span class="formcon mr_5">
    								<input type="text" class="Wdate" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" name="startTime" id="startTime" size="15"/></span>
    								<span class="spacing">到</span> 
    								<input type="text" class="Wdate" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" name="endTime" id="endTime" size="15" />
							</span>
						</p>

						<p class="formCloumn">
							<span class="formText">
								处理状态：
							</span>
							<span class="formContainer">
								<select name="status" id="status" style="width:90px;">
						           <option value="">全部</option>
						           <option value="10">等待确认</option>
						           <option value="11">等待录入</option>
						        </select>
							</span>
						</p>
						
						<p class="formCloumn">
							<span class="formText">
								用户ID：
							</span>
							<span class="formContainer">
								<input type="text" id="userId" name="userId" style="width:120px;"/>
							</span>
						</p>
						<p>
							<input type="hidden" name="${coint.coinParam }" value="${coint.propTag }"  />
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
							<a href="javascript:vip.list.resetForm();" id="idReset" class="search-submit">重置</a>
						</p>
						<a class="search-submit" href="javascript:exportUser('');">导出excel</a>
					</form>
				</div>
					
				<div class="tab_head" id="userTab" style="display: none;">
					<a href="javascript:vip.list.search({tab : 'charge'});" id="charge" class="current"><span>充值</span></a>
					<a href="javascript:vip.list.search({tab : 'sysCharge'});" id="sysCharge"><span>系统充值</span></a>
					<a href="javascript:vip.list.search({tab : 'sysDeduct'});" id="sysDeduct"><span>系统扣除</span></a>
					<a href="javascript:vip.list.search({tab : 'all'});" id="all"><span>所有记录</span></a>
				</div>
				
				<div class="tab-body" id="shopslist">
					<jsp:include page="ajax.jsp" />
				</div>
			</div>
		</div>
<script type="text/javascript">
$(function(){ 
 	vip.list.ui();
	vip.list.funcName = "${coint.propCnName }交易";
	vip.list.basePath = "/admin/btc/recharge/";
});

function reload2(){
	Close();
	vip.list.reload();
}

function confirm(id){
	if(!couldPass){
		commid = id;
		googleCode("confirm", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({title : "确定要手动确认充值成功吗！请谨慎操作..." , url : "/admin/btc/recharge/doConfirm?id="+commid+"&mCode="+id+"&coint=${coint.propTag }"});
}
var commid ="";
function succonfirm(id){
	Iframe({
	    Url:"/admin/btc/recharge/aoru?connId="+id+"&useTypeId=7&coint=${coint.propTag }",//充值
        zoomSpeedIn		: 200,
        zoomSpeedOut	: 200,
        Width:560,
        Height:480,
        scrolling:"no",
        isIframeAutoHeight:false,
        isShowIframeTitle: true,
        Title:"${coint.propTag }充值财务录入"
	});
}
//全选按钮的方法
function selectAll(){
	
	changeCheckBox('delAll'); 
	$(".hd .checkbox").trigger("click");
}

function syncConfirmTimes(id){
	vip.ajax({
		url : vip.list.basePath+"syncConfirmTimes?id=" + id+"&coint=${coint.propTag }",
		dataType : "json",
		suc : function(json) {
			reload2();
		}
	});
}
</script>
</body>
</html>
