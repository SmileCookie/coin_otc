<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page isELIgnored="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>提现管理</title>
<jsp:include page="/admins/top.jsp" />
   <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script> 
		
		<style type="text/css">
		label.checkbox{  margin: 3px 6px 0 7px;}
		label.checkbox em{ padding-left:18px; line-height:15px; float:left; font-style:normal;}
		.form-search .formline{float:left;}
		.form-search p{float:none;}
		tbody.operations  td{ padding:0; border:0 none;}
		tbody.operations  td label.checkbox{ margin-top:10px; width:55px;}
		
.tab_head .search-submit, .tab_head .search-submit:hover {
    background: none repeat scroll 0 0 #2f7fc2;
    border: 1px solid #226eac;
    color: #fff;
    display: inline;
    float: right;
    font-family: 微软雅黑;
    height: 26px;
    line-height: 26px;
    margin: 0 0 0 5px;
    text-align: center;
    text-decoration: none;
    width: 55px;
}

.tab_head .search-submit.red{
    background: none repeat scroll 0 0 #db2222;
    border: 1px solid #c11010;
}
		</style>
		<script type="text/javascript">
			function exportUser(mCode){
				if(!couldPass){
					googleCode("exportUser", true);
					return;
				}
				couldPass = false;
				Close();
				var actionUrl = "/admin/btc/download/homeconfirm/exportUser?mCode="+mCode+"&isAll=true";
				var datas = FormToStr("searchContaint");
				location.href = actionUrl+"&"+datas;
			}
		</script>
	</head>
	<body>
	<jsp:include page="/admins/top.jsp" />
		<div class="mains">
			<jsp:include page="/admins/topTab.jsp" />
			<div class="col-main">
				<form autocomplete="off" name="searchForm" id="searchContaint">
				<div class="form-search" id="searchContaint">
					<input type="hidden" id="tab" name="tab" value="${tab }" />

					<div class="formline">
						<span class="formtit">确认时间：</span>
						<span class="spacing">从</span>
						<span class="formcon mr_5">
							<input type="text" class="inputW2 Wdate" id="confirmStartDate" name="confirmStartDate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : 'cn'})" style="width:140px;"/>
						</span>
						<span class="spacing">到</span>
						<span class="formcon">
							<input type="text" class="inputW2 Wdate" id="confirmEndDate" name="confirmEndDate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : 'cn'})" style="width:140px;"/>
						</span>
						
						<span class="formtit">提交时间：</span> 
						<span class="spacing">从</span>
						<span class="formcon mr_5">
							<input type="text" class="inputW2 Wdate" id="startDate" name="startDate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : 'cn'})" style="width:140px;"/>
						</span> 
						<span class="spacing">到</span> 
						<span class="formcon">
							<input type="text" class="inputW2 Wdate" id="endDate" name="endDate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',lang : 'cn'})" style="width:140px;"/>
						</span>

						<span class="formtit">提现金额：</span> 
						<span class="formcon mr_5">
							<input type="text" name="moneyMin" id="moneyMin" size="10" />
						</span> 
						<span class="spacing">到</span> 
						<span class="formcon">
							<input type="text" name="moneyMax" id="moneyMax" size="10" />
						</span>
					</div>
					<div style="clear: both;"></div>
					<div class="formline">
						<span class="formtit" style="margin-left: 10px;">提现用户ID：</span>
						<span class="formcon">
							<input type="text" name="userId" mytitle="请输入用户ID"  id="userId" size="15" />
						</span>
						<span class="formtit" style="margin-left: 10px;">打币类型：</span> 
						<span class="formcon">
							<select name="commandId" id="commandId" style="width:100px;"> 
				           		<option value="">--请选择--</option>
				             	<option value="1">自动</option>
				             	<option value="0">人工</option>
				             	<option value="2">免审</option>
				            </select>
						</span>
						<span class="formtit" style="margin-left:10px;">备注：</span>
						<span class="formcon">
							<input type="text" name="remark" id="remark" size="20"/>
						</span>
						<p>
							<input type="hidden" name="${coint.coinParam }" value="${coint.propTag }"  />
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
							<a href="javascript:vip.list.resetForm();" id="idReset" class="search-submit">重置</a>
							<a class="search-submit" href="javascript:exportUser('');">导出excel</a> 
						</p>
					</div>
						
				</div>		
				</form>		
				<div class="tab_head" id="userTab">			
					<a href="javascript:vip.list.search({tab : 'wait'});" id="wait" class="current"><span>待确认</span></a>
					<a href="javascript:vip.list.search({tab : 'confirm'});" id="confirm"><span>已确认</span></a>
					<a href="javascript:vip.list.search({tab : 'success'});" id="success"><span>已成功</span></a>
					<a href="javascript:vip.list.search({tab : 'fail'});" id="fail"><span>已失败</span></a>
					<a href="javascript:vip.list.search({tab : 'cancel'});" id="cancel"><span>已取消</span></a>
					<a href="javascript:vip.list.search({tab : 'sendding'});" id="sendding"><span>发送中</span></a>
					<a href="javascript:vip.list.search({tab : 'all'});" id="all"><span>所有提现</span></a>
					
					<a class="search-submit red" href="javascript:confirmAllWallet(false)">选中打币</a>
					<a class="search-submit" href="javascript:confirmAllWallet(true);">全部打币</a>
				</div>
				<div class="tab-body"  id="shopslist">
					<jsp:include page="ajax.jsp" />	
				</div>
			</div>
		</div>
		
