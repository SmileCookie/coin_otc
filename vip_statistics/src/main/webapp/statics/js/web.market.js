define(function (require, exports, module) {
	"require:nomunge,exports:nomunge,module:nomunge";
	var tmpl_s = require("module_tmpl");
	var M = require("module_method");
	var market = {};
	market.allMarkets = {};
	market.priceObj = {};
	market.rangeObj = {};
	market.clickNum = 0;
	market.searchName = '';
	market.thIndex = 0;
	market.sortName = '';
	market.exChangeBtc = 0;
	market.exChangeUsdt = 0;
	market.DOMAIN_BASE = "." + DOMAIN_BASE;
	market.silidmove = true;
	var $title = window.top.document.title;
	market.init = function (t) {
		var $this = this;

		// var readyFun = setInterval(function () {
		// 	if (webSocket) {
		// 		webSocket.init(function () {
		// 			webSocket.sendMessage("{'event':'addChannel','channel':'top_all','isZip':'true'}");
		// 		});
		// 		clearInterval(readyFun);
		// 	}
		// }, 50);

		$this.getAllMarket(function () {
			$this.setTradeMarketSel();
			$this.getMarket();
		});

		 setInterval(function (t) {
			// if (ajaxRun == false) {
             // 	return;
             // }
			// $this.getMarket(function () { });
			 $this.getMarket(function () {
				 
             })
		 }, 1000);
		$this.getExchangeRate();
	};

	market.getMarketSocket = function (json, callback) {
		var $this = this;
		$this.setTopAll(json, callback);
	};

	market.getMarket = function (callback) {
        //console.log(browser.versions.iPhone)
		// console.log("topall web.market.js")
		var $this = this;
		$.getJSON(DOMAIN_TRANS + "/line/topall?jsoncallback=?", function (json) {
			$this.setTopAll(json, callback);
		});
		
	};
	//币种过滤
	market.filterRecord = function(data){
		var newData = data;
		var filterVal = market.searchName;
        // const {filterVal,isHideZerobalance} = this.state
        var newfilterVal = filterVal.toUpperCase()
        if(filterVal){
            var nameArr = data.filter(function(element, index, array){
                return element.propTag.indexOf(newfilterVal) !== -1
            })
            newData = nameArr;
        }
        // if(isHideZerobalance){
        //     newData = newData.filter(function(element, index, array){
        //         return element.total6 > 0;
        //     })
        // }
        return newData;
	}
	market.getExchangeRate = function(){
		//获取汇率
		$.ajax({
			type: "GET",
			url: DOMAIN_TRANS + "/getExchangeRate",
			dataType: 'json',
			async: false,
			success: function (json) {
				if (json.isSuc) {
					var resUSD = json.datas.exchangeRateUSD;
					for (var k in resUSD) {
						if ($.cookie("currency") == k) {
							market.exChangeUsdt = resUSD[k];
						}
					}
					var resBTC = json.datas.exchangeRateBTC;
					for (var s in resBTC) {
						if ($.cookie("currency") == s) {
							market.exChangeBtc = resBTC[s];
						}
					}
				}
			}
		});
	};

	market.getAllMarket = function (callback) {
		var $this = this;
		$.getJSON(DOMAIN_TRANS + "/getAllMarket?jsoncallback=?", function (json) {
			var markets = json.datas;
			for (var name in markets) {
				var m = markets[name];
				// if(name.indexOf("etc") > -1 || name.indexOf("dash") > -1 || name.indexOf("zec") > -1){
				// }else{
				$this.allMarkets[name] = m;
				// }
			}
			if (typeof callback == "function") {
				callback();
			}
		});
	};

	market.setTopAll = function (json, callback) {
		var $this = this;
		var obj = {};
		for (var name in json[0]) {
			var hotdata = json[0][name];
			if (hotdata instanceof Array) {
				var key = name.split("_")[0] + name.split("_")[1] + "Market";
				market[key] = hotdata;
			}
			// if(name.indexOf("etc") > -1 || name.indexOf("dash") > -1 || name.indexOf("zec") > -1){

			// }else{
			obj[name] = hotdata;
			// }
		}
		if (location.href.indexOf("trade") != -1) {
			$this.upTradeMarket(obj);
		} else if (location.href.indexOf("manage") != -1 || location.href.indexOf("promotion") != -1) {
			$this.upCurrencyPrice(obj);
		} else {
			$this.upHomeMarket(obj);
		}

		if (typeof callback == "function") {
			callback();
		}

	};

	market.upTradeMarket = function (result) {
		var $this = this;
		var listDiv = "#topAllMarket";
		var tagTab = $("#tagTab .active").text();
		if (document.getElementById("topAllMarket") == null) {
			return;
		}
		var currentMoney = $("#currentMoney").val();
		var record = $this.formatTopAll(result, tagTab);
		$(listDiv).html(tmpl("tmpl-" + (listDiv.replace(/[#,.]/g, "")), record));
		// for(var i =0;i<record.length;i++){
		// 	$.plot($("#"+record[i].market+"_plot"), [{shadowSize:2, data:record[i].trendPrice}],  { grid: { borderWidth: 0}, xaxis: { mode: "time", ticks: false}, yaxis : {tickDecimals: 0, ticks: false}});
		// }
	};

	market.upCurrencyPrice = function (result) {
		var $this = this;
		var listDiv = "#trendMarket";
		if (document.getElementById("trendMarket") == null) {
			return;
		}
		var record = $this.formatTopAll(result);
		$(listDiv).html(tmpl("tmpl-" + (listDiv.replace(/[#,.]/g, "")), record));
	}

	market.formatTopAll = function (result, moneyType) {
		var $this = this;
		var marketCookie = [];
		Big.RM = 0;
		if ($.cookie("userCollectMarket")) {
			marketCookie = $.cookie("userCollectMarket").split("-");
		}
		var record = [], record2 = [];
		var i = 0;
		for (var key in result) {
			var keyName = key;
			var keyColor = 0;
			var exchangePrice = 0;
			// 判断收藏cookie
			for (var j = 0; j < marketCookie.length; j++) {
				marketCookie[j] = marketCookie[j].replace('"', '');
				marketCookie[j] = marketCookie[j].replace('+', ' ');
				if (marketCookie[j] == keyName) {
					keyColor = 1;
				}
			}
			var hotdata = result[key];
			var tags_type = key.split("_")[0] + "_" + key.split("_")[1];
			var propTag = key.split("_")[0].toUpperCase();
			var stag = key.split("_")[0];
			var moneyTag = key.split("_")[1].toUpperCase();
			var market = key.split("_")[0] + "_" + key.split("_")[1];
			var desc = key.split("_")[3].replace('+', ' ');
			if (moneyType && moneyType != "") {
				if (moneyType != moneyTag) continue;
			}
			if (hotdata instanceof Array) {
				var price = hotdata[0];
				var dayVolume = hotdata[9] || 0; //24小时总成交金额
				var priceBtc = M.decimal(dayVolume);
				var priceTotal = M.numFm(dayVolume);
				var volume = hotdata[5];//成交量
				var last24minPrice = hotdata[6];//过去24小时最低价格
				var lastminPrice = hotdata[3]||0;
				var lastmorePrice = hotdata[4]||0;
				var range = 0;//范围
				var rangeOf24h = 0;
				var unitArry = key.split("_");
				var thisUnit = unitArry[0] + "_" + unitArry[1];
				var unit = $this.allMarkets[thisUnit].exchangeBixDian;
				var trendPrice = [];//价格趋势
				if (hotdata.length > 6) {
					trendPrice = hotdata[7];
				}
				if (hotdata.length > 7) {
					rangeOf24h = hotdata[8];
				}
				
				
				record[i] = {};
				record[i].propTag = propTag;
				$this.propTag = propTag;
				record[i].stag = stag;
				
				record[i].price = M.fixNumber(price, unit);
				record[i].priceBtc = priceBtc;
				record[i].priceTotal2 = dayVolume;
				record[i].priceTotal = priceTotal;
				if ($this['priceObj'][key]) {
					range = M.subtract(price, $this['priceObj'][key]);
				}
				$this['priceObj'][key] = price;
				if (range == 0) {
					record[i].range = $this['rangeObj'][key] || '0.00';
				} else {
					range = M.divide(range, $this['priceObj'][key]);
					range = M.multiply(range, 100);
					record[i].range = M.fixNumber(range, 2);
					$this['rangeObj'][key] = record[i].range;
				}
				record[i].volume = M.fixNumber(volume, 2);
				record[i].rangeOf24h = M.fixNumber(rangeOf24h, 2);
				record[i].market = market;
				record[i].moneyTag = moneyTag;
				record[i].symbol = key.split("_")[0].toUpperCase() + "/" + key.split("_")[1].toUpperCase();
				record[i].symbol_btc = key.split("_")[1].toUpperCase();
				record[i].trendPrice = trendPrice;
				record[i].desc = desc;
				record[i].lastminPrice = new Big(lastminPrice).toFixed(unit) ;
				record[i].lastmorePrice = new Big(lastmorePrice).toFixed(unit);
				record[i].keyName = keyName;
				record[i].keyColor = keyColor;
				var new_price = new Big(record[i].price);
				var new_exChangeUsdt = new Big($this.exChangeUsdt);  //usdt汇率
				var new_exChangeBtc = new Big($this.exChangeBtc); // btc汇率
				record[i].exchangePrice = new_price.times(new_exChangeUsdt).toFixed(2);
				record[i].exChangePriceBtc = new_price.times(new_exChangeBtc).toFixed(2);
				i++;
			}
		}
		record2 = $this.sortRecord(record);
		record2 = $this.filterRecord(record2)
		return record2;
	};
	market.upHomeMarket = function (result) {
		
		var $this = this;
		var listDiv = "#homeMarket";
		var listDiv_1 = "#homeMarket_1";
		var listDiv_2 = "#homeMarket_2";
		if (document.getElementById("homeMarket") == null) {
			return;
		}
		var record = $this.formatTopAll(result, "USDT");

		var record_BTC = $this.formatTopAll(result, "BTC");
		var record_All = $this.formatTopAll(result, "");

		var record_Favorit = [], record_Favorit1 = [];
		for (var ket in record_All) {
			if (record_All[ket].keyColor == 1) {
				record_Favorit.push(record_All[ket]);
			}
		}
		record_Favorit1 = $this.sortRecord(record_Favorit);

		$(listDiv_1).html(tmpl("tmpl-" + (listDiv_1.replace(/[#,.]/g, "")), record_BTC));
		$(listDiv).html(tmpl("tmpl-" + (listDiv.replace(/[#,.]/g, "")), record));
		html='<div class="none_favorit"><p><i class="iconfont icon-tongchang-tishi"></i>'+ bitbank.L("收藏列表为空")+'</p></div>';
		if(record_Favorit.length==0){
			$(listDiv_2).html(html);
		}else{
			$(listDiv_2).html(tmpl("tmpl-" + (listDiv_2.replace(/[#,.]/g, "")), record_Favorit));
		}
		//$(listDiv_2).html(tmpl("tmpl-" + (listDiv_2.replace(/[#,.]/g, "")), record_Favorit));
		$this.storageCookie();
		windowFunds();
		function windowFunds() {
			var coinUnit = $.cookie("currency") || "USD";
			$('.cerrencyUnit').html(moneySymbol(coinUnit));
		};

		function moneySymbol(unit) {
			var iconMon = "$";
			var enSpace = LANG == "en" ? "&nbsp;" : "";
			if (unit == "USD") {
				iconMon = enSpace + "$";
			} else if (unit == "CNY") {
				iconMon = enSpace + "¥";
			} else if (unit == "EUR") {
				iconMon = enSpace + "€";
			} else if (unit == "GBP") {
				iconMon = enSpace + "£";
			} else if (unit == "AUD") {
				iconMon = enSpace + "A$";
			}
			return iconMon;
		}
	};

	market.setTradeMarketSel = function () {
		var $this = this;
		if (document.getElementById("tradeMarketSel") == null) {
			return;
		}
		var pageMarket = $("#market").val();
		var options = "";
		for (var key in $this.allMarkets) {
			var market = key;
			var symbol = $this.allMarkets[key].numberBi + "/" + $this.allMarkets[key].exchangeBi;
			if (pageMarket == market) {
			} else {
				options += "<option value=\"" + market + "\">" + symbol + "</option>";
				options += "<option valu e=\"" + market + "\" selected>" + symbol + "</option>";
			}
		}
		$("#tradeMarketSel").html(options);
		$("#tradeMarketSel").on("change", function () {
			window.location.href = "/entrust/" + this.value;
		});
	};

	market.storageCookie = function () {
		//点击收藏事件
		var $this = this;
		$(".gray").on("click", function () {
			var CollectMarket = $.cookie("userCollectMarket");
			var coinNname = $(this).attr("id");
			coinNname = coinNname.replace(/ /g, '+');
			var coinCookie = [];
			var coinState = true;
			if (!!CollectMarket){
				coinCookie = CollectMarket.split("-");
				if (coinCookie.length > 0) {
					for (var i = 0; i < coinCookie.length; i++) {
						coinCookie[i] = coinCookie[i].replace('"', "");
						if (coinCookie[i] == coinNname) {
							coinState = false;
						}
					}
				}
			}
			if (coinState) {
				$.ajax({
					type: "get",
					url: DOMAIN_VIP + "/manage/userCollect?" + "market=" + coinNname,
					dataType: "JSON",
					success: function (data) {
					}
				});
				CollectMarket = CollectMarket ? CollectMarket : "";
				CollectMarket = CollectMarket == '""' ? "" : CollectMarket;
				var cookie_new = '"' + CollectMarket.replace(/"/g,'') + "-" + coinNname + '"';
				$.cookie('userCollectMarket', cookie_new, { expires: 365, path: "/", domain: $this.DOMAIN_BASE, raw: true});
				$(this).addClass("yellow").removeClass("gray")
				market.getMarket();
			}
			return false;
		});
		$(".yellow").on("click", function () {
            var coinCookie = $.cookie("userCollectMarket").replace(/"/g,'');
			var coinNname = $(this).attr("id");
			coinNname = coinNname.replace(/ /g, '+');
			var coinArray = [];
			var deleteCookie = "";
			coinArray = coinCookie.split("-");
			for (i = 0; i < coinArray.length; i++) {
				if (coinArray[i] == coinNname) {
					coinState = 1;
					coinArray.splice(i,1);
				}
			}
			$.ajax({
				type: "get",
				url: DOMAIN_VIP + "/manage/closeCollect?" + "market=" + coinNname,
				dataType: "JSON",
				success: function (data) {
				}
			});
			deleteCookie = coinArray.join("-");
			$.cookie('userCollectMarket', deleteCookie, { expires: 365, path: "/", domain: $this.DOMAIN_BASE, raw: true  });
			$(this).addClass("gray").removeClass("yellow");
			market.getMarket();
			return false;
			
		})


			//表格点击事件
		$(".market_show").find("tr").on("click", function () {
			var marketId = $(this).attr("id");
			var parentMark = $(this).parent().attr("id");
			var marketShow;
			if(parentMark == 'homeMarket_2'){
                marketShow = 'store'
			}else if(parentMark == 'homeMarket'){
                marketShow = 'usdt'
			}else{
                marketShow = 'btc'
			}
            var browser = {
                versions:function() {
                    var u = navigator.userAgent, app = navigator.appVersion;
                    return {//移动终端浏览器版本信息
                        trident : u.indexOf('Trident') > -1, //IE内核
                        presto : u.indexOf('Presto') > -1, //opera内核
                        webKit : u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
                        gecko : u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1, //火狐内核
                        mobile : !! u.match(/AppleWebKit.*Mobile.*/) || !! u.match(/AppleWebKit/) && u.indexOf('QIHU') && u.indexOf('QIHU') > -1 && u.indexOf('Chrome') < 0, //是否为移动终端
                        ios : !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/),
                        android : u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或者uc浏览器
                        iPhone : u.indexOf('iPhone') > -1 || u.indexOf('Mac') > -1, //是否为iPhone或者QQHD浏览器
                        iPad: u.indexOf('iPad') > -1, //是否iPad
                        webApp : u.indexOf('Safari') == -1,//是否web应该程序，没有头部与底部
                        google:u.indexOf('Chrome')>-1
                    };
                }(),
                language : (navigator.browserLanguage || navigator.language).toLowerCase()
            };
            //console.log(browser.versions.mobile)
			if(/Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent)){
                if(browser.versions.android){
                    window.location.href = '/downApp_And'
                }
                if(browser.versions.iPhone){
                    window.location.href = '/downApp_ios'
                }
			}else{
                    window.open('/bw/trade/' + marketId + '?market=' + marketShow );
                }
            // if(browser.versions.android){
            //     window.location.href = '/downApp_And'
            // }
            // if(browser.versions.iPhone){
            //     window.location.href = '/downApp_ios'
            // }else{
            //     window.open('/v2/trade/' + marketId + '');
            // }
		});
	}
	market.upTopMarket = function () {
		var $this = this;
		var O_btcMarket = ($("#T_btcLastPrice").text() || "0").replace(/[￥,฿,-]/g, "") || "0",
			O_ethMarket = ($("#T_ethLastPrice").text() || "0").replace(/[￥,฿,-]/g, "") || "0",
			O_ethbtcMarket = ($("#T_ethbtcLastPrice").text() || "0").replace(/[￥,฿,-]/g, "") || "0",
			O_etcMarket = ($("#T_etcLastPrice").text() || "0").replace(/[￥,฿,-]/g, "") || "0",
			O_ltcMarket = ($("#T_ltcLastPrice").text() || "0").replace(/[￥,฿,-]/g, "") || "0";
		if ($this.btcMarket[6] != "equal" || parseFloat(O_btcMarket) == 0) {
			var btcClass = $this.btcMarket[6] == "up" ? "text-primary" : "text-second";
			$("#T_btcLastPrice").removeClass().addClass(btcClass).html("￥" + $this.btcMarket[0] + "<i class='fa fa-arrow-" + $this.btcMarket[6] + " fa-fw'></i>");
		}
		if ($this.ltcMarket[6] != "equal" || parseFloat(O_ltcMarket) == 0) {
			var ltcClass = $this.ltcMarket[6] == "up" ? "text-primary" : "text-second";
			$("#T_ltcLastPrice").removeClass().addClass(ltcClass).html("￥" + $this.ltcMarket[0] + "<i class='fa fa-arrow-" + $this.ltcMarket[6] + " fa-fw'></i>");
		}
		if ($this.ethMarket[6] != "equal" || parseFloat(O_ethMarket) == 0) {
			var ethClass = $this.ethMarket[6] == "up" ? "text-primary" : "text-second";
			$("#T_ethLastPrice").removeClass().addClass(ethClass).html("￥" + $this.ethMarket[0] + "<i class='fa fa-arrow-" + $this.ethMarket[6] + " fa-fw'></i>");
		}
		if ($this.etcMarket[6] != "equal" || parseFloat(O_etcMarket) == 0) {
			var etcClass = $this.etcMarket[6] == "up" ? "text-primary" : "text-second";
			$("#T_etcLastPrice").removeClass().addClass(etcClass).html("￥" + $this.etcMarket[0] + "<i class='fa fa-arrow-" + $this.etcMarket[6] + " fa-fw'></i>");
		}
		if ($this.ethbtcMarket[6] != "equal" || parseFloat(O_ethbtcMarket) == 0) {
			var ethbtcClass = $this.ethbtcMarket[6] == "up" ? "text-primary" : "text-second";
			$("#T_ethbtcLastPrice").removeClass().addClass(ethbtcClass).html("฿" + $this.ethbtcMarket[0] + "<i class='fa fa-arrow-" + $this.ethbtcMarket[6] + " fa-fw'></i>");
		}
		if (window.top.location.pathname.indexOf("ltc") != -1) {
			window.top.document.title = "LTC:￥" + $this.ltcMarket[0] + "-" + $title;
		} else if (window.top.location.pathname.indexOf("etc") != -1) {
			window.top.document.title = "ETC:￥" + $this.etcMarket[0] + "-" + $title;
		} else if (window.top.location.pathname.indexOf("ethbtc") != -1) {
			window.top.document.title = "ETH:฿" + $this.ethbtcMarket[0] + "-" + $title;
		} else if (window.top.location.pathname.indexOf("eth") != -1) {
			window.top.document.title = "ETH:￥" + $this.ethMarket[0] + "-" + $title;
		} else if (window.top.location.pathname.indexOf("btq") != -1) {
			window.top.document.title = "BTQ:￥" + $this.btqMarket[0] + "-" + $title;
		} else {
			window.top.document.title = "BTC:￥" + $this.btcMarket[0] + "-" + $title;
		}
	};


	market.sortRecord = function (data) {
		var sortData = [];
		var clickSortName = market.sortName;
		var sortABC = function (a, b) {
			return a[clickSortName] < b[clickSortName] ? -1 : 1;
		}
		var sortNumber2 = function (a, b) {
			return a[clickSortName] - b[clickSortName];
		}
		if (clickSortName && market.clickNum !== 0) {
			;
			if (market.clickNum == 1 && market.thIndex < 1) {
				sortData = data.sort(sortABC).reverse();
			} else if (market.clickNum == 2 && market.thIndex < 1) {
				sortData = data.sort(sortABC)
			} else if (market.clickNum == 2 && market.thIndex >= 1) {
				sortData = data.sort(sortNumber2);
			} else if (market.clickNum == 1 && market.thIndex >= 1) {
				sortData = data.sort(sortNumber2).reverse()
			}
			return sortData
		} else {
			return data;
		}

	};


	module.exports = market;
});