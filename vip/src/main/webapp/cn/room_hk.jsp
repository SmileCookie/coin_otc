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
                <h5>新幣投票活動正在進行中</h5>
                <p>我們准備了20種候選幣，期待您的參與！</p>
            </div>
        </div>
        <div class="vote_right_text clearfix">
            <div><a class="vote_home_href" href="####">立即參與</a></div>
            <div class="vote_home_close"></div>
        </div>
    </section>
    <div class="container_index">
        <div class="top" id="top">
            <div class="content">
                <div class="text-bitglobal">BTCWINEX</div>
                <div class="text-memo">全球首家支持黃金兌換的數字資產交易平台</div>
            </div>

            <div class="count-down">
                <div class="button">
                    <div class="btn login_btn" myHref = "${vip_domain}/login/"></div>
                    <div class="btn zhuche_btn" myHref = "${vip_domain}/register/">立即註冊</div>

                </div>
            </div>
            <div class="content2">
                <div class="text-about">關於Btcwinex交易平台</div>
                <div class="text-memo2">Btcwinex致力於建立傳統資產交易市場與數字資產交易市場的紐帶，通過打通黃金與數字資產的互換壁壘，布局全球資產數字化發展，真正實現資產數字化的全球應用。</div>
            </div>
            <div class="table-list">
                <div class="item">
                    <img src="${static_domain }/statics/img/icoimgs/icon_xinren.png" width=35px height=42px alt="">
                    <span class="text1">信任與安全</span>
                    <span class="text2">冷儲存、防篡改、和多重加密</span>
                </div>
                <div class="item">
                    <img src="${static_domain }/statics/img/icoimgs/icon_quanqiu.png" width=49px height=40px alt="">
                    <span class="text1">全球化多語言</span>
                    <span class="text2">業務覆蓋全世界一百多個國家</span>
                </div>
                <div class="item">
                    <img src="${static_domain }/statics/img/icoimgs/icon_zhichi.png" width=43px height=40px alt="">
                    <span class="text1">支持多幣種</span>
                    <span class="text2">支持十幾種加密數字貨幣的交易</span>
                </div>
                <div class="item">
                    <img src="${static_domain }/statics/img/icoimgs/icon_quanpingtai.png"  width=51px height=40px alt="">
                    <span class="text1">全平台覆蓋</span>
                    <span class="text2">支持Web、iOS、Android</span>
                </div>
            </div>
        </div>
        <div class="second" id="second">
            <div class="content">
                 <p>我們的特點</p>
                <div class="content-box">
                    <div class="item">
                        <img src="${static_domain }/statics/img/icoimgs/secend1.png" width=143px height=127px alt="">
                         <span class="text1">融資融幣</span>
                        <span class="text2">以個人在平台的資產作為擔保，向交易平台或其他用戶借入資金，可以利用小額的資金來進行數倍於原始金額的投資。</span>
                    </div>
                    <div class="item">
                        <img src="${static_domain }/statics/img/icoimgs/secend2.png" width=123px height=127px alt="">
                        <span class="text1">實體黃金兌換</span>
                        <span class="text2">擁有黃金服務私鑰的用戶可以充值數字資產進行黃金買賣，我們提供完善的物流配送服務與黃金儲存服務。</span>
                    </div>
                    <div class="item">
                        <img src="${static_domain }/statics/img/icoimgs/secend3.png" width=84px height=114px style="margin-bottom:13px">
                        <span class="text1">數字身份</span>
                        <span class="text2">擁有黃金服務私鑰的用戶具有完全的匿名特性，用戶使用私鑰進行登錄交易，不存留任何用戶信息。</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="thrid" id="thrid">
            <div class="content">
                <div class="text1">關於ABCDEF</div>
                <div class="text2">Btcwinex發行的數字資產為Global Coin，簡稱“ABCDEF”。ABCDEF是基於P2P加密數字算法編程平臺以太坊（Ethereum）發行的去中心化的區塊鏈數字資產，發行總量為1000萬個。</div>
                <img src="${static_domain }/statics/img/icoimgs/icon_baipishu.png" alt="">
            </div>
        </div>

        <div class="four" id="four">
            <div class="content">
                <p>ABCDEF回購機制 價值分紅</p>
                <div class="pic" id="picbox">
                    <div class="div1">全幣種交易的50%手續費</div>
                    <div class="div2">促進ABCDEF市場價格持續上升</div>
                    <div class="div3">封存</div>
                    <span>實時全額分紅</span>
                    <canvas id="myCanvas" width="882px" height="403px"></canvas>
                </div>
                
                <div class="text">
                    我們將拿出平臺每一筆利潤的50%（交易手續費、融資融幣服務費）實時回購ABCDEF。我們將一直回購，直至1000萬總量ABCDEF全部回購完畢為止；回購的ABCDEF封存在固定的私鑰地址，用戶可通過區塊鏈瀏覽器進行查詢，確保公開透明。 Btcwinex每年定期從回購賬戶中拿出發行總量的1%，即10萬ABCDEF分紅給當期持有ABCDEF的用戶（不足10萬按照實際回購數量進行分紅）。每個持有ABCDEF的用戶都會按照持有比例獲得相應分紅。
                </div>
                <div class="text1">
                    
                </div>
            </div>
        </div>

        <div class="five" id="five">
            <div class="content">
                <div class="title">ABCDEF價值轉化</div>
                <div class="pic">
                    <canvas id="myCanvas2" width="713px" height="192px"></canvas>
                </div>
                <div class="memo">當實體黃金兌換業務上線後，ABCDEF智能合約將開啟黃金私鑰兌換機制。Btcwinex會將回購的ABCDEF按市場價格，出售給需要實體黃金兌換服務的客戶，客戶花費1000個ABCDEF即可在智能合約中獲得黃金VIP服務私鑰，實現價值轉化。</div>
            </div>
        </div>

        <div class="six" id="six">
            <div class="content">
                <div class="text1">數字資產兌換實體黃金</div>
                <div class="center">
                    <img src="${static_domain }/statics/img/icoimgs/gold-ingots.png" alt="" class="img1">
                    <img src="${static_domain }/statics/img/icoimgs/duihuan.png" alt="" class="img2">
                    <img src="${static_domain }/statics/img/icoimgs/number-bi.png" alt="" class="img3">
                </div>
                <div class="text2">兌換實體黃金服務是針對數字資產領域億萬富翁人群定製的增值服務。如果您持有黃金私鑰，即可在本平台購買、存儲黃金，購買黃金理財，甚至將實體黃金提現到您的家中。</div>
            </div>
        </div>
        

        <div class="seven" id="seven">
            <div class="content">
                 <div class="title">獲取最新動態</div>
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
                        <span class="text">微信公眾號</span>
                    </div>
                    <div class="item qq">
                        <i><div class="qrcode"></div></i>
                        <span class="text">官方QQ群</span>
                    </div>
                    <div class="item email">
                        <i class="icon"></i>
                        <span class="text">官方郵箱</span>
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