<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<style>
/*字体引入*/
@font-face {font-family: "iconfont";
  src: url('/common/fonts/iconfont.eot'); /* IE9*/
  src: url('/common/fonts/iconfont.eot?#iefix') format('embedded-opentype'), /* IE6-IE8 */
  url('/common/fonts/iconfont.woff') format('woff'), /* chrome, firefox */
  url('/common/fonts/iconfont.ttf') format('truetype'), /* chrome, firefox, opera, Safari, Android, iOS 4.2+*/
  url('/common/fonts/iconfont.svg#iconfont') format('svg'); /* iOS 4.1- */
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

<script type="text/javascript" src="${static_domain }/statics/js/common/note.js"></script>
<script type="text/javascript" src="${static_domain }/statics/js/u/sxb/sxb.js"></script>
<div id="h_import" class="ctips" style="display:none;"></div>

  <header class="navbar navbar-static-top bk-nav navbar-black">
    <div class="bk-toolBar clearfix">
      <ul class="pull-left" id="topMenuPrice">
        <li>
          <p>&nbsp;&nbsp;<i class="fa fa-line-chart fa-fw"></i>
            &nbsp;BTC最新成交价：<b id="T_btcLastPrice" class="text-primary">￥0.00<i class="fa fa-arrow-up fa-fw"></i></b>
            &nbsp;&nbsp;24H最高价：<b id="T_btcMaxPrice">￥0.00</b>
            &nbsp;&nbsp;24H最低价：<b id="T_btcMinPrice">￥0.00</b>
          </p>
        </li>
        <li><p>&nbsp;&nbsp;LTC最新成交价：<b id="T_ltcLastPrice" class="text-second">￥0.00<i class="fa fa-arrow-down fa-fw"></i></b></p></li>
      </ul>
      
      <ul class="pull-right" id="topMenuNav">
        <li id="menuNew"><a href="${main_domain }/news" target="_self"><i class="fa fa-list-alt fa-fw"></i>${L:l(lan,'信息资讯') }<span class="badge"></span></a></li>
        <%-- <li id="menuApp"><a href="javascript:JuaBox.bubble('尽请期待')" target="_self"><i class="glyphicon glyphicon-phone fa-fw"></i>${L:l(lan,'App客户端') }</a></li>	 --%>      
        <li class="dropdown bk-secd" id="menuAll">
          <a class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><i class="fa fa-navicon fa-fw"></i>${L:l(lan,'网站导航') }</a>
          <ul class="dropdown-menu animated bk-secd-menu" data-animation="fadeIn" role="menu" style="left:auto;right:0;">
            <div class="bk-secd-table">
              <div class="col-xs-2 bk-secd-menu-list">
                <h4>${L:l(lan,'行情交易') }</h4>
                <li><a href="${trans_domain }/btc" target="_self">${L:l(lan,'比特币交易') }</a></li>
                <li><a href="${trans_domain }/ltc" target="_self">${L:l(lan,'莱特币交易') }</a></li>
                <li><a href="${trans_domain }/btq" target="_self">${L:l(lan,'比特权交易') }</a></li>
              </div>
              <div class="col-xs-2 bk-secd-menu-list">
                <h4>${L:l(lan,'充值') }</h4>
                <li><a href="${vip_domain }/u/pay/recharge" target="_self">人民币</a></li>
                <li><a href="${vip_domain }/u/btc/uprecord" target="_self">比特币</a></li>
                <li><a href="${vip_domain }/u/ltc/uprecord" target="_self">莱特币</a></li>
              </div>
              <div class="col-xs-2 bk-secd-menu-list">
                <h4>${L:l(lan,'提现') }</h4>
                <li><a href="${vip_domain }/u/pay/cash" target="_self">人民币</a></li>
                <li><a href="${vip_domain }/u/btc/download" target="_self">比特币</a></li>
                <li><a href="${vip_domain }/u/ltc/download" target="_self">莱特币</a></li>
              </div>
              <div class="col-xs-2 bk-secd-menu-list">
                <h4>${L:l(lan,'安全') }</h4>
                <li><a href="${vip_domain }/u/safe" target="_self">${L:l(lan,'安全设置') }</a></li>
<%--                 <li><a href="${vip_domain }/u/safe" target="_self">${L:l(lan,'安全策略') }</a></li> --%>
                <li><a href="${vip_domain }/u/safe" target="_self">${L:l(lan,'异地登录') }</a></li>
                <li><a href="${main_domain }/sxb" target="_self">${L:l(lan,'资产凭证') }</a></li>
              </div>
              <div class="col-xs-2 bk-secd-menu-list">
                <h4>${L:l(lan,'用户') }</h4>
                <li><a href="${vip_domain }/u/safe" target="_self">${L:l(lan,'基本信息') }</a></li>
                <li><a href="${vip_domain }/u/safe/auth" target="_self">${L:l(lan,'实名认证') }</a></li>
                <li><a href="${vip_domain }/u/level" target="_self">${L:l(lan,'积分等级') }</a></li>
                <%-- <li><a href="" target="_self">${L:l(lan,'我的消息') }</a></li> --%>
                <li><a href="${vip_domain }/u/api/rest" target="_self">${L:l(lan,'API管理') }</a></li>
              </div>
              <div class="col-xs-2 bk-secd-menu-list">
                <h4>更多</h4>
                <li><a href="${main_domain }/news" target="_self">${L:l(lan,'公告') }</a></li>
                <li><a href="${main_domain }/help/onlineqa" target="_self">${L:l(lan,'帮助') }</a></li>
                <li><a href="https://www.jua.com/" target="_blank">JUA理财</a></li>
                <li><a href="https://www.bitbank.com/" target="_blank">Bitbank</a></li>
                <%-- <li><a href="${main_domain }/help/contact" target="_self">${L:l(lan,'联系我们') }</a></li> --%>
              </div>
            </div>
          </ul>
        </li>
      </ul>
      <a id="btnRecommed" role="button" class="btn btn-primary btn-skew btn-sm pull-right mr10" href="${vip_domain }/u/recommed" target="_self"><span><i class="iconfont icon-ruhechongzhiicon"></i>推广获取高额奖励</span></a>
    </div>
    <div class="bk-menuBar">
      <div class="container">
        <div class="navbar-header">
          <a class="navbar-toggle bk-nav-toggle" href="#bitMMenu" role="button"><i class="fa fa-navicon"></i></a>
          <a class="navbar-brand bk-nav-logo" href="${main_domain }/" title="btcwinex.COM">btcwinex</a>
        </div>
        <nav class="navbar-collapse bk-navbar collapse" aria-expanded="false" id="bitMenu">
          <ul class="nav navbar-nav navbar-right" style="margin-right:0;">
            <li><a href="${main_domain }" target="_self"><i class="fa fa-home fa-fw xs-icon"></i>${L:l(lan,'首页') }</a></li>
            <li><a href="${trans_domain }/btc" target="_self"><i class="fa fa-tags fa-fw xs-icon"></i>${L:l(lan,'交易') }</a></li>
            <li><a href="${trans_domain }/markets/BTC" target="_blank"><i class="fa fa-tags fa-fw xs-icon"></i>${L:l(lan,'行情') }</a></li>
            <li><a href="${vip_domain }/u" target="_self"><i class="fa fa-tags fa-fw xs-icon"></i>${L:l(lan,'财务') }</a></li>
            <li class="nologin"><a href="${vip_domain }/user/login" target="_blank"><i class="fa fa-user fa-fw xs-icon"></i>${L:l(lan,'登录') }</a></li>
            <li class="nologin"><a href="${vip_domain }/user/register" target="_blank"><i class="fa fa-pencil fa-fw xs-icon"></i>${L:l(lan,'注册') }</a></li>
            <li class="logined dropdown bk-secd" style="display: none;">
              <a class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false" onClick="location.href='${vip_domain }/u/safe'">
                <i class="fa fa-user fa-fw xs-icon"></i><i id="M_userLevelIco"></i>&nbsp;&nbsp;<span id="M_userName"></span>&nbsp;&nbsp;<i class="caret"></i>
              </a>
              <div class="dropdown-menu animated bk-secd-menu" data-animation="fadeIn" role="menu" id="M_userDrop">
                  <div class="m-userDrop">
                    <div class="m-userDrop-hd">
                      <p>
                        <a id="topMobileStatus" href="${vip_domain }/u/safe/approve/mobile" target="_self" title="手机认证" style="font-size:18px;"><i class="fa fa-mobile"></i></a>
                        <a id="topEmailStatus" href="${vip_domain }/u/safe/approve/email" target="_self" title="邮箱认证"><i class="fa fa-envelope-o"></i></a>
                        <a id="topAuthStatus" href="${vip_domain }/u/safe/auth" target="_self" title="实名认证"><i class="fa fa-user"></i></a>
                        <a id="topGoogleStatus" href="${vip_domain }/u/safe/safeAuth?oper=0&dealType=googleAuth&dealVal=99" target="_self" title="Google认证"><i class="fa fa-google"></i></a>
                        <a class="quit pull-right" href="${vip_domain }/user/logout" target="_self" title="退出登录"><i class="fa fa-sign-out"></i>退出登录</a>
                        <a class="quit pull-right" style="margin-right:10px;" href="https://www.jua.com/" target="_blank">JUA理财</a>
                      </p>
                    </div>
                    <div class="m-userDrop-bd" id="M_userDrop1">
                      <a class="pull-right mr20" href="${vip_domain }/u" target="_self">财务中心</a>
                      <p>账户总资产：<span class="assetNum text-primary">0.00</span> CNY</p>
                      <p>账户净资产：<span class="assetNum text-primary">0.00</span> CNY</p>
                    </div>
                    <div class="m-userDrop-bd" id="M_userDrop2">
                      <table width="100%" border="0" cellspacing="0" cellpadding="0">
										    <thead>
										        <tr>
										            <th style="width:60px">币种</th>
										            <th style="display:none;">总额</th>
										            <th>可用</th>
										            <th>冻结</th>
										            <th><span class="daiTit">借贷</span></th>
										            <!-- <th style="width:80px">操作</th> -->
										        </tr>
										    </thead>
										    <tbody>
										        <tr>
										            <td>人民币</td>
										            <td style="display:none;"><span class="assetNum">0.00</span></td>
										            <td><span class="assetNum text-primary">0.00</span></td>
										            <td><span class="assetNum">0.00</span></td>
										            <td><span class="daiNum">0.00</span></td>
										            <%-- <td>
											            <a href="${vip_domain }/u/pay/recharge" title="充值RMB">充值</a>
											            <a href="${vip_domain }/u/pay/cash" title="提现RMB">提现</a>
											            <a style="display: none;" href="${vip_domain }/u/pay/fundsdetails" title="人民币明细">明细</a>
										            </td> --%>
										        </tr>
										        <tr>
										            <td>比特币</td>
										            <td style="display:none;"><span class="assetNum">0.00</span></td>
										            <td><span class="assetNum text-primary">0.00</span></td>
										            <td><span class="assetNum">0.00</span></td>
										            <td><span class="daiNum">0.00</span></td>
										            <%-- <td>
										            <a href="${vip_domain }/u/btc/uprecord" title="充值BTC">转入</a>
										            <a href="${vip_domain }/u/btc/download" title="BTC提现">转出</a>
										            <a style="display: none;" href="${trans_domain }/btc" title="交易BTC">交易</a>
										            </td> --%>
										        </tr>
										        <tr>
										            <td>莱特币</td>
										            <td style="display:none;"><span class="assetNum">0.00</span></td>
										            <td><span class="assetNum text-primary">0.00</span></td>
										            <td><span class="assetNum">0.00</span></td>
										            <td><span class="daiNum">0.00</span></td>
										            <%-- <td>
										            <a href="${vip_domain }/u/ltc/uprecord" title="充值LTC">转入</a>
										            <a href="${vip_domain }/u/ltc/download" title="LTC提现">转出</a>
										            <a style="display: none;" href="${trans_domain }/ltc" title="交易LTC">交易</a>
										            </td> --%>
										        </tr>
										        <tr>
										            <td>比特权</td>
										            <td style="display:none;"><span class="assetNum">0.00</span></td>
										            <td><span class="assetNum text-primary">0.00</span></td>
										            <td><span class="assetNum">0.00</span></td>
										            <td><span class="">--</span></td>
										            <%-- <td>
										            <a href="${trans_domain }/btq" title="交易BTQ">交易</a>
										            <a href="${vip_domain }/u/score" title="BTQ明细">明细</a>
										            </td> --%>
										        </tr>
										    </tbody>
										  </table>
                    </div>
                    <div class="m-userDrop-fd">
                      <p class="text-center">
                        <a href="${vip_domain }/u/payin" class="btn btn-primary btn-sm mr30" role="button" target="_self"><i class="bk-ico incoin"></i>收款/充值</a>
                        <a href="${vip_domain }/u/payout" class="btn btn-outline btn-sm" role="button" target="_self"><i class="bk-ico outcoin"></i>付款/提现</a>
                      </p>
                      <%-- <table width="100%" border="0" cellspacing="0" cellpadding="0">
										    <tbody>
										        <tr>
										            <td><a class="btn btn-primary btn-sm" href="${vip_domain }/u/pay/recharge" target="_self">充值</a></td>
										            <td><a class="btn btn-primary btn-sm" href="${vip_domain }/u/pay/cash" target="_self">提现</a></td>
										            <td><a class="btn btn-primary btn-sm" href="${main_domain }/sxb" target="_self">资产凭证</a></td>
										        </tr>
										    </tbody>
										  </table> --%>
                    </div>
                  </div>
              </div>
            </li>            
          </ul>
        </nav>
      </div>
    </div>
  </header>

<script type="text/javascript" charset="utf-8">
  try{
    var oldDomain=document.domain;
    var numm1=oldDomain.indexOf('vip');
    document.domain = oldDomain.substring(numm1,oldDomain.length)
  } catch(msg) {
    document.domain = 'vip.com';  
  }
  vip.user.init();
</script>