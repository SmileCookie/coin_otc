package com.world.model.financialproift.userfininfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.world.cache.Cache;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.dao.task.Worker;
import com.world.model.entity.financialproift.FinUserRewardStatus;
import com.world.model.entity.financialproift.FinUserfinancialinfo;
import com.world.model.entity.financialproift.FinancialTask;

/**
 * @Author Ethan
 * @Date 2019-07-29 13:33
 * @Description
 **/

public class NewUserDetailWork extends Worker {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String sql = "";
	private static boolean workFlag = true;
	/*展示批量耗时的标识*/
    private boolean showInfoFlag = false;
    /*日志打印计数器执行10次打印一次*/
    private static int intLogInfoFlag = 1;

	public NewUserDetailWork(String name, String des) {
		super(name, des);
	}

	public static void main(String[] args) {
//		for (int i = 0; i < 10; i++) {
//			FinThreadTest ft = new FinThreadTest();
//	    	ft.start();
//	    	try {
//				Thread.sleep(1000L);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		new NewUserDetailWork("","").run();
	}

	@Override
	public void run() {
		if (intLogInfoFlag == 10) {
			log.info("理财报警REWARDINFO:【新增VIP奖励记录-生成】开始");
		}
		long startTime = System.currentTimeMillis();
		FinancialTask financialTask;
		
		boolean taskflag = false;
		showInfoFlag = false;
		try {
			/**
			 * 取得类名称，作为任务名称
			 * 新增VIP奖励记录-生成
			 */
			sql = "select * from fin_task where taskName = 'NewUserDetailWork' and taskTime <= NOW() and taskFlag = 1 ";
			log.info("NewUserDetailWork sql = " + sql);
			financialTask = (FinancialTask) Data.GetOne("vip_financial", sql, null, FinancialTask.class);
			//任务状态为1可执行，并且可分配明细状态为1可分配明细，并且是否已分配明细为0未分配明细，才可执行
			if (null == financialTask) {
				if (intLogInfoFlag == 10) {
					log.info("理财报警REWARDINFO:【新增VIP奖励记录-生成】没有需要执行的任务");
				}
				return;
			} else {
				if(1 == financialTask.getTaskFlag()) {
					/*可以执行*/
					taskflag = true;
				} else {
					taskflag = false;
					if (intLogInfoFlag == 10) {
						log.info("理财报警REWARDINFO:【新增VIP奖励记录-生成】没有需要执行的任务，任务状态 = " + financialTask.getTaskFlag());
					}
					return;
				}
			}
			log.info("NewUserRewardWork taskflag:" + taskflag);
		} catch (Exception e1) {
			financialTask = new FinancialTask();
			financialTask.setTaskName("NewUserDetailWork");
			log.info("理财报警REWARDERROR:【新增VIP奖励记录-生成】任务启动失败:NewUserDetailWork", e1);
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
				// 初始化分红奖励数据集
				FinUserRewardStatus rewardStatus = getRewardStatus();
				if (rewardStatus != null) {
					// 生成奖励明细
					setProfitAssignDetail(rewardStatus);
					// 记录任务日志（成功）
					updateTask(financialTask, startTime, 0);
				} else {
					if (intLogInfoFlag == 10) {
						log.info("理财报警REWARDINFO:【新增VIP奖励记录-生成】没有需要分配的配置记录");
					}
				}
				long endTime = System.currentTimeMillis();
				if (showInfoFlag) {
					log.info("理财报警REWARDTASK:新增用户奖励时任务结束... 耗时：" + (endTime - startTime));
				}
			} catch (Exception e) {
				// 记录任务日志(失败)
				updateTask(financialTask, startTime, 1);
				log.info("理财报警REWARDERROR:VIP分红奖励任务启动失败:NewUserDetailWork", e);
			} finally {
				workFlag = true;
			}
		} else {
			log.info("理财报警REWARDINFO:【新增VIP奖励记录-生成】,上一轮定时任务没有结束，本轮不需要执行");
		}

	}

	/**
	 * 取得可分红记录
	 * 
	 * @return
	 */
	private FinUserRewardStatus getRewardStatus() {
		sql = "select * from fin_user_reward_status where distType = 7 and distStatus = 0 and distFlag = 1 and distBal > 0 and now() >= distTime ";
		log.info("sql = " + sql);
		FinUserRewardStatus rewardTask = (FinUserRewardStatus) Data.GetOne("vip_financial", sql, null,
				FinUserRewardStatus.class);
		return rewardTask;
	}

	private void setProfitAssignDetail(FinUserRewardStatus rewardStatus) {
		Map<String, Object> map = new HashMap<String, Object>();
		TransactionObject txObj = new TransactionObject();
		List<OneSql> sqls = new ArrayList<>();
		// 获取奖励用户
		String sql = "";
		sql = "select userid from fin_userfinancialinfo where authPayFlag = 2 and matrixLevel= 6 "
			+ "and profittime > '" + rewardStatus.getDistStartTime() + "' and profittime <= '" + rewardStatus.getDistEndTime() + "'";
		log.info("NewUserDetailWork sql = " + sql);
		List<FinUserfinancialinfo> rewardUserList = Data.QueryT("vip_financial", sql, null, FinUserfinancialinfo.class);
		
		if (rewardUserList != null && rewardUserList.size() > 0) {
			log.info("理财报警REWARDTASK:【新增VIP奖励记录-生成】任务按照预期启动,开始进行记录初始化");
			try {
				// 可分配用户数
				int usersize = rewardUserList.size();
				map.put("distNum", usersize);
				// String vdsUsdt = Cache.Get("vds_usdt_l_price");
				String vdsUsdt = Cache.Get("vds_usdt_l_price");
				BigDecimal bdVdsUsdt = BigDecimal.ZERO;
				if (StringUtils.isEmpty(vdsUsdt)) {
					log.info("理财报警REWARDERROR:获取vdsUsdt价格异常 = " + vdsUsdt);
					// usdt价格
					map.put("usdtPrice", 0);
					map = null;
				} else {
					try {
						bdVdsUsdt = new BigDecimal(vdsUsdt);
						// usdt价格
						map.put("usdtPrice", bdVdsUsdt);
					} catch (Exception e) {
						log.info("理财报警REWARDERROR:Cache获取vdsUsdt价格异常 = " + vdsUsdt);
						// usdt价格
						map.put("usdtPrice", 0);
						map = null;
					}
					if (bdVdsUsdt.compareTo(BigDecimal.ZERO) <= 0) {
						log.info("理财报警REWARDERROR:Cache获取vdsUsdt价格异常小于0 = " + vdsUsdt);
						// usdt价格
						map.put("usdtPrice", 0);
						map = null;
					}
				}
				// 每人分配金额
				log.info("可分配金额[" + rewardStatus.getDistBal() + "],可分配用户数[" + BigDecimal.valueOf(usersize) + "]");
				BigDecimal rewardPer = rewardStatus.getDistBal().divide(BigDecimal.valueOf(usersize), 4, BigDecimal.ROUND_DOWN);
				map.put("vdsPer", rewardPer);
				// 每人分配USDT
				BigDecimal usdtPer = rewardPer.multiply(bdVdsUsdt).setScale(4, BigDecimal.ROUND_DOWN);
				map.put("usdtPer", usdtPer);
				// 已分配金额
				BigDecimal yetAmt = rewardPer.multiply(BigDecimal.valueOf(usersize));
				map.put("yetAmt", yetAmt);
				/* vip_financial库 插入奖励明细 fin_profit_newvip_detail*/
				sql = "insert into fin_profit_newvip_detail (profitUserId,profitUserName,profitType,profitAmount,usdtPrice,usdtAmount,NewVipWeekUser,"
					+ "NewVipWeekAmount,createtime,parentid,flag,distStartTime,distEndTime) "
					+ "select userId,userName,'7'," + rewardPer + "," + bdVdsUsdt + "," + usdtPer + "," + usersize + "," + rewardStatus.getDistBal() + ",NOW() , " 
					+ rewardStatus.getSeqNo() + ",0, '" + rewardStatus.getDistStartTime() + "','" + rewardStatus.getDistEndTime()+"' "
					+ " from fin_userfinancialinfo " + "where authPayFlag=2 " + "and profittime > '"
					+ rewardStatus.getDistStartTime() + "' " + "and profittime <= '" + rewardStatus.getDistEndTime()
					+ "' ";
				log.info("NewUserDetailWork sql = " + sql);
				sqls.add(new OneSql(sql, -1, null, "vip_financial"));
				/* 更新 vip_financail库fin_user_reward_status表的状态 */
				sql = "update fin_user_reward_status set yetAmt = " + map.get("yetAmt")
						+ " , distStatus = 1 ,distFlag = 0 , vdsPer = " + map.get("vdsPer") + ", " + "usdtPer = "
						+ map.get("usdtPer") + " , distNum=" + map.get("distNum") + " , updatetime = now() , usdtPrice="
						+ map.get("usdtPrice") + " " + "where id = " + rewardStatus.getId();
				log.info("理财报警REWARDINFO:fin_reward_task sql = " + sql);
				sqls.add(new OneSql(sql, 1, null, "vip_financial"));
				txObj.excuteUpdateList(sqls);
				if (txObj.commit()) {
					log.info("理财报警REWARDTASK:VIP分红奖励分红明细记录成功， 每个用户分配金额 = " + map.get("vdsPer") + ", 分配总金额 = " + map.get("yetAmt") + ", 分配总人数 = " + map.get("distNum"));
				} else {
					log.info("理财报警REWARDERROR:VIP分红奖励分红明细记录失败");
				}
				showInfoFlag = true;
			} catch (Exception e) {
				log.info("理财报警REWARDERROR:VIP分红奖励分红明细记录失败{}", e);
			}

		} else {
			log.info("理财报警REWARDTASK:【超级节点-用户奖励收益明细-生成】,本次没有需要执行的分配资金记录 = " + sql);
		}

	}

	private void updateTask(FinancialTask financialTask, long startTime, int taskFlag) {
		TransactionObject txObj = new TransactionObject();
		List<OneSql> sqls = new ArrayList<>();
		String msg = "";
		if (taskFlag ==0) {
			msg = "分配明细生成成功";
		}else {
			msg = "分配明细生成失败";
		}
		/* 更新任务执行状态 */
		sql = "update fin_task set handleTime=now(),nowStep = 1,taskError=" + taskFlag + ", taskFlag = " + taskFlag + " "
			+ "where taskName='" + financialTask.getTaskName() + "' ";
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

}
