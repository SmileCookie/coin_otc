define(function(require, exports, module) {
		"require:nomunge,exports:nomunge,module:nomunge";
	    var user = require("module_user");
	    var market = require("module_market");
	    var tmpl_s = require("module_tmpl");
	    var M = require("module_method");
		var wheel = require("module_wheeler");

	    var lockRequest = false;
        var asset = {};
		var allCurrency = {};
		asset.sortName='';
		asset.clickNum=0;
		asset.thIndex=0;	
		asset.isHideZerobalance = false;
		asset.filterVal = '';
		asset.search_val = '';
		asset.exchangeRate = 0;
        asset.init = function(t) {
        	var $this = this ;
        	if(typeof user == "object"){
        		user.init();
        	}else{
        		try {
        			var userInit = setInterval(function(){
        				user = require("module_user");
                		if(typeof user == "object"){
                			user.init();
                			clearInterval(userInit);
                		}
                	},50);

        			} catch (e) {
        		    console.log("user.init failed");
        		}
        	}
        	if(!user.isLogin()) return false;
        	var userId = $.cookie(UID);
        	// var readyFun = setInterval(function(){
        	// 	if(webSocket){
        	// 		webSocket.init(function(){
            //     		webSocket.sendMessage('{"event":"addChannel","channel":"push_user_asset","userId":"'+userId+'","isZip":"true"}');
            //     	});
        	// 		clearInterval(readyFun);
        	// 	}
			// },50);
        	//$this.getUserAsset();
        	setInterval(function(){
        		if(ajaxRun==false){
    				return;
    			}
        		//$this.getUserAsset();
        	}, 5000);
        };
        asset.hasLoan = 0;
        asset.allTotal = 0;
        asset.netTotal = 0;
        asset.getUserAssetBySocket = function(result,callback){
        	if(!user.isLogin()) return false;
        	var $this = this ;
        	$this.setUserAssetToPage(result,callback);
        };
        asset.setAssetSel = function(){
        	var $this = this ;
        	if(document.getElementById("setAssetSel")!=null){
        		var options = "";
            	for(var key in allCurrency){
            		var symbol = allCurrency[key];
            		if(location.href.split("/manage/account/")[1].indexOf(key) != -1){
            			options+="<option value=\""+key+"\" selected>"+symbol+"</option>";
            		}else{
            			options+="<option value=\""+key+"\">"+symbol+"</option>";
            		}
            	}
            	$("#setAssetSel").html(options);
            	$("#setAssetSel").on("change",function(){
            		if(location.href.split("/manage/account/")[1].indexOf("downrecord") != -1){
            			window.location.href="/manage/account/downrecord/"+this.value;
            		}else{
            			window.location.href="/manage/account/chargerecord/"+this.value;
            		}
            	});
        	}
        };
        asset.getUserAsset = function(callback){
        	if(!user.isLogin() || user.needAuth()) return false;
			var $this = this ;
        	$.getJSON(DOMAIN_VIP + "/manage/getAssetsDetail?callback=?", function(result) {
        	   $this.setUserAssetToPage(result,callback);
        	// $this.topFundsDetail(result);
        	});
        };
        asset.setUserAssetToPage = function(result,callback){
        	var $this = this ;
        	for(var key in result){
        		var funds = result[key];
        		allCurrency[key.toLowerCase()] = key;
        		var decimalNum = 6;
        		asset[key.toLowerCase()] ={
        			total:funds.balance+funds.freeze,
        			usable:funds.balance,
        			freeze:funds.freeze,
        			loan:0,
        			canLoanIn:0,
        			decimal:decimalNum
        		};
        	}
     	    $this.setAssetSel();
        	var listDiv  = "#fundsDetail";
        	if(document.getElementById("fundsDetail")!=null){
				var currency = $.cookie("currency") || "USD";
				$.ajax({
					type: "GET",
					url: DOMAIN_TRANS+"/getExchangeRate",
					dataType: 'json',
					success: function (json){
						if(json.isSuc){
							var res = json.datas.exchangeRateUSD;
							asset.exchangeRate = res[currency]
							$(listDiv).html(tmpl("tmpl-"+(listDiv.replace(/[#,.]/g,"")), $this.formatFundsDetail(result)));
							$this.tips_hover();
						}
					}
				})
        	}
     	    if($.isFunction(callback)){
 				callback();
 			};
        };
        asset.topFundsDetail = function(result){
			var $this = this;
			var listDiv  = "#topFundsDetail";
			if(document.getElementById("topFundsDetail")!=null){
				$(listDiv).html(tmpl("tmpl-"+(listDiv.replace(/[#,.]/g,"")), $this.formatFundsDetail(result)));
			}
        }
        asset.formatFundsDetail = function(result){
        	var $this = this;
        	var record = [],record2 = [],record3 = [];
			var i=0;
			Big.RM = 0;
        	for(var key in result){
        		var funds = result[key];
    			var balance =  funds.balance;
    			var freeze = funds.freeze;
    			var fundsType = funds.fundsType;
    			var unitTag = funds.unitTag;
    			var propTag = funds.propTag;
                var total = funds.total;
				var coinFullName = funds.coinFullNameEn;
				var canCharge = funds.canCharge;
				var canWithdraw = funds.canWithdraw;
				var eventFreez = funds.eventFreez;
				var usdExchange = funds.usdExchange!="--"&&asset.exchangeRate?funds.usdExchange*total*asset.exchangeRate:0;
				
    			record[i] = {};
    			record[i].propTag = propTag;
    			record[i].coinFullName = coinFullName;
				record[i].stag = propTag.toLowerCase();
    			record[i].balance6 = new Big(balance).toFixed(8) //可用余额
    			record[i].freeze6 = new Big(freeze).toFixed(8);  //冻结资金
				record[i].total6 = new Big(total).toFixed(8);   //总额
				record[i].balance = balance
                record[i].freeze =freeze
				record[i].total = M.fixNumber(total);
                record[i].canCharge =canCharge
				record[i].canWithdraw =canWithdraw
				record[i].valuation = new Big(usdExchange).toFixed(2);
				record[i].eventFreez = eventFreez;
    			i++;
			}
			record2 = $this.filterRecord(record);
			record3 = $this.sortRecord(record2);
    		return record3;
		};
		asset.filterRecord = function(data){
			var newData = data;
			if(asset.filterVal){
				nameArr = data.filter(function(element, index, array){
					return element.propTag.indexOf(asset.filterVal) !== -1
				})
				newData = nameArr;
			}
			if(asset.isHideZerobalance){
				newData = newData.filter(function(element, index, array){
					return element.total6 > 0;
				})
			}
			return newData;
		}
		asset.sortRecord = function(data){
			var sortData = [];
			var clickSortName = asset.sortName;
			var sortABC = function(a,b){
				return a[clickSortName] < b[clickSortName]?-1:1;
			}
			var sortNumber2 = function(a,b){
				return a[clickSortName] - b[clickSortName];
			}
			if(clickSortName&&asset.clickNum!==0){
				if(asset.clickNum==1 && asset.thIndex<2){
					sortData = data.sort(sortABC)
				}else if(asset.clickNum==2 && asset.thIndex<2){
					sortData = data.sort(sortABC).reverse();
				}else if(asset.clickNum==1 && asset.thIndex>=2){
					sortData = data.sort(sortNumber2)
				}else if(asset.clickNum==2 && asset.thIndex>=2){
					sortData = data.sort(sortNumber2).reverse()
				}
				return sortData
			}else{
				return data;
			}
			
		}
		
        asset.getTotalAsset = function(callback){
        	if(!user.isLogin()) return false;
        	var $this = this ;
        	var cnyTotalAsset = $this.cny.total;
        	var btcTotalAsset = $this.btc.total * market.btcMarket[0];
        	var ethTotalAsset = $this.eth.total * market.ethMarket[0];
        	var ltcTotalAsset = $this.ltc.total * market.ltcMarket[0];
        	var etcTotalAsset = $this.etc.total * market.etcMarket[0];
        	var btqTotalAsset = $this.btq.total * market.btqMarket[0] * market.btcMarket[0];
        	$this.allTotal = M.fixNumber(cnyTotalAsset + btcTotalAsset + ethTotalAsset + etcTotalAsset + ltcTotalAsset + btqTotalAsset, $this.cny.decimal);
        	if($this.hasLoan == 1){
        		cnyTotalAsset -=  $this.cny.loan;
        		btcTotalAsset -=  $this.btc.loan * market.btcMarket[0];
        		ethTotalAsset -=  $this.eth.loan * market.ethMarket[0];
        		etcTotalAsset -=  $this.etc.loan * market.etcMarket[0];
        		ltcTotalAsset -=  $this.ltc.loan * market.ltcMarket[0];
        	}else{
        		cnyTotalAsset +=  $this.cny.loan;
        		btcTotalAsset +=  $this.btc.loan * market.btcMarket[0];
        		ethTotalAsset +=  $this.eth.loan * market.ethMarket[0];
        		etcTotalAsset +=  $this.etc.loan * market.etcMarket[0];
        		ltcTotalAsset +=  $this.ltc.loan * market.ltcMarket[0];
        	}
        	$this.netTotal = M.fixNumber(cnyTotalAsset + btcTotalAsset + ethTotalAsset + etcTotalAsset + ltcTotalAsset + btqTotalAsset, $this.cny.decimal);
        	$("#D_allAsset").html($this.allTotal);
			$("#D_canAsset").html($this.netTotal);
			$('#totalAssets').html($this.allTotal);
			$('#totalCny').html(M.fixDecimal($this.cny.total, $this.cny.decimal));
			$('#totalBtc').html(M.fixDecimal($this.btc.total, $this.btc.decimal));
			$('#totalLtc').html(M.fixDecimal($this.ltc.total, $this.ltc.decimal));
			$('#totalEth').html(M.fixDecimal($this.eth.total, $this.eth.decimal));
			$('#totalEtc').html(M.fixDecimal($this.etc.total, $this.etc.decimal));
			formatNum();
    	    if($.isFunction(callback)){
				callback();
			};
        };
        asset.getLoanAsset = function(callback){
        	if(!user.isLogin()) return false;
        	var $this = this ;
        	$.getJSON(DOMAIN_P2P + "/getNetFund?callback=?", function(result) {
        		if(result.isSuc){
        			$this.cny.canLoanIn = M.fixFloat(result.datas.canLoanCny, $this.cny.decimal);
            	    $this.btc.canLoanIn = M.fixFloat(result.datas.canLoanBtc, $this.btc.decimal);
            	    $this.ltc.canLoanIn = M.fixFloat(result.datas.canLoanLtc, $this.ltc.decimal);
            	    $this.eth.canLoanIn = M.fixFloat(result.datas.canLoanEth, $this.eth.decimal);
            	    $this.etc.canLoanIn = M.fixFloat(result.datas.canLoanEtc, $this.etc.decimal);
            	    if($.isFunction(callback)){
        				callback();
        			};
        		}
        	});
        };
        asset.showPage = function(listDiv, type, pageIndex, rsCount, pageSize){
        	var $this = this;
        	var $pageDiv = $(listDiv+"_Page");
        	if(rsCount < pageSize && pageIndex == 1) {
        		$pageDiv.html("");
        		return false;
			}
			var pageCount = rsCount % pageSize == 0 ? parseInt(rsCount / pageSize) : parseInt(rsCount / pageSize) + 1 ;
			$pageDiv.createPage({
    			noPage:false,
    			pageSize:pageSize,
		        rsCount:rsCount,
		        pageCount:pageCount,
		        current: pageIndex || 1,
		        backFn:function(pageNum){
		        	   if(listDiv == '#chargeRecordDetail'){
		        	   		$this.getDownloadRecordDetail(listDiv, type , "/manage/account/chargeRecord/getChargeRecordList",pageNum, pageSize);
		        	   }else if(listDiv == '#downloadRecordDetail'){
		        	   		$this.getDownloadRecordDetail(listDiv, type , "/manage/account/DownRecord/getDownloadRecordList",pageNum, pageSize);
		        	   }else if(listDiv == '#billDetail'){
		             		$this.getBillDetail(listDiv, type, pageNum, pageSize);
		        	   }else if(listDiv == '#loanDetail' || listDiv == '#loanDetailAlready'){
        	    			$this.getLoanRecordDetail(listDiv, type, "/manage/loan/getLoanRecordList", pageNum, pageSize);						   
					   }else if(listDiv == '#loanOutDetail' || listDiv == '#loanOutDetailAlready'){
        	    			$this.getLoanOutRecordDetail(listDiv, type, "/manage/loan/getMyLoanOut", pageNum, pageSize);						   
					   }else if(listDiv == '#chargeRecordDetailH'){
						    $this.getHistoryRecord(listDiv, type, "/manage/account/chargeDownHistory/getChargeRecordList", pageNum, pageSize);						   
				   	   }else if(listDiv == '#downloadRecordDetailH'){
							$this.getHistoryRecord(listDiv, type, "/manage/account/chargeDownHistory/getDownloadRecordList", pageNum, pageSize);						   
					   } else if (listDiv == '#address_list_body') {
						   $this.getDownloadAddress(listDiv, type, "/manage/account/download/getAddressPage/" + type, pageNum, pageSize);
					   }else if(listDiv == '#butionHistory'){
						   $this.getButionHistory(listDiv,type,"/manage/queryUserDistribution",pageNum, pageSize)
					   }
		        }
		    });
        };
        asset.pageIndexInit = function(){
        	var $this = this ;
        	var billType    =    ["all","payin","payout","trade"],
        		isPage      =    false,
        		pageIndex   =    1,
        		pageSize    =    10,
        		listDiv     =    "#billDetail";

        	$(".bk-tabList").slide({
        	    mainCell: "none",
        	    titCell: ".btn-group .btn",
        	    effect: "fade",
        	    trigger: "click",
        	    titOnClassName: "active1",
        	    startFun : function(i,c){
        	    	$this.getUserAsset(function(){
        	    		$this.getBillDetail(listDiv, billType[i], pageIndex, pageSize);
        	    	});
        	    }
        	});
        }
        asset.getBillDetail = function(listDiv, type, pageIndex, pageSize){
        	if(!user.isLogin()) return false;
        	if(lockRequest)  return JuaBox.showWrong(bitbank.L("您有未完成的请求，请等待后重试"));
        	var $this = this ;
        	var htmlNoLogin  = "<tr><td colspan='7'>"+bitbank.L("通用未登录提示")+"</td></tr>";
        	var htmlNoRecord = "<tr><td colspan='7' class='billDetail_no_list'>"+bitbank.L("通用没有任何记录")+"</td></tr>";
        	if(!user.isLogin()){
        		$(listDiv).html(htmlNoLogin);
				$(listDiv+"_Page").html("");
        		return false;
        	};

        	        	lockRequest = true ;
        	$.ajax({
    			url: '/manage/account/billDetail',
    			type: 'POST',
    			data: {
    				pageIndex : pageIndex,
    				pageSize:pageSize, 
					type : "inAndOut"
    			},
    			dataType: 'json',
    			success: function(json) {
    				lockRequest = false ;
    				if (json.isSuc) {
    					if(json.datas.list == null || json.datas.list.length == 0 ){
	    	        			$(listDiv).html(htmlNoRecord);
		    	        		if(pageIndex == 1){
									$(listDiv+"_Page").html("");
		            		}else{
		            		  $this.showPage(listDiv, type, pageIndex, 0, pageSize);
		            		}
	    	    			}else{
	    	    				$(listDiv).html(tmpl("tmpl-"+(listDiv.replace(/[#,.]/g,"")), $this.formatBillDetail(json.datas.list)));
	                			$this.showPage(listDiv, type, pageIndex, json.datas.totalCount, pageSize);
	    	    			}
    				} else {
    					JuaBox.sure(json.des);
    				}
    			},
    			error: function() {
    				lockRequest = false ;
    				JuaBox.sure(bitbank.L('网络访问出错，请稍后重试。'));
    			}
    		});
        }
        asset.formatBillDetail = function(json){

        	var $this = this;
        	var record = [] ;
    		for(var i = 0; i<json.length; i++){
    			var id = json[i].id;
    			var showType = json[i].showType;
    			var sendTime = json[i].sendTime.time;
    			var amount = json[i].amount;
    			var balance = json[i].balance;
				var coinName = json[i].coinName;
				var coinImgName = json[i].coinName;
    			var inout = json[i].bt.inout;
    			var fees = json[i].fees;
				var status = json[i].status;
				
    			record[i] = {};
    			record[i].id = id;
    			record[i].showType = showType;
    			record[i].sendTime = LANG == 'en'? M.formatDate(sendTime, "MM-dd-yyyy hh:mm:ss") : M.formatDate(sendTime, "yyyy-MM-dd hh:mm:ss");
    			record[i].showType = showType;
    			record[i].amount = M.fixNumber(amount, $this[coinName.toLowerCase()].decimal);
    			record[i].balance =M.fixNumber( M.floorNumber(balance, $this[coinName.toLowerCase()].decimal), $this[coinName.toLowerCase()].decimal);
				record[i].coinName = coinName;
				record[i].coinImgName = coinName.toLowerCase();
    			record[i].inout = inout == 1 ? "+" : "-";
    			record[i].numFees = fees;
    			record[i].fees = M.fixNumber(fees, $this[coinName.toLowerCase()].decimal);
    			record[i].status = status;
    		}
    		return record;
        };

        asset.downloadRecordInit = function(type){
        	var $this = this ;
        	var isPage      =    false,
        		url			= "/manage/account/DownRecord/getDownloadRecordList",
        		pageIndex   =    1,
        		pageSize    =    5,
        		listDiv     =    "#downloadRecordDetail";
           	$this.getDownloadRecordDetail(listDiv, type, url,pageIndex, pageSize);
        };

		asset.downloadAddressInit = function(type){  //地址接口
			var $this = this;
			var isPage = false,
				url = "/manage/account/download/getAddressPage/" + type,
				pageIndex = 1,
				pageSize = 5,
				listDiv = "#address_list_body";
			$this.getDownloadAddress(listDiv, type, url,pageIndex, pageSize)	
		}

        asset.chargeRecordInit = function(type){
        		var $this = this ;
        		var isPage      =    false,
        			url			= "/manage/account/chargeRecord/getChargeRecordList",
        			pageIndex   =    1,
        			pageSize    =    5,
        			listDiv     =    "#chargeRecordDetail";
       	 	$this.getDownloadRecordDetail(listDiv, type, url,pageIndex, pageSize);
        };

        asset.getDownloadRecordDetail = function(listDiv, type, url, pageIndex, pageSize){
        	if(!user.isLogin() || user.needAuth()) return false;
        	// if(lockRequest)  return JuaBox.showWrong(bitbank.L("您有未完成的请求，请等待后重试"));
        	var $this = this ;
        	var htmlNoLogin  = "<tr><td colspan='7'>"+bitbank.L("通用未登录提示")+"</td></tr>";
        	var htmlNoRecord = "<tr><td colspan='7' class='billDetail_no_list'>"+bitbank.L("通用没有充值提现记录")+"</td></tr>";
			var loadingHtml = "<tr><td colspan='7'><div class='loading'></div></td></tr>";
			$(listDiv).html(loadingHtml);
			if(!user.isLogin()){
        		$(listDiv).html(htmlNoLogin);
        		$(listDiv+"_Page").html("");
        		return false;
			};
        	// lockRequest = true ;
        	$.ajax({
    			url:url,
    			type: 'POST',
    			data: {
    				pageIndex : pageIndex,
    				pageSize:pageSize,
    				coint:type
    			},
    			dataType: 'json',
    			success: function(json) {
    				// lockRequest = false ;
    				if (json.isSuc){
    					if(json.datas.list == null || json.datas.list.length == 0 ){
    	        			$(listDiv).html(htmlNoRecord);
    	        			if(pageIndex == 1){
            					$(listDiv+"_Page").html("");
            				}else{
              				$this.showPage(listDiv, type, pageIndex, 0, pageSize);
            				}
    	    			}else{
    	    				$(listDiv).html(tmpl("tmpl-"+(listDiv.replace(/[#,.]/g,"")), $this.formatDownloadDetail(json.datas.list)));
                  			$this.showPage(listDiv, type,pageIndex, json.datas.totalCount, pageSize);
    	    			}
    				}else{
    					JuaBox.sure(json.des);
    				}
    			},
    			error: function() {
    				// lockRequest = false ;
    				JuaBox.sure(bitbank.L('网络访问出错，请稍后重试。'));
    			}
    		});
		};

		asset.getDownloadAddress = function (listDiv, type, url, pageIndex, pageSize) {
			if (!user.isLogin() || user.needAuth()) return false;
			if (lockRequest) return JuaBox.showWrong(bitbank.L("您有未完成的请求，请等待后重试"));
			var $this = this;
			var htmlNoLogin = "<div class='address_none'>" + bitbank.L("通用未登录提示") + "</div>";
			var loadingHtml = "<div class='address_none'>loading...</div>";
			var htmlNoRecord = "<div class='address_none'><i class='iconfont2 mr5 icon-tishi'></i>" + bitbank.L("您暂时没有添加任何") + type + bitbank.L("提现地址")+ "</div>";
			$(listDiv).html(loadingHtml);
			if (!user.isLogin()) {
				$(listDiv).html(htmlNoLogin);
				$(listDiv + "_Page").html("");
				return false;
			};
			lockRequest = true;
			$.ajax({
				url: url + "?pageIndex=" + pageIndex + "&pageSize=" + pageSize,
				type: 'GET',
				dataType: 'json',
				success: function (json) {
					lockRequest = false;
					if (json.isSuc) {
						if (json.datas.list == null || json.datas.list.length == 0) {
							$(listDiv).html(htmlNoRecord);
							if (pageIndex == 1) {
								$(listDiv + "_Page").html("");
							} else {
								$this.showPage(listDiv, type, pageIndex, 0, pageSize);
							}
						} else {
							$(listDiv + "_Page").height("44px");
							$(listDiv).html(tmpl("tmpl-" + (listDiv.replace(/[#,.]/g, "")), json.datas.list));
							$this.showPage(listDiv, type, pageIndex, json.datas.totalCount, pageSize);
							$this.tips_hover();
							$this.addressDelete(type);
							$this.addressMemo(type);
						}
					} else {
						JuaBox.sure(json.des);
					}
				},
				error: function () {
					lockRequest = false;
					JuaBox.sure(bitbank.L('网络访问出错，请稍后重试。'));
				}
			});
		};

		asset.tips_hover = function(){
			// $(".text_div").show();
			$(".hover_text").hover(function () {
				$(this).find(".text_divcon").show();
			}, function () {
				$(this).find(".text_divcon").hide();
			})
		}

		asset.addressDelete = function(type){  //删除地址
			var $this = this;
			$(".address_delete").on("click",function(){
				var $parent = $(this).parent();
				var ids = $parent.attr("data-id");
				var memo = $parent.attr("data-memo");
				var address = $parent.attr("data-address");
				$this.alertBox({
					memo: memo,
					address: address,
					type:type,
					id: ids,
					judgment:false
				});
				

			})
		}

		asset.addressMemo = function(type){ //修改标签
			var $this = this;
			$(".address_memo").on("click",function(){
				var $parent = $(this).parent();
				var ids = $parent.attr("data-id");
				var memo = $parent.attr("data-memo");
				var address = $parent.attr("data-address");
				$this.alertBox({
					title: address,
					type: type,
					id: ids,
					judgment:true
				});
			})
		}

		asset.alertBox = function(data){ 
			var $this = this;
			this.docW = $(document).width();
			this.docH = $(document).height();
			this.winW = $(window).width();
			this.winH = $(window).height();
			var title = "";
			var html_body = "";
			var foot_btn = "";
			if (data.judgment){
				title = '<div class="tiltes mb30 mt65">' + data.title + '</div>';
				html_body = '<div class="input_div">'+
					'<input placeholder="' + bitbank.L("请输入标签")+'" type="text" class="alert_memo"/>'+
							'</div>';
				foot_btn = '<div class="btns_div">' +
								'<span class="btn close_alertBox">'+bitbank.L("暂不设置")+'</span>'+
								'<span class="btn submit">'+bitbank.L("设置标签")+'</span>'+
							'</div>';
			}
			else{
				data.memo = data.memo == "" ? "--" : data.memo;
				title = '<div class="tiltes mb40 mt35">' + bitbank.L("是否删除该地址")+'</div>';
				html_body = '<div class="alertBox_text">'+
								'<div class="mb10">'+bitbank.L("标签：")+data.memo+'</div>'+
								'<div>'+bitbank.L("地址：")+data.address+'</div>'+
							'</div>';
				foot_btn = '<div class="btns_div">' +
								'<span class="btn close_alertBox">'+bitbank.L("取消")+'</span>'+
								'<span class="btn submit">'+bitbank.L("删除")+'</span>'+
							'</div>';
			}
			var html = '<div class="alertBox">'+
							'<div class="alertBox_back"></div>'+
							'<div class="alertBox_body">'+
								title +
								html_body+
								foot_btn+
							'</div>'+
						'</div>';
			$("body").append(html);
			$(".alertBox").css({
				"width": Math.max(this.docW, this.winW),
				"min-width": "320px",
				"height": Math.max(this.docH, this.winH),
				"z-index": 100
			});
			$(".close_alertBox").on("click",function(){
				$(".alertBox").remove();
			})
			$(".submit").on("click",function(){
				if (data.judgment){  //修改标签
					var alert_memo = $.trim($(".alert_memo").val());
					if (alert_memo.length > 20 ){
						JuaBox.showWrong(bitbank.L("标签不得超过20个字符"));
						return;
					}
					$this.addressEdit({
						type: data.type,
						id: data.id,
						memo: alert_memo
					})
				}
				else{ //删除地址
					$this.addressDeleteAjax({
						type:data.type,
						id:data.id
					});
				}
			})

		}

		asset.addressEdit = function(data){  //修改标签
			$.ajax({
				url: "/manage/account/download/updateReceiveAddr/" + data.type,
				data: {
					receiveId: data.id,
					memo: data.memo
				},
				type: "post",
				dataType: "json",
				success: function (json) {
					if (json.isSuc) {
						window.location.reload();
					}
				},
				error: function (err) {
					console.log(err)
				}
			})
		}

		asset.addressDeleteAjax = function(data){  //删除地址
			$.ajax({
				url: "/manage/account/download/doDel/" + data.type,
				data:{
					receiveId: data.id
				},
				type:"post",
				dataType:"json",
				success:function(json){
					if(json.isSuc){
						window.location.reload();
					}
				},
				error:function(err){
					console.log(err)
				}
			})
		}
		
        asset.formatDownloadDetail = function(json){
        	var $this = this;
			var record = [] ;
			Big.RM = 0;
    		for(var i = 0; i<json.length; i++){
    			var id = json[i].id;
    			var submitTime = json[i].submitTime.time;
    			var amount = json[i].amount;
				var coinName = json[i].coinName;
				var coinImgName = coinName;
				var status = json[i].status;
    			var showStatus = json[i].showStatus;				
    			var confirmTime = json[i].confirmTime ? json[i].confirmTime.time : '';
    			var confirmTimes = json[i].confirmTimes;
    			var commandId = json[i].commandId;
    			var toAddress = json[i].toAddress;
				var afterAmount = json[i].afterAmount;
				var webUrl = json[i].webUrl;
				var txId = json[i].txId;
    			var totalConfirmTimes = json[i].totalConfirmTimes;
				var addressMemo = json[i].addressMemo == "" ? "--" : json[i].addressMemo || "--" ;
				var memo = json[i].memo == "" ? "--" : json[i].memo || "--";

    			record[i] = {};
    			record[i].id = id;
    			record[i].submitTime = LANG == 'en'? M.formatDate(submitTime, "MM-dd-yyyy hh:mm:ss") : M.formatDate(submitTime, "yyyy-MM-dd hh:mm:ss");
    			record[i].amount = new Big(amount).toFixed(8);
				record[i].coinName = coinName;
				record[i].coinImgName = coinImgName.toLowerCase();
				record[i].status = status;
    			record[i].showStatus = showStatus;				
    			record[i].commandId = commandId;
    			record[i].toAddress = toAddress;
				record[i].afterAmount = M.fixNumber(afterAmount, 6);			
    			if(confirmTime){
    				record[i].confirmTime = LANG == 'en'? M.formatDate(confirmTime, "MM-dd-yyyy hh:mm:ss") : M.formatDate(confirmTime, "yyyy-MM-dd hh:mm:ss");
    			}else{
    				record[i].confirmTime = "--";
				}
				record[i].recentTime = confirmTime ? record[i].confirmTime : record[i].submitTime;
				record[i].confirmTimes = confirmTimes;
				record[i].webUrl = webUrl;
				record[i].totalConfirmTimes = totalConfirmTimes;
				record[i].txId = txId;				
				record[i].addressMemo = addressMemo;
				record[i].memo = memo;
			}
    		return record ;
        };
        //loan demands
        asset.loanRecordInit = function(type){
        	var $this = this ;
        	var isPage      =    false,
    		url			= "/manage/loan/getLoanRecordList",
    		pageIndex   =    1,
    		pageSize    =    10,
    		listDiv     =    "#loanDetail";
        	var curreny = type; 
			var listFind =  0 ;
			$(".modal-select").on('change',function(){
				curreny = $(this).val();
        	    $this.getLoanRecordDetail(listDiv, curreny,url, pageIndex, pageSize);									
			});
			 $("#loDetail").slide({
				titOnClassName:"active",
				effect: "fade",
				trigger:"click",
				startFun:function(i,c){
					listDiv = i == 0? '#loanDetail' : '#loanDetailAlready';
        	    	$this.getLoanRecordDetail(listDiv, curreny,url, pageIndex, pageSize);					
				}
			 });
        };

        asset.getLoanRecordDetail = function(listDiv, type, url, pageIndex, pageSize){
        	if(!user.isLogin()) return false;
        	if(lockRequest)  return JuaBox.showWrong(bitbank.L("您有未完成的请求，请等待后重试"));
        	var $this = this;
        	var htmlNoLogin  = "<td colspan='8' class='botnone'><p>"+bitbank.L("通用未登录提示")+"</p></td>";
        	var htmlNoRecord = "<tr><td colspan='8' class='botnone'>"+bitbank.L("通用没有任何记录")+"</td></tr>";
			var stamp = listDiv == '#loanDetail'? 1 : 2;
        	if(!user.isLogin()){
        		$(listDiv).html(htmlNoLogin);
        		$(listDiv+"_Page").html("");
        		return false;
        	};
        	lockRequest = true ;
        	$.ajax({
    			url:url ,
    			type: 'POST',
    			data: {
    				pageIndex : pageIndex,
    				pageSize:pageSize,
    				coint:type,
					type:stamp
    			},
    			dataType: 'json',
    			success: function(json) {
    				lockRequest = false ;
    				if (json.isSuc) {
    					if(json.datas.list == null || json.datas.list.length == 0 ){
    	        			$(listDiv).html(htmlNoRecord);
    	        			if(pageIndex == 1){
            					$(listDiv+"_Page").html("");
            				}else{
              					$this.showPage(listDiv, type, pageIndex, 0, pageSize);								
            				}
    	    			}else{
    	    				$(listDiv).html(tmpl("tmpl-"+(listDiv.replace(/[#,.]/g,"")),$this.formatLoanDetail(json.datas.list)));
                  			$this.showPage(listDiv, type, pageIndex, json.datas.totalCount, pageSize);    	    			
						}
    				} else {
    					JuaBox.sure(json.des);
    				}
    			},
    			error: function() {
    				lockRequest = false ;
    				JuaBox.sure(bitbank.L('网络访问出错，请稍后重试。'));
    			}
    		});
        };


        asset.formatLoanDetail = function(json){
        	var $this = this;
			var record = [] ;
    		for(var i = 0; i<json.length; i++){
    			var id = json[i].id;
    			var createTime = json[i].createTime;
    			var repayDate = json[i].repayDate;				
    			var amount = json[i].amount;
    			var hasRepay = json[i].hasRepay;
    			var rate = json[i].rate;
    			var hasLx = json[i].hasLx;
    			var dikouLx = json[i].dikouLx;
    			var shouldRepayBX = json[i].shouldRepayBX;
    			var lx = json[i].lx;
    			var status = json[i].status;
				var statusShow = json[i].statusShow;
    			var coinName = json[i].coinName;
				var showRepayButton = json[i].showRepayButton;
                var coinLowerName = json[i].coinName.toLowerCase();
				var allAmount = M.floorNumber(amount+hasRepay,3);	
				var hasBidRate =hasRepay/allAmount*100;
                
    			record[i] = {};
    			record[i].id = id;
    			record[i].createTime = LANG == 'en'? M.formatDate(createTime, "MM-dd-yyyy hh:mm:ss") : M.formatDate(createTime, "yyyy-MM-dd hh:mm:ss");				
    			record[i].repayDate = LANG == 'en'? M.formatDate(repayDate, "MM-dd-yyyy hh:mm:ss") : M.formatDate(repayDate, "yyyy-MM-dd hh:mm:ss");				    			
				record[i].amount = amount;
    			record[i].hasRepay = hasRepay;
    			record[i].rate = M.fixDecimal(rate*100-0.0049,2);
    			record[i].hasLx = hasLx;
    			record[i].dikouLx = dikouLx;
    			record[i].shouldRepayBX = shouldRepayBX;
    			record[i].lx = lx;
    			record[i].coinName = coinName;
    			record[i].coinLowerName = coinLowerName;				
    			record[i].status = status;
				record[i].showRepayButton = showRepayButton;
				record[i].statusClass = status == 2 ? 'loan-green':'loan-red';
				record[i].statusShow = statusShow;
				record[i].total = M.floorNumber(hasRepay + hasLx, 6);
    			record[i].hasBidRate = M.floorNumber(hasBidRate,0);	
				record[i].allAmount = allAmount;	
    		}
    		return record ;
        };
         
		//loan offers
        asset.loanOutRecordInit = function(type){
        	var $this = this ;
        	var isPage      =    false,
    		url			= "/manage/loan/getMyLoanOut",
    		pageIndex   =    1,
    		pageSize    =    10,
    		listDiv     =    "#loanOutDetail";
        	var curreny = type; 
			var listFind =  0 ;
			$(".modal-select").on('change',function(){
				curreny = $(this).val();
        	    $this.getLoanOutRecordDetail(listDiv, curreny,url, pageIndex, pageSize);									
			});
			 $("#loDetail").slide({
				titOnClassName:"active",
				effect: "fade",
				trigger:"click",
				startFun:function(i,c){	
					listDiv = i == 0? '#loanOutDetail' : '#loanOutDetailAlready';
        	    	$this.getLoanOutRecordDetail(listDiv, curreny,url, pageIndex, pageSize);					
				}
			 });
        };

		asset.getLoanOutRecordDetail = function(listDiv, type, url, pageIndex, pageSize){
        	if(!user.isLogin()) return false;
        	if(lockRequest)  return JuaBox.showWrong(bitbank.L("您有未完成的请求，请等待后重试"));
        	var $this = this;
        	var htmlNoLogin  = "<td colspan='8' class='botnone'><p>"+bitbank.L("通用未登录提示")+"</p></td>";
        	var htmlNoRecord = listDiv =='#loanOutDetail' ? "<tr><td colspan='8' class='botnone'>"+bitbank.L("通用没有任何记录")+"</td></tr>":"<tr><td colspan='9' class='botnone'>"+bitbank.L("通用没有任何记录")+"</td></tr>";
			var stamp = listDiv == '#loanOutDetail'? 1 : 2;
        	if(!user.isLogin()){
        		$(listDiv).html(htmlNoLogin);
        		$(listDiv+"_Page").html("");
        		return false;
        	};
        	lockRequest = true ;
        	$.ajax({
    			url:url ,
    			type: 'POST',
    			data: {
    				pageIndex : pageIndex,
    				pageSize:pageSize,
    				coint:type,
					type:stamp
    			},
    			dataType: 'json',
    			success: function(json) {
    				lockRequest = false ;
    				if (json.isSuc) {
    					if(json.datas.data.loanOutList == null || json.datas.data.loanOutList.length == 0 ){
    	        			$(listDiv).html(htmlNoRecord);
    	        			if(pageIndex == 1){
            					$(listDiv+"_Page").html("");
            				}else{
              					$this.showPage(listDiv, type, pageIndex, 0, pageSize);								
            				}
    	    			}else{
    	    				$(listDiv).html(tmpl("tmpl-"+(listDiv.replace(/[#,.]/g,"")),$this.formatLoanOutDetail(json.datas.data.loanOutList)));
							$this.showPage(listDiv, type, pageIndex, json.datas.totalCount, pageSize);    	    			
						}
    				} else {
    					JuaBox.sure(json.des);
    				}
    			},
    			error: function() {
    				lockRequest = false ;
    				JuaBox.sure(bitbank.L('网络访问出错，请稍后重试。'));
    			}
    		});
        };
		asset.formatLoanOutDetail = function(json){
        	var $this = this;
			var record = [] ;
    		for(var i = 0; i<json.length; i++){
				var id = json[i][0];
				var coinName = json[i][1];
				var createTime = json[i][2];
				var recoverTime = json[i][3];
				var amount = json[i][6];
				var lendAmount = json[i][7];
				var recoverAmount = json[i][8];
				var income = json[i][9];
                var lendRange = parseInt(lendAmount/amount*100);
				var recoverRange = parseInt(recoverAmount/amount*100);
				var rate = json[i][10];
				var status = json[i][11];
				var coinLowerName = coinName.toUpperCase();
                
				record[i] = {};
				record[i].id = id;
				record[i].coinName = coinName;
				record[i].createTime = LANG == 'en'? M.formatDate(createTime, "MM-dd-yyyy hh:mm:ss") : M.formatDate(createTime, "yyyy-MM-dd hh:mm:ss");				
				record[i].recoverTime = LANG == 'en'? M.formatDate(recoverTime, "MM-dd-yyyy hh:mm:ss") : M.formatDate(recoverTime, "yyyy-MM-dd hh:mm:ss");								
				record[i].amount = amount;
				record[i].lendAmount = lendAmount;
				record[i].recoverAmount = recoverAmount;
				record[i].income = M.floorNumber(income,7);
				record[i].lendRange = lendRange;
				record[i].recoverRange = recoverRange;	
				record[i].rate = M.fixDecimal(rate*100,6);
				record[i].status = status;
				record[i].coinLowerName = coinLowerName;							
    		}		
    		return record ;
        };
		//  history paper  record
		asset.cointList = function(){
			$.ajax({
				url:"/manage/account/chargeDownHistory/getCoins",
				type:"GET",
				dataType:"json",
				success:function(data){
					var htmlT = "<dd data-val=''>"+bitbank.L("全部")+"</dd>"
					if(data.isSuc){
						var result = data.datas;
						for(var k=0; k<result.length; k++){
							htmlT += "<dd data-val="+result[k]+">"+result[k]+"</dd>";
						}
						$("#cointSelect").html(htmlT);
					}else{
    					JuaBox.sure(data.des);						
					}
				}
			})
		}
		asset.depositRecordInit = function(type){
        	var $this = this ;
        	var isPage      =    false,
        		url			= "/manage/account/chargeDownHistory/getChargeRecordList",
        		pageIndex   =    1,
        		pageSize    =    10,
				listDiv     =    "#chargeRecordDetailH",
				type = type || "";
           	$this.getHistoryRecord(listDiv, type, url,pageIndex, pageSize);
		};
		asset.distriButionHistory = function(type){
			var $this = this ;
        	var isPage      =    false,
        		url			= "/manage/queryUserDistribution",
        		pageIndex   =    1,
        		pageSize    =    30,
				listDiv     =    "#butionHistory",
				type = type || "";
           	$this.getButionHistory(listDiv, type, url,pageIndex, pageSize);
		}

        asset.withdrawRecordInit = function(type){
        		var $this = this ;
        		var isPage      =    false,
        			url			= "/manage/account/chargeDownHistory/getDownloadRecordList",
        			pageIndex   =    1,
        			pageSize    =    10,
					listDiv     =    "#downloadRecordDetailH",
					type = type || "";
					
       	 	$this.getHistoryRecord(listDiv, type, url,pageIndex, pageSize);
        };

        asset.getHistoryRecord = function(listDiv, type, url, pageIndex, pageSize){
        	if(!user.isLogin() || user.needAuth()) return false;
        	var $this = this ;
        	var htmlNoLogin  = "<tr><td colspan='7'>"+bitbank.L("通用未登录提示")+"</td></tr>";
        	var htmlNoRecord = "<tr><td colspan='7' class='billDetail_no_list'>"+bitbank.L("通用没有充值提现记录")+"</td></tr>";
			var loadingHtml = "<tr><td colspan='7'><div class='loading'></div></td></tr>";
			$(listDiv).html(loadingHtml);
			if(!user.isLogin()){
        		$(listDiv).html(htmlNoLogin);
        		$(listDiv+"_Page").html("");
        		return false;
        	};
        	$.ajax({
    			url:url,
    			type: 'POST',
    			data: {
    				pageIndex : pageIndex,
    				pageSize:pageSize,
    				coint:type
    			},
    			dataType: 'json',
    			success: function(json) {
    				if (json.isSuc){
    					if(json.datas.list == null || json.datas.list.length == 0 ){
    	        			$(listDiv).html(htmlNoRecord);
    	        			if(pageIndex == 1){
            					$(listDiv+"_Page").html("");
            				}else{
              					$this.showPage(listDiv, type, pageIndex, 0, pageSize);
            				}
    	    			}else{
    	    				$(listDiv).html(tmpl("tmpl-"+(listDiv.replace(/[#,.]/g,"")), $this.formatHistoryDetail(json.datas.list)));
                  			$this.showPage(listDiv, type,pageIndex, json.datas.totalCount, pageSize);
    	    			}
    				}else{
    					JuaBox.sure(json.des);
    				}
    			},
    			error: function() {
    				JuaBox.sure(bitbank.L('网络访问出错，请稍后重试。'));
    			}
    		});
		};
		asset.getButionHistory = function(listDiv, type, url, pageIndex, pageSize){
        	if(!user.isLogin() || user.needAuth()) return false;
        	var $this = this ;
        	var htmlNoLogin  = "<tr><td colspan='5'>"+bitbank.L("通用未登录提示")+"</td></tr>";
        	var htmlNoRecord = "<tr><td colspan='5' class='billDetail_no_list'>"+bitbank.L("当前没有分发记录")+"</td></tr>";
			var loadingHtml = "<tr><td colspan='5'><div class='loading'></div></td></tr>";
			$(listDiv).html(loadingHtml);
			if(!user.isLogin()){
        		$(listDiv).html(htmlNoLogin);
				$(listDiv+"_Page").html("");
        		return false;
			};
        	$.ajax({
    			url:url,
    			type: 'POST',
    			data: {
    				pageIndex : pageIndex,
    				pageSize:pageSize,
    			},
    			dataType: 'json',
    			success: function(json) {
    				if (json.isSuc){
    					if(json.datas.list == null || json.datas.list.length == 0 ){
    	        			$(listDiv).html(htmlNoRecord);
    	        			if(pageIndex == 1){
								$(listDiv+"_Page").html("");
            				}else{
              					$this.showPage(listDiv, type, pageIndex, 0, pageSize);
            				}
    	    			}else{
    	    				$(listDiv).html(tmpl("tmpl-"+(listDiv.replace(/[#,.]/g,"")), $this.formatButionDetail(json.datas.list)));
                  			$this.showPage(listDiv, type,pageIndex, json.datas.totalCount, pageSize);
    	    			}
    				}else{
    					JuaBox.sure(json.des);
    				}
    			},
    			error: function() {
    				JuaBox.sure(bitbank.L('网络访问出错，请稍后重试。'));
    			}
    		});
		};
		asset.formatButionDetail = function(json){
			var $this = this;
			var record = [] ;
			Big.RM = 0;
    		for(var i = 0; i<json.length; i++){
    			var id = json[i].id;
    			var sendTime = json[i].sendTime;
				 var typeView = json[i].typeView;
				 var coinView = json[i].coinView;
				 var amount = json[i].amount;
				 var sourceRemark = json[i].sourceRemark;
				 var amountll = '';
				
    			record[i] = {};
    			record[i].id = id;
    			record[i].sendTime = M.formatDate(sendTime, "yyyy-MM-dd hh:mm:ss");
				 record[i].typeView = typeView;
				 record[i].coinView = coinView;
				record[i].amount = String(amount.toFixed(9)).substring(0, String(amount.toFixed(9)).length-1);
				 console.log(record[i].amount);
				 record[i].sourceRemark = sourceRemark;
			}
			return record;
		};
        asset.formatHistoryDetail = function(json){
        	var $this = this;
			var record = [] ;
			Big.RM = 0;
    		for(var i = 0; i<json.length; i++){
    			var id = json[i].id;
    			var submitTime = json[i].submitTime.time;
    			var amount = json[i].amount;
				var coinName = json[i].coinName;
				var coinImgName = coinName;
    			var showStatus = json[i].showStatus;				
    			var status = json[i].status;
    			var confirmTime = json[i].confirmTime ? json[i].confirmTime.time : '';
				var confirmTimes = json[i].confirmTimes;
				var totalConfirmTimes = json[i].totalConfirmTimes;
    			var commandId = json[i].commandId;
    			var toAddress = json[i].toAddress;
				var afterAmount = json[i].afterAmount;
				var webUrl = json[i].webUrl;
				var txId = json[i].txId;
				var addressMemo = json[i].addressMemo == "" ? "--" : json[i].addressMemo || "--";
				var memo = json[i].memo == "" ? "--" : json[i].memo || "--";
				
    			record[i] = {};
    			record[i].id = id;
    			record[i].submitTime = LANG == 'en'? M.formatDate(submitTime, "MM-dd-yyyy hh:mm:ss") : M.formatDate(submitTime, "yyyy-MM-dd hh:mm:ss");
    			record[i].amount = new Big(amount).toFixed(8);
				record[i].coinName = coinName;
				record[i].coinImgName = coinImgName.toLowerCase();
    			record[i].status = status;
    			record[i].commandId = commandId;
    			record[i].toAddress = toAddress;
    			record[i].afterAmount = M.fixNumber(afterAmount, 8);
    			if(confirmTime){
    				record[i].confirmTime = LANG == 'en'? M.formatDate(confirmTime, "MM-dd-yyyy hh:mm:ss") : M.formatDate(confirmTime, "yyyy-MM-dd hh:mm:ss");
    			}else{
    				record[i].confirmTime = "--";
				}
				record[i].recentTime = confirmTime ? record[i].confirmTime : record[i].submitTime;
				record[i].confirmTimes = confirmTimes;
				record[i].webUrl = webUrl;
    			record[i].showStatus = showStatus;				
				record[i].txId = txId;	
				record[i].addressMemo = addressMemo;
				record[i].memo = memo;
				record[i].totalConfirmTimes = totalConfirmTimes;
			}
    		return record ;
       };

		asset.download_init = function(stag){  //提现页面默认函数
			var $this = this;
			$this.downloadAddressInit(stag);
			$this.downloadRecordInit(stag);
			// setTimeout(function () {
				
			// }, 100);
			$this.searchFunc({types:true});
			
		}

		asset.charge_init = function(stag){
			var $this = this;
			$this.chargeRecordInit(stag);
			// setTimeout(function () {
				
			// }, 100);
			$this.searchFunc({ types: false });
		}

	   asset.searchFunc = function(data){ //充值提现点击搜索框
			var $this = this; 
			
		   $(".search").on("click", function () {
			   $this.getUserAsset_1(data.types);
			   $("#search_warp").show();
			   $(this).find(".jiantou").addClass("active");
			   return false;
		   })
		   $(".input_warp").on("click",function () {
			   return false;
		   })
		   $(document).on("click",function () { 
			   if ($("#search_warp").is(":hidden")){
			   }
			   else{
				   $("#search_warp").hide();
				   $(this).find(".jiantou").removeClass("active");
			   }
		    })
		   
			$("#checkcoin").on("keyup", function () {
				$this.filterVal = $.trim($("#checkcoin").val()).toUpperCase();
				if ($this.filterVal) {
					$("#btnCheckCoin").addClass("icon-guanbiguanggao")
				} else {
					$("#btnCheckCoin").removeClass("icon-guanbiguanggao")
				}
				$this.getUserAsset_1();
			})
			$("#btnCheckCoin").on("click", function () {
				$(this).removeClass("icon-guanbiguanggao");
				$("#checkcoin").val("");
				$this.filterVal = "";
				$this.getUserAsset_1();
			})
		}

		asset.getUserAsset_1 = function (types,callback){ //提现/充值列表
        	if(!user.isLogin() || user.needAuth()) return false;
        	var $this = this ;
        	$.getJSON(DOMAIN_VIP + "/manage/getAssetsDetail?callback=?", function(result) {
				$this.download_list(result, callback, types);
        	});
        };

		asset.download_list = function(result,callback,types){
			var $this = this;
			for (var key in result) {
				var funds = result[key];
				allCurrency[key.toLowerCase()] = key;
				var decimalNum = 6;
				asset[key.toLowerCase()] = {
					total: funds.balance + funds.freeze,
					usable: funds.balance,
					freeze: funds.freeze,
					loan: 0,
					canLoanIn: 0,
					decimal: decimalNum
				};
			}
			$this.setAssetSel();
			var listDiv = "#btc_list";
			var json_result = $this.formatFundsDetail(result);
			if (types == 1){
				json_result.sort(function (a, b) {  //排序
					return b.balance6 - a.balance6
				});
			}
			$(listDiv).html(tmpl("tmpl-" + (listDiv.replace(/[#,.]/g, "")), json_result ));
			wheel.wheeler("#btc_list"); //出滚动条

			if ($.isFunction(callback)) {
				callback();
			};
		}

        module.exports = asset;
});