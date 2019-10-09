// JavaScript Document
/*显示隐藏*/
function SlideBox(onbox,showbox)
{
  $(onbox).mouseover(function(){
  $(showbox).slideDown(100);
  });
  $(onbox).mouseleave(function(){
  $(showbox).fadeOut(10);
  });
}
/*
 * 菜单class
 * @param {[type]} id [description]
 */
function set_menu_hover(id){
	$('.d_menu1 li').removeClass('on');
	$('.d_menu1 li.m'+id+'').addClass('on');
}
/*调整并排DIV高度*/
function Set2DivHeight(){
   $(".side_l").css("height",$(".side_r").outerHeight());
}


function voids(num){
   
}

String.prototype.replaceAll = function(s1,s2){   

	var r = new RegExp(s1.replace(/([\(\)\[\]\{\}\^\$\+\-\*\?\.\"\'\|\/\\])/g,"\\$1"),"ig");
	return this.replace(r,s2);
	}
Object.extend = function(destination, source) { // 一个静态方法表示继承, 目标对象将拥有源对象的所有属性和方法
	for (var property in source) {
    destination[property] = source[property];   // 利用动态语言的特性, 通过赋值动态添加属性与方法
}
  return destination;   // 返回扩展后的对象
}
 
Object.extend(Object, { 
inspect: function(object) {   // 一个静态方法, 传入一个对象, 返回对象的字符串表示
    try {
      if (object == undefined) return 'undefined'; // 处理undefined情况
      if (object == null) return 'null';     // 处理null情况
      // 如果对象定义了inspect方法, 则调用该方法返回, 否则返回对象的toString()值
      return object.inspect ? object.inspect() : object.toString(); 
    } catch (e) {
      if (e instanceof RangeError) return '...'; // 处理异常情况
      throw e;
    }
},
keys: function(object) {     // 一个静态方法, 传入一个对象, 返回该对象中所有的属性, 构成数组返回
    var keys = [];
    for (var property in object)
      keys.push(property);     // 将每个属性压入到一个数组中
    return keys;
},
values: function(object) {   // 一个静态方法, 传入一个对象, 返回该对象中所有属性所对应的值, 构成数组返回
    var values = [];
    for (var property in object) values.push(object[property]); // 将每个属性的值压入到一个数组中
    return values;
},
clone: function(object) {    // 一个静态方法, 传入一个对象, 克隆一个新对象并返回
    return Object.extend({}, object);
}
});

//获取绝对x坐标
function getX(obj){  
            return obj.offsetLeft + (obj.offsetParent ? getX(obj.offsetParent) : obj.x ? obj.x : 0);  
        }   
//获取绝对Y坐标
 function getY(obj){  
            return (obj.offsetParent ? obj.offsetTop + getY(obj.offsetParent) : obj.y ? obj.y : 0);  
 } 
//获取高度和宽度
 var Style = {
 		  //获取元素最终的样式
 		  getFinalStyle: function(elem, css){
 		    if (window.getComputedStyle) {
 		      return window.getComputedStyle(elem, null)[css];
 		    } else if (elem.currentStyle) {
 		      return elem.currentStyle[css];
 		    } else {
 		      return elem.style[css];
 		    }
 		  },
 		  height: function(elem){
 		    if (this.getFinalStyle(elem, "display") !== "none") {
 		      return elem.offsetHeight || elem.clientHeight;
 		    } else {
 		      //获取隐藏掉的函数的高度，先让它显示，获取到高度之后再隐藏，下同
 		      elem.style.display = "block";
 		      var h = elem.offsetHeight || elem.clientHeight;
 		      elem.style.display = "none";
 		      return h;
 		    }
 		  },
 		  width: function(elem){
 		    if (this.getFinalStyle(elem, "display") !== "none") {
 		      return elem.offsetWidth || elem.clientWidth;
 		    } else {
 		      elem.style.display = "block";
 		      var w = elem.offsetWidth || elem.clientWidth;
 		      elem.style.display = "none";
 		      return w;
 		    }
 		  }
 		};
 /**
  *功能：创建一个随机ID
  *参数：无
  */
 function numberID(){
   return Math.round(Math.random()*10000)*Math.round(Math.random()*10000);
 }
//弹出框插件
function T$(i){return document.getElementById(i)}
//针对自动关闭的计数，如果已经关闭过了就不自动关闭了
var sysCloseCount=0;
NetBox=function(){
	var p,m,b,fn,ic,iw,ih,oe,ir,f=0;//p:最外层对象 m:蒙板层对象 b:内容容器   oe:old element 原来的元素
	var oeT,oeL,oeW,oeH,currT,currL;//原尺寸的位置和大小,当前顶部 当前左边距离
	var showing=false;//是否整处于显示中
	var firstResied=false;//是否已经第一次缩放过了,发现会会发缩放两次
	var cc='';//当前内容
	return{ 
		//显示 c:内容  w 宽度   h:高度   t >0说明这些秒后返回
		show:function(c,w,h,tb,r){
		
		//参数c:内容    宽度高度 w:宽度  h:高度   t:是否在指定的秒之后关闭   rresize的缩放id,如果存在的话就用这个进行缩放
		ir=r;
			if(!f){//如果不存在包含容器就先创建一个容器
				p=document.createElement('div'); p.id='tinybox';
				m=document.createElement('div'); m.id='tinymask';
				b=document.createElement('div'); b.id='tinycontent';
				document.body.appendChild(m); document.body.appendChild(p); p.appendChild(b);
			//	m.onclick=NetBox.hide;
				// window.onresize=NetBox.resize; 
				f=1
			}
			cc=c;
			 var t=(NetPage.height()/2)-(h/2); t=t<10?10:t;
				var endtop=0;
				 if(h<100)
					endtop=(t+NetPage.top()-60);
				 else
					 endtop=(t+NetPage.top());
				var endLeft=(NetPage.width()/2)-(w/2);
				
			if(!showing){
			    ic=c;  iw=w; ih=h;
			    if(r){
			    currT=oeT=getY(r);
			    currL=oeL=getX(r);
			    oeW=Style.width(r)+12;
			     oeH=Style.height(r)+12;
			    
			    }
			    //如果需要缩放显示
				p.style.backgroundImage='none';p.innerHTML='';
				if(r){
				  p.style.width=(oeW-12)+'px'; 
				  p.style.height=(oeH-12)+'px';
				  p.style.top=(oeT-6)+'px';
				  p.style.left=(oeL-6)+'px';
				}else{
					  p.style.width=w+'px'; 
					  if(h>99){
						  p.style.height=h+'px';
						  p.style.top=(endtop-6)+'px';
						  p.style.left=(endLeft-6)+'px';
					   //   p.style.height="auto";//h+'px';
					 // else
					  }
					  else{
						  p.style.height='auto';
						  p.style.top=(endtop+37)+'px';
						  p.style.left=(endLeft)+'px';
					  }
	
				}
				 
			   this.mask();  
			  
			   if(r){
			    //this.alpha(m,1,50,3);
			    //	FadeTo("tinymask",50,10);
				   p.style.display='block';
				   
				   $("#tinybox").animate({left:endLeft,top:endtop,width:w,height:h},150,'',function(){
					   p.innerHTML=cc;
					   p.style.height="auto";
					 //  p.style.height=($(p).find("div:first-child").outerHeight())+'px';
				   });
			    }else
			    {
			    	//this.mask(); 
			    	p.innerHTML=cc;
			    	// var newW=$(p).width();
					// var newH=$(p).height();
			    //	this.resize(newW,newH);
			    	$(p).show();//fadeIn(100);	
			    }
			}
			else 
			{
				//清空内容 
				p.style.backgroundImage='none'; // p.innerHTML=cc;
				p.style.display='block';
				if(h<100)
					 endtop=endtop-50;
				//不然在auto的情况下会突然变短
				 p.style.height=$(p).height()+"px";
				 p.innerHTML=cc;
				 var newW=w;
				 var newH=0;
				 if(h==99){
				 if($(p).find("iframe:first").length>0)
					 newH=$(p).find("iframe:first").height();
				 else
					 newH=$(p).find("div:first").height(); 
				     this.resize(newW,newH,null,99); 
				 }
				 else{
					 newH=h; 
				    this.resize(newW,newH);
				 }
				// $("#tinybox").animate({left:endLeft,top:endtop,width:w,height:h},200,'',function(){
					// p.innerHTML=cc; 
					 //if(h==99)//自动高度
					 //  p.style.height=($(p).find("div:first-child").outerHeight())+'px';
				 //  });
//				emile('tinybox', 'left:'+endLeft+'px;top:'+endtop+'px;width:'+w+'px;height:'+h+'px;', {
//		               duration: 200,
//		          after: function(){
//			             p.innerHTML=cc;
//		            }
//		        });
			}
			showing=true;
			if(tb){
				sysCloseCount=0;//还原到可以关闭的状态
				setTimeout(function(){
				if(sysCloseCount==0)//如果没有关闭过就关闭
				{
				   NetBox.hide()
				}
				},1000*tb)}
},
		//隐藏
		hide:function(callback){
//	    if(!showing || sysCloseCount==0)
//		  return;
	        sysCloseCount=-1;
			//NetBox.alpha(p,-1,0,5); 
			showing=false;
//			 FadeTo("tinymask",0,5,function(){
//				 m.style.display="none";
//			 });
			 $(m).fadeOut(150);
			 if(ir){
				      p.style.height=$(p).height()+"px";
				      p.innerHTML='';
				   $("#tinybox").animate({left:(oeL-6),top:(oeT)-6,width:(oeW-12),height:(oeH-12)},200,'',function(){
					  p.style.display="none";
				   });
			 }
			 else
			 {
				 $(p).fadeOut(150);
				// NetBox.alpha(p,-1,0,5);
				// p.style.display="none";
				// m.style.display='none';
			 }
		},
		//重设大小 w:宽度 h:高度  callback:选填回调函数 ,h2:原始高度
		resize:function(w,h,callback,h2){ 
		
			 var t=(NetPage.height()/2)-(h/2); t=t<10?10:t;
				var endtop=(t+NetPage.top());
				var endLeft=(NetPage.width()/2)-(w/2);
				if(iw==w)
					w=0;
				if(ih==h)
					h=0;
				if(w>0&&h>0){
				  $("#tinybox").animate({left:endLeft,top:endtop,width:w,height:h},150,'',function(){
					if(h2==99)  p.style.height='auto';
					$("#fancybox-frame").css("height","100%");
					  if(callback!=null)
			                 callback; 
			                
				   });
				}else if(w>0){
					$("#tinybox").animate({left:endLeft,width:w},150,'',function(){
						if(h2==99)  p.style.height='auto';
						$("#fancybox-frame").css("height","100%");
						 if(callback!=null)
				                 callback; 
				           
					   });
				}else if(h>0){
					$("#tinybox").animate({top:endtop,height:h},150,'',function(){
						if(h2==99)  p.style.height='auto';
						$("#fancybox-frame").css("height","100%");
						  if(callback!=null)
				                 callback;
				                
					   });
				}else
				{
					if(h2==99)  p.style.height='auto';
				//	$("#fancybox-frame").css("height","100%");
					if(callback!=null)
		                 callback;
				}	
		},
		//遮罩
		mask:function(){
			//if(ir){
			//}else
			//{	m.style.opacity=0.2; m.style.filter='alpha(opacity=50)';}
			m.style.display="block";
			m.style.height=NetPage.theight()+'px';
			m.style.width=NetPage.twidth()+'px';
			m.style.opacity=0.4; m.style.filter='alpha(opacity=40)';
			// $(m).fadeTo(150,0.5);
		},
		//位置
		pos:function(){
			//$("body").append("d ");测试是否没有结束
		},
		//大小  
		size:function(e,w,h,s){ 
			e=typeof e=='object'?e:T$(e); clearInterval(e.si);
			var ow=e.offsetWidth, oh=e.offsetHeight,
			wo=ow-parseInt(e.style.width), ho=oh-parseInt(e.style.height);
			var wd=ow-wo>w?-1:1, hd=(oh-ho>h)?-1:1; 
			e.si=setInterval(function(){NetBox.twsize(e,w,wo,wd,h,ho,hd,s)},10)
		},
		twsize:function(e,w,wo,wd,h,ho,hd,s){ 
			var ow=e.offsetWidth-wo, oh=e.offsetHeight-ho;
			if(ow==w&&oh==h){
				clearInterval(e.si); p.style.backgroundImage='none'; b.style.display='block';
				p.innerHTML=cc;
			}else{
				if(ow!=w){e.style.width=ow+(Math.ceil(Math.abs(w-ow)/s)*wd)+'px'}
				if(oh!=h){e.style.height=oh+(Math.ceil(Math.abs(h-oh)/s)*hd)+'px'}
				this.pos();
				if(lastWidth==ow&&lastHeight==oh){
					
					 clearInterval(e.si);
					 }
				lastWidth=ow;
				lastHeight=oh;
				//$("body").append(" 9"+ow+"+"+w);测试是否没有结束
			}
		}
	}
	var lastWidth=0;
	var lastHeight=0;
}();
//页面  
NetPage=function(){
	return{
		top:function(){return document.body.scrollTop||document.documentElement.scrollTop},
		width:function(){return self.innerWidth||document.documentElement.clientWidth},
		height:function(){return self.innerHeight||document.documentElement.clientHeight},
		theight:function(){
			var d=document, b=d.body, e=d.documentElement;
			return Math.max(Math.max(b.scrollHeight,e.scrollHeight),Math.max(b.clientHeight,e.clientHeight))
		},
		twidth:function(){
			var d=document, b=d.body, e=d.documentElement;
			return Math.max(Math.max(b.scrollWidth,e.scrollWidth),Math.max(b.clientWidth,e.clientWidth))
		}
	}
}();
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               $.fn.extend({
	jscroll:function(j){
		return this.each(function(){
			j = j || {}
			j.Bar = j.Bar||{};//2级对象
			j.Btn = j.Btn||{};//2级对象
			j.Bar.Bg = j.Bar.Bg||{};//3级对象
			j.Bar.Bd = j.Bar.Bd||{};//3级对象
			j.Btn.uBg = j.Btn.uBg||{};//3级对象
			j.Btn.dBg = j.Btn.dBg||{};//3级对象
			var jun = { W:"15px"
						,BgUrl:""
						,Bg:"#efefef"
						,Bar:{  Pos:"up"
								,Bd:{Out:"#b5b5b5",Hover:"#ccc"}
								,Bg:{Out:"#fff",Hover:"#fff",Focus:"orange"}}
						,Btn:{  btn:true
								,uBg:{Out:"#ccc",Hover:"#fff",Focus:"orange"}
								,dBg:{Out:"#ccc",Hover:"#fff",Focus:"orange"}}
						,Fn:function(){}}
			j.W = j.W||jun.W;
			j.BgUrl = j.BgUrl||jun.BgUrl;
			j.Bg = j.Bg||jun.Bg;
				j.Bar.Pos = j.Bar.Pos||jun.Bar.Pos;
					j.Bar.Bd.Out = j.Bar.Bd.Out||jun.Bar.Bd.Out;
					j.Bar.Bd.Hover = j.Bar.Bd.Hover||jun.Bar.Bd.Hover;
					j.Bar.Bg.Out = j.Bar.Bg.Out||jun.Bar.Bg.Out;
					j.Bar.Bg.Hover = j.Bar.Bg.Hover||jun.Bar.Bg.Hover;
					j.Bar.Bg.Focus = j.Bar.Bg.Focus||jun.Bar.Bg.Focus;
				j.Btn.btn = j.Btn.btn!=undefined?j.Btn.btn:jun.Btn.btn;
					j.Btn.uBg.Out = j.Btn.uBg.Out||jun.Btn.uBg.Out;
					j.Btn.uBg.Hover = j.Btn.uBg.Hover||jun.Btn.uBg.Hover;
					j.Btn.uBg.Focus = j.Btn.uBg.Focus||jun.Btn.uBg.Focus;
					j.Btn.dBg.Out = j.Btn.dBg.Out||jun.Btn.dBg.Out;
					j.Btn.dBg.Hover = j.Btn.dBg.Hover||jun.Btn.dBg.Hover;
					j.Btn.dBg.Focus = j.Btn.dBg.Focus||jun.Btn.dBg.Focus;
			j.Fn = j.Fn||jun.Fn;
			var _self = this;
			var Stime,Sp=0,Isup=0;
			$(_self).css({overflow:"hidden",position:"relative",padding:"0px"});
			var dw = $(_self).width(), dh = $(_self).height()-1;
			var sw = j.W ? parseInt(j.W) : 21;
			var sl = dw - sw
			var bw = j.Btn.btn==true ? sw : 0;
			if($(_self).children(".jscroll-c").height()==null){//存在性检测
		$(_self).wrapInner("<div class='jscroll-c' style='top:0px;z-index:8000;zoom:1;position:relative'></div>");
			$(_self).children(".jscroll-c").prepend("<div style='height:0px;overflow:hidden'></div>");
			$(_self).append("<div class='jscroll-e' unselectable='on' style=' height:100%;top:0px;right:1px;-moz-user-select:none;position:absolute;overflow:hidden;z-index:8002;'><div class='jscroll-u' style='position:absolute;top:0px;width:100%;left:0;background:blue;overflow:hidden'></div><div class='jscroll-h'  unselectable='on' style='background:green;position:absolute;left:0;-moz-user-select:none;border:1px solid'></div><div class='jscroll-d' style='position:absolute;bottom:0px;width:100%;left:0;background:blue;overflow:hidden'></div></div>");
			}
			var jscrollc = $(_self).children(".jscroll-c");
			var jscrolle = $(_self).children(".jscroll-e");
			var jscrollh = jscrolle.children(".jscroll-h");
			var jscrollu = jscrolle.children(".jscroll-u");
			var jscrolld = jscrolle.children(".jscroll-d");
			if($.browser.msie){document.execCommand("BackgroundImageCache", false, true);}
			jscrollc.css({"padding-right":sw});
			jscrolle.css({width:sw,background:j.Bg,"background-image":j.BgUrl});
			jscrollh.css({top:bw,background:j.Bar.Bg.Out,"background-image":j.BgUrl,"border-color":j.Bar.Bd.Out,width:sw-2});
			jscrollu.css({height:bw,background:j.Btn.uBg.Out,"background-image":j.BgUrl});
			jscrolld.css({height:bw,background:j.Btn.dBg.Out,"background-image":j.BgUrl});
			jscrollh.hover(function(){if(Isup==0)$(this).css({background:j.Bar.Bg.Hover,"background-image":j.BgUrl,"border-color":j.Bar.Bd.Hover})},function(){if(Isup==0)$(this).css({background:j.Bar.Bg.Out,"background-image":j.BgUrl,"border-color":j.Bar.Bd.Out})})
			jscrollu.hover(function(){if(Isup==0)$(this).css({background:j.Btn.uBg.Hover,"background-image":j.BgUrl})},function(){if(Isup==0)$(this).css({background:j.Btn.uBg.Out,"background-image":j.BgUrl})})
			jscrolld.hover(function(){if(Isup==0)$(this).css({background:j.Btn.dBg.Hover,"background-image":j.BgUrl})},function(){if(Isup==0)$(this).css({background:j.Btn.dBg.Out,"background-image":j.BgUrl})})
			var sch = jscrollc.height();
			//var sh = Math.pow(dh,2) / sch ;//Math.pow(x,y)x的y次方
			var sh = (dh-2*bw)*dh / sch
			if(sh<10){sh=10}
			var wh = sh/6 //滚动时候跳动幅度
		//	sh = parseInt(sh);
			var curT = 0,allowS=false;
			jscrollh.height(sh);
			if(sch<=dh){jscrollc.css({padding:0});jscrolle.css({display:"none"})}else{allowS=true;}
			if(j.Bar.Pos!="up"){
			curT=dh-sh-bw;
			setT();
			}
			jscrollh.bind("mousedown",function(e){
				j['Fn'] && j['Fn'].call(_self);
				Isup=1;
				jscrollh.css({background:j.Bar.Bg.Focus,"background-image":j.BgUrl})
				var pageY = e.pageY ,t = parseInt($(this).css("top"));
				$(document).mousemove(function(e2){
					 curT =t+ e2.pageY - pageY;//pageY浏览器可视区域鼠标位置，screenY屏幕可视区域鼠标位置
						setT();
				});
				$(document).mouseup(function(){
					Isup=0;
					jscrollh.css({background:j.Bar.Bg.Out,"background-image":j.BgUrl,"border-color":j.Bar.Bd.Out})
					$(document).unbind();
				});
				return false;
			});
			jscrollu.bind("mousedown",function(e){
			j['Fn'] && j['Fn'].call(_self);
				Isup=1;
				jscrollu.css({background:j.Btn.uBg.Focus,"background-image":j.BgUrl})
				_self.timeSetT("u");
				$(document).mouseup(function(){
					Isup=0;
					jscrollu.css({background:j.Btn.uBg.Out,"background-image":j.BgUrl})
					$(document).unbind();
					clearTimeout(Stime);
					Sp=0;
				});
				return false;
			});
			jscrolld.bind("mousedown",function(e){
			j['Fn'] && j['Fn'].call(_self);
				Isup=1;
				jscrolld.css({background:j.Btn.dBg.Focus,"background-image":j.BgUrl})
				_self.timeSetT("d");
				$(document).mouseup(function(){
					Isup=0;
					jscrolld.css({background:j.Btn.dBg.Out,"background-image":j.BgUrl})
					$(document).unbind();
					clearTimeout(Stime);
					Sp=0;
				});
				return false;
			});
			_self.timeSetT = function(d){
				var self=this;
				if(d=="u"){curT-=wh;}else{curT+=wh;}
				setT();
				Sp+=2;
				var t =500 - Sp*50;
				if(t<=0){t=0};
				Stime = setTimeout(function(){self.timeSetT(d);},t);
			}
			jscrolle.bind("mousedown",function(e){
					j['Fn'] && j['Fn'].call(_self);
							curT = curT + e.pageY - jscrollh.offset().top - sh/2;
							asetT();
							return false;
			});
			function asetT(){				
						if(curT<bw){curT=bw;}
						if(curT>dh-sh-bw){curT=dh-sh-bw;}
						jscrollh.stop().animate({top:curT},100);
						var scT = -((curT-bw)*sch/(dh-2*bw));
						jscrollc.stop().animate({top:scT},1000);
			};
			function setT(){				
						if(curT<bw){curT=bw;}
						if(curT>dh-sh-bw){curT=dh-sh-bw;}
						jscrollh.css({top:curT});
						var scT = -((curT-bw)*sch/(dh-2*bw));
						jscrollc.css({top:scT});
			};
			$(_self).mousewheel(function(){
					if(allowS!=true) return;
					j['Fn'] && j['Fn'].call(_self);
						if(this.D>0){curT-=wh;}else{curT+=wh;};
						setT();
			})
		});
	}
});
$.fn.extend({//添加滚轮事件//by jun
	mousewheel:function(Func){
		return this.each(function(){
			var _self = this;
		    _self.D = 0;//滚动方向
			if($.browser.msie||$.browser.safari){
			   _self.onmousewheel=function(){_self.D = event.wheelDelta;event.returnValue = false;Func && Func.call(_self);};
			}else{
			   _self.addEventListener("DOMMouseScroll",function(e){
					_self.D = e.detail>0?-1:1;
					e.preventDefault();
					Func && Func.call(_self);
			   },false); 
			}
		});
	}
});
//通用的简化Close方法
function Close(){
	NetBox.hide();
}
/***************************
Labels
***************************/
var jqTransformGetLabel = function(objfield){
	var selfForm = $(objfield.get(0).form);
	var oLabel = objfield.next();
	if(!oLabel.is('label')) {
		oLabel = objfield.prev();
		if(oLabel.is('label')){
			var inputname = objfield.attr('id');
			if(inputname){
				oLabel = selfForm.find('label[for="'+inputname+'"]');
			} 
		}
	}
	if(oLabel.is('label')){return oLabel.css('cursor','pointer');}
	return false;
};
//询问插件  Ask({Title:"测试问",Msg:"这是问题"});
function Ask(options){
	var defaults = {
		     CloseTime:0,//几秒钟后自动关闭 
		     Msg:'？',//信息
		     Title:'？',
		     Height:99,
		     callback:"Close()",  //点击确定时的回调函数
		     callback2:"Close()",  //点击否时的回调函数
		     fromObj        :null  //从这个地方弹出来,如果是null就不弹
		};
		options=Object.extend(defaults, options);
		if(options.Title!='？')
		  options.Msg=options.Title;
		var content='<div class="AlertMessage"><div class="MessageTitle">Question</div><div class="MessageHelp"><div class="Message">'+options.Msg+'</div></div><div class="MessageControl"><div class="MessageControl2">'+
	 '<a onfocus="this.blur()" id="nobackButton_1"  class="noback" onclick="'+options.callback2+'" href="javascript:Void();">'+vip.L("取消")+'</a>'+
	 '<a onfocus="this.blur()" id="okButton_1" class="ok" onclick="'+options.callback+'" href="javascript:Void();">'+vip.L("确定")+'</a>'+
	 '</div></div></div>';
	NetBox.show(content,440,options.Height,options.CloseTime,options.fromObj);
	 $('body').bind('keyup', function(event){
		   if (event.keyCode=="13"){
		    $("#okButton_1").trigger('click');
		   }
		});
}
/**
* 功能:询问功能
* 这是为了保持兼容写的，跟以前的不参数不太一样
*/
function Ask2(options)
{
	 var defaults = {
	       call:function(data){
	       }, //返回处理函数 
	         data:'', //需要携带的参数
	       CloseTime:0,//几秒钟后自动关闭 
		     Msg:'？',//信息
		     Height:99,
		     Title:"？",
		     callback:"",  //点击确定时的回调函数
		     callback2:"Close()",  //点击否时的回调函数
		     fromObj        :null  //从这个地方弹出来,如果是null就不弹 
		     };
	   options= $.extend(defaults, options);
	   if(options.Title!='？')
			  options.Msg=options.Title;
	   //$(this).bind("click",function(){
			var content='<div class="AlertMessage"><div class="MessageTitle">Question</div><div class="MessageHelp"><div class="Message">'+options.Msg+'</div></div><div class="MessageControl"><div class="MessageControl2">'+
			'<a onfocus="this.blur()" id="nobackButton_2"  class="noback" onclick="'+options.callback2+'" href="javascript:Void();">'+vip.L("取消")+'</a>'+
			 '<a onfocus="this.blur()" id="okButton_2" class="ok" onclick="'+options.callback+'" href="javascript:Void();">'+vip.L("确定")+'</a>'+
		 '</div></div></div>';
		 NetBox.show(content,440,options.Height,options.CloseTime,options.fromObj);
		   $("#okButton_2").bind("click",function(){
			   options.call(options.data);
			     return false;
		   });
		   
		   $('body').bind('keyup', function(event){
			   if (event.keyCode=="13"){
			    $("#okButton_2").trigger('click');
			   }
			});
		   //Ask({callback:options.});
	   //}); 
		   
		   
}
//用Iframe来显示一个网页地址
$.fn.Ask = function(options){  
  $(this).click(function(){
	Ask2(options);
	return false;
 });
}


//去掉当前一个的链接请求
$.fn.LineOne=function(){
	$(this).bind('focus',function(){
        if(this.blur){
            this.blur();
    };
});
}
//用Iframe来显示一个网页地址
//调用方法:
//function testCloseback(a,b){
//    alert(a+b);
//    NetBox.resize(500,500);
//}

//T$('all').onclick=function(){
//    Wrong({Msg:"测试的消息",Height:130,CloseTimme:0,fromObj:this,callback:'testCloseback(45,56)'});
//};
//也可以直接  Alert("googf");
function Message(msg,style,options){ 
var defaults = {
     CloseTime:0,//几秒钟后自动关闭
     Msg:'',//信息
     Style:'Alert',//使用的样式
     Height:99,  //如果是这个高度就自动高度
     callback:"Close()",  //从这个地方弹出来,如果是null就不谈
     fromObj        :null,  //从这个地方弹出来,如果是null就不弹
     call : null
};
options=Object.extend(defaults, options);
if(msg.length>0)
	options.Msg=msg;
if(style) 
	options.Style=style;
   var content='<div class="AlertMessage">'+
   '<div class="MessageTitle">'+options.Style+'</div><div class="Message'+options.Style+'">'+
   '<div class="Message">'+options.Msg+'</div></div>'+
   '<div class="MessageControl"><div class="MessageControl2">'+
'<a onfocus="this.blur()" id="okButton_2013"  class="ok" onclick="'+options.callback+'" href="javascript:Void();">Ok</a>'+
'</div></div></div>';
   NetBox.show(content,440,options.Height,options.CloseTime,options.fromObj);
   if(typeof options.call == "function"){
	   $("#okButton_2013").attr("href" , "javascript:;");
	   $("#okButton_2013").click(function(){
		     options.call();
		     return false;
	   });
   }
   $('body').bind('keyup', function(event){
	   if (event.keyCode=="13"){
	    $("#okButton_2013").trigger('click');
	   }
	});
}
//弹出警告对话框
function Alert(msg,options)
{
  Message(msg+"","Alert",options);

}
//信息对话框
function Info(msg,options)
{
Message(msg+"","Info",options);

}
//信息对话框
function msg(msg,options)
{
Message(msg+"","Info",options);

}
//信息对话框
function Msg(msg,options)
{
Message(msg+"","Info",options);

}
//信息对话框
function Wrong(msg,options)
{
Message(msg+"","Wrong",options);

}
//正确信息对话框
function Right(msg,options)
{
Message(msg+"","Right",options);

}
//帮助/询问信息对话框
function Help(msg,options)
{
Message(msg+"","Help",options);

}
//用Iframe来显示一个网页地址
function Iframe(options){ 
var defaults = {
     Url:'',
     zoomSpeedIn		: 100,
     zoomSpeedOut	: 100, 
     Width:540,
     Height:190, 
     Title:"",
     overlayShow     : false,
     modal : true,
     isShowIframeTitle:true,
     isShowClose:true,
     isIframeAutoHeight:false,
     scrolling:"auto",
     overlayOpacity	: 0.5,
     overlayColor	: '#000000',
     padding	    : 0,
     IsShow         :false,
     fromObj        :null  //从这个地方弹出来,如果是null就不谈
};
options=Object.extend(defaults, options);  
//options.fromObj=T$(options.fromObj); 
	var iframeHeight=(options.height-36)+"px"; 
	var content='';
	if(options.isShowIframeTitle)
	{
		var isClose="";
		if(!options.isShowClose)
			isClose=" noClose";
	  if(options.isIframeAutoHeight)//需要自适应高度 
	  {
		 content='<div class="popIframeTitle'+isClose+'"><div class="popIframeTitle">&nbsp;&nbsp;'+options.Title+'</div><div class="popIframeCloseC">'+
				  '<a class="popIframeClose" onfocus="this.blur()" href="javascript:Close()">Close</a></div></div><iframe id="fancybox-frame" name="fancybox-frame' + new Date().getTime() + '" frameborder="0"   hspace="0" ' + ($.browser.msie ? 'allowtransparency="true""' : '') + ' scrolling="' + options.scrolling + '" onload="$(this).height($(this).contents().height());NetBox.resize(0,($(this).contents().height()+36));" src="' + options.Url + '"></iframe>';
	  } 
	  else{   
		 content='<div class="popIframeTitle'+isClose+'"><div class="popIframeTitle">&nbsp;&nbsp;'+options.Title+'</div><div class="popIframeCloseC">'+ 
	     '<a class="popIframeClose" onfocus="this.blur()" href="javascript:Close()">Close</a></div></div><iframe id="fancybox-frame" name="fancybox-frame' + new Date().getTime() + '" frameborder="0" hspace="0"  scrolling="' + options.scrolling + '" style="height:'+(options.Height-36)+'px"  src="' + options.Url + '"></iframe>';
	  }
	}
	else
	{
		if(options.isIframeAutoHeight)//需要自适应高度
			content='<iframe id="fancybox-frame" name="fancybox-frame' + new Date().getTime() + '" frameborder="0" hspace="0" ' + ($.browser.msie ? 'allowtransparency="true""' : '') + '  onload="$(this).height($(this).contents().height());NetBox.resize(0,($(this).contents().height()));" scrolling="' + options.scrolling + '" src="' + options.Url + '"></iframe>';		
		else 						
			content='<iframe id="fancybox-frame" name="fancybox-frame' + new Date().getTime() + '" frameborder="0" hspace="0" ' + ($.browser.msie ? 'allowtransparency="true""' : '') + ' scrolling="' + options.scrolling + '" src="' + options.Url + '"></iframe>';		
	}
	 NetBox.show(content,options.Width,options.Height,0,options.fromObj);

}

//用Iframe来显示一个网页地址
$.fn.Iframe = function(options){  
  $(this).click(function(){
	Iframe(options);
	return false;
 });
}
$.extend({
	 Iframe : function(options){
	Iframe(options);
}
});



   


/**
*功能：客户端cookie函数相关操作组件,标准第三方组件
*使用方法：
* @设置cokie.
* @example $.cookie('the_cookie', 'the_value', { expires: 7, path: '/', domain: 'jquery.com', secure: true });
* @desc Create a cookie with all available options.
* @example $.cookie('the_cookie', 'the_value');
* @desc Create a session cookie.
* @example $.cookie('the_cookie', null);
*/
jQuery.cookie = function(name, value, options) {
   if (typeof value != 'undefined') { // name and value given, set cookie
       options = options || {};
       if (value === null) {
           value = '';
           options.expires = -1;
       }
       var expires = '';
       if (options.expires && (typeof options.expires == 'number' || options.expires.toUTCString)) {
           var date;
           if (typeof options.expires == 'number') {
               date = new Date();
               date.setTime(date.getTime() + (options.expires * 24 * 60 * 60 * 1000));
           } else {
               date = options.expires;
           }
           expires = '; expires=' + date.toUTCString(); // use expires attribute, max-age is not supported by IE
       }
       // CAUTION: Needed to parenthesize options.path and options.domain
       // in the following expressions, otherwise they evaluate to undefined
       // in the packed version for some reason...
       var path = options.path ? '; path=' + (options.path) : '';
       var domain = options.domain ? '; domain=' + (options.domain) : '';
       var secure = options.secure ? '; secure' : '';
       document.cookie = [name, '=', encodeURIComponent(value), expires, path, domain, secure].join('');
   } else { // only name given, get cookie
       var cookieValue = null;
       if (document.cookie && document.cookie != '') {
           var cookies = document.cookie.split(';');
           for (var i = 0; i < cookies.length; i++) {
               var cookie = jQuery.trim(cookies[i]);
               // Does this cookie string begin with the name we want?
               if (cookie.substring(0, name.length + 1) == (name + '=')) {
                   cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                   break;
               }
           }
       }
       return cookieValue;
   }
};
//联动控制区域
$.fn.DropSelecter=function(options){
	   var defaults = {
	       StyleHover:'hover', //悬停状态的style，必须有的
	       StyleSelect: '',  //，选中状态的style，不是必须的，对于类似下拉框的样式会存在三种样式，正常，悬停，下拉，这里为空就不起作用
	       ControlSeleter:'',
	       Left:0,
	       Top:0,
	       IsShow:false,//初始状态是否显示
	       Auto:true, //是否鼠标悬停自动显示，鼠标移出自动消失
	       call : function(){}
	  };
	    options= $.extend(defaults,options);  
	    if(options.StyleSelect=='')
	    	options.StyleSelect=options.StyleHover;
	   var $this=$(this);
	   var $control=$(options.ControlSeleter);
	   var timer;
	   var sleep=500;
	   var offset =$this.position(); 
	   if(!offset)
	      return;
	   
	   var first = true;
	   
	  $control.css({left:offset.left+options.Left,top:offset.top+$this.height()+options.Top});
	  var id="";
	  if(!$(this).data("DropSelecterId"))
	  {
	    id=numberID();
	    $this.data("DropSelecterId",id);
	      
	     if(!options.Auto){
	    	 $("body").click(function(){
	    		 if($this.data("opened"))
	    		 {
		    		    $control.hide(); 
		    		    $this.removeClass(options.StyleSelect);
		    		    $this.removeData("opened");
		    	 }
	    	 });
	    	 //如果需要点击才下拉出来
	    	 $this.click(function(event){
	    		 if($this.data("opened")){
	    		    $control.hide();
	    		    $this.removeClass(options.StyleSelect);
	    		    $this.removeData("opened");
	    		 }else{
	    			 //先触发一次body点击，主动促使其他浮动框关闭
		    		 $("body").trigger("click");  
	    			 $this.data("opened",true);
	    			 $control.show(); 
	    			 $this.addClass(options.StyleSelect);
	    		 }
	    		 event.stopPropagation();
	    	 });
	    	 //阻止点击的时间传播
	    	 $control.click(function(event){
	    		 event.stopPropagation(); 
	    	 });
	     }
	     else
	     {
	    	 
	    		 $control.mouseenter(function(){
	    			 clearTimeout(timer);
	    	    	 timer=setTimeout(function(){
	    	    		 $control.show();
					      $this.addClass(options.StyleHover);
	    	    	 },sleep);
				 }).mouseleave(function(){
					 clearTimeout(timer);
	    	    	 timer=setTimeout(function(){
	    	    		 $control.hide();
			                $this.removeClass(options.StyleHover);  
	    	    	 },sleep);
		         });
	     }
	     
	   //控制鼠标放上去的那个a

    		$this.focus(function(){
			      $(this).blur();
		    }).mouseover(function(){
		    	 clearTimeout(timer);
		     	 timer=setTimeout(function(){
		     		if(options.Auto){
			    		  $control.show(); 
			    		  if(first){
			    		  	(function(v){ return options.call(v);})(1)
			    		  }
			    	  }
		     			$this.addClass(options.StyleHover);
		     			first = false;
		     		
		     	 },sleep);
	         }).mouseleave(function(){
	        	 clearTimeout(timer);
		     	 timer=setTimeout(function(){
		     		if(options.Auto)
				       $control.hide();
			           $this.removeClass(options.StyleHover);
		     	 },sleep);
	           
	        });

	     $(window).resize(function(){  
		     var offset =$this.offset(); 
	        $control.css({left:offset.left+options.Left,top:offset.top+$this.height()+options.Top});
		 });
	      //控制下拉出来的那一块    
	    if(options.IsShow)
  	    { 
//	    	 $this.data("opened",true);
//			 $control.show(); 
//			 $this.addClass(options.StyleSelect);
	    }
	  }
};



$.fn.UiTitle = function (){
	$(this).MyTitle({defaultCss:'tipsr',title:$(this).attr("mytitle"),html:true});
};
///////////////////////////////按钮部分//////////////////////////////////////
$.fn.UIButton = function() { 
//按钮的样式 
 if($(this).attr("NewStyle")){//如果已经格式化过了就直接返回
   return ;
  }
  $(this).attr("NewStyle","true");  
   $(this).wrap("<a  href='javascript:void(0)'></a>");
   if(!$(this).attr("StyleName"))
       $(this).attr("StyleName","buttonCommon");  // 如果没有就设置一个通用的按钮函数
       
   if($(this).attr("disabled"))
      $(this).parent().addClass($(this).attr("StyleName")+"Disabled");
   else
     $(this).parent().addClass($(this).attr("StyleName"));

   $(this).bind('focus', function(){
    if(this.blur){ //如果支持this.blur
        this.blur();
    }
 });
}
/***************************
	文本框,输入框美化
***************************/
$.fn.UiText = function () {
	  if (!$(this).attr("NewStyle")){
		  $(this).attr("NewStyle");
	      var $input = $(this);
	      //加上提示信息
	      if($input.attr("mytitle"))
          {
			$input.MyTitle({defaultCss:'tipsr',title:$input.attr("mytitle"),trigger:"manual"});
			// $input.data("mytitle",true);//标示已经打开过了标题栏
          }
	      //加上默认值
	      if($(this).attr("valueDemo"))
   	      {
	      	if($(this).val()==""){
	            $(this).val($(this).attr("valueDemo"));
	            $(this).css({"color":"#C7C7C7"});
	      	}
   	      }
	      //绑定获取焦点的值
	      	 $(this).bind("focus",function(){
	      		
	      		 //移除错误信息
	      		  $(this).removeAttr("errorStyle");
	      	 if($(this).attr("valueDemo"))
	   	      {
	         	 if($(this).attr("valueDemo")==$(this).val()){
	         		$(this).val("");
	         	 $(this).css({"color":"#333333"});
	         	 }
	   	      }
	         	$input.addClass("inputFocue");
	         	if($input.data('tipsy'))
	        	  $input.MyTitle("show"); 
			  	
	         }).bind("blur",function(){//绑定失去焦点的值
	        	 
	         if($(this).attr("valueDemo"))
	   	      {
	        	 if($(this).val()=="") {
	              		$(this).val($(this).attr("valueDemo"));
	              		$(this).css({"color":"#C7C7C7"});
	        	 }	 
	   	      }
	        	 $input.removeClass("inputFocue"); 
	        	 if($input.data('tipsy'))
			        $input.MyTitle("hide"); 
			    if($(this).val().length>0)
                   CheckTextBox($(this)); 
	         });

	      //就是输入的时候不隐藏提示框信息
	      if(!$(this).attr("noHide") && $input.data('tipsy')){
	    	  $input.keydown(function(){
	    		 
	    		     $input.MyTitle("hide");
					//$(".TitleRight").fadeOut("fast"); 
				}); 
	      }  
  }
};


//checkbox美化 
$.fn.UiCheckbox=function(options){
	
			$(':checkbox+label',this).each(function(){
				$(this).addClass('checkbox');
	            if($(this).prev().is(':disabled')==false){
	                if($(this).prev().is(':checked'))
					    $(this).addClass("checked");
	            }else{
	                $(this).addClass('disabled');
	            }
			}).click(function(event){
					if(!$(this).prev().is(':checked')){
					    $(this).addClass("checked");
	                    $(this).prev()[0].checked = true;
	                }
	                else{
	                    $(this).removeClass('checked');			
	                    $(this).prev()[0].checked = false;
	                }
	                event.stopPropagation();
				}
			).prev().hide();
}
/*
 * 是一个不可用状态的radio变成可用
 * selecter：jquery选择器
 */
function EnableRadio(selecter){
	var $input = $(selecter);
	var aLink =$input.parent().find("a:first");
	 aLink.removeClass('jqTransformRadioEnabled').css({cursor:"pointer"});
	 $input.removeAttr("disabled");
	 $input.change(function(){
		 $input[0].checked && aLink.addClass('jqTransformRadioChecked') || aLink.removeClass('jqTransformRadioChecked');
			return true;
		  });
		  // Click Handler
		  aLink.click(function(){
			if($input.attr('disabled')){return false;}
			$input.trigger('click').trigger('change');

			// uncheck all others of same name input radio elements
			$('input[name="'+$input.attr('name')+'"]').not($input).each(function(){
				$(this).attr('type')=='radio' && $(this).trigger('change');
			});

			return false;					
		  });
		  // set the default state
	  	 // inputSelf.checked && aLink.addClass('jqTransformRadioChecked');
}
/*
 * 是一个可用状态的radio变成不可用
 * selecter：jquery选择器
 */
function DisenableRadio(selecter){
	var $input = $(selecter);
	var aLink =$input.parent().find("a:first");
	$input.attr("disabled","disabled");
	 aLink.addClass('jqTransformRadioEnabled').css({cursor:"default"});
	 aLink.unbind("click");
	 $input.unbind("change");
}
/***************************
Radio Buttons
***************************/	
$.fn.UiRadio = function(){
	return this.each(function(){
	 if (!$(this).attr("NewStyle")) {
		if($(this).hasClass('jqTransformHidden')) {return;}
		var $input = $(this);
		//$(this).addClass("jqTransformHidden");
		var inputSelf = this;		
		//oLabel = jqTransformGetLabel($input);
		//oLabel && oLabel.click(function(){aLink.trigger('click');});

			 $input.addClass('jqTransformHidden');
			var aLink;
			if($(this).parent().hasClass("jqTransformRadioWrapper"))
			   aLink =$input.parent().find("a:first");
			else
			{
			   aLink = $('<a  class="jqTransformRadio" style="cursor:pointer;"></a>');
			   $input.wrap('<span class="jqTransformRadioWrapper"></span>').parent().prepend(aLink);
			}
			if($input.attr("disabled")){ 
				 aLink.addClass('jqTransformRadioEnabled').css({cursor:"default"});
				 return;//不需要事件就返回了
			}else
			{
			  $input.change(function(){
				inputSelf.checked && aLink.addClass('jqTransformRadioChecked') || aLink.removeClass('jqTransformRadioChecked');
				return true;
			  });
			  // Click Handler
			  aLink.click(function(){
				if($input.attr('disabled')){return false;}
				$input.trigger('click').trigger('change');

				// uncheck all others of same name input radio elements
				$('input[name="'+$input.attr('name')+'"]',inputSelf.form).not($input).each(function(){
					$(this).attr('type')=='radio' && $(this).trigger('change');
				});

				return false;					
			  });
			  // set the default state
		  	  inputSelf.checked && aLink.addClass('jqTransformRadioChecked');
			}
			$input.hide();
	   }
	});
};
 
 
//选择框美化
$.fn.UiSelect=function(options){
	   var defaults = {
	       StyleNormal: 'SelectGray',  //正常状态的样式
	       StyleHover: 'SelectBlue',  //悬停的样式
	       StyleDropDown: 'SelectDropDown',  //下拉的样式
	       itemHeight:24, //下拉项每项的高度
	       InnerWidth:0,//如果是0就自动计算，否则就是用这个固定的大小
	       InnerWidthOffset:-15,//内部的便宜量，对于多种样式的可能需要设置改变
	       OuterWidth:0,//如果是0就自动计算，否则就是用这个固定的大小
	       OuterWidthOffset:12,//外部自动偏移量（对于多种样式的需要改变）
	       MaxShow:10,//最多显示多少项
	       Top:0,//下拉部分上偏移量
	       Left:0,//下拉部分左偏移量
	       ControlSeleter:'',//被控制区域，如果为空就创建一个
	       Auto:false,//是否需要鼠标悬停就下拉
	       IsShow:true //是否显示，true为显示 
	  };
	   if($(this).attr("NewStyle"))
	      return;
	  options= $.extend(defaults,options);  
	  var $this=$(this);
	   var $selecter=$(this);//先设定类型为jauery
	   var id="0";
	   var again=false;//是否是第二次更新
	  if($this.attr("SelectId"))
	  {//如果有就先移除一下
		  id=$this.attr("SelectId").split('_')[1];
		//  $("#"+$this.attr("SelectId")).remove();
		  //移除原有的下拉部分
		 // $("#down_"+ids).remove();
		 $selecter=$("#select_"+id); 
		 // $selecter.unbind();  
		   $selecter.removeData("dropDownSelected"); 
		 //设置新的选项文字 
		 $selecter.find("span i").text($this.find("option:selected").text());
		 again=true; 
	  } 
      else{
		id=numberID();
		$this.attr("SelectId","select_"+id);
	    $selecter=$("<div id='select_"+id+"' class='"+options.StyleNormal+"'><span><i><i><span><div>").insertAfter($this); 
		//初始设置span文字
		$selecter.find("span i").text($this.find("option:selected").text());
		$selecter.mouseover(function(){
			$selecter.addClass(options.StyleHover);	
		}).mouseout(function(){
			$selecter.removeClass(options.StyleHover);
		});
	 }
	 //var cIndex=$this.get(0).selectedIndex;
	 // if($.browser.safari)
		//  {//sofari宽度太短
      //       options.InnerWidthOffset=options.InnerWidthOffset+26;
	 //        options.OuterWidthOffset=options.OuterWidthOffset+26;
		//  }
	    var cIndex=$this.selectedIndex;
		//先实现一个简易的状态响应，等到点击的时候再初始化下边部分
		if(options.InnerWidth==0)
			options.InnerWidth=$this.width()+options.InnerWidthOffset;
		if(options.OuterWidth==0)
			options.OuterWidth=$this.width()+options.OuterWidthOffset;
	    //设置到选中状态
		$selecter.find("span i").css('width',options.InnerWidth);  

        $selecter.mouseover(function(){ 
    		options.InnerWidth=$this.width()+options.InnerWidthOffset;
    		options.OuterWidth=$this.width()+options.OuterWidthOffset;
		    again=$("#down_"+ id).length>0; 
			if(!$(this).data("dropDownSelected")){ 
				$(this).data("dropDownSelected",true);
				var oLi="";
				if(again)
				  oLi="<span id='downDiv_"+ id+"' >";
				 else
				   oLi="<p id='down_"+ id+"' ><span id='downDiv_"+ id+"' >";
				var itemCount=0;
				$('option', $this).each(function(i){
					if(cIndex==i)
					 oLi += '<a href="javascript:void(0)"  class="selected" index="'+ i +'">'+ $(this).text() +'</a>';
					else
						oLi += '<a href="javascript:void(0)"  index="'+ i +'">'+ $(this).text() +'</a>';
					itemCount++;
				});
				if(again)
				  oLi += "</span>";   
				else
				   oLi += "</span></p>"; 
				var needScrool=false;  
				var heights=itemCount*options.itemHeight;
				if(options.MaxShow<itemCount)
				{
				   needScrool=true;
				   heights=options.MaxShow*options.itemHeight;
				}
				var $ul=$(oLi);
				if(again)
				{
				   $ul= $("#down_"+id).html(oLi);
				}
			    $ul.css({'width':options.OuterWidth,'height':heights}); 
				$ul.find("span").css({'width':options.OuterWidth,'height':heights}); 
				$ul.find('a').click(function(){
					if($selecter.data('tipsy')) 
						$selecter.MyTitle("hide"); 
					$this[0].selectedIndex=$(this).attr("index"); 
					$ul.find('a').removeClass('selected');
					$(this).addClass('selected');	
					$selecter.find("span i").text($(this).text());
					$this.trigger("change");
			    	  $(".TitleErrorRight:visible").hide("fast");
			    	  $(".TitleErrorTop:visible").hide("fast");
					$("body").trigger("click"); //关闭当前下拉框  
						return false; 
				});
				$ul.find('a').focus(function(){
					$(this).blur();
				});
				//alert(oLi); 
				if(!again)
				{
				   $ul.appendTo($selecter); 
				}
				//样式化他 
				$selecter.DropSelecter({ControlSeleter:"#down_"+id,StyleHover:options.StyleHover,StyleSelect:options.StyleDropDown,Left:0,Top:-1,Auto:false,IsShow:true});  
				
				
				//$("#downDiv_"+ id).jscroll({W:"19px",Btn:{btn:false}}); 
				 if(needScrool){
				// 	 alert($("#downDiv_"+ id).text()); 
					$ul.css("padding-bottom","3px"); 
					
				///////解决二次ui无滚动条
				$("#down_"+id).show();
				$("#downDiv_"+ id).jscroll({
					     W:"17px" 
		                 ,BgUrl:"url(/body/images/form/s_bg.png)"
		                 ,Bg:"right 0 repeat-y"
		                 ,Bar:{Pos:"up" 
		                 ,Bd:{Out:"#a3c3d5",Hover:"#b7d5e6"}
		                 ,Bg:{Out:"-51px 0 repeat-y",Hover:"-66px 0 repeat-y",Focus:"-81px 0 repeat-y"}}
		                 ,Btn:{btn:true 
		                 ,uBg:{Out:"0 0",Hover:"-17px 0",Focus:"-34px 0"}
		                 ,dBg:{Out:"0 -21px",Hover:"-17px -21px",Focus:"-34px -21px"}}
		                 ,Fn:function(){}
                    }); 
                } 
                $ul.hide();
			 }
        });
         $(this).hide(); 
};

function changeCheckBox(id){
	var obj=T$("ck_"+id);
	var objo=T$(id);
	if(objo.checked){
		 obj.className="checkbox";
		 objo.checked=false;
		 
	}
	else{
	  obj.className="checkbox checked";
	  objo.checked=true;
	}
	$(objo).trigger("change");
}
//样式化一个checkbox
function UICheckbox(obj){
	   if($(obj).attr("newStyle"))
		   return;
	    var nowId=obj.getAttribute("id");
	    
        if(nowId){
        	if($("#"+nowId).attr("checked"))
        	    $(obj).after("<label onclick=\"changeCheckBox('"+nowId+"')\" class='checkbox checked' id='ck_"+nowId+"'></label>");
        	else
        		$(obj).after("<label onclick=\"changeCheckBox('"+nowId+"')\" class='checkbox' id='ck_"+nowId+"'></label>");
        }
        else
        {
        	var id=numberID();
		    obj.setAttribute("id",id);
		    if($("#"+id).attr("checked"))
		         $(obj).after("<label onclick=\"changeCheckBox('"+id+"')\" class='checkbox' id='ck_"+id+"'></label>");
		    else
		    	 $(obj).after("<label onclick=\"changeCheckBox('"+id+"')\" class='checkbox checked' id='ck_"+id+"'></label>");
        }
}


/*
*功能：判断是否为与指定对象匹配的公用pattern
*/
function matchof(s,srcid,id){
var sr=$("#"+srcid).val();
if(sr==s){
 return true;
}else {
 return false;
}
}

/*
*功能：判断是否为数字的公用pattern
*/
function num(s,id){

var patrn=/^[0-9]{1,20}$id/;
if(s.search("^-?\\d+(\\.\\d+)?$")==0){
 return true;
}else {
 if(s.toString().length==0){
  
   return true;
 }else {
 
   return false;
 }
}
}
/*
*功能：判断是否为中文字符公用pattern
*/
function cnChar(s,id){
var patrn=new RegExp("[^\x00-\xff]");
return patrn.exec(s);
}
//3 长时间，形如 (2008-07-22 13:04:06)
function strDateTime(str,id)
{
	if(str==null)
		return true;
	if(str=="")
		return true;
var reg = /^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2}) (\d{1,2}):(\d{1,2}):(\d{1,2})$/;
var r = str.match(reg);
if(r==null)return false;
var d= new Date(r[1], r[3]-1,r[4],r[5],r[6],r[7]);
return (d.getFullYear()==r[1]&&(d.getMonth()+1)==r[3]&&d.getDate()==r[4]&&d.getHours()==r[5]&&d.getMinutes()==r[6]&&d.getSeconds()==r[7]);
}
/*
*功能：判断是否为指定限制大小的的公用pattern
*/
function limit(s,minNum,maxNum,id){
	var b1=s;
	var len = b1.match(/[^ -~]/g) == null ? b1.length : b1.length + b1.match(/[^ -~]/g).length ;
	
if(len>=minNum && len<=maxNum)
	 return true;
else
	return false;

}
//不能等于
function notmatch(s,s1,id){

	if(s==s1)
		 return false;
	else
		return true;

	}
function email(s,id){
       if(s.length==0)
    	   return false;
	if(s.indexOf('.')>0&&s.indexOf('@')>0)
		 return true;
	else
		return false;

	}
function telphone(s,id){
	 var patrn=new RegExp("[0-9]{2})+-([0-9]{4})+-([0-9]{4}");
	if(!patrn.exec(s)) 
	{
	    return false;
	}
else
	return true;
}

function isMobile(number){
	var pattern = /^1[3|4|5|8][0-9]\d{8}$/;
	if(pattern.test(number)){
		return true;
	}else{
		return false;
	}
}

function isPhone(number){
	var pattern = /((\d{11})|^((\d{7,8})|(\d{4}|\d{3})-(\d{7,8})|(\d{4}|\d{3})-(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1})|(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1}))$)/;
	if(pattern.test(number)){
		return true;
	}else{
		return false;
	}
}

