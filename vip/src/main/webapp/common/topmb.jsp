<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<style type="text/css">
    @font-face {
        font-family: "iconfont";
        src: url('/common/fonts/iconfont.eot?v=${CH_VERSON }');
        /* IE9*/
        src: url('/common/fonts/iconfont.eot?#iefix&v=${CH_VERSON }') format('embedded-opentype'),
            /* IE6-IE8 */
        url('/common/fonts/iconfont.woff?v=${CH_VERSON }') format('woff'),
            /* chrome, firefox */
        url('/common/fonts/iconfont.ttf?v=${CH_VERSON }') format('truetype'),
            /* chrome, firefox, opera, Safari, Android, iOS 4.2+*/
        url('/common/fonts/iconfont.svg?v=${CH_VERSON }#iconfont') format('svg');
        /* iOS 4.1- */
    }

    @font-face {
        font-family: "iconfont2";
        src: url('/common/fonts/iconfont2.eot?v=${CH_VERSON }');
        /* IE9*/
        src: url('/common/fonts/iconfont2.eot?#iefix&v=${CH_VERSON }') format('embedded-opentype'),
            /* IE6-IE8 */
        url('/common/fonts/iconfont2.woff?v=${CH_VERSON }') format('woff'),
            /* chrome, firefox */
        url('/common/fonts/iconfont2.ttf?v=${CH_VERSON }') format('truetype'),
            /* chrome, firefox, opera, Safari, Android, iOS 4.2+*/
        url('/common/fonts/iconfont2.svg?v=${CH_VERSON}#iconfont') format('svg');
        /* iOS 4.1- */
    }

    @font-face {
        font-family: 'FontAwesome';
        src: url('/common/fonts/fontawesome-webfont.eot?v=4.3.0');
        src: url('/common/fonts/fontawesome-webfont.eot?#iefix&v=4.3.0') format('embedded-opentype'),
        url('/common/fonts/fontawesome-webfont.woff2?v=4.3.0') format('woff2'),
        url('/common/fonts/fontawesome-webfont.woff?v=4.3.0') format('woff'),
        url('/common/fonts/fontawesome-webfont.ttf?v=4.3.0') format('truetype'),
        url('/common/fonts/fontawesome-webfont.svg?v=4.3.0#fontawesomeregular') format('svg');
        font-weight: normal;
        font-style: normal;
    }

    @font-face {
        font-family: 'Glyphicons Halflings';
        src: url('/common/fonts/glyphicons-halflings-regular.eot');
        src: url('/common/fonts/glyphicons-halflings-regular.eot?#iefix') format('embedded-opentype'),
        url('/common/fonts/glyphicons-halflings-regular.woff2') format('woff2'),
        url('/common/fonts/glyphicons-halflings-regular.woff') format('woff'),
        url('/common/fonts/glyphicons-halflings-regular.ttf') format('truetype'),
        url('/common/fonts/glyphicons-halflings-regular.svg#glyphicons_halflingsregular') format('svg');
    }

    .navbar-nav>li.nav-right-tab>.dropdown-menu{
        border-radius:0;
        margin-top:0;
        height:auto;
        overflow: hidden;
        opacity:1;
    }
    .navbar-collapse .nav>li.logined {
        margin-top: 10px;
        margin-left: 0;
        position: relative;
    }

