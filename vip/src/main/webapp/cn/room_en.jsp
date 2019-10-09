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
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<meta name="viewport" content="width=device-width,height=device-height,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
<link rel="stylesheet" href="https://at.alicdn.com/t/font_6cpm9gh0h82rzfr.css">
<link rel="stylesheet" id="css" href="${static_domain }/statics/css/icocss/index.css?V${CH_VERSON }">
    <script>
        var isphone = false;//是否为手机
        if(JuaBox.isMobile()){
            JuaBox.mobileFontSize();
            isphone = true;
            document.getElementById('css').setAttribute("href","${static_domain }/statics/css/icocss/index-mobile.css?V${CH_VERSON }");
            $("<link>").attr({ rel: "stylesheet",type: "text/css",href: "${static_domain }/statics/css/mobile/top_foot_mobile.css?V${CH_VERSON }"}).appendTo("head");
        }
        
    </script>
<body>
<jsp:include page="/common/top.jsp" />
<section class="float_vote clearfix">
    <div class="vote_left_text clearfix">
        <img src="${static_domain }/statics/img/icoimgs/vote_home_bg.svg" />
        <div class="clearfix">
            <h5>New coin voting is underway</h5>
            <p>We have prepared 20 candidates for your participation!</p>
        </div>
    </div>
    <div class="vote_right_text clearfix">
        <div><a class="vote_home_href" href="####">Vote Now</a></div>
        <div class="vote_home_close"></div>
    </div>
