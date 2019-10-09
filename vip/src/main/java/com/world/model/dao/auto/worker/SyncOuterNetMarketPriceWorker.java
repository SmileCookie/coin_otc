package com.world.model.dao.auto.worker;

import com.alibaba.fastjson.JSONObject;
import com.world.cache.Cache;
import com.world.model.dao.task.Worker;
import com.world.model.entity.Market;
import com.world.util.request.HttpUtil;
import com.world.util.string.StringUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;

/**
 * 同步外网市场价格
 *
 * Created by suxinjie on 2017/7/21.
 */
public class SyncOuterNetMarketPriceWorker extends Worker {
    public static Logger LOGGER = Logger.getLogger(SyncOuterNetMarketPriceWorker.class.getName());

    public SyncOuterNetMarketPriceWorker(String name, String des) {
        super(name, des);
    }

    @Override
    public void run() {
        super.run();

//        syncBterMarketPrice();
        syncBitfinexMarketPrice();
//        LOGGER.info("新增同步币种行情开始");
        /*start by xzhang 20170904 JYPT-1275 新增okcoin，Huobi和Binance交易市场行情*/
//        syncOkCoinMarketPrice();
//        syncHuobiMarketPrice();
//        syncBinanceMarketPrice();
        /**end*/
//        syncPoloniexMarketPrice();
    }

