<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions"%>

<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>-${WEB_NAME }-${WEB_TITLE }</title>
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<link rel="stylesheet" href="${static_domain }/statics/css/web.loan.css?V${CH_VERSON }">
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
	.detail-top p{
		clear:both;
		margin:0 0 0 0;
		padding:5px 0 0 0;
		font-size: 13px;
		color: #9A9CA4;
	}
</style>
</head>

<body>
  <div class="lists">
   <div class="list_query" style="display: none;">
	  	<form autocomplete="off" name="searchForm" id="searchContaint">
	       <input name="id" value="${lid}" type="hidden" />
	    </form> 
    </div>
    <div class="list_container" id="shopslist" style="border: none;min-height:584px; overflow-y:auto;">    
    	<jsp:include page="ajax.jsp" />
    </div>
  </div>
<input id="languageInput" value="cn" type="hidden"/>
<script type="text/javascript">
	$(function(){
	 	vip.list.ui();
	 	vip.list.basePath = "/manage/loan/repay/";
	 	
	 	$("#actionTd a").UiTitle();
		$(".des").UiTitle(); 
	    $('.detail-top a').on('click',function(){
              window.top.location.href = '/terms/termsExpenseTable';
		});
	});
	
	function huan(id){///users/admin/finance/repayofqi?lid=43
		vip.list.reloadAsk({title : "${L:l(lan,'确定要还款吗')}？" , url : "/u/repay/qihuan?id="+id});
	}
	
	function onBoxChanges(box, type){
		var oneboxs = $("input[name=onebox]:not(:disabled)");
		if(type == 1){	//全选，反选
			if($(box)[0].checked){
				oneboxs.attr("checked", true);
				$("#batch").show();
			}else{
				oneboxs.attr("checked", false);
				$("#batch").hide();
			}
		}
		if(type == 2){
			var flag = false;
			oneboxs.each(function(){
				if($(this)[0].checked){
					flag = true;
				}
			});
			if(flag){
				$("#allbox").attr("checked", true);
				$("#batch").show();
			}else{
				$("#allbox").attr("checked", false);
				$("#batch").hide();
			}
		}
	}
	
	function batchRepay(){
		var oneboxs = $("input[name=onebox]:not(:disabled)");
		var ids = "";
		oneboxs.each(function(){
				if($(this)[0].checked){
					ids += $(this).val() + ",";
				}
		});
		if(ids == ""){
			Wrong("${L:l(lan,'请选择需要还款的记录')}!", {callback:"Close()"});
			return;
		}
		ids = ids.substring(0,ids.length - 1);
		vip.list.reloadAsk({title : "${L:l(lan,'您要确定为所选的记录还款吗')}？" , url : "/u/repay/batchQihuan?ids="+ids});
	}
	function Close(obj){
		  $("div[id^='JuaMask']", parent.document).eq(1).remove();
		  $("div[id^='JuaBox']", parent.document).eq(1).remove();
	}
</script>
</body>
</html>
