<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>
<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
<jsp:include page="/common/head.jsp" />
<style type="text/css">
	@font-face {font-family: "iconfont2";
	src: url('/common/fonts/iconfont2.eot?v=${CH_VERSON }'); /* IE9*/
	src: url('/common/fonts/iconfont2.eot?#iefix&v=${CH_VERSON }') format('embedded-opentype'), /* IE6-IE8 */
	url('/common/fonts/iconfont2.woff?v=${CH_VERSON }') format('woff'), /* chrome, firefox */
	url('/common/fonts/iconfont2.ttf?v=${CH_VERSON }') format('truetype'), /* chrome, firefox, opera, Safari, Android, iOS 4.2+*/
	url('/common/fonts/iconfont2.svg?v=${CH_VERSON}#iconfont') format('svg'); /* iOS 4.1- */
	}
	.detail-top{
		padding:12px 20px;
	}
	.detail-top h2{
		float:left;
		margin:0;
		padding:0;
		font-size: 18px;
		color: #484C4B;
		border: none;
	}
	.detail-top .ps1-close{
        float:right;
		display: inline;
		font-size:26px;
		color:#484C4B;
		line-height:24px;
		position:relative;
		top:-2px;
		cursor:pointer;
	}
	.table-list tbody tr{
        border-bottom: 1px solid #EEEEEE;
		text-align:center;
	}
	.table-list thead{
		background: #F0F0F0;
	}
	.table-list thead tr th{
		padding:10px 20px;
		font-size: 13px;
		color: #484C4B;
		text-align: center;
	}
	.table-list thead tr th:first-child, .table-list tbody tr td:first-child{
		text-align: center;
	}
	.loan-label{
		padding:2px 5px;
		font-size: 13px;
		color:#fff;
		border-radius:2px;
	}
	.loan-green{
		background: #2EAE88;
	}
	.loan-red{
		background: #E46161;
	}
	.loan-yellow{
		background: yellowgreen;
	}
	.table-invest tbody tr td,.table-invest thead tr th{
        padding:10px 5px;
	}
	.table-invest tbody tr td a:hover{
		text-decoration:underline;
	}
</style>
</head>

<body>
  <div class="lists">
   <div class="list_query" style="display: none;">
	  	<form autocomplete="off" name="searchForm" id="searchContaint">
	       <input name="lid" value="${lid}" type="hidden" />
	    </form> 
    </div>
    <div class="list_container" id="shopslist" style="border: none; min-height:584px;">    
    	<jsp:include page="winAjax.jsp" />
    </div>
  </div>
<input id="languageInput" value="cn" type="hidden"/>
<script type="text/javascript">
	$(function(){
	 	vip.list.ui();
	 	vip.list.basePath = "/users/admin/finance/repayofqi/";
	 	
	 	$("#actionTd a").UiTitle();
	 	$(".des").UiTitle(); 
		 
	});
	function Close(){
		  $("div[id^='JuaMask']", parent.document).remove();
		  $("div[id^='JuaBox']", parent.document).remove();
	}
	function huan(id){///users/admin/finance/repayofqi?lid=43
		vip.list.reloadAsk({title : "${L:l(lan,'确定要还款吗？')}" , url : "/users/admin/finance/repayofqi/huan?id="+id});
	}
</script>
</body>
</html>
