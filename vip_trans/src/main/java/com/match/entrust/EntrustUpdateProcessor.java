package com.match.entrust;

import com.alibaba.fastjson.JSONObject;
import com.kafka.ProducerSend;
import com.match.domain.*;
import com.match.money.FundsBackProcessor;
import com.match.money.FundsUpdateProcessor;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.model.Market;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>@Description: 数据库更新队列任务，采用队列的形式异步更新数据库</p>
 *
 * @author buxianguan
 * @date 2018/6/19下午8:29
 */
public class EntrustUpdateProcessor {
    public static Logger logger = LoggerFactory.getLogger(EntrustUpdateProcessor.class);

    private static final int POOL_SIZE = 50;
    private static ExecutorService entrustUpdatePool = Executors.newFixedThreadPool(POOL_SIZE);

    /**
     * 未保存的成交记录条数
     * @return
     */
    public static int entrustUpdateSize() {
        return ((ThreadPoolExecutor)entrustUpdatePool).getQueue().size();
    }

    public static void addQueue(Market market, List<EntrustUpdateInfo> entrustUpdateInfoList, TransRecordInfo transRecordInfo, TransFundsBack transFundsBack) {
        if (MemEntrustDataProcessor.reloadResult) {
            EntrustMatchResult dbUpdateInfo = new EntrustMatchResult(market, entrustUpdateInfoList, transRecordInfo, transFundsBack);
            asyncUpdateEntrustDb(dbUpdateInfo);
        }
    }

    public static void resetQueue() {
        logger.info(" [撮合交易] 重置委托更新队列开始！");
        entrustUpdatePool.shutdownNow();
        try {
            //等待3秒钟，让数据更新完
            Thread.sleep(3000);
        } catch (Exception e) {
        }
        entrustUpdatePool = Executors.newFixedThreadPool(POOL_SIZE);
        logger.info(" [撮合交易] 重置委托更新队列结束！");
    }

    private static void asyncUpdateEntrustDb(EntrustMatchResult entrustMatchResult) {
        entrustUpdatePool.execute(() -> {
            updateEntrustDb(entrustMatchResult);
        });
    }

    private static void updateEntrustDb(EntrustMatchResult entrustMatchResult) {
        List<OneSql> sqls = new ArrayList<>();
        List<Long> entrustIds = new ArrayList<>();
        Set<Integer> userIds = new HashSet<>();
        Market market = entrustMatchResult.getMarket();
        try {
            long start = System.currentTimeMillis();

            List<EntrustUpdateInfo> entrustUpdateInfoList = entrustMatchResult.getEntrustUpdateInfoList();
            //组装委托记录更新语句
            for (EntrustUpdateInfo entrustUpdateInfo : entrustUpdateInfoList) {
                entrustIds.add(entrustUpdateInfo.getEntrustId());
                userIds.add(entrustUpdateInfo.getUserId());
                MatchResultEnum matchResult = entrustUpdateInfo.getMatchResult();
                switch (matchResult) {
                    case CANCEL:
                        cancel(entrustUpdateInfo, sqls, market);
                        break;
                    case NO_TRANS:
                        noTrans(entrustUpdateInfo, sqls, market);
                        break;
                    case CAN_TRANS:
                        canTrans(entrustUpdateInfo, sqls, market);
                        break;
                    case UPDATE:
                        updateStatus(entrustUpdateInfo, sqls, market);
                        break;
                    default:
                        break;
                }
            }

            //组装成交记录更新语句
            TransRecordInfo transRecord = entrustMatchResult.getTransRecordInfo();
            if (null != transRecord) {
                saveTransRecord(transRecord, sqls, market);
            }

            //组装资金回退更新语句
            TransFundsBack transFundsBack = entrustMatchResult.getTransFundsBack();
            if (null != transFundsBack) {
                saveTransFundsBack(transFundsBack, sqls, market);
            }

            //执行事务
            if (Data.doTrans(sqls)) {
                logger.info(market.market + " [撮合交易] 委托更新耗时：" + (System.currentTimeMillis() - start) + " 毫秒，委托记录id:{}", entrustIds);

                if (null != transRecord) {
                    //资金任务开始
                    FundsUpdateProcessor.setNewWork(market.market, true);
                    logger.info("推送驾驶舱成交数据埋点开始");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("useridbuy",transRecord.getUserIdBuy());
                    jsonObject.put("entrustidbuy",transRecord.getEntrustIdBuy());
                    jsonObject.put("useridsell",transRecord.getUserIdSell());
                    jsonObject.put("entrustidsell",transRecord.getEntrustIdSell());
                    jsonObject.put("tradeid",transRecord.getTransRecordId());
                    jsonObject.put("tradesum",transRecord.getNumbers());
                    jsonObject.put("fundstype",market.getNumberBiFundsType());
                    jsonObject.put("entrustmarket",market.getMarket());
                    jsonObject.put("unitprice",transRecord.getUnitPrice());
                    ProducerSend producerSend = new ProducerSend();
                    producerSend.sendMessage("trade",jsonObject.toString());
                    logger.info("推送驾驶舱成交数据埋点成功："+jsonObject);
                }

                if (null != transFundsBack) {
                    //回退用户资金任务开始
                    FundsBackProcessor.setNewWork(market.market, true);
                }

                //更新个人委托列表缓存
                for (int userId : userIds) {
                    //委托列表缓存清除，用户页面触发缓存，减少cache消耗
                    MemEntrustMatchProcessor.da.clearUserRecord(market, userId);

//                    //刷量账号直接清空缓存，普通用户更新缓存
//                    if (brushAccountService.isBrushAccountCache(String.valueOf(userId))) {
//                        MemEntrustMatchProcessor.da.clearUserRecord(market, userId);
//                    } else {
//                        MemEntrustMatchProcessor.da.getTop(userId, market);
//                    }
                }
            } else {
                //不修复数据，等待数据重新reload，否则会存在并发问题
                logger.error(market.market + " [撮合交易] 委托更新执行事物失败，委托记录id:{}", entrustIds);

                //通知撮合引擎reload内存值，加上同步锁，保证同时只执行一次
                MemEntrustDataProcessor.needReload = true;
            }
        } catch (Exception e) {
            logger.error(market.market + " [撮合交易] 委托更新异常！", e);
        }
    }

