var alert_methods = [];
var alert_sent = false;

var depths = [];
var trades = [];
var last_depth_date = "";
var last_trade_date = "";
var	bFirst = true;
var depth_chart_data = [];
var time_passed = 0;
var show_range = 100;
var show_limit = 50;
var updateTimeIntervalID = -1;
var isShowingChart = false;

$(document).ready(function(){
	$("#alert_method").multiselect({
		buttonWidth : "75px",
		buttonClass: 'btn btn-default btn-sm narrow-control',
		nonSelectedText : lang.alert_method,
		onChange: function(option, checked) {
			if(checked == true){
				if($(option).html() == "QQ" && !hasQQ){
					alert(lang.qq_not_linked);
					$('#alert_method').multiselect('deselect', option.val());
				}
				if($(option).html() == "Email" && !hasEmail){
					alert(lang.email_not_linked);
					$('#alert_method').multiselect('deselect', option.val());
				}
				if($(option).html() == lang.sms){
					if(!hasPhone){
						alert(lang.phone_not_linked);
						$('#alert_method').multiselect('deselect', option.val());
					}
					else if(sms_remain_cnt <= 0){
						alert(lang.sms_not_enough);
						$('#alert_method').multiselect('deselect', option.val());
					}
				}
			}
		}
	});
	$(".multiselect").removeClass("hidden");
	
	$("#alert_high").numeric({negative: false});
	$("#alert_low").numeric({negative: false});

	updateTicker();
	//updateDepth();
	//updateTrade();
	updateTimeIntervalID = setInterval(updateTimePassed, 1000);
	$(".chart_button .btn").click(function(){
		if($(this).html() == lang.show_chart){
			$("#depth_chart_wrapper").animate({height:223},200);
			$(this).html(lang.hide_chart);
			isShowingChart = true;
		}
		else{
			$("#depth_chart_wrapper").animate({height:0},200);
			$(this).html(lang.show_chart);
			isShowingChart = false;
		}
		if(typeof(depths.bids) != "undefined" )
			processDepths(depths);
	});

	$("#chart_range button").click(function(){
		$("#chart_range button.active").removeClass("active");
		$(this).addClass("active");
		show_range = parseInt($(this).attr("data"));
		drawDepthChart();
	});

	$(".chart_button .btn").trigger("click");
});


function updateTimePassed(){
	var msg = lang.last_refresh_time;
	msg = msg.replace("[time_spent]", time_passed);
	$("#server_time").html(msg);
	time_passed++;
	if(time_passed > 180){
		clearInterval(updateTimeIntervalID);
		location.reload();
	}
}

function updateTicker(){
//	$.ajax({
//        type: "POST",
//        url: context_root + "get_tickers_by_markets",
//		data: "markets="+market_ids,
//        contentType: "application/x-www-form-urlencoded;",
//        dataType: "json",
//        beforeSend: function() {
//        },
//        error: function(data, status, errThrown){
//			setTimeout(updateTicker, 10000);
//        },
//        success: function(data){
//			if(data.result == "success"){
//				var new_tickers = data.tickers;
//				for(var i=0;i<new_tickers.length;i++){
//					processNewTicker(new_tickers[i]);
//					if(alert_started && new_tickers[i]["id"] == ticker_id)
//						checkAlert(new_tickers[i]);
//				}
//            }
//			setTimeout(updateTicker, 10000);
//	    },
//        statusCode: {
//            404: function() {
//            }
//        }
//    });
}
function processNewTicker(new_ticker){
	var old_ticker_seq = getOldTickerSequenceById(new_ticker["id"]);
	if(old_ticker_seq == -1)
		return;
	var old_ticker = tickers[old_ticker_seq];
	var currencySign = "";
	if(new_ticker["symbol"].indexOf("USD") != -1)
		currencySign = lang.sign_usd;
	else if(new_ticker["symbol"].indexOf("CNY") != -1)
		currencySign = lang.sign_cny;
	else if(new_ticker["symbol"].indexOf("BTC") != -1)
		currencySign = lang.sign_btc;
	else if(new_ticker["symbol"].indexOf("LTC") != -1)
		currencySign = lang.sign_ltc;

	if(new_ticker["id"] == ticker_id){
		updateTickValue(old_ticker["last"], new_ticker["last"], $("#fund_last"), currencySign, true);
		if(current_lang == "en"){
			$("#fund_high").html(currencySign + new_ticker["high"]);
			$("#fund_low").html(currencySign + new_ticker["low"]);
		}
		else{
			$("#fund_high").html(new_ticker["high"] + "<span class='currency_small'>"+currencySign+"</span>");
			$("#fund_low").html(new_ticker["low"] + "<span class='currency_small'>"+currencySign+"</span>");
		}
		$("#fund_volume").html(new_ticker["volume"]);
		if(current_lang == "en")
			document.title = currency_sign + new_ticker["last"] + " " + market_name + "_" + default_title;
		else
			document.title = new_ticker["last"] + currency_sign + " " + market_name + "_" + default_title;
	}
	updateTickValue(old_ticker["last"], new_ticker["last"], $("#top_ticker_"+new_ticker["id"]+" .fundamental_value"), currencySign, false);

	tickers[old_ticker_seq] = new_ticker;
}

