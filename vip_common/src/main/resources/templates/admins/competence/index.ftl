<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="cn" lang="cn" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>角色权限高可视化管理</title>
<#include "/admins/top.ftl" />
<style type="text/css">
</style>
<script type="text/javascript">
var inAjaxing=false;
var lastIds=0;
var lastneedType=false;
var opeType=0;
var currentRoleId="";
$(function(){
	ShowRoles();
});

function menu(id){
	var $this = $("#roleId_"+id);
	//不选 的时候 roleInfo2
 	if($this.attr('select')){
			$("#StepTwo").fadeOut();
			$("#StepThree").hide();
 			$("#selectGroupInfo").empty();
			$("#StepThree").hide();
			
   	   		$("#StepTwo").fadeOut("fast");
   			$("#groupsDiv").fadeOut("fast");
   			$("#selectRoleInfo").html("").attr("class","");
   			$this.parent().removeClass("adeleteselect");
   			$this.removeAttr('select');
   			currentRoleId="";
	 }else{
		 	$("#groupsDiv").empty();
  	   		$("#StepTwo").fadeIn("fast");
			$("#StepThree").hide();
 			$("#selectGroupInfo").empty();
  		   		//选中的时候
   			$("#rolesDiv").find('.roleName').each(function(){
	   			$(this).parent().removeClass("adeleteselect");
	   			$(this).parent().removeAttr('select');
   			});
  		   	currentRoleId = id;
		   	groups();
   			$("#groupsDiv").fadeIn("fast").parent().attr("css","groudSpan");
   			$this.addClass("adeleteselect");
   			$this.attr('select','true');
   			
   			$("#selectRoleInfo").empty();
   			$("<b><font color='red'>当前选择的角色是:"+$this.parent().find(".roleName").text()+",编号："+id+"</font></b>").appendTo($("#selectRoleInfo"));
  	  }
}

function ShowRoles(){
	//if(!couldPass){
	//	googleCode("ShowRoles", true);
	//	return;
	//}
	//Close();
	//couldPass = false;
	$("#rolesDiv").empty();
	vip.ajax({
		url : "/admin/competence/role/jsons?mCode=",
		dataType : "json",
		suc : function(json){
			$.each(json.datas , function(index , cont){
				$("#StepTwo").hide("fast");
				$("#selectGroupInfo").hide("fast");
				$("#StepThree").hide("fast");
				var newRoles=$("<span id='roleId_"+cont.id+"' class='roleSpan adelete' ><a href=\"javascript:menu('"+cont.id+"');\" class='roleName' title='生成菜单'>"+cont.roleName+"</a><a href=\"javascript:del_role('"+cont.id+"');\" class='delete iDelete'>&nbsp;&nbsp;&nbsp;&nbsp;</a></span>");
					 
				newRoles.appendTo($("#rolesDiv"));
			});
		},
		err : function(){
			
		}
	});
}	 

function del_role(ids){
	if(!couldPass){
		commids = ids;
		googleCode("del_role", true);
		return;
	}
	couldPass = false;
	var url = "/admin/competence/role/dodel?id="+commids+"&mCode="+ids;
	Ask2({Msg:"确定要删除该角色吗？", call:function(){
		vip.ajax({url : url, suc : function(xml){
			Right($(xml).find("Des").text() , {callback:"reload2()"});
		},err:function(xml){
			Wrong($(xml).find("Des").text());
		}}); 
	}});
}

function reload2(){
	Close();
	ShowRoles();
}
	
