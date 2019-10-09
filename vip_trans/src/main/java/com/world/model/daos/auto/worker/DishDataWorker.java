package com.world.model.daos.auto.worker;

import com.tenstar.timer.dish.DishDataManager;
import com.world.model.Market;
import com.world.model.dao.task.Worker;

/**
 * @author apple
 */
public class DishDataWorker extends Worker {

    private static final long serialVersionUID = 1L;

    private Market m;

    public DishDataWorker(String name, String des, Market m) {
        super(name, des);
        this.m = m;
    }

    @Override
    public void run() {
        try {
            super.run();

            long s1 = System.currentTimeMillis();
            DishDataManager.cacheDishData(m);
            long s2 = System.currentTimeMillis();

            log.info("[盘口数据生成] 生成市场:" + m.market + "盘口数据, 耗时：" + (s2 - s1));
        } catch (Exception e) {
            log.error(e.toString(), e);
        }

    }

}
