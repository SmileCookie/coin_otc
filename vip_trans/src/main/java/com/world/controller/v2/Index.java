package com.world.controller.v2;

import com.tenstar.HTTPTcp;
import com.tenstar.Message;
import com.tenstar.RecordMessage;
import com.tenstar.robotConfig;
import com.world.cache.Cache;
import com.world.config.GlobalConfig;
import com.world.controller.IndexServer;
import com.world.dish.DishDataCacheService;
import com.world.model.Market;
import com.world.timer.auto.MarketDataWorker;
import com.world.util.cookie.CookieUtil;
import com.world.util.ip.IpUtil;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import com.world.web.action.UserAction;
import com.world.web.sso.session.SsoSessionManager;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.math.BigDecimal;

public class Index extends UserAction  {

    private IndexServer server = new IndexServer();
    @Page
    public void index(){

        String ip = IpUtil.getIp(request);
        CookieUtil cookieUtil = new CookieUtil(request,response);
        String lanCur = cookieUtil.getCookieValue(SsoSessionManager.LANGUAGE);
        if(!StringUtil.exist(lanCur)){
            try{
                String curLan = "cn";
                //国家
                String province = "";
                //是否是外网IP
                if(IpUtil.isIpv4(ip) && IpUtil.isPublicIp(ip)){
                    if(StringUtils.isNotBlank(Cache.Get("ip_" + ip))){
                        curLan = Cache.Get("ip_" + ip);
                    }else{
                        province = IpUtil.getProvinceBySina(ip);
                        if("中国".equals(province)){
                            curLan = "cn";
                        }else if("香港".equals(province) || "澳门".equals(province) || "台湾".equals(province)){
                            curLan = "hk";
                        }else{
                            curLan = "en";
                        }
                        Cache.Set("ip_" + ip, curLan, 5*60);
                    }
                }
                setAttr("lan", curLan);
                Cookie lanCookie = new Cookie(SsoSessionManager.LANGUAGE, curLan);
                lanCookie.setHttpOnly(true);
                //失效时间7天
                lanCookie.setMaxAge(60 * 60 * 24 * 7);
                lanCookie.setPath("/");
                lanCookie.setDomain(GlobalConfig.baseDomain);
                response.addCookie(lanCookie);
            }catch (Exception e){
                log.error("trans设置语言失败，失败信息为：",e);
            }
        }
        try {
            response.sendRedirect("/btc");
        } catch (IOException e) {
            log.error(e.toString(), e);
        }

    }

    /**
     * 比特币
     */
//    @Page(Viewer = "/cn/u/v2/index.jsp")
    public void btc(){
        request.setAttribute("serijavascripparam", Market.getMarket("btc_cny"));
        setAttr("coinType", "BTC");
        setAttr("coinType_s", "btc");
        setAttr("coinSymbol", "฿");
        setAttr("coinName", "比特币");
        setAttr("moneyType", "CNY");
        setAttr("moneyType_s", "cny");
        setAttr("moneySymbol", "￥");
        setAttr("moneyName", "人民币");
    }

    /**
     * 莱特币
     */
//    @Page(Viewer = "/cn/u/v2/index.jsp")
    public void ltc(){
        request.setAttribute("serijavascripparam", Market.getMarket("ltc_cny"));
        setAttr("coinType", "LTC");
        setAttr("coinType_s", "ltc");
        setAttr("coinSymbol", "Ł");
        setAttr("coinName", "莱特币");
        setAttr("moneyType", "CNY");
        setAttr("moneyType_s", "cny");
        setAttr("moneySymbol", "￥");
        setAttr("moneyName", "人民币");
    }

    /**
     * 以太币
     */
//    @Page(Viewer = "/cn/u/v2/index.jsp")
    public void eth(){
        request.setAttribute("serijavascripparam", Market.getMarket("eth_cny"));
        setAttr("coinType", "ETH");
        setAttr("coinType_s", "eth");
        setAttr("coinSymbol", "E");
        setAttr("coinName", "以太币");
        setAttr("moneyType", "CNY");
        setAttr("moneyType_s", "cny");
        setAttr("moneySymbol", "￥");
        setAttr("moneyName", "人民币");
    }

