<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${L:l(lan,'Btcwinex新币上线投票火热进行中，立即参与！')}</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0 maximum-scale=1, user-scalable=no">
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<link rel="stylesheet" href="${static_domain }/statics/css/web.vote.css?V${CH_VERSON }">
<script>
    if(JuaBox.isMobile()){
            JuaBox.mobileFontSize();
            $("<link>").attr({ rel: "stylesheet",type: "text/css",href: "${static_domain }/statics/css/mobile/top_foot_mobile.css?V${CH_VERSON }"}).appendTo("head");
            $("<link>").attr({ rel: "stylesheet",type: "text/css",href: "${static_domain }/statics/css/mobile/mobile.vote.css?V${CH_VERSON }"}).appendTo("head");
    }
</script>
</head>
<body class="room">
<jsp:include page="/common/top.jsp" />

<div class="content vote">
    <header class="vote_head">
        <h1 id="title"></h1>
        <h4 id="title_p"></h4>
        <div class="time_box">
            <h5>${L:l(lan,'活动结束倒计时')}</h5>
            <div class="number">
                <span class="time days"><i class="item"></i></span>
                <span class="text">${L:l(lan,'天')}</span>
                <span class="time hrs"><i class="item"></i></span>
                <span class="text">${L:l(lan,'时')}</span>
                <span class="time min"><i class="item"></i></span>
                <span class="text">${L:l(lan,'分')}</span>
                <span class="time sec"><i class="item"></i></span>
                <span class="text">${L:l(lan,'秒')}</span>
            </div>
         </div>
    </header>
    <section class="vote_main">
        <table class="vote_table">
            <thead>
                <tr>
                    <th>${L:l(lan,'币种缩写')}</th>
                    <th>${L:l(lan,'币种全称')}</th>
                    <th>${L:l(lan,'获得票数')}</th>
                    <th>${L:l(lan,'占比')}</th>
                    <th>${L:l(lan,'操作')}</th>
                </tr>
            </thead>
            <tbody id="listData">
                
            </tbody>
        </table>
    </section>
    <!-- <div id="submit" class="submit"></div> -->
    <div id="rule" class="vote_foot">
        <div>${L:l(lan,'活动规则')}</div>
        <div class="vote_foot_text">

        </div>
    </div>
    <div class="float_div">
        <div></div>
        <div class="draw_li"><a class="float_a" href="/lottery">${L:l(lan,'抽奖')}</a></div>
        <div id="home_top">
            <img src="${static_domain }/statics/img/common/home_top.png" alt="">
        </div>
    </div>
</div>

<script type="text/javascript">
    require(["module_vote"],function(vote){
        vote.init();
    });
</script>
<script type="text/x-tmpl" id="tmpl-listData">
    {% for (var i = 0; i <= rs.length -1; i++) { %}
              <tr>
                    <td> <a class="name_href" target="_blank" href="{%=rs[i].urlJson%}">{%=rs[i].titlename%}</a></td>
                    <td> <a class="name_href" target="_blank" href="{%=rs[i].urlJson%}">{%=rs[i].name%}</a></td>
                    <td>{%=rs[i].votecount%}</td>
                    <td class="percentage">
                        <div class="per_num">
                            <span style="width:{%=rs[i].rate%}%" class="per_num_bar"></span>
                        </div>
                        <div>{%=rs[i].rate%}%</div>
                    </td>
                    <td>
                     {% if(rs[i].vote_true == 1){ %}
                         <div coin-id={%=rs[i].coinId%} id="coinId_{%=rs[i].coinId%}" class="status rule_state_vote">
                            +1
                        </div>
                     {% }else{ %}
                        <div coin-id={%=rs[i].coinId%} id="coinId_{%=rs[i].coinId%}" class="status status_click rule_state_{%=rs[i].state%}">
                            ${L:l(lan,'投票')}
                        </div>
                    {%}%}
                        
                    </td>
              </tr>
    {% } %}
</script>
<jsp:include page="/common/foot.jsp" />
<script type="text/javascript">
        var addthis_share = {
            description: "",
            title: "${L:l(lan,'新币投票分享标题')}",
            url: document.URL
        }
    </script>
<script type="text/javascript" src="//s7.addthis.com/js/300/addthis_widget.js#pubid=ra-59cc5a9e6b75ce39"></script>
</body>
</html>
