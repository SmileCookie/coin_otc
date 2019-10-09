<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>系统功能</title>
</head>
<body >
<#include "/admins/top.ftl" />
<script type="text/javascript">
$(function(){ 
 	vip.list.ui();
	vip.list.funcName = "系统功能";
	vip.list.basePath = "/admin/competence/function/";
});
</script>
<div class="mains">
<div class="col-main">
<div class="form-search">
				<form autocomplete="off" name="searchForm" id="searchContaint">
					<div id="formSearchContainer">
						<p>
							<span>理员编号：</span>
							<input errormsg="请检查字段AdmId是否是数字类型的,注意,本字段功能如下: 管理员编号" id="admId"
								mytitle="AdmId要求填写一个数字类型的值" name="admId" pattern="num()"
								size="10" type="text" value="${admId }"/>
						</p>
						<p>
							<span>用户名：</span>
							<input
								errormsg="请检查字段AdmName长度小于20的字符串(每个中文算两个字符),注意,本字段功能如下: 用户名"
								id="admName" mytitle="AdmName要求填写一个长度小于20的字符串" name="admName"
								pattern="limit(0,20)" size="10" type="text" value="${admName}"/>
						</p>
						<p>
							<span>真实姓名：</span>
							<input
								errormsg="请检查字段AdmUName长度小于30的字符串(每个中文算两个字符),注意,本字段功能如下: 真实姓名"
								id="admUName" mytitle="AdmUName要求填写一个长度小于30的字符串" name="admUName"
								pattern="limit(0,30)" size="10" type="text" value="${admUName}"/>
						</p>
						<p>
							<span>角色编号：</span>
							<input errormsg="请检查字段AdmRoleId是否是数字类型的,注意,本字段功能如下: 角色编号"
								id="admRoleId" mytitle="AdmRoleId要求填写一个数字类型的值" name="admRoleId"
								pattern="num()" size="10" type="text" value="${admRoleId}"/>
						</p>
						<p>
							<span>描述：</span>
							<input errormsg="请检查字段AdmDes长度小于50的字符串(每个中文算两个字符),注意,本字段功能如下: 描述"
								id="admDes" mytitle="AdmDes要求填写一个长度小于50的字符串" name="admDes"
								pattern="limit(0,50)" size="20" type="text" value="${admDes}"/>
						</p>
						<p>
							<a class="search-submit" id="idSearch" href="javascript:vip.list.search();">查找</a> 
							<a id="idReset" class="search-submit" href="javascript:vip.list.resetForm();">重置</a>
							<a class="search-submit" href="javascript:vip.list.aoru({id : 0 , width : 600 , height : 660});">添加</a>
						</p>
					</div>

				</form>
			</div>
			<div class="tab-body" id="shopslist">
				<jsp:include page="ajax.ftl" />
			</div>
</div>
   </div>
	</body>
</html>
