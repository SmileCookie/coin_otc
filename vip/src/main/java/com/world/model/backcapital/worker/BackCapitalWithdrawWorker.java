package com.world.model.backcapital.worker;

import com.world.model.backcapital.service.BackCapitalService;
import com.world.model.dao.task.Worker;
import com.world.model.entity.backcapital.BackCapitalConfig;
import com.world.model.entity.backcapital.CountDownInfo;
import org.apache.log4j.Logger;

/**
 * 回购资金转出定时任务
 * Created by buxianguan on 17/8/4.
 */
public class BackCapitalWithdrawWorker extends Worker {
    private final static Logger logger = Logger.getLogger(BackCapitalWithdrawWorker.class);
    private final static Logger alarmLogger = Logger.getLogger("alarmAll");

    private BackCapitalService backCapitalService = new BackCapitalService();

    private volatile boolean running = false;

    public BackCapitalWithdrawWorker() {
    }

    public BackCapitalWithdrawWorker(String name, String des) {
        super(name, des);
    }

    @Override
    public void run() {
        try {
            super.run();

            if (running) {
                logger.info("[回购提现] 上一个定时任务还没有执行完毕,等待下一个轮询");
                return;
            }
            running = true;

            //获取回购参数配置
            BackCapitalConfig config = backCapitalService.getConfig();
            if (null == config) {
                logger.error("[回购提现] 从数据库获取回购配置信息为空！");
                return;
            }

            if (config.getWithdrawTaskStatus() != 1) {
                logger.info("[回购提现] 回购提现任务为非启动状态，不提现");
                return;
            }

            //获取平台转出倒计时信息
            CountDownInfo countDownInfo = backCapitalService.getWithdrawCountDown(config);
            if (countDownInfo.getCountDown() > 0) {
                return;
            }

            logger.info("[回购提现] 回购提现开始...");

            //处理回购账户资金
            boolean transferResult = backCapitalService.userFundsTransfer(config);
            if (!transferResult) {
                logger.error("[回购提现] 处理回购账户资金失败！");
            }

            logger.info("[回购提现] 回购提现结束...");
        } catch (Exception e) {
            logger.error("[回购提现] 执行异常！", e);
        } finally {
            running = false;
        }
    }

    public static void main(String[] args) {
        BackCapitalWithdrawWorker backCapitalWithdrawWorker = new BackCapitalWithdrawWorker("", "");
        backCapitalWithdrawWorker.run();
    }
}