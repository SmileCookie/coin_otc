package com.world.model.financialproift.worker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.world.cache.Cache;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.dao.task.Worker;
import com.world.model.entity.financialproift.FinancialTask;
import com.world.model.entity.financialproift.UserFinancialInfo;
import com.world.model.financialproift.thread.EcoRewardAssignThread;
import com.world.util.date.TimeUtil;

import me.chanjar.weixin.common.util.StringUtils;

public class EcoRewardAssignWork extends Worker {

    private static Logger log = LoggerFactory.getLogger(EcoRewardAssignWork.class);

    private static final long serialVersionUID = 1L;
    private static boolean workFlag = true;/*此轮定时任务结束标识*/
    FinancialTask financialTask;//查询定时任务
    private static String taskName = "EcoRewardAssignWork";
    /*展示批量耗时的标识*/
    private boolean showInfoFlag = false;
    /*日志打印计数器执行10次打印一次*/
    private static int intLogInfoFlag = 1;

    public EcoRewardAssignWork(String name, String des) {
        super(name, des);
    }

    @Override
    public void run() {
    	String sql = "";
    	//任务是否可执行
      	boolean taskflag = false;
    	showInfoFlag = false;
        /*1、记录VDS生态奖励分配定时任务开始时间*/
        long startTime = System.currentTimeMillis();
        if (intLogInfoFlag == 10) {
        	log.info("理财报警REWARDINFO:【VDS生态奖励分配】开始");
        }
        /*获取VDS当时对应的USDT市价*/
        BigDecimal profitUdstPrice = BigDecimal.ZERO;
        try {
            /*设置VDSUSDT价格*/
            String vdsUsdt = Cache.Get("vds_usdt_l_price");
            if (StringUtils.isEmpty(vdsUsdt)) {
                log.info("理财报警REWARDWARN:Cache获取vdsUsdt价格异常 = " + vdsUsdt);
                insertExceptionLog("Cache获取vdsUsdt价格异常");
                return;
            } else {
                try {
                    profitUdstPrice = new BigDecimal(vdsUsdt);
                } catch (Exception e) {
                    log.info("理财报警REWARDERROR:Cache获取vdsUsdt价格异常 = " + vdsUsdt);
                    insertExceptionLog("Cache获取vdsUsdt价格异常");
                    return;
                }
                if (profitUdstPrice.compareTo(BigDecimal.ZERO) <= 0) {
                    log.info("理财报警REWARDWARN:Cache获取vdsUsdt价格异常小于0 = " + vdsUsdt);
                    insertExceptionLog("Cache获取vdsUsdt价格异常小于0");
                    return;
                }
            }
        } catch (Exception e) {
            log.info("理财报警REWARDERROR:EcoRewardAssignWork-Cache获取vdsUsdt价格异常", e);
            insertExceptionLog("Cache获取vdsUsdt价格异常-Exception");
            return;
        }
        
        /*2、获取 fin_task 表 VDS生态奖励分配定时任务可否启动标记*/
        try {
            /*获取定时任务配置信息*/
            sql = "select * from fin_task where taskName = 'EcoRewardAssignWork' and taskTime <= NOW() and taskFlag = 1 ";
            log.info("EcoRewardAssignWork sql = " + sql);
            financialTask = (FinancialTask) Data.GetOne("vip_financial", sql, null, FinancialTask.class);
            //判断有无配置该定时任务
            if (financialTask == null) {
            	if (intLogInfoFlag == 10) {
            		log.info("理财报警REWARDINFO【VDS生态奖励分配】fin_task未配置定时任务:EcoRewardAssignWork");
            	}
                return;
            }
            //查询定时任务是否可执行
            if (financialTask.getTaskFlag() != 1) {
            	if (intLogInfoFlag == 10) {
            		log.info("理财报警REWARDINFO【VDS生态奖励分配】定时任务 EcoRewardAssignWork 为不可执行状态");
            		insertExceptionLog("定时任务 EcoRewardAssignWork 为不可执行状态");
            	}
                return;
            }
//            //判断是否配置了执行周期
//            if (financialTask.getCallStartDate() == null || financialTask.getCallEndDate() == null) {
//                log.info("理财报警REWARDWARN【VDS生态奖励分配】定时任务 EcoRewardAssignWork 未配置执行周期");
//                insertExceptionLog("定时任务 EcoRewardAssignWork 未配置执行周期");
//                return;
//            }
            //所有的限制条件都判断后，设置为可执行标记
            taskflag = true;
            log.info("EcoRewardAssignWork taskflag:" + taskflag);
        } catch (Exception e1) {
            log.info("理财报警REWARDERROR【VDS生态奖励分配】定时任务启动失败:EcoRewardAssignWork", e1);
            insertExceptionLog("定时任务启动失败:EcoRewardAssignWork");
            return;
        } finally {
        	if (intLogInfoFlag == 10) {
        		intLogInfoFlag = 0;
        	}
        	intLogInfoFlag++;
        }
        /**
         * 如果超过预设值的时间切换成分配
         * EcoRewardAssignWork - VDS生态12层投资收益分配
         * EcoRewardBillWork - VDS生态12层投资收益发放
         */
        if (financialTask.getDistEndTime().compareTo(new Date()) < 0) {
        	List<OneSql> sqls = new ArrayList<>();
            TransactionObject txObj = new TransactionObject();
        	sql = "update fin_task set taskFlag = 0 where taskName = 'EcoRewardAssignWork' ";
        	log.info("sql = " + sql);
        	sqls.add(new OneSql(sql, 1, null, "vip_financial"));
        	
        	sql = "update fin_task set taskFlag = 1 where taskName = 'EcoRewardBillWork' ";
        	log.info("sql = " + sql);
        	sqls.add(new OneSql(sql, 1, null, "vip_financial"));
            txObj.excuteUpdateList(sqls);
            if (txObj.commit()) {
                log.info("理财报警REWARDTASK:任务【" + taskName + "】生态分配和结算切换成功");
            } else {
                log.info("理财报警REWARDERROR:任务【" + taskName + "】生态分配和结算切换失败");
            }
            return;
        }

        /*3、当且仅当上轮定时任务已结束且定时任务启动标记为true时，执行本轮定时任务*/
        if (workFlag && taskflag) {
            workFlag = false;
            try {
                /**
                 * 3.1、联查fin_productinvest投资理财记录表和fin_userfinancialinfo用户理财信息表获取用户userid/vid/投资金额/投资ID
                 *   (查询条件：2用户已支付 0未计算分配)
                 */
                sql = "select fu.userId userId , fu.userVID userVID,fp.investAmount investUsdtAmount,fp.id id " 
                	+ "from fin_productinvest fp left join fin_userfinancialinfo fu on fp.userId = fu.userId " 
                	+ "where fu.authPayFlag = 2 and fp.ecologySystemDealFlag = 0 and fu.userVID != '' ";
                log.info("EcoRewardAssignWork sql = " + sql);
                
                List<Bean> investList = (List<Bean>) Data.Query("vip_financial", sql, null, UserFinancialInfo.class);
                /* 3.2、多线程处理用户投资收益分配，每处理一条，开启1个事务*/
                ExecutorService es = Executors.newFixedThreadPool(1);
                if (null != investList && investList.size() > 0) {
                	log.info("理财报警REWARDINFO:【VDS生态奖励分配】定时任务,本次分配总人数 = " + investList.size());
                    CountDownLatch countDownLatch = new CountDownLatch(investList.size());
                    for (int i = 0; i < investList.size(); i++) {
                        EcoRewardAssignThread ecoRewardAssignThread = new EcoRewardAssignThread((UserFinancialInfo) investList.get(i), profitUdstPrice, countDownLatch);
                        es.execute(ecoRewardAssignThread);
                    }
                    countDownLatch.await();
                    showInfoFlag = true;
                } else {
                	if (intLogInfoFlag == 10) {
                		log.info("理财报警REWARDINFO:【VDS生态奖励分配】定时任务继续执行,本次没有需要分配的用户 sql = " + sql);
                	}
                }
                es.shutdown();
                
                /*4、记录VDS生态奖励分配定时任务结束时间*/
                long endTime = System.currentTimeMillis();
                if(showInfoFlag) {
                	log.info("理财报警REWARDINFO:【VDS生态奖励分配】结束!!!结束时间：" + endTime + "【核算耗时：{" + (endTime - startTime) + "}】");
                }
            } catch (Exception e) {
                insertExceptionLog("EcoRewardAssignWork - Exception");
                log.info("理财报警REWARDERROR:【VDS生态奖励分配】定时任务执行失败EcoRewardAssignWork", e);
            } finally {
                workFlag = true;
            }
        } else {
        	log.info("理财报警REWARDINFO:【VDS生态奖励分配】上一轮分配任务尚未结束，本轮不需要进行");
        }
    }

