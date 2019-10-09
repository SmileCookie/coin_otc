var inAjaxing = false;
function getCurr(){
     isRunning=true; 
     var u="https://data.vip.com/data";
   
     $.getJSON("/chart/GetData?lastTime="+lastTime+"&jsoncallback=?",  function(result) {
    	  
    	 show50(result);
	 });
} 


function getRecordFirst(){
	if (inAjaxing) {
		//Wrong("sorry click too fast!");
		return;
	}
	inAjaxing = true;
	
		$("#showList").Loadding({
			OffsetXGIF : 270,
			OffsetYGIF : 80
		});
	
	$.getJSON("/chart/GetRecord?lastTime="+lastTimeRecord+"&jsoncallback=?",  function(result) {
		inAjaxing = false;
		//关闭lodding动画 
		
			$("#showList").Loadding({
				IsShow : false
			});    
	     showRecord(result,0);
	 });
}

function getRecord(pageIndex,type,timeFrom,timeTo,numberFrom,numberTo,priceFrom,priceTo){
	if (inAjaxing) {
		//Wrong("sorry click too fast!");
		return;
	}
	inAjaxing = true;
		$("#mylist").Loadding({
			OffsetXGIF : 270,
			OffsetYGIF : 80
		});
	$.getJSON("/chart/GetRecord?lastTime="+lastTimeRecord+"&type="+type+"&pageIndex="+pageIndex+"&timeFrom="+timeFrom+"&timeTo="+timeTo+"&numberFrom="+numberFrom+"&numberTo="+numberTo+"&priceFrom="+priceFrom+"&priceTo="+priceTo+"&jsoncallback=?",  function(result) {
		inAjaxing = false;
		//关闭lodding动画 
			$("#mylist").Loadding({
				IsShow : false
			});     
		showRecord(result,pageIndex);
   });
}

/**
 * 是否显示结果
 * @param result 结果
 * @param pageIndex 大于0代表需要显示分页
 */
function showRecord(result,pageIndex){
	var record=result[0].record;
	var res = [];
	var maxLength=record.length-1;
	if(pageIndex==0&&maxLength>4)
		maxLength=4;
	for(var i=0;i<maxLength;i++){
		var entrustId=record[i][0];
		var unitPrice=record[i][1];
		var numbers=record[i][2];
		var completeNumber=record[i][3];
		var completeTotalMoney=record[i][4];
		var types=record[i][5];
		var submitTime=record[i][6];
		var status=record[i][7];
		
		var cl = '';
		if(status == 0 || status == 3)
			cl += '<a href="javascript:;" id="cancel1_'+entrustId+'" onclick="cancle('+entrustId+' , '+types+',1)">取消</a>';
	    if(completeNumber>0)
		    cl += '<a href="javascript:;" onclick="details('+onee.id+')">查看</a>';

	    if(cl=='')
	    	cl='—';
		if(status==0)
			status="处理中";
		else if(completeNumber==0&&status==3)
			status="等待成交";
		else if(status==3)
			status="部分成交";
		else if(status==2)
			status="已成交";
		else if(completeNumber==0&&status==1)
			status="已取消";
		else if(status==1)
			status="已取消部分未成交";
		var classT = "pi";
		var nameType="买";
		if(types == 0){
			nameType = "卖";
			classT = "po";
		}

		var date = new Date(submitTime);
		var nowStr = date.format("MM-dd hh:mm:ss"); 
		
		var junjia='-';
		if(completeNumber>0)
			junjia=completeTotalMoney/completeNumber;
	
		var comp='-';
		if(completeTotalMoney>0)
			comp=completeTotalMoney;
		var row= '<li>'+
    	   '<div class="s" title="'+submitTime+'">'+nowStr+'</div>'+
    	   '<div class="pp">'+unitPrice+'</div>'+
    	   '<div class="'+classT+'">'+nameType+numbers+'</div>'+
       
        '<div class="cn">'+completeNumber+'</div>'+
        '<div class="mp">'+junjia+'</div> <div class="tn">'+comp+'</div>'+
        '<div class="st">'+status+'</div>'+
        '<div class="o">'+cl+'</div>'+
    '</li>';
		res.push(row);
	}
	if(res.length > 0){
		if(pageIndex>0){
			$("#mylist ul").html(res.join(""));
			
			var rtn=GetPaper(pageIndex,result[0].count);
			$("#page_navA").html("<div class=\"con\">"+rtn+"</div>");
		}
		else
		    $("#showList ul").html(res.join(""));
	}
	//if(pageIndex>0){//显示分页
	
	//}
}


