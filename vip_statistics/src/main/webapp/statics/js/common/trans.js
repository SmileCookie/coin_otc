var reloadDue=0;//临时任务，加急处理的
var cookieLoadSpace = $.cookie("trans_load_space");
var loadSpace=30;//5秒钟一次正常的，正常任务时间周期
if(cookieLoadSpace != null && vip.tool.isFloat(cookieLoadSpace)){
	loadSpace = parseFloat(cookieLoadSpace);
}

var loadTimes=0;//总循环次数
var longTime = 0;//
var isRunning=false;
var userInit = false;
var marketInit = false;
vip.tips["取消S"] = ["取消","Cancel"];
function gengxin(isLow){
	if(isLow==1){
		if(loadSpace>10){
			loadSpace-=10;
			$("#gengxin").text(loadSpace/10 + vip.L("秒更新"));
			var pl = loadSpace/10;
			//alert(vip.L("数据正常更新速度加快为x1秒,可能需要几秒钟生效！" , reps));
			 $.jGrowl(vip.L("数据正常更新速度加快为x1秒,可能需要几秒钟生效！" , [{k : "x1" , v : pl}]), { life: 5000,position:"bottom-right" });
			 lastTime=0;reloadDue=0;
			 $.cookie("trans_load_space" , loadSpace);
		}else
			  $.jGrowl(vip.L("已经是最快的更新速度了"), { life: 5000,position:"bottom-right" });
	}else{
			if(loadSpace<150){
				loadSpace+=10;
				var pl = loadSpace/10;
				$("#gengxin").text(pl+"秒更新");
				$.jGrowl(vip.L("数据正常更新速度减慢为x1秒,可能需要几秒钟生效！" , [{k : "x1" , v : pl}]), { life: 5,position:"bottom-right" });
				lastTime=0;
				reloadDue=0;
				$.cookie("trans_load_space" , loadSpace);
			}else
				$.jGrowl(vip.L("不支持更慢的更新速度"), { life: 5000,position:"bottom-right" });
	}
	
}

function dangwei(isLow){
	if(isLow==1){
		       if(length==50){ $.jGrowl(vip.L("已经是最多的档位显示了"), { life: 5000,position:"bottom-right" });
		    }else
			{
			   if(length==5)
				   length=10;
			   else if(length==10)
				   length=20;
			   else if(length==20)
				   length=50;
			   else
				   length=5;
				$("#dangwei").text(vip.L("查看")+length+vip.L("档"));
				   $.jGrowl(vip.L("数据显示档位调整为x1档,可能需要几秒钟生效！" , [{"k" : "x1" , "v" : length}]), { life: 5,position:"bottom-right" });
				   lastTime=0;reloadDue=0;
			  }
		}else
		{
			if(length==5){ $.jGrowl(vip.L("已经是最少的档位显示了"), { life: 5000,position:"bottom-right" });
		    }else
			   {
			   if(length==10)
				   length=5;
			   else if(length==20)
				   length=10;
			   else if(length==50)
				   length=20;
			   else
				   length=5;
				$("#dangwei").text(vip.L("查看")+length+vip.L("档"));
				   $.jGrowl(vip.L("数据显示档位调整为x1档,可能需要几秒钟生效！" , [{"k" : "x1" , "v" : length}]), { life: 5000,position:"bottom-right" });
				   lastTime=0;reloadDue=0;
			  }
		
		}

}


var inAjaxing = false;

function getCurr(){
     isRunning=true; 
     $.getJSON(entrustUrlBase+"Line/GetTrans-"+market+"?lastTime="+lastTime+"&length="+length+"&jsoncallback=?",  function(result) {
    	 show(result,length);
	 }); 
}

//当前用户可用的数量
var currentUserNumber=0; 
//当前用户可用的购买方的数量
var currentUserExchange=0

var currentPrice=1000;

function getMoney(){
		 $.getJSON(vip.vipDomain+"/u/getBalance?callback=?",  function(result) {
			 $(".b_tradinfo h2").text($.cookie(vip.cookiKeys.uname));
			 
	    	 var okMoney=result.funds[1];
	    	 var okBtc=result.funds[4];
	    	 var okLtc=result.funds[7];
	    	 var okEth=result.funds[19];
	    	 var okbtq=result.funds[10];
	    	 if(market=='btcdefault')
	    	 {
	    		  currentUserNumber=okBtc;
	    		  currentUserExchange=okMoney;
	    	 }
	    	 else if(market=='ltcdefault')
	    	 {
	    		 currentUserNumber=okLtc;
	    		 currentUserExchange=okMoney;
	    	 }
	    	 else if(market=='ethdefault')
    		 {
    		   currentUserNumber=okEth;
    		   currentUserExchange=okMoney;
    		 }
	    	 else if(market=='btqdefault')
    		 {
    		    currentUserNumber=okbtq;//暂时没有提供
    		    currentUserExchange=okBtc;
    		 }
	    	 //先格式化在显示是为了统一，内部所有数据都统一最小化单位存储，显示的时候再格式化
	    	 $("#exchangeBiNum").html(currentUserExchange);
	    	 $("#numberBiNum").html(currentUserNumber);
	    	
	    	 $("#suggestSellNumber").html(currentUserNumber);
	    	 $( "#sellSlider" ).slider( "option", "max", currentUserNumber );
	    	 var funds = result.funds;
	    	 if($(".b_tradinfo .bd em").length > 0){
				$(".b_tradinfo .bd em").each(function(i){
					$(this).text(funds[i]);
				}); 
				
				$(".b_tradinfo h3").text(funds[16]);
			 }
	    	 
	    	 if($("#finaPanelDown2 b").length > 0){
				$("#finaPanelDown2 b").each(function(i){
					$(this).text(funds[i]);
				});
				var hasLoan = funds[15] == 1 ? vip.L("借入") : vip.L("借出");
				$("#finaPanelDown2 .dai").html($("#finaPanelDown2 .dai").html().replaceAll("[XX]" , hasLoan));
				$(".etinfo .dai li").css("background","none");
			 }
	    	 
	    	 userInit = true;
		 });
		 
		 if(!vip.user.cookieInit)
			 vip.user.zcticker();
}


function timeChatBaddy(){
	longTime = window.setInterval(function(){
		 if(isRunning)
			   return;
		loadTimes++;//这个变量持续增长 
		if((loadTimes % loadSpace > 0) && reloadDue == 0)//没有到既定的执行周期并且没有临时加急任务，就返回
		   return;
		if(reloadDue>0)//主动触发（设置为1）的时候才会进入这个时间周期，达到快速执行的目的
			reloadDue++;
		if(reloadDue>50){//快速执行够多了，就回复到正常的执行轨道中来
			reloadDue=0;
			return; 
		}
		if(reloadDue%5>0)//这种任务也是0.5秒才执行一次
			return;
		
		getCurr();
	}, 100);//0.1秒钟执行一次
	vip.user.uticker();
 	setInterval(function(){
 		vip.user.uticker();
 	},30000);
}


$(function() {
	
	jQuery(".b_tradtab_pro").slide({mainCell:".bd",titCell:".d_tradtab ul li",trigger:"click",endFun:function(){Set2DivHeight();}});
	$("#dangwei").text(length+vip.L("档"));
	$("#gengxin").text(loadSpace/10+vip.L("秒更新"));
	$("#BuyTab h3").text(vip.L("买入")+numberBiEn);
	$("#BuyTab").bind("click",function(){

		$("#listFirestDiv").show();
		$("#d_marketCon").show();
		$("#d_market").show();
		$("#p2").hide();
		$("#p1").fadeIn();
		
	});
	$("#EntrustManageTab").bind("click",function(){
		
		$("#listFirestDiv").hide(); 
		$("#d_marketCon").hide();
		$("#p3").fadeIn();
		$("#p2").hide();
		$("#d_market").hide();
		 getRecord(1,-1,0,0,0,0,0,0,0,3);
	});
	$("#KlineCharge").bind("click",function(){
		document.kline.action = vip.transDomain+"/markets/"+numberBiEn;//+"full";
    	document.kline.target = "_blank";
    	document.kline.submit();
    	
    	$("#BuyTab").trigger("click");
		
		/*$("#klineFullScreen").attr("src",vip.transDomain+"/Line/"+numberBiEn);
		$("#d_market").hide(); 
		$("#p4").fadeIn();
		$("#p3").hide();
		$("#d_marketCon").hide();
		$("#listFirestDiv").hide();*/
	});
	$("#SellTab h3").text(vip.L("卖出")+numberBiEn);
	$("#SellTab").bind("click",function(){
	
		$("#listFirestDiv").show();
		$("#d_marketCon").show();
		
		$("#p1").hide();
		$("#p2").fadeIn();
		$("#d_market").show();
	});
	
	$("#changeToPro").attr("href",vip.transDomain+"/"+numberBiEn+"Pro");
	$("#changeSimple").attr("href",vip.transDomain+"/"+numberBiEn);
	
	if(numberBiEn=='LTC'){
		$("#t1").removeClass("on");
		$("#t2").addClass("on");
	}
	if(numberBiEn=='BTQ'){
		$("#t1").removeClass("on");
		$("#t3").addClass("on");
	}
	
	$("#listFirestDiv").show();
	$("#d_marketCon").show();
	
	useSafePwd();

	
	show([{"lastTime":1397804744068,"listUp":[],"listDown":[],"transction":[]}],length);
//	if($("#useSafe").val()==0){
//		$(".safePassword").hide();
//		$("#closeSafe1").text("开启").attr("href","javascript:startSafePwd()");
//		$("#closeSafe2").text("开启").attr("href","javascript:startSafePwd()");
//	}
	getCurr(); 
	timeChatBaddy();
//	getMoney();
});

 function controlTrade(){
	 if($("#closeTrade").text()==vip.L("关闭") + '↑')
		 {
		     $("#closeTrade").text(vip.L("显示") + '↓');
		     $("#mk_table_con").hide();
		 }else{
			 $("#closeTrade").text(vip.L("关闭") + '↑');
			 $("#mk_table_con").show();
		 }
 }

