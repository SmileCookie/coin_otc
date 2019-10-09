
<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ page session="false"%>
<!doctype html>
<html>

<head>
	<jsp:include page="/common/head.jsp" />
	<title>${WEB_NAME}-${WEB_TITLE }</title>
	<meta name="viewport" content="width=device-width,height=devi`ce-height,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
	<meta name="baidu-site-verification" content="2G9wYgRLgY" />
	<meta name="keywords" content="${WEB_KEYWORD }" />
	<meta name="description" content="${WEB_DESC }" />
	<script type="text/javascript" src="${static_domain }/statics/js/jquery.flot-min.js"></script>
	<link rel="stylesheet" href="${static_domain }/statics/css/web.index.css">
	<link rel="stylesheet" href="${static_domain }/statics/css/swiper-4.3.3.min.css">
	<link rel="stylesheet" type="text/css" href="${static_domain}/statics/css/iconfont.css">
	<link rel="stylesheet" type="text/css" href="${static_domain}/statics/css/jquery.mCustomScrollbar.css">
	<script>
		// var _date = new Date().getTime();
		// console.log(_date)
        var _hmt = _hmt || [];
        (function() {
          var hm = document.createElement("script");
          hm.src = "https://hm.baidu.com/hm.js?85c8ebdf8519b81a4f922e0bdbc5f5d2";
          var s = document.getElementsByTagName("script")[0]; 
          s.parentNode.insertBefore(hm, s);
        })();
    </script> 
	<style>
		.content_box .clearFloat::after{
			content: ".";
			clear: both;
			display: block;
			overflow: hidden;
			font-size: 0;
			height: 0;
		}
		html{
			background-color: #181b2a;
		}
		.market_list_frist{
			position: relative;
		}
		.market_list_frist .favoritBtn:hover span{
			/*display: inline-block;*/
		}
		.market_list_frist .favoritBtn span{
			position: absolute;
			top: -26px;
			left: 17px;
			font-size: 12px;
			display: none;
			background: #4A4F5B;
			padding: 0 12px;
			line-height: 30px;
			border-radius: 2px;
			box-shadow: 0 2px 6px 0 rgba(0,0,0,0.15);
			color: #B6C1DA;
		}
		.friend_box a{
			display: inline-block!important;
		}
		.market_list_frist .favoritBtn span:after{
			content: '';
			display: transparent;
			border: 6px solid #4A4F5B;
			border-color: #4A4F5B transparent transparent transparent;
		/* margin-top: 20px; */
			position: absolute;
			top: 100%;
			left: 5px;
		}

	</style>
</head>

