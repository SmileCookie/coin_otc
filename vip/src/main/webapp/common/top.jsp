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
            background-color: rgba(47, 50, 63, .9);
        }
        .navbar-collapse .nav>li.logined {
            margin-top: 10px;
            margin-left: 0;
            position: relative;
        }
        .moneyControl:hover .dropdown-menu{
            display: block;
        }
        .moneyControl:hover .dropdown-menu li a{
            font-size: 12px;
        }
        .moneyControl:hover .btn{
            color: #fff;
        }
        .navbar-collapse .nav .moneyControl .btn:hover{
            color: #fff;
        }
        #setMoney > a:hover{
            color: #ffffff;
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
    <header class="navbar navbar-static-top bk-nav font300" style="position:absolute;left: 0;top: 0;">
        <div class="bk-menuBar clearfix">
            <div class="navbar-header headerUp">
                <a class="navbar-brand"  href="${vip_domain}/" style="position:absolute;left: 0;top: 4px;">
                    <img style="width: 157px;height: auto;" src="${static_domain }/statics/images/winexlogo.png" alt="">
                </a>
                <ul class="nav navbar-nav navbar-left" id="bitNav">
                    <li class="nav_mobile_close"></li>
                    <li class="login_mobile_hide"><a class="prev_login_href"  href="${vip_domain}/bw/login" target="_self">${L:l(lan,'登录-top')}</a></li>
                    <li class="login_mobile_hide"><a  href="${vip_domain}/signup" target="_self">${L:l(lan,'注册-top')}</a></li>
                    <%--<li class="nav-left-tab"><a class="trade" href="${vip_domain}/v2/margin/trade" target="new">${L:l(lan,'合约交易中心') }</a></li>--%>
                    <li class="nav-left-tab"><a title="${L:l(lan,'币币交易中心') }" class="trade" href="${vip_domain}/bw/trade/" target="_self">${L:l(lan,'币币交易中心') }</a></li>
                    <li class="nav-left-tab hots"><a class="trade" href="${vip_domain}/bw/multitrade"  target="_self">${L:l(lan,'多屏看板')}</a></li>
<%--                    <li class="nav-left-tab"><a class="trade" href="${vip_domain}/bw/announcements" target="_self">${L:l(lan,'公告111')}</a></li>--%>
<%--                    <li class="nav-left-tab"><a class="trade" href="${vip_domain}/bw/news"  target="_self">${L:l(lan,'新闻')}</a></li>--%>
<%--                    todo--%>
                    <li class="nav-left-tab"><a class="trade" href="${vip_domain}/bw/money" target="_self">${L:l(lan,'理财')}</a></li>

                    <li class="nav-left-tab"><a class="trade" href="${vip_domain}/otc/trade/" target="_self">${L:l(lan,'法币交易') }</a></li>
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
                <%--<li class="nav-right-tab dropdown">--%>
                        <%--<a href="${vip_domain }/v2/announcements" class="btn">--%>
                        <%--<span class="iconfont icon-gonggao-moren"></span>--%>
                        <%--<i>${L:l(lan,'公告111')}</i>--%>
                        <%--</a>--%>
                    <%--</li>--%>


                    <%--  vip 商家 --%>
<%--                    <li class="nologin nav-right-tab dropdown" id="vip_store">--%>

