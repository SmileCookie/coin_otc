package com.match.entrust;

import com.match.domain.CompleteDealInfo;
import com.match.rabbitmq.websocket.UserEntrustCompleteProducer;
import com.world.data.mysql.Data;
import com.world.model.Market;
import com.world.model.service.BrushAccountService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>@Description: 更新内存最新成交记录队列</p>
 *
 * @author buxianguan
 * @date 2018/6/19下午8:29
 */
public class CompleteDealNotifyProcessor extends TimerTask {
    public static Logger logger = Logger.getLogger(CompleteDealNotifyProcessor.class);

    private static ConcurrentLinkedQueue<CompleteDealInfo> completeDealInfoList = new ConcurrentLinkedQueue<>();
    private ExecutorService notifyPool = Executors.newFixedThreadPool(20);
    private UserEntrustCompleteProducer userEntrustCompleteProducer = new UserEntrustCompleteProducer();
    private BrushAccountService brushAccountService = new BrushAccountService();

    public CompleteDealNotifyProcessor() {
    }

    @Override
    public void run() {
        try {
            CompleteDealInfo completeDealInfo = null;
            do {
                completeDealInfo = completeDealInfoList.poll();
                if (null != completeDealInfo) {
                    sendUserCompleteNotify(completeDealInfo.getMarket(), completeDealInfo.getEntrustId(), completeDealInfo.getUserId());
                }
            } while (null != completeDealInfo);
        } catch (Exception e) {
            logger.error("更新内存成交记录错误！", e);
        }
    }

    public static void add(CompleteDealInfo completeDealInfo) {
        completeDealInfoList.offer(completeDealInfo);
    }

    /**
     * 完全成交后发送websocket通知
     *
     * @param market
     * @param entrustId
     * @param userId
     */
    private void sendUserCompleteNotify(Market market, long entrustId, int userId) {
        notifyPool.execute(() -> {
            try {
                //排除掉刷量账号
                if (!brushAccountService.isBrushAccountCache(String.valueOf(userId))) {
                    //查询一下有多少笔成交记录
                    String sql = "SELECT transRecordId FROM transrecord WHERE ((userIdBuy=? and entrustIdBuy=?) or (userIdSell=? AND entrustIdSell=?)) and unitPrice>0 union all " +
                            "SELECT transRecordId FROM transrecord_all WHERE ((userIdBuy=? and entrustIdBuy=?) or (userIdSell=? AND entrustIdSell=?)) and unitPrice>0";
                    List lists = Data.Query(market.db, sql, new Object[]{userId, entrustId, userId, entrustId, userId, entrustId, userId, entrustId});
                    if (CollectionUtils.isNotEmpty(lists)) {
                        userEntrustCompleteProducer.send(userId, 1, lists.size());
                    }
                }
            } catch (Exception e) {
                logger.error(market.market + " [撮合交易] 撮合完全成交后发送websocket通知异常", e);
            }
        });
    }
}