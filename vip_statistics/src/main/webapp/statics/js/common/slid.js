
    function slide( data ){
        var _self = this;
        _self.Id = $(data.id);
        _self.timer = "";
        _self.nowleft = 0;
        _self.Class = data.class;
        _self.ulWidth = data._width;
        this.relay();
    };
    slide.prototype = {
        relay:function(){
            this.nowCss(); 
        },
        nowCss:function(){
            var _self=this;
            var newUl = this.Id.next();
            newUl.css("width",_self.ulWidth)
            this.time(newUl);
            this.overOut(newUl);
        },
        slidmove:function(newUl){
            var newLeft = this.ulWidth+this.nowleft;
            newUl.css("left",newLeft);
            this.Id.css("left",this.nowleft);
            this.nowleft--;
            if( newLeft == 0 ){
                this.nowleft = 0;
            }
        },
        overOut:function(newUl){
            var _self = this;
            this.Id.parent().hover(function(){
                clearInterval(_self.timer);
            },
            function(){
                _self.time(newUl);
            })
           
        },
        time:function(newUl){
            var _self = this;
            this.timer = setInterval(function(){
                _self.slidmove(newUl);
            },30)
        }
    };
 