var isShowLogin=false;


//获取未完成的列表
function getRecordFirst(){
	if(vip.user.isLogin()){
	
	if (inAjaxing) {
		return; 
	} 
	 getMoney();
	inAjaxing = true;
	$.getJSON(entrustUrlBase+"Record/Get-"+market+"?status=3&lastTime="+lastTimeRecord+"&jsoncallback=?",  function(result) {
		inAjaxing = false;
		if(result[0].lastTime==-1){
			//说明没登陆
			if(!isShowLogin)//仅仅弹出来一次
				{isShowLogin=true;
			   vip.user.login();
				}
			return;
		}
		isShowLogin=false;
		if(result[0].lastTime==0){
			Wrong(bitbank.L("系统忙碌，请稍候！"));
			return;
		}
		if(lastTimeRecord!=result[0].lastTime){
		  lastTimeRecord=result[0].lastTime;
	       showRecord(result,0);
		}
	 });
	//获取成交数据
	if(trade_record_page_index <= 1)
		getTradeRecordFirst(lastTimeRecord, trade_record_page_index);
	
	}else{if(!isShowLogin){vip.user.login();isShowLogin=true;}}
		
}

var trade_record_page_index=1;

function getTradeRecordFirst(lastTimeRecord, pageIndex, dateTo){
	var pageSize=10;
	if(dateTo && dateTo == 5){
		pageSize = 20;
	}else{
		dateTo = 0;
	}
	$.getJSON(entrustUrlBase+"Record/traderecord-"+market+"?&pageIndex="+pageIndex+"&pageSize="+pageSize+"&dateTo="+dateTo+"&lastTime="+lastTimeRecord+"&jsoncallback=?",  function(result) {
		var record=result[0].record;
		if(result[0].lastTime==0){
			//Wrong(vip.L("系统忙碌，请稍候！"));
			return;
		}
		var price, balance, amount, type, time;
		var res = [];
		for(var i=0;i < record.length;i++){
			var price=record[i][0];
			var balance=record[i][1];
			var amount=record[i][2];
			var type=record[i][3];
			var time=record[i][4];
			
			var nameType=vip.L("买入");
			var classT = "pi";
			if(type == 0){
				nameType = vip.L("卖出");
				classT = "po";
			}
			
			var date = new Date(time);
			var nowStr = date.format("MM-dd hh:mm:ss"); 
			
			var cla='';
			if(i%2!=0)
				cla=' class="double" ';
			var row= '<dd '+cla+'>'+
			'<span class="t1" title="'+date.format("yyyy-MM-dd hh:mm:ss")+'">'+nowStr+'</span>'+
			'<span class="t2 '+classT+'">'+nameType+'</span>'+
			'<span class="t3">'+price+'</span>'+
			'<span class="t4">'+amount+'</span>'+
			'<span class="t5">'+balance+'</span>'+
			'</dd>';
			res.push(row);
		}
		if(record.length<1){
			$("#listFirest2").html('<div class="air-tips"><p>'+vip.L("暂时没有符合要求的记录")+'</p></div>');
			$("#page_navA2").html('');
		}else{
		   $("#listFirest2").html(res.join(""));
		   var rtn = GetPaper2(pageIndex,result[0].count,dateTo);
		   $("#page_navA2").html("<div class=\"con\">"+rtn+"</div>");
		}
		
		trade_record_page_index = pageIndex;
	});
}

function getRecord(pageIndex,type,timeFrom,timeTo,numberFrom,numberTo,priceFrom,priceTo){
	getRecord(pageIndex,type,timeFrom,timeTo,numberFrom,numberTo,priceFrom,priceTo,0);
}

var statisticsRecord = null;

function getRecord(pageIndex,type,timeFrom,timeTo,numberFrom,numberTo,priceFrom,priceTo,status,dateTo,pageSize){
	if(!pageSize)
		pageSize = 10;
	
	var s = 0;
	if(status){
		s = status;
	}
	if (inAjaxing) {
		//Wrong("sorry click too fast!");
		return;
	} 
	inAjaxing = true;
//		$("#p3").Loadding({
//			OffsetXGIF : 270,
//			OffsetYGIF : 80 
//		});
	$.getJSON(entrustUrlBase+"Record/Get-"+market+"?lastTime="+lastTimeRecord+"&type="+type+"&pageIndex="+pageIndex+"&pageSize="+pageSize+"&timeFrom="+timeFrom+"&timeTo="+timeTo+"&numberFrom="+numberFrom+"&numberTo="+numberTo+"&priceFrom="+priceFrom+"&priceTo="+priceTo+"&status="+s+"&dateTo="+dateTo+"&jsoncallback=?",  function(result) {
		inAjaxing = false;
		//关闭lodding动画 
//			$("#p3").Loadding({
//				IsShow : false
//			});    
		if(result[0].lastTime==0){
			Wrong(bitbank.L("系统忙碌，请稍候！"));
			return;
		}
		showRecord(result,pageIndex,pageSize);
		
//		if(pageIndex == 1 && dateTo != 5){
//			$.getJSON(entrustUrlBase+"Record/getStatisticsRecord-"+market+"?lastTime="+lastTimeRecord+"&type="+type+"&timeFrom="+timeFrom+"&timeTo="+timeTo+"&numberFrom="+numberFrom+"&numberTo="+numberTo+"&priceFrom="+priceFrom+"&priceTo="+priceTo+"&status="+s+"&jsoncallback=?"
//					,  function(result) {
//				var record=result[0].record;
//				if(result[0].lastTime==0){
//					//Wrong(vip.L("系统忙碌，请稍候！"));
//					return;
//				}
//				statisticsRecord = record;
//				var buy_no_complete = 0,buy_total_amount=0,buy_avg_money=0,buy_total_money=0;
//				var sell_no_complete=0,sell_total_amount=0,sell_avg_money=0,sell_total_money=0;
//				for(var i=0;i < record.length;i++){
//					var numbers=record[i][0];
//					var completeNumber=record[i][1];
//					var completeTotalMoney=record[i][2];
//					var types=record[i][3];
//					var status=record[i][4];
//					if(types == 1){
//						if(status !=2 && status != 1)	//买入未成交
//							buy_no_complete+= Number(numbers) - Number(completeNumber);
//						else{	//已买入数量
//							buy_total_amount+= Number(completeNumber);
//							buy_total_money+=Number(completeTotalMoney);
//						}
//					}else if(types == 0){
//						if(status != 2 && status != 1)	//卖出未成交
//							sell_no_complete+= Number(numbers) - Number(completeNumber);
//						else{	//已卖出数量
//							sell_total_amount+= Number(completeNumber);
//							sell_total_money+=Number(completeTotalMoney);
//						}
//					}
//				}
//				if(buy_total_money > 0 && buy_total_amount > 0)
//					buy_avg_money = buy_total_money/buy_total_amount;
//				if(sell_total_money > 0 && sell_total_amount > 0)
//					sell_avg_money = sell_total_money/sell_total_amount;
//				
//				var symbal = "";
//				var currency = "";
//				if(market == "btcdefault"){
//					symbal = "฿";
//					currency = "￥";
//				}
//				if(market == "ltcdefault"){
//					symbal = "Ł";
//					currency = "￥";
//				}
//				if(market == "btqdefault"){
//					symbal = "Q";
//					currency = "฿";
//				}
//				$("#buy_no_complete").text(symbal+fNumber(buy_no_complete));
//				$("#buy_total_amount").text(symbal+fNumber(buy_total_amount));
//				$("#buy_avg_money").text(currency+buy_avg_money.toFixed(2));
//				//$("#buy_total_money").text(currency+buy_total_money.toFixed(2));
//				$("#sell_no_complete").text(symbal+fNumber(sell_no_complete));
//				$("#sell_total_amount").text(symbal+fNumber(sell_total_amount));
//				$("#sell_avg_money").text(currency+sell_avg_money.toFixed(2));
//				//$("#sell_total_money").text(currency+sell_total_money.toFixed(2));
//				//$("#buy_sell_caer").text(currency+Math.abs(buy_total_money-sell_total_money).toFixed(2));
//				
//				var c_money = (buy_total_money-sell_total_money).toFixed(2);
//				var c_amount = fNumber(buy_total_amount-sell_total_amount);
//				$("#buy_sell_over_money").text(currency+c_money);
//				$("#buy_sell_over_amount").text(symbal+c_amount);
//				if(c_money == 0)
//					$("#buy_sell_avg_money").text(currency+"0.00");
//				else if(c_amount == 0)
//					$("#buy_sell_avg_money").text(currency+"0.00");
//				else
//					$("#buy_sell_avg_money").text(currency+(c_money/c_amount).toFixed(2));
//		   });
//			$("#btips").show(100);
//		}else{  
//			if(statisticsRecord == null)
//				$("#btips").hide();
//		}
		$("#btips").hide();
   });
}

