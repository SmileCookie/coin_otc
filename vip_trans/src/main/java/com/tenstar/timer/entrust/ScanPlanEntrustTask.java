package com.tenstar.timer.entrust;

import com.world.model.Market;
import org.apache.log4j.Logger;

import java.util.TimerTask;

public class ScanPlanEntrustTask extends TimerTask {

    public static Logger log = Logger.getLogger(ScanPlanEntrustTask.class);

    private Market m;

    public ScanPlanEntrustTask(Market m) {
        this.m = m;
    }

    ;


    @Override
    public void run() {

        try {
            //做一个循环，不断获取需要处理的数据
            int dueWork = 0;
            long start = System.currentTimeMillis();

            MemPlanEntrustProcessor.processOne(m);
//            log.info("[扫描计划委托] 处理市场:" +m.market+ "计划委托单, 耗时：" + (System.currentTimeMillis() - start));

            Thread.sleep(100);

        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

    }

}