    /**
     * 以太币
     */
//    @Page(Viewer = "/cn/u/v2/index.jsp")
    public void ethbtc(){
        request.setAttribute("serijavascripparam", Market.getMarket("eth_btc"));
        setAttr("coinType", "ETH");
        setAttr("coinType_s", "eth");
        setAttr("coinSymbol", "E");
        setAttr("coinName", "以太币");
        setAttr("moneyType", "BTC");
        setAttr("moneyType_s", "btc");
        setAttr("moneySymbol", "฿");
        setAttr("moneyName", "比特币");
    }

    /**比特权
     */
//    @Page(Viewer = "/cn/u/v2/index.jsp")
    public void btq(){
        request.setAttribute("serijavascripparam", Market.getMarket("btq_cny"));
        setAttr("coinType", "BTQ");
        setAttr("coinType_s", "btq");
        setAttr("coinSymbol", "Q");
        setAttr("coinName", "比特权");
        setAttr("moneyType", "BTC");
        setAttr("moneyType_s", "btc");
        setAttr("moneySymbol", "฿");
        setAttr("moneyName", "比特币");
    }


    /**比特权自动交易
     */
//    @Page(Viewer = "/cn/u/auto.jsp" )
    public void btcAuto(){
        request.setAttribute("serijavascripparam", Market.getMarket("btc_cny"));
    }

    /**比特权自动交易
     */
//    @Page(Viewer = "/cn/u/auto.jsp" )
    public void ltqAuto(){
        request.setAttribute("serijavascripparam", Market.getMarket("ltc_cny"));
    }

    /**以太币自动交易
     */
//    @Page(Viewer = "/cn/u/auto.jsp" )
    public void ethAuto(){
        request.setAttribute("serijavascripparam", Market.getMarket("eth_cny"));
    }

    /**以太币自动交易
     */
//    @Page(Viewer = "/cn/u/auto.jsp" )
    public void ethbtcAuto(){
        request.setAttribute("serijavascripparam", Market.getMarket("eth_btc"));
    }

    /**比特权自动交易
     */
//    @Page(Viewer = "/cn/u/auto.jsp" )
    public void btqAuto(){
        request.setAttribute("serijavascripparam", Market.getMarket("btq_cny"));
    }


    /**
     * 比特权专业
     */
//    @Page(Viewer = "/cn/u/index.jsp" , Cache = 60)
    public void btqPro(){
        request.setAttribute("serijavascripparam", Market.getMarket("btq_cny"));
    }

//    @Page(Viewer = "/cn/u/entrustMore.jsp")
    public void entrustMore() {
        String userId = userId(false , true);
        setAttr("useSafe", Cache.Get("use_pwd_"+userId)==null?true:false);
        setAttr("isBuy", param("isBuy"));

        request.setAttribute("serijavascripparam", Market.getMarket("btc_cny"));
    }
//    @Page(Viewer = "/cn/u/cancleMore.jsp")
    public void cancelMore() {
        //String userId = userId(false , true);
        //	setAttr("useSafe", useSafePwd(userId));
        setAttr("isBuy", param("isBuy"));

        request.setAttribute("serijavascripparam", Market.getMarket("btc_cny"));
    }


    /**系统配置
     */
//    @Page(Viewer = "/cn/u/autoSys.jsp" )
    public void autoSysBtc(){
        request.setAttribute("serijavascripparam", Market.getMarket("btc_cny"));
        Object obj=Cache.Get("systemDefaultRobotConfigBTC");
        com.tenstar.robotConfig	 rc=(com.tenstar.robotConfig)HTTPTcp.StringToObject(obj.toString());
        request.setAttribute("rc", rc);


    }

    /**系统配置
     */
//    @Page(Viewer = "/cn/u/autoSysLtc.jsp" )
    public void autoSysLtc(){
        request.setAttribute("serijavascripparam", Market.getMarket("ltc_cny"));
        Object obj=Cache.Get("systemDefaultRobotConfigLTC");
        com.tenstar.robotConfig	 rc=(com.tenstar.robotConfig)HTTPTcp.StringToObject(obj.toString());
        request.setAttribute("rc", rc);
    }

    /**系统配置
     */
//    @Page(Viewer = "/cn/u/autoSysEth.jsp" )
    public void autoSysEth(){
        request.setAttribute("serijavascripparam", Market.getMarket("eth_cny"));
        Object obj=Cache.Get("systemDefaultRobotConfigETH");
        com.tenstar.robotConfig	 rc=(com.tenstar.robotConfig)HTTPTcp.StringToObject(obj.toString());
        request.setAttribute("rc", rc);
    }

