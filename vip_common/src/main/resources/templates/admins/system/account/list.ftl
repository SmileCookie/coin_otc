<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>系统功能</title>
<#include "/admins/top.ftl" />
</head>
<body >

<script type="text/javascript">
$(function(){ 
 	vip.list.ui();
	vip.list.funcName = "邮件账户";
	vip.list.basePath = "/admin/system/account/";
});
</script>
<div class="mains">
<div class="col-main">
<div class="form-search">
				<form autocomplete="off" name="searchForm" id="searchContaint">
					<div id="formSearchContainer">
						<p>
							<span>编号：</span>
							<input errormsg="请检查字段是否正确" id="id"
								mytitle="要求填写一个数字类型的值" name="id" pattern="num()"
								size="10" type="text" value=""/>
						</p>
						<p>
							<span>发送名称：</span>
							<input errormsg="请检查字段是否正确" id="sendName"
								mytitle="要求填写正确的值" name="sendName"
								size="30" type="text" value=""/>
						</p>
						<p>
							<span>发送邮箱：</span>
							<input errormsg="请检查字段是否正确" id="fromAddr"
								mytitle="要求填写正确的值" name="fromAddr"
								size="30" type="text" value=""/>
						</p>
						
						<p>
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
							<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
							<a class="search-submit" href="javascript:vip.list.aoru({id : 0 , width : 500 , height : 500});">添加</a>
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
