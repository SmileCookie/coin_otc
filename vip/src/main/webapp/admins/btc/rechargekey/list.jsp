<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page isELIgnored="false"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>充值地址</title>
   <jsp:include page="/admins/top.jsp" />

<script type="text/javascript">
$(function(){
	vip.list.ui();
	vip.list.funcName = "充值地址";
	vip.list.basePath = "/admin/btc/rechargekey/"; 
});

function beSign(){
	Iframe({
	    Url:"/admin/btc/rechargekey/sign?coint=${coint.tag}",
        zoomSpeedIn		: 200,
        zoomSpeedOut	: 200,
        Width:400,
        Height:250,
        scrolling:'auto',
        isIframeAutoHeight:false,
        isShowIframeTitle: true,
        Title:"标记钱包地址已过期"
	});
}
</script>

		<style type="text/css">
		.col-main{float:left;width:100%;}
		.tb-list2 th{padding:8px 10px;}
		.tb-list2 .hd td span b{font-weight:normal;color:#C92707;}
		.tb-list2 {width: 100%;}
		.tb-list2 a span{color:#C92707;display: inline-block;width: 110px;}
		
		.form-search .formline{float:left;}
.form-search p{float:none;}
		</style>
	</head>
	<body>
	
		<div class="mains">
		<jsp:include page="/admins/topTab.jsp" />
		<input type="hidden" value="${currentTab }" id="currentTab"/>
		
			<div class="col-main">
				<form autocomplete="off" name="searchForm" id="searchContaint">
				<div class="form-search" id="searchContainer">
					<input type="hidden" id="tab" name="tab" value="${tab }" />
					
					<div class="formline">
						<span class="formtit">用户ID：</span> 
						<span class="formcon">
							<input id="userId" mytitle="用户Id搜索" name="userId" pattern="limit(0,50)" size="10" type="text"/>
						</span>

						<%--<span class="formtit">用户名：</span>
						<span class="formcon">
							<input id="userName" mytitle="用户名要求填写一个长度小于50的字符串" name="userName" pattern="limit(0,50)" size="20" type="text"/>
						</span>--%>
						
						<span class="formtit">钱包名称：</span> 
						<span class="formcon">
							<select name="wallet" id="wallet" style="width:120px;display: none;" selectid="select_24962646">
					           <option value="">全部</option>
					           <c:forEach var="wallet" items="${wallets}">
					             	<option value="${wallet.wallet}">${wallet.wallet}</option>
				           		</c:forEach>
					         </select>
					         <div class="SelectGray" id="select_24962646"><span><i style="width: 111px;">全部</i></span></div>
						</span>
					</div>
					<div style="clear: both;"></div>
					<div class="formline">
						<span class="formtit">充值地址：</span> 
						<span class="formcon">
							<input id="address" mytitle="请输入地址。" name="address" pattern="limit(0,50)" size="45" type="text"/>
						</span>
						<p>
							<input type="hidden" name="${coint.coinParam }" value="${coint.propTag }"  />
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
							<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
							<a id="idReset" class="search-submit" href="javascript:beSign();" style="width:200px;">标记钱包地址已过期</a>
						</p>
					</div>
				</div>
				</form>
				<div class="tab_head" id="userTab">			
					<a href="javascript:vip.list.search({tab : 'hasset'});" id="hasset" class="current"><span>已分配</span></a>
					<a href="javascript:vip.list.search({tab : 'hascharge'});" id="hascharge"><span>已充值</span></a>
					<a href="javascript:vip.list.search({tab : 'nocharge'});" id="nocharge"><span>未充值</span></a>
					<a href="javascript:vip.list.search({tab : 'noset'});" id="noset"><span>未分配</span></a>
				</div>
				<div class="tab-body"  id="shopslist">
					<jsp:include page="ajax.jsp" />
				</div>
			</div>
			
		</div>
	</body>
</html>
