package com.world.model.balaccount.job.feeaccount;

import com.world.data.database.DatabasesUtil;
import com.world.model.balaccount.job.feeaccount.thread.FeeAccountDealThread;
import com.world.model.balaccount.job.finaccdetailswork.thread.FinAccDetailsThread;
import com.world.model.dao.task.Worker;
import com.world.model.entity.coin.CoinProps;
import com.world.util.date.TimeUtil;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xie on 2017/7/5.
 */
public class FeeAccountDealWork  extends Worker {

    private static Logger log = Logger.getLogger(FeeAccountDealWork.class);

    public FeeAccountDealWork(String name, String des) {
        super(name, des);
    }

    @Override
    public void run() {
        log.info("手续费计算定时任务开始...");
        Map<String, CoinProps> coinPropMaps = DatabasesUtil.getNewCoinPropMaps();
        /*创建一个可重用固定线程数的线程池*/
        ExecutorService feeAccountDealThreadWorkPool = Executors.newFixedThreadPool(1);
        //循环执行每个币种
        for(Map.Entry<String, CoinProps> entry : coinPropMaps.entrySet()){
            CoinProps coint = entry.getValue();
            log.info("计算"+coint.getDatabaseKey() + "手续费定时任务开始");
            String conitName = coint.getDatabaseKey();//币种名称
            if(coint.getFundsType() > 0){
                FeeAccountDealThread feeAccountDealThread = new FeeAccountDealThread(conitName);
                feeAccountDealThreadWorkPool.execute(feeAccountDealThread);
            }
        }
    }
}
