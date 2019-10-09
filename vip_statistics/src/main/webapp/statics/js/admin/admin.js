var showids = "";
function showUser(id) {
	showids = id;
	var uName = $("#text_"+showids).text()=="undefined"?"":" " + $("#text_"+showids).text() + " ";
	$.Iframe({
		Url : vip.vipDomain+'/admin/user/show?userId=' + showids+"&mCode="+id,
		Width : 1000,
		Height : 500,
		Title : "用户"+uName+"信息"
	});
}

/**
 * 输入验证码页面
 */
var couldPass = false;
function googleCode(call, iframe){
	Iframe({
		Url:"/admin/manager/iframegooglecode?callback="+call+"&needClose="+iframe,
		Width:380,
		Height:245,
		Title:"谷歌验证",
		scrolling:"no"
	});
}

var _uname = "";
function getUname(uname){
	if(uname)
		_uname = uname;
	else if($("#userName").length > 0)
		_uname = $("#userName").val();
	
	return _uname;
}

function btcCharge(uname, coint){
	var url = "/admin/btc/recharge/charge-"+getUname(uname);
	if(coint)
		url += "?coint="+coint;
	Iframe({Url: url,Width:600,Height:500,Title:"系统充值"+coint,scrolling:"no"});
}

function btcDeduct(uname, coint){
	var url = "/admin/btc/recharge/deduction-"+getUname(uname);
	if(coint)
		url += "?coint="+coint;
	Iframe({Url: url,Width:600,Height:456,Title:"系统扣除"+coint,scrolling:"no"});
}

function btcFreez(uname, coint){
	var url = "/admin/btc/freez/freez-"+getUname(uname);
	if(coint)
		url += "?coint="+coint;
	Iframe({Url: url,Width:650,Height:430,Title:"系统冻结"+coint});
}
function btcUnFreez(uname, coint){
	var url = "/admin/btc/freez/handsUnFreezPage-"+getUname(uname);
	if(coint)
		url += "?coint="+coint;
	Iframe({Url: url,Width:660,Height:476,Title:"系统解冻"+coint,scrolling:"no"});
}

function shiftCapital(id){
	Iframe({Url:"/admin/pay/recharge/shiftCapital-"+id,Width:660,Height:476,Title:"资金转移",scrolling:"no"});
}

$(function(){
	if($("#ajax_phone_get").length > 0){
		$("#ajax_phone_get").bind("click", postCode);
	}
});

function postCode(){
	var actionUrl = "/ad_admin/postcode";
	vip.ajax( {
		url : actionUrl,
		dataType : "json",
		suc : function(json) {
			waitTime();
			$("#ajax_phone_get").attr("disabled", "disabled");
			//Right(json.des);
		}
	});
}

var timeInterval = "";
window.clearInterval(timeInterval);
function waitTime(){
	var count = 120;
	window.clearInterval(timeInterval);
	var timeInterval = window.setInterval(function(){
		count --;
		if(count == 0){
			window.clearInterval(timeInterval);
			$("#ajax_phone_get").text("获取验证码").removeAttr("disabled");								
		}else{
			$("#ajax_phone_get").text(count+"秒后重新获取");								
		}
	}, 1000);
}

function tongji(isAll){
	var ids="";
	if(!isAll){
		$(".checkItem").each(function(){
			var id=$(this).val();
			if($(this).attr("checked")==true){
				ids+=id+",";
			}
		});
		var list=ids.split(",");
		if(list.length==1){ 
			Wrong("请选择一项"); 
			return;
		}
	}
	vip.ajax({
		url : vip.list.basePath+"tongji?eIds=" + ids+"&isAll="+isAll,
		formId : "searchContaint",
		dataType : "json",
		suc : function(json) {
			var obj = json.datas;
			$.each(json.datas, function(i,v){
				if(i==0){
					$("#totalM").text(v).parent("span").show();
				}else{
					$("#totalM"+(i+1)).text(v).parent("span").show();
				}
			});
		}
	});
}