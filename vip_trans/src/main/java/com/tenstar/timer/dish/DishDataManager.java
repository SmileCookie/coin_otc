package com.tenstar.timer.dish;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.match.entrust.MemEntrustDataProcessor;
import com.match.entrust.MemEntrustMatchProcessor;
import com.match.rabbitmq.websocket.DishDepthProducer;
import com.match.rabbitmq.websocket.LatestTradeProducer;
import com.redis.RedisUtil;
import com.tenstar.timer.chart.ChartArray;
import com.tenstar.timer.chart.ChartData;
import com.world.cache.Cache;
import com.world.dish.DishDataCacheService;
import com.world.model.Market;
import com.world.model.daos.chart.ChartManager;
import com.world.util.DigitalUtil;
import com.world.util.string.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/***
 * 盘口
 * 深度档位数据
 *
 */
public class DishDataManager {
    public static Logger log = Logger.getLogger(DishDataManager.class);
    private final static int CACHE_EID_TIME = 2 * 60 * 60;

    private static Map<String, String[][]> buyArrMap = new HashMap<String, String[][]>();
    private static Map<String, String[][]> sellArrMap = new HashMap<String, String[][]>();

    static LatestTradeProducer latestTradeProduce = new LatestTradeProducer();
    static DishDepthProducer dishDepthProduce = new DishDepthProducer();
    private static final String BTC_EXCHANGE_RATE_KEY = "exchange:rate:btc";
    private static final String USDT_EXCHANGE_RATE_KEY = "exchange:rate:usdt";

