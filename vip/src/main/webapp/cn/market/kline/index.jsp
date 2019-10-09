<%@ page session="false" language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="L" uri="http://w.btcbt.com/jstl/functions" %>
<%@ page import="com.world.web.action.Action"%>
<%@ page import="com.world.config.GlobalConfig"%>
<%! 
  private static long CH_VERSON = 0; 
%>
<%
	String header = GlobalConfig.session;
	String vip_domain=Action.VIP_DOMAIN;
	String static_domain=Action.STATIC_DOMAIN;
	String main_domain=Action.MAIN_DOMAIN;
	String p2p_domain=Action.P2P_DOMAIN;
	String trans_domain=Action.TRANS_DOMAIN;
	if(CH_VERSON==0){
		CH_VERSON = new Date().getTime(); 
	}
	request.setAttribute("CH_VERSON", CH_VERSON);
 %>
<!DOCTYPE html>
<html>
<head>
<script type="text/javascript">
var GLOBAL             = {},
    VERSION            = GLOBAL['VERSION']           = '${CH_VERSON }',
    ZNAME              = GLOBAL['ZNAME']              = '<%=header%>',
    DOMAIN_BASE        = GLOBAL['DOMAIN_BASE']       = '${baseDomain }',
    <%--DOMAIN_MAIN        = GLOBAL['DOMAIN_MAIN']       = '<%=main_domain%>',--%>
    <%--DOMAIN_VIP         = GLOBAL['DOMAIN_VIP']        = '<%=vip_domain%>',--%>
    DOMAIN_MAIN        = GLOBAL['DOMAIN_MAIN']       = '${main_domain}',
    DOMAIN_VIP         = GLOBAL['DOMAIN_VIP']        = '${vip_domain}',
    DOMAIN_STATIC      = GLOBAL['DOMAIN_STATIC']     = '<%=static_domain%>',
    DOMAIN_P2P         = GLOBAL['DOMAIN_P2P']        = '<%=p2p_domain%>',
    DOMAIN_TRANS       = GLOBAL['DOMAIN_TRANS']      = '<%=trans_domain%>',
    LANG               = GLOBAL['LANG']              = '${lan }',
    UON                = GLOBAL['UON']               = '<%=header%>uon',
    UID                = GLOBAL['UID']               = '<%=header%>uid',
    AID                = GLOBAL['AID']               = '<%=header%>aid',
    RID                = GLOBAL['RID']               = '<%=header%>rid',
    VIP                = GLOBAL['VIP']               = '<%=header%>vip',
    UNAME              = GLOBAL['UNAME']             = '<%=header%>uname',
    ANAME              = GLOBAL['ANAME']             = '<%=header%>aname',
    NOTE               = GLOBAL['NOTE']              = '<%=header%>note';

</script>

<meta charset="utf-8">
<title>${L:l(lan,'行情图表-比特全球兑换中心-用心服务每一刻')}</title>
<meta name="renderer" content="webkit">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta http-equiv="Cache-Control" content="no-siteapp">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta content="yes" name="apple-touch-fullscreen">
<meta content="telephone=no" name="format-detection">
<meta content="black" name="apple-mobile-web-app-status-bar-style">
<meta name="author" content="BitGlobal">
<meta name="copyright"  content="Copyright &copy;Btcwinex版权所有">
<meta name="revisit-after"  content="1 days">
<link rel="shortcut icon" href="${static_domain }/statics/img/favicon/favicon_red.ico?V${CH_VERSON }">
<link href="${static_domain }/statics/js/kline/css/kline.css?V${CH_VERSON }" rel="stylesheet" type="text/css">
<script type="text/javascript" src="${static_domain }/statics/js/web.base.js?V${CH_VERSON }"></script>
<script type="text/javascript" src="${static_domain }/statics/js/common/jquery.history.js?V${CH_VERSON }"></script>
<script type="text/javascript" src="${static_domain }/statics/js/kline/jquery.mousewheel.js?V${CH_VERSON }"></script>
<script type="text/javascript">
	var marketData = ${marketData };
</script>
</head>

<body>
<div class="bk-animationload" style="display:block">
	<div class="bk-preloader"></div>
