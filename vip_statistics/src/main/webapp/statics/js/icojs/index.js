var ainim = null;
var box = null;
$(function () {
    ainim = new CarControll();
    box = new BoxControll();
    ainim.initRes(function(){
        ainim.isReady = true;
        if(box.isReady){
            box.mapImg = box.getRes('box_bg1');
            loop()
        }
    })
    box.initRes(function(){
        box.isReady = true;
        if(ainim.isReady){
            box.mapImg = box.getRes('box_bg0');
            loop();
        }
    })
    $(document).scroll(function(){
            var top = $(window).scrollTop();
            if(top <= 5){
                    $(".navbar").css({"background":"transparent","top":"0px"})
                    $(".bk-menuBar").css({"background":"transparent","top":"0px"})
                    no_scrmove();
            }else{
                    $(".navbar").css({"background":"#1C1D1A","top":"0"})
                    $(".bk-menuBar").css({"background":"#1C1D1A","top":"0"})
                    scrmove();
            }
                win_scroll();  
    }) 
    function win_scroll(){  
        var top_left= document.body.scrollLeft;
        var thiswidth =  $(document).width();
        $(".bk-menuBar").css("left",-top_left+"px");
        $(".bk-menuBar").css("min-width",thiswidth);
    }
    initPage();

    //vote_add();
    function vote_add(){
        return;
        $.ajax({   //投票入口
            url:DOMAIN_VIP + "/vote/activity",
            type:"GET",
            dataType:"json",
            success:function(data){
                if( data.isSuc ){
                    var datas = data.datas;
                    var cookieid = $.cookie("activity");
                    if(datas.activityId != cookieid){
                        if( datas.state == 1 ){
                            $(".vote_home_href").attr("href",datas.url+"/");
                            $(".vote_home_href").attr("cookie_name",datas.activityId);
                            $(".float_vote").show();
                        }
                        else{
                            $(".float_vote").hide();
                        }
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
    $(".vote_home_close").on("click touchend",function(ev){
        ev.preventDefault();
        var cookie_name = $(".vote_home_href").attr("cookie_name");
        $(".float_vote").remove();
        $.cookie("activity",cookie_name, { expires: 365});
    })

})

// 首页导航背景样式
function scrmove(){
        $(".logined>a").css({
            "background":"#5c5964",
            "color":"rgba(255,255,255,1)"
        })
        $(".logined>a").mouseout(function(){
            $(this).css("color","rgba(255,255,255,1)")
        })
}
function no_scrmove(){
        $(".logined>a").css({
            "background":"none",
            "color":"rgba(255,255,255,0.5)"
        })
        $(".logined>a").hover(function(){
            $(this).css("color","rgba(255,255,255,1)")
        },function(){
            $(this).css("color","rgba(255,255,255,0.5)")
        })
}
// 首页导航背景样式  END
//初始化页面元素
function initPage(){
   
   $("#lang").mouseover(function(){
        $("#lang .lang-list").css("display","block");
   });
   $("#lang").mouseout(function(){
        $("#lang .lang-list").css("display","none");
   });
    if(!isphone){
        $(".top .table-list .item .text2").css({"display": "inline-block","width": "270px"})
    }
    document.onmousemove = mouseMove; //注册鼠标移动获取坐标事件

   shareClick();
    
}
$(".btn").on("click",function(){
    var hrefs = $(this).attr("myHref");
     window.location.href = hrefs;
})
//分享按钮的点击事件注册
function shareClick(){
    $("#seven .weibo").click(function(){
        window.open("http://weibo.com/HelloBitglobal")
    })
    $("#seven .face").click(function(){
        window.open("https://www.facebook.com/HelloBitglobal/")
    })
    $("#seven .twitter").click(function(){
        window.open("https://twitter.com/HelloBitglobal")
    })
    $("#seven .email").click(function(){
        window.location.href = "/terms/termsContactUs/";
    })
    if(!isphone){
        $("#seven .weixin").mouseover(function(){
            $(".seven .content .bottom .item.weixin i .qrcode").css({'display':'block'})
        })
        $("#seven .weixin").mouseleave(function(){
            $(".seven .content .bottom .item.weixin i .qrcode").css({'display':'none'})
        })
        $("#seven .qq").mouseover(function(){
            $(".seven .content .bottom .item.qq i .qrcode").css({'display':'block'})
        })
        $("#seven .qq").mouseleave(function(){
            $(".seven .content .bottom .item.qq i .qrcode").css({'display':'none'})
        })
    }else{
        $("#seven .weixin").click(function(){
            $(".seven .content .bottom .item.weixin i .qrcode").toggle()
        })
        $("#seven .qq").click(function(){
            $(".seven .content .bottom .item.qq i .qrcode").toggle()
        })
    }
}
//根据鼠标位置显示小车动画里的提示文字
function mouseMove(ev){
    ev = ev || window.event;
    var mousePos = mousePosition(ev);

    var picbox = document.getElementById("picbox")
    var dx = mousePos.x- picbox.offsetLeft
    var dy = mousePos.y - picbox.offsetTop
    if(dx>=15 && dx<=165 && dy >=30 && dy <= 230){
        $("#four .div1").show();
    }else{
        $("#four .div1").hide();
    }

    if(dx>=585 && dx<=875 && dy >=30 && dy <= 230){
        $("#four .div2").show();
    }else{
        $("#four .div2").hide();
    }

    if(dx>=15 && dx<=165 && dy >=280 && dy <= 400){
        $("#four .div3").show();
    }else{
        $("#four .div3").hide();
    }

}
//鼠标位置转化函数
function mousePosition(ev){
    if(ev.pageX || ev.pageY){
    return {x:ev.pageX, y:ev.pageY};
    }
    return {
        x:ev.clientX + document.body.scrollLeft - document.body.clientLeft,
        y:ev.clientY + document.body.scrollTop - document.body.clientTop
    };
}



// 小车,box主循环
function loop(){
    updateCar()  //更新小车
    updateBox()
    requestAnimationFrame(loop)
}

//更新小车
function updateCar(){
    ainim.ctx.clearRect(0, 0, ainim.canvas.width, ainim.canvas.height); // 清除画布
    ainim.drawMap();
    ainim.drawCar();
    ainim.update();
}
//更新box
function updateBox(){
    box.ctx.clearRect(0, 0, ainim.canvas.width, ainim.canvas.height); // 清除画布
    box.drawCar();
    box.update();
    box.drawMap();
    
}

//鼠标指上分享,改变分享的图片和文字颜色
function changeIcon(name,num){
    // if(num == 1){
    //     switch (name) {
    //     case 'xinlang':
    //         $(".icon .xinlang").find('i').css({'background-position':'-10px -50px'})
    //         $(".icon .xinlang").css({"color":"#676A73"})
    //         break;
    //     case 'face':
    //         $(".icon .face").find('i').css({'background-position':'-10px -130px'})
    //         $(".icon .face").css({"color":"#676A73"})
    //         break;
    //     case 'twitter':
    //         $(".icon .twitter").find('i').css({'background-position':'-10px -210px'})
    //         $(".icon .twitter").css({"color":"#676A73"})
    //         break;
    //     default:
    //         break;
    //     }
    // }else if(num ==2){
    //     switch (name) {
    //     case 'xinlang':
    //         $(".icon .xinlang").find('i').css({'background-position':'-10px -10px'})
    //         $(".icon .xinlang").css({"color":"#B1B1B1"})
    //         break;
    //     case 'face':
    //         $(".icon .face").find('i').css({'background-position':'-10px -90px'})
    //         $(".icon .face").css({"color":"#B1B1B1"})
    //         break;
    //     case 'twitter':
    //         $(".icon .twitter").find('i').css({'background-position':'-10px -170px'})
    //         $(".icon .twitter").css({"color":"#B1B1B1"})
    //         break;
    //     default:
    //         break;
    //     }

    // }
    
}
// var pd, ph, pm, ps,nowinv,NowTime,EndTime;
// function startTimeAinim() {
//     $.ajax({
//         url:DOMAIN_VIP+"/getDealTime",
//         type:"GET",
//         "dataType":"json",
//         success:function(data){
//             var datas = data.datas;
//             if(data.isSuc){
//                 NowTime = datas.currentTime;
//                 EndTime = datas.dealTime;
//                 setTime(NowTime,EndTime);
//                 nowinv = setInterval(function(){ 
//                     NowTime += 1000;
//                     setTime(NowTime,EndTime); 
//                 }, 1000);
//             }
//             else{
//                 console.log("出错了,data="+data);
//             }
//         },
//         error:function(err){
//             console.log(err)
//         }
//     })
// }
// function setTime(NowTime,EndTime) {
//     var t   = EndTime  - NowTime;    
//     var d = 0;
//     var h = 0;
//     var m = 0;
//     var s = 0;
//     if (t >= 0) {
//         d = Math.floor(t / 1000 / 60 / 60 / 24);
//         h = Math.floor(t / 1000 / 60 / 60 % 24);
//         m = Math.floor(t / 1000 / 60 % 60);
//         s = Math.floor(t / 1000 % 60);
//     }
//     d = d.toString().length < 2 ? '0' + d : d
//     h = h.toString().length < 2 ? '0' + h : h
//     m = m.toString().length < 2 ? '0' + m : m
//     s = s.toString().length < 2 ? '0' + s : s

//     $(".count-down span.time").eq(0).html(d + '<i></i>');
//     if (d != pd) $(".count-down span.time i").eq(0).addClass('ainim');
//     $(".count-down span.time").eq(1).html(h + '<i></i>');
//     if (h != ph) $(".count-down span.time i").eq(1).addClass('ainim');

//     $(".count-down span.time").eq(2).html(m + '<i></i>');
//     if (m != pm) $(".count-down span.time i").eq(2).addClass('ainim');

//     $(".count-down span.time").eq(3).html(s + '<i></i>');
//     $(".count-down span.time i").eq(3).addClass('ainim');

//     pd = d;
//     ph = h;
//     pm = m;
//     ps = s;
// }