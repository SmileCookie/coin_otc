<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ page session="false"%>
<!doctype html>
<html>

<head>
    <jsp:include page="/common/headmb.jsp" />
    <title>${WEB_NAME}-${WEB_TITLE }</title>
    <meta name="viewport" content="width=device-width,height=device-height,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta name="baidu-site-verification" content="2G9wYgRLgY" />
    <meta name="keywords" content="${WEB_KEYWORD }" />
    <meta name="description" content="${WEB_DESC }" />
    <script type="text/javascript" src="${static_domain }/statics/js/jquery.flot-min.js"></script>
    <link rel="stylesheet" href="${static_domain }/statics/css/web.index.css">
    <link rel="stylesheet" href="${static_domain }/statics/css/swiper-4.3.3.min.css">
    <link rel="stylesheet" type="text/css" href="${static_domain}/statics/css/iconfont.css">
    <link rel="stylesheet" type="text/css" href="${static_domain}/statics/css/jquery.mCustomScrollbar.css">
    <script>
        var _hmt = _hmt || [];
        var browser = {
            versions:function() {
                var u = navigator.userAgent, app = navigator.appVersion;
                return {//移动终端浏览器版本信息
                    trident : u.indexOf('Trident') > -1, //IE内核
                    presto : u.indexOf('Presto') > -1, //opera内核
                    webKit : u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
                    gecko : u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1, //火狐内核
                    mobile : !! u.match(/AppleWebKit.*Mobile.*/) || !! u.match(/AppleWebKit/) && u.indexOf('QIHU') && u.indexOf('QIHU') > -1 && u.indexOf('Chrome') < 0, //是否为移动终端
                    ios : !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/),
                    android : u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或者uc浏览器
                    iPhone : u.indexOf('iPhone') > -1 || u.indexOf('Mac') > -1, //是否为iPhone或者QQHD浏览器
                    iPad: u.indexOf('iPad') > -1, //是否iPad
                    webApp : u.indexOf('Safari') == -1,//是否web应该程序，没有头部与底部
                    google:u.indexOf('Chrome')>-1
                };
            }(),
            language : (navigator.browserLanguage || navigator.language).toLowerCase()
        };
        (function() {
            var hm = document.createElement("script");
            hm.src = "https://hm.baidu.com/hm.js?85c8ebdf8519b81a4f922e0bdbc5f5d2";
            var s = document.getElementsByTagName("script")[0];
            s.parentNode.insertBefore(hm, s);
        })();
    </script>
</head>

