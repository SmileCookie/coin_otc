package com.world.model.statisticalreport;

import com.world.constant.Const;
import com.world.model.dao.bill.BillDetailDao;
import com.world.model.dao.reconciliation.ReconciliationDao;
import com.world.model.dao.task.Worker;
import com.world.model.entity.reconciliation.Reconciliation;
import com.world.model.entity.statisticalReport.*;
import com.world.model.statisticalreport.dao.*;
import com.world.util.date.TimeUtil;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.world.constant.Const.*;

/**
 * <p>标题: 数据计算</p>上tat
 * <p>描述: 计算bill表中数据</p>
 * <p>版权: Copyright (c) 2017</p>
 *
 * @author chendi
 */
public class StatisticalReportWork extends Worker {
    private static final long serialVersionUID = 1L;
    /*上次更新时间默认为当天,即明天凌晨执行数据同步; 如果要改为前天:TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(1));*/
    private Timestamp lastUpdateTime = TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(-1));
    TimerLogDao timerLogDao = new TimerLogDao();

    public StatisticalReportWork(String name, String des) {
        super(name, des);
    }

    BillDetailDao billDetailDao = new BillDetailDao();
    BillCountDao billCountDao = new BillCountDao();
    BillAllCountDao billAllCountDao = new BillAllCountDao();
    AliveUserDataCountDao aliveUserDataCountDao = new AliveUserDataCountDao();
    AliveUserCountDao aliveUserCountDao = new AliveUserCountDao();
    ReconciliationDao reconciliationDao = new ReconciliationDao();

    @Override
    public void run() {
        try {

            /*时间控制*/
            Timestamp tsTodayTime = TimeUtil.getTodayFirst();
            //统计每日的用户资金
            Timestamp tsTodayLastTime = TimeUtil.getTodayLast();
            Date nowDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            /*现在时间获取*/
            String strNowTime = sdf.format(nowDate);
            log.info("StatisticalReportWork...strNowTime = " + strNowTime + ", tsTodayTime = " + tsTodayTime + ", lastUpdateTime = " + lastUpdateTime);
            List<TimerLog> mysqltLog = timerLogDao.getList(sdf.format(tsTodayTime), sdf.format(tsTodayLastTime), Const.Mysql_Transfer);
            List<TimerLog> mongoUsertLog = timerLogDao.getList(sdf.format(tsTodayTime), sdf.format(tsTodayLastTime), Const.Mongo_User_Import);
            List<TimerLog> mongotLog = timerLogDao.getList(sdf.format(tsTodayTime), sdf.format(tsTodayLastTime), Const.Mongo_UserLoginIp_Import);
            List<TimerLog> billWalletLog = timerLogDao.getList(sdf.format(tsTodayTime), sdf.format(tsTodayLastTime), Const.bill_wallet_Transfer);
            List<TimerLog> billOtcLog = timerLogDao.getList(sdf.format(tsTodayTime), sdf.format(tsTodayLastTime), Const.bill_otc_Transfer);
            if (!CollectionUtils.isEmpty(mysqltLog)
                    && !CollectionUtils.isEmpty(mongotLog)
                    && !CollectionUtils.isEmpty(mongoUsertLog)
                    && !CollectionUtils.isEmpty(billWalletLog)
                    && !CollectionUtils.isEmpty(billOtcLog)) {

                //统计平台资金日报
                List<TimerLog> billCountLog = timerLogDao.getList(sdf.format(tsTodayTime), sdf.format(tsTodayLastTime), Const.Bill_Count);
                if (CollectionUtils.isEmpty(billCountLog)) {
                    log.info("开始统计平台资金日报表.");
                    List<BillCount> billCountList = billDetailDao.sumBillCount(TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(-1)), tsTodayTime);
                    if (!CollectionUtils.isEmpty(billCountList)) {
                        //计算昨天的资金统计并插入到billcout表中
                        log.info("billDetailDao.sumBillCount.查询结束,执行saveBillCount保存执行结果.");
                        Boolean flag = billCountDao.saveBillCount(billCountList, tsTodayTime, tsTodayLastTime);
                        if (flag) {
                            log.info("billCountDao.saveBillCount成功,插入定时任务日志.");
                            timerLogDao.insert(sdf.format(nowDate), sdf.format(new Date()), Bill_Count, billCountList.size());
                        }
                    }
                }
                log.info("billCount.统计结束.");

                //统计平台资金累计
                List<TimerLog> billAllCountLog = timerLogDao.getList(sdf.format(tsTodayTime), sdf.format(tsTodayLastTime), Const.Bill_ALL_Count);
                if (CollectionUtils.isEmpty(billAllCountLog)) {
                    //计算累计到昨天的资金统计并插入到billAllcout表中
                    log.info("开始统计平台资金累计表.");
                    List<BillAllCount> billCountList = billCountDao.sumBillAllCount();
                    if (!CollectionUtils.isEmpty(billCountList)) {
                        //计算昨天的资金统计并插入到billcout表中
                        log.info("billCountDao.sumBillAllCount.查询结束,执行saveBillAllCount保存执行结果.");
                        Boolean flag = billAllCountDao.saveBillAllCount(billCountList, tsTodayTime, tsTodayLastTime);
                        if (flag) {
                            log.info("billAllCountDao.saveBillAllCount成功,插入定时任务日志.");
                            timerLogDao.insert(sdf.format(nowDate), sdf.format(new Date()), Bill_ALL_Count, billCountList.size());
                        }
                    }
                }
                log.info("billAllCount.统计结束.");

                //统计活跃用户数据统计
                List<TimerLog> aliveUserDataCountLog = timerLogDao.getList(sdf.format(tsTodayTime), sdf.format(tsTodayLastTime), Const.All_User_Data_Count);
                if (CollectionUtils.isEmpty(aliveUserDataCountLog)) {
                    //计算累计到昨天的活跃用户数据信息并插入到aliveUserDataCount表中
                    AliveUserDataCount aliveUserDataCount = billDetailDao.sumAliveUserData(TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(-1)), tsTodayTime);
                    Boolean flag = aliveUserDataCountDao.saveBillAllCount(aliveUserDataCount, tsTodayTime, tsTodayLastTime);
                    if (flag) {
                        timerLogDao.insert(sdf.format(nowDate), sdf.format(new Date()), All_User_Data_Count, 1);
                    }
                }
                //活跃用户统计
                List<TimerLog> aliveUserCountLog = timerLogDao.getList(sdf.format(tsTodayTime), sdf.format(tsTodayLastTime), Const.All_User_Count);
                if (CollectionUtils.isEmpty(aliveUserCountLog)) {
                    //计算累计到昨天的用户信息并插入到aliveUserCount表中
                    AliveUserCount aliveUserCount = aliveUserCountDao.sumAliveUserData(TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(-1)), tsTodayTime);
                    Boolean flag = aliveUserCountDao.saveBillAllCount(aliveUserCount, tsTodayTime, tsTodayLastTime);
                    if (flag) {
                        timerLogDao.insert(sdf.format(nowDate), sdf.format(new Date()), All_User_Count, 1);
                    }
                }
                //交易平台资金总账
                List<TimerLog> reconciliationLog = timerLogDao.getList(sdf.format(tsTodayTime), sdf.format(tsTodayLastTime), Const.Capital_Account);
                if (CollectionUtils.isEmpty(reconciliationLog)) {
                    List<Reconciliation> reconciliationList = reconciliationDao.getBatchThread(null, tsTodayTime);
                    List<Reconciliation> gbcIco = reconciliationDao.getGbcIcoExchange(null, tsTodayTime);
                    for (Reconciliation reconciliation : reconciliationList) {
                        if (reconciliation.getFundsType() == 9) {
                            reconciliation.setIcoExchange(gbcIco.get(0).getIcoExchange());
                        }
                    }
                    if (!CollectionUtils.isEmpty(reconciliationList)) {
                        Boolean flag = reconciliationDao.saveReconciliation(reconciliationList, tsTodayTime, tsTodayLastTime);
                        if (flag) {
                            timerLogDao.insert(sdf.format(nowDate), sdf.format(new Date()), Capital_Account, reconciliationList.size());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }

    }

    public static void main(String[] args) {
        StatisticalReportWork statisticalReportWork = new StatisticalReportWork("", "");
        statisticalReportWork.run();
    }

}
