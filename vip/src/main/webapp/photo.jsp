<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>视频</title>
<script type="text/javascript" src="/statics/js/common/zoom_assets/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="/statics/js/common/zoom_assets/jquery.smoothZoom.min.js"></script>
<script type="text/javascript" charset="utf-8">
  try{
    var curDomain = window.location.host;
    
    var curDomains = curDomain.split(".");
    if(curDomains.length == 3){
    	curDomain = curDomains[1] + "." + curDomains[2];
    }
    var oldDomain=document.domain;
    var ind=oldDomain.indexOf(curDomain);
    document.domain = oldDomain.substring(ind,oldDomain.length)
  } catch(msg) {
    document.domain = curDomain;   
  }
</script>
<script>
   jQuery.fn.extend({
	everyTime: function(interval, label, fn, times) {
		return this.each(function() {
			jQuery.timer.add(this, interval, label, fn, times);
		});
	},
	oneTime: function(interval, label, fn) {
		return this.each(function() {
			jQuery.timer.add(this, interval, label, fn, 1);
		});
	},
	stopTime: function(label, fn) {
		return this.each(function() {
			jQuery.timer.remove(this, label, fn);
		});
	}
});
   
   
jQuery.extend({
	timer: {
		global: [],
		guid: 1,
		dataKey: "jQuery.timer",
		regex: /^([0-9]+(?:\.[0-9]*)?)\s*(.*s)?$/,
		powers: {
			// Yeah this is major overkill...
			'ms': 1,
			'cs': 10,
			'ds': 100,
			's': 1000,
			'das': 10000,
			'hs': 100000,
			'ks': 1000000
		},
		timeParse: function(value) {
			if (value == undefined || value == null)
				return null;
			var result = this.regex.exec(jQuery.trim(value.toString()));
			if (result[2]) {
				var num = parseFloat(result[1]);
				var mult = this.powers[result[2]] || 1;
				return num * mult;
			} else {
				return value;
			}
		},
		add: function(element, interval, label, fn, times) {
			var counter = 0;
			if (jQuery.isFunction(label)) {
				if (!times) 
					times = fn;
				fn = label;
				label = interval;
			}
			interval = jQuery.timer.timeParse(interval);
			if (typeof interval != 'number' || isNaN(interval) || interval < 0)
				return;
			if (typeof times != 'number' || isNaN(times) || times < 0) 
				times = 0;
			times = times || 0;
			var timers = jQuery.data(element, this.dataKey) || jQuery.data(element, this.dataKey, {});
			if (!timers[label])
				timers[label] = {};
			fn.timerID = fn.timerID || this.guid++;
			var handler = function() {
				if ((++counter > times && times !== 0) || fn.call(element, counter) === false)
					jQuery.timer.remove(element, label, fn);
			};
			handler.timerID = fn.timerID;
			if (!timers[label][fn.timerID])
				timers[label][fn.timerID] = window.setInterval(handler,interval);
			this.global.push( element );
		},
		remove: function(element, label, fn) {
			var timers = jQuery.data(element, this.dataKey), ret;
			if ( timers ) {
				if (!label) {
					for ( label in timers )
						this.remove(element, label, fn);
				} else if ( timers[label] ) {
					if ( fn ) {
						if ( fn.timerID ) {
							window.clearInterval(timers[label][fn.timerID]);
							delete timers[label][fn.timerID];
						}
					} else {
						for ( var fn in timers[label] ) {
							window.clearInterval(timers[label][fn]);
							delete timers[label][fn];
						}
					}
					for ( ret in timers[label] ) break;
					if ( !ret ) {
						ret = null;
						delete timers[label];
					}
				}
				for ( ret in timers ) break;
				if ( !ret ) 
					jQuery.removeData(element, this.dataKey);
			}
		}
	}
});
//解除绑定
jQuery(window).bind("unload", function() {
   parent.$("#tinymask").unbind("click");
	jQuery.each(jQuery.timer.global, function(index, item) {
		jQuery.timer.remove(item);
	});
});
   function initPic(){
           var currentPicWidth=parent.currentPicWidth;
          var currentPicHeight=parent.currentPicHeight;
          var currentPicSrc=parent.currentPicSrc;  
	     $("#yourImageID").attr("src",currentPicSrc);
	
		$('#yourImageID').smoothZoom({
			width: currentPicWidth,
			height: currentPicHeight,
			button_SIZE: 22,
			button_ALIGN: "bottom right",
			zoom_OUT_TO_FIT: "NO",
			button_AUTO_HIDE: "YES",
			button_AUTO_HIDE_DELAY: 2
		});
     }  
     
     var inquesting=false;
	jQuery(function($){
	//每100毫秒检查一次父页面
	$('body').everyTime('100ms',function(){
	if(inquesting)
	  return;
	  inquesting=true;
          if(parent.picShowchanged){//如果改变过了
          parent.picShowchanged=false;
                 var currentPicWidth=parent.currentPicWidth;
                 var currentPicHeight=parent.currentPicHeight;
                  var currentPicSrc=parent.currentPicSrc;  
                  $(".noSel").remove();
                 
	        parent.$("#fancybox-frame").css({"width":currentPicWidth+"px","height":currentPicHeight+"px"});
	       
		var $p=parent.$("#tinybox");
		var left=$p.position().left;
		var top=$p.position().top;
		
		var width=$p.width();
		var height=$p.height();
			
		var newtop=top+(height-currentPicHeight-24)/2;
		var newleft=left+(width-currentPicWidth)/2;
	if (jQuery.browser.msie && jQuery.browser.version == "6.0"){
        parent.$("#tinybox").css({
        width: currentPicWidth+"px",
        height: (currentPicHeight+24)+"px",
        top:newtop+"px",
         left:newleft+"px"
      });
    }else
    {
        parent.$("#tinybox").animate({
        width: currentPicWidth+"px",
        height: (currentPicHeight+24)+"px",
        top:newtop+"px",
         left:newleft+"px"
       }, 300);
    }

           if(parent.currentPicIndex==0)
               parent.$("#picLeftControl").hide();
           else
               parent.$("#picLeftControl").show();
           if(parent.currentPicIndex==(parent.maxPicIndex-1))
               parent.$("#picRightControl").hide();
           else if(parent.currentPicIndex==0)
               parent.$("#picRightControl").show();
		   else
		       parent.$("#picRightControl").show();
        
           
           $("body").append("<img id='yourImageID' src='"+parent.currentPicSrc+"'/>");
	         	$('#yourImageID').smoothZoom({
			width: currentPicWidth,
			height: currentPicHeight,
			button_SIZE: 22,
			button_ALIGN: "bottom right",
			zoom_OUT_TO_FIT: "NO",
			button_AUTO_HIDE: "YES",
			button_AUTO_HIDE_DELAY: 2
		});
		
            }
       inquesting=false;
     });
	    
		initPic();
		 
	});
