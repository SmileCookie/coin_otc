<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>更新角色</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<#include "/admins/top.ftl" />

<script type="text/javascript">
$(function(){
  $("#admin_user_update").Ui();
});//结束body load部分

function save(mCode){
	if(!couldPass){
		googleCode("save", true);
		return;
	}
	couldPass = false;
	vip.ajax({formId : "admin_user_update" , url : "/admin/competence/role/doAoru?mCode="+mCode , div : "admin_user_update" , suc : function(xml){
	    parent.Right($(xml).find("Des").text(), {callback:"reload2()"});
	}});
}
</script>
	</head>
	<body>
		<div id="admin_user_update" class="main-bd">
			<div class="form-line">
				<div class="form-tit">
					父角色：
				</div>
				<div class="form-con">
					<select name="pid" id="pid">
						<option value="">---请选择---</option>
						<#list all as list>
							<option value="${list.id }" <#if (curData.pid)?? && curData.pid == list.id>selected="selected"</#if>>${list.roleName }</option>
						</#list>
					</select>
					<span style="float: left;margin: 0 0 0 5px;color: red;">当前角色将继承父角色的所有权限</span>
				</div>
			</div>
			<div class="form-line">
				<div class="form-tit">
					名称：
				</div>
				<div class="form-con">
					<input
						errormsg="请检查字段RoleName长度小于50的字符串(每个中文算两个字符),注意,本字段功能如下: 角色名称"
						id="roleName" mytitle="RoleName要求填写一个长度小于50的字符串" name="roleName"
						pattern="limit(0,50)" size="20" type="text" value="${(curData.roleName) ! ''}"
						valueDemo="例：参数例子"/>
				</div>
			</div>
			
			<div class="form-line">
				<div class="form-tit">
					备注：
				</div>
				<div class="form-con">
						<textarea errormsg="长度太长，不能超过50个字符"
							id="des" mytitle="可以添加一些描述性文字" name="des"
							pattern="limit(0,50)" size="50" type="text" valueDemo="例：参数例子" cols="30" rows="3">${(curData.des) ! ''}</textarea>
				</div>
				
			</div>
			
			
			<div class="form-line">
				<div class="form-con">
					<input id="id" name="id" type="hidden" value="${(curData.id) ! ''}"/>
				</div>
			</div>
			
			<div class="form-btn">
				<a class="btn" href="javascript:save();"><i class="left"></i><span class="cont">确定</span><i class="right"></i></a> <!--<a href="#" class="btn btn-gray"><i class="left"></i><span class="cont">取消</span><i class="right"></i></a>-->
			</div>
		</div>

	</body>
</html>
