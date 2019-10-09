var submit=false;
var currentTab="";
$(function(){
	$("#listSearch").Ui();
	currentTab = $("#currentTab").val();
    SetTab();
    //响应变化tab转换 
    $("#userTab a").each(function(){
         $(this).click(function(){
            currentTab=$(this).attr("id");
            $("#currentTab").val(currentTab);
            var urls="recharge/ajaxList-"+currentTab+"-1-";  
            ajaxUrl(urls);
            SetTab(); 
         });
    });
    //设置tab
    function SetTab(){ 
          $("#userTab a").removeClass("current");
          $("#"+currentTab).addClass("current");
    }	
    $("#idSearch").click(function(){
		var param=DoPost(1);
		
		if(!param)
			return;
		var urls="recharge/ajaxList-"+currentTab+"-1"+param; 
		if(!submit)
		ajaxUrl(urls);
	});
	//页面加载后进行第一次设置
	ConfigTable(true);
});

function recharge(){
	Iframe({Url:"/admin/pay/recharge/charge-"+$("#userName").val(),Width:600,Height:456,Title:"系统充值人民币",scrolling:"no"})
}

function reduce(){
	//资金扣除 sysDecutionBtn
	Iframe({Url:"/admin/pay/recharge/deduction-"+$("#userName").val(),Width:600,Height:456,Title:"系统扣除人民币",scrolling:"no"});
}

var commid = "";
function succonfirm(id){
	Iframe({
	    Url:"/admin/pay/recharge/aoru?connId="+id+"&useTypeId=5",//充值
        zoomSpeedIn		: 200,
        zoomSpeedOut	: 200,
        Width:560,
        Height:460,
        scrolling:"no",
        isIframeAutoHeight:false,
        Title:"在线充值账务录入"
	});
}



function ConfigTable(first){ 
	$("#JumpButton").click(function(){
	     var param=DoPost($("#PagerInput").val())
	     if(!param) 
	    	return;
	     
	 	 var urls="recharge/ajaxList-"+$("#PagerInput").val()+param;  
	     ajaxUrl(urls);
	});
	$("#PagerInput").keydown(function(event){
		 event.keyCode==13 && $("#JumpButton").trigger("click"); 
	});
	//固定表头,就是浏览到下面的部分时候依然可以固定住表的头部
	//设置导航链接成ajax方式
	$(".page_nav  a").each(function(){
		  $(this).AStop();
		  $(this).bind("click",function(){     
			  var urls=$(this).attr("href").replace("recharge","recharge/ajaxList");
			  ajaxUrl(urls);
		  }); 
	});//设置完成 

	$(".item_list_bd").each(function(i){
        $(this).mouseover(function(){
            $(this).css("background","#fff8e1");
        }).mouseout(function(){
        	  $(this).css("background","#ffffff");
        });
    });
	//有误失败
	$(".state").each(function(){
		var $this=$(this);
		$this.idSer=$this.attr("ids");
		$this.setStat=$this.attr("setStat"); 
		if($this.setStat==1){
			$this.title="确定关闭本次提现吗，关闭后此次提现失败，并返还用户的提现资金。";
		}else if($this.setStat==2){
			$this.title="确定本次提现成功吗，确定后系统将提醒用户确认。";
		}
		
		var urls="/admin/pay/recharge/doUpdateStat-"+$this.idSer+"-"+$this.setStat;
		$this.Ask({Title:$this.title,call:function(){
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
			           }
				   }
			});
		}});
	});
	
	//删除
	$(".del").each(function(){
		var $this=$(this);
		$this.idSer=$this.attr("ids");
		
		var urls="recharge/doDel-"+$this.idSer;
		$this.Ask({Title:"确定要删除此汇款记录吗？",call:function(){
			$.ajax({
				   async:true, 
				   cache:false,
				   type:"POST",
				   dataType:"xml", 
				   data:"", 
				   url:urls,
				   error:function(xml){
				     Wrong("获取列表数据失败,请检查网络，可能是网络过慢导致超时或者远程服务出现故障!");
			        	  //关闭lodding动画 
				   },
				   timeout:60000,
				   success:function(xml){
					   if($(xml).find("State").text()=="true"){
				           Right($(xml).find("Des").text());
				           ///刷新显示区域
				           // reload();
				           $("#row"+$this.idSer).remove();
			           }else{
			               Wrong("发生错误:"+$(xml).find("Des").text());
			               reload();
			           }
				   }
			});
		}});
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
}
	function toSearch(){
		var param=DoPost(0);
		if(!param)
			return;
		$("#idSearch").Enable({IsEnable:true}); 
		var urls="recharge-"+currentTab+"-"+1+param;  
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
					  submit=false;
				   }
			}); 
	 }
  function reset(){
	  $("#startDate").val("");
	  $("#endDate").val("");
	  //$("#status").val("");//
	  $("#userName").val("");
  }
    //换页 
	function DoPost(index){	
	  var datas=FormToStr("listSearch");
	  if(datas==null)
          return null;
	 
      var startDate=$("#startDate").val();
      var endDate=$("#endDate").val();
     
      var status=$("#status").val();
      var userName=$("#userName").val();
      var param="";
      
      if(startDate.length>0)
          param+="-"+encodeURI(startDate);
          else
          param+="-";
      if(endDate.length>0)
          param+="-"+encodeURI(endDate);
          else
          param+="-";
    
      if(status.length>0)
          param+="-"+encodeURI(status);
          else
          param+="-";
      if(userName.length>0)
          param+="-"+encodeURI(userName);
      else
          param+="-";
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
		var urls = "recharge-ajaxList-"+currentTab+"-"+ pi + param;
		ajaxUrl(urls, false, false);
	}
	 //重新刷新
	function rechargeSucReload() {
		var pi = "";
		if ($("#PagerInput").val())
			pi = $("#PagerInput").val();
		var param = DoPost(pi);
		if (!param)
			return;
		var urls = "recharge-ajaxList-"+currentTab+"-1-sysrecharge"
		$("#userTab a").removeClass("current");
        $("#currentTab").addClass("current");
		
		ajaxUrl(urls, false, false);
	}
	
	function reload2(){
		Close();
		reload();
	}