</section>
    <div class="container_index">
        <div class="top" id="top">
            <div class="content">
                <div class="text-bitglobal">BTCWINEX</div>
                <div class="text-memo">The First Digital Assets Trading Platform Supporting Gold Exchange</div>
            </div>

            <div class="count-down">
                <div class="button">
                    <div class="btn login_btn" myHref = "${vip_domain}/login/"></div>
                    <div class="btn zhuche_btn" myHref = "${vip_domain}/register/">Open Account</div>
                </div>
            </div>
            <div class="content2">
                <div class="text-about">About Btcwinex Trading Platform</div>
                <div class="text-memo2">By breaking gold and digital asset free exchange barriers, Btcwinex is devoted to bridge the gap between traditional assets trading market and digital assets trading market so as to realize the real digitalization of all assets globally.</div>
            </div>
            <div class="table-list">
                <div class="item">
                    <img src="${static_domain }/statics/img/icoimgs/icon_xinren.png" width=35px height=42px alt="">
                    <span class="text1">Trust and Security</span>
                    <span class="text2">Cold storage, tamper-proof, and multiple encryption</span>
                </div>
                <div class="item">
                    <img src="${static_domain }/statics/img/icoimgs/icon_quanqiu.png" width=49px height=40px alt="">
                     <span class="text1">Multilingual and Global Coverage</span>
                    <span class="text2">with our business spread across 100+ countries around the globe</span>
                </div>
                <div class="item">
                    <img src="${static_domain }/statics/img/icoimgs/icon_zhichi.png" width=43px height=40px alt="">
                    <span class="text1">Multi-currency Support</span>
                    <span class="text2">with more than ten different cryptocurrency trading markets</span>
                </div>
                <div class="item">
                    <img src="${static_domain }/statics/img/icoimgs/icon_quanpingtai.png"  width=51px height=40px alt="">
                    <span class="text1">All Platform Access</span>
                    <span class="text2">Web, iOS and Android</span>
                </div>
            </div>
        </div>
        <div class="second" id="second">
            <div class="content">
                <p>Our Features</p>
                <div class="content-box">
                    <div class="item">
                        <img src="${static_domain }/statics/img/icoimgs/secend1.png" width=143px height=127px alt="">
                        <span class="text1">Margin Trading</span>
                        <span class="text2">With their assets on the platform as collaterals, Btcwinex allows trading participants to borrow funds from other participants on the platform or the platform itself so as to enable users to trade with leverage.</span>
                    </div>
                    <div class="item">
                        <img src="${static_domain }/statics/img/icoimgs/secend2.png" width=123px height=127px alt="">
                        <span class="text1">Physical Gold Exchange</span>
                        <span class="text2">Users with private key for gold service can trade gold with their digital assets on the platform. Btcwinex will provide gold logistics, distribution and storage services with our users. </span>
                    </div>
                    <div class="item">
                        <img src="${static_domain }/statics/img/icoimgs/secend3.png" width=84px height=114px style="margin-bottom:13px">
                        <span class="text1">Digital Identity</span>
                        <span class="text2">Users with private key for gold service can make complete anonymous transactions on the platform.</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="thrid" id="thrid">
            <div class="content">
                <div class="text1">About ABCDEF</div>
                <div class="text2">Issued by Btcwinex, Global Coin (ABCDEF) is an Ethereum-based blockchain digital asset with a total supply of 10 million.</div>
                <img src="${static_domain }/statics/img/icoimgs/icon_baipishu.png" alt="">
            </div>
        </div>

        <div class="four" id="four">
            <div class="content">
                <p>ABCDEF Repo and Profit-sharing Mechanism</p>
                <div class="pic" id="picbox">
                    <div class="div1">50 % of transaction fees for all currencies</div>
                    <div class="div2">promote the ABCDEF market prices to rise steadily</div>
                    <div class="div3">Seal</div>
                    <span>Full of dividend in real time</span>
                    <canvas id="myCanvas" width="882px" height="403px"></canvas>
                </div>
                
                <div class="text">
                    Btcwinex will take 50% out of its profits (including transaction fee and margin service fee) to repurchase the ABCDEF in circulation in real time, and will continue to repurchase until all the 10 million ABCDEF has been bought out. The repurchase process is transparent and all the repurchased ABCDEF will be stored in a fixed address. Users can check all the records in our blockchain explorer in real time. In addition, Btcwinex will take 1% out of its repurchased ABCDEF annually to share with all the users based on the quantity of ABCDEF held in their accounts.
                </div>
                <div class="text1">
                    
                </div>
            </div>
        </div>

        <div class="five" id="five">
            <div class="content">
                <div class="title">ABCDEF Value Transformation</div>
                <div class="pic">
                    <canvas id="myCanvas2" width="713px" height="192px"></canvas>
                </div>
                <div class="memo">When the gold exchange service is online, ABCDEF smart contract will activate the gold private key exchange mechanism. Btcwinex will sell the repurchased ABCDEF at market price to those who need physical gold exchange service. With 1000 ABCDEF, they will obtain the private key for our gold VIP service so as to realize the value transformation of ABCDEF.</div>
            </div>
        </div>

        <div class="six" id="six">
            <div class="content">
                <div class="text1">Digital Assets and Physical Gold Exchange</div>
                <div class="center">
                    <img src="${static_domain }/statics/img/icoimgs/gold-ingots.png" alt="" class="img1">
                    <img src="${static_domain }/statics/img/icoimgs/duihuan.png" alt="" class="img2">
                    <img src="${static_domain }/statics/img/icoimgs/number-bi.png" alt="" class="img3">
                </div>
                <div class="text2">Btcwinex’s physical gold exchange service is tailored to the high-net-worth individuals of digital assets. With our gold private key, you can not only buy, store gold, make gold investment, but even withdraw the physical gold to your home.</div>
            </div>
        </div>
        

        <div class="seven" id="seven">
            <div class="content">
                <div class="title">Latest Updates</div>
                <div class="bottom">
                    <div class="item face">
                        <i class="icon"></i>
                        <span class="text">Facebook</span>
                    </div>
                    <div class="item twitter">
                        <i class="icon"></i>
                        <span class="text">Twitter</span>
                    </div>
                    <div class="item weixin">
                        <i><div class="qrcode"></div></i>
                        <span class="text">WeChat Account</span>
                    </div>
                    <div class="item qq">
                        <i><div class="qrcode"></div></i>
                        <span class="text">Official QQ</span>
                    </div>
                    <div class="item email">
                        <i class="icon"></i>
                        <span class="text">Official Email</span>
                    </div>
                    <div class="item weibo">
                        <i class="icon"></i>
                        <span class="text">Sina Weibo</span>
                    </div>
                </div>
            </div>
        </div>
        <jsp:include page="/common/foot.jsp" />
        <!-- <div class="eight">
            Copyright © 2017 BTCWINEX. All Rights Reserved
        </div> -->
    </div>
    
    <script type="text/javascript" src="${static_domain }/statics/js/icojs/Car.js?V${CH_VERSON }"></script>
    <script type="text/javascript" src="${static_domain }/statics/js/icojs/CarControll.js?V${CH_VERSON }"></script>
    <script type="text/javascript" src="${static_domain }/statics/js/icojs/Box.js?V${CH_VERSON }"></script>
    <script type="text/javascript" src="${static_domain }/statics/js/icojs/BoxControll.js?V${CH_VERSON }"></script>
    <script type="text/javascript" src="${static_domain }/statics/js/icojs/index.js?V${CH_VERSON }"></script>
    <script type="text/javascript">
        var cnzz_protocol = (("https:" == document.location.protocol) ? " https://" : " http://");
        document.write(unescape("<div style='display:none'>%3Cspan id='cnzz_stat_icon_1262958896'%3E%3C/span%3E%3Cscript src='" + cnzz_protocol + "s19.cnzz.com/z_stat.php%3Fid%3D1262958896%26show%3Dpic' type='text/javascript'%3E%3C/script%3E</div>"));
    </script>
    <script>
      (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
      (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
      m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
      })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

      ga('create', 'UA-102891487-1', 'auto');
      ga('send', 'pageview');

    </script>
</body>
</html>