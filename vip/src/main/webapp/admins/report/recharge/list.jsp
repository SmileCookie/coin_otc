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
			function exportRechargeReport(){
                var exportFlag = $("#exportFlag").html();
                if(exportFlag == "没有符合要求的记录!") {
                    alert("没有需要导出的数据!");
                    return;
                }
				var actionUrl = "/admin/report/recharge/exportRechargeReport?isAll=true";
				var datas = FormToStr("searchContaint");
				location.href = actionUrl+"&"+datas;
			}
		</script>
	</head>
	<body> 
		<div class="mains">
			<div class="col-main">
				<div class="form-search" id="listSearch">
					<form autocomplete="off" name="searchForm" id="searchContaint">

						<span class="formtit">货币类型：</span>
						<span class="formcon">
							<select name="coint" id="coint" style="width:100px;">
				             	<c:forEach items="${coinMap}" var="coin">
									<option value='${coin.key}'><span>${coin.value.propTag}</span></option>
								</c:forEach>
				            </select>
						</span>
						<p class="formCloumn">
							<span class="formText"><span>&nbsp;&nbsp;&nbsp;</span>提交时间：</span>
							<span class="formContainer">
								<span class="spacing">从</span><span class="formcon mr_5">
    								<input type="text" class="Wdate" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" name="startTime" id="startTime" size="15"/></span>
    								<span class="spacing">到</span> 
    								<input type="text" class="Wdate" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" name="endTime" id="endTime" size="15" />
							</span>
						</p>
						<span class="formtit"><span>&nbsp;&nbsp;&nbsp;</span>确认时间：</span>
						<span class="spacing">从</span>
						<span class="formcon mr_5">
							<input type="text" class="inputW2 Wdate" id="configStartDate" name="configStartDate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : 'cn'})" style="width:140px;"/>
						</span>
						<span class="spacing">到</span>
						<span class="formcon">
							<input type="text" class="inputW2 Wdate" id="configEndDate" name="configEndDate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : 'cn'})" style="width:140px;"/>
						</span>
						<div style="clear: both;"></div>

						<p class="formCloumn">
							<span class="formText">
								用户编号：
							</span>
							<span class="formContainer">
								<input type="text" id="userId" name="userId" style="width:120px;"/>
							</span>
						</p>

						<p class="formCloumn">
							<span class="formText">
								处理状态：
							</span>
							<span class="formContainer">
								<select name="status" id="status" style="width:90px;">
						           <option value="00">全部</option>
						           <option value="10">等待确认</option>
						           <option value="11">失败</option>
									<option value="12" selected ="selected">成功</option>
						        </select>
							</span>
						</p>
						<p>
							<input type="hidden" name="${coint.coinParam }" value="${coint.propTag }"  />
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a>
							<a href="javascript:vip.list.resetForm();" id="idReset" class="search-submit">重置</a>
						</p>
						<a class="search-submit" href="javascript:exportRechargeReport();">导出excel</a>
					</form>
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
	vip.list.basePath = "/admin/report/recharge/";
});

function reload2(){
	Close();
	vip.list.reload();
}

//全选按钮的方法
function selectAll(){
	
	changeCheckBox('delAll'); 
	$(".hd .checkbox").trigger("click");
}
</script>
</body>
</html>