function addRole(){
	vip.list.aoru({url : "/admin/competence/role/aoru"});
}
var currentGroupId="";
//显示组信息
function groups(){
	if(currentRoleId.length>0){
		vip.ajax({
			url : "/admin/competence/role_menu/jsons?roleId="+currentRoleId,
			dataType : "json",
			suc : function(json){
				$("#groupsDiv").empty();
	   		    $("#addGroupInfo").empty();
	   		    if(json.datas.length > 0){
	   		    	$.each(json.datas , function(index , cont){
		   			    var newGroup=$("<span id='gId_"+cont.id+"' class='roleSpan  adelete'><a href=\"javascript:sons('"+cont.id+"');\" class='groupName' title='选择角色'>"+cont.name+"</a><!--a href='javascript:void(0);' class='delete iDelete '>&nbsp;&nbsp;&nbsp;&nbsp;</a--></span>");
		   		  	    //查的class为close的元素
		   		  	   	var parent=$(this).parent();
		   		  	    
		   		  	    $("addGroupInfo >spanNull").empty();
	   					$("#gcmDiv").html("当前目录包含二级菜单如下：<a class='edite_btn' href='javascript:sMenuEdite();'>编辑</a> &nbsp;<a class='edite_btn' href='javascript:sMenuEdite2();'>授权</a>");
	   		  			$(newGroup).appendTo($("#groupsDiv"));
		   		    });
	   		    }else{
	   		    	$("#groupsDiv").html("<div class='confirm'>当前角色下无一级菜单！</div>");
	   		    }
			},
			err : function(){
				$("#groupsDiv").html("<div class='confirm'>当前角色下无一级菜单！</div>");
			}
		});
	}	
}
//子菜单
function sons(id){
		var $this = $("#gId_"+id);
	
		$("#gcmDiv").fadeIn("fast");
 	   		//不选 的时候 
		if($this.attr('select')){
   			$("#selectGroupInfo").hide("fast");
   			//$("#cmDiv").hide("fast");
   			$("#StepThree").hide();
   			$this.removeClass("adeleteselect");
   			$this.removeAttr('select');
   			currentGroupId="";
   			$("#addGroupInfo").empty();
   			$("#selectGroupInfo").empty();
		}else{
			currentGroupId = id;
   			$("#selectGroupInfo").fadeIn("fast");
   			$("#StepThree").show();
			$("#cmDiv").show("fast");
 		   		//选中的时候
   			$("#groupsDiv").find('.groupName').each(function(){
	   			$(this).parent().removeClass("adeleteselect");
	   			$(this).removeAttr('select');
   			});
   			$this.addClass("adeleteselect");
   			$("#selectGroupInfo").empty();
   			$this.attr('select','true');
   			
   			//查询
   			if(currentGroupId.length>0&&currentRoleId.length>0){
   				sMenus(currentRoleId,currentGroupId);
   			}else{
   				Alert("请选择相应的组");
   			}
 	 	}
}

function menuEdite(){
	vip.list.aoru({url : "/admin/competence/role_menu/aoru?id=" + currentRoleId});
}

function sMenuEdite(){
	vip.list.aoru({url : "/admin/competence/role_menu/sMenus?id=" + currentRoleId + "&mid="+currentGroupId , width : 800 , height : 566});
}

function sMenuEdite2(){
	vip.list.aoru({url : "/admin/competence/role_menu/hierarchyMenus?id=" + currentRoleId + "&mid="+currentGroupId , width : 800 , height : 566});
}

function sMenus(currentRoleId,currentGroupId){
	$("#cmDiv :checkbox").each(function(){
		$(this).attr("checked",false);
	});
	
	vip.ajax({
		url : "/admin/competence/role_menu/sjsons?id="+currentRoleId+"&mid="+currentGroupId,
		dataType : "json",
		suc : function(json){
			if(json.datas.length > 0){
				$("#addGroupInfo").empty();
   		    	$.each(json.datas , function(index , cont){
	   			    var autId = cont.id;
			        var newRGInfo=$("<span id='rgi_"+autId+"' class='roleSpan  adelete'><a href='javascript:void(0);' class='rgName' title='选择权限 '>"+cont.name+"</a><!--a href='javascript:void(0);' class='delete iDelete'>&nbsp;&nbsp;&nbsp;&nbsp;</a--></span>");
			        newRGInfo.appendTo($("#addGroupInfo"));
					var sckeck=$("#sck"+autId);
					if(autId==sckeck.attr("value")){
						sckeck.attr("checked",true);
					}
	   		    });
   		    }else{
   		    	$("#addGroupInfo").html("<div class='confirm'>当前角色下无二级菜单！</div>");
   		    }
		},
		err : function(){
			$("#addGroupInfo").html("<div class='confirm'>当前角色下无二级菜单！</div>");
		}
	});
}
</script>

