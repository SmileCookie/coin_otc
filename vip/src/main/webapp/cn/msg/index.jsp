<%@ page  session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>

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
</script>
</head>
<body class="room notice_room">
<jsp:include page="/common/top.jsp" />
    <section class="notice_main">
        <div class="header"></div>
        <div class="notice_body">
            <div class="notice_head clearfix">
                <div class="item active" data-id="0">
                    <svg class="icon" aria-hidden="true">
                        <use xlink:href="#icon-quanbu"></use>
                    </svg>
                    ${L:l(lan,'全部公告')}
                </div>
                <div class="item" data-id="1">
                    <svg class="icon" aria-hidden="true">
                        <use xlink:href="#icon-xinbi"></use>
                    </svg>
                     ${L:l(lan,'新币上线')}
                </div>
                <div class="item" data-id="2">
                    <svg class="icon" aria-hidden="true">
                        <use xlink:href="#icon-weihu"></use>
                    </svg>
                    ${L:l(lan,'系统维护')}
                </div>
                <div class="item" data-id="3">
                    <svg class="icon" aria-hidden="true">
                        <use xlink:href="#icon-huodong"></use>
                    </svg>
                    ${L:l(lan,'最新活动')}
                </div>
                <div class="item" data-id="4">
                    <svg class="icon" aria-hidden="true">
                        <use xlink:href="#icon-pingtaidongtai"></use>
                    </svg>
                    ${L:l(lan,'平台动态')}
                </div>
            </div>
            <section class="notice_table"></section>
            <div class="pages"></div>
        </div>
    </section>
