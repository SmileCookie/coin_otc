package com.world.controller.api.m;

import com.alibaba.fastjson.JSONObject;
import com.api.common.SystemCode;
import com.api.util.http.HttpUtil;
import com.tenstar.HTTPTcp;
import com.tenstar.Message;
import com.tenstar.MessageCancle;
import com.tenstar.RecordMessage;
import com.world.cache.Cache;
import com.world.controller.IndexServer;
import com.world.dish.DishDataCacheService;
import com.world.model.Market;
import com.world.util.DigitalUtil;
import com.world.util.sign.RSACoder;
import com.world.web.Page;
import com.world.web.action.MobileUserAction;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class V1_6 extends MobileUserAction {
    private static final long serialVersionUID = -4333874723709921756L;

    public static final String MARKETSETS_KEY ="marketSets_";

    private final String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJG14+94DEgzyd6G8+Ue+lpLKK9uIftpSZ7wvnX3jtw+6SUKldkvL1mYq9W8qIJD7w5t3YQIkVoWIlm5Eba5NcDYgfDC/QnYyr9zfDthlJECvQ8TC0wjy9cOtCC4FntewsqmGxLjTA17Zn0RJpsqXvNFjZEinR6IawvnlhPKJ/IwIDAQAB";
    private final String priKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIkbXj73gMSDPJ3obz5R76Wksor24h+2lJnvC+dfeO3D7pJQqV2S8vWZir1byogkPvDm3dhAiRWhYiWbkRtrk1wNiB8ML9CdjKv3N8O2GUkQK9DxMLTCPL1w60ILgWe17CyqYbEuNMDXtmfREmmype80WNkSKdHohrC+eWE8on8jAgMBAAECgYA0EsPx0FkEyf9szgnqNn55gBsbsnbhqpu391WjE9y/GUp0IdShqJ1EcIOENeevW2zYXCbn6mLmZzv6oqIzMuFtZ4GGbHvTsMNGtoBJsvIjV36FjdiXU7FAGqtUI+I/kFBvxFuKcil6JBFGKheQle2segoB9hAsKGoUSayAE5yjqQJBAMJllnMTMeuomhZxSQfuq4Ke3BAGGbbUfcCYnCoK1y9LBe3qXmynWYnc2caIHgbMdDiGYcTm1XOZ5lR/a2GP4HUCQQC0jiUFKWmWkx+MgverbA4QBoh+ff5M95c5T/8W2QbrUW7DV++aW4y+4D92Ei6nFcF1V8SSMxgDmqiz6pOqS243AkEAl6vlR6GZWHGyz4HR5kN8Q6yorEPmOjTubJ9lcJQGspqJZMhwpbuoa50JuRGow8svfo6yp4smzUwtXo4P/Q3hpQJAC8AIZrqYNYVjkzhet9gzXhWewmSerRGb1M4A8tKy4ZOOGsZZQHlewnlDiAKM6LDAw0sv7rfGg02IVxUYAQghpwJABiNcbBh7MnDfGaRZzE7SX/UwRn7OmGY7lFMBWadiQ/R5pKpdPrVmwdlTsefzb1acYy41LQCFCPKxVjv7sUduXQ==";

    private IndexServer server = new IndexServer(lan);

    @Page(Viewer = JSON)
    public void marketData() {
        setLan();
        String url = "https://www.btc123.com/api/getTicker";
        try {
            String symbols = param("symbols").toLowerCase();
//			String symbols = "['btcchinabtccny','huobibtccny','okcoincnbtccny']";
            JSONArray symbolsAry = new JSONArray();
            if (StringUtils.isBlank(symbols)) {
                json(SystemCode.code_1001, L("找不到货币对"));
            } else {
                symbolsAry = JSONArray.fromObject(symbols);
            }
            JSONArray datas = new JSONArray();
            Map<String, String> params = new HashMap<String, String>();
            for (Object object : symbolsAry) {
                params.put("symbol", object.toString());
                String rtn = HttpUtil.doGet(url, params);
                JSONObject rtnJson = JSONObject.parseObject(rtn);
                if (rtnJson.getBoolean("isSuc")) {
                    JSONObject data = rtnJson.getJSONObject("datas");
                    datas.add(data);
                } else {
                    continue;
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

    /**
     * 获取图表数据
     */
    @Page(Viewer = JSON)
    public void indexMarketChart() {
        setLan();
        try {
            int time = intParam("step");
            String currencyType = param("currencyType").toUpperCase();
            String exchangeType = param("exchangeType").toUpperCase();
            String marketName = getMarketName(currencyType, exchangeType);
            int size = intParam("size");

            Market m = Market.getMarkeByName(marketName);
            if (m == null) {
                json(SystemCode.code_1001);
                return;
            }

            String rtn = data(time, m);
            JSONArray jarray = new JSONArray();
            if (null != rtn && !"".equals(rtn)) {
                JSONArray json = JSONArray.fromObject("[" + rtn + "]");
                if (size > json.size()) {
                    if (json.size() > 0) {
                        for (int i = json.size() - 1; i > -1; i--) {
                            JSONArray jarry = json.getJSONArray(i);
                            JSONArray jarry2 = new JSONArray();
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
            Map<String, Object> reData = new HashMap<String, Object>();
            reData.put("chartData", jarray);
            json(SystemCode.code_1000, reData);
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
            json(SystemCode.code_1002, L("服务器出错，请稍后重试"));
        }
    }

    /**
     * 获取图表数据
     *
     * @param time
     *            时间
     * @return
     */
    public String data(int time, Market m) {
        try {
            return DishDataCacheService.getKline(m.market, String.valueOf(time));
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
            return null;
        }
    }

    @Page(Viewer = JSON)
    public void indexMarketChartTrades() {
        setLan();
        /**
         * 获取委托和交易历史记录数据
         */
        try {
            String currencyType = param("currencyType").toUpperCase();
            String exchangeType = param("exchangeType").toUpperCase();
            String market = getMarketName(currencyType, exchangeType);

            int since = request.getParameter("since") == null ? 0 : Integer.parseInt(request.getParameter("since"));
            Market m = Market.getMarkeByName(market);

            Object obj = DishDataCacheService.getSinceTrade(m.market, since);
            log.info("客户端取：" + m.market + "_OuderTrade_" + since);

            if (obj == null) {
                RecordMessage myObj = new RecordMessage();
                myObj.setPageindex(since);
                myObj.setMarket(m.market);
                try {
                    RecordMessage rtn2 = new RecordMessage();
                    if(m.listenerOpen){
                        rtn2 = server.getTradeOuter(myObj, m);
                    }else{
                        String param = HTTPTcp.ObjectToString(myObj);
                        log.info("[请求转发] 请求转发到业务处理服务器 " + m.ip + ":" + m.port);
                        String rtn = HTTPTcp.Post(m.ip, m.port, "/server/getTradeOuter", param);
                        // log.info(rtn);
                        rtn2 = (RecordMessage) HTTPTcp.StringToObject(rtn);
                    }
                    Map<String, Object> reData = new HashMap<String, Object>();
                    reData.put("marketChartDatas", rtn2.getMessage());
                    json(SystemCode.code_1000, reData);
                } catch (Exception ex2) {
                    log.error(ex2.toString(), ex2);
                    json(SystemCode.code_1001);
                }
            } else {
                Map<String, Object> reData = new HashMap<String, Object>();
                reData.put("marketChartDatas", obj.toString());
                json(SystemCode.code_1000, reData);
            }
        } catch (Exception ex) {
        }
    }
    /**
     * 设置首页显示行情模块
     * http://trans.vip.com/api/m/indexMarketSet
     */
    @Page(Viewer = JSON)
    public void indexMarketSet() {
        setLan();
        String userId = param("userId");
        String token = param("token");
        String marketSets = param("marketSets");
//		marketSets[]

        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }else if (StringUtils.isEmpty(marketSets)) {
            json(SystemCode.code_1001, L("总部没有收到您的设置信息"));
            return;
        }

        //String[] marketSets = {"abc","0"};//行情模块数组 下标：0，行情标识（btc123 api的symbol值） 1，位置（Integer）

        boolean isSetSuc = Cache.Set(MARKETSETS_KEY + userId,marketSets.toString());

        if (isSetSuc) {
            json(SystemCode.code_1000);
        } else {
            json(SystemCode.code_1002);
        }
    }
    /**
     * 获取首页显示行情模块
     * http://trans.vip.com/api/m/getIndexMarketSet
     */
    @Page(Viewer = JSON)
    public void getIndexMarketSet() {
        setLan();
        String userId = param("userId");
        String token = param("token");

        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }

        String marketSets = Cache.Get(MARKETSETS_KEY + userId);

        if (StringUtils.isNotEmpty(marketSets)) {
            Map<String, Object> reData = new HashMap<String, Object>();
            reData.put("marketSets", marketSets);
            json(SystemCode.code_1000, reData);
        } else {
            json(SystemCode.code_1002);
        }
    }
    @Page(Viewer = JSON )
    public void test(){
        Map<String, Object> reData = new HashMap<String, Object>();
        reData.put("suc", 1);
        json(SystemCode.code_1000, reData);
    }

    /**
     * 获取两种货币之间的汇率比值
     * http://trans.vip.com/api/m/exchangeRate
     * http://www.k780.com/api/finance.rate
     */
    @Page(Viewer = JSON)
    public void exchangeRate() {
        setLan();
        String currencyA = param("currencyA").toLowerCase(); // String 是 类型：CNY，USD等
        String currencyB = param("currencyB").toLowerCase(); // String 是 类型：CNY，USD等

//		// url=http://api.k780.com:88/?app=finance.rate&scur=BTC&tcur=USD&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4
//		String appkey2 = "18756";
//		String appsign2 = "a8a1c707b2bcd34a52874ab797b37c8c";
//		String url = "http://api.k780.com:88/";
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("app", "finance.rate");
//		params.put("scur", currencyB.toUpperCase());
//		params.put("tcur", currencyA.toUpperCase());
//		params.put("appkey", "16976");
//		params.put("sign", "75dc10bc5f00fdf1c33c5b9c345629e1");
//		try {
//			String rtn = HttpUtil.doPost(url, params, 1500, 1500);
//			log.info("exchangeRate rtn: " + rtn);
//			JSONObject jo = JSONObject.parseObject(rtn);
//			if (null != jo && jo.containsKey("result")) {
//				jo = jo.getJSONObject("result");
//				if (null != jo && jo.containsKey("rate")) {
//					Map<String, Object> rtnData = new HashMap<String, Object>();
//					rtnData.put("rate", jo.get("rate"));
//					rtnData.put("update", jo.get("update"));
//					json(SystemCode.code_1000, rtnData);
//				} else {
//					json(SystemCode.code_1001, "内部错误，请稍后重试");
//				}
//			} else {
//				json(SystemCode.code_1001, "内部错误，请稍后重试");
//			}
//		} catch (IOException e) {
//			log.error(e.toString(), e);
//			json(SystemCode.code_4001);
//		}

        try {
            double price = 6.4501;
            if ("cny".equals(currencyA) && "usd".equals(currencyB)) {
                if (null != Cache.Get(CACHE_CNYUSD_PRICE)) {
                    price = Double.valueOf(Cache.Get(CACHE_CNYUSD_PRICE));
                } else {
                    price = updateRade();
                }
            } else {
                if (null != Cache.Get(CACHE_CNYUSD_PRICE)) {
                    price = Double.valueOf(Cache.Get(CACHE_CNYUSD_PRICE));
                } else {
                    price = updateRade();
                }
            }
            Map<String, Object> rtnData = new HashMap<String, Object>();
            rtnData.put("rate", price);
            json(SystemCode.code_1000, rtnData);
            return;
        } catch (Exception e) {
            log.error(e.toString(), e);
            json(SystemCode.code_4001);
        }
        json(SystemCode.code_1001, L("汇率转换出错"));
    }

    final String CACHE_CNYUSD_PRICE = "cache_cnyusd_price";

    public double updateRade() {
        setLan();
        double price = 6.45D;
        try {
            String html = HttpUtil.doGet("http://download.finance.yahoo.com/d/quotes.csv?e=.csv&f=l1&s=USDCNY=x", null);
            price = Double.valueOf(html);
            Cache.Set(CACHE_CNYUSD_PRICE, price + "", 60 * 60);
        } catch (Exception e) {
            try {
                String html = HttpUtil.doGet("https://finance.yahoo.com/webservice/v1/symbols/allcurrencies/quote", null);
                String result = html.substring(html.indexOf("<field name=\"name\">USD/CNY</field>") + 34,
                        html.indexOf("<field name=\"symbol\">CNY=X</field>"));
                result = result.replace("<field name=\"price\">", "").replace("</field>", "").trim();
                price = Double.valueOf(result);
                Cache.Set(CACHE_CNYUSD_PRICE, price + "", 60 * 60);
            } catch (IOException e1) {
                log.error(e1.toString(), e1);
            }
            log.error(e.toString(), e);
        }
        return price;
    }

    /**
     * 获取用户自己的委托交易记录
     * http://trans.vip.com/api/m/entrustRecord
     */
    @Page(Viewer = JSON)
    public void entrustRecord(){
        setLan();
        int userId = intParam("userId");
        String userIdStr = userId+"";
        String token = param("token");
        String currencyType = param("currencyType").toUpperCase();
        String exchangeType = param("exchangeType").toUpperCase();

        if (isLogin(userIdStr, token) == SystemCode.code_1003 || isLogin(userIdStr, token) == SystemCode.code_402) {
            json(isLogin(userIdStr, token), L(isLogin(userIdStr, token).getValue()));
            return;
        }

        String marketName = getMarketName(currencyType, exchangeType);

        Market m = Market.getMarkeByName(marketName);
        if(m==null){
            json(SystemCode.code_1001,L("错误的市场"));
            return;
        }
        /**
         * 获取用户指定类型的交易管理数据
         * @param webId 网站id 网站id（暂时都设置城8）
         * @param userId 用户id 用户id
         * @param pageIndex 页码从1开始
         * @param pageSize 页码大小 10
         * @param type 类型   0 卖出  1 买入  -1不限制
         * @param timeFrom //时间   System.currentTimeMillis()
         * @param timeTo
         * @param numberFrom//数量查询，数量等于用户提交的数量*Market.numberBixNormal    提交过来
         * @param numberTo//数量查询
         * @param priceFrom 最低价格
         * @param priceTo 最高价格
         * @param pagesize 页码大小 最大200
         * @param status 订单状态 0不限制 1 已取消成功 2 交易成功 3 交易中（未完全成交）
         * @return 返回的是json数据，格式为 count：总数量  record数组代表结果集合entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status
         */

        //long lastTime=Long.parseLong(request.getParameter("lastTime"));
        int pageIndex=StringUtils.isEmpty(request.getParameter("pageIndex"))?1:Integer.parseInt(request.getParameter("pageIndex"));
        int type=StringUtils.isEmpty(request.getParameter("type"))?-1:Integer.parseInt(request.getParameter("type"));
        long timeFrom=StringUtils.isEmpty(request.getParameter("timeFrom"))?0:Long.parseLong(request.getParameter("timeFrom"));
        long timeTo=StringUtils.isEmpty(request.getParameter("timeTo"))?0:Long.parseLong(request.getParameter("timeTo"));
        long numberFrom=StringUtils.isEmpty(request.getParameter("numberFrom"))?0:Long.parseLong(request.getParameter("numberFrom"));
        long numberTo=StringUtils.isEmpty(request.getParameter("numberTo"))?0:Long.parseLong(request.getParameter("numberTo"));
        long priceFrom=StringUtils.isEmpty(request.getParameter("priceFrom"))?0:Long.parseLong(request.getParameter("priceFrom"));
        long priceTo=StringUtils.isEmpty(request.getParameter("priceTo"))?0:Long.parseLong(request.getParameter("priceTo"));
        int pageSize=StringUtils.isEmpty(request.getParameter("pageSize"))?0:Integer.parseInt(request.getParameter("pageSize"));
        int status=StringUtils.isEmpty(request.getParameter("status"))?0:Integer.parseInt(request.getParameter("status"));

        String dayIn3 = request.getParameter("dayIn3");
        int dateTo = 0;
        if (!StringUtils.isEmpty(dayIn3) && Integer.parseInt(dayIn3) == 0) {
            dateTo = 5;
            pageIndex++;
        }

        String cacheData = null;	//缓存中的数据
        //这里先看缓存有无数据，有数据就返回缓存的数据
        if(type==-1&&pageIndex==1&&timeFrom==0&&timeTo==0&&numberFrom==0&&numberTo==0&&priceFrom==0&&priceTo==0&&status==3&&pageSize==10){
            cacheData = Cache.Get(m.market+"_userrecord_" + userId);
        }

        if(type==-1&&pageIndex==1&&timeFrom==0&&timeTo==0&&numberFrom==0&&numberTo==0&&priceFrom==0&&priceTo==0&&status==2&&pageSize==10){
            cacheData = Cache.Get(m.market+"_userrecord_status2_" + userId);
        }

        if(type==-1&&pageIndex==1&&timeFrom==0&&timeTo==0&&numberFrom==0&&numberTo==0&&priceFrom==0&&priceTo==0&&status==-1&&pageSize==10){
            cacheData = Cache.Get(m.market+"_userrecord_status_1_" + userId);
        }
        log.info("cacheData="+cacheData);

        RecordMessage myObj = new RecordMessage();
        myObj.setUserId(userId);
        myObj.setAuth("");
        myObj.setWebId(5);
        myObj.setTypes(type);
        myObj.setTimeFrom(timeFrom);
        myObj.setTimeTo(timeTo);
        myObj.setNumberFrom(numberFrom);
        myObj.setNumberTo(numberTo);
        myObj.setPriceFrom(priceFrom);
        myObj.setPriceTo(priceTo);
        myObj.setPageindex(pageIndex);
        myObj.setPageSize(pageSize);
        myObj.setStatus(status);
        myObj.setDateTo(dateTo);
        myObj.setMarket(m.market);
        try{
            Map<String, Object> msgMap = new HashMap<String, Object>();
            String dataJsonStr = null;
            if(cacheData!=null && cacheData.length()>0){
                dataJsonStr = "{" + cacheData + "}";
            }else{
                RecordMessage rtn2  = new RecordMessage();

                if(m.listenerOpen){
                    rtn2 = server.userrecord(myObj,m);
                }else{
                    String param=HTTPTcp.ObjectToString(myObj);
                    log.info("[请求转发] 请求转发到业务处理服务器 " + m.ip + ":" + m.port);
                    String rtn=HTTPTcp.Post(m.ip,m.port,"/server/userrecord",param);

                    rtn2 =(RecordMessage)HTTPTcp.StringToObject(rtn);
                }
                dataJsonStr = "{" + rtn2.getMessage() + "}";
            }
            JSONObject dataJson = JSONObject.parseObject(dataJsonStr);

//			msgMap.put("entrustTrades", dataJson.get("record"));
            JSONArray rtnJa = new JSONArray();

            JSONArray ja = JSONArray.fromObject(dataJson.get("record"));
            for (int i = 0; i < ja.size(); i++) {
                JSONArray innerJa = ja.getJSONArray(i);

                net.sf.json.JSONObject  jo = new net.sf.json.JSONObject();
                jo.put("entrustId", innerJa.get(0));//√
                jo.put("submitTime", innerJa.get(6));//√
                jo.put("type", innerJa.get(5));//
                jo.put("unitPrice", innerJa.get(1));//
                jo.put("number", innerJa.get(2));//√
                jo.put("completeNumber", innerJa.get(3));//
                jo.put("junjia", innerJa.get(4));//
                jo.put("completeTotalMoney", innerJa.get(4));//
                jo.put("status", innerJa.get(7));//√

                rtnJa.add(jo);
            }

            pageSize = pageSize>0?pageSize:10;

            msgMap.put("entrustTrades", rtnJa);

            msgMap.put("pageIndex", pageIndex);
            msgMap.put("pageSize", pageSize);
            int totalRecord = dataJson.getIntValue("count");
            //int totalPage = count%pageSize>0?count%pageSize+1:count%pageSize;
            int totalPage = (totalRecord + pageSize -1) / pageSize;
            msgMap.put("totalPage", totalPage);
            json(SystemCode.code_1000, msgMap);

        }catch(Exception ex2){
            //Response.append(jsoncallback+"([{\"lastTime\":0}])");
            log.error(ex2.toString(), ex2);
        }
    }

    @Page(Viewer = JSON)
    public void marketDepth() {
        setLan();
        try {
            String currencyType = param("currencyType").toUpperCase();
            String exchangeType = param("exchangeType").toUpperCase();
            String marketName = getMarketName(currencyType, exchangeType);

            Market m = Market.getMarkeByName(marketName);
            if (m == null) {
                json(SystemCode.code_1001);
                return;
            }
            long lastTime = System.currentTimeMillis();
            int length = Integer.parseInt(request.getParameter("length"));
            String depthStr = request.getParameter("depth");
            double depth = 0D;
            if (null != depthStr && !"".equals(depthStr)) {
                depth = Double.parseDouble(request.getParameter("depth"));
            }
            String sResult = "";
            if (depth <= 0 || depth == 0.01 || depth == 0.000001) {
                if (length != 5 && length != 10 && length != 20 && length != 50) {
                    return;
                }
                Object obj = DishDataCacheService.getDishDepthLastTime(m.market);
                if (obj == null) {
                    RecordMessage myObj = new RecordMessage();
                    myObj.setPageSize(length);
                    myObj.setMarket(m.market);
                    try {
                        RecordMessage rtn2 = new RecordMessage();
                        if(m.listenerOpen){
                            rtn2 = server.getTrade(myObj,m);
                        }else{
                            String param = HTTPTcp.ObjectToString(myObj);
                            log.info("[请求转发] 请求转发到业务处理服务器 " + m.ip + ":" + m.port);
                            String rtn = HTTPTcp.Post(m.ip, m.port, "/server/getTrade", param);
                            // log.info(rtn);
                            rtn2 = (RecordMessage) HTTPTcp.StringToObject(rtn);
                        }

                        sResult = rtn2.getMessage().replace("(", "").replace(")", "");
                    } catch (Exception ex2) {
                        json(SystemCode.code_1001);
                        log.error(ex2.toString(), ex2);
                    }
                } else if (lastTime == Long.parseLong(obj.toString())) {
                    json(SystemCode.code_1001);
                } else {
                    sResult = DishDataCacheService.getDishDepthData(m.market, length);
                }
            } else {
                if (depth != 0.01 && depth != 0.1 && depth != 0.3 && depth != 0.5 && depth != 1.0 && depth != 0.000001
                        && depth != 0.0001 && depth != 0.0003 && depth != 0.0005) {
                    depth = 0.01;// 默认显示到0.01精度
                }
                if ("eth_btc".equals(marketName) && depth == 0.01) {
                    depth = 0.000001;
                }

                // 从缓存获取5档深度合并交易委托数据
                if(depth==0.1){
                    sResult = DishDataCacheService.getMegerDepthData(marketName, "01");//深度为0.1的数据
                }else if(depth==0.3){
                    sResult = DishDataCacheService.getMegerDepthData(marketName, "03");//深度为0.3的数据
                }else if(depth==0.5){
                    sResult = DishDataCacheService.getMegerDepthData(marketName, "05");//深度为0.5的数据
                }else if(depth==1){
                    sResult = DishDataCacheService.getMegerDepthData(marketName, "1");//深度为1的数据
                }else if(depth==0.000001){
                    sResult = DishDataCacheService.getMegerDepthData(marketName, "0000001");//深度为0.000001的数据
                }else if(depth==0.0001){
                    sResult = DishDataCacheService.getMegerDepthData(marketName, "00001");//深度为0.0001的数据
                }else if(depth==0.0003){
                    sResult = DishDataCacheService.getMegerDepthData(marketName, "00003");//深度为0.0003的数据
                }else if(depth==0.0005){
                    sResult = DishDataCacheService.getMegerDepthData(marketName, "00005");//深度为0.0005的数据
                }else{
                    sResult = DishDataCacheService.getMegerDepthData(marketName, "001");//深度为0.01的数据
                }
            }
            sResult = sResult.replace("(", "").replace(")", "");
            if (sResult.startsWith("[")) {
                sResult = sResult.substring(1, sResult.length());
            }
            if (sResult.endsWith("]")) {
                sResult = sResult.substring(0, sResult.length() - 1);
            }
            JSONObject json = JSONObject.parseObject(sResult);
            Map<String, Object> o = new HashMap<String, Object>();
            o.put("asks", null ==json ? "-" : json.get("listUp"));
            o.put("bids", null ==json ? "-" : json.get("listDown"));
            o.put("currentPrice", null ==json ? "-" : json.get("currentPrice"));
            json(SystemCode.code_1000, o);
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }


    /**
     * 委托
     */
    @Page(Viewer = JSON)
    public void doEncryptEntrust() {
        setLan();
        try {
            String userId = param("userId");
            String token = param("token");

            String entrust = param("entrust");
            entrust = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(entrust), priKey));
            JSONObject entrustJson = JSONObject.parseObject(entrust);


            String currencyType = entrustJson.getString("currencyType");

            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }
            String key = getMarketName(currencyType, null);
            Market m = Market.getMarkeByName(key);
            if (m == null) {
                // json("",false,L("错误的市场"));
                json(SystemCode.code_1001, L("错误的市场"));
                return;
            }
            int isBuy = entrustJson.getIntValue("type");// Integer.parseInt(request.getParameter("isBuy"));卖0买1

            int userid = intParam("userId");

            double unitPrice = DigitalUtil.roundDown(entrustJson.getIntValue("unitPrice"), m.exchangeBixDian);
            double number = DigitalUtil.roundDown(entrustJson.getIntValue("number"), m.numberBixDian);

            //long unitPrice = DigitalUtil.longMultiply(price, m.exchangeBixShow);
            //long number = DigitalUtil.longMultiply(count, m.numberBixShow);

            int isPlan = entrustJson.getIntValue("isPlan");
            // boolean isPlan=isReal.equals("false");//true代表是计划委托
            //log.error("用户ID：" + userId + ",currencyType：" + currencyType + ",price: " + price + ",count:" + count + ",isBuy:" + isBuy + ",isPlan:" + isPlan + ",ip:" + ip());
            Message myObj = new Message();
            myObj.setUserId(userid);
            myObj.setWebId(5);
            myObj.setNumbers(BigDecimal.valueOf(number));

            myObj.setTypes(isBuy);
            myObj.setUnitPrice(BigDecimal.valueOf(unitPrice));
            myObj.setStatus(isPlan);// 0代表真实委托 1代表计划委托
            myObj.setMarket(m.market);
            String param = HTTPTcp.ObjectToString(myObj);
            Message rtn2 =  new Message();
            if(m.listenerOpen){
                rtn2 = server.Entrust(myObj, m);
            }else{
                String rtn = HTTPTcp.Post(m.ip, m.port, "/server/entrust", param);
                rtn2 = (Message) HTTPTcp.StringToObject(rtn);
            }
            // json(L(rtn2.getMessage()),true, "{\"code\" :" +
            // rtn2.getStatus()+"}");

            // Map<String, Object> reData = new HashMap<String, Object>();
            // reData.put("message", L(rtn2.getMessage()));
            // json(SystemCode.code_1000, reData);

            if (rtn2.getStatus() == 100) {
                json(SystemCode.code_1000, L(rtn2.getMessage()));
            } else {
                json(SystemCode.code_1001, L(rtn2.getMessage()));
            }

        } catch (Exception ex2) {
            json(SystemCode.code_1002, L("委托失败，交易大盘忙碌，请稍后再试，或者通知网站！"));
            log.error(ex2.toString(), ex2);
        }
    }

    /**
     * 委托
     */
    @Page(Viewer = JSON)
    public void doEntrust() {
        setLan();
        try {
            String userId = param("userId");
            String token = param("token");
            String currencyType = param("currencyType");
            String exchangeType = param("exchangeType");
            String timeStamp = param("timeStamp");

            String sign = param("sign");
            sign = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(sign), priKey));

            String[] signAry = sign.split(",");
            if (signAry.length != 3){
                json(SystemCode.code_1003);
                return;
            }

            String userIdtmp = signAry[0];
            String tokentmp = signAry[1];
            String timeStamptmp = signAry[2];//Long.parseLong(signAry[2]);
            if (!userId.equals(userIdtmp) || !token.equals(tokentmp) || !timeStamp.equals(timeStamptmp)) {
                json(SystemCode.code_1003);
                return;
            }

            if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                return;
            }



            String fingerprint = param("fingerprint");// 指纹识别码，只要有传入，谷歌/短信、资金密码都不用验证
            if (StringUtils.isNotBlank(fingerprint))
                fingerprint = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(fingerprint),priKey));
            String safePwd = param("safePwd");//资金密码 （RSA加密）
            if (StringUtils.isNotBlank(safePwd))
                safePwd = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(safePwd),priKey));

            String marketName = getMarketName(currencyType, exchangeType);

            if(!fingerprintOrSafePwd(userId, safePwd, fingerprint, marketName)){
                return;
            }

            Market m = Market.getMarkeByName(marketName);
            if (m == null) {
                // json("",false,L("错误的市场"));
                json(SystemCode.code_1001, L("错误的市场"));
                return;
            }
            int isBuy = intParam("type");// Integer.parseInt(request.getParameter("isBuy"));卖0买1

            int userid = intParam("userId");

            double unitPrice = DigitalUtil.roundDown(doubleParam("unitPrice"), m.exchangeBixDian);
            double number = DigitalUtil.roundDown(doubleParam("number"), m.numberBixDian);

            //long unitPrice = DigitalUtil.longMultiply(price, m.exchangeBixShow);
            //long number = DigitalUtil.longMultiply(count, m.numberBixShow);

            int isPlan = intParam("isPlan");
            // boolean isPlan=isReal.equals("false");//true代表是计划委托


            log.error("用户ID：" + userId + ",currencyType：" + currencyType + ",exchangeType:" + exchangeType + ",price: " + unitPrice + ",count:" + number + ",isBuy:" + isBuy + ",isPlan:" + isPlan + ",ip:" + ip());

            Message myObj = new Message();
            myObj.setUserId(userid);
            myObj.setWebId(5);
            myObj.setNumbers(BigDecimal.valueOf(number));

            myObj.setTypes(isBuy);
            myObj.setUnitPrice(BigDecimal.valueOf(unitPrice));
            myObj.setStatus(isPlan);// 0代表真实委托 1代表计划委托
            myObj.setMarket(m.market);//市场
            String param = HTTPTcp.ObjectToString(myObj);
            Message rtn2 =  new Message();
            if(m.listenerOpen){
                rtn2 = server.Entrust(myObj, m);
            }else{
                String rtn = HTTPTcp.Post(m.ip, m.port, "/server/entrust", param);
                rtn2 = (Message) HTTPTcp.StringToObject(rtn);
            }

            // json(L(rtn2.getMessage()),true, "{\"code\" :" +
            // rtn2.getStatus()+"}");

            // Map<String, Object> reData = new HashMap<String, Object>();
            // reData.put("message", L(rtn2.getMessage()));
            // json(SystemCode.code_1000, reData);

            if (rtn2.getStatus() == 100) {
                json(SystemCode.code_1000, L(rtn2.getMessage()));
            } else {
                json(SystemCode.code_1001, L(rtn2.getMessage()));
            }

        } catch (Exception ex2) {
            json(SystemCode.code_1002, L("内部异常：") + ex2.getMessage());
            log.error(ex2.toString(), ex2);
        }
    }

    /**
     * 取消委托
     */
    @Page(Viewer = JSON)
    public void cancelEntrust(){
        setLan();
        String userId = param("userId");
        String token = param("token");
        String currencyType = param("currencyType").toUpperCase();
        String exchangeType = param("exchangeType").toUpperCase();

        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }

        String marketName = getMarketName(currencyType, exchangeType);

        Market m = Market.getMarkeByName(marketName);
        if(m==null){
            json(SystemCode.code_1001,L("错误的市场"));
            return;
        }

        int userid=intParam("userId");
        int webid=5;
        long entityId=StringUtils.isEmpty(request.getParameter("entrustId"))?0:Long.parseLong(request.getParameter("entrustId"));
        if(entityId == 0L){
            json(SystemCode.code_1001,L("此委托单id为0"));
            return;
        }

        MessageCancle myObj = new MessageCancle();
        myObj.setUserId(userid);
        myObj.setWebId(webid);

        myObj.setEntrustId(entityId);//
        myObj.setStatus(0);
        myObj.setMarket(m.market);
        try{
            MessageCancle rtn2 = new MessageCancle();
            if(m.listenerOpen){
                rtn2 = server.cancle(myObj,m);
                if(rtn2.getStatus()!=100 && rtn2.getStatus()!=200){
                    rtn2 = server.canclePlanEntrust(myObj,m);
                }
            }else{
                String param=HTTPTcp.ObjectToString(myObj);
                String rtn=HTTPTcp.DoRequest2(true,m.ip,m.port,"/server/cancle",param);
                String rtnPlan = HTTPTcp.DoRequest2(true,m.ip,m.port,"/server/canclePlanEntrust",param);
                rtn2 =(MessageCancle)HTTPTcp.StringToObject(rtn);
                if(rtn2.getStatus()!=100 && rtn2.getStatus()!=200){
                    rtn2 =(MessageCancle)HTTPTcp.StringToObject(rtnPlan);
                }
            }


            if (rtn2.getStatus()==100 || rtn2.getStatus()==200) {
                json(SystemCode.code_1000, L(rtn2.getMessage()));
            } else {
                json(SystemCode.code_1001, L(rtn2.getMessage()));
            }

        }catch(Exception ex2){
            json(SystemCode.code_1002,L("委托失败，交易大盘忙碌，请稍后再试，或者通知网站！"));
            log.error(ex2.toString(), ex2);
        }
    }
    /**
     * 批量取消
     */
    @Page(Viewer = JSON)
    public void cancelBatchEntrust() {
        setLan();
        int userid = intParam("userId");
        String cacheSyncKey = "cancelmore_entrust_" + userid;
        synchronized ("cancelmore_entrust_" + userid) {
            try {
                String lock = Cache.Get(cacheSyncKey);
                if (null != lock) {
                    json(SystemCode.code_1001, L("您的取消操作太频繁了，请稍后重试或刷新查看数据。"));
                    return;
                }
                String userId = param("userId");
                String token = param("token");
                int type = intParam("type");
                String currencyType = param("currencyType").toUpperCase();
                String exchangeType = param("exchangeType").toUpperCase();

                if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
                    json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
                    return;
                }

                String marketName = getMarketName(currencyType, exchangeType);

                Market m = Market.getMarkeByName(marketName);
                if (m == null) {
                    json(SystemCode.code_1001, L("错误的市场"));
                    return;
                }
                // String safePwd = param("payPass");
                // String userId = userId(true , true);
                // if(!safePwd(safePwd, userId)){
                // Write("",false,"请输入安全密码");
                // return;
                // }

                int webid = 5;

                double priceLow = 0D;// DigitalUtil.roundDown(doubleParam("minPrice"),
                // m.exchangeBixDian);
                double priceHigh = 0D;/// DigitalUtil.roundDown(doubleParam("maxPrice"),
                /// m.exchangeBixDian);

                //long priceLow = DigitalUtil.longMultiply(minPrice, m.exchangeBixShow);
                //long priceHigh = DigitalUtil.longMultiply(maxPrice, m.exchangeBixShow);

//				int type = 3;// Integer.parseInt(param("types").equals("")?"0":param("types"));//0
                // 按照区间设置 1取消买入 2取消卖出 3 取消所有

                MessageCancle myObj = new MessageCancle();
                myObj.setUserId(userid);
                myObj.setWebId(webid);
                myObj.setPriceLow(BigDecimal.valueOf(priceLow));
                myObj.setPriceHigh(BigDecimal.valueOf(priceHigh));

                /**
                 * 约定type值：0 卖出  1 买入  -1不限制
                 * 实际 1取消买入 2取消卖出 3 取消所有
                 */
                if (type == 0) {
                    type = 2;
                } else if (type == 1) {
                    type = 1;
                } else {
                    type = 3;
                }

                myObj.setType(type);
                myObj.setMarket(m.market);

                MessageCancle rtn2 = new MessageCancle ();
                if(m.listenerOpen){
                    rtn2 = server.cancelmorePlanEntrust(myObj,m);
                }else{
                    String param = HTTPTcp.ObjectToString(myObj);

                    String rtn = HTTPTcp.DoRequest2(true, m.ip, m.port, "/server/cancelmorePlanEntrust", param);

                    log.info(rtn);

                    rtn2 = (MessageCancle) HTTPTcp.StringToObject(rtn);
                }



                if (rtn2.getStatus() > 0) {
                    json(SystemCode.code_1000, L("批量取消成功"));
                } else {
                    json(SystemCode.code_1001, L("批量取消失败"));
                }

            } catch (Exception ex2) {
                json(SystemCode.code_1002, L("委托失败，交易大盘忙碌，请稍后再试，或者通知网站！"));
                log.error(ex2.toString(), ex2);
            } finally {
                try {
                    Cache.Delete(cacheSyncKey);
                } catch (Exception e) {}
            }
        }
    }

    /**
     * 获取各个平台配置信息-首页行情获取
     */
    @Page(Viewer = JSON)
    public void getPlatformSet() {
        setLan();
        int version = intParam("version");
        int curVersion = 1;
        List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
        if (version != curVersion) {

        }
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("platformSets", list);
        retMap.put("version", curVersion);
        json(SystemCode.code_1000, retMap);
    }

    private Map<String, Object> getPlatformSet(String platformNameChn,String platformNameEn,String simpleName, String currency, String exchangeCurrency, int isVisible) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("symbol", simpleName.concat(currency).concat(exchangeCurrency).toLowerCase());
        map.put("name", platformNameChn);
        map.put("englishName", platformNameEn);
        map.put("currencyType", currency.toUpperCase());
        map.put("isVisible", isVisible);
        return map;
    }

    /**
     * 获取交易货币配置
     */
    @Page(Viewer = JSON)
    public void getCurrencySet() {
        setLan();
        int version = intParam("version");
        int curVersion = 1;
        List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
        if (version != curVersion) {

            Map<String, Object> mapCny = new HashMap<String, Object>();
            mapCny.put("currency", "CNY");
            mapCny.put("symbol", "￥");
            mapCny.put("name", "人民币");
            mapCny.put("englishName", "RMB");
            mapCny.put("coinUrl", "");
            mapCny.put("financeCoinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_cny_finance.png");
            mapCny.put("prizeRange", "0");
            mapCny.put("marketDepth", new JSONArray());
            mapCny.put("marketLength", new JSONArray());
//			list.add(mapCny);

            Map<String, Object> mapUsd = new HashMap<String, Object>();
            mapUsd.put("currency", "USD");
            mapUsd.put("symbol", "$");
            mapUsd.put("name", "美元");
            mapUsd.put("englishName", "dollar");
            mapUsd.put("coinUrl", "");
            mapUsd.put("financeCoinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_usd_finance.png");
            mapUsd.put("prizeRange", "0");
            mapUsd.put("marketDepth", new JSONArray());
            mapUsd.put("marketLength", new JSONArray());
            /**
             * 美元不参与
             */
//			list.add(mapUsd);

            Map<String, Object> mapBtc = new HashMap<String, Object>();
            mapBtc.put("currency", "BTC");
            mapBtc.put("symbol", "฿");
            mapBtc.put("name", "比特币");
            mapBtc.put("englishName", "Bitcoin");
            mapBtc.put("coinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_btc_type_sm@3x.png");
            mapBtc.put("financeCoinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_btc_finance.png");
            mapBtc.put("prizeRange", "0.01");

//			JSONObject jBtc = new JSONObject();
//			jBtc.put("currency", "CNY");
//			jBtc.put("optional", new Double[]{0.01, 0.1, 1D});

            JSONArray jBtcs = new JSONArray();
            jBtcs.add(getMarketDepth("CNY", new Double[]{0.01, 0.1, 1D}));

            JSONArray jMLBtcs = new JSONArray();
            jMLBtcs.add(getMarketLength("CNY"));

            mapBtc.put("marketDepth", jBtcs);
            mapBtc.put("marketLength", jMLBtcs);

//			list.add(mapBtc);

            Map<String, Object> mapLtc = new HashMap<String, Object>();
            mapLtc.put("currency", "LTC");
            mapLtc.put("symbol", "Ł");
            mapLtc.put("name", "莱特币");
            mapLtc.put("englishName", "Litecoin");
            mapLtc.put("coinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_ltc_type_sm@3x.png");
            mapLtc.put("financeCoinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_ltc_finance.png");
            mapLtc.put("prizeRange", "0.2");

            JSONArray jLtcs = new JSONArray();
            jLtcs.add(getMarketDepth("CNY", new Double[]{0.01, 0.1, 0.3, 0.5}));

            JSONArray jMLLtcs = new JSONArray();
            jMLLtcs.add(getMarketLength("CNY"));

            mapLtc.put("marketDepth", jLtcs);
            mapLtc.put("marketLength", jMLLtcs);
//			list.add(mapLtc);

            Map<String, Object> mapEth = new HashMap<String, Object>();
            mapEth.put("currency", "ETH");
            mapEth.put("symbol", "E");
            mapEth.put("name", "以太币");
            mapEth.put("englishName", "Ethereum");
            mapEth.put("coinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_eth_type_sm@3x.png");
            mapEth.put("financeCoinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_eth_finance.png");
            mapEth.put("prizeRange", "0.05");

            JSONArray jEths = new JSONArray();
            jEths.add(getMarketDepth("CNY", new Double[]{0.01, 0.1, 0.3, 0.5}));
            jEths.add(getMarketDepth("BTC", new Double[]{0.000001, 0.0001, 0.0003, 0.0005}));

            JSONArray jMLEths = new JSONArray();
            jMLEths.add(getMarketLength("CNY"));
            jMLEths.add(getMarketLength("BTC"));

            mapEth.put("marketDepth", jEths);
            mapEth.put("marketLength", jMLEths);
//			list.add(mapEth);

            Map<String, Object> mapEtc = new HashMap<String, Object>();
            mapEtc.put("currency", "ETC");
            mapEtc.put("symbol", "E");
            mapEtc.put("name", "经典以太");
            mapEtc.put("englishName", "Ethereum Classic");
            mapEtc.put("coinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_etc_type_sm@3x.png");
            mapEtc.put("financeCoinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_etc_finance.png");
            mapEtc.put("prizeRange", "0.05");

            JSONArray jEtcs = new JSONArray();
            jEtcs.add(getMarketDepth("CNY", new Double[]{0.01, 0.1, 0.3}));

            JSONArray jMLEtcs = new JSONArray();
            jMLEtcs.add(getMarketLength("CNY"));

            mapEtc.put("marketDepth", jEtcs);
            mapEtc.put("marketLength", jMLEtcs);
//			list.add(mapEtc);

            /**
             * 排序
             * 人民币
             * 比特币
             * 以太币
             * 经典以太
             * 莱特币
             */
            list.add(mapCny);
            list.add(mapBtc);
            list.add(mapEth);
            list.add(mapEtc);
            list.add(mapLtc);

        }

        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("currencySets", list);
        retMap.put("version", curVersion);
        json(SystemCode.code_1000, retMap);
    }

    private JSONObject getMarketDepth(String string, Double[] doubles) {
        JSONObject jo = new JSONObject();
        jo.put("currency", string);
        List<String> optionals = new ArrayList<String>();
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(6);
        for (Double item : doubles) {
            optionals.add(format.format(item)/*BigDecimal.valueOf(item).toString()*/);
        }

        jo.put("optional", optionals);
        return jo;
    }

    private JSONObject getMarketLength(String currency) {
        JSONObject jo = new JSONObject();
        jo.put("currency", currency);
        jo.put("optional", new Integer[]{5, 10, 20, 50});
        return jo;
    }

    /**
     * 获取详情数据
     */
    @Page(Viewer = JSON)
    public void entrustDetails(){
        setLan();
        String userId = param("userId");
        String token = param("token");
        String entrustId=param("entrustId");

        if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
            json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
            return;
        }
        int userIdInt = Integer.parseInt(userId);
        String currencyType = param("currencyType").toUpperCase();
        String exchangeType = param("exchangeType").toUpperCase();
        String marketName = getMarketName(currencyType, exchangeType);

        Market m = Market.getMarkeByName(marketName);
        if (m == null) {
            json(SystemCode.code_1001,L("找不到此币种!"));
            return;
        }
        try{
            RecordMessage myObj = new RecordMessage();
            myObj.setUserId(userIdInt);
            myObj.setWebId(5);
            myObj.setTypes(101);//详情
            myObj.setMessage(entrustId);//借用来保存id的

            RecordMessage rtn2 = server.userrecord(myObj,m);

            Map<String, Object> recordMap = new HashMap<String, Object>();
            String recordJsonStr = "{" + rtn2.getMessage() + "}";
            JSONObject recordJson = JSONObject.parseObject(recordJsonStr);

            JSONArray rtnJa = new JSONArray();

            JSONArray ja = JSONArray.fromObject(recordJson.get("record"));
            for (int i = 0; i < ja.size(); i++) {
                JSONArray innerJa = ja.getJSONArray(i);

                JSONObject  jo = new JSONObject();
                jo.put("entrustId", innerJa.get(0));//√
                jo.put("transactionPrice", innerJa.get(1));//√
                jo.put("transactionTotalMoney", innerJa.get(2));//√
                jo.put("transactionNumber", innerJa.get(3));//√
                jo.put("transactionTime", innerJa.get(5));//√

                rtnJa.add(jo);
            }

            recordMap.put("entrustOrders", rtnJa);

//			msgMap.put("pageIndex", pageIndex);
//			msgMap.put("pageSize", pageSize);
//			int totalRecord = dataJson.getIntValue("count");
//			int totalPage = (totalRecord + pageSize -1) / pageSize;
//			msgMap.put("totalPage", totalPage);
            json(SystemCode.code_1000, recordMap);

        }catch(Exception ex){
            log.error(ex.toString(), ex);
            json(SystemCode.code_1002);
        }
    }

    @Page(Viewer = JSON)
    public void getVersion() {
        Map<String, Object> reData = new HashMap<String, Object>();

        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy.MM.dd G 'at' hh:mm:ss z");

        String timeStr = "2016-09-10 15:49:00";
        reData.put("version", timeStr);
        json(SystemCode.code_1000, reData);
    }

}