<script type="text/javascript">
$(function(){ 
 	vip.list.ui();
	vip.list.funcName = "${coint.propCnName }下载";
	vip.list.basePath = "/admin/btc/download/";
});

function reload2(){
	Close();
	vip.list.reload();
}
var commid="";
function confirm(did){
	Iframe({
		    Url:"/admin/btc/download/homeconfirm/confirmWallet?id="+did+"&coint=${coint.propTag }",//下载
	        zoomSpeedIn		: 200,
	        zoomSpeedOut	: 200,
	        Width:560,
	        Height:390,
	        scrolling:"no",
	        isIframeAutoHeight:false,
        	isShowIframeTitle: true,
	        Title:"${coint.propTag }提现打款"
	});
}

<%--function merchantsConfirm(did){--%>
	<%--if("${coint.coin}"=="false"){--%>
		<%--//商户版不支持的提币，调用confirm方法--%>
		<%--confirm(did);--%>
		<%--return;--%>
	<%--}--%>
	<%----%>
	<%--Iframe({--%>
		<%--Url:"/admin/btc/download/merchantsConfirm?id="+did+"&coint=${coint.propTag }",//下载--%>
		<%--zoomSpeedIn		: 200,--%>
		<%--zoomSpeedOut	: 200,--%>
		<%--Width:560,--%>
		<%--Height:390,--%>
		<%--scrolling:"no",--%>
		<%--isIframeAutoHeight:false,--%>
		<%--Title:"提现到商户系统"--%>
	<%--});--%>
<%--}--%>


function confirmSuc(did){
		commid = did;
<%--	if(!couldPass){--%>
<%--		googleCode("confirmSuc", true);--%>
<%--		return;--%>
<%--	}--%>
	Ask2({Msg:"您确定要确认该提现成功吗？说明${coint.propTag }已经成功提现给用户", call:function(){
		
		Iframe({
		    Url:"/admin/btc/download/aoru?connId="+commid+"&useTypeId=6"+"&coint=${coint.propTag }",//下载
	        zoomSpeedIn		: 200,
	        zoomSpeedOut	: 200,
	        Width:560,
	        Height:460,
	        scrolling:"no",
	        isIframeAutoHeight:false,
            isShowIframeTitle: true,
	        Title:"${coint.propTag }提现账务录入"
		});
	}});
<%--	vip.list.reloadAsk({--%>
<%--		title : "您确定要确认该提现成功吗？说明BTC已经成功提现给用户",--%>
<%--		url : "/admin/btc/download/confirmSuc?did="+did--%>
<%--	});--%>
}

