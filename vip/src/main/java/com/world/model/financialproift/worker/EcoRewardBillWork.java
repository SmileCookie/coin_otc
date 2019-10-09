package com.world.model.financialproift.worker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.dao.task.Worker;
import com.world.model.entity.financialproift.FinProfitAssignDetail;
import com.world.model.entity.financialproift.FinancialTask;
import com.world.model.financialproift.thread.EcoRewardBillThread;
import com.world.util.date.TimeUtil;

public class EcoRewardBillWork extends Worker {

    private static Logger log = LoggerFactory.getLogger(EcoRewardBillWork.class);

    private static final long serialVersionUID = 1L;
    private static boolean workFlag = true;/*此轮定时任务结束标识*/
    FinancialTask financialTask;//查询定时任务
    private static String taskName = "EcoRewardBillWork";
    /*展示批量耗时的标识*/
    private boolean showInfoFlag = false;
    /*日志打印计数器执行10次打印一次*/
    private static int intLogInfoFlag = 1;
    
    public EcoRewardBillWork(String name, String des) {
        super(name, des);
    }

    @Override
    public void run() {
    	//任务是否可执行
    	boolean taskflag = false;
    	showInfoFlag = false;
        /*1、记录VDS生态奖励分配定时任务开始时间*/
        long startTime = System.currentTimeMillis();
        if (intLogInfoFlag == 10) {
        	log.info("理财报警REWARDINFO:【VDS生态奖励分配-结算】开始,startTime={}");
        }
        /*2、获取 fin_task 表 VDS生态奖励结算定时任务可否启动标记*/
        try {
            /*取得类名称，作为任务名称*/
            String sql = "select * from fin_task where taskName = 'EcoRewardBillWork' and taskTime <= NOW() and taskFlag = 1 ";
            log.info("EcoRewardBillWork sql = " + sql);
            financialTask = (FinancialTask) Data.GetOne("vip_financial", sql, null, FinancialTask.class);
            //判断有无配置该定时任务
            if (financialTask == null) {
            	if (intLogInfoFlag == 10) {
            		log.info("理财报警REWARDINFO【VDS生态奖励分配-结算】fin_task未配置定时任务:EcoRewardBillWork");
            	}
                return;
            }
            //查询定时任务是否可执行
            if (financialTask.getTaskFlag() != 1) {
            	if (intLogInfoFlag == 10) {
            		log.info("理财报警REWARDINFO【VDS生态奖励分配-结算】定时任务 EcoRewardBillWork 为不可执行状态");
            		insertExceptionLog("定时任务 EcoRewardBillWork 为不可执行状态");
            	}
                return;
            }
            //所有的限制条件都判断后，设置为可执行标记
            taskflag = true;
        } catch (Exception e1) {
            insertExceptionLog("定时任务启动失败:EcoRewardBillWork - Exception");
            log.info("理财报警REWARDERROR【VDS生态奖励分配-结算】定时任务启动失败:EcoRewardBillWork", e1);
            return;
        } finally {
        	if (intLogInfoFlag == 10) {
        		intLogInfoFlag = 0;
        	}
        	intLogInfoFlag++;
        }
        
        /*3、当且仅当上轮定时任务已结束且定时任务启动标记为true时，执行本轮定时任务*/
        if (workFlag && taskflag) {
            workFlag = false;
            try {
                /**
                 * 3.1 查询 fin_profit_assign_detail 表中
                 * 结算状态 flag = 0 未结算
                 * 收益类型 profitType = 'EcoRewardAssignWork' 的数据进行结算
                 *   (查询条件：flag 0 未结算 1 已结算 profitType 收益类型：6 VDS生态奖励)
                 *   按照 profitUserid group by
                 *   最终统计的金额要大于触发值
                 */
                String sql = "select * from fin_profit_assign_detail where flag = 0 and profitType = 6 ";
                log.info("EcoRewardBillWork sql = " + sql);
                
                List<Bean> assignList = (List<Bean>) Data.Query("vip_financial", sql, null, FinProfitAssignDetail.class);
                if (null != assignList) {
                	log.info("理财报警REWARDTASK:【VDS生态奖励分配-结算】定时任务可以继续执行,本次分配总用户人数 = " + assignList.size());
                } else {
                	if (intLogInfoFlag == 10) {
                		log.info("理财报警REWARDTASK:【VDS生态奖励分配-结算】定时任务继续执行,本次没有需要分配的用户");
                	}
                	return;
                }
                log.info("assignList.size = " + assignList.size());
                Map<String, FinProfitAssignDetail> rewardMap = new HashMap<String, FinProfitAssignDetail>();
                for (int i = 0; i < assignList.size(); i++) {
                    FinProfitAssignDetail currentDetail = (FinProfitAssignDetail) assignList.get(i);
                    String userId = String.valueOf(currentDetail.getProfituserid());
                    if (rewardMap.containsKey(userId)) {
                        FinProfitAssignDetail oldDetail = rewardMap.get(userId);
                        oldDetail.setProfituserid(currentDetail.getProfituserid());//奖励人ID
                        /*获奖人用户名*/
                        oldDetail.setProfitusername(currentDetail.getProfitusername());
                        /*获奖人VID*/
                        oldDetail.setProfitvid(currentDetail.getProfitvid());
                        oldDetail.setProfitamount(oldDetail.getProfitamount().add(currentDetail.getProfitamount()));//奖励金额
                        oldDetail.setUsdtamount(oldDetail.getUsdtamount().add(currentDetail.getUsdtamount()));//折合USDT金额
                        oldDetail.setAssignIds(oldDetail.getAssignIds() + currentDetail.getId() + ",");//分配表来源ID
                        rewardMap.put(userId, oldDetail);
                    } else {
                        FinProfitAssignDetail detail = new FinProfitAssignDetail();
                        detail.setProfituserid(currentDetail.getProfituserid());
                        /*获奖人用户名*/
                        detail.setProfitusername(currentDetail.getProfitusername());
                        /*获奖人VID*/
                        detail.setProfitvid(currentDetail.getProfitvid());
                        detail.setProfitamount(currentDetail.getProfitamount());
                        detail.setUsdtamount(currentDetail.getUsdtamount());
                        detail.setAssignIds(currentDetail.getId() + ",");
                        rewardMap.put(userId, detail);
                    }
                }

//                log.info(JSON.toJSONString(rewardMap));

                //判断金额是否大于触发值，满足条件参与结算
                List<String> userIds = new LinkedList<String>();
                Map<String, FinProfitAssignDetail> dealMap = new HashMap<String, FinProfitAssignDetail>();
                Iterator<Map.Entry<String, FinProfitAssignDetail>> iterator = rewardMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, FinProfitAssignDetail> next = iterator.next();
                    String userId = next.getKey();
                    FinProfitAssignDetail detail = next.getValue();
                    //大于等于触发值
                    if (detail.getProfitamount().compareTo(financialTask.getTriggerVal()) == 1 || detail.getProfitamount().compareTo(financialTask.getTriggerVal()) == 0) {
                        userIds.add(userId);
                        dealMap.put(userId,detail);
                    } else {
//                    	log.info("金额小于触发金额 = " + financialTask.getTriggerVal() + ", = " + detail.getProfitamount());
                    }
                }
                
                if (null != dealMap && dealMap.size() > 0) {
                	/* 3.2、多线程处理生态收益分配结算任务，每处理一条，开启1个事务*/
                    ExecutorService es = Executors.newFixedThreadPool(1);
                    CountDownLatch countDownLatch = new CountDownLatch(dealMap.size());
                    for (int i = 0; i < userIds.size(); i++) {
                        EcoRewardBillThread ecoRewardBillThread = new EcoRewardBillThread((FinProfitAssignDetail) dealMap.get(userIds.get(i)), countDownLatch);
                        es.execute(ecoRewardBillThread);
                    }
                    countDownLatch.await();
                    es.shutdown();
                    showInfoFlag = true;
                } else {
                	log.info("理财报警REWARDTASK:【VDS生态奖励分配-结算】定时任务继续执行,本次需要分配的用户没有达到分配值 = " + financialTask.getTriggerVal());
                	return;
                }
               
                /*3.3 多线程任务完结后，事务记录定时任务执行的日志*/
                List<OneSql> sqls = new ArrayList<>();
                TransactionObject txObj = new TransactionObject();
                /*更新任务执行状态 taskFlag=0,*/
                /**
                 * 如果超过预设值的时间切换成分配
                 * EcoRewardAssignWork - VDS生态12层投资收益分配
                 * EcoRewardBillWork - VDS生态12层投资收益发放
                 */
                sql = "update fin_task set handleTime = now(), taskError = 1, taskFlag = 0 where taskName = 'EcoRewardBillWork' ";
                log.info("EcoRewardAssignWork sql = " + sql);
                sqls.add(new OneSql(sql, 1, null, "vip_financial"));
                /**
                 * 切换分配任务开启
            	 * 计算需要补充的资金
            	 */
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            	String distEndTime = sdf.format(TimeUtil.getBeforeTime(7)) + " 05:00:00";
                sql = "update fin_task set taskFlag = 1, distEndTime = '" + distEndTime + "' where taskName = 'EcoRewardAssignWork' ";
            	log.info("sql = " + sql);
            	sqls.add(new OneSql(sql, 1, null, "vip_financial"));
                
                /*插入任务执行日志*/
                sql = "insert into fin_task_logs (taskName,taskType,taskTime,taskIndex,taskStartTime,taskEndTime,taskResult,resultInfo) values('"
                        + financialTask.getTaskName() + "',1,now(),1,'"
                        + TimeUtil.getNow() + "',"
                        + "now(),"
                        + "1,"
                        + "'执行成功'"
                        + ")";
                log.info("EcoRewardAssignWork sql = " + sql);
                sqls.add(new OneSql(sql, 1, null, "vip_financial"));
                txObj.excuteUpdateList(sqls);
                if (txObj.commit()) {
                    log.info("理财报警REWARDINFO:任务【VDS生态奖励分配-结算】执行状态更新成功");
                } else {
                    log.info("理财报警REWARDERROR:任务【VDS生态奖励分配-结算】执行状态更新失败");
                }
                
                /*4、记录VDS生态奖励分配定时任务结束时间*/
                long endTime = System.currentTimeMillis();
                if (showInfoFlag) {
                	log.info("理财报警REWARDTASK:【VDS生态奖励分配-结算】结束!!!【核算耗时：{" + (endTime - startTime) + "}】");
                }
            } catch (Exception e) {
                log.info("理财报警REWARDERROR:【VDS生态奖励分配-结算】EcoRewardBillWork", e);
                insertExceptionLog("定时任务执行失败:EcoRewardBillWork - Exception");
            } finally {
                workFlag = true;
            }
        } else {
        	log.info("理财报警REWARDTASK:【VDS生态奖励分配-结算】上一轮分配任务尚未结束，本轮不需要进行");
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
        String sql = "update fin_task set taskFlag=0,handleTime=now(),taskError=2 where taskName='" + taskName + "' ";
        sqls.add(new OneSql(sql, 1, null, "vip_financial"));
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
        EcoRewardBillWork ecoRewardBillWork = new EcoRewardBillWork("", "");
        ecoRewardBillWork.run();
    }

}
