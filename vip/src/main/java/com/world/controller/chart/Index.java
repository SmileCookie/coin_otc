package com.world.controller.chart;

import com.world.cache.Cache;
import com.world.model.chart.ChartConstant;
import com.world.web.Page;
import com.world.web.action.BaseAction;
import org.apache.log4j.Logger;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/7/25下午5:58
 */
public class Index extends BaseAction {
    private static Logger logger = Logger.getLogger(Index.class);

    /**
     * 获取平台24小时成交量
     */
    @Page(Viewer = JSON)
    public void getTradingVolume() {
        try {
            String tradingVolume = Cache.Get(ChartConstant.ALL_MARKET_24HOUR_VOLUME_CACHE_KEY);
            json("success", true, tradingVolume, true);
        } catch (Exception e) {
            logger.error("获取平台24小时成交量异常", e);
            json("出现异常", false, "", true);
        }
    }
}
