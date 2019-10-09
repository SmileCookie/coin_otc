<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ page session="false"%>
<!doctype html>
<html>
<head>
    <jsp:include page="/common/head.jsp" />
    <title>${L:l(lan,'抽奖')}-${WEB_NAME}-${WEB_TITLE }</title>
    <meta name="keywords" content="${WEB_KEYWORD }" />
    <meta name="description" content="${WEB_DESC }" />
    <meta name="viewport" content="width=device-width,height=device-height,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <link rel="stylesheet" id="css" href="${static_domain }/statics/css/lucky_draw.css?V${CH_VERSON }">
<body>
<div class="container_index">
    <jsp:include page="/common/top.jsp" />
    <div class="container_index_main clearfix">
        <div class="content">
            <div class="title" id="title">
                ${eventInfo.eventTitleJson}
            </div>
            <div class="info" id="info">
                ${eventInfo.eventContentJson}
            </div>
            <div class="draw-box clearfix">
                <section class="draw-box-main">
                    <div class="draw-awarded">
                        <div class="draw-box-title">
                            ${L:l(lan,'奖池已发放')}
                        </div>
                        <div class="draw-box-content">
                            <div class="num" id="awarded">
                                <font><fmt:formatNumber value="${luckyRule.occurAmount}" pattern="0.######"/></font>
                            </div>
                        </div>
                    </div>

                    <section class="scroll_mian">
                        <div class="backgr_01">
                            <div class="backgr_02">
                                <div class="backgr_03">
                                    <div class="backgr_04">
                                        <div class="scroll-num" id="scroll-num">
                                            <div class="scroll">
                                                <span class="num num1"></span>
                                                <span class="num num2"></span>
                                                <span class="num num3"></span>
                                            </div>
                                            <div class="noStart">ABCDEF</div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>

                    <div class="draw-obtained">
                        <div class="draw-box-title">
                            ${L:l(lan,'您已获得')}
                        </div>
                        <div class="draw-box-content">
                            <div class="num" id="obtained">
                                <fmt:formatNumber value="0.00" pattern="0.######"/>
                            </div>
                        </div>
                    </div>
                </section>
                <input type="hidden" id="token">
                <div class="tip-box"></div>
                <div class="draw-button" id="drawBut">抽奖</div>
                <div class="draw-bottom">
                    <div class="draw-bottom-title">${L:l(lan,'活动规则-1')}</div>
                    <div id="rulesBox">
                        ${eventInfo.eventRuleJson}
                    </div>
                </div>
            </div>
            <div class="foot_tips">*${L:l(lan,"本次活动最终解释权归btcwinex平台所有！")}</div>
        </div>
    </div>
    <jsp:include page="/common/foot.jsp" />
