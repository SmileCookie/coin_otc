package com.world.timer;

import javax.servlet.ServletContextEvent;

import com.world.model.balaccount.job.finaccwalbill.thread.WalletCheckThread;
import com.world.model.dao.auto.AutoFactory;
import com.world.model.dao.task.TaskListener;
import com.world.model.loan.worker.LoanAutoFactory;
import com.world.model.service.RechargeParamBuild;
import com.world.model.service.SpecialAddressBuild;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

public class listener extends TaskListener {
    //销毁er{
    //时间间隔(一天)
    private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;

    public void contextDestroyed(ServletContextEvent event) {
        log.info("主动关闭miner服务器》》》》》》》》》");
        super.contextDestroyed(event);
    }

    //初始化监听器
    public void contextInitialized(ServletContextEvent event) {

        AutoFactory.start();
        LoanAutoFactory.start();
        log.info("定时器容器已启动");
        log.info("已经添加任务");
        log.info("启动定时器");
        //启动时将充值的参数传递给充值服务 renfei
        RechargeParamBuild.INSTANCE.threadStart();
        SpecialAddressBuild.INSTANCE.threadStart();
    }









}