</style>
<style>
    /*移动端*/
    .icon-logo-Hitman-wp{
        width: 3.97037rem;
        height: 1.296296rem;
    }
    .hd-wp{
        padding: .277778rem .555556rem;
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        z-index: 9999;
        background: #232731;
    }
    a{
        text-decoration: none!important;
    }
    .lng_wp{
        padding-top: .277778rem;
        font-size: .518519rem;
        color: #D4DCF0;
    }
    .lng_wp *{
        font-weight: normal;

    }

    .lng_wp .setMoney-ct{
        margin-right: .400741rem;
    }
    .setMoney-mu-wp{
        display: none;
        position: fixed;
        left: 0;
        right: 0;
        top: 0;
        bottom: 0;
        background: #2C303A;
    }
    .setMoney-mu-wp .xp li{
        text-align: center;
        padding-left: 0 !important;
        padding-right: 0 !important;
    }
    .setMoney-mu-wp .xp li a{
        margin-left: 0 !important;
    }
    .setMoney-mu-wp .mu-wp a{
        color: #D4DCF0;
    }
    .setMoney-mu-wp.sp0{
        text-align: left;
    }
    .setMoney-mu-wp.sp0 li{
        padding-left: 50%;
    }
    .setMoney-mu-wp.sp0 li a{
        margin-left: -1.87037rem;
    }
    .setMoney-mu-wp .g-ico{
        width: .888889rem;
        height: .592593rem;
        margin-right: .185185rem;
    }
    .setMoney-mu-wp ul{
        position: absolute;
        left: 0;
        right: 0;
        top: 50%;

    }
    .setMoney-mu-wp ul li{
        line-height: 1.62963rem;
        padding: 0 1.481481rem;
        font-size: .592593rem;
        margin-bottom: .185185rem;

    }
    .setMoney-mu-wp .iconfont2{
        font-size: .592593rem;
    }
    .setMoney-mu-wp .ac{
        background: #3C414C;
    }
    .setMoney-mu-wp .mu-wp .ac a,
    .setMoney-mu-wp .ac a *{
        color: #3E85A2;
    }
    .setMoney-mu-wp .r{
        float: right;
    }
    .setMoney-mu-wp .th{
        padding-left: .185185rem;
    }
    .setMoney-mu-wp a *{
        color: #D4DCF0;
        font-weight: normal;
    }
    .opt-wp{
        position: absolute;
        top: .518519rem;
        right: .611111rem;
        width: .814815rem;
        height: .814815rem;
        background-size: 100%;
    }
    .fl{
        float: left;
    }
    .fr{
        float: right;
    }
    .gq .g-ico{
        margin: .030593rem .400741rem 0 0;
        width: .888889rem;
        height: .592593rem;

    }
    .menu-wp{
        width: .814815rem;
        height: .814815rem;
        display: inline-block;
        background-size: 100%;
    }

    body{
        min-width: auto;
    }
    .swiper-container{
        height: 5.033333rem;
    }
    .sys_nobox{
        text-align: left;
        padding: 0 .896296rem 0 1.455556rem;
    }
    .notice_tit{
        max-width: 11.537037rem;
    }
    .notice_time{
        display: none;
    }
    .sys_notice{
        height: 1.333333rem;
        line-height: 1.333333rem;
    }
    .sys_notice a{
        vertical-align: middle;
    }
    .content_box{
        padding: 0 .555556rem;
    }
    .content_newsmark{
        padding-top: .740741rem;
    }
    .market_show .markt_top li{
        width: 1.962963rem !important;
        height: 1.481481rem !important;
        line-height: 1.481481rem !important;
        font-size: .444444rem;
        font-weight: normal;
    }

    .market_show .markt_top .market_top_border:nth-of-type(2),
    .market_show .markt_top .market_top_border:nth-of-type(1){
        left: 1.962963rem !important;
        height: 1.481481rem !important;
    }
    .market_show .markt_top .market_top_border:nth-of-type(2){
        left: 3.925926rem !important;
    }

    .market_show .markt_top{
        width: auto;
        height: auto;
        background-color: #22262E;
    }
    .market_show .markt_top .slider{
        width: 1.962963rem;
        height: 1.481481rem;
        left: 1.962963rem;
    }

    .market_show{
        position: relative;
        min-width: auto;
        width: 100%;
        /*background-color: #22262E;*/
    }
    .inp_search{
        position: absolute;
        right: 0;
        top: 0;
        width: 100%;
    }
    .inp_search .sh-wp{
        color: #737A8D;
        font-size: .518519rem;
        background-size: 0.53815rem;
        padding-right: 0.918519rem;
        margin-top: .35rem;
        width: 100%;
        text-align: right;
        background-position: 97% 62%;
    }
    .inp_search em{
        display:inline-block;
        font-weight: normal;
    }
    .hd{
        visibility: hidden;
    }
    .content_newsmark i{
        font-size: .518519rem
    }
    .inp_search .inp_consearch{
        background: #2C303A;
        border: 1px solid #3E85A2;
        border-radius: 4px;
        padding: 0.55rem 0;
        padding-left: 0.2rem;
        width: 95%;
        font-size: .392593rem;
        color: #D4DCF0;
    }
    }
    .inp_search .qx-wp{
        position: absolute;
        width: .518519rem;
        height: .518519rem;
        right: .644444rem;
        top: .34181rem;

        background-size: 100%;
    }
    .inp_search .qx-cl{
        position: absolute;
        right: -20%;
        height: 1.203703rem;
        line-height: 1.203703rem;
        font-size: 0.492593rem;
        color: #D4DCF0;
    }

    .market_show .market_title li:nth-of-type(1){
        width: 37%;
        padding-left: 0.703704rem;
    }
    .market_show .market_title li:nth-of-type(3){
        padding-right: .5rem;
        float:right;
        width: 33%;
        text-align: right;
    }
    .market_show .market_title li:nth-of-type(2){
        width: 30%;
        text-align: left;
    }
    .market_show .market_title li span{
        font-size: .444444rem;
    }
    .market_show .market_title li i{
        top:0.086787rem !important;
    }
    .market_show .market_table .market_list td,
    .market_show .market_title li{
        line-height: 1.481481rem;
        height: 1.481481rem;
    }
    .market_show .market_title li:nth-of-type(3) i{
        top:0.49787rem !important;
    }
    .market_show .market_table .market_list .price_show *{
        vertical-align: middle;
        margin: 0;
        display:inline-block;
        margin-top:0;
        float: none;
    }
    .market_show .market_table td{
        font-size: .38rem !important;
    }
    .market_show .market_table .market_list td:nth-of-type(1){
        width: 37%;
        padding-left: 2%;
    }
    .market_show .market_table .market_list td:nth-of-type(2){
        width: 40%;
    }
    .market_show .market_table .market_list td:nth-of-type(3){
        width: 18%;
        text-align: left;
    }
    .news_list .timescross{
        width: 1px;
        height: 13rem;
        background-color: #414654;
        position: absolute;
        top: 1.5rem;
        left: 0.32rem;
    }
    .market_show .market_table .yellow,
    .market_show .market_table .gray{
        margin: 0 .07037rem;
        margin-top: -2%;
    }
    .market_show .market_table td .grey_box{
        width: 2.437037rem;
        line-height: .744444rem;
        margin-right: 0.1rem !important;
    }
    .market_show .market_table .market_list td:nth-of-type(2) *{
        font-size: .444444rem !important;
    }
    .triangle_up{
        left: 0.07037rem;
        top: -0.40rem;
    }
    .triangle_down{
        left: 0rem;
        top: 0.47rem;
    }
    .n-wp{
        float: none;
        clear: both;
        padding-top: .740741rem;
    }
    .bbyh-ns{
        position: relative;
    }
    .bbyh-wooke_tit-child{
        padding-top: 9px !important;
        margin-top: -9px !important;
    }
    .bbyh-ns .news_title{
        position: absolute;
        z-index: 9;
        /*background: #2C303A;*/
        left: 0.19rem;
        top: 1.01rem;
        right: 15px;
    }
    .bbyh-ns .news_list{
        z-index: 1;
    }
    .n-wp .news_list{
        margin: 0;
        width: 100%;
    }
    .news_title{
        font-size: .418519rem;
        line-height: .777778rem;
        letter-spacing: .021111rem;
        color: #fff;
    }
    #timeline{
        padding-left: .592593rem;
        padding-right: 5px;
    }
    #timeline li{
        margin: .814815rem 0;
    }
    .date{
        font-size: .444444rem;
        letter-spacing: 0.01rem;
    }
    .wooke_tit{
        margin-top: .337037rem;
        font-size: .518519rem;
        letter-spacing: 0;
    }
    .circle{
        margin-top: -0.115185rem;
        top: .277778rem;
        left: -0.747407rem;
        width: .296296rem;
        height: .296296rem;
    }
    #timeline p,
    .n-wp .content{
        font-size: .407407rem;
        letter-spacing: 0;
        line-height: .611111rem;
        padding-right: 2%;
    }
    .chart_box{
        width: 100%;
    }

    .main_title,
    .main_dic{
        width: 100%;
        text-align: center;
        padding:0 1.851852rem;
        float: none;
        height: auto;
    }
    .main_title{
        font-size: .750778rem;
    }
    .main_dic{
        font-size: .518519rem;
        margin-top: .37037rem;
        color: #737A8D;
    }
    .hereyChart_right .mb_btn{
        overflow: hidden;
        padding: 0.6rem  0.3rem 0.6rem 0.3rem;
    }

    .hereyChart_right .mb_btn div{
        float: left;
        width: 3.8rem;
        height: 1.1rem;
        background: #6687D3;
        border-radius: 4px;
        text-align: center;
    }
    .hereyChart_right .mb_btn div img{
        height: 0.8rem;
        padding-bottom: 0.2rem;
    }
    .hereyChart_right .mb_btn div span{
        display: inline-block;
        font-size: 0.4rem;
        color: #fff;
        padding-left: 0.25rem;
    }
    .hereyChart_right .mb_btn .iphones{

        margin-right: 0.5rem;
    }

    .hereyChart_right,
    .catChart_left,
    .axisChart_right{
        padding: 1.481481rem 0 0 0;
        float: none;
        width: 100%;
        height: auto;
        margin: 0;
        clear: both;
    }
    .friend_link{
        width: 100%;
        margin: 0;
        padding: 0;
    }
    .friend_title{
        font-size: .518519rem;
        line-height: .574074rem;
        text-align: center;
        margin-bottom: .185185rem;
    }
    .friend_box a{
        font-size: .344444rem;
        line-height: .666667rem;
        padding-right: .47037rem;
    }
    .bk-footer{
        width: 100%;
        min-width: inherit;
    }
    .bk-footer-hd{
        padding: 1.111111rem 0 .833333rem 0;
        border-color: #393D46;
        width: auto;
    }
    .bk-footer .bk-footer-hd .container{
        min-width: inherit;
        width: 100%;
    }
    .bk-footer-hd-right{
        width: 100%;
    }
    .bk-footer-logo .icon{
        width: 3.481481rem;
        height: 1.055556rem;
    }
    .bk-footer-logo .icon{
        margin: 0;
    }
    .bk-footer-logo{

        height: 1.055556rem;
        margin-bottom: 1.018519rem;
    }

    .bk-footer-hd-right ul{
        width: 100%;
        font-size: .444444rem;
        line-height: .666667rem;
    }
    .bk-footer-hd-right ul li{
        margin-right: .240741rem
    }
    .bk-footer-hd-right ul li.lst{
        margin-right: 0;
    }
    .bk-footer-hd-right ul li a{
        font-size: inherit;
        width: auto;
        line-height:inherit;
        padding:0;
    }
    .bk-footer-hd-right ul li a i{
        font-size: .4rem;
    }
    .bk-footer-hd-center a{
        font-size: .444444rem;
        line-height: .666667rem;
    }
    .bk-footer-hd-center{
        margin-bottom: .3rem;
    }
    .bk-footer-hd-right .share_it .item{
        width: .814815rem;
        height: .814815rem;
        line-height: inherit;
        margin-right: .207778rem;
    }
    .bk-footer-hd-right .share_it{
        height: auto;
        margin: 0;

    }
    .bk-footer-hd-right .share_it .item .iconfont{
        font-size: .8rem;
    }
    .bk-footer-info{
        font-size: .444444rem;
        line-height: .666667rem;
        padding: .37037rem 0 .555556rem 0;
    }
    .bk-footer-info p{
        font-size: inherit;
    }
    .mCSB_container{
        padding-left: .307778rem;
    }
    ._mCS_1 .mCSB_inside > .mCSB_container{
        margin-right: .307778rem;
    }
    .bbqh-more{
        font-size: .359259rem;
    }

    .notice_href *{
        vertical-align: top;
    }
    .top_bg .top_banner .banner_title_mo{
        font-family: Avenir-Light;
        font-size: 0.45rem;
        width: 89%;
        text-align: center;
        padding-bottom: 5%;
        color: #FFFFFF;
        margin: 0 auto;
        letter-spacing: 0.6px;
    }
    .chart_box .app_down{
        width: 100%;
        height: 10.5rem;
        background-color: #22262E;
        box-sizing: border-box;
        padding: 7% ;
        position: relative;
        margin-bottom: 10%;
    }
    .chart_box .app_down h4{
        font-family: Avenir-Roman;
        font-size: 0.53rem;
        line-height: 1rem ;
        color: #FFFFFF;
        letter-spacing: 0.56px;
    }
    .chart_box .app_down .titles{
        font-size: .344444rem;
        color: #737A8D;
    }
    .app_down .down_btn{
        padding-top: 0.3rem;
    }
    .app_down .down_btn .btn_app{
        border: 1px solid #3E85A2;
        width: 60%;
        height: 1rem;
        color: #3E85A2;
        box-sizing: border-box;
        margin: 0.4rem auto;
        line-height: 1rem;
        font-size: 0.35rem;
        border-radius: 0.5rem;
        padding-left: 1rem;
        position: relative;
    }
    .app_down .down_btn .btn_app .icons{
        width: 0.5rem;
        position: absolute;
        top: -0.05rem;
        left: 0.35rem;
    }
    .app_down .down_btn .btn_app .icons img{
        width: 100%;
    }
    .app_down .phone_img_mo{
        position: absolute;
        bottom: 0;
        right: 1.3rem;
        width: 5.7rem;
    }
    .app_down .phone_img_mo img{
        width: 100%;
    }

    .notice_href,
    .notice_tit{
        width: 100%;
    }
    #moneynameswp ul li{
        text-align: center;
    }
    #down_b{
        position: fixed;
        bottom: 0;
        left: 0;
        width: 100%;
        height: 2rem;
        background-color: #fff;
        box-sizing: border-box;
        padding: 0.25rem 0.35rem;
        z-index:10000;

    }
    #down_b>img{
        float: left;
        width: 1.5rem;
    }
    #down_b .texts{
        float: left;
        padding-left: 0.2rem;
        padding-top: 0.3rem;
    }
    #down_b .texts p{
        font-family: "Open Sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
        font-size: 0.45rem;
        color: #000;
    }
    #down_b .texts .p2{
        color: #62656A;
        font-size: 0.3rem;
        padding-top: 0.15rem;
    }
    #down_b .downs{
        float: right;
        padding-top: 0.4rem;

    }
    #down_b .downs #down_now{
        border: 0.037rem solid #3E85A2;
        padding: .1rem .45rem;
        border-radius: .5rem;
        font-size: 0.4rem;
        color: #3E85A2;
    }
    #down_b .downs #down_now img{
        width: 0.4rem;
    }
    #down_b .closed{
        float: right;
        width: 0.35rem;
        padding-top: 0.6rem;
        margin-left: 0.3rem;
    }
