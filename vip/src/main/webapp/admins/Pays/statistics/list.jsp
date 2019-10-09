<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>用户资金管理</title>
 <jsp:include page="/admins/top.jsp" />
      <style type="text/css">
      .commodity_action a{padding: 0 5px;}
		label.checkbox{  margin: 3px 6px 0 7px;}
		label.checkbox em{ padding-left:18px; line-height:15px; float:left; font-style:normal;}
		.operation { height: 20px; line-height: 20px; text-align: left;margin-top: 10px;padding-left: 10px;}
		tbody.operations  td{ padding:0; border:0 none;}
		tbody.operations  td label.checkbox{ margin-top:10px; width:55px;}
      </style>
      <script type="text/javascript">
		$(function(){ 
		 	vip.list.ui();
			vip.list.basePath = "/admin/pay/statistics/";
		});
		
		function reload2(obj){
			var datas = FormToStr("searchContaint");
			datas = "?"+datas;
			var name = $(obj).attr("name");
			if(name){
				datas += "&"+name+"="+$(obj).val();
			}
			var urls="ajax"+datas;
			vip.ajax({
				url : vip.list.basePath+urls, 
				dataType : "text",
				suc : function(text){
					$("#shopslist").html(text);
				}
			});
		}
		//全选按钮的方法
		function selectAll(){
			
			changeCheckBox('delAll'); 
			$(".item_list_bd .checkbox").trigger("click");
		}
      </script>
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
							余额：
						</span>
						<span class="formContainer">
							<input type="text" id="minB" name="minB" style="width:120px;"/>
						到
							<input type="text" id="maxB" name="maxB" style="width:120px;"/>
						</span>
					</p>
					<p>
						<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
						<a class="search-submit" href="javascript:vip.list.resetForm();" id="idReset">重置</a>
						<a class="search-submit" href="javascript:exportUser('');">导出excel</a>
					</p>
				</form>
			</div>
			
			<div class="tab_head" id="userTab">
				<c:forEach items="${coinMap}" var="coin">
					<a href="javascript:vip.list.search({tab : '${coin.key}'});" id="${coin.key}" class="${tab==coin.key?'current':''}"><span>${coin.key.toUpperCase()}</span></a>
				</c:forEach>
				
			</div>
			
			<div class="tab-body" id="shopslist">
				<jsp:include page="ajax.jsp" />	
			</div>
			
	</div>
</div>
<script type="text/javascript">
function exportUser(mCode){
	if(!couldPass){
		googleCode("exportUser", false);
		return;
	}
	couldPass = false;
	Close();
	var actionUrl = "/admin/pay/statistics/exportUser?mCode="+mCode+"&isAll=true";
	var datas = FormToStr("searchContaint");
	location.href = actionUrl+"&"+datas;
}
</script>	
</body>
</html>

