<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>
<jsp:include page="/admins/top.jsp" />

<style type="text/css">
	.tableoutDiv{padding: 10px 10px 20px 10px;float:left;width:620px;}
	.tableoutDiv .outtab{width:610px;height:280px;}
	.tableoutDiv .outtab td{vertical-align: top;line-height: 22px;}
	.tableoutDiv .outtab .lefts{width:100px;height:auto;text-align: right;padding:10px 10px 0px 0px;}
</style>
<script type="text/javascript"> 
var inAjaxing=false;
$(function(){
	//$("#antique_evaluate").Ui();
	$("#audit_No_Reason").UiText();
	$("#mySaveDes").UiSelect();
/********审核不通过部分*********/
	 // hasReasonBind();
});//结束body load部分

	/******拼接 审核不通过的 原因 ******/
	function spliceReason(){
		var reason="";
		var itemIndex=0;
		$("input[name='reason']").each(function(){
			if($(this).attr("checked")){
				itemIndex++;
				reason+=itemIndex+"："+$(this).val()+"<br>";
			}
		});
		itemIndex++;
		reason+=itemIndex+"："+$("#reason").val();
		return reason;
	}
	/****重置表单***/
	function Reset(){
		$("input[name='reason']").each(function(){
			if($(this).attr("checked")){
				$(this).parent().find("a").trigger("click");
			}
		});
		$("#reason").val("");
	}

	function hasReasonBind(){
		var evaluateDesText = $("#reason");
		evaluateDesText.setCaret();
		$("#mySaveDes").change(function(){
			evaluateDesText.insertAtCaret($(this).val()); 
		});
		//设置鉴定理由
		$("#setReasons").click(function(){
			var url="/admin/evaluate/reason/nopass-35";
			Iframe({Url:url,Title:"设置自定义理由", Width:580, Height:390,isShowIframeTitle:true,overlayShow:true,modal:false});
			return false;
		});
	}

	function reShowSelect(htm){
		$("#mySaveDes").html(htm);
		$("#mySaveDes").UiSelect();
	}
	
	function ok(mCode){
		if(inAjaxing){
			Wrong("请求中...");
			return;
		}
		  var datas=FormToStr("bankBox");
		  if(datas==null)
		  return;
		if(!couldPass){
			googleCode("ok", true);
			return;
		}
		couldPass = false;
		  inAjaxing=true;
		  $("#bankBox").Loadding({OffsetXGIF:270,OffsetYGIF:80});
		    $.ajax({
				   async:true,
				   cache:false,
				   type:"POST",
				   dataType:"xml",
				   data:datas,
				   url:"/admin/btc/freez/doFreez?mCode="+mCode+"&coint=${coint.propTag }",
				   error:function(xml){Wrong("添加数据失败,请检查网络，可能是网络过慢导致超时或者远程服务出现故障!");$("#bankBox").Loadding({IsShow:false});inAjaxing=false;},
				   timeout:60000,
				   success:function(xml){
					   inAjaxing=false;
					   $("#bankBox").Loadding({IsShow:false}); 
			           if($(xml).find("State").text()=="true"){
				            parent.Right($(xml).find("Des").text(),{callback:"reload2()"});
				        }else{
				             Wrong($(xml).find("Des").text());
				        }
					}//ajax调用成功处理函数结束
			});//ajax结束
		}
</script>
</head>
<body>
	<div class="tableoutDiv" id="bankBox">
			<table class="outtab">
				<tr>
					<td class="lefts">用户账号：</td>
					<td>
						<div style="height:30px;float:left;padding: 10px 0px 0px 0px;">
							<input type="text" value="${userName }"  name="userName" id="userName" pattern="limit(2,20)" mytitle="请输入您要冻结资金的用户名" errormsg="请输入用户名2-20个字"/>
						</div>
					</td>
				</tr>
			
				<tr>
					<td class="lefts">冻结${coint.propTag }数量：</td>
					<td>
						<fmt:formatNumber var="total_money" value="${total }" pattern="0.00######"/>
						<div style="height:30px;float:left;padding: 10px 0px 0px 0px;">
							<input type="text" value="${total_money }" size="30" mytitle="请输入扣除数量 数字形式"  pattern="num();limit(1,15)" name="money"  id="money" errormsg="请输入扣除数量数字形式" />
							<font color="red">可冻结总额：${total_money }</font>
						</div> 
					</td>
				</tr>
			
				<tr style="display: none;">
					<td class="lefts">可选备注：</td>
					<td>
             			<div style="height:30px;float:left;padding: 10px 0px 0px 0px;">
							<span style="float:left;">
								<select id="mySaveDes">
									<option value="">选择已有的自定义理由</option>
									<c:forEach items="${reasons}" var="rs">
										<option value="${rs.cont }">${rs.showCont }</option>
									</c:forEach>
								</select>
							</span>
						</div>
						<div style="float:left;line-height:25px;padding-left: 0px;width:100%;">
								<a href="javascript:void(0)" id="setReasons">设置自定义理由</a>
						</div>						
					</td>
				</tr>
				<tr>
					<td class="lefts">附加备注：</td>
					<td>
						<textarea pattern="limit(2,100)" mytitle="填写备注信息" errmsg="字符长度应大于0小于100" style="width:350px;height:80px;" position="s" id="reason" name="reason"></textarea>
						<br/>
						<font color="#4488bd">【提示：</font><font color="#CC5600">* 填写备注信息</font><font color="#4488bd">】</font>
					</td>
				</tr>
				<tr>
					<td class="lefts">
					</td>
					<td>
						<div class="formBtn form-search" id="FormButton">
							<a class="search-submit ok" href="javascript:ok()" >确定</a>
							<a onclick="javascript:parent.Close();" id="cancle" class="search-submit cancel" href="javascript:void(0);">取消</a>
						</div>
					</td>
				</tr>
			</table>
		</div>
</body>
</html>
