//package com.world.model.daos.world;
//
//import com.alibaba.fastjson.JSONObject;
//import com.api.common.BillType;
//import com.tenstar.timer.TransRecordBean;
//import com.world.cache.Cache;
//import com.world.data.mysql.Data;
//import com.world.data.mysql.OneSql;
//import com.world.data.mysql.transaction.TransactionObject;
//import com.world.model.Market;
//import org.apache.log4j.Logger;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class WorldManager2 {
//    private final static Logger log = Logger.getLogger(WorldManager2.class);
//
//    private static FundsUserDao payUserDao = new FundsUserDao();
//
//    private static FeeDao feeDao = new FeeDao();
//
//    private static ExecutorService moneyUpdateThreadPool = Executors.newCachedThreadPool();
//
//    //private static String exchangeBi = Market.exchangeBiEn;
//
//    //private static String numberBi = Market.numberBiEn;
//
//    /***
//     * 查询pay_user 资产状况 fundsType  资金类型
//     * 可用资金：balance,冻结资金：freez
//     * @return
//     */
//    public static List<Object> excuteQueryPayUser(TransactionObject txObj, int userId, int fundsType) {
//        return txObj.excuteQuery(payUserDao.excuteQueryPayUserSql(userId, fundsType));
//    }
//
//    /**
//     * 委托下单查询用户资产
//     */
//    public static List<Object> excuteQueryPayUserEntrust(TransactionObject txObj, int userId, int fundsType) {
//        return txObj.excuteQuery(payUserDao.excuteQueryPayUserEntrust(userId, fundsType));
//    }
//
//
//    /**
//     * 委托买入
//     *
//     * @return
//     * @throws Exception
//     */
//    public static boolean[] buy(int userId, BigDecimal amount, List<OneSql> sqls, Market m) {
//        ///0cny  1btc 2ltc 3btq 4eth 5dao 6etc   需要更新缓存的资产
//        boolean[] changes = new boolean[]{false, false, false, false, false, false, false};
//        try {
//            if (m.exchangeBiFundsType != 0) {
//                payUserDao.freezCny(userId, amount, m.exchangeBiFundsType, sqls);
//            } else {
//                return null;
//            }
//        } catch (Exception e) {
//            log.error(e.toString(), e);
//            return null;
//        }
//        return changes;
//    }
//
//    /***
//     * 委托卖出
//     * @param userId
//     * @param amount
//     * @param sqls
//     * @return
//     */
//    public static boolean[] sell(int userId, BigDecimal amount, List<OneSql> sqls, Market m) {
//        ///0cny  1btc 2ltc 3btq 4eth 5dao 6etc
//        boolean[] changes = new boolean[]{false, false, false, false, false, false, false};
//
//        try {
//
//
//            //CurrencyType ct = CurrencyType.getCurrencyType(m.numberBi.toLowerCase());
//            if (m.numberBiFundsType != 0) {
//                payUserDao.freezSzbs(userId, amount, m.numberBiFundsType, sqls);
//            }
//        } catch (Exception e) {
//            log.error(e.toString(), e);
//            return null;
//        }
//        return changes;
//    }
//
//    /**
//     * 取消买入
//     *
//     * @return
//     * @throws Exception
//     */
//    public static boolean[] cancelBuy(int userId, BigDecimal amount, List<OneSql> sqls, Market m) {
//        ///0cny  1btc 2ltc 3btq 4eth 5dao 6etc
//        boolean[] changes = new boolean[]{false, false, false, false, false, false, false};
//        try {
//
//            if (m.exchangeBiFundsType != 0) {
//                payUserDao.unFreezCnyNotDeduct(userId, amount, m.exchangeBiFundsType, sqls);
//            } else {
//                return null;
//            }
//        } catch (Exception e) {
//            log.error(e.toString(), e);
//            return null;
//        }
//
//        return changes;
//    }
//
//    /***
//     * 取消卖出
//     * @param userId
//     * @param amount
//     * @param sqls
//     * @return
//     */
//    public static boolean[] cancelSell(int userId, BigDecimal amount, List<OneSql> sqls, Market m) {
//        ///0cny  1btc 2ltc 3btq 4eth 5dao,6etc
//        boolean[] changes = new boolean[]{false, false, false, false, false, false, false};
//
//        try {
//
//            //CurrencyType ct = CurrencyType.getCurrencyType(m.numberBi.toLowerCase());
//            if (m.numberBiFundsType != 0) {
//                payUserDao.unFreezSzbsNotDeduct(userId, amount, m.numberBiFundsType, sqls);
//            }
//        } catch (Exception e) {
//            log.error(e.toString(), e);
//            return null;
//        }
//        return changes;
//    }
//
//
//    /**
//     * @param buyerId
//     * @param sellerId
//     * @param biamount         交易币种
//     * @param exchangeBiAmount 本金币种
//     * @param price
//     * @param entrustId
//     * @param sqls
//     * @return
//     */
//    public static boolean tradeSuccess(BillType buyType, BillType sellType, TransRecordBean transRecord, int buyerId, int sellerId, BigDecimal biamount, BigDecimal exchangeBiAmount,
//                                       Market m) {
//        try {
//
//            //1.计算手续费用和扣除手续费后资产变动
//            //计算卖家手续费
//            BigDecimal sellFeeRate = getFeeRateByUserId(String.valueOf(sellerId), m, 0);
//            BigDecimal sellerTradeFee = exchangeBiAmount.multiply(sellFeeRate).setScale(9, BigDecimal.ROUND_UP);
//            //计算卖家收益
//            BigDecimal sellerChangeRmb = exchangeBiAmount.subtract(sellerTradeFee);
//
//
//            //计算买家手续费
//            BigDecimal buyFeeRate = getFeeRateByUserId(String.valueOf(buyerId), m, 1);
//            BigDecimal buyerTradeFee = biamount.multiply(buyFeeRate).setScale(9, BigDecimal.ROUND_UP);
//            //计算买家收益
//            BigDecimal buyChangeCoin = biamount.subtract(buyerTradeFee);
//
//
//            long t1 = System.currentTimeMillis();
//
//            ///2.扣除卖家交易币种给买家
//            if (m.numberBiFundsType != 0 && m.exchangeBiFundsType != 0) {
////                CountDownLatch updateDownLatch = new CountDownLatch(4);
//
//                //将相同类型的币种余额更新和bill表插入放在同一个事务中执行，减少死锁冲突
//                moneyUpdateThreadPool.execute(() -> {
//                    List<OneSql> sqls = new ArrayList<>();
//                    try {
//                        TransactionObject txObj = new TransactionObject();
//                        //1、给卖家用户加上卖币所得的金钱
//                        sqls.add(payUserDao.addCny(sellerId, sellerChangeRmb, m.exchangeBiFundsType));
//                        //卖家账单
//                        //1、记录卖币所得兑换币金额账单明细   记录手续费的是20 和31
//                        sqls.add(payUserDao.getInsertBillSqlNew(BillType.exchangeOut, sellerId, sellerChangeRmb, sellerTradeFee, m.exchangeBiFundsType, m.getExchangeBi(), m.getNumberBi(), buyChangeCoin));
//                        txObj.excuteUpdateList(sqls);
//                        txObj.commit();
//                    } catch (Exception e) {
//                        log.error("[资金处理] 数据库操作异常，sql:" + JSONObject.toJSONString(sqls), e);
//                    } finally {
////                        updateDownLatch.countDown();
//                    }
//                });
//
//                moneyUpdateThreadPool.execute(() -> {
//                    List<OneSql> sqls1 = new ArrayList<>();
//                    try {
//                        TransactionObject txObj1 = new TransactionObject();
//                        sqls1 = new ArrayList<>();
//                        //2、给卖家用户解冻并减少已卖出的币
//                        sqls1.add(payUserDao.unFreezSzbsDeduct(sellerId, biamount, m.numberBiFundsType));
//                        //2、记录卖币减少的币账单明细
//                        sqls1.add(payUserDao.getInsertBillSqlNew(sellType, sellerId, biamount, BigDecimal.ZERO, m.numberBiFundsType, m.getExchangeBi(), m.getNumberBi(), exchangeBiAmount));  //type=21
//                        txObj1.excuteUpdateList(sqls1);
//                        txObj1.commit();
//                    } catch (Exception e) {
//                        log.error("[资金处理] 数据库操作异常，sql:" + JSONObject.toJSONString(sqls1), e);
//                    } finally {
////                        updateDownLatch.countDown();
//                    }
//                });
//
//
//                moneyUpdateThreadPool.execute(() -> {
//                    List<OneSql> sqls2 = new ArrayList<>();
//                    try {
//                        TransactionObject txObj2 = new TransactionObject();
//                        sqls2 = new ArrayList<>();
//                        //买家资金变动
//                        //1、给买家用户减掉冻结的购买资金
//                        sqls2.add(payUserDao.unFreezCnyDeduct(buyerId, exchangeBiAmount, m.exchangeBiFundsType));
//                        //1、记录买币花费的资金变动账单明细
//                        sqls2.add(payUserDao.getInsertBillSqlNew(BillType.exchangeIn, buyerId, exchangeBiAmount, BigDecimal.ZERO, m.exchangeBiFundsType, m.getExchangeBi(), m.getNumberBi(), biamount));
//                        txObj2.excuteUpdateList(sqls2);
//                        txObj2.commit();
//                    } catch (Exception e) {
//                        log.error("[资金处理] 数据库操作异常，sql:" + JSONObject.toJSONString(sqls2), e);
//                    } finally {
////                        updateDownLatch.countDown();
//                    }
//                });
//
//                moneyUpdateThreadPool.execute(() -> {
//                    List<OneSql> sqls3 = new ArrayList<>();
//                    try {
//                        TransactionObject txObj3 = new TransactionObject();
//                        sqls3 = new ArrayList<>();
//                        //2、给买家用户加上买到的币
//                        sqls3.add(payUserDao.addSzbs(buyerId, buyChangeCoin, m.numberBiFundsType));
//                        //记录手续费
//                        sqls3.add(payUserDao.getInsertBillSqlNew(buyType, buyerId, buyChangeCoin, buyerTradeFee, m.numberBiFundsType, m.getExchangeBi(), m.getNumberBi(), sellerChangeRmb)); //type=20
//                        txObj3.excuteUpdateList(sqls3);
//                        txObj3.commit();
//                    } catch (Exception e) {
//                        log.error("[资金处理] 数据库操作异常，sql:" + JSONObject.toJSONString(sqls3), e);
//                    } finally {
////                        updateDownLatch.countDown();
//                    }
//                });
//
////                try {
////                    updateDownLatch.await(1, TimeUnit.SECONDS);
////                } catch (Exception e) {
////                    log.error("[资金处理] 数据库操作异常获取线程唤醒异常! ", e);
////                }
//
//                //卖家资金变动
//                //1、给卖家用户加上卖币所得的金钱
////                txObj.excuteUpdate(payUserDao.addCny(sellerId, sellerChangeRmb, m.exchangeBiFundsType));
//                //2、给卖家用户解冻并减少已卖出的币
////                txObj.excuteUpdate(payUserDao.unFreezSzbsDeduct(sellerId, biamount, m.numberBiFundsType));
//                //卖家账单
//                //1、记录卖币所得兑换币金额账单明细   记录手续费的是20 和31
////                sqls.add(payUserDao.getInsertBillSql(BillType.exchangeOut, sellerId, sellerChangeRmb, sellerTradeFee, m.exchangeBiFundsType, entrustId, txObj, m.getExchangeBi(), m.getNumberBi(), buyChangeCoin));
//                //2、记录卖币减少的币账单明细
////                sqls.add(payUserDao.getInsertBillSql(sellType, sellerId, biamount, BigDecimal.ZERO, m.numberBiFundsType, entrustId, txObj, m.getExchangeBi(), m.getNumberBi(), exchangeBiAmount));  //type=21
//
//                //买家资金变动
//                //1、给买家用户减掉冻结的购买资金
////                txObj.excuteUpdate(payUserDao.unFreezCnyDeduct(buyerId, exchangeBiAmount, m.exchangeBiFundsType));
//                //2、给买家用户加上买到的币
////                txObj.excuteUpdate(payUserDao.addSzbs(buyerId, buyChangeCoin, m.numberBiFundsType));
//                //买家账单
//                //1、记录买币花费的资金变动账单明细
////                sqls.add(payUserDao.getInsertBillSql(BillType.exchangeIn, buyerId, exchangeBiAmount, BigDecimal.ZERO, m.exchangeBiFundsType, entrustId, txObj, m.getExchangeBi(), m.getNumberBi(), biamount));
//                //记录手续费
////                sqls.add(payUserDao.getInsertBillSql(buyType, buyerId, buyChangeCoin, buyerTradeFee, m.numberBiFundsType, entrustId, txObj, m.getExchangeBi(), m.getNumberBi(), sellerChangeRmb)); //type=20
//
//            }
//
//            //3.处理手续费信息到数据库
//            StringBuilder feeSqls = new StringBuilder("INSERT INTO Fee(userId,type,currency,amount,time,transRecordId,transRecordTime," +
//                    "market,numberBi,exchangeBi,totalPrice,numbers,unitPrice,flag) values(");
//            boolean isUpdateFee = true;
//            if (sellerTradeFee.compareTo(BigDecimal.ZERO) > 0 && buyerTradeFee.compareTo(BigDecimal.ZERO) > 0) {
//                feeSqls.append(sellerId).append(",").append(1).append(",'").append(m.exchangeBi.equalsIgnoreCase("RMB") ? "CNY" : m.exchangeBi).append("',").append(sellerTradeFee)
//                        .append(",").append(System.currentTimeMillis()).append(",").append(transRecord.getTransRecordId()).append(",").append(transRecord.getTimes()).append(",'").append(m.db)
//                        .append("','").append(m.numberBi).append("','").append(m.exchangeBi).append("',").append(transRecord.getTotalPrice()).append(",").append(transRecord.getNumbers())
//                        .append(",").append(transRecord.getUnitPrice()).append(",").append(0).append(")");
//                feeSqls.append(",(").append(buyerId).append(",").append(1).append(",'").append(m.numberBi).append("',").append(buyerTradeFee)
//                        .append(",").append(System.currentTimeMillis()).append(",").append(transRecord.getTransRecordId()).append(",").append(transRecord.getTimes()).append(",'").append(m.db)
//                        .append("','").append(m.numberBi).append("','").append(m.exchangeBi).append("',").append(transRecord.getTotalPrice()).append(",").append(transRecord.getNumbers())
//                        .append(",").append(transRecord.getUnitPrice()).append(",").append(0).append(");");
//            }else if(sellerTradeFee.compareTo(BigDecimal.ZERO) > 0){
//                //卖家手续费
//                feeSqls.append(sellerId).append(",").append(1).append(",'").append(m.exchangeBi.equalsIgnoreCase("RMB") ? "CNY" : m.exchangeBi).append("',").append(sellerTradeFee)
//                        .append(",").append(System.currentTimeMillis()).append(",").append(transRecord.getTransRecordId()).append(",").append(transRecord.getTimes()).append(",'").append(m.db)
//                        .append("','").append(m.numberBi).append("','").append(m.exchangeBi).append("',").append(transRecord.getTotalPrice()).append(",").append(transRecord.getNumbers())
//                        .append(",").append(transRecord.getUnitPrice()).append(",").append(0).append(");");
//            }else if(buyerTradeFee.compareTo(BigDecimal.ZERO) > 0){
//                //买家手续费
//                feeSqls.append(buyerId).append(",").append(1).append(",'").append(m.numberBi).append("',").append(buyerTradeFee)
//                        .append(",").append(System.currentTimeMillis()).append(",").append(transRecord.getTransRecordId()).append(",").append(transRecord.getTimes()).append(",'").append(m.db)
//                        .append("','").append(m.numberBi).append("','").append(m.exchangeBi).append("',").append(transRecord.getTotalPrice()).append(",").append(transRecord.getNumbers())
//                        .append(",").append(transRecord.getUnitPrice()).append(",").append(0).append(");");
//            }else{
//                isUpdateFee = false;
//            }
//
//            log.info(m.market + " [资金处理] feeSql:" + feeSqls.toString());
//
//            if(isUpdateFee){
//                moneyUpdateThreadPool.execute(() -> {
//                    Data.insertNoId("vip_main", feeSqls.toString(), null);
//                });
//            }
//
//            //transrecord表记录手续费
//            moneyUpdateThreadPool.execute(() -> {
//                Data.Update(m.db,"update transrecord set status=2, feesBuy=?, feesSell=? where transRecordId=? and status<>2", new Object[]{buyerTradeFee, sellerTradeFee, transRecord.getTransRecordId()});
//            });
//
//
//            log.info(m.market + " [资金处理] 数据库操作耗时：" + (System.currentTimeMillis() - t1) + " 毫秒。");
//        } catch (Exception e) {
//            log.error(e.toString(), e);
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * 获取计算用户的手续费费率 标志手续费*折扣率，区分买卖双方手续费
//     *
//     * @param userId 用户ID
//     * @param m      市场配置对象
//     * @return 计算后的费率
//     */
//    private static BigDecimal getFeeRateByUserId(String userId, Market m, int type) {
//        BigDecimal feeRate = BigDecimal.ZERO;
//        if (type == 1) {
//            feeRate = BigDecimal.valueOf(m.getBuyFeeRate());
//        } else {
//            feeRate = BigDecimal.valueOf(m.getSellFeeRate());
//        }
//        BigDecimal feeDiscount = (BigDecimal) Cache.GetObj("user_vip_fee_discount_" + userId);
//        if (feeDiscount != null) {
//            feeRate = feeRate.multiply(feeDiscount);
//        }
//        return feeRate;
//    }
//
//}