    /**系统配置
     */
//    @Page(Viewer = "/cn/u/autoSysEthBtc.jsp" )
    public void autoSysEthBtc(){
        request.setAttribute("serijavascripparam", Market.getMarket("eth_btc"));
        Object obj=Cache.Get("systemDefaultRobotConfigETHBTC");
        com.tenstar.robotConfig	 rc=(com.tenstar.robotConfig)HTTPTcp.StringToObject(obj.toString());
        request.setAttribute("rc", rc);
    }

    /**系统配置
     */
//    @Page(Viewer = ".xml" )
    public void saveautoSys(){
//		if(!IsLogin()){
//			   toLogin();
//			   return;
//		}

        Market m=Market.getMarkeByName(GetPrama(0));
        if(m==null){
            Write("",false,L("错误的市场"));
            return;
        }
        try{
            int userid=userId();
            int webid=m.webId;

            robotConfig rc=new robotConfig();



            rc.setIsStart(Integer.parseInt(request.getParameter("isStart")));//总数量
            rc.setIsStartTrade(Integer.parseInt(request.getParameter("isStartTrade")));
            rc.setEntrustQuShi(Integer.parseInt(request.getParameter("entrustQuShi")));//总数量

            rc.setSafePriceQuJian(Integer.parseInt(request.getParameter("safePriceQuJian")));//总数量

            rc.setEntrustRobotQuJian(Integer.parseInt(request.getParameter("entrustRobotQuJian")));//总数量
            rc.setBaseShowAdd(Long.parseLong(request.getParameter("baseShowAdd")));//总数量

            //以下是买档 外围配置
            rc.setNumberTotalBuyOut(Integer.parseInt(request.getParameter("numberTotalBuyOut")));//总数量

            rc.setMaxDangweiBuyOut(Integer.parseInt(request.getParameter("maxDangweiBuyOut")));//总数量
            rc.setLowPriceBuyOut(Integer.parseInt(request.getParameter("lowPriceBuyOut")));//总数量
            rc.setTimesSpaceBuyOut(Integer.parseInt(request.getParameter("timesSpaceBuyOut")));//总数量
            rc.setSplitNumberBuyOut(Integer.parseInt(request.getParameter("splitNumberBuyOut")));//总数量
            rc.setDuiQiBaiFenBiBuyOut(Integer.parseInt(request.getParameter("duiQiBaiFenBiBuyOut")));//总数量



            //以下是卖档 外围配置
            rc.setNumberTotalSellOut(Integer.parseInt(request.getParameter("numberTotalSellOut")));//总数量
            rc.setMaxDangweiSellOut(Integer.parseInt(request.getParameter("maxDangweiSellOut")));//总数量
            rc.setHighPriceSellOut(Integer.parseInt(request.getParameter("highPriceSellOut")));//总数量
            rc.setTimesSpaceSellOut(Integer.parseInt(request.getParameter("timesSpaceSellOut")));//总数量
            rc.setSplitNumberSellOut(Integer.parseInt(request.getParameter("splitNumberSellOut")));//总数量
            rc.setDuiQiBaiFenBiSellOut(Integer.parseInt(request.getParameter("duiQiBaiFenBiSellOut")));//总数量



            //以下是买档 内围配置
            rc.setNumberTotalBuyIn(Integer.parseInt(request.getParameter("numberTotalBuyIn")));//总数量
            rc.setMaxDangweiBuyIn(Integer.parseInt(request.getParameter("maxDangweiBuyIn")));//总数量
            rc.setTimesSpaceBuyIn(Integer.parseInt(request.getParameter("timesSpaceBuyIn")));//总数量
            rc.setSplitNumberBuyIn(Integer.parseInt(request.getParameter("splitNumberBuyIn")));//总数量
            rc.setDuiQiBaiFenBiBuyIn(Integer.parseInt(request.getParameter("duiQiBaiFenBiBuyIn")));//总数量

            //以下是卖档 内围配置
            rc.setNumberTotalSellIn(Integer.parseInt(request.getParameter("numberTotalSellIn")));//总数量
            rc.setMaxDangweiSellIn(Integer.parseInt(request.getParameter("maxDangweiSellIn")));//总数量
            rc.setTimesSpaceSellIn(Integer.parseInt(request.getParameter("timesSpaceSellIn")));//总数量
            rc.setSplitNumberSellIn(Integer.parseInt(request.getParameter("splitNumberSellIn")));//总数量
            rc.setDuiQiBaiFenBiSellIn(Integer.parseInt(request.getParameter("duiQiBaiFenBiSellIn")));//总数量



            //每日的总成交量
            rc.setTotalTransEveryDay(Long.parseLong(request.getParameter("totalTransEveryDay")));//总数量

            rc.setTimeSpaceTM(Integer.parseInt(request.getParameter("timeSpaceTM")));//总数量

            log.info("信息");
            try{
                String rtn= server.setSystemAuto(rc,m);
                if(rtn.equals("ok"))
                    Write(rtn,true,rtn+"");
                else
                    Write(rtn,false,rtn+"");

            }catch(Exception ex2){
                Write(L("保存失败！"),false,"");
            }
        }catch(Exception ex){
            log.error(ex.toString(), ex);
        }
    }




