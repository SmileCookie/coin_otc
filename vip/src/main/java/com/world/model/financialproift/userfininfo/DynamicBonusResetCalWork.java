package com.world.model.financialproift.userfininfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.dao.task.Worker;
import com.world.model.entity.financialproift.FinancialTask;
import com.world.util.date.TimeUtil;

public class DynamicBonusResetCalWork extends Worker {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*查询SQL*/
	private String sql = "";
	
	/*此轮定时任务结束标识*/
    private static boolean workFlag = true;
    /*展示批量耗时的标识*/
    private boolean showInfoFlag = false;
    /*日志打印计数器执行10次打印一次*/
    private static int intLogInfoFlag = 1;
	
	public DynamicBonusResetCalWork(String name, String des) {
		super(name, des);
	}
	
	@Override
	public void run() {	
		/*记录核算开始时间*/
        long startTime = System.currentTimeMillis();
        FinancialTask financialTask;
        /*现在时间获取*/
        if (intLogInfoFlag == 10) {
        	log.info("理财报警REWARDINFO:【动态奖金(建点,指导,晋升)重新计算】开始");
        }
        //任务是否可执行
      	boolean taskflag = false;
      	showInfoFlag = false;
        try {
			/**
			 * 取得类名称，作为任务名称
			 */
			sql = "select * from fin_task where taskName = 'DynamicBonusResetCalWork' and taskTime <= NOW() and taskFlag = 1 ";
			log.info("HierarchyWork sql = " + sql);
			financialTask = (FinancialTask) Data.GetOne("vip_financial",sql, null,FinancialTask.class);
			if (null == financialTask) {
				if (intLogInfoFlag == 10) {
					log.info("理财报警REWARDINFO:【动态奖金(建点,指导,晋升)重新计算】没有需要执行的任务");
				}
				return;
			} else {
				if(1 == financialTask.getTaskFlag()) {
					taskflag = true;
				} else {
					taskflag = false;
					if (intLogInfoFlag == 10) {
						log.info("理财报警REWARDINFO:【动态奖金(建点,指导,晋升)重新计算】没有需要执行的任务，任务状态 = " + financialTask.getTaskFlag());
					}
					return;
				}
			}
		} catch (Exception e1) {
			financialTask = new FinancialTask();
			financialTask.setTaskName("HierarchyWork");
			log.info("理财报警REWARDERROR:【动态奖金(建点,指导,晋升)重新计算】任务配置初始化失败", e1);
		}  finally {
        	if (intLogInfoFlag == 10) {
        		intLogInfoFlag = 0;
        	}
        	intLogInfoFlag++;
        }
        
        if (workFlag && taskflag) {
        	workFlag = false;
        	try {
    			Date calStart = financialTask.getCallStartDate();
    			Date calEnd = financialTask.getCallEndDate();
    			log.info("calStart = " + calStart + ", calEnd = " + calEnd);
    			
    			/**
    			 * 重置需要重新分配的用户
    			 */
    			sql = "update t_user_call_level set dealflag = 0 where createdate < '" + calEnd + "' and createdate >= '" + calStart + "'";
    			log.info("sql = " + sql);
    			int intUpdate = Data.Update("vdsapollo", sql, null);
    			
    			/**
            	 * 开启事务
            	 */
            	List<OneSql> sqls = new ArrayList<>();
    			TransactionObject txObj = new TransactionObject();
    			
    			/*动态奖金(建点,指导,晋升)重新计算*/
    			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    			/*获取7天后的时间*/
            	String nextDistStartDate = sdf.format(TimeUtil.getBeforeTime(7));
            	/*前一个周期的开始时间*/
            	String beforeDistStartDate = sdf.format(TimeUtil.getBeforeTime(-7));
            	/*当前日期 */
            	String curDate = sdf.format(new Date());
            	/*更新 taskTime 时间，即下一轮开启时间*/
            	String taskTime = nextDistStartDate + " 04:00:00";
            	/*动态奖金(建点,指导,晋升)分配-跑的时候开(cal分配区间)，结算任务的开始时间*/
            	String settleTaskTime = curDate + " 05:30:00";
            	/*askFlag, callStartDate, callEndDate*/
            	String nextCallStartDate = curDate + " 04:00:00";
            	String nextCallEndDate = nextDistStartDate + " 04:00:00";
            	String beforetCallEndDate = beforeDistStartDate + " 05:00:00";
            	/*动态奖金(建点,指导,晋升)分配-跑的时候开(cal分配区间), 结算结束时间*/
            	String nextSettleCallEndDate = curDate + " 05:00:00";
    			
    			sql = "update fin_task set taskFlag = 1, handleTime = now(), taskTime = '" + taskTime + "', "
    				+ "callStartDate = '" + nextCallStartDate + "', callEndDate = '" + nextCallEndDate + "' "
    				+ "where taskName = 'DynamicBonusResetCalWork' ";
            	log.info("sql = " + sql);
            	sqls.add(new OneSql(sql, 1, null, "vip_financial"));
            	
            	sql = "update fin_task set taskFlag = 1, taskTime = '" + settleTaskTime + "', "
            		+ "callStartDate = '" + beforetCallEndDate + "', callEndDate = '" + nextSettleCallEndDate + "' "
            		+ "where taskName = 'DynamicBonusDisWork' ";
            	log.info("sql = " + sql);
            	sqls.add(new OneSql(sql, 1, null, "vip_financial"));
				
            	txObj.excuteUpdateList(sqls);
                if (txObj.commit()) {
                	
                } else {
                	log.info("理财报警REWARDERROR:【动态奖金(建点,指导,晋升)重新计算】状态设置异常");
                }
                
				showInfoFlag = true;
        		long endTime = System.currentTimeMillis();
        		if (showInfoFlag) {
        			log.info("理财报警REWARDTASK:【动态奖金(建点,指导,晋升)重新计算】结束!!!【核算耗时：{" + (endTime - startTime) + "}】,需要重新计算用户数量 = " + intUpdate);
        		}
    		} catch (Exception e) {
    			// 记录任务日志(失败)
				updateTask(financialTask, startTime, 1);
    			log.info("理财报警REWARDERROR:【动态奖金(建点,指导,晋升)重新计算】", e);
    		} finally {
            	workFlag = true;
            }
        } else {
        	log.info("理财报警REWARDTASK:【动态奖金(建点,指导,晋升)重新计算】上一轮分配任务尚未结束，本轮不需要进行");
        }
	}
	
	private void updateTask(FinancialTask financialTask, long startTime, int taskFlag) {
		TransactionObject txObj = new TransactionObject();
		List<OneSql> sqls = new ArrayList<>();
		String msg = "";
		if (taskFlag ==0) {
			msg = "动态奖金分配明细生成成功";
		}else {
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
		DynamicBonusResetCalWork hierarchyWork = new DynamicBonusResetCalWork("", "");
		hierarchyWork.run();
	}
	
}