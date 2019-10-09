<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
	<title></title>
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
		vip.ajax({formId : "admin_user_update" , url : "/admin/competence/menu/doAoru?mCode="+mCode , div : "admin_user_update" , suc : function(xml){
			parent.vip.list.reload();
		    parent.Right($(xml).find("Des").text());
		}});
	}
	</script>
	</head>
	<body>
		<div id="admin_user_update" class="main-bd">
			<div class="form-line">
				<div class="form-tit">
					名称：
				</div>
				<div class="form-con">
					<input
						errormsg="字段长度小于50的字符串(每个中文算两个字符),注意,本字段功能如下: 名称"
						id="name" mytitle="请填写菜单名称" name="name"
						pattern="limit(0,50)" size="20" type="text" value="${(curData.name) ! ''}"
						/>
				</div>
			</div>
			
			<div class="form-line">
				<div class="form-tit">
					描述：
				</div>
				<div class="form-con">
						<textarea errormsg="长度太长，不能超过50个字符"
							id="des" mytitle="请填写菜单说明" name="des"
							pattern="limit(0,50)" size="50" type="text"  cols="30" rows="3">${(curData.des) ! ''}</textarea>
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
