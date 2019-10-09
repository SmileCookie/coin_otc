
define(function(require, exports, module) {
    "require:nomunge,exports:nomunge,module:nomunge";
    var wheel = {

    };
    wheel.wheeler = function(box,data){
        var barColor="#333";
        if(data&&data.barcolor){
            barColor=data.barcolor;
        }
        var scrollColor = "rgba(153,153,153,0)";
        if(data&&data.scrollcolor){
            scrollColor=data.scrollcolor;
        }
        var main = $(box);
        var mainHtml = '<div class="clearfix scrollerbox">'+main.html()+'</div>';
        main.html(mainHtml);
        var mainBox = main.find('.scrollerbox');
        var boxHeight=parseInt(mainBox.css('height').split("px")[0]);
        // console.log(boxHeight);
        var heighter = main.height();
        // if (heighter < boxHeight) {
            var minToper = 0;
            main.css({
                'position': 'relative',
                'overflow':'hidden'
            });
            mainBox.css({
                'position': 'absolute',
                'width':'100%',
                'top':0,
                'left':0
            });
            main.append('<div class="barbox"></div>');
            var barbox = main.find('.barbox')
            barbox.css({
                'position': 'absolute',
                'background': scrollColor,
                'width':'8px',
                'height':heighter+'px',
                'right':'-8px',
                'top':0
            });
            barbox.append('<div class="bar"></div>');
            var barH = parseInt(heighter*(heighter/boxHeight));
            var bar = barbox.find('.bar');
            bar.css({
                'position': 'absolute',
                'top':minToper,
                'right':'1px',
                'background': barColor,
                'width':'6px',
                'height':barH+'px',
                'min-height':'15px',
                'max-height':(heighter-minToper*2)+'px',
                'border-radius':'6px',
                'opacity':0.5
            });
            bar.hover(function() {
                bar.css({
                    'opacity':1
                });
            }, function() {
                bar.css({
                    'opacity':0.5
                });
            });
            var barHeight = parseInt(bar.css('height').split("px")[0]);
            var maxToper = heighter-barHeight-minToper-5;
            main.hover(function() {

                var mBox = main.find('.scrollerbox');
                boxHeight=parseInt(mBox.css('height').split("px")[0]);
                var iH = parseInt(heighter*(heighter/boxHeight));
                barbox.find('.bar').css({
                    'height':iH+'px'
                });
                maxToper = heighter-iH-minToper;

                barbox.animate({'right':'0px'}, 200)
            }, function() {
                barbox.animate({'right':'-8px'}, 200)
            });
            main.on("mousewheel DOMMouseScroll", function (e) {
                e.preventDefault();
                var windNow = $(document).scrollTop();
                // console.log(windNow);
                var mBox = main.find('.scrollerbox');
                boxHeight=parseInt(mBox.css('height').split("px")[0]);
                var iH = parseInt(heighter*(heighter/boxHeight));
                barbox.find('.bar').css({
                    'height':iH+'px'
                });
                maxToper = heighter-iH;
                // var da = (e.originalEvent.wheelDelta && (e.originalEvent.wheelDelta > 0 ? 1 : -1)) ||  // chrome & ie
                //             (e.originalEvent.detail && (e.originalEvent.detail > 0 ? -1 : 1));              // firefox
                var da = (e.originalEvent.wheelDelta && (e.originalEvent.wheelDelta / 10)) ||  // chrome & ie
                            (e.originalEvent.detail && (e.originalEvent.detail / 1));
                var toper = parseInt(bar.css('top').split("px")[0]);
                var moveTo = toper; 
                if (e.originalEvent.wheelDelta ) {
                    moveTo-=da;
                    if(moveTo<=minToper||moveTo>= maxToper){
                        $(document).scrollTop(windNow-da);
                    }
                } else if(e.originalEvent.detail){
                    moveTo+=da;
                    if(moveTo<=minToper||moveTo>= maxToper){
                        $(document).scrollTop(windNow+da);
                    }
                };
                barMove(moveTo);
            });
            drag(bar);
            function drag(drager){ 
                var move=false;//移动标记 
                var _x,_y;//鼠标离控件左上角的相对位置 
                var drager = $(document).find(drager)
                drager.mousedown(function(e){ 
                    move=true; 
                    _x=e.pageX-parseInt($(this).css("left")); 
                    _y=e.pageY-parseInt($(this).css("top")); 
                }); 
                $(document).mousemove(function(e){ 
                    //e.preventDefault();
                    if(move){ 
                        var x=e.pageX-_x;//控件左上角到屏幕左上角的相对位置 
                        var y=e.pageY-_y; 
                        barMove(y);
                    } 
                }).mouseup(function(){ 
                    move=false; 
                }); 
            }
            function barMove(topor){
                if (heighter < boxHeight) {
                    var nextTop = topor;
                    if(topor<=minToper){
                        nextTop = minToper;
                    }else if(nextTop>= maxToper){
                        nextTop = maxToper;
                    }
                    bar.css('top',nextTop);
                    mainBox.css('top',-(nextTop*(boxHeight/heighter)));
                }
            }
        };
    // }
    module.exports = wheel;
    (function(){ return this || (0,eval)('this'); }()).wheel = wheel;
});