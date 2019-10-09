<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
<jsp:include page="/common/head.jsp" />
<style type="text/css">
.loan-info { width: 95%; margin: 0 auto; position: relative; zoom: 1; font-family:"微软雅黑"; }
.loan-info h1 { font-size: 14px; font-weight: 500; font-family: 微软雅黑; height: 30px; line-height: 30px; padding: 0 0 0 1px; }
.loan-info-table td { padding: 10px 10px; border: 1px #eeeeee solid; color: #666666; text-align: left; font-size:20px; font-family:"微软雅黑"; }
.loan-info-table td.tit { width: 125px; text-align: right; background: #fafafa; color: #888888; font-size:12px; }
.loan-info-table td span { color: #CC0000; }
.loan-info-table td span.schedule { display: inline-block; overflow: hidden; vertical-align: -2px; }
.loan-info-table td span.schedule i { background: #ddd; border-radius: 3px 3px 3px 3px; display: inline-block; height: 12px; width: 100px; }
.loan-info-table td span.schedule i u { background: #F06000; border-radius: 3px 3px 3px 3px; display: inline-block; height: 12px; width: 50px; }
.loan-info-list { padding-top: 15px; }
.loan-info-list li { height: 34px; line-height: 34px; padding-bottom: 10px; color: #666666; text-align: left; }
.loan-info-list li .red { font-size: 20px; font-family: "微软雅黑"; }
.loan-info-list li span.tit { width: 125px; text-align: right; display: inline-block; padding: 0 10px; color: #999999; }
.loan-info-list li input { width: 220px; padding: 5px 10px; height: 32px; line-height: 22px; font-size: 18px; color: #666; }
.loan-info-list li span.prompt { color: #666666; }
.loan-info-list li span.red { color: #CC0000; }
.loan-info-list li a.btn { background: #F9F9F9; border: 1px solid #D9D9D9; border-radius: 5px 5px 5px 5px; box-shadow: 0 1px 0 rgba(255, 255, 255, 0.3) inset, 0 1px 1px rgba(50, 50, 50, 0.05); color: #666666; display: inline-block; line-height: 24px; padding: 0 5px; text-align: center; width: 60px; text-decoration: none; }
.loan-info-list li a { color: #3366CC; }
.loan-info-button { padding-top: 10px; text-align: center; }
.loan-info-button a { background: #B61D00; border: 1px solid #AA1800; border-radius: 5px 5px 5px 5px; box-shadow: 0 1px 0 rgba(255, 255, 255, 0.3) inset, 0 1px 1px rgba(50, 50, 50, 0.05); color: #FFFFFF; display: inline-block; font-family: 微软雅黑; font-size: 18px; font-weight: 500; line-height: 38px; padding: 0 10px; text-align: center; text-decoration: none; text-shadow: 0 1px 1px rgba(0, 0, 0, 0.2); width: 150px; }
.loan-info-caption { background: #FFFCF0; position: absolute; bottom: -10px; right: 0; padding: 10px; color: #666666; }
.loan-info-caption strong { display: block; color: #333333; padding: 10px 0; }
.loan-info-caption table { width: 300px; }
.loan-info-caption td { border: 1px #eeeeee solid; background: #fff; padding: 5px; text-align: center; }
.loan-info-caption .txt td { color: #999999; }
/*******control.css end****/
.header { }
.Toolbar { width: 100%; text-align: left; }
.Toolbar h2 { border-bottom: 1px solid #F2F2F2; height: 36px; line-height: 36px; text-indent: 12px; font-size: 14px; font-weight: bold; }
.ps1-close { color: #D5D5D5; cursor: pointer; font-size: 14px; height: 16px; line-height: 16px; position: absolute; right: 10px; text-align: center; top: 10px; width: 16px; }
.ps1-close:hover { color: #666666; text-decoration: none; }
.jqTransformRadioWrapper { margin-top: 10px; margin-right:5px; }
.do { padding:5px 0px 25px; text-align:center;}
</style>
</head>
<body style="background: #FFFFFF;">
<div class="header">
  <div class="Toolbar">
    <h2>
      <c:choose>
        <c:when test="${curLoan.isIn }"> ${L:l(lan,'确认投资信息')} </c:when>
        <c:otherwise> ${L:l(lan,'确认借入信息')} </c:otherwise>
      </c:choose>
    </h2>
    <div class="ps1-close" onclick="parent.Close();" style="font-weight:bold;">×</div>
  </div>
</div>
<div class="loan-info" id="uimain">
  <table class="loan-info-table" style="width:100%;margin:10px 0 0 0;">
    <%--<tr>
	    <td class="tit">总金额：</td>
	    <td><span>${curLoan.fundsType.tag }<fmt:formatNumber value="${curLoan.amount }" pattern="0.0#######" /></span></td>
	  </tr>--%>
    <tr>
      <td class="tit">${L:l(lan,'确认借入信息')}  ${L:l(lan,'剩余可借入金额')}：</td>
      <td>${curLoan.getFt().unitTag }
        <fmt:formatNumber value="${curLoan.balance }" pattern="0.0#######" /></td>
    </tr>
    <tr>
      <td class="tit">${L:l(lan,'确认借入信息')} ${L:l(lan,'日利率')}：</td>
      <td><span>
        <fmt:formatNumber value="${curLoan.rateOfDayShow }" pattern="0.0##" />
        %</span></td>
    </tr>
    <tr>
      <c:if test="${curLoan.isIn ==true }">
      	<td class="tit">${L:l(lan,'我的可投资金额'}：</td>
      </c:if>
      <c:if test="${curLoan.isIn ==false }">
      	<td class="tit">${L:l(lan,'我的可借入金额'}：</td>
      </c:if>
      
      <td>
        <c:choose>
          <c:when test="${curLoan.isIn }">
          	<c:if test="${curUser.funds!=null }">
          		<c:forEach items="${curUser.funds}" var="fund">
          			<c:if test="${fund.key.toUpperCase() ==curLoan.getFt().propTag}">
          				<c:set value="${fund.value.canLoan}" var="loans"/>
          			</c:if>
          		</c:forEach>
          	</c:if>
          	
          <%-- 	<c:set value="${curUser.loansOut[curLoan.fundsType.key - 1] }" var="loans"/>
           <fmt:formatNumber value="${curUser.loansOut[curLoan.fundsType.key - 1] }" pattern="${curLoan.fundsType.pattern}"/>--%>
          </c:when>
          <c:otherwise>
          	 	<c:if test="${curUser.funds!=null }">
          		<c:forEach items="${curUser.funds}" var="fund">
          			<c:if test="${fund.key.toUpperCase() ==curLoan.getFt().propTag}">
          				<c:set value="${fund.value.canLoan}" var="loans"/>
          			</c:if>
          		</c:forEach>
          	</c:if>
           <%--   	<c:set value="${curUser.loansIn[curLoan.fundsType.key - 1] }" var="loans"/>
        <fmt:formatNumber value="${curUser.loansIn[curLoan.fundsType.key - 1] }" pattern="${curLoan.fundsType.pattern}"/>--%>
          </c:otherwise>
        </c:choose>
        <span class="red">${curLoan.getFt().unitTag } <i id="availableAmount"><fmt:formatNumber value="${loans }" pattern="0.0#######"/></i> </span>
      </td>
    </tr>
    <%-- 
	  <tr>
	    <td class="tit">还款方式：</td>
	    <td>${curLoan.repayWay.value }</td>
	  </tr>
	  --%>
    <%--<tr>
	    <td class="tit">已完成：</td>
	    <td><span class="schedule"><i><u style="width:${curLoan.hasBidRate2}px"></u></i></span> ${curLoan.hasBidRate}%</td>
	  </tr>--%>
    <%-- 
	  <tr>
	    <td class="tit">最低投标金额：</td>
	    <td>${curLoan.fundsType.tag } <fmt:formatNumber value="${curLoan.lowestAmount}" pattern="0.0#######" /></td>
	  </tr>
	  <tr>
	    <td class="tit">最高投标金额：</td>
	    <td><c:if test="${curLoan.gxeshow!='不限' }">${curLoan.fundsType.tag } </c:if>${curLoan.gxeshow }</td>
	  </tr>
	  --%>
  </table>
  <ul class="loan-info-list">
    <%--<li><span class="tit" >我的可${curLoan.isIn ? "投资" : "借入"}金额：</span> <span class="red">${curLoan.fundsType.tag }<i id="availableAmount">
      <c:choose>
        <c:when test="${curLoan.isIn }">
          <fmt:formatNumber value="${curUser.loansOut[curLoan.fundsType.key - 1] }" pattern="${curLoan.fundsType.pattern}"/>
        </c:when>
        <c:otherwise>
          <fmt:formatNumber value="${curUser.loansIn[curLoan.fundsType.key - 1] }" pattern="${curLoan.fundsType.pattern}"/>
        </c:otherwise>
      </c:choose>
      </i> </span> </li>--%>
    <c:if test="${curLoan.isIn}">
		      <li> <span class="tit" style="float:left;">${L:l(lan,'平仓风险控制')}：</span> <span style="float: left; margin-right:15px;">
		        <input type="radio" value="1" name="riskManage" checked="checked"/>
		        ${L:l(lan,'自担风险（费用20%利润）')}</span> <span style="float: left;">
		        <input type="radio" value="2" name="riskManage"/>
		        ${L:l(lan,'我只要本金币种（费用30%利润）')}</span> </li>
		    </c:if>
		    <li><span class="tit">${L:l(lan,'借入金额')}：</span>
		      <input name="amount" position="s" valueDemo="${L:l(lan,'单笔最低')}<fmt:formatNumber value="${minUnit }" pattern="0.0#" />" pattern="limit(1,10);num();amoutCheck(${minUnit } , ${loans })" onkeyup="xswCheck(this , 2 , 1)" errormsg="${L:l(lan,'请输入正确的借贷金额，本次借贷的最少金额')}<fmt:formatNumber value="${minUnit }" pattern="0.0#"/>" mytitle="${L:l(lan,'请输入正确的借贷金额')}" id="bidAmount" type="text" size="30" />
		    </li>
    
<%--     <c:if test="${not(curLoan.isIn)}"> --%>
<%-- 	    <c:if test="${(not empty(list)) and (fn:length(list) gt 0)}"> --%>
<!-- 	          <li><span class="tit" style="float:left;margin-right:4px;">使用免息券：</span> -->
<%-- 	              	<select style="width:228px;" id="couponList" name="couponList" onchange="chooseFreeCoupon(this, ${curLoan.fundType })"> --%>
<!-- 	               		<option value="0">---请选择---</option> -->
<%-- 	               		<c:forEach items="${list }" var="obj"> --%>
<%-- 	               			<c:if test="${obj.status eq 2 && obj.couldUseAmount >= 0.01}"> --%>
<%-- 		               			<option value="${obj.id }"> --%>
<%-- 		               				额度<c:if test="${curLoan.fundType eq 1 }">￥</c:if><c:if test="${curLoan.fundType eq 2 }">฿</c:if><c:if test="${curLoan.fundType eq 3 }">Ł</c:if><c:if test="${curLoan.fundType eq 4 }">E</c:if><c:if test="${curLoan.fundType eq 5 }">e</c:if><fmt:formatNumber value="${obj.couldUseAmount}" pattern="0.00"/>，免息${obj.usedays }天 --%>
<!-- 		               			</option> -->
<%-- 	               			</c:if> --%>
<%-- 	               		</c:forEach> --%>
<!-- 	               	</select> -->
<!-- 	               	<div id="freeTips" class="tip" style="display:none;">&nbsp;&nbsp;有效期至<span id="endDate"></span></div> -->
<!-- 	           </li> -->
<!-- 	           <li id="freeText" style="display:none;"><span class="tit" style="float:left;margin-right:4px;">本次使用额度：</span> -->
<!-- 	      		 <input type="text" id="useamount"  size="30"name="useamount" onkeyup="xswCheck(this , 2 , 1)" class="dai_input" mytitle="请填写您本次要使用额度" errormsg="请填写您本次要使用额度" value="" /> -->
<!-- 	      	   </li> -->
<!-- 	      	    <input type="hidden" name="freecouponId" id="freecouponId" value="0"/> -->
<%-- 	    </c:if> --%>
<%--     </c:if> --%>
    
<!--     <li><span class="tit">资金安全密码：</span> -->
<!--       <input name="password" type="password" size="30" pattern="limit(4,18)" errormsg="请输入合法的资金安全密码" /> -->
<%--       <a href="${vip_domain}/service/self/forgetsafepwd" target="_blank">&nbsp;忘记资金安全密码</a> </li> --%>
    <%--  
	<li><span class="tit">验证码：</span><input name="" type="text" size="10" /></li>
	--%>
  </ul>
  <%--	<div class="loan-info-button">--%>
  <%--		<input type="hidden" name="id" value="${curLoan.id}"/>--%>
  <%--		<a href="javascript:dosubmit();">确定</a>--%>
  <%--	</div>--%>
  <hr/>
  <div class="do">
    <input type="hidden" name="id" value="${curLoan.id}"/>
    <a class="alibtn_orange35" tabindex="5" href="javascript:parent.Close();">
    <h4>${L:l(lan,'取消')}</h4>
    </a> <a class="alibtn_orange35" tabindex="8" style="margin-left:36px;" href="javascript:dosubmit();">
    <h4>${L:l(lan,'提交')}</h4>
    </a> </div>
</div>

<script type="text/javascript">
	$("#uimain").Ui();
	$(function(){
		var minUnit = ${minUnit};
		var loans = ${loans};
		if(parseFloat(minUnit) > parseFloat(loans)){
			$("#bidAmount").attr("disabled","disabled");
		}else{
			$("#bidAmount").removeAttr("disabled");
		}
	})
	function dosubmit(){
		if(!vip.user.checkLogin()){
			return;
		}
		var datas = FormToStr("uimain"); 
		if(datas == null){return;}
		vip.ajax({
			 needLogin : true, //需要登录
			 formId : "uimain",//表单ID
			 div : "uimain",//lodding的div
			 url : "/manage/loan/record/doLoan" ,
			 dataType : "json",
			 suc : function(json){
				 parent.Right(json.des , {callback : "reloadPage();"});
			 }
		});
	} 
	
	function huan(id){
		vip.list.reloadAsk({title : "${L:l(lan,'确定要还款吗')}？" , url : "/users/admin/finance/repayofqi/huan?id="+id});
	}
	
	function chooseFreeCoupon(o, fundsType){
			var id = $(o).find("option:selected").val();
			if(id != 0){
			
				vip.ajax({
					 url : "/manage/loan/freecoupon/freeCoupon?id=" + id + "&fundsType=" + fundsType, 
					 dataType : "json",
					 suc : function(json){
						 if(json.isSuc){
							 var data = json.datas;
							 var couldUseAmount = data.couldUseAmount;
							 var endDate = data.endFormatDate;
							 //$("#couldUseAmount").text(couldUseAmount);
							 $("#endDate").text(endDate);
							 $("#freeTips").show();
							 $("#freeText").show();
							 $("#useamount").attr("pattern", "limit(1,10);num()");
						 }else{
							 Wrong(json.des);
						 }
					  }
				});
				$("#freecouponId").val(id);
			}else{
				$("#freeTips").hide();
				$("#freeText").hide();
				$("#freecouponId").val(0);
				$("#useamount").val('');
				$("#useamount").removeAttr("pattern");
			}
		}
	
	function amoutCheck(v , min , max){
		var amount = $("input[name='amount']").val();
		if(amount < min){
			return false;
		}
		if(amount > ${curLoan.balance }){
			return false;
		}
		if(amount > max){
			return false;
		}
		return true;
	}
	
	function xswCheck(obj , max , type){//小数位验证
		var m = max + 1;
		var iv = $(obj).val();
		if(iv.indexOf(".") > 0 && iv.substring(iv.indexOf(".")).length > m){
				$(obj).val(iv.substring(0 , iv.indexOf(".") + m));
		}
	}
	
	$("body").click(function(){
		$(".tipsr").remove();
	});
</script>
</body>
</html>
