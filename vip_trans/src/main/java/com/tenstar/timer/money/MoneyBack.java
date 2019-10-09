//package com.tenstar.timer.money;
//
//import com.api.common.BillType;
//import com.tenstar.Info;
//import com.tenstar.SystemStatus;
//import com.tenstar.TimeUtil;
//import com.tenstar.timer.TransRecordBean;
//import com.tenstar.timer.entrust.Exchange;
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
//import java.sql.Timestamp;
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
//public class MoneyBack extends TimerTask {
//    public static Logger log = Logger.getLogger(MoneyBack.class);
//    public static long times = System.currentTimeMillis();
//    private Market m;
//    private static int limit = 50;//默认处理50条数据
//
//    public MoneyBack(Market m) {
//        this.m = m;
//    }
//
//    public MoneyBack() {
//    }
//
//    @Override
//    public void run() {
//
//        if (SystemStatus.getSystemStatus(m.market + "_" + SystemStatus.moneyRuning)) {
//            try {
//                //log.info(times);
//                if ((System.currentTimeMillis() - times) > 3000 || SystemStatus.getSystemStatus(m.market + "_" + SystemStatus.moneyNewWork)) {//在固定周期内或者主动状态被触发就会执行
//                    //SystemStatus.moneyNewWork=false;
//                    SystemStatus.setSystemStatus(m.market + "_" + SystemStatus.moneyNewWork, false);
//                    //	log.info("例行循环");
//                    //做一个循环，不断获取需要处理的数据
//                    int dueWork = 0;
//                    long s1 = System.currentTimeMillis();
//
//                    while (Info.NoMission != process50(m)) {
//
//                        long s2 = System.currentTimeMillis();
//
//                        log.info("[资金处理] 处理一次资产, 耗时：" + (s2 - s1) + " 毫秒！");
//
//                        s1 = System.currentTimeMillis();
//                        dueWork++;
//                    }
//                    if (dueWork > 0)
//                        log.info("[资金处理] 处理了" + dueWork + "个资金任务");
//                    times = System.currentTimeMillis();//增加一个单位跳出本循环
//
//                } else
//                    Thread.sleep(10);
//            } catch (Exception ex) {
//                log.error(ex.toString(), ex);
//            }
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
//            //所有新任务才会引起变化    or (status=1 and dealTimes<2)   解决死锁造成的资金处理失败，操作两次，降低死锁概率
//            log.info("[资金处理] 准备处理资金，查询数量：" + limit + "");
//            long t1 = System.currentTimeMillis();
//            List<TransRecordBean> bebs = Data.QueryT(m.db, "select * from transrecord where status=0 or (status=1 and dealTimes<5)  order by transRecordId limit 0," + limit + " ",
//                    new Object[]{}, TransRecordBean.class);
//            long t2 = System.currentTimeMillis();
//            if (bebs != null && bebs.size() > 0) {
//                log.info("[资金处理] 查询成交记录数：" + bebs.size() + "条, 耗时：" + (t2 - t1));
//            }
//
//            if (bebs == null || bebs.size() <= 0) {
//                return Info.NoMission;
//            }
//            setLimit(bebs.size());
//            for (TransRecordBean beb : bebs) {
//                Timestamp start = TimeUtil.getNow();
//                log.info("[资金处理] 准备处理资金，编号(transRecordId)：" + beb.getTransRecordId());
//
//                EntrustBean eb = (EntrustBean) Data.GetOne(m.db, "select * from entrust where entrustId=?", new Object[]{beb.getTypes() == 1 ? beb.getEntrustIdBuy() : beb.getEntrustIdSell()}, EntrustBean.class);
//
//                if (eb == null) {//已经被迁移
//                    eb = (EntrustBean) Data.GetOne(m.db, "select * from entrust_all where entrustId=?", new Object[]{beb.getTypes() == 1 ? beb.getEntrustIdBuy() : beb.getEntrustIdSell()}, EntrustBean.class);
//                }
//                if (eb == null) {
//                    //log.info(beb.getEntrustIdBuy()+"资金命令有问题");
//                    Data.Update(m.db, "update TransRecord set status=1,dealTimes=dealTimes+1 where TransRecordId=? ", new Object[]{beb.getTransRecordId()});
////                    Timestamp end1 = TimeUtil.getNow();
////                    log.info("耗时：" + (start.getTime() - end1.getTime()) + ",ID:" + beb.getTransRecordId());
//                    //return Info.DueError;
//                }
//                if (beb.getUnitPrice().compareTo(BigDecimal.ZERO) == 0) {//取消
//                    Info i = entrustCancel(eb, beb, m);
//                    if (i != null) {
//                        SystemStatus.setSystemStatus(m.market + "_" + SystemStatus.moneyNewWork, true);
//                    }
////                    Timestamp end1 = TimeUtil.getNow();
////                    log.info("耗时：" + (start.getTime() - end1.getTime()) + ",ID:" + beb.getTransRecordId());
//                    //return i;
//                } else if (beb.getTypes() == 0 || beb.getTypes() == 1) {
//                    Info i = entrustMoney(eb, beb, beb.getTypes() == 1 ? beb.getEntrustIdSell() : beb.getEntrustIdBuy(), m);
//                    if (i != null) {
//                        //SystemStatus.moneyNewWork=true;
//                        SystemStatus.setSystemStatus(m.market + "_" + SystemStatus.moneyNewWork, true);
//                    }
////                    Timestamp end1 = TimeUtil.getNow();
////                    log.info("耗时：" + (start.getTime() - end1.getTime()) + ",ID:" + beb.getTransRecordId());
//                    //return i;
//                } else {
//                    Data.Update(m.db, "update TransRecord set status=1,dealTimes=dealTimes+1 where TransRecordId=? ", new Object[]{beb.getTransRecordId()});
//                    log.info("[资金处理] 有一个异常的type=" + beb.getTypes() + ", transRecordId=" + beb.getTransRecordId() + " 的资金命令！");
////                    Timestamp end1 = TimeUtil.getNow();
////                    log.info("耗时：" + (start.getTime() - end1.getTime()) + ",ID:" + beb.getTransRecordId());
//                    //return Info.DueError;
//                }
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
//        String method = "trade" + m.numberBiEn + "Success";
//        if (!"cny".equalsIgnoreCase(m.exchangeBiEn) && !"rmb".equalsIgnoreCase(m.exchangeBiEn) && !"btq".equalsIgnoreCase(m.numberBiEn)) {
//            method = "trade" + m.numberBiEn + m.exchangeBiEn + "Success";
//        }
//        long t1 = System.currentTimeMillis();
//        String buyerId = Integer.toString(beb.getUserIdBuy());
//        String sellerId = Integer.toString(beb.getUserIdSell());
//        BigDecimal cnyAmount = beb.getTotalPrice();//保存总金额
//        BigDecimal btcAmount = beb.getNumbers();//保存数量
//
//        long entrustId = beb.getTransRecordId();//因为有两个id，所以只能给出成交记录的id
//        List<OneSql> sqls = new ArrayList<OneSql>();
//
//        //只有双方的资金可能剩余，币一定是完全成交或者被取消
//        //购买已完成数量但是资金有剩余
//        if ((eb.getNumbers().compareTo(eb.getCompleteNumber()) == 0) && eb.getTypes() == 1 && eb.getCompleteTotalMoney().compareTo(eb.getTotalMoney()) < 0) {
//            //己方资金剩余
//            Object obj = Data.GetOne(m.db, "select * from transrecord where unitPrice=0 and numbers=0 and entrustIdBuy=?", new Object[]{eb.getEntrustId()});
//            if (obj == null) {//不存在的情况下才会这样
//                //数量已经完全成交了，但是还有剩余资金，产生一个新的资金命令，避免无记录的锁定问题
//                sqls.add(new OneSql(
//                        "INSERT INTO transrecord (unitPrice, totalPrice, numbers, entrustIdBuy, userIdBuy, entrustIdSell, userIdSell, types, times, timeMinute,webIdBuy,webIdSell) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
//                        1, new Object[]{
//                        0,
//                        eb.getTotalMoney().subtract(eb.getCompleteTotalMoney()),//剩余的所有钱
//                        0,//剩余的所有都取消
//                        eb.getEntrustId(),//卖卖记录一样的
//                        eb.getUserId(),
//                        0,//买卖记录一样的
//                        0,
//                        eb.getTypes(),
//                        TimeUtil.getNow().getTime(),
//                        TimeUtil.getMinuteFirst().getTime(),
//                        eb.getWebId(),
//                        0
//                }, m.db));
//            }
//
//
//            //本次仅仅只是添加，需要到下一条循环才会执行
//        }
//
//        //交易对方
//        EntrustBean ebOther = (EntrustBean) Data.GetOne(m.db, "select * from Entrust where entrustId=?", new Object[]{otherId}, EntrustBean.class);
//
//
//        if (ebOther == null) {
//            log.info("交易对方委托entrust表记录为空，对方委托id:" + otherId + ",本方委托id:" + eb.getEntrustId() + ",继续查询entrust_all表");
//            ebOther = (EntrustBean) Data.GetOne(m.db, "select * from Entrust_all where entrustId=?", new Object[]{otherId}, EntrustBean.class);
//        }
//        if (ebOther != null && ebOther.getTypes() == 1 && (ebOther.getNumbers().compareTo(ebOther.getCompleteNumber()) == 0) && ebOther.getCompleteTotalMoney().compareTo(ebOther.getTotalMoney()) < 0) {
//            //己方资金剩余
//            Object obj = Data.GetOne(m.db, "select * from transrecord where unitPrice=0 and numbers=0 and entrustIdBuy=?", new Object[]{ebOther.getEntrustId()});
//            if (obj == null) {//不存在的情况下才会这样
//                //数量已经完全成交了，但是还有剩余资金，产生一个新的资金命令，避免无记录的锁定问题
//                sqls.add(new OneSql(
//                        "INSERT INTO transrecord (unitPrice, totalPrice, numbers, entrustIdBuy, userIdBuy, entrustIdSell, userIdSell, types, times, timeMinute,webIdBuy,webIdSell) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
//                        1, new Object[]{
//                        0,
//                        ebOther.getTotalMoney().subtract(ebOther.getCompleteTotalMoney()),//剩余的所有钱
//                        0,//剩余的所有都取消
//                        ebOther.getEntrustId(),//卖卖记录一样的
//                        ebOther.getUserId(),
//                        0,//买卖记录一样的
//                        0,
//                        ebOther.getTypes(),
//                        TimeUtil.getNow().getTime(),
//                        TimeUtil.getMinuteFirst().getTime(),
//                        ebOther.getWebId(),
//                        0
//                }, m.db));
//            }
//            //本次仅仅只是添加，需要到下一条循环才会执行
//        }
//
//        sqls.add(new OneSql(//更新状态
//                "update TransRecord set status=2 where TransRecordId=? and status<>2",
//                1,
//                new Object[]{
//                        beb.getTransRecordId()
//                }, m.db));
//
//        log.info(method + ":" + cnyAmount + ":" + btcAmount + ":" + buyerId + ":" + sellerId);
//
//        BillType buyType = null;
//        BillType sellType = null;
//        BigDecimal biamount = btcAmount, exchangeBiAmount = cnyAmount;
//
//
//        TransactionObject txObj = new TransactionObject();
//
//        //String tradeMarket = m.numberBiEn.toLowerCase() + "_" + m.exchangeBiEn.toLowerCase();
//
//        buyType = BillType.buy;
//        sellType = BillType.sell;
//
//
//        if (!WorldManager.tradeSuccess(buyType, sellType, beb, beb.getUserIdBuy(), beb.getUserIdSell(), biamount, exchangeBiAmount, beb.getUnitPrice(), entrustId, sqls, txObj, m)) {
//            txObj.rollback("tradeSuccess is error");
//        }
//
//        txObj.excuteUpdateList(sqls);
//        if (txObj.commit()) {
//            long t2 = System.currentTimeMillis();
//            log.info("[资金处理] 处理一条成交记录资金耗时：" + (t2 - t1));
//            //if (Data.doTransWithHttp(sqls, TradeManager.class, method,objs)) {
//            //处理完成，缓存用户成交数据
//            AsynMethodFactory.addWork(MoneyBack.class, "setTraderecordToMem", new Object[]{beb.getUserIdBuy(), beb.getUserIdSell(), beb, m});
//            UserMoneyChangeTimer.add(new Object[]{buyType, beb.getUserIdBuy()});
//            UserMoneyChangeTimer.add(new Object[]{sellType, beb.getUserIdSell()});
//            //AsynMethodFactory.addWork(FundsUserDao.class, "updateFunds", new Object[]{buyType, beb.getUserIdBuy()});
//            //AsynMethodFactory.addWork(FundsUserDao.class, "updateFunds", new Object[]{sellType, beb.getUserIdSell()});
//            return Info.DoEntrustSuccess;
//        } else {
//            Data.Update(m.db, "update TransRecord set status=1,dealTimes=dealTimes+1 where TransRecordId=? ", new Object[]{beb.getTransRecordId()});
//            log.info("[资金处理] 有个资金处理发生错误, transRecordId:" + beb.getTransRecordId());
//            return Info.DoEntrustFaildPromError;
//        }
//    }
//
//    public void setTraderecordToMem(int userId1, int userId2, TransRecordBean beb, Market m) {
//
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
//     *
//     * @param cammandId 0 代表不需要管理他   大于0说明有个具体的命令也要处理更新掉
//     * @param beb
//     * @return
//     */
//
//
//    /**
//     * 取消，或者交易完成后进行的余额解冻功能
//     *
//     * @param eb  不能为空 委托本身的对象
//     * @param trb 命令的对象，可以为空
//     * @return 取消详情
//     */
//    public static Info entrustCancel(EntrustBean eb, TransRecordBean trb, Market m) {
//
//        String method = "";
//        BigDecimal amount = BigDecimal.ZERO;
//        if (eb.getTypes() == 1) {
//            method = "cancelBuy" + m.numberBiEn;
//            if (!"cny".equalsIgnoreCase(m.exchangeBiEn) && !"rmb".equalsIgnoreCase(m.exchangeBiEn) && !"btq".equalsIgnoreCase(m.numberBiEn)) {
//                method += m.exchangeBiEn;
//            }
//            amount = eb.getTotalMoney().subtract(eb.getCompleteTotalMoney());
//        } else {
//            method = "cancelSell" + m.numberBiEn;
//            amount = eb.getNumbers().subtract(eb.getCompleteNumber());
//        }
//
//        log.info(amount + ":" + method);
//        if (amount.compareTo(BigDecimal.ZERO) == 0) {
//
//            Data.Update(m.db, "update TransRecord set status=1,dealTimes=dealTimes+1 where TransRecordId=? and status<>2",
//
//                    new Object[]{
//                            trb.getTransRecordId()
//                    });
//            log.info(trb.getTransRecordId() + "没有任何余额，任然需要执行取消");
//
//            return Info.NoMission;
//        }
//
//        if (eb.getTypes() == 1)//买
//        {
//            //反向核算一次本轮委托还剩下多少
////				List buy=(List)Data.GetOne("SELECT SUM(totalPrice) FROM transrecord WHERE entrustIdBuy=?",
////						new Object[]{eb.getEntrustId()});
////				if(buy!=null&&buy.get(0)!=null){
////					long p=Long.parseLong(buy.get(0).toString());
////					if(p!=eb.getTotalMoney()){
////						  Data.Update("update TransRecord set status=3 where TransRecordId=? and status<>2",
////									new Object[] {
////											trb.getTransRecordId()
////				                        });
////						  log.info(trb.getTransRecordId()+"出现了超出界限的取消买命令，所有资金都已经处理完毕了");
////						return Info.DoCancleFailPriceError;
////					}
////				}//==null说明没有成交不用管
//
//        } else {
//
//            //反向核算一次本轮委托还剩下多少
////				List sell=(List)Data.GetOne("SELECT SUM(numbers) FROM transrecord WHERE entrustIdSell=?",
////						new Object[]{eb.getEntrustId()});
////				if(sell!=null&&sell.get(0)!=null){
////					long n=Long.parseLong(sell.get(0).toString());
////					if(n!=eb.getNumbers()){
////						  Data.Update("update TransRecord set status=3 where TransRecordId=? and status<>2",
////									new Object[] {
////											trb.getTransRecordId()
////				                        });
////						  log.error(trb.getTransRecordId()+"出现了超出界限的取消卖命令，所有币都已经处理完毕了");
////						return Info.DoCancleFailPriceError;
////					}
////				}//==null说明没有成交不用管
//
//        }
//
//
//        String userId = Integer.toString(eb.getUserId());
//
//        List<OneSql> sqls = new ArrayList<OneSql>();
//        if (trb != null) {
//            sqls.add(new OneSql(//更新状态
//                    "update TransRecord set status=2 where TransRecordId=? and status<>2",
//                    1,
//                    new Object[]{
//                            trb.getTransRecordId()
//                    }, m.db));
//        }
//        //下面的参数记录暂时并没有明确的用途，用户反向查询，有些参数可能没有调试对准
//              /* if(eb.getTypes()==1){
//			       sqls.add(new OneSql(//更新状态
//						"INSERT INTO moneyrecord(webId, userId, moneys,number, entrustId, submitTime) VALUES (?,?,?,?,?,?);",
//						1,
//						new Object[] {
//								eb.getWebId(),eb.getUserId(),eb.getTotalMoney()-eb.getCompleteTotalMoney(),0,eb.getEntrustId(),System.currentTimeMillis()
//	                        }));
//
//			   }else{
//			       sqls.add(new OneSql(//更新状态
//						"INSERT INTO moneyrecord(webId, userId, moneys,number, entrustId, submitTime) VALUES (?,?,?,?,?,?);",
//						1,
//						new Object[] {
//								eb.getWebId(),eb.getUserId(),0,eb.getNumbers()-eb.getCompleteNumber(),eb.getEntrustId(),System.currentTimeMillis()
//			                        }));
//			   }*/
//        log.info("getNumbers:" + eb.getNumbers() + " CompleteNumber:" + eb.getCompleteNumber() + " TotalMoney:" + eb.getTotalMoney() + " CompleteTotalMoney:" + eb.getCompleteTotalMoney());
//        //更新委托余额  这里不需要，会导致自动取消的部分显示错误
////			   sqls.add(new OneSql(//更新状态
////						"update Entrust set CompleteTotalMoney=TotalMoney,CompleteNumber=Numbers  where entrustId=?",
////						1,
////						new Object[] {
////								eb.getEntrustId()
////			                        }));
//
//
//        long start = TimeUtil.getNow().getTime();
//        boolean[] changes = null;
//        if (eb.getTypes() == 1) {
//            changes = WorldManager.cancelBuy(eb.getUserId(), amount, sqls, m);
//        } else {
//            changes = WorldManager.cancelSell(eb.getUserId(), amount, sqls, m);
//        }
//
//        if (Data.doTrans(sqls)) {
//            //if (Data.doTransWithHttp(sqls, TradeManager.class, method, new Object[]{userId,amount})) {
//            long end = TimeUtil.getNow().getTime();
//            AsynMethodFactory.addWork(FundsUserDao.class, "updateFundsByChange", new Object[]{eb.getUserId()});
//            log.info("资金事物耗时：" + (end - start));
//            return Info.DoCancleSuccess;
//        } else {
//
//            Data.Update(m.db, "update TransRecord set status=1,dealTimes=dealTimes+1 where TransRecordId=? and status<>2",
//
//                    new Object[]{
//                            trb.getTransRecordId()
//                    });
//            log.error("transRecordId=" + trb.getTransRecordId() + " 解冻资金发生错误");
//
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