function Ui(obj)
{
	 var str="";
	 if(!$(obj))//不存在就返回
	   return null;
		var arr=T$(obj).getElementsByTagName("*");
		for(var i=0;i<arr.length;i++)
		{
		  if(arr[i].tagName!=undefined)
		  {
			  var tagName=arr[i].tagName.toLowerCase();
			
		    var controlId=arr[i].getAttribute("id"); 
		   if(tagName=="input" ||tagName=="textarea")
		   {//处理文本框
			   if(arr[i].type.toLowerCase()=="text"||arr[i].type.toLowerCase()=="hidden"|| arr[i].type.toLowerCase()=="password"||arr[i].tagName.toLowerCase()=="textarea")
			   {
				   $(arr[i]).UiText();
			   }

			  else if(arr[i].type.toLowerCase()=="radio")
			  {
				  $(arr[i]).UiRadio(); 
			  }
			  else if(arr[i].type.toLowerCase()=="checkbox")
			  {
				  UICheckbox(arr[i]);
			  }
			  else if(arr[i].type.toLowerCase()=="button")
			  {
				  $(arr[i]).UIButton();
			  }
		   }
			else if(tagName=="select")
			{ 
				$(arr[i]).UiSelect();
			}
			
		}
  }
}

$.fn.Ui = function(){
	var arr = [];
	if($(this)[0]){
		arr=$(this)[0].getElementsByTagName("*");
	}
	for(var i=0;i<arr.length;i++)
	{
	  if(arr[i].tagName!=undefined)
	  {
		  var tagName=arr[i].tagName.toLowerCase();
		
	    var controlId=arr[i].getAttribute("id"); 
	   if(tagName=="input" ||tagName=="textarea")
	   {//处理文本框
		   if(arr[i].type.toLowerCase()=="text"||arr[i].type.toLowerCase()=="hidden"|| arr[i].type.toLowerCase()=="password"||arr[i].tagName.toLowerCase()=="textarea")
		   {
			   $(arr[i]).UiText();
		   }

		  else if(arr[i].type.toLowerCase()=="radio")
		  {
			  $(arr[i]).UiRadio();
		  }
		  else if(arr[i].type.toLowerCase()=="checkbox")
		  {
			  UICheckbox(arr[i]);
		  }
		  else if(arr[i].type.toLowerCase()=="button")
		  {
			  $(arr[i]).UIButton();
		  }
	   }
		else if(tagName=="select")
		{ 
			$(arr[i]).UiSelect();
		}

	}
  }
}
/*
 * 功能:阻止a的自动跳转事件,用于执行javascript事件,不然即使绑定了js事件也可能因为先执行了a的跳转而失效
 * 参数:没有参数
 * 注意: 对于fancebox相关的直接绑定事件,暂时如果href写入的是#,会导致无法打开
 * 用例: $("seecter").AStop();
 */ 
