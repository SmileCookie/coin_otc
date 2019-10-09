<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<style type="text/css">
/*字体引入*/
@font-face {font-family: "iconfont";
  src: url('/common/fonts/iconfont.eot?v=${CH_VERSON }'); /* IE9*/
  src: url('/common/fonts/iconfont.eot?#iefix&v=${CH_VERSON }') format('embedded-opentype'), /* IE6-IE8 */
  url('/common/fonts/iconfont.woff?v=${CH_VERSON }') format('woff'), /* chrome, firefox */
  url('/common/fonts/iconfont.ttf?v=${CH_VERSON }') format('truetype'), /* chrome, firefox, opera, Safari, Android, iOS 4.2+*/
  url('/common/fonts/iconfont.svg?v=${CH_VERSON }#iconfont') format('svg'); /* iOS 4.1- */
}
@font-face{font-family:'FontAwesome';
	src:url('/common/fonts/fontawesome-webfont.eot?v=4.3.0');
	src:url('/common/fonts/fontawesome-webfont.eot?#iefix&v=4.3.0') format('embedded-opentype'),
	   url('/common/fonts/fontawesome-webfont.woff2?v=4.3.0') format('woff2'),
	   url('/common/fonts/fontawesome-webfont.woff?v=4.3.0') format('woff'),
	   url('/common/fonts/fontawesome-webfont.ttf?v=4.3.0') format('truetype'),
	   url('/common/fonts/fontawesome-webfont.svg?v=4.3.0#fontawesomeregular') format('svg');
	   font-weight:normal;font-style:normal;
}
@font-face{font-family:'Glyphicons Halflings';
    src:url('/common/fonts/glyphicons-halflings-regular.eot');
	src:url('/common/fonts/glyphicons-halflings-regular.eot?#iefix') format('embedded-opentype'),
	    url('/common/fonts/glyphicons-halflings-regular.woff2') format('woff2'),
		url('/common/fonts/glyphicons-halflings-regular.woff') format('woff'),
		url('/common/fonts/glyphicons-halflings-regular.ttf') format('truetype'),
		url('/common/fonts/glyphicons-halflings-regular.svg#glyphicons_halflingsregular') format('svg');
}
</style>

<!--[if lt IE 9]>
  <div class="alert alert-warning text-left" role="alert" style="margin-bottom:0; border-radius:0;position:relative;z-index:999999;">
    <p><i class="fa fa-exclamation-triangle fa-fw"></i>你的浏览器不支持vip.COM的一些新特性，请升级或更换你的浏览器至以下新版本。</p>
    <p>
      <a class='alert-link' href="http://rj.baidu.com/soft/detail/14744.html" target="_blank">(1)、谷歌浏览器(百度下载)</a><br>
      <a class='alert-link' href="https://www.google.com/intl/zh-CN/chrome/browser/" target="_blank">(2)、谷歌浏览器(官方下载)</a><br>
      <a class='alert-link' href="http://www.firefox.com.cn/download/" target="_blank">(3)、火狐浏览器</a><br>
      <a class='alert-link' href="http://chrome.360.cn/" target="_blank">(4)、360极速浏览器</a>
    </p>
    <p>HTML5时代已经来临，是时候换个浏览器了...</p>
  </div>
<![endif]-->

<script type="text/javascript" src="${static_domain }/statics/js/common/note.js?V${CH_VERSON }" charset="UTF-8"></script>
<script type="text/javascript" src="${static_domain }/statics/js/u/sxb/sxb.js?V${CH_VERSON }" charset="UTF-8"></script>
<div id="h_import" class="ctips" style="display:none;"></div>
<div class="bk-app" style="display:none;">
  <div class="clearfix">
    <div class="app-info pull-left">
      <img src="${static_domain }/statics/img/v2/common/app_vip57_red.png" alt="vip APP">
      <p>btcwinex APP<br>BTC/ETH/ETC/LTC实时交易</p>
    </div>
    <div class="app-btn pull-right">
      <a href="https://www.vip.com/mobile/download" class="btn btn-primary" target="_blank">立即下载</a>
    </div>
  </div>
