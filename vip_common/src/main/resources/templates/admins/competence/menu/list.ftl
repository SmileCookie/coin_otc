<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>菜单管理</title>
<#include "/admins/top.ftl">
</head>
<body >

<script type="text/javascript">
	$(function(){
	 	vip.list.ui();
	 	vip.list.funcName = "菜单";
	 	vip.list.basePath = "/admin/competence/menu/";
	});
	
function doDel(ids){
	if(!couldPass){
		commids = ids;
		googleCode("doDel", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({
		title : "确定要删除该视图菜单吗？",
		url : "/admin/competence/menu/dodel?id="+commids+"&mCode="+ids
	});
}
</script>
<div class="mains">
<div class="col-main">
	<div class="form-search">
		<form autocomplete="off" name="searchForm" id="searchContaint">
			<div id="formSearchContainer">
				<p>
					<span>菜单编号：</span>
					<input errormsg="请检查是否是数字类型的"
						id="id" mytitle="AdmRoleId要求填写一个数字类型的值" name="id"
						pattern="num()" size="10" type="text"/>
				</p>
				<p>
					<span>名称：</span>
					<input id="name" mytitle="填写一个长度小于50的字符串" name="name"
						pattern="limit(0,50)" size="20" type="text"/>
				</p>
				<p>
					<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
					<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
					<a class="search-submit" href="javascript:vip.list.aoru({id : 0 , width : 600 , height : 300});">添加</a>
				</p>
			</div>
	
		</form>
	</div>
	
	<div class="tab-body" id="shopslist">
		<#include "ajax.ftl" />
	</div>
</div>
</div>
</body>
</html>
