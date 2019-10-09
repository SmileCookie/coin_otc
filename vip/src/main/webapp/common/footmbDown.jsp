<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<style type="text/css">

</style>
<div class="bk-footer">

</div>
<script type="text/javascript">

    if(location.href.indexOf("/trade") != -1){
        require(["module_market","module_asset","module_trans","module_common"],function(market,asset,trans){
            market.init(2000);
            asset.init();
            trans.pageIndexInit("${market}",2000);
        });
    }else if(location.href.indexOf("/entrust") != -1){
        require(["module_market","module_asset","module_trans","module_common"],function(market,asset,trans){
            market.init(2000);
            asset.init();
            // trans.pageRecordInit("${market}",5000);
            trans.getEntrustInit()
        });
    }else{
        require(["module_market","module_asset","module_common"],function(market,asset){
            market.init(2000);
            asset.init();
        });
    }

    <%--$(function(){--%>
        <%--$.ajax({--%>
            <%--url:"${vip_domain }/manage/popout",--%>
            <%--type:"GET",--%>
            <%--dataType:'json',--%>
            <%--success:function(data){--%>
                <%--if(data.isSuc && data.datas != ""){--%>
                    <%--draw_surprise({--%>
                        <%--num:data.datas--%>
                    <%--});--%>
                <%--}--%>
            <%--},--%>
            <%--error:function(err){--%>
                <%--console.log(err)--%>
            <%--}--%>
        <%--})--%>

    <%--})--%>
    function draw_surprise(data) { //抽奖翻倍弹窗
        var docW = $(document).width();
        var docH = $(document).height();
        var winW = $(window).width();
        var winH = $(window).height();
        var html = '<div class="foot_vote_alert"></div>';
        var html_home = '<div class="foot_home_box">' +
            '<div class="draw_sup"></div>'
            '<div class="draw_sup"></div>'+
            '<h3>'+data.num+' ABCDEF</h3>' +
            '<div class="text_box">${L:l(lan,'恭喜您投中新币，奖励翻倍！')}</div>'+
            '<div class="foot_btn">' + bitbank.L("知道了") + '</div>' +
            '</div>';
        $("body").append(html);
        $("body").append(html_home);
        $(".foot_vote_alert").css({
            "width": Math.max(docW, winW),
            "min-width": "320px",
            "height": Math.max(docH, winH),
            "z-index": 10000
        });

        $(".foot_btn").on("click",function(){
            $(".foot_vote_alert").remove();
            $(".foot_home_box").remove();
        })
    }

    //vote_btn();//投票入口
    function vote_btn() {
        return;
        $.ajax({   //投票入口
            url:"${vip_domain }/vote/activity",
            type:"GET",
            dataType:"json",
            success:function(data){
                if( data.isSuc ){
                    var datas = data.datas;
                    $(".vote_status").attr("state",datas.state);
                    $(".vote_status").attr("activityId",datas.activityId);
                    if( datas.state == 1 || datas.state == 2 || datas.state == 3 ){
                        $(".vote_status").attr("href",datas.url+"/");
                        $(".vote_status").css("display",'inline-block');
                    }
                    else{
                        $(".vote_status").hide();
                    }
                }
                else{
                    $(".vote_status").hide();
                }

            },
            error: function (err) {
                console.log(err);
            }
        })
    }
    //抽奖入口
    //draw_fun();
    function draw_fun() {
        return;
        $.ajax({
            url:"${vip_domain }/lucky/getluckyInfo",
            type:"GET",
            dataType:"json",
            success: function(data){
                if(data.isSuc){
                    if( data.datas.isShow == 1 ){
                        $(".draw_li").show();
                    }else{
                        $(".draw_li").hide();
                    }
                }else{
                    $(".draw_li").hide();
                }

            },
            error:function(err){
                console.log(err);
            }
        })
    }

    if(JuaBox.isMobile()){
        $(".share_it .weixin").click(function(){
            $(".share_it .qq .qrcode").hide()
            $(".share_it .weixin .qrcode").toggle()
        })
        $(".share_it .qq").click(function(){
            $(".share_it .weixin .qrcode").hide()
            $(".share_it .qq .qrcode").toggle()
        })
    }
    else{
        $(".share_it .weixin").mouseover(function(){
            $(".share_it .weixin .qrcode").css({'display':'block'})
        })
        $(".share_it .weixin").mouseleave(function(){
            $(".share_it .weixin .qrcode").css({'display':'none'})
        })
        $(".share_it .qq").mouseover(function(){
            $(".share_it .qq .qrcode").css({'display':'block'})
        })
        $(".share_it .qq").mouseleave(function(){
            $(".share_it .qq .qrcode").css({'display':'none'})
        })
    }

</script>
<script>
    // get friend link
    (function(){
        var link_wp = $('#lk_wp'),
            str = '';
        if(window.location.pathname === '/'){
            $.ajax({
                url: '/getFriendUrl',
                type: 'GET',
                dataType: 'json',
                success: function(res){
                    var result = res.datas;
                    if(result.length){
                        result.forEach(function(v,k){
                            str += '<a href="'+v.url+'" target="_blank">'+v.name+'</a>'
                        })
                    }

                    link_wp.html(str);
                }
            })
        } else {
            var firwp = link_wp.parent();
            firwp.prev().css({
                paddingBottom:'60px'
            });
            firwp.remove();
        }
    })();


    (function() {
        var init = function () {
            var updateOrientation = function () {//方向改变执行的函数
                var orientation = window.orientation;
                switch (orientation) {
                    case 90:
                    case -90:
                        orientation = 'landscape'; //这里是横屏
                        break;
                    default:
                        orientation = 'portrait'; //这里是竖屏
                        break;
                }
                //html根据不同的旋转状态，加上不同的class，横屏加上landscape，竖屏
                //加上portrait
                document.body.parentNode.setAttribute('class', orientation);
            };
            // 每次旋转，调用这个事件。
            window.addEventListener('orientationchange', updateOrientation, false);
            // 事件的初始化
            updateOrientation();
        };
        window.addEventListener('DOMContentLoaded', init, false);
    })();
    window.addEventListener('orientationchange',function(){
        window.location.href = window.location.href;
    },false);

</script>
