package com.match.money;

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
 * @date 2018/5/16下午4:23
 */
public class Listener implements ServletContextListener {
    private final static Logger logger = Logger.getLogger(Listener.class);
    private ScheduledExecutorService executorService = null;

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if (null != executorService) {
            executorService.shutdownNow();
            logger.info(" [资金处理] 定时任务销毁");
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //启动资金处理定时任务
        List<Market> openMarkets = new ArrayList<>();
        for (Map.Entry<String, Market> entry : Market.markets.entrySet()) {
            Market market = entry.getValue();
            if (market.listenerOpen) {
                openMarkets.add(market);
            }
        }

        executorService = Executors.newScheduledThreadPool(openMarkets.size() * 2);
        if (openMarkets.size() > 0) {
            for (Market market : openMarkets) {
                executorService.scheduleAtFixedRate(new FundsUpdateProcessor(market), 0, 10, TimeUnit.MILLISECONDS);
                executorService.scheduleAtFixedRate(new FundsBackProcessor(market), 0, 10, TimeUnit.MILLISECONDS);
            }
            logger.info(" [资金处理] 定时任务开启");
        }
    }
}
