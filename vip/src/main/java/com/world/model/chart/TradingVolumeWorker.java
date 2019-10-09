package com.world.model.chart;

import com.alibaba.fastjson.JSONObject;
import com.world.cache.Cache;
import com.world.model.dao.task.Worker;
import com.world.model.entity.Market;
import com.world.model.loan.worker.LoanAutoFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Set;

/**
 * <p>@Description: 统计所有市场24小时成交量定时任务</p>
 *
 * @author buxianguan
 * @date 2018/7/25下午5:32
 */
public class TradingVolumeWorker extends Worker {
    private final static Logger logger = Logger.getLogger(TradingVolumeWorker.class);

    public TradingVolumeWorker() {
    }

    public TradingVolumeWorker(String name, String des) {
        super(name, des);
    }

    @Override
    public void run() {
        try {
            super.run();

            BigDecimal usdtAmount = BigDecimal.ZERO;
            BigDecimal btcAmount = BigDecimal.ZERO;

            //获取所有市场
            Set<String> marketNames = Market.getAllMarketName();
            for (String marketName : marketNames) {
                //从缓存获取市场24小时成交量
                String volumeCache = Cache.Get(String.format(ChartConstant.MARKET_24HOUR_VOLUME_CACHE_KEY, marketName));
                if (StringUtils.isNotBlank(volumeCache)) {
                    BigDecimal volume = new BigDecimal(volumeCache);
                    //分别累计usdt和btc市场的成交量
                    if (marketName.toLowerCase().contains("_usdt")) {
                        usdtAmount = usdtAmount.add(volume);
                    }
                    if (marketName.toLowerCase().contains("_btc")) {
                        btcAmount = btcAmount.add(volume);
                    }
                }
            }

            if (btcAmount.compareTo(BigDecimal.ZERO) > 0) {
                JSONObject prices = LoanAutoFactory.getPrices();
                BigDecimal btcUsdtPrice = prices.getBigDecimal("btc_usdt");
                if (null != btcUsdtPrice) {
                    btcAmount = btcAmount.multiply(btcUsdtPrice).setScale(9, BigDecimal.ROUND_DOWN);
                }
                usdtAmount = usdtAmount.add(btcAmount);
            }

            Cache.Set(ChartConstant.ALL_MARKET_24HOUR_VOLUME_CACHE_KEY, usdtAmount.toPlainString(), 24 * 60 * 60);
        } catch (Exception e) {
            logger.error("[统计24小时成交量] 执行异常！", e);
        }
    }
}