    /**
     * 根据bitglobal市场同步bter市场信息
     */
    private void syncBterMarketPrice() {
        Map<String, JSONObject> markets = Market.getMarketsMap();
        Iterator<Map.Entry<String, JSONObject>> iter = markets.entrySet().iterator();

        JSONObject result = new JSONObject();

        while (iter.hasNext()) {
            Map.Entry<String, JSONObject> entry = iter.next();
            JSONObject market = entry.getValue();
            String symbol = market.getString("market");

            if (!"gbc_btc".equalsIgnoreCase(symbol)&&!"gbc_usdt".equalsIgnoreCase(symbol)) {
                if(symbol.indexOf("usdt")>0){
                    symbol = symbol.substring(0,symbol.length()-1);
                    continue;
                }
                try {
                    String response = HttpUtil.doGet("http://data.bter.com/api/1/ticker/" + symbol, null);
                    if (StringUtil.exist(response)) {
                        JSONObject json = JSONObject.parseObject(response);
                        if (json != null) {
                            String lastPrice = json.getString("last");
                            String vol = json.getString("vol_" + symbol.split("_")[0]);
                            if (StringUtil.exist(lastPrice) && new BigDecimal(lastPrice).compareTo(BigDecimal.ZERO) > 0) {
                                LOGGER.info("同步外网市场价格, 从bter获取 [" + symbol + "] 价格 : " + lastPrice);

                                OuterNetMarketTicker outerNetMarketTicker = new OuterNetMarketTicker(lastPrice, vol);
                                result.put(symbol, outerNetMarketTicker);
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("同步外网市场价格, 从bter获取 [" + symbol + "] 价格失败", e);
                }
            }

        }

        if (result.size() > 0) {
            Cache.Set("market_price_bter", result.toJSONString());
        }
    }

    /**
     * 根据bitglobal市场同步bitfinex市场信息
     *
     * TODO 没有24小时成交的btc数量,只有成交的当前币种的数量
     */
    private void syncBitfinexMarketPrice() {
        Map<String, JSONObject> markets = Market.getMarketsMap();
        Iterator<Map.Entry<String, JSONObject>> iter = markets.entrySet().iterator();

        JSONObject result = new JSONObject();

        while (iter.hasNext()) {
            Map.Entry<String, JSONObject> entry = iter.next();
            JSONObject market = entry.getValue();
            String symbol = market.getString("market");
            String symbolFinal = symbol;

            // TODO: 2017/11/10 苏新杰先暂时屏蔽了dash相关市场,张祥修改下这里的逻辑,现在市场有dash_usdt和dash_btc, 但是在bitfinex对应的市场名字是dshbtc和dshusd, 可以通过https://api.bitfinex.com/v1/symbols_details获取所有市场
//            if (!"dash_btc".equalsIgnoreCase(symbol)&&!"gbc_btc".equalsIgnoreCase(symbol)&&!"gbc_usdt".equalsIgnoreCase(symbol)) {
            if (!"gbc_btc".equalsIgnoreCase(symbol)&&!"gbc_usdt".equalsIgnoreCase(symbol)) {
                if(symbol.indexOf("usdt")>0){
                    symbol = symbol.substring(0,symbol.length()-1);
                }
                if("dash_usd".equalsIgnoreCase(symbol)){
                    symbol =  "dshusd";
                }else if("dash_btc".equalsIgnoreCase(symbol)){
                    symbol =  "dshbtc";
                }
                try {
                    String response = "";
                    boolean flag = true;
                    int count = 0;
                    while(flag){
                        try{
                            response = HttpUtil.doGet("https://api.bitfinex.com/v1/pubticker/" + symbol.replace("_",""), null,15000,20000);
                            flag = false;
                        }catch (IOException e){
                        }catch(Exception e){
                            LOGGER.error("同步外网市场价格, 从bitfinex获取 [" + symbolFinal + "] 价格失败,发生异常：",e);
                            flag = false;
                        }
                        if(count<2){
                            count ++;
                        }else{
                            flag = false;
                            LOGGER.error("同步外网市场价格, 从bitfinex获取 [" + symbolFinal + "] 价格失败,超时重试三次结束");
                        }
                    }
                    if (StringUtil.exist(response)) {
                        JSONObject json = JSONObject.parseObject(response);
                        if (json != null) {
                            String lastPrice = json.getString("last_price");
                            String vol = json.getString("volume");
                            if (StringUtil.exist(lastPrice) && new BigDecimal(lastPrice).compareTo(BigDecimal.ZERO) > 0) {
                                LOGGER.info("同步外网市场价格, 从bitfinex获取 [" + symbolFinal + "] 价格 : " + lastPrice);

                                OuterNetMarketTicker outerNetMarketTicker = new OuterNetMarketTicker(lastPrice, vol);
                                result.put(symbolFinal, outerNetMarketTicker);
                            }
                        }
                    }
                }catch (Exception e){
                    LOGGER.error("同步外网市场价格, 从bitfinex获取 [" + symbolFinal + "] 价格失败,发生异常：",e);
                }
            }
        }

        if (result.size() > 0) {
            Cache.Set("market_price_bitfinex", result.toJSONString());
        }
    }

    /**
     * 根据bitglobal市场同步poloniex市场信息
     *
     * 直接在香港服务器访问没有问题,在大陆访问因为有google人机验证,所以不能请求到数据,但是翻墙通过浏览器访问是可以请求的
     */
    private void syncPoloniexMarketPrice() {
        Map<String, JSONObject> markets = Market.getMarketsMap();
        Iterator<Map.Entry<String, JSONObject>> iter = markets.entrySet().iterator();

        JSONObject result = new JSONObject();

        while (iter.hasNext()) {
            Map.Entry<String, JSONObject> entry = iter.next();
            JSONObject market = entry.getValue();
            String symbol = market.getString("market");
            String[] symbolArr = symbol.split("_");

            if (!"gbc_btc".equalsIgnoreCase(symbol)&&!"gbc_usdt".equalsIgnoreCase(symbol)) {
                if(symbol.indexOf("usdt")>0){
                    symbol = symbol.substring(0,symbol.length()-1);
                }
                try {
                    String response = HttpUtil.doGet("https://poloniex.com/public?command=returnTicker", null);
                    if (StringUtil.exist(response)) {
                        JSONObject json = JSONObject.parseObject(response);
                        if (json != null) {
                            JSONObject marketInfo = json.getJSONObject(symbolArr[1] + "_" + symbolArr[0]);
                            if(marketInfo != null){
                                String lastPrice = marketInfo.getString("last");
                                String vol = marketInfo.getString("quoteVolume");
                                if (StringUtil.exist(lastPrice) && new BigDecimal(lastPrice).compareTo(BigDecimal.ZERO) > 0) {
                                    LOGGER.info("同步外网市场价格, 从poloniex获取 [" + symbol + "] 价格 : " + lastPrice);

                                    OuterNetMarketTicker outerNetMarketTicker = new OuterNetMarketTicker(lastPrice, vol);
                                    result.put(symbol, outerNetMarketTicker);
                                }
                            }

                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("同步外网市场价格, 从poloniex获取 [" + symbol + "] 价格失败", e);
                }
            }
        }

        if (result.size() > 0) {
            Cache.Set("market_price_poloniex", result.toJSONString());
        }
    }

    /*start by xzhang 20170904 JYPT-1275 新增okcoin，Huobi和Binance交易市场行情*/
    /**
     * 根据bitglobal市场同步OkCoin市场信息
     */
    private void syncOkCoinMarketPrice() {
        Map<String, JSONObject> markets = Market.getMarketsMap();
        Iterator<Map.Entry<String, JSONObject>> iter = markets.entrySet().iterator();

        JSONObject result = new JSONObject();

        while (iter.hasNext()) {
            Map.Entry<String, JSONObject> entry = iter.next();
            JSONObject market = entry.getValue();
            String symbol = market.getString("market");
            if (!"gbc_btc".equalsIgnoreCase(symbol)&&!"gbc_usdt".equalsIgnoreCase(symbol)&&!"zec_btc".equalsIgnoreCase(symbol)&&!"dash_btc".equalsIgnoreCase(symbol)
                    &&!"zec_usdt".equalsIgnoreCase(symbol)&&!"dash_usdt".equalsIgnoreCase(symbol)) {
                try {
                    String response = null;
                    if(symbol.indexOf("usdt")>0){
                        symbol = symbol.substring(0,symbol.length()-1);
                        response = HttpUtil.doGet("https://www.okcoin.com/api/v1/ticker.do?symbol=" + symbol, null);
                    }else{
                        response = HttpUtil.doGet("https://www.okex.com/api/v1/ticker.do?symbol=" + symbol, null);
                    }
                    LOGGER.info("同步外网市场价格, 从OkCoin获取 [" + symbol + "] 响应信息 : " + response);
                    if (StringUtil.exist(response)) {
                        JSONObject json = JSONObject.parseObject(response);
                        if (json != null) {
                            JSONObject ticker = json.getJSONObject("ticker");
                            if(ticker != null){
                                String lastPrice = ticker.getString("last");
                                String vol = ticker.getString("vol");
                                if (StringUtil.exist(lastPrice) && new BigDecimal(lastPrice).compareTo(BigDecimal.ZERO) > 0) {
                                    LOGGER.info("同步外网市场价格, 从OkCoin获取 [" + symbol + "] 价格 : " + lastPrice);
                                    OuterNetMarketTicker outerNetMarketTicker = new OuterNetMarketTicker(lastPrice, vol);
                                    result.put(symbol, outerNetMarketTicker);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("同步外网市场价格, 从OkCoin获取 [" + symbol + "] 价格失败", e);
                }
            }

        }

        if (result.size() > 0) {
            Cache.Set("market_price_OkCoin", result.toJSONString());
            LOGGER.info("同步外网市场价格, 从OkCoin获取交易信息，缓存设置成功 : " + result.toJSONString());
        }
    }


    /**
     * 根据bitglobal市场同步Huobi市场信息
     */
    private void syncHuobiMarketPrice() {
        Map<String, JSONObject> markets = Market.getMarketsMap();
        Iterator<Map.Entry<String, JSONObject>> iter = markets.entrySet().iterator();

        JSONObject result = new JSONObject();

        while (iter.hasNext()) {
            Map.Entry<String, JSONObject> entry = iter.next();
            JSONObject market = entry.getValue();
            String symbol = market.getString("market");
            //zec,dash
            if (!"gbc_btc".equalsIgnoreCase(symbol)&&!"gbc_usdt".equalsIgnoreCase(symbol)&&!"zec_btc".equalsIgnoreCase(symbol)&&!"dash_btc".equalsIgnoreCase(symbol)
                    &&!"zec_usdt".equalsIgnoreCase(symbol)&&!"dash_usdt".equalsIgnoreCase(symbol)) {
                if(symbol.indexOf("usdt")>0){
//                    symbol = symbol.substring(0,symbol.length()-1);
                    continue;
                }
                try {
                    String response = HttpUtil.doGet("https://api.huobi.pro/market/detail/merged?symbol=" + symbol.replace("_",""), null);
                    LOGGER.info("同步外网市场价格, 从Huobi获取 [" + symbol + "] 响应信息 : " + response);
                    if (StringUtil.exist(response)) {
                        JSONObject json = JSONObject.parseObject(response);
                        if (json != null) {
                            JSONObject ticker = json.getJSONObject("tick");
                            if (ticker != null) {
                                String lastPrice = ticker.getString("close");
                                String vol = ticker.getString("amount");//24小时成交量
                                if (StringUtil.exist(lastPrice) && new BigDecimal(lastPrice).compareTo(BigDecimal.ZERO) > 0) {
                                    LOGGER.info("同步外网市场价格, 从Huobi获取 [" + symbol + "] 价格 : " + lastPrice);

                                    OuterNetMarketTicker outerNetMarketTicker = new OuterNetMarketTicker(lastPrice, vol);
                                    result.put(symbol, outerNetMarketTicker);
                                }
                            }

                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("同步外网市场价格, 从Huobi获取 [" + symbol + "] 价格失败", e);
                }
            }

        }

        if (result.size() > 0) {
            Cache.Set("market_price_Huobi", result.toJSONString());
            LOGGER.info("同步外网市场价格, 从Huobi获取交易信息，缓存设置成功 : " + result.toJSONString());
        }
    }

    /**
     * 根据bitglobal市场同步binance市场信息
     */
    private void syncBinanceMarketPrice() {
        Map<String, JSONObject> markets = Market.getMarketsMap();
        Iterator<Map.Entry<String, JSONObject>> iter = markets.entrySet().iterator();

        JSONObject result = new JSONObject();

        while (iter.hasNext()) {
            Map.Entry<String, JSONObject> entry = iter.next();
            JSONObject market = entry.getValue();
            String symbol = market.getString("market");

            if (!"gbc_btc".equalsIgnoreCase(symbol)&&!"gbc_usdt".equalsIgnoreCase(symbol)&&!"zec_btc".equalsIgnoreCase(symbol)&&!"dash_btc".equalsIgnoreCase(symbol)
                    &&!"zec_usdt".equalsIgnoreCase(symbol)&&!"dash_usdt".equalsIgnoreCase(symbol)&&!"etc_usdt".equalsIgnoreCase(symbol)&&!"etc_btc".equalsIgnoreCase(symbol)) {
                if(symbol.indexOf("usdt")>0){
//                    symbol = symbol.substring(0,symbol.length()-1);
                    continue;
                }
                try {
                    String response = HttpUtil.doGet("https://www.binance.com/api/v1/ticker/24hr?symbol=" + symbol.replace("_","").toUpperCase(), null);
                    LOGGER.info("同步外网市场价格, 从Binance获取 [" + symbol + "] 响应信息 : " + response);
                    if (StringUtil.exist(response)) {
                        JSONObject json = JSONObject.parseObject(response);
                        if (json != null) {
                            String lastPrice = json.getString("lastPrice");
                            String vol = json.getString("volume");
                            if (StringUtil.exist(lastPrice) && new BigDecimal(lastPrice).compareTo(BigDecimal.ZERO) > 0) {
                                LOGGER.info("同步外网市场价格, 从Binance获取 [" + symbol + "] 价格 : " + lastPrice);
                                OuterNetMarketTicker outerNetMarketTicker = new OuterNetMarketTicker(lastPrice, vol);
                                result.put(symbol, outerNetMarketTicker);
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("同步外网市场价格, 从Binance获取 [" + symbol + "] 价格失败", e);
                }
            }

        }

        if (result.size() > 0) {
            Cache.Set("market_price_Binance", result.toJSONString());
            LOGGER.info("同步外网市场价格, 从Binance获取交易信息，缓存设置成功 : " + result.toJSONString());
        }
    }
    /**end*/
}

class OuterNetMarketTicker {

    public OuterNetMarketTicker(String last, String quoteVolume) {
        this.last = last;
        this.quoteVolume = quoteVolume;
    }

    private String last;        // 现价
    private String baseVolume;  // 24小时成交量(BTC)
    private String quoteVolume; // 24小时成交量(本币)

    public String getBaseVolume() {
        return baseVolume;
    }

    public void setBaseVolume(String baseVolume) {
        this.baseVolume = baseVolume;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getQuoteVolume() {
        return quoteVolume;
    }

    public void setQuoteVolume(String quoteVolume) {
        this.quoteVolume = quoteVolume;
    }
}
