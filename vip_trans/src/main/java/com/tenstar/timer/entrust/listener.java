//package com.tenstar.timer.entrust;
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
//        log.info("交易定时器销毁");
//    }
//
//    @Override
//    public void contextInitialized(ServletContextEvent event) {
//        timer = new Timer(true);
//        log.info("交易系统定时器已启动");
//        Iterator<Entry<String, Market>> iter = Market.markets.entrySet().iterator();
//        while (iter.hasNext()) {
//            Market m = iter.next().getValue();
//            if (m.listenerOpen) {
//                //撮合引擎定时任务
//                timer.schedule(new Exchange(m), 0, 10);
//                //1秒跑一次计划委托是否触发 计划委托暂时不用
//                timer.schedule(new ScanPlanEntrustTask(m), 0, 1000);
//            }
//        }
//
//
//    }
//}
