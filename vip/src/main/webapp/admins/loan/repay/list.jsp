<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>用户还款记录</title>
<jsp:include page="/admins/top.jsp" />

<style type="text/css">
label.checkbox{  margin: 3px 6px 0 7px;}
label.checkbox em{ padding-left:18px; line-height:15px; float:left; font-style:normal;}
.page_nav{ margin-top:10px;}

.form-search .formline{float:left;}
.form-search p{float:none;}

.operation { height: 20px; line-height: 20px; text-align: left;margin-top: 10px;padding-left: 10px;}
tbody.operations  td{ padding:0; border:0 none;}
tbody.operations  td label.checkbox{ margin-top:10px; width:55px;}
</style>
</head>
<body >
<div class="mains">
	<div class="col-main">
		<div class="form-search">
			<form autocomplete="off" name="searchForm" id="searchContaint">
				<div class="form-search" id="searchContainer">
					<input type="hidden" id="tab" name="tab" value="${tab }" />
					
					<div class="formline">
						<span class="formtit">借入者用户名：</span> 
						<span class="formcon">
							<input id="userId" mytitle="字段要求填写一个长度小于50的字符串" name="inUserName" pattern="limit(0,50)" size="20" type="text"/>
						</span>

						<span class="formtit">投资者ID：</span> 
						<span class="formcon">
							<input id="outUserId" mytitle="字段要求填写一个长度小于50的字符串" name="outUserId" pattern="limit(0,50)" size="20" type="text"/>
						</span>
						<p>
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
							<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
						</p>
					</div>
				</div>
		
			</form>
		</div>
		<div class="tab_head" id="userTab">
		<!-- <a href="javascript:vip.list.search({tab:'0'})" class="current" id="0"><span>所有借贷</span></a> -->
			<c:if test="${coinMaps!=null }">
				<c:forEach items="${coinMaps}" var="coin" >
					<a href="javascript:vip.list.search({tab:'${coin.value.fundsType}'})" id="${coin.value.fundsType}"><span>${coin.value.propTag}记录</span></a>
				</c:forEach>
			</c:if>
		<!-- 	<a href="javascript:vip.list.search({tab:'rmb'})" id="rmb" class="current"><span>RMB记录</span></a>
			<a href="javascript:vip.list.search({tab:'btc'})" id="btc"><span>BTC记录</span></a>
			<a href="javascript:vip.list.search({tab:'ltc'})" id="ltc"><span>LTC记录</span></a>
			<a href="javascript:vip.list.search({tab:'eth'})" id="eth"><span>ETH记录</span></a>
			<a href="javascript:vip.list.search({tab:'etc'})" id="etc"><span>ETC记录</span></a> -->
		</div>
		<div class="tab-body" id="shopslist">
			<jsp:include page="ajax.jsp" />
		</div>
	</div>
</div>	

<script type="text/javascript">
$(function(){ 
 	vip.list.ui();
	vip.list.funcName = "还款记录";
	vip.list.basePath = "/admin/loan/repay/";
});

function reload2(){
	Close();
	vip.list.reload();
}

//全选按钮的方法
function selectAll(){
	changeCheckBox('delAll'); 
	$(".bd .checkbox").trigger("click");
}

function tongji(isAll){
	var ids="";
	if(!isAll){
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
		url : "/admin/loan/repay/tongji?eIds=" + ids+"&isAll="+isAll,
		formId : "searchContaint",
		dataType : "json",
		suc : function(json) {
			$.each(json.datas, function(i, v){
				$("#total_"+(i+1)).text(v);
				$("#total_"+(i+1)).parent("span").show();
			});
		}
	});
}
</script>

</body>
</html>