    /**系统配置
     */
//    @Page(Viewer = "/cn/u/autoUser.jsp" )
    public void autoUserBtc(){
        request.setAttribute("serijavascripparam", Market.getMarket("btc_cny"));

        int userId=userId();

        Object obj=Cache.Get("userAutoConfigBTC"+userId);

        com.tenstar.UserConfig	 rc;
        if(obj!=null)
            rc=(com.tenstar.UserConfig)HTTPTcp.StringToObject(obj.toString());
        else
            rc=new com.tenstar.UserConfig();
        request.setAttribute("rc", rc);

    }

    /**系统配置
     */
//    @Page(Viewer = "/cn/u/autoUserLtc.jsp" )
    public void autoUserLtc(){
        request.setAttribute("serijavascripparam", Market.getMarket("btc_cny"));
        int userId=userId();
        Object obj=Cache.Get("userAutoConfigLTC"+userId);
        com.tenstar.UserConfig	 rc;
        if(obj!=null)
            rc=(com.tenstar.UserConfig)HTTPTcp.StringToObject(obj.toString());
        else
            rc=new com.tenstar.UserConfig();
        request.setAttribute("rc", rc);
    }
    /**系统配置
     */
//    @Page(Viewer = ".xml" )
    public void saveAutoUser(){
//		if(!IsLogin()){
//			   toLogin();
//			   return;
//		}

        Market m=Market.getMarkeByName(GetPrama(0));
        if(m==null){
            Write("",false,L("错误的市场"));
            return;
        }
        try{
            int userid=userId();
            int webid=m.webId;
            int uierid=userId();
            com.tenstar.UserConfig rc=new com.tenstar.UserConfig();



            rc.setIsStart(Integer.parseInt(request.getParameter("isStart")));//总数量

            rc.setEntrustQuShi(Integer.parseInt(request.getParameter("entrustQuShi")));//总数量

            rc.setSafePriceQuJian(Integer.parseInt(request.getParameter("safePriceQuJian")));//总数量

            rc.setUserId(userid);
            rc.setWebId(8);



            rc.setEntrustQuJian(Integer.parseInt(request.getParameter("entrustQuJian")));//总数量


            rc.setExtrustMaxBuy(BigDecimal.valueOf(Double.parseDouble(request.getParameter("extrustMaxBuy"))));//总数量
            rc.setEntrustMaxDangWei(BigDecimal.valueOf(Double.parseDouble(request.getParameter("entrustMaxDangWei"))));//总数量


            //针对已经买入的订单部分，上涨获利设置可以卖出，如果反向下跌一定额度需要卖出止损
            rc.setMaxUpToSell(BigDecimal.valueOf(Double.parseDouble(request.getParameter("maxUpToSell"))));//总数量
            rc.setMinUpToSell(BigDecimal.valueOf(Double.parseDouble(request.getParameter("minUpToSell"))));//总数量
            rc.setMaxDownToSell(BigDecimal.valueOf(Double.parseDouble(request.getParameter("maxDownToSell"))));//总数量
            rc.setMaxUpPriceSpaceToCancle(BigDecimal.valueOf(Double.parseDouble(request.getParameter("maxUpPriceSpaceToCancle"))));//总数量


            //针对主动做空卖出的部分   下跌趋势的时候会主动做空卖出，卖出后的订单遵循如下规则进行买入补仓，如果上涨一定额度需要止损即使补仓买入
            rc.setMaxDownToBuy(BigDecimal.valueOf(Double.parseDouble(request.getParameter("maxDownToBuy"))));//总数量
            rc.setMinDownToBuy(BigDecimal.valueOf(Double.parseDouble(request.getParameter("minDownToBuy"))));//总数量
            rc.setMaxUpToBuy(BigDecimal.valueOf(Double.parseDouble(request.getParameter("maxUpToBuy"))));//总数量
            rc.setMaxDownPriceSpaceToCancle(BigDecimal.valueOf(Double.parseDouble(request.getParameter("maxDownPriceSpaceToCancle"))));//总数量

            rc.setMaxTimeToTransNoMoney(Integer.parseInt(request.getParameter("maxTimeToTransNoMoney")));//总数量
            rc.setTimeSpace(Integer.parseInt(request.getParameter("timeSpace")));//总数量
            rc.setAutoName("userAutoConfig"+uierid);

            log.info("信息");
            try{
                String rtn= server.setUserAuto(rc,m);
                if(rtn.equals("ok"))
                    Write(rtn,true,rtn+"");
                else
                    Write(rtn,false,rtn+"");

            }catch(Exception ex2){
                Write(L("保存失败！"),false,"");
            }
        }catch(Exception ex){
            log.error(ex.toString(), ex);
        }
    }