$.fn.AStop= function(options) {  
	$(this).click(function(oEvent){
         var oEvent = oEvent ? oEvent : window.event,
             tar;
         oEvent.preventDefault();//阻止超链接  
         if(navigator.appName=="Microsoft Internet Explorer"){
             tar = oEvent.srcElement;  
         }else{ 
         	 tar=oEvent.target;  
         }
         if(tar.getAttribute("disabled")){         
             return false;//阻止点击事件   
         }
	 });					
}
/*
*功能:在指定的区域内fixed,不会超过该区域
*参数:offsetX 相对于x的偏移量,注意,这里不同于正常的偏移的是,因为显示在区域,所以一旦到达底部的时候就不会再继续滚动了
*             -1代表x方向始终处于被控制区域的居中位置
*     offsetY 相对于Y轴的偏移量,原理同上 
*             -1代表y方向始终处于被控制区域的居中位置 
*     selecter:被浮动者的悬着器,不用用this等直接选择器,比如传入"#id .float"类似的直接选择标识
*/

jQuery.fn.fixedIn= function (options) {
	 var defaults = {
			 offsetX:0,
			 offsetY:0,
			 selecter:""  //是否禁用
		      }; 
//再根据参数优先配置
	options= $.extend(defaults, options);  

// this.each(function (i) {
 	//母体控件
         var $tbl = $(this);
         //受控的控件

         var id=numberID();
         
         var $tblhfixed = $(options.selecter);

         //显示的偏移量,默认为浮动控件自身的高度
       
         $(window).scroll(function () {
             var sctop = $(window).scrollTop();  
             var elmtop =$tbl.offset().top;  
             var buttonsize=elmtop + $tbl.height()-2*options.offsetY-$tblhfixed.height();
             var ietop=sctop-elmtop+options.offsetY;
             var ietopbutton=sctop-elmtop+options.offsetY; 
             if (sctop > elmtop && sctop <= buttonsize) 
             {//如果还在显示区域就固定浮动 
                 	$tblhfixed.css({
                     "position": "fixed",  
                     "top": options.offsetY+"px", 
                     "left":($tbl.offset().left+options.offsetX)+"px"  
                   }); 
             	//$tblhfixed.show();
             }
             else 
             {//否则就跟随文档滚动 
             	  if( sctop> buttonsize){ //下届越出
             			
             		  $tblhfixed.css({ 
                           "position": "absolute", 
                           "top": ($tbl.height()-options.offsetY-$tblhfixed.height())+"px",  
                           "left":options.offsetX+"px"
                      }); 
             	  }
             	  else //上届越出 
             	  {
             		  $tblhfixed.css({ 
                           "position": "absolute", 
                           "top": options.offsetY+"px",  
                           "left":options.offsetX+"px"  
                      }); 
             	  } 
             } 
         });
         $(window).trigger("scroll");
         
        // $(window).resize(function () { 
//             if ($clonedTable.outerWidth() != $tbl.outerWidth()) {
//                 $tblhfixed.find(headerelement).each(function (index) {
//                     var w = $(this).width();
//                     $(this).css("width", w);
//                     $clonedTable.find(headerelement).eq(index).css("width", w);
//                 });
//                 $clonedTable.width($tbl.outerWidth());
//             }
            // $tblhfixed.css("left", options.offsetX); 
       //  });
   
// });

};
/*
* 功能:显示一个lodding动画在制定的区域上面
* 例子:
* 现有问题:在生成的列表页中使用该控件,在ie6下页面会首先跳转到最上面
*/
$.fn.Loadding = function(options) { 
var defaults = {
   StyleName: 'Loadding',
   Str: '加载中...',
   Width:0,  //宽度 <0 就不需要设置 0代表当前控件的宽度 >0的值就用这个指定的固定值
   Height:0,  //高度 <0 就不需要设置 0代表当前控件的宽度 >0的值就用这个指定的固定值
   Position:"on", //on正好在其上 top在控件上方 bottom在控件下方 left 在控件左边 right 在控件右边
   IsShow:true, //true为显示 false为隐藏 
   OffsetY:0, //Y方向的偏移量
   OffsetX:0, //X方向的偏移量
   OffsetYGIF:0, //Y方向的GIF动画偏移量
   OffsetXGIF:0 //X方向的GIF动画偏移量
};
options= $.extend(defaults, options);  
if(!options.IsShow)
{//清理工作
	 var ids=$(this).attr("LoddingId"); 
		 $("#Lodding_"+ids).remove();
		 $(this).removeAttr("LoddingId");
	 return; 
} 

 var id=numberID();
 var style=";position:absolute;";
 if(options.Position=="top")
	  style+="margin-top:-"+$(this).outerHeight()+"px;";
 else if(options.Position=="bottom")
	  style+="margin-top:0;"; 
 else if(options.Position=="left")
	  style+="margin-left:-"+$(this).outerWidth()+"px;";
 else if(options.Position=="right")
	  style+="margin-right:"+$(this).outerWidth()+"px;";
 else
	  style+="top:"+$(this).offset().top+"px;left:"+$(this).offset().left+"px;";  
 
 if(options.Width==0)  
 { 
	
	  style+="width:"+$(this).outerWidth()+"px;"; 
	  
 }
 else if(options.Width>0){
	  style+="width:"+options.Width+"px;";
	
 }
 
 
 if(options.OffsetX==0){
	 options.OffsetXGIF=$(this).outerWidth()/2-110;
 }else{
	 options.OffsetXGIF=$(this).outerWidth()/2-110+options.OffsetXGIF;
 }
 
 if(options.Height==0)
	 {
	 
	  style+="height:"+$(this).outerHeight()+"px;";
	 }
 else if(options.Height>0)
	  style+="height:"+options.Height+"px;";  

// $("#MessageContainer").append("<div id=\"Lodding_"+id+"\" class=\""+options.StyleName+"\"><div class=\"GIF\">"+options.Str+"</div></div>"); 
 $(this).after("<div id=\"Lodding_"+id+"\" class=\""+options.StyleName+"\" style=\""+style+"\" ><div class=\"GIF\">"+options.Str+"</div></div>"); 
 $(this).attr("LoddingId",id);
// $("#Lodding_"+id).fadeTo("normal",0,function(){
	  $("#Lodding_"+id).fadeTo("fast",0.6);     
// });
// $("#Lodding_"+id+" .GIF").floatdiv({left:"20px",top:$(this).offset().top+"px"});
 $(this).fixedIn({selecter:"#Lodding_"+id+" .GIF",offsetX:options.OffsetXGIF,offsetY:options.OffsetYGIF});
}

