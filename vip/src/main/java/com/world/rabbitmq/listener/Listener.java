package com.world.rabbitmq.listener;

import com.world.rabbitmq.consumer.InitPayUserWalletConsumer;
import com.world.rabbitmq.consumer.OperateLogInfoConsumer;
import com.world.rabbitmq.consumer.UserLoginLogConsumer;
import org.apache.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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
        logger.info("消费者线程销毁");
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        executorService = Executors.newScheduledThreadPool(2);
        //启动消费者监听--登录日志
        executorService.execute(new UserLoginLogConsumer());
        //启动消费者监听--操作日志
        executorService.execute(new OperateLogInfoConsumer());
        //启动消费者监听--初始化用户钱包数据
        executorService.execute(new InitPayUserWalletConsumer());
    }
}
