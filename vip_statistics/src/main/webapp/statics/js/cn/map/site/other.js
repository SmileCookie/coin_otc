

function toggleMingxi(){
	
	window.open("/u/transaction/entrustdeatils");
	
}

$(function() {
	$("#klineTop").html(numberBiEn+"/K线图");
	$("title").text(numberBi+"K线图-交易网");
	getCurr();
	timeChatBaddy();
	
});


var longTime = 0;
var isRunning=false;
var chatLoop=0;
function timeChatBaddy()
{
	longTime = window.setInterval(function(){
		   if(isRunning)
		   return;
		getCurr();
	}, 7300);
}
var lastTime=0;//last get data time
var currentPriceLast=0;
function getCurr(){
    isRunning=true;
     $.getJSON(entrustUrlBase+"Line/GetTrans-"+market+"?lastTime="+lastTime+"&length=20&jsoncallback=?",  function(result) {
    	 isRunning=false;
    	 var nowTime=result[0].lastTime;
    	 if(nowTime==lastTime)
            return;   	 
    	 lastTime=nowTime;
		var currentPrice=result[0].currentPrice;
		
		//alert(currentPrice);
		var buyOne=0;//买一
		var sellOne=0;//卖一
		var high=result[0].high;//最高价格
		var low=result[0].low;//最低价格
       
		var totalBtcToday=result[0].totalBtc ? result[0].totalBtc : 0;//今日的总btc
		
		var listUp=result[0].listUp;
		var topS="";
		var tops100=0;
		for(var i=0;i<listUp.length;i++)
		{
			var btc =listUp[i][1];
			if(btc>tops100)
				tops100=btc;
		}
			for(var i=0;i<listUp.length;i++)
			{
				var price = listUp[i][0];
				var btc =listUp[i][1];
				if(i==0)
					sellOne=price;
				var per=btc*100/tops100;
				
				topS=topS+' <li>'+
				'<div class="depthPrice">'+price+'</div>'+
				'<div>'+btc+'</div>'+
					'<div align="right" class="bar-volume-outer"><span style="width:'+per+'%" class="ask"></span>'+
					'</div></li>';
				//alert(price+":"+btc);
		   } 
		   $("#lineTwo").html(topS);
		    
		   
		var listDown=result[0].listDown;
		var downS="";
		var downS100=0;
		for(var i=0;i<listDown.length;i++)
		{
			var btc =listDown[i][1];
			if(btc>downS100)
				downS100=btc;
		}
			for(var i=0;i<listDown.length;i++)
			{
				
				var price = listDown[i][0];
				if(i==0)
					buyOne=price;
				var btc =listDown[i][1];
				var per=btc*100/downS100;
				downS=downS+'  <li>'+
				'<div align="right" class="bar-volume-outer"><span style="width:'+per+'%" class="bid"></span></div>'+
			'<div>'+btc+'</div>'+
			'<div class="depthPrice">'+price+'</div></li>';
				//alert(price+":"+btc);
		   } 
			   $("#lineOne").html(downS);
			var listRight=result[0].transction;
			var rightS="";
			var right100=0;
			for(var i=0;i<listRight.length;i++)
			{
				var btc =listRight[i][1];
				if(btc>right100)
					right100=btc;
			}
			
			var currentIsBuy=0; 
			for(var i=0;i<listRight.length;i++)
			{
					var price = listRight[i][0];
					var isBuy = listRight[i][2];
					var btc =listRight[i][1];
					var t =listRight[i][3];
					
					var date = new Date(t);
					var nowStr = date.format("hh:mm:ss"); 
					
					
					var per=btc*100/right100;
					var bid='bid';
					if(isBuy==1){
						bid='ask';
						if(i==0)
						currentIsBuy=1;
					}
						
					rightS=rightS+'<li> '+
					'<div>'+nowStr+'</div>'+
					'<div class="'+bid+'">'+price+'</div>'+
					'<div>'+btc+'</div>'+
					'<div class="bar-volume-outer"><span style="width:'+per+'%" class="'+bid+'"></span></div></li> ';
					//alert(price+":"+btc);
			   } 
				   $("#lineThree").html(rightS);
				
            $("#best_bid").text(buyOne);
             $("#best_ask").text(sellOne);
            $("#fund_high").text(high);
             $("#fund_low").text(low);
				   $("#currentPrice").text(currentPrice); 
					 currentPriceLast=currentPrice;
		if(currentIsBuy==1)//说明下跌了
			$("#fund_last").removeClass("value_green").addClass("value_red");
		else if(currentIsBuy==0)
			 $("#fund_last").removeClass("value_red").addClass("value_green");
     $("#fund_volume").text(totalBtcToday);
		   isRunning=false;
		  
	
		 }); 
}


function sillyEntrust(isBuy){
	Iframe({
	    Url:"/u/silly?isBuy="+isBuy,
        zoomSpeedIn		: 200,
        zoomSpeedOut	: 200,
        Width:610, 
        Height:isBuy>=0?350:300,
        scrolling:"no",
        isIframeAutoHeight:false,
        Title:isBuy==1?"分散批量委托买入":isBuy==0?"分散批量委托卖出":"批量撤单"
	});
}
function btcEntrust(isBuy){
	Iframe({
	    Url:"/u/transaction/entrust/toEntrust?isBuy="+isBuy,
        zoomSpeedIn		: 200,
        zoomSpeedOut	: 200,
        Width:620,
        Height:490,
        scrolling:"no",
        isIframeAutoHeight:false,
        Title:isBuy==1?"委托买入":"委托卖出"
	});
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
