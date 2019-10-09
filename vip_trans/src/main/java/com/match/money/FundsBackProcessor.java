package com.match.money;

import com.match.domain.TransFundsBack;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.model.Market;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <p>@Description: 资金回退定时任务</p>
 *
 * @author buxianguan
 * @date 2018/11/17 4:00 PM
 */
public class FundsBackProcessor extends TimerTask {
    private final static Logger logger = LoggerFactory.getLogger(FundsBackProcessor.class);

    private static long times = System.currentTimeMillis();

    private static Map<String, Boolean> newWorkMap = new HashMap<>();

    private static Executor executor = Executors.newFixedThreadPool(30);

    private Market market;

    public FundsBackProcessor(Market market) {
        this.market = market;
    }

    @Override
    public void run() {
        //如果没有任务，间隔3秒钟，防止过多访问数据库
        if ((System.currentTimeMillis() - times) > 3000 || FundsBackProcessor.getNewWork(market.market)) {
            FundsBackProcessor.setNewWork(market.market, false);

            boolean hasMore = false;
            do {
                hasMore = process();
            } while (hasMore);

            times = System.currentTimeMillis();
        }
    }

    private boolean process() {
//        long start = System.currentTimeMillis();
        List<TransFundsBack> list = Data.QueryT(market.db, "select id, money, entrustId, userId, fundsType from trans_funds_back where status=0 and dealTimes<5 order by id limit 0,100",
                new Object[]{}, TransFundsBack.class);
//        logger.info(market.market + " [资金处理] 处理用户回退资金耗时：" + (System.currentTimeMillis() - start) + " 毫秒。");

        if (CollectionUtils.isEmpty(list)) {
            return false;
        }

        CountDownLatch countDownLatch = new CountDownLatch(list.size());
        for (TransFundsBack transFundsBack : list) {
            executor.execute(() -> {
                fundsBackUpdate(transFundsBack, market, countDownLatch);
            });
        }

        try {
            countDownLatch.await(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.error(market.market + " [资金处理] 处理用户回退资金异步countdown异常！", e);
        }

        FundsBackProcessor.setNewWork(market.market, true);

        return true;
    }

    private void fundsBackUpdate(TransFundsBack transFundsBack, Market market, CountDownLatch countDownLatch) {
        try {
            long t = System.currentTimeMillis();

            List<OneSql> sqls = new ArrayList<>();
            //回退用户资金
            BigDecimal amount = transFundsBack.getMoney();
            sqls.add(new OneSql("update pay_user set balance=balance+?, freez=freez-? where userId=? and fundsType=? and freez>=?", 1,
                    new Object[]{amount, amount, transFundsBack.getUserId(), transFundsBack.getFundsType(), amount}, "vip_main"));

            //更新记录处理状态
            sqls.add(new OneSql("update trans_funds_back set status=1 where id=?", 1, new Object[]{transFundsBack.getId()}, market.db));

            if (Data.doTrans(sqls)) {
                //异步队列刷新用户资金
                UserFundsUpdateProcessor.add(transFundsBack.getUserId());
            } else {
                Data.Update(market.db, "update trans_funds_back set dealTimes=dealTimes+1 where id=? ", new Object[]{transFundsBack.getId()});
                logger.error(market.market + " [资金处理] 用户资金回退异常！id=" + transFundsBack.getId());
            }
            logger.info(market.market + " [资金处理] 处理用户回退资金结束，id：" + transFundsBack.getId() + ", 耗时：" + (System.currentTimeMillis() - t) + " 毫秒。");
        } catch (Exception e) {
            logger.error(market.market + " [资金处理] 用户资金回退异常！id=" + transFundsBack.getId(), e);
        } finally {
            countDownLatch.countDown();
        }
    }

    private static boolean getNewWork(String market) {
        return newWorkMap.computeIfAbsent(market, k -> true);
    }

    public static void setNewWork(String market, boolean status) {
        newWorkMap.put(market, status);
    }
}