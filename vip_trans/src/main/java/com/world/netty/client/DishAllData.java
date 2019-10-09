package com.world.netty.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.dish.DishDataCacheService;
import com.world.web.response.DataResponse;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DishAllData {


    private static Logger log =  Logger.getLogger(DishAllData.class);
    private static long tradesSince, klineSince =0;//暂时先不用

    public static  void pushData(){
        String symbol = "btc_cny";
        String dishAllData = getDishAllData(tradesSince, klineSince,symbol);
        log.info(symbol+" 压缩前:"+dishAllData.length());
        long start = System.currentTimeMillis();
        dishAllData = ZipUtil.gzip(dishAllData);//数据进行压缩
        long end = System.currentTimeMillis();
        log.info(symbol+" 压缩后:"+dishAllData.length() +"  耗时："+(end-start));

        //websocket 客户端推送给Netty 服务器
        WebSocketClient.pushDishData(dishAllData,symbol);
    }

    public static String getDishAllData(long tradesSince,long klineSince,String symbol){
        JSONObject json = new JSONObject();

        //1、获取盘口深度/档位数据
        JSONObject depLenData =	getDishDepthLengthData(symbol);

        //2、获取盘口Kline tiker depth,trades数据
        //long tradesSince = super.longParam("tradesSince");
        JSONObject klineTradeData = getKlineTradeData(symbol,tradesSince);

        //3、获取Kline数据
        //long klineSince = super.longParam("klineSince");
        JSONObject klineData = getKlineLastData( symbol, klineSince);
        json.put("depthLength", depLenData);
        json.put("klineTrade", klineTradeData);
        json.put("klineData", klineData);


        return json.toJSONString();
    }

    /**
     * 获取档位深度数据
     * @param symbol 币种btcdefault/ltcdefault/ethdefault/ethbtcdefault
     * @return 字符串
     */
    private static JSONObject getDishDepthLengthData(String symbol){

        String lengths[] =new String[]{"5","10","20","50"};
        String[] depths = getDepthByMarket(symbol);
        JSONObject json = new JSONObject();
        //获取深度
        for(int i=0;i<depths.length;i++){
            json.put("dish_depth_"+depths[i].replace(".", "")+"_"+symbol, DishDataCacheService.getMegerDepthData(symbol, depths[i].replace(".", "")));
        }
        //获取档位
        for(int len=0 ;len<lengths.length;len++){
            json.put("dish_length_"+lengths[len]+"_"+symbol, DishDataCacheService.getDishDepthData(symbol, Integer.parseInt(lengths[len])));
        }

        return json;
    }


    /**
     * 获取Kline 页面的数据
     * @param symbol 币种btcdefault/ltcdefault/ethdefault/ethbtcdefault
     * @param tradesSince 交易记录获取的最后ID
     * @return
     */
    private static JSONObject getKlineTradeData(String symbol,long tradesSince){
        JSONObject json = new JSONObject();

        json.put("dish_depth_"+symbol, DishDataCacheService.getDishDepthKline50(symbol));
        json.put("dish_ticker_"+symbol, DishDataCacheService.getTicker(symbol));

        String trades = DishDataCacheService.getSinceTrade(symbol, tradesSince);
        JSONArray newTrades = new JSONArray();
        if (trades!=null && trades.startsWith("[")) {
            JSONArray tradesArr = JSONArray.parseArray(trades);
            if (tradesArr.size() > 0) {
                // 最新的交易数据放在这个数组

                for (int index = 0; index < tradesArr.size(); index++) {
                    Long tid = Long.parseLong(tradesArr.getJSONObject(index).getString("tid"));
                    if (tradesSince < tid) { // 如果数据里的tid大于上次推送的最新tid，才好推送
                        newTrades.add(tradesArr.get(index));
                    }
                }
            }
        }

        json.put("dish_trades_"+symbol, newTrades);

        return json;
    }


    /**
     * 获取增量数据kline数据
     * @param symbol   币种btcdefault/ltcdefault/ethdefault/ethbtcdefault
     * @param klineSince 最后一个获取ID
     * @return 字符串
     */
    private static JSONObject getKlineLastData(String symbol,long klineSince){

        JSONObject json = new JSONObject();

        String moneyType = "cny";
        String market = "btcdefault";
        if ("btcdefault".equalsIgnoreCase(symbol)) {
            symbol = "btc";
            market = "btcdefault";
        }else if ("ltcdefault".equalsIgnoreCase(symbol)) {
            symbol = "ltc";
            market = "ltcdefault";
        }else if ("btqdefault".equalsIgnoreCase(symbol)) {
            symbol = "btq";
            moneyType = "btc";
            market = "btqdefault";
        }else if ("ethdefault".equalsIgnoreCase(symbol)) {
            symbol = "eth";
            market = "ethdefault";
        }else if ("etcdefault".equalsIgnoreCase(symbol)) {
            symbol = "etc";
            market = "etcdefault";
        }else if ("ethbtcdefault".equalsIgnoreCase(symbol)) {
            symbol = "eth";
            moneyType = "btc";
            market = "ethbtcdefault";
        }else if ("daodefault".equalsIgnoreCase(symbol)) {
            symbol = "dao";
            market = "daodefault";
        }else {
            symbol = "btc";
        }


        if (market.length() == 0) {
            market = "btcdefault";
        }

        Map<String,String>  types = new HashMap<String,String>();
        types.put("1min", "60");
        types.put("3min", "180");
        types.put("5min", "300");
        types.put("15min", "900");
        types.put("30min", "1800");
        types.put("1hour", "3600");
        types.put("2hour", "7200");
        types.put("4hour", "14400");
        types.put("6hour", "21600");
        types.put("12hour", "43200");
        types.put("1day", "86400");
        types.put("3day", "259200");
        types.put("1week", "604800");


        //String[] types = new String[] {"60","180","300","900","1800","3600","7200","14400","21600","43200","86400","259200","604800"};


        try{

            //String rtn=String name=m.market+"_getchar"+time;
            Iterator iter = types.keySet().iterator();
            while(iter.hasNext()){

                JSONObject data = new JSONObject();
                data.put("symbol", symbol);
                data.put("moneyType", moneyType);
                data.put("contractUnit", "**");
                data.put("marketName", "**");
                data.put("USDCNY", "**");


                String key = iter.next().toString();
                String time = types.get(key);

                String rtn = DishDataCacheService.getKline(market, time);

                long since=0;
                if(klineSince>0 && !time.equals("60")){
                    long baseTime = Timestamp.valueOf("2000-01-01 00:00:00").getTime();
                    long cha = (klineSince-baseTime) % (Long.parseLong(time)*1000);
                    since = klineSince - cha;
                }else{
                    since = klineSince;
                }


                StringBuffer sb=new StringBuffer();
                sb.append("[");

                sb.append(rtn);

                sb.append("]");

                JSONArray arrays = JSONArray.parseArray(sb.toString());
                JSONArray resultArr = new JSONArray();
                int start = 0;//开始位置
                int limit = 2000;//限制数量
                int num = 0;//获取记录计数
                if(arrays.size()>limit) start = arrays.size()-limit;//k线数据起始标识
                for(int i=start;i<arrays.size();i++){
                    JSONArray ja = (JSONArray) arrays.get(i);
                    if(ja!=null && ja.size()>0){
                        long tid = ja.getLong(0) * 1000;
                        ja.set(0, tid);
                        if(since<=tid){
                            num++;
                            resultArr.add(ja);
                            if(num>=limit) break;
                        }
                        if(ja.size() == 8){
                            ja.remove(1);
                            ja.remove(1);
                        }
                    }

                }

                //log.info(time+"---"+resultArr.size());
                data.put("data", resultArr);
                DataResponse dr = new DataResponse("", true, data.toJSONString());

                json.put("dish_kline_"+market+"_"+key, dr.getJsonResponseStr());


            }
        }catch(Exception ex){
            log.error(ex.toString(), ex);
        }

        return json;
    }

    /**
     * 深度合并档位，不同盘口合并的深度不一样
     * @param symbol 币种
     * @return 深度档位数组
     * @author zhanglinbo 20160825
     */
    public static String[] getDepthByMarket(String symbol){
        String[]  depth = null;

        if (symbol.equals("btcdefault")) {
            depth = new String[]{"0.01","0.1","1"};
        }
        if ((symbol.equals("ltcdefault") || symbol.equals("ethdefault")
                || symbol.equals("etcdefault"))
                || symbol.equals("daodefault")) {
            depth = new String[]{"0.01","0.1","0.3","0.5"};
        }
        if (symbol.equals("btqdefault")) {
            depth = new String[]{"0.01"};
        }
        if (symbol.equals("ethbtcdefault")) {
            depth = new String[]{"0.000001","0.0001","0.0003","0.0005"};
        }
        if (symbol.equals("daoethdefault")) {
            depth = new String[]{"0.0001","0.001","0.002","0.003"};
        }

        return depth;
    }
}
