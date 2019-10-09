package com.world.controller.api.m;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.api.common.SystemCode;
import com.world.cache.Cache;
import com.world.dish.DishDataCacheService;
import com.world.model.Market;
import com.world.model.entity.LegalTenderType;
import com.world.util.CommonUtil;
import com.world.util.DigitalUtil;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class V1_2 extends AbstractMobileUserAction {
    private static final long serialVersionUID = -4333874723709921756L;

    public static final String MARKETSETS_KEY ="marketSets_";

    private final String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJG14+94DEgzyd6G8+Ue+lpLKK9uIftpSZ7wvnX3jtw+6SUKldkvL1mYq9W8qIJD7w5t3YQIkVoWIlm5Eba5NcDYgfDC/QnYyr9zfDthlJECvQ8TC0wjy9cOtCC4FntewsqmGxLjTA17Zn0RJpsqXvNFjZEinR6IawvnlhPKJ/IwIDAQAB";
    private final String priKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIkbXj73gMSDPJ3obz5R76Wksor24h+2lJnvC+dfeO3D7pJQqV2S8vWZir1byogkPvDm3dhAiRWhYiWbkRtrk1wNiB8ML9CdjKv3N8O2GUkQK9DxMLTCPL1w60ILgWe17CyqYbEuNMDXtmfREmmype80WNkSKdHohrC+eWE8on8jAgMBAAECgYA0EsPx0FkEyf9szgnqNn55gBsbsnbhqpu391WjE9y/GUp0IdShqJ1EcIOENeevW2zYXCbn6mLmZzv6oqIzMuFtZ4GGbHvTsMNGtoBJsvIjV36FjdiXU7FAGqtUI+I/kFBvxFuKcil6JBFGKheQle2segoB9hAsKGoUSayAE5yjqQJBAMJllnMTMeuomhZxSQfuq4Ke3BAGGbbUfcCYnCoK1y9LBe3qXmynWYnc2caIHgbMdDiGYcTm1XOZ5lR/a2GP4HUCQQC0jiUFKWmWkx+MgverbA4QBoh+ff5M95c5T/8W2QbrUW7DV++aW4y+4D92Ei6nFcF1V8SSMxgDmqiz6pOqS243AkEAl6vlR6GZWHGyz4HR5kN8Q6yorEPmOjTubJ9lcJQGspqJZMhwpbuoa50JuRGow8svfo6yp4smzUwtXo4P/Q3hpQJAC8AIZrqYNYVjkzhet9gzXhWewmSerRGb1M4A8tKy4ZOOGsZZQHlewnlDiAKM6LDAw0sv7rfGg02IVxUYAQghpwJABiNcbBh7MnDfGaRZzE7SX/UwRn7OmGY7lFMBWadiQ/R5pKpdPrVmwdlTsefzb1acYy41LQCFCPKxVjv7sUduXQ==";

    /**
     * 5.3.1  获取行情
     *
     * - **请求参数**

     | 参数名         | 类型     | 是否必须 | 描述      |
     | :---------- | :----- | :--- | :------ |
     | exchangeType | String | 是    | 市场类型（兑换货币类型）：<br>CNY：人民币 BTC：比特币，LTC：莱特币 |
     | currencyTypes | json数组       | 否    | 货币类型数组（不输入表示获取全部）：<br> 例如 ['BTC', 'ETH', 'LTC', 'DAO'] |
     | step | int       | 否    | k线类型秒数：1分钟K线：1*60 ，3分钟K线：3*60 ，5分钟K线 ：5*60，以此类推 ，如果不传或传0 则返回空数组|
     | size | int     | 否    |  指定返回K线最新数据条数 |


     - **返回结果**

     | 参数名     | 类型     | 是否必须 | 示例      | 描述   |
     | :------ | :----- | :--- | :------ | :--- |
     | markets | MarketData[] | 是    | | 行情数据，具体看返回示例json结构  |
     */
    @Page(Viewer = JSON)
    public void getTickerArray() {
        setLan();

        // 如果用户不传userId,则默认法币类型为 美元
        // 如果用户传了userId,则对用户进行鉴权,取memcached中的法币信息
        String legalTender = "usd_$";
        String cachePriceBtc = "0";
        String cachePriceUsdt = "0";

        String userId = param("userId");
        String token = param("token");

        /** start 20170901 xzhang  新增前端传递货币折算币种,兼容IOS折算币种老版本，优先取传递，其次去缓存。最后去默认*/
        String legal_Tender = param("legal_tender");
        String legalTenderParam = "";
        if(StringUtils.isNotBlank(legal_Tender)){
            if (!LegalTenderType.existKey(legal_Tender)) {
                json(SystemCode.code_1001, "不支持的货币类型");
                return;
            }
            LegalTenderType legalTenderType = LegalTenderType.valueOf(legal_Tender);
            legalTenderParam = legalTenderType.getKey() + "_" + legalTenderType.getValue();
        }
        if(StringUtil.exist(legalTenderParam)){
            legalTender = legalTenderParam;
        }else if (StringUtil.exist(userId)) {
            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            String legalTenderCache = Cache.Get("user_legal_tender_" + userId);
            legalTender = StringUtil.exist(legalTenderCache) ? legalTenderCache : legalTender;
        }
        /**end **/
        cachePriceBtc = Cache.Get("btc_" + legalTender.split("_")[0].toLowerCase());
        cachePriceUsdt = Cache.Get("usdt_" + legalTender.split("_")[0].toLowerCase());

        try {
            String exchangeType = param("exchangeType").toLowerCase();
            String currencyTypes = request.getParameter("currencyTypes");
            int time = intParam("step");// k线图时间类型
            int size = intParam("size");// 获取k线数据数量
            JSONArray currencyArr = new JSONArray();
			/*if (StringUtils.isBlank(exchangeType)) {
				json(SystemCode.code_1001, L("exchangeType参数不能为空！"));
			}*/

            if (StringUtils.isBlank(currencyTypes)) {// 货币类型数组（不输入表示获取全部）
                Map<String, Market> map = CommonUtil.sortMapByValue(Market.markets);
                Iterator<Map.Entry<String, Market>> iter = map.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, Market> entry = iter.next();
                    Market m = entry.getValue();
                    if (StringUtils.isNotBlank(exchangeType) && exchangeType.equalsIgnoreCase(m.getExchangeBiEn())) {
                        currencyArr.add(m.getNumberBiEn().toLowerCase()+"_"+exchangeType);
                    }else if(StringUtils.isBlank(exchangeType)){
                        currencyArr.add(m.getNumberBiEn().toLowerCase()+"_"+m.getExchangeBiEn().toLowerCase());
                    }
                }
            } else {//本币类型不为空
                currencyTypes = currencyTypes.toLowerCase();
                JSONArray arr = JSONObject.parseArray(currencyTypes);
                if(StringUtils.isNotBlank(exchangeType)){//指定了兑换币种
                    for(int i=0;i<arr.size();i++){
                        String numberBi = arr.getString(i);
                        currencyArr.add(numberBi+"_"+exchangeType);
                    }
                }else{//未指定兑换币种
                    for(int i=0;i<arr.size();i++){
                        String numberBi = arr.getString(i);
                        Map<String, Market> map = CommonUtil.sortMapByValue(Market.markets);
                        Iterator<Map.Entry<String, Market>> iter = map.entrySet().iterator();
                        while (iter.hasNext()) {
                            Map.Entry<String, Market> entry = iter.next();
                            Market m = entry.getValue();
                            if (numberBi.equalsIgnoreCase(m.getNumberBiEn())) {
                                currencyArr.add(numberBi+"_"+m.getExchangeBiEn().toLowerCase());
                            }
                        }
                    }
                }
            }

            JSONArray datas = new JSONArray();
            for (Object currency : currencyArr) {
                String marketName = currency.toString() ;
                Market m = Market.getMarkeByName(marketName);
                if (m == null) {
                    continue;
                }
                JSONArray jarray = new JSONArray();
                // 处理K线数据 start
                if (time > 0) {// 指定时间才进行处理
                    String rtn = data(time, m);

                    if (null != rtn && !"".equals(rtn)) {
                        JSONArray json = JSONObject.parseArray("[" + rtn + "]");
                        if (size > json.size() || size == 0) {
                            if (json.size() > 0) {
                                for (int i = json.size() - 1; i > -1; i--) {
                                    JSONArray jarry = json.getJSONArray(i);
                                    JSONArray jarry2 = new JSONArray();
                                    if(jarry==null || jarry.size()==0){continue;}
                                    jarry2.add(jarry.get(0));// 时间
                                    jarry2.add(0);
                                    jarry2.add(0);
                                    jarry2.add(jarry.get(1));// 高开
                                    jarry2.add(jarry.get(4));// 关闭
                                    jarry2.add(jarry.get(2));// 高
                                    jarry2.add(jarry.get(3));// 低
                                    jarry2.add(jarry.get(5));// 总数量
                                    jarray.add(jarry2);
                                }
                            }
                        } else {
                            if (json.size() > 0) {
                                for (int i = json.size() - 1; i > json.size() - size - 1; i--) {
                                    JSONArray jarry = json.getJSONArray(i);
                                    JSONArray jarry2 = new JSONArray();
                                    if(jarry==null || jarry.size()==0){continue;}
                                    jarry2.add(jarry.get(0));// 时间
                                    jarry2.add(0);
                                    jarry2.add(0);
                                    jarry2.add(jarry.get(1));// 高开
                                    jarry2.add(jarry.get(4));// 关闭
                                    jarry2.add(jarry.get(2));// 高
                                    jarry2.add(jarry.get(3));// 低
                                    jarry2.add(jarry.get(5));// 总数量
                                    jarray.add(jarry2);
                                }
                            }
                        }
                    }
                }

                // 处理K线数据 end

                // 处理Ticker
                String ticker = DishDataCacheService.getTicker(marketName);
                if (StringUtils.isNotBlank(ticker)) {
                    JSONObject json = JSONObject.parseObject(ticker);
                    JSONObject tickerJson = json.getJSONObject("ticker");
                    tickerJson.put("highdollar", 0);
                    tickerJson.put("lowdollar", 0);
                    tickerJson.put("selldollar", 0);
                    tickerJson.put("highdollar", 0);
                    tickerJson.put("buydollar", 0);
                    tickerJson.put("dollar", 0);

                    BigDecimal legalTenderPrice = BigDecimal.ZERO;
                    if(marketName.indexOf("_btc")!= -1){
                        if (StringUtil.exist(cachePriceBtc)) {
                            legalTenderPrice = tickerJson.getBigDecimal("last").multiply(new BigDecimal(cachePriceBtc));
                        }
                    }else{
                        if (StringUtil.exist(cachePriceUsdt)) {
                            legalTenderPrice = tickerJson.getBigDecimal("last").multiply(new BigDecimal(cachePriceUsdt));
                        }
                    }
                    tickerJson.put("legal_tender", legalTender.split("_")[1] + " " + DigitalUtil.roundDownStr(legalTenderPrice.doubleValue(), 2));

                    json.put("coinName", m.getNumberBi());
                    json.put("coinFullNameEn", m.getNumberBiFullName());
                    json.put("exeByRate", 1);
                    json.put("symbol", marketName);
                    json.put("moneyType", m.getNumberBiFundsType());// 币种类型
                    json.put("time", System.currentTimeMillis() / 1000);
                    json.put("type", 100);
                    json.put("cName", "比特全球");
                    json.put("name", "btcwinex");
                    json.put("tline", jarray);
                    datas.add(json);
                }else{
                    JSONObject json = new JSONObject();
                    JSONObject tickerJson = new JSONObject();
                    tickerJson.put("highdollar", 0);
                    tickerJson.put("lowdollar", 0);
                    tickerJson.put("selldollar", 0);
                    tickerJson.put("highdollar", 0);
                    tickerJson.put("buydollar", 0);
                    tickerJson.put("dollar", 0);
                    tickerJson.put("legal_tender", legalTender.split("_")[1] + " 0.00");
                    tickerJson.put("high", 0);
                    tickerJson.put("low", 0);
                    tickerJson.put("sell", 0);
                    tickerJson.put("high", 0);
                    tickerJson.put("buy", 0);
                    tickerJson.put("last", 0);
                    tickerJson.put("weekRiseRate", 0);
                    tickerJson.put("monthRiseRate", 0);

                    json.put("coinName", m.getNumberBi());
                    json.put("coinFullNameEn", m.getNumberBiFullName());
                    json.put("exeByRate", 1);
                    json.put("symbol", marketName);
                    json.put("moneyType", m.getNumberBiFundsType());// 币种类型
                    json.put("time", System.currentTimeMillis() / 1000);
                    json.put("type", 100);
                    json.put("cName", "比特全球");
                    json.put("name", "btcwinex");
                    json.put("tline", jarray);
                    json.put("ticker", tickerJson);
                    datas.add(json);
                }
            }
            Map<String, Object> retMap = new HashMap<String, Object>();
            retMap.put("marketDatas", datas);
            json(SystemCode.code_1000, retMap);

        } catch (JSONException e) {
            // TODO: handle exception
            json(SystemCode.code_1001, L("json解析失败!"));
        } catch (Exception e) {
            // TODO: handle exception
            log.error(e.toString(), e);
            json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
        }
    }
    /*start by xzhang 20171027 新增APP通过接口获取小数配置信息*/
    @Page(Viewer = JSON)
    public void getMarkerPoint() {
        Map<String, Object> returnMap = new HashMap<>();
        JSONObject jsonPoint = null;
        Map<String, Market> marketMap = CommonUtil.sortMapByValue(Market.markets);
        Iterator<Map.Entry<String, Market>> iter = marketMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Market> entry = iter.next();
            jsonPoint = new JSONObject();
            Market m = entry.getValue();
            jsonPoint.put("numberBixDian",m.getNumberBixDian());
            jsonPoint.put("exchangeBixDian",m.getExchangeBixDian());
            returnMap.put(m.getMarket(),jsonPoint);
        }
        json(SystemCode.code_1000, returnMap);
    }
    /*end*/
}
