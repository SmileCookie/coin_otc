<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>

<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${n.title}-${L:l(lan,'新闻')}-${WEB_NAME }-${WEB_TITLE }</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0 maximum-scale=1, user-scalable=no">
<meta name="keywords" content="${n.keyword},${WEB_KEYWORD }" />
<meta name="description" content="${n.digest},${WEB_DESC }" />
<link rel="stylesheet" href="${static_domain }/statics/css/web.news.css?V${CH_VERSON }">
<script>
    if(JuaBox.isMobile()){
            JuaBox.mobileFontSize();
            $("<link>").attr({ rel: "stylesheet",type: "text/css",href: "${static_domain }/statics/css/mobile/mobile.news.css?V${CH_VERSON }"}).appendTo("head");
            $("<link>").attr({ rel: "stylesheet",type: "text/css",href: "${static_domain }/statics/css/mobile/top_foot_mobile.css?V${CH_VERSON }"}).appendTo("head");
    }
</script>
</head>
<body class="room">
<jsp:include page="/common/top.jsp" />

<div class="news_mainer">
    <div class="news_container">
        <div class="news_con-msg clearfix">
            <h2 class="details_title">${n.title}</h2>
            <div class="details_other clearfix">
                <div class="news_time"><span class="time_img"></span>${n.pubTimeStr}</div>
                <div class="news_source">${L:l(lan,'来源')}：${n.source}</div>
                <!--<div class="details_pinglun">Content:&nbsp;<span>18</span></div>-->
            </div>
            <div class="details_contene">
                ${n.content}
            </div>
            <div class="details_foottext">${L:l(lan,'版权声明：作者保留权利。文章为作者独立观点，不代表Btcwinex立场')}</div>
            <!--<footer class="details_con clearfix">
                <h3>Content</h3>
                <div class="text_main">
                     <textarea onkeyup="this.value=this.value.replace(/\uD83C[\uDF00-\uDFFF]|\uD83D[\uDC00-\uDE4F]/g,'')" maxlength="220" id="sub_text"></textarea>
                </div>
                <div class="submit">Commment</div>
            </footer>
            <section class="comment_list">
                <div class="comment_warp">
                     <div class="list_head clearfix">
                        <span class="list_name">名字名字名字名字</span>
                        <span class="list_time">2019-02-02</span>
                    </div>
                    <div class="common_main">
                        内容内容内容内容内容内容内容内容内容内
                    </div>
                </div>
             </section>
             <div class="news_foot">加载更多</div>-->
        </div>
    </div>
</div>


<jsp:include page="/common/foot.jsp" />
</body>
</html>