</script>
<style type="text/css">
.mF_fancy .prev,.mF_fancy .next{width:45px;height:75px;overflow:hidden;position:absolute;z-index:9;left:0px;cursor:pointer; top:38%;}
.mF_fancy .prev a,.mF_fancy .next a{display:block;color:#fff;text-align:center;text-decoration:none;visibility:hidden; opacity:0;-webkit-transition:all .4s;-moz-transition:all .4s;-o-transition:all .4s;}
.mF_fancy .prev a:hover,.mF_fancy .next a:hover{color:#f60;}
.mF_fancy .show a{ visibility:visible; opacity:1;}

.mF_fancy .prev, .mF_fancy .next {background: url(/statics/img/list_ico.png) 0px -968px no-repeat; cursor: pointer; filter: alpha(opacity=20); opacity: 0.2; }
.mF_fancy .next { left: auto; right: 0px; background-position: -49px -968px; }
.mF_fancy .prev:hover, .mF_fancy .next:hover { filter: alpha(opacity=100); opacity: 1; }
</style>
</head>
<body style="paddind:0;margin:0;">
<div style="paddind:0;margin:0;" class="mF_fancy">
	<div id='picLeftControl' class="prev" onclick="parent.zoomLeft()"><a href='javascript:;'></a></div>
	<img  id="yourImageID" /> 
	<div id='picRightControl' class="next" onclick="parent.zoomRight();"><a href='javascript:;'></a></div>
</div>

</body>
</html>
