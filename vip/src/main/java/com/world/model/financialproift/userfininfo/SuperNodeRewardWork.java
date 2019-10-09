package com.world.model.financialproift.userfininfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.dao.task.Worker;
import com.world.model.entity.financialproift.FinProfitAssignDetail;
import com.world.model.entity.financialproift.FinancialTask;
import com.world.model.financialproift.thread.SuperNodeRewardThread;

/**
 * @Author Ethan
 * @Date 2019-07-30 15:24
 * @Description 超级节点奖励定时任务
 **/

public class SuperNodeRewardWork extends Worker {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private String sql = "";
    /**
     * 此轮定时任务结束标识
     */
    private static boolean workFlag = true;
    /*展示批量耗时的标识*/
    private boolean showInfoFlag = false;
    /*日志打印计数器执行10次打印一次*/
    private static int intLogInfoFlag = 1;
    
    public SuperNodeRewardWork(String name, String des) {
        super(name, des);
    }

    @Override
    public void run() {
    	if (intLogInfoFlag == 10) {
    		log.info("理财报警REWARDINFO:【VIP分红奖励-结算】开始");
    	}
        long startTime = System.currentTimeMillis();
        FinancialTask financialTask;
        boolean taskflag = false;
        showInfoFlag = false;
        try {
            /**
             * 取得类名称，作为任务名称
             * 【VIP分红奖励-结算】
             */
            sql = "select * from fin_task where taskName = 'SuperNodeRewardWork' and taskTime <= NOW() and taskFlag = 1 ";
            log.info("SuperNodeRewardWork sql = " + sql);
            financialTask = (FinancialTask) Data.GetOne("vip_financial", sql, null, FinancialTask.class);
			if (null == financialTask) {
				if (intLogInfoFlag == 10) {
					log.info("理财报警REWARDINFO:【VIP分红奖励-结算】没有需要执行的任务");
				}
				return;
			} else {
				if(1 == financialTask.getTaskFlag()) {
					/*可以执行*/
					taskflag = true;
				} else {
					taskflag = false;
					if (intLogInfoFlag == 10) {
						log.info("理财报警REWARDINFO:【VIP分红奖励-结算】没有需要执行的任务，任务状态 = " + financialTask.getTaskFlag());
					}
					return;
				}
			}
            log.info("SuperNodeRewardWork taskflag:" + taskflag);
        } catch (Exception e1) {
            financialTask = new FinancialTask();
            financialTask.setTaskName("SuperNodeRewardWork");
            log.info("理财报警REWARDERROR:【VIP分红奖励-结算】定时任务启动失败,失败原因:", e1);
        } finally {
        	if (intLogInfoFlag == 10) {
        		intLogInfoFlag = 0;
        	}
        	intLogInfoFlag++;
        }
        
        if (workFlag && taskflag) {
            workFlag = false;
            try {
                log.info("超级节点-用户奖励收益明细-分配，定时任务开始...");
                //多线程处理奖励分配
                sql = "select * from fin_profit_supernode_detail where flag = 0 and profitType = 5 ";
                log.info("SuperNodeRewardWork sql="+sql);
                List<FinProfitAssignDetail> profitAssignDetailList = Data.QueryT("vip_financial", sql, null, FinProfitAssignDetail.class);
                if (profitAssignDetailList != null && profitAssignDetailList.size() > 0) {
//                	log.info("理财报警REWARDTASK:【VIP分红奖励-结算】任务按照预期启动,开始进行记录结算 = " + profitAssignDetailList.size());
                    ExecutorService executorPool = Executors.newFixedThreadPool(1);
                    CountDownLatch countDownLatch = new CountDownLatch(profitAssignDetailList.size());
                    for (FinProfitAssignDetail profitAssignDetail : profitAssignDetailList) {
                        SuperNodeRewardThread thread = new SuperNodeRewardThread(profitAssignDetail, countDownLatch);
                        executorPool.execute(thread);
                    }
                    countDownLatch.await();
                    executorPool.shutdown();
                    showInfoFlag = true;
                } else {
                	if (intLogInfoFlag == 10) {
                		log.info("理财报警:【VIP分红奖励-结算】定时任务可以继续执行,本次没有需要分配的人员 sql = " + sql);
                	}
                	return;
                }
                if (intLogInfoFlag == 10) {
                	updateTask(financialTask, startTime, 0, 0);
                }
                long endTime = System.currentTimeMillis();
                if (showInfoFlag) {
                	log.info("理财报警REWARDTASK:【VIP分红奖励-结算】任务结束... 耗时：" + (endTime - startTime) + ", 分配记录总数 = " + profitAssignDetailList.size());
                }
            } catch (Exception e) {
                log.info("理财报警REWARDERROR:【VIP分红奖励-结算】发生不控异常,异常信息", e);
                updateTask(financialTask, startTime, 1,1);
            } finally {
                workFlag = true;
            }
        } else {
        	log.info("理财报警REWARDTASK:【VIP分红奖励-结算】上一轮分配任务尚未结束，本轮不需要进行");
        }

    }

    private void updateTask(FinancialTask financialTask, long startTime, int taskFlag, int taskError) {
        TransactionObject txObj = new TransactionObject();
        List<OneSql> sqls = new ArrayList<>();

        String taskInfo = "执行成功";
        if (taskFlag != 0)
            taskInfo = "执行失败，请查看日志记录";
        
        /* 更新任务执行状态 */
        sql = "update fin_task set handleTime = now(),nowStep = 1,taskError = " + taskError + " "
        	+ "where taskName = '" + financialTask.getTaskName() + "' ";
        log.info("SuperNodeProfitWork sql = " + sql);
        sqls.add(new OneSql(sql, 1, null, "vip_financial"));
        /* 插入任务执行日志 */
        sql = "insert into fin_task_logs (taskName,taskType,taskTime,taskIndex,sumStep,nowStep,taskStartTime,taskEndTime,taskResult,resultInfo) "
                + "values('" + financialTask.getTaskName() + "'," + financialTask.getTaskType() + ",'"
                + financialTask.getTaskTime() + "'," + financialTask.getTaskIndex() + "," + financialTask.getSumStep()
                + "," + financialTask.getSumStep() + ",from_unixtime(" + startTime + "),now()," + taskFlag + ",'"
                + taskInfo + "')";
        log.info("SuperNodeProfitWork sql = " + sql);
        sqls.add(new OneSql(sql, 1, null, "vip_financial"));
        txObj.excuteUpdateList(sqls);

        if (txObj.commit()) {
            log.info("理财报警REWARDINFO:任务【" + financialTask.getTaskName() + "】执行状态更新成功");
        } else {
            log.info("理财报警REWARDINFO:任务【" + financialTask.getTaskName() + "】执行状态更新失败");
        }
    }

    public static void main(String[] args) {
        SuperNodeRewardWork work=new SuperNodeRewardWork("","");
        work.run();
    }
}