</div>
  <header class="navbar navbar-static-top bk-nav navbar-black">
    <div class="bk-toolBar clearfix">
      <ul class="pull-left" id="topMenuPrice">
        <li><p>&nbsp;&nbsp;<i class="fa fa-line-chart fa-fw"></i>&nbsp;BTC/CNY：<b id="T_btcLastPrice" class="text-primary">--<i class="fa fa-arrow-up fa-fw"></i></b></p></li>
        <li><p>&nbsp;&nbsp;ETH/CNY：<b id="T_ethLastPrice" class="text-primary">--<i class="fa fa-arrow-up fa-fw"></i></b></p></li>
        <li><p>&nbsp;&nbsp;ETH/BTC：<b id="T_ethbtcLastPrice" class="text-primary">--<i class="fa fa-arrow-up fa-fw"></i></b></p></li>
          <li class="etcDisplay"><p>&nbsp;&nbsp;ETC/CNY：<b id="T_etcLastPrice" class="text-primary">--<i class="fa fa-arrow-up fa-fw"></i></b></p></li>
        <li><p>&nbsp;&nbsp;LTC/CNY：<b id="T_ltcLastPrice" class="text-primary">--<i class="fa fa-arrow-up fa-fw"></i></b></p></li>
<!--         <li><p>&nbsp;&nbsp;DAO/CNY：<b id="T_daoLastPrice" class="text-primary">--<i class="fa fa-arrow-up fa-fw"></i></b></p></li> -->
      </ul>
      
      <ul class="pull-right" id="topMenuNav">
        <li id="menuNew"><a href="${main_domain }/news" target="_self"><i class="fa fa-list-alt fa-fw"></i>${L:l(lan,'信息资讯') }<span class="badge"></span></a></li>
      </ul>
      <a id="btnRecommed" role="button" class="btn btn-primary btn-skew btn-sm pull-right mr10" style="margin-top:1px;" href="${vip_domain }/recommend" target="_self">
      	<span><i class="iconfont mr5" style="font-size:14px">&#xe664;</i>${L:l(lan,'推荐领奖励') }</span>
      </a>
    </div>
    <div class="bk-menuBar">
      <div class="container">
        <div class="navbar-header">
          <a class="navbar-brand bk-nav-logo" href="${main_domain }/" title="vip.COM"><img src="${static_domain }/statics/img/v2/common/vip_logo.png" alt="BitGlobal" /></a>
        </div>
        <nav class="navbar-collapse bk-navbar collapse" aria-expanded="false" id="bitMenu">
          <ul class="nav navbar-nav navbar-right" style="margin-right:0;">
            <li><a href="${main_domain }" target="_self">${L:l(lan,'首页') }</a></li>
            <li><a href="${vip_domain }/onekey" target="_self">${L:l(lan,'一键买卖') }</a></li>
            <li class="dropdown bk-secd regbtn">
              <a class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false" onClick="location.href='${trans_domain }/btc'">${L:l(lan,'专业交易') }&nbsp;&nbsp;<i class="caret"></i></a>
              <ul class="dropdown-menu animated bk-secd-menu" data-animation="fadeIn" role="menu" id="M_transDrop">
                 <li><a href="${trans_domain }/btc" target="_self"><img src="${static_domain }/statics/img/v2/trans/market-btc.png">${L:l(lan,'BTC/CNY交易')}</a></li>
                 <li><a href="${trans_domain }/ltc" target="_self"><img src="${static_domain }/statics/img/v2/trans/market-ltc.png">${L:l(lan,'LTC/CNY交易')}</a></li>
                 <li><a href="${trans_domain }/eth" target="_self"><img src="${static_domain }/statics/img/v2/trans/market-eth.png">${L:l(lan,'ETH/CNY交易')}</a></li>
                 <li class="etcDisplay"><a href="${trans_domain }/etc" target="_self"><img src="${static_domain }/statics/img/v2/trans/market-etc.png">${L:l(lan,'ETC/CNY交易')}</a></li>
                 <li><a href="${trans_domain }/ethbtc" target="_self"><img src="${static_domain }/statics/img/v2/trans/market-eth.png">${L:l(lan,'ETH/BTC交易')}</a></li>
                 <li class="btqDisplay" style="display:none;"><a href="${trans_domain }/btq" target="_self"><img src="${static_domain }/statics/img/v2/trans/market-btq.png">${L:l(lan,'BTQ/BTC交易')}</a></li>