/**
 * 是否显示结果
 * @param result 结果
 * @param pageIndex 大于0代表需要显示分页
 */
function showRecord(result,pageIndex,pageSize){
	var record=result[0].record;
	var res = [];
	var maxLength=record.length;
	if(pageIndex==0&&maxLength>8&&!sellDao)
		maxLength=8;
	for(var i=0;i<maxLength;i++){
		var entrustId=record[i][0];
		var unitPrice=record[i][1];
		var numbers=record[i][2];
		var completeNumber=record[i][3];
		var completeTotalMoney=record[i][4];
		var types=record[i][5];
		var submitTime=record[i][6];
		var status=record[i][7];
		
		var classT = "pi";
		var nameType=vip.L("买入");
		if(status == -1)
			nameType = vip.L("计划买入");
		if(types == 0){
			nameType = vip.L("卖出");
			if(status == -1)
				nameType = vip.L("计划卖出");
			classT = "po";
		}
		
        var compercent = (completeNumber/numbers*100).toFixed(0);
//		if(status==1)
//		compercent = 0;

		var cl = '';
		if(status == 0 || status == 3 || status == -1) 
			cl += '<a id="cancel1_'+entrustId+'" href="javascript:cancle(\''+entrustId+'\' , '+types+',1);">'+vip.L("取消S")+'</a>';
//	    if(completeNumber>0)
//		    cl += ' <a href="javascript:details(\''+entrustId+'\',\''+classT+'\');">'+vip.L("查看")+'</a>';

	    if(cl=='')
	    	cl='—';
		if(status==0)
			status="-";
		else if(status==3)
			status="<i style='color:gray;'>"+vip.L("待成交")+"</i>";
		else if(status==2)
			status="<i style='color:orange;'>"+vip.L("已成交")+"</i>";
		
		else if(status==1)
			status="<i style='color:red;'>"+vip.L("已取消")+"</i>";
		else if(status==-1)
			status="<i style='color:orange;'>"+vip.L("计划中")+"</i>";

		var date = new Date(submitTime);
		var nowStr = date.format("MM-dd hh:mm:ss"); 
		
		var junjia='-';
		if(completeNumber>0)
			junjia=formatMoney((completeTotalMoney*exchangeBixNormal)/completeNumber);
	
		var comp='-';
		if(completeTotalMoney>0)
			comp=completeTotalMoney;
		var cla='';
		if(i%2!=0)
			cla=' class="double" ';
//		var row='<dd '+cla+'><span class="t1">'+nowStr+'</span><span class="t2 '+classT+'">'+nameType+'</span>'+
//		'<span class="t3">'+exchangeBiNote+''+unitPrice+'/'+junjia+'</span><span class="t4">'+numberBiNote+" "+numbers+'/'+completeNumber+'</span>'+
//		'<span class="t5">'+exchangeBiNote+formatMoney(numbers*unitPrice*exchangeBixNormal)+' / '+completeTotalMoney+'</span>'+
//		'<span class="t6">'+status+'</span><span class="t7">'+cl+'</span></dd>';
		var row= '<dd '+cla+'>'+
		'<span class="t1" title="'+date.format("yyyy-MM-dd hh:mm:ss")+'">'+nowStr+'</span>'+
		'<span class="t2 '+classT+'">'+nameType+'</span>'+
		'<span class="t3">'+unitPrice+'</span>'+
		'<span class="t4">'+numbers+'</span>'+
	
		'<span class="t6 " style="text-align:left;"><div class="schedule  '+classT+'" style="margin-left:10px;" title="完成度：'+compercent+'%"><i><u style="width:'+compercent+'%;float:left;"></u></i></div> '+completeNumber+'</span>'+
		'<span class="t5 ">'+junjia+'</span>'+
		'<span class="t7 ">'+completeTotalMoney+'</span>'+
		'<span class="t8">'+status+'</span>'+
		'<span class="t9">'+cl+'</span>'+
		'</dd>';
		res.push(row);
		
	}

		if(pageIndex>0){
			if(record.length<1){
				$("#listNormal").html('<div class="air-tips"><p>'+vip.L("暂时没有符合要求的记录")+'</p></div>');
				 $("#page_navA").html('');
			}
			else{
			   $("#listNormal").html(res.join(""));
			   var rtn=GetPaper(pageIndex,result[0].count, pageSize);
			   $("#page_navA").html("<div class=\"con\">"+rtn+"</div>");
		  }
		}
		else{
			
			if(record.length<1){
				$("#listFirest").html('<div class="air-tips"><p>'+vip.L("暂时没有符合要求的记录")+'</p></div>');
			}
			else{
				
		       $("#listFirest").html(res.join(""));
			}
		}
	 
	//if(pageIndex>0){//显示分页
	
	//}
}

function details(id,clas){
	var typeN = clas=="pi"? vip.L("买入"):vip.L("卖出");
	vip.list.funcName = "<b>"+numberBi+"委托"+typeN+"</b>成交记录详情";
	vip.list.look({url : entrustUrlBase+'Record/'+market+'-'+id,height:460,width:680,title:""});
}

function init(type , ajax){
	//initEntrusts(type , ajax);
	getRecordFirst();
	//dsc.user.ticker();
	getCurr();
}

var lastTimeRecord=0;
var entrust = null;


function cancle(ids , type, listType){
	vip.tips["取消"] = ["否","No"];
	Ask({
		Msg : "确定取消当前委托吗？",
		callback : "cle('" + ids + "' , "+type+", "+listType+")"
	});
}
function cle(id , type , listType){
	var url1 = entrustUrlBase+"Entrust/cancle-"+market+"-" + id;
	vip.ajax({url : url1, 
		div : "listFirestDiv", 
		suc : function(xml){
			 var code=$(xml).find("MainData").text();
			 var des=$(xml).find("Des").text();
			
			 if(code==200){
				 //先关闭点击按钮
				$("#cancel1_"+id).attr("href","javascript:void()").text("取消中");
				 reload=1;//紧急任务快速显示
			  Close();
			   $.jGrowl(des, { life: 8000,position:"bottom-right" });
			   getRecordFirst();
			 }
			 else {
				  Close();
				 $.jGrowl(des, { life: 10000,position:"bottom-right" });
			 }
		},
		err : function(xml){
			 
			Wrong($(xml).find("Des").text());
			
		}
	});
}


var lastTime=0;//last get data time
var buyOne=0;//买一
var sellOne=0;//卖一
/**
 * 显示的原始长度
 * @param result
 * @param length
 */