</head>
<body>
		<div class="mains">
			<div class="col-main">
				<div class="main-hd">
					<span>现有角色</span>
				</div>
				<div class="categories_other"></div>
				<div class="categories">
					<div class="categories_hd">
						<div class="hdl"></div>
						<div class="hdr"></div>
					</div>
					<div class="categories_bd">
						<div class="listContent">
							<div id="roleInfo" class="roleInfo">
								<span class="CDiv">创建一个新角色：</span>
								<a class="del_Btn  AButton blue_button" id="del_Btn"
									href="javascript:addRole();">添加</a>

							</div>
							<br />

							<div id="RoleListDiv" class="groudSpan">
								<div id="rolesDiv"></div>
							</div>
						</div>
					</div>
					<div class="categories_ft">
						<div class="ftl"></div>
						<div class="ftr"></div>
						<div class="ftcon">
							<div class="ftrow"></div>
						</div>
					</div>
				</div>


				<!--这里开始第二部分-->
				<div id="StepTwo" class="contentMain" style="display: none;">
					<div class="categories">
						<div class="categories_hd">
							<div class="hdl"></div>
							<div class="hdr"></div>
							<div class="hdcon">
								<h3>
									当前角色一级菜单
									<button class="open"></button>
								</h3>
								<div class="categories_tab">
									<a href="javascript:void(0)" id="all" class="current"><span></span>
									</a>
								</div>
								<div class="operation"></div>
							</div>
						</div>
						<div class="categories_bd">
							<div class="listContent">
								<form method="post" action="">
									<fieldset>
										<div id="AddGroupDiv" style="padding-bottom: 5px;">
											<span class="CDiv">修改该角色的一级菜单：</span>
											<a class="edite_btn" href="javascript:menuEdite();">编辑</a>

										</div>
										<div id="GroupList">
											<div id="groupsDiv" class="groudSpan" style="width: 98%"></div>
										</div>


										<div id="selectGroupInfo" class="CommanWidthDiv"></div>
									</fieldset>
								</form>
							</div>
						</div>
						<div class="categories_ft">
							<div class="ftl"></div>
							<div class="ftr"></div>
							<div class="ftcon">
								<div class="ftrow"></div>
							</div>
						</div>
					</div>


				</div>
				<!--结束第三部分-->

				<!--这里开始第二部分-->
				<div id="StepThree" class="contentMain" style="display: none;">
					<div class="categories">
						<div class="categories_hd">
							<div class="hdl"></div>
							<div class="hdr"></div>
							<div class="hdcon">
								<h3>
									当前一级菜单下属二级菜单
									<button class="open"></button>
								</h3>
								<div class="categories_tab">
									<a href="javascript:void(0)" id="all" class="current"><span></span>
									</a>
								</div>
								<div class="operation"></div>
							</div>
						</div>
						<div class="categories_bd">
							<div class="listContent">
								<form method="post" action="">
									<fieldset>
										<div id="gcmDiv" class="CommanWidthDiv"></div>

										<div id="cgiInfo2">
											<div id="addGroupInfo"></div>
										</div>
										<div>
											<div style="width: 100%; height: 10px;"></div>
											<div style="width: 100%; text-align: right;">
											</div>
										</div>
									</fieldset>
								</form>
							</div>
						</div>
						<div class="categories_ft">
							<div class="ftl"></div>
							<div class="ftr"></div>
							<div class="ftcon">
								<div class="ftrow"></div>
							</div>
						</div>
					</div>
				</div>
				<!--结束第三部分-->
			</div>
		</div>
	</body>
</html>
