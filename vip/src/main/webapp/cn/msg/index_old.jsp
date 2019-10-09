<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>

<%-- 老版公告 --%>

<!doctype html>
<html>
<head>
<jsp:include page="/common/head.jsp" />
<title>${L:l(lan,'一级导航-居左内容-超链接-4')}-${WEB_NAME }-${WEB_TITLE }</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0 maximum-scale=1, user-scalable=no">
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
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
         <h2>${L:l(lan,'一级导航-居左内容-超链接-4')}</h2>
         <div class="b_newslist">
         </div>
        <div class="news_foot">
            ${L:l(lan,'加载更多')}
        </div>
    </div>
</div>
</div>
<jsp:include page="/common/foot.jsp" />
<script type="text/javascript">
    var pageIndex = 1;
    $(".news_foot").on("click",function(){
        pageIndex++;
        next_data();
    })
    next_data();
    function next_data(){
        $.ajax({
            url:"/msg/newsOrAnnList?type=1",
            type:"GET",
            data:{
                page:pageIndex,
                pageSize:10
            },
            dataType:"json",
            success:function(data){
                suc_data(data);
            },
            error: function (e) {
                console.log(e);
                $('.news_foot').hide();
            }
        }) 
    }
    function suc_data(data){
        var datas = data.datas;
        var total = datas.total;
        var row = datas.datalist;
        var ishtml = "";
        if( total > 10 ){
            $('.news_foot').show();
        }
        for (var i = 0; i < row.length; i++) {
            ishtml += '<div class="item clearfix">'+
                            '<h3>'+row[i].title+'</h3>'+
                            '<div class="time">'+
                                '<span class="time_img"></span>'+row[i].pubTimeStr+
                            '</div>'+
                            '<div class="new-con">'+row[i].content+'</div>'+
                        '</div>';
        }
        $(".b_newslist").append(ishtml);
        if( total <= $(".item").length ){
             $('.news_foot').html("${L:l(lan,'暂无更多记录')}").unbind('click').css("cursor","default");
        }
    }
</script>
</body>
</html>
