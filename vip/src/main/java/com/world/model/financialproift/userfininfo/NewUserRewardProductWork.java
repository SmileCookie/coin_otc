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
import com.world.model.entity.financialproift.FinSuperNodeMiningDetail;
import com.world.model.entity.financialproift.FinUserRewardStatus;
import com.world.model.entity.financialproift.FinancialTask;
import com.world.util.date.TimeUtil;


public class NewUserRewardProductWork extends Worker {
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

    public NewUserRewardProductWork(String name, String des) {
        super(name, des);
    }

	@SuppressWarnings("unchecked")
	@Override
    public void run() {
		if (intLogInfoFlag == 10) {
			log.info("理财报警REWARDINFO:【新人加成-区块奖励记录生成(1周区块)-生成】开始");
		}
        long startTime = System.currentTimeMillis();
        FinancialTask financialTask;
        boolean taskflag = false;
        showInfoFlag = false;
        try {
            /**
             * 取得类名称，作为任务名称
             * 新人加成-区块奖励记录生成(1周区块)-生成
             */
            sql = "select * from fin_task where taskName = 'NewUserRewardProductWork' and now() >= taskTime "
            	+ "and now() >= distStartTime and now() <= distEndTime and taskFlag = 1 ";
            log.info("NewUserRewardProductWork sql = " + sql);
            financialTask = (FinancialTask) Data.GetOne("vip_financial", sql, null, FinancialTask.class);
            if (null == financialTask) {
            	if (intLogInfoFlag == 10) {
            		log.info("理财报警REWARDINFO:【新人加成-区块奖励记录生成(1周区块)-生成】没有需要执行的任务");
            	}
				return;
			} else {
				if(1 == financialTask.getTaskFlag()) {
					/*可以执行*/
					taskflag = true;
				} else {
					taskflag = false;
					if (intLogInfoFlag == 10) {
						log.info("理财报警REWARDINFO:【新人加成-区块奖励记录生成(1周区块)-生成】没有需要执行的任务，任务状态 = " + financialTask.getTaskFlag());
					}
					return;
				}
			}
            log.info("NewUserRewardProductWork taskflag:" + taskflag);
        } catch (Exception e1) {
            financialTask = new FinancialTask();
            financialTask.setTaskName("NewUserRewardProductWork");
            log.info("理财报警REWARDERROR:【新人加成-区块奖励记录生成(1周区块)-生成】定时任务启动失败,失败原因:", e1);
        } finally {
        	if (intLogInfoFlag == 10) {
        		intLogInfoFlag = 0;
        	}
        	intLogInfoFlag++;
        }
        
        if (workFlag && taskflag) {
            try {
            	workFlag = false;
                log.info("新人加成-区块奖励记录生成(1周区块)-生成，定时任务开始...");
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
                sql = "select * from fin_user_reward_status "
                	+ "where distType = 7 and distTime >= '" + startCureDateTime + "' and distTime <= '" + endCureDateTime + "' ";
                log.info("sql = " + sql);
                
                /**
                 * 产出金额和分配金额
                 */
                /*新进人数*/
                long intDistUsers = 0;
                BigDecimal distBalOriginal = BigDecimal.ZERO;
            	BigDecimal distBal = BigDecimal.ZERO;
                FinUserRewardStatus finUserRewardStatus = (FinUserRewardStatus) Data.GetOne("vip_financial", sql, null, FinUserRewardStatus.class);
                if (null == finUserRewardStatus) {
                    /*查询截止到当前日期4点之前的所有区块*/
                	/*任务开始时间*/
                	String distTime = curDate + " 04:30:00";
                	/*VIP新人截止时间*/
                	String distEndTime = curDate + " 04:00:00";
                    sql = "select id, miningAmount from fin_supernode_mining_detail "
                    	+ "where sNodeBelType != 1 and profitBatchNo = 0 and sNodeShowFlag = 1 and createTime <= '" + distEndTime + "' ";
                    log.info("sql = " + sql);
                    List<Bean> listFinSuperNodeMiningDetail = (List<Bean>) Data.Query("vip_financial", sql, null, FinSuperNodeMiningDetail.class);
                    if (null != listFinSuperNodeMiningDetail && listFinSuperNodeMiningDetail.size() > 0) {
                    	/*插入表 fin_user_reward_status */
                    	/*5 = VIP分红*/
                    	int superNodeProfitCount = listFinSuperNodeMiningDetail.size();
                    	String distType = "7";
                    	String seqNo = curBatchNo + distType;
                    	/**
                    	 * 计算上周新进人数
                    	 */
                    	/*VIP新人开始时间*/
                    	String distStartTime = sdf.format(TimeUtil.getBeforeTime(-7)) + " 04:00:00";
                    	sql = "select count(*) cnt from fin_userfinancialinfo where authPayFlag = 2 and matrixLevel= 6 "
                    		+ "and profittime > '" + distStartTime + "' and profittime <= '" + distEndTime + "' ";
                    	log.info("sql = " + sql);
                    	List<Long> listCountDistUser = (List<Long>) Data.GetOne("vip_financial", sql, null);
                    	if (null != listCountDistUser && listCountDistUser.size() > 0) {
                    		if (null != listCountDistUser.get(0)) {
                    			intDistUsers = listCountDistUser.get(0);
                    		}
                    	}
                    	if (intDistUsers < 1) {
                    		log.info("理财报警REWARDTASK:【新人加成-区块奖励记录生成(1周区块)-生成】统计分配人数异常，新进人数 = " + intDistUsers + ", sql = " + sql);
                    		return;
                    	}
                    	
                    	/*统计本周的区块产出*/
                    	FinSuperNodeMiningDetail finSuperNodeMiningDetail = null;
                    	int id = 0;
                    	String ids = "";
                    	BigDecimal miningAmount = BigDecimal.ZERO;
                    	for (int i = 0; i < listFinSuperNodeMiningDetail.size(); i++) {
                    		finSuperNodeMiningDetail = (FinSuperNodeMiningDetail) listFinSuperNodeMiningDetail.get(i);
                    		miningAmount = finSuperNodeMiningDetail.getMiningAmount();
                    		id = finSuperNodeMiningDetail.getId();
                    		distBalOriginal = distBalOriginal.add(miningAmount).setScale(9, BigDecimal.ROUND_DOWN);
                    		if (id == 0) {
                    			return;
                    		} else {
                    			ids = ids + id + ", ";
                    		}
                    	}
                    	if (!StringUtils.isEmpty(ids) && ids.length() > 0) {
                    		ids = ids.substring(0, ids.length() - 2);
                    	} else {
                    		log.info("理财报警REWARDTASK:【新人加成-区块奖励记录生成(1周区块)-生成】统计分配人数异常，ids = " + ids);
                    		return;
                    	}
                    	
                    	/**
                    	 * 本周新人加成最多发送188 * 0.05
                    	 * intDistUsers
                    	 */
                    	BigDecimal calDistAmount = new BigDecimal(intDistUsers).multiply(new BigDecimal(9.4)).setScale(9, BigDecimal.ROUND_DOWN);
                    	
                    	if ((distBalOriginal.multiply(BigDecimal.valueOf(0.98))).compareTo(calDistAmount) > 0) {
                    		/*上限限制*/
                    		distBal = calDistAmount;
                    	} else {
                    		/*98% 产出进行发放*/
                        	distBal = distBalOriginal.multiply(BigDecimal.valueOf(0.98)).setScale(9, BigDecimal.ROUND_DOWN);
                    	}
                    	
                    	/**
                    	 * 开启事务
                    	 */
                    	List<OneSql> sqls = new ArrayList<>();
            			TransactionObject txObj = new TransactionObject();
                    	
                    	sql = "insert into fin_user_reward_status (seqNo, distTime, superNodeProfitCount, distType, "
                    		+ "distBal, distBalOriginal, distStatus, distFlag, distStartTime, distEndTime) "
                    		+ "values ('" + seqNo + "', '" + distTime + "', " + superNodeProfitCount + ", " + distType + ", " + distBal + ", "
                    		+ "" + distBalOriginal + ", 0, 1, '" + distStartTime + "', '" + distEndTime + "')";
                    	log.info("sql = " + sql);
                    	sqls.add(new OneSql(sql, 1, null, "vip_financial"));
                    	
                    	/*更新 fin_supernode_mining_detail 中的批次状态*/
//                    	update fin_supernode_mining_detail a inner join (
//                    	select * from fin_supernode_mining_detail where sNodeType = 1 and sNodeBelType = 1 and profitBatchNo = 0 and height <= 239040) b  
//                    	on a.id = b.id set a.profitBatchNo = 9;
                    	sql = "update fin_supernode_mining_detail set profitBatchNo = " + seqNo + " "
                    		+ "where sNodeBelType != 1 and profitBatchNo = 0 and sNodeShowFlag = 1 and id in (" + ids + ")";
                    	log.info("sql = " + sql);
                    	sqls.add(new OneSql(sql, superNodeProfitCount, null, "vip_financial"));
                    	/**
                    	 * NewUserDetailWork		更新开跑状态		新人加成奖励分配记录生成-跑的时候开
                    	 * NewUserRewardProductWork	更新下次启动时间		新人加成-区块奖励记录生成(1周区块)-生成-用distTime
                    	 */
                    	sql = "update fin_task set taskTime = '" + distTime + "', taskFlag = 1 where taskName = 'NewUserDetailWork' ";
                    	log.info("sql = " + sql);
                    	sqls.add(new OneSql(sql, 1, null, "vip_financial"));
                    	
                    	/**
                    	 * NewUserRewardProductWork		1周后的启动时间
                    	 */
                    	/*VIP新人开始时间*/
                    	String nextDistStartDate = sdf.format(TimeUtil.getBeforeTime(7));
                    	String nextDistStartTime = nextDistStartDate + " 04:00:00";
                    	String nextDistEndTime = nextDistStartDate + " 18:00:00";
                    	sql = "update fin_task set taskTime = '" + nextDistStartTime + "', "
                    		+ "distStartTime = '" + nextDistStartTime + "', "
                    		+ "distEndTime = '" + nextDistEndTime + "' "
                    		+ "where taskName = 'NewUserRewardProductWork' ";
                    	log.info("sql = " + sql);
                    	sqls.add(new OneSql(sql, 1, null, "vip_financial"));
                    	
                    	txObj.excuteUpdateList(sqls);
                        if (txObj.commit()) {
                        	
                        } else {
                        	log.info("理财报警REWARDERROR:【新人加成-区块奖励记录生成(1周区块)-生成】发生异常");
                        }
                    } else {
                    	if (intLogInfoFlag == 10) {
                    		log.info("理财报警REWARDINFO:【新人加成-区块奖励记录生成(1周区块)-生成】,产生的区块数量不足，当前产生数量 = " + listFinSuperNodeMiningDetail.size());
                    	}
                    }
                    showInfoFlag = true;
                } else {
                	if (intLogInfoFlag == 10) {
                		log.info("理财报警REWARDINFO:【新人加成-区块奖励记录生成(1周区块)-生成】,本次没有需要执行的分配资金记录任务 = " + sql);
                	}
                	return;
                }
                
                long endTime = System.currentTimeMillis();
                if (showInfoFlag) {
                	updateTask(financialTask, startTime, 0, 0);
                	log.info("理财报警REWARDTASK:【新人加成-区块奖励记录生成(1周区块)-生成】... 耗时：" + (endTime - startTime) + ", 分配数量 = " + distBal + ", 分配人数 = " + intDistUsers);
                }
            } catch (Exception e) {
                log.info("理财报警REWARDERROR:【新人加成-区块奖励记录生成(1周区块)-生成】发生不控异常,异常信息", e);
                updateTask(financialTask, startTime, 1, 1);
            } finally {
                workFlag = true;
            }
        } else {
        	log.info("理财报警REWARDERROR:【新人加成-区块奖励记录生成(1周区块)-生成】,上一轮定时任务没有结束，本轮不需要执行");
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
    	NewUserRewardProductWork snrpw = new NewUserRewardProductWork("", "");
    	snrpw.run();
//    	FinThreadTest ft = new FinThreadTest();
//    	ft.start();
//    	FinThreadTest ft2 = new FinThreadTest();
//    	ft2.start();
    }
}