function confirmCancel(did){
	if(!couldPass){
		commid = did;
		googleCode("confirmCancel", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({
		title : "您确定要取消该${coint.propTag }提现吗？取消此笔提现不进入提现流程。",
		url : "/admin/btc/download/confirmCancel?did="+commid+"&mCode="+did+"&coint=${coint.propTag }"
	});
}
function confirmFail(did){
	if(!couldPass){
		commid = did;
		googleCode("confirmFail", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({
		title : "打币状态已成功，如果记录未被确认，可能要重新打币，您可以进行些操作。确认要这样操作吗？有可能会重新打币，请慎重操作。",
		url : "/admin/btc/download/confirmFail?did="+commid+"&mCode="+did+"&coint=${coint.propTag }"
	});
}
function sucFail(did){
	if(!couldPass){
		commid = did;
		googleCode("sucFail", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({
		title : "该笔提现已成功，您确定要确认该提现失败吗，失败后用户资金会返还。",
		url : "/admin/btc/download/sucFail?did="+commid+"&mCode="+did+"&coint=${coint.propTag }"
	});
}

function failToSuc(did){
	if(!couldPass){
		commid = did;
		googleCode("failToSuc", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({
		title : "该记录已经被确认打币未确认，您确定要标记该记录已成功吗？",
		url : "/admin/btc/download/failToSuc?did="+commid+"&mCode="+did+"&coint=${coint.propTag }"
	});
}

/**
 * 钱包自动打币成功,根据实际产生的手续费扣除
 * @param {Object} did
 */
function fees(did){
	Iframe({
		    Url:"/admin/btc/download/fees?connId="+did+"&useTypeId=6"+"&coint=${coint.propTag }",//比特币提现扣除手续费，用途是：比特币提现
	        zoomSpeedIn		: 200,
	        zoomSpeedOut	: 200,
	        Width:560,
	        Height:460,
	        scrolling:"no",
	        isIframeAutoHeight:false,
        	isShowIframeTitle: true,
	        Title:"${coint.propTag }打币成功录入实际手续费"
	});
}

/**
 * 打币失败后，返还打币账户资金
 * @param {Object} did
 */
function failEntry(did){
	Iframe({
		    Url:"/admin/btc/download/fees?connId="+did+"&useTypeId=10"+"&coint=${coint.propTag }",//比特币提现失败返还账户，用途是：比特币其他
	        zoomSpeedIn		: 200,
	        zoomSpeedOut	: 200,
	        Width:560,
	        Height:460,
	        scrolling:"no",
	        isIframeAutoHeight:false,
			isShowIframeTitle: true,
	        Title:"${coint.propTag }打币失败返还账户资金"
	});
}
//全选按钮的方法
function selectAll(){
	
	changeCheckBox('delAll'); 
	$(".hd .checkbox").trigger("click");
}

var uuid = "";
function firstCheck(did,userId){
	if(!couldPass){
		commid = did;
		uuid = userId;
		googleCode("firstCheck", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({
		title : "您确定用户‘"+$("#text_"+uuid).text()+"’的该笔记录可以打出吗？",
		url : "/admin/btc/download/firstCheck?did="+commid+"&mCode="+did+"&coint=${coint.propTag }"
	});
}

function confirmAllWallet(isAll){
	var ids="";
	var datas = "";
	if(!isAll){
		$(".checkItem").each(function(){
			var id=$(this).val();
			if($(this).attr("checked")==true){
				ids+= id+",";
			}
		});
		var list=ids.split(",");
		if(list.length==1){ 
			Wrong("请选择一项"); 
			return;
		}
	}else{
		datas = FormToStr("searchContaint");
	}
	
	Iframe({
		    Url:"/admin/btc/download/homeconfirm/confirmAllWallet?eIds="+ids+"&isAll="+isAll+"&"+datas+"&coint=${coint.stag }",//下载
	        zoomSpeedIn		: 200,
	        zoomSpeedOut	: 200,
	        Width:560,
	        Height:390,
	        scrolling:"no",
	        isIframeAutoHeight:false,
        	isShowIframeTitle: true,
	        Title:"${coint.propTag }批量提币打款"
	});
}

//批量打币操作
function confirmAll(isAll, datas){
	var ids="";
	if(!isAll || isAll == "false"){
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
		url : "/admin/btc/download/homeconfirm/confirmAll?eIds=" + ids+"&isAll="+isAll+"&"+datas,
		formId : "searchContaint",
		dataType : "json",
		suc : function(json) {
			Right(json.des, {callback : "reload2()"});
		}
	});
}

function retrySend(did){
	if(!couldPass){
		commid = did;
		googleCode("retrySend", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({
		title : "确定要重新打币吗？重复打币有可能会从钱包支出两次，请再次确认之后再操作。",
		url : "/admin/btc/download/retrySend?connId="+commid+"&mCode="+did+"&coint=${coint.propTag }"
	});
}
</script>
	</body>
</html>