</style>
<script src="${static_domain }/statics/js/common/iconfont.js" charset="UTF-8"></script>
<!--[if lt IE 9]>
<div class="alert alert-warning text-left" role="alert" style="margin-bottom:0; border-radius:0;position:relative;z-index:999999;">
<p><i class="fa fa-exclamation-triangle fa-fw"></i>${L:l1(lan,'你的浏览器不支持%%的一些新特性，请升级或更换你的浏览器至以下新版本。',WEB_NAME)}</p>
<p>
<a class='alert-link' href="https://www.google.com/intl/zh-CN/chrome/browser/" target="_blank">(2)、${L:l(lan,"谷歌浏览器")}</a><br>
<a class='alert-link' href="http://www.firefox.com.cn/download/" target="_blank">(3)、${L:l(lan,"火狐浏览器")}</a><br>
</p>
<p>${L:l(lan,"HTML5时代已经来临，是时候换个浏览器了...")}</p>
</div>
<![endif]-->
<%if(false){%>
<header class="navbar navbar-static-top bk-nav font300">
    <div class="bk-menuBar clearfix">
        <div class="navbar-header">
            <a class="navbar-brand bk-nav-logo" href="${vip_domain}/">
                <img style="width: 157px;height: auto;margin-top:0.2rem;" src="${static_domain }/statics/images/winexlogo.png" alt="">
            </a>
            <ul class="nav navbar-nav navbar-left" id="bitNav">
                <li class="nav_mobile_close"></li>
                <li class="login_mobile_hide"><a class="prev_login_href downLinkPage"  href="javascript:void(0)" target="_self">${L:l(lan,'登录-top')}</a></li>
                <li class="login_mobile_hide"><a  class="downLinkPage" href="javascript:void(0)" target="_self">${L:l(lan,'注册-top')}</a></li>
                <li class="nav-left-tab"><a class="trade downLinkPage" href="javascript:void(0)"  target="_self">${L:l(lan,'币币交易中心') }</a></li>
                <%--<li class="nav-left-tab"><a class="trade" href="${vip_domain}/v2/trade/" target="new">${L:l(lan,'法币交易中心') }</a></li>--%>
                <%--<li class="nav-left-tab"><a class="trade" href="${vip_domain}/bw/trade/" target="new">${L:l(lan,'法币交易中心') }</a></li>--%>
            </ul>
        </div>
        <div class="nav_mobile_menu_1"></div>
        <div class="nav_mobile_menu"></div>
        <ul class="nav_mobile_langs">
            <li class="nav_mobile_close_1"></li>
            <li><a class="en" href="javascript:vip.setLan('en');" target="_self"><i class="g-ico left_ico en"></i>English</a></li>
            <li><a class="hk" href="javascript:vip.setLan('hk');" target="_self"><i class="g-ico hk"></i>繁體中文</a></li>
            <li><a class="cn" href="javascript:vip.setLan('cn');" target="_self"><i class="g-ico cn"></i>简体中文</a></li>
        </ul>
        <nav class="navbar-collapse bk-navbar collapse nav-right headerbar" aria-expanded="false" id="bitMenu">
            <ul class="nav navbar-nav navbar-right" style="margin-right:0;">
                <li class="nav-right-tab dropdown">
                    <a href="javascript:void(0)" class="btn downLinkPage">
                        <span class="iconfont icon-gonggao-moren"></span>
                        <i>${L:l(lan,'公告111')}</i>
                    </a>
                </li>
                <li class="nologin mr10"><a class="btn btn-login sig_btn prev_login_href downLinkPage" prevhref="" href="javascript:void(0)" target="_self">${L:l(lan,'登录-top')}</a></li>
                <li class="nologin mr10"><a class="btn btn-login account-btn downLinkPage" href="javascript:void(0)" target="_self">${L:l(lan,'注册-top')}</a></li>

                <!--    Login after login    -->

                <li class="logined nav-right-tab dropdown">
                    <a  href="javascript:void(0)" class="btn downLinkPage">
                        <span class="iconfont icon-zichanguanli-moren"></span>
                        <i>${L:l(lan,'资产管理')}</i>
                        <!-- <span class="topnext"></span> -->
                    </a>
                    <ul class="dropdown-menu min-menu">
                        <li class="tab-logout">
                            <a class="downLinkPage" href="javascript:void(0)">${L:l(lan,'我的钱包')}</a>
                        </li>
                        <li class="tab-logout">
                            <a class="downLinkPage" href="javascript:void(0)">${L:l(lan,'交易帐户')}</a>
                        </li>
                    </ul>
                </li>
                <li class="logined nav-right-tab dropdown">
                    <a href="javascript:void(0)" class="btn downLinkPage">
                        <span class="iconfont icon-weituoguanli-yiru"></span>
                        <i>${L:l(lan,'委托管理')}</i>
                    </a>
                    <ul class="dropdown-menu min-menu" role="menu">
                        <li class="tab-logout">
                            <a class="downLinkPage" id="historyList" href="javascript:void(0)">${L:l(lan,'当前委托')}</a>
                        </li>
                        <li class="tab-logout">
                            <a class="downLinkPage" id="historyList" href="javascript:void(0)">${L:l(lan,'导航-历史委托')}</a>
                        </li>
                        <li class="tab-logout">
                            <a class="downLinkPage" href="javascript:void(0)">${L:l(lan,'导航-历史成交')}</a>
                        </li>
                    </ul>
                </li>
                <li class="logined nav-right-tab dropdown" id="userAccount">
                    <a href="javascript:void(0)" class="btn font300 downLinkPage">
                        <i class="iconfont icon-zhanghu-moren"></i>
                        <span class="topnext"></span>
                    </a>
                    <ul class="dropdown-menu new-downmenu" role="menu">
                        <li class="big-menu">
                            <a class="downLinkPage" href="javascript:void(0)">
                                <h4>${L:l(lan,'导航-用户中心')}</h4>
                                <p id="M_userName" class="M_userName ft12"></p>
                                <span class="vip-grade"><span class="iconfont icon-liebiaojiantou-moren"></span></span>
                            </a>
                        </li>
                        <li class="big-menu">
                            <a class="downLinkPage" href="javascript:void(0)">
                                <h5 class="ft12">${L:l(lan,'一级导航-居右内容-用户名下拉菜单标签-5')}</h5>
                                <p>
                                    <span class="now-acc-cny">0.0000</span>
                                    <i class="legal_tender_unit"></i>
                                </p>
                            </a>
                        </li>
                        <li class="tab-logout"><a href="javascript:void(0)" target="_self">${L:l(lan,'一级导航-居右内容-用户名下拉菜单标签-1')}</a></li>
                    </ul>
                </li>
                <li class="nav-right-tab dropdown" id="setMoney">
                    <a href="javascript:void(0)" class="btn downLinkPage">
                        <!-- <i class="iconfont2 icon-renminbi"></i> -->
                        <p id="moneyname" class="texthead"></p>
                        <span class="topnext"></span>
                    </a>
                    <ul class="dropdown-menu" id="drowCurrency" role="menu">
                        <li>
                            <a href="javascript:vip.setCurrency('USD')">
                                <i class="iconfont2 icon-meiyuan-copy"></i> <span>${L:l(lan,'美元')}</span>

                            </a>
                        </li>
                        <li>
                            <a href="javascript:vip.setCurrency('CNY')">
                                <i class="iconfont2 icon-renminbi"></i> <span>${L:l(lan,'人民币')}</span>

                            </a>
                        </li>
                        <li>
                            <a href="javascript:vip.setCurrency('EUR')">
                                <i class="iconfont2 icon-icon"></i>  <span>${L:l(lan,'欧元')}</span>

                            </a>
                        </li>
                        <li>
                            <a href="javascript:vip.setCurrency('GBP')">
                                <i class="iconfont2 icon-yingbang_pounds"></i>  <span>${L:l(lan,'英镑')}</span>
                            </a>
                        </li>
                        <li>
                            <a href="javascript:vip.setCurrency('AUD')">
                                <i class="iconfont2 icon-aoyuan font20"></i> <span>${L:l(lan,'澳元')}</span>
                            </a>
                        </li>
                    </ul>
                </li>
                <li class="nav-right-tab dropdown langtab">
                    <a href="javascript:void(0)" class="btn downLinkPage">
                        <span id="lanWord" class="lanWord g-ico"></span>
                        <p id="lantxt" class="texthead"></p>
                        <i class="topnext"></i>
                    </a>
                    <ul class="dropdown-menu langs_enhk" role="menu">
                        <li><a class="en" href="javascript:vip.setLan('en');" target="_self"><i class="g-ico en"></i>English</a></li>
                        <li><a class="hk" href="javascript:vip.setLan('hk');" target="_self"><i class="g-ico hk"></i>繁體中文</a></li>
                        <li><a class="cn" href="javascript:vip.setLan('cn');" target="_self"><i class="g-ico cn"></i>简体中文</a></li>
                    </ul>
                </li>
                <!--    登录后导航二 END   -->
            </ul>
        </nav>
    </div>
</header>
<script type="text/x-tmpl" id="tmpl-topFundsDetail">
        {% for (var i = 0; i <= rs.length -1; i++) { %}
            <tr>
            <td><span class="text-depgray">{%=rs[i].propTag%}</span></td>
            <td>{%=rs[i].balance%}</td>
            <td>{%=rs[i].freeze%}</td>
            </tr>
        {% } %}
    </script>
<script>
    $(function () {
        var url = window.location.pathname;
        if (url.indexOf("/trade") != -1) {
            $("#bitNav > li > a").removeClass("active");
            $("#bitNav > li > a.trade").addClass("active");
        }
        if (url.indexOf("/msg") != -1) {
            $("#bitNav > li > a").removeClass("active");
            $("#bitNav > li > a.msg").addClass("active");
            // $(".topnext").addClass("topnextactive");
        }
        if (url.indexOf("/msg/appdownload") != -1) {
            $("#bitNav > li > a").removeClass("active");
            $("#bitNav > li > a.appdownload").addClass("active");
            // $(".topnext").addClass("topnextactive");
        }
        if (url.indexOf("/msg/newslist") != -1 || url.indexOf("/msg/newsdetails") != -1) {
            $("#bitNav > li > a").removeClass("active");
            $("#bitNav > li > a.newslist").addClass("active");
            // $(".topnext").addClass("topnextactive");
        }
        if (url.indexOf("/manage/loan") != -1 || url.indexOf("/manage/loan/out") != -1) {
            $("#bitNav > li > a").removeClass("active");
            $("#bitNav > li > a.loan").addClass("active");
        }
        if (url == "/") {
            $("#bitNav > li > a").removeClass("active");
            $("#bitNav > li > a.index").addClass("active");
        }
        if (url.indexOf("/vote") != -1) {
            $("#bitNav > li > a").removeClass("active");
            $("#bitNav > li > a.vote").addClass("active");
        }
        $('.asset').on('click', function () {
            window.top.location.href = "${vip_domain }/manage/account/";
        })
        $('.user').on('click', function () {
            window.top.location.href = "${vip_domain }/manage/";
        })

        $(".prev_login_href").on("click",function () {
            var prevherfss = window.location.pathname;
            $.cookie("prevhref",prevherfss,{path:"/"});
        })
    })
    vote_btn();//投票入口
    function vote_btn() {
        return;
        $.ajax({   //投票入口
            url:"${vip_domain }/vote/activity",
            type:"GET",
            dataType:"json",
            success:function(data){
                if( data.isSuc ){
                    var datas = data.datas;
                    $(".vote_status").attr("state",datas.state);
                    $(".vote_status").attr("activityId",datas.activityId);
                    if( datas.state == 1 || datas.state == 2 || datas.state == 3 ){
                        $(".vote_status").attr("href",datas.url+"/");
                        $(".vote_status").css("display",'inline-block');
                    }
                    else{
                        $(".vote_status").hide();
                    }
                }
                else{
                    $(".vote_status").hide();
                }

            },
            error: function (err) {
                console.log(err);
            }
        })
    }

</script>
<script type="text/javascript">
    // require(['module_method'], function (method) {
    //     $.ajax({
    //         type: "GET",
    //         url: "/manage/level/getLevelInfo",
    //         dataType: 'json',
    //         success: function (data) {
    //             if (data.isSuc) {
    //                 var $curRate = $('#currentRate,#userRate');
    //                 var datar = data.datas;
    //                 $curRate.html(datar.currentRate);
    //             }
    //         }
    //     });
    // });

    $(".navFirst").on("click",function(){
        window.location.href = "/v2/manage/level"
    })

    $("#historyList").on("click",function(){
        sessionStorage.clear()
    })
    $(".navFirst").on("click",function(){
        window.location.href = "/v2/manage/level"
    })

    // var langhtml = "";
    // 	if( LANG == "hk" ){
    // 		langhtml = '<li><a class="en" href= "javascript:vip.setLan(\'en\');" target="_self"> <i class="g-ico en"></i>English</a></li>'+
    // 					'<li><a class="hk" href= "javascript:vip.setLan(\'cn\');" target="_self"><i class="g-ico cn"></i>简体中文</a></li>';
    //     }
    //    else if( LANG == "cn" ){
    //        langhtml = '<li><a class="en" href= "javascript:vip.setLan(\'en\');" target="_self"><i class="g-ico en"></i>English</a></li>'+
    // 					'<li><a class="hk" href= "javascript:vip.setLan(\'hk\');" target="_self"><i class="g-ico hk"></i>繁體中文</a></li>';

    //     }
    //     else{
    //         $(".account-btn").css("width","150px")
    // 		langhtml = '<li><a class="en" href= "javascript:vip.setLan(\'cn\');" target="_self"><i class="g-ico cn"></i>简体中文</a></li>'+
    // 					'<li><a class="hk" href= "javascript:vip.setLan(\'hk\');" target="_self"><i class="g-ico hk"></i>繁體中文</a></li>';
    //     }
    // 	$(".langs_enhk").html(langhtml);

</script>
<script type="text/x-tmpl" id="tmpl-topFundsDetail">
        {% for (var i = 0; i <= rs.length -1; i++) { %}
            <tr>
            <td><span class="text-depgray">{%=rs[i].propTag%}</span></td>
            <td>{%=rs[i].balance%}</td>
            <td>{%=rs[i].freeze%}</td>
            </tr>
	    {% } %}
    </script>
<%}%>

