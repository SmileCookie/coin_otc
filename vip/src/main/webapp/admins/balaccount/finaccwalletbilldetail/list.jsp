<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>钱包流水明细查询</title>
   <jsp:include page="/admins/top.jsp" />
   <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script> 
		<script type="text/javascript">
			$(function(){ 
			 	vip.list.ui();
			 	vip.list.basePath = "/admin/balaccount/finaccwalletbill/BillDetail/";
			});
			
			function reload2() {
				Close();
				vip.list.reload();
			}
			
			
		//全选按钮的方法
		function selectAll(){
			
			changeCheckBox('delAll'); 
			$(".item_list_bd .checkbox").trigger("click");
		}
		</script>
		<style type="text/css">
		label.checkbox{  margin: 3px 6px 0 7px;}
		label.checkbox em{ padding-left:18px; line-height:15px; float:left; font-style:normal;}
		.operation { height: 20px; line-height: 20px; text-align: left;margin-top: 10px;padding-left: 10px;}
		tbody.operations  td{ padding:0; border:0 none;}
		tbody.operations  td label.checkbox{ margin-top:10px; width:55px;}
		</style>
		<script type="text/javascript">
			function exportWalletBillDetail(){
				var exportFlag = $("#exportFlag").html();
				if(exportFlag == "没有符合要求的记录!") {
					alert("没有需要导出的数据!");
					return;
				}
				if(!couldPass){
					googleCode("exportWalletBillDetail", true);
					return;
				}
				couldPass = false;
				Close();
				var fundsType = $("#fundsType").val();
				var actionUrl = "/admin/balaccount/finaccwalletbill/billDetail/exportWalletBillDetail?fundsType="+fundsType;
				var datas = FormToStr("searchContaint");
				location.href = actionUrl+"&"+datas;
			}
		</script>
</head>
<body>
<div class="mains" style="width:1505px; overflow:scroll;">
	<div class="col-main">
		<%-- <jsp:include page="/admins/topTab.jsp" /> --%>
		<div class="form-search">
			<form autocomplete="off" name="searchForm" id="searchContaint">
				<input type="hidden" id="tab" name="tab" value="${tab }" />
				<div id="formSearchContainer">
					<p>
						<span>交易流水号：</span>
						<span class="formContainer">
							<span class="formcon mr_5">
   								<input type="text" name="txId" id="txId" size="16"/>(模糊匹配)
   							</span>
						</span>
					</p>

					<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>
					<p>
						<span>资金类型：</span>
						<select name="fundsType" id="fundsType" selectid="select_fundsType" style="display: none;">
							<option value="0">--请选择--</option>
							<c:forEach var="ft" items="${ft }">
								<option value="${ft.value.fundsType}">${ft.value.propTag}</option>
				            </c:forEach>
						</select>
						<div class="SelectGray" id="select_fundsType">
							<span><i style="width: 70px;">--请选择--</i></span>
						</div>
					</p>

					<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>
					<p>
						<span>钱包类型：</span>
						<select name="walType" id="walType" selectid="select_walType" style="display: none;">
							<option value="0">--请选择--</option>
							<option value="4">热冲到冷</option>
							<option value="2">热提到用户</option>
							<option value="3">冷到热提</option>
						</select>
						<div class="SelectGray" id="select_walType">
							<span><i style="width: 70px;">--请选择--</i></span>
						</div>
					</p>

					<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>
					<p>
						<span>交易类型：</span>
						<select name="dealType" id="dealType" selectid="select_dealType" style="display: none;">
							<option value="0">--请选择--</option>
							<option value="1">充值</option>
							<option value="2">提现(热提)</option>
							<option value="3">冷到热提</option>
							<option value="4">热冲到冷</option>
							<option value="5">其他到热提</option>
							<option value="6">其他到冷</option>
							<option value="7">冷到其他</option>
							<option value="8">热提到其他</option>
						</select>
						<div class="SelectGray" id="select_dealType">
							<span><i style="width: 70px;">--请选择--</i></span>
						</div>
					</p>
					<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>
					<p class="ormCloumn">
						<span class="formText">
							确认时间：
						</span>
						<span class="formContainer">
							<span class="spacing">从</span><span class="formcon mr_5">
   								<input type="text" class="Wdate" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd'})" name="startDate" id="startDate" size="15"/></span>
   								<span class="spacing">到</span>
   								<input type="text" class="Wdate" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd'})" name="endDate" id="endDate" size="15" />
						</span>
					</p>

					<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>
					<p class="ormCloumn">
						<span class="formText">
							区块高度：
						</span>
						<span class="formContainer">
							<span class="spacing">从</span><span class="formcon mr_5">
   								<input type="text" name="startBlockHeight" id="startBlockHeight" size="9"/></span>
   								<span class="spacing">到</span>
   								<input type="text" name="endBlockHeight" id="endBlockHeight" size="9" />
						</span>
					</p>

					<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>
					<p>
						<span>钱包编号：</span>
						<span class="formContainer">
							<span class="formcon mr_5">
   								<input type="text" name="walId" id="walId" size="16"/>(模糊匹配)
   							</span>
						</span>
					</p>

					<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>
					<p>
						<input type="hidden" name="${coint.coinParam }" value="${coint.propTag }"  />
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<a class="search-submit" href="javascript:exportWalletBillDetail();">导出excel</a>
					</p>
				</div>
			</form>
		</div>
		<div class="tab-body" id="shopslist" style="width:1500px; overflow:scroll;">
			<jsp:include page="ajax.jsp" />
		</div>
	</div>
</div>
</body>
</html>
