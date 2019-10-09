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
    <div class="container_index">
        <div class="top" id="top">
            <div class="content">
                <div class="text-bitglobal">BTCWINEX</div>
                <div class="text-memo">全球首家支持黄金兑换的数字资产交易平台</div>
            </div>

            <div class="count-down">
                <div class="button">
                    <div class="btn login_btn" myHref = "${vip_domain}/login/"></div>
                    <div class="btn zhuche_btn" myHref = "${vip_domain}/register/">立即注册</div>
                </div>
            </div>

            <div class="content2">
                <div class="text-about">关于btcwinex交易平台</div>
                <div class="text-memo2">btcwinex致力于建立传统资产交易市场与数字资产交易市场的纽带，通过打通黄金与数字资产的互换壁垒，布局全球资产数字化发展，真正实现资产数字化的全球应用。</div>
            </div>
            <div class="table-list">
                <div class="item">
                    <img src="${static_domain }/statics/img/icoimgs/icon_xinren.png" width=35px height=42px alt="">
                    <span class="text1">信任与安全</span>
                    <span class="text2">冷储存、防篡改、和多重加密</span>
                </div>
                <div class="item">
                    <img src="${static_domain }/statics/img/icoimgs/icon_quanqiu.png" width=49px height=40px alt="">
                    <span class="text1">全球化多语言</span>
                    <span class="text2">业务覆盖全世界一百多个国家</span>
                </div>
                <div class="item">
                    <img src="${static_domain }/statics/img/icoimgs/icon_zhichi.png" width=43px height=40px alt="">
                    <span class="text1">支持多币种</span>
                    <span class="text2">支持十几种加密数字货币的交易</span>
                </div>
                <div class="item">
                    <img src="${static_domain }/statics/img/icoimgs/icon_quanpingtai.png"  width=51px height=40px alt="">
                    <span class="text1">全平台覆盖</span>
                    <span class="text2">支持Web、iOS、Android</span>
                </div>
            </div>
        </div>
        <div class="second" id="second">
            <div class="content">
                <p>我们的特点</p>
                <div class="content-box">
                    <div class="item">
                        <img src="${static_domain }/statics/img/icoimgs/secend1.png" width=143px height=127px alt="">
                        <span class="text1">融资融币</span>
                        <span class="text2">以个人在平台的资产作为担保，向交易平台或其他用户借入资金，可以利用小额的资金来进行数倍于原始金额的投资。</span>
                    </div>
                    <div class="item">
                        <img src="${static_domain }/statics/img/icoimgs/secend2.png" width=123px height=127px alt="">
                        <span class="text1">实体黄金兑换</span>
                        <span class="text2">拥有黄金服务私钥的用户可以充值数字资产进行黄金买卖，我们提供完善的物流配送服务与黄金储存服务。</span>
                    </div>
                    <div class="item">
                        <img src="${static_domain }/statics/img/icoimgs/secend3.png" width=84px height=114px style="margin-bottom:13px">
                        <span class="text1">数字身份</span>
                        <span class="text2">拥有黄金服务私钥的用户具有完全的匿名特性，用户使用私钥进行登录交易，不存留任何用户信息。</span>
                    </div>
                </div>
            </div>
        </div>


        <div class="thrid" id="thrid">
            <div class="content">
                <div class="text1">关于ABCDEF</div>
                <div class="text2">btcwinex发行的数字资产为Global Coin，简称“ABCDEF”。ABCDEF是基于P2P加密数字算法编程平台以太坊（Ethereum）发行的去中心化的区块链数字资产，发行总量为1000万个。</div>
                <img src="${static_domain }/statics/img/icoimgs/icon_baipishu.png" alt="">
            </div>
        </div>

        <div class="four" id="four">
            <div class="content">
                <p>ABCDEF回购机制 价值分红</p>
                <div class="pic" id="picbox">
                    <div class="div1">全币种交易的50%手续费</div>
                    <div class="div2">促进ABCDEF市场价格持续上升</div>
                    <div class="div3">封存</div>
                    <span>实时全额分红</span>
                    <canvas id="myCanvas" width="882px" height="403px"></canvas>
                </div>
                
                <div class="text">
                    我们将拿出平台每一笔利润的50%（交易手续费、融资融币服务费）实时回购ABCDEF。我们将一直回购，直至1000万总量ABCDEF全部回购完毕为止；回购的ABCDEF封存在固定的私钥地址，用户可通过区块链浏览器进行查询，确保公开透明。 btcwinex每年定期从回购账户中拿出发行总量的1%，即10万ABCDEF分红给当期持有ABCDEF的用户（不足10万按照实际回购数量进行分红）。每个持有ABCDEF的用户都会按照持有比例获得相应分红。
                </div>
                <div class="text1">
                    
                </div>
            </div>
        </div>

        <div class="five" id="five">
            <div class="content">
                <div class="title">ABCDEF价值转化</div>
                <div class="pic">
                    <canvas id="myCanvas2" width="713px" height="192px"></canvas>
                </div>
                <div class="memo">当实体黄金兑换业务上线后，ABCDEF智能合约将开启黄金私钥兑换机制。btcwinex会将回购的ABCDEF按市场价格，出售给需要实体黄金兑换服务的客户，客户花费1000个ABCDEF即可在智能合约中获得黄金VIP服务私钥，实现价值转化。</div>
            </div>
        </div>

        <div class="six" id="six">
            <div class="content">
                <div class="text1">数字资产兑换实体黄金</div>
                <div class="center">
                    <img src="${static_domain }/statics/img/icoimgs/gold-ingots.png" alt="" class="img1">
                    <img src="${static_domain }/statics/img/icoimgs/duihuan.png" alt="" class="img2">
                    <img src="${static_domain }/statics/img/icoimgs/number-bi.png" alt="" class="img3">
                </div>
                <div class="text2">兑换实体黄金服务是针对数字资产领域亿万富翁人群定制的增值服务。如果您持有黄金私钥，即可在本平台购买、存储黄金，购买黄金理财，甚至将实体黄金提现到您的家中。</div>
            </div>
        </div>
        

        <div class="seven" id="seven">
            <div class="content">
                <div class="title">获取最新动态</div>
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
                        <span class="text">微信公众号</span>
                    </div>
                    <div class="item qq">
                        <i><div class="qrcode"></div></i>
                        <span class="text">官方QQ群</span>
                    </div>
                    <div class="item email">
                        <i class="icon"></i>
                        <span class="text">官方邮箱</span>
                    </div>
                    <div class="item weibo">
                        <i class="icon"></i>
                        <span class="text">新浪微博</span>
                    </div>
                </div>
            </div>
        </div>
        <jsp:include page="/common/foot.jsp" />
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