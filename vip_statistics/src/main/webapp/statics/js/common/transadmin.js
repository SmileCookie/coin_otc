function gengxin(a){1==a?loadSpace>10?(loadSpace-=10,$("#gengxin").text(loadSpace/10+"秒更新"),$.jGrowl("数据正常更新速度加快为"+loadSpace/10+"秒,可能需要几秒钟生效！",{life:5e3,position:"bottom-right"}),lastTime=0,reloadDue=0):$.jGrowl("已经是最快的更新速度了",{life:5e3,position:"bottom-right"}):150>loadSpace?(loadSpace+=10,$("#gengxin").text(loadSpace/10+"秒更新"),$.jGrowl("数据正常更新速度减慢为"+loadSpace/10+"秒,可能需要几秒钟生效！",{life:5,position:"bottom-right"}),lastTime=0,reloadDue=0):$.jGrowl("不支持更慢的更新速度",{life:5e3,position:"bottom-right"})}function dangwei(a){1==a?50==length?$.jGrowl("已经是最多的档位显示了",{life:5e3,position:"bottom-right"}):(length=5==length?10:10==length?20:20==length?50:5,$("#dangwei").text("查看"+length+"档"),$.jGrowl("数据显示档位调整为"+length+"档,可能需要几秒钟生效！",{life:5,position:"bottom-right"}),lastTime=0,reloadDue=0):5==length?$.jGrowl("已经是最少的档位显示了",{life:5e3,position:"bottom-right"}):(length=10==length?5:20==length?10:50==length?20:5,$("#dangwei").text("查看"+length+"档"),$.jGrowl("数据显示档位调整为"+length+"档,可能需要几秒钟生效！",{life:5e3,position:"bottom-right"}),lastTime=0,reloadDue=0)}function getCurr(){isRunning=!0,$.getJSON(entrustUrlBase+"Line/GetTrans-"+market+"?lastTime="+lastTime+"&length="+length+"&jsoncallback=?",function(a){show(a,length)})}function getMoney(){$.getJSON(vip.vipDomain+"/u/getBalance?callback=?",function(a){var b,c,d,e,f,g;$(".b_tradinfo h2").text($.cookie(vip.cookiKeys.uname)),b=a.funds[1],c=a.funds[4],d=a.funds[7],e=a.funds[10],"btcdefault"==market?(currentUserNumber=c,currentUserExchange=b):"ltcdefault"==market?(currentUserNumber=d,currentUserExchange=b):"btqdefault"==market&&(currentUserNumber=e,currentUserExchange=c),$("#exchangeBiNum").html(currentUserExchange),$("#numberBiNum").html(currentUserNumber),$("#suggestBuyNumber").html(currentUserNumber),f=a.funds,$(".b_tradinfo .bd em").length>0&&($(".b_tradinfo .bd em").each(function(a){$(this).text(f[a])}),$(".b_tradinfo h3").text(f[16])),$("#finaPanelDown2 b").length>0&&($("#finaPanelDown2 b").each(function(a){$(this).text(f[a])}),g=1==f[15]?"借入":"借出",$("#finaPanelDown2 .dai").html($("#finaPanelDown2 .dai").html().replaceAll("[XX]",g)),$(".etinfo .dai li").css("background","none"))}),vip.user.cookieInit||vip.user.zcticker()}function timeChatBaddy(){longTime=window.setInterval(function(){return isRunning||(loadTimes++,loadTimes%loadSpace>0&&0==reloadDue)?void 0:(reloadDue>0&&reloadDue++,reloadDue>50?(reloadDue=0,void 0):(reloadDue%5>0||getCurr(),void 0))},100),vip.user.uticker(),setInterval(function(){vip.user.uticker()},3e4)}function controlTrade(){"关闭↑"==$("#closeTrade").text()?($("#closeTrade").text("显示↓"),$("#mk_table_con").hide()):($("#closeTrade").text("关闭↑"),$("#mk_table_con").show())}function getRecordFirst(){inAjaxing||(getMoney(),inAjaxing=!0,$.getJSON(entrustUrlBase+"Record/Get-"+market+"?status=3&lastTime="+lastTimeRecord+"&jsoncallback=?",function(a){return inAjaxing=!1,-1==a[0].lastTime?(isShowLogin||(isShowLogin=!0,vip.user.login()),void 0):(isShowLogin=!1,0==a[0].lastTime?(Wrong("系统忙碌，请稍候！"),void 0):(lastTimeRecord!=a[0].lastTime&&(lastTimeRecord=a[0].lastTime,showRecord(a,0)),void 0))}))}function getRecord(a,b,c,d,e,f,g,h){inAjaxing||(inAjaxing=!0,$.getJSON(entrustUrlBase+"Record/Get-"+market+"?lastTime="+lastTimeRecord+"&type="+b+"&pageIndex="+a+"&pageSize=10&timeFrom="+c+"&timeTo="+d+"&numberFrom="+e+"&numberTo="+f+"&priceFrom="+g+"&priceTo="+h+"&jsoncallback=?",function(b){return inAjaxing=!1,0==b[0].lastTime?(Wrong("系统忙碌，请稍候！"),void 0):(showRecord(b,a),void 0)}))}function showRecord(a,b){var f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,c=a[0].record,d=[],e=c.length;for(0==b&&e>8&&!sellDao&&(e=8),f=0;e>f;f++)g=c[f][0],h=c[f][1],i=c[f][2],j=c[f][3],k=c[f][4],l=c[f][5],m=c[f][6],n=c[f][7],o="pi",p="买入",-1==n&&(p="计划买入"),0==l&&(p="卖出",-1==n&&(p="计划卖出"),o="po"),q=(100*(j/i)).toFixed(0),1==n&&(q=0),r="",(0==n||3==n||-1==n)&&(r+='<a id="cancel1_'+g+'" href="javascript:cancle('+g+" , "+l+',1);">取消</a>'),j>0&&(r+=' <a href="javascript:details('+g+",'"+o+"');\">查看</a>"),""==r&&(r="—"),0==n?n="-":3==n?n="<i style='color:red;'>待成交</i>":2==n?n="<i style='color:green;'>已成交</i>":1==n?n="<i style='color:gray;'>已取消</i>":-1==n&&(n="<i style='color:orange;'>计划中</i>"),s=new Date(m),t=s.format("MM-dd hh:mm:ss"),u="-",j>0&&(u=formatMoney(k*exchangeBixNormal/j)),v="-",k>0&&(v=k),w="",0!=f%2&&(w=' class="double" '),x="<dd "+w+">"+'<span class="t1">'+t+"</span>"+'<span class="t2 '+o+'">'+p+"</span>"+'<span class="t3">'+exchangeBiNote+h+"</span>"+'<span class="t4">'+numberBiNote+" "+i+"</span>"+'<span class="t5 '+o+'">'+exchangeBiNote+u+"</span>"+'<span class="t6 '+o+'"><div class="schedule  '+o+'"><i><u style="width:'+q+'%;"></u></i></div>'+numberBiNote+" "+j+"</span>"+'<span class="t7 '+o+'">'+exchangeBiNote+k+"</span>"+'<span class="t8">'+n+"</span>"+'<span class="t9">'+r+"</span>"+"</dd>",d.push(x);b>0?c.length<1?($("#listNormal").html('<div class="air-tips"><p>暂时没有符合要求的记录</p></div>'),$("#page_navA").html("")):($("#listNormal").html(d.join("")),y=GetPaper(b,a[0].count),$("#page_navA").html('<div class="con">'+y+"</div>")):c.length<1?$("#listFirest").html('<div class="air-tips"><p>暂时没有符合要求的记录</p></div>'):$("#listFirest").html(d.join(""))}function details(a,b){var c="pi"==b?"买入":"卖出";vip.list.funcName="<b>"+numberBi+"委托"+c+"</b>成交记录详情",vip.list.look({url:entrustUrlBase+"Record/"+market+"-"+a,height:460,width:680,title:""})}function init(){alert(0),getRecordFirst(),getCurr()}function cancle(a,b,c){vip.tips["取消"]="否",Ask({Msg:"确定取消当前委托吗？",callback:"cle("+a+" , "+b+", "+c+")"})}function cle(a){var d=entrustUrlBase+"Entrust/cancle-"+market+"-"+a;vip.ajax({url:d,div:"listFirestDiv",suc:function(b){var c=$(b).find("MainData").text(),d=$(b).find("Des").text();200==c?($("#cancel1_"+a).attr("href","javascript:void()").text("取消中"),reload=1,Close(),$.jGrowl(d,{life:8e3,position:"bottom-right"}),getRecordFirst()):(Close(),$.jGrowl(d,{life:1e4,position:"bottom-right"}))},err:function(a){Wrong($(a).find("Des").text())}})}function show(a,b){var c,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,A,B,D,E,F,G,H,I,J,K,L,M;if(isRunning=!1,c=a[0].lastTime,c!=lastTime){for(getRecordFirst(),lastTime=c,a[0].currentPrice,a[0].high,a[0].low,a[0].totalBtc?a[0].totalBtc:0,h=a[0].listUp,i=[],j=0,h.length>b&&(h=h.slice(h.length-b,h.length)),k=0;k<h.length;k++)l=h[k][1],l>j&&(j=l);for(m=1,sellDao&&(m=h.length),n=0,k=0;k<h.length;k++){if(o=k,sellDao&&(o=h.length-k-1),p=h[o][0],l=h[o][1],0!=k||sellDao||(sellOne=p),k==h.length-1&&sellDao&&(sellOne=p),q=100*l/j,1>q&&(q=1),n=accAdd(n,l),r="",null!=entrust)for(s=0;s<entrust.sell.length;s++)entrust.sell[s].price==p&&(r=entrust.sell[s].btcs);t="",0==m%2&&(t=' class="double" '),u="<dd"+t+'><span class="t1">卖'+m+'</span><span class="t2">'+p+'</span><span class="t3">'+l+'</span><span class="t4"><div style="width:'+q+'%;">'+r+"</div></span>",sellDao||(u+='<span class="t5">'+n+"</span></dd>"),i.push(u),sellDao?m--:m++}if(v=b-h.length,v>0&&sellDao)for(w=0;v>w;w++)i.push('<dd><span class="t1">卖'+(h.length+w+1)+'</span><span class="t2">-</span><span class="t3">-</span><span class="t4"><div style="width:0%;">-</div></span></dd>');else for(w=0;v>w;w++)i.push('<dd><span class="t1">卖'+(h.length+w+1)+'</span><span class="t2">-</span><span class="t3">-</span><span class="t4"><div style="width:0%;">-</div></span><span class="t5">-</span></dd>');for($("#sellListIn").html(i.join("")),x=a[0].listDown,y=[],A=0,x.length>b&&(x=x.slice(0,b)),k=0;k<x.length;k++)l=x[k][1],l>A&&(A=l);for(m=1,n=0,k=0;k<x.length;k++){if(p=x[k][0],0==k&&(buyOne=p),l=x[k][1],q=100*l/A,1>q&&(q=1),n=accAdd(n,l),r="",null!=entrust)for(s=0;s<entrust.buy.length;s++)entrust.buy[s].price==p&&(r=entrust.buy[s].btcs);t="",0==m%2&&(t=' class="double" '),u="<dd"+t+'><span class="t1">买'+m+'</span><span class="t2">'+p+'</span><span class="t3">'+l+'</span><span class="t4"><div style="width:'+q+'%;">'+r+"</div></span>",sellDao||(u+='<span class="t5">'+n+"</span></dd>"),y.push(u),m++}if(v=b-x.length,v>0&&sellDao)for(w=0;v>w;w++)y.push('<dd><span class="t1">买'+(h.length+w+1)+'</span><span class="t2">-</span><span class="t3">-</span><span class="t4"><div style="width:0%;">-</div></span></dd>');else for(w=0;v>w;w++)y.push('<dd><span class="t1">买'+(h.length+w+1)+'</span><span class="t2">-</span><span class="t3">-</span><span class="t4"><div style="width:0%;">-</div></span><span class="t5">-</span></dd>');for($("#buyListIn").html(y.join("")),B=a[0].transction,D=[],E=0,B.length>b&&(B=B.slice(0,b)),k=0;k<B.length;k++)l=B[k][1],l>E&&(E=l);for(k=0;k<B.length;k++)p=B[k][0],F=B[k][1],G=B[k][2],H=B[k][3],q=100*F/E,1>q&&(q=1),I="outer",1==G&&(I="in"),J="",0==k%2&&(J=' class="double" '),t="",0==G&&(t=' class="down"'),K=new Date(H),L=K.format("hh:mm:ss"),M="<dd"+J+'><span class="t1">'+L+'</span><span class="t2">'+p+'</span><span class="t3">'+F+'</span><span class="t4"><div '+t+' style="width:'+q+'%;"></div></span></dd>',D.push(M);$("#transList").html(D.join("")),$("#suggestPrice").text(sellOne),$("#suggestBuyPrice").text(buyOne),isRunning=!1}}function accountInfoShow(){}function changeSell(){var b,c,a=document.getElementById("buy");a.style.display="none",b=document.getElementById("sell"),b.style.display="block",c=document.getElementById("manage"),c.style.display="none",$("#showList").show(),$("#mylist").hide(),getRecordFirst()}function changeBuy(){var b,c,a=document.getElementById("buy");a.style.display="block",b=document.getElementById("sell"),b.style.display="none",c=document.getElementById("manage"),c.style.display="none",$("#showList").show(),$("#mylist").hide(),getRecordFirst()}function changeMan(){var a,b,c;getRecord(1,-1,0,0,0,0,0,0),a=document.getElementById("buy"),a.style.display="none",b=document.getElementById("sell"),b.style.display="none",c=document.getElementById("manage"),c.style.display="block",$("#showList").hide(),$("#mylist").show()}function accSub(a,b){var c,d,e,f;try{c=a.toString().split(".")[1].length}catch(g){c=0}try{d=b.toString().split(".")[1].length}catch(g){d=0}return e=Math.pow(10,Math.max(c,d)),f=c>=d?c:d,((a*e-b*e)/e).toFixed(f)}function accAdd(a,b){var c,d,e;try{c=a.toString().split(".")[1].length}catch(f){c=0}try{d=b.toString().split(".")[1].length}catch(f){d=0}return e=Math.pow(10,Math.max(c,d)),((a*e+b*e)/e).toFixed(3)}function xswCheck(a,b,c){var f,g,h,i,d=b+1,e=$(a).val();e.indexOf(".")>0&&e.substring(e.indexOf(".")).length>d&&$(a).val(e.substring(0,e.indexOf(".")+d)),"realAccount"!=$(a).attr("name")?initTotal(c):(f=getFormIdByType(c),g=$("#"+f+" .btcNumInput"),h=$(a).val(),i=$("#"+f+" .unitPrice").val(),vip.tool.isFloat(h)&&vip.tool.isFloat(i)&&g.val(formatNumber(formatNumberUse(accDiv(h,i)))))}function numCheck(a){var b=getFormIdByType(a),d=$("#"+b+" .btcNumInput").val();vip.tool.isFloat(d)&&d>0&&(parseFloat(d)<.001&&$("#"+b+" .btcNumInput").val(.001),parseFloat(d)>1e3&&$("#"+b+" .btcNumInput").val(1e3),d.indexOf(".")>0&&d.substring(d.indexOf(".")).length>numberBixDian+1&&$("#"+b+" .btcNumInput").val(d.substring(0,d.indexOf(".")+(numberBixDian+1))),initTotal(a))}function initVal(a){var b=getFormIdByType(a),c=$("#"+b+" .unitPrice");""==c.val()&&c.val($("#"+b+" .suggest").text())}function getFormIdByType(a){var b="buy";return 1!=a&&(b="sell"),b}function initTotal(a){var e,b=getFormIdByType(a),c=$("#"+b+" .btcNumInput").val(),d=$("#"+b+" .unitPrice").val();null!=d&&vip.tool.isFloat(d)&&1==a&&(e=$("#buy .useRmb").text().replace(exchangeBiNote,""),$("#"+b+" .bigNum").text(formatNumber(formatNumberUse(accDiv(e,d))))),null!=d&&vip.tool.isFloat(d)&&vip.tool.isFloat(c)&&$("#"+b+" .realAccount").val(accMul(d,c))}function accMul(a,b){var g,c=0,d=a.toString(),e=b.toString();try{c+=d.split(".")[1].length}catch(f){}try{c+=e.split(".")[1].length}catch(f){}return g=Number(d.replace(".",""))*Number(e.replace(".",""))/Math.pow(10,c),null!=g&&null!=g.toString().split(".")&&null!=g.toString().split(".")[1]&&g.toString().split(".")[1].length>4&&(g=g.toFixed(4)),g}function sqxsw(a,b){var d,e,c=a.toString();return vip.tool.isFloat(a)&&(d=c.split("."),2==d.length&&d[1].length>b&&(e=c.indexOf(".")+b+1,c=c.substring(0,e))),c}function accDiv(arg1,arg2){var r1,r2,tradeAmount,t1=0,t2=0;try{t1=arg1.toString().split(".")[1].length}catch(e){}try{t2=arg2.toString().split(".")[1].length}catch(e){}with(Math)return r1=Number(arg1.toString().replace(".","")),r2=Number(arg2.toString().replace(".","")),tradeAmount=r1/r2*pow(10,t2-t1),null!=tradeAmount&&null!=tradeAmount.toString().split(".")&&null!=tradeAmount.toString().split(".")[1]&&tradeAmount.toString().split(".")[1].length>3&&(tradeAmount=sqxsw(tradeAmount,3)),tradeAmount}function numBlur(a){numCheck(a)}function priceBlur(a){moneyCheck(a),initTotal(a)}function moneyCheck(a){var b=getFormIdByType(a),c=$("#"+b+" .unitPrice");checkPrice(c,$("#"+b+" .suggest").text())}function checkPrice(a,b){var c=a.val();vip.tool.isFloat(c)&&c>0&&(parseFloat(c)<0&&a.val(b),parseFloat(c)>1e5&&a.val(b)),c.indexOf(".")>0&&c.substring(c.indexOf(".")).length>exchangeBixDian+1&&a.val(c.substring(0,c.indexOf(".")+exchangeBixDian+1))}function trans(a,b){var c,d,e,f,g,j,k,l,m,n;if(vip.user.checkLogin()){if(c=getFormIdByType(a),d=$("#"+c+" .btcNumInput").val(),e=$("#"+c+" .unitPrice").val(),f=$("#"+c+" .safePassword").val(),g=1==a?"购买":"出售",0==e.length||!vip.tool.isFloat(e)||parseFloat(e)<=0)return Wrong("请输入"+g+numberBi+"的单价!",{CloseTime:1}),void 0;if(0==d.length||!vip.tool.isFloat(d)||1e-4>d||d>1e3)return Wrong("请输入"+g+numberBi+"的数量!",{CloseTime:1}),void 0;if(j=$("#useSafe").val(),1==j&&(f.length<6||f.length>16))return Wrong("请正确输入您的安全密码,6-16位。",{CloseTime:1}),void 0;if(k="",b||(k="计划"),0==a){if(l=parseFloat($("#sellUnitPrice").val()),b){if(buyOne>0&&.6>l/buyOne)return Wrong("您的"+k+"卖出单价过低！请检查是否错误输入"+k+"卖出单价！",{CloseTime:2}),void 0}else{if(buyOne>0&&.1>l/buyOne)return Wrong("您的"+k+"卖出单价过低！请检查是否错误输入"+k+"卖出单价！",{CloseTime:2}),void 0;if(l>=buyOne)return Wrong("止损计划卖出的单价应该低于买一价，否则请直接使用立即卖出！",{CloseTime:2}),void 0}if(d>currentUserNumber)return Wrong("您当前可出售的最多数量为:"+currentUserNumber+"个！",{CloseTime:2}),void 0}else if(1==a){if(m=parseFloat($("#suggestNumber").text()),n=parseFloat($("#buyNumber").val()),l=parseFloat($("#buyUnitPrice").val()),n>m)return Wrong("按照您的报价，您当前最多可买入的数量为:"+m+"个！",{CloseTime:2}),void 0;if(b){if(sellOne>0&&l/sellOne>1.4)return Wrong("购买单价过高，请检查是否正确输入了购买单价！",{CloseTime:2}),void 0}else{if(sellOne>0&&l/sellOne>8)return Wrong("购买单价过高，请检查是否正确输入了购买单价！",{CloseTime:2}),void 0;if(sellOne>=l)return Wrong("追高计划买入的单价应该高于卖一价，如果想以此价格委托请直接使用立即买入！",{CloseTime:2}),void 0}}$("#b_tradtab_pro").Loadding({IsShow:!0,OffsetYGIF:40,Str:"委托中"}),submitfirst=!0,$.post(entrustUrlBase+"entrust/doEntrust-"+market+"-"+b,{Action:"post",safePassword:f,unitPrice:e,number:d,isBuy:a},function(a){var d,e;$("#b_tradtab_pro").Loadding({IsShow:!1}),$("#"+c+" .safePassword").val(""),d=a.datas.code,e=a.des,100==d?(reload=1,reloadDue=1,$(".btcNumInput").val(""),$(".realAccount").val(""),$.jGrowl(e,{life:8e3,position:"bottom-right"}),getRecordFirst()):(Wrong(e,{CloseTime:1}),useSafePwd(),$.jGrowl(e,{life:8e3,position:"bottom-right"}))},"json")}}function copyVal(a,b){$(b).val($(a).text())}function useSafePwd(){var a=vip.transDomain+"/isSafe";vip.ajax({url:a,dataType:"json",suc:function(a){"false"==a.des?($(".safePassword").hide(),$("#closeSafe1").text("开启").attr("href","javascript:startSafePwd()"),$("#closeSafe2").text("开启").attr("href","javascript:startSafePwd()"),$("#useSafe").val(0)):($("#useSafe").val(1),$(".safePassword").show(),$("#closeSafe1").text("关闭").attr("href","javascript:closeSafePwd()"),$("#closeSafe2").text("关闭").attr("href","javascript:closeSafePwd()"))}})}function cancleMore(){Iframe({Url:vip.transDomain+"/cancelMore",zoomSpeedIn:200,zoomSpeedOut:200,Width:648,Height:370,scrolling:"no",isIframeAutoHeight:!1,Title:"批量取消"})}function entrustMore(a){Iframe({Url:vip.transDomain+"/entrustMore?isbuy="+a,zoomSpeedIn:200,zoomSpeedOut:200,Width:648,Height:460,scrolling:"no",isIframeAutoHeight:!1,Title:"批量委托"})}function closeSafePwd(){Iframe({Url:vip.vipDomain+"/u/safe/closeSafePwd",zoomSpeedIn:200,zoomSpeedOut:200,Width:500,Height:260,scrolling:"no",isIframeAutoHeight:!1,Title:"安全验证"})}function startSafePwd(){Ask2({Msg:"您确定要开启安全密码吗？",call:function(){$.getJSON(vip.vipDomain+"/u/safe/useOrCloseSafePwd?callback=?",function(){Close(),$(".safePassword").show(),$("#closeSafe1").text("关闭").attr("href","javascript:closeSafePwd()"),$("#closeSafe2").text("关闭").attr("href","javascript:closeSafePwd()"),$("#useSafe").val("0"),$.jGrowl("开启密码成功！",{life:8e3,position:"bottom-right"})})}})}function formatNumber(a){return a=parseFloat(a)/numberBixNormal,numberBixNormal!=numberBixShow?Math.floor(Math.pow(10,numberBixDian)*parseFloat(a)):parseFloat(a.toFixed(numberBixDian))}function formatNumberUse(a){return a=parseFloat(a),numberBixNormal!=numberBixShow?(a=Math.floor(a),Math.floor(a*Math.pow(10,numberBixDian))):Math.floor(accMul(a,numberBixNormal))}function accMul(a,b){var c=0,d=a.toString(),e=b.toString();try{c+=d.split(".")[1].length}catch(f){}try{c+=e.split(".")[1].length}catch(f){}return Number(d.replace(".",""))*Number(e.replace(".",""))/Math.pow(10,c)}function accDiv_old(arg1,arg2){var r1,r2,t1=0,t2=0;try{t1=arg1.toString().split(".")[1].length}catch(e){}try{t2=arg2.toString().split(".")[1].length}catch(e){}with(Math)return r1=Number(arg1.toString().replace(".","")),r2=Number(arg2.toString().replace(".","")),r1/r2*pow(10,t2-t1)}function formatMoney(a){return a=parseFloat(a)/exchangeBixNormal,exchangeBixNormal!=exchangeBixShow?Math.floor(Math.pow(10,exchangeBixDian)*parseFloat(a)):parseFloat(a.toFixed(exchangeBixDian))}function formatMoneyUse(a){return a=parseFloat(a),exchangeBixNormal!=exchangeBixShow?(a=Math.floor(a),Math.floor(a*Math.pow(10,exchangeBixDian))):Math.floor(accMul(a,exchangeBixNormal))}function ToPage(a){getRecord(a,-1,0,0,0,0,0,0)}function ToPage2(){var a=parseInt($("#PagerInput").val()),b=$("#PagerInput").attr("maxSize");parseInt(b)<a?(Alert("输入页码过大"),$("#PagerInput").val(num)):ToPage(a)}function GetPaper(a,b){var e,f,g,h,c=3,d=b/10;if(d=0==b%10?parseInt(b/10):parseInt(b/10)+1,e=[],1==a?e.push('<span class="Pbtn first">第一页</span><span class="Pbtn pre"><i>&lt;</i> 上一页</span>'):(e.push('<a class="Pbtn first"   href="javascript:ToPage(1)">第一页</a>'),e.push('<a class="Pbtn pre"  href="javascript:ToPage('+(a-1)+')"><i>&lt;</i> 上一页</a>')),a>c+1)for(f=a-c,a+c>d?(f=d-2*c,0>=f&&(f=1)):f=a-c,f>1&&e.push('<span class="ellipsis">...</span>'),g=f;a>g;g++)e.push('<a href="javascript:ToPage('+g+')"   class="num" >'+g+"</a>");else for(g=1;a>g;g++)e.push('<a href="javascript:ToPage('+g+')"   class="num"  >'+g+"</a>");if(e.push("<a class='num current'   >"+a+"</a>"),d>a+c){for(h=c,h=c>=a?d>2*c+1?2*c+2:d+1:a+c+1,g=a+1;h>g;g++)e.push('<a href="javascript:ToPage('+g+')"    class="num" >'+g+"</a>");d>2*c+1&&e.push('<span class="ellipsis">...</span>')}else for(g=a+1;d+1>g;g++)e.push('<a  href="javascript:ToPage('+g+')"    class="num" >'+g+"</a>");return a==d?e.push('<span class="Pbtn next">下一页<i>&gt;</i></span>'):e.push('<a class="Pbtn next"    href="javascript:ToPage('+(a+1)+')">下一页 <i>&gt;</i></a>'),e.push('<div class="go_page"><input type="text" position="s"　 id="PagerInput" size=2 maxSize="'+d+'" mytitle="最多'+d+'页" TitlePosition="Left"  pattern="num()" errmsg="最多'+d+'页"  value="'+a+'" /><a href="javascript:ToPage2()" id="JumpButton" class="Pbtn jump">跳转</a></div>'),e.join("")}var isShowLogin,lastTimeRecord,entrust,lastTime,buyOne,sellOne,twoTimeout,threeTimeout,startTimer,submitfirst,reloadDue=0,loadSpace=30,loadTimes=0,longTime=0,isRunning=!1,inAjaxing=!1,currentUserNumber=0,currentUserExchange=0,currentPrice=1e3;$(function(){jQuery(".b_tradtab_pro").slide({mainCell:".bd",titCell:".d_tradtab ul li",trigger:"click",endFun:function(){Set2DivHeight()}}),$("#dangwei").text("查看"+length+"档"),$("#gengxin").text(loadSpace/10+"秒更新"),$("#BuyTab h3").text("委托买入"+numberBiEn),$("#BuyTab").bind("click",function(){$("#listFirestDiv").show(),$("#d_marketCon").show(),$("#d_market").show(),$("#p2").hide(),$("#p1").fadeIn()}),$("#EntrustManageTab").bind("click",function(){$("#listFirestDiv").hide(),$("#p3").fadeIn(),$("#p2").hide(),$("#d_market").hide(),getRecord(1,-1,0,0,0,0,0,0)}),$("#KlineCharge").bind("click",function(){$("#klineFullScreen").attr("src",vip.transDomain+"/Line/"+numberBiEn),$("#d_market").hide(),$("#p4").fadeIn(),$("#p3").hide(),$("#listFirestDiv").hide()}),$("#SellTab h3").text("委托卖出"+numberBiEn),$("#SellTab").bind("click",function(){$("#listFirestDiv").show(),$("#d_marketCon").show(),$("#p1").hide(),$("#p2").fadeIn(),$("#d_market").show()}),$("#changeToPro").attr("href",vip.transDomain+"/"+numberBiEn+"Pro"),$("#changeSimple").attr("href",vip.transDomain+"/"+numberBiEn),"LTC"==numberBiEn&&($("#t1").removeClass("on"),$("#t2").addClass("on")),"BTQ"==numberBiEn&&($("#t1").removeClass("on"),$("#t3").addClass("on")),$("#listFirestDiv").show(),$("#d_marketCon").show(),useSafePwd(),getCurr(),timeChatBaddy()}),isShowLogin=!1,lastTimeRecord=0,entrust=null,lastTime=0,buyOne=0,sellOne=0,startTimer=!1,submitfirst=!1,Date.prototype.format=function(a){var c,b={"M+":this.getMonth()+1,"d+":this.getDate(),"h+":this.getHours(),"m+":this.getMinutes(),"s+":this.getSeconds(),"q+":Math.floor((this.getMonth()+3)/3),S:this.getMilliseconds()};/(y+)/.test(a)&&(a=a.replace(RegExp.$1,(this.getFullYear()+"").substr(4-RegExp.$1.length)));for(c in b)new RegExp("("+c+")").test(a)&&(a=a.replace(RegExp.$1,1==RegExp.$1.length?b[c]:("00"+b[c]).substr((""+b[c]).length)));return a};