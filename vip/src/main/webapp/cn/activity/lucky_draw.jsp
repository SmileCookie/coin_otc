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
<title>${L:l(lan,'抽奖')}-${WEB_NAME}-${WEB_TITLE }</title>
<meta name="viewport" content="width=device-width,height=device-height,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<link rel="stylesheet" id="css" href="${static_domain }/statics/css/lucky_draw.css?V${CH_VERSON }">
    <script>
        var isphone = false;//是否为手机
        if(JuaBox.isMobile()){
            JuaBox.mobileFontSize();
            isphone = true;
            document.getElementById('css').setAttribute("href","${static_domain }/statics/css/mobile/mobile.draw.css?V${CH_VERSON }");
            $("<link>").attr({ rel: "stylesheet",type: "text/css",href: "${static_domain }/statics/css/mobile/top_foot_mobile.css?V${CH_VERSON }"}).appendTo("head");
        }
    </script>
<body>
    <div class="container_index lucky_draw_page">
        <jsp:include page="/common/top.jsp" />
        <div class="container_index_main clearfix">
            <div class="content">
                <div class="title" id="title"></div>
                <p class="info" id="info"></p>
                <div class="draw-box clearfix">
                    <section class="draw-box-main">
                        <div class="draw-awarded">
                            <div class="draw-box-title">
                                ${L:l(lan,'奖池已发放')}
                            </div>
                            <div class="draw-box-content">
                                <div class="num" id="awarded"></div>
                                <!-- <div class="icon"></div> -->
                            </div>
                        </div>

                        <section class="scroll_mian">
                            <div class="backgr_01">
                                <div class="backgr_02">
                                    <div class="backgr_03">
                                        <div class="backgr_04">
                                            <div class="scroll-num" id="scroll-num">
                                                <div class="scroll clearfix scroll_none">
                                                </div>
                                            </div>
                                            <div class="noStart">ABCDEF</div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                        </section>
                        
                        <div class="draw-obtained">
                            <div class="draw-box-title">
                                ${L:l(lan,'您已获得')}
                            </div>
                            <div class="draw-box-content">
                                <div class="num" id="obtained"></div>
                            </div>
                        </div>
                        <div class="tip_box_alert"></div>
                    </section>
                    <input type="hidden" id="token">
                    <div class="tip-box">抽奖机会：次</div>
                    <div class="tip-box_1">(${L:l(lan,'参与“新币投票”可获得抽奖机会')})</div>
                    <div class="draw-button" id="drawBut"></div>
                    <div class="draw-bottom">
                        <div class="draw-bottom-title">${L:l(lan,'活动规则-1')}</div>
                        <div id="rulesBox">
                            
                        </div>
                    </div>
                </div>
                
                <div class="foot_tips">*${L:l(lan,"本次活动最终解释权归btcwinex平台所有！")}</div>
            
            </div>
            <a class="toupiao-icon" href="/vote" id="toupiaoIcon">${L:l(lan,'投票')}</a>
        </div>
        <jsp:include page="/common/foot.jsp" />
        
    </div>
    
    <script type="text/javascript">
        var addthis_share = {
            description: "",
            title:"${L:l(lan,'起来嗨！投票即可抽取ABCDEF，还不点进来，立即参与？')}",
            url: document.URL
        }
        require(["module_draw","module_vconsole"],function(draw,VConsole){
            draw.init();
            //var vConsole = new VConsole();
        });
    </script>
    <script type="text/javascript" src="//s7.addthis.com/js/300/addthis_widget.js#pubid=ra-59cc5a9e6b75ce39"></script>
</body>
</html>