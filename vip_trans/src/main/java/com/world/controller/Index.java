package com.world.controller;

import com.alibaba.fastjson.JSONArray;
import com.match.entrust.EntrustUpdateProcessor;
import com.match.entrust.MemEntrustDataProcessor;
import com.match.entrust.MemTransRecordProcessor;
import com.match.money.UserFundsUpdateProcessor;
import com.tenstar.HTTPTcp;
import com.tenstar.Message;
import com.tenstar.robotConfig;
import com.tenstar.timer.dish.DishDataManager;
import com.world.cache.Cache;
import com.world.dish.DishDataCacheService;
import com.world.model.Market;
import com.world.timer.auto.MarketDataWorker;
import com.world.util.CommonUtil;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import com.world.web.action.UserAction;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Index extends UserAction {

    private IndexServer server = new IndexServer();

    @Page
    public void index() {

        try {
            response.sendRedirect("/btc");
        } catch (IOException e) {
            log.error(e.toString(), e);
        }

    }


    /**
     * 是否启用安全密码
     */
    @Page(Viewer = ".json")
    public void isSafe() {
        String userId = userId(false, true);
        //useSafePwd(userId);
        String useKey = Cache.Get("use_pwd_" + userId);
        if (useKey == null) {
            json("true", true, "");
        } else {
            json("false", true, "");
        }
    }

    //新的获取市场数据的JSON
    @Page(Viewer = JSON)
    public void getMarket() {
        StringBuilder json = new StringBuilder("");
        json.append("[{");
        String type = param("type");
        if (StringUtils.isNotEmpty(type)) {
            Market market = Market.getMarket(type);
            if (market == null) return;
            json.append("\"numberBi\":\"" + market.numberBi + "\"");
            json.append(",\"numberBiEn\":\"" + market.numberBiEn + "\"");
            json.append(",\"numberBiNote\":\"" + market.numberBiNote + "\"");
            json.append(",\"numberBixNormal\":\"" + market.numberBixNormal + "\"");
            json.append(",\"numberBixShow\":\"" + market.numberBixShow + "\"");
            json.append(",\"numberBixDian\":\"" + market.numberBixDian + "\"");
            json.append(",\"exchangeBi\":\"" + market.exchangeBi + "\"");
            json.append(",\"exchangeBiEn\":\"" + market.exchangeBiEn + "\"");
            json.append(",\"exchangeBiNote\":\"" + market.exchangeBiNote + "\"");
            json.append(",\"market\":\"" + market.market + "\"");
            json.append(",\"exchangeBixNormal\":\"" + market.exchangeBixNormal + "\"");
            json.append(",\"exchangeBixShow\":\"" + market.exchangeBixShow + "\"");
            json.append(",\"exchangeBixDian\":\"" + market.exchangeBixDian + "\"");
            json.append(",\"entrustUrlBase\":\"" + market.entrustUrlBase + "\"");
            json.append(",\"feeRate\":\"" + market.feeRate + "\"");
            json.append(",\"takerFeeRate\":\"" + market.takerFeeRate + "\"");
            json.append(",\"makerFeeRate\":\"" + market.makerFeeRate + "\"");
            json.append(",\"db\":\"" + market.db + "\"");
            json.append(",\"numberBiFundsType\":\"" + market.numberBiFundsType + "\"");
            json.append(",\"exchangeBiFundsType\":\"" + market.exchangeBiFundsType + "\"");
            json.append(",\"minAmount\":\"" + market.minAmount + "\"");
            json.append(",\"bixMinNum\":\"" + market.bixMinNum + "\"");
            json.append(",\"bixMaxNum\":\"" + market.bixMaxNum + "\"");// add by kinghao 20181121

        }
        json.append("}]");
        json("success...", true, json.toString(), true);

    }


    /**
     * 获取盘口配置文件的所有配置
     */
    @Page(Viewer = JSON)
    public void getAllMarket() {
        try {
            com.alibaba.fastjson.JSONObject json = Market.getAllMarkets();

            //log.info(json.toString());
            json("success...", true, json.toString(), true);
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
            json("出现异常", false, "", true);
        }

    }

    //旧方法依旧保留
    @Page(Viewer = JSON)
    public void market() {
        StringBuilder json = new StringBuilder("");
        json.append("[{");
        String type = param("type");
        if (StringUtils.isNotEmpty(type)) {
            Market market = Market.getMarket(type + "default");
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
        json("success...", true, json.toString(), true);

    }

    /**
     * 获取盘口配置文件的所有配置
     */
    @Page(Viewer = JSON)
    public void getMarketRelate() {
        JSONObject json = new JSONObject();

        Map<String, List<String>> exchangeNumbersMap = new HashMap<>();
        Map<String, Market> map = CommonUtil.sortMapByValue(Market.markets);
        for (Map.Entry<String, Market> entry : map.entrySet()) {
            Market market = entry.getValue();
            String exchangeBiEn = market.getExchangeBiEn();
            String numberBiEn = market.getNumberBiEn();
            List<String> numberBis = exchangeNumbersMap.get(exchangeBiEn);
            if (null == numberBis) {
                numberBis = new ArrayList<>();
            }
            numberBis.add(numberBiEn);
            exchangeNumbersMap.put(exchangeBiEn, numberBis);
        }

        for (Map.Entry<String, List<String>> entry : exchangeNumbersMap.entrySet()) {
            String exchangeBi = entry.getKey();
            List<String> numberBis = entry.getValue();
            json.put(exchangeBi, numberBis);
        }

        json("success", true, json.toString(), true);
    }

    /**
     * BW.COM 当前算力
     * JUA.COM 累计理财总额、日利率
     * 当前BTC行情
     */
    @Page(Viewer = JSON)
    public void jsonData() {
        try {

            String rtn = Cache.Get(MarketDataWorker.marketDataKey);
            if (StringUtils.isEmpty(rtn)) {
                //MarketDataWorker.getDataToMem();
                rtn = Cache.Get(MarketDataWorker.marketDataKey);
            }

            json("success", true, rtn, true);
        } catch (Exception ex) {
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
    public void jsonData2() {
        try {
            String rtn = Cache.Get(MarketDataWorker.marketDataKey);
            if (StringUtils.isEmpty(rtn)) {
                //MarketDataWorker.getDataToMem();
                rtn = Cache.Get(MarketDataWorker.marketDataKey);
            }

            json("success", true, rtn);
        } catch (Exception ex) {
            json("success", true, "{}");
            log.error(ex.toString(), ex);
        }
    }

    @Page(Viewer = JSON)
    public void ticker() {
        try {
            String market = GetPrama(0);
            Market m = Market.getMarkeByName(market);
            if (m == null) {
                Response.append("[{error market}]");
                return;
            }
            String data = DishDataCacheService.getTicker(m.market);

            response.setContentType("text/javascript");
            response.getWriter().write(data);
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

    }

    /**
     * 获取图表数据
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
     *
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

                Message rtn2 = new Message();
                if (m.listenerOpen) {
                    rtn2 = server.wisdom(myObj, m);
                } else {
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

            String data = "";
            response.setContentType("text/javascript");

            Market m = Market.getMarkeByName(market);
            if (m == null) {
                JSONObject json = new JSONObject();
                json.put("error", "币种参数无效！");
                response.getWriter().write(json.toString());
                return;
            }

            JSONObject object = new JSONObject();
            object.put("result", "success");

            data = DishDataCacheService.getDishDepthKline50(m.market);
            object.put("return", data);
            json("success", true, object.toString(), true);
            //Response.append(object.toString());
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
            if (m == null) {
                Response.append("[{error market}]");
                return;
            }
            response.setContentType("text/javascript");
            String data = DishDataCacheService.getSinceTrade(m.market, since);
            response.getWriter().write(data);
        } catch (Exception ex) {
        }
    }

    /**
     * 获取大盘成交记录,需求确认25条即可
     */
    @Page(Viewer = JSON)
    public void getLastTrades() {
        try {
            String symbol = param("symbol");
            String market = symbol;

            int since = request.getParameter("since") == null ? 0 : Integer.parseInt(request.getParameter("since"));
            Market m = Market.getMarkeByName(market);
            if (m == null) {
                Response.append("[{error market}]");
                return;
            }

            //从缓存获取
            String trades = DishDataCacheService.getSinceTrade(m.market, since);

            String lastId = (String) request.getParameter("last_trade_tid");
            if (lastId == null) { // 没有的话，就给刚获取来的，交易数据，最新的tid
                lastId = "0";
            }
            long maxId = 0;
            if (trades != null && trades.startsWith("[")) {
                // log.info(coint+"_lasttrades，推送的交易数据："
                // +
                // trades);
                JSONArray tradesArr = JSONArray.parseArray(trades);

                if (tradesArr.size() > 0) {
                    // 最新的交易数据放在这个数组
                    JSONArray newTrades = new JSONArray();

                    for (int index = 0; index < tradesArr.size(); index++) {
                        //FIXME 20170321 需求只需要近25条记录
                        if (index >= tradesArr.size() - 60) {
                            Long tid = Long.parseLong(tradesArr.getJSONObject(index).getString("tid"));
                            // log.info(coint+"_lasttrades，lastId:"
                            // + lastId + "\ttid:" + tid );
                            if (Long.parseLong(lastId) < tid) { // 如果数据里的tid大于上次推送的最新tid，才好推送
                                com.alibaba.fastjson.JSONObject tradeObject = tradesArr.getJSONObject(index);
                                //如果是gbc_usdt市场，增加回购标志判断 add by buxianguan 20171121
                                if("gbc_usdt".equals(market)) {
                                    //判断是否是回购账户，如果是，type设置repo
                                    String configCache = Cache.Get("BACK_CAPITAL_CONFIG");
                                    if (StringUtils.isNotBlank(configCache)) {
                                        com.alibaba.fastjson.JSONObject config = com.alibaba.fastjson.JSONObject.parseObject(configCache);
                                        String bcUserId = config.getString("bcUserId");
                                        String userIdBuy = tradeObject.getString("userIdBuy");
                                        if (StringUtils.isNotBlank(bcUserId) && StringUtils.isNotBlank(userIdBuy) && userIdBuy.equals(bcUserId)) {
                                            tradeObject.put("type", "repo");
                                        }
                                    }
                                }
                                newTrades.add(tradeObject);
                            }
                            if (tid > maxId) {
                                maxId = tid;
                            }
                        }
                    }

                    if (newTrades.size() > 0) { // 要推送的数据没有的话，就不要推送了
                        JSONObject json = new JSONObject();

                        //json.put("data", newTrades);
                        //Response.append(json.toString());
                        json("success", true, newTrades.toJSONString(), true);
                    } else {
                        json("success", true, newTrades.toJSONString(), true);
                    }

                    return;
                }
            }

            json("success", true, "", true);
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
            json("出现异常", false, "", true);
        }

    }


    /**
     * newrelic监听端口,判断服务是否存活
     */
    @Page(Viewer = JSON)
    public void heartbeat() {
        json("heart-beat", true, "{\"status\":\"alive\"}");
    }

    /*start by xzhang 20171215 交易页面三期PRD:法币折算*/
    @Page(Viewer = JSON)
    public void getExchangeRate(){
        try {
            JSONObject jobj = new JSONObject();
            jobj.put("exchangeRateBTC", DishDataManager.genExchangeRateBTC());
            jobj.put("exchangeRateUSD", DishDataManager.genExchangeRateUSDT());
            json("success", true, jobj.toString());
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
            json("出现异常", false, "", true);
        }
    }
    /*end*/


    @Page(Viewer = JSON)
    public void matchHealth() {
        try {
            com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();

            Market market;
            for (Map.Entry<String, Market> entry : Market.markets.entrySet()) {
                market = entry.getValue();
                if (market.listenerOpen) {
                    if (MemEntrustDataProcessor.marketsNoMatchingEntrustMap.containsKey(market.market)) {
                        // 主动委托单数量
                        result.put(market.getMarket() + "_noMatching", MemEntrustDataProcessor.marketsNoMatchingEntrustMap.get(market.market).size());
                        result.put(market.getMarket() + "_buy", MemEntrustDataProcessor.marketsNoDealBuyEntrustMap.get(market.market).size());
                        result.put(market.getMarket() + "_sell", MemEntrustDataProcessor.marketsNoDealSellEntrustMap.get(market.market).size());
                    }
                }
            }
            // 撮合完未成交单数量
            result.put("entrustUpdateSize", EntrustUpdateProcessor.entrustUpdateSize());
            result.put("memTransSize", MemTransRecordProcessor.memTransSize());
            result.put("userFundsUpdateSize", UserFundsUpdateProcessor.userFundsUpdateSize());
            json("success", true, result.toString());
        } catch (Exception e) {
            log.error(e.toString(), e);
            json("出现异常", false, "", true);
        }
    }
}