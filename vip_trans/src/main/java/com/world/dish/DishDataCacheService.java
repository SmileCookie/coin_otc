package com.world.dish;

import com.redis.RedisUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/7/14上午10:08
 */
public class DishDataCacheService {
    private final static Logger logger = LoggerFactory.getLogger(DishDataCacheService.class);

    public static String setLatestTrade(String market, String value, int expireSeconds) {
        String key = String.format(DishDataConstant.MARKET_DISH_LATEST_TRADE_KEY, market);
        RedisUtil.set(key, value, expireSeconds);
        return key;
    }

    public static String getLatestTrade(String market) {
        String key = String.format(DishDataConstant.MARKET_DISH_LATEST_TRADE_KEY, market);
        return RedisUtil.get(key);
    }

    public static void setSinceTrade(String market, long since, String value, int expireSeconds) {
        String key = String.format(DishDataConstant.MARKET_DISH_SINCE_TRADE_KEY, market, since);
        RedisUtil.set(key, value, expireSeconds);
    }

    public static String getSinceTrade(String market, long since) {
        String key = String.format(DishDataConstant.MARKET_DISH_SINCE_TRADE_KEY, market, since);
        return RedisUtil.get(key);
    }

    public static String setDishDepthData(String market, int length, String value, int expireSeconds) {
        String redisKey = getDishDepthKey(market, length);
        if (StringUtils.isNotBlank(redisKey)) {
            RedisUtil.set(redisKey, value, expireSeconds);
        }
        return redisKey;
    }

    public static String getDishDepthData(String market, int length) {
        String redisKey = getDishDepthKey(market, length);
        if (StringUtils.isNotBlank(redisKey)) {
            return RedisUtil.get(redisKey);
        }
        return null;
    }

    private static String getDishDepthKey(String market, int length) {
        String redisKey = "";
        switch (length) {
            case 5:
                redisKey = String.format(DishDataConstant.MARKET_DISH_DEPTH_KEY_5, market);
                break;
            case 8:
                redisKey = String.format(DishDataConstant.MARKET_DISH_DEPTH_KEY_8, market);
                break;
            case 10:
                redisKey = String.format(DishDataConstant.MARKET_DISH_DEPTH_KEY_10, market);
                break;
            case 20:
                redisKey = String.format(DishDataConstant.MARKET_DISH_DEPTH_KEY_20, market);
                break;
            case 50:
                redisKey = String.format(DishDataConstant.MARKET_DISH_DEPTH_KEY_50, market);
                break;
            case 60:
                redisKey = String.format(DishDataConstant.MARKET_DISH_DEPTH_KEY_60, market);
                break;
            case 200:
                redisKey = String.format(DishDataConstant.MARKET_DISH_DEPTH_KEY_200, market);
                break;
            default:
                break;
        }
        return redisKey;
    }

    public static void setDishDepthKline50(String market, String value, int expireSeconds) {
        String key = String.format(DishDataConstant.MARKET_DISH_DEPTH_KEY_50_OUTER, market);
        RedisUtil.set(key, value, expireSeconds);
    }

    public static String getDishDepthKline50(String market) {
        String key = String.format(DishDataConstant.MARKET_DISH_DEPTH_KEY_50_OUTER, market);
        return RedisUtil.get(key);
    }

    public static void setDishDepthLastTime(String market, String value, int expireSeconds) {
        String key = String.format(DishDataConstant.MARKET_DISH_DEPTH_LAST_TIME_KEY, market);
        RedisUtil.set(key, value, expireSeconds);
    }

    public static String getDishDepthLastTime(String market) {
        String key = String.format(DishDataConstant.MARKET_DISH_DEPTH_LAST_TIME_KEY, market);
        return RedisUtil.get(key);
    }

    public static String setMegerDepthData(String market, String strDepth, String value, int expireSeconds) {
        String key = String.format(DishDataConstant.MARKET_MERGE_DEPTH_KEY, strDepth, market);
        RedisUtil.set(key, value, expireSeconds);
        return key;
    }

    public static String getMegerDepthData(String market, String strDepth) {
        String key = String.format(DishDataConstant.MARKET_MERGE_DEPTH_KEY, strDepth, market);
        return RedisUtil.get(key);
    }

    public static void setHotData(String market, String value, int expireSeconds) {
        String key = String.format(DishDataConstant.MARKET_HOT_DATA_KEY, market);
        RedisUtil.set(key, value, expireSeconds);
    }

    public static String getHotData(String market) {
        String key = String.format(DishDataConstant.MARKET_HOT_DATA_KEY, market);
        return RedisUtil.get(key);
    }

    public static void setTicker(String market, String value, int expireSeconds) {
        String key = String.format(DishDataConstant.MARKET_TICKER_KEY, market);
        RedisUtil.set(key, value, expireSeconds);
    }

    public static String getTicker(String market) {
        String key = String.format(DishDataConstant.MARKET_TICKER_KEY, market);
        return RedisUtil.get(key);
    }

    public static void setKline(String market, String time, String value, int expireSeconds) {
        String key = String.format(DishDataConstant.MARKET_DISH_KLINE, market, time);
        RedisUtil.set(key, value, expireSeconds);
    }

    public static String getKline(String market, String time) {
        String key = String.format(DishDataConstant.MARKET_DISH_KLINE, market, time);
        return RedisUtil.get(key);
    }

    public static void setALlMarketHotData(String market, String value, int expireSeconds) {
        RedisUtil.hset(DishDataConstant.All_MARKET_HOT_DATA_KEY, market, value, expireSeconds);
    }
}