function showMsg(currentPage, pageSize) {
    //回调方法自定义，两个参数，第一个为当前页，第二个为每页记录数
    /****************将currentPage和pageSize请求数据更新列表,最好使用ajax技术******************/
    alert("请求数据要的相应参数>> currentPage: "+ currentPage + " pageSize: " + pageSize);
  }
   
   /*****如果要更新分页系统请如下操作******/
    //psys.update(count); //@count 为记录总数
function updatePageSys(){
     psys.update(150); //@count 为记录总数
}

$(function() {
	init(1 , true);
	timeChatBaddy();
});

function init(type , ajax){
	//initEntrusts(type , ajax);
	getRecordFirst();
	//dsc.user.ticker();
	getCurr();
}

var lastTimeRecord=0;
var entrust = null;


function cancle(ids , type, listType){
	dsc.tips["取消"] = "否";
	Ask({
		Msg : "确定取消当前委托吗？",
		callback : "cle(" + ids + " , "+type+", "+listType+")"
	});
}

function cle(id , type , listType){
	var url1 = "/chart/cancle?entrustId=" + id;
	
	
	dsc.ajax({url : url1, 
		div : "showList",
		suc : function(){
			 var code=$(xml).find("MainData").text();
			 var des=$(xml).find("Des").text();
			 if(code==100){
			   Right(des);
			   getRecordFirst();
			 }
			 else
				 Wrong(des); 
		},
		err : function(xml){
			Wrong($(xml).find("Des").text());
			
		}
	});
}

var longTime = 0;
var isRunning=false;
function timeChatBaddy(){
	longTime = window.setInterval(function(){
		   if(isRunning)
		   return;
		getCurr();
	}, 6000);
	dsc.user.uticker();
 	setInterval(function(){
 		 dsc.user.uticker();
 	},30000);
}
var lastTime=0;//last get data time


function show50(result){
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
		var sellArray = [];
		var tops100=0;
		if(listUp.length > 50){
			listUp = listUp.slice(listUp.length - 50 , listUp.length);
		}
		
		for(var i=0;i<listUp.length;i++){
			var btc =listUp[i][1];
			if(btc>tops100)
				tops100=btc;
		}
		var iIndex = 1;
		var dep = 0;
		
		for(var i=0;i<listUp.length-1;i++){
				var price = listUp[i][0];
				var btc = listUp[i][1];
				if(i == listUp.length-1)
					sellOne=price;
				var per = btc*100/tops100;
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
				
				var note = '<li><div class="s">'+iIndex+'</div>'+
							  '<div class="p">'+price+'</div>'+
							  '<div class="n">'+btc+'</div>'+
							  '<div class="outer"><span style="width:'+per+'%">'+nums+'</span></div>'+
							  '<div class="d">'+dep+'</div></li>';
				//sellArray.push(note);
				iIndex++;				
		}
		var addNum = 50 - listUp.length;
		if(addNum > 0){
			for(var a=0;a<addNum;a++){
				sellArray.push('<li><div class="s">'+(listUp.length + a + 1)+'</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>');
			}
		}
		
		$("#sellout ul").html(sellArray.join(""));
		
		var listDown=result[0].listDown;
		var buyArray = [];
		var downS="";
		var downS100=0;
		
		if(listDown.length > 50){
			listDown = listDown.slice(0 , 50);
		}
		
		for(var i=0;i<listDown.length;i++){
			var btc =listDown[i][1];
			if(btc>downS100)
				downS100=btc;
		}
		iIndex = 1;
		dep = 0;
		for(var i=0;i<listDown.length;i++){
			var price = listDown[i][0];
			if(i==0)
				buyOne=price;
			var btc =listDown[i][1];
			var per=btc*100/downS100;
			if(per < 1)per = 1;
			dep = accAdd(dep , btc);
			
			var nums = "";
			if(entrust != null){
				for(var h = 0; h<entrust.buy.length; h++){
					if(entrust.buy[h].price == price){
						nums = entrust.buy[h].btcs;
					}
				}
			}
			var note = '<li><div class="s">'+iIndex+'</div>'+
							  '<div class="p">'+price+'</div>'+
							  '<div class="n">'+btc+'</div>'+
							  '<div class="outer"><span style="width:'+per+'%">'+nums+'</span></div>'+
							  '<div class="d">'+dep+'</div></li>';
			buyArray.push(note);
			iIndex++;
	   } 
		
	   var addNum = 50 - listDown.length;
	   if(addNum > 0){
			for(var a=0;a<addNum;a++){
				buyArray.push('<li><div class="s">'+(listDown.length + a + 1)+'</div><div class="p">-</div><div class="n">-</div><div   class="outer"><span  style="width:0%"></span></div><div class="d">-</div></li>');
			}
	   }
	   $("#buyin ul").html(buyArray.join(""));
	   
	   
		var listRight=result[0].transction;
		var rightS="";
		var transArray = [];
		var right100=0;
		
		if(listRight.length > 50){
			listRight = listRight.slice(0 , 50);
		}
		
		for(var i=0;i<listRight.length;i++){
			var btc =listRight[i][2];
			if(btc>right100)
				right100 = btc;
		}
		for(var i=0;i<listRight.length;i++){
			var price = listRight[i][0];
			var isBuy = listRight[i][1];
			var btc =listRight[i][2];
			var t =listRight[i][3];
			var per=btc*100/right100;
			if(per < 1)per = 1;
			var bid='outer';
			if(isBuy == 1)
				bid='in';
			var note = '<li><div class="s">'+t+'</div>'+
							  '<div class="p">'+price+'</div>'+
							  '<div class="n">'+btc+'</div>'+
							  '<div class="'+bid+'"><span style="width:'+per+'%"></span></div>';
			transArray.push(note);
			
			rightS=rightS+'<li> '+
			'<div>'+t+'</div>'+
			'<div class="'+bid+'">'+price+'</div>'+
			'<div>'+btc+'</div>'+
			'<div class="bar-volume-outer"><span style="width:'+per+'%" class="'+bid+'"></span></div></li> ';
			//alert(price+":"+btc);
	   } 
	   $("#trans ul").html(transArray.join(""));
             $("#buyone").text(buyOne);
             $("#sellone").text(sellOne);
             $("#highone").text(high);
             $("#lowone").text(low);
             $("#currentOne").text(currentPrice);
             $("#totalBtcOne").text(totalBtcToday);
		     isRunning=false;
}

