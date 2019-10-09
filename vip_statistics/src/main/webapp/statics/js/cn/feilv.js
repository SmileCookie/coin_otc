$(function(){
	$(".mh4").addClass("current");
	$("#dazong").bind("mouseover",function(){
		removeCurrent();
	});
	
	$("#zhuanye").bind("mouseover",function(){
		addCurrent("pro");
	});
	$("#guibin").bind("mouseover",function(){
		addCurrent("guibin");
	});
	
	showS();
});

function addCurrent(cls){
	$("#mapIn").attr("class", cls);
	$("#miaoshu").attr("class", cls);
}
function removeCurrent(){
	$("#mapIn").removeAttr("class");
	$("#miaoshu").removeAttr("class");
}

function vers(v){
	if(!vip.user.checkLogin(vers,false,false))
		return;
	
	if(v==11){
		Ask2({Msg:"您确定要申请成为专业版用户吗，申请通过后将不能使用大众版？", call:function(){
			versionc(v);
		}});
	}else{
		Ask2({Msg:"您确定要成为贵宾版用户吗？", call:function(){
			versionc(v);
		}});
	}
}

function versionc(v){
	vip.ajax({
		url : "/u/applyVersion?v="+v,
		dataType : "json",
		suc : function(json) {
			parent.Right(json.des, {callback:"window.location.reload()"});
		}
	});
}

function showS(){
	if(!vip.user.loginStatus){
		return;
	}
	var shtml = '';
	vip.ajax({
		url : "/u/getVersion",
		dataType : "json",
		suc : function(json) {
			var v = json.datas;
			addOrremove(v.version);
			if(v.specialReason || v.guestReason){
				if(v.statu==13){
					shtml+='<div style="float: right;">专业版申请未通过审核，<a href="javascript:Alert(\''+v.reason+'\');" style="color: red;line-height: 33px;">查看原因</a></div>';
				}else if(v.statu==18){
					shtml+='<div style="float: right;">贵宾版申请未通过审核，<a href="javascript:Alert(\''+v.reason+'\');" style="color: red;line-height: 33px;">查看原因</a></div>';
				}
			}
			shtml=shtml+'<div class="btn">';
			if(v.special || v.special=='true'){
				shtml+='<a href="javascript:vers(11)">申请成为专业版</a>';
			}
			if(v.guest || v.guest=='true'){
				shtml+='<a  href="javascript:vers(16)">申请成为贵宾版</a>';
			}
			if((!v.special || v.special=='false') && (!v.guest || v.guest=='false')){
				if(v.statu==11){
					var al = "您提交的专业版申请正在审核中，请耐心等待。";
					shtml+='<a class="red" href="javascript:Alert(\''+al+'\');">正在申请成为专业版</a>';
				}else if(v.statu==16){
					var al = "您提交的贵宾版申请正在审核中，请耐心等待。";
					shtml+='<a class="red" href="javascript:Alert(\''+al+'\');">正在申请成为贵宾版</a>';
				}
			}

			shtml+='</div>';
			
			$("#showStatu").html(shtml);
		}
	});
	
}

function addOrremove(status){
	var cooUid = $.cookie("userID"); 
	if(cooUid!=null && cooUid.length){
		if(status==1){
			$("#level").attr("class", "current pro");
			addCurrent("pro");
		}else if(status==2){
			$("#level").attr("class", "current guibin");
			addCurrent("guibin");
		}else{
			removeCurrent();
		}
	}else{
		$("#level").attr("class", "current").css({"background":"none"});
		removeCurrent();
	}
}