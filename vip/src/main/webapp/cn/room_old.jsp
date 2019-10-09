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
 <meta name="viewport" content="width=device-width, initial-scale=1.0 maximum-scale=1, user-scalable=no">
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<script type="text/javascript">
      (function (doc, win) {
            var docEl = doc.documentElement,
             resizeEvt = 'orientationchange' in window ? 'orientationchange' : 'resize',
               recalc = function () {
                  var clientWidth = docEl.clientWidth;
                  if (!clientWidth) return;
                    docEl.style.fontSize = 100 * (clientWidth / 750) + 'px';
                };
                // Abort if browser does not support addEventListener
              if (!doc.addEventListener) return;
              win.addEventListener(resizeEvt, recalc, false);
              doc.addEventListener('DOMContentLoaded', recalc, false);
    })(document, window);
    </script>
<link rel="stylesheet" href="${static_domain }/statics/css/web.index.css?V${CH_VERSON }">
<script type="text/javascript" src="${static_domain }/statics/js/jquery.flot-min.js"></script>
<style type="text/css">
/*.flot-base{width:105px!important;margin-left:0px;height:70px;}*/
.bk-menuBar{
	background-color:transparent;
	box-shadow:none;
}
</style>
</head>
<body class="room">
<div class="bk-body">
	<div class="bk-topbg">
		<video autoplay loop muted id="myVideo">
			<source src="${static_domain }/statics/img/common/index/index_video.mp4" type="video/mp4" />
		</video>
		<!-- Common TopMenu Begin -->
		<jsp:include page="/common/top.jsp" />
		<!-- Common TopMenu End -->
		<!--mobile Header Begin-->
		<header class="mobile_head">
			<svg class="icon" aria-hidden="true">
                <use xlink:href="#icon-bitequanqiuLOGO-text"></use>
            </svg>
			<i class="iconfont mobile-icon">&#xe60c;</i>
		</header>
		<!--mobile Header End-->
		<!-- Body From mainPage Begin -->
		<div class="bk-focus">
		<div class="container">
			<h1>
				 <span class="bkmain-logo">
				   <svg class="icon" aria-hidden="true">
				     <use xlink:href="#icon-bitequanqiuLogo"></use>
				   </svg>
      			 </span>
      			 ${L:l(lan,'首页-横幅-主标题-1')}
				 <b class="tit-line"></b>
			</h1>
        	    <p>${L:l(lan,'首页-横幅-副标题-1')}</p>
        	    <p class="login-btn mobile_upbtn">
        	    	<a class="nologin log-create mr5" href="${vip_domain}/register" target="_self">${L:l(lan,'首页-横幅-按钮-1')}</a>
        	    </p>
		</div>
	</div>
	</div>
	<div class="bk-currency">
	    <div class="container">
				 <ul id="homeMarket" class="homeMarket clearfix">
	        	</ul>
	    </div>
    </div>
    <div class="bk-detail">
    	    <div class="container">
                   <h3 class="bk-title">${L:l(lan,'首页-平台优势介绍-主标题-1')}<b class="bk-tline"></b></h3>					
    	    	   <div class="bk-detail-con clearfix">
    	    	     <div class="bk-detail-list">
    	    	     	<div class="col-m">
							<div class="bk-listcon">
    	    	     			<h4>${L:l(lan,'首页-平台优势介绍-左上标题-1')}<b></b></h4>
    	    	     			<p>${L:l(lan,'首页-平台优势介绍-左上内容-1')}</p>
							</div>
    	    	     	</div>
						<div class="col-s bk-bg1"></div>
    	    	     </div>
    	    	     <div class="bk-detail-list">
    	    	     	<div class="col-m">
							<div class="bk-listcon">
								<div class="bk-listcon">
									<h4>${L:l(lan,'首页-平台优势介绍-右上标题-1')}<b></b></h4>
    	    	     				<p>${L:l(lan,'首页-平台优势介绍-右上内容-1')}</p>
								</div>
							</div>
    	    	     	</div>
						<div class="col-s bk-bg2"></div>
    	    	     </div>
    	    	     <div class="bk-detail-list">
    	    	     	<div class="col-m">
							<div class="bk-listcon">
    	    	     			<h4>${L:l(lan,'首页-平台优势介绍-左下标题-1')}<b></b></h4>
    	    	     			<p>${L:l(lan,'首页-平台优势介绍-左下内容-1')}</p>
							</div>
    	    	     	</div>
						<div class="col-s bk-bg3"></div>
    	    	     </div>
    	    	     <div class="bk-detail-list">
    	    	     	<div class="col-m">
							<div class="bk-listcon">
    	    	     			<h4>${L:l(lan,'首页-平台优势介绍-右下标题-1')}<b></b></h4>
    	    	     			<p>${L:l(lan,'首页-平台优势介绍-右下内容-1')}</p>
							</div>
    	    	     	</div>
						<div class="col-s bk-bg4"></div>
    	    	     </div>
    	    </div>
    	  </div>
    </div>
    <div class="bk-mobile">
        <div class="container">
		     <div class="bk-mobile-img"></div>
			 <div class="bk-mobile-app">
                 <h4>${L:l(lan,'首页-移动端下载-主标题-1')}</h4>
				 <p>${L:l(lan,'首页-移动端下载-副标题-1')}</p>
				 <h5>${L:l(lan,'首页-移动端下载-提示-1')}</h5>
				 <div class="bk-mobile-download clearfix">
					 <a class="mr20" href="">
					 	<i class="mr5 iconfont">&#xe629;</i>
						<p>${L:l(lan,'首页-移动端下载-按钮-1')} <br/>
							<span>APP Store</span> 
						</p>
					 </a>
					 <a href="" class="mobile_right" >
					 	<i class="mr5 iconfont">&#xe604;</i>
						<p>${L:l(lan,'首页-移动端下载-按钮-2')}  <br/>
							<span>Local Download</span>
						</p>
					</a>
				 </div>
		     </div>
        </div>
    </div>
	<div class="bk-coins">
        <div class="container">
              <h3 class="bk-title">${L:l(lan,'首页-融资融币介绍-标题-1')}<b class="bk-tline"></b></h3>
			  <div class="bk-coinshr"></div>
			  <p>${L:l(lan,'首页-融资融币介绍-副标题-1')}</p>
			  <article></article>
        </div>
	</div>
	<div class="bk-introduce">
		<div class="container">
              	<h3 class="bk-title">${L:l(lan,'首页-换取比特币-标题-1')}<b class="bk-tline"></b></h3>
				<ul class="bitcoin-list clearfix">
					<li class="bitcoin"><a href="https://www.bitfinex.com" target="_blank"><img src="${static_domain }/statics/img/common/index/bitcoin3.png"/></a></li>
					<li class="bitcoin bitcoin_right"><a href="https://www.bitstamp.net" target="_blank"><img src="${static_domain }/statics/img/common/index/bitcoin4.png"/></a></li>					
					<li class="bitcoin"><a href="https://www.coinbase.com" target="_blank"><img src="${static_domain }/statics/img/common/index/bitcoin1.png"/></a></li>
					<li class="bitcoin bitcoin_right"><a href="https://gemini.com" target="_blank"><img src="${static_domain }/statics/img/common/index/bitcoin2.png"/></a></li>
				</ul>
		</div>
 </div>
 <!--<span class="{%=rs[i].propTag == 'DASH'?'dash':'right'%} " >{%=rs[i].propTag%}</span> -->
