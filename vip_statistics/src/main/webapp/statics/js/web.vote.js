define(function(require, exports, module) {
    "require:nomunge,exports:nomunge,module:nomunge";
    var vote = {};
    var pd,ph,pm,ps,timer;
    var tmpl_s = require("module_tmpl");
    vote.init = function(){
        var $this = this;
        var url = location.search;
        this._state = "" //$(".vote_status").attr("state");  //活动状态
        this._activityId = "";
        this._canVoteFlag = null;   //是否已投票
        this._coinId = []; //投票币种id
        this._selectCount = 0;
        this._voteNum = 0; //投几种币
        this.draw_jin();
        this.activeid(function () {
            $this.getdata(true);
            $this.getTime();
        });
        $("#home_top").on("click",function(){
            $(window).scrollTop(0);
        })
    }
    vote.activeid = function (cb) {
        return;
        var $this = this;
        $.ajax({   //投票入口
            url: DOMAIN_VIP + "/vote/activity",
            type: "GET",
            dataType: "json",
            success: function (data) {
                if (data.isSuc) {
                    var datas = data.datas;
                    var preview_state = $this.GetQueryString("preview_state");//预览
                    vote._state = datas.state;
                    if (preview_state != "" && preview_state != null) {
                        vote._activityId = preview_state;
                    } else {
                        vote._activityId = datas.activityId;
                    }
                    if (cb) {
                        cb();
                    }
                }
            },
            error: function (err) {
                console.log(err);
            }
        })
    }
    vote.draw_jin = function () {  //抽奖浮动窗
        return;
        $.ajax({
            url: DOMAIN_VIP + "/lucky/getluckyInfo",
            type: "GET",
            dataType: "json",
            success: function (data) {
                if (data.isSuc) {
                    if (data.datas.isShow == 1) {
                        $(".draw_li").show();
                    }
                    else {
                        $(".draw_li").hide();
                    }
                }else {
                    $(".draw_li").hide();
                    window.location.href = DOMAIN_VIP
                }

            },
            error: function (err) {
                console.log(err);
            }
        })
    }
    vote.clickVote = function (data) {
        var $this = this;
        var selectCount = $this._selectCount;
        $(".status_click").on("click touchend",function(ev){
            ev.preventDefault();
            if ($(this).html() == "+1") return false;
            if( vote._state != 1 ) return false;  //活动状态不是进行中，无法进行点击操作
            if( !$this.isLogin() ) return $this.alertHtml(true);   //未登录无法投票
            // if (!vote._canVoteFlag) { //是否已经投过票
            //     JuaBox.showWrong(bitbank.L("本次活动每人最多享有") + selectCount + bitbank.L("票"));
            //     return false;
            // }
            // if (selectCount > 0) {
            //     if (vote._voteNum >= selectCount) return JuaBox.showWrong(bitbank.L("本次活动每人最多享有") + selectCount + bitbank.L("票"));
            // }
            var coin_id = parseInt($(this).attr("coin-id"));
            //vote._voteNum ++; //投几种币 
            $(this).attr("data-status","yes");
            $this.submit_vote(coin_id, $(this));
            
        })       
    }

    vote.submit_vote = function(coin_id,obj){
        var $this = this;
        $.ajax({
            url: DOMAIN_VIP + "/vote/postVote?coinIds=" + coin_id + "&activityId=" + vote._activityId,
            type: "GET",
            dataType: "json",
            success: function (data) {
                if (data.isSuc) {
                    JuaBox.showWrong(bitbank.L("投票成功，感谢您的参与。"));
                    obj.html("+1").removeClass("rule_state_1").addClass("rule_state_vote");
                    $this.getdata(true);
                }
                else {
                    JuaBox.showWrong(bitbank.L(data.des));
                }
            },
            error: function (err) {
                console.log(err)
            }
        })
    }

    vote.alertHtml = function(types,data,url){
        var $this = this;
        this.docW = $(document).width();
        this.docH = $(document).height();
        this.winW = $(window).width();
        this.winH = $(window).height();
        var h3html = "";
        var btnhtml = "";
        var texthtml = "";
        if (types){
            h3html = '<h3>' + bitbank.L("请登录后再进行投票") + '</h3>';
            btnhtml = '<div class="vote_login"><a class="prev_login_href" href="' + DOMAIN_VIP + '/login/" target="_self">' + bitbank.L("登录") + '</a></div>';
            texthtml = '<div class="vote_reg"><a class="reg_a" href="' + DOMAIN_VIP + '/register/" target="_self">' + bitbank.L("还没有账户") + '</a></div>';
        }
        else{
            h3html = '<h3 class="martop_60">' + data + '</h3>';
            btnhtml = '<div class="vote_login"><a class="prev_login_href" href="' + url + '" target="_self">' + bitbank.L("立即参与") + '</a></div>';
        }

        var html = '<div class="vote_alert close_alert"></div>';
        var html_home = '<div class="vote_home">'+
                            '<div class="vote_close close_alert"></div>'+
                            h3html + btnhtml + texthtml+
                        '</div>';
        $("body").append(html);
        $("body").append(html_home);
        $(".vote_alert").css({
            "width" : Math.max(this.docW,this.winW),
            "min-width" : "320px",
            "height" : Math.max(this.docH,this.winH),
            "z-index" : 1100
        });
        $(".close_alert").on("click", function () {
            if (types){
                $(".close_alert").remove();
                $(".vote_home").remove();
            }else{
                window.location.reload();
            }
            
        })
        $(".prev_login_href").on("click",function () { 
            var prevherfss = window.location.pathname;
            $.cookie("prevhref",prevherfss,{path:"/"});
        })
    }
    vote.isLogin = function(){
        return $.cookie(UON) == "1" && $.cookie(UNAME) != null && $.cookie(UID) != null;
    }

    vote.getdata = function(bool){
        var $this = this;
        var listDiv = "#listData";
        $.ajax({
            url:DOMAIN_VIP + "/vote/init?activityId="+vote._activityId+"&lan="+LANG+"&state="+vote._state,
            type:"GET",
            dataType:"json",
            success: function(data){
               if( data.isSuc ){
                   console.log(data)
                    $this.setdata(data.datas.activity);
                    vote._canVoteFlag = data.datas.canVoteFlag;
                    vote._selectCount = data.datas.activity.selectCount;
                   var activityLog = data.datas.activityLog;
                   if (bool){
                       $(listDiv).html(tmpl("tmpl-" + (listDiv.replace(/[#,.]/g, "")), $this.formatListDetail(data.datas.coin, activityLog)));
                       $this.defaultStatus(data.datas);
                    }
               }
            },
            error:function(err){
                console.log(err)
            }
        })
    }
    vote.defaultStatus = function (data){
        var _submit = $(".submit");
        var _status = $(".status");
        var $this = this;
        if( vote._state == 1 ){  //活动正在进行中
            if( $this.isLogin() ){   
                if(vote._canVoteFlag){   //用户已登录 判断用户是否已投票
                    _submit.html(bitbank.L("提交投票"));
                    _submit.addClass("submit_active");
                    $this.clickVote(data.activity);
                }else{
                    $this.voteResults(data.activityLog);
                    _submit.html(bitbank.L("已投票"));
                    _status.removeClass("rule_state_1").addClass("rule_state_2");
                }
            }else{
                $this.clickVote(data.activity);
                _submit.html(bitbank.L("提交投票"));
                _submit.addClass("submit_active");
            }
        }
        else if( vote._state == 2 ){
            _submit.html(bitbank.L("投票暂停"));
        }
        else if( vote._state == 3 ){
            _submit.html(bitbank.L("投票已结束"));
            if( $this.isLogin() ){ 
                if( !vote._canVoteFlag ){ 
                    $this.voteResults(data.activityLog);
                }
            }
        }

    }
    vote.voteResults = function(data){
        for(var i=0; i<data.length; i++){
            var num = data[i].voteId;
            $("#coinId_"+num).html("+1").addClass("rule_state_vote");
        }
    }
    vote.setdata = function(data){
        $("#title").html(data.activityNameJson);
        $("#title_p").html(data.activityContentJson);
        $("#rule .vote_foot_text").html(data.activityRuleJson);
    }

    vote.formatListDetail = function (json, activityLog){
        var $this = this;
        var record = [];
        for(var i=0; i<json.length; i++ ){
            
            var rate = json[i].rate;
            record[i] = {};
            record[i].titlename = json[i].coinNameJson; //币种简称
            record[i].name = json[i].coinFullNameJson; //币种全称
            record[i].votecount = json[i].voteCount; // 得票数
            record[i].rate = json[i].rate;     //占比
            record[i].coinId = json[i].coinId; //币种ID
            record[i].urlJson = json[i].urlJson //币种外链
            record[i].state = vote._state;  //活动状态
            record[i].canVoteFlag = vote._canVoteFlag; //是否已投票
        
            for (var j = 0; j < activityLog.length; j++) {
                if ( activityLog[j].voteId == json[i].coinId ){
                    record[i].vote_true = 1;
                    break;
                }
                else{
                    record[i].vote_true = 0;
                }
            }
        }
        return record;
    }
    vote.setIntval = function(endTime,currentTime){
        var $this = this ;
        var ends = endTime;
        var current = currentTime
        $this.currtime(endTime,currentTime);
        timer = setInterval(function(){
            current += 1000;
            $this.currtime(ends,current);
        },1000);
    }

    vote.getTime = function(start,ends){
        var $this = this ;
        $.ajax({
            url:DOMAIN_VIP + "/vote/time?activityId=" + vote._activityId,
            type:"GET",
            dataType:"json",
            success: function(data){
                console.log(vote._state)
                console.log(new Date(data.datas.endTime))
                if(vote._state != 1&&vote._state != 3){
                    $this.setIntval(0,data.datas.currentTime)
                }else if(vote._state == 3){
                    $(".time_box").html('<div class="active_end">'+bitbank.L("本轮投票已结束")+'</div>');
                }else{
                    $this.setIntval(data.datas.endTime,data.datas.currentTime)
                }
            },
            error:function(err){
                console.log(err);
            }
        })
    }

    vote.currtime = function(endTime,currentTime){
        var t = endTime - currentTime;
        var d = 0;
        var h = 0;
        var m = 0;
        var s = 0;
        if( t >= 0 ){
            d = Math.floor(t / 1000 / 60 / 60 / 24);
            h = Math.floor(t / 1000 / 60 / 60 % 24);
            m = Math.floor(t / 1000 / 60 % 60);
            s = Math.floor(t / 1000 % 60);
        }
        d = d.toString().length < 2 ? "0" + d : d;
        h = h.toString().length < 2 ? "0" + h : h;
        m = m.toString().length < 2 ? "0" + m : m;
        s = s.toString().length < 2 ? "0" + s : s;

        $(".days").html( d + "<i class='item'></i>" );
        if ( d != pd ) $(".days i.item").addClass("ainim");
        $(".hrs").html( h + "<i class='item'></i>" );
        if ( h != ph ) $(".hrs i.item").addClass("ainim");
        $(".min").html( m + "<i class='item'></i>" );
        if ( m != pm ) $(".min i.item").addClass("ainim");
        $(".sec").html( s + "<i class='item'></i>");
        if ( s != ps )  $(".sec i.item").addClass("ainim");
        pd = d;
        ph = h;
        pm = m;
        ps = s;
    }
    vote.removeByValue = function(arr, val) {
        for(var i=0; i<arr.length; i++) {
          if(arr[i] == val) {
            arr.splice(i, 1);
            break;
          }
        }
      }
      vote.GetQueryString = function(name){
           var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
           var r = window.location.search.substr(1).match(reg);
           if(r!=null)return  unescape(r[2]); return null;
      }
    

    module.exports = vote;
});