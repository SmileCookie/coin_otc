package com.match.money;

import com.alibaba.fastjson.JSONObject;
import com.api.common.BillType;
import com.kafka.ProducerSend;
import com.match.domain.TransRecordInfo;
import com.match.rabbitmq.RabbitMqProducer;
import com.world.cache.Cache;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.Market;
import com.world.model.daos.world.FeeDao;
import com.world.model.daos.world.FundsUserDao;
import com.world.util.date.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/11/29 7:55 PM
 */
public class FundsUpdateService {
    private final static Logger logger = LoggerFactory.getLogger(FundsUpdateService.class);

    private FundsUserDao payUserDao = new FundsUserDao();
    private FeeDao feeDao = new FeeDao();

    public void fundsUpdate(TransRecordInfo transRecord, Market market, CountDownLatch countDownLatch) {
        BigDecimal numbers = transRecord.getNumbers();
        BigDecimal totalMoney = transRecord.getTotalPrice();
        int buyerId = transRecord.getUserIdBuy();
        int sellerId = transRecord.getUserIdSell();

        List<OneSql> sqls = new ArrayList<>();
        TransactionObject txObj = new TransactionObject();

        try {
            //1.计算手续费用和扣除手续费后资产变动
            //获取买卖家手续费
            BigDecimal[] feeRates = getFeeRateByUserIds(buyerId, sellerId, transRecord.getTypes(), market);

            //计算卖家手续费
            BigDecimal sellFeeRate = feeRates[1];
            BigDecimal sellerTradeFee = totalMoney.multiply(sellFeeRate).setScale(9, BigDecimal.ROUND_UP);
            //计算卖家收益
            BigDecimal sellerIncome = totalMoney.subtract(sellerTradeFee);

            //计算买家手续费
            BigDecimal buyFeeRate = feeRates[0];
            BigDecimal buyerTradeFee = numbers.multiply(buyFeeRate).setScale(9, BigDecimal.ROUND_UP);
            //计算买家收益
            BigDecimal buyerIncome = numbers.subtract(buyerTradeFee);

            String sendTime = TimeUtil.parseDate(transRecord.getTimes());

            //1、给卖家用户加上卖币所得的金钱
            txObj.excuteUpdate(payUserDao.addCny(sellerId, sellerIncome, market.exchangeBiFundsType));
            //1、记录卖币所得兑换币金额账单明细   记录手续费的是20 和31
            sqls.add(payUserDao.getInsertBillSqlNew(BillType.exchangeOut, sellerId, sellerIncome, sellerTradeFee, market.exchangeBiFundsType, sendTime, market.getExchangeBi(), market.getNumberBi(), buyerIncome, txObj, transRecord.getTransRecordId()));


            //2、给卖家用户解冻并减少已卖出的币
            txObj.excuteUpdate(payUserDao.unFreezSzbsDeduct(sellerId, numbers, market.numberBiFundsType));
            //2、记录卖币减少的币账单明细
            sqls.add(payUserDao.getInsertBillSqlNew(BillType.sell, sellerId, numbers, BigDecimal.ZERO, market.numberBiFundsType, sendTime, market.getExchangeBi(), market.getNumberBi(), totalMoney, txObj, transRecord.getTransRecordId()));

            //2、给卖家用户解冻并减少已卖出的币
            //买家资金变动
            //1、给买家用户减掉冻结的购买资金
            txObj.excuteUpdate(payUserDao.unFreezCnyDeduct(buyerId, totalMoney, market.exchangeBiFundsType));
            //1、记录买币花费的资金变动账单明细
            sqls.add(payUserDao.getInsertBillSqlNew(BillType.exchangeIn, buyerId, totalMoney, BigDecimal.ZERO, market.exchangeBiFundsType, sendTime, market.getExchangeBi(), market.getNumberBi(), numbers, txObj, transRecord.getTransRecordId()));

            //2、给买家用户加上买到的币
            txObj.excuteUpdate(payUserDao.addSzbs(buyerId, buyerIncome, market.numberBiFundsType));
            //记录手续费
            sqls.add(payUserDao.getInsertBillSqlNew(BillType.buy, buyerId, buyerIncome, buyerTradeFee, market.numberBiFundsType, sendTime, market.getExchangeBi(), market.getNumberBi(), sellerIncome, txObj, transRecord.getTransRecordId()));

//            //卖家手续费
//            if (sellerTradeFee.compareTo(BigDecimal.ZERO) > 0) {
//                sqls.add(feeDao.addFee(sellerId, 1, sellerTradeFee, market.exchangeBi.equalsIgnoreCase("RMB") ? "CNY" : market.exchangeBi, transRecord, market, 0));
//            }
//            //买家手续费
//            if (buyerTradeFee.compareTo(BigDecimal.ZERO) > 0) {
//                sqls.add(feeDao.addFee(buyerId, 1, buyerTradeFee, market.numberBi, transRecord, market, 0));
//            }

            //transrecord表记录手续费
            sqls.add(new OneSql("update transrecord set status=2, feesBuy=?, feesSell=? where transRecordId=?", 1,
                    new Object[]{buyerTradeFee, sellerTradeFee, transRecord.getTransRecordId()}, market.db));

//            sqls.add(new OneSql("update transrecord set status=2, feesBuy=" + buyerTradeFee + ", feesSell=" + sellerTradeFee +
//                    " where transRecordId=" + transRecord.getTransRecordId(), 1, new Object[]{}, market.db));

            long t1 = System.currentTimeMillis();
            txObj.excuteUpdateList(sqls);
            if (txObj.commit()) {
                logger.info(market.market + " [资金处理] 数据库操作耗时：" + (System.currentTimeMillis() - t1) + " 毫秒。");


                //异步队列刷新用户资金
                if (transRecord.getUserIdBuy() == transRecord.getUserIdSell()) {
                    UserFundsUpdateProcessor.add(transRecord.getUserIdBuy());
                } else {
                    UserFundsUpdateProcessor.add(transRecord.getUserIdBuy());
                    UserFundsUpdateProcessor.add(transRecord.getUserIdSell());
                }

                if (transRecord.getUserIdBuy() != transRecord.getUserIdSell()) {
                    JSONObject mqMessage = new JSONObject();
                    mqMessage.put("tid", transRecord.getTransRecordId());
                    mqMessage.put("price", transRecord.getUnitPrice().stripTrailingZeros().toPlainString());
                    mqMessage.put("num", transRecord.getNumbers().stripTrailingZeros().toPlainString());
                    mqMessage.put("market", market.market);
                    mqMessage.put("bid", transRecord.getEntrustIdBuy());
                    mqMessage.put("sid", transRecord.getEntrustIdSell());
                    mqMessage.put("buid", transRecord.getUserIdBuy());
                    mqMessage.put("suid", transRecord.getUserIdSell());
                    mqMessage.put("bf", buyerTradeFee.stripTrailingZeros().toPlainString());
                    mqMessage.put("sf", sellerTradeFee.stripTrailingZeros().toPlainString());
                    mqMessage.put("types", transRecord.getTypes());
                    mqMessage.put("time", transRecord.getTimes());

                    RabbitMqProducer.sendText("spot.hedge", "spot.hedge", mqMessage.toJSONString(), 3);
                }

                logger.info("推送驾驶舱埋点交易费数据开始");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("sellfee",sellerTradeFee);
                jsonObject.put("buyfee",buyerTradeFee);
                jsonObject.put("market",market.getMarket());
                jsonObject.put("sendTime",sendTime);
                jsonObject.put("fundsType",market.getNumberBiFundsType());
                ProducerSend producerSend = new ProducerSend();
                producerSend.sendMessage("tradingFee",jsonObject.toString());
                logger.info("推送驾驶舱交易费数据埋点成功："+jsonObject);

            } else {
                Data.Update(market.db, "update transrecord set dealTimes=dealTimes+1 where transRecordId=? ", new Object[]{transRecord.getTransRecordId()});
                logger.error(market.market + " [资金处理] 有个资金处理发生错误, transRecordId:" + transRecord.getTransRecordId());
            }
        } catch (Exception e) {
            logger.error(market.market + " [资金处理] 资金处理异常！transRecordId:" + transRecord.getTransRecordId(), e);
            Data.Update(market.db, "update transrecord set dealTimes=dealTimes+1 where transRecordId=? ", new Object[]{transRecord.getTransRecordId()});
        } finally {
            countDownLatch.countDown();
        }
    }