<script type="text/x-tmpl" id="tmpl-homeMarket">
{% for (var i = 0; i <= rs.length-1; i++) { %}
<li>
	<a href="/trade/{%=rs[i].market%}">
		<div class="homeicon-left clearfix">
			<p>
				<span class="name">{%=rs[i].symbol%}</span>
				<span class="undulate right"> 
					{%  if( rs[i].rangeOf24h>0 ){ %} <em class="up">+{%=rs[i].rangeOf24h%}%</em> {% } 
						else if( rs[i].rangeOf24h<0 ){ %} <em class="down">{%=rs[i].rangeOf24h%}%</em> {% } 
						else { %} <em> {%=rs[i].rangeOf24h%}%</em> {% }
					%}
				</span>
			</p>
			<p class="ft14">
				<span class="price">{%=rs[i].price%}</span>
				
			</p>
			
			<p class="foot_p">
				${L:l(lan,'首页-轮播行情-标签-1')} 
				<span class="">
					<span class="volume">{%=rs[i].priceBtc%}</span> 
					<span class=""> BTC</span>
				</span>
			</p>
			<p class="chart-plot" id="{%=rs[i].market%}_plot"></p>
		</div>
		
	</a>
</li>
{% } %}
</script>	
	<!-- Body From mainPage End -->
	<!-- Common FootMain Begin -->
	<jsp:include page="/common/foot.jsp" />
	<!-- Common FootMain End -->
</div>

</body>
</html>
