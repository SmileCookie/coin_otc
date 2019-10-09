$("html").addClass(LANG);

$("#bitMenu,#bitLan").bootstrapDropdownOnHover({
    mouseOutDelay: 100,
    responsiveThreshold: 769
});
/*
$(".nav-right-tab").hover(function(){
    $(this).find('.dropdown-menu').first().stop(true, true).slideDown(100);
}, function() {
    $(this).find('.dropdown-menu').first().stop(true, true).slideUp(100);

});*/

$("#userAccount,#setMoney,.langtab").hover(function(){
    $(this).find('.dropdown-menu').first().stop(true, true).slideDown(100);
}, function() {
    $(this).find('.dropdown-menu').first().stop(true, true).slideUp(100);

});

$('#menuAll').on('show.bs.dropdown', function() {
    $(this).find(".dropdown-menu").width($(window).width() - 20);
})

$('[data-toggle="tooltip"]').tooltip();
$('[data-toggle="popover"]').popover();


$('.bk-animationload').delay(500).fadeOut(300);

if ($(document).height() - $(window).height() > 200) {
    $('img[data-src]').on('inview', function(event, visible) {
        if (visible == true) {
            $(this).attr("src", $(this).attr("data-src"));
        }
    });
} else {
    $('img[data-src]').each(function(index, element) {
        $(this).attr("src", $(this).attr("data-src"));
    });
}
var goScrollTo = function(div, offtop) {
    $("html,body").animate({ scrollTop: $(div).offset().top - parseInt(offtop) }, 500);
    return false;
}
if ($(".bk-topScroll").length == 0) {}
$("#topScroll").on("click", function() {
    goScrollTo('html,body', 0);
});
$(window).scroll(function() {
    if ($(this).scrollTop() > 500) {
        $('.bk-siderBar .top').fadeIn(500);
    } else {
        $('.bk-siderBar .top').fadeOut(500);
    }
});
$(window).resize(function() {
    if ($(window).width() < 1010) {
        $(".bk-focus .bd").find("ul,li").width(1010);
    } else {
        $(".bk-focus .bd").find("ul,li").width($(window).width());
    }
});

if (document.location.href == DOMAIN_MAIN + "/") {
    $(".onlyhome").removeClass("onlyhome");
}

$("input[data-drop=true]").after("<i class='drop-cert' onclick='dropSelect(this);'></i>");
$("input[data-drop=true]").bind({
    "focus": function() {
        var $this = $(this);
        var $formLine = $(this).parents(".drop-group").find(".input-drop");
        if ($formLine.find("li").length === 0) {
            return false;
        } else {
            $formLine.show();
            $formLine.find("li").on('click', function() {
                $this.blur();
            });
        }
    },
    "blur": function() {
        var $formLine = $(this).parents(".drop-group").find(".input-drop");
        if ($formLine.find("li").length === 0) return false;
        if ($formLine.is(':visible')) {
            setTimeout(function() { $formLine.hide() }, 150);
        }
    }
});

$(".dropdown-menu").on("click", "li", function() {
    var dropMenu = $(this).parents(".dropdown");
    var bindInput = dropMenu.find(".dropdown-menu").data("bind");

    $("#" + bindInput).val($(this).data("value"));
    $(dropMenu).find(".text-g").text($(this).text());
});

formatNum();

function dropSelect(obj) {
    var $formLine = $(obj).parents(".drop-group").find(".input-drop");
    if ($formLine.find("li").length === 0) {
        return false;
    } else {
        $formLine.toggle();
    }
}
topMore();
function topMore() {
    var $obj = $(".more-hover");
    var url = window.location.pathname;
    // $obj.hover(function() {
    //     $(this).find(".nav-more").stop(true,true).animate({height:"88px"},200,function(){
    //         $(this).css({'overflow':'visible'})
    //     });
    //     // $(".topnext").addClass("topnextactive");
    // }, function() {
    //     $(this).find(".nav-more").stop(true,true).animate({opacity:0},200,function(){
    //             $(this).css({overflow:'hidden',height:0})
    //             $(this).css({opacity:1})
    //     });
    //     // $(".topnext").removeClass("topnextactive");
    // })
    if (url.indexOf("/msg") == -1) {
        $(".msg").hover(function() {
            $(".topnext").addClass("topnextactive");
        }, function() {
            $(".topnext").removeClass("topnextactive");
        })
    }
}
//埋点数据
sendPoint();
function sendPoint(){
    $.ajax({
        type: "GET",
        url: DOMAIN_VIP + "/browseHome",
        dataType: 'json',
        success: function (json){

        }
    })
}


