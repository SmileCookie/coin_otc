$(function(){
	vip.list.ui();
	vip.list.funcName = "";
	vip.list.basePath = "/admin/pay/user/";
	
	//$("#addExpBtn").Iframe({Url:"/admin/account/user/exp",Width:460,Height:306,Title:"经验值添加",scrolling:"no"});
	
	//页面加载后进行第一次设置
	ConfigTable(true);
	
});

function withdrawRate(id,uname){
	Iframe({Url:"/admin/pay/recharge/withdrawRate-"+id+"-"+getUname(uname),Width:600,Height:456,Title:"设置提现费率",scrolling:"no"})
}
function reload2(){
	Close();
	vip.list.reload();
}

function ConfigTable(first){
	$("#shopslist").Ui();
	/* $("#JumpButton").click(function(){
	     var param=DoPost($("#PagerInput").val())
	     if(!param)  
	    	return;
	     
	 	 var urls="user/ajaxList-"+$("#PagerInput").val()+param;  
	     ajaxUrl(urls);
	});*/
	$("#PagerInput").keydown(function(event){
		 event.keyCode==13 && $("#JumpButton").trigger("click"); 
	});
	//固定表头,就是浏览到下面的部分时候依然可以固定住表的头部
	$("#ListTable").FixedHeader();	
	//设置导航链接成ajax方式
	/*$(".page_nav  a").each(function(){
		  $(this).AStop();
		  $(this).bind("click",function(){     
			  var urls=$(this).attr("href").replace("user","user/ajaxList");
			  ajaxUrl(urls);
		  }); 
	});//设置完成 
*/
	$(".item_list_bd").each(function(i){
        $(this).mouseover(function(){
            $(this).css("background","#fff8e1");
        }).mouseout(function(){
        	  $(this).css("background","#ffffff");
        });
    });
	
	$(".addExpC").each(function(){
		var userName=$(this).attr("uname");
		$(this).Iframe({Url:"/admin/account/user/exp-"+userName,Width:460,Height:306,Title:"经验值添加",scrolling:"no"});
	});
	
	///用户信息
	$(".userInfoA").each(function(){
		$(this).Iframe({
            Url:'/admin/account/user/show-'+$(this).attr("ids"),
            Width:590,
            Height:430,
            Title:"用户信息"
         });
	});
	
	//冻结
	
	
	//解冻noFreez
	$(".noFreez").each(function(){
		var $this=$(this);
		$this.idSer=$this.attr("ids");
		var urls="/admin/pay/freez/noFreez-"+$this.idSer;
		$this.Ask({Title:"确定解冻用户的全部系统资金吗？",call:function(){
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
				           Right($(xml).find("Des").text());
				           ///刷新显示区域
				           reload();
			           }else{
			               Wrong("发生错误:"+$(xml).find("Des").text());
			               reload();
			           }
				   }
			});
		}});
	});
}
	function toSearch(){
		var param=DoPost(0); 
		if(!param)
			return;
		$("#idSearch").Enable({IsEnable:true}); 
		var urls="ajaxList-"+1+param;  
		ajaxUrl(urls);
	}
	/*
	 * ajax的请求url的部分,提取出来
	 */
	 function ajaxUrl(urls){
		 if(urls=="javascript:void(0)"){
			 return;
		 }
	    submit=true;
		$("#ListTable").Loadding({OffsetXGIF:270,OffsetYGIF:180});
		$.ajax({
			   async:true, 
			   cache:false,
			   type:"GET",
			   dataType:"text", 
			   data:"", 
			   url:urls,
			   error:function(xml){
			     Wrong("获取列表数据失败,请检查网络，可能是网络过慢导致超时或者远程服务出现故障!");
		        	  //关闭lodding动画 
		        	  $("#shopslist").Loadding({IsShow:false}); 
		        	  submit=false;
			   },
			   timeout:60000,
			   success:function(xml){
	        	  $("#shopslist").Loadding({IsShow:false}); 
	        	  $("#shopslist").html(xml);
				  ConfigTable(false); 
				  //$("body").ScrollTo(); 
				  submit=false;
			   }
		}); 
	 }
	 
	 function setFees(set , userId){
		 var msg = set==0 ? '确定将该用户设为按普通费率提现吗？' : '确定将该用户设为按区域费率提现吗？';
		 var urls = '/admin/pay/user/setFees';
		 Ask2({Title:msg,call:function(){
				$.ajax({
					   async:true, 
					   cache:false,
					   type:"POST",
					   dataType:"xml", 
					   data:"set="+set+"&userId="+userId,
					   url:urls,
					   error:function(xml){
					     Wrong("获取列表数据失败,请检查网络，可能是网络过慢导致超时或者远程服务出现故障!");
					   },
					   timeout:60000,
					   success:function(xml){
						   if($(xml).find("State").text()=="true"){
					           Right($(xml).find("Des").text());
					           ///刷新显示区域
					           reload();
				           }else{
				               Wrong("发生错误:"+$(xml).find("Des").text());
				               reload();
				           }
					   }
				});
			}});
	 }

//换页 
	function DoPost(index){	
	  	//先进行条件验证,防止错误输入
	    var datas=FormToStr("listSearch");
	    if(datas==null)
	    	return null;
	    var currentTab=$("#currentTab").val();
        //var startDate=$("#startDate").val();
        //var endDate=$("#endDate").val();
        var userName=$("#userName").val();
        var orderWay=$("#orderWay").val();
        
        var param="";
        if(currentTab.length>0)
            param+="-"+encodeURI(currentTab);
          else
            param+="-";
        
        if(userName.length>0)
            param+="-"+encodeURI(userName);
        else
            param+="-"
    	if(orderWay.length>0)
            param+="-"+encodeURI(orderWay);
        else
            param+="-"
		return param;
	}
	
	 //重新刷新
	function reload() {
		var pi = "";
		if ($("#PagerInput").val())
			pi = $("#PagerInput").val();
		var param = DoPost(pi);
		if (!param)
			return;
		var urls = "user/ajax-" + pi + param;
		ajaxUrl(urls);
	}
	
