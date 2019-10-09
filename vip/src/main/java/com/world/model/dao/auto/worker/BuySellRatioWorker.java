package com.world.model.dao.auto.worker;

import com.alibaba.fastjson.JSONObject;
import com.world.cache.Cache;
import com.world.data.mysql.Data;
import com.world.model.dao.task.Worker;
import com.world.model.entity.Market;
import com.world.util.DigitalUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 统计每个市场的买卖占比
 *
 * Created by suxinjie on 2017/7/24.
 */
public class BuySellRatioWorker extends Worker{
    public static Logger LOGGER = Logger.getLogger(BuySellRatioWorker.class.getName());

    // 一天的成交记录, transrecord表成交记录3天迁移一次,所以取24小时的,查源表即可
    private static final String SQL_SELL_NUM = "SELECT IFNULL(sum(totalPrice),0) as sellNum FROM transrecord where types=0 and unitPrice > 0 and times >(UNIX_TIMESTAMP(NOW()) - 86400) * 1000";
    private static final String SQL_BUY_NUM = "SELECT IFNULL(sum(totalPrice),0) as buyNum FROM transrecord where types=1 and unitPrice > 0 and times >(UNIX_TIMESTAMP(NOW()) - 86400) * 1000";

    public BuySellRatioWorker(String name, String des) {
        super(name, des);
    }

    @Override
    public void run() {
        super.run();

        //1. 获取所有市场
        //2. 统计每个市场的买卖占比(已成交的数量)
        //3. 放入缓存

        Map<String,JSONObject> markets = Market.getMarketsMap();
        if (!markets.isEmpty()) {
            Iterator<Map.Entry<String, JSONObject>> iterator = markets.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, JSONObject> entry = iterator.next();
                JSONObject market = entry.getValue();

                BigDecimal buyNum = BigDecimal.ZERO;
                BigDecimal sellNum = BigDecimal.ZERO;

                List<BigDecimal> buyList = (List<BigDecimal>) Data.GetOne(market.getString("db"), SQL_BUY_NUM, null);
                if (CollectionUtils.isNotEmpty(buyList)) {
                    buyNum = buyList.get(0);
                }

                List<BigDecimal> sellList = (List<BigDecimal>) Data.GetOne(market.getString("db"), SQL_SELL_NUM, null);
                if (CollectionUtils.isNotEmpty(sellList)) {
                    sellNum = sellList.get(0);
                }

                Cache.Set(market.getString("market") + "_buy_sell", "{\"buy\":" + DigitalUtil.roundDown(buyNum, 7) + ",\"sell\":" + DigitalUtil.roundDown(sellNum, 7) + "}");
                LOGGER.info(market.getString("market") + " 市场, 计算24小时买卖数量 buyNum=" + buyNum + " ,sellNum=" + sellNum);
            }
        }
    }
}
