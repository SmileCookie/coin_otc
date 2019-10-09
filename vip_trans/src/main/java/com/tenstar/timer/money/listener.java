//package com.tenstar.timer.money;
//
//import com.world.model.Market;
//import org.apache.log4j.Logger;
//
//import javax.servlet.ServletContextEvent;
//import javax.servlet.ServletContextListener;
//import java.util.Iterator;
//import java.util.Map.Entry;
//import java.util.Timer;
//
//public class listener implements ServletContextListener {
//    private final static Logger log = Logger.getLogger(listener.class);
//    private Timer timer = null;
//
//    @Override
//    public void contextDestroyed(ServletContextEvent event) {
//        timer.cancel();
//        log.info("资金处理系统定时器销毁");
//    }
//
//    @Override
//    public void contextInitialized(ServletContextEvent event) {
//        timer = new Timer(true);
//        log.info("资金处理系统定时器已启动");
//        Iterator<Entry<String, Market>> iter = Market.markets.entrySet().iterator();
//        while (iter.hasNext()) {
//            Market m = iter.next().getValue();
//            if (m.listenerOpen) {
//                //打开监听器定时器
//                log.info(m.market + "--->资金处理启动");
//                timer.schedule(new Money(m), 0, 10);
//                timer.schedule(new UserMoneyChangeTimer(), 0, 50);
//            }
//        }
//    }
//}
