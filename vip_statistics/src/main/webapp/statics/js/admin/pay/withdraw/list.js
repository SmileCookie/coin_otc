var submit=false;
$(function(){
	vip.list.ui();
	vip.list.funcName = "提现";
	vip.list.basePath = "/admin/pay/withdraw/";
	
	ConfigTable(true);
	
	$("#statistics").click(function(){
		Iframe({
            Url:'/admin/pay/withdraw/withdrawStati',
            Width:660,
            Height:430,
            Title:"提现统计"
        });
	});
});

function success(id){
	Iframe({
	    Url:"/admin/pay/withdraw/aoru?connId="+id+"&useTypeId=4",
        zoomSpeedIn		: 200,
        zoomSpeedOut	: 200,
        Width:560,
        Height:460,
        scrolling:"no",
        isIframeAutoHeight:false,
        Title:"提现账务录入"
	});
}

function comment(id){
	Iframe({
		Url:"/admin/pay/withdraw/comment?sn="+id,
		zoomSpeedIn		: 200,
		zoomSpeedOut	: 200,
		Width:560,
		Height:260,
		scrolling:"no",
		isIframeAutoHeight:false,
		Title:"备注"
	});
}

//成功后撤销成功  sucFail
function sucFail(ids){
	if(!couldPass){
		commid=ids;
		googleCode("sucFail", true);
		return;
	}
	couldPass = false;
	
	Ask2({Title:"确定此次提现是失败的吗？确定后将返还用户资金。",call:function(){
		var urls="/admin/pay/withdraw/sucFail?ids="+commid+"&mCode="+ids;
		$.ajax({
			   async:true, 
			   cache:false,
			   type:"POST",
			   dataType:"xml", 
			   data:"",
			   url:urls,
			   error:function(xml){
			     Wrong("获取列表数据失败,请检查网络，可能是网络过慢导致超时或者远程服务出现故障!");
			   },
			   timeout:60000,
			   success:function(xml){
				   if($(xml).find("State").text()=="true"){
			           ///刷新显示区域
			           reload();
			           Right($(xml).find("Des").text());
		           }else{
		        	   reload();
		               Wrong("发生错误:"+$(xml).find("Des").text());
		           }
			   }
		});
	}});
}

