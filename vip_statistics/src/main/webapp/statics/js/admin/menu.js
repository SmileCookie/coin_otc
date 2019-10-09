 function $_getID(id)
	    {
		    return document.getElementById(id);
	    }
function FrameRedirect(frameId, url){
	if(url.indexOf('/')!=0)
		url="/"+url;
    if (($_getID(frameId) != null) && (url != "#") && (url.length > 0))
    {
        window.frames[frameId].location.href = url;
    }
}
/**
 * Cookies保存导航菜单状态 
 * @param id
 * @return
 */
function operationMenuState(id)
{
	var menuState=$.cookie("menuState");
	//cookie中存在 此值，
	if(menuState){//存在 值 时
		if(menuState.indexOf(","+id+",")>=0)
		{
			menuState=menuState.replace(","+id+",","");
		}else{
			menuState+=","+id+",";
		}
	}else {
		menuState=","+id+",";
	}
	$.cookie("menuState",menuState, {  path: '/admin' });
	//alert(menuState);
}
function CurrentActive(id)
{
	//var CurrentState=$.cookie("CurrentState");
	//cookie中存在 此值，
	//if(CurrentState){//存在 值 时
	//	$.cookie("CurrentState",null);
	//}else{
	$.cookie("CurrentState",null);
	$.cookie("CurrentState",id);
	//}
	//alert(CurrentState);
//	alert(id);
}
//选择按钮
function selectmenu(ids){
	$("li.current").removeClass("current");
	$("#"+ids).addClass("current");
}

$(function(){
	$("#middle").click(function(){
        if($(this).hasClass("closed")){
     	   $(this).removeClass("closed");
     	   $("#leftList").show();
          }else{
         	 $(this).addClass("closed");
       	     $("#leftList").hide();
          }
	});

	var selURL=window.location.pathname;
	selURL=selURL.substring(1,selURL.length);
	
	var cmCookie=$.cookie(vip.cookiKeys.rid);//角色ID作为角色相对应的文件名称,$.cookie("AdminID",null, { path: '/' });
	var menuSate="";
	if(cmCookie){
		var uName=$.cookie(vip.cookiKeys.aname);
		$("#userMsg").empty();
		var userMsg=$("<a href='javascript:void(0);'>"+uName+"</a> &nbsp; <a id='loginOut' href='javascript:loginOut();'>退出</a>");
		userMsg.appendTo($("#userMsg"));
		vip.ajax({
			url : "/admin/competence/menu/roleJsons",
			dataType : "json",
			suc : function(json){
				var menuState=$.cookie("menuState");
				if(!menuState){
					menuState="";
				}
				var str="";
				var CurrentState=$.cookie("CurrentState");
				$.each(json.datas , function(index , cont){
						var id = cont.id;
		  				var css=""; 
		  				var open="";
		  				var style="";
			  			if(menuState.indexOf(","+id+",")>=0){
			  				open=" open=\"true\"";
			  				style=" style=\"display:block\"";
			  			}else{
			  				style=" style=\"display:none\"";
			  		    }
			  			var lis='<div class="menubox" id="'+id+'"> <h3 class="title" ><a href="javascript:void(0);" '+open+'>'+cont.name+'</a></h3> <ul class="group" '+style+'>';
			  			
			  			$.each(cont.viewFunctions , function(i , sc){
			  				  var idd = sc.id;
							  var href = sc.url;
							  if(href.indexOf('/')!=0)
								  href="/"+href;
							  var liCss="selA";
							  lis+='<li id="'+idd+'" class="'+liCss+'"><a  href="'+href+'" onclick="selectmenu('+idd+')"  target="Main"  aId="'+idd+'"   >'+sc.name+'</a></li>';
							  css="";
							  open="";
							  style="";
			  			});
						lis+='</ul></div>';
						str+=lis;
				});
				
				
				
				$("#mainmenu").append(str);
			    $("#mainmenu").show();
			    $(".menubox").each(function(){
				     $(this).find("a:first").bind("click",function(){
				    	 var aid=$(this).parent().parent().attr("id");
				    	 operationMenuState(aid);
				    	 if(!$(this).attr("open")){
				    		  $(this).attr("open","true");
				              $(this).parent().parent().find("ul").slideDown("normal");
				    	 }else{
				    	     $(this).removeAttr("open");
				    	     $(this).parent().parent().find("ul").slideUp("fast");//each(function (){$(this).hide()});//.slideUp("normal");
				    	 }
				     });
				});
			    $("#leftList a").each(function(){
	               $(this).LineOne();
	            });
			}
		});
	}else{ 
		Redirect("/ad_admin/admin_login/");
	}
});

function loginOut(){
	//处理登录
	Ask2({
	     call : function(data){
	    	Redirect(vip.vipDomain + "/ad_admin/logout");
	    	return; 
	     },
	     Title : "您确定要注销么?"
	 }); 
}