var isloadtransrecord=true;
function show(result,length){
	isRunning=false;
    	 var nowTime=result[0].lastTime;
    	 if(nowTime==lastTime) 
            return;   
    	 //当有交易的时候才进行一次用户数据记录获取
    	 if(isloadtransrecord){
    		 getRecordFirst();
    	 }
    	
    	 lastTime=nowTime;
		var currentPrice=result[0].currentPrice;
		//alert(currentPrice);
		
		var high=result[0].high;//最高价格
		var low=result[0].low;//最低价格
       
		var totalBtcToday=result[0].totalBtc ? result[0].totalBtc : 0;//今日的总btc
		
		var listUp=result[0].listUp;
		var sellArray = [];
		var tops100=0;
		if(listUp.length > length){
			listUp = listUp.slice(listUp.length - length , listUp.length);
		}
		
		for(var i=0;i<listUp.length;i++){
			var btc =listUp[i][1];
			if(btc>tops100)
				tops100=btc;
		}
		
		
		var listDown=result[0].listDown;
		var buyArray = [];
		var downS="";
		var downS100=0;
		
		if(listDown.length > length){
			listDown = listDown.slice(0 , length);
		}
		
		for(var i=0;i<listDown.length;i++){
			var btc =listDown[i][1];
			if(btc>downS100)
				downS100=btc;
		}
		
		
		
		 
		var listRight=result[0].transction;
		var rightS="";
		var transArray = [];
		var right100=0;
		
		if(listRight.length > length){
			listRight = listRight.slice(0 , length);
		}
		
		for(var i=0;i<listRight.length;i++){
			var btc =listRight[i][1];
			if(btc>right100)
				right100 = btc;
		}
		
		
		
		var per100=tops100>downS100?tops100:downS100;
		if(right100>per100)
			per100=right100;
		
		var iIndex = 1;
		//if(sellDao)
		//	iIndex=listUp.length;
		var dep = 0;
	
		for(var i=0;i<listUp.length;i++){
			var j=i;
			//简洁版
//			if(sellDao)
//				j=listUp.length-i-1;
			
				var price = listUp[j][0];
				var btc = listUp[j][1];
				if(i ==0)
					sellOne=price;
//				if(i ==(listUp.length-1)&&sellDao)
//					sellOne=price;
				var per = btc*100/per100;
				if(per < 1)per = 1;
				dep = accAdd(dep , btc);
				var nums = "";
				if(entrust != null){
					for(var h = 0; h<entrust.sell.length; h++){
						if(entrust.sell[h].price == price){
							nums = entrust.sell[h].btcs;
						}
					}
				}
				var cla='';
				if(iIndex%2==0)
					cla=' class="double" ';
				var note='<dd'+cla+' onclick="gearSelect(this,0)"><span class="t1">' +vip.L("卖") +iIndex+'</span><span class="t2">'+price+'</span><span class="t3">'+btc+'</span><span class="t4"><div style="width:'+per+'%;">'+nums+'</div></span>';
				if(!sellDao){
					note+='<span class="t5">'+dep+'</span></dd>';
				}else{
					note+='<span style="display:none;" class="t5">'+dep+'</span></dd>';
				}
					
				if(sellDao){
					sellArray.splice(0,0,note);
				}else{
					sellArray.push(note);
				}
//				if(sellDao)
//					iIndex--;
//				else 
				  iIndex++;				
		}
		var addNum = length - listUp.length;
		if(addNum > 0 && sellDao){
			for(var a=0;a<addNum;a++){
				sellArray.splice(0,0,'<dd onclick="gearSelect(this,0)"><span class="t1">' + vip.L("卖") +(listUp.length + a + 1)+'</span><span class="t2">-</span><span class="t3">-</span><span class="t4"><div style="width:0%;">-</div></span></dd>');
			}
		} else {
			for(var a=0;a<addNum;a++){
				sellArray.push('<dd onclick="gearSelect(this,0)"><span class="t1">' + vip.L("卖") +(listUp.length + a + 1)+'</span><span class="t2">-</span><span class="t3">-</span><span class="t4"><div style="width:0%;">-</div></span><span class="t5">-</span></dd>');
			}
			}
		$("#sellListIn").html(sellArray.join(""));
		//initGearSelect(0);
		
		iIndex = 1;
		dep = 0;
		for(var i=0;i<listDown.length;i++){
			var price = listDown[i][0];
			if(i==0)
				buyOne=price;
			var btc =listDown[i][1];
			var per=btc*100/per100;
			if(per < 1)per = 1;
			dep = accAdd(dep ,btc);
			
			var nums = "";
			if(entrust != null){
				for(var h = 0; h<entrust.buy.length; h++){
					if(entrust.buy[h].price == price){
						nums = entrust.buy[h].btcs;
					}
				}
			}
			var cla='';
			if(iIndex%2==0)
				cla=' class="double" ';
			var note='<dd'+cla+' onclick="gearSelect(this,1)"><span class="t1">' + vip.L("买") +iIndex+'</span><span class="t2">'+price+'</span><span class="t3">'+btc+'</span><span class="t4"><div style="width:'+per+'%;">'+nums+'</div></span>';
			if(!sellDao){
				note+='<span class="t5">'+dep+'</span></dd>';
			}else{
				note+='<span style="display:none;" class="t5">'+dep+'</span></dd>';
			}
			buyArray.push(note);
			iIndex++;
	   } 
		
	   var addNum = length - listDown.length;
	   if(addNum > 0 && sellDao){
			for(var a=0;a<addNum;a++){
				buyArray.push('<dd onclick="gearSelect(this,1)"><span class="t1">' + vip.L("买") +(listUp.length + a + 1)+'</span><span class="t2">-</span><span class="t3">-</span><span class="t4"><div style="width:0%;">-</div></span></dd>');
				}
	   } else {
			for(var a=0;a<addNum;a++){
				buyArray.push('<dd onclick="gearSelect(this,1)"><span class="t1">' + vip.L("买") +(listUp.length + a + 1)+'</span><span class="t2">-</span><span class="t3">-</span><span class="t4"><div style="width:0%;">-</div></span><span class="t5">-</span></dd>');
				}
		   }
	   $("#buyListIn").html(buyArray.join(""));
	   //initGearSelect(1);
		for(var i=0;i<listRight.length;i++){
			var price = listRight[i][0];
			var number = listRight[i][1];
			var isBuy =listRight[i][2];
			var t =listRight[i][3]; 
			var per=number*100/per100;
			if(per < 1)per = 1;
			var bid='outer';
			if(isBuy == 1)
				bid='in';
			
			var cla1='';
			if(i%2==0)
				cla1=' class="double" ';
			
			var cla='';
		
			if(isBuy==0) 
				cla=' class="down"';
		
			var date = new Date(t);
			var nowStr = date.format("hh:mm:ss"); 
			var noteT='<dd'+cla1+'><span class="t1">'+nowStr+'</span><span class="t2">'+price+'</span><span class="t3">'+number+'</span><span class="t4"><div '+cla+' style="width:'+per+'%;"></div></span></dd>';

			transArray.push(noteT);
	   } 
		
		  var addNum = length - listRight.length;
		   if(addNum > 0 ){
				for(var a=0;a<addNum;a++){
					transArray.push('<dd ><span class="t1">-</span><span class="t2">-</span><span class="t3">-</span><span class="t4"><div   style="width:0%;"></div></span></dd>');
					}
		   } 
		   
		    
	   $("#transList").html(transArray.join(""));
             $("#suggestPrice").text(sellOne);
             if(buyOne > 0){
            	 $("#suggestBuyPrice").text(buyOne);
                 marketInit= true;
             }
             
             //$("#buyUnitPrice").trigger("onblur");
             
             //priceBlur(1);
             
            
           //  $("#highone").text(high);
           //  $("#lowone").text(low);
           //  $("#currentOne").text(currentPrice);//这个字段可以找个地方写出来
           //  $("#totalBtcOne").text(totalBtcToday);
		     isRunning=false;
}

function accountInfoShow(rmb , btc , frmb , fbtc , buyBtc , sellRmb , total , buyone , sellone , btq , noBtc , nextBtc){
//	$("#userPayAccountInfo .t .name").text($.cookie("name"));
//	$("#userPayAccountInfo .b").text(total);
//	$("#userPayAccountInfo .useRmb").text(rmb);
//	$("#userPayAccountInfo .freezRmb").text(frmb);
//	$("#userPayAccountInfo .totalRmb").text(accAdd(frmb,rmb));
//	
//	$("#userPayAccountInfo .useBtc").text(btc);
//	$("#userPayAccountInfo .freezBtc").text(fbtc);
//	$("#userPayAccountInfo .totalBtc").text(accAdd(fbtc,btc));
//	
//	$("#buy .useRmb").text(rmb);
//	$("#buy .canBuy").text(buyBtc);
//	$("#buy .suggest").text(sellone);
//	
//	$("#sell .useBtc").text(btc);
//	$("#sell .suggest").text(buyone);
//	$("#sell .bigNum").text(btc);
//	$("#tipsTotalBtq").text(btq);
//	$("#tipsNoBtc").text(noBtc);
//	$("#tipsNextBtc").text(nextBtc);
//	$("#assets .assets span").text(total);
}


function changeSell(){
    var submenu = document.getElementById('buy');
    submenu.style.display = "none";
    var submenuSell = document.getElementById('sell');
    submenuSell.style.display = "block";
    var submenuMan = document.getElementById('manage');
    submenuMan.style.display = "none";
    $("#showList").show();
    $("#mylist").hide();
    getRecordFirst();
}

function changeBuy() {
    var submenu = document.getElementById('buy');
    submenu.style.display = "block";
    var submenuSell = document.getElementById('sell');
    submenuSell.style.display = "none";
    var submenuMan = document.getElementById('manage');
    submenuMan.style.display = "none";
    $("#showList").show();
    $("#mylist").hide(); 
    getRecordFirst();
}
function changeMan() {
	 getRecord(1,-1,0,0,0,0,0,0,0,3);
    var submenu = document.getElementById('buy');
    submenu.style.display = "none";
    var submenuSell = document.getElementById('sell');
    submenuSell.style.display = "none";
    var submenuMan = document.getElementById('manage');
    submenuMan.style.display = "block";
    $("#showList").hide();
    $("#mylist").show();
   
//    initEntrusts(2 , true);
}

function accSub(arg1,arg2){
       var r1,r2,m,n;
       try{r1=arg1.toString().split(".")[1].length}catch(e){r1=0}
       try{r2=arg2.toString().split(".")[1].length}catch(e){r2=0}
       m=Math.pow(10,Math.max(r1,r2));
       //last modify by deeka
       //动态控制精度长度
       n=(r1>=r2)?r1:r2;
       return ((arg1*m-arg2*m)/m).toFixed(n);
}

function accAdd(arg1,arg2){
    var r1,r2,m;
    try{r1=arg1.toString().split(".")[1].length}catch(e){r1=0}
    try{r2=arg2.toString().split(".")[1].length}catch(e){r2=0}
    m=Math.pow(10,Math.max(r1,r2))
    return ((arg1*m+arg2*m)/m).toFixed(3)
}