//移动端导航条
if(JuaBox.isMobile()){
    mobile_top_Click();
}
function mobile_top_Click(){

    $(".nav_mobile_menu").on("click touchend",function(ev){
        ev.preventDefault();
        var iHeight = screen.height;
        $(".bk-nav .navbar-nav").height(iHeight);
        $(".bk-nav .navbar-nav").show();
    })
    $(".nav_mobile_close").on("click touchend",function(ev){
        ev.preventDefault();
        $(".bk-nav .navbar-nav").hide();
    })
    if( isLogin() ){
        $(".login_mobile_hide").hide();
        $(".log_zhijin").find("a").attr("href",DOMAIN_VIP+"/manage/account/").html(bitbank.L("资金"));
        $(".log_zhanghu").find("a").attr("href",DOMAIN_VIP+"/manage/").html( bitbank.L("帐户"));
        var lougoutHtml = '<li><a class="log_zhijin" href="'+DOMAIN_VIP+'/manage/account/" target="_self">'+bitbank.L("资金")+'</a></li>'+
                          '<li><a class="log_zhanghu" href="'+DOMAIN_VIP+'/manage/" target="_self">'+bitbank.L("帐户")+'</a></li>'+
                          '<li><a class="zhuxiao zhuxiao_btn" href="'+DOMAIN_VIP+'/login/logout/" target="_self">'+bitbank.L("注销")+'</a></li>';
        $("#bitNav").append(lougoutHtml);
    }
    $(".nav_mobile_menu_1").addClass('pin_'+LANG);
    $(".nav_mobile_menu_1").on("click touchend",function(ev){
        ev.preventDefault();
        var iHeight = screen.height;
        $(".nav_mobile_langs").height(iHeight);
        $(".nav_mobile_langs").show();
    })
    $(".nav_mobile_close_1").on("click touchend",function(ev){
        ev.preventDefault();
        $(".bk-nav .nav_mobile_langs").hide();
    })

    $(".nav_mobile_langs li,#bitNav li").on("click",function(){
        $(this).css("background","#353535");
        $(this).find("a").css("color","#B9B9B9");
    })

}
function isLogin(){
    return $.cookie(UON) == "1" && $.cookie(UNAME) != null && $.cookie(UID) != null;
};
var Map = function() {
    this._entrys = new Array();

    this.put = function(key, value) {
        if (key == null || key == undefined) {
            return;
        }
        var index = this._getIndex(key);
        if (index == -1) {
            var entry = new Object();
            entry.key = key;
            entry.value = value;
            this._entrys[this._entrys.length] = entry;
        } else {
            this._entrys[index].value = value;
        }
    };
    this.get = function(key) {
        var index = this._getIndex(key);
        return (index != -1) ? this._entrys[index].value : null;
    };
    this.remove = function(key) {
        var index = this._getIndex(key);
        if (index != -1) {
            this._entrys.splice(index, 1);
        }
    };
    this.clear = function() {
        this._entrys.length = 0;;
    };
    this.contains = function(key) {
        var index = this._getIndex(key);
        return (index != -1) ? true : false;
    };
    this.getCount = function() {
        return this._entrys.length;
    };
    this.getEntrys = function() {
        return this._entrys;
    };
    this._getIndex = function(key) {
        if (key == null || key == undefined) {
            return -1;
        }
        var _length = this._entrys.length;
        for (var i = 0; i < _length; i++) {
            var entry = this._entrys[i];
            if (entry == null || entry == undefined) {
                continue;
            }
            if (entry.key === key) {
                return i;
            }
        }
        return -1;
    };
}
var monName = '',moneyIcon = '';
var _moneyCall = $.cookie("currency")
//console.log(setIndex)
// if($.cookie('zlan')=='cn'){var  index
switch(_moneyCall){
    case 'CNY':
        moneyIcon = 'icon-renminbi'
        monName = bitbank.L("人民币") ;
        break;
    case 'EUR':
        moneyIcon = 'icon-icon'
        monName = bitbank.L("欧元") ;
        break;
    case 'GBP':
        moneyIcon = 'icon-yingbang_pounds'
        monName = bitbank.L("英镑");
        break;
    case 'AUD':
        moneyIcon = 'icon-aoyuan'
        monName = bitbank.L("澳元") ;
        break;
    case 'USD':
        moneyIcon = 'icon-meiyuan-copy'
        monName = bitbank.L("美元");
        break;
    default:
        moneyIcon = 'icon-renminbi'
        monName = bitbank.L("人民币") ;
        break;
}

$("#setMoney > a > p").text(monName);
$("#moneynames").text(monName);
$("#setMoney > a > i").addClass(moneyIcon);
// $("#moneyIcon").addClass(moneyIcon);

