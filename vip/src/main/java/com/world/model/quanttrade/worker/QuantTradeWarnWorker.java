package com.world.model.quanttrade.worker;

import com.world.cache.Cache;
import com.world.model.dao.task.Worker;
import com.world.model.quanttrade.service.QuantTradeWarnService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * 量化交易异常预警定时任务
 * Created by buxianguan on 17/8/8.
 */
public class QuantTradeWarnWorker extends Worker {
    private final static Logger log = Logger.getLogger(QuantTradeWarnWorker.class);

    private static String CACHE_TIME_KEY = "qt.warn.time";

    QuantTradeWarnService quantTradeWarnService = new QuantTradeWarnService();

    public QuantTradeWarnWorker(String name, String des) {
        super(name, des);
    }

    @Override
    public void run() {

        log.info("量化交易异常预警定时任务开始...");

        //从缓存中获取上一次任务结束的时间
        long lastTime = System.currentTimeMillis();
        String lastTimeCache = Cache.Get(CACHE_TIME_KEY);
        if (StringUtils.isNotEmpty(lastTimeCache)) {
            lastTime = Long.parseLong(lastTimeCache);
        }

        long endTime = System.currentTimeMillis();
        log.info("time:" + lastTime + ", endTime:" + endTime);
        //监控GBC刷量账号是否异常
//        quantTradeWarnService.checkGBCHightFrequencyTrans(lastTime, endTime);

        //监控回购账号交易是否异常
//        quantTradeWarnService.checkBackCapital(lastTime, endTime);

        //向前多探一秒钟，防止由于短时间内大量成交导致数据遗漏。数据库有唯一索引可以保证数据不重复
        lastTime = lastTime - 1000;

        //监控量化交易账号交易是否异常
        quantTradeWarnService.checkTransRecord(lastTime, endTime);

        //监控量化交易账号资金是否异常
//        quantTradeWarnService.checkBalance();

        //把任务结束时间放入缓存中
        Cache.Set(CACHE_TIME_KEY, String.valueOf(endTime));

        log.info("量化交易异常预警定时任务结束...");
    }

    public static void main(String[] args) {
        QuantTradeWarnWorker worker = new QuantTradeWarnWorker("aaa", "aaa");
        worker.run();
    }
}
