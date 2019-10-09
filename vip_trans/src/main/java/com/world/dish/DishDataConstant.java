package com.world.dish;

import com.redis.RedisUtil;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/7/14上午10:12
 */
public class DishDataConstant {

    public final static String MARKET_DISH_DEPTH_KEY_5 = "%s_datachart5";
    public final static String MARKET_DISH_DEPTH_KEY_8 = "%s_datachart8";
    public final static String MARKET_DISH_DEPTH_KEY_10 = "%s_datachart10";
    public final static String MARKET_DISH_DEPTH_KEY_20 = "%s_datachart20";
    public final static String MARKET_DISH_DEPTH_KEY_50 = "%s_datachart50";
    public final static String MARKET_DISH_DEPTH_KEY_60 = "%s_datachart60";
    public final static String MARKET_DISH_DEPTH_KEY_200 = "%s_datachart200";

    public final static String MARKET_DISH_DEPTH_KEY_50_OUTER = "%s_datachart50Outer";

    public final static String MARKET_DISH_DEPTH_LAST_TIME_KEY = "%s_datachartLastTime";

    public final static String MARKET_DISH_LATEST_TRADE_KEY = "%s_OuderTrade_0";

    public final static String MARKET_DISH_SINCE_TRADE_KEY = "%s_OuderTrade_%s";

    public final static String MARKET_MERGE_DEPTH_KEY = "dish_depth_%s_%s";

    public final static String MARKET_HOT_DATA_KEY = "%s_hotdata";

    public final static String MARKET_TICKER_KEY = "%s_hotdata2";

    public final static String MARKET_DISH_KLINE = "%s_getchar%s";

    public final static String All_MARKET_HOT_DATA_KEY = "all_market_hotdata";

    public static void main(String[] args) {
//        RedisUtil.set("test", "test1", 0);
        System.out.println(RedisUtil.get("test"));
    }
}