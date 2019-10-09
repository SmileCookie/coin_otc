package com.world.model.financialproift.userfininfo;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.dao.task.Worker;
import com.world.model.entity.financialproift.FinDouProfitLog;
import com.world.model.entity.financialproift.FinUserRewardStatus;
import com.world.model.entity.financialproift.FinancialTask;
import com.world.util.date.TimeUtil;


public class VIPRewardFromDoubleThrowWork extends Worker {
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

    public VIPRewardFromDoubleThrowWork(String name, String des) {
        super(name, des);
    }

	@SuppressWarnings("unchecked")
	@Override
    public void run() {
		if (intLogInfoFlag == 10) {
			log.info("理财报警REWARDINFO:【VIP分红奖励记录生成(从复投释放)-生成】开始");
		}
        long startTime = System.currentTimeMillis();
        FinancialTask financialTask;
        boolean taskflag = false;
        showInfoFlag = false;
        try {
            /**
             * 取得类名称，作为任务名称
             * VIP分红奖励记录生成(从复投释放)-生成
             */
            sql = "select * from fin_task where taskName = 'VIPRewardFromDoubleThrowWork' and now() >= taskTime "
            	+ "and now() >= distStartTime and now() <= distEndTime and taskFlag = 1 ";
            log.info("NewUserRewardProductWork sql = " + sql);
            financialTask = (FinancialTask) Data.GetOne("vip_financial", sql, null, FinancialTask.class);
            if (null == financialTask) {
            	if (intLogInfoFlag == 10) {
            		log.info("理财报警REWARDINFO:【VIP分红奖励记录生成(从复投释放)-生成】没有需要执行的任务");
            	}
				return;
			} else {
				if(1 == financialTask.getTaskFlag()) {
					/*可以执行*/
					taskflag = true;
				} else {
					taskflag = false;
					if (intLogInfoFlag == 10) {
						log.info("理财报警REWARDINFO:【VIP分红奖励记录生成(从复投释放)-生成】没有需要执行的任务，任务状态 = " + financialTask.getTaskFlag());
					}
					return;
				}
			}
            log.info("NewUserRewardProductWork taskflag:" + taskflag);
        } catch (Exception e1) {
            financialTask = new FinancialTask();
            financialTask.setTaskName("NewUserRewardProductWork");
            log.info("理财报警REWARDERROR:【VIP分红奖励记录生成(从复投释放)-生成】定时任务启动失败,失败原因:", e1);
        } finally {
        	if (intLogInfoFlag == 10) {
        		intLogInfoFlag = 0;
        	}
        	intLogInfoFlag++;
        }
        
        if (workFlag && taskflag) {
            try {
            	workFlag = false;
                log.info("VIP分红奖励记录生成(从复投释放)-生成，定时任务开始...");
                /**
                 * 每日记录检查是否已经生成过，没有生成的进行生成，已生成的不再生成
                 */
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String curDate = sdf.format(new Date());
                String startCureDateTime = curDate + " 00:00:00";
                String endCureDateTime = curDate + " 23:59:59";
                /*批次处理*/
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
                String curBatchNo = sdf2.format(new Date());
                log.info("curDate = " + curDate);
                /*判断记录是否已生成*/
                sql = "select * from fin_user_reward_status where distType = 5 and superNodeProfitCount = 0 "
                	+ "and distTime >= '" + startCureDateTime + "' and distTime <= '" + endCureDateTime + "' ";
                log.info("sql = " + sql);
                
                /**
                 * 产出金额和分配金额
                 */
                BigDecimal distBalOriginal = BigDecimal.ZERO;
            	BigDecimal distBal = BigDecimal.ZERO;
            	/*需要补充分配的金额*/
            	BigDecimal vipNeedPayAmount = BigDecimal.ZERO;
            	/*已分配金额*/
            	BigDecimal vipPayAmount = BigDecimal.ZERO;
                FinUserRewardStatus finUserRewardStatus = (FinUserRewardStatus) Data.GetOne("vip_financial", sql, null, FinUserRewardStatus.class);
                if (null == finUserRewardStatus) {
                	/*任务开始时间*/
                	String distTime = curDate + " 04:30:00";
                	/*VIP新人截止时间*/
                	String distEndTime = curDate + " 04:00:00";
                	/*查询未分配记录*/
                    sql = "select id, toSuperNodeAmount from fin_dou_profit_log where batchNo = 0 order by toSuperNodeAmount asc ";
                    log.info("sql = " + sql);
                    List<Bean> listFinDouProfitLog = (List<Bean>) Data.Query("vip_financial", sql, null, FinDouProfitLog.class);
                    if (null != listFinDouProfitLog && listFinDouProfitLog.size() > 0) {
                    	/*插入表 fin_user_reward_status */
                    	/*0 = VIP分红-复投*/
                    	String distType = "5";
                    	String seqNo = curBatchNo + "0";
                    	/**
                    	 * 计算需要补充的资金
                    	 */
                    	String distStartTime = sdf.format(TimeUtil.getBeforeTime(-7)) + " 04:00:00";
                    	sql = "select sum(distBal) distBal from fin_user_reward_status where distType = 5 and superNodeProfitCount != 0 "
                    		+ "and distTime >= '" + distStartTime + "' and distTime < '" + distEndTime + "' ";
                    	log.info("sql = " + sql);
                    	List<BigDecimal> listVIPPayAmount = (List<BigDecimal>) Data.GetOne("vip_financial", sql, null);
                    	if (null != listVIPPayAmount && listVIPPayAmount.size() > 0) {
                    		if (null != listVIPPayAmount.get(0)) {
                    			vipPayAmount = listVIPPayAmount.get(0);
                    		}
                    	}
                    	if (vipPayAmount.compareTo(new BigDecimal(10)) < 0) {
                    		log.info("理财报警REWARDTASK:【VIP分红奖励记录生成(从复投释放)-生成】统计已分配金额异常，vipPayAmount = " + vipPayAmount);
                    		return;
                    	}
                    	if (vipPayAmount.compareTo(new BigDecimal(80000)) >= 0) {
                    		log.info("理财报警REWARDTASK:【VIP分红奖励记录生成(从复投释放)-生成】不需要分配，已分配金额，vipPayAmount = " + vipPayAmount);
                    		return;
                    	}
                    	
                    	/*每周分配总金额*/
                    	BigDecimal vipWeekPayAmount = new BigDecimal(50000);
                    	vipNeedPayAmount = (vipWeekPayAmount.subtract(vipPayAmount)).divide(new BigDecimal(0.98), 0, BigDecimal.ROUND_UP);
                    	log.info("vipPayAmount = " + vipPayAmount + ", vipNeedPayAmount = " + vipNeedPayAmount);
                    	
                    	/*复投记录*/
                    	FinDouProfitLog finDouProfitLog = null;
                    	long id = 0;
                    	String ids = "";
                    	BigDecimal toSuperNodeAmount = BigDecimal.ZERO;
                    	/*使用笔数*/
                    	int useDoubleProfitNum = 0;
                    	for (int i = 0; i < listFinDouProfitLog.size(); i++) {
                    		finDouProfitLog = (FinDouProfitLog) listFinDouProfitLog.get(i);
                    		toSuperNodeAmount = finDouProfitLog.getToSuperNodeAmount();
                    		id = finDouProfitLog.getId();
                    		distBalOriginal = distBalOriginal.add(toSuperNodeAmount).setScale(9, BigDecimal.ROUND_DOWN);
                    		if (id == 0) {
                    			log.info("理财报警REWARDTASK:【VIP分红奖励记录生成(从复投释放)-生成】统计分配人数异常，id = " + id);
                    			return;
                    		} else {
                    			ids = ids + id + ", ";
                    		}
                    		useDoubleProfitNum++;
                    		if (vipNeedPayAmount.compareTo(distBalOriginal) < 0) {
                    			log.info("理财报警REWARDTASK:【VIP分红奖励记录生成(从复投释放)-生成】金额达到分配邀请，vipNeedPayAmount = " + vipNeedPayAmount + ", distBalOriginal = " + distBalOriginal);
                    			break;
                    		}
                    	}
                    	if (!StringUtils.isEmpty(ids) && ids.length() > 0) {
                    		ids = ids.substring(0, ids.length() - 2);
                    	} else {
                    		log.info("理财报警REWARDTASK:【VIP分红奖励记录生成(从复投释放)-生成】统计分配人数异常，ids = " + ids);
                    		return;
                    	}
                    	
                    	/*98% 产出进行发放*/
                    	distBal = distBalOriginal.multiply(BigDecimal.valueOf(0.98)).setScale(9, BigDecimal.ROUND_DOWN);
                    	/**
                    	 * 开启事务
                    	 */
                    	List<OneSql> sqls = new ArrayList<>();
            			TransactionObject txObj = new TransactionObject();
                    	sql = "insert into fin_user_reward_status (seqNo, distTime, superNodeProfitCount, distType, "
                    		+ "distBal, distBalOriginal, distStatus, distFlag, distStartTime, distEndTime) "
                    		+ "values ('" + seqNo + "', '" + distTime + "', 0, " + distType + ", " + distBal + ", "
                    		+ "" + distBalOriginal + ", 0, 1, '" + distStartTime + "', '" + distEndTime + "')";
                    	log.info("sql = " + sql);
                    	sqls.add(new OneSql(sql, 1, null, "vip_financial"));
                    	
                    	/*更新 fin_supernode_mining_detail 中的批次状态*/
//                    	update fin_supernode_mining_detail a inner join (
//                    	select * from fin_supernode_mining_detail where sNodeType = 1 and sNodeBelType = 1 and profitBatchNo = 0 and height <= 239040) b  
//                    	on a.id = b.id set a.profitBatchNo = 9;
                    	sql = "update fin_dou_profit_log set batchNo = " + seqNo + " "
                    		+ "where batchNo = 0 and id in (" + ids + ")";
                    	log.info("sql = " + sql);
                    	sqls.add(new OneSql(sql, useDoubleProfitNum, null, "vip_financial"));
                    	/*更新 SuperNodeRewardDetailWork 结算时间和状态*/
                    	sql = "update fin_task set taskTime = '" + distTime + "', taskFlag = 1 where taskName = 'SuperNodeRewardDetailWork' ";
                    	log.info("sql = " + sql);
                    	sqls.add(new OneSql(sql, 1, null, "vip_financial"));
                    	/**
                    	 * VIPRewardFromDoubleThrowWork		1周后的启动时间
                    	 */
                    	/*VIP新人开始时间*/
                    	String nextDistStartDate = sdf.format(TimeUtil.getBeforeTime(7));
                    	String nextDistStartTime = nextDistStartDate + " 04:00:00";
                    	String nextDistEndTime = nextDistStartDate + " 18:00:00";
                    	sql = "update fin_task set taskTime = '" + nextDistStartTime + "', "
                        	+ "distStartTime = '" + nextDistStartTime + "', "
                        	+ "distEndTime = '" + nextDistEndTime + "' "
                        	+ "where taskName = 'VIPRewardFromDoubleThrowWork' ";
                        log.info("sql = " + sql);
                        sqls.add(new OneSql(sql, 1, null, "vip_financial"));
                        
                    	txObj.excuteUpdateList(sqls);
                        if (txObj.commit()) {
                        	
                        } else {
                        	log.info("理财报警REWARDERROR:【VIP分红奖励记录生成(从复投释放)-生成】发生异常");
                        }
                    } else {
                    	if (intLogInfoFlag == 10) {
                    		log.info("理财报警REWARDINFO:【VIP分红奖励记录生成(从复投释放)-生成】,没有复投资金，当前可用复投数量 = " + listFinDouProfitLog.size());
                    	}
                    	return;
                    }
                    showInfoFlag = true;
                } else {
                	if (intLogInfoFlag == 10) {
                		log.info("理财报警REWARDINFO:【VIP分红奖励记录生成(从复投释放)-生成】,本次没有需要执行的分配资金记录任务 = " + sql);
                	}
                	return;
                }
                
                long endTime = System.currentTimeMillis();
                if (showInfoFlag) {
                	updateTask(financialTask, startTime, 0, 0);
                	log.info("理财报警REWARDTASK:【VIP分红奖励记录生成(从复投释放)-生成】... 耗时：" + (endTime - startTime) + ", 分配数量 = " + distBal + ", 补充数量 = " + vipNeedPayAmount + ", 已分配数量= " + vipPayAmount);
                }
            } catch (Exception e) {
                log.info("理财报警REWARDERROR:【VIP分红奖励记录生成(从复投释放)-生成】发生不控异常,异常信息", e);
                updateTask(financialTask, startTime, 1, 1);
            } finally {
                workFlag = true;
            }
        } else {
        	log.info("理财报警REWARDTASK:【VIP分红奖励记录生成(从复投释放)-生成】,上一轮定时任务没有结束，本轮不需要执行");
        }

    }