    @Page(Viewer=JSON)
    public void market() {
        StringBuilder json = new StringBuilder("");
        json.append("[{");
        String type = param("type");
        if(StringUtils.isNotEmpty(type)) {
            Market market = Market.getMarket(type);
            json.append("\"exchangeBi\":\"" + market.exchangeBi + "\"");
            json.append(",\"exchangeBiNote\":\"" + market.exchangeBiNote + "\"");
            json.append(",\"exchangeBixDian\":\"" + market.exchangeBixDian + "\"");
            json.append(",\"numberBixNormal\":\"" + market.numberBixNormal + "\"");
            json.append(",\"numberBixShow\":\"" + market.numberBixShow + "\"");
            json.append(",\"numberBixDian\":\"" + market.numberBixDian + "\"");
            json.append(",\"entrustUrlBase\":\"" + market.entrustUrlBase + "\"");
            json.append(",\"market\":\"" + market.market + "\"");
            json.append(",\"exchangeBixShow\":\"" + market.exchangeBixShow + "\"");
            json.append(",\"exchangeBixNormal\":\"" + market.exchangeBixNormal + "\"");
        }
        json.append("}]");
        json("success...",true,json.toString(),true);

    }

    /**
     * BW.COM 当前算力
     * JUA.COM 累计理财总额、日利率
     * 当前BTC行情
     */
    @Page(Viewer = JSON)
    public void jsonData(){
        try{
//			 //当前BTC行情
//			 String data=Cache.Get("btc_cny_hotdata2");
//			 if(data==null){
//				  Market m=Market.getMarkeByName("btc_cny");
//				  RecordMessage myObj = new RecordMessage();
//				  String param=HTTPTcp.ObjectToString(myObj);
//			      String rtn=HTTPTcp.Post(m.ip,m.port,"/getticker",param);
//				  RecordMessage rtn2 =(RecordMessage)HTTPTcp.StringToObject(rtn);
//				  data = rtn2.getMessage();
//			 }
//
//			 JSONObject rtn = JSONObject.fromObject(data);
//			 if(rtn == null){
//				 json("success", true, "{}", true);
//				 return;
//			 }
//
//			 //JUA.COM 累计理财总额
//			 String jua = HttpUtil.doGet("https://www.jua.com/getBtcTotalStorage?callback=?", null);
//			 if(StringUtils.isNotEmpty(jua)){
//					jua = jua.substring(2);
//					jua = jua.substring(0, jua.length() - 1);
//			 }
//			 JSONObject json = JSONObject.fromObject(jua);
//			 rtn.element("finance", json == null ? 0 : json.getJSONArray("datas").get(0));
//
//			 String rate = HttpUtil.doGet("https://www.jua.com/getTodayRate?coint=btc&callback=?", null);
//			 if(StringUtils.isNotEmpty(rate)){
//				 rate = rate.substring(2);
//				 rate = rate.substring(0, rate.length() - 1);
//			 }
//			 JSONObject json3 = JSONObject.fromObject(rate);
//			 rtn.element("rate", json == null ? 0 : json3.getJSONArray("datas").get(0));
//
//			 //BW.COM 当前算力
//			 String bw = HttpUtil.doGet("https://www.bw.com/totalForce", null);
//			 JSONObject json2 = JSONObject.fromObject(bw);
//			 rtn.element("force", json2 == null ? 0 : json2.getJSONObject("datas").getDouble("force"));
//			 //log.info(rtn.toString());

            String rtn = Cache.Get(MarketDataWorker.marketDataKey);
            if(StringUtils.isEmpty(rtn)){
                //MarketDataWorker.getDataToMem();
                rtn = Cache.Get(MarketDataWorker.marketDataKey);
            }

            json("success", true, rtn, true);
        }catch(Exception ex){
            json("success", true, "{}", true);
            log.error(ex.toString(), ex);
        }
    }

