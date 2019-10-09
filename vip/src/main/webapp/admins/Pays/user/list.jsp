<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>用户资金管理</title>
 <jsp:include page="/admins/top.jsp" />
   <script type="text/javascript" src="${static_domain }/statics/js/common/DatePicker/WdatePicker.js" ></script>
    
   <script type="text/javascript" src="${static_domain }/statics/js/admin/pay/user/list.js" ></script> 
		
		<script type="text/javascript">
			var counter=function(){
				var value=0;
				return {
						increment : function(incNum){
							value+=typeof incNum == 'number' ? incNum : 0;
						},
						getValue : function(){
							return value;
						}
					   };
			}

			var c=new counter();
			c.increment(10);
			c.increment(5);
		</script>
      <style type="text/css">
      .commodity_action a{padding: 0 5px;}
      </style>
	</head>
<body>
<div class="mains">
	<div class="col-main">
			<div class="form-search" id="listSearch">
				<form autocomplete="off" name="searchForm" id="searchContaint">
					<p class="formCloumn">
						<span class="formText">
							用户ID：
						</span>
						<span class="formContainer">
							<input type="text" id="userId" name="userId" style="width:120px;"/>
						</span>
					</p>
					<p class="formCloumn">
						<span class="formText">
							排序：
						</span>
						<span class="formContainer">
							<select id="orderWay" name="orderWay">
								<option value="1">用户ID</option>
								<option value="2">总余额</option>
							</select>
						</span>
					</p>
					<p>
						<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
						<a class="search-submit" href="javascript:vip.list.resetForm();" id="idReset">重置</a>
						<a class="search-submit" id="idSearch" href="javascript:btcCharge();">充值</a> 
					</p>
				</form>
			</div>
			
			<div class="tab_head" id="userTab">
				<a href="javascript:vip.list.search({tab : 'all'});" id="all" class="current"><span>所有用户</span></a>
				<c:forEach var="ft" items="${ft }">
				   <a href="javascript:vip.list.search({tab : '${ft.value.stag }'});" id="${ft.value.stag }"><span>${ft.value.propTag }</span></a>
	            </c:forEach>
			</div>
			
			<div class="tab-body" id="shopslist">
				<jsp:include page="ajax.jsp" />	
			</div>
			
	</div>
</div>
<script type="text/javascript">
function transfer(id){
	var url = "/admin/pay/user/transferFinance";
	if(id){
		url += "?id="+id;
	}
	
	Iframe({
		Url : url,
		Width : 550,
		Height : 430,
		isShowIframeTitle: true,
		Title : "转移账户资金"
	});
}

function setDiscount(id){
	Iframe({
		Url:"/admin/pay/user/setFees?userId="+id,
		Width:600,
		Height:356,
		Title:"设置收益折扣率",
        isShowIframeTitle: true,
		scrolling:"no"
	});
}
</script>	
</body>
</html>