//小数位验证
function xswCheck(obj , max , type){
	var m = max + 1;

	if(obj.value == ""){
		$("input[name=btcNumber]").each(function(){
			$(this).val("");
		});
		$("input[name=realAccount]").each(function(){
			$(this).val("");
		});
		return;
	}
		
	var iv = $(obj).val();
	if(iv.indexOf(".") > 0 && iv.substring(iv.indexOf(".")).length > m){
			$(obj).val(iv.substring(0 , iv.indexOf(".") + m));
	}
	if($(obj).attr("name") != "realAccount"){
		initTotal(type);//计算
	}else{//计算数量
		var formId = getFormIdByType(type);
		var btcNumber = $("#"+formId + " .btcNumInput");
		var total = $(obj).val();
		
		var price = $("#"+formId + " .unitPrice").val();
		if(vip.tool.isFloat(total) && vip.tool.isFloat(price)){
			btcNumber.val(formatNumber(formatNumberUse(accDiv(total , price))));
		}
	}
}

function numCheck(type){
	var formId = getFormIdByType(type); 
	var res = true;
	var btcNumber = $("#"+formId + " .btcNumInput").val();
	
	if(vip.tool.isFloat(btcNumber) && btcNumber > 0){
		if(parseFloat(btcNumber) < 0.001){
			$("#"+formId + " .btcNumInput").val(0.001);
		}
		if(parseFloat(btcNumber) > 10000){//
			$("#"+formId + " .btcNumInput").val(10000);
		}
		if(btcNumber.indexOf(".") > 0 && btcNumber.substring(btcNumber.indexOf(".")).length >(numberBixDian+1)){
			$("#"+formId + " .btcNumInput").val(btcNumber.substring(0 , btcNumber.indexOf(".") + (numberBixDian+1)));
		}
		reachUnitPrice($("#"+formId + " .btcNumInput").val(), type);
	}
}

function fNumber(num){
	num = num + "";
	if(num.indexOf(".") > 0){
		if(num.length - num.indexOf(".") > 3){
			return num = num.substring(0,num.indexOf(".") + 4);
		}
	}
	return Number(num).toFixed(2);
}

 function initVal(type){
	 var formId = getFormIdByType(type);
	 var unitPrice = $("#"+formId + " .unitPrice");
	 if(unitPrice.val() == ""){
		 unitPrice.val($("#"+formId+" .suggest").text());
	 }
 }
 
 function getFormIdByType(type){
	 var formId = "buy";
	 if(type != 1){
		 formId = "sell";
	 }
	 return formId;
 }
 
 function reachUnitPrice(number, type){
	 var formId = getFormIdByType(type);
	 
	 var price = $("#"+formId + " .unitPrice").val();
	 if(price == null || !vip.tool.isFloat(price)){
		 //type==1  计算委托卖出的单价     type==0计算委托买入的单价
		 var entrustList = type==1?"sellListIn":"buyListIn";
		 var unitPrice = 0;
		 var dep = 0;//累计深度
		 var _this = "";
		 if(type == 1 && sellDao){
			 for(var i = $("#"+entrustList + " dd").length-1; i >= 0; i --){
				 _this = $("#"+entrustList + " dd").eq(i);
				 dep += parseFloat(_this.children("span.t3").text());
				 if(parseFloat(dep) >= parseFloat(number)){
					 unitPrice = _this.children("span.t2").text();
					 break;
				 }
			 }
		 }else{
			 for(var i = 0; i < $("#"+entrustList + " dd").length; i ++){
				 _this = $("#"+entrustList + " dd").eq(i);
				 dep += parseFloat(_this.children("span.t3").text());
				 if(parseFloat(dep) >= parseFloat(number)){
					 unitPrice = _this.children("span.t2").text();
					 break;
				 }
			 }
		 }
		 if(unitPrice > 0){
			 $("#"+formId + " .unitPrice").val(unitPrice);
		 }
	 }
	 initTotal(type);
 }
 
 function initTotal(type){
	 var formId = getFormIdByType(type);
	 
	 var btcNumber = $("#"+formId + " .btcNumInput").val();
	 var price = $("#"+formId + " .unitPrice").val();
	 if(price != null && vip.tool.isFloat(price)){
		 if(type == 1){
			 var useRmb = $("#buy .useRmb").text().replace(exchangeBiNote , "");
			 var maxNum = formatNumber(formatNumberUse(accDiv(useRmb , price)));
			 $("#"+formId + " .bigNum").text(maxNum);
			 $( "#buySlider" ).slider( "option", "max", maxNum );
		 }
	 } 
	 if(price != null && vip.tool.isFloat(price)){
//		 if(parseFloat(btcNumber) > parseFloat($("#"+formId + " .bigNum").text())){
//			 $("#"+formId + " .btcNumInput").val($("#"+formId + " .bigNum").text());
//			 btcNumber = $("#"+formId + " .btcNumInput").val();
//		 }
		 if(btcNumber != null && vip.tool.isFloat(btcNumber)){
			 $("#"+formId + " .realAccount").val(accMul(price , btcNumber));
		 }else{
			 var realAccount = $("#"+formId + " .realAccount").val();
			 if(realAccount != null && vip.tool.isFloat(realAccount)){
				 var maxNum = accDiv(realAccount, price);
				 $("#"+formId + " .btcNumInput").val(maxNum);
			 }
		 }
	 }
 }
 
 function accMul(arg1,arg2){ 
    var m=0,s1=arg1.toString(),s2=arg2.toString();
    try{m+=s1.split(".")[1].length}catch(e){}
    try{m+=s2.split(".")[1].length}catch(e){}
    
    var tradeAmount =Number(s1.replace(".",""))*Number(s2.replace(".",""))/Math.pow(10,m);
    if(tradeAmount!= null && tradeAmount.toString().split(".")!=null &&tradeAmount.toString().split(".")[1] != null && tradeAmount.toString().split(".")[1].length>4){
		tradeAmount = tradeAmount.toFixed(4);		
	}
    return tradeAmount;
 }
 //舍去指定小数位   最多保留sqWs个小数位
 function sqxsw(amount , sqWs){
	 var str = amount.toString();
	 if(vip.tool.isFloat(amount)){
		 var ss = str.split(".");
		 if(ss.length == 2){
			 if(ss[1].length > sqWs){
				 var dianHou = str.indexOf(".") + sqWs + 1;
				 str = str.substring(0 ,dianHou)
			 }
		 }
	 }
	 return str;
 }
 
 function accDiv(arg1,arg2){
    var t1=0,t2=0,r1,r2;
    try{t1=arg1.toString().split(".")[1].length}catch(e){}
    try{t2=arg2.toString().split(".")[1].length}catch(e){}
    with(Math){
        r1=Number(arg1.toString().replace(".",""))
        r2=Number(arg2.toString().replace(".",""))
        
        var tradeAmount = (r1/r2)*pow(10,t2-t1);
        if(tradeAmount!= null && tradeAmount.toString().split(".")!=null &&tradeAmount.toString().split(".")[1] != null && tradeAmount.toString().split(".")[1].length>3){
        	tradeAmount = sqxsw(tradeAmount , 3);
			//tradeAmount = tradeAmount.toFixed(3);
		}
        return tradeAmount;
    }
}
 
 function numBlur(type){
	 numCheck(type);
 }
 
 function priceBlur(type){
	 moneyCheck(type);
	 //addVal(type);
	 initTotal(type);
 }
 
function moneyCheck(type){
	var formId = getFormIdByType(type);
	var unitPrice = $("#"+formId + " .unitPrice");
	//非数字  且 ...
	checkPrice(unitPrice , $("#"+formId+" .suggest").text());
}

function checkPrice(obj ,def){
	var unitPrice = obj.val();
	if(vip.tool.isFloat(unitPrice) && unitPrice > 0){
		if(parseFloat(unitPrice) < 0){
			obj.val(def);
		}
		if(parseFloat(unitPrice) > 100000){//
			obj.val(def); 
		}
	}
	if(unitPrice.indexOf(".") > 0 && unitPrice.substring(unitPrice.indexOf(".")).length > (exchangeBixDian+1)){
		obj.val(unitPrice.substring(0 , unitPrice.indexOf(".") + exchangeBixDian+1));
	}
}