    /**
     * 定时器调用方法缓存盘口深度和档位数据
     */
    public static void cacheDishData(Market m) {
        try {

            if (initData(m)) {
                //完成初始化数据后再处理合并和档位数据获取
                cacheDishLengthData(m);
                generateDishData_for_200(m);
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    /**
     * 初始化买/卖盘数据 200档
     * 读取数据加锁，防止多线程并发异常
     *
     * @author zhanglinbo 20160701
     */
    public static boolean initData(Market m) {
        // 缓存汇率至Redis
        cacheExchangeRate2Redis();
        if (!MemEntrustDataProcessor.isInit(m) || null == MemEntrustMatchProcessor.da) {
            log.error("系统未初始化完成，暂不返回盘口数据");
            return false;
        }
        if (!MemEntrustDataProcessor.reloadResult) {
            log.info("撮合reload数据未执行完，暂不更新盘口数据");
            return false;
        }
        String[][] buyArr = null;
        buyArr = MemEntrustDataProcessor.getBuyEntrustMap(m.market);
        if (null != buyArr) {
            /*start by xzhang 20171215 交易页面三期PRD:创建数组*/
            //买盘挂单200档数据
            for (int i = 0; i < buyArr.length; i++) {
                if (buyArr[i] == null) {
                    buyArr[i] = new String[3];
                }
            }

            buyArrMap.put(m.market, buyArr);
        }

        String[][] sellArr = null;
        sellArr = MemEntrustDataProcessor.getSellEntrustMap(m.market);
        if (null != sellArr) {
            for (int i = 0; i < sellArr.length; i++) {
                if (sellArr[i] == null) {
                    sellArr[i] = new String[3];
                }
            }
        /*end*/
            sellArrMap.put(m.market, sellArr);
        }

        return true;
    }

    /**
     * 缓存汇率到Redis
     */
    private static void cacheExchangeRate2Redis() {
        if (!RedisUtil.exists(BTC_EXCHANGE_RATE_KEY)) {
            JSONObject exchangeRate = getExchangeRateBTCFromMemcached();
            RedisUtil.set(BTC_EXCHANGE_RATE_KEY, exchangeRate.toString(), 60);
        }
        if (!RedisUtil.exists(USDT_EXCHANGE_RATE_KEY)) {
            JSONObject exchangeRate = getExchangeRateUSDTFromMemcached();
            RedisUtil.set(USDT_EXCHANGE_RATE_KEY, exchangeRate.toString(), 60);
        }
    }

    /**
     * 从Memcached中获取USDT汇率
     *
     * @return
     */
    private static JSONObject getExchangeRateUSDTFromMemcached() {
        JSONObject exchangeRate = new JSONObject();
        String usdt_usd = "1";
        exchangeRate.put("USD", new BigDecimal(StringUtil.exist(usdt_usd) ? usdt_usd : "1"));
        String usdt_cny = Cache.Get("usdt_cny");
        exchangeRate.put("CNY", new BigDecimal(StringUtil.exist(usdt_cny) ? usdt_cny : "1"));
        String usdt_eur = Cache.Get("usdt_eur");
        exchangeRate.put("EUR", new BigDecimal(StringUtil.exist(usdt_eur) ? usdt_eur : "1"));
        String usdt_gbp = Cache.Get("usdt_gbp");
        exchangeRate.put("GBP", new BigDecimal(StringUtil.exist(usdt_gbp) ? usdt_gbp : "1"));
        String usdt_aud = Cache.Get("usdt_aud");
        exchangeRate.put("AUD", new BigDecimal(StringUtil.exist(usdt_aud) ? usdt_aud : "1"));
        String usdt_btc = Cache.Get("usdt_btc");
        exchangeRate.put("BTC", new BigDecimal(StringUtil.exist(usdt_btc) ? usdt_btc : "1"));
        return exchangeRate;
    }

    /**
     * 从Memcached中获取BTC汇率
     *
     * @return
     */
    private static JSONObject getExchangeRateBTCFromMemcached() {
        JSONObject exchangeRate = new JSONObject();
        String btcUsdt = Cache.Get("btc_usdt");
        exchangeRate.put("USD", new BigDecimal(StringUtil.exist(btcUsdt) ? btcUsdt : "1"));
        String btcCny = Cache.Get("btc_cny");
        exchangeRate.put("CNY", new BigDecimal(StringUtil.exist(btcCny) ? btcCny : "1"));
        String btcEur = Cache.Get("btc_eur");
        exchangeRate.put("EUR", new BigDecimal(StringUtil.exist(btcEur) ? btcEur : "1"));
        String btcGbp = Cache.Get("btc_gbp");
        exchangeRate.put("GBP", new BigDecimal(StringUtil.exist(btcGbp) ? btcGbp : "1"));
        String btcAud = Cache.Get("btc_aud");
        exchangeRate.put("AUD", new BigDecimal(StringUtil.exist(btcAud) ? btcAud : "1"));
        return exchangeRate;
    }

    public static void generateDishData_for_200(Market m) {

        try {
            String[] depth = getDepthByMarket(m);
            if (depth != null && depth.length > 0) {
                for (int i = 0; i < depth.length; i++) {
                    //缓存深度为0.01的数据
                    JSONObject jobjDepth = generateDishDepthData(depth[i], m);
                    if (jobjDepth != null) {
                        String strDepth = depth[i].replace(".", "");
                        String redisKey = DishDataCacheService.setMegerDepthData(m.market, strDepth, jobjDepth.toJSONString(), CACHE_EID_TIME);

                        //推送websocket通知
                        String type = "step%s";
                        dishDepthProduce.send(m.market, String.format(type, i + 1), redisKey);
                    }
                }
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
        }

    }

    /**
     * 根据市场返回合并深度的类型
     *
     * @return String[]<深度>
     * @author zhanglinbo 20160701
     */
    public static String[] getDepthByMarket(Market m) {
        String[] depth = new String[]{};
        String mergePrice = m.getMergePrice();

        if (StringUtils.isNotBlank(mergePrice)) {
            depth = mergePrice.split(",");
        }

        if (depth == null || depth.length == 0) {
            depth = new String[]{"0.000001", "0.0001", "0.0003", "0.0005"};
        }

        return depth;
    }

    /**
     * 获取有效的深度合并档位，（对传入的深度档位进行判断，如存在直接返回，如不存在，返回第一个）
     *
     * @param market 市场信息
     * @param depth  传入档位
     * @return
     */
    public static String getValidDepth(String market, double depth) {

        String[] depths = getDepthByMarket(Market.getMarket(market));
        String strDepth = "";//字符串
        boolean depthFlag = false;
        for (int i = 0; i < depths.length; i++) {
            if (BigDecimal.valueOf(Double.parseDouble(depths[i])).compareTo(BigDecimal.valueOf(depth)) == 0) {
                depthFlag = true;
                strDepth = depths[i].replace(".", "");
                break;
            }
        }
        if (!depthFlag) {//不符合数据返回第一个
            strDepth = depths[0].replace(".", "");
        }
        return strDepth;
    }

    /***
     * 参数：depth 0.01,0.1,1
     * 返回{"listUp" : [[1,2,1],[2,1,3]],"listDown" : [[1,2,1],[2,1,3]]}
     * 处理合并深度数据
     * 2016-04-16
     */
    public static JSONObject generateDishDepthData(String depth, Market m) {
        try {

            //定义成员变量存放 深度合并后的数据
            TreeMap<BigDecimal, JSONArray> buyDepthsMap = new TreeMap<BigDecimal, JSONArray>(new Comparator<BigDecimal>() {
                @Override
                public int compare(BigDecimal o1, BigDecimal o2) {
                    return o2.compareTo(o1);
                }
            });//按价格从高到低排序

            TreeMap<BigDecimal, JSONArray> sellsDepthsMap = new TreeMap<BigDecimal, JSONArray>();//按价格从高到低排序

            int i = 0;
            JSONObject jobj = new JSONObject();

            JSONArray buyJarry = new JSONArray();

            BigDecimal buynumber = BigDecimal.ZERO;
            String[][] buyArr = buyArrMap.get(m.market);
            if (buyArr != null) {
                for (String[] entry : buyArr) {
                    if (entry == null || entry[0] == null) break;
                    JSONArray parry = new JSONArray();
                    BigDecimal priceKey = DigitalUtil.getBigDecimal(entry[0]);
                    BigDecimal snumber = DigitalUtil.getBigDecimal(entry[1]);
                    /*start by xzhang 20171215 交易页面三期PRD:新增用户ID*/
//                    String userIdStr = entry[2];
                    buynumber = buynumber.add(snumber);
                    //----买盘深度合并处理----
                    String price = "";
                    //根据深度合并处理价格
                    priceKey = dealMegerBuyPrice(depth, priceKey);
                    price = String.valueOf(Market.formatMoney(priceKey, m));

                    JSONArray buyData = buyDepthsMap.get(priceKey);//深度合并的对象
                    if (buyData == null) {
                        parry.add(0, DigitalUtil.getBigDecimal(price));//价格
                        parry.add(1, DigitalUtil.getBigDecimal(Market.formatNumber(snumber, m)));//数量
                        parry.add(2, DigitalUtil.getBigDecimal(Market.formatNumber(buynumber, m)));//累计深度
                        parry.add(3, "0");//用户编码
                        buyDepthsMap.put(priceKey, parry);
                    } else {
                        BigDecimal num1 = DigitalUtil.getBigDecimal(Market.formatNumber(snumber, m));//
                        BigDecimal num2 = buyData.getBigDecimal(1);
                        String userIds = buyData.getString(3);
                        //同一深度进行挂单数量累加
                        buyData.set(1, num1.add(num2));
                        buyData.set(2, DigitalUtil.getBigDecimal(Market.formatNumber(buynumber, m)));//累计深度
                        buyData.set(3, "0");//用户编码
                        buyDepthsMap.put(priceKey, buyData);
                        /*end*/
                    }

                    //买盘深度合并结束

                    i++;
                    // FIXME: 2017/8/7 suxinjie 产品要求显示25条
                    // FIXME:  产品要求显示60条
                    // 新版交易平台最多显示50条
                    if (i > 200 || buyDepthsMap.size() >= 50) {
                        break;
                    }
                }


                for (Entry<BigDecimal, JSONArray> entry : buyDepthsMap.entrySet()) {
                    JSONArray buy = entry.getValue();
                    buyJarry.add(buy);
                }
            }

            jobj.put("listDown", buyJarry);


            //卖盘合并开始
            BigDecimal sellnumber = BigDecimal.ZERO;
            JSONArray sellJarry = new JSONArray();
            int j = 0;
            String[][] sellArr = sellArrMap.get(m.market);

            if (sellArr != null) {
                for (String[] entry : sellArr) {
                    if (entry == null || entry[0] == null) break;

                    JSONArray parry = new JSONArray();
                    BigDecimal priceKey = DigitalUtil.getBigDecimal(entry[0]);
                    BigDecimal snumber = DigitalUtil.getBigDecimal(entry[1]);
                    /*start by xzhang 20171215 交易页面三期PRD:新增用户ID*/
//                    String userIdStr = entry[2];
                    sellnumber = sellnumber.add(snumber);
                    //----卖盘深度合并处理----
                    String price = "";
                    //根据深度合并卖盘价格
                    priceKey = dealMegerSellPrice(depth, priceKey);
                    price = String.valueOf(Market.formatMoney(priceKey, m));

                    JSONArray sellData = sellsDepthsMap.get(priceKey);//深度合并的对象
                    if (sellData == null) {
                        parry.add(0, DigitalUtil.getBigDecimal(price));//价格
                        parry.add(1, DigitalUtil.getBigDecimal(Market.formatNumber(snumber, m)));//数量
                        parry.add(2, DigitalUtil.getBigDecimal(Market.formatNumber(sellnumber, m)));//累计深度
                        parry.add(3, "0");//用户编码
                        sellsDepthsMap.put(priceKey, parry);
                    } else {
                        BigDecimal num1 = DigitalUtil.getBigDecimal(Market.formatNumber(snumber, m));//
                        BigDecimal num2 = sellData.getBigDecimal(1);
                        String userIds = sellData.getString(3);
                        //同一深度进行挂单数量累加
                        sellData.set(1, num1.add(num2));
                        sellData.set(2, DigitalUtil.getBigDecimal(Market.formatNumber(sellnumber, m)));//累计深度
                        sellData.set(3, "0");//用户编码
                        sellsDepthsMap.put(priceKey, sellData);
                        /*end*/
                    }
                    //买盘深度合并结束
                    j++;
                    if (j > 200 || sellsDepthsMap.size() >= 50) {
                        break;
                    }
                }
                for (Entry<BigDecimal, JSONArray> entry : sellsDepthsMap.entrySet()) {
                    JSONArray sells = entry.getValue();
                    sellJarry.add(sells);
                }
            }

            jobj.put("listUp", sellJarry);

            double[] hll = null;
            ChartArray ca = ChartData.caMap.get(m.market);
            if (ca != null) {
                hll = ca.dataMinute.get24();
            } else {
                hll = new double[]{0, 0, 0, 0};
            }
            //{"currentPrice":1506.24,"high":1506.25,"low":1506.24,"currentIsBuy":false,"dayNumber":0.0,"totalBtc":389.84}
            //String top="([{\"lastTime\":"+version+",\"currentPrice\":"+Market.formatMoney(lastPrice)+",\"high\":"+hll[0]+",\"low\":"+hll[1]+",\"currentIsBuy\":"+currentIsBuy+",\"dayNumber\":"+Market.formatNumber(numberOf24hour)+",\"totalBtc\":"+hll[2]+",

            jobj.put("lastTime", System.currentTimeMillis());
            jobj.put("currentPrice", ChartManager.getPrice(m));
            jobj.put("high", hll[0]);
            jobj.put("low", hll[1]);
            jobj.put("currentIsBuy", ChartManager.lisBuy(m));
            jobj.put("dayNumber", hll[2]);
//			jobj.put("dayNumber", new BigDecimal(hll[2]).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue());
            jobj.put("totalBtc", hll[3]);
            jobj.put("last24RiseRate", ChartManager.getLast24RiseRate(m));

//			FIXME 20170721 suxinjie 通过SynCoinPriceWorker计算出了btc对各种法币的价格,这里无需在计算了,直接去缓存即可
//			//计算币种汇率
//			String btcUsdStr = Cache.Get("btc_usd");
//			String usdCnyStr = Cache.Get("usd_cny");
//			String audCnyStr = Cache.Get("aud_cny");
//			String eurCnyStr = Cache.Get("eur_cny");
//			String gbpCnyStr = Cache.Get("gbp_cny");
//
//			BigDecimal btcUsdNumber = BigDecimal.ZERO;
//			BigDecimal btcCnyNumber = BigDecimal.ZERO;
//			BigDecimal btcAudNumber = BigDecimal.ZERO;
//			BigDecimal btcEurNumber = BigDecimal.ZERO;
//			BigDecimal btcGbpNumber = BigDecimal.ZERO;
//
//			if (StringUtil.exist(btcUsdStr)) {
//				btcUsdNumber = new BigDecimal(btcUsdStr);
//			}
//
//			if (StringUtil.exist(usdCnyStr)
//					&& new BigDecimal(usdCnyStr).compareTo(BigDecimal.ZERO) > 0
//					&& btcUsdNumber.compareTo(BigDecimal.ZERO) > 0) {
//
//				btcCnyNumber = btcUsdNumber.multiply(new BigDecimal(usdCnyStr));
//			}
//
//
//			//TODO 公共方法计算精度
//			if (StringUtil.exist(audCnyStr)
//					&& new BigDecimal(audCnyStr).compareTo(BigDecimal.ZERO) > 0
//					&& btcCnyNumber.compareTo(BigDecimal.ZERO) > 0) {
//				btcAudNumber = btcCnyNumber.divide(new BigDecimal(audCnyStr), 5,  BigDecimal.ROUND_HALF_EVEN);
//			}
//
//			if (StringUtil.exist(eurCnyStr)
//					&& new BigDecimal(eurCnyStr).compareTo(BigDecimal.ZERO) > 0
//					&& btcCnyNumber.compareTo(BigDecimal.ZERO) > 0) {
//				btcEurNumber = btcCnyNumber.divide(new BigDecimal(eurCnyStr), 5,  BigDecimal.ROUND_HALF_EVEN);
//			}
//
//			if (StringUtil.exist(gbpCnyStr)
//					&& new BigDecimal(gbpCnyStr).compareTo(BigDecimal.ZERO) > 0
//					&& btcCnyNumber.compareTo(BigDecimal.ZERO) > 0) {
//				btcGbpNumber = btcCnyNumber.divide(new BigDecimal(gbpCnyStr), 5,  BigDecimal.ROUND_HALF_EVEN);
//			}

			/*start by xzhang 20170912 新增USD折算比例，并修改JSON key：exchangeRate为exchangeRateBTC*/
            jobj.put("exchangeRateBTC", genExchangeRateBTC());
            jobj.put("exchangeRateUSD", genExchangeRateUSDT());
            /*end**/

            return jobj;
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return null;

    }

    /**
     * 生成USDT兑各币种的汇率
     *
     * @return
     */
    public static JSONObject genExchangeRateUSDT() {
        String rateStr = RedisUtil.get(USDT_EXCHANGE_RATE_KEY);
        if (StringUtil.exist(rateStr)) {
            return JSONObject.parseObject(rateStr);
        }
        return getExchangeRateUSDTFromMemcached();
    }

    /**
     * 生成BTC兑各币种的汇率
     *
     * @return
     */
    public static JSONObject genExchangeRateBTC() {
        String rateStr = RedisUtil.get(BTC_EXCHANGE_RATE_KEY);
        if (StringUtil.exist(rateStr)) {
            return JSONObject.parseObject(rateStr);
        }
        return getExchangeRateBTCFromMemcached();
    }

    /**
     * 处理买盘价格深度合并，对深度取余，价格减掉余数。向下合并
     *
     * @param depth    深度
     * @param priceKey 价格
     * @return 合并后价格
     * @author zhanglinbo 20160701
     */
    public static BigDecimal dealMegerBuyPrice(String depth, BigDecimal priceKey) {
        /*
        if(depth.equals("0.1")){
			priceKey = priceKey.subtract(priceKey.divideAndRemainder(BigDecimal.valueOf(0.1))[1]);
		}else if(depth.equals("0.3")){
			priceKey = priceKey.subtract(priceKey.divideAndRemainder(BigDecimal.valueOf(0.3))[1]);
		}else if(depth.equals("0.5")){
			priceKey = priceKey.subtract(priceKey.divideAndRemainder(BigDecimal.valueOf(0.5))[1]);
		}else if(depth.equals("1")){
			priceKey = priceKey.subtract(priceKey.divideAndRemainder(BigDecimal.valueOf(1))[1]);
		}else if(depth.equals("0.000001")){
			priceKey = priceKey.subtract(priceKey.divideAndRemainder(BigDecimal.valueOf(0.000001))[1]);
		}else if(depth.equals("0.0001")){
			priceKey = priceKey.subtract(priceKey.divideAndRemainder(BigDecimal.valueOf(0.0001))[1]);
		}else if(depth.equals("0.0003")){
			priceKey = priceKey.subtract(priceKey.divideAndRemainder(BigDecimal.valueOf(0.0003))[1]);
		}else if(depth.equals("0.0005")){
			priceKey = priceKey.subtract(priceKey.divideAndRemainder(BigDecimal.valueOf(0.0005))[1]);
		}else if(depth.equals("0.001")){
			priceKey = priceKey.subtract(priceKey.divideAndRemainder(BigDecimal.valueOf(0.001))[1]);
		}else if(depth.equals("0.002")){
			priceKey = priceKey.subtract(priceKey.divideAndRemainder(BigDecimal.valueOf(0.002))[1]);
		}else if(depth.equals("0.003")){
			priceKey = priceKey.subtract(priceKey.divideAndRemainder(BigDecimal.valueOf(0.003))[1]);
		}
		*/
        priceKey = priceKey.subtract(priceKey.divideAndRemainder(DigitalUtil.getBigDecimal(depth))[1]);
        return priceKey;
    }


    /**
     * 处理买盘价格深度合并，对深度取余，价格减掉余数  + 深度。向上合并
     *
     * @param depth    深度
     * @param priceKey 买盘价格
     * @return 合并后价格
     * @author zhanglinbo 20160701
     */
    public static BigDecimal dealMegerSellPrice(String depth, BigDecimal priceKey) {
        /*if(depth.equals("0.1")){
            int ys= (priceKey % 10);
			if(ys>0){
				priceKey = priceKey - ys +10 ;
			}
		}else if(depth.equals("0.3")){
			int ys= (priceKey % 30);
			if(ys>0){
				priceKey = priceKey - ys +30 ;
			}
		}else if(depth.equals("0.5")){
			int ys= (priceKey % 50);
			if(ys>0){
				priceKey = priceKey - ys +50 ;
			}
		}else if(depth.equals("1")){
			int ys= (priceKey % 100);
			if(ys>0){
				priceKey = priceKey - ys +100 ;
			}
		}else if(depth.equals("0.000001")){
			int ys= (priceKey % 100);
			if(ys>0){
				priceKey = priceKey - ys + 100 ;
			}
		}else if(depth.equals("0.0001")){
			int ys= (priceKey % 10000);
			if(ys>0){
				priceKey = priceKey - ys + 10000 ;
			}
		}else if(depth.equals("0.0003")){
			int ys= (priceKey % 30000);
			if(ys>0){
				priceKey = priceKey - ys +30000 ;
			}
		}else if(depth.equals("0.0005")){
			int ys= (priceKey % 50000);
			if(ys>0){
				priceKey = priceKey - ys +50000 ;
			}
		}else if(depth.equals("0.001")){
			int ys= (priceKey % 100000);
			if(ys>0){
				priceKey = priceKey - ys + 100000 ;
			}
		}else if(depth.equals("0.002")){
			int ys= (priceKey % 200000);
			if(ys>0){
				priceKey = priceKey - ys +200000 ;
			}
		}else if(depth.equals("0.003")){
			int ys= (priceKey % 300000);
			if(ys>0){
				priceKey = priceKey - ys +300000 ;
			}
		}*/
        BigDecimal ys = priceKey.divideAndRemainder(DigitalUtil.getBigDecimal(depth))[1];
        if (ys.compareTo(BigDecimal.ZERO) > 0) {
            priceKey = priceKey.subtract(ys).add(DigitalUtil.getBigDecimal(depth));
        }
        return priceKey;
    }


    /**
     * 缓存各币种盘口档位数据
     * modify by xwz 20171107 增加200档数据
     */
    public static void cacheDishLengthData(Market m) {
        long version = System.currentTimeMillis();
        StringBuilder data5 = new StringBuilder();
        StringBuilder data8 = new StringBuilder();
        StringBuilder data10 = new StringBuilder();
        StringBuilder data20 = new StringBuilder();
        StringBuilder data50 = new StringBuilder();
        StringBuilder data60 = new StringBuilder();
        /*add by xwz 20171108*/
        StringBuilder data200 = new StringBuilder();
        StringBuilder data50Outer = new StringBuilder();
        //时间    curNow  防止新交易充值lastTime导致前台看不到数据    9-13 0：
        double[] hll = null;
        ChartArray ca = ChartData.caMap.get(m.market);
        if (ca != null) {
            hll = ca.dataMinute.get24();
        } else {
            hll = new double[]{0, 0, 0, 0};
        }

        StringBuilder top = new StringBuilder();
        top.append("{\"lastTime\":").append(version).append(",\"currentPrice\":").append(ChartManager.getPrice(m)).append(",\"high\":").append(hll[0])
                .append(",\"low\":").append(hll[1]).append(",\"currentIsBuy\":").append(ChartManager.lisBuy(m)).append(",\"dayNumber\":").append(hll[2])
                .append(",\"totalBtc\":").append(hll[3]).append(",\"last24RiseRate\":").append(ChartManager.getLast24RiseRate(m))
                //添加折算比例
                .append(",\"exchangeRateBTC\":").append(genExchangeRateBTC().toJSONString())
                .append(",\"exchangeRateUSD\":").append(genExchangeRateUSDT().toJSONString())

                .append(",\"listUp\":[");

//        data5.append(top).append(getSellList(5, false, m)).append("],\"listDown\":[").append(getBuyList(5, m)).append("],\"transction\":[]}");
//        data10.append(top).append(getSellList(10, false, m)).append("],\"listDown\":[").append(getBuyList(10, m)).append("],\"transction\":[]}");
//        data20.append(top).append(getSellList(20, false, m)).append("],\"listDown\":[").append(getBuyList(20, m)).append("],\"transction\":[]}");
        data50.append(top).append(getSellList(50, false, m)).append("],\"listDown\":[").append(getBuyList(50, m)).append("],\"transction\":[]}");
        data60.append(top).append(getSellList(60, false, m)).append("],\"listDown\":[").append(getBuyList(60, m)).append("],\"transction\":[]}");
        data200.append(top).append(getSellList(200, false, m)).append("],\"listDown\":[").append(getBuyList(200, m)).append("],\"transction\":[]}");
        data50Outer.append("{\"asks\":[").append(getSellList(50, true, m)).append("],\"bids\":[").append(getBuyList(50, m)).append("]}");

//        data8.append(top).append(getSellList(8, false, m)).append("],\"listDown\":[").append(getBuyList(8, m)).append("],\"transction\":[]}");

        //缓存移到redis，降低memcache的压力
//        DishDataCacheService.setDishDepthData(m.market, 5, data5.toString(), 2 * 60 * 60);
//        DishDataCacheService.setDishDepthData(m.market, 10, data10.toString(), 2 * 60 * 60);
//        DishDataCacheService.setDishDepthData(m.market, 20, data20.toString(), 2 * 60 * 60);
//        DishDataCacheService.setDishDepthData(m.market, 50, data50.toString(), 2 * 60 * 60);
        DishDataCacheService.setDishDepthData(m.market, 60, data60.toString(), 2 * 60 * 60);
        //页面显示50条，缓存50条，用于websocket推送
        String dishDepthRedisKey = DishDataCacheService.setDishDepthData(m.market, 50, data50.toString(), 2 * 60 * 60);
        //发送mq通知websocket
        dishDepthProduce.send(m.market, "step0", dishDepthRedisKey);
        DishDataCacheService.setDishDepthData(m.market, 200, data200.toString(), 2 * 60 * 60);

        DishDataCacheService.setDishDepthKline50(m.market, data50Outer.toString(), 2 * 60 * 60);

        JSONArray lastTradeArray = ChartManager.getLastTrade50(m);
        if (lastTradeArray != null) {
            String rtn = lastTradeArray.toJSONString();
            String latestTradeRedisKey = DishDataCacheService.setLatestTrade(m.market, rtn, 60);
            //发送mq通知websocket
            latestTradeProduce.send(m.market, latestTradeRedisKey);
        }

        //存储一个最后时间戳
        DishDataCacheService.setDishDepthLastTime(m.market, version + "", 60 * 120);

        //缓存最新价格
        String cacheKey = m.numberBiEn.toLowerCase() + "_" + m.exchangeBiEn.toLowerCase() + "_l_price";
        Cache.Set(cacheKey, Market.formatMoney(ChartManager.getPrice(m), m) + "", 15 * 60 * 60 * 24);//保存15天
    }


    /**
     * 获取指定档位数数据
     *
     * @param desc
     * @param num
     * @return
     */
    public static String getSellList(int num, boolean desc, Market m) {
        String[][] sellArr = sellArrMap.get(m.market);
        if (sellArr == null) {
            num = 0;
        } else {
            if (sellArr.length < num) {
                num = sellArr.length;
            }
        }
        StringBuilder sb = new StringBuilder();
        if (desc) {
            for (int i = (num - 1); i > -1; i--) {
                //start by xzhang 20171215 交易页面三期PRD:新增用户ID
                //新交易平台，去掉与我相关挂单，没啥用
                if (sellArr[i] != null && StringUtils.isNotBlank(sellArr[i][0]) && StringUtils.isNotBlank(sellArr[i][1]))
                    sb.append(",[").append(Market.formatMoney(DigitalUtil.getBigDecimal(sellArr[i][0]), m)).append(",").append(Market.formatNumber(DigitalUtil.getBigDecimal(sellArr[i][1]), m)).append(",\"0\"]");
                else
                    continue;
            }
        } else {
            for (int i = 0; i < num; i++) {
                if (sellArr[i] != null && StringUtils.isNotBlank(sellArr[i][0]) && StringUtils.isNotBlank(sellArr[i][1]))
                    //start by xzhang 20171215 交易页面三期PRD:新增用户ID
                    //新交易平台，去掉与我相关挂单，没啥用
                    sb.append(",[").append(Market.formatMoney(DigitalUtil.getBigDecimal(sellArr[i][0]), m)).append(",").append(Market.formatNumber(DigitalUtil.getBigDecimal(sellArr[i][1]), m)).append(",\"0\"]");
                else
                    break;
            }
        }
        String rtn = sb.toString();

        if (rtn.length() > 0)
            rtn = rtn.substring(1);
        return rtn;
    }

    /**
     * 获取指定档位数数据
     *
     * @param num
     * @param m
     * @return
     */
    public static String getBuyList(int num, Market m) {
        StringBuilder sb = new StringBuilder();
        String[][] buyArr = buyArrMap.get(m.market);
        if (buyArr == null) {
            num = 0;
        } else {
            if (buyArr.length < num) {
                num = buyArr.length;
            }
        }
        for (int i = 0; i < num; i++) {
            if (buyArr[i] != null && StringUtils.isNotBlank(buyArr[i][0]) && StringUtils.isNotBlank(buyArr[i][1]))
                //start by xzhang 20171215 交易页面三期PRD:新增用户ID
                //新交易平台，去掉与我相关挂单，没啥用
                sb.append(",[").append(Market.formatMoney(DigitalUtil.getBigDecimal(buyArr[i][0]), m)).append(",").append(Market.formatNumber(DigitalUtil.getBigDecimal(buyArr[i][1]), m)).append(",\"0\"]");
            else
                break;
        }

        String rtn = sb.toString();

        if (rtn.length() > 0)
            rtn = rtn.substring(1);
        return rtn;
    }
}