function getOldTickerSequenceById(id){
	for(i=0;i<tickers.length;i++){
		if(tickers[i]["id"] == id)
			return i;
	}
	return -1;
}
function updateTickValue(old_val, new_val, obj, sign, show_arrow){
	if(old_val > new_val){
		html = "<span class='value_red'>";
		if(current_lang == "cn")
			html += new_val + "<span class='currency_small'>"+sign+"</span>";
		else
			html += sign + new_val;
		if(show_arrow)
			html += " <i class='icon-arrow-down'></i>";
		html += "</span>";
		$(obj).html(html);
	}
	else if(old_val < new_val){
		html = "<span class='value_green'>";
		if(current_lang == "cn")
			html += new_val + "<span class='currency_small'>"+sign+"</span>";
		else
			html += sign + new_val;
		if(show_arrow)
			html += " <i class='icon-arrow-up'></i>";
		html += "</span>";
		$(obj).html(html);
	}
	else{
		$(obj).find(".value_red").removeClass("value_red");
		$(obj).find(".value_green").removeClass("value_green");
		$(obj).find("i").remove();
	}
}


///////////////////////////////////// depth update ////////////////////////////////////////////
function changeRows(obj, rows){
	show_limit = rows;
	$("#show_limit_text").html(show_limit);
	$("#show_limit_links li.active").removeClass("active");
	$(obj).closest("li").addClass("active");
	last_depth_date = "";
	//updateDepth();
	last_trade_date = "";
	updateTrade();
}