    /**
     * BW.COM 当前算力
     * JUA.COM 累计理财总额、日利率
     * 当前BTC行情
     */
    @Page(Viewer = JSON)
    public void jsonData2(){
        try{
            String rtn = Cache.Get(MarketDataWorker.marketDataKey);
            if(StringUtils.isEmpty(rtn)){
                //MarketDataWorker.getDataToMem();
                rtn = Cache.Get(MarketDataWorker.marketDataKey);
            }

            json("success", true, rtn);
        }catch(Exception ex){
            json("success", true, "{}");
            log.error(ex.toString(), ex);
        }
    }

    @Page(Viewer = JSON)
    public void ticker(){
        try{
            String market = GetPrama(0);
            if(market.length() ==0){
                market = "btc_cny";
            }
            String data= DishDataCacheService.getTicker(market);
            response.setContentType("text/javascript");
            if(data==null){
                Market m=Market.getMarkeByName(market);
                RecordMessage myObj = new RecordMessage();
                RecordMessage rtn2 = server.getticker(myObj,m);
                response.getWriter().write(rtn2.getMessage());
            }else{
                response.getWriter().write(data);
            }

        }catch(Exception ex){
            log.error(ex.toString(), ex);
        }

    }

    /**
     *获取图表数据
     */
    @Page(Viewer = ".json")
    public void period() {
        try {
            int time = intParam("step");
            String symbol = param("symbol");
            Market m = null;
            m = Market.getMarket(symbol);

            if (m == null) {
                Response.append("[{error market}]");
                return;
            }
            String rtn = data(time, m);
            Response.append("[" + rtn + "]");
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
    }

    /**
     * 获取图表数据
     * @param time 时间
     * @return
     */
    public String data(int time, Market m) {
        try {
            String name = m.market + "_wisdomchar" + time;
            String data = Cache.Get(name);
            log.info(name);
            if (data == null) {
                Message myObj = new Message();
                myObj.setTypes(time);
                myObj.setMarket(m.market);
                Message rtn2 =  new Message();
                if(m.listenerOpen){
                    rtn2 = server.wisdom(myObj, m);
                }else{
                    String param = HTTPTcp.ObjectToString(myObj);
                    log.info(m.ip + ":" + m.port);
                    String rtn = HTTPTcp.DoRequest2(true, m.ip, m.port, "/server/wisdom", param);
                    log.info(rtn);
                    rtn2 = (Message) HTTPTcp.StringToObject(rtn);
                }


                if (rtn2 != null && rtn2.getMessage().equals("ok")) {
                    return Cache.Get(name);
                } else
                    return null;
            } else {
                log.info(m.market + "_wisdomchar" + time + " has cached.");
                return data;
            }
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
            return null;
        }
    }

    @Page(Viewer = JSON)
    public void depth() {
        try {
            String symbol = param("symbol");
            String market = symbol;
            response.setContentType("text/javascript");
            Market m=Market.getMarkeByName(market);
            if(m==null){
                JSONObject json = new JSONObject();
                json.put("error", "币种参数无效！");
                response.getWriter().write(json.toString());
                return;
            }

            String data = DishDataCacheService.getDishDepthKline50(m.market);
            if(StringUtils.isNotBlank(data)){
                response.getWriter().write(data);
            }

            JSONObject object = new JSONObject();
            object.put("result", "success");
            object.put("return", data);
            Response.append(object.toString());
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
    }

    @Page
    public void trades() {
        /**
         * 获取委托和交易历史记录数据
         */
        try {
            String symbol = param("symbol");
            String market = symbol;
            int since = request.getParameter("since") == null ? 0 : Integer.parseInt(request.getParameter("since"));
            Market m = Market.getMarkeByName(market);

            log.info("客户端取：" + m.market + "_OuderTrade_" + since);

            response.setContentType("text/javascript");

            String data = DishDataCacheService.getDishDepthKline50(m.market);
            response.getWriter().write(data);
        } catch (Exception ex) {
        }
    }
}