<header class="clearfix hd-wp">
    <h1 class="logo_wp fl">
        <a class="" href="${vip_domain}/">
            <img style="width: 157px;height: auto;margin-top:0.2rem;" src="${static_domain }/statics/images/winexlogo.png" alt="">
        </a>
    </h1>
    <!--end logo_wp-->
    <div class="lng_wp fr">
        <strong class="setMoney-ct" id="moneynames"></strong><span class="gq" id="gq"></span>
        <%--<em class="menu-wp" id="logined-cl"></em>--%>
    </div>
    <!--end lng_wp-->

    <div class="setMoney-mu-wp" id="moneynameswp">
        <em class="opt-wp close-wp"></em>
        <ul style="margin-top: -4.2rem">
            <li>
                <a href="javascript:vip.setCurrency('USD')">
                     <i class="iconfont2 icon-meiyuan-copy"></i><em class="th">${L:l(lan,'美元')}</em>
                </a>
            </li>
            <li>
                <a href="javascript:vip.setCurrency('CNY')">
                    <i class="iconfont2 icon-renminbi"></i><em class="th">${L:l(lan,'人民币')}</em>
                </a>
            </li>
            <li>
                <a href="javascript:vip.setCurrency('EUR')">
                    <i class="iconfont2 icon-icon"></i><em class="th">${L:l(lan,'欧元')}</em>
                </a>
            </li>
            <li>
                <a href="javascript:vip.setCurrency('GBP')">
                    <i class="iconfont2 icon-yingbang_pounds"></i><em class="th">${L:l(lan,'英镑')}</em>
                </a>
            </li>
            <li>
                <a href="javascript:vip.setCurrency('AUD')">
                   <i class="iconfont2 icon-aoyuan font20"></i><em class="th">${L:l(lan,'澳元')}</em>
                </a>
            </li>
        </ul>
    </div>
    <!--end setMoney-mu-wp-->

    <div class="setMoney-mu-wp sp0" id="lng">
        <em class="opt-wp close-wp"></em>
        <ul style="margin-top: -2.5rem">
            <li><a class="en" href="javascript:vip.setLan('en');" target="_self"><i class="g-ico en"></i><em class="th">ENGLISH</em></a></li>

            <%--<li><a class="hk" href="javascript:vip.setLan('hk');" target="_self"><i class="g-ico hk"></i><em class="th">繁體中文</em></a></li>--%>
            <li><a class="cn" href="javascript:vip.setLan('cn');" target="_self"><i class="g-ico cn"></i><em class="th">简体中文</em></a></li>
            <li><a class="jp" href="javascript:vip.setLan('jp');" target="_self"><i class="g-ico jp"></i><em class="th">日本語</em></a></li>
            <li><a class="ko" href="javascript:vip.setLan('kr');" target="_self"><i class="g-ico ko"></i><em class="th">한국어</em></a></li>
        </ul>
    </div>

    <div class="setMoney-mu-wp setMoney-mu-wpSpicel sp0" id="logined-wp">
        <em class="opt-wp close-wp"></em>
        <ul class="mu-wp xp" style="margin-top: -4rem">
            <li class="ac"><a class="trade downLinkPage" title="${L:l(lan,'币币交易中心') }" href="javascript:void(0)" target="_self">${L:l(lan,'币币交易中心') }</a></li>
            <%--<li><a class="trade" href="${vip_domain}/v2/trade/" target="new">${L:l(lan,'法币交易中心') }</a></li>--%>

            <!--no login-->
            <li class="nologin"><a class="downLinkPages" href="javascript:void(0)">${L:l(lan,'公告111')}</a></li>
            <li class="nologin"><a class="downLinkPages" href="javascript:void(0)" target="_self">${L:l(lan,'登录-top')}</a></li>
            <li class="nologin"><a class="downLinkPages" href="javascript:void(0)" target="_self">${L:l(lan,'注册-top')}</a></li>
            <!--login-->
            <li class="logined"><a class="downLinkPages" href="javascript:void(0)">${L:l(lan,'资产管理')}</a></li>
            <li class="logined"><a class="downLinkPages" href="javascript:void(0)">${L:l(lan,'委托管理')}</a></li>
            <li class="logined"><a class="downLinkPages" href="javascript:void(0)">${L:l(lan,'导航-用户中心')}</a></li>
            <li class="logined"><a class="downLinkPages" href="javascript:void(0)">${L:l(lan,"退出登录") }</a></li>
        </ul>
    </div>

</header>
<script>
    (function(){

        $(".setMoney-mu-wpSpicel").each(function(){
            var _pself = $(this).find("li").eq(0);
            $(this).find("li").on("click", function(){
                var _this = $(this); var browser = {
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


                // _pself.removeClass("ac");
                // _this.addClass("ac");
                //
                // _pself = _this;
                //
                // eval(_this.find("a").attr("href"));
                $("#logined-wp").hide();

            });
        });

        $("#moneynames").on("click", function(){
            $("#moneynameswp").show();
        });

        $(".setMoney-mu-wp .close-wp").on("click", function(){
            $(this).parent().hide();
        });

        $("#gq").on("click", function(){
            $("#lng").show();
        })

        $("#logined-cl").on("click", function(){
            $("#logined-wp").show();
        })

    })()
</script>