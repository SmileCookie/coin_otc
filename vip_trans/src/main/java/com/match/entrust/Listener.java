package com.match.entrust;

import com.tenstar.timer.entrust.ScanPlanEntrustTask;
import com.world.model.Market;
import org.apache.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/5/16下午4:17
 */
public class Listener implements ServletContextListener {
    private final static Logger logger = Logger.getLogger(Listener.class);
    private ScheduledExecutorService executorService = null;

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        executorService.shutdownNow();
        logger.info(" [撮合交易] 撮合引擎销毁");
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        List<Market> openMarkets = new ArrayList<>();
        for (Map.Entry<String, Market> entry : Market.markets.entrySet()) {
            Market market = entry.getValue();
            if (market.listenerOpen) {
                openMarkets.add(market);
            }
        }

        executorService = Executors.newScheduledThreadPool(openMarkets.size() * 2 + 3);
        for (Market market : openMarkets) {
            //撮合定时任务
            executorService.scheduleAtFixedRate(new MemEntrustMatchProcessor(market), 0, 1, TimeUnit.MILLISECONDS);
            //1秒跑一次计划委托是否触发
            executorService.scheduleAtFixedRate(new ScanPlanEntrustTask(market), 0, 1000, TimeUnit.MILLISECONDS);
            logger.info(market.market + " [撮合交易] 撮合引擎开启");
        }

        if (openMarkets.size() > 0) {
            //更新内存成交记录任务
            executorService.scheduleAtFixedRate(new MemTransRecordProcessor(), 0, 500, TimeUnit.MILLISECONDS);
            executorService.scheduleAtFixedRate(new CompleteDealNotifyProcessor(), 0, 1, TimeUnit.SECONDS);
            //定时每30分钟从数据库重新load内存数据
            executorService.scheduleWithFixedDelay(new MemEntrustDataProcessor(), 0, 30, TimeUnit.MINUTES);
        }
    }
}
