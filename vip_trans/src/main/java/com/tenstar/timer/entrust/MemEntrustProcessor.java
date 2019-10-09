//package com.tenstar.timer.entrust;
//
//import com.alibaba.fastjson.JSON;
//import com.match.entrust.MemEntrustMatchProcessor;
//import com.tenstar.Info;
//import com.tenstar.SystemStatus;
//import com.tenstar.TimeUtil;
//import com.world.cache.Cache;
//import com.world.data.mysql.Data;
//import com.world.data.mysql.OneSql;
//import com.world.model.Market;
//import com.world.model.daos.chart.ChartManager;
//import com.world.model.entity.LegalTenderType;
//import com.world.model.entitys.entrust.EntrustBean;
//import com.world.model.entitys.summary.TransactionSummary;
//import com.world.util.string.StringUtil;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.log4j.Logger;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.NavigableMap;
//import java.util.TreeMap;
//
///***
// * 内存交易撮合引擎
// * @author apple
// *
// */
//public class MemEntrustProcessor {
//    public static Logger log = Logger.getLogger(MemEntrustProcessor.class);
//
//    private final static String sellSql = "select * from entrust where types=0 and completeNumber<numbers and status=3  order by unitPrice asc,entrustId asc";
//    private final static String buySql = "select * from entrust where  types=1 and completeNumber<numbers and status=3  order by unitPrice desc,entrustId asc";
//
//    private final static String entrustSql = "select * from entrust where entrustId=?";
//
//    private final static String noMatchEntrustSql = "select * from entrust where status=0 order by entrustId asc";
//
//    //最后一次从数据库中加载未撮合委托时间，根据市场区分，防止一台机器开多个撮合引擎
//    private static Map<String, Long> lastLoadNoMatchEntrustTimeMap = new HashMap<>();
//    //从数据库中加载未撮合委托频率
//    private final static long loadNoMatchEntrustFrequency = 20 * 60 * 1000;
//
//    //定义全局存放各币种的委托Map<币种,treeMap>
//    private static Map<String, TreeMap<BigDecimal, TreeMap<Long, EntrustRecord>>> marketsBuyEntrustMap = new HashMap<>();
//
//    private static Map<String, TreeMap<BigDecimal, TreeMap<Long, EntrustRecord>>> marketsSellEntrustMap = new HashMap<>();
//
//    private static Map<String, TreeMap<Long, EntrustRecord>> marketsNoMatchingMap = new HashMap<>();
//
//    private static Map<String, Boolean> initMap = new HashMap<>();
//
//    //缓存盘口200条委托
//    public static Map<String, String[][]> marketsBuyEntrustArr200 = new HashMap<>();
//
//    public static Map<String, String[][]> marketsSellEntrustArr200 = new HashMap<>();
//
//    public static boolean isInit(Market m) {
//        return initMap.get(m.market);
//    }
//
//    static {
//        for (Entry<String, Market> marketEntry : Market.markets.entrySet()) {
//            Market market = marketEntry.getValue();
//            if (market.listenerOpen) {
//                init(market);
//            }
//        }
//    }
//
//    private static synchronized void init(Market m) {
//        log.info(m.market + " [撮合交易] 开始初始化交易盘数据......");
//        long s1 = System.currentTimeMillis();
//
//        //组装未撮合委托列表
//        loadNoMatchEntrustFromDB(m);
//
//        //初始化买卖委托map
//        //买单按价格从高到低排序
//        TreeMap<BigDecimal, TreeMap<Long, EntrustRecord>> buyEntrustsMap = new TreeMap<>(new Comparator<BigDecimal>() {
//            @Override
//            public int compare(BigDecimal o1, BigDecimal o2) {
//                return o2.compareTo(o1);
//            }
//        });
//        //卖单按价格从低到高排序
//        TreeMap<BigDecimal, TreeMap<Long, EntrustRecord>> sellEntrustsMap = new TreeMap<>();
//
//        //组装买单被动委托列表
//        List<EntrustBean> buyEntrusts = Data.QueryT(m.db, buySql, new Object[]{}, EntrustBean.class);
//        if (buyEntrusts.size() > 0) {
//            for (EntrustBean eb : buyEntrusts) {
//                BigDecimal price = eb.getUnitPrice();
//                EntrustRecord newEntrust = EntrustRecord.transferRecordByBean(eb);
//                TreeMap<Long, EntrustRecord> trans = buyEntrustsMap.get(price);
//                if (trans == null) {
//                    trans = new TreeMap<>();
//                    trans.put(eb.getEntrustId(), newEntrust);
//                    buyEntrustsMap.put(price, trans);
//                } else {
//                    trans.put(eb.getEntrustId(), newEntrust);
//                }
//            }
//        }
//        marketsBuyEntrustMap.put(m.market, buyEntrustsMap);
//
//        //组装卖单被动委托列表
//        List<EntrustBean> sellEntrusts = Data.QueryT(m.db, sellSql, new Object[]{}, EntrustBean.class);
//        if (sellEntrusts.size() > 0) {
//            for (EntrustBean eb : sellEntrusts) {
//                BigDecimal price = eb.getUnitPrice();
//                EntrustRecord newEntrust = EntrustRecord.transferRecordByBean(eb);
//                TreeMap<Long, EntrustRecord> trans = sellEntrustsMap.get(price);
//                if (trans == null) {
//                    trans = new TreeMap<>();
//                    trans.put(eb.getEntrustId(), newEntrust);
//                    sellEntrustsMap.put(price, trans);
//                } else {
//                    trans.put(eb.getEntrustId(), newEntrust);
//                }
//            }
//        }
//        marketsSellEntrustMap.put(m.market, sellEntrustsMap);
//
//        resetPanEntrustArr200(m.market);
//        initMap.put(m.market, true);
//        log.info(m.market + " [撮合交易] 初始化交易盘数据完成共耗时：" + (System.currentTimeMillis() - s1) + " 毫秒。");
//        log.info(m.market + " [撮合交易] 买盘数据价格区间长度：" + (buyEntrustsMap.size()) + "，委托单数量：" + buyEntrusts.size()
//                + "；卖盘数据价格区间长度：" + (sellEntrustsMap.size()) + "，委托单数量：" + sellEntrusts.size());
//    }
//
//    public static Info processOne(Market m) {
//        try {
//            //获取未撮合委托
//            EntrustRecord er = getNoMatchingEntrust(m);
//            if (er == null) {
//                return Info.NoMission;
//            }
//            log.info(m.market + " [撮合交易] 处理单条委托，委托Id:" + er.getId());
//
//            long start = System.currentTimeMillis();
//            if (er.getTypes() == -1 && er.getFreezId() > 0) {
//                Info info = cancel(er, m);
//                if (info == Info.DueCancleSuccess) {
//                    //更新资金新任务内存数据，通知有新的资金任务
//                    SystemStatus.setSystemStatus(m.market + "_" + SystemStatus.moneyNewWork, true);
//                } else {
//                    log.error(m.market + " [撮合交易] 取消委托发生错误，错误信息：" + info.toString());
//                }
//
//                //重置盘口200档数据
//                resetPanEntrustArr200(m.market);
//                log.info(m.market + " [撮合交易] 取消委托：" + er.getId() + "，耗时：" + (System.currentTimeMillis() - start) + " 毫秒。");
//                return info;
//            } else if (er.getTypes() == -1) {
//                Data.Update(m.db, "update entrust set status=2 where entrustId=?", new Object[]{er.getId()});
//
//                removeNoMatchingEntrust(er.getId(), m);
//                log.error(m.market + " [撮合交易] 有一个异常的取消命令，没有冻结Id！委托Id：" + er.getId());
//                return Info.DueEntrustFaildUnKonwType;
//            } else if (er.getTypes() == 0 || er.getTypes() == 1) {
//                Info info = doEntrust(er, m);
//                if (info == Info.DueEntrustSuccess || info == Info.DueEntrustSuccessUnDo) {
//                    //更新资金新任务内存数据，通知有新的资金任务
//                    SystemStatus.setSystemStatus(m.market + "_" + SystemStatus.moneyNewWork, true);
//
//                    //重置盘口200档数据
//                    resetPanEntrustArr200(m.market);
//                } else {
//                    log.error(m.market + " [撮合交易] 处理委托发生错误：" + er.getId() + "，错误信息：" + info.toString());
//                }
//
//                log.info(m.market + " [撮合交易] 处理委托：" + er.getId() + "，耗时：" + (System.currentTimeMillis() - start) + " 毫秒。");
//                return info;
//            } else {
//                int rtn = Data.Update(m.db, "update Entrust set status=2 where entrustId=?", new Object[]{er.getId()});
//                if (rtn > -1) {
//                    removeNoMatchingEntrust(er.getId(), m);
//                }
//                log.error(m.market + " [撮合交易] 处理委托：有一个异常的type为" + er.getTypes() + "命令！");
//                return Info.DueError;
//            }
//        } catch (Exception e) {
//            log.error(m.market + " [撮合交易] 处理单条委托异常！", e);
//            return Info.DueEntrustFaildUnKonwType;
//        }
//    }
//
//    /***
//     * 获取未撮合委托
//     */
//    private static EntrustRecord getNoMatchingEntrust(Market m) {
//        if (needLoadNoMatchFromDB(m.market)) {
//            loadNoMatchEntrustFromDB(m);
//        }
//        EntrustRecord er = null;
//        TreeMap<Long, EntrustRecord> noMatchingMap = marketsNoMatchingMap.get(m.market);
//        try {
//            if (noMatchingMap != null && noMatchingMap.size() > 0) {
//                Entry<Long, EntrustRecord> erEntry = noMatchingMap.firstEntry();
//                if (null == erEntry) {
//                    return null;
//                }
//                er = erEntry.getValue();
//            }
//
//            if (er != null) {
//                //防止某个有问题的挂单导致停止撮合
//                er.setMatchTimes(er.getMatchTimes() + 1);
//                if (er.getMatchTimes() > 100) {
//                    noMatchingMap.remove(er.getId());
//                    return null;
//                } else {
//                    //从数据库里检查一遍数据，防止并发时导致重复处理 add by buxianguan 可以考虑去掉了
//                    EntrustBean dbEntrust = (EntrustBean) Data.GetOne(m.db, "select * from entrust where entrustId=? and status=0",
//                            new Object[]{er.getId()}, EntrustBean.class);
//                    if (null == dbEntrust) {
//                        noMatchingMap.remove(er.getId());
//                        return null;
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.error(m.market + " [撮合交易] 获取未撮合委托失败！", e);
//        }
//        return er;
//    }
//
//    /**
//     * 判断是否需要重新从数据库加载未撮合数据到内存中
//     */
//    private static boolean needLoadNoMatchFromDB(String market) {
//        Long lastLoadTime = lastLoadNoMatchEntrustTimeMap.get(market);
//        if (null == lastLoadTime) {
//            lastLoadTime = 0L;
//        }
//        return (System.currentTimeMillis() - lastLoadTime) > loadNoMatchEntrustFrequency;
//    }
//
//    private static synchronized void loadNoMatchEntrustFromDB(Market m) {
//        if (needLoadNoMatchFromDB(m.market)) {
//            lastLoadNoMatchEntrustTimeMap.put(m.market, System.currentTimeMillis());
//
//            //未撮合委托单
//            List<EntrustBean> noMatchEntrusts = Data.QueryT(m.db, noMatchEntrustSql, new Object[]{}, EntrustBean.class);
//            log.info(m.market + " [撮合交易] 查找未匹配过的委托单，发现了：" + noMatchEntrusts.size() + "个。");
//            if (noMatchEntrusts.size() > 0) {
//                for (EntrustBean eb : noMatchEntrusts) {
//                    EntrustRecord er = EntrustRecord.transferRecordByBean(eb);
//                    er.setFreezId(eb.getFreezeId());
//                    addNoMatchingEntrust(er, m);
//                }
//            }
//        } else {
//            log.info(m.market + " [撮合交易] 并发查询未成交的委托单，此次无需查找了......");
//        }
//    }
//
//    /***
//     * 处理新的买单
//     * @param eb
//     */
//    public synchronized static Info doEntrust(EntrustRecord eb, Market m) {
//        if (!initMap.get(m.market)) {
//            init(m);
//        }
//        Info info = null;
//        BigDecimal price = eb.getPrice();
//        TreeMap<BigDecimal, TreeMap<Long, EntrustRecord>> sellEntrustsMap = marketsSellEntrustMap.get(m.market);
//        TreeMap<BigDecimal, TreeMap<Long, EntrustRecord>> buyEntrustsMap = marketsBuyEntrustMap.get(m.market);
//        NavigableMap<BigDecimal, TreeMap<Long, EntrustRecord>> canTrans = null;
//        if (eb.getTypes() == 1) {
//            canTrans = sellEntrustsMap.headMap(price, true);
//        } else {
//            canTrans = buyEntrustsMap.headMap(price, true);
//        }
//        if (canTrans == null || canTrans.size() <= 0) {
//            //无法成交
//            info = noTrans(eb, m);
//        } else {
//            //撮合成交
//            info = doTrans(canTrans, eb, m);
//        }
//
//        return info;
//    }
//
//    private static Info noTrans(EntrustRecord eb, Market m) {
//        try {
//            //说明没有符合条件的记录，更新记录状态为3，归入撮合被动委托内存中
//            long start1 = System.currentTimeMillis();
//            int rtn = Data.Update(m.db, "update entrust set status=3 where entrustId=? and status=0", new Object[]{eb.getId()});
//            log.info(m.market + " [撮合交易] 无法成交数据库处理耗时：" + (System.currentTimeMillis() - start1) + " 毫秒。");
//            if (rtn > -1) {
//                //移除待撮合委托列表
//                removeNoMatchingEntrust(eb.getId(), m);
//                //把这个委托加到被动委托内存中
//                addToMarketsEntrustMap(eb, m);
//                //更新用户挂单列表
//                MemEntrustMatchProcessor.da.getTop(eb.getUserId(), m);
//            }
//            return Info.DueEntrustSuccessUnDo;
//        } catch (Exception e) {
//            log.error(m.market + " [撮合交易] 更新未撮合记录没有成交状态异常！", e);
//        }
//
//        return Info.DoCancleFaildNoOrder;
//    }
//
//    /***
//     * 撮合成交
//     * @param canTrans
//     * @param initiativeEntrust 主动委托
//     */
//    private static Info doTrans(NavigableMap<BigDecimal, TreeMap<Long, EntrustRecord>> canTrans, EntrustRecord initiativeEntrust, Market m) {
//        log.info(m.market + " [撮合交易] 开始撮合交易，委托id：" + initiativeEntrust.getId());
//
//        //主动委托状态，0初始化，2完全成交，3部分成交
//        int initiativeStatus = 0;
//        //处理交易摘要委托集合
//        List<EntrustRecord> transactionSummaryList = new ArrayList<>();
//        BigDecimal lastNewPrice = null;
//        BigDecimal lastNewNumber = null;
//
//        Entry<BigDecimal, TreeMap<Long, EntrustRecord>> entry = canTrans.firstEntry();
//        while (entry != null && initiativeStatus != 2) {
//            BigDecimal canPrice = entry.getKey();
//            log.info(m.market + " [撮合交易] 当前撮合价格，canPrice=" + canPrice);
//
//            //校验主被动委托价格，主动买价必须小于被动卖价，主动卖价必须大于被动买价
//            if (initiativeEntrust.getTypes() == 1 && canPrice.compareTo(initiativeEntrust.getPrice()) > 0) {
//                return Info.DueEntrustSuccessUnDo;
//            }
//            if (initiativeEntrust.getTypes() == 0 && canPrice.compareTo(initiativeEntrust.getPrice()) < 0) {
//                return Info.DueEntrustSuccessUnDo;
//            }
//
//            TreeMap<Long, EntrustRecord> records = entry.getValue();
//            //循环当前价位的被动委托，直到当前价位无被动委托或者主动委托已经完全成交
//            while (null != records && !records.isEmpty() && initiativeStatus != 2) {
//                long start0 = System.currentTimeMillis();
//
//                Entry<Long, EntrustRecord> firstEntry = records.firstEntry();
//                EntrustRecord passiveEntrust = firstEntry.getValue();
//
//                //被动委托剩余数量
//                BigDecimal passiveNumbers = passiveEntrust.getNumber();
//                log.info(m.market + " [撮合交易] passiveId:" + passiveEntrust.getId() + "，passiveNumbers:" + passiveNumbers);
//
//                //主动委托剩余数量
//                BigDecimal initiativeNumbers = initiativeEntrust.getNumber();
//                log.info(m.market + " [撮合交易] initiativeId:" + initiativeEntrust.getId() + "，initiativeNumbers:" + initiativeNumbers);
//
//                //本次能成交的数量
//                BigDecimal thisNumbers = initiativeNumbers;
//                //主被动委托的状态
//                int passiveStatus = 3;
//                initiativeStatus = 3;
//                if (initiativeNumbers.compareTo(passiveNumbers) > 0) {
//                    thisNumbers = passiveNumbers;
//                    passiveStatus = 2;
//                } else if (initiativeNumbers.compareTo(passiveNumbers) == 0) {
//                    passiveStatus = 2;
//                    initiativeStatus = 2;
//                } else {
//                    initiativeStatus = 2;
//                }
//
//                //本次交易的钱
//                BigDecimal thisMoney = Market.totalMoney(passiveEntrust.getPrice(), thisNumbers);
//                //组装sql语句
//                List<OneSql> sqls = getSqls(initiativeEntrust, initiativeStatus, passiveEntrust, passiveStatus, thisNumbers, thisMoney, m);
//
//                long start1 = System.currentTimeMillis();
//                if (Data.doTrans(sqls)) {
//                    log.info(m.market + " [撮合交易] 数据库处理耗时：" + (System.currentTimeMillis() - start1) + " 毫秒。");
//
//                    //封装交易摘要
//                    getTransactionSummaryList(initiativeEntrust, passiveEntrust, thisNumbers, transactionSummaryList);
//
//                    //更新当前市场最新价格和数量，以本次成交为准，撮合完后更新内存
//                    lastNewPrice = passiveEntrust.getPrice();
//                    lastNewNumber = thisNumbers;
//
//                    //如果被动成交
//                    if (passiveStatus == 2) {
//                        //当前委托单已经成交完了，删掉
//                        records.remove(passiveEntrust.getId());
//                        //被动委托已成交，更新个人挂单列表
//                        MemEntrustMatchProcessor.da.getTop(passiveEntrust.getUserId(), m);
//                    } else {
//                        //更新被动剩余数量
//                        passiveEntrust.setNumber(passiveEntrust.getNumber().subtract(thisNumbers));
//                        passiveEntrust.setCompleteNumber(passiveEntrust.getCompleteNumber().add(thisNumbers));
//                        passiveEntrust.setCompleteTotalMoney(passiveEntrust.getCompleteTotalMoney().add(thisMoney));
//                        //被动委托部分成交，更新个人挂单列表
//                        MemEntrustMatchProcessor.da.getTop(passiveEntrust.getUserId(), m);
//                    }
//
//                    //如果主动成交
//                    if (initiativeStatus == 2) {
//                        //移除未匹配列表
//                        removeNoMatchingEntrust(initiativeEntrust.getId(), m);
//                        //主动委托已成交，更新个人挂单列表
//                        MemEntrustMatchProcessor.da.getTop(initiativeEntrust.getUserId(), m);
//                    } else {
//                        //更新循环内存中的数据
//                        initiativeEntrust.setNumber(initiativeEntrust.getNumber().subtract(thisNumbers));
//                        initiativeEntrust.setCompleteNumber(initiativeEntrust.getCompleteNumber().add(thisNumbers));
//                        initiativeEntrust.setCompleteTotalMoney(initiativeEntrust.getCompleteTotalMoney().add(thisMoney));
//                    }
//                } else {
//                    ///处理失败，刷新一下当前价位下的挂单
//                    log.error(m.market + " [撮合交易] 执行事物失败重新设置当前档位内存，主动委托Id：" + initiativeEntrust.getId() + "，被动委托Id：" + passiveEntrust.getId());
//                    refreshCurrentEntrust(initiativeEntrust, m);
//                    refreshCurrentEntrust(passiveEntrust, m);
//                    return Info.DueEntrustFaildProError;
//                }
//
//                log.info(m.market + " [撮合交易] 撮合一笔委托耗时：" + (System.currentTimeMillis() - start0) + " 毫秒。");
//            }
//
//            if (null == records || records.isEmpty()) {
//                canTrans.remove(canPrice);
//                entry = canTrans.firstEntry();
//            }
//        }
//
//        log.info(m.market + " [撮合交易] 主动委托状态，initiativeStatus=" + initiativeStatus);
//
//        //如果主动委托未撮合
//        if (initiativeStatus == 0) {
//            //更新数据库为3，变成被动委托
//            int rtn = Data.Update(m.db, "update Entrust set status=3 where entrustId=? and status=0", new Object[]{initiativeEntrust.getId()});
//            if (rtn > -1) {
//                initiativeStatus = 3;
//            }
//        }
//
//        //如果主动委托未完全成交
//        if (initiativeStatus == 3) {
//            //把主动委托加到被动委托内存中
//            addToMarketsEntrustMap(initiativeEntrust, m);
//            //移除待撮合委托列表
//            removeNoMatchingEntrust(initiativeEntrust.getId(), m);
//            //更新主动委托个人挂单列表
//            MemEntrustMatchProcessor.da.getTop(initiativeEntrust.getUserId(), m);
//        }
//
//        long start2 = System.currentTimeMillis();
//        //根据委托列表处理交易摘要
//        updateTransactionSymmary(transactionSummaryList, m);
//        log.info(m.market + " [撮合交易] 交易摘要处理耗时：" + (System.currentTimeMillis() - start2) + " 毫秒。");
//
//        if (null != lastNewPrice) {
//            log.info(m.market + " [撮合交易] 市场最新价格和数量，lastNewPrice:" + lastNewPrice + "，lastNewNumber:" + lastNewNumber);
//            //更新当前市场最新价格和数量
//            MemEntrustMatchProcessor.da.UpdateEntrust(lastNewPrice, lastNewNumber, initiativeEntrust.getTypes(), m);
//        }
//
//        return Info.DueEntrustSuccess;
//    }
//
//    /**
//     * 取消委托
//     * 委托表最终0 原始状态  1取消  2成功 3 交易一部分
//     *
//     * @param er
//     * @return
//     */
//    public static Info cancel(EntrustRecord er, Market m) {
//        try {
//            EntrustBean originalEr = (EntrustBean) Data.GetOne(m.db, "select * from Entrust where entrustId=?", new Object[]{er.getFreezId()}, EntrustBean.class);
//            if (originalEr == null) {
//                log.info(m.market + " [撮合交易] 取消委托错误，发现一个没有原始记录的取消");
//                removeNoMatchingEntrust(er.getId(), m);
//                return Info.DueCancleFaildNoFouce;
//            }
//
//            //校验原纪录是否已经撮合完成
//            if (originalEr.getStatus() == 2 || originalEr.getNumbers().compareTo(originalEr.getCompleteNumber()) == 0 || originalEr.getStatus() == 1) {
//                log.info(m.market + " [撮合交易] 取消委托，原始委托已经处理完毕，无需操作");
//                int rtn = Data.Update(m.db, "update entrust set status=2 where entrustId=?", new Object[]{er.getId()});
//                if (rtn > -1) {
//                    removeNoMatchingEntrust(er.getId(), m);
//                    removeFromMarketsEntrustMap(originalEr.getTypes(), originalEr.getUnitPrice(), originalEr.getEntrustId(), m);
//                }
//                return Info.DueCancleFaildHasDued;
//            }
//
//            List<OneSql> sqls = new ArrayList<>();
//            //取消委托，判断被取消的委托是否有成交，如果有部分成交，取消后更新状态为2 ：已完成 ，如果没有成交，取消更新状态为1:已取消
//            int cancelStatus = 1;
//            if (originalEr.getCompleteNumber().compareTo(BigDecimal.ZERO) > 0) {
//                cancelStatus = 2;
//            }
//            sqls.add(new OneSql("update entrust set status=? where entrustId=?", 1, new Object[]{cancelStatus, originalEr.getEntrustId()}, m.db));
//            //更新当前这个取消命令
//            sqls.add(new OneSql("update entrust set status=2 where entrustId=?", 1, new Object[]{er.getId()}, m.db));
//
//            //生成成交记录
//            if (originalEr.getTypes() == 1) {
//                Object obj = Data.GetOne(m.db, "select * from transrecord where entrustIdBuy=? and unitPrice=0 and numbers=0", new Object[]{originalEr.getEntrustId()});
//                if (obj == null) {//对于买会存在多冻结的问题，所以可能已经产生了返还命令，然后这边如果再取消，相当与就多了一次
//                    sqls.add(new OneSql(
//                            "INSERT INTO transrecord (unitPrice, totalPrice, numbers, entrustIdBuy, userIdBuy, entrustIdSell, userIdSell, types, times, timeMinute,webIdBuy,webIdSell) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
//                            1, new Object[]{
//                            0,
//                            originalEr.getTotalMoney().subtract(originalEr.getCompleteTotalMoney()),//剩余的所有钱   可能出现负职，因为总资金四舍五入的问题
//                            originalEr.getNumbers().subtract(originalEr.getCompleteNumber()),//剩余的所有都取消
//                            originalEr.getEntrustId(),//卖卖记录一样的
//                            originalEr.getUserId(),
//                            0,//买卖记录一样的
//                            0,
//                            originalEr.getTypes(),
//                            TimeUtil.getNow().getTime(),
//                            TimeUtil.getMinuteFirst().getTime(),
//                            originalEr.getWebId(),
//                            0
//                    }, m.db));
//                }
//            } else if (originalEr.getTypes() == 0) {
//                sqls.add(new OneSql(
//                        "INSERT INTO transrecord (unitPrice, totalPrice, numbers, entrustIdBuy, userIdBuy, entrustIdSell, userIdSell, types, times, timeMinute,webIdBuy,webIdSell) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
//                        1, new Object[]{
//                        0,
//                        0,//剩余的所有钱买房
//                        originalEr.getNumbers().subtract(originalEr.getCompleteNumber()),//剩余的所有都取消
//                        0,//卖卖记录一样的
//                        0,
//                        originalEr.getEntrustId(),//买卖记录一样的
//                        originalEr.getUserId(),
//                        originalEr.getTypes(),
//                        TimeUtil.getNow().getTime(),
//                        TimeUtil.getMinuteFirst().getTime(),
//                        0,
//                        originalEr.getWebId()
//                },
//                        m.db));
//            }
//
//            long start1 = System.currentTimeMillis();
//            if (Data.doTrans(sqls)) {
//                log.info(m.market + " [撮合交易] 取消数据库处理耗时：" + (System.currentTimeMillis() - start1) + " 毫秒。");
//
//                removeNoMatchingEntrust(er.getId(), m);
//                removeFromMarketsEntrustMap(originalEr.getTypes(), originalEr.getUnitPrice(), originalEr.getEntrustId(), m);
//
//                //更新个人挂单列表
//                MemEntrustMatchProcessor.da.getTop(er.getUserId(), m);
//                return Info.DueCancleSuccess;
//            } else {
//                //移除掉不处理了  防止交易大盘停住
//                removeNoMatchingEntrust(er.getId(), m);
//                return Info.DueCancleFaildPromError;
//            }
//        } catch (Exception e) {
//            log.error(m.market + " [撮合交易] 取消委托处理失败", e);
//            return Info.DueCancleFaildPromError;
//        }
//    }
//
//    /**
//     * 返回200档数据 买盘
//     *
//     * @param market 币种市场名称
//     * @return 字符串二维数组
//     * @author zhanglinbo 20170119
//     */
//    public static String[][] getBuyEntrustMap(String market) {
//        return marketsBuyEntrustArr200.get(market);
//    }
//
//    /**
//     * 返回200档数据 卖盘
//     *
//     * @param market 币种市场名称
//     * @return 字符串二维数组
//     * @author zhanglinbo 20170119
//     */
//    public static String[][] getSellEntrustMap(String market) {
//        return marketsSellEntrustArr200.get(market);
//    }
//
//
//    public synchronized static boolean addNoMatchingEntrust(EntrustRecord er, Market m) {
//        try {
//            TreeMap<Long, EntrustRecord> noMatchingMap = marketsNoMatchingMap.get(m.market);
//            if (noMatchingMap == null) {
//                noMatchingMap = new TreeMap<>();
//                marketsNoMatchingMap.put(m.market, noMatchingMap);
//            }
//            noMatchingMap.put(er.getId(), er);
//            return true;
//        } catch (Exception e) {
//            log.error(m.market + " [撮合交易] 添加未撮合委托内存失败！entrustId=" + er.getId(), e);
//            return false;
//        }
//    }
//
//    public synchronized static boolean containsNoMatchingEntrust(long id, Market m) {
//        try {
//            return marketsNoMatchingMap.get(m.market).containsKey(id);
//        } catch (Exception e) {
//            log.error(m.market + " [撮合交易] 判断委托是否存在未撮合内存中失败！entrustId=" + id, e);
//            return false;
//        }
//    }
//
//    public synchronized static boolean removeNoMatchingEntrust(long id, Market m) {
//        try {
//            marketsNoMatchingMap.get(m.market).remove(id);
//            return true;
//        } catch (Exception e) {
//            log.error(m.market + " [撮合交易] 移除未撮合委托内存失败！entrustId=" + id, e);
//            return false;
//        }
//    }
//
//    /**
//     * 获取最新买一卖一
//     *
//     * @return
//     */
//    public static synchronized BigDecimal[] getBuyOneAndSellOne(Market m) {
//        Boolean marketListened = initMap.get(m.market);
//        if (null != marketListened && marketListened) {
//            BigDecimal sellOne = BigDecimal.ZERO;
//            BigDecimal buyOne = BigDecimal.ZERO;
//            TreeMap<BigDecimal, TreeMap<Long, EntrustRecord>> buyEntrustsMap = marketsBuyEntrustMap.get(m.market);
//            TreeMap<BigDecimal, TreeMap<Long, EntrustRecord>> sellEntrustsMap = marketsSellEntrustMap.get(m.market);
//
//            if (sellEntrustsMap != null && sellEntrustsMap.size() > 0) {
//                sellOne = sellEntrustsMap.firstKey();
//            }
//            if (buyEntrustsMap != null && buyEntrustsMap.size() > 0) {
//                buyOne = buyEntrustsMap.firstKey();
//            }
//            log.info("[买一卖一价格] 市场:" + m.market + ", 买一价:" + buyOne + ", 卖一价:" + sellOne);
//            return new BigDecimal[]{buyOne, sellOne};
//        }
//        return null;
//    }
//
//    /***
//     * 修复内存操作
//     * @param er
//     */
//    private static synchronized void refreshCurrentEntrust(EntrustRecord er, Market m) {
//        log.error(m.market + " [撮合交易] 修复委托单：" + er.getId());
//        EntrustBean eb = Data.GetOneT(m.db, entrustSql, new Object[]{er.getId()}, EntrustBean.class);
//
//        //如果委托记录为空或者已经完全成交
//        if (null == eb || eb.getStatus() == 2) {
//            removeNoMatchingEntrust(er.getId(), m);
//            removeFromMarketsEntrustMap(er.getTypes(), er.getPrice(), er.getId(), m);
//        } else if (eb.getStatus() == 3 && eb.getNumbers().compareTo(eb.getCompleteNumber()) > 0) {
//            //如果委托记录没有完全成交，从主动委托移除，重新添加到被动委托中
//            EntrustRecord newEntrust = EntrustRecord.transferRecordByBean(eb);
//            addToMarketsEntrustMap(newEntrust, m);
//            removeNoMatchingEntrust(er.getId(), m);
//        } else if (eb.getStatus() == 0) {
//            //如果委托记录还是初始值，重新加载到主动委托中
//            EntrustRecord newEntrust = EntrustRecord.transferRecordByBean(eb);
//            addNoMatchingEntrust(newEntrust, m);
//        }
//    }
//
//    /**
//     * 获取交易摘要列表原始数据
//     */
//    private static void getTransactionSummaryList(EntrustRecord initiativeEntrust, EntrustRecord passiveEntrust, BigDecimal thisNumbers, List<EntrustRecord> transactionSummaryList) {
//        //交易摘要处理移到最后，先封装交易摘要待处理列表，减少请求量
//        //交易摘要,被动方处理
//        EntrustRecord passiveSummary = getTransactionSymmary(thisNumbers, passiveEntrust.getPrice(), passiveEntrust.getTypes(), passiveEntrust.getUserId());
//        transactionSummaryList.add(passiveSummary);
//
//        //交易摘要,主动方处理
//        EntrustRecord initiativeSummary = getTransactionSymmary(thisNumbers, passiveEntrust.getPrice(), initiativeEntrust.getTypes(), initiativeEntrust.getUserId());
//        transactionSummaryList.add(initiativeSummary);
//    }
//
//    /**
//     * 组装插入sql
//     */
//    private static List<OneSql> getSqls(EntrustRecord initiativeEntrust, int initiativeStatus, EntrustRecord passiveEntrust, int passiveStatus, BigDecimal thisNumbers, BigDecimal thisMoney, Market m) {
//        List<OneSql> sqls = new ArrayList<>();
//        //主动委托状态
//        sqls.add(new OneSql(
//                "update entrust set status=?,completeNumber=completeNumber+?,completeTotalMoney=completeTotalMoney+? where entrustId=? and completeNumber+?<=numbers and status in(0,3)",
//                1, new Object[]{initiativeStatus, thisNumbers, thisMoney, initiativeEntrust.getId(), thisNumbers}, m.db));
//        //被动委托状态
//        sqls.add(new OneSql(
//                "update entrust set status=?,completeNumber=completeNumber+?,completeTotalMoney=completeTotalMoney+? where entrustId=? and completeNumber+?<=numbers and status in(0,3)",
//                1, new Object[]{passiveStatus, thisNumbers, thisMoney, passiveEntrust.getId(), thisNumbers}, m.db));
//
//        //封装成交记录
//        if (initiativeEntrust.getTypes() == 1) {
//            sqls.add(new OneSql(
//                    "INSERT INTO transrecord (unitPrice, totalPrice, numbers, entrustIdBuy, userIdBuy, entrustIdSell, userIdSell, types, times, timeMinute,webIdBuy,webIdSell,actStatus) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)",
//                    1, new Object[]{
//                    passiveEntrust.getPrice(),
//                    thisMoney,
//                    thisNumbers,
//                    initiativeEntrust.getId(),
//                    initiativeEntrust.getUserId(),
//                    passiveEntrust.getId(),
//                    passiveEntrust.getUserId(),
//                    initiativeEntrust.getTypes(),//当前记录是买行为还是卖行为
//                    TimeUtil.getNow().getTime(),
//                    TimeUtil.getMinuteFirst().getTime(),
//                    initiativeEntrust.getWebId(),
//                    passiveEntrust.getWebId(),
//                    1
//            }, m.db));
//        } else {
//            sqls.add(new OneSql(
//                    "INSERT INTO transrecord (unitPrice, totalPrice, numbers, entrustIdBuy, userIdBuy, entrustIdSell, userIdSell, types, times, timeMinute,webIdBuy,webIdSell,actStatus) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)",
//                    1, new Object[]{
//                    passiveEntrust.getPrice(),
//                    thisMoney,
//                    thisNumbers,
//                    passiveEntrust.getId(),
//                    passiveEntrust.getUserId(),
//                    initiativeEntrust.getId(),
//                    initiativeEntrust.getUserId(),
//                    initiativeEntrust.getTypes(),//当前记录是买行为还是卖行为
//                    TimeUtil.getNow().getTime(),
//                    TimeUtil.getMinuteFirst().getTime(),
//                    passiveEntrust.getWebId(),
//                    initiativeEntrust.getWebId(),
//                    1
//            }, m.db));
//        }
//        return sqls;
//    }
//
//    /**
//     * 组装交易摘要处理类
//     * 交易摘要获取初始价格,数量,不同的情景设置不同的交易类型和用户ID即可
//     *
//     * @param completeNumber
//     * @param price
//     * @param types
//     * @param userId
//     * @return
//     */
//    private static EntrustRecord getTransactionSymmary(BigDecimal completeNumber, BigDecimal price, int types, int userId) {
//        EntrustRecord summary = new EntrustRecord();
//        summary.setCompleteNumber(completeNumber);
//        summary.setPrice(price);
//        summary.setTypes(types);
//        summary.setUserId(userId);
//        return summary;
//    }
//
//    /**
//     * 把委托添加到被动委托列表中
//     */
//    private static void addToMarketsEntrustMap(EntrustRecord er, Market m) {
//        TreeMap<BigDecimal, TreeMap<Long, EntrustRecord>> entrustsMap = new TreeMap<>();
//        if (er.getTypes() == 1) {
//            entrustsMap = marketsBuyEntrustMap.get(m.market);
//        } else if (er.getTypes() == 0) {
//            entrustsMap = marketsSellEntrustMap.get(m.market);
//        }
//        TreeMap<Long, EntrustRecord> records = entrustsMap.get(er.getPrice());
//        if (records == null) {
//            records = new TreeMap<>();
//            records.put(er.getId(), er);
//            entrustsMap.put(er.getPrice(), records);
//        } else {
//            records.put(er.getId(), er);
//        }
//    }
//
//    /**
//     * 从被动委托列表中移除记录
//     */
//    private static void removeFromMarketsEntrustMap(int types, BigDecimal price, long entrustId, Market m) {
//        TreeMap<BigDecimal, TreeMap<Long, EntrustRecord>> entrustsMap = new TreeMap<>();
//        if (types == 1) {
//            entrustsMap = marketsBuyEntrustMap.get(m.market);
//        } else if (types == 0) {
//            entrustsMap = marketsSellEntrustMap.get(m.market);
//        }
//        TreeMap<Long, EntrustRecord> records = entrustsMap.get(price);
//        if (null != records && records.size() > 0) {
//            records.remove(entrustId);
//        }
//        if (null == records || records.size() <= 0) {
//            entrustsMap.remove(price);
//        }
//    }
//
//    /**
//     * 重置盘口200档数据
//     *
//     * @param market 币种名称
//     * @author zhanglinbo 20170119
//     */
//    private static void resetPanEntrustArr200(String market) {
//        int sellIndex = 0;//计数下标
//        int buyIndex = 0;//计数下标
//        String[][] buyArr = new String[200][3];
//        String[][] sellArr = new String[200][3];
//        for (Entry<BigDecimal, TreeMap<Long, EntrustRecord>> entry : marketsSellEntrustMap.get(market).entrySet()) {
//            BigDecimal priceKey = entry.getKey();
//            TreeMap<Long, EntrustRecord> v = entry.getValue();
//            BigDecimal sNumber = BigDecimal.ZERO;
//            StringBuilder sellUserIdBuilder = new StringBuilder();
//            for (Entry<Long, EntrustRecord> sentry : v.entrySet()) {
//                EntrustRecord er = sentry.getValue();
//                sNumber = sNumber.add((er.getSrcNumbers().subtract(er.getCompleteNumber())));
//                sellUserIdBuilder.append(er.getUserId()).append("a");
//            }
//            if (sNumber.compareTo(BigDecimal.ZERO) <= 0) {
//                continue;
//            }
//            if (sellIndex < 200) {
//                String[] data = new String[3];
//                data[0] = String.valueOf(priceKey);
//                data[1] = String.valueOf(sNumber);
//                data[2] = String.valueOf(sellUserIdBuilder.substring(0, sellUserIdBuilder.length() - 1));
//                sellArr[sellIndex] = data;
//            } else {
//                break;
//            }
//            sellIndex++;
//        }
//        //保存到缓存
//        marketsSellEntrustArr200.put(market, sellArr);
//
//        for (Entry<BigDecimal, TreeMap<Long, EntrustRecord>> entry : marketsBuyEntrustMap.get(market).entrySet()) {
//            BigDecimal priceKey = entry.getKey();
//            TreeMap<Long, EntrustRecord> v = entry.getValue();
//            BigDecimal sNumber = BigDecimal.ZERO;
//            StringBuilder buyUserIdBuilder = new StringBuilder();
//            for (Entry<Long, EntrustRecord> sentry : v.entrySet()) {
//                EntrustRecord er = sentry.getValue();
//                sNumber = sNumber.add(er.getSrcNumbers().subtract(er.getCompleteNumber()));
//                buyUserIdBuilder.append(er.getUserId()).append("a");
//            }
//            if (sNumber.compareTo(BigDecimal.ZERO) <= 0) {
//                continue;
//            }
//
//            if (buyIndex < 200) {
//                String[] data = new String[3];
//                data[0] = String.valueOf(priceKey);
//                data[1] = String.valueOf(sNumber);
//                data[2] = String.valueOf(buyUserIdBuilder.substring(0, buyUserIdBuilder.length() - 1));
//                buyArr[buyIndex] = data;
//            } else {
//                break;
//            }
//            buyIndex++;
//        }
//        marketsBuyEntrustArr200.put(market, buyArr);
//    }
//
//    /**
//     * 根据委托列表更新交易摘要
//     *
//     * @param erList
//     * @param m
//     */
//    public static void updateTransactionSymmary(List<EntrustRecord> erList, Market m) {
//        if (CollectionUtils.isEmpty(erList)) {
//            return;
//        }
//        for (LegalTenderType tenderType : LegalTenderType.values()) {
//            BigDecimal legalPrice = new BigDecimal(1);
//            String legalConvert = "1";
//            if (m.getMarket().toLowerCase().contains("_usdt")) {
//                if ("USD".equals(tenderType.getKey())) {
//                    legalPrice = ChartManager.getPrice(m);
//                } else {
//                    legalConvert = Cache.Get("usdt_" + tenderType.getKey().toLowerCase());
//                    if (!StringUtil.exist(legalConvert)) {
//                        legalConvert = "1";
//                    }
//                    legalPrice = ChartManager.getPrice(m).multiply(new BigDecimal(legalConvert));
//                }
//            } else if (m.getMarket().toLowerCase().contains("_btc")) {
//                if ("USD".equals(tenderType.getKey())) {
//                    legalConvert = Cache.Get("btc_usdt");
//                    if (!StringUtil.exist(legalConvert)) {
//                        legalConvert = "1";
//                    }
//                    legalPrice = ChartManager.getPrice(m).multiply(new BigDecimal(legalConvert));
//                } else {
//                    legalConvert = Cache.Get("btc_" + tenderType.getKey().toLowerCase());
//                    if (!StringUtil.exist(legalConvert)) {
//                        legalConvert = "1";
//                    }
//                    legalPrice = ChartManager.getPrice(m).multiply(new BigDecimal(legalConvert));
//                }
//            } else {
//                log.error(m.market + " [撮合交易] 交易摘要尚未维护" + m.getMarket() + "该市场信息");
//                break;
//            }
//
//            log.info(m.market + " [撮合交易] 交易摘要，legalPrice:" + legalPrice + "，legalConvert:" + legalConvert);
//
//            for (EntrustRecord er : erList) {
//                BigDecimal transPrice = er.getPrice();
//                transPrice = transPrice.multiply(new BigDecimal(legalConvert));
//
//                String summaryKey = "transaction_summary_" + m.getMarket().toLowerCase() + "_" + tenderType.getKey().toLowerCase() + "_" + er.getUserId();
//                TransactionSummary transactionSummary;
//
//                //买为正,卖为负
//                BigDecimal num = er.getTypes() == 1 ? er.getCompleteNumber() : er.getCompleteNumber().negate();
//
//                String summaryJson = Cache.Get(summaryKey);
//                if (!StringUtil.exist(summaryJson)) {
//                    transactionSummary = new TransactionSummary();
//                    transactionSummary.setNum(num);
//                    transactionSummary.setTransactionPrice(transPrice);
//                    transactionSummary.setCost(num.multiply(transPrice));
//                    transactionSummary.setNetAmount(num);
//                    transactionSummary.setCostPrice(transPrice);
//
////			//盈亏 = | |市价| - |成本价| | * |净额|
////			transactionSummary.setProfitLoss(
////					currencyPrice.abs().subtract(transactionSummary.getCostPrice().abs()).abs()
////							.multiply(transactionSummary.getNetAmount().abs()));
//
//                    Cache.Set(summaryKey, JSON.toJSONString(transactionSummary));
//                    continue;
//                }
//
//                TransactionSummary ts = JSON.parseObject(summaryJson, TransactionSummary.class);
//
//                //净额=0的话会导致后续处理异常,所以盈亏用当前价格基于上一次的数据来计算
//                BigDecimal cost = ts.getCost().add(num.multiply(transPrice));
//                BigDecimal netAmount = ts.getNetAmount().add(num);
//
//                transactionSummary = new TransactionSummary();
//                transactionSummary.setNum(num);
//                transactionSummary.setTransactionPrice(transPrice);
//                transactionSummary.setCost(cost);
//                transactionSummary.setNetAmount(netAmount);
//
//                if (netAmount.compareTo(BigDecimal.ZERO) == 0) {
//                    transactionSummary.setCostPrice(BigDecimal.ZERO);
////			//当 净额==0 的时候,盈亏=成本
////			transactionSummary.setProfitLoss(transactionSummary.getCost());
//
//                    Cache.Set(summaryKey, JSON.toJSONString(transactionSummary));
//                    continue;
//                }
//
//                transactionSummary.setCostPrice(cost.divide(netAmount, 5, BigDecimal.ROUND_HALF_EVEN));
//                transactionSummary.setProfitLoss(
//                        legalPrice.abs().subtract(ts.getCostPrice().abs()).abs()
//                                .multiply(ts.getNetAmount().abs()));
//
//                Cache.Set(summaryKey, JSON.toJSONString(transactionSummary));
//            }
//        }
//    }
//}