function updateDepth(){
	$.ajax({
        type: "POST",
        url: context_root + "get_depth",
		data: "market="+market_id+"&size="+0+"&last="+last_depth_date,
        contentType: "application/x-www-form-urlencoded;",
        dataType: "json",
        beforeSend: function() {
        },
        error: function(data, status, errThrown){
			setTimeout(updateDepth, 10000);
        },
        success: function(data){
			if(data.result == "success"){
				var new_depths = data.depths;
				processDepths(new_depths);
				bFirst = false;
				last_depth_date = new_depths.update_date;
				depths = new_depths;
				drawDepthChart();
				$("#chart_depth_block .nice-scroll").getNiceScroll().resize();
				time_passed = 0;
            }
			else{
				$("#bid_block .new_depth, #ask_block .new_depth").removeClass("new_depth");
			}
			setTimeout(updateDepth, 10000);
	    },
        statusCode: {
            404: function() {
               // alert('page not found');
            }
        }
    });
}
function processDepths(new_depths){
	depth_chart_data = {bids:[], asks:[]};
	var bid_html = [];
	var sum = 0;
	var depth_show_limit = show_limit;
	if(isShowingChart)
		depth_show_limit -= 13;

	for(var i=0;i<new_depths["bids"].length;i++){
		bid = new_depths["bids"][i];
		price = bid[0];
		volume = bid[1];
		sum += volume;
		
		depth_chart_data.bids.push([price, sum]);
		if(i>=depth_show_limit)
			continue;
		sum_string = sum.toFixed(2);
		if(currency == "CNY")
			price_string = price.toFixed(2);
		else
			price_string = price.toFixed(4);
		volume_string = volume.toFixed(4);
		class_name = '';
		obj_id = ("bid_"+price).replace(".","_");
		if(!bFirst && $("#"+obj_id).length < 1){
			class_name = 'new_depth';
		}
		var light_class = "";
		if(Math.floor(price) == price)
			light_class = "light1";

		tmp = [];
		tmp.push('<tr id="'+obj_id+'" class="'+class_name+'">');
		if(light_class == "")
			tmp.push('<td class="noborder right dark1 firstcol">'+sum_string+'</td>');
		else
			tmp.push('<td class="noborder right firstcol '+light_class+'">'+sum_string+'</td>');
		tmp.push('<td class="noborder right '+light_class+'">');
		tmp.push(zeroSplitString(volume_string));
		tmp.push('</td><td class="noborder right col-light1 '+light_class+'">');
		tmp.push(zeroSplitString(price_string));
		tmp.push('</td></tr>');
		bid_html.push(tmp.join(''));

	}

	ask_html = [];
	sum = 0;
	for(var i=0;i<new_depths["asks"].length;i++){
		ask = new_depths["asks"][i];
		price = ask[0];
		volume = ask[1];
		sum += volume;
		
		depth_chart_data.asks.push([price, sum]);
		if(i>=depth_show_limit)
			continue;

		sum_string = sum.toFixed(2);
		if(currency == "CNY")
			price_string = price.toFixed(2);
		else
			price_string = price.toFixed(4);
		volume_string = volume.toFixed(4);
		class_name = '';
		obj_id = ("ask_"+price).replace(".","_");
		if(!bFirst && $("#"+obj_id).length < 1){
			class_name = 'new_depth';
		}

		var light_class = "";
		if(Math.floor(price) == price)
			light_class = "light1";

		tmp = [];
		tmp.push('<tr id="'+obj_id+'" class="'+class_name+'">')
		tmp.push('<td class="noborder left col-light1 '+light_class+'">');
		tmp.push(zeroSplitString(price_string));
		tmp.push('</td><td class="noborder right '+light_class+'">');
		tmp.push(zeroSplitString(volume_string));
		if(light_class == "")
			tmp.push('</td><td class="noborder right dark1">'+sum_string+'</td>');
		else
			tmp.push('</td><td class="noborder right '+light_class+'">'+sum_string+'</td>');
		tmp.push('</tr>');
		ask_html.push(tmp.join(''));

	}
	best_bid = new_depths["bids"][0][0];
	best_ask = new_depths["asks"][0][0];
	if(currency == "CNY"){
		//$(".best_bid span").html(best_bid.toFixed(2));
	//	$(".best_ask span").html(best_ask.toFixed(2));
	}
	else{
		//$(".best_bid span").html(best_bid.toFixed(5));
		//$(".best_ask span").html(best_ask.toFixed(5));
	}

	$("#bid_block > table > tbody").html(bid_html.join(''));
	$("#ask_block > table > tbody").html(ask_html.join(''));
}
function drawDepthChart(){
	var total_rows = depth_chart_data.bids.length < depth_chart_data.asks.length ? depth_chart_data.bids.length : depth_chart_data.asks.length; 
	show_rows = total_rows;
	if(show_range < 100)
		show_rows = Math.floor(total_rows * show_range / 100.0);
	var bids = depth_chart_data.bids.slice(0, show_rows);
	var asks = depth_chart_data.asks.slice(0, show_rows);
	var min_price = bids[bids.length-1][0];
	var max_price = asks[asks.length-1][0];
	var range = max_price - min_price;
	var	interval = 10;
	var decimals = 0;
	if(range > 10000)
		interval = 1000;
	else if(range > 1000)
		interval = 500;
	else if(range > 500)
		interval = 100;
	else if(range > 200)
		interval = 50;
	else if(range > 100)
		interval = 20;
	else if(range > 50)
		interval = 10;
	else if(range > 20){
		interval = 5;
	}else if(range > 10){
		interval = 2;
	}else if(range > 2){
		interval = 1;
	}else{
		interval = 0.2;
		decimals = 2;
	}
	
	var options = {
			lines: {
				show: true,
				lineWidth: 1,
				fill: true
			},
			points: {
				show: false
			},
			xaxis: {
				tickDecimals: decimals,
				tickSize: interval
			},
			yaxis: {
				position: "e",
			},
			grid: {
				hoverable: true,
				clickable: true
			},
			crosshair: {
				mode: "xy",
				color: "#808080"
			},
			legend: {
				backgroundColor: null,
				backgroundOpacity: 0,
				position: "ne",
				noColumns: 4,
				color: "#a0a0a0",
				margin: [100,5]
			}
			
	};
	var data = [
		{
			label: lang.bid, 
			data: bids,
			color: "#00FF00"
		},
		{ 
			label: lang.ask, 
			data: asks,
			color: "#FF6000"
		}
	];
	plot = $.plot("#depth_chart", data, options);

	legends = $("#depth_chart .legendLabel");
	legends.each(function () {
		// fix the widths so they don't jump around
		$(this).css('width', 40);
	});

//	$("#depth_chart").bind("plothover", function (event, pos, item) {
//		if (item) {
//			if (previousPoint != item.dataIndex) {
//
//				previousPoint = item.dataIndex;
//
//				$("#tooltip").remove();
//				var x = item.datapoint[0].toFixed(5),
//				y = item.datapoint[1].toFixed(5);
//
//				showTooltip(item.pageX, item.pageY,
//					item.series.label + ": " + x + "<br/>"+lang.vol+": "+y);
//			}
//		} else {
//			$("#tooltip").remove();
//			previousPoint = null;            
//		}
//	});
}
function showTooltip(x, y, contents) {
	if(x < $(window).width() - 130)
		$("<div id='tooltip'>" + contents + "</div>").css({
			position: "absolute",
			top: y + 5,
			left: x + 5,
			border: "1px solid #333",
			padding: "2px",
			"background-color": "#1f1f1f",
			color: "#fff",
			"font-size": "12px",
			opacity: 0.80
		}).appendTo("body");//.fadeIn(200);
	else
		$("<div id='tooltip'>" + contents + "</div>").css({
			position: "absolute",
			top: y + 5,
			right: $(window).width() - x - 5,
			border: "1px solid #333",
			padding: "2px",
			"background-color": "#1f1f1f",
			color: "#fff",
			"font-size": "12px",
			opacity: 0.80
		}).appendTo("body");//.fadeIn(200);
}
var previousPoint = null;

