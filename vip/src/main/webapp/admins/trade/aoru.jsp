<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>添加行情地址</title>
		<jsp:include page="/admins/top.jsp" />
		<script type="text/javascript" src="${static_domain}/statics/js/common/DatePicker/WdatePicker.js"></script>

		<script type="text/javascript">
			$(function(){
				$("#add_or_update").Ui();
			});
			
			function ok(){
				var actionUrl = "/admin/trade/doaoru";
				vip.ajax( {
					formId : "add_or_update",
					url : actionUrl,
					div : "add_or_update",
					dataType : "json",
					suc : function(xml) {
						parent.vip.list.reload();
						parent.Right(xml.des);
					}
				});
			}
		</script>
	</head>
	
	<body>
		<div id="add_or_update" class="main-bd">
			<div class="form-line">
				<div class="form-tit" style="width:100px;">网站名称：</div>
				<div class="form-con">
					<input type="text" value="${trade.name}" style="width:300px;" name="name" id="name" pattern="limit(1,50)" errormsg="必填项"/>
					<font>如：chbtc、bter</font>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit" style="width:100px;">币种：</div>
				<div class="form-con">
					<select name="fundsType" id="fundsType" pattern="true" style="width:140px;" errormsg="请选择币种"> 
		               <option value="">--请选择--</option>
		               <c:forEach var="ft" items="${ft }">
				           <option id="fundT_${ft.value.fundsType }" value="${ft.value.fundsType }" <c:if test="${ft.value.fundsType==trade.fundsType }">selected="selected"</c:if>>${ft.value.propCnName }</option>
			           </c:forEach>
		            </select>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit" style="width:100px;">交易所标识：</div>
				<div class="form-con">
					<input type="text" value="${trade.symbol}" style="width:300px;" name="symbol" id="symbol" pattern="limit(1,50)" errormsg="必填项"/>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit" style="width:100px;">行情API：</div>
				<div class="form-con">
					<textarea style="width: 400px;height: 50px;" name="tickerUrl" id="tickerUrl" pattern="limit(1,500)">${trade.tickerUrl}</textarea>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit" style="width:100px;">兑BTC价格：</div>
				<div class="form-con">
					<fmt:formatNumber var="lastPrice" value="${trade.lastPrice }" pattern="0.00######"/>
					<input type="text" value="${lastPrice}" style="width:300px;" name="lastPrice" id="lastPrice" pattern="limit(0,15)" />
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit" style="width:100px;">备注：</div>
				<div class="form-con">
					<textarea style="width: 280px;height: 100px;" name="remark">${trade.remark }</textarea>
				</div>
			</div>
			<div class="form-btn" style="padding: 15px 0 0 80px;">
				<input id="id" name="id" type="hidden" value="${trade.id}" /> 
				<a class="btn" href="javascript:ok();"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a> 
				<a href="javascript:parent.Close();" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>
			</div>
		</div>
	</body>
</html>