$.ajax({
    type: "GET",
    url: DOMAIN_TRANS+"/getExchangeRate",
    dataType: 'json',
    success: function (json){
        if(json.isSuc){
            var res = json.datas.exchangeRateUSD;
            var $drowCurrency = $("#drowCurrency > li"),
                i=0,
                setIndex=0;
            for(var k in res){
                if(k != "BTC"){
                    $drowCurrency.eq(i).data(k,res[k]);
                    if($.cookie("currency") == k){
                        setIndex = i
                    }
                    i++
                }
            }
        // var nowClass = $drowCurrency.eq(setIndex).find("i")[0].className;
        // $("#setMoney > a > i").removeClass().addClass(nowClass);
        // let
            var monName = '',moneyIcon = '';
            var _moneyCall = $.cookie("currency")

            console.log('============='  + setIndex)
        // if($.cookie('zlan')=='cn'){var  index
            switch(_moneyCall){
                case 'CNY':
                    moneyIcon = 'icon-renminbi';
                    monName = bitbank.L("人民币") ;
                    break;
                case 'EUR':
                    moneyIcon = 'icon-icon';
                    monName = bitbank.L("欧元") ;
                    break;
                case 'GBP':
                    moneyIcon = 'icon-yingbang_pounds'
                    monName = bitbank.L("英镑");
                    break;
                case 'AUD':
                    moneyIcon = 'icon-aoyuan';
                    monName = bitbank.L("澳元") ;
                    break;
                case 'USD':
                    moneyIcon = 'icon-meiyuan-copy';
                    monName = bitbank.L("美元");
                    break;
                default:
                    moneyIcon = 'icon-renminbi';
                    monName = bitbank.L("人民币") ;
                    break;
            }

            $("#setMoney > a > p").text(monName);
            $("#setMoney > a > i").addClass(moneyIcon);
            $("#moneynames").text(monName);
            // $("#moneyIcon").addClass(moneyIcon);
        //windowFunds();
        ajaxCell();
        }
    }
})

$("#userAccount").on("mouseenter",function(){
    //windowFunds();
    ajaxCell()
})
var coinUnitName = $.cookie("currency")||"USD";
var coinSpace = LANG == "en"? " ":"";
$("#valName").html(bitbank.L(coinUnitName)+coinSpace);

function windowFunds(){
    var langs = LANG;
    // if(langs == "en"){
        langs = "USD";
    // }
    // else{
    //     langs = "CNY";
    // }

    Promise.all([_ajax1,_ajax2]).then(function (result){
            var coinUnit = $.cookie("currency")||"USD";
            var wallent =  new Big(result[0]).plus(new Big(result[1]))
            $(".now-acc-cny").text(wallent+" "+coinUnit.toUpperCase());
        })
}
var _ajax1 = new Promise(function (resolve, reject) {
    ajaxCall(resolve, reject);// 将resolve和reject传过去
});
var _ajax2 = new Promise(function (resolve, reject) {
    ajaxCell(resolve, reject);// 将resolve和reject传过去
});
 function ajaxCall(resolve,reject){
    var langs = LANG;
    // if(langs == "en"){
        langs = "USD";
    return $.ajax({
        url:"/manage/account/getUserWalletTotalAssest?legal_tender="+langs,
        type:"GET",
        dataType:"json",
        success:function(data){
            var datas = data.datas;
            Big.RM = 0;
            if(data.isSuc){
                var rmbTotal = new Big(datas.total_legal_tender).toFixed(2);
                var usdtTotal = datas.total_usdt;
                var coinUnit = $.cookie("currency")||"USD";
                var nowPrice;
                var $drowCurrency = $("#drowCurrency > li");
                for(var i=0;i<$drowCurrency.length;i++){
                    if($drowCurrency.eq(i).data(coinUnit)){

                        nowPrice = new Big(usdtTotal).times($drowCurrency.eq(i).data(coinUnit)).toFixed(2);
                        break;
                    }
                }
                nowPrice = nowPrice ? nowPrice :0.00;
                resolve(nowPrice)
            }
        },
        error: function (err) {
            console.log(err);
        }
    })
}
function ajaxCell(resolve,reject){
    var langs = LANG;
    // if(langs == "en"){
        langs = "USD";
    return $.ajax({
        url:"/manage/account/getUserTotalAssest?legal_tender="+langs,
        type:"GET",
        dataType:"json",
        success:function(data){
            var datas = data.datas;
            Big.RM = 0;
            if(data.isSuc){
                var rmbTotal = new Big(datas.total_legal_tender).toFixed(2);
                var usdtTotal = datas.total_usdt;
                var coinUnit = $.cookie("currency")||"USD";
                var nowPrice;
                var $drowCurrency = $("#drowCurrency > li");
                for(var i=0;i<$drowCurrency.length;i++){
                    if($drowCurrency.eq(i).data(coinUnit)){
                        nowPrice = new Big(usdtTotal).times($drowCurrency.eq(i).data(coinUnit)).toFixed(2);
                        break;
                    }
                }
                nowPrice = nowPrice ? nowPrice :0.00;
                $(".now-acc-cny").text(nowPrice+" "+coinUnit.toUpperCase());
            }
        },
        error: function (err) {
            console.log(err);
        }
    })
}

function moneySymbol(unit){
    var iconMon = "$";
    var enSpace = LANG == "en"? "&nbsp;":"";
    if(unit == "USD"){
        iconMon = enSpace+"$";
    }else if(unit == "CNY"){
        iconMon = enSpace+"¥";
    }else if(unit == "EUR"){
        iconMon = enSpace+"€";
    }else if(unit == "GBP"){
        iconMon = enSpace+"£";
    }else if(unit == "AUD"){
        iconMon = enSpace+"A$";
    }
    return iconMon;
}


