/**
 * 功能:阻止浏览器默认事件
 * @param e
 * @return
 */

function stopDefault( e ) { 
	// Prevent the default browser action (W3C) 
	if ( e && e.preventDefault ) 
	e.preventDefault(); 
	// A shortcut for stoping the browser action in IE 
	else 
	window.event.returnValue = false; 
	return false; 
}
function checkPageInput(e,page){
	 e.keyCode==13 && $("#JumpButton").trigger("click"); 
}

/**
 * 功能:屏幕可视位置滚动到指定的元素位置
 * 参数: 1 Speed 时间,单位毫秒;2 Top int数值,滚动到距离顶部的top px距离
 * 例子: $("target").ScrollTo({Speed:500,Top:100}); 完成功能:移动到现在选定的目标具体顶部100px的位置,.05秒内完成
 */
jQuery.fn.ScrollTo = function(options){ 
	  var defaults = {
		        Speed: 300, //移动的毫秒数量
		        Top: 0      //距离顶部的距离
		      };
	  options= $.extend(defaults, options);  
	o = jQuery.speed(options.Speed);
	return this.each(function(){
		new jQuery.fx.ScrollTo(this, o,options.Top);
	});
};
//上面函数的内部只用函数,不对外直接使用
jQuery.fx.ScrollTo = function (e,o,top)
{
	var z = this;
	z.o = o;
	z.e = e;
	z.p = jQuery.getPos(e);
	z.s = jQuery.getScroll();
	z.clear = function(){clearInterval(z.timer);z.timer=null};
	z.t=(new Date).getTime();
	z.step = function(){
		var t = (new Date).getTime();
		var p = (t - z.t) / z.o.duration;
		if (t >= z.o.duration+z.t) {
			z.clear();
			setTimeout(function(){z.scroll(z.p.y, z.p.x)},13);
		} else {
			st = ((-Math.cos(p*Math.PI)/2) + 0.5) * (z.p.y-z.s.t) + z.s.t;
			sl = ((-Math.cos(p*Math.PI)/2) + 0.5) * (z.p.x-z.s.l) + z.s.l;
		
			z.scroll(st, sl);
		}
	};
	z.scroll = function (t, l){window.scrollTo(l-top, t-top)};
	z.timer=setInterval(function(){z.step();},13);
};
 
