//package com.tenstar.timer.entrust;
//
//
//import com.tenstar.Info;
//import com.tenstar.SystemStatus;
//import com.world.model.Market;
//import org.apache.log4j.Logger;
//
//import java.util.TimerTask;
//
//public class Exchange extends TimerTask {
//
//    public static Logger log = Logger.getLogger(Exchange.class);
//    public static long times = System.currentTimeMillis();
//    public static boolean doOne = false;
////    public static DataArray da;
//
//    private Market m;
//
//    public Exchange(Market m) {
//        this.m = m;
////        da = new DataArray(m.market, m.maxPrice);
//        log.info("[交易大盘启动] 市场:" + m.market + "交易大盘系统已启动成功！至少占用内存" + 8 * m.maxPrice * 3 / (1024 * 1024) + "M内存");
//    }
//
//    @Override
//    public void run() {
//        try {
//            //在固定周期内或者主动状态被触发就会执行
//            if ((System.currentTimeMillis() - times) > 3000 || SystemStatus.getSystemStatus(m.market + "_" + SystemStatus.exchangeNewWork)) {
//                SystemStatus.setSystemStatus(m.market + "_" + SystemStatus.exchangeNewWork, false);
//
//                //做一个循环，不断获取需要处理的数据
//                int dueWork = 0;
//                long start = System.currentTimeMillis();
//                Info result = Info.NoMission;
//                do {
//                    result = MemEntrustProcessor.processOne(m);
//                    if (result != Info.NoMission) {
//                        dueWork++;
//                    }
//                } while (result != Info.NoMission);
//
//                log.info(m.market + " [撮合交易] 委托处理统计，本轮处理任务：" + dueWork + "个, 耗时:" + (System.currentTimeMillis() - start) + "毫秒。");
//                //增加一个单位跳出本循环
//                times = System.currentTimeMillis();
//            } else {
//                Thread.sleep(10);
//            }
//        } catch (Exception ex) {
//            log.error(m.market + " [撮合交易] 定时任务执行异常！", ex);
//        }
//
////        try {
////            Thread.sleep(10);
////        } catch (Exception ex) {
////        }
//    }
//
///**
// * 处理一个任务
// *
// * @return 是否处理成功
// */
////    public static Info ProcessOne(Market m) {
////        try {
////            //所有新任务才会引起变化
////            EntrustBean beb = (EntrustBean) Data.GetOne(m.db,
////                    "select * from entrust where status=0 order by entrustId asc limit 0,1",
////                    new Object[]{}, EntrustBean.class);
////            if (beb == null) {
////                //	log.info("没有任务");
////                return Info.NoMission;
////            }
////            log.info("=========================================================");
////            log.info("准备处理委托，编号：" + beb.getEntrustId());
////            log.info("=========================================================");
////
////            if (beb.getTypes() == -1 && beb.getFreezeId() > 0) {
////                long start = System.currentTimeMillis();
////                Info i = cancle(beb, m);//cancle(beb);
////                log.info("entrustProcessOne取消耗时：" + (System.currentTimeMillis() - start));
////                if (i == Info.DueCancleSuccess) {//更新内存数据
////                    //SystemStatus.moneyNewWork=true;
////                    //SystemStatus.chartDataNewWork=true;
////
////                    SystemStatus.setSystemStatus(m.market + "_" + SystemStatus.moneyNewWork, true);
////                    SystemStatus.setSystemStatus(m.market + "_" + SystemStatus.chartDataNewWork, true);
////
////
////                    //SystemStatus.exchangeNewWork=true;
////                } else {
////                    //SystemStatus.chartDataNewWork=true;
////                    SystemStatus.setSystemStatus(m.market + "_" + SystemStatus.chartDataNewWork, true);
////                    log.error("取消发生错误：" + i.toString());
////                }
////                return i;
////            } else if (beb.getTypes() == -1) {
////                Data.Update("update entrust set status=2 where entrustId=?", new Object[]{+beb.getEntrustId()});
////                log.error("有一个异常的取消命令！");
////                return Info.DueEntrustFaildUnKonwType;
////            } else if (beb.getTypes() == 0 || beb.getTypes() == 1) {
////                long start = System.currentTimeMillis();
////                Info i = entrust(beb, m);
////                log.info("entrustProcessOne处理委托耗时：" + (System.currentTimeMillis() - start));
////                if (i == Info.DueEntrustSuccess || i == Info.DueEntrustSuccessUnDo) {//更新内存数据
////
////                    //SystemStatus.moneyNewWork=true;
////                    //SystemStatus.chartDataNewWork=true;
////                    SystemStatus.setSystemStatus(m.market + "_" + SystemStatus.moneyNewWork, true);
////                    SystemStatus.setSystemStatus(m.market + "_" + SystemStatus.chartDataNewWork, true);
////                    //SystemStatus.exchangeNewWork=true;
////                }
////                //通知更新委托视图
////                return i;
////            } else {
////                Data.Update("update entrust set status=2 where entrustId=?", new Object[]{+beb.getEntrustId()});
////                log.error("有一个异常的type为" + beb.getTypes() + "命令！");
////                return Info.DueError;
////            }
////
////        } catch (Exception ex) {
////            log.error(ex.toString(), ex);
////            return Info.DueEntrustFaildUnKonwType;
////        }
////    }
////
////
////    public static Info entrust(EntrustBean beb, Market m) {
////
////        //如果执行期间已经有一个取消任务了，那么取消任务
//////		EntrustBean bebCancle = (EntrustBean) Data.GetOne("select * from entrust where freezeId=? and types=-1 and status=0",
//////						new Object[] { beb.getEntrustId() }, EntrustBean.class);
////        log.info("==========================" + beb.getTypes() + "========================");
////
////        while (true) {
////            long s1 = System.currentTimeMillis();
////            // 当前剩余总资金
////            //long totalMoney = beb.getTotalMoney()-beb.getCompleteTotalMoney();
////            //当前剩余数量
////            BigDecimal numbers = beb.getNumbers().subtract(beb.getCompleteNumber());// 对于购买
////            String sql = "";
////            //找到未完全处理或者已经完全处理过的
////            //if (bebCancle != null&&beb.getTypes()==1)
////            //  sql = "select * from Entrust where unitPrice<=? and EntrustId<" + bebCancle.getEntrustId()+" and types=0 and CompleteNumber<numbers and (status=0 or status=3)  limit 0,1";
////            //else if (bebCancle != null&&beb.getTypes()==0)
////            //	sql = "select * from Entrust where unitPrice>=? and EntrustId<" + bebCancle.getEntrustId()+" and types=1 and CompleteNumber<numbers and (status=0 or status=3)   limit 0,1";
////            if (beb.getTypes() == 1)//买
////                sql = "select * from entrust where unitPrice<=? and entrustId<" + beb.getEntrustId() + " and types=0 and completeNumber<numbers and status=3  order by unitPrice asc,entrustId asc  limit 0,1";
////            else if (beb.getTypes() == 0)//卖
////                sql = "select * from entrust where unitPrice>=? and entrustId<" + beb.getEntrustId() + " and types=1 and completeNumber<numbers and status=3  order by unitPrice desc,entrustId asc limit 0,1";
////            else
////                return Info.DueEntrustFaildUnKonwType;
////
////            //找到第一个合适的价格
////            EntrustBean entrusts = (EntrustBean) Data.GetOne(m.db, sql, new Object[]{
////                    beb.getUnitPrice()
////            }, EntrustBean.class);
////
////            log.info("......处理委托任务查询耗时：" + (System.currentTimeMillis() - s1) + ",sqls:" + sql);
////
////            if (entrusts == null) {//如果没有，直接更新当前的状态，然后返回
////                //说明没有符合条件的记录，更新记录为已经部分成交（其实本次成交为0）
////                Data.Update(m.db, "update entrust set status=3 where entrustId=?", new Object[]{beb.getEntrustId()});
////                da.getTop(beb.getUserId(), m);
////                //da.UpdateEntrustNo(beb.getUnitPrice(),beb.getNumbers().subtract(beb.getCompleteNumber()),beb.getTypes());
////                return Info.DueEntrustSuccessUnDo;
////            }
////
////            //说明可以成交，测试是否有计划委托，如果有就更新并跳出本次循环更改计划委托状态，从而让计划委托提前进入交易流程
////            //如果自己的价格有重叠会跳过计划委托，等待别人成交
////            //对于卖，说明价格下降，寻找卖出的计划委托抢先卖
////            String sqlPlanWhere = "  where status=-1 and types=0 and unitPrice>=" + entrusts.getUnitPrice();//and userid<>"+entrusts.getUserId()+"  方向一致 不影响
////            if (beb.getTypes() == 1)//对于买入 这里的类型是traicker的类型 其他的id是maker类型id
////                sqlPlanWhere = "  where status=-1 and types=1  and unitPrice<=" + entrusts.getUnitPrice();
////
////            EntrustBean entrustsPlan = (EntrustBean) Data.GetOne(m.db, "select * from entrust " + sqlPlanWhere + " limit 0,1", new Object[]{}, EntrustBean.class);
////            if (entrustsPlan != null) {//说明有
////                int co = Data.Update(m.db, "update entrust set status=0 " + sqlPlanWhere, new Object[]{});
////                log.info("激活" + co + "个计划委托");
////                ///return Info.DueEntrustSuccessUnDo;//这里直接返回就好了能执行就执行，不能执行就不管了 计划委托仅仅进行到这一步
////                //这里不不能停必须让当前委托完成
////            }
////
////            BigDecimal nextNumbers = entrusts.getNumbers().subtract(entrusts.getCompleteNumber());
////            // 本次交易的btc
////            BigDecimal thisNumbers = numbers;
////            if (numbers.compareTo(nextNumbers) > 0) {
////                thisNumbers = nextNumbers;
////            }
////            //本次交易的钱
////            BigDecimal thisMoney = Market.formatTotalMoney(entrusts.getUnitPrice(), thisNumbers);
////            List<OneSql> sqls = new ArrayList<OneSql>();
////
////            int status0 = 3;
////            if (numbers.compareTo(nextNumbers) <= 0)
////                status0 = 2;
////            //更改当前tricker委托方的状态
////
////            /***
////             * + status in(0,3) 2014-10-11 21:21   填补漏洞 ，否则可能出现取消的委托被生成  transrecord
////             */
////            sqls.add(new OneSql(
////                    "update entrust set status=?,completeNumber=completeNumber+?,completeTotalMoney=completeTotalMoney+? where entrustId=? and completeNumber+?<=numbers and status in(0,3)",
////                    1, new Object[]{status0, thisNumbers, thisMoney, beb.getEntrustId(), thisNumbers}, m.db));
////
////            int status = 3;
////            if (numbers.compareTo(nextNumbers) >= 0)
////                status = 2;
////            //更改被动原maker委托方的状态
////            sqls.add(new OneSql(
////                    "update entrust set status=?,completeNumber=completeNumber+?,completeTotalMoney=completeTotalMoney+? where entrustId=? and completeNumber+?<=numbers and status in(0,3)",
////                    1, new Object[]{status, thisNumbers, thisMoney, entrusts.getEntrustId(), thisNumbers}, m.db));
////
////            //产生记录
////            if (beb.getTypes() == 1) {
////                sqls.add(new OneSql(
////                        "INSERT INTO transrecord (unitPrice, totalPrice, numbers, entrustIdBuy, userIdBuy, entrustIdSell, userIdSell, types, times, timeMinute,webIdBuy,webIdSell,actStatus) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)",
////                        1, new Object[]{
////                        entrusts.getUnitPrice(),
////                        thisMoney,
////                        thisNumbers,
////                        beb.getEntrustId(),
////                        beb.getUserId(),
////                        entrusts.getEntrustId(),
////                        entrusts.getUserId(),
////                        beb.getTypes(),//当前记录是买行为还是卖行为
////                        TimeUtil.getNow().getTime(),
////                        TimeUtil.getMinuteFirst().getTime(),
////                        beb.getWebId(),
////                        entrusts.getWebId(),
////                        1
////                }));
////            } else {
////                sqls.add(new OneSql(
////                        "INSERT INTO transrecord (unitPrice, totalPrice, numbers, entrustIdBuy, userIdBuy, entrustIdSell, userIdSell, types, times, timeMinute,webIdBuy,webIdSell,actStatus) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)",
////                        1, new Object[]{
////                        entrusts.getUnitPrice(),
////                        thisMoney,
////                        thisNumbers,
////                        entrusts.getEntrustId(),
////                        entrusts.getUserId(),
////                        beb.getEntrustId(),
////                        beb.getUserId(),
////                        beb.getTypes(),//当前记录是买行为还是卖行为
////                        TimeUtil.getNow().getTime(),
////                        TimeUtil.getMinuteFirst().getTime(),
////                        entrusts.getWebId(),
////                        beb.getWebId(),
////                        1
////                }));
////            }
////            //上面问号的意思是没卖双方在这里可能没有判断好，可能引起问题，时间不足
////            long start = System.currentTimeMillis();
////            if (Data.doTrans(sqls)) {
////                long start1 = System.currentTimeMillis();
////                log.info("......处理委托任务的事物耗时:" + (start1 - start));
////                da.UpdateEntrust(entrusts.getUnitPrice(), thisNumbers, beb.getTypes(), m);
////
////                //更新卖一
////                List l = (List) Data.GetOne(m.db, "select max(transrecordId) from transrecord", new Object[]{});
////                long transrecordId = (l == null || l.get(0) == null) ? 0 : Long.parseLong(l.get(0).toString());
////                //更新队列
////                //da.updateRecord(transrecordId,entrusts.getUnitPrice(),thisNumbers,beb.getTypes(),TimeUtil.getNow().getTime());
////                //更新循环内存中的数据
////                beb.setCompleteNumber(beb.getCompleteNumber().add(thisNumbers));
////                beb.setCompleteTotalMoney(beb.getCompleteTotalMoney().add(thisMoney));
////                log.debug("执行一次交易成功");
////                log.info("......处理委托任务的事物耗时。。。后续任务耗时:" + (System.currentTimeMillis() - start1));
////                if (numbers.compareTo(thisNumbers) > 0) {//只要还有没买完毕的
////                    start1 = System.currentTimeMillis();
////                    da.getTop(entrusts.getUserId(), m);
////                    log.info("......处理委托任务的事物耗时getTop耗时:" + (System.currentTimeMillis() - start1));
////                    continue;
////                } else {
////                    start1 = System.currentTimeMillis();
////                    da.getTop(entrusts.getUserId(), m);
////                    da.getTop(beb.getUserId(), m);
////                    log.info("......处理委托任务的事物耗时else getTop耗时:" + (System.currentTimeMillis() - start1));
////                    return Info.DueEntrustSuccess;
////                }
////
////            } else {
////                log.debug("执行事物失败");
////                return Info.DueEntrustFaildProError;
////            }
////        }
////
////    }
////
////
////    /**
////     * 取消委托
////     * 委托表最终0 原始状态  1取消  2成功 3 交易一部分
////     *
////     * @param beb
////     * @return
////     */
////    public static Info cancle(EntrustBean beb, Market m) {
////        try {
////            EntrustBean bebNew = (EntrustBean) Data.GetOne(m.db, "select * from entrust where entrustId=?", new Object[]{beb.getFreezeId()}, EntrustBean.class);
////            if (bebNew == null) {
////                log.info("错误，发现一个没有原始记录的取消");
////                return Info.DueCancleFaildNoFouce;
////            }
////
////            //已经成功或者已经取消的时候 4已经处理过一次 5预取消  这里可以判定数量一定不能相等
////            if ((bebNew.getStatus() == 2 || bebNew.getStatus() == 1) || bebNew.getNumbers() == bebNew.getCompleteNumber()) {//只要不是未处理或者未完全处理，就无需处理 -1是计划委托类型
////                log.info("已经处理完毕，所以无需操作");
////                Data.Update(m.db, "update entrust set status=2 where entrustId=?", new Object[]{beb.getEntrustId()});
////                Data.Update(m.db, "update entrust set status=1 where entrustId=? and status<>2 and status<>-1", new Object[]{bebNew.getEntrustId()});
////                return Info.DueCancleFaildHasDued;
////            }
////
////            List<OneSql> sqls = new ArrayList<OneSql>();
////            //取消目标，到这里，说明前面的肯定处理过了，所以状态一定//,TotalMoney=CompleteTotalMoney
////            sqls.add(new OneSql("update entrust set status=1 where entrustId=?",
////                    1,
////                    new Object[]{bebNew.getEntrustId()},
////                    m.db
////            ));
////            //更新当前这个取消命令
////            sqls.add(new OneSql("update entrust set status=2 where entrustId=?",
////                    1,
////                    new Object[]{beb.getEntrustId()},
////                    m.db
////            ));
////            //已经交易完毕并且没有需要取消的
////            if (bebNew.getTotalMoney() == bebNew.getCompleteTotalMoney()) {
////                Data.Update(m.db, "update entrust set status=2 where entrustId=?",
////                        new Object[]{beb.getEntrustId()}
////                );
////                log.error("已经处理完毕资金，还要求取消：" + bebNew.getTotalMoney() + ":" + bebNew.getCompleteTotalMoney());
////                return Info.DueCancleFaildHasDued;
////            }
////            //产生记录
////            if (bebNew.getTypes() == 1) {
////                Object obj = Data.GetOne(m.db, "select * from transrecord where unitPrice=0 and numbers=0 and entrustIdBuy=?", new Object[]{bebNew.getEntrustId()});
////                if (obj == null) {//对于买会存在多冻结的问题，所以可能已经产生了返还命令，然后这边如果再取消，相当与就多了一次
////                    sqls.add(new OneSql(
////                            "INSERT INTO transrecord (unitPrice, totalPrice, numbers, entrustIdBuy, userIdBuy, entrustIdSell, userIdSell, types, times, timeMinute,webIdBuy,webIdSell) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
////                            1, new Object[]{
////                            0,
////                            bebNew.getTotalMoney().subtract(bebNew.getCompleteTotalMoney()),//剩余的所有钱   可能出现负职，因为总资金四舍五入的问题
////                            bebNew.getNumbers().subtract(bebNew.getCompleteNumber()),//剩余的所有都取消
////                            bebNew.getEntrustId(),//卖卖记录一样的
////                            bebNew.getUserId(),
////                            0,//买卖记录一样的
////                            0,
////                            bebNew.getTypes(),
////                            TimeUtil.getNow().getTime(),
////                            TimeUtil.getMinuteFirst().getTime(),
////                            bebNew.getWebId(),
////                            0
////                    }, m.db));
////                }
////
////            } else if (bebNew.getTypes() == 0) {
////                sqls.add(new OneSql(
////                        "INSERT INTO transrecord (unitPrice, totalPrice, numbers, entrustIdBuy, userIdBuy, entrustIdSell, userIdSell, types, times, timeMinute,webIdBuy,webIdSell) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
////                        1, new Object[]{
////                        0,
////                        0,//剩余的所有钱买房
////                        bebNew.getNumbers().subtract(bebNew.getCompleteNumber()),//剩余的所有都取消
////                        0,//卖卖记录一样的
////                        0,
////                        bebNew.getEntrustId(),//买卖记录一样的
////                        bebNew.getUserId(),
////                        bebNew.getTypes(),
////                        TimeUtil.getNow().getTime(),
////                        TimeUtil.getMinuteFirst().getTime(),
////                        0,
////                        bebNew.getWebId()
////                }, m.db));
////            }
////
////            if (Data.doTrans(sqls)) {
////                da.getTop(beb.getUserId(), m);
////                //da.updateCancleEntrust(bebNew.getUnitPrice(), bebNew.getNumbers()-bebNew.getCompleteNumber(),bebNew.getTypes()==1);
////                return Info.DueCancleSuccess;
////            } else {
////                return Info.DueCancleFaildPromError;
////            }
////        } catch (Exception ex) {
////            return Info.DueCancleFaildPromError;
////        }
////    }
//
//}