var plot = null;
var legends = null;
var updateLegendTimeout = null;
var latestPosition = null;

function updateLegend() {

	updateLegendTimeout = null;

	var pos = latestPosition;
	var axes = plot.getAxes();

	if (pos.x < axes.xaxis.min || pos.x > axes.xaxis.max ||
		pos.y < axes.yaxis.min || pos.y > axes.yaxis.max) {
		return;
	}

	var i, j, dataset = plot.getData();
	for (i = 0; i < dataset.length; ++i) {

		var series = dataset[i];

		// Find the nearest points, x-wise

		for (j = 0; j < series.data.length; ++j) {
			if (series.data[j][0] > pos.x) {
				break;
			}
		}

		// Now Interpolate

		var y,
			p1 = series.data[j - 1],
			p2 = series.data[j];
		if (p1 == null) {
			y = p2[1];
		} else if (p2 == null) {
			y = p1[1];
		} else {
			y = p1[1] + (p2[1] - p1[1]) * (pos.x - p1[0]) / (p2[0] - p1[0]);
		}

		legends.eq(i).text(series.label.replace(/=.*/, "= " + y.toFixed(2)));
	}
}

function zeroSplitString(value){
	zeros = [];
	cnt = value.length-1;
	while(cnt >=0){
		c = value.charAt(cnt--);
		if(c == "0"){
			zeros.push(c);
		}
		else
			break;
	}
	return value.substr(0,cnt+2) + '<span class="zero">'+zeros.join('')+'</span>';
}