jQuery.intval = function (v)
{
	v = parseInt(v);
	return isNaN(v) ? 0 : v;
};
jQuery.getClient = function(e)
{
	if (e) {
		w = e.clientWidth;
		h = e.clientHeight;
	} else {
		w = (window.innerWidth) ? window.innerWidth : (document.documentElement && document.documentElement.clientWidth) ? document.documentElement.clientWidth : document.body.offsetWidth;
		h = (window.innerHeight) ? window.innerHeight : (document.documentElement && document.documentElement.clientHeight) ? document.documentElement.clientHeight : document.body.offsetHeight;
	}
	return {w:w,h:h};
};
jQuery.getScroll = function (e) 
{
	if (e) {
		t = e.scrollTop;
		l = e.scrollLeft;
		w = e.scrollWidth;
		h = e.scrollHeight;
	} else  {
		if (document.documentElement && document.documentElement.scrollTop) {
			t = document.documentElement.scrollTop;
			l = document.documentElement.scrollLeft;
			w = document.documentElement.scrollWidth;
			h = document.documentElement.scrollHeight;
		} else if (document.body) {
			t = document.body.scrollTop;
			l = document.body.scrollLeft;
			w = document.body.scrollWidth;
			h = document.body.scrollHeight;
		}
	}
	return { t: t, l: l, w: w, h: h };
};

jQuery.getPos = function (e)
{
	var l = 0;
	var t  = 0;
	var w = jQuery.intval(jQuery.css(e,'width'));
	var h = jQuery.intval(jQuery.css(e,'height'));
	var wb = e.offsetWidth;
	var hb = e.offsetHeight;
	while (e.offsetParent){
		l += e.offsetLeft + (e.currentStyle?jQuery.intval(e.currentStyle.borderLeftWidth):0);
		t += e.offsetTop  + (e.currentStyle?jQuery.intval(e.currentStyle.borderTopWidth):0);
		e = e.offsetParent;
	}
	l += e.offsetLeft + (e.currentStyle?jQuery.intval(e.currentStyle.borderLeftWidth):0);
	t  += e.offsetTop  + (e.currentStyle?jQuery.intval(e.currentStyle.borderTopWidth):0);
	return {x:l, y:t, w:w, h:h, wb:wb, hb:hb};
};
//上面函数的内部只用函数,不对外直接使用
jQuery.fx.ScrollTo = function (e,o,top)
{
	var z = this;
	z.o = o;
	z.e = e;
	z.p = jQuery.getPos(e);
	z.s = jQuery.getScroll();
	z.clear = function(){clearInterval(z.timer);z.timer=null};
	z.t=(new Date).getTime();
	z.step = function(){
		var t = (new Date).getTime();
		var p = (t - z.t) / z.o.duration;
		if (t >= z.o.duration+z.t) {
			z.clear();
			setTimeout(function(){z.scroll(z.p.y, z.p.x)},13);
		} else {
			st = ((-Math.cos(p*Math.PI)/2) + 0.5) * (z.p.y-z.s.t) + z.s.t;
			sl = ((-Math.cos(p*Math.PI)/2) + 0.5) * (z.p.x-z.s.l) + z.s.l;
		
			z.scroll(st, sl);
		}
	};
	z.scroll = function (t, l){window.scrollTo(l-top, t-top)};
	z.timer=setInterval(function(){z.step();},13);
};
/*
 * 通用的aja的方式加载下一页,针对后台
 */
function ajaxNextPage(e){
	var urls=$(this).attr("href").replace("ForBuy","ForBuy/ajaxList");
		ajaxUrl(urls,true,true);
}

Title=function(){ 
	var box,mask,nowTop,nowLeft,nowHeight,nowWidth,f=0;
	return{
		Show:function(options){
		   var defaults = {
			     CloseTime:0,//几秒钟后自动关闭
			     Height:99,  //如果是这个高度就自动高度
			     callback:"Close()",  //从这个地方弹出来,如果是null就不谈
			     fromObj        :null  //从这个地方弹出来,如果是null就不弹
			};
			options=Object.extend(defaults, options);
			if(!f){//如果不存在包含容器就先创建一个容器
				box=document.createElement('div'); p.id='tinybox';
				mask=document.createElement('div'); m.id='tinymask';
				b=document.createElement('div'); b.id='tinycontent';
				document.body.appendChild(m); document.body.appendChild(p); p.appendChild(b);
				m.onclick=NetBox.hide;
				window.onresize=NetBox.resize; f=1
			}
			
			
	    },
	    Close:function(){
	    	
	    }
	}
	
}();

