package com.match.money;

import com.match.domain.TransRecordInfo;
import com.world.data.mysql.Data;
import com.world.model.Market;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/11/17 4:00 PM
 */
public class FundsUpdateProcessor extends TimerTask {
    private final static Logger logger = LoggerFactory.getLogger(FundsUpdateProcessor.class);

    private static long times = System.currentTimeMillis();

    private static Map<String, Boolean> newWorkMap = new HashMap<>();

    private static Executor executor = Executors.newFixedThreadPool(50);

    private static FundsUpdateService fundsUpdateService = new FundsUpdateService();

    private Market market;

    public FundsUpdateProcessor(Market market) {
        this.market = market;
    }

    @Override
    public void run() {
        //如果没有任务，间隔3秒钟，防止过多访问数据库
        if ((System.currentTimeMillis() - times) > 3000 || FundsUpdateProcessor.getNewWork(market.market)) {
            FundsUpdateProcessor.setNewWork(market.market, false);

            boolean hasMore = false;
            do {
                hasMore = process();
            } while (hasMore);

            times = System.currentTimeMillis();
        }
    }

    private boolean process() {
        long t = System.currentTimeMillis();

//        int index = 0;
//        String transRowIndex = GlobalConfig.getValue("trans_row_index");
//        if (null != transRowIndex) {
//            index = Integer.parseInt(transRowIndex);
//        }
//        String sql = "select * from transrecord where status=0 and dealTimes<5 and transrecordId%2=" + index + " order by transRecordId";
//        List<TransRecordInfo> list = Data.QueryT(market.db, sql, new Object[]{}, TransRecordInfo.class);

        String sql = "select * from transrecord where status=0 and dealTimes<10 order by transRecordId limit 0, 500";
        List<TransRecordInfo> list = Data.QueryT(market.db, sql, new Object[]{}, TransRecordInfo.class);

//        logger.info(market.market + " [资金处理] 处理用户资金，读取数据耗时：" + (System.currentTimeMillis() - t) + " 毫秒。");

        if (CollectionUtils.isEmpty(list)) {
            return false;
        }

        long start = System.currentTimeMillis();

        CountDownLatch countDownLatch = new CountDownLatch(list.size());
        for (TransRecordInfo transRecordInfo : list) {
            executor.execute(() -> {
                fundsUpdateService.fundsUpdate(transRecordInfo, market, countDownLatch);
            });
        }

        try {
            countDownLatch.await(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.error(market.market + " [资金处理] 处理用户资金异步countdown异常！", e);
        }
        logger.info(market.market + " [资金处理] 处理用户资金，数据量：" + list.size() + ", 耗时：" + (System.currentTimeMillis() - start) + " 毫秒。");

        FundsUpdateProcessor.setNewWork(market.market, true);
        return true;
    }

    private static boolean getNewWork(String market) {
        return newWorkMap.computeIfAbsent(market, k -> true);
    }

    public static void setNewWork(String market, boolean status) {
        newWorkMap.put(market, status);
    }
}
