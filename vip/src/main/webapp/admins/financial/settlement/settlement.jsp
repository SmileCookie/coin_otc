<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<jsp:include page="/admins/top.jsp" />
<script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>

<style type="text/css">
.form-tit {width: 100px;}
.form-line {margin-left:20px;}
.col-main .prompt.b_yellow.remind2.pl_35 {
	padding:0 10px 20px 10px;
}
.form-btn {
    padding: 15px 0 0 121px;
}
.select_wrap select {
    color: #6D6D6D;
    float: left;
 
    padding: 5px;
}

.bankbox{ padding:15px;}
.bankbox .bd {
	padding-right: 20px;
	padding-left: 20px;
}
.formlist .formline {
	overflow:hidden;
	padding-bottom:8px;
	clear: both;
}
.get-btn{padding: 0 10px;
    font-weight: bold;
    background: #2C78B7;color:#fff;line-height:30px;height:30px;}
span.txt{float:left;margin-right: 5px;}
</style>

<script type="text/javascript">
$(function(){
	$("#bankBox").Ui();
	
});

function ok() {
	var actionUrl = "/admin/financial/settlement/doSettlement";
	chbtc.ajax( {
		formId : "bankBox",
		url : actionUrl,
		div : "bankBox",
		suc : function(xml) {
			parent.Right($(xml).find("MainData").text(), {
				callback : "reload2()"
			});
		}
	});
}

</script>
</head>

<body >
<div class="bankbox" id="bankBox">

<div class="bankbox_bd">
	<div class="form-line" id="merchantTotalBox">
		<div class="form-tit">结算时间：</div>
		<div class="form-con"> <input type="text" value="<fmt:formatDate value="${curData.startTime }" pattern="yyyy-MM-dd HH:mm:ss" />" style="width:120px;margin-right:5px;" name="startTime" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" id="startTime"/> </div>
		<div class="form-con"> <input type="text" value="<fmt:formatDate value="${curData.endTime }" pattern="yyyy-MM-dd HH:mm:ss" />" style="width:120px;margin-right:5px;" name="endTime" onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" id="endTime"/> </div>
		<div class="form-con"><input type="button" class="get-btn" onclick="javascript:getMerchantTotal();" value="获取" /> </div>
		<input type="hidden" id="type" name="type" value="${type }"/>
	</div>
	<div class="form-line" id="capital" style="display:none;">
		<table class="tb-list2" style="width: 80%">
			<tr>
				<td>站点</td>
				<td id="online">线上充值</td>
				<td>${type==1?"汇款充值":"人民币提现" }</td>
			</tr>
			<tr>
				<td>chbtc</td>
				<td id="onlineRmb">0</td>
				<td id="rmb">0</td>
			</tr>
			<tr>
				<td>merchants-chbtc</td>
				<td id="merchantOnlineRmb">0</td>
				<td id="merchantRmb">0</td>
			</tr>
		</table>
		<input type="hidden" id="onlineRmbInput" name="onlineRmb" value="" />
		<input type="hidden" id="rmbInput" name="rmb" value="" />
		<input type="hidden" id="merchantOnlineRmbInput" name="merchantOnlineRmb" value="" />
		<input type="hidden" id="merchantRmbInput" name="merchantRmb" value="" />
	</div>
	<div class="form-line" id="merchantTotalBox">
		<div class="form-tit">备注：</div>
		<div class="form-con">
			<textarea rows="3" cols="40" name="memo"></textarea>
		</div>
	</div>

	<div class="form-btn">
		<a class="btn" href="javascript:ok();"><span class="cont">确定</span></a>
		<a href="javascript:parent.Close();" class="btn btn-gray"><span class="cont">取消</span></a>
	</div>
</div>
</div>

<script type="text/javascript">
/**
 * 获取人民币充值账户
 */
function getMerchantTotal() {
	var actionUrl = "/admin/financial/settlement/getMerchantTotal";
	chbtc.ajax({
		formId : "merchantTotalBox",
		url : actionUrl,
		dataType : "json",
		suc : function(json) {
			var type = $('#type').val();
			if (type == 1) {
				if (json.datas.onlineRmb) {
					$('#onlineRmb').html(json.datas.onlineRmb);
					$('#onlineRmbInput').val(json.datas.onlineRmb);
				}
				if (json.datas.merchantOnlineRmb) {
					$('#merchantOnlineRmb').html(json.datas.merchantOnlineRmb);
					$('#merchantOnlineRmbInput').val(json.datas.merchantOnlineRmb);
				}
			} else {
				$('#online').hide();
				$('#onlineRmb').hide();
				$('#merchantOnlineRmb').hide();
			}
			if (json.datas.rmb) {
				$('#rmb').html(json.datas.rmb);
				$('#rmbInput').val(json.datas.rmb);
			}
			if (json.datas.merchantRmb) {
				$('#merchantRmb').html(json.datas.merchantRmb);
				$('#merchantRmbInput').val(json.datas.merchantRmb);
			}
			$('#capital').show();
		}
	});
}
</script>
</body>
</html>