var twoTimeout;
var threeTimeout;
var startTimer=false;
var submitfirst=false;
//isBuy 是否是买  isReal 是否是计划委托
function trans(isBuy,isReal){
	if(!vip.user.checkLogin()){
		return;
	}
	
	var formId = getFormIdByType(isBuy);
	var btcNumber = $("#"+formId + " .btcNumInput").val();
	var unitPrice = $("#"+formId + " .unitPrice").val();
	var safePwd = $("#"+formId + " .safePassword").val();
	var type = isBuy == 1 ? vip.L("买入") : vip.L("卖出");
	
	var buttonId = isBuy == 1 ? "buyButton" : "sellButton";
	var buttonText = isBuy == 1 ? vip.L("正在买入") : vip.L("正在卖出");
	
	if(unitPrice.length==0 || !vip.tool.isFloat(unitPrice) || parseFloat(unitPrice) <= 0){
		Wrong(vip.L("请输入X1 X2的单价" , [{"k" : "X1" , "v" : type} , {k : "X2" , v : numberBi}]),{CloseTime:1});
		  //$.jGrowl("请输入"+type+''+numberBi+"的单价!", { life: 5000,position:"bottom-right" });
		return;
	}
	
	if(btcNumber.length==0 || !vip.tool.isFloat(btcNumber) || btcNumber < 0.0001 || btcNumber > 10000){
		Wrong(vip.L("请输入X1 X2的数量" , [{"k" : "X1" , "v" : type} , {k : "X2" , v : numberBi}]),{CloseTime:1});
		 //$.jGrowl("请输入"+type+''+numberBi+"的数量!", { life: 5000,position:"bottom-right" });
		return; 
	}
	var useSafe = $("#useSafe").val();
	if(useSafe==1 && (safePwd.length < 6 || safePwd.length > 16)){
		 //$.jGrowl("请正确输入您的安全密码,6-16位。", { life: 5000,position:"bottom-right" });
		Wrong("请正确输入您的安全密码,6-16位。",{CloseTime:1});
		return;
	}
	var tx="";
	if(!isReal)
		tx="计划";
	
	
	//获取最新价格===============================================================
	var last = 0;
	if(market == "btcdefault")
		last = $("#statisticsDiv .btc em").eq(0).text();
	else if(market == "ltcdefault")
		last = $("#statisticsDiv .ltc em").eq(0).text();	
	else if(market == "ethdefault")
		last = $("#statisticsDiv .eth em").eq(0).text();	
	if(isNaN(last))
		last = 0;
	else
		last = Number(last);
	//如果输入价格偏离最新成交价格1%时，做二次提醒
	var overplus = last / 100;
	
	//=========================================================================
	var title = "";
	if(isBuy==0){
		
		var p=parseFloat($("#sellUnitPrice").val()); 
		
		//如果卖出价格低于最新成交价格1%时，做二次提醒
		if(overplus >0 && (p < last) && ((last - p) >= overplus)){
			title = "您输入的卖出价格<b style='color:red'>["+p+"]</b>已低于最新成交价格<b style='color:#4775A9'>["+last+"]</b>的1%，是否继续委托卖出?";
		}
		
		if(isReal){
		if(buyOne>0&&((p/buyOne)<0.6)){
			// $.jGrowl("您的"+tx+"卖出单价过低！请检查是否错误输入"+tx+"卖出单价！", { life: 5000,position:"bottom-right" });
			 Wrong("您的"+tx+"卖出单价过低！请检查是否错误输入"+tx+"卖出单价！",{CloseTime:2});
			  return;
		}
		}
		else{
			
			if(buyOne>0&&((p/buyOne)<0.1)){
				// $.jGrowl("您的"+tx+"卖出单价过低！请检查是否错误输入"+tx+"卖出单价！", { life: 5000,position:"bottom-right" });
				 Wrong("您的"+tx+"卖出单价过低！请检查是否错误输入"+tx+"卖出单价！",{CloseTime:2});
				  return;
			}else if(p>=buyOne){//如果不低于于买一价，计划就是没有意义的
				// $.jGrowl("止损计划卖出的单价应该低于买一价，否则请直接使用立即卖出！", { life: 5000,position:"bottom-right" });
				 Wrong("止损计划卖出的单价应该低于买一价，否则请直接使用立即卖出！",{CloseTime:2});
				  return;
			}
		}
		if(btcNumber>currentUserNumber){
			// $.jGrowl("您当前可出售的最多数量为:"+currentUserNumber+"个！", { life: 5000,position:"bottom-right" });
		Wrong("您当前可出售的最多数量为:"+currentUserNumber+"个！",{CloseTime:2});
		return;
		}
		
		
	}else if(isBuy==1){
		var n=parseFloat($("#suggestNumber").text()); 
		var num=parseFloat($("#buyNumber").val()); 
		var p=parseFloat($("#buyUnitPrice").val()); 
		
		//如果卖出价格低于最新成交价格1%时，做二次提醒
		if(overplus >0 && p >= (overplus + last)){
			title = "您输入的买入价格<b style='color:red'>["+p+"]</b>已高于最新成交价格<b style='color:#4775A9'>["+last+"]</b>的1%，是否继续委托买入?";
			//title = "您输入的买出价格<font color='#4775A9'><b>["+p+"]</b></font>已高于最新成交价格<font color='red'><b>["+last+"]</b></font>的1%，是否继续委托买入?";
		}
		
		if(n<num){
			 //$.jGrowl("按照您的报价，您当前最多可"+tx+"买入的数量为:"+n+"个！", { life: 5000,position:"bottom-right" });
		  Wrong("按照您的报价，您当前最多可买入的数量为:"+n+"个！",{CloseTime:2});
		  return;
		}
		if(isReal){
		  if(sellOne>0&&((p/sellOne)>1.4)){
			// $.jGrowl(tx+"购买单价过高，请检查是否正确输入了"+tx+"购买单价！", { life: 5000,position:"bottom-right" });
			 Wrong("购买单价过高，请检查是否正确输入了购买单价！",{CloseTime:2});
			  return;
		   }
		}else{
			 if(sellOne>0&&((p/sellOne)>8)){
				// $.jGrowl(tx+"购买单价过高，请检查是否正确输入了"+tx+"购买单价！", { life: 5000,position:"bottom-right" });
				 Wrong("购买单价过高，请检查是否正确输入了购买单价！",{CloseTime:2});
				  return;
			   }
			 else if(sellOne>=p){
					// $.jGrowl(tx+"购买单价过高，请检查是否正确输入了"+tx+"购买单价！", { life: 5000,position:"bottom-right" });
					 Wrong("追高计划买入的单价应该高于卖一价，如果想以此价格委托请直接使用立即买入！",{CloseTime:2});
					  return;
				   }
			
		}
	}
	submitfirst = true;
	if(title == ""){
		sucFunc();
	}else{
		Ask2({Title:vip.L(title),call:function(){
			sucFunc();
			Close();
		}});
	}
	function sucFunc(){
		//p1 使用lodding
		$("#b_tradtab_pro").Loadding({IsShow:true,OffsetYGIF:40,Str:"委托中"});
		//初次验证通过，锁住交易按钮，避免用户由于请求延迟而多次点击按钮从而请求多次。
		//$("#" + buttonId).attr("href", "javascript:Wrong('连续点击',{CloseTime:1})");
		//更改显示文字
		//$("#" + buttonId).text(buttonText);
		//更改样式
		//$("#" + buttonId).addClass("ing");
		$.post(entrustUrlBase+"entrust/doEntrust-"+market+"-"+isReal, { Action: "post", safePassword:safePwd, unitPrice:unitPrice,number:btcNumber,isBuy:isBuy },
		    function (json, textStatus){
			 $("#b_tradtab_pro").Loadding({IsShow:false});
				 $("#"+formId + " .safePassword").val("");
				 var code = json.datas.code;//$(xml).find("MainData").text();
				 var des= json.des;//$(xml).find("Des").text();
				 if(code==100){
					 reload=1;//紧急任务快速显示
					 reloadDue=1;
					// $("#buyNumber").val("");
					 $(".btcNumInput").val("");
					 $(".realAccount").val("");
					// Right(des,{CloseTime:1});
					  $.jGrowl(des, { life: 8000,position:"bottom-right" });
				
				   getRecordFirst();
				 } 
				 else{
					 Wrong(des,{CloseTime:1});
					 useSafePwd();
					 $.jGrowl(des, { life: 8000,position:"bottom-right" });
				 }
				
				// buttonText = isBuy == 1 ? "立即买入" : "立即卖出";
				// $("#" + buttonId).attr("href", "javascript:trans("+isBuy+")");
				//更改显示文字
				//$("#" + buttonId).text(buttonText);
				//更改样式
				//$("#" + buttonId).removeClass("ing");	
				 
			}, "json"); 
	}
}



function copyVal(obj , clsXz){
	$(clsXz).val($(obj).text());
}

function useSafePwd(){
	var url = vip.transDomain +"/isSafe"
	vip.ajax({
		url : url, 
		dataType : "json", 
		suc:function(json){
			if(json.des=="false"){
				$(".safePassword").hide();
				$("#closeSafe1").text(vip.L("开启")).attr("href","javascript:startSafePwd()");
				$("#closeSafe2").text(vip.L("开启")).attr("href","javascript:startSafePwd()");
				$("#useSafe").val(0);
			}else{
				$("#useSafe").val(1);
				$(".safePassword").show();
				$("#closeSafe1").text(vip.L("关闭")).attr("href","javascript:closeSafePwd()");
				$("#closeSafe2").text(vip.L("关闭")).attr("href","javascript:closeSafePwd()");
			}
		}
	});

}
 
function cancleMore(){
	if(!vip.user.checkLogin()){
		return;
	}
	Iframe({
	    Url: vip.transDomain+"/cancelMore",
        zoomSpeedIn		: 200,
        zoomSpeedOut	: 200,
        Width:648,
        Height:370,
        scrolling:"no",
        isIframeAutoHeight:false,
        Title:vip.L("批量取消")
	});
}

function entrustMore(isBuy){
	if(!vip.user.checkLogin()){
		return;
	}
	Iframe({
	    Url: vip.transDomain+"/entrustMore?isbuy="+isBuy,
        zoomSpeedIn		: 200,
        zoomSpeedOut	: 200,
        Width:648,
        Height:460,
        scrolling:"no",
        isIframeAutoHeight:false,
        Title:vip.L("批量委托")
	});
}