function accountInfoShow(rmb , btc , frmb , fbtc , buyBtc , sellRmb , total , buyone , sellone , btq , noBtc , nextBtc){
	$("#userPayAccountInfo .t .name").text($.cookie("name"));
	$("#userPayAccountInfo .b").text(total);
	
	$("#userPayAccountInfo .useRmb").text(rmb);
	$("#userPayAccountInfo .freezRmb").text(frmb);
	$("#userPayAccountInfo .totalRmb").text(accAdd(frmb,rmb));
	
	$("#userPayAccountInfo .useBtc").text(btc);
	$("#userPayAccountInfo .freezBtc").text(fbtc);
	$("#userPayAccountInfo .totalBtc").text(accAdd(fbtc,btc));
	
	$("#buy .useRmb").text("￥"+rmb);
	$("#buy .canBuy").text("฿"+buyBtc);
	$("#buy .suggest").text(sellone);
	
	$("#sell .useBtc").text("฿"+btc);
	$("#sell .suggest").text(buyone);
	$("#sell .bigNum").text(btc);
	$("#tipsTotalBtq").text(btq);
	$("#tipsNoBtc").text(noBtc);
	$("#tipsNextBtc").text(nextBtc);
	$("#assets .assets span").text(total);
}


function changeSell() {
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
	 getRecord(1,0,0,0,0,0,0,0);
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
		if(dsc.tool.isFloat(total) && dsc.tool.isFloat(price)){
			btcNumber.val(accDiv(total , price));
		}
	}
}