</div>
<div id="trade_container">
        <div class="m_cent">
            <div class="m_guadan">
                <div id="market">${L:l(lan,'比特全球')} ${numberBi }/${exchangeBi } ${L:l(lan,'交易')}</div>
                <div id="orderbook">
                    <div id="asks">
                        <div class="table">
                        </div>
                   </div>
                   <div id="gasks">
                       <div class="table">
                       </div>
                   </div>
                   <div id="price" class="red"></div>
                   <div id="bids">
                       <div class="table">
                       </div>
                    </div>
                    <div id="gbids">
                        <div class="table">
                        </div>
                    </div>
                </div>
                <div id="trades" class="trades">
	                <div class="trades_list">
	                  </div>
                </div>
            
            </div>
        
        </div>
</div>
 <!-- Chart Container -->
<div id="chart_container" class="dark">

    <!-- Dom Element Cache -->
    <div id="chart_dom_elem_cache"></div>

    <!-- ToolBar -->
    <div id="chart_toolbar">
        <div class="chart_toolbar_minisep"> </div>

        <!-- Periods -->
        <div class="chart_dropdown" id="chart_toolbar_periods_vert">
            <div class="chart_dropdown_t"><a class="chart_str_period">TIME</a></div>
            <div class="chart_dropdown_data" style="margin-left: -58px;">
                <table>
                <tbody>
                <tr>
                    <td>
                        <ul>
                            <li id="chart_period_1w_v" name="1w">  <a class="chart_str_period_1w">1w</a></li>
                            <li id="chart_period_3d_v" name="3d">  <a class="chart_str_period_3d">3d</a></li>
                            <li id="chart_period_1d_v" name="1d">  <a class="chart_str_period_1d">1d</a></li>
                        </ul>
                    </td>
                </tr>
                <tr>
                    <td>
                        <ul>
                            <li id="chart_period_12h_v" name="12h"> <a class="chart_str_period_12h">12h</a></li>
                            <li id="chart_period_6h_v" name="6h">  <a class="chart_str_period_6h">6h</a></li>
                            <li id="chart_period_4h_v" name="4h">  <a class="chart_str_period_4h">4h</a></li>
                            <li id="chart_period_2h_v" name="2h">  <a class="chart_str_period_2h">2h</a></li>
                            <li id="chart_period_1h_v" name="1h">  <a class="chart_str_period_1h">1h</a></li>
                        </ul>
                    </td>
                </tr>
                <tr>
                    <td>
                        <ul>
                            <li id="chart_period_30m_v" name="30m"> <a class="chart_str_period_30m">30m</a></li>
                            <li id="chart_period_15m_v" name="15m"> <a class="chart_str_period_15m">15m</a></li>
                            <li id="chart_period_5m_v" name="5m">  <a class="chart_str_period_5m">5m</a></li>
                            <li id="chart_period_3m_v" name="3m">  <a class="chart_str_period_3m">3m</a></li>
                            <li id="chart_period_1m_v" name="1m">  <a class="chart_str_period_1m">1m</a></li>
                            <li id="chart_period_line_v" name="line"><a class="chart_str_period_line">Line</a></li>
                        </ul>
                    </td>
                </tr>
                </tbody>
                </table>
            </div>
        </div>
        <div id="chart_toolbar_periods_horz">
            <ul class="chart_toolbar_tabgroup" style="padding-left:5px; padding-right:11px;">
                <li id="chart_period_1w_h" name="1w" style="display: inline-block;">  <a class="chart_str_period_1w">1w</a></li>
                <li id="chart_period_3d_h" name="3d" style="display: inline-block;">  <a class="chart_str_period_3d">3d</a></li>
                <li id="chart_period_1d_h" name="1d" style="display: inline-block;">  <a class="chart_str_period_1d">1d</a></li>
                <li id="chart_period_12h_h" name="12h" style="display: none;"> <a class="chart_str_period_12h">12h</a></li>
                <li id="chart_period_6h_h" name="6h" style="display: none;">  <a class="chart_str_period_6h">6h</a></li>
                <li id="chart_period_4h_h" name="4h" style="display: none;">  <a class="chart_str_period_4h">4h</a></li>
                <li id="chart_period_2h_h" name="2h" style="display: none;">  <a class="chart_str_period_2h">2h</a></li>
                <li id="chart_period_1h_h" name="1h" style="display: inline-block;">  <a class="chart_str_period_1h selected">1h</a></li>
                <li id="chart_period_30m_h" name="30m" style="display: inline-block;"> <a class="chart_str_period_30m">30m</a></li>
                <li id="chart_period_15m_h" name="15m" style="display: inline-block;"> <a class="chart_str_period_15m">15m</a></li>
                <li id="chart_period_5m_h" name="5m" style="display: inline-block;">  <a class="chart_str_period_5m">5m</a></li>
                <li id="chart_period_3m_h" name="3m" style="display: none;">  <a class="chart_str_period_3m">3m</a></li>
                <li id="chart_period_1m_h" name="1m" style="display: inline-block;">  <a class="chart_str_period_1m">1m</a></li>
                <li id="chart_period_line_h" name="line" style="display: inline-block;"><a class="chart_str_period_line">Line</a></li>
            </ul>
        </div>

        <!-- Periods -->
        

        <!-- Open TabBar -->
        

        <!-- Open ToolPanel -->
        

        <!-- Theme -->
        

        <!-- Settings -->
        <div id="chart_show_indicator" class="chart_toolbar_button chart_str_indicator_cap selected">INDICATOR</div>
        <div id="chart_show_tools" class="chart_toolbar_button chart_str_tools_cap selected">TOOLS</div>
        <div id="chart_toolbar_theme">
            <div class="chart_toolbar_label chart_str_theme_cap">THEME1</div>
            <a name="dark" class="chart_icon chart_icon_theme_dark selected" title="${L:l(lan,'红涨绿跌')}"></a>
            <a name="light" class="chart_icon chart_icon_theme_light" title="${L:l(lan,'绿涨红跌')}"></a>
        </div>
        <div class="chart_dropdown" id="chart_dropdown_settings">
            <div class="chart_dropdown_t"><a class="chart_str_settings">MORE</a></div>
            <div class="chart_dropdown_data" style="margin-left: -142px;">
                <table>
                <tbody>
                <tr id="chart_select_main_indicator">
                    <td class="chart_str_main_indicator">Main Indicator</td>
                    <td>
                        <ul>
                            <li><a name="MA" class="">MA</a></li>
                            <li><a name="EMA" class="">EMA</a></li>
                            <li><a name="BOLL" class="">BOLL</a></li>
                            <li><a name="SAR" class="">SAR</a></li>
                            <li><a name="NONE" class="">None</a></li>
                        </ul>
                    </td>
                </tr>
                <tr id="chart_select_chart_style">
                    <td class="chart_str_chart_style">Chart Style</td>
                    <td>
                        <ul>
                            <li><a class="">CandleStick</a></li>
                            <li><a>CandleStickHLC</a></li>
                            <li><a class="">OHLC</a></li>
                        </ul>
                    </td>
                </tr><tr id="chart_select_theme" style="display: none;">
                    <td class="chart_str_theme">Theme</td>
                    <td>
                        <ul>
                            <li><a name="dark" class="chart_icon chart_icon_theme_dark selected" title="${L:l(lan,'红涨绿跌')}"></a></li>
                            <li><a name="light" class="chart_icon chart_icon_theme_light" title="${L:l(lan,'绿涨红跌')}"></a></li>
                        </ul>
                    </td>
                </tr><tr id="chart_enable_tools" style="display: none;">
                    <td class="chart_str_tools">Tools</td>
                    <td>
                        <ul>
                            <li><a name="on" class="chart_str_on selected">On</a></li>
                            <li><a name="off" class="chart_str_off">Off</a></li>
                        </ul>
                    </td>
                </tr><tr id="chart_enable_indicator">
                    <td class="chart_str_indicator">Indicator</td>
                    <td>
                        <ul>
                            <li><a name="on" class="chart_str_on selected">On</a></li>
                            <li><a name="off" class="chart_str_off">Off</a></li>
                        </ul>
                    </td>
                </tr><tr>
                    <td></td>
                    <td>
                        <ul><li><a id="chart_btn_parameter_settings" class="chart_str_indicator_parameters">Indicator Parameters</a></li></ul>
                    </td>
                </tr></tbody></table>
            </div>
        </div>
        
        <div class="chart_dropdown market_chooser" id="chart_exchanges_setting_div" style="padding-left: 5px;">
			<div class="chart_dropdown_t">
			<a class="chart_exchanges_setting">MORE MARKETS</a>
			</div>
			<div class="chart_dropdown_data">
				<table>
          <tbody>
           <tr>
					    <td class="marketName_"><a href="${vip_domain}" target="_blank" class="dark">${L:l(lan,'比特全球')}</a>
					    </td>
					    <td>
					        <ul>
					        	<c:forEach items="${markets}" var ="market">
					        		<li><a name="vip${market.key}" onclick="window.top.location.href='${vip_domain }/markets/${market.key}'" title="${market.value.numberBi}/${market.value.exchangeBi}"> ${market.value.numberBi}/${market.value.exchangeBi}</a></li>
					        	</c:forEach>
					        </ul>
					    </td>
					</tr>
          </tbody>
        </table>
			</div>
		</div>
		
		<div class="chart_dropdown" id="chart_language_setting_div" style="padding-left: 5px;">
			<div class="chart_dropdown_t">
			<a class="chart_language_setting">LANGUAGE</a>
			</div>
			<div class="chart_dropdown_data" style="padding-top: 15px;">
				<ul>
					<li style="height: 25px;"><a name="zh-cn">简体中文(zh-CN)</a></li>
					<li style="height: 25px;"><a name="en-us">English(en-US)</a></li>
					<li style="height: 25px;"><a name="zh-tw">繁體中文(zh-HK)</a></li>
				</ul>
			</div>
			
		</div>
		
        <div id="chart_updated_time">
            <span class="chart_str_updated">Updated</span>
            <span id="chart_updated_time_text">0s</span>
            <span class="chart_str_ago">ago</span>
        </div>
    </div>
    <!-- ToolPanel -->
    <div id="chart_toolpanel" style="display: inline; left: 0px; top: 30px; width: 32px; height: 949px;">
        <div class="chart_toolpanel_separator"></div>
        <div class="chart_toolpanel_button">
            <div class="chart_toolpanel_icon" id="chart_Cursor" name="Cursor"></div>
            <div class="chart_toolpanel_tip chart_str_cursor">Cursor</div>
        </div>
        <div class="chart_toolpanel_button selected">
            <div class="chart_toolpanel_icon" id="chart_CrossCursor" name="CrossCursor"></div>
            <div class="chart_toolpanel_tip chart_str_cross_cursor">Cross Cursor</div>
        </div>
        <div class="chart_toolpanel_button">
            <div class="chart_toolpanel_icon" id="chart_SegLine" name="SegLine"></div>
            <div class="chart_toolpanel_tip chart_str_seg_line">Trend Line</div>
        </div>
        <div class="chart_toolpanel_button">
            <div class="chart_toolpanel_icon" id="chart_StraightLine" name="StraightLine"></div>
            <div class="chart_toolpanel_tip chart_str_straight_line">Extended</div>
        </div>
        <div class="chart_toolpanel_button">
            <div class="chart_toolpanel_icon" id="chart_RayLine" name="RayLine"></div>
            <div class="chart_toolpanel_tip chart_str_ray_line">Ray</div>
        </div>
        <div class="chart_toolpanel_button">
            <div class="chart_toolpanel_icon" id="chart_ArrowLine" name="ArrowLine"></div>
            <div class="chart_toolpanel_tip chart_str_arrow_line">Arrow</div>
        </div>
        <div class="chart_toolpanel_button">
            <div class="chart_toolpanel_icon" id="chart_HoriSegLine" name="HoriSegLine"></div>
            <div class="chart_toolpanel_tip chart_str_horz_seg_line">Horizontal Line</div>
        </div>
        <div class="chart_toolpanel_button">
            <div class="chart_toolpanel_icon" id="chart_HoriStraightLine" name="HoriStraightLine"></div>
            <div class="chart_toolpanel_tip chart_str_horz_straight_line">Horizontal Extended</div>
        </div>
        <div class="chart_toolpanel_button">
            <div class="chart_toolpanel_icon" id="chart_HoriRayLine" name="HoriRayLine"></div>
            <div class="chart_toolpanel_tip chart_str_horz_ray_line">Horizontal Ray</div>
        </div>
        <div class="chart_toolpanel_button">
            <div class="chart_toolpanel_icon" id="chart_VertiStraightLine" name="VertiStraightLine"></div>
            <div class="chart_toolpanel_tip chart_str_vert_straight_line">Vertical Extended</div>
        </div>
        <div class="chart_toolpanel_button">
            <div class="chart_toolpanel_icon" id="chart_PriceLine" name="PriceLine"></div>
            <div class="chart_toolpanel_tip chart_str_price_line">Price Line</div>
        </div>
        <div class="chart_toolpanel_button">
            <div class="chart_toolpanel_icon" id="chart_TriParallelLine" name="TriParallelLine"></div>
            <div class="chart_toolpanel_tip chart_str_tri_parallel_line">Parallel Channel</div>
        </div>
        <div class="chart_toolpanel_button">
            <div class="chart_toolpanel_icon" id="chart_BiParallelLine" name="BiParallelLine"></div>
            <div class="chart_toolpanel_tip chart_str_bi_parallel_line">Parallel Lines</div>
        </div>
        <div class="chart_toolpanel_button">
            <div class="chart_toolpanel_icon" id="chart_BiParallelRayLine" name="BiParallelRayLine"></div>
            <div class="chart_toolpanel_tip chart_str_bi_parallel_ray">Parallel Rays</div>
        </div>
        <div class="chart_toolpanel_button">
            <div class="chart_toolpanel_icon" id="chart_DrawFibRetrace" name="DrawFibRetrace"></div>
            <div class="chart_toolpanel_tip chart_str_fib_retrace">Fibonacci Retracements</div>
        </div>
        <div class="chart_toolpanel_button">
            <div class="chart_toolpanel_icon" id="chart_DrawFibFans" name="DrawFibFans"></div>
            <div class="chart_toolpanel_tip chart_str_fib_fans">Fibonacci Fans</div>
        </div>
         <div style="padding-left: 3px;padding-top: 10px;">
            <button style="color: red;" id="clearCanvas" title="Clear All" >X</button>
        </div>
    </div>


    <!-- Canvas Group -->
    <div id="chart_canvasGroup" style="left: 33px; top: 30px; width: 2500px; height: 926px;">
        <canvas class="chart_canvas" id="chart_mainCanvas" width="2527" height="926" style="cursor: none;"></canvas>
        <canvas class="chart_canvas" id="chart_overlayCanvas" width="2527" height="926" style="cursor: none;"></canvas>
     </div>
     <!-- TabBar -->
    <div id="chart_tabbar" style="display: block; position:relative; left: 33px; top: 956px; width: 2527px; height: 22px;">
        
        <ul>
            <li><a name="MACD" class="">MACD</a></li>
            <li><a name="KDJ" class="">KDJ</a></li>
            <li><a name="StochRSI" class="">StochRSI</a></li>
            <li><a name="RSI" class="">RSI</a></li>
            <li><a name="DMI" class="">DMI</a></li>
            <li><a name="OBV" class="">OBV</a></li>
            <li><a name="BOLL" class="">BOLL</a></li>
            <li><a name="SAR" class="">SAR</a></li>
            <li><a name="DMA" class="">DMA</a></li>
            <li><a name="TRIX" class="">TRIX</a></li>
            <li><a name="BRAR" class="">BRAR</a></li>
            <li><a name="VR" class="">VR</a></li>
            <li><a name="EMV" class="">EMV</a></li>
            <li><a name="WR" class="">WR</a></li>
            <li><a name="ROC" class="">ROC</a></li>
            <li><a name="MTM" class="">MTM</a></li>
            <li><a name="PSY">PSY</a></li>
        </ul>
        
    </div>
	
    <!-- Parameter Settings -->
    <div id="chart_parameter_settings" style="left: 960px; top: 281px;">
        <h2 class="chart_str_indicator_parameters">Indicator Parameters</h2>
        <table>
            <tbody><tr>
                <th>MA</th>
                <td><input name="MA"><input name="MA"><input name="MA"><input name="MA"><br><input name="MA"><input name="MA"></td>
                <td><button class="chart_str_default">default</button></td>

                <th>DMA</th>
                <td><input name="DMA"><input name="DMA"><input name="DMA"></td>
                <td><button class="chart_str_default">default</button></td>
            </tr>

            <tr>
                <th>EMA</th>
                <td><input name="EMA"><input name="EMA"><input name="EMA"><input name="EMA"><br><input name="EMA"><input name="EMA"></td>
                <td><button class="chart_str_default">default</button></td>

                <th>TRIX</th>
                <td><input name="TRIX"><input name="TRIX"></td>
                <td><button class="chart_str_default">default</button></td>
            </tr>

            <tr>
                <th>VOLUME</th>
                <td><input name="VOLUME"><input name="VOLUME"></td>
                <td><button class="chart_str_default">default</button></td>

                <th>BRAR</th>
                <td><input name="BRAR"></td>
                <td><button class="chart_str_default">default</button></td>
            </tr>

            <tr>
                <th>MACD</th>
                <td><input name="MACD"><input name="MACD"><input name="MACD"></td>
                <td><button class="chart_str_default">default</button></td>

                <th>VR</th>
                <td><input name="VR"><input name="VR"></td>
                <td><button class="chart_str_default">default</button></td>
            </tr>

            <tr>
                <th>KDJ</th>
                <td><input name="KDJ"><input name="KDJ"><input name="KDJ"></td>
                <td><button class="chart_str_default">default</button></td>

                <th>EMV</th>
                <td><input name="EMV"><input name="EMV"></td>
                <td><button class="chart_str_default">default</button></td>
            </tr>

            <tr>
                <th>StochRSI</th>
                <td><input name="StochRSI"><input name="StochRSI"><input name="StochRSI"><input name="StochRSI"></td>
                <td><button class="chart_str_default">default</button></td>

                <th>WR</th>
                <td><input name="WR"><input name="WR"></td>
                <td><button class="chart_str_default">default</button></td>
            </tr>

            <tr>
                <th>RSI</th>
                <td><input name="RSI"><input name="RSI"><input name="RSI"></td>
                <td><button class="chart_str_default">default</button></td>

                <th>ROC</th>
                <td><input name="ROC"><input name="ROC"></td>
                <td><button class="chart_str_default">default</button></td>
            </tr>

            <tr>
                <th>DMI</th>
                <td><input name="DMI"><input name="DMI"></td>
                <td><button class="chart_str_default">default</button></td>

                <th>MTM</th>
                <td><input name="MTM"><input name="MTM"></td>
                <td><button class="chart_str_default">default</button></td>
            </tr>

            <tr>
                <th>OBV</th>
                <td><input name="OBV"></td>
                <td><button class="chart_str_default">default</button></td>

                <th>PSY</th>
                <td><input name="PSY"><input name="PSY"></td>
                <td><button class="chart_str_default">default</button></td>
            </tr>

            <tr>
                <th>BOLL</th>
                <td><input name="BOLL"></td>
                <td><button class="chart_str_default">default</button></td>
            </tr>
        </tbody></table>
        <div id="close_settings"><a class="chart_str_close">CLOSE</a></div>
    </div>

    <!-- Loading -->
    <div id="chart_loading" class="chart_str_loading" style="left: 1180px; top: 232px;">Loading...</div>

