<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>审核不通过填写原因页面</title>
   <jsp:include page="/admins/top.jsp" />
</head>
<body>
<div id="col_main">
	<input type="hidden" id="roleId" value="${roleId }"/>
	<div class="main-bd">
		<div class="form-line" style="display: none;">
			<div class="form-tit">系统理由：</div>
			<div class="form-con">
				<div style="float:left;padding-top: 3px;">
				<select id="mySaveDes" style="width:330px;">
					<option value="">选择已有的系统理由</option>
					<c:forEach items="${reasons}" var="rs">
						<option value="${rs.cont }">${rs.showCont }</option>
					</c:forEach>
				</select>
				</div>
				<c:if test="${roleId == 1}">
					<div style="float:left;line-height:25px;padding-left: 0px;width:100%;">
							<a href="javascript:void(0)" id="setReasons">设置系统自定义理由</a>
					</div>		
				</c:if>	
			</div>
		</div>
		
		<c:if test="${roleId != 1}">
		<div class="form-line">
			<div class="form-tit">我的理由：</div>
			<div class="form-con">
				<select id="mySaveDes" style="width:430px;">
					<option value="">选择已有的自定义理由</option>
					<c:forEach items="${reasons}" var="rs">
						<option value="${rs.cont }">${rs.showCont }</option>
					</c:forEach>
				</select>
				
				<div style="float:left;line-height:25px;padding-left: 0px;width:100%;display: none;">
					<a href="javascript:void(0)" id="setReasons">设置我的自定义理由</a>
				</div>	
			</div>
		</div>
		</c:if>
		
		<div class="form-line">
			<div class="form-tit">附加理由：</div>
			<div class="form-con">
				<textarea pattern="limit(6,8000)" mytitle="请输入审核的原因。" errmsg="字符长度应大于5小于500" style="width:350px;height:140px;" position="s" id="audit_No_Reason" name="audit_No_Reason" pattern="limit(0,500)"></textarea>
				<br/>
				<font color="#4488bd">【提示：</font><font color="#CC5600">* 填写审核不通过的原因</font><font color="#4488bd">】</font>
			</div>
		</div>
		
		<div class="form-btn">
			<input type="hidden" id="beanId" value="${beanId }"/>
			<a href="javascript:void(0)" id="do_submit" class="btn"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a> <a href="javascript:parent.Close();" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>
		</div>
	</div>
</div>

<script type="text/javascript">
$(function(){
	$("#audit_No_Reason").UiText();
	$("#mySaveDes").UiSelect();
	$("#userSaveDes").UiSelect();
	
	/********审核不通过部分*********/
	$("#do_submit").bind("click",function(){
		var reason=spliceReason();
		if($.trim(reason).length<=3){
			Wrong("请认真选填理由！");
			return;
		}
		// Lodding("正在保存数据...");
		var beanId=$("#beanId").val();
		propName="";
		/////调用父页面的修改属性方法 
		parent.unpass(beanId, reason,"${operation}"); 
	});
	//hasReasonBind();
});//结束body load部分

	/******拼接 审核不通过的 原因 ******/
	function spliceReason(){
		var reason="";
		var itemIndex=0;
		$("input[name='reason']").each(function(){
			if($(this).attr("checked")){
				itemIndex++;
				reason+="·"+$(this).val()+"<br>";
			}
		});
		itemIndex++;
		reason+="·"+$("#audit_No_Reason").val();
		return reason;
	}
	/****重置表单***/
	function Reset(){
		$("input[name='reason']").each(function(){
			if($(this).attr("checked")){
				$(this).parent().find("a").trigger("click");
			}
		});
		$("#audit_No_Reason").val("");
	}

	function hasReasonBind(){
		var evaluateDesText = $("#audit_No_Reason");
		evaluateDesText.setCaret();
		$("#mySaveDes").change(function(){
			evaluateDesText.insertAtCaret($(this).val()); 
		});
		$("#userSaveDes").change(function(){
			evaluateDesText.insertAtCaret($(this).val()); 
		});
		//设置鉴定理由
		$("#setReasons").click(function(){
			var url="/admin/evaluate/reason/nopass-"+$("#type").val();
			Iframe({Url:url,Title:"设置自定义理由", Width:500, Height:360,isShowIframeTitle:true,overlayShow:true,modal:false});
			return false;
		});
	}

	function reShowSelect(htm){
		if($("#roleId").val() == 1){
			$("#mySaveDes").html(htm);
			$("#mySaveDes").UiSelect();
		}else{
			$("#userSaveDes").html(htm);
			$("#userSaveDes").UiSelect();
		}
	}
</script>
</body>
</html>
