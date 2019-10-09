<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>api管理列表</title>
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
			vip.list.basePath = "/admin/api/";
		});
		
		function reload2(){
			Close();
			vip.list.reload();
		}
		function setStatus(id,userName){
			Iframe({
				Url:"/admin/api/aoru?id="+id,
				Width:600,
				Height:300,
				Title:"设置用户 "+userName+" API状态",
                isShowIframeTitle: true,
				scrolling:"no"
			});
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
							用户名：
						</span>
						<span class="formContainer">
							<input type="text" id="userName" name="userName" style="width:120px;"/>
						</span>
					</p>
					<p class="formCloumn">
						<span class="formText">
							IP地址：
						</span>
						<span class="formContainer">
							<input type="text" id="ipaddrs" name="ipaddrs" style="width:120px;"/>
						</span>
					</p>
					<p class="formCloumn">
						<span class="formtit">激活状态：</span> 
						<span class="formcon">
							<select name="isAct" id="isAct">
								<option value="">---请选择---</option>
								<option value="2" >已激活</option>
								<option value="1" >未激活</option>
							</select>
						</span>
					</p>
					<p class="formCloumn">
						<span class="formtit">锁定状态：</span> 
						<span class="formcon">
							<select name="isLock" id="isLock">
								<option value="">---请选择---</option>
								<option value="2" >已锁定</option>
								<option value="1" >未锁定</option>
							</select>
						</span>
					</p>
					<p>
						<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
						<a class="search-submit" href="javascript:vip.list.resetForm();" id="idReset">重置</a>
					</p>
				</form>
			</div>
			
			<div class="tab_head" id="userTab">
<!-- 				<a href="javascript:vip.list.search({tab : 'btc'});" id="btc" class="current"><span>BTC</span></a> -->
<!-- 				<a href="javascript:vip.list.search({tab : 'ltc'});" id="ltc"><span>LTC</span></a> -->
			</div>
			
			<div class="tab-body" id="shopslist">
				<jsp:include page="ajax.jsp" />	
			</div>
			
	</div>
</div>
</body>
</html>