</div> <!-- End Of ChartContainer -->

<div style="display: none">
    <form style="display: inline" id="chart_input_interface">
        <input style="display: inline" type="text" id="chart_input_interface_text">
        <input style="display: inline" type="submit" id="chart_input_interface_submit">
    </form>
    <form style="display: inline" id="chart_output_interface">
        <input style="display: inline" type="text" id="chart_output_interface_text">
        <input style="display: inline" type="submit" id="chart_output_interface_submit">
    </form>
</div>
<div style="display: none" id="chart_language_switch_tmp">

    <span name="chart_str_period" zh_tw="週期" zh_cn="周期" en_us="TIME">
    <span name="chart_str_period_line" zh_tw="分時" zh_cn="分时" en_us="Line">
    <span name="chart_str_period_1m" zh_tw="1分钟" zh_cn="1分钟" en_us="1m">
    <span name="chart_str_period_3m" zh_tw="3分钟" zh_cn="3分钟" en_us="3m">
    <span name="chart_str_period_5m" zh_tw="5分钟" zh_cn="5分钟" en_us="5m">
    <span name="chart_str_period_15m" zh_tw="15分钟" zh_cn="15分钟" en_us="15m">
    <span name="chart_str_period_30m" zh_tw="30分钟" zh_cn="30分钟" en_us="30m">
    <span name="chart_str_period_1h" zh_tw="1小時" zh_cn="1小时" en_us="1h">
    <span name="chart_str_period_2h" zh_tw="2小時" zh_cn="2小时" en_us="2h">
    <span name="chart_str_period_4h" zh_tw="4小時" zh_cn="4小时" en_us="4h">
    <span name="chart_str_period_6h" zh_tw="6小時" zh_cn="6小时" en_us="6h">
    <span name="chart_str_period_12h" zh_tw="12小時" zh_cn="12小时" en_us="12h">
    <span name="chart_str_period_1d" zh_tw="日線" zh_cn="日线" en_us="1d">
    <span name="chart_str_period_3d" zh_tw="3日" zh_cn="3日" en_us="3d">
    <span name="chart_str_period_1w" zh_tw="周線" zh_cn="周线" en_us="1w">

    <span name="chart_str_settings" zh_tw="更多" zh_cn="更多" en_us="MORE">
    <span name="chart_setting_main_indicator" zh_tw="均線設置" zh_cn="均线设置" en_us="Main Indicator">
    <span name="chart_setting_main_indicator_none" zh_tw="關閉均線" zh_cn="关闭均线" en_us="None">
    <span name="chart_setting_indicator_parameters" zh_tw="指標參數設置" zh_cn="指标参数设置" en_us="Indicator Parameters">

    <span name="chart_str_chart_style" zh_tw="主圖樣式" zh_cn="主图样式" en_us="Chart Style">
    <span name="chart_str_main_indicator" zh_tw="主指標" zh_cn="主指标" en_us="Main Indicator">
    <span name="chart_str_indicator" zh_tw="技術指標" zh_cn="技术指标" en_us="Indicator">
    <span name="chart_str_indicator_cap" zh_tw="技術指標" zh_cn="技术指标" en_us="INDICATOR">
    <span name="chart_str_tools" zh_tw="畫線工具" zh_cn="画线工具" en_us="Tools">
    <span name="chart_str_tools_cap" zh_tw="畫線工具" zh_cn="画线工具" en_us="TOOLS">
    <span name="chart_str_theme" zh_tw="主題選擇" zh_cn="主题选择" en_us="Theme">
    <span name="chart_str_theme_cap" zh_tw="主題選擇" zh_cn="主题选择" en_us="THEME">
    
    <span name="chart_language_setting" zh_tw="語言(LANG)" zh_cn="语言(LANG)" en_us="LANGUAGE">
    <span name="chart_exchanges_setting" zh_tw="更多市場" zh_cn="更多市场" en_us="MORE MARKETS">
    <span name="chart_othercoin_setting" zh_tw="其它市場" zh_cn="其它市场" en_us="OTHER MARKETS">
    

    <span name="chart_str_none" zh_tw="關閉" zh_cn="关闭" en_us="None">

    <span name="chart_str_theme_dark" zh_tw="深色主題" zh_cn="深色主题" en_us="Dark">
    <span name="chart_str_theme_light" zh_tw="淺色主題" zh_cn="浅色主题" en_us="Light">

    <span name="chart_str_on" zh_tw="開啟" zh_cn="开启" en_us="On">
    <span name="chart_str_off" zh_tw="關閉" zh_cn="关闭" en_us="Off">

    <span name="chart_str_close" zh_tw="關閉" zh_cn="关闭" en_us="CLOSE">
    <span name="chart_str_default" zh_tw="默認值" zh_cn="默认值" en_us="default">

    <span name="chart_str_loading" zh_tw="正在讀取數據..." zh_cn="正在读取数据..." en_us="Loading...">

    <span name="chart_str_indicator_parameters" zh_tw="指標參數設置" zh_cn="指标参数设置" en_us="Indicator Parameters">

    <span name="chart_str_cursor" zh_tw="光標" zh_cn="光标" en_us="Cursor">
    <span name="chart_str_cross_cursor" zh_tw="十字光標" zh_cn="十字光标" en_us="Cross Cursor">
    <span name="chart_str_seg_line" zh_tw="線段" zh_cn="线段" en_us="Trend Line">
    <span name="chart_str_straight_line" zh_tw="直線" zh_cn="直线" en_us="Extended">
    <span name="chart_str_ray_line" zh_tw="射線" zh_cn="射线" en_us="Ray">
    <span name="chart_str_arrow_line" zh_tw="箭頭" zh_cn="箭头" en_us="Arrow">
    <span name="chart_str_horz_seg_line" zh_tw="水平線段" zh_cn="水平线段" en_us="Horizontal Line">
    <span name="chart_str_horz_straight_line" zh_tw="水平直線" zh_cn="水平直线" en_us="Horizontal Extended">
    <span name="chart_str_horz_ray_line" zh_tw="水平射線" zh_cn="水平射线" en_us="Horizontal Ray">
    <span name="chart_str_vert_straight_line" zh_tw="垂直直線" zh_cn="垂直直线" en_us="Vertical Extended">
    <span name="chart_str_price_line" zh_tw="價格線" zh_cn="价格线" en_us="Price Line">
    <span name="chart_str_tri_parallel_line" zh_tw="價格通道線" zh_cn="价格通道线" en_us="Parallel Channel">
    <span name="chart_str_bi_parallel_line" zh_tw="平行直線" zh_cn="平行直线" en_us="Parallel Lines">
    <span name="chart_str_bi_parallel_ray" zh_tw="平行射線" zh_cn="平行射线" en_us="Parallel Rays">
    <span name="chart_str_fib_retrace" zh_tw="斐波納契回調" zh_cn="斐波纳契回调" en_us="Fibonacci Retracements">
    <span name="chart_str_fib_fans" zh_tw="斐波納契扇形" zh_cn="斐波纳契扇形" en_us="Fibonacci Fans">

    <span name="chart_str_updated" zh_tw="更新於" zh_cn="更新于" en_us="Updated">
    <span name="chart_str_ago" zh_tw="前" zh_cn="前" en_us="ago">
