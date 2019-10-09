<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
	<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
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
		
		$(".item .tree-node .disk_ico").each(function(){
			var $this = $(this);
			$this.click(function(){
				if($this.hasClass("opened")){//需要关闭
					$this.removeClass("opened");
					$this.addClass("closed");
					$this.parent(".tree-node").nextAll(".subitem").hide();
				}else{
					$this.removeClass("closed");
					$this.addClass("opened");
					$this.parent(".tree-node").nextAll(".subitem").show();
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
	</script>
	</head>
	<body>
		<div id="admin_user_update" class="main-bd">
			<div class="form-line" style="padding: 0 0 0 0;border-bottom: 1px dotted #CCCCCC;">
				<h3>当前角色：${curData.roleName}</h3>
			</div>
		
			<#list lastHierachy.sonHierarchys as item>
				<div class="item">
					<div class="tree-node selected-node">
						<span class="disk_ico closed"></span><span title="我的文件" class="txt on">${item.des }</span>
					</div>
					<#list item.sonHierarchys as sitem>
						<div class="subitem" style="display: none;">
							<div class="item">
								<div class="tree-node"><span class="disk_ico opened"></span><span title="软件" class="txt">${sitem.des }</span></div>
								<div style="display: block;" class="subitem">
									<#list sitem.group.functions as fc>
										<span class="input-name">
											<span class="group-input"><input type="checkbox" value="${fc.url }" save="${fc.inRole?c}" name="shas" <#if fc.inRole>checked="checked"</#if>/></span> 
											<span class="group-name">${fc.des} 
												<#if fc.vc.plate>
													<a href="javascript:;">[板块]</a>
												</#if>
											</span>
										</span>
									</#list>
								</div>
								
								<#list sitem.sonHierarchys as ssitem>
									<div class="subitem">
										<div class="item">
											<div class="tree-node"><span class="disk_ico opened"></span><span title="软件" class="txt">${ssitem.des }</span></div>
											<div style="display: block;" class="subitem">
												<#list ssitem.group.functions as sfc>
													<span class="input-name">
														<span class="group-input"><input type="checkbox" value="${sfc.url }" save="${sfc.inRole?c}" name="shas" <#if sfc.inRole>checked="checked"</#if>/></span> 
														<span class="group-name">${sfc.des} 
															<#if sfc.vc.plate>
																<a href="javascript:;">[板块]</a>
															</#if>
														</span>
													</span>
												</#list>
											</div>
										</div>
									</div>
								</#list>
							</div>
						</div>
					</#list>
					
					<div style="display: none;" class="subitem">
						<div class="left"></div>
						<#list item.group.functions as fc>
							<span class="input-name">
								<span class="group-input"><input type="checkbox" value="${fc.url }" save="${fc.inRole?c}" name="shas" <#if fc.inRole>checked="checked"</#if>/></span> 
								<span class="group-name">${fc.des} 
									<#if fc.vc.plate>
										<a href="javascript:;">[板块]</a>
									</#if>
								</span>
							</span>
						</#list>
					</div>
				</div>
			</#list>
			
			<div class="form-btn" style="float: left;width:100%;padding: 15px 0 0 0px;">
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