<jsp:include page="/common/foot.jsp" />
<script type="text/javascript">
    var notice_id = $.cookie("notice_id");
    body_height();
    //动态高度
    function body_height() {
            var body_height = parseInt($(".notice_body").height())+ 150;     
            $(".notice_main").height(body_height);
    }

    var ajax_data = {};
    ajax_data.noticeType=0;
    notice_ajax(ajax_data);
    
    $(".notice_head .item").on("click",function(){
        $(".notice_head .item").removeClass("active");
        $(this).addClass("active");
        var noticeType = $(this).attr("data-id");
        ajax_data = {};
        ajax_data.noticeType = noticeType;
        notice_ajax(ajax_data);
    })
    
    function showLogPage(listDiv, pageIndex, rsCount, pageSize,noticeType){
        		var $this = this;
        	    var $pageDiv = listDiv;
	        	if(rsCount < pageSize && pageIndex == 1) {
	        		$pageDiv.html("");
	        		return false;
	        	}
	        	var pageCount = rsCount % pageSize == 0 ? parseInt(rsCount / pageSize) : parseInt(rsCount / pageSize) + 1 ;
        	    $pageDiv.createPage({
	    			noPage:false,
	    			pageSize:pageSize,
			        rsCount:rsCount,
			        pageCount:pageCount,
			        current: pageIndex || 1,
			        backFn:function(pageNum){
			            var datas = {};
                        datas.pageIndex = pageNum;
                        datas.pageSize = pageSize;
                        datas.listDiv = listDiv;
                        datas.noticeType = noticeType;
                        notice_ajax(datas)
			        }
		    });
        }

    function notice_ajax(param) {
        var data = {};
        data.pageIndex = param.pageIndex || 0;
        data.pageSize = param.pageSize || 10;
        data.noticeType = param.noticeType || 0;
        $.ajax({
            url: "/msg/newsOrAnnList?type=1&pageIndex="+ data.pageIndex+"&pageSize="+ data.pageSize+"&noticeType="+data.noticeType,
            type: "GET",
            dataType: "json",
            success: function (data) {
                if( data.isSuc ){
                    var datas = data.datas;
                    notice_innerHTML(datas,param.noticeType);
                }
            },
            error: function (e) {
                console.log(e);
            }
        }) 
    }
    function notice_innerHTML(params,noticeType) {
        if( !params.datalist ){ 
            var svg_icon = svg_icon_func(noticeType);
            if( LANG == "en" ){
                noticeType = "No notice at present.";
            }
            else{
                if(noticeType == 1){
                    noticeType = "${L:l(lan,'当前没有')}${L:l(lan,'新币上线')}${L:l(lan,'公告')}。"
                }
                else if(noticeType == 2){
                    noticeType = "${L:l(lan,'当前没有')}${L:l(lan,'系统维护')}${L:l(lan,'公告')}"
                }
                else if(noticeType == 3){
                    noticeType = "${L:l(lan,'当前没有')}${L:l(lan,'最新活动')}${L:l(lan,'公告')}"
                }
                else if(noticeType == 4){
                    noticeType = "${L:l(lan,'当前没有')}${L:l(lan,'平台动态')}${L:l(lan,'公告')}"
                }
                else{
                    noticeType = "${L:l(lan,'当前没有')}${L:l(lan,'公告')}";
                }
            }
            var nohtml = '<div class="no_notice">'+
                            '<svg class="icon" aria-hidden="true">'+
                                    '<use xlink:href="'+svg_icon+'"></use>'+
                            '</svg>'+
                            noticeType+
                        '</div>';
             $(".notice_table").html(nohtml);
            $(".pages").html("");
            return;
        }
        var total = parseInt(params.total); //总条数
        var pageSize = parseInt(params.pageSize); //当前页条数
        var pageIndex = params.pageIndex; // 当前页码
        var total_page = Math.ceil( total/ pageSize ); //总页数
        var list_data = params.datalist;
        var html = ""; 
        showLogPage($(".pages"),pageIndex,total,pageSize,noticeType);
        for (var i = 0; i < list_data.length; i++) {
            var item = list_data[i];
            var noticeType = item.noticeType;
            var svg_icon = "";
            item.pubTime = formatDate(item.pubTime, "yyyy-MM-dd hh:mm:ss");
            var svg_icon = svg_icon_func(noticeType);
            html +=  '<div class="body_item">'+
                        '<div class="body_tilte clearfix">'+
                            '<div class="item">'+
                                '<svg class="icon" aria-hidden="true">'+
                                    '<use xlink:href="'+svg_icon+'"></use>'+
                                '</svg>'+
                            '</div>'+
                            '<div class="item">'+item.pubTime+
                            '</div>'+
                            '<div class="item">'+item.title+'</div>'+
                        '</div>'+
                        '<div class="body_content '+item.id+'">'+item.content+'</div>'+
                    '</div>';
        }
        $(".notice_table").html(html);
        
        $("."+notice_id).toggle();
        body_height();
        bodylist_click();
    }
    function bodylist_click() {
        $(".body_item .body_tilte").on("click",function(){
            var obj = $(this).next();
            notice_id = $.cookie("notice_id");
            $(".body_content").hide();
            if( !obj.hasClass("act") ){
                obj.toggle();
                $(".body_content").removeClass("act");
                obj.addClass("act");
                if(notice_id){
                    $("."+notice_id).hide();
                    $("."+notice_id).removeClass("act");
                    $.cookie('notice_id', '', { expires: -1,path: '/' });
                }
                
            }else{
                obj.removeClass("act");
            }
            body_height();
        })
    }
    function svg_icon_func(noticeType) {
        var svg_icon = "";
        if(noticeType == 1){
            svg_icon = "#icon-xinbi";
        }
        else if(noticeType == 2){
            svg_icon = "#icon-weihu";
        }
        else if(noticeType == 3){
            svg_icon = "#icon-huodong";
        }
        else if(noticeType == 4){
            svg_icon = "#icon-pingtaidongtai";
        }
        else{
            svg_icon = "#icon-quanbu";
        }
        return svg_icon;
    }
    function formatDate(timestamp,format){
		Date.prototype.format = function (format) {
		    var o = {
		        "M+": this.getMonth() + 1,
		        "d+": this.getDate(),
		        "h+": this.getHours(),
		        "m+": this.getMinutes(),
		        "s+": this.getSeconds(),
		        "q+": Math.floor((this.getMonth() + 3) / 3),
		        "S": this.getMilliseconds()
		    }
		    if (/(y+)/.test(format)) {
		        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
		    }
		    for (var k in o) {
		        if (new RegExp("(" + k + ")").test(format)) {
		            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
		        }
		    }
		    return format;
		}
		return new Date(timestamp).format(format);
	}
</script>
</body>
</html>
