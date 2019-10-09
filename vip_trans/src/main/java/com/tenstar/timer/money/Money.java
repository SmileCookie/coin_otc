//package com.tenstar.timer.money;
//
//import com.api.common.BillType;
//import com.match.entrust.MemEntrustMatchProcessor;
//import com.tenstar.Info;
//import com.tenstar.SystemStatus;
//import com.tenstar.TimeUtil;
//import com.tenstar.timer.TransRecordBean;
//import com.world.data.mysql.Data;
//import com.world.data.mysql.OneSql;
//import com.world.data.mysql.transaction.TransactionObject;
//import com.world.model.Market;
//import com.world.model.daos.chart.ChartManager;
//import com.world.model.daos.world.FundsUserDao;
//import com.world.model.daos.world.WorldManager;
//import com.world.model.entitys.entrust.EntrustBean;
//import com.world.model.entitys.record.TransRecord;
//import com.world.util.callback.AsynMethodFactory;
//import org.apache.log4j.Logger;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.TimerTask;
//
//
///**
// * 定时器跟主项目之间发生的timer，所有的委托交易完成的时候，或者取消或者有剩余的时候，会在这里进行与资金系统交互扣除或者归还
// * 这样保证在多个大盘对接的情况下可以保证即使其中一个资金系统出现问题，整个交易大盘不至于停止
// * 实际应用中最好每个项目启动一个MoneyTimer，保证各个项目之间不关联
// *
// * @author netpet
// */
//public class Money extends TimerTask {
//    public static Logger log = Logger.getLogger(Money.class);
//    public static long times = System.currentTimeMillis();
//    private Market m;
//    private static int limit = 50;//默认处理50条数据
//
//    public Money(Market m) {
//        this.m = m;
//    }
//
//    public Money() {
//    }
//
//    @Override
//    public void run() {
//        try {
//            //在固定周期内或者主动状态被触发就会执行
//            if ((System.currentTimeMillis() - times) > 3000 || SystemStatus.getSystemStatus(m.market + "_" + SystemStatus.moneyNewWork)) {
//                SystemStatus.setSystemStatus(m.market + "_" + SystemStatus.moneyNewWork, false);
//                //做一个循环，不断获取需要处理的数据
//                int dueWork = 0;
//                long s0 = System.currentTimeMillis();
//                Info result = Info.NoMission;
//                do {
//                    long s1 = System.currentTimeMillis();
//                    result = process50(m);
//                    log.info(m.market + " [资金处理] 处理一次资产, 耗时：" + (System.currentTimeMillis() - s1) + " 毫秒。");
//                    if (result != Info.NoMission) {
//                        dueWork++;
//                    }
//                } while (result != Info.NoMission);
//
//                log.info(m.market + " [资金处理] 处理了" + dueWork + "个资金任务, 耗时：" + (System.currentTimeMillis() - s0) + " 毫秒。");
//                //增加一个单位跳出本循环
//                times = System.currentTimeMillis();
//            } else {
//                Thread.sleep(10);
//            }
//        } catch (Exception ex) {
//            log.error(m.market + " [资金处理] 处理一次资产异常！", ex);
//        }
//    }
//
//    /**
//     * 处理一个任务
//     *
//     * @return 是否处理成功
//     */
//    public static Info process50(Market m) {
//        try {
//            //所有新任务才会引起变化    or (status=1 and dealTimes<5)   解决死锁造成的资金处理失败，操作5次，降低死锁概率
//            log.info(m.market + " [资金处理] 准备处理资金，查询数量：" + limit + "");
//            long t0 = System.currentTimeMillis();
//            List<TransRecordBean> bebs = Data.QueryT(m.db, "select * from transrecord where status=0 or (status=1 and dealTimes<5)  order by transRecordId limit 0," + limit + " ",
//                    new Object[]{}, TransRecordBean.class);
//            if (bebs == null || bebs.size() <= 0) {
//                return Info.NoMission;
//            }
//
//            log.info(m.market + " [资金处理] 查询成交记录数：" + bebs.size() + "条, 耗时：" + (System.currentTimeMillis() - t0) + " 毫秒。");
//
//            setLimit(bebs.size());
//            for (TransRecordBean beb : bebs) {
//                long t1 = System.currentTimeMillis();
//                log.info(m.market + " [资金处理] 准备处理资金，编号(transRecordId)：" + beb.getTransRecordId());
//
//                EntrustBean eb = (EntrustBean) Data.GetOne(m.db, "select * from entrust where entrustId=?", new Object[]{beb.getTypes() == 1 ? beb.getEntrustIdBuy() : beb.getEntrustIdSell()}, EntrustBean.class);
//                //已经被迁移，查询all表
//                if (eb == null) {
//                    eb = (EntrustBean) Data.GetOne(m.db, "select * from entrust_all where entrustId=?", new Object[]{beb.getTypes() == 1 ? beb.getEntrustIdBuy() : beb.getEntrustIdSell()}, EntrustBean.class);
//                }
//                if (eb == null) {
//                    //没有找到，更新交易记录状态失败，记录失败次数
//                    Data.Update(m.db, "update TransRecord set status=1,dealTimes=dealTimes+1 where TransRecordId=? ", new Object[]{beb.getTransRecordId()});
//                    continue;
//                }
//                if (beb.getUnitPrice().compareTo(BigDecimal.ZERO) == 0) {
//                    //取消
//                    entrustCancel(eb, beb, m);
//                } else if (beb.getTypes() == 0 || beb.getTypes() == 1) {
//                    Info i = entrustMoney(eb, beb, beb.getTypes() == 1 ? beb.getEntrustIdSell() : beb.getEntrustIdBuy(), m);
//                    if (i != null) {
//                        SystemStatus.setSystemStatus(m.market + "_" + SystemStatus.moneyNewWork, true);
//                    }
//                } else {
//                    Data.Update(m.db, "update TransRecord set status=2 where TransRecordId=? ", new Object[]{beb.getTransRecordId()});
//                    log.error(m.market + " [资金处理] 有一个异常的type=" + beb.getTypes() + ", transRecordId=" + beb.getTransRecordId() + " 的资金命令！");
//                }
//                log.info(m.market + " [资金处理] 处理资金，编号(transRecordId)：" + beb.getTransRecordId()+ ", 耗时：" + (System.currentTimeMillis() - t1) + " 毫秒。");
//            }
//
//        } catch (Exception ex) {
//            log.error(ex.toString(), ex);
//            return Info.DueEntrustFaildUnKonwType;
//        }
//
//        return Info.DoEntrustSuccess;
//
//    }
//
//    /**
//     * 委托交易类型的
//     *
//     * @param beb     记录条数
//     *                beb
//     * @param otherId 交易对方的id
//     * @return
//     */
//    public static Info entrustMoney(EntrustBean eb, TransRecordBean beb, long otherId, Market m) {
//        List<OneSql> sqls = new ArrayList<>();
//
//        //对于买单，如果数量完全成交，金额有剩余，插入一条价格为0的成交记录，下次循环处理剩余金额，回退到账户中
//        if (eb.getTypes() == 1) {
//            if (eb.getNumbers().compareTo(eb.getCompleteNumber()) == 0 && eb.getCompleteTotalMoney().compareTo(eb.getTotalMoney()) < 0) {
//                Object obj = Data.GetOne(m.db, "select * from transrecord where entrustIdBuy=? and unitPrice=0 and numbers=0", new Object[]{eb.getEntrustId()});
//                if (obj == null) {
//                    sqls.add(new OneSql(
//                            "INSERT INTO transrecord (unitPrice, totalPrice, numbers, entrustIdBuy, userIdBuy, entrustIdSell, userIdSell, types, times, timeMinute,webIdBuy,webIdSell) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
//                            1, new Object[]{
//                            0,
//                            eb.getTotalMoney().subtract(eb.getCompleteTotalMoney()),//剩余的所有钱
//                            0,//剩余的所有都取消
//                            eb.getEntrustId(),//卖卖记录一样的
//                            eb.getUserId(),
//                            0,//买卖记录一样的
//                            0,
//                            eb.getTypes(),
//                            TimeUtil.getNow().getTime(),
//                            TimeUtil.getMinuteFirst().getTime(),
//                            eb.getWebId(),
//                            0
//                    }, m.db));
//                }
//            }
//        } else {
//            //交易对方
//            EntrustBean ebOther = (EntrustBean) Data.GetOne(m.db, "select * from Entrust where entrustId=?", new Object[]{otherId}, EntrustBean.class);
//            if (ebOther == null) {
//                ebOther = (EntrustBean) Data.GetOne(m.db, "select * from Entrust_all where entrustId=?", new Object[]{otherId}, EntrustBean.class);
//            }
//            if (ebOther != null && ebOther.getTypes() == 1 && ebOther.getNumbers().compareTo(ebOther.getCompleteNumber()) == 0 && ebOther.getCompleteTotalMoney().compareTo(ebOther.getTotalMoney()) < 0) {
//                Object obj = Data.GetOne(m.db, "select * from transrecord where entrustIdBuy=? and unitPrice=0 and numbers=0", new Object[]{ebOther.getEntrustId()});
//                if (obj == null) {
//                    sqls.add(new OneSql(
//                            "INSERT INTO transrecord (unitPrice, totalPrice, numbers, entrustIdBuy, userIdBuy, entrustIdSell, userIdSell, types, times, timeMinute,webIdBuy,webIdSell) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
//                            1, new Object[]{
//                            0,
//                            ebOther.getTotalMoney().subtract(ebOther.getCompleteTotalMoney()),//剩余的所有钱
//                            0,//剩余的所有都取消
//                            ebOther.getEntrustId(),//卖卖记录一样的
//                            ebOther.getUserId(),
//                            0,//买卖记录一样的
//                            0,
//                            ebOther.getTypes(),
//                            TimeUtil.getNow().getTime(),
//                            TimeUtil.getMinuteFirst().getTime(),
//                            ebOther.getWebId(),
//                            0
//                    }, m.db));
//                }
//            }
//        }
//
//        //更新状态
//        sqls.add(new OneSql("update TransRecord set status=2 where TransRecordId=? and status<>2", 1, new Object[]{beb.getTransRecordId()}, m.db));
//
//        //数量
//        BigDecimal biAmount = beb.getNumbers();
//        //金额
//        BigDecimal exchangeBiAmount = beb.getTotalPrice();
//
//        //因为有两个id，所以只能给出成交记录的id，add by buxianguan 应该没有用的
//        long entrustId = beb.getTransRecordId();
//
//        BillType buyType = BillType.buy;
//        BillType sellType = BillType.sell;
//
//        long t1 = System.currentTimeMillis();
//        TransactionObject txObj = new TransactionObject();
//        if (!WorldManager.tradeSuccess(buyType, sellType, beb, beb.getUserIdBuy(), beb.getUserIdSell(), biAmount, exchangeBiAmount, beb.getUnitPrice(), entrustId, sqls, txObj, m)) {
//            txObj.rollback("tradeSuccess is error");
//        }
//
//        txObj.excuteUpdateList(sqls);
//        if (txObj.commit()) {
//            log.info(m.market + " [资金处理] 数据库操作耗时：" + (System.currentTimeMillis() - t1) + " 毫秒。");
//
//            //处理完成，缓存用户成交数据
//            AsynMethodFactory.addWork(Money.class, "setTraderecordToMem", new Object[]{beb.getUserIdBuy(), beb.getUserIdSell(), beb, m});
//            //异步队列刷新用户资金
//            UserMoneyChangeTimer.add(new Object[]{beb.getUserIdBuy()});
//            UserMoneyChangeTimer.add(new Object[]{beb.getUserIdSell()});
//
//            return Info.DoEntrustSuccess;
//        } else {
//            Data.Update(m.db, "update TransRecord set status=1,dealTimes=dealTimes+1 where TransRecordId=? ", new Object[]{beb.getTransRecordId()});
//            log.info(m.market + " [资金处理] 有个资金处理发生错误, transRecordId:" + beb.getTransRecordId());
//            return Info.DoEntrustFaildPromError;
//        }
//    }
//
//    public void setTraderecordToMem(int userId1, int userId2, TransRecordBean beb, Market m) {
//        boolean isSame = userId1 == userId2;
//        //动态增加成交记录到缓存。提高性能速度
//        TransRecord tr = new TransRecord();
//        tr.setTransRecordId(beb.getTransRecordId());
//        tr.setUnitPrice(beb.getUnitPrice());
//        tr.setTotalPrice(beb.getTotalPrice());
//        tr.setNumbers(beb.getNumbers());
//        tr.setEntrustIdBuy(beb.getEntrustIdBuy());
//        tr.setUserIdBuy(beb.getUserIdBuy());
//        tr.setEntrustIdSell(beb.getEntrustIdSell());
//        tr.setUserIdSell(beb.getUserIdSell());
//        tr.setTypes(beb.getTypes());
//        tr.setTimes(beb.getTimes());
//        tr.setTimeMinute(beb.getTimeMinute());
//        tr.setStatus(beb.getStatus());
//        tr.setIsCount(beb.getIsCount());
//        tr.setWebIdBuy(beb.getWebIdBuy());
//        tr.setWebIdSell(beb.getWebIdSell());
//
//        ChartManager.addNewTransRecord(tr, m);
//        MemEntrustMatchProcessor.da.setTraderecordToMem(userId1, beb, false, m);
//        MemEntrustMatchProcessor.da.setTraderecordToMem(userId2, beb, isSame, m);
//    }
//
//    /**
//     * 取消，或者交易完成后进行的余额解冻功能
//     *
//     * @param eb  不能为空 委托本身的对象
//     * @param trb 命令的对象，可以为空
//     * @return 取消详情
//     */
//    public static Info entrustCancel(EntrustBean eb, TransRecordBean trb, Market m) {
//        BigDecimal amount = BigDecimal.ZERO;
//        if (eb.getTypes() == 1) {
//            amount = eb.getTotalMoney().subtract(eb.getCompleteTotalMoney());
//        } else {
//            amount = eb.getNumbers().subtract(eb.getCompleteNumber());
//        }
//
//        if (amount.compareTo(BigDecimal.ZERO) == 0) {
//            Data.Update(m.db, "update TransRecord set status=2 where TransRecordId=? and status<>2", new Object[]{trb.getTransRecordId()});
//            return Info.NoMission;
//        }
//
//        List<OneSql> sqls = new ArrayList<>();
//        //更新状态
//        sqls.add(new OneSql("update TransRecord set status=2 where TransRecordId=? and status<>2", 1, new Object[]{trb.getTransRecordId()}, m.db));
//
//        if (eb.getTypes() == 1) {
//            WorldManager.cancelBuy(eb.getUserId(), amount, sqls, m);
//        } else {
//            WorldManager.cancelSell(eb.getUserId(), amount, sqls, m);
//        }
//
//        long t1 = System.currentTimeMillis();
//        if (Data.doTrans(sqls)) {
//            log.info(m.market + " [资金处理] 取消数据库操作耗时：" + (System.currentTimeMillis() - t1) + " 毫秒。");
//            AsynMethodFactory.addWork(FundsUserDao.class, "updateFundsByChange", new Object[]{eb.getUserId()});
//            return Info.DoCancleSuccess;
//        } else {
//            Data.Update(m.db, "update TransRecord set status=1,dealTimes=dealTimes+1 where TransRecordId=? and status<>2", new Object[]{trb.getTransRecordId()});
//            log.error(m.market + " [资金处理] 取消记录解冻资金异常！transRecordId=" + trb.getTransRecordId());
//            SystemStatus.setSystemStatus(m.market + "_" + SystemStatus.moneyNewWork, true);
//            return Info.DoEntrustFaildPromError;
//        }
//    }
//
//    /**
//     * 设置查询记录集合大小，自由动态伸缩大小
//     *
//     * @param size 数据库查询大集合大小
//     * @author zhanglinbo 20170117
//     */
//    public static void setLimit(int size) {
//        if (size == 50) {
//            limit = 100;
//        } else if (size == 100) {
//            limit = 200;
//        } else if (size == 200) {
//            limit = 300;
//        } else if (size == 300) {
//            limit = 400;
//        } else if (size == 400) {
//            limit = 500;
//        } else if (300 < size && size < 400) {
//            limit = 300;
//        } else if (200 < size && size < 300) {
//            limit = 200;
//        } else if (100 < size && size < 200) {
//            limit = 100;
//        } else if (size < 100) {
//            limit = 50;
//        }
//
//    }
//
//}
