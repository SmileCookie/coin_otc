<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>语言管理</title>
<#include "/admins/top.ftl" />

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
<#if error??>
<#else>
<div class="mains">
	<div class="col-main">
		<div class="form-search">
			<form autocomplete="off" name="searchForm" id="searchContaint">
				<input type="hidden" name="tab" id="tab" value="${tab }"/>
			</form>
		</div>
		
		<div class="tab_head" id="userTab">
			<a href="javascript:vip.list.search({tab:'en'})" class="current" id="en"><span>英文</span></a>
			<a href="javascript:vip.list.search({tab:'cn'})" id="cn"><span>中文</span></a>
		</div>
		
		<div class="tab-body" id="shopslist">
			<#include "ajax.ftl" />
		</div>
	</div>
</div>	
</#if>
<script type="text/javascript">
$(function(){
 	vip.list.ui();
	vip.list.funcName = "语言管理";
	vip.list.basePath = "/admin/lan/";
});

</script>
<#include "/admins/code.ftl" />
</body>
</html>
