<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>用户管理</title>
<#include "/admins/top.ftl" />
<script type="text/javascript">
$(function(){ 
 	vip.list.ui();
	vip.list.funcName = "管理员";
	vip.list.basePath = "/admin/manager/";
	getAdmins();
});
function getAdmins(){
	vip.list.ajaxPage({url : vip.list.basePath+"ajax", suc : function(){}});
}
function insertAjax(id){
	  window.location.reload();
}
 
//编辑回调
function editAjax(id){
	 window.location.reload(); 
}
var commset = "";
function biaoZhiKeFu(set , id){
	var setStr = "有效";
	if(set == 0){
		setStr = "无效";
	}
	if(!couldPass){
		commids = id;
		commset = set;
		googleCode("biaoZhiKeFu", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({
		title : "确定标识当前管理员为"+setStr+"客服吗？",
		url : "/admin/manager/doSetKefu?set=" + commset + "&id="+commids+"&mCode="+set
	});
}

function doDel(ids){
	if(!couldPass){
		commids = ids;
		googleCode("doDel", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({
		title : "确定要删除该管理员吗？",
		url : "/admin/manager/dodel?id="+commids+"&mCode="+ids
	});
}

function unLocked(ids){
	if(!couldPass){
		commids = ids;
		googleCode("unLocked", true);
		return;
	}
	couldPass = false;
	vip.list.reloadAsk({
		title : "确定要解锁该管理员吗？",
		url : "/admin/manager/unLocked?id="+commids+"&mCode="+ids
	});
}
 
function syncAdmin(ids){
	Iframe({
		Url : "/admin/syncAdmin?id=" + ids,
		Width : 400,
		Height : 280,
		isShowIframeTitle: true,
		Title : "同步管理员用户"
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
							<span>管理员编号：</span>
							<input errormsg="请检查字段AdmId是否是数字类型的,注意,本字段功能如下: 管理员编号" id="admId"
								mytitle="AdmId要求填写一个数字类型的值" name="admId" pattern="num()"
								size="15" type="text"/>
						</p>
						<p>
							<span>用户名：</span>
							<input
								errormsg="请检查字段AdmName长度小于20的字符串(每个中文算两个字符),注意,本字段功能如下: 用户名"
								id="admName" mytitle="AdmName要求填写一个长度小于20的字符串" name="admName"
								pattern="limit(0,20)" size="15" type="text"/>
						</p>
						<p>
							<span>真实姓名：</span>
							<input
								errormsg="请检查字段AdmUName长度小于30的字符串(每个中文算两个字符),注意,本字段功能如下: 真实姓名"
								id="admUName" mytitle="AdmUName要求填写一个长度小于30的字符串" name="admUName"
								pattern="limit(0,30)" size="10" type="text"/>
						</p>
						<p>
							<span>角色：</span>
							<select name="admRoleId" id="admRoleId" style="width:90px;">
                              <option value="">全部</option>
                              <#list roles as role>
                                 <option value="${role.id }">${role.roleName }</option>
                              </#list>
                            </select>
						</p>
						<p>
							<span>谷歌验证：</span>
							<select name="secret" id="secret" style="width:90px;">
                              <option value="">全部</option>
                              <option value="1">已添加</option>
                              <option value="0">未添加</option>
                            </select>
						</p>
						<p>
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
							<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
							<a class="search-submit" href="javascript:vip.list.aoru({id : 0 , width : 600 , height : 660});">添加</a>
						</p>
					</div>
					<div style="clear: both;"></div>
				</form>
			</div>
			<div class="tab_head" id="userTab">			
				<a href="javascript:vip.list.search({tab : '0'});" id="0" class="current"><span>未锁定</span></a>
				<a href="javascript:vip.list.search({tab : '1'});" id="1"><span>已锁定</span></a>
			</div>
			<div class="tab-body" id="shopslist">
				<#include "ajax.ftl" />
			</div>
</div>
   </div>
	</body>
</html>