    private void updateTask(FinancialTask financialTask, long startTime, int taskFlag, int taskError) {
        TransactionObject txObj = new TransactionObject();
        List<OneSql> sqls = new ArrayList<>();

        String taskInfo = "执行成功";
        if (taskFlag != 0) {
            taskInfo = "执行失败，请查看日志记录";
        }
        /* 更新任务执行状态 */
        sql = "update fin_task set handleTime = now(), nowStep = 1, taskError = " + taskError + " "
        	+ "where taskName = '" + financialTask.getTaskName() + "' ";
        log.info("NewUserRewardProductWork sql = " + sql);
        sqls.add(new OneSql(sql, 1, null, "vip_financial"));
        /* 插入任务执行日志 */
        sql = "insert into fin_task_logs (taskName,taskType,taskTime,taskIndex,sumStep,nowStep,taskStartTime,taskEndTime,taskResult,resultInfo) "
                + "values('" + financialTask.getTaskName() + "'," + financialTask.getTaskType() + ",'"
                + financialTask.getTaskTime() + "'," + financialTask.getTaskIndex() + "," + financialTask.getSumStep()
                + "," + financialTask.getSumStep() + ",from_unixtime(" + startTime + "),now()," + taskFlag + ",'"
                + taskInfo + "')";
        log.info("NewUserRewardProductWork sql = " + sql);
        sqls.add(new OneSql(sql, 1, null, "vip_financial"));
        txObj.excuteUpdateList(sqls);
        
        if (txObj.commit()) {
            log.info("理财报警REWARDINFO:任务【" + financialTask.getTaskName() + "】执行状态更新成功");
        } else {
            log.info("理财报警REWARDINFO:任务【" + financialTask.getTaskName() + "】执行状态更新失败");
        }
    }

    public static void main(String[] args) {
    	VIPRewardFromDoubleThrowWork snrpw = new VIPRewardFromDoubleThrowWork("", "");
    	snrpw.run();
//    	FinThreadTest ft = new FinThreadTest();
//    	ft.start();
//    	FinThreadTest ft2 = new FinThreadTest();
//    	ft2.start();
    }
}