<%--                 <li><a href="${vip_domain }/u/api" target="_self">${L:l(lan,'API交易')}</a></li> --%>
		          </ul>
            </li>
            <li class="dropdown bk-secd">
              <a class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false" onClick="location.href='${trans_domain }/markets/btc'">${L:l(lan,'K线交易') }&nbsp;&nbsp;<i class="caret"></i></a>
              <ul class="dropdown-menu animated bk-secd-menu" data-animation="fadeIn" role="menu" id="M_klineDrop">
                 <li><a href="${trans_domain }/markets/btc" target="_self"><img src="${static_domain }/statics/img/v2/trans/market-btc.png">${L:l(lan,'BTC/CNY行情')}</a></li>
                 <li><a href="${trans_domain }/markets/ltc" target="_self"><img src="${static_domain }/statics/img/v2/trans/market-ltc.png">${L:l(lan,'LTC/CNY行情')}</a></li>
                 <li><a href="${trans_domain }/markets/eth" target="_self"><img src="${static_domain }/statics/img/v2/trans/market-eth.png">${L:l(lan,'ETH/CNY行情')}</a></li>
                 <li class="etcDisplay"><a href="${trans_domain }/markets/etc" target="_self"><img src="${static_domain }/statics/img/v2/trans/market-etc.png">${L:l(lan,'ETC/CNY行情')}</a></li>
                 <li><a href="${trans_domain }/markets/ethbtc" target="_self"><img src="${static_domain }/statics/img/v2/trans/market-eth.png">${L:l(lan,'ETH/BTC行情')}</a></li>
		          </ul>
            </li>
            <li class="nologin"><a href="${vip_domain }/user/login" target="_self">${L:l(lan,'登录') }</a></li>
            <li class="nologin"><a href="${vip_domain }/user/register" target="_self">${L:l(lan,'注册') }</a></li>
            <li class="dropdown bk-secd logined" style=" display: none;">
              <a class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false" onClick="location.href='${vip_domain }/u/asset'">${L:l(lan,'财务') }&nbsp;&nbsp;<i class="caret"></i></a>
              <ul class="dropdown-menu animated bk-secd-menu" data-animation="fadeIn" role="menu" style="right:-30px;">
                <li><a href="${vip_domain }/u/asset" target="_self">${L:l(lan,'账户资产')}</a></li>
                <li><a href="${vip_domain }/u/payin/cny" target="_self">${L:l(lan,'充值业务')}</a></li>
                <li><a href="${vip_domain }/u/payout/btc" target="_self">${L:l(lan,'提现业务')}</a></li>
                <li><a href="${p2p_domain }/u/loan" target="_self">${L:l(lan,'融资融币')}</a></li>
		          </ul>
            </li>
            <li class="logined dropdown bk-secd" style="display: none;">
              <a class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false" onClick="location.href='${vip_domain }/u/safe'">
                <i id="M_userLevelIco"></i>&nbsp;&nbsp;<span id="M_userName"></span>&nbsp;&nbsp;<i class="caret"></i>
              </a>
              <div class="dropdown-menu animated bk-secd-menu" data-animation="fadeIn" role="menu" id="M_userDrop">
                  <div class="m-userDrop">
                    <div class="m-userDrop-hd">
                      <p>
                        <a id="topMobileStatus" href="${vip_domain }/u/auth/mobile" target="_self" title="${L:l(lan,'手机认证')}" style="font-size:18px;"><i class="fa fa-mobile"></i></a>
                        <a id="topEmailStatus" href="${vip_domain }/u/auth/email" target="_self" title="${L:l(lan,'邮箱认证')}"><i class="fa fa-envelope-o"></i></a>
                        <a id="topAuthStatus" href="${vip_domain }/u/auth/simple" target="_self" title="${L:l(lan,'初级实名认证')}"><i class="fa fa-user"></i></a>
                        <a id="topAuthDepth" href="${vip_domain }/u/auth/simple" target="_self" title="${L:l(lan,'高级实名认证')}"><i class="iconfont icon-cc-card"></i></a>
                        <a id="topGoogleStatus" href="${vip_domain }/u/auth/google?oper=0&dealType=googleAuth&dealVal=99" target="_self" title="${L:l(lan,'Google认证')}"><i class="fa fa-google"></i></a>
                        <a class="quit pull-right" href="${vip_domain }/user/logout" target="_self" title="${L:l(lan,'退出登录')}"><i class="fa fa-sign-out"></i>${L:l(lan,'退出登录')}</a>
                      </p>
                    </div>
                    <div class="m-userDrop-bd" id="M_userDrop1">
                      <a class="pull-right mr20" href="${vip_domain }/u/bill" target="_self">${L:l(lan,'综合账单')}</a>
                      <p>${L:l(lan,'账户总资产')}：<span class="assetNum text-primary" id="D_allAsset">--</span> CNY</p>
                      <a class="pull-right mr20" href="${main_domain }/sxb" target="_self">${L:l(lan,'资产凭证')}</a>
                      <p>${L:l(lan,'账户净资产')}：<span class="assetNum text-primary" id="D_canAsset">--</span> CNY</p>
                      <a class="pull-right mr20" href="${vip_domain }/u/level" target="_self">${L:l(lan,'积分等级')}<i id="D_userLevel"></i></a>
                      <p>${L:l(lan,'账户总积分')}：<span class="assetNum text-primary" id="D_userTotal">--</span></p>
                    </div>
                    <div class="m-userDrop-bd" id="M_userDrop2">
                      <table width="100%" border="0" cellspacing="0" cellpadding="0">
										    <thead>
										        <tr>
										            <th style="width:88px">${L:l(lan,'币种')}</th>
										            <th>${L:l(lan,'可用')}</th>
										            <th>${L:l(lan,'冻结')}</th>
										            <th><span class="daiTit">${L:l(lan,'借贷')}</span></th>
										        </tr>
										    </thead>
										    <tbody>
										        <tr>
										            <td>${L:l(lan,'人民币')}[CNY]</td>
										            <td><span id="D_cnyCanUse" class="assetNum text-primary">--</span></td>
										            <td><span id="D_cnyFreeze" class="assetNum">--</span></td>
										            <td><span id="D_cnyLoan" class="daiNum">--</span></td>
										        </tr>
										        <tr>
										            <td>${L:l(lan,'比特币')}[BTC]</td>
										            <td><span id="D_btcCanUse" class="assetNum text-primary">--</span></td>
										            <td><span id="D_btcFreeze" class="assetNum">--</span></td>
										            <td><span id="D_btcLoan" class="daiNum">--</span></td>
										        </tr>
										        <tr>
										            <td>${L:l(lan,'莱特币')}[LTC]</td>
										            <td><span id="D_ltcCanUse" class="assetNum text-primary">--</span></td>
										            <td><span id="D_ltcFreeze" class="assetNum">--</span></td>
										            <td><span id="D_ltcLoan" class="daiNum">--</span></td>
										        </tr>
										        <tr>
										            <td>${L:l(lan,'ETH')}[ETH]</td>
										            <td><span id="D_ethCanUse" class="assetNum text-primary">--</span></td>
										            <td><span id="D_ethFreeze" class="assetNum">--</span></td>
										            <td><span id="D_ethLoan" class="daiNum">--</span></td>
										        </tr>
										        <tr> 
										            <td>${L:l(lan,'ETC')}[ETC]</td>
										            <td><span id="D_etcCanUse" class="assetNum text-primary">--</span></td>
										            <td><span id="D_etcFreeze" class="assetNum">--</span></td>
										            <td><span id="D_etcLoan" class="daiNum">--</span></td>
										        </tr>