function ConfigTable(first){

	$(".item_list_bd").each(function(i){
        $(this).mouseover(function(){
            $(this).css("background","#fff8e1");
        }).mouseout(function(){
        	  $(this).css("background","#ffffff");
        });
    });
	
	$(".failState").each(function(){//提现失败
		var $this=$(this);
		var id=$this.attr("ids");
		$this.Iframe({Url:"/admin/pay/withdraw/toFail-"+id,Width:760,Height:506,Title:"提现失败",scrolling:"no"});
	});
	
	/***确认可以提款***/
	$(".confirmTrue").each(function(){
		var $this=$(this);
		$this.idSer=$this.attr("ids");
		$this.Iframe({Url:"/admin/pay/withdraw/confirmWithdraw-"+$this.idSer,Width:560,Height:286,Title:"确定打款时间",scrolling:"no"});
	});
	
	/***提交提现申请到商户系统***/
	$(".customerAudit").each(function(){
		console.log('-----');
		var $this=$(this);
		$this.idSer=$this.attr("ids");
		$this.Iframe({Url:"/admin/pay/withdraw/merchantWithdraw-"+$this.idSer,Width:560,Height:286,Title:"客服审核（提交申请到商户系统）",scrolling:"no"});
	});
	
	$(".DeleAllSel").click(function(){
		if($(this).attr("checked")){
			$(".deleteck").attr("checked",true);
		}else{
			$(".deleteck").attr("checked",false);
		}
	});
	
	//绑定两个批量删除按钮的样式
	$(".manyok").click(function(){
		var returns=false;
	     var arr = $('.deleteck');
	     var idStr="";
	  	 arr.each(function(){
	  		if($(this).attr("checked")){
	  			idStr+=","+$(this).val();
		  		returns=true;
		  	}
	  	 });
	     if(!returns){
	    	 Pause(this,300); //之所以这样做是因为这个Wrong跟Ask同系一个控件演变,直接弹出会因为上一个控件还未执行完毕而导致无法显示,如果用系统的alert直接便可弹出
	    	 this.NextStep = function(){
	    		 Wrong("您还没有选中任何项"); 
	    	 }
	    	 return;
	     }
	     idStr=idStr.substr(1);
		
		Ask({
			Msg:"确定要批量确认选中项吗？",
			callback:"delMany('"+idStr+"')"
		})
	});
	
	
	$(".manytext").click(function(){
		var isAll = false;
	    isAll = $(this).attr("isAll")=='true' ? true : false;
		var returns=false;
	     var arr = $('.deleteck');
	     var idStr="";
	  	 arr.each(function(){
	  		if($(this).attr("checked")){
	  			idStr+=","+$(this).val();
		  		returns=true;
		  	}
	  	 });
	     if(!returns && !isAll){
	    	 Pause(this,300); //之所以这样做是因为这个Wrong跟Ask同系一个控件演变,直接弹出会因为上一个控件还未执行完毕而导致无法显示,如果用系统的alert直接便可弹出
	    	 this.NextStep = function(){
	    		 Wrong("您还没有选中任何项"); 
	    	 }
	    	 return;
	     }
	     idStr=idStr.substr(1);
	     
	     
	     doManyText(idStr,isAll);
	});
	
	$(".manysuc").click(function(){
		var isAll = false;
	    isAll = $(this).attr("isAll")=='true' ? true : false;
	    
		var returns=false;
	     var arr = $('.deleteck');
	     var idStr="";
	  	 arr.each(function(){
	  		if($(this).attr("checked")){
	  			idStr+=","+$(this).val();
		  		returns=true;
		  	}
	  	 });
	     if(!returns && !isAll){
	    	 Pause(this,300); //之所以这样做是因为这个Wrong跟Ask同系一个控件演变,直接弹出会因为上一个控件还未执行完毕而导致无法显示,如果用系统的alert直接便可弹出
	    	 this.NextStep = function(){
	    		 Wrong("您还没有选中任何项");
	    	 }
	    	 return;
	     }
	     idStr=idStr.substr(1);
	     var tips = "确定要批量确认选中项吗？";
	     if(isAll){
	    	 tips = "确定要批量处理所有处理中的提现吗？";
	     }
	     
		 Ask2({
			 Msg : tips,
			 call : function(){
			 	Iframe({
				    Url:"/admin/pay/withdraw/addFinanAll?idStr="+idStr+"&isAll="+isAll,
			        zoomSpeedIn		: 200,
			        zoomSpeedOut	: 200,
			        Width:560,
			        Height:460,
			        scrolling:"no",
			        isIframeAutoHeight:false,
			        Title:"提现批量确认账务录入"
				});
			 }
		 });
		 
	});
	
	$(".sucManyFail").click(function(){
		var isAll = false;
	    isAll = $(this).attr("isAll")=='true' ? true : false;
	    
		var returns=false;
	     var arr = $('.deleteck');
	     var idStr="";
	  	 arr.each(function(){
	  		if($(this).attr("checked")){
	  			idStr+=","+$(this).val();
		  		returns=true;
		  	}
	  	 });
	     if(!returns && !isAll){
	    	 Pause(this,300); //之所以这样做是因为这个Wrong跟Ask同系一个控件演变,直接弹出会因为上一个控件还未执行完毕而导致无法显示,如果用系统的alert直接便可弹出
	    	 this.NextStep = function(){
	    		 Wrong("您还没有选中任何项");
	    	 }
	    	 return;
	     }
	     idStr=idStr.substr(1);
	     var tips = "确定要批量确认选中项吗？";
	     if(isAll){
	    	 tips = "确定要批量处理所有处理中的提现吗？";
	     }
	     
		 Ask({
			 Msg : tips,
			 callback:"doManySucFail('"+idStr+"')"
		 });
	});
	
	$(".manyJisuan").click(function(){
		var isAll = false;
	    isAll = $(this).attr("isAll")=='true' ? true : false;
		var returns=false;
	     var arr = $('.deleteck');
	     var idStr="";
	  	 arr.each(function(){
	  		if($(this).attr("checked")){
	  			idStr+=","+$(this).val();
		  		returns=true;
		  	}
	  	 });
	     if(!returns && !isAll){
	    	 Pause(this,300); //之所以这样做是因为这个Wrong跟Ask同系一个控件演变,直接弹出会因为上一个控件还未执行完毕而导致无法显示,如果用系统的alert直接便可弹出
	    	 this.NextStep = function(){
	    		 Wrong("您还没有选中任何项"); 
	    	 }
	    	 return;
	     }
	     idStr=idStr.substr(1);
	     
	     
	    Iframe({
	        Url:'/admin/pay/withdraw/jisuan-'+idStr+'-'+isAll,
	        Width:590,
	        Height:430,
	        Title:"统计提现金额"
	     });
	});
}