///////////////////////////////////// trade update ///////////////////////////////
function updateTradeFromChart(new_trades){
	processTrades(new_trades);
	//trades = new_trades;
	$("#chart_trade_block").getNiceScroll().resize();
	time_passed = 0;
}
function updateTrade(){
	$.ajax({
        type: "POST",
        url: context_root + "get_trade",
		data: "market="+market_id+"&size="+show_limit+"&last="+last_trade_date,
        contentType: "application/x-www-form-urlencoded;",
        dataType: "json",
        beforeSend: function() {
        },
        error: function(data, status, errThrown){
			setTimeout(updateTrade, 5000);
        },
        success: function(data){
			if(data.result == "success"){
				var new_trades = data.trades;
				processTrades(new_trades);
				last_trade_date = new_trades.update_date;
				trades = new_trades;
				$("#chart_trade_block").getNiceScroll().resize();
				time_passed = 0;
            }
			setTimeout(updateTrade, 5000);
	    },
        statusCode: {
            404: function() {
                //alert('page not found');
            }
        }
    });
}
function processTrades(new_trades){
	if(last_trade_date == "")
		bFirst = true;
	var html = [];
	for(var i=0;i<new_trades.length;i++){
		data = new_trades[i];
		time = data.date;
		price = data.price;
		volume = data.amount;
		type = data.trade_type;
		if(time<=last_trade_date)
			break;

		class_name = '';
		if(!bFirst && $("#trade_"+price).length == 0){
			class_name = 'new_trade';
		}
		if(currency == "CNY"){
			price_string = price.toFixed(2);
		}
		else{
			price_string = price.toFixed(5);
		}
		volume_string = volume.toFixed(4);

		tmp = [];
		tmp.push('<tr id="trade_'+price+'" class="'+class_name+'" style="height:0px;"><td class="noborder center padding1">'+timeString(time)+'</td>');
		tmp.push('<td class="noborder right padding1">');
		row_class = 'value';
		if(type == 'bid' || type == 'buy')
			row_class+='_green';
		else
			row_class+='_red';
		tmp.push('<span class="'+row_class+'">'+price_string+'</span>');
		tmp.push('</td><td class="noborder right padding1">');
		tmp.push(volume_string);
		tmp.push('</td></tr>');
		html.push(tmp.join(''));
	}
	//console.log(new_trades[0]);
	if(new_trades.length > 0 && last_trade_date < new_trades[0].date)
		last_trade_date = new_trades[0].date;

	if(bFirst){
		$("#trade_block > table > tbody").prepend(html.join(''));
	}
	else{
		addRowsToTable($("#trade_block > table > tbody"), html);
	}

	//$("#trade_block > table > tbody").prepend(html.join(''));
	/*
	for(var i=html.length-1;i>=0;i++){
		$("#trade_block > table > tbody").prepend(html[i]).animate();
	}
	*/
}
function addRowsToTable(wrapper, list){
	if(list.length == 0){
		var total_rows = $("#trade_block > table > tbody > tr").length;
		if(total_rows > 100){
			for(var i=total_rows-100;i<total_rows;i++)
				$("#trade_block > table > tbody > tr:nth-child("+i+")").remove();
		}
		return;
	}
	$(wrapper).prepend(list.pop()).animate({height:14},200,function(){addRowsToTable(wrapper, list);});
}
/*
function processTrades(new_trades){
	if(trades.length > 0)
		bFirst = false;
	var html = [];
	for(var i=0;i<new_trades["data"].length;i++){
		data = new_trades["data"][i];
		time = data[0];
		price = data[1];
		volume = data[2];

		class_name = '';
		if(!bFirst && $("#trade_"+price).length == 0){
			class_name = 'new_trade';
		}
		price_string = price.toFixed(5);
		volume_string = volume.toFixed(4);
		tmp = [];
		tmp.push('<tr id="trade_'+price+'" class="'+class_name+'"><td class="noborder center padding1">'+timeString(time)+'</td>');
		tmp.push('<td class="noborder right padding1">');
		row_class = 'value';
		if(data[3] == 'buy')
			row_class+='_green';
		else if(data[3] == 'sell')
			row_class+='_red';
		tmp.push('<span class="'+row_class+'">'+price_string+'</span>');
		tmp.push('</td><td class="noborder right padding1">');
		tmp.push(volume_string);
		tmp.push('</td></tr>');
		html.push(tmp.join(''));
	}
	
	$("#trade_block > table > tbody").html(html.join(''));
}
*/
function timeString(time){
	value = time + offset;
	hour = Math.floor(value / 3600) % 24;
	min = Math.floor((value % 3600) / 60);
	sec = value % 60;
	if(hour < 10)
		hour = '0' + hour;
	if(min < 10)
		min = '0' + min;
	if(sec < 10)
		sec = '0' + sec;
	return hour+':'+min+":"+sec;
}
//////////////////////////////////// set alert ///////////////////////////////////
var alert_high = -1, alert_low = -1, alert_currency=currency, alert_started = false, alert_sound_playing = false;
function toggleAlert(){
	if(alert_started){
		alert_started = false;
		alert_high = -1;
		alert_low = -1;
		$("#alert_submit").html(lang.set_alert);
		$("#fund_last").removeClass("alert_high").removeClass("alert_low");
		stopAlertSound();
		return;
	}
	var high_val = parseFloat($("#alert_high").val());
	var low_val = parseFloat($("#alert_low").val());
	if(!high_val && !low_val){
		alert(lang.limit_msg1);
		return;
	}
	if(high_val && low_val && high_val < low_val){
		alert(lang.limit_msg2);
		return;
	}
	alert_started = true;
	if(high_val)
		alert_high = high_val;
	if(low_val)
		alert_low = low_val;
	$("#alert_submit").html(lang.stop);
	
	var alert_ways = $("#alert_method").val();
	if(alert_ways){
		for(i=0;i<alert_ways.length;i++){
			if(alert_ways[i]=="sms" && sms_remain_cnt <= 0){
				alert(lang.sms_not_enough);
				$('#alert_method').multiselect('deselect', "sms");
				alert_ways.splice(i,1);
			}
		}
		alert_methods = alert_ways;
	}
	else
		alert_methods = [];
	alert_sent = false;

	$.ajax({
        type: "POST",
        url: context_root + "save_alert_info",
		data: "market="+market_alias+"&currency="+alert_currency+"&high="+alert_high+"&low="+alert_low,
        contentType: "application/x-www-form-urlencoded;",
        dataType: "json",
        error: function(data, status, errThrown){
        },
        success: function(data){
	    },
        statusCode: {
            404: function() {
                //alert('page not found');
            }
        }
    });
}
function checkAlert(ticker){
	if(alert_high > -1 && alert_high <= ticker["last"]){
		spanObj = "#fund_last";
		$(spanObj).removeClass("alert_low");
		$(spanObj).addClass("alert_low").animate();

		playAlertSound();
		sendAlert(market_alias, alert_currency, alert_high, ticker["last"]);
	}
	else if(alert_low > -1 && alert_low >= ticker["last"]){
		spanObj = "#fund_last";
		$(spanObj).removeClass("alert_high");
		$(spanObj).addClass("alert_low").animate();

		playAlertSound();
		sendAlert(market_alias, alert_currency, alert_low, ticker["last"]);
	}
	else{
		stopAlertSound();
		spanObj = "#fund_last";
		$(spanObj).removeClass("alert_low");
		$(spanObj).removeClass("alert_high");
	}
}
function sendAlert(alert_exchange, alert_currency, limit, lastprice){
	if(alert_methods && alert_methods.length > 0){
		$.ajax({
			type: "POST",
			url: context_root + "send_alert",
			data: "market="+alert_exchange+"&currency="+alert_currency+"&limit="+limit+"&price="+lastprice+"&way="+alert_methods.join(","),
			contentType: "application/x-www-form-urlencoded;",
			dataType: "json",
			error: function(data, status, errThrown){
			},
			success: function(data){
				if(data.result == "success"){
					if(typeof(data.sms_remain_cnt) != "undefined"){
						sms_remain_cnt = data.sms_remain_cnt;
					}
				}
				else if(typeof(data.msg) != "undefined"){
					alert(data.msg);
				}
				alert_sent = true;
			},
			statusCode: {
				404: function() {
					//alert('page not found');
				}
			}
		});
	}
}
function playAlertSound(){
	if(!alert_sound_playing){
		alert_sound_playing = true;
		$("#alert_jplayer").jPlayer( "play");
	}
}
function stopAlertSound(){
	alert_sound_playing = false;
	$("#alert_jplayer").jPlayer( "stop");
}