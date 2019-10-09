package com.world.model.backcapital.worker;

import com.world.model.backcapital.dao.EntrustRecordDao;
import com.world.model.backcapital.service.BackCapitalService;
import com.world.model.dao.task.Worker;
import com.world.model.entity.trace.Entrust;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * 回购记录更新定时任务
 * Created by buxianguan on 17/8/4.
 */
public class BackCapitalUpdateWorker extends Worker {
    private final static Logger logger = Logger.getLogger(BackCapitalUpdateWorker.class);

    private EntrustRecordDao entrustRecordDao = new EntrustRecordDao();

    private BackCapitalService backCapitalService = new BackCapitalService();

    private volatile boolean running = false;

    public BackCapitalUpdateWorker() {
    }

    public BackCapitalUpdateWorker(String name, String des) {
        super(name, des);
    }

    @Override
    public void run() {
        try {
            super.run();

            if (running) {
                logger.info("[回购记录更新] 上一个定时任务还没有执行完毕,等待下一个轮询");
                return;
            }
            running = true;

            List<Entrust> entrusts = backCapitalService.getEntrustsFromCache();
            for (Entrust entrust : entrusts) {
                entrustRecordDao.updateEntrustByEntrustId(entrust.getEntrustId(), entrust.getCompleteTotalMoney(), entrust.getCompleteNumber(), entrust.getSubmitTime());
            }
        } catch (Exception e) {
            logger.error("[回购记录更新] 执行异常！", e);
        } finally {
            running = false;
        }
    }

    public static void main(String[] args) {
        BackCapitalUpdateWorker backCapitalWorker = new BackCapitalUpdateWorker("", "");
        backCapitalWorker.run();
    }
}