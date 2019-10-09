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
<title>${L:l(lan,'下载')}-${WEB_NAME}-${WEB_TITLE }</title>
<meta name="keywords" content="${WEB_KEYWORD }" />
<meta name="description" content="${WEB_DESC }" />
<meta name="viewport" content="width=device-width,height=device-height,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
<link rel="stylesheet" id="css" href="${static_domain }/statics/css/icocss/app-download.css?V${CH_VERSON }">
    <script>
        var isphone = false;//是否为手机
        if(JuaBox.isMobile()){
            JuaBox.mobileFontSize();
            isphone = true;
            document.getElementById('css').setAttribute("href","${static_domain }/statics/css/icocss/app-download-mobile.css?V${CH_VERSON }");
            $("<link>").attr({ rel: "stylesheet",type: "text/css",href: "${static_domain }/statics/css/mobile/top_foot_mobile.css?V${CH_VERSON }"}).appendTo("head");
        }
    </script>
<body>
    <div class="appdownload-mark" onclick="closeMark()">
        <div class="app-one">
            ${L:l(lan,'app下载提示层1')}
            <div class="img"></div>
        </div>
        <div class="app-two">
            ${L:l(lan,'app下载提示层2')}
        </div>
    </div>
    <div class="appdownload-mark appdownload-mark_1 " onclick="closeMark()">
        <div class="app-one">
        </div>
        <div class="app-two two_2">
            ${L:l(lan,'app下载提示层3')}
        </div>
    </div>

    <div class="container_index">
        <jsp:include page="/common/top.jsp" />
        <div class="content">
            <div class="top">
                <div class="left"></div>
                <div class="right">
                    <h1>${L:l(lan,'随地进行自由的交易')}</h1>
                    <div class="text">${L:l(lan,'您可以随时随地的浏览行情、资金情况，或进行自由的交易')}</div>
                    <div class="button">
                        <div class="button-box iphone" onclick="downIphone()">
                            <i class="icon"></i>
                            <span class="name">iPhone</span>
                            <span class="memo">${L:l(lan,'本地下载')}</span>
                        </div>
                        <div class="button-box android" onclick="downAndroid()">
                            <i class="icon"></i>
                            <span class="name">Android</span>
                            <span class="memo">${L:l(lan,'本地下载')}</span>
                        </div>
                    </div>
                    <div class="info">
                        <span class="text1">${L:l(lan,'iPhone用户下载App前请阅读')}<a href="${vip_domain}/login/zendesk/?viewFlag=app_down" target="_blank">${L:l(lan,'安装说明')}</a> ${L:l(lan,'安装之前')}</span>
                    </div>
                    
                </div>
            </div>
            <div class="center">
                <div class="box">
                    <div class="title">${L:l(lan,'为什么选择我们')}</div>
                    <div class="left">
                        <div class="item one">
                            <i class="icon"></i>
                            <span class="item-top">${L:l(lan,'简洁、精致的行情列表')}</span>
                            <span class="item-bottom">${L:l(lan,'让您更直观的掌握行情走势，聚焦您最关注的信息。')}</span>
                        </div>
                        <div class="item two">
                            <i class="icon"></i>
                            <span class="item-top">${L:l(lan,'理性的数据分析')}</span>
                            <span class="item-bottom">${L:l(lan,'买卖成交分步统计、多维度涨跌变化统计、趋势分析图，帮助您更精准的判断行情走势。')}</span>
                        </div>
                        <div class="item three">
                            <i class="icon"></i>
                            <span class="item-top">${L:l(lan,'多市场价格对比')}</span>
                            <span class="item-bottom">${L:l(lan,'实时获取主流交易平台的市场价格，节省您的时间成本。')}</span>
                        </div>
                    </div>
                    <div class="right">
                        <div class="item one">
                            <i class="icon"></i>
                            <span class="item-top">${L:l(lan,'价格监测预警')}</span>
                            <span class="item-bottom">${L:l(lan,'实时监控价格，一旦达到预警值，立刻发出提示音，让您绝不错过交易的最佳时机。')}</span>
                        </div>
                        <div class="item two">
                            <i class="icon"></i>
                            <span class="item-top">${L:l(lan,'化繁为简的交易操作')}</span>
                            <span class="item-bottom">${L:l(lan,'独有的交易操作界面，给您更新鲜的用户体验。买卖按钮并排，二选一永远不会错。')}</span>
                        </div>
                        <div class="item three">
                            <i class="icon"></i>
                            <span class="item-top">${L:l(lan,'交易、K线灵活切换')}</span>
                            <span class="item-bottom">${L:l(lan,'在K线图与交易操作之间快速切换，让您快捷操作短线。')}</span>
                        </div>
                    </div>
                </div>
            </div>
            <div class="bottom">
                <p class="text">${L:l(lan,'欢迎您使用btcwinexapp')}</p>
                <button class="button" onclick="gotoContactUs()">${L:l(lan,'联系我们')}</button>
            </div>
        </div>
        
        <jsp:include page="/common/foot.jsp" />
    </div>
    
    <script type="text/javascript">
        function gotoContactUs(){
            window.location.href = '${vip_domain}/terms/termsContactUs'
        }
        function downIphone(){
            var userAgent = navigator.userAgent;

            if( JuaBox.isMobile() ){
                if(userAgent.indexOf('iPhone') > -1){
                    if (userAgent.indexOf("Safari") > -1 ) {
                        console.log("itms-services://?action=download-manifest&url=${vip_domain }/html/manifest.plist")
                        window.location.href="itms-services://?action=download-manifest&url=${vip_domain }/html/manifest.plist";
                    }
                    else{
                        $(".appdownload-mark_1").css({'display':'block'})
                        $('.appdownload-mark_1').bind( "touchmove", function (e) {
                            e.preventDefault();
                        });
                    }
                }else{
                     JuaBox.showWrong("${L:l(lan,'该链接仅限iPhone用户使用。')}")
                }
                
            }
            else{
               JuaBox.showWrong("${L:l(lan,'请在手机上使用浏览器打开本页面进行下载')}")
            }
            
        }
        function downAndroid(){
            if(isWeiXin()){
                $(".appdownload-mark").css({'display':'block'})
                $('.appdownload-mark').bind( "touchmove", function (e) {
                    e.preventDefault();
                });
            }else{
                window.open("https://o4we6sxpt.qnssl.com/bitglobal/download/bitglobal-android.apk");
            }

        }
        function closeMark(){
            $(".appdownload-mark").css({'display':'none'})
        }
        function isWeiXin(){ 
            var ua = window.navigator.userAgent.toLowerCase();
            if(ua.match(/MicroMessenger/i)){ 
                return true; 
            }else{ 
                return false; 
            } 
            
        } 

    </script>
</body>
</html>