function closeSafePwd(){
	if(!vip.user.checkLogin()){
		return;
	}
	Iframe({
	    Url:vip.vipDomain+"/u/safe/closeSafePwd",
        zoomSpeedIn		: 200,
        zoomSpeedOut	: 200,
        Width:500,
        Height:260,
        scrolling:"no",
        isIframeAutoHeight:false,
        Title:vip.L("安全验证")
	});
}

function startSafePwd(){ 
	if(!vip.user.checkLogin()){
		return;
	}
	Ask2({Msg:vip.L("您确定要开启安全密码吗？"), call:function(){
		$.getJSON(vip.vipDomain + "/u/safe/useOrCloseSafePwd?callback=?", function(json) {
			//Right(json.des, {callback:"window.location.reload()"});
			Close();
			$(".safePassword").show();
			$("#closeSafe1").text(vip.L("关闭")).attr("href","javascript:closeSafePwd()");
			$("#closeSafe2").text(vip.L("关闭")).attr("href","javascript:closeSafePwd()");
			$("#useSafe").val("0"); 
			  $.jGrowl(vip.L("开启密码成功！"), { life: 8000,position:"bottom-right" });
		});
//		vip.ajax({url : vip.vipDomain+"/u/safe/useOrCloseSafePwd", dataType : "json", suc:function(json){
//			Right(json.des, {callback:"window.location.reload()"})
//		}});
	}});
}


//格式化商品,用于显示,将基础整数位的商品格式化成需要的显示
function formatNumber(num){
    num=parseFloat(num)/numberBixNormal;
	if(numberBixNormal!=numberBixShow)//不等于就说明取整数的位数
	      return Math.floor(Math.pow(10,numberBixDian)*parseFloat(num));
	else
		 return parseFloat(num.toFixed(numberBixDian));
}
//和上面过程反向,统一格式化到最小单位
function formatNumberUse(num){
	num=parseFloat(num);
	if(numberBixNormal!=numberBixShow)//不等于就说明取整数的位数
		{
		num=Math.floor(num);
	      return Math.floor(num*Math.pow(10,numberBixDian));
		}
	else
		{
		
		 return Math.floor(accMul(num,numberBixNormal));
		}
}
//精确的乘法
function accMul(arg1,arg2)   
{ 
    var m=0,s1=arg1.toString(),s2=arg2.toString();  
    try{m+=s1.split(".")[1].length}catch(e){}  
    try{m+=s2.split(".")[1].length}catch(e){}  
    return Number(s1.replace(".",""))*Number(s2.replace(".",""))/Math.pow(10,m);  
}  
//精确的除法
function accDiv_old(arg1,arg2){  
    var t1=0,t2=0,r1,r2;  
    try{t1=arg1.toString().split(".")[1].length}catch(e){}  
    try{t2=arg2.toString().split(".")[1].length}catch(e){}  
    with(Math){  
        r1=Number(arg1.toString().replace(".",""));  
        r2=Number(arg2.toString().replace(".",""));  
        return (r1/r2)*pow(10,t2-t1);  
    }  
} 
//格式化金钱
function formatMoney(num){
	 num=parseFloat(num)/exchangeBixNormal;
		if(exchangeBixNormal!=exchangeBixShow)//不等于就说明取整数的位数
		      return Math.floor(Math.pow(10,exchangeBixDian)*parseFloat(num));
		else
			 return parseFloat(num.toFixed(exchangeBixDian));
}
//和上面过程反向，统一格式化到最小单位
function formatMoneyUse(num){
	num=parseFloat(num);
		if(exchangeBixNormal!=exchangeBixShow)//不等于就说明取整数的位数
			{
			num=Math.floor(num);
		      return Math.floor(num*Math.pow(10,exchangeBixDian));
			}
		else
			 return Math.floor(accMul(num,exchangeBixNormal)); 
}


//扩展Date的format方法
Date.prototype.format = function (format) {
    var o = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "h+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "q+": Math.floor((this.getMonth() + 3) / 3),
        "S": this.getMilliseconds()
    }
    if (/(y+)/.test(format)) {
        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
        }
    }
    return format;
}


/**
 * 跳转加载新页面
 */
function ToPage(page){
	getList(page);
	 //getRecord(page,-1,0,0,0,0,0,0);
}
/**
 * 跳转加载新页面
 */
function ToPage2(){
	
	var now=parseInt($("#PagerInput").val());
	var max=$("#PagerInput").attr("maxSize");
	if(parseInt(max)<now){
		Alert("输入页码过大");
		  $("#PagerInput").val(num);
	}else
	  ToPage(now);
}
function GetPaper(curpage,numbers){
	return GetPaper(curpage,numbers,10);
}

/**
 * 当前页面，最大页面
 */
function  GetPaper(curpage,numbers,pageSize)
{
	if(!pageSize)
		pageSize = 10;
		
	var showItems=3;
	var getPages=numbers/pageSize;
	if(numbers%pageSize==0){
		getPages=parseInt(numbers/pageSize);
	}else{
		getPages=parseInt(numbers/pageSize)+1;
	}

	//初始化一个字符串缓冲区
    var rtn=[];
	/**
	 * 按钮
	 */
	//如果是第一页,定制按钮
	if(curpage == 1){
		rtn.push("<span class=\"Pbtn first\">第一页</span><span class=\"Pbtn pre\"><i>&lt;</i> 上一页</span>");
	}else{
		rtn.push("<a class=\"Pbtn first\"   href=\"javascript:ToPage(1)\">第一页</a>");
		rtn.push("<a class=\"Pbtn pre\"  href=\"javascript:ToPage("+(curpage-1)+")\"><i>&lt;</i> 上一页</a>");
	}
	/*
	 * 前面部分
	 */ 
	if(curpage>(showItems+1))
	{//如果前面是满的
		
		var start=curpage-showItems;
		if((curpage+showItems)>getPages)
		{
			start=getPages-2*showItems;
			if(start<=0)
				start=1;
		}
		else
			start=curpage-showItems;
		if(start>1)
			rtn.push("<span class=\"ellipsis\">...</span>");
		
	    for(var i=start;i<curpage;i++){
	    	rtn.push("<a href=\"javascript:ToPage("+i+")\"   class=\"num\" >"+i+"</a>");
	    }
	}
	else
	{
		 for(var i=1;i<curpage;i++){
			 rtn.push("<a href=\"javascript:ToPage("+i+")\"   class=\"num\"  >"+i+"</a>");
		  }
	}
	//中间部分
	rtn.push("<a class='num current'   >"+curpage+"</a>");
	//后面部分
	if(getPages>(curpage+showItems))
	{
		var end=showItems;
		if(curpage<=showItems)//刚开始，左边本身没有显示完全
		{
			if(getPages>(2*showItems+1))
				end=2*showItems+2;
			else
				end=getPages+1;
		}
		else
			end=curpage+showItems+1;
		for(var i=(curpage+1);i<end;i++){
			rtn.push("<a href=\"javascript:ToPage("+i+")\"    class=\"num\" >"+i+"</a>");
	       }
		if(getPages>(2*showItems+1))
			rtn.push("<span class=\"ellipsis\">...</span>");
	}
	else
	{
		for(var i=(curpage+1);i<(getPages+1);i++){
			rtn.push("<a  href=\"javascript:ToPage("+i+")\"    class=\"num\" >"+i+"</a>");
		  }
	}
	/**
	 * 最后一页
	 */ 
	//如果是最后一页
	if(curpage == getPages){
		rtn.push("<span class=\"Pbtn next\">下一页<i>&gt;</i></span>");
	}else{
		rtn.push("<a class=\"Pbtn next\"    href=\"javascript:ToPage("+(curpage+1)+")\">下一页 <i>&gt;</i></a>");
	}
	//bar.append("</div>");
	//如果现实搜索 
	 
		rtn.push("<div class=\"go_page\"><input type=\"text\" position=\"s\"　 id=\"PagerInput\" size=2 maxSize=\""+getPages+"\" mytitle=\"最多"+getPages+"页\" TitlePosition=\"Left\"  pattern=\"num()\" errmsg=\"最多"+getPages+"页\"  value=\""+curpage+"\" /><a href=\"javascript:ToPage2()\" id=\"JumpButton\" class=\"Pbtn jump\">跳转</a></div>");
	
	return rtn.join(""); 
}

/**
 * 跳转加载新页面
 */
function ToPage3(page){	
	var dateTo = $("#dateTo").val();
	if(!dateTo){
		dateTo = 0;
	}
	getTradeRecordFirst(1, page, dateTo);
}
/**
 * 跳转加载新页面
 */
function ToPage4(){
	
	var now=parseInt($("#PagerInput").val());
	var max=$("#PagerInput").attr("maxSize");
	if(parseInt(max)<now){
		Alert("输入页码过大");
		  $("#PagerInput").val(num);
	}else
	  ToPage3(now);
}

/**
 * 当前页面，最大页面
 */
