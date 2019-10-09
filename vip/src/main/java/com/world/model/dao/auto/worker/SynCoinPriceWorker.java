package com.world.model.dao.auto.worker;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redis.RedisUtil;
import com.world.cache.Cache;
import com.world.config.GlobalConfig;
import com.world.model.dao.task.Worker;
import com.world.model.entity.CacheKeys;
import com.world.model.loan.worker.LoanAutoFactory;
import com.world.model.entity.LegalTenderType;
import com.world.system.Sys;
import com.world.util.request.HttpUtil;
import com.world.util.string.StringUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpHead;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by suxinjie on 2017/6/6.
 * <p/>
 * 1.获取bitfinex网站的btc-usd的价格
 * 2.从中国人民银行获取各个币种对人民币的价格
 * 3.计算为btc_法币的价格
 *
 * 缓存信息Key为 : btc_usd, btc_cny, .....
 */
public class SynCoinPriceWorker extends Worker {

    public static Logger LOGGER = Logger.getLogger(SynCoinPriceWorker.class.getName());
    private AtomicInteger counter = new AtomicInteger(1);

    public SynCoinPriceWorker(String name, String des) {
        super(name, des);
    }

    @Override
    public void run() {
        super.run();
        try {
            // 1.1 获取本站的btc_usd的价格
            getPriceFromLocal();
            // 1.2缓存外网USDT_USD的价格
            cacheUsdtPrice();
            // 1.3 计算btc对各种法币的价格
            computePrice();
            // 1.4 计算usd对各种法币的价格
            computeUsdPrice();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        AtomicInteger counter = new AtomicInteger(1);
        for (int i = 0; i < 100; i++) {
            if (counter.addAndGet(-1) > 0) {

            } else {
                System.out.println(i);
                counter.set(3);
            }
        }
    }

    private void cacheUsdtPrice() {
        try {
            BigDecimal usdtPrice = getUsdtPriceFromHuobi();
            if (usdtPrice != null) {
                Cache.Set("usd_cny", usdtPrice.setScale(5, BigDecimal.ROUND_DOWN).toPlainString());
                return;
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 从货币获取USDT价格
     * @return
     */
    private BigDecimal getUsdtPriceFromHuobi() throws IOException {
        HttpClient client = new HttpClient();
        String urlPattern = "https://otc-api.eiijo.cn/v1/data/trade-market?country=37&currency=1&payMethod=0&currPage=1&coinId=2&tradeType=sell&blockType=general&online=1";
        GetMethod request = new GetMethod(urlPattern);
        request.setRequestHeader(HttpHeaders.ACCEPT, "application/json");
        request.setRequestHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36");
        int code = client.executeMethod(request);
        String result = request.getResponseBodyAsString();
        LOGGER.info("huobi otc <<< " + result);
        if (code == 200) {
            JSONObject res = JSONObject.parseObject(result);

            if (res.containsKey("data")) {
                JSONArray data = res.getJSONArray("data");
                if (!data.isEmpty()) {
                    JSONObject trade = data.getJSONObject(0);
                    if (trade.containsKey("price")) {
                        return trade.getBigDecimal("price");
                    }
                }
            }
        }
        return null;
    }

    /**
     * 从bitfinex获取btc_usd价格并缓存
     *
     * TODO 将地址信息放入配置文件
     */
    private void getPriceFromBitfinex() {

        try {
            String response = HttpUtil.doGet("https://api.bitfinex.com/v1/pubticker/btcusd", null);
            if (StringUtil.exist(response)) {
                JSONObject json = JSONObject.parseObject(response);
                if (json != null) {
                    String lastPrice = json.getString("last_price");
                    if (StringUtil.exist(lastPrice) && new BigDecimal(lastPrice).compareTo(BigDecimal.ZERO) > 0) {
                        LOGGER.info("从bitfinex获取btc_usd价格 : " + lastPrice);
                        Cache.Set("bitfinex_btc_usd", lastPrice);
                        Cache.Set("btc_usd", lastPrice);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("从bitfinex获取btc_usd价格失败", e);
        }

    }

    private void getPriceFromLocal(){
        JSONObject prices = LoanAutoFactory.getPrices();
        if (prices != null) {
            String lastPrice = prices.getString("btc_usdt");
            if (StringUtil.exist(lastPrice) && new BigDecimal(lastPrice).compareTo(BigDecimal.ZERO) > 0) {
                Cache.Set("local_btc_usdt", lastPrice);
                Cache.Set("btc_usdt", lastPrice);
            }
        }
    }

    /**
     * 从缓存中获取数据,计算btc对各种法币的价格
     */
    private void computePrice() {
        String btcUsd = Cache.Get("local_btc_usdt");

        if (!StringUtil.exist(btcUsd)) {
            LOGGER.info("缓存中 [local_btc_usd] 值为空,无法进行下一步计算");
            return;
        }

        // 计算 btc_法币 价格
        List<String> legalTenders = LegalTenderType.getkeys();

        // 单独计算btc_cny价格
        String usdCny = Cache.Get("usd_cny");
        String btcCny = "";
        if (StringUtil.exist(usdCny)) {
            btcCny = new BigDecimal(btcUsd).multiply(new BigDecimal(usdCny)).setScale(5, BigDecimal.ROUND_HALF_EVEN).toPlainString();
            Cache.Set("btc_cny", btcCny);
            LOGGER.info("计算获取 [btc_cny] 价格 : " + btcCny);
        }

        // 计算 btc_其他法币(除cny)  价格
        for (String legal : legalTenders) {
            // 人民币单独计算
            if (!"cny".equalsIgnoreCase(legal) && StringUtil.exist(btcCny)) {
                // 从缓存获取法币对人民币价格
                String otherCny = Cache.Get(legal.toLowerCase() + "_cny");
                if (StringUtil.exist(otherCny)) {
                    String btcOther = new BigDecimal(btcCny).divide(new BigDecimal(otherCny), 5, BigDecimal.ROUND_HALF_EVEN).toPlainString();
                    Cache.Set("btc_" + legal.toLowerCase(), btcOther);

                    LOGGER.info("计算获取 [btc_" + legal.toLowerCase() + "] 价格 : " + btcOther);
                }
            }
        }
    }

     /*start by xzhang 20170912 去掉bitfinex获取btc兑美元接口，改为从本平台获取*/
    /**
     * 从缓存中获取数据,计算usd对各种法币的价格
     */
    private void computeUsdPrice() {
        // 计算 USD_法币 价格
        List<String> legalTenders = LegalTenderType.getkeys();
        String usdCny = Cache.Get("usd_cny");
        String btcUsd =  Cache.Get("btc_usdt");
        if (!StringUtil.exist(btcUsd) || !StringUtil.exist(usdCny)) {
            LOGGER.info("缓存中 [local_btc_usd] 值为空,无法进行下一步计算");
            return;
        }
        Cache.Set("usdt_cny", usdCny);
        LOGGER.info("计算获取 [usdt_cny] 价格 : " + usdCny);

        Cache.Set("usdt_btc", new BigDecimal("1").divide(new BigDecimal(btcUsd), 5, BigDecimal.ROUND_HALF_EVEN).toPlainString());
        // 计算 usd_其他法币(除cny)  价格
        for (String legal : legalTenders) {
            // 人民币单独计算
            if (!"cny".equalsIgnoreCase(legal) && StringUtil.exist(usdCny)) {
                // 从缓存获取法币对人民币价格
                String otherCny = Cache.Get(legal.toLowerCase() + "_cny");
                if (StringUtil.exist(otherCny)) {
                    String btcOther = new BigDecimal(usdCny).divide(new BigDecimal(otherCny), 5, BigDecimal.ROUND_HALF_EVEN).toPlainString();
                    Cache.Set("usdt_" + legal.toLowerCase(), btcOther);

                    LOGGER.info("计算获取 [usdt_" + legal.toLowerCase() + "] 价格 : " + btcOther);
                }
            }
        }
    }
    /**end**/

}