/*
*功能：这里仅仅是用于产生一个错误消息到任何对象上，正常的提示不会用于这里，为了更好地兼容以前版本
*描述：目前支持top，right两个方向
*      由此可演变出 提示 、错误的消息框，以及title和右上角的消息提示
*/
$.fn.Title = function(options){ 
var defaults ={
  StyleName: 'TitleError',
  Title:'Message',
  Position:'w', //显示位置  w:右侧  n：下面   e：左边   s: 北侧
  Offset:10, //显示位置偏移量
  OffsetY:5,
  IsShow:true
}; 
options= $.extend(defaults,options); 
if($(this).attr("TitleStyleName"))
  options.StyleName=$(this).attr("TitleStyleName"); 
  if($(this).attr("title"))
  options.Title=$(this).attr("title"); 
  if(options.Title.indexOf('#')==0)
  	options.Title=$(options.Title).html();
 if($(this).attr("Offset"))
  options.Offset=$(this).attr("Offset"); 
 if($(this).attr("TitlePosition"))
	  options.Position=$(this).attr("TitlePosition"); 
 
  var id=$(this).attr("id")+options.StyleName;
  var offsets = $(this).offset();
   
if(options.IsShow)
  {  

	//首先查明当前容器有没有创建过
	$(this).MyTitle({defaultCss:'tipsr',title:"错误消息",trigger:"manual"});
	if(!$(this).attr("errmsg")) 
		$(this).attr("errmsg",options.Title);
      $(this).attr("errorStyle","tipsr");
	 $(this).MyTitle("show"); 
  }
  else
  {
	  $(this).MyTitle("hide"); 
	  $(this).removeAttr("errorStyle"); 
	  //
	 // if(offsets!=null)
     //  $("#"+id).animate({opacity:"0",top:offsets.top},200).fadeOut(100);
  }
}
/*
*功能：检验textbox内容是否符合要求，并给出相应的提示
*参数：obj：jquery对象  要做""判断
*/  
function CheckTextBox(obj){
 var pattern=obj.attr("pattern");
 if(obj.attr("webpattern"))
		pattern=obj.attr("webpattern");
 if(pattern!=null&&pattern!=undefined&&pattern!=""){
	 if($(obj).attr("valueDemo"))
	 {
		 if($(obj).attr("valueDemo")==$(obj).val()){
			 if(!$(obj).data('tipsy'))
				   $(obj).MyTitle({defaultCss:'tipsr',title:"An error occurs here!",trigger:"manual"});
			   $(obj).attr("errorStyle","tipsr");
			   $(obj).MyTitle("show"); 
			 return false;
		 }
	 }
 var subs=pattern.split(";");
 for(var j=0;j<subs.length;j++){
   var cValue=obj.val();
   cValue=cValue.replaceAll("(","（");
   cValue=cValue.replaceAll(")","）");
   //cValue=cValue.replaceAll("[.]","___"); 
   var paraValue=cValue.replace(/["'\n\r\t]/g," "); 
   //alert(paraValue);
   var execStr=subs[j].replace("(","(\""+paraValue+"\",");
   
   if(execStr.indexOf(",)")>0){
     execStr=execStr.replace(")","'"+obj.name+"')"); 
   }else{
     execStr=execStr.replace(")",",'"+obj.name+"')");
   }
   //alert(execStr + "--" + this.eval(execStr));
   if(!this.eval(execStr)){
	   var srcEm = $(obj).attr("errormsg");
	   if(vip.form.error.length > 0){
		   $(obj).attr("errormsg" , vip.form.error);
	   }
	   if(!$(obj).data('tipsy')){
		   $(obj).MyTitle({defaultCss:'tipsr',title:"请重新填写本信息。",trigger:"manual"});
	   }
	    $(obj).attr("errorStyle","tipsr");
	    $(obj).MyTitle("show");
	    if(vip.form.error.length > 0){
			$(obj).attr("errormsg" , srcEm);
			vip.form.error = "";
		} 
        return false;
   }
   else
   {
	   $(obj).removeAttr("errorStyle"); 
	   showRightIcon(obj); 
   } 
  }  
   // $(obj).Title({IsShow:false});
 if($(obj).data('tipsy'))
     $(obj).MyTitle("hide"); 
    return true;
  }else{
    return true;
 }
}
//显示一个勾号icon
function showRightIcon(obj){
	var offset=$(obj).offset();
	var $this=$("<div class='rightIcon' style='left:"+(offset.left+$(obj).width()+20)+"px;top:"+(offset.top+5)+"px'></div>").appendTo("body");
	$(obj).bind("focus",function(){
		$this.remove();
	});
	$(obj).bind("keydown",function(){
		$this.remove();
	});
}
/*
*功能：检验select内容是否符合要求，并给出相应的提示
*参数 obj：jquery对象
*/
function CheckSelect(basicObj){
 var index=basicObj.selectedIndex;
 
 var pattern=basicObj.getAttribute("pattern");
	if(basicObj.getAttribute("webpattern"))
		pattern=basicObj.getAttribute("webpattern");
	
 if(pattern!=null&&pattern!=undefined&&index<=0){
  // $(basicObj).focus();
  var ui=$("#"+$(basicObj).attr("selectid"));//select_16557372

  
  
  if(!ui.data('tipsy'))
	  ui.MyTitle({defaultCss:'tipsr',title:"请选择该选项。",trigger:"manual"});
  if($(basicObj).attr("errormsg"))
	  ui.attr("errormsg",$(basicObj).attr("errormsg"));
  
  ui.attr("errorStyle","tipsr");
  ui.MyTitle("show"); 
  return false;

 }else {
// if(basicObj.getAttribute("pattern")!=null&&basicObj.getAttribute("pattern")!=undefined){
 	 //$(basicObj).parent().Title({Title:"",StyleName:"TitleRight",OffsetY:-5,IsShow:true});
 	// $(".TitleErrorRight").hide("fast");
   return true;
// }
  }
} 

//上面一个函数的继续附属函数
function GoOn(ind)
{
    var obj=window.eventList[ind];
    window.eventList[ind] = null;
    if(obj.NextStep)
      obj.NextStep();
    else
      obj();
}
function Pause(obj,pSecond)
{
/*利用window.eventList系统对象来传递Test这个弱对象，这是由于你的函数有可能是带参数的。
由面向对象的思想，传递参数尽量不要采用全局变量，因为你的对象有可能有1个也有可能有n个，而
有些时候所创建对象的个数并不是你事先可以知道的，那么要创建全局变量的个数自然很难判断了。
所以此处用一个中间载体来传递对象，而不是参数值！*/
   if(window.eventList==null) 
	   window.eventList=new Array();
   var ind=-1;
   for(var i=0;i<window.eventList.length;i++)
   {
      if(window.eventList[i]==null)
      {
        window.eventList[i]=obj;
        ind=i;
        break;
      }
    }
    if(ind==-1)
    {
      ind=window.eventList.length;
      window.eventList[ind]=obj;
    }
    setTimeout("GoOn("+ind+")",pSecond);
}
function FormToStr(obj){
	return FormToStrFun(obj,true);
}
 
function errCalback(isScrool){
	　Close();
	if(isScrool)
	 $(currentErrorObj).ScrollTo({Top:100}); 
}
function errSelectCalback(isScrool){
	Close();
	if(isScrool){
		if($(currentErrorObj).attr("selectid"))
	      $(currentErrorObj).parent().ScrollTo({Top:100});  
		else
			 $(currentErrorObj).ScrollTo({Top:100});
	}
}
//当前错误的对象，用于滚动 
var currentErrorObj=null;
/*
*功能：将一个网页指定ID内部的所有表单格式化成可提交的形式，并且会触发自动验证
*参数 obj:指定id
*/
function FormToStrFun(obj,scrool){
	var str="";
	  if(!T$(obj)){
	     return null;
	  } 
	  var arr=T$(obj).getElementsByTagName("*");
	  for(var i=0;i<arr.length;i++){
	  if(arr[i].tagName!=undefined){
	     if(arr[i].tagName.toLowerCase()=="input"||arr[i].tagName.toLowerCase()=="textarea"){
	      if(arr[i].type.toLowerCase()=="text"||arr[i].type.toLowerCase()=="hidden"||arr[i].type.toLowerCase()=="password"||arr[i].tagName.toLowerCase()=="textarea"){
	    	  if(arr[i].name.length>0){
	    		 if($(arr[i]).attr("valueDemo")!=$(arr[i]).val())
	                str+="&"+encodeURIComponent(arr[i].name)+"="+encodeURIComponent(arr[i].value);
	    		 else
	    			 str+="&"+encodeURIComponent(arr[i].name)+"= ";
	    	 }
	       if(!CheckTextBox($(arr[i]))){ 
	    	    if($(arr[i]).attr("errorName"))
	    	    { 
	    	    	currentErrorObj=arr[i];
	     		  // Wrong("Input error,Please check feild "+$(arr[i]).attr("errorName")+"！",{callback:'errCalback('+scrool+')'});
	    	    }
	    	    else
	    	    {if(scrool)
	    	    	　 $(arr[i]).ScrollTo({Top:100});
	    	    }
	         return null;
	       }
	     }else {
	       if(arr[i].type.toLowerCase()=="checkbox"){
		      // if(!CheckRadio($(arr[i]))){
		       //	 $(arr[i]).ScrollTo(400);
		       //  return null;
		      // }else {
	    	   if(arr[i].checked){
		         if(str.indexOf("&"+encodeURIComponent(arr[i].name)+"=")>-1){
		           str=str.replace("&"+encodeURIComponent(arr[i].name)+"=","&"+encodeURIComponent(arr[i].name)+"="+encodeURIComponent(arr[i].value)+encodeURIComponent("\u2229"));
		         }else {
		           str+="&"+encodeURIComponent(arr[i].name)+"="+encodeURIComponent(arr[i].value);
		         }
	    	   }
		      // }
	        }else {
		       if(arr[i].type.toLowerCase()=="radio"||arr[i].type.toLowerCase()=="checkbox"){
		         if(arr[i].checked){
		           str+="&"+encodeURIComponent(arr[i].name)+"="+encodeURIComponent(arr[i].value);
		         }
		       }
	        }
	     } 
	   }else if(arr[i].tagName.toLowerCase()=="select"){
	       var index=arr[i].selectedIndex;
	       if(arr[i].getAttribute("pattern")!=null&&arr[i].getAttribute("pattern")!=undefined&&index<=0){
	         if(!CheckSelect(arr[i])){
	        	if($(arr[i]).attr("errorName"))
	     	    {
	        		currentErrorObj=arr[i];
	      		   Wrong("请选择"+$(arr[i]).attr("errorName")+"！",{callback:'errSelectCalback('+scrool+')'});　
//	      		   Pause(this,1000);
//	      		　  this.NextStep = function(){
//	      		 　　 　Close();
//	      		if(scrool)
//	      		 　　 $(arr[i]).parent().ScrollTo({Top:100}); 
//	      	     }
	     	    }
	     	    else
	     	    { 
	     	    	if(scrool)
	     	    	 $(arr[i]).parent().ScrollTo({Top:100});
	     	    }
	         	// $(arr[i]).ScrollTo(400);
	        	
	             return null;
	         }
	       }else {
	    	   //没救了
	    	 
	       }
	       if(index>=0){
	         if(arr[i].options){
 
	        	 if(arr[i].name.length>0){
	              str+="&"+encodeURIComponent(arr[i].name)+"="+encodeURIComponent(arr[i].options[index].value);
	              
	        	 }
	         }
	       
	     }
	   }
	  }
	 }
	 
	  return str.substring(1,str.length);
}

//转向到
function Redirect(url){
	self.location=url;
}
/**
 * 原Api地址:http://onehackoranother.com/projects/jquery/tipsy/#
 * 新添加了自动判断方向的功能,该功能会强制沿着顺时针方向改变控件位置
 * 现在宽度是不固定的,当页面在拉伸过成功浮动层本身的宽度也在变化,给计算带来困扰,所以现在限定在100-236像素之间
 * 目前支持两个样式,红色和黄色,黄色为默认样式,只有默认样式才会自动隐藏,其他样式由于设置的data固定了,暂时不支持
 * 并且改名原控件名称为为MyTitle,方便记忆
 */
(function($) {
 function fixTitle($ele) {
     if ($ele.attr('title') || typeof($ele.attr('mytitle')) != 'string') {
         $ele.attr('mytitle', $ele.attr('title') || '').removeAttr('title');
     }
 }
 function Tipsy(element, options) {
     this.$element = $(element);
     this.options = options;
     this.enabled = true;
     fixTitle(this.$element);
 }
Tipsy.prototype = {
     show: function() {
         var title = this.getTitle();
         if (title && this.enabled) {
             var $tip = this.tip();
             $tip.addClass("tipsr"); 
         //alert(this.$element.attr("errorStyle"));
            var hasError=this.$element.attr("errorStyle");
            if(!hasError){
            	if(this.$element.attr("mytitle"))
                 $tip.find('.inner-inner')[this.options.html ? 'html' : 'text'](this.$element.attr("mytitle"));
            	else
            		$tip.find('.inner-inner')[this.options.html ? 'html' : 'text'](title);
              $tip[0].className = 'tipsy';
            }
            else{
            	 var errmsg=this.$element.attr("errormsg");
            	 if(this.$element.attr("errmsg"))
            		 errmsg=this.$element.attr("errmsg");
              $tip.find('.inner-inner')[this.options.html ? 'html' : 'text'](errmsg);
              $tip[0].className = 'tipsy '+hasError;
            }
       
             $tip.remove().css({top: 0, left: 0, visibility: 'hidden', display: 'block'}).appendTo(document.body);
             //这里是新添加的,对于大于236宽度的统一限制宽度
             var nowWidth=$tip.width();
            // if(nowWidth>236){
            	 $tip.find(".tipsy-inner").css({"width":"236px"});
            	 nowWidth=236;
            // }
            // else if(nowWidth<100)
           //  {
            //	 $tip.find(".tipsy-inner").css({"width":"236px"});
            //	 nowWidth=100;
           //  }
             
             var pos = $.extend({}, this.$element.offset(), {
                 width: this.$element[0].offsetWidth,
                 height: this.$element[0].offsetHeight
             });
             
             var actualWidth = $tip[0].offsetWidth, actualHeight = $tip[0].offsetHeight;
             var gravity = (typeof this.options.gravity == 'function')
                             ? this.options.gravity.call(this.$element[0])
                             : this.options.gravity;
           var po=this.$element.attr("position");
           if(po)
        	   gravity=po;
     //   alert(actualHeight+":"+ pos.top);
        //获取总宽度
        var docWidth=$(window).width();  
        var docHeight=$(window).height();
      //如果定义在右边并且宽度不够,那么换到下面,冗余10个像素
  		if((docWidth<(pos.left + pos.width+nowWidth+10))&& (gravity.charAt(0)=='w'))
  			gravity='n'; 
  	   //如果定义在下面并且下面不够或者放在下面还是不够宽度那么定义在左边,冗余10个像素
  		if(((docHeight<(pos.top + pos.height+actualHeight+10)) ||  docWidth<(pos.left + pos.width/2+nowWidth/2+10))&& (gravity.charAt(0)=='n'))
  			gravity='e';  
  	  //如果定义在左边并且宽度不够,那么换到上面,冗余10个像素
  		if((pos.left<(nowWidth+10))&& (gravity.charAt(0)=='e'))
  			gravity='s'; 
  	   //如果定义在下面并且下面不够那么定义在下边,冗余10个像素
  		if((pos.top<(actualHeight+10))&& (gravity.charAt(0)=='s'))
  			gravity='n';
             var tp;
             switch (gravity.charAt(0)){
                 case 'n':
                     tp = {top: pos.top + pos.height + this.options.offset, left: pos.left + pos.width / 2 - actualWidth / 2};
                     break;
                 case 's':
                	 var tipsyArrow = $tip.find('.tipsy-arrow');
                	 tipsyArrow.css({top : actualHeight - tipsyArrow.height()});
                     tp = {top: pos.top - actualHeight - this.options.offset, left: pos.left + pos.width / 2 - actualWidth / 2};
                     break;
                 case 'e':
                     tp = {top: pos.top + pos.height / 2 - actualHeight / 2, left: pos.left - actualWidth - this.options.offset};
                     break;
                 case 'w':
                     tp = {top: pos.top + pos.height / 2 - actualHeight / 2, left: pos.left + pos.width + this.options.offset};
                     break;
             }
             
             if (gravity.length == 2) {
                 if (gravity.charAt(1) == 'w') {
                     tp.left = pos.left + pos.width / 2 - 15;
                 } else {
                     tp.left = pos.left + pos.width / 2 - actualWidth + 15;
                 }
             }
             $tip.css(tp).addClass("tipsy-"+ gravity);
             if (this.options.fade) { 
                 $tip.stop().css({opacity: 0, display: 'block', visibility: 'visible'}).animate({opacity: this.options.opacity});
             } else {
                 $tip.css({visibility: 'visible', opacity: this.options.opacity});
             }
         }
     },
     hide: function() {
         if (this.options.fade) {
             this.tip().stop().fadeOut(function() { $(this).remove(); });
         } else {
             this.tip().remove();
         }
     },
     getTitle: function() {
         var title, $e = this.$element, o = this.options;
         fixTitle($e);
         var title, o = this.options;
         if (typeof o.title == 'string') {
             title =o.title == 'title' ? $e.attr('mytitle') : o.title;
            
         } else if (typeof o.title == 'function') {
             title = o.title.call($e[0]);
         }
         title = ('' + title).replace(/(^\s*|\s*$)/, "");
         return title || o.fallback;
     },
     
     tip: function() {
         if (!this.$tip) {
             this.$tip = $('<div class="tipsy"></div>').html('<div class="tipsy-arrow"></div><div class="tipsy-inner"><div class="inner-inner"></div></div></div>');
         }
         return this.$tip;
     },
     validate: function() {
         if (!this.$element[0].parentNode) {
             this.hide();
             this.$element = null;
             this.options = null;
         }
     },
     enable: function() { this.enabled = true; },
     disable: function() { this.enabled = false; },
     toggleEnabled: function() { this.enabled = !this.enabled; }
 };
 
 $.fn.MyTitle = function(options){
     if (options === true){
         return this.data('tipsy');
         }else if (typeof options == 'string'){
        	 if(this.data('tipsy'))
               return this.data('tipsy')[options]();
        	 else
        		 return null;
     } 
     options = $.extend({}, $.fn.MyTitle.defaults, options);
     function get(ele) {
         var tipsy = $.data(ele,'tipsy');
         if (!tipsy) {
             tipsy = new Tipsy(ele, $.fn.MyTitle.elementOptions(ele, options));
             $.data(ele,'tipsy', tipsy);
         }
         return tipsy;
     }
     function enter() {
         var tipsy = get(this);
         tipsy.hoverState = 'in';
         if (options.delayIn == 0) {
             tipsy.show();
         } else {
             setTimeout(function() { if (tipsy.hoverState == 'in') tipsy.show(); }, options.delayIn);
         }
     };
     function leave(){
         var tipsy = get(this);
         tipsy.hoverState = 'out';
         if (options.delayOut == 0){
             tipsy.hide();
         }else{
             setTimeout(function() { if (tipsy.hoverState == 'out') tipsy.hide(); }, options.delayOut);
         }
     };
     if (!options.live) this.each(function() { get(this); });
     if (options.trigger != 'manual') {
         var binder   = options.live ? 'live' : 'bind',
             eventIn  = options.trigger == 'hover' ? 'mouseenter' : 'focus',
             eventOut = options.trigger == 'hover' ? 'mouseleave' : 'blur';
         this[binder](eventIn, enter)[binder](eventOut, leave);
     }
     return this;
 };
  
 $.fn.MyTitle.defaults ={
	 defaultCss:' ', //不为空的时候会使用这个css
     delayIn: 0,
     delayOut: 0,
     fade: false,
     fallback: '',
     gravity: 'w',
     html: true,
     live: false,
     offset: 0,
     opacity: 1,
     title: 'title',
     trigger: 'hover'
 };
 
 // Overwrite this method to provide options on a per-element basis.
 // For example, you could store the gravity in a 'tipsy-gravity' attribute:
 // return $.extend({}, options, {gravity: $(ele).attr('tipsy-gravity') || 'n' });
 // (remember - do not modify 'options' in place!)
 $.fn.MyTitle.elementOptions = function(ele, options) {
     return $.metadata ? $.extend({}, options, $(ele).metadata()) : options;
 };
 
 $.fn.MyTitle.autoNS = function() {
     return $(this).offset().top > ($(document).scrollTop() + $(window).height() / 2) ? 's' : 'n';
 };
 
 $.fn.MyTitle.autoWE = function() {
     return $(this).offset().left > ($(document).scrollLeft() + $(window).width() / 2) ? 'e' : 'w';
 };
 
})(jQuery);
//兼容的Title
 

/*
 * 更改title的样式为错误或者其他,当为错误时传入的是errormsg信息采用另一个样式
 * style : true false title:标题
 */
function changeTitle(obj,style){
	if(style.length>0)
	  $(obj).attr('errorStyle',style);
	else
		 $(obj).removeAttr('errorStyle');
}

//解决ie下跳转不记录referer值
function redirecToWithReferer(url) {
    var referLink = document.createElement('a');  
    referLink.href = url;  
    document.body.appendChild(referLink);  
    referLink.click();  
}


/****************************************************/

jQuery.fn.FixedHeader = function(options) { var settings = jQuery.extend({ headerrowsize: 1, highlightrow: false, highlightclass: "highlight" }, options); this.each(function(i) { var $tbl = $(this); var $parent=$(this).parent(); var $tblhfixed = $tbl.find("tr:lt(" + settings.headerrowsize + ")"); var headerelement = "th"; if ($tblhfixed.find(headerelement).length == 0) headerelement = "td"; if ($tblhfixed.find(headerelement).length > 0) { $tblhfixed.find(headerelement).each(function() { $(this).css("width", $(this).width()); }); var $clonedTable = $tbl.clone().empty(); var tblwidth = GetTblWidth($tbl); $clonedTable.attr("id", "fixedtableheader" + i).css({ "position": "fixed", "top": "0", "left": $tbl.offset().left }).append($tblhfixed.clone()).width(tblwidth).hide().appendTo($parent); if (settings.highlightrow) $("tr:gt(" + (settings.headerrowsize - 1) + ")", $tbl).hover(function() { $(this).addClass(settings.highlightclass); }, function() { $(this).removeClass(settings.highlightclass); }); $(window).scroll(function() { if (jQuery.browser.msie && jQuery.browser.version == "6.0") $clonedTable.css({ "position": "absolute", "top": $(window).scrollTop(), "left": $tbl.offset().left }); else $clonedTable.css({ "position": "fixed", "top": "0", "left": $tbl.offset().left - $(window).scrollLeft() }); var sctop = $(window).scrollTop(); var elmtop = $tblhfixed.offset().top; if (sctop > elmtop && sctop <= (elmtop + $tbl.height() - $tblhfixed.height())) $clonedTable.show(); else $clonedTable.hide(); }); $(window).resize(function() { if ($clonedTable.outerWidth() != $tbl.outerWidth()) { $tblhfixed.find(headerelement).each(function(index) { var w = $(this).width(); $(this).css("width", w); $clonedTable.find(headerelement).eq(index).css("width", w); }); $clonedTable.width($tbl.outerWidth()); } $clonedTable.css("left", $tbl.offset().left); }); } }); function GetTblWidth($tbl) { var tblwidth = $tbl.outerWidth(); return tblwidth; } };

function encodeURI(str){
	str=str+"";
	str=str.replaceAll("-","__");
	str=str.replaceAll(".","___");
	str=str.replaceAll("[.]","___");
	return encodeURIComponent(str);
}

/**动态加载js  解决jquery自带加载js 出错**/
$.extend({
    loadScript : function(file) {
	   var d=new Date().getTime(); 
       var files = typeof file == "string" ? [file]:file;
       var header = $("head");
       for (var i = 0; i < files.length; i++) {
    	   var h=document.getElementsByTagName("head")[0]; 
    	   var f=document.createElement("script");  
    	   f.type="text/javascript";  
    	   f.id = d + '-' + i;
    	   f.src=files[i]+'?'+d;
    	   h.appendChild(f); 
       }
    },
    getServerDate : function(xhs){
    	return new Date(xhs.getResponseHeader('Date'));
    },
    getServerTime : function(xhs){
    	return this.getServerDate(xhs).getTime(); 
    }
});


function Void(){}


var urlreplace="/ajaxList-";
//新url地址
function newUrl(e,thiss){
	var url=$(thiss).attr("href");
	if(url&&url.indexOf('javascript')!=0&&url.indexOf('-')>0){
		//说明是正常的访问
		url=url.replace("-",urlreplace);
		ajaxUrl(url,true,false,"text");
	}
	if ( e && e.preventDefault ) 
		e.preventDefault(); 
		// A shortcut for stoping the browser action in IE 
		else 
		window.event.returnValue = false; 
		return false; 
}
function checkPageInput(e,page){
	 e.keyCode==13 && $("#JumpButton").trigger("click"); 
}
/**
* 功能:进行跳转
* @return
*/
function jumpPage(maxSize){
	var nowvalue=T$("PagerInput").value;
	if(nowvalue>maxSize)
	{
		Wrong("page number is too large",{CloseTime:1});
		return;
	}
	if(nowvalue<1)
	{
		if(nowvalue=='')
			Info('please input page number',{CloseTime:1});
		else
		   Wrong("page number is too small",{CloseTime:1});
		return;
	}
	else{
		//这个正则盖过了，以前的有误，答大页码数不进去to
		var regNum =/^[0-9]+.?[0-9]*$/;
		if(regNum.test(nowvalue)){
			var url=getPageUrl();//在客户端定义
			if(url.length>0)
			  ajaxUrl(url,true,true,"text");
		}
		else
		{
			Wrong("Page number you entered is not valid, please re-enter.",{CloseTime:1});
		}
		
		
	}
}
var vip = {
//	cookiKeys : {
//		uon : "wuon",
//		uname : "wuname",
//		uid : "wuid",
//		aid : "waid",
//		rid : "wrid",
//		aname : "waname"
//	},
//	vipDomain : "https://tvip.vip.com",
//	p2pDomain : "https://tp2p.vip.com",
//	transDomain : "https://ttrans.vip.com",
//	staticDomain : "https://ts.vip.com",

	cookiKeys : {
		uon : JsCommon.uon,
		uname : JsCommon.uname,
		uid : JsCommon.uid,
		aid : JsCommon.aid,
		rid : JsCommon.rid,
		aname : JsCommon.aname,
		note : JsCommon.note,//最新查看过的公告id
		lan : JsCommon.lan
	},
	mainDomain : JsCommon.mainDomain,
	vipDomain : JsCommon.vipDomain,
	p2pDomain : JsCommon.p2pDomain,
	transDomain : JsCommon.transDomain,
	staticDomain : JsCommon.staticDomain,
	urlsAjax : {},
	/******
	 * 通用的ajax处理方法  
	 * ops ：formId , urls , sucFunction , errorFunction , dataType
	 * @param {Object} ops.formId  包含表单信息的标签ID
	 * @param {Object} ops.url 后台操作的URL  此方法默认为POST提交，一般用于处理添加、修改等操作
	 * @param {Object} ops.suc 成功时的额外调度   
	 * @param {Object} ops.err 失败时的额外调度  
	 * @param {Object} ops.dataType ajax返回的数据类型
	 * @param {Object} ops.div 显示loading效果的DIV ID
	 * 
	 * @memberOf {TypeName} 
	 * @return {TypeName} 
	 */
	ajax : function(ops){//为每个url产生一个对象
		//[0] loading : false ,
		//[1] needLoading : true,
		var nld = true;
		if(ops.needLoading == undefined){
			nld = true;
		}else{
			nld = ops.needLoading == false ? false : true;
		}
		var _this = this;
		var rurl = ops.url.indexOf("?") > -1 ? ops.url.substring(0 , ops.url.indexOf("?")) : ops.url;
		var curURL = _this.urlsAjax[rurl];
		if(curURL == null){
			_this.urlsAjax[rurl] = {loading : false , needLoading : nld}; 
			curURL = _this.urlsAjax[rurl];
		}
		new this.ajaxDeal(ops , curURL);
	},
	ajaxDeal : function(ops , curURL){
			if(!ops.url){Wrong("url参数必须传递！");}
			if(curURL.loading){return;}
			var _this = this,
				datas = "",
				divId = ops.div || null,
				needLogin = ops.needLogin || false;
			var nasync = true;
			if(ops.async == undefined){
				nasync = true;
			}else{
				nasync = ops.async == false ? false : true;
			}
			_this.dataType = ops.dataType || "xml";
			if(curURL.needLogin && !vip.user.checkLogin()){return;}
			if(ops.formId){
				datas=FormToStr(ops.formId);
				if(datas==null){return;}
			}
			curURL.loading = true;
			//alert(divId + "-" + curURL.needLoading);
			if(divId && curURL.needLoading){$("#"+divId).Loadding({OffsetXGIF:0,OffsetYGIF:0});}
		    $.ajax({
			   async:nasync,
			   cache:false,
			   type:"post",
			   dataType : _this.dataType,
			   url : ops.url,
			   data : datas,
			   error:function(xml){curURL.loading = false;if(divId){$("#"+divId).Loadding({IsShow:false});}},
			   timeout:60000,
			   contentType : "application/x-www-form-urlencoded; charset=UTF-8",			   
			   success:function(xml){
	        	  curURL.loading = false;
	        	  if(divId){$("#"+divId).Loadding({IsShow:false});}
	        	  if(_this.dataType == 'xml'){
	        		  if($(xml).find("State").text()=="true"){
			              if(typeof ops.suc == "function"){
			            	  (function(v){ return ops.suc(v);})(xml)
			              }else{Right($(xml).find("Des").text());}
			          }else{
			              if(typeof ops.err == "function"){
			            	  (function(v){ return ops.err(v);})(xml)
			           	  }else{Wrong($(xml).find("Des").text());}
			          }
	        	  }else if(_this.dataType == 'json'){
	        		  if(xml.isSuc){
			              if(typeof ops.suc == "function"){
			            	  (function(v){ return ops.suc(v);})(xml)
			              }else{Right(xml.des);}
			          }else{
			              if(typeof ops.err == "function"){
			            	  (function(v){ return ops.err(v);})(xml)
			           	  }else{Wrong(xml.des);}
			          }
	        	  }else{
	        		  if(typeof ops.suc == "function"){
			           	  (function(v){ return ops.suc(v);})(xml)
			          }
	        		  if(typeof ops.err == "function"){
			              (function(v){ return ops.err(v);})(xml)
			          }
	        	  }
			   }
		    });
		},
	top : {
		init : function(){
			vip.user.init();
		},
		close_city : function(){
			$("#selectcity_div").hide();
		},
		show_city : function(){
			$("#selectcity_div").show();
		},
		changeCity : function(c){
			$(".tuangouflsy_pf_chengshi").each(function(){
				$(this).hide();
			});
			$("#city_span_"+c).show();
		}
	},
	tool : {
		param : function(key){//获取url中的参数    key为要获取的参数名称
			var r = window.location.search.substr(1).match(new RegExp("(^|&)" + key + "=([^&]*)(&|$)", "i"));
	    	if (r != null) return unescape(r[2]); return null;
		},
		addBookmark : function(title){/**加入收藏**/
			var url=location.href;
			if (window.sidebar){
				window.sidebar.addPanel(title, url,"");
			}else if( document.all ){
				window.external.AddFavorite( url, title);
			}else if( window.opera && window.print ){
				return true;
			}
		},
		initBackBtn : function(){
			var roleId=$.cookie("RoleId");
			if(roleId>0){
				var path = '/admin/Module/list.js';
				$.loadScript(path);
			}
		},
		getTimeShowByMillSeconds : function(cha){
			var day= parseInt(cha/(24*60*60*1000)); 
			var hour=parseInt(cha/(60*60*1000)-day*24); 
			hour=hour>=10?hour:"0"+hour;
			var min=parseInt((cha/(60*1000))-day*24*60-hour*60); 
			min=min>=10?min:"0"+min;
			var s=parseInt(cha/1000-day*24*60*60-hour*60*60-min*60); 
			s=s>=10?s:"0"+s;
			return ""+day+"天"+hour+"小时"+min+"分"+s+"秒";
		},
		isFloat : function(num){
			if(num){
				var reg=/^[0-9]*\.?[0-9]*$/;
			    if(!reg.test(num)){
			    	return false;
			    }
			}else{
				return false;
			}
		    return true;
		}
	},
	user : {///用户相关
		cookieInit : false,
		loginStatus : false,
		inAjaxing : false,
		lastPrice : 0,
		tickJson : null,
		isLogin : function(){
			if(!this.cookieInit){
				this.init();
			}
			return this.loginStatus;
		},
		init : function(){
			 this.cookieInit = true;
			 var userNameCookie=$.cookie("name");
			 var loginS = !this.loginStatus ? $.cookie("on")=="1" : this.loginStatus;
			//if(this.cookieInit){return;}
			 var _this = this;
			 if(_this.firstTitle.length == 0){
				 _this.firstTitle = document.title;
			 }
		 	 _this.ticker();
		 	 //_this.uticker();
		 	 var curUrl = document.location.pathname;
			 if(loginS){
				$.getJSON(vip.vipDomain + "/data/zcticker?callback=?",  function(json) {
					$("#assets .info b").html(json.total);
				});
			 }
		 	 setInterval(function(){
		 		 _this.ticker();
		 	 },32000);
			 
			 if(loginS || this.loginStatus){//登录成功
				    $("#myAccount").hide();
				   	$("#assets").show();
					$("#logOut").show();
					$("#assets .info span a").text($.cookie("name")+"，"+this.showTime());
					$("#Mine").show();
					$("#topbaright .msg").show();
					
					//$("#jiaoYiXiangGuan").show();
			 //	this.userInfo.init();
			 	this.loginStatus = true;
			 	//this.getMsg();//获取站内信 
			 	this.assets();
			 }else{
				    $("#topbaright .msg").hide();
				 	$("#myAccount").show();
				 	$("#assets").hide();
				    $("#myAccountInfo a").show();
				    $("#assets p").hide();
			 }
		},
		showTime : function(){
			var now = new Date();
			var hours = now.getHours();
			return hours >= 0&&hours < 6 ?"早上好" : hours >=6&&hours<12?"上午好":hours>=12&&hours<13?"中午好":hours>=13&&hours<18?"下午好":"晚上好";
		},
		firstTitle : "",
		ticker : function(){
			var _this = this;
			$.getJSON(vip.vipDomain + "/data/webticker?callback=?",  function(json) {
				_this.tickJson=json;
				if(json.ticker[12] == 2){
					$("#statisticsDiv li").eq(0).removeClass("up").addClass("down");
				}else{
					$("#statisticsDiv li").eq(0).removeClass("down").addClass("up");
				}
				if(json.ticker[13] == 2){
					$("#statisticsDiv li").eq(1).removeClass("up").addClass("down");
				}else{
					$("#statisticsDiv li").eq(1).removeClass("down").addClass("up");
				}
				_this.lastPrice = json.ticker[0];
				if(json.ticker.length == 14){
					$("#statisticsDiv em").each(function(i){
						$(this).text(json.ticker[i]);
					});
				}
				
			});
		},
		uticker : function(){
			var _this = this;
			$.getJSON(vip.vipDomain + "/data/userticker?callback=?",  function(json) {
//				_this.tickJson=json;
//				_this.lastPrice = json.ticker.last;
//				var balanceBuyOrSell = $("#balanceBuyOrSell");
//				if(balanceBuyOrSell.length > 0){
//					buyOrSellInfoShow(json.ticker.rmb , json.ticker.btcs , json.ticker.sellRmb , json.ticker.buyBtc , json.ticker.btq , json.ticker.fbtq, json.ticker.ltc , json.ticker.fltc , json.ticker.buyLtc , json.ticker.ltcSellRmb);
//				}
//				_this.exeTickerJson();
				$("#assets .info b").html(json.total);
			});
		},
		exeTickerJson : function(){
			if(this.tickJson != null){
				var ticker = this.tickJson.ticker;
				if(($("#userPayAccountInfo").length > 0) && ticker != null){
					accountInfoShow(ticker.rmb , ticker.btcs , ticker.frmb , 
						ticker.fbtc , ticker.buyBtc , ticker.sellRmb , ticker.total , ticker.buy , ticker.sell, ticker.btq ,ticker.noBtc, ticker.nextBtc, ticker.ltc , ticker.fltc , ticker.buyLtc , ticker.sellLtc,ticker.nextLtcs);
				}
			}
		},
		assets : function(){///获取用户资金信息
			var ccLength=parseInt($("#assets").width())-637;
			$("#assets").DropSelecter({ControlSeleter:"#finaPanelDown",Top:-6,Left:ccLength,StyleHover:"download_hover",call : function(){
				$.getJSON(vip.vipDomain + "/u/getBalance?callback=?",  function(result) {
					var funds = result.funds;
					$("#finaPanelDown b").each(function(i){
						$(this).text(funds[i]);
					});
				});
			}});
		},
		userInfo : {
			timer : null ,
			init : function(){
			},
			show : function(){
			},
			hide : function(){
			}
		},
		login : function(call,isShowTitle,nce){
			var ist = isShowTitle || true;
			var callBack = call || "Close";
			Iframe({Url: vip.vipDomain + '/user/loginAuthentication?callback='+callBack+'&needClose='+nce,isShowIframeTitle:ist, fromObj:T$("login"),Width:450,Height:350,scrolling:'no',	isShowClose:false,Title:'Login',isShowIframeTitle:true});
		},
		checkLogin : function(call,isShowTitle,needToUser){//回调 是否显示标题 是否转到用户首页
			this.loginStatus = $.cookie("on") == '1';
			var ist = isShowTitle?isShowTitle:true;
			var nce = needToUser;
			if(!this.loginStatus){
				this.login(call,ist,nce);
				return false;
			}
			return true;
		},
		loginScuess : function(userName , isClose){
			if(isClose){Close();}else{Right(vip.L("登陆成功！"));}
			$("#userNameCookie").text(userName);
			this.loginStatus = true;
			this.init();
		}
	},
	form : {error : ""},
	getLan : function(){
		if($("#languageInput").val() == "cn"){
			return "cn";
		}
		return "en";
	},
	L : function(key){
		if(this.getLan() == "cn"){
			return this.tips[key][0]; 
		}
		return this.tips[key][1];
	},
	addTips : function(key , value){
		if(this.getLan() == "cn"){
			vip.tips[key] = [value,'']; 
		}else{
			vip.tips[key] = ['',value]; 
		}
	},
	tips : {"登陆成功！" : ["登陆成功！","Login Success!"],
			"您确定注销登录么？" : ["您确定注销登录么？","Are you sure you want to log out!"],
			"请检查网络，可能是网络过慢导致超时或者远程服务出现故障!":["请检查网络，可能是网络过慢导致超时或者远程服务出现故障!","net errors!"],
			"确定":["确定","Ok"],
			"取消":["取消","Cancel"],
			"确定删除":["您确定要删除该项吗？删除后无法恢复！","Are you sure you want to delete that? Deleted can not be recovered!"]
				,"确定要取消本次充值吗？":["确定要取消本次充值吗？","Sure you want to cancel the recharge?"]
				,"确定删除本项吗？":["确定删除本项吗？","make sure delete this items?"]
				,"确定取消吗？":["确定取消吗？","make sure cancle?"]
				,"用户登录":["用户登录","User Login"]
				,"登录":["登录","Login"]
				,"注册":["注册","Sign up"]
				,"收件箱":["收件箱","Inbox"]
				,"未读邮件":["未读邮件",""]
				,"请选择一项":["请选择一项","Please select a letter to be deleted!"]
				,"确定要删除选中的项吗？":["确定要删除选中的项吗？","Are you sure you want to delete the selected messages?"]
				,"单价":["单价", "Unit Price"]
		   }
		   
};

///////////////////列表通用js
vip.list = {
	basePath : "",///基础路径
	funcName : "",///功能模块的名称  比如说当前操作模块为产品模块  则可以指定fucName = "产品";
	isInit : false,
	ui : function(ops){////uiform模块
		if(!ops)ops={};
		var formId = ops.formId || "searchContaint";
		var curForm = $("#"+formId);
		if(curForm.length > 0){curForm.Ui();}
		this.pageInit();
		this.tabInit();
	},
	tabInit : function(){
		var tab = vip.tool.param("tab");
		if(tab != null){
			this.dealTab(tab);
		}
	},
	aoru : function(ops){///addOrUpdate 简写  在窗口中添加或修改 ops={id: , width : ,height : title: otherParam:};//除了id参数其他均可为空   传参方式   ： {id : "1223"}  otherParam其他参数
		if(!ops)ops={};
		if(!ops.id){Wrong("ID参数必须传递！");}
		var id = ops.id,
			iw = ops.width || 560,
			ht = ops.height || 360,
			tit = ops.title || "添加/编辑" + this.funcName,
			scroll = ops.scroll || "auto";
			otherParam = ops.otherParam || "",
			_this = this,
			url = ops.url || _this.basePath+"aoru?id="+id+otherParam;
		Iframe({
	        Url : url,
	        Width : iw,
	        Height : ht,
	        scrolling : scroll,
	        Title : tit
	    });
	},
	del : function(ops){///del  delete缩写  实现当前模块的删除功能   ops={id }
		if(!ops)ops={};
		if(!ops.id){Wrong("ID参数必须传递！");}
		var id = ops.id,
			_this = this;
		Ask2({Title:"确定删除本项吗？",call:function(){
			vip.ajax({url : _this.basePath+"doDel?id="+id , suc : function(xml){
				_this.reload();
				Right($(xml).find("Des").text());
			}});
		}});
	},
	dealTab : function(tab){
		$("#"+tab).parent("div").find("a").removeClass("current");
		$("#"+tab).parent("li").parent("ul").find("li a").removeClass("current");
		$("#"+tab).addClass("current");
	},
	search : function(ops){///搜索功能  ops={formId : div:};formId:搜索对应的表单域id  div:ajax加载区域ID page:当前要加载的页码
		if(!ops)ops={};
		var formId = ops.formId || "searchContaint",
		    div = ops.div || "shopslist",
		    page = ops.page || 1,
		    datas = "",
		    special = ops.special || false,//是否需要特效
		    validate = ops.validate || false,//是否进行表单验证
		    tab = ops.tab,
		    _this = this;
		var nld = true;
		if(ops.needLoading == undefined){
			nld = true;
		}else{
			nld = ops.needLoading == false ? false : true;
		}
		if($("#"+formId).length > 0){
			if(tab){//tab处理
				var curtab = $("#"+formId).find("input[name='tab']");
				if(curtab.length > 0){
					curtab.val(tab);
				}else{
					$("<input name='tab' value='"+tab+"' type='hidden'>").appendTo($("#"+formId));
				}
				this.dealTab(tab);
			}
			datas=FormToStr(formId);
			if(validate){
				if(datas == null)return;
			}else{
				datas = datas == null ?　"" : datas;
			}
			datas = "?"+datas+"&page="+page;
		}
		var urls="ajax"+datas;
		vip.ajax({url : _this.basePath+urls, needLoading : nld , suc : function(text){
			if(special){
				$("#"+div).html(text);
			}else{
				$("#"+div).html(text);
			}
			_this.pageInit();
		},dataType : "text",div : div});
	},
	ajaxPage : function(ops , e){
		if(ops.needInit && !this.isInit){return;}
		if(!ops)ops={};
		if(!ops.url){Wrong("url参数必须传递！");}
		var url = ops.url,
			div = ops.div || "shopslist",
			_this = this;
		var nld = true;
		if(ops.needLoading == undefined){
			nld = true;
		}else{
			nld = ops.needLoading == false ? false : true;
		}
		vip.ajax({url : url ,needLoading : nld,  suc : function(text){
			var isTextChange = text != $("#"+div).html();
			if(isTextChange)
				$("#"+div).html(text);
			if(ops.needInit){
				_this.pageInit();
			}
			if(typeof ops.suc == "function"){
				(function(){ops.suc(isTextChange);}());
			}
		},dataType : "text",div : div});
		if(e){
			if ( e && e.preventDefault ) 
			e.preventDefault(); 
			// A shortcut for stoping the browser action in IE 
			else 
			window.event.returnValue = false; 
			return false; 
		}
	},
	jumpPage : function(maxSize){
		var nowvalue=$("#PagerInput").val();
		if(nowvalue > maxSize){
			Wrong("您输入的页码过大，请重新输入。",{CloseTime:1});
			return;
		}
		if(nowvalue<1){
			if(nowvalue=='')
				Info('请输入页码',{CloseTime:1});
			else
			   Wrong("您输入的页码太小了，请重新输入。",{CloseTime:1});
			return;
		}else{
			//这个正则盖过了，以前的有误，答大页码数不进去
			var regNum =/^[0-9]+.?[0-9]*$/;
			if(regNum.test(nowvalue)){
				this.search({page : nowvalue});
			}else{
				Wrong("您输入不是有效页码，请重新输入。",{CloseTime:1});
			}
		}
	},
	resetForm : function(ops){//保证在form中
		if(!ops)ops={};
		var formId = "",
			_this = this;
		formId = ops.formId || "searchContaint"
		$("#"+formId).each(function(){
			this.reset();
			_this.search()
		});
	},
	config : function(){////配置功能
		$(".item_list_bd").each(function(i){
	        $(this).mouseover(function(){
	            $(this).css("background","#fff8e1");
	        }).mouseout(function(){
	        	  $(this).css("background","#ffffff");
	        });
	    });
	},
	reload : function(ops) {///页面刷新  ops={formId : div:};formId:搜索对应的表单域id  div:ajax加载区域ID page:当前要加载的页码
		if(!ops)ops={};
		ops.page = $("#PagerInput").val() || 1;
		this.search(ops);
	},	
	look : function(ops){///查看功能
		if(!ops)ops={};
		if(!ops.url){Wrong("url参数必须传递！");}
		var url = ops.url,
			iw = ops.width || 820,
			ht = ops.height || 716,
			isShowIframeTile = ops.isShowIframeTile == undefined ? true : ops.isShowIframeTile,
			scrolling = ops.scrolling || "auto",
			needLogin = ops.needLogin || false,
			_this = this;
		if(needLogin && !vip.user.checkLogin()){
			return;
		}
		Iframe({
	        Url : url,
	        Width : iw,
	        Height : ht,
	        scrolling : scrolling,
	        isShowIframeTitle : isShowIframeTile,
	        Title : "查看"+_this.funcName
	    });
	},
	noPass : function(beanId){
		Iframe({
			Url : "/admin/user/reason?id=" + beanId,
			zoomSpeedIn : 200,
			zoomSpeedOut : 200,
			Width : 600,
			Height: 460,
			Title : "填写不通过的原因"
		});
	},
	url : function(ops){//访问一个url  什么也不做  只提示是否成功
		if(!ops)ops={};
		if(!ops.url){Wrong("url参数必须传递！");}
		var url = ops.url;
		vip.ajax({url : url});
	},
	pageInit : function(){
		//固定表头,就是浏览到下面的部分时候依然可以固定住表的头部
		$("#ListTable").FixedHeader();
		//设置导航链接成ajax方式
		$("#pagin a,#page_navA a").each(function(){
			$(this).AStop();
		});
		$("#PagerInput").UiText();
		this.isInit = true;
		
	},
	reloadAsk : function(ops){//带有自动刷新页面的ask
		if(!ops)ops={};
		if(!ops.title){Wrong("请传递您的询问标题！");return;}
		if(!ops.url){Wrong("请传递您的后台url！");return;}
		var _this = this;
		Ask2({Msg:ops.title, call:function(){
			vip.ajax({url : ops.url, suc : function(xml){
				Right($(xml).find("Des").text() , {call:function(){
					_this.reload();
					Close();
				}});
			},err:function(xml){
				Wrong($(xml).find("Des").text());
			}}); 
		}});
	}
};

var showids = "";
function showUser(id) {
	showids = id;
	var uName = $("#text_"+showids).text()=="undefined"?"":" " + $("#text_"+showids).text() + " ";
	$.Iframe({
		Url : '/admin/user/show?userId=' + showids+"&mCode="+id,
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

function setDomain(){
	try {
		if (parent.document.domain != document.domain) {
			document.domain = parent.document.domain;
		}
	}catch (ex){
		document.domain = parent.document.domain;
	}
}