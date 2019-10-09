<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>

  <header class="navbar navbar-static-top bk-nav navbar-black">
    <div class="bk-toolBar clearfix">
      <ul class="pull-left" id="topMenuPrice">
        <li>
          <p>&nbsp;&nbsp;<i class="fa fa-line-chart fa-fw"></i>
            &nbsp;BTC最新成交价：<b id="T_btcLastPrice" class="text-primary">￥2177.83<i class="fa fa-arrow-up fa-fw"></i></b>
            &nbsp;&nbsp;24H最高价：<b id="T_btcMaxPrice">￥2177.83</b>
            &nbsp;&nbsp;24H最低价：<b id="T_btcMinPrice">￥2177.83</b>
          </p>
        </li>
        <li><p>&nbsp;&nbsp;LTC最新成交价：<b id="T_ltcLastPrice" class="text-second">￥20.83<i class="fa fa-arrow-down fa-fw"></i></b></p></li>
      </ul>
      <ul class="pull-right" id="topMenuNav">
        <li id="menuNew"><a href="/news" target="_self"><i class="fa fa-list-alt fa-fw"></i>${L:l(lan,'信息资讯') }<span class="badge"></span></li>
        <li id="menuApp"><a href="/bitbank/ac/app" target="_self"><i class="glyphicon glyphicon-phone fa-fw"></i>${L:l(lan,'App客户端') }</a></li>	      
        <li class="dropdown bk-secd" id="menuAll">
          <a class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><i class="fa fa-navicon fa-fw"></i>${L:l(lan,'网站导航') }</a>
          <ul class="dropdown-menu animated bk-secd-menu" data-animation="fadeIn" role="menu" style="left:auto;right:0;">
            <div class="bk-secd-table">
              <div class="col-xs-2 bk-secd-menu-list">
                <h4>${L:l(lan,'行情交易') }</h4>
                <li><a href="${trans_domain }/v2/btc" target="_self">${L:l(lan,'比特币交易') }</a></li>
                <li><a href="${trans_domain }/v2/ltc" target="_self">${L:l(lan,'莱特币交易') }</a></li>
                <li><a href="${trans_domain }/v2/btq" target="_self">${L:l(lan,'比特权交易') }</a></li>
              </div>
              <div class="col-xs-2 bk-secd-menu-list">
                <h4>${L:l(lan,'交易管理') }</h4>
                <li class="hideen"><a href="/bitbank/exchange/btc"><i class="glyphicon glyphicon-piggy-bank fa-fw xs-icon"></i>${L:l(lan,'人民币比特币交易') }</a></li>
                <li class="hideen"><a href="/bitbank/exchange/ltc"><i class="glyphicon glyphicon-piggy-bank fa-fw xs-icon"></i>${L:l(lan,'人民币莱特币交易') }</a></li>
              </div>
              <div class="col-xs-2 bk-secd-menu-list">
                <h4>${L:l(lan,'充值提现') }</h4>
                <li><a href="" target="_self">${L:l(lan,'比特币') }</a></li>
                <li><a href="" target="_self">${L:l(lan,'莱特币') }</a></li>
                <li><a href="" target="_self">${L:l(lan,'人民币') }</a></li>
              </div>
              <div class="col-xs-2 bk-secd-menu-list">
                <h4>${L:l(lan,'安全') }</h4>
                <li><a href="" target="_self">${L:l(lan,'安全设置') }</a></li>
                <li><a href="" target="_self">${L:l(lan,'安全策略') }</a></li>
                <li><a href="" target="_self">${L:l(lan,'异地登录') }</a></li>
                <li><a href="" target="_self">${L:l(lan,'资产凭证') }</a></li>
              </div>
              <div class="col-xs-2 bk-secd-menu-list">
                <h4>${L:l(lan,'用户') }</h4>
                <li><a href="" target="_self">${L:l(lan,'基本信息') }</a></li>
                <li><a href="" target="_self">${L:l(lan,'实名认证') }</a></li>
                <li><a href="" target="_self">${L:l(lan,'积分等级') }</a></li>
                <li><a href="" target="_self">${L:l(lan,'我的消息') }</a></li>
                <li><a href="" target="_self">${L:l(lan,'API管理') }</a></li>
              </div>
              <div class="col-xs-2 bk-secd-menu-list">
                <h4>${L:l(lan,'更多') }</h4>
                <li><a href="/news" target="_self">${L:l(lan,'公告') }</a></li>
                <li><a href="" target="_self">${L:l(lan,'帮助') }</a></li>
                <li><a href="" target="_self">${L:l(lan,'下载') }</a></li>
                <li><a href="" target="_self">${L:l(lan,'联系我们') }</a></li>
              </div>
            </div>
          </ul>
        </li>
      </ul>
    </div>
    <div class="bk-menuBar">
      <div class="container">
        <div class="navbar-header">
          <a class="navbar-toggle bk-nav-toggle" href="#bitMMenu" role="button"><i class="fa fa-navicon"></i></a>
          <a class="navbar-brand bk-nav-logo" href="/i" title="vip.COM"></a>
        </div>
        <nav class="navbar-collapse bk-navbar collapse" aria-expanded="false" id="bitMenu">
          <ul class="nav navbar-nav navbar-right" style="margin-right:0;">
            <li><a href="/i" target="_self"><i class="fa fa-home fa-fw xs-icon"></i>${L:l(lan,'首页') }</a></li>
            <li><a href="/v2/btc" target="_self"><i class="fa fa-tags fa-fw xs-icon"></i>${L:l(lan,'交易') }</a></li>
            <li><a href="/v2/btc" target="_self"><i class="fa fa-tags fa-fw xs-icon"></i>${L:l(lan,'行情') }</a></li>
            <li><a href="/v2/btc" target="_self"><i class="fa fa-tags fa-fw xs-icon"></i>${L:l(lan,'财务') }</a></li>
            <li class="nologin"><a href="/bitbank/user/log"><i class="fa fa-user fa-fw xs-icon"></i>${L:l(lan,'登录') }</a></li>
            <li class="nologin"><a href="/bitbank/user/mobile_reg"><i class="fa fa-pencil fa-fw xs-icon"></i>${L:l(lan,'注册') }</a></li>
            <li class="logined dropdown bk-secd" style="display: none;">
              <a class="dropdown-toggle a1" data-toggle="dropdown" role="button" aria-expanded="false" onClick="location.href='/u/admin'">
                <i class="fa fa-user fa-fw xs-icon"></i><span class="am"></span><i class="caret"></i>
              </a>
              <ul class="dropdown-menu animated bk-secd-menu" data-animation="fadeIn" role="menu">
                <div class="col-xs-3 pull-right bk-secd-menu-list">
                  <li><a href="/u/admin"><i class="fa fa-diamond fa-fw"></i>${L:l(lan,"资产管理") }</a></li>
                  <li><a href="/manage"><i class="fa fa-shield fa-fw"></i>${L:l(lan,"安全中心") }</a></li>
                  <li><a href="/help/jbao/"><i class="fa fa-legal fa-fw"></i>${L:l(lan,"资产凭证") }</a></li>
                  <li><a href="/user/logout"><i class="fa fa-sign-out fa-fw"></i>${L:l(lan,"退出登录") }</a></li>
                </div>
              </ul>
            </li>            
          </ul>
        </nav>
      </div>
    </div>
  </header>