function doManySuc(idStr , isAll){
	
	Iframe({
	    Url:"/admin/pay/withdraw/aoru?useTypeId=4&idStr="+idStr+"&isAll="+isAll,
        zoomSpeedIn		: 200,
        zoomSpeedOut	: 200,
        Width:560,
        Height:460,
        scrolling:"no",
        isIframeAutoHeight:false,
        Title:"批量确认账务录入"
	});
	
//	if(submit){
//		return;
//	}
//	submit = true;
//	$.ajax({
//		   async:true,
//		   cache:false,
//		   type:"POST",
//		   dataType:"xml",
//		   data:"ids="+idStr+"&isAll="+isAll,
//		   url:"/admin/pay/withdraw/doManySuc",
//		   error:function(xs){Wrong("删除数据失败,请检查网络，可能是网络过慢导致超时或者远程服务出现故障!");submit = false;},
//		   timeout:60000,
//		   success:function(xml){
//			   submit = false;
//	         if($(xml).find("State").text()=="true"){
//	         	Alert("成功处理了选中数据!");
//	         	reload();
//	         }else{
//	           	Wrong("发生错误:"+$(xml).find("Des").text());
//	         }
//		   }
//	});
}


function doManySucFail(idStr){
	if(submit){
		return;
	}
	
	if(!couldPass){
		commid=idStr;
		googleCode("doManySucFail", true);
		return;
	}
	couldPass = false;
	submit = true;
	$.ajax({
		   async:true,
		   cache:false,
		   type:"POST",
		   dataType:"xml",
		   data:"ids="+commid+"&mCode="+idStr,
		   url:"/admin/pay/withdraw/sucFailMany",
		   error:function(xs){Wrong("删除数据失败,请检查网络，可能是网络过慢导致超时或者远程服务出现故障!");submit = false;},
		   timeout:60000,
		   success:function(xml){
			   submit = false;
	         if($(xml).find("State").text()=="true"){
	         	Alert("成功处理了选中数据!");
	         	reload();
	         }else{
	           	Wrong("发生错误:"+$(xml).find("Des").text());
	         }
		   }
	});
}

function doManyText(idStr , isAll){
	 Iframe({
        Url:'/admin/pay/withdraw/yeepay-'+idStr+'-'+isAll+"-"+$("#tab").val(),
        Width:590,
        Height:430,
        Title:"易宝批处理文本"
     });
}
var commid="";
function delMany(idStr){
	if(!couldPass){
		commid=idStr;
		 googleCode("delMany", true);
		 return;
	}
	couldPass = false;
	$.ajax({
		   async:true,
		   cache:false,
		   type:"POST",
		   dataType:"xml",
		   data:"ids="+commid+"&mCode="+idStr,
		   url:"/admin/pay/withdraw/doManyConfirm",
		   error:function(xml){Wrong("删除数据失败,请检查网络，可能是网络过慢导致超时或者远程服务出现故障!");},
		   timeout:60000,
		   success:function(xml){
            if($(xml).find("State").text()=="true"){
	            var ids=$(xml).find("MainData").text();
            	Alert("成功处理了选中数据!");
            	reload();
            }else{
              	Wrong("发生错误:"+$(xml).find("Des").text());
            }
		 }
	});

}

function getProvinceAndCityById(pid,cid,did,showDiv){
	$.ajax({
		type:"GET",
		url:"/Useradmin/menu/area.xml",
		cache: true,		    
		dataType:"xml",			
		success:function(xml){
			var str='不详';
			var ptr=$(xml).find("province[id='"+pid+"']");
			str+=$(ptr).attr("name")+'&';
			var ctr=$(ptr).find("city[id='"+cid+"']");	
			str+=$(ctr).attr("name")+'&';

			var strShow="";
			proStr=$(ptr).attr("name");
			cityStr=$(ctr).attr("name");
			if(proStr){
				strShow+=proStr;
			}else{
				strShow+="-";
			}
			if(cityStr){
				strShow+=cityStr;
			}else{
				strShow+="-";
			}
			$('#'+showDiv).html(strShow);//+$(dtr).attr("name")
			iop=str;
			return str;
		}
	});
}

function reload(){
	vip.list.reload();
}
	
function reload2(){
	Close();
	reload();
}