function numCheck(type){
	var formId = getFormIdByType(type); 
	var res = true;
	var btcNumber = $("#"+formId + " .btcNumInput").val();
	
	if(dsc.tool.isFloat(btcNumber) && btcNumber > 0){
		if(parseFloat(btcNumber) < 0.001){
			$("#"+formId + " .btcNumInput").val(0.001);
		}
		if(parseFloat(btcNumber) > 1000){//
			$("#"+formId + " .btcNumInput").val(1000);
		}
		if(btcNumber.indexOf(".") > 0 && btcNumber.substring(btcNumber.indexOf(".")).length > 4){
			$("#"+formId + " .btcNumInput").val(btcNumber.substring(0 , btcNumber.indexOf(".") + 4));
		}
		initTotal(type);
	}
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
 
 function initTotal(type){
	 var formId = getFormIdByType(type);
	 
	 var btcNumber = $("#"+formId + " .btcNumInput").val();
	 var price = $("#"+formId + " .unitPrice").val();
	 if(price != null && dsc.tool.isFloat(price)){
		 if(type == 1){
			 var useRmb = $("#buy .useRmb").text().replace("￥" , "");
			 $("#"+formId + " .bigNum").text(accDiv(useRmb , price));
		 }
	 }
	 if(price != null && dsc.tool.isFloat(price) && dsc.tool.isFloat(btcNumber)){
		 $("#"+formId + " .realAccount").val(accMul(price , btcNumber));
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
	 if(dsc.tool.isFloat(amount)){
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
	if(dsc.tool.isFloat(unitPrice) && unitPrice > 0){
		if(parseFloat(unitPrice) < 0){
			obj.val(def);
		}
		if(parseFloat(unitPrice) > 100000){//
			obj.val(def);
		}
	}
	if(unitPrice.indexOf(".") > 0 && unitPrice.substring(unitPrice.indexOf(".")).length > 3){
		obj.val(unitPrice.substring(0 , unitPrice.indexOf(".") + 3));
	}
}
var twoTimeout;
var threeTimeout;
var startTimer=false;
var submitfirst=false;
function trans(isBuy){
//	if(!dsc.user.checkLogin()){
//		return;
//	}
	
	var formId = getFormIdByType(isBuy);
	var btcNumber = $("#"+formId + " .btcNumInput").val();
	var unitPrice = $("#"+formId + " .unitPrice").val();
	var safePwd = $("#"+formId + " .safePassword").val();
	var type = isBuy == 1 ? "买入" : "卖出";
	
	if(unitPrice.length==0 || !dsc.tool.isFloat(unitPrice) || parseFloat(unitPrice) <= 0){
		Wrong("请输入"+type+"的价格，数字类型且大于0.00");
		return;
	}
	
	if(btcNumber.length==0 || !dsc.tool.isFloat(btcNumber) || btcNumber < 0.001 || btcNumber > 1000){
		Wrong("请输入委托"+type+"的比特币数量，数字类型且大于0.001小于1000.00");
		return;
	}
	var useSafe = $("#useSafe").val();
	if(useSafe==1 && (safePwd.length < 6 || safePwd.length > 16)){
		Wrong("请输入您的安全密码，6-16位。");
		return;
	}
		
	var datas = FormToStr(formId); 
	if(datas == null){return;}
	submitfirst = true;
	dsc.ajax({
		 needLogin : true, //需要登录
		 formId : formId,//表单ID
		 url : "/chart/entrust", 
		 div : formId,//lodding的div
		 suc : function(xml){ 
			 var code=$(xml).find("MainData").text();
			 var des=$(xml).find("Des").text();
			 if(code==100){
			   Right(des);
			   getRecordFirst();
			 }
			 else
				 Wrong(des); 

		  },
		  err : function(xml){
			  Wrong($(xml).find("Des").text());

		  }
	});
}



function copyVal(obj , clsXz){
	$(clsXz).val($(obj).text());
}

function closeSafePwd(){
	if(!dsc.user.checkLogin()){
		return;
	}
	
	Ask2({Msg:"您确定要关闭安全密码吗？", call:function(){
		Iframe({
		    Url:"/en/u/safe/safePwdFrame.html",
	        zoomSpeedIn		: 200,
	        zoomSpeedOut	: 200,
	        Width:500,
	        Height:330,
	        scrolling:"no",
	        isIframeAutoHeight:false,
	        Title:"安全验证"
		});
	}});
}
function startSafePwd(){
	if(!dsc.user.checkLogin()){
		return;
	}
	
	Ask2({Msg:"您确定要开启安全密码吗？", call:function(){
		dsc.ajax({url : "/u/safe/useOrCloseSafePwd", dataType : "json", suc:function(json){
			Right(json.des, {callback:"window.location.reload()"})
		}});
	}});
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
	 getRecord(page,0,0,0,0,0,0,0);
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

/**
 * 当前页面，最大页面
 */
function  GetPaper(curpage,numbers)
{

	var showItems=3;
	var getPages=numbers/10;
	if(numbers%10==0){
		getPages=parseInt(numbers/10);
	}else{
		getPages=parseInt(numbers/10)+1;
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