<%--                    </li>--%>

                    <%-- 新闻 公告 --%>
                    <li class="nologin nav-right-tab dropdown" id="userAccount">
                        <a href="javascript:void(0)" class="btn font300">
                            <i class="iconfont icon-gonggao-moren"></i>
                            <span class="topnext"></span>
                        </a>
                        <ul class="dropdown-menu min-menu" role="menu" style="width: 120px;">
                            <li class="tab-logout" style="text-align: center;">
                                <a style="font-size: 12px;" href="${vip_domain}/bw/news">${L:l(lan,'新闻')}</a>
                            </li>
                            <li class="tab-logout" style="text-align: center;">
                                <a style="font-size: 12px;"   href="${vip_domain}/bw/announcements">${L:l(lan,'公告111')}</a>
                            </li>
                        </ul>
                    </li>




                    <li class="nologin mr10"><a class="btn btn-login sig_btn prev_login_href" prevhref="" href="${vip_domain}/bw/login/" target="_self"><span>${L:l(lan,'登录-top')}</span></a></li>
                    <li class="nologin mr10"><a class="btn btn-login account-btn" href="${vip_domain}/bw/signup/" target="_self"><span>${L:l(lan,'注册-top')}</span></a></li>

                    <!--    Login after login    -->

                    <%--  vip 商家 --%>
                    <li class="logined nav-right-tab dropdown" id="vip_store">

                    </li>

                    <%-- 新闻 公告 --%>
                    <li class="logined nav-right-tab dropdown" id="userAccount">
                        <a href="javascript:void(0)" class="btn font300">
                            <i class="iconfont icon-gonggao-moren"></i>
                            <span class="topnext"></span>
                        </a>
                        <ul class="dropdown-menu min-menu" role="menu" style="width: 120px;">
                            <li class="tab-logout" style="text-align: center;">
                                <a style="font-size: 12px;" href="${vip_domain}/bw/news">${L:l(lan,'新闻')}</a>
                            </li>
                            <li class="tab-logout" style="text-align: center;">
                                <a style="font-size: 12px;"   href="${vip_domain}/bw/announcements">${L:l(lan,'公告111')}</a>
                            </li>
                        </ul>
                    </li>


                    <li class="logined nav-right-tab dropdown moneyControl">
                        <a href="${vip_domain }/bw/manage/account/balance" class="btn ">
                            <span class="iconfont icon-zichanguanli-moren"></span>
                            <i>${L:l(lan,'资产')}</i>
                            <span class="topnext"></span>
                        </a>
                        <ul class="dropdown-menu min-menu">
                            <li class="tab-logout">
                                <a href="${vip_domain }/bw/manage/account/balance">${L:l(lan,'我的钱包')}</a>
                            </li>
                            <li class="tab-logout">
                                <a href="${vip_domain }/bw/manage/account/currency">${L:l(lan,'交易帐户')}</a>
                            </li>
                        </ul>
                    </li>
                    <li class="logined nav-right-tab dropdown">
                        <a href="${vip_domain}/bw/entrust/current" class="btn">
                            <span class="iconfont icon-weituoguanli-yiru"></span>
                            <i>${L:l(lan,'委托')}</i>
                        </a>
                        <ul class="dropdown-menu min-menu" role="menu">
                        <li class="tab-logout">
                                <a id="historyList" href="${vip_domain}/bw/entrust/list/">${L:l(lan,'当前委托')}</a>
                            </li>
                            <li class="tab-logout">
                                <a id="historyList" href="${vip_domain}/bw/entrust/list/">${L:l(lan,'导航-历史委托')}</a>
                            </li>
                            <li class="tab-logout">
                                <a href="${vip_domain}/bw/entrust/transrecord">${L:l(lan,'导航-历史成交')}</a>
                            </li>
                        </ul>
                    </li>
                    <li class="logined nav-right-tab dropdown" id="userAccount">
                        <a href="javascript:void(0)" class="btn font300">
                            <i class="iconfont icon-zhanghu-moren"></i>
                            <span class="topnext"></span>
                        </a>
                        <ul class="dropdown-menu new-downmenu" role="menu" style="width: 200px;">
                            <li class="big-menu" id="userInforId">
                              <a href="${vip_domain }/bw/mg/account">
                                <h4>${L:l(lan,'导航-用户中心')}</h4>
                                <p id="M_userName" class="M_userName ft12"></p>
                                <span class="vip-grade"><span class="iconfont icon-liebiaojiantou-moren" style="font-size: 12px;"></span></span>
                              </a>
                            </li>
                            <li class="big-menu">
                              <a href="${vip_domain }/bw/manage/account/">
                                <h5 style="font-size: 14px">${L:l(lan,'一级导航-居右内容-用户名下拉菜单标签-5')}:</h5>
                                <p style="font-size: 12px;color: #D4DCF0">
                                    <span class="now-acc-cny">0.0000</span>
                                    <i class="legal_tender_unit"></i>
                                </p>
                              </a>
                            </li>
                            <li class="tab-logout"><a style="font-size: 12px" href="${vip_domain}/login/logout/" target="_self">${L:l(lan,'一级导航-居右内容-用户名下拉菜单标签-1')}</a></li>
                        </ul>
                    </li>
                    <li class="nav-right-tab dropdown" id="setMoney">
                        <a href="javascript:void(0)" class="btn">
                            <i id="moneyicon" class="iconfont2 " style="font-size: 12px;"></i>
