package com.socket;

import com.world.cache.Cache;
import com.world.config.GlobalConfig;
import com.world.util.string.StringUtil;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>@Description: </p>
 *
 * @author Sue
 * @date 2018/4/18下午3:46
 */
public class IOSocketCache {

    public static Logger log = Logger.getLogger(IOSocketCache.class.getName());

    /**
     * 本地异步缓存数据
     */
    protected static final Map<String, String> data = new ConcurrentHashMap<>();

    public static String get(String key) {

        String result;
        if (GlobalConfig.proxyEnable) {
            IOClient.get(key);
            result = data.get(key);
        } else {
            result = Cache.Get(key);
        }

        return StringUtil.exist(result) ? result : "";
    }

    public static void put(String key, String val) {
        data.put(key, val);
    }
}
