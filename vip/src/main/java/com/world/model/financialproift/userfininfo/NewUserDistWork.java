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
import com.world.model.financialproift.userfininfo.thread.NewUserRewardThread;

/**
 * @Author Ethan
 * @Date 2019-07-29 13:33
 * @Description
 **/

public class NewUserDistWork extends Worker {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String sql = "";
	private boolean workFlag = true;
	/*展示批量耗时的标识*/
    private boolean showInfoFlag = false;
    /*日志打印计数器执行10次打印一次*/
    private static int intLogInfoFlag = 1;

	public NewUserDistWork(String name, String des) {
		super(name, des);
	}

	public static void main(String[] args) {
		NewUserDistWork nw = new NewUserDistWork("", "");
		nw.run();
	}

	@Override
	public void run() {
		if (intLogInfoFlag == 10) {
			log.info("理财报警REWARDINFO:【新人加成-结算】开始");
		}
		long startTime = System.currentTimeMillis();
		FinancialTask financialTask;
		boolean taskflag = false;
		showInfoFlag = false;
		try {
			/**
			 * 取得类名称，作为任务名称
			 * 新人加成-结算
			 */
			sql = "select * from fin_task where taskName = 'NewUserDistWork' and taskTime <= NOW() and taskFlag = 1 ";
			log.info("NewUserDistWork sql = " + sql);
			financialTask = (FinancialTask) Data.GetOne("vip_financial", sql, null, FinancialTask.class);
			//任务状态为1可执行，并且是否已分配明细为1已分配明细，并且分配状态为1可分配，并且是否已分配为0未分配，才可执行
			if (null == financialTask) {
				if (intLogInfoFlag == 10) {
					log.info("理财报警REWARDINFO:【新人加成-结算】没有需要执行的任务");
				}
				return;
			} else {
				if(1 == financialTask.getTaskFlag()) {
					/*可以执行*/
					taskflag = true;
				} else {
					taskflag = false;
					if (intLogInfoFlag == 10) {
						log.info("理财报警REWARDINFO:【新人加成-结算】没有需要执行的任务，任务状态 = " + financialTask.getTaskFlag());
					}
					return;
				}
			}
		} catch (Exception e1) {
			financialTask = new FinancialTask();
			financialTask.setTaskName("NewUserDistWork");
			log.info("理财报警REWARDERROR:【新人加成-结算】任务启动失败:NewUserDistWork", e1);
		} finally {
        	if (intLogInfoFlag == 10) {
        		intLogInfoFlag = 0;
        	}
        	intLogInfoFlag++;
        }
		
		if (workFlag && taskflag) {
			workFlag = false;
			try {
				log.info("新增用户奖励，定时任务开始...");
				// 开户多线程处理奖励分配
				newUserRewardThread(financialTask,startTime,0);
//				log.info("理财报警REWARDTASK:【新人加成-结算】任务按照预期结算完成");
				long endTime = System.currentTimeMillis();
				if (showInfoFlag) {
					log.info("理财报警REWARDTASK:【新人加成-结算】任务结束... 耗时：" + (endTime - startTime));
				}
			} catch (Exception e) {
				// 记录任务日志(失败)
				updateTask(financialTask, startTime, 1);
				log.info("理财报警REWARDERROR:【新人加成-结算】任务启动失败:NewUserDistWork", e);
			} finally {
				workFlag = true;
			}
		} else {
			log.info("理财报警REWARDERROR:【新人加成-结算】,上一轮定时任务没有结束，本轮不需要执行");
		}

	}

	private void updateTask(FinancialTask financialTask, long startTime, int taskFlag) {
		TransactionObject txObj = new TransactionObject();
		List<OneSql> sqls = new ArrayList<>();
		String msg = "";
		if (taskFlag ==0) {
			msg = "分配成功";
		}else {
			msg = "分配失败";
		}
		/* 更新任务执行状态 */
		sql = "update fin_task set handleTime=now(),nowStep = 1,taskError=" + taskFlag
				+ " where taskName='" + financialTask.getTaskName() + "' ";
		log.info("NewUserDistWork sql = " + sql);
		sqls.add(new OneSql(sql, 1, null, "vip_financial"));
		/* 插入任务执行日志 */
		sql = "insert into fin_task_logs (taskName,taskType,taskTime,taskIndex,sumStep,nowStep,taskStartTime,taskEndTime,taskResult,resultInfo) "
				+ "values('" + financialTask.getTaskName() + "'," + financialTask.getTaskType() + ",'"
				+ financialTask.getTaskTime() + "'," + financialTask.getTaskIndex() + "," + financialTask.getSumStep()
				+ "," + financialTask.getSumStep() + ",from_unixtime(" + startTime + "),now()," + taskFlag + ",'"
				+ msg + "')";
		log.info("NewUserDistWork sql = " + sql);
		sqls.add(new OneSql(sql, 1, null, "vip_financial"));
		txObj.excuteUpdateList(sqls);

		if (txObj.commit()) {
			log.info("理财报警REWARDINFO:任务【新人加成-结算】执行状态更新成功");
		} else {
			log.info("理财报警REWARDINFO:任务【新人加成-结算】执行状态更新失败");
		}
	}

	private void newUserRewardThread(FinancialTask financialTask, long startTime, int taskFlag) {
		try {
			sql = "select * from fin_profit_newvip_detail where flag = 0 and profitType = 7 ";
			log.info("NewUserDistWork sql = " + sql);
			List<FinProfitAssignDetail> rewardDetailList = Data.QueryT("vip_financial", sql, null, FinProfitAssignDetail.class);
			if (rewardDetailList != null && rewardDetailList.size() > 0) {
				log.info("理财报警REWARDTASK:【新人加成-结算】任务按照预期启动,开始进行记录结算");
				int size = rewardDetailList.size();
				ExecutorService executorPool = Executors.newFixedThreadPool(1);
				CountDownLatch countDownLatch = new CountDownLatch(size);
//				log.info("理财报警REWARDWARN:新增用户奖励时任务按照预期启动,开始进行记录分配,本次需要分配的记录有 = " + rewardDetailList.size() + "条");
				for (FinProfitAssignDetail rewardDetail : rewardDetailList) {
					NewUserRewardThread newUserRewardThread = new NewUserRewardThread(rewardDetail, countDownLatch);
					executorPool.execute(newUserRewardThread);
				}
				countDownLatch.await();
				executorPool.shutdown();
				// 记录任务日志（成功）
				updateTask(financialTask, startTime, 0);
				showInfoFlag = true;
			} else {
				if (intLogInfoFlag == 10) {
					log.info("理财报警REWARDTASK:【新人加成-结算】定时任务可以继续执行,本次没有需要分配的人员 sql = " + sql);
				}
			}
		} catch (Exception e) {
			// 记录任务日志（成功）
			updateTask(financialTask, startTime, 1);
			log.info("理财报警REWARDERROR:新增用户奖励定时任务发生不可控异常,异常信息{}}", e);
		}
	}
}
