<%--
  Created by IntelliJ IDEA.
  User: oswald
  Date: 2018/11/28
  Time: 下午5:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <jsp:include page="/common/head.jsp" />
    <title>${WEB_NAME}-${WEB_TITLE }</title>
    <style>
        html{
            font:13px Roboto,'Microsoft YaHei',sans-serif;
        }
        *{
            margin:0;
            padding:0;
        }
        body{
            background: #232431;
            padding: 60px 0;
        }
        .m{
            width: 1400px;
            margin: 0 auto;
        }
        .ptith{
            font-size: 24px;
            color: #D4DCF0;
            margin-bottom: 25px;
            font-weight: normal;
        }
        .mi{
            background: #383E4C;
            padding: 95px 200px 115px 200px;
        }
        .mi h2{
            font-size: 18px;
            color: #D4DCF0;
            font-weight:normal;
            margin-bottom: 8px;
        }
        .awp li{
            display: inline-block;
            margin-right: 40px;
            line-height: 28px;
            list-style: none;
        }
        .awp a{
            font-size: 14px;
            color: #9199AF;
            text-decoration: none;

        }
        .awp a:hover{
            text-decoration: underline;
            color: #3E85A2;
        }
        .awp_sp{
            padding-bottom: 15px;
            border-bottom: 1px solid #464C59;
            margin-bottom: 35px;
        }
    </style>
</head>
<body>
    <div class="m">
        <h1 class="ptith">网站地图</h1>
        <div class="mi">
            <h2>首页</h2>
            <ul class="awp awp_sp">
                <c:forEach items="${seoList}" var="seo">
                    <li>
                        <a title="${seo.title}" href="${seo.url}">${seo.tith}</a>
                    </li>
                </c:forEach>
            </ul>

            <h2>币币交易</h2>

            <ul class="awp">
                <li>
                    <a title="币币交易" href="/bw/trade/">币币交易</a>
                </li>
                <li>
                    <a title="多屏看板" href="/bw/multitrade">多屏看板</a>
                </li>
                <li>
                    <a title="新闻" href="/bw/news">新闻</a>
                </li>
            </ul>

        </div>
    </div>
</body>
</html>