<%--                            <p id="moneyname" class="texthead" style="font-weight: 400"></p>--%>
                            <span class="topnext"></span>
                        </a>
                        <ul class="dropdown-menu new-downmoney" id="drowCurrency" role="menu" style="width: auto;padding: 10px 0;">
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
                                    <i style="font-size: 16px;padding-right: 26px" class="iconfont2 icon-aoyuan font20"></i> <span>${L:l(lan,'澳元')}</span>
                                </a>
                            </li>
                        </ul>
                    </li>                        
                    <li class="nav-right-tab dropdown langtab">
                        <a href="javascript:void(0)" class="btn">
                            <span id="lanWord" class="lanWord g-ico"></span>
<%--                            <p id="lantxt" class="texthead"></p>--%>
                            <i class="topnext"></i>
                        </a>
                        <ul class="dropdown-menu langs_enhk" role="menu" >
                            <%--<li><a class="en" href="javascript:vip.setLan('en');" target="_self"><i class="g-ico en"></i>English</a></li>--%>
                            <li><a class="en" href="javascript:vip.setLan('en');" target="_self"><i class="g-ico en"></i>English</a></li>
                            <%--<li><a class="hk" href="javascript:vip.setLan('hk');" target="_self"><i class="g-ico hk"></i>繁體中文</a></li>--%>
                            <li><a class="cn" href="javascript:vip.setLan('cn');" target="_self"><i class="g-ico cn"></i>简体中文</a></li>
                            <li><a class="jp" href="javascript:vip.setLan('jp');" target="_self"><i class="g-ico jp"></i>日本語</a></li>
                            <li><a class="ko" href="javascript:vip.setLan('kr');" target="_self"><i class="g-ico ko"></i>한국어</a></li>
                        </ul>
                    </li>
                    <!--    登录后导航二 END   -->
                </ul>
            </nav>
        </div>
    </header>
    <style>

        a.shangjia{
            color: #B3954C !important;
        }
        a.shangjia:hover{
            color: #B3954C !important;
        }
    </style>
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
            // 阿波罗计划
           // function test(){
            $.ajax({   //投票入口
                url:"${vip_domain }/manage/financial/userFinCenInfo",
                type:"GET",
                dataType:"json",
                success:function(data){
                    if(data.datas.authPayFlag == 2 || data.datas.authPayFlag == 3){
                        $("#userInforId").after("<li class=\"big-menu licai\">\n" +
                            "                              <a href=\"${vip_domain }/bw/manage/account/cmoney\">\n" +
                            "                                <h4 style=\"font-size: 14px\">${L:l(lan,'理财中心')}</h4>\n" +
                            "                              </a>\n" +
                            "                            </li>")
                    }

                },
                error: function (err) {
                    console.log(err);
                }
            })
            //}

            // $(".moneyControl").hover(function () {
            //     console.log(1123)
            // },
            // function () {
            //     console.log(23445)
            // })
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

        // var storeStatus,storeType; // 全局商家state

        // 判断vip商家
        function checkVip(){
            if ($.cookie("zloginStatus")!= 4){
                $.ajax({   //投票入口
                    url:"${vip_domain }/manage/auth/authenticationJson",
                    type:"GET",
                    dataType:"json",
                    success:function(data){
                        // console.log("=======vip"   + data);
                        if( data.isSuc){
                            var d = data.datas;
                            // console.log("=======vip" + d);
                            var storeStatus= d.storeStatus;
                            var storeType = d.storeType;
                            setVipDiv(storeStatus,storeType)
                        }
                        else{

                        }

                    },
                    error: function (err) {
                        console.log(err);
                    }
                })
            }

        }
        checkVip();

        function setVipDiv(status,type){
            var html = '';
            if( (status == 1 &&  type == 1) || (status !== 1 &&  type == 2) ){
                html = `<a onclick='goUserStatus()'  class='btn vip' id="vip-control"><img id="vip-img" style="width:14px;" src="${vip_domain }/html/images/control.png" alt=""/>
                           <i>${L:l(lan,'工作台')}</i>
                        </a>`
            }else{
                html = `<a onclick='goUserStatus()' class='btn shangjia'><img style="width:14px;" src="${vip_domain }/html/images/shangjia.png" alt=""/>
                           <i>${L:l(lan,'商家认证')}</i>
                        </a>`
            }
            $('#vip_store').html(html)
        }

        $('#vip_store').on('mouseover', function () {
            // console.log('========')
            $("#vip-img").attr('src','${vip_domain }/html/images/control_hover.png')
        })
        $('#vip_store').on('mouseout', function () {
            // console.log('========')
            $("#vip-img").attr('src','${vip_domain }/html/images/control.png')
        })

        //
        function goUserStatus(){
            $.ajax({   //投票入口
                url:"${vip_domain }/otcweb/web/v1/store/lockStatus",
                type:"GET",
                dataType:"json",
                success:function(data){
                    if( data.code == 200 ){
                            var msg = data.msg
                            if(data.code == 200){
                                window.location.href = '/otc/business'
                                // if(storeStatus == 1 && storeType == 1){
                                //     window.location.href = '/otc/business'
                                // }
                                // if(storeStatus !== 1 && storeType == 1){
                                //     window.location.href = '/otc/business'
                                // }
                                // if(storeStatus == 0 && storeType == 2){
                                //     window.location.href = '/bw/mg/cancleUserInforIng'
                                // }
                                // if(storeStatus == 1 && storeType == 2){
                                //     window.location.href = '/otc/business'
                                // }
                                // if(storeStatus == 2 && storeType == 2){
                                //     window.location.href = '/bw/mg/cancleUserInforBack'
                                // }
                                // if(storeStatus == -1){
                                //     window.location.href = '/otc/business'
                                // }
                                // if(storeStatus == 1){
                                //     window.location.href = `/bw/mg/cancleUserInfor`
                                // }
                                // if(storeStatus == 0){
                                //     window.location.href = `/bw/mg/cancleUserInforIng`
                                // }
                                // if(storeStatus == 2){
                                //     window.location.href = `/bw/mg/cancleUserInforBack`
                                // }
                            }else{
                                // optPop(() =>{},msg,{timer: 1500},true)
                            }

                    }
                    else{

                    }

                },
                error: function (err) {
                    console.log(err);
                }
            })
        }

        //检测浏览器种类
        // function explorType() {
        //     var Sys = {};
        //     var ua = navigator.userAgent.toLowerCase();
        //     var s;
        //     (s = ua.indexOf('edge') !== - 1 ? Sys.edge = 'edge' : ua.match(/rv:([\d.]+)\) like gecko/)) ? Sys.ie = s[1]:
        //         (s = ua.match(/msie ([\d.]+)/)) ? Sys.ie = s[1] :
        //             (s = ua.match(/firefox\/([\d.]+)/)) ? Sys.firefox = s[1] :
        //                 (s = ua.match(/chrome\/([\d.]+)/)) ? Sys.chrome = s[1] :
        //                     (s = ua.match(/opera.([\d.]+)/)) ? Sys.opera = s[1] :
        //                         (s = ua.match(/version\/([\d.]+).*safari/)) ? Sys.safari = s[1] : 0;
        //     return Sys;
        // }
        //
        // var _exporType = explorType()
        // console.log(Object.keys(_exporType)[0])
        // if(Object.keys(_exporType)[0] !== 'chrome'){
        //     if(Object.keys(_exporType)[0] == 'safari'){
        //         $('#bbyhNew_logos').css({
        //             marginTop:'5.5%'
        //         })
        //     }else{
        //         $('#bbyhNew_logos').css({
        //             marginTop:'21px'
        //         })
        //     }
        //
        // }
        //
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
            window.location.href = "/bw/manage/level"
        })

        $("#historyList").on("click",function(){
            sessionStorage.clear()
        })
        $(".navFirst").on("click",function(){
            window.location.href = "/bw/manage/level"
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