<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
   <jsp:include page="/admins/top.jsp" />

	<script type="text/javascript">
	$(function(){
	  $("#admin_user_update").Ui();
	  
	  $("#type").change(function(){
		var val = $(this).find("option:selected").val();
		if(val == 1){
			//获取打币钱包
			getBtcAccount('btc');
		}else if(val == 3){
			getBtcAccount('ltc');
		}else{
			$("#waveVal").unbind("change");
			$("#autoFinanAccount").html('<input	errormsg="波动值为数字"	id="waveVal" mytitle="请填写波动值，1为生效，0为不生效" name="waveVal" pattern="limit(0,10);num()" size="20" type="text" value="${curData.waveVal}"/>');
		}
	  });
	  
	  $("#type").trigger("change");
	});//结束body load部分
	
	function save(){
		vip.ajax({formId : "admin_user_update" , url : "/admin/wave/doAoru" , div : "admin_user_update" , suc : function(xml){
			parent.vip.list.reload();
		    parent.Right($(xml).find("Des").text());
		}});
	}
	</script>
	</head>
	<body>
		<div id="admin_user_update" class="main-bd">
			<div class="form-line">
				<div class="form-tit">
					类型：
				</div>
				<div class="form-con">
					<select name="type" id="type">
						<c:forEach items="${types}" var="group">
							
							<option  value="${group.key}" <c:if test="${group.key == curData.type}">selected="selected"</c:if>
							>${group.value}</option>
						</c:forEach>
					</select>
				</div> 
			</div>
			
			<div class="form-line">
				<div class="form-tit">
					参数值：
				</div>
				<div class="form-con" id="autoFinanAccount">
					<input
						errormsg="波动值为数字"
						id="waveVal" mytitle="请填写波动值，1为生效，0为不生效" name="waveVal"
						pattern="limit(0,10);num()" size="20" type="text" value="${curData.waveVal}"
						/>
				</div>
			</div>
			
			<div class="form-line">
				<div class="form-tit">
					描述：
				</div>
				<div class="form-con">
						<textarea errormsg="长度太长，不能超过50个字符"
							id="des" mytitle="请填写描述" name="des"
							pattern="limit(0,200)" size="50" type="text"  cols="30" rows="3">${curData.des}</textarea>
				</div>
			</div>
			
			<div class="form-line">
				<div class="form-con">
					<input id="id" name="id" type="hidden" value="${curData.id}"/>
				</div>
			</div>
			
			<div class="form-btn">
				<a class="btn" href="javascript:save();"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a> <!--<a href="#" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>-->
			</div>
		</div>

	</body>
<script type="text/javascript">
/**
 * 获取打币账户
 */
function getBtcAccount(coin) {
	var actionUrl = "/admin/wave/findAccount?coint="+coin;
	vip.ajax({
		url : actionUrl,
		dataType : "json",
		suc : function(json) {
			var htmSel = '<select name="waveVal" id="waveVal" style="width:280px;" errormsg="请选择打款账户">';
				htmSel += '<option value="0">关闭自动打币功能</option>';
			var isSelect = "";
			$.each(json.datas, function(i, obj){
				if(obj.id == "${curData.waveVal}"){
					isSelect = "selected=selected";
				}
				htmSel += '<option value="'+obj.id+'" btc="'+obj.funds+'" '+isSelect+'>'+obj.name+' | 余额：'+obj.funds+'</option>';
			});		
			htmSel += '</select>';	
			$("#autoFinanAccount").html(htmSel);
			$("#waveVal").UiSelect();
			
			$("#waveVal").change(function(){
				getBtcWallet($(this).val());
			});
		}
	});
}
/**
 * 获取莱特币钱包余额
 */
function getBtcWallet(accountId) {
	if(accountId.length==0 || accountId == 0){
		return;
	}
	var actionUrl = "/admin/wave/getWalletBalance?accountId="+accountId;
	vip.ajax({
		url : actionUrl,
		dataType : "json",
		suc : function(json) {
			if(json.des == 'notexsit'){
				Wrong("账户对应的打币钱包不存在。");
				return;
			}
		}
	});
}
</script>
</html>
