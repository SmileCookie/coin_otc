define(function(require, exports, module) {
    "require:nomunge,exports:nomunge,module:nomunge";
    var draw = {};
    var it;
    draw.init = function(){
        var $this = this
        this.beginDraw()    // 开始抽奖
        this.getStateInfo() // 获取当前状态信息
        // this.alertHtml_box();
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
    draw.alertHtml_box = function (title) {
        var $this = this;
        this.docW = $(document).width();
        this.docH = $(document).height();
        this.winW = $(window).width();
        this.winH = $(window).height();
        var html = '<div class="vote_alert vote_alert_draw"></div>';
        var html_home = '<div class="vote_home_box vote_home_box_'+LANG+'">' +
                            '<h3>'+title+'</h3>' +
                            '<div class="vote_login">' + bitbank.L("知道了") + '</div>' +
                        '</div>';
        $("body").append(html);
        $("body").append(html_home);
        $(".vote_alert").css({
            "width": Math.max(this.docW, this.winW),
            "min-width": "320px",
            "height": Math.max(this.docH, this.winH),
            "z-index": 99
        });

        $(".vote_login").on("click",function(){
            $(".vote_alert").remove();
            $(".vote_home_box").remove();

        })
    }
    
    draw.isLogin = function(){
        return $.cookie(UON) == "1" && $.cookie(UNAME) != null && $.cookie(UID) != null;
    }
    draw.beginDraw = function(){
        var $this = this
        var list  = $('#scroll-num .scroll span')
    }
    window.toLogin = function(){
        var prevherfss = window.location.pathname;
        $.cookie("prevhref", prevherfss, { path: "/" });
        window.location.href = DOMAIN_VIP+'/login/';
    }
    draw.goDraw = function(){
        
        var $this = this;
        var $draw_button = $(".draw-button");// 按钮
        if(!this.isLogin()){
            this.alertHtml()
            return
        }
        $draw_button.unbind('click');
        $.ajax({
            url:DOMAIN_VIP + "/lucky/goodLucky?token="+$("#token").val(),
            type:"GET",
            dataType:"json",
            success: function(data){
                if(data.isSuc){
                    var datas = data.datas
                    var html = '';
                    $("#token").val(datas.token);
                    $(".tip-box").html(bitbank.L("抽奖机会：") + datas.chance + bitbank.L("次"))
                        if (datas.viewFlag == "02") {
                            $this.alertHtml_box(bitbank.L("很抱歉，奖项已全部发放。"));
                            $draw_button.bind('click', $this.goDraw.bind($this)); // 抽奖按钮绑定点击事件
                        }
                        else if (datas.viewFlag == "04") {
                            $this.alertHtml_box(bitbank.L("您没有机会参与本次抽奖。"));
                            $draw_button.bind('click', $this.goDraw.bind($this)); // 抽奖按钮绑定点击事件
                        }
                        else if (datas.viewFlag == "05"){
                            $this.alertHtml_box(bitbank.L("您今天的抽奖机会已用完")); 
                            $draw_button.bind('click', $this.goDraw.bind($this)); // 抽奖按钮绑定点击事件
                        }
                        else if (datas.viewFlag == "06") {
                            $this.alertHtml_box(bitbank.L("您的抽奖机会已过期。")); 
                            $draw_button.bind('click', $this.goDraw.bind($this)); // 抽奖按钮绑定点击事件
                        }
                        else if (datas.viewFlag == "07") {
                            $this.alertHtml_box(bitbank.L("无可用抽奖次数。"));
                            $draw_button.bind('click', $this.goDraw.bind($this)); // 抽奖按钮绑定点击事件
                        }
                        else {
                            $(".scroll").html("").removeClass("scroll_end");
                            if(JuaBox.isMobile()){
                                var numArr = datas.currAmount.split('')
                                $(".scroll").removeClass("scroll_none scroll_end").addClass("scroll_gif"); //中奖效果
                                if (parseFloat(datas.currAmount) > 0) {
                                    for (var i = 0; i < numArr.length; i++) {
                                        if (numArr[i] == '.') {
                                            html += '<img class="dian" src="' + DOMAIN_STATIC + '/statics/img/activity/1_dian.png"/> '
                                        }
                                        else {
                                            html += '<img src="' + DOMAIN_STATIC + '/statics/img/activity/1_' + numArr[i] + '.png"/> '
                                        }
                                    }
                                    $("#awarded").html(datas.jackpotSize + '<span class="awarded_gbc">GBC</span>')
                                    $("#obtained").html(datas.userAmount + '<span class="awarded_gbc">GBC</span>')
                                    setTimeout( function() {
                                        $this.alertHtml_box(bitbank.L("恭喜您获得GBC", datas.currAmount));
                                    }, 1000);
                                }
                                else {
                                    html += '<img src="' + DOMAIN_STATIC + '/statics/img/activity/1_0.png"/> ';
                                    setTimeout(function () {
                                        $this.alertHtml_box(bitbank.L("很遗憾，没有获得奖品。", datas.currAmount));
                                    }, 1000);
                                }
                                $(".scroll").html(html);
                                $(".scroll").removeClass("scroll_gif");
                                $(".scroll").css({
                                    "background-image": ""
                                })
                                if (isphone) {

                                } else {
                                    $(".scroll").addClass("scroll_end")
                                }
                                $draw_button.bind('click', $this.goDraw.bind($this)); // 抽奖按钮绑定点击事件
                            }else{
                                var numArr = datas.currAmount.split('')
                                $(".scroll").removeClass("scroll_none scroll_end").addClass("scroll_gif"); //中奖效果
                                $(".scroll_gif").css({
                                    "background-image": "url(" + DOMAIN_STATIC + "/statics/img/activity/web_draw_gif.gif?vtime=" + new Date().getTime() +")"
                                })
                                setTimeout( function() {
                                    if (parseFloat(datas.currAmount) > 0) {
                                        for (var i = 0; i < numArr.length; i++) {
                                            if (numArr[i] == '.') {
                                                html += '<img class="dian" src="' + DOMAIN_STATIC + '/statics/img/activity/1_dian.png"/> '
                                            }
                                            else {
                                                html += '<img src="' + DOMAIN_STATIC + '/statics/img/activity/1_' + numArr[i] + '.png"/> '
                                            }
                                        }
                                        $("#awarded").html(datas.jackpotSize + '<span class="awarded_gbc">GBC</span>')
                                        $("#obtained").html(datas.userAmount + '<span class="awarded_gbc">GBC</span>')
                                        setTimeout( function() {
                                            $this.alertHtml_box(bitbank.L("恭喜您获得GBC", datas.currAmount));
                                        }, 1000);
                                    }
                                    else {
                                        html += '<img src="' + DOMAIN_STATIC + '/statics/img/activity/1_0.png"/> ';
                                        setTimeout(function () {
                                            $this.alertHtml_box(bitbank.L("很遗憾，没有获得奖品。", datas.currAmount));
                                        }, 1000);
                                    }
                                    $(".scroll").html(html);
                                    $(".scroll").removeClass("scroll_gif");
                                    $(".scroll").css({
                                        "background-image": ""
                                    })
                                    if (isphone) {

                                    } else {
                                        $(".scroll").addClass("scroll_end")
                                    }
                                    $draw_button.bind('click', $this.goDraw.bind($this)); // 抽奖按钮绑定点击事件
                                }, 5000);
                            }
                        }
                }
            },
            error:function(err){
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
        var $this = this;

        var $drawBut = $("#drawBut"); //按钮
        var $draw_button = $(".draw-button");// 按钮
        var $toupiaoIcon = $("#toupiaoIcon"); //投票悬浮按钮 
        var $noStart = $(".noStart"); //数量单位     
        var $scroll_num= $("#scroll-num");  //中间显示的数字

        $draw_button.unbind('click'); // 按钮取消点击事件
        $("#token").val(data.token)
        $("#title").html(data.eventTitleJson)
        $("#info").html(data.eventContentJson)
        $("#awarded").html(data.jackpotSize + '<span class="awarded_gbc">GBC</span>')
        $("#obtained").html(data.userAmount + '<span class="awarded_gbc">GBC</span>')
        $("#rulesBox").html(data.eventRuleJson)
        $(".tip-box").html(bitbank.L("抽奖机会：") + data.chance + bitbank.L("次"))
        $toupiaoIcon.css({ 'display': data.isVoteShow == "1" ? 'block' : "none" })
        switch (data.viewFlag) {
            case '01': // 01未开始页面
                $drawBut.html(bitbank.L("未开始"))
                $drawBut.css({
                    'background': '#485E6E',
                    "color": "rgba(255,255,255,0.3)"
                })
                break;
            case '02': // 02全部中出
                $drawBut.html(bitbank.L("奖池已全部发放"))
                $drawBut.css({
                    'background': '#485E6E',
                    "color": "rgba(255,255,255,0.3)"
                })
                break;
            case '03': // 03可抽奖”页面
                $drawBut.html(bitbank.L("抽奖"))
                $drawBut.css({
                    'background': '#485E6E',
                    "color": "rgba(255,255,255,1)",
                    "cursor": "pointer"
                })
                if (isphone) {
                    // $drawBut.css({ "margin-top": "0.6rem" })
                }
                else {
                    // $drawBut.css({ "margin-top": "53px" })
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
            // case '04': // 04 无权限抽奖
            //     $drawBut.html(bitbank.L("抽奖"));  
            //     $drawBut.css({
            //                 'background': '#485E6E',
            //                 "color":"rgba(255,255,255,0.3)",
            //                 "margin-top": "10px",
            //             })
            //     break;
            // case '05': // 05 抽奖次数用完 
            //     $drawBut.html(bitbank.L("已抽奖"))
            //     $drawBut.css({
            //         'background': '#485E6E',
            //         "color": "rgba(255,255,255,0.3)"
            //     })
            //     if (isphone) {
            //         $drawBut.css({ "margin-top": "0.6rem" })
            //     }
            //     else{
            //         $drawBut.css({ "margin-top": "53px" })
            //     }
            //     break;
            default:
                break;
        }

    }
    
    draw.isLogin = function(){
        	return $.cookie(UON) == "1" && $.cookie(UNAME) != null && $.cookie(UID) != null;
        };

    module.exports = draw;
});
/**
 * 利用img来做抽奖
 * draw.beginDraw = function () {
    var $this = this
    var list = $('#scroll-num .scroll');
    var img_arr = [];
    for (var i = 0; i < 10; i++) {
        var url = DOMAIN_STATIC + "/statics/img/activity/" + i + ".png";
        var imghtml = '<img src="' + url + '" class="num num_img_' + i + '"></img>';
        img_arr.push(imghtml);
    }
    it = setInterval(function () {
        $this.loop(list, img_arr)
    }, 50)
}

draw.loop = function (list, img_arr) {
    var maxNum = "";
    var r_num = Math.ceil(Math.random() * 100);
    if (r_num == 100) {
        list.html(img_arr[1] + img_arr[0] + img_arr[0]);
    }
    else if (r_num >= 10) {
        list.html(img_arr[r_num.toString().charAt(0)] + img_arr[r_num.toString().charAt(1)]);
    }
    else {
        list.html(img_arr[r_num]);
    }
    list.css("backgroundSize", "contain");

}
 */