<body class="room">
<div class="bk-body">
    <jsp:include page="/common/topmb.jsp"/>

    <% if(true){%>
    <!-- 轮播背景 -->
    <div class="top_bg">
        <div class="top_banner top_banner_mo">
            <p class="banner_title_mo">${L:l(lan,'迪拜数字资产交易中心')}</p>
            <%--style="margin-top: 63px;height: 4.333333rem;"--%>
            <div class="swiper-container swiper-container-mo" style="height: 5.033333rem;" >
                <div class="swiper-wrapper">
                </div>
                <!-- 如果需要分页器 -->
                <div class="swiper-pagination"></div>
            </div>
        </div>
        <div class="sys_notice" style="background-color:#1A1D24;border-bottom: 1px solid #22262E;">
            <div class="sys_nobox"></div>
        </div>
        <div class="content_box" style="background-color: #1A1D24;">
            <div class="content_newsmark" >
                <div class="market_show">
                    <ul class="markt_top" id="markt_toped">
                        <li class="favorit_bth">
                            <i class="iconfont icon-shoucang-da-xuanzhong"></i>
                            <span>${L:l(lan,'自选区')}</span>
                        </li>
                        <li class="markttop_on">USDT</li>
                        <li style="margin-right: -2px">BTC</li>

                        <%--<div class="market_top_border"></div>--%>
                        <%--<div class="market_top_border"></div>--%>
                        <div class="slider"></div>
                    </ul>
                    <%if(false){%>
                    <span class="market_num">${L:l(lan,'目前上线币种数量')}：<b id="coin_num"></b></span>
                    <%}%>
                    <div class="inp_search">
                        <em class="sh-wp" id="sh-wp">${L:l(lan,'搜索')}</em>
                        <div style="display: none;position: relative;width: 80%;text-align: right;color: #D4DCF0;font-size: .592593rem" id="sh-ct">
                            <span class="qx-cl" id="qx-cl">${L:l(lan,'取消')}</span>
                            <input type="text" class="inp_consearch pal5" placeholder=${L:l(lan,'币种搜索')} id="key_word" /><em class="qx-wp" id="qx-wp"></em>
                        </div>
                    </div>
                    <div class="market_box" style="height: auto;overflow: hidden;">
                        <ul class="market_title">
                            <li class="sort_btn" data-sortName="propTag"><span>${L:l(lan,'home_title')}
												<i>
													<svg class="icon show_on" aria-hidden="true">
														<use xlink:href="#icon-paixujiantou-moren"></use>
													</svg>

												</i>
											</span>

                            </li>
                            <li class="sort_btn" data-sortName="price"><span>${L:l(lan,'最新价')}
													<i>
														<svg class="icon show_on" aria-hidden="true">
															<use xlink:href="#icon-paixujiantou-moren"></use>
														</svg>
													</i>
											</span>

                            </li>
                            <li class="sort_btn" data-sortName="rangeOf24h"><span>${L:l(lan,'24小时涨跌幅')}
												<i>
													<svg class="icon show_on" aria-hidden="true">
														<use xlink:href="#icon-paixujiantou-moren"></use>
													</svg>
												</i>
											</span>
                            </li>




                        </ul>
                        <!-- <div class="market_line"></div> -->
                        <div class="markettable market_collect">
                            <table class="market_table " id="homeMarket_2">
                            </table>
                        </div>
                        <div class="markettable market_usdc">
                            <table class="market_table" id="homeMarket">
                            </table>
                        </div>
                        <div class="markettable market_btc">
                            <table class="market_table " id="homeMarket_1">
                            </table>
                        </div>

                    </div>

                </div>
                <%--<div class="n-wp bbyh-ns">--%>

                    <%--<h4 class="news_title"><a href="javascript:void(0)" class="bbqh-more downLinkPage">${L:l(lan,'查看更多')}</a>${L:l(lan,'区块链新闻')}</h4>--%>
                    <%--<div class="news_list  clearfix" style="padding-top: 8%;height: 13rem;overflow: hidden">--%>
                        <%--<div class="timescross"></div>--%>
                        <%--<ul id="timeline" style="border: none;padding-left:.92593rem;overflow: hidden;position: relative;">--%>
                        <%--</ul>--%>
                    <%--</div>--%>
                <%--</div>--%>
            </div>
            <div class="chart_box clearfix" style="padding-top:0.5rem;padding-bottom: 1.481481rem">
                <%--<div class="main_leftx">--%>
                    <%--<h4 class="main_title" style="font-size: .450778rem;padding: 0;">${L:l(lan,'实时用户分布数据展示')}</h4>--%>
                    <%--<p class="main_dic" style="padding: 0;font-size: .318519rem">${L:l(lan,'采用大数据技术，智能实时采集全球ToKen交易者分布。')}</p>--%>
                <%--</div>--%>
                <%--<div id="main" style="position:relative;width: 100%;height: 3.955556rem;margin-top: 1.111111rem">--%>
                    <%--<img style="width: 74%;position: absolute;left: 14%;top: 10%;" src="${static_domain}/statics/images/img_pic_1.png" alt="">--%>
                <%--</div>--%>

                <%--<div class="axisChart_right">--%>
                    <%--<h4 class="main_title" style="font-size: .450778rem;padding: 0;">${L:l(lan,'轻松查看当前交易量区间')}</h4>--%>
                    <%--<p  class="main_dic" style="padding: 0;font-size: .318519rem">${L:l(lan,'深度筛选全球最权威20名交易所，实时反映成交分布。')}</p>--%>
                <%--</div>--%>
                <%--<div id="axisChart" style="position:relative;width: 100%;height: 4.388889rem;margin-top: 1.111111rem;float: none;">--%>
                    <%--<img style="width: 72%;position: absolute;right: 16%;top:-13%;" src="${static_domain}/statics/images/img_pic_2.png" alt="">--%>
                <%--</div>--%>



                <%--<div class="catChart_left">--%>
                    <%--<h4 class="main_title" style="font-size: .450778rem;padding: 0;">${L:l(lan,'实时平台资金状态')}</h4>--%>
                    <%--<p class="main_dic" style="padding: 0;font-size: .318519rem">${L:l(lan,'构建实时透明的资产与交易数据状态，每一笔投资安全有效。')}</p>--%>
                <%--</div>--%>
                <%--<div id="catChart" style="position:relative;width: 100%;height: 4.188889rem;margin-top: 1.111111rem;float: none;">--%>
                    <%--<img style="width: 53%;position: absolute;left: 23%;top: -1%;" src="${static_domain}/statics/images/img_pic_3.png" alt="">--%>
                <%--</div>--%>


                <%--<div class="hereyChart_right">--%>
                    <%--<h4 class="main_title" style="font-size: .450778rem;padding: 0;">${L:l(lan,'管理委托数据')}</h4>--%>
                    <%--<p class="main_dic" style="padding: 0;font-size: .318519rem;padding: 0;">${L:l(lan,'纳斯达克风险监控系统，规避程序化，频繁申报风险。')}</p>--%>
                <%--</div>--%>
                <%--<div id="hereyChart" style="position:relative;width: 100%;height: 4.588889rem;margin-top: 1.111111rem;float: none;">--%>
                    <%--<img style="width: 74%;position: absolute;right: 8%;top: 0%;" src="${static_domain}/statics/images/img_pic_4.png" alt="">--%>
                <%--</div>--%>

                <%--<div style="clear: both;padding-bottom: 2.222222rem;"></div>--%>
                <%--<div class="hereyChart_right">--%>
                <%--<h4 class="main_title" style="padding: 0;">${L:l(lan,'多平台终端接入')}</h4>--%>
                <%--<p class="main_dic" style="padding: 0 0.3rem;">${L:l(lan,'覆盖IOS、Android多个平台，支持全业务功能')}</p>--%>
                <%--<div class="mb_btn">--%>
                <%--<a href="/downApp_ios"><div class="btn iphones"><img src="${static_domain }/statics/images/ios2x_w.png" ><span>${L:l(lan,'IOS版下载')}</span></div></a>--%>
                <%--<a href="/downApp_And"><div class="btn acroid"><img src="${static_domain }/statics/images/Android2x_w.png" ><span>${L:l(lan,'Android版下载')}</div></a>--%>
                <%--</div>--%>
                <%--</div>--%>
                <div class="app_down" style="background-size:100%;background-position:bottom;background-repeat:no-repeat;background-image: url(${static_domain}/statics/images/phone_bg_mo.png)">
                    <h4>${L:l(lan,'多平台终端接入')}</h4>
                    <p class="titles">${L:l(lan,'覆盖IOS、Android多个平台，支持全业务功能')}</p>
                    <div class="down_btn">
                        <a href="/downApp_ios">
                            <div class="btn_app">
                                <div class="icons">
                                    <img src="${static_domain}/statics/images/ios_pic.png" alt="">
                                </div	>
                                ${L:l(lan,'IOS版下载')}
                            </div>
                        </a>
                        <a href="/downApp_And">
                            <div class="btn_app">
                                <div class="icons">
                                    <img src="${static_domain}/statics/images/Android_pic.png" alt="">
                                </div	>
                                ${L:l(lan,'Android版下载')}
                            </div>
                        </a>
                    </div>
                    <div class="phone_img_mo">
                        <img src="${static_domain}/statics/images/Phone-pic.png" alt="">
                    </div>
                </div>
                <div class="friend_link" >
                    <p class="friend_title" style="font-size:.418519rem;">${L:l(lan,'友情链接')}</p>
                    <div class="friend_box" style="text-align: center"></div>
                </div>
                <div id="down_b">
                    <img  src="${static_domain}/statics/images/b_Icon.png" alt="">
                    <div class="texts">
                        <p>Btcwinex</p>
                        <p class="p2">${L:l(lan,'迪拜数字资产交易中心')}</p>
                    </div>
                    <%--<img class="closed" src="${static_domain}/statics/images/ad-close.png" alt="">--%>
                    <%--<span class="closed">X</span>--%>
                    <img  class="closed" src='data:img/jpg;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAYAAAByDd+UAAAAAXNSR0IArs4c6QAAAXVJREFUSA29
ljtOBDEMhu3QcQ04CJQ8jgEHQIsoEKJAVIgVEi0cg4USLsI16BiTP6usstk8bJghxc4qsf/PthLL
PLucPxLxATs+vb89+6AJ1vnVwx7J8CxCr46ZDolkl0QWF9fz/bF5QdNre9gOEnPE7oSZv0Rke/jm
lzGh0IImtMFAFR3K6LbkeGxoDgMDLI4lLBnc3cze47nl29JaASHYMtQCexprwL9CezDobwB/C9XA
qkArVAtrArVQC6wL7EGtMBWwBsV++qjxzjTPqHhpIJavPBucxw6ihcFHDYRxCg3Ovl1ZYPBx+PnP
pc4wzQ59F0FOVtIchjICOMmlKcHibWydIaDSapZUI6ixScFVoEXIYlsEWgRi9FqfDaDWMYLSr8Z3
DahxSAGl/z2NFbBnWBKv7bW0ArBlUBPt7dc0eTmkYm5cjnLW3tgC51BiPnIyyNMUMASCBoEE4giK
6ds3b3nz0/cn6LGDtKK2ngVNrx0YxIsfk0syFiPT0J4AAAAASUVORK5CYII='/>
                    <div class="downs">
                        <div id="down_now">
                            <img src="${static_domain}/statics/images/addown.png" alt="">
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>
    <jsp:include page="/common/footmb.jsp" />
</div>
<script src="${static_domain}/statics/js/iconfont.js"></script>

<script src="${static_domain}/statics/js/room.js"></script>

<script type="text/x-tmpl" id="tmpl-homeMarket">
		{% for (var i = 0; i <= rs.length-1; i++) { %}
		<tr class="market_list" id= "{%=rs[i].market%}">
				<td class="market_list_frist">
						{% if( rs[i].keyColor == 1 ){ %}<i class="favoritBtn iconfont yellow icon-shoucang-da-xuanzhong" id="{%=rs[i].keyName%}"></i> {% }
						else { %}<i class="favoritBtn iconfont gray icon-shoucang-da-moren" id="{%=rs[i].keyName%}"></i> {% }
						%}
				{%=rs[i].symbol%}
				</td>
				<td class="price_show"><span class="grey_box">{%=rs[i].price%}</span>{% if ( false ) { %}<i class="cerrencyUnit"></i>{%=rs[i].exchangePrice%}{% } %}</td>
				{% if( rs[i].rangeOf24h>=0 ){ %} <td class="home_up">{%=rs[i].rangeOf24h%}%<i class="triangle_up"></i></td> {% }
							else { %} <td class="home_down">{%=rs[i].rangeOf24h%}%<i class="triangle_down"></i></=td> {% }
							%}

			</tr>
		{% } %}
	</script>
<script type="text/x-tmpl" id="tmpl-homeMarket_1">
		{% for (var i = 0; i <= rs.length-1; i++) { %}
		<tr class="market_list" id= "{%=rs[i].market%}">
				<td class="market_list_frist">
				{% if( rs[i].keyColor == 1 ){ %}<i class="favoritBtn iconfont yellow icon-shoucang-da-xuanzhong" id="{%=rs[i].keyName%}"></i> {% }
				else { %}<i class="favoritBtn iconfont gray icon-shoucang-da-moren" id="{%=rs[i].keyName%}"></i> {% }
				%}
			{%=rs[i].symbol%}
				</td>
				<td class="price_show"><span class="grey_box">{%=rs[i].price%}</span>{% if ( false ) { %}<i class="cerrencyUnit"></i>{%=rs[i].exChangePriceBtc%}{% } %}</td>
				{% if( rs[i].rangeOf24h>=0 ){ %} <td class="home_up">{%=rs[i].rangeOf24h%}%<i class="triangle_up"></i></td> {% }
						else { %} <td class="home_down">{%=rs[i].rangeOf24h%}%<i class="triangle_down"></i></=td> {% }
						%}


			</tr>
		{% } %}
		</script>
<script type="text/x-tmpl" id="tmpl-homeMarket_2">

			{% for (var i = 0; i <= rs.length-1; i++) { %}
			<tr class="market_list" id= "{%=rs[i].market%}">
					<td class="market_list_frist">
						{% if( rs[i].keyColor == 1 ){ %}<i class="favoritBtn iconfont yellow icon-shoucang-da-xuanzhong" id="{%=rs[i].keyName%}"></i> {% }
						else { %}<i class="favoritBtn iconfont gray icon-shoucang-da-moren" id="{%=rs[i].keyName%}"></i> {% }
						%}
					{%=rs[i].symbol%}
					</td>
					<td class="price_show">
							<span class="grey_box">{%=rs[i].price%}</span>
					</td>
					{% if( rs[i].rangeOf24h>=0 ){ %} <td class="home_up">{%=rs[i].rangeOf24h%}%<i class="triangle_up"></i></td> {% }
								else { %} <td class="home_down">{%=rs[i].rangeOf24h%}%<i class="triangle_down"></i></=td> {% }
								%}


				</tr>
				{% } %}
		</script>
</body>
<script>

    (function($){
        $(window).load(function(){
            // $(".news_list").mCustomScrollbar({
            //     scrollButtons:{
            //         enable:true
            //     },
            // })
            var _lan = $.cookie('zlan');
            if(_lan == 'en'){
                $('.mb_btn').find('.btn span').css('fontSize','0.3rem');
            }
            var _close = $('.closed');
            var _down_b = $('#down_b');
            _close.click(function () {
                _down_b.animate({bottom:'-200px'},500)
            })
            var _linkDom = $('.downLinkPage , .wooke_tit , .content_a , #down_now');
            // var _linkCheckUrl = $('.downLinkPage')
            _linkDom.each(function (i) {
                $(this).click(function () {
                    if(browser.versions.android){
                        window.location.href = '/downApp_And'
                        // window.location.reload()
                    }
                    if(browser.versions.iPhone){
                        window.location.href = '/downApp_ios'
                        // window.location.reload()
                    }else{
                        return false
                    }

                })
            })
            // $(".market_box").mCustomScrollbar({
            //     scrollButtons:{
            //         enable:true
            //     },
            // })
            //根据设备跳转下载页

            //console.log(browser.versions.iPhone)


        
        })
    })(jQuery)
    // var text = document.getElementById("notice_tit");
    // var str = text.innerHTML;
    // window.onresize = function(){
    // 	overflowhidden("notice_tit",1,str);
    // }
    // 			 overflowhidden("notice_tit",1,str);
    // 			 var overflowhidden = function(id, rows, str){
    // 				var text = document.getElementById(id);
    // 				var style = getCSS(text);
    // 				var lineHeight = style["line-height"];
    // 				var at = rows*parseInt(lineHeight);
    // 				var tempstr = str;
    // 				text.innerHTML = tempstr;
    // 				var len = tempstr.length;
    // 				var i = 0;
    // 				if(text.offsetHeight <= at){                  //如果所有文本在写入html后文本没有溢出，那不需要做溢出处理
    // 					/*text.innerHTML = tempstr;*/
    // 				}
    // 				else {                                        //否则 二分处理需要截断的文本
    // 					var low = 0;
    // 					var high = len;
    // 					var middle;
    // 					while(text.offsetHeight > at){
    // 						middle = (low+high)/2;
    // 						text.innerHTML = tempstr.substring(0,middle);   //写入二分之一的文本内容看是否需要做溢出处理
    // 						if(text.offsetHeight < at){                         //不需要 则写入全部内容看是否需要 修改low的值
    // 							text.innerHTML = tempstr.substring(0,high);
    // 							low = middle;
    // 						}
    // 						else{                                          //写入二分之一的文本内容依旧需要做溢出处理 再对这二分之一的内容的二分之一的部分做判断
    // 							high = middle-1;                           //修改high值
    // 						}
    // 					}
    // 					tempstr = tempstr.substring(0, high)+'';
    // 					text.innerHTML = tempstr;
    // 					if(text.offsetHeight > at){
    // 						tempstr = tempstr.substring(0, high-3)+"";
    // 					}
    // 					text.innerHTML = tempstr;
    // 					text.height = at +"px";
    // 				}
    // 				}
    // 		})
    // })(jQuery);
</script>
<%}%>
<script>
    $(function(){
        $("#sh-wp").on("click", function(){
            $(this).hide();
            $("#markt_toped").addClass("hd");
            $("#sh-ct").show();
        })

        $("#qx-wp").on("click", function(){
            $("#key_word").val("");
            $("#key_word").triggerHandler("input");
        })

        $("#qx-cl").on("click", function() {
            $("#sh-ct").hide();
            $("#markt_toped").removeClass("hd");
            $("#sh-wp").show();
        })

    });
    (function(){

        function IsPC(){
            var userAgentInfo = navigator.userAgent;
            var Agents = new Array("Android", "iPhone", "SymbianOS", "Windows Phone", "iPod");
            var flag = true;
            for (var v = 0; v < Agents.length; v++) {
                if (userAgentInfo.indexOf(Agents[v]) > 0) { flag = false; break; }
            }
            return flag;
        }

        window.dps = $("html").css("fontSize").replace("px","") / 3;

        var url = '';
        var isJmp = true;
        // each other mobile or pc
        if(IsPC()){
            window.location.href.indexOf("web") > -1 && (isJmp = false)
            url = '/web/room';
        } else {
            window.location.href.indexOf("mob") > -1 && (isJmp = false)
            url = '/mob/room';
        }

        // isJmp && (window.location.href = url);

    })();
</script>
</html>