    private static void noTrans(EntrustUpdateInfo entrustUpdateInfo, List<OneSql> sqls, Market market) {
        OneSql oneSql = new OneSql("update entrust set status=3 where entrustId=? and status=0",
                -2, new Object[]{entrustUpdateInfo.getEntrustId()}, market.db);
        sqls.add(oneSql);
    }

    private static void canTrans(EntrustUpdateInfo entrustUpdateInfo, List<OneSql> sqls, Market market) {
        //乐观锁状态去掉，防止异步取消更新比成交快，导致成交的记录更新出错
        OneSql oneSql = new OneSql(
                "update entrust set completeNumber=completeNumber+?,completeTotalMoney=completeTotalMoney+? where entrustId=? and completeNumber+?<=numbers",
                1, new Object[]{entrustUpdateInfo.getCompleteNumber(), entrustUpdateInfo.getCompleteTotalMoney(), entrustUpdateInfo.getEntrustId(), entrustUpdateInfo.getCompleteNumber()}, market.db);
        OneSql twoSql = new OneSql(
                "update entrust set status = (case when completeNumber=numbers then 2 else 3 end) where entrustId=? and status in (0,3)",
                -2, new Object[]{entrustUpdateInfo.getEntrustId()}, market.db);
        sqls.add(oneSql);
        sqls.add(twoSql);

        //异步队列完全成交发送websocket mq消息
        if (entrustUpdateInfo.getStatus() == 2) {
            CompleteDealInfo completeDealInfo = new CompleteDealInfo(entrustUpdateInfo.getEntrustId(), entrustUpdateInfo.getUserId(), market);
            CompleteDealNotifyProcessor.add(completeDealInfo);
        }
    }

    private static void cancel(EntrustUpdateInfo entrustUpdateInfo, List<OneSql> sqls, Market market) {
        //取消委托，判断被取消的委托是否有成交，如果有部分成交，取消后更新状态为2 ：已完成 ，如果没有成交，取消更新状态为1:已取消
        sqls.add(new OneSql(
                "update entrust set status = (case when completeNumber>0 then 2 else 1 end) where entrustId=? and status!=2",
                -2, new Object[]{entrustUpdateInfo.getEntrustId()}, market.db));
    }

    private static void updateStatus(EntrustUpdateInfo entrustUpdateInfo, List<OneSql> sqls, Market market) {
        sqls.add(new OneSql(
                "update entrust set status=? where entrustId=?", 1, new Object[]{entrustUpdateInfo.getStatus(), entrustUpdateInfo.getEntrustId()}, market.db));
    }

    private static void saveTransRecord(TransRecordInfo transRecord, List<OneSql> sqls, Market market) {
        sqls.add(new OneSql(
                "INSERT INTO transrecord (unitPrice, totalPrice, numbers, entrustIdBuy, userIdBuy, entrustIdSell, userIdSell, types, times, timeMinute,webIdBuy,webIdSell,status,actStatus) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,0,1)", 1,
                new Object[]{transRecord.getUnitPrice(), transRecord.getTotalPrice(), transRecord.getNumbers(), transRecord.getEntrustIdBuy(),
                        transRecord.getUserIdBuy(), transRecord.getEntrustIdSell(), transRecord.getUserIdSell(), transRecord.getTypes(), transRecord.getTimes(),
                        transRecord.getTimeMinute(), transRecord.getWebIdBuy(), transRecord.getWebIdSell()}, market.db));
    }

    private static void saveTransFundsBack(TransFundsBack transFundsBack, List<OneSql> sqls, Market market) {
        sqls.add(new OneSql(
                "INSERT IGNORE INTO trans_funds_back (money, entrustId, userId, fundsType, times) VALUES (?,?,?,?,?)",
                -2, new Object[]{transFundsBack.getMoney(), transFundsBack.getEntrustId(), transFundsBack.getUserId(), transFundsBack.getFundsType(), transFundsBack.getTimes()}, market.db));
    }
}