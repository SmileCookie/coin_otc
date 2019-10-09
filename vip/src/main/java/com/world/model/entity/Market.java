package com.world.model.entity;

import com.alibaba.fastjson.JSONObject;
import com.world.cache.Cache;
import com.world.util.request.HttpUtil;
import com.world.web.action.Action;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Market {

    private final static Logger log = Logger.getLogger(Market.class);

    private static Map<String, JSONObject> marketsMap = new HashMap<>();
    public static Set<String> marketTypes = new HashSet<>();
    private static String defMarketName = "btc_usdt";//默认市场名称

    static {
        //初始化先清除一下重新拿
        Cache.Delete("ALL_MARKETS");
//        Cache.Delete("ALL_MARKET_TYPES");
    }

    /**
     * 获取盘口配置的市场信息 存放在静态变量map中，key:市场名称如 etc_btc value:JSONObject
     *
     * @return map
     * @author 20160929
     */
    public static Map<String, JSONObject> getMarketsMap() {
        if (marketsMap.isEmpty()) {
            initMarket();
        }
        return marketsMap;
    }

    /**
     * 从trans初始化市场配置
     */
    private static void initMarket() {
        try {
            JSONObject json = HttpUtil.getJson(Action.TRANS_DOMAIN + "/getAllMarket", null, 3000, 3000, false);
            JSONObject markets = json.getJSONObject("datas");
//                int defNo = 0;
            if (markets != null) {
                marketsMap = new HashMap<>();
                marketTypes = new HashSet<>();

                Iterator<Entry<String, Object>> iter = markets.entrySet().iterator();
                while (iter.hasNext()) {
                    Entry<String, Object> entry = iter.next();
                    JSONObject m = (JSONObject) entry.getValue();

                    marketsMap.put(entry.getKey(), m);
                    marketTypes.add(entry.getKey().split("_")[1]);
                }
            }
        } catch (IOException e) {
            log.error("获取市场新信息异常", e);
        }
    }

    /**
     * 重新加载市场配置
     */
    public static synchronized boolean reloadMarket() {
        try {
            log.info("重新加载市场配置(vip):开始");
//            Cache.Delete("ALL_MARKETS");
            initMarket();
            log.info("重新加载市场配置(vip):结束");
            return true;
        } catch (Exception e) {
            log.info("重新加载市场配置(vip)失败");
            return false;
        }
    }

    /**
     * 根据市场名称获取市场详细配置信息对象，返回JSON对象
     *
     * @param name 市场名称 如:etc_btc
     * @return JOSN对象
     * @author 20160929
     */
    public static JSONObject getMarketByName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        name = name.toLowerCase();//转为小写
        JSONObject market = null;
        market = getMarketsMap().get(name);
        return market;
    }

    /**
     * 获取默认市场名称
     *
     * @return 默认市场 字符串
     * @author 20160929
     */
    public static String getDefMarketName() {
         /*modify by xwz 20171017 默认市场设置为btc_usdt*/
//        if (StringUtils.isEmpty(defMarketName)) {
//            getMarketsMap();
//        }
        return defMarketName;
    }

    /**
     * 获取所有市场的名称
     * @return
     */
    public static Set<String> getAllMarketName() {
        return Market.getMarketsMap().keySet();
    }


}
