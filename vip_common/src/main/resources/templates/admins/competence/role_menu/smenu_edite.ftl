<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<style type="text/css">
		.red{color:red;}
	</style>
	<#include "/admins/top.ftl" />

	<script type="text/javascript">
	$(function(){
		$("input[name='has']").each(function(){
			var $this = $(this);
			$this.click(function(){
				if($this.attr("checked")){
					$this.parents(".left").next(".right").find("input[name='shas']").attr("checked" , true); 
				}else{
					$this.parents(".left").next(".right").find("input[name='shas']").attr("checked" , false);
				}
			});
		});
	});
	function save(mCode){
		if(!couldPass){
			googleCode("save", true);
			return;
		}
		couldPass = false;
		Ask2({Msg : "确定要保存吗？", call:function(){
			var ids = '';
			$("input[name='shas']").each(function(){
				if($(this).attr("checked")){//当前保存
					if($(this).attr("save") == "false"){//数据库未保存  保存
						ids += ',' + $(this).val() + ":" + true;
					}
				}else{///不保存
					if($(this).attr("save") == "true"){//数据库已保存   删除
						ids += ',' + $(this).val() + ":" + false;
					}
				}
			});
			if(ids.length > 0){
				$("#ids").val(ids.substring(1));
			}
			vip.ajax({formId : "admin_user_update" , url : "/admin/competence/role_menu/doSmenus?mCode="+mCode , div : "admin_user_update" , suc : function(xml){
		    	Right($(xml).find("Des").text());
			}});
		}});
	}
	
	function plate(url){
		vip.list.aoru({url : "/admin/competence/role_menu/plate?id=${curData.id}&mid=${mid}&url="+encodeURIComponent(url)})
	}
	function updateUrl(obj){
		$(obj).parents(".right").find(".group-name").removeClass("red");//.css({color : "#000000"}).;
		$(obj).addClass("red");//.css({color : "red"});
		var inputDuo = $(obj).prev(".group-input").find("input");
		var newUrlIput = $(obj).parents(".right").find("input[name='newurl']");
		newUrlIput.parent("span").show();
		newUrlIput.val(inputDuo.val());

	}
	function doUpdateUrl(obj){
		Ask2({Msg : "确定要修改吗？", call:function(){
			var curDes = $(obj).parents(".right").find(".red");
			var inputDuo = curDes.prev(".group-input").find("input");
			if(inputDuo.length == 1){
				var oldVal = inputDuo.attr("ov");
				var newVal = $(obj).prev("input").val();
				if(oldVal != newVal){
					inputDuo.val(newVal);
					inputDuo.attr("save" , "false");
					Right("修改成功");
				}else{
					Wrong("未作任何修改");
				}
				return;
			}
			Wrong("修改失败");
		}});
	}
	</script>
	</head>
	<body>
		<div id="admin_user_update" class="main-bd">
			<div class="win-form-line">
				<h3>当前角色：${curData.roleName}</h3>
			</div>
		
		
			<div class="win-form-line top">
				<span class="left" style="text-align: center;color: #000000;">
					视图
				</span>
				<span class="right">
					子功能
				</span>
			</div>
			<#if groups??>
			<#list groups?keys as key>
				<div class="win-form-line">
					<span class="left">
						${groups[key].des}
						<#if groups[key].plate>
							<a href="javascript:plate('${groups[key].url}');">[板块]</a>
						</#if>
						<span class="group-input"><input type="checkbox"  name="has"  alt="全选/反选"/></span>
						
					</span>
					<span class="right">
						<#list groups[key].functions as sd>
							<span class="input-name">
								<span class="group-input"><input type="checkbox" value="${sd.url }" ov="${sd.url }" save="${sd.inRole ?c}" name="shas" <#if sd.inRole>checked="checked"</#if>/></span> 
								<span class="group-name" onclick="updateUrl(this)" style="cursor: pointer;">${sd.des} 
									<#if sd.vc.plate>
										<a href="javascript:;">[板块]</a>
									</#if>
								</span>
							</span>
						</#list>
						<span style="width:100%;float: left;display: none;">
							<input type="text" name="newurl" style="float: left;width: 80%;"/> 
							<a href="javascript:;" onclick="doUpdateUrl(this);">修改</a>
						</span>
					</span>
				</div>
			</#list>
			</#if>
			<div class="form-btn">
					<input id="id" name="id" type="hidden" value="${id}"/>
					<input id="mid" name="mid" type="hidden" value="${mid}"/>
					<input id="ids" name="ids" type="hidden" value=""/>
					<a class="btn" href="javascript:save();"><i class="left"></i><span class="cont">保存</span><i class="right"></i></a> 
					<a href="javascript:parent.Close();" class="btn btn-gray"><i class="left"></i><span class="cont">退出</span><i class="right"></i></a>
			</div>
			<div class="form-line"></div>
		</div>

	</body>
</html>
