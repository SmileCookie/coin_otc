seajs.config({
    alias: {
        module_base: "dist/web.base",
        module_method: "dist/web.method",
        module_lang: "dist/web.lang",
        module_common: "dist/web.common",
        module_user: "dist/web.user",
        module_asset: "dist/web.asset",
        module_trans: "dist/web.trans",
        module_market: "dist/web.market",
        module_simChart: "dist/web.simchart",
        module_tmpl: "dist/pack.tmpl",
        module_range: "dist/pack.range",
        module_pako: "dist/pack.pako",
        module_encrypt: "dist/pack.encrypt",
        module_highCharts: "dist/pack.highcharts",
		module_highStock: "dist/pack.highstock",
		module_vote: "dist/web.vote",
		module_authType:"dist/web.auth_type",
		module_draw: "dist/web.draw",
		module_wheeler:"dist/web.wheeler",
    },
    paths: {
        dev: "statics/js",
        dist: "statics/js"
    },
    debug: false,
    base: DOMAIN_STATIC,
    map: [
        [/^(.*\/statics\/.*\.(?:css|js))(?:.*)$/i, "$1?" + VERSION]
    ],
    charset: "utf-8"
});





var webSocket={};
var ajaxRun = true;
webSocket.socket=null; 
var t1=new Date().getTime();
var i =0;
webSocket.init = function(callback){
	return ajaxRun = true;
	var $this = this ;

		var url = window.location.href;  
		i++;
		if(($this.socket==null || ($this.socket && $this.socket.readyState != WebSocket.OPEN)) && i==1){
			$this.socketConnection();
		}else{
			if(i>1) i--;
		}	
		 console.info("调用 socket "+$this.socket);
		if(!$this.socket){
        	return;
		}
		$this.socket.onclose = function(){
			ajaxRun = true;
			console.info("socket close");

						setTimeout(function(){if(i>0) i--;
				}, 1000);

						var t2=new Date().getTime();
			console.log("失败耗时："+(t2-t1));
		};  
		$this.socket.onerror = function(){
			ajaxRun = true;
			console.info("socket error");
			setTimeout(function(){
				if(i>0) i--;
				}, 1000);

						var t2=new Date().getTime();
			console.log("失败耗时："+(t2-t1));
		};

				if($.isFunction(callback)){ 
			console.log("callback:"+($this.socket.readyState == WebSocket.OPEN));
			var callbackFn = setInterval(function(){
				if($this.socket.readyState == WebSocket.OPEN){
					callback();
					clearInterval(callbackFn);
				}
			},20);
		};

	   };


webSocket.socketConnection = function (){
	var $this = this ;
    var targetUrl = "wss://1kline.vip.com/websocket"; 
    if(typeof ZNAME != 'undefined'){
    	if(ZNAME=='z'){
    		targetUrl = "wss://1kline.vip.com/websocket";
         }else if(ZNAME=='t'){
        	 targetUrl = "ws://1tkline.vip.com:28080/websocket"; 
         }else if(ZNAME=='w'){
        	 targetUrl = "ws://1wkline.vip.com:8580/websocket"; 
         }
    }else{
    	return;
    }


        	if (!window.WebSocket) {
		window.WebSocket = window.MozWebSocket;
	}
	if (window.WebSocket) {
		$this.socket = new WebSocket(targetUrl);
		 console.info("init socket finish");
	}else{
		 ajaxRun = true;
		 return;
	} 
	$this.socket.onopen = function() {
		 console.info("socket socketConnection");	
		 ajaxRun = false;
		 i--;

		 		 var t2=new Date().getTime();
			console.log("成功耗时："+(t2-t1));
     }

	 	var i =0;

       if(!!$this.socket){
    	$this.socket.onmessage = function(result) {
    		var json = null;
    		if (result.data instanceof Blob) {    
       			 var blob = result.data;            
       			 var reader = new FileReader();            
       			 reader.readAsText(blob);          
       			 reader.onload = function(evt){       
       				 if(evt.target.readyState == FileReader.DONE){ 
       					 var before = evt.target.result.length;
       					 ungzip(evt.target.result,function(result){
       						 if(i<10){
       							console.log("解压前："+before+" 解压后："+result.length);
       							i++;
       						 }

       						          					 if(result.indexOf("(")!=0){
          						 json = eval("("+result+")");
          					 }else{
          						 json = eval(result);
          					 }

          					$this.dealMessage(json);
       					 });

       					        				 }
       			 }
	   		 }else{
	   			  ungzip(result.data,function(result){
     					 if(result.indexOf("(")!=0){
     						 json = eval("("+result+")");
     					 }else{
     						 json = eval(result);
     					 }
     					$this.dealMessage(json);
  					 });

	   				      	 }
        };
    }
};

webSocket.sendMessage = function(message){
	var $this = this ;
	var connectionTimes =0;
		var readyFun = setInterval(function(){
			connectionTimes += 50;
			if(connectionTimes >= 10000){
				clearInterval(readyFun);
			}
			if ($this.socket.readyState == WebSocket.OPEN) {
				$this.socket.send(message);
				clearInterval(readyFun);
			}
		},50);
}


webSocket.dealMessage = function(result){
	 var channel = result.channel;
	 if(!channel){
		 channel = result[0].channel;
	 }
	seajs.use(["module_market","module_asset","module_trans","module_common"],function(market,asset,trans){
		if(channel=="top_all"){
			market.getMarketSocket(result);
		}else if(channel=="push_user_asset"){
			asset.getUserAssetBySocket(result);
		}else if(channel.indexOf("dish_depth_")>=0 || channel.indexOf("dish_length_")>=0){
			trans.setDish(result);
		}else if(channel=="push_user_record"){
			 trans.setEntrustRecordSocket(result); 
		}else if(channel.indexOf("_kline_")>0){
			var ifr = document.getElementById('marketFrame');
			var win = ifr.window || ifr.contentWindow;
			win.updateKlineData(result); 
		}else  if(channel.endWith("_lasttrades") || channel.endWith("_depth")){
			var ifr = document.getElementById('marketFrame');
			var win = ifr.window || ifr.contentWindow;
			if(channel.endWith("_lasttrades")){
				win.kline.pushTrades(result.data);
				win.kline.klineTradeInit=true;
				win.clear_refresh_counter();
			}
			if(channel.endWith("_depth")){
				win.kline.updateDepth(result);
			}
		}
	});
}
webSocket.init();

window.onbeforeunload =function(){
	if(webSocket.socket){
		return webSocket.socket.close();
	}

	}

String.prototype.startWith=function(str){  
    if(str==null||str==""||this.length==0||str.length>this.length)  
      return false;  
    if(this.substr(0,str.length)==str)  
      return true;  
    else  
      return false;  
    return true;  
}  
String.prototype.endWith=function(str){  
    if(str==null||str==""||this.length==0||str.length>this.length)  
      return false;  
    if(this.substring(this.length-str.length)==str)  
      return true;  
    else  
      return false;  
    return true;  
}  

function ungzip(zipData,callback){
	var unZipData = "";
	seajs.use(["module_pako"],function(pako){
		if(zipData.indexOf("channel")>=0){
			unZipData = zipData;
		}else{
			try{
				var strData     = atob(zipData);
				var charData    = strData.split('').map(function(x){return x.charCodeAt(0);});
				var binData     = new Uint8Array(charData);
				var data        = pako.inflate(binData);
				unZipData     = String.fromCharCode.apply(null, new Uint16Array(data));
			}catch(e){
				unZipData = zipData;
			}
		}
		if($.isFunction(callback)){
			callback(unZipData);
		}
	});
}