    /**
     * 记录异常日志
     *
     * @param resultInfo
     */
    public static void insertExceptionLog(String resultInfo) {
        List<OneSql> sqls = new ArrayList<>();
        TransactionObject txObj = new TransactionObject();
        String sql = "";
//        String sql = "update fin_task set taskFlag=0,handleTime=now(),taskError=2 where taskName='" + taskName + "' ";
//        sqls.add(new OneSql(sql, 1, null, "vip_financial"));
        sql = "insert into fin_task_logs (taskName,taskType,taskTime,taskIndex,taskStartTime,taskEndTime,taskResult,resultInfo) values('"
                + taskName + "',1,now(),1,'"
                + TimeUtil.getNow() + "',"
                + "now(),"
                + "2,"
                + "'" + resultInfo + "'"
                + ")";

        //执行失败，请查看执行日志
        sqls.add(new OneSql(sql, 1, null, "vip_financial"));
        txObj.excuteUpdateList(sqls);
        if (txObj.commit()) {
            log.info("理财报警REWARDINFO:任务【" + taskName + "】执行状态更新成功");
        } else {
            log.info("理财报警REWARDWARN:任务【" + taskName + "】执行状态更新失败");
        }
    }

    public static void main(String[] args) {
        EcoRewardAssignWork ecoRewardAssignWork = new EcoRewardAssignWork("", "");
        ecoRewardAssignWork.run();
//    	FinThreadTest ftt = new FinThreadTest();
//    	ftt.start();
    	 
//    	FinThreadTest ftt2 = new FinThreadTest();
//    	ftt2.start();
    }

}