    /**
     * 获取用户手续费
     * @param buyUserId 买方用户ID
     * @param sellUserId 卖方用户ID
     * @param transType 成交买卖方向【1：买， 0：卖 】
     * @param market 市场
     * @return 用户手续费数组【0：买方手续费；1：卖方手续费】
     */
    private BigDecimal[] getFeeRateByUserIds(int buyUserId, int sellUserId, long transType, Market market) {


        BigDecimal buyFeeRate;
        BigDecimal sellFeeRate;
        if (transType == 1) {
            // 主动买
            buyFeeRate = BigDecimal.valueOf(market.getTakerFeeRate());
            sellFeeRate = BigDecimal.valueOf(market.getMakerFeeRate());
        } else {
            // 主动卖
            sellFeeRate = BigDecimal.valueOf(market.getTakerFeeRate());
            buyFeeRate = BigDecimal.valueOf(market.getMakerFeeRate());
        }
        try {
            //计算买方手续费
            BigDecimal buyFeeDiscount = BigDecimal.ONE;
            Object objBuyFeeDiscount = Cache.GetObj("user_vip_fee_discount_" + buyUserId);
            if (null != objBuyFeeDiscount) {
                buyFeeDiscount = (BigDecimal) objBuyFeeDiscount;
            }
            buyFeeRate = buyFeeRate.multiply(buyFeeDiscount);

            //计算卖方手续费
            if (buyUserId == sellUserId) {
                sellFeeRate = buyFeeRate;
            } else {
                BigDecimal sellFeeDiscount = BigDecimal.ONE;
                Object objSellFeeDiscount = Cache.GetObj("user_vip_fee_discount_" + sellUserId);
                if (null != objSellFeeDiscount) {
                    sellFeeDiscount = (BigDecimal) objSellFeeDiscount;
                }
                sellFeeRate = sellFeeRate.multiply(sellFeeDiscount);
            }
        } catch (Exception e) {
            logger.error(market.market + " [资金处理] 获取用户手续费异常！", e);
        }

        return new BigDecimal[]{buyFeeRate, sellFeeRate};
    }
}