</div>
<script type="text/javascript">
    var draw = {};
    var it;
    draw.init = function(){
        var $this = this
         this.beginDraw()    // 开始抽奖
        // this.getStateInfo() // 获取当前状态信息
        
        setTimeout(function() { // 500ms之后显示滚动数字
            $(".scroll-num").css("display","block");
        }, 500);
    }

    draw.alertHtml = function(){
        var $this = this;
        this.docW = $(document).width();
        this.docH = $(document).height();
        this.winW = $(window).width();
        this.winH = $(window).height();
        var html = '<div class="vote_alert close_alert"></div>';
        var html_home = '<div class="vote_home">'+
                            '<div class="vote_close close_alert"></div>'+
                            '<h3>'+bitbank.L("请登录后再进行抽奖")+'</h3>'+
                            '<div class="vote_login" onclick="toLogin()"><a class="prev_login_href"  target="_self">'+bitbank.L("登录")+'</a></div>'+
                            '<div class="vote_reg"><a class="reg_a" href="'+DOMAIN_VIP+'/register/" target="_self">'+bitbank.L("还没有账户")+'</a></div>'+
                        '</div>';
        $("body").append(html);
        $("body").append(html_home);
        $(".vote_alert").css({
            "width" : Math.max(this.docW,this.winW),
            "min-width" : "320px",
            "height" : Math.max(this.docH,this.winH),
            "z-index" :99
        });


        $(document).on("click",".close_alert",function(){
            $(".close_alert").remove();
            $(".vote_home").remove();
        })
    }
    draw.isLogin = function(){
        return $.cookie(UON) == "1" && $.cookie(UNAME) != null && $.cookie(UID) != null;
    }
    
    draw.beginDraw = function(){
        var $this = this
        var list  = $('#scroll-num .scroll span')
        it = setInterval(function(){
            $this.loop(list)
        },50)
    }
        
    draw.loop = function(list){
        var maxNum = "";
        var r_num = Math.ceil(Math.random() * 100);
        // for(var i=0;i<list.length;i++){
        //         var r = Math.floor(Math.random()*10)
        //         maxNum += r;
        //     list[i].style.background = "url(" + DOMAIN_STATIC + "/statics/img/activity/" + r + ".png) no-repeat"
        //     list[i].style.backgroundSize = 'contain';
        // }
        if ( r_num == 100 ){
            $(".num2").css({ display: "inline-block" })
            $(".num3").css({ display: "inline-block" })
            list[0].style.background = "url(" + DOMAIN_STATIC + "/statics/img/activity/" + 1 + ".png) no-repeat"
            list[1].style.background = "url(" + DOMAIN_STATIC + "/statics/img/activity/" + 0 + ".png) no-repeat"
            list[2].style.background = "url(" + DOMAIN_STATIC + "/statics/img/activity/" + 0 + ".png) no-repeat"
        }
        else if ( r_num >= 10 ){
            $(".num2").css({ display: "inline-block" })
            $(".num3").hide();
            list[0].style.background = "url(" + DOMAIN_STATIC + "/statics/img/activity/" + r_num.toString().charAt(0) + ".png) no-repeat"
            list[1].style.background = "url(" + DOMAIN_STATIC + "/statics/img/activity/" + r_num.toString().charAt(1) + ".png) no-repeat"
        }
        else{
            $(".num2").hide();
            $(".num3").hide();
            list[0].style.background = "url(" + DOMAIN_STATIC + "/statics/img/activity/" + r_num + ".png) no-repeat"
        }
        list.css("backgroundSize","contain");
        
    }
    window.toLogin = function(){
        var prevherfss = window.location.pathname;
        $.cookie("prevhref", prevherfss, { path: "/" });
        window.location.href = DOMAIN_VIP+'/login/';
    }
    draw.goDraw = function(){
        var $this = this
        if(!this.isLogin()){
            this.alertHtml()
            return
        }
        $(".draw-button").unbind('click');
        $.ajax({
            url:DOMAIN_VIP + "/lucky/goodLucky?token="+$("#token").val(),
            type:"GET",
            dataType:"json",
            success: function(data){
                console.log(data)
                if(data.isSuc){
                    var datas = data.datas
                    var numArr = datas.currAmount.split('')
                    $(".scroll").css({"display":'none'})
                    if(it){
                        clearInterval(it)
                        it=null;
                        $(".scroll").html('')
                        var html = ''
                        if (datas.viewFlag == "02"){
                            $(".tip_box_alert").html(bitbank.L("很抱歉，奖项已全部发放。"))
                            html = '<img src="' + DOMAIN_STATIC + '/statics/img/activity/0.png"/> ';
                        }
                        else if (datas.viewFlag == "06"){
                            $(".tip_box_alert").html(bitbank.L("您的抽奖机会已过期。"))
                            html = '<img src="' + DOMAIN_STATIC + '/statics/img/activity/0.png"/> ';
                        }
                        else{
                            if (parseFloat(datas.currAmount) > 0) {
                                for (var i = 0; i < numArr.length; i++) {
                                    if (numArr[i] == '.') {
                                        html += '<img class="dian" src="' + DOMAIN_STATIC + '/statics/img/activity/dian.png"/> '
                                    } 
                                    else {
                                        html += '<img src="' + DOMAIN_STATIC + '/statics/img/activity/' + numArr[i] + '.png"/> '
                                    }
                                }
                                $(".tip_box_alert").html(bitbank.L("恭喜您获得ABCDEF", datas.currAmount));
                            } 
                            else {
                                html += '<img src="' + DOMAIN_STATIC + '/statics/img/activity/0.png"/> '
                                $(".tip_box_alert").html(bitbank.L("很抱歉，没有中奖", datas.currAmount))
                            }
                        }
                        
                        $(".scroll").html(html)
                        $(".scroll").css({"display":'block'})
                        if(isphone){
                            if( LANG == "en" ){
                                $(".tip_box_alert").css({"width":"90%"});
                            }
                        }
                        $(".tip_box_alert").css({"display":'block'})
                        $(".noStart").show();
                    }
                }
                setTimeout(function() {
                    window.location.reload();
                }, 2000);
            },
            error:function(err){
                $(".tip-box span").html(bitbank.L("网络异常，请您再次尝试"))
                $(".tip-box").css({"display":'block'})
                setTimeout(function() {
                    window.location.reload();
                }, 2000);
                console.log(err);
            }
        })
        
    }

    draw.getStateInfo = function(){
        return;
        var $this = this
        $.ajax({
            url:DOMAIN_VIP + "/lucky/getluckyInfo",
            type:"GET",
            dataType:"json",
            success: function(data){
                if(data.isSuc){
                    $this.setPageState(data.datas)
                }else{
                    window.location.href = DOMAIN_VIP
                }
                
            },
            error:function(err){
                console.log(err);
            }
        })
    }

    draw.setPageState = function(data){
        console.log(data)
        var $this = this;

        var $drawBut = $("#drawBut"); //按钮
        var $draw_button = $(".draw-button");// 按钮
        var $tip_box = $(".tip-box"); //提示信息
        var $toupiaoIcon = $("#toupiaoIcon"); //投票悬浮按钮 
        var $noStart = $(".noStart"); //数量单位     
        var $scroll_num= $("#scroll-num");  //中间显示的数字

        $draw_button.unbind('click'); // 按钮取消点击事件
        $("#token").val(data.token)
        $("#title").html(data.eventTitleJson)
        $("#info").html(data.eventContentJson)
        $("#awarded").html(data.jackpotSize + '<span class="awarded_gbc">ABCDEF</span>')
        $("#obtained").html(data.userAmount + '<span class="awarded_gbc">ABCDEF</span>')
        $("#rulesBox").html(data.eventRuleJson)
        $toupiaoIcon.css({ 'display': data.isVoteShow == "1" ? 'block' : "none" })
        $noStart.hide();
        switch (data.viewFlag) {
            case '01': // 01未开始页面
                $drawBut.html(bitbank.L("未开始"))
                $drawBut.css({
                    'background': '#485E6E',
                    "color": "rgba(255,255,255,0.3)"
                })
                if (isphone) {
                    $drawBut.css({ "margin-top": "0.6rem" })
                }
                else {
                    $drawBut.css({ "margin-top": "53px" })
                    $scroll_num.css({ "line-height": "74px" })
                }
                $scroll_num.html(bitbank.L("活动未开始"))
                break;
            case '02': // 02全部中出
            
                $drawBut.html(bitbank.L("奖池已全部发放"))
                $drawBut.css({
                    'background': '#485E6E',
                    "color": "rgba(255,255,255,0.3)"
                })
                if (isphone) {
                    $drawBut.css({ "margin-top": "0.6rem" })
                    $scroll_num.css({ "margin-top": "0.3rem" })
                }
                else {
                    $drawBut.css({ "margin-top": "53px" })
                    $scroll_num.css({ "margin-top": "20px" })
                }
                $scroll_num.html(bitbank.L("奖池已全部发放"))
                break;
            case '03': // 03可抽奖”页面
                $drawBut.html(bitbank.L("抽奖"))
                $drawBut.css({
                    'background': '#485E6E',
                    "color": "rgba(255,255,255,1)",
                    "cursor": "pointer"
                })
                if (isphone) {
                    $drawBut.css({ "margin-top": "0.6rem" })
                }
                else {
                    $drawBut.css({ "margin-top": "53px" })
                    $drawBut.hover(function () {
                        $(this).css({
                            'background': '#4F748E'
                        })
                    }, function () {
                        $(this).css({
                            'background': '#485E6E'
                        })
                    })
                }
                
                $draw_button.bind('click',$this.goDraw.bind($this)); // 抽奖按钮绑定点击事件
                break;
            case '04': // 04 无权限抽奖
                $tip_box.show().html(bitbank.L("您没有机会参与本次抽奖"));
                $drawBut.html(bitbank.L("抽奖"));  
                $drawBut.css({
                            'background': '#485E6E',
                            "color":"rgba(255,255,255,0.3)",
                            "margin-top": "10px",
                        })
                break;
            case '05': // 05 抽奖次数用完 
                $drawBut.html(bitbank.L("已抽奖"))
                $drawBut.css({
                    'background': '#485E6E',
                    "color": "rgba(255,255,255,0.3)"
                })
                if (isphone) {
                    $drawBut.css({ "margin-top": "0.6rem" })
                }
                else{
                    $drawBut.css({ "margin-top": "53px" })
                }
                break;
            default:
                break;
        }

    }
    
    draw.isLogin = function(){
        	return $.cookie(UON) == "1" && $.cookie(UNAME) != null && $.cookie(UID) != null;
    };
    draw.init();
</script>
</body>
</html>