</span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></span></div>
<script src="${static_domain }/statics/js/kline/kline.assistant.js?V${CH_VERSON }"></script>
<script type="text/javascript">  
	$(function(){
		
		$("#orderbook").on("click",".row",function(){
   		$(window.top.document.body).find("#buyUnitPrice,#sellUnitPrice").val($(this).find(".price").text()).focus().blur();
			var depthNumber = 0 ;
    		if($(this).parents("#asks").length > 0){
    			for(i = $(this).index() ; i < $("#asks .row").length; i ++){
    				depthNumber = parseFloat(depthNumber) + parseFloat($("#asks .row").eq(i).find(".amount").text());
    			}
    		}else{
    			for(i = 0; i <= $(this).index(); i ++){
    				depthNumber = parseFloat(depthNumber) + parseFloat($("#bids .row").eq(i).find(".amount").text());
    			}
    		}
   		if(window.top.location.pathname.indexOf('ltc') != -1){
   		  $(window.top.document.body).find("#buyNumber,#sellNumber").val(depthNumber.toFixed(2)).focus().blur();
   		}else{
   			$(window.top.document.body).find("#buyNumber,#sellNumber").val(depthNumber.toFixed(3)).focus().blur();
   		}
   	});
		$(".trades_list").on("click","ul",function(){
   		$(window.top.document.body).find("#buyUnitPrice,#sellUnitPrice").val($(this).find("li:eq(1)").text()).focus().blur();
   	});
		$("#price").on("click",function(){
   		$(window.top.document.body).find("#buyUnitPrice,#sellUnitPrice").val($(this).text()).focus().blur();
   	});
	});
	
	
	
	
	
	
	var kline = new Kline();
	kline.symbol="${symbol }";
	kline.basePath="${static_domain }";
	kline.depthChannel="${fn:toLowerCase(numberBi)}_${fn:toLowerCase(exchangeBi)}_depth";
	kline.tradeChannel="${fn:toLowerCase(numberBi)}_${fn:toLowerCase(exchangeBi)}_lasttrades";
	kline.tradesLimit=${tradesLimit};
	kline.dateInit(${currentTimeMillis});
	
	kline.moneyDecimal={
			<c:forEach items="${markets}" var ="market">
				${market.key}:${market.value.exchangeBixDian},
	     	</c:forEach>
	};  
	
	kline.coinDecimal = {
			<c:forEach items="${markets}" var ="market">
			${market.key}:${market.value.numberBixDian},
     		</c:forEach>
	}
	/* var websocketInterval = setInterval("keepalive(kline.klineWebsocket)",10000);
	
	function keepalive(ws){
	 var time = new Date().getTime();
		 if (ws.bufferedAmount == 0) {
		 	ws.send("{time:"+time+"}");
	     }
	} */
	try{
	    var oldDomain=document.domain;
	    var numm1=oldDomain.indexOf('${baseDomain}');
	    document.domain = oldDomain.substring(numm1,oldDomain.length)
	  } catch(msg) {
	    document.domain = '${baseDomain}';  
	  }
</script>
<script src="${static_domain }/statics/js/kline/kline.draw.js?V${CH_VERSON }"></script>

</body>
</html>