package com.world.model.financialproift.userfininfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.world.cache.Cache;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.dao.task.Worker;
import com.world.model.entity.financialproift.FinancialBonus;
import com.world.model.entity.financialproift.FinancialTask;
import com.world.model.financialproift.userfininfo.thread.DynamicBonusDisThread;
import me.chanjar.weixin.common.util.StringUtils;

public class DynamicBonusDisWork extends Worker {
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
	
	public DynamicBonusDisWork(String name, String des) {
		super(name, des);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {	
		/*记录核算开始时间*/
        long startTime = System.currentTimeMillis();
        FinancialTask financialTask;
        /*现在时间获取*/
        if (intLogInfoFlag == 10) {
        	log.info("理财报警REWARDINFO:【动态奖金(建点,指导,晋升)结算】开始");
        }
        //任务是否可执行
      	boolean taskflag = false;
      	showInfoFlag = false;
        try {
			/**
			 * 取得类名称，作为任务名称
			 */
			sql = "select * from fin_task where taskName = 'DynamicBonusDisWork' and taskTime <= NOW() and taskFlag = 1 ";
			log.info("HierarchyWork sql = " + sql);
			financialTask = (FinancialTask) Data.GetOne("vip_financial",sql, null,FinancialTask.class);
			if (null == financialTask) {
				if (intLogInfoFlag == 10) {
					log.info("理财报警REWARDINFO:【动态奖金(建点,指导,晋升)结算】没有需要执行的任务");
				}
				return;
			} else {
				if(1 == financialTask.getTaskFlag()) {
					/*可以执行*/
					taskflag = true;
				} else {
					taskflag = false;
					if (intLogInfoFlag == 10) {
						log.info("理财报警REWARDINFO:【动态奖金(建点,指导,晋升)结算】没有需要执行的任务，任务状态 = " + financialTask.getTaskFlag());
					}
					return;
				}
			}
		} catch (Exception e1) {
			financialTask = new FinancialTask();
			financialTask.setTaskName("HierarchyWork");
			log.info("理财报警REWARDINFO:【动态奖金(建点,指导,晋升)结算】任务配置初始化失败", e1);
		} finally {
        	if (intLogInfoFlag == 10) {
        		intLogInfoFlag = 0;
        	}
        	intLogInfoFlag++;
        }
        
        if (workFlag && taskflag) {
        	workFlag = false;
        	try {
        		String vdsUsdt = Cache.Get("vds_usdt_l_price");
    			BigDecimal bdVdsUsdt = BigDecimal.ZERO;
    			if (StringUtils.isEmpty(vdsUsdt)) {
    				log.info("理财报警REWARDERROR:【动态奖金(建点,指导,晋升)结算】Cache获取vdsUsdt价格异常 = " + vdsUsdt);
    				return;
    			} else {
    				try {
    					bdVdsUsdt = new BigDecimal(vdsUsdt);
    				} catch (Exception e) {
    					log.info("理财报警REWARDERROR:【动态奖金(建点,指导,晋升)结算】Cache获取vdsUsdt价格异常 = " + vdsUsdt);
    					return;
    				}
    				if (bdVdsUsdt.compareTo(BigDecimal.ZERO) <= 0) {
    					log.info("理财报警REWARDERROR:【动态奖金(建点,指导,晋升)结算】Cache获取vdsUsdt价格异常小于0 = " + vdsUsdt);
    					return;
    				}
    			}
    			Date calStart = financialTask.getCallStartDate();
    			Date calEnd = financialTask.getCallEndDate();
    			log.info("calStart = " + calStart + ", calEnd = " + calEnd);
    			/**
    			 * 重置需要重新分配的用户
    			 */
    			long longResetCalUsers = 0;
    			sql = "select count(*) cnt from t_user_call_level where dealflag = 0 "
    				+ "and createdate < '" + calEnd + "' and createdate >= '" + calStart + "'";
    			log.info("sql = " + sql);
                List<Long> listResetCalUsers = (List<Long>) Data.GetOne("vdsapollo", sql, null);
                if (null != listResetCalUsers) {
                    /*个数检查*/
                	longResetCalUsers = listResetCalUsers.get(0);
                }
                log.info("longResetCalUsers = " + longResetCalUsers);
    			if (longResetCalUsers > 0) {
    				log.info("理财报警REWARDTASK:【动态奖金(建点,指导,晋升)结算】存在没有计算完成的用户数量 = " + longResetCalUsers);
    				return;
    			}
    			
    			/**
    			 * 1、获取需要分配的用户记录。按照用户，奖金类型 group by
    			 * 2、循环保存
    			 * 3、记入流水
    			 * 4、已处理的进行update 标记
    			 * 没处理一条，开启1个事务
    			 * and user_id = 1207883
    			 */
        		sql = "select user_id, bonus_type, sum(bonus_price) bonus_price from t_bonus "
        			+ "where deal_flag = 0 and bonus_time < '" + calEnd + "' and bonus_time >= '" + calStart + "' "
        			+ "group by user_id, bonus_type having sum(bonus_price) > 0";
    			
//    			sql = "select user_id, bonus_type, bonus_price, id from t_bonus where deal_flag = 0 ";
    			log.info("HierarchyWork sql = " + sql);
    			List<Bean> listFinancialBonus = (List<Bean>) Data.Query("vdsapollo", sql, null, FinancialBonus.class);
    			log.info("listFinancialBonus.size = " + listFinancialBonus.size());
    			if (null != listFinancialBonus && listFinancialBonus.size() > 0) {
    				ExecutorService executorSuperNode = Executors.newFixedThreadPool(1);
    				log.info("理财报警REWARDTASK:【动态奖金(建点,指导,晋升)结算】定时任务可以继续执行,本次分配总人数 = " + listFinancialBonus.size());
    				CountDownLatch countDownLatch = new CountDownLatch(listFinancialBonus.size());
    				/*定义接收变量*/
    				FinancialBonus financialBonus = null;
    				for (int i = 0; i < listFinancialBonus.size(); i++) {
    					financialBonus = (FinancialBonus) listFinancialBonus.get(i);
        				DynamicBonusDisThread dynamicBonusDisThread = new DynamicBonusDisThread(financialBonus, countDownLatch, bdVdsUsdt, calStart, calEnd);
//        				dynamicBonusDisThread.run();
        				executorSuperNode.execute(dynamicBonusDisThread);
    				}
    				countDownLatch.await();
    				/*关闭线程池*/
        			executorSuperNode.shutdown();
    				showInfoFlag = true;
    			} else {
    				if (intLogInfoFlag == 10) {
    					log.info("理财报警REWARDTASK:【动态奖金(建点,指导,晋升)结算】定时任务可以继续执行,本次没有需要分配的人员 sql = " + sql);
    				}
    				return;
    			}
    			
        		long endTime = System.currentTimeMillis();
        		if (showInfoFlag) {
        			// 记录任务日志（成功）
    				updateTask(financialTask, startTime, 0);
        			log.info("理财报警REWARDTASK:【动态奖金(建点,指导,晋升)结算】结束!!!【核算耗时：{" + (endTime - startTime) + "}】");
        		}
    		} catch (Exception e) {
    			// 记录任务日志(失败)
				updateTask(financialTask, startTime, 1);
    			log.info("理财报警REWARDERROR:HierarchyWork", e);
    		} finally {
            	workFlag = true;
            }
        } else {
        	log.info("理财报警REWARDTASK:【动态奖金(建点,指导,晋升)结算】上一轮分配任务尚未结束，本轮不需要进行");
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
		sql = "update fin_task set handleTime=now(),nowStep = 1, taskFlag = 0, taskError=" + taskFlag
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
		DynamicBonusDisWork hierarchyWork = new DynamicBonusDisWork("", "");
		hierarchyWork.run();
	}
}