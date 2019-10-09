<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ page session="false"%>
<!doctype html>
<html>

<head>
    <jsp:include page="/common/headmb.jsp" />
    <title>${WEB_NAME}-${WEB_TITLE }</title>
    <meta name="viewport" content="width=device-width,height=device-height,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta name="keywords" content="${WEB_KEYWORD }" />
    <meta name="description" content="${WEB_DESC }" />
    <script type="text/javascript" src="${static_domain }/statics/js/jquery.flot-min.js"></script>
    <link rel="stylesheet" href="${static_domain }/statics/css/web.index.css?V${CH_VERSON }">
    <link rel="stylesheet" href="${static_domain }/statics/css/mobile/mobile.down.css?V${CH_VERSON }">
    <link rel="stylesheet" href="${static_domain }/statics/css/swiper-4.3.3.min.css">
    <link rel="stylesheet" type="text/css" href="${static_domain}/statics/css/iconfont.css">
    <link rel="stylesheet" type="text/css" href="${static_domain}/statics/css/jquery.mCustomScrollbar.css">
    <style>
        html, body, div, p, ul, li, dl, dt, dd, h1, h2, h3, h4, h5, h6, form, input, select, button, textarea, iframe, table, th, td { margin: 0; padding: 0; }
        img { border: 0 none; }
        ul, li { list-style-type: none; }
        h1, h2, h3, h4, h5, h6 {  font-weight:normal; }
        body, input, select, button, textarea { font-size: 0.12rem;}
        button { cursor: pointer; }
        i, em, cite { font-style: normal; }
        a{text-decoration: none}

        html{
            height: 100%;
            font-family: "microsoft yahei";
            font-size: 16px;

        }
        body{

        }
        .clear{
            content: '';
            clear: both;
        }
        .content{
            /* margin-top: 1rem; */
            /* position: relative; */
            position: fixed;
            height: 100%;
            width: 100%;
            padding-top: 1rem;
            background: url('${static_domain }/statics/images/BG-png@2x.png') left bottom no-repeat;
            background-size: cover;
        }
        .content .mark-img{
            width: 2.5rem;
            height: 3.0rem;
            margin: 2.06rem auto 0;
        }
        .content .mark-img img{
            width:100%;
            /* height: 100%; */
        }
        .mark-text{
            font-size: 0.45rem;
            line-height: 0.14rem;
            font-family: 'PingFangSC-Medium';
            color: #fff;
            text-align: center;
            font-weight: 600;
            margin-top: 0.16rem;
        }
        .btn-box{
            margin: 1.36rem auto 0;
        }
        .btn-box li{
            border: 1px solid #3E85A2;
            width: 5.5rem;
            height: 1.3rem;
            border-radius: 0.7rem;
            margin: 0 auto 0.4rem;
            line-height: 0.86rem;
        }
        .btn-box .btn-img{
            width: 0.6rem;
            height: 100%;
            position: absolute;
            left: 6%;
            top: 15%;
        }
        .btn-box .btn-img img{
            width: 100%;
            vertical-align: middle;
        }
        .btn{
            box-sizing: border-box;
            display: inline-block;
            width: 100%;
            font-size: 0.28rem;
            color: #ffffff;
            position: relative;
        }
        .btn-text{
            width: 100%;
            text-align: center;
            height: 100%;
            font-size: 0.4rem;
            line-height: 0.86rem;
            color: #3E85A2;

        }
        .footer-hint{
            width: 5.6rem;
            height: 0.8rem;
            margin: 0.9rem auto 0;
            background: #EDEEF0 ;
            color: #676A73;
            font-size: 0.24rem;
            font-family: 'PingFangSC-Regular';
            color: #676A73;
            letter-spacing: 1px;
            text-align: center;
            line-height: 0.8rem;
        }
    </style>
</head>

<body class="room">
<div class="bk-body">
    <jsp:include page="/common/topmb.jsp"/>
    <% if(true){%>
    <!-- 轮播背景 -->
    <div class="content">
        <div class="content">
            <div class="mark-img">
                <img src="${static_domain }/statics/images/icon2x.png" alt="">
            </div>
            <p class="mark-text">${L:l(lan,'迪拜数字资产交易中心')}</p>
            <ul class="btn-box ">
                <li class="clear">
                    <a class="btn" href="https://www.btcwinex.com/downloadOnlyApp/Btcwinex.apk">
                        <div class="btn-img">
                            <img src="${static_domain }/statics/images/Android (2)-2@2x.png" alt="">
                        </div>
                        <p class="btn-text">${L:l(lan,'本地下载')}</p>
                    </a>
                </li>
                <%--<li class="clear">--%>
                    <%--<a class="btn" href="javascript:void(0);">--%>
                        <%--<div class="btn-img">--%>
                            <%--<img src="${static_domain }/statics/images/google-play-2@2x.png" alt="">--%>
                        <%--</div>--%>
                        <%--<p class="btn-text">${L:l(lan,'前往Google')}</p>--%>
                    <%--</a>--%>
                <%--</li>--%>
            </ul>
            <!-- <p class="footer-hint">如何设置“未受信任的企业级开发者”</p> -->
        </div>

    </div>
    <jsp:include page="/common/footmbDown.jsp" />
</div>
</body>
<script>


</script>
<%}%>
<script>

</script>
</html>
