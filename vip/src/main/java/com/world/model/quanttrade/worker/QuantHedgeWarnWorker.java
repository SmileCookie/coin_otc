package com.world.model.quanttrade.worker;

import com.world.cache.Cache;
import com.world.model.dao.task.Worker;
import com.world.model.quanttrade.service.QuantTradeWarnService;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>量化对冲数量预警定时任务</p>
 *
 * @author zhangwt
 * @date 2018/9/28 15:50
 */
public class QuantHedgeWarnWorker extends Worker {
    private final static Logger log = Logger.getLogger(QuantHedgeWarnWorker.class);

    private QuantTradeWarnService quantTradeWarnService = new QuantTradeWarnService();
    private static String CACHE_HEDGE_LASTTIME = "qt.hedge.warn.time";

    public QuantHedgeWarnWorker(String name, String des) {
        super(name, des);
    }

    @Override
    public void run() {
        log.info("[对冲对账] 对账异常预警定时任务开始");
        long lastTime;
        long endTime;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
            Date now = new Date();
            //从缓存中获取上一次任务结束的时间
            String lastTimeCache = Cache.Get(CACHE_HEDGE_LASTTIME);
            //获取半点的整时间
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = formatter.format(now);
            String min = dateString.substring(14, 16);

            if (StringUtils.isEmpty(lastTimeCache)) {
                //13:15 取12:00-13:00
                endTime = simpleDateFormat.parse(simpleDateFormat.format(now)).getTime();
                lastTime =  endTime-DateUtils.MILLIS_PER_MINUTE*30;
                //如果当前时间的分钟数是超过30的
                if (Integer.valueOf(min)>30){
                    //取当前时间的小时数 13:45 取13:00-13:30
                    lastTime = simpleDateFormat.parse(simpleDateFormat.format(now)).getTime();
                    endTime =  lastTime+DateUtils.MILLIS_PER_MINUTE*30;
                }
            } else {
                lastTime = Long.parseLong(lastTimeCache);
                endTime = simpleDateFormat.parse(simpleDateFormat.format(now)).getTime();
                if (Integer.valueOf(min)>30){
                    endTime = endTime+DateUtils.MILLIS_PER_MINUTE*30;
                }
            }
            //获取结束long时间
            log.info("time:" + lastTime + ", endTime:" + endTime);
            quantTradeWarnService.checkHedgeCount(lastTime, endTime);
            //把任务结束时间放入缓存中
            Cache.Set(CACHE_HEDGE_LASTTIME, String.valueOf(endTime));
        } catch (ParseException e) {
            log.error("[对冲对账] 对账异常预警定时任务异常", e);
        }

        log.info("[对冲对账] 对账异常预警定时任务结束");
    }

    public static void main(String[] args) {
        Cache.Set(CACHE_HEDGE_LASTTIME, String.valueOf("1538013600000"));
//        Cache.Set(CACHE_HEDGE_LASTTIME, String.valueOf(""));
        QuantHedgeWarnWorker worker = new QuantHedgeWarnWorker("aaa", "aaa");
        worker.run();
//        while (true){
//            worker.run();
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

    }
}
