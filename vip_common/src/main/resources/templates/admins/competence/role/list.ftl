<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>角色管理</title>
<#include "/admins/top.ftl" />
<script type="text/javascript">

$(function(){ 
 	vip.list.ui();
	vip.list.funcName = "角色";
	vip.list.basePath = "/admin/competence/role/";
});
function insertAjax(id){
	  window.location.reload();
}
 
//编辑回调
function editAjax(id){
	 window.location.reload(); 
}
   
function doDel(ids){
	if(!couldPass){
		commids = ids;
		googleCode("doDel", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({
		title : "确定要删除该角色吗？",
		url : "/admin/competence/role/dodel?id="+commids+"&mCode="+ids
	});
}

function reload2(){
	Close();
	vip.list.reload();
}
</script>	
</head>
<body >
<div class="mains">
<div class="col-main">
<div class="form-search">
				<form autocomplete="off" name="searchForm" id="searchContaint">
					<div id="formSearchContainer">
						<p>
							<span>角色编号：</span>
							<input errormsg="请检查字段AdmRoleId是否是数字类型的,注意,本字段功能如下: 角色编号"
								id="id" mytitle="AdmRoleId要求填写一个数字类型的值" name="id"
								pattern="num()" size="10" type="text"/>
						</p>
						<p>
							<span>名称：</span>
							<input id="roleName" mytitle="AdmDes要求填写一个长度小于50的字符串" name="roleName"
								pattern="limit(0,50)" size="20" type="text"/>
						</p>
						<p>
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
							<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
							<a class="search-submit" href="javascript:vip.list.aoru({id : 0 , width : 600 , height : 398});">添加</a>
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