<!-- 										        <tr> -->
<%-- 										            <td>${L:l(lan,'道资金')}[DAO]</td> --%>
<!-- 										            <td><span id="D_daoCanUse" class="assetNum text-primary">--</span></td> -->
<!-- 										            <td><span id="D_daoFreeze" class="assetNum">--</span></td> -->
<!-- 										            <td><span id="D_daoLoan" class="daiNum">--</span></td> -->
<!-- 										        </tr> -->
										        <tr class="btqDisplay" style="display:none;">
										            <td>${L:l(lan,'比特权')}[BTQ]</td>
										            <td><span id="D_btqCanUse" class="assetNum text-primary">--</span></td>
										            <td><span id="D_btqFreeze" class="assetNum">--</span></td>
										            <td><span id="D_btqLoan" class="">--</span></td>
										        </tr>
										    </tbody>
										  </table>
                    </div>
                    <div class="m-userDrop-fd">
                      <p class="text-center">
                        <a href="${vip_domain }/u/payin/cny" class="btn btn-primary btn-sm mr30" role="button"><i class="bk-ico incoin"></i>${L:l(lan,'充值/充币')}</a>
		                <a href="${vip_domain }/u/payout/btc" class="btn btn-second btn-sm" role="button"><i class="bk-ico outcoin"></i>${L:l(lan,'提现/提币')}</a>
                      </p>
                    </div>
                  </div>
              </div>
            </li>
            <li class="logined quit" style="display: none;">
              <a href="${vip_domain }/user/logout" target="_self" title="${L:l(lan,'退出登录')}"><i class="fa fa-sign-out"></i></a>
            </li>
            
            <li class="top_langdown nologin">
	            <c:if test="${lan == 'cn' }">
					<a title="中文"  class="btncn"></a>
	            	<div class="down">
	            		<a title="English" href="javascript:vip.setLan('en');" class="btnen"></a>
	            	</div>
				</c:if>
	            <c:if test="${lan == 'en' }">
					<a title="English"  class="btnen"></a>
	            	<div class="down">
	            		<a title="English" href="javascript:vip.setLan('cn');" class="btncn"></a>
	            	</div>
				</c:if>
            </li>
            
          </ul>
        </nav>
      </div>
    </div>
  </header>