import { UDFCompatibleDatafeed } from './bundle.js'
import { DOMAIN_TRANS,DOMAIN_VIP } from '../../conf'
import cookie from 'js-cookie';
const jstz = require('jstimezonedetect');

const KlineConfig = (currentMarket,index,skin) => {
  let skinCss,color1,bgColor;
  let localeLang = '',interval = '15';
  const sessionInterval = localStorage.getItem("tradingview.interval");
  if(sessionInterval){
    interval = sessionInterval
  }else{
    localStorage.setItem("tradingview.interval",15)
  }
  const klineSkin = cookie.get("skin")||skin
  switch(cookie.get("zlan")){
    case "cn":
      localeLang = "zh"
      break;
    case "hk":
      localeLang = "zh_TW"
      break;
    case "en":
      localeLang = "en"
      break;
    case"kr":
    localeLang = "ko"
    break;
    case"jp":
    localeLang = "ja"
  }
  if(cookie.get("zlan")==undefined){
    localeLang="en"
  }
  if(klineSkin == 'dark'){
    skinCss = 'night';
    color1 = '#3D4454'
    bgColor = '#121418'
  }else{
    skinCss = 'light';
    color1 = '#F1F1F3'
    bgColor = '#FFFFFF'
  }
  const fetchUrl = `${DOMAIN_TRANS}/markets/klineLastData`;
  const timezone = jstz.determine().name()||"Etc/UTC";
  return {
            autosize:true,
            debug: false, // uncomment this line to see Library errors and warnings in the console
            fullscreen: false,
            details:false,
            watchlist:false,
            symbol: currentMarket,
            interval: interval,
            custom_css_url:`${skinCss}.css`,
            container_id:`kline-wrap-${index}`,
            timezone:"Etc/UTC",
            //	BEWARE: no trailing slash is expected in feed URL
            datafeed: new UDFCompatibleDatafeed(fetchUrl,1000,currentMarket),
            library_path:'/bw/src/charting_library/',
            locale:localeLang,
            //	Regression Trend-related functionality is not implemented yet, so it's hidden for a while
            drawings_access: { type: 'black', tools: [ { name: "Regression Trend" } ] },
            disabled_features: [
              "header_widget",
              "header_widget_dom_node",
              "header_saveload",
              "header_screenshot",
              "volume_force_overlay",
              "header_symbol_search",
              "header_undo_redo",
              "timeframes_toolbar",
              "show_hide_button_in_legend",
              "display_market_status",
              "legend_context_menu",
              "header_interval_dialog_button",
              "header_compare",
              "link_to_tradingview",
              "14851",
              "widget_logo",
            ],
            enabled_features: [
              "move_logo_to_main_pane",
              "hide_last_na_study_output",
              "keep_left_toolbar_visible_on_small_screens",
              "disable_resolution_rebuild",
            ],
            overrides: {
              "volumePaneSize": "medium",
              "paneProperties.background": bgColor,
              "paneProperties.vertGridProperties.color": "rgba(255,255,255,0)",
              "paneProperties.vertGridProperties.style": 0,
              "paneProperties.horzGridProperties.color": color1,
              "paneProperties.horzGridProperties.style": 0,
              "paneProperties.crossHairProperties.color": "#989898",
              "paneProperties.topMargin": 10,
              "scalesProperties.backgroundColor" : bgColor,
              "scalesProperties.lineColor" : "#737A8D",
              "scalesProperties.textColor" : "#737A8D",
            },
            studies_overrides: {
                "volume.volume.color.0": "#CE6144",
                "volume.volume.color.1": "#49A664",
                "volume.volume.transparency":100,
                "volume.volume ma.color": "#FF0000",
                "volume.volume ma.transparency": 100,
                "volume.volume ma.linewidth": 5,
                "volume.show ma": false,
                "bollinger bands.median.color": "#33FF88",
                "bollinger bands.upper.linewidth": 7,
            },
            charts_storage_url: 'http://saveload.btcwinex.com',
            charts_storage_api_version: "1.1",
            client_id: 'btcwinex.com',
            user_id: 'public_user_id',
        }
}

export default KlineConfig;








