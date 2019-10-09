<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<#include "/admins/top.ftl" />

	<script type="text/javascript">
	
	function save(){
		Ask2({Msg : "确定要保存吗？", call:function(){
			var ids = '';
			$("input[name='has']").each(function(){
				if($(this).attr("checked")){
					ids += ',' + $(this).val();
				}
			});
			
			if(ids.length > 0){
				$("#ids").val(ids.substring(1));
			}
			vip.ajax({formId : "admin_user_update" , url : "/admin/competence/role_menu/doPlate" , div : "admin_user_update" , suc : function(xml){
		    	Right($(xml).find("Des").text());
			}});
		}});
	}
	</script>
	</head>
	<body>
		<div id="admin_user_update" class="main-bd">
			<div class="win-form-line top">
				请选择是否包含当前板块：
			</div>
		
			<div class="win-form-line">
				<#list dataList as data>
					<span class="group-input"><input type="checkbox" value="${data.id }" name="has" <#if data.inRole>checked="checked"</#if>/></span> <span class="group-name">${data.dataDes}</span> 
				</#list>
			</div>
			
			<div class="form-btn">
				<input id="id" name="id" type="hidden" value="${curData._id}"/>
				<input id="mid" name="mid" type="hidden" value="${mid}"/>
				<input id="ids" name="ids" type="hidden" value=""/>
				<a class="btn" href="javascript:save();"><i class="left"></i><span class="cont">保存</span><i class="right"></i></a> 
				<a href="javascript:parent.Close();" class="btn btn-gray"><i class="left"></i><span class="cont">退出</span><i class="right"></i></a>
			</div>
		</div>

	</body>
</html>