<body class="room">
	<div class="bk-body">
		<jsp:include page="/common/top.jsp"/>
		<!-- 轮播背景 -->
		<div class="top_bg">
			<div class="top_banner">
				<div class="banner_bg">
					<p class="banner_title">${L:l(lan,'迪拜数字资产交易中心')}</p>
					<div class="swiper-container swiper-container-pc">
						<div class="swiper-wrapper">
						</div>
						<!-- 如果需要分页器 -->
						<div class="swiper-pagination"></div>
					</div>
				</div>
			</div>
			<div class="bbyh-sys_notice sys_notice">
				<div class="sys_nobox hot_box"></div>
			</div>
			<div class="content_box">
				<div class="main_box">
					<div class="content_newsmark clearfix">
						<div class="market_show" style="background-color:#22262E;">
							<ul class="markt_top">
								<li class="favorit_bth">
									<i class="iconfont icon-shoucang-da-xuanzhong"></i>
									<span>${L:l(lan,'自选区')}</span>
								</li>
								<li class="markttop_on">USDT</li>
								<li>BTC</li>

								<%--<div class="market_top_border"></div>--%>
								<%--<div class="market_top_border"></div>--%>
								<div class="slider"></div>
							</ul>
							<span class="market_num">${L:l(lan,'目前上线币种数量')}：<b id="coin_num"></b></span>
							<div class="inp_search">
								<label class="searchicon iconfont icon-search-bizhong"></label><label class="searchicon iconfont icon-shanchu-yiru"></label><input type="text" class="inp_consearch pal5" placeholder=${L:l(lan,'币种搜索')} id="key_word">
							</div>
							<div class="market_box" style="background-color: #22262E;border: none;">
								<ul class="market_title" style="background-color: #2C313C">
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
									<li class="sort_btn" data-sortName="rangeOf24h"><span>${L:l(lan,'涨跌幅')}
												<i>
													<svg class="icon show_on" aria-hidden="true">
														<use xlink:href="#icon-paixujiantou-moren"></use>
													</svg>
												</i>
											</span>
									</li>
									<li class="sort_btn" data-sortName="lastminPrice"><span>${L:l(lan,'24小时最高价')}
												<i>
													<svg class="icon show_on" aria-hidden="true">
														<use xlink:href="#icon-paixujiantou-moren"></use>
													</svg>
												</i>
											</span>
										<!-- 已将最高价与最低价文字互换 -->

									</li>
									<li class="sort_btn" data-sortName="lastmorePrice"><span>${L:l(lan,'24小时最低价')}
												<i>
													<svg class="icon show_on" aria-hidden="true">
														<use xlink:href="#icon-paixujiantou-moren"></use>
													</svg>
												</i>
											</span>

									</li>
									<li class="sort_btn" data-sortName="priceTotal2">
												<span>${L:l(lan,'24小时成交量')}
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
					<div class="bbyh-adwp">
						<h4 class="news_title"><a href="/bw/news" class="bbqh-more">${L:l(lan,'查看更多')} ></a>${L:l(lan,'区块链新闻')} </h4>
						<div class="news_list mCustomScrollbar" style="overflow: hidden;background-color:#22262E;">
							<ul id="timeline" style="padding-left: 17px;padding-right: 10px;padding-top: 16px;">
							</ul>
						</div>
					</div>
					<div class="chart_box" style="width: 100%">
						<div class="img_section">
							<div class="img_div img_l">
								<div class="imgs">
									<img src="${static_domain}/statics/images/img_pic_1.png" alt="">
								</div>
								<div class="img_text">
									<h4>${L:l(lan,'实时用户分布数据展示')}</h4>
									<p class="">${L:l(lan,'采用大数据技术，智能实时采集全球ToKen交易者分布。')}</p>
								</div>
							</div>
							<div class="img_div img_r">
								<div class="imgs">
									<img src="${static_domain}/statics/images/img_pic_2.png" alt="">
								</div>
								<div class="img_text">
									<h4>${L:l(lan,'轻松查看当前交易量区间')}</h4>
									<p class="">${L:l(lan,'深度筛选全球最权威20名交易所，实时反映成交分布。')}</p>
								</div>
							</div>
						</div>
						<div class="img_section">
							<div class="img_div img_l">
								<div class="imgs">
									<img style="width: 71%" src="${static_domain}/statics/images/img_pic_3.png" alt="">
								</div>
								<div class="img_text">
									<h4>${L:l(lan,'实时平台资金状态')}</h4>
									<p class="">${L:l(lan,'构建实时透明的资产与交易数据状态，每一笔投资安全有效。')}</p>
								</div>
							</div>
							<div class="img_div img_r">
								<div class="imgs">
									<img src="${static_domain}/statics/images/img_pic_4.png" alt="">
								</div>
								<div class="img_text">
									<h4>${L:l(lan,'管理委托数据')}</h4>
									<p class="">${L:l(lan,'纳斯达克风险监控系统，规避程序化，频繁申报风险。')}</p>
								</div>
							</div>
						</div>
						<%--<div id="main" style="width:500px;height:300px;position: relative;">--%>
						<%--<img style="width: 65%;position: absolute;left: 29%;top: -5%;" src="${static_domain}/statics/img/common/img_e1.png" alt="">--%>
						<%--</div>--%>
						<%--<div class="main_left">--%>
						<%--<h4 class="main_title">${L:l(lan,'实时用户分布数据展示')}</h4>--%>
						<%--<p class="main_dic">${L:l(lan,'采用大数据技术，智能实时采集全球ToKen交易者分布。')}</p>--%>
						<%--</div>--%>
						<%--<div class="axisChart_right">--%>
						<%--<h4 class="main_title">${L:l(lan,'轻松查看当前交易量区间')}</h4>--%>
						<%--<p  class="main_dic">${L:l(lan,'深度筛选全球最权威20名交易所，实时反映成交分布。')}</p>--%>
						<%--</div>--%>
						<%--<div id="axisChart" style="width: 500px;height:300px;position: relative">--%>
						<%--<img style="width: 72%;position: absolute;right: 28%;" src="${static_domain}/statics/img/common/img_e2.png" alt="">--%>
						<%--</div>--%>
						<%--<div id="catChart" style="width: 500px;height:300px;position: relative">--%>
						<%--<img style="width: 87%;position: absolute;left: 25%;" src="${static_domain}/statics/img/common/img_e3.png" alt="">--%>
						<%--</div>--%>
						<%--<div class="catChart_left">--%>
						<%--<h4 class="main_title">${L:l(lan,'实时平台资金状态')}</h4>--%>
						<%--<p class="main_dic">${L:l(lan,'构建实时透明的资产与交易数据状态，每一笔投资安全有效。')}</p>--%>
						<%--</div>--%>
						<%--<div class="hereyChart_right">--%>
						<%--<h4 class="main_title">${L:l(lan,'管理委托数据')}</h4>--%>
						<%--<p class="main_dic">${L:l(lan,'纳斯达克风险监控系统，规避程序化，频繁申报风险。')}</p>--%>
						<%--</div>--%>
						<%--<div id="hereyChart" style="width: 500px;height:300px;position: relative">--%>
						<%--<img style="width: 120%;position: absolute;right: 22%;" src="${static_domain}/statics/img/common/img_e4.png" alt="">--%>
						<%--</div>--%>
					</div>
					<div class="mobile_down">
						<div class="content">
							<div class="mobile_text">
								<p class="p_title">${L:l(lan,'多平台终端接入')}</p>
								<p class="p_text">
									${L:l(lan,'覆盖IOS、Android多个平台，支持全业务功能')}
								</p>
							</div>
							<div class="mobile_down_icon" id="downApp">
									<div class="btn_down" style="margin-bottom: 20px">
										<div class="icons">
											<img src="${static_domain}/statics/images/ios_pic.png" alt="">
										</div>
										<div class="icons pic_hover">
											<img src="${static_domain}/statics/images/ios (2)@2x.png" alt="">
										</div>
										<div class="code_pic">
											<img src="${static_domain}/statics/images/iOS_code.png" alt="">
										</div>
										<span>${L:l(lan,'IOS版下载')}</span>
									</div>
									<div class="btn_down">
										<div class="icons">
											<img src="${static_domain}/statics/images/Android_pic.png" alt="">
										</div	>
										<div class="icons pic_hover">
											<img src="${static_domain}/statics/images/Android2x_w.png" alt="">
										</div>
										<div class="code_pic">
											<img src="${static_domain}/statics/images/and_code.png" alt="">
										</div>
										<span>${L:l(lan,'Android版下载')}</span>
									</div>
							</div>
						</div>
						<div class="phone_img">
							<img src="${static_domain}/statics/images/Phone-pic.png" alt="">
						</div>
					</div>
					<%--<div>--%>
					<%--<p style="text-align: center;font-size: 36px;color: #B6C1DA;">${L:l(lan,'多平台终端接入')}</p>--%>
					<%--<p style="font-size: 14px;color: #737A8D;text-align: center">${L:l(lan,'覆盖IOS、Android多个平台，支持全业务功能')}</p>--%>
					<%--<div style="width: 100%;padding-top: 84px;">--%>
					<%--<span style="display:inline-block;width: 49%;text-align: center;position: relative;border-right: 1px solid #5F6575;padding-left: 8%">--%>
					<%--<img style="height: 28px;display: inline-block;position: absolute;top: -2px;" src="${static_domain }/statics/images/ios2x_b.png" >--%>
					<%--<span id="iosTouch" style="display: inline-block;line-height: 28px;font-size: 15px;color: #737A8D;padding-left: 43px;cursor: pointer;">${L:l(lan,'IOS版下载')}</span>--%>
					<%--<img id="iosCodeImg" style="display:none;position: absolute;top: -196px;left:calc(58% - 80px);" src="${static_domain }/statics/images/donwImg.png" alt="">--%>
					<%--</span>--%>
					<%--<span style="display:inline-block;width: 50%;text-align: center;position: relative;padding-right: 8%">--%>
					<%--<img style="height: 28px;display: inline-block;position: absolute;top: -2px;" src="${static_domain }/statics/images/Android2x_b.png" >--%>
					<%--<span id="AndTouch" style="display: inline-block;line-height: 28px;font-size: 15px;color: #737A8D;padding-left: 43px;cursor: pointer;">${L:l(lan,'Android版下载')}</span>--%>
					<%--<img id="AndCodeImg" style="display:none;position: absolute;top: -196px;left:calc(42% - 80px);" src="${static_domain }/statics/images/donwImg.png" alt="">--%>

					<%--</span>--%>
					<%--</div>--%>
					<%--</div>--%>
					<div class="friend_link">
						<p class="friend_title">${L:l(lan,'友情链接')}</p>
						<div class="friend_box"></div>
					</div>
				</div>

			</div>
		</div>
		<jsp:include page="/common/foot.jsp" />
	</div>
	<script src="${static_domain}/statics/js/iconfont.js"></script>
	
	<script src="${static_domain}/statics/js/room.js"></script>
	
	<script type="text/x-tmpl" id="tmpl-homeMarket">
		{% for (var i = 0; i <= rs.length-1; i++) { %}
		<tr class="market_list" id= "{%=rs[i].market%}">
				<td class="market_list_frist">
						{% if( rs[i].keyColor == 1 ){ %}<i class="favoritBtn iconfont yellow icon-shoucang-da-xuanzhong" id="{%=rs[i].keyName%}"><span>${L:l(lan,'取消自选')}</span></i> {% }
						else { %}<i class="favoritBtn iconfont gray icon-shoucang-da-moren" id="{%=rs[i].keyName%}"><span>${L:l(lan,'加入自选')}</span></i> {% }
						%}
				{%=rs[i].symbol%}  
				</td>
				<td class="price_show"><span class="grey_box">{%=rs[i].price%}</span></td>
				{% if( rs[i].rangeOf24h>=0 ){ %} <td class="home_up">{%=rs[i].rangeOf24h%}%<i class="triangle_up"></i></td> {% } 
							else { %} <td class="home_down">{%=rs[i].rangeOf24h%}%<i class="triangle_down"></i></=td> {% }
							%}
				<td>{%=rs[i].lastminPrice%}</td>
				<td>{%=rs[i].lastmorePrice%}</td>
				<td>{%=rs[i].priceTotal%}
				</td>
			
			</tr>
		{% } %}
	</script>
	<script type="text/x-tmpl" id="tmpl-homeMarket_1">
		{% for (var i = 0; i <= rs.length-1; i++) { %}
		<tr class="market_list" id= "{%=rs[i].market%}">
				<td class="market_list_frist">
				{% if( rs[i].keyColor == 1 ){ %}<i class="favoritBtn iconfont yellow icon-shoucang-da-xuanzhong" id="{%=rs[i].keyName%}"><span>${L:l(lan,'取消自选')}</span></i> {% }
				else { %}<i class="favoritBtn iconfont gray icon-shoucang-da-moren" id="{%=rs[i].keyName%}"><span>${L:l(lan,'加入自选')}</span></i> {% }
				%}
			{%=rs[i].symbol%}  
				</td>
				<td class="price_show"><span class="grey_box">{%=rs[i].price%}</span></td>
				{% if( rs[i].rangeOf24h>=0 ){ %} <td class="home_up">{%=rs[i].rangeOf24h%}%<i class="triangle_up"></i></td> {% }
						else { %} <td class="home_down">{%=rs[i].rangeOf24h%}%<i class="triangle_down"></i></=td> {% }
						%}
				<td>{%=rs[i].lastminPrice%}</td>
				<td>{%=rs[i].lastmorePrice%}</td>
				<td>{%=rs[i].priceTotal%}
				</td>
			
			</tr>
		{% } %}
		</script>
		<script type="text/x-tmpl" id="tmpl-homeMarket_2">
			
			{% for (var i = 0; i <= rs.length-1; i++) { %}
			<tr class="market_list" id= "{%=rs[i].market%}">
					<td class="market_list_frist">
						{% if( rs[i].keyColor == 1 ){ %}<i class="favoritBtn iconfont yellow icon-shoucang-da-xuanzhong" id="{%=rs[i].keyName%}"><span>${L:l(lan,'取消自选')}</span></i> {% }
						else { %}<i class="favoritBtn iconfont gray icon-shoucang-da-moren" id="{%=rs[i].keyName%}"><span>${L:l(lan,'加入自选')}</span></i> {% }
						%}
					{%=rs[i].symbol%} 
					</td>
					<td class="price_show">
							<span class="grey_box">
							{%=rs[i].price%}</span>
							{% if( rs[i].symbol_btc == "BTC" ){ %}
							{% if ( false ) { %}
							<i class="cerrencyUnit"></i>
							{%=rs[i].exChangePriceBtc%}
							  {% }}
							else { if(false){ %}<i class="cerrencyUnit"></i>{%=rs[i].exchangePrice%} {% }}
						%}

					</td>
					{% if( rs[i].rangeOf24h>=0 ){ %} <td class="home_up">{%=rs[i].rangeOf24h%}%<i class="triangle_up"></i></td> {% } 
								else { %} <td class="home_down">{%=rs[i].rangeOf24h%}%<i class="triangle_down"></i></=td> {% }
								%}
					<td>{%=rs[i].lastminPrice%}</td>
					<td>{%=rs[i].lastmorePrice%}</td>
					<td>{%=rs[i].priceTotal%}
					</td>
				
				</tr>
				{% } %}
		</script>
</body>
<script>
	(function($){
		$(window).load(function(){
            // var _dates = new Date().getTime();
            // console.log( _dates - _date)
			$(".news_list").mCustomScrollbar({
				scrollButtons:{
					enable:true
				},
			})
			$(".market_box").mCustomScrollbar({
				scrollButtons:{
					enable:true
				},
			})

			$("#iosTouch").hover(function () {
				$(this).css("color","#B6C1DA")
				$('#iosCodeImg').css('display','block')
			},function(){
				$(this).css("color","#737A8D")
				$('#iosCodeImg').css('display','none')
			});

			$("#AndTouch").hover(function () {
				$(this).css("color","#B6C1DA")
				$('#AndCodeImg').css('display','block')
			},function(){
				$(this).css("color","#737A8D")
				$('#AndCodeImg').css('display','none')
			})

            var _hoverSwiper = $('.pc_hover');
            _hoverSwiper.each(function () {
                $(this).hover(function () {
                     $(this).css('border','2px solid #479EC2')
					//console.log($(this))
                },function(){
                    $(this).css('border','1px solid #3E6DA2')

                });

            })

			// console.log($(".notice_tit"))
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
;

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
