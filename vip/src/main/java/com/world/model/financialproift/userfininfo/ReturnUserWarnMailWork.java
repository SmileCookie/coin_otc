package com.world.model.financialproift.userfininfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.dao.task.Worker;
import com.world.model.entity.financialproift.FinUserReturnOrderInfo;
import com.world.model.entity.financialproift.FinancialTask;
import com.world.util.financialproift.FinancialProiftUtils;

public class ReturnUserWarnMailWork extends Worker {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /*查询SQL*/
    private String sql = "";

    /*此轮定时任务结束标识*/
    private static boolean workFlag = true;
    /*日志打印计数器执行10次打印一次*/
    private static int intLogInfoFlag = 1;

    public ReturnUserWarnMailWork(String name, String des) {
        super(name, des);
    }

    @SuppressWarnings("unused")
    @Override
    public void run() {
        /*记录核算开始时间*/
        long startTime = System.currentTimeMillis();
        FinancialTask financialTask;
        /*现在时间获取*/
        if (intLogInfoFlag == 10) {
            log.info("理财报警REWARDINFO:【回本未复投用户邮件提醒-3天】开始");
        }
        //任务是否可执行
        boolean taskflag = false;

        try {
            /**
             * 取得类名称，作为任务名称
             */
            sql = "select * from fin_task where taskName = 'ReturnUserWarnMailWork' and taskTime <= NOW() and taskFlag = 1 ";
            log.info("HierarchyWork sql = " + sql);
            financialTask = (FinancialTask) Data.GetOne("vip_financial", sql, null, FinancialTask.class);
            if (null == financialTask) {
                if (intLogInfoFlag == 10) {
                    log.info("理财报警REWARDINFO:【回本未复投用户邮件提醒-3天】没有需要执行的任务");
                }
                return;
            } else {
                if (1 == financialTask.getTaskFlag()) {
                    /*可以执行*/
                    taskflag = true;
                } else {
                    taskflag = false;
                    if (intLogInfoFlag == 10) {
                        log.info("理财报警REWARDINFO:【回本未复投用户邮件提醒-3天】没有需要执行的任务，任务状态 = " + financialTask.getTaskFlag());
                    }
                    return;
                }
            }
        } catch (Exception e1) {
            log.info("理财报警REWARDERROR:【回本未复投用户邮件提醒-3天】任务配置初始化失败", e1);
            return;
        } finally {
            if (intLogInfoFlag == 10) {
                intLogInfoFlag = 0;
            }
            intLogInfoFlag++;
        }

        if (workFlag && taskflag) {
            workFlag = false;
            //执行成功数量
            int successNum = 0;
            try {
                /**
                 * 参照 ReturnUserOrderWork 编写
                 */
                FinUserReturnOrderInfo finUserReturnOrderInfo = new FinUserReturnOrderInfo();
                sql = "select userName,userId,investAmount,investAvergPrice,expectProfitUsdt,"
                        + "staticProfitSumUsdt,profitTime,resetProfitTime,createTime as userName"
                        + " from fin_userreturnorderinfo where dealFlag = 1 and authPayFlag = 3 "
                        + "and DATE_FORMAT(createTime,'%Y-%m-%d') = date_sub(DATE_FORMAT(now(),'%Y-%m-%d') ,interval 4 day) ";
                List<Bean> listUserFinancialInfos = (List<Bean>) Data.Query("vip_financial", sql, null, FinUserReturnOrderInfo.class);
                log.info("listUserFinancialInfos.size() = " + listUserFinancialInfos.size());
                if (null == listUserFinancialInfos) {
                    log.info("理财报警REWARDTASK:【回本未复投用户邮件提醒-3天】本轮没有需要发送的用户");
                    return;
                } else {
                    for (int i = 0; i < listUserFinancialInfos.size(); i++) {
                        try {
                            finUserReturnOrderInfo = (FinUserReturnOrderInfo) listUserFinancialInfos.get(i);
                            /*发送邮件*/
                            FinancialProiftUtils.sendUserReturnCapitalMsg(finUserReturnOrderInfo.getUserId() + "",
                                    finUserReturnOrderInfo.getInvestAmount(), finUserReturnOrderInfo.getInvestAvergPrice(),
                                    finUserReturnOrderInfo.getExpectProfitUsdt(),
                                    finUserReturnOrderInfo.getStaticProfitSumUsdt(), finUserReturnOrderInfo.getProfitTime(),
                                    finUserReturnOrderInfo.getResetProfitTime(), new Date(), "3");
                            successNum++;
                        } catch (Exception e) {
                            log.info("理财报警REWARDTASK:【回本未复投用户邮件提醒-3天】发送邮件失败", e);
                        }
                    }
                }
                long endTime = System.currentTimeMillis();
                log.info("理财报警REWARDTASK:【回本未复投用户邮件提醒-3天】结束!!!【核算耗时：{" + (endTime - startTime) + "}】,发送数量 = " + successNum);
            } catch (Exception e) {
                log.info("理财报警REWARDTASK:【回本未复投用户邮件提醒-3天】", e);
            } finally {
                // 记录任务日志(失败)
                updateTask(financialTask, startTime, 1);
                workFlag = true;
            }
        } else {
            log.info("理财报警REWARDTASK:【回本未复投用户邮件提醒-3天】上一轮分配任务尚未结束，本轮不需要进行");
        }
    }

    private void updateTask(FinancialTask financialTask, long startTime, int taskFlag) {
        TransactionObject txObj = new TransactionObject();
        List<OneSql> sqls = new ArrayList<>();
        String msg = "";
        if (taskFlag == 0) {
            msg = "动态奖金分配明细生成成功";
        } else {
            msg = "动态奖金分配明细生成失败";
        }
        /* 更新任务执行状态 , taskFlag = 0 */
        sql = "update fin_task set handleTime=now(),nowStep = 1, taskError=" + taskFlag
                + " where taskName='" + financialTask.getTaskName() + "' ";
        log.info("NewUserDetailWork sql = " + sql);
        sqls.add(new OneSql(sql, 1, null, "vip_financial"));
        /* 插入任务执行日志 */
        sql = "insert into fin_task_logs (taskName,taskType,taskTime,taskIndex,sumStep,nowStep,taskStartTime,taskEndTime,taskResult,resultInfo) "
                + "values('" + financialTask.getTaskName() + "'," + financialTask.getTaskType() + ",'"
                + financialTask.getTaskTime() + "'," + financialTask.getTaskIndex() + "," + financialTask.getSumStep()
                + "," + financialTask.getSumStep() + ",from_unixtime(" + startTime + "),now()," + taskFlag + ",'"
                + msg + "')";
        log.info("NewUserDetailWork sql = " + sql);
        sqls.add(new OneSql(sql, 1, null, "vip_financial"));
        txObj.excuteUpdateList(sqls);

        if (txObj.commit()) {
            log.info("理财报警REWARDINFO:任务【" + financialTask.getTaskName() + "】执行状态更新成功");
        } else {
            log.info("理财报警REWARDINFO:任务【" + financialTask.getTaskName() + "】执行状态更新失败");
        }
    }

    public static void main(String[] args) {
        ReturnUserWarnMailWork returnUserOrderWork = new ReturnUserWarnMailWork("", "");
        returnUserOrderWork.run();
    }

}