function  GetPaper2(curpage,numbers,dateTo)
{
	var pageSize = 10;
	if(dateTo && dateTo == 5){
		pageSize = 20;
	}else{
		dateTo = 0;
	}

	var showItems=3;
	var getPages=numbers/pageSize;
	if(numbers%pageSize==0){
		getPages=parseInt(numbers/pageSize);
	}else{
		getPages=parseInt(numbers/pageSize)+1;
	}

	//初始化一个字符串缓冲区
    var rtn=[];
	/**
	 * 按钮
	 */
	//如果是第一页,定制按钮
	if(curpage == 1){
		rtn.push("<span class=\"Pbtn first\">第一页</span><span class=\"Pbtn pre\"><i>&lt;</i> 上一页</span>");
	}else{
		rtn.push("<a class=\"Pbtn first\"   href=\"javascript:ToPage3(1)\">第一页</a>");
		rtn.push("<a class=\"Pbtn pre\"  href=\"javascript:ToPage3("+(curpage-1)+")\"><i>&lt;</i> 上一页</a>");
	}
	/*
	 * 前面部分
	 */ 
	if(curpage>(showItems+1))
	{//如果前面是满的
		
		var start=curpage-showItems;
		if((curpage+showItems)>getPages)
		{
			start=getPages-2*showItems;
			if(start<=0)
				start=1;
		}
		else
			start=curpage-showItems;
		if(start>1)
			rtn.push("<span class=\"ellipsis\">...</span>");
		
	    for(var i=start;i<curpage;i++){
	    	rtn.push("<a href=\"javascript:ToPage3("+i+")\"   class=\"num\" >"+i+"</a>");
	    }
	}
	else
	{
		 for(var i=1;i<curpage;i++){
			 rtn.push("<a href=\"javascript:ToPage3("+i+")\"   class=\"num\"  >"+i+"</a>");
		  }
	}
	//中间部分
	rtn.push("<a class='num current'   >"+curpage+"</a>");
	//后面部分
	if(getPages>(curpage+showItems))
	{
		var end=showItems;
		if(curpage<=showItems)//刚开始，左边本身没有显示完全
		{
			if(getPages>(2*showItems+1))
				end=2*showItems+2;
			else
				end=getPages+1;
		}
		else
			end=curpage+showItems+1;
		for(var i=(curpage+1);i<end;i++){
			rtn.push("<a href=\"javascript:ToPage3("+i+")\"    class=\"num\" >"+i+"</a>");
	       }
		if(getPages>(2*showItems+1))
			rtn.push("<span class=\"ellipsis\">...</span>");
	}
	else
	{
		for(var i=(curpage+1);i<(getPages+1);i++){
			rtn.push("<a  href=\"javascript:ToPage3("+i+")\"    class=\"num\" >"+i+"</a>");
		  }
	}
	/**
	 * 最后一页
	 */ 
	//如果是最后一页
	if(curpage == getPages){
		rtn.push("<span class=\"Pbtn next\">下一页<i>&gt;</i></span>");
	}else{
		rtn.push("<a class=\"Pbtn next\"    href=\"javascript:ToPage3("+(curpage+1)+")\">下一页 <i>&gt;</i></a>");
	}
	//bar.append("</div>");
	//如果现实搜索 
	 
		rtn.push("<div class=\"go_page\"><input type=\"text\" position=\"s\"　 id=\"PagerInput\" size=2 maxSize=\""+getPages+"\" mytitle=\"最多"+getPages+"页\" TitlePosition=\"Left\"  pattern=\"num()\" errmsg=\"最多"+getPages+"页\"  value=\""+curpage+"\" /><a href=\"javascript:ToPage4()\" id=\"JumpButton\" class=\"Pbtn jump\">跳转</a></div>");
	
	return rtn.join(""); 
}



var isRunning = false;
var isStatis = true;
function getList(pageNo, statis){
	var page = 1;
	if(pageNo){
		page = pageNo;
	}

	if(statis) isStatis = statis;
	
	var status = $("#estatus").val();
	var timeFrom = 0;
	var timeTo = 0;
	var numberFrom = 0;
	var numberTo = 0;
	var priceFrom = 0;
	var priceTo = 0;
	var pageSize = 0;
	var startDate = $("#startDate").val();
	if(startDate.length > 0){
		timeFrom = new Date(Date.parse(startDate.replace(/-/g,   "/"))).getTime();
	}
	var endDate = $("#endDate").val();
	if(endDate.length > 0){
		timeTo = new Date(Date.parse(endDate.replace(/-/g,   "/"))).getTime();
	}
	
	var dateTo = $("input[name='dateTo']").val();
	var startNumber = $("#startNumber").val();
	if(startNumber.length > 0){
		numberFrom = formatNumberUse(startNumber);
	}
	
	var endNumber = $("#endNumber").val();
	if(endNumber.length > 0){
		numberTo = formatNumberUse(endNumber);
	}
	
	var startPrice = $("#startPrice").val();
	if(startPrice.length > 0){
		priceFrom = formatMoneyUse(startPrice);
	}
	
	var endPrice = $("#endPrice").val();
	if(endPrice.length > 0){
		priceTo = formatMoneyUse(endPrice);
	}
	var ps = $("#pageSize").val();
	if(ps && ps.length > 0){
		pageSize = ps;
	}
	getRecord(page,-1,timeFrom,timeTo,numberFrom,numberTo,priceFrom,priceTo , status,dateTo, pageSize);
	
}

function close_1(){
	isStatis = false;
	$("#btips").hide();
}

function resetSearch(){
	getRecord(1,-1,0,0,0,0,0,0,0,3);
	
	vip.list.resetForm();
	$('#estatus option:first').attr('selected','selected'); 
	$("#estatus").UiSelect();
}
var initTradeIsFinish = false;
function initTrade(){
	if(marketInit && userInit){
		 initVal(1);
		 priceBlur(1);
		 $("#buyNumber").val(0);
		 
		 initVal(0);
		 initTradeIsFinish = true;
	}
}
setInterval(function(){
	if(!initTradeIsFinish){
		initTrade();
	}
}, 100);

//档位选择
function gearSelect(obj,type){
	var dd = $(obj);
	if(type == 1){
		$("#SellTab").trigger("click");
	}else{
		$("#BuyTab").trigger("click");
	}
	var price = dd.find(".t2").text();
	//var num = dd.find(".t3").text();
	var depth = parseFloat(dd.find(".t5").text());
	if(price != null && vip.tool.isFloat(price)){
		 if(type == 1){
			 var maxNum = parseFloat($("#suggestSellNumber").text());
			 if(maxNum < depth){
				 depth = maxNum;
			 }
			 $("#buy .bigNum").text(maxNum);
			 $( "#sellSlider").slider( "option", "max", maxNum);
			 $( "#sellSlider").slider("value", depth);
		 }else{
			 var useRmb = $("#buy .useRmb").text().replace(exchangeBiNote , "");
			 var maxNum = formatNumber(formatNumberUse(accDiv(useRmb , price)));
			 if(maxNum < depth){
				 depth = maxNum;
			 }
			 $( "#buySlider").slider( "option", "max", maxNum);
			 $( "#buySlider").slider("value", depth);
		 }
	}
	if(type == 1){
		var sellNumber = $("#sellNumber").length > 0 ? $("#sellNumber") : $("#btcNumInput");
		$("#SellTab").trigger("click");
		$("#sellUnitPrice").val(price);
		sellNumber.val(depth);
		sellNumber.blur();
	}else{
		$("#BuyTab").trigger("click");
		$("#buyUnitPrice").val(price);
		$("#buyNumber").val(depth);
		$("#buyNumber").blur();
	}
}
function initGearSelect(type){
	var container = type ==1 ? $("#buyListIn dd") : $("#sellListIn dd");
	container.each(function(){
		var dd = $(this);
		dd.click(function(){
			if(type == 1){
				$("#SellTab").trigger("click");
			}else{
				$("#BuyTab").trigger("click");
			}
			var price = dd.find(".t2").text();
			//var num = dd.find(".t3").text();
			var depth = parseFloat(dd.find(".t5").text());
			if(price != null && vip.tool.isFloat(price)){
				 if(type == 1){
					 var maxNum = parseFloat($("#suggestSellNumber").text());
					 if(maxNum < depth){
						 depth = maxNum;
					 }
					 $("#buy .bigNum").text(maxNum);
					 $( "#sellSlider").slider( "option", "max", maxNum);
					 $( "#sellSlider").slider("value", depth);
				 }else{
					 var useRmb = $("#buy .useRmb").text().replace(exchangeBiNote , "");
					 var maxNum = formatNumber(formatNumberUse(accDiv(useRmb , price)));
					 if(maxNum < depth){
						 depth = maxNum;
					 }
					 $( "#buySlider").slider( "option", "max", maxNum);
					 $( "#buySlider").slider("value", depth);
				 }
			}
			if(type == 1){
				var sellNumber = $("#sellNumber").length > 0 ? $("#sellNumber") : $("#btcNumInput");
				$("#SellTab").trigger("click");
				$("#sellUnitPrice").val(price);
				sellNumber.val(depth);
				sellNumber.blur();
			}else{
				$("#BuyTab").trigger("click");
				$("#buyUnitPrice").val(price);
				$("#buyNumber").val(depth);
				$("#buyNumber").blur();
			}
		});
	});
}

