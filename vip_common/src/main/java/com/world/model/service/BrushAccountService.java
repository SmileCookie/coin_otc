package com.world.model.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.world.cache.Cache;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.CommAttrBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/3/21下午8:10
 */
public class BrushAccountService extends DataDaoSupport<CommAttrBean> {
    private final static Logger logger = Logger.getLogger(BrushAccountService.class);

    private final static String BRUSH_ACCOUNT_CACHE_KEY = "comm_attr_brushUser";

    private final static String BRUSH_ACCOUNT_SQL = "SELECT * FROM comm_attr WHERE parentId = 10202000 AND attrState = 1";

    //撮合引擎获取刷量账号，不能频繁读memcache，会有并发异常，在内存中缓存1小时
    private static long lastLoadTime = 0;
    private final static long LoadFrequency = 60 * 60 * 1000;
    private static List<String> brushAccountsForMatch = new ArrayList<>();

    public List<String> getBrushAccouts() {
        List<String> brushAccounts = new ArrayList<>();
        try {
            String configCache = Cache.Get(BRUSH_ACCOUNT_CACHE_KEY);
            if (StringUtils.isBlank(configCache)) {
                List<CommAttrBean> list = super.find(BRUSH_ACCOUNT_SQL, new Object[]{}, CommAttrBean.class);
                brushAccounts = Lists.transform(list, new Function<CommAttrBean, String>() {
                    @Override
                    public String apply(CommAttrBean input) {
                        return input.getParaValue().trim();
                    }
                });
                Cache.Set(BRUSH_ACCOUNT_CACHE_KEY, JSON.toJSONString(brushAccounts), 60 * 60);
            } else {
                brushAccounts = JSONObject.parseArray(configCache, String.class);
            }
        } catch (Exception e) {
            logger.error("获取刷量账号异常！", e);
        }
        brushAccountsForMatch = brushAccounts;
        return brushAccounts;
    }

    public boolean isBrushAccount(String userId) {
        List<String> brushAccounts = getBrushAccouts();
        if (CollectionUtils.isNotEmpty(brushAccounts)) {
            return brushAccounts.contains(userId);
        }
        return false;
    }

    /**
     * 从内存获取，定期更新内存，用于不准确要求获取刷量账号的情况
     *
     * @param userId
     * @return
     */
    public boolean isBrushAccountCache(String userId) {
        if ((System.currentTimeMillis() - lastLoadTime) > LoadFrequency) {
            getBrushAccouts();
            lastLoadTime = System.currentTimeMillis();
        }

        if (CollectionUtils.isNotEmpty(brushAccountsForMatch)) {
            return brushAccountsForMatch.contains(userId);
        }
        return false;
    }
}