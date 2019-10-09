package com.world.model.financialproift.userfininfo;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.StringUtils;

import com.world.cache.Cache;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.dao.task.Worker;
import com.world.model.entity.financialproift.FinSuperNodeMiningDetail;
import com.world.model.entity.financialproift.FinUserRewardStatus;
import com.world.model.entity.financialproift.FinancialTask;
import com.world.util.date.TimeUtil;


public class ReturnUserCapitalProductWork extends Worker {
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

    public ReturnUserCapitalProductWork(String name, String des) {
        super(name, des);
    }

	@Override
    public void run() {
		if (intLogInfoFlag == 10) {
			log.info("理财报警REWARDINFO:【回本加成-区块奖励记录生成(1周区块)-生成】开始");
		}
        long startTime = System.currentTimeMillis();
        FinancialTask financialTask;
        boolean taskflag = false;
        showInfoFlag = false;
        try {
            /**
             * 取得类名称，作为任务名称
             * 回本加成-区块奖励记录生成(1周区块)-生成
             */
            sql = "select * from fin_task where taskName = 'ReturnUserCapitalProductWork' and now() >= taskTime "
            	+ "and now() >= distStartTime and now() <= distEndTime and taskFlag = 1 ";
            log.info("NewUserRewardProductWork sql = " + sql);
            financialTask = (FinancialTask) Data.GetOne("vip_financial", sql, null, FinancialTask.class);
            if (null == financialTask) {
            	if (intLogInfoFlag == 10) {
            		log.info("理财报警REWARDINFO:【回本加成-区块奖励记录生成(1周区块)-生成】没有需要执行的任务");
            	}
				return;
			} else {
				if(1 == financialTask.getTaskFlag()) {
					/*可以执行*/
					taskflag = true;
				} else {
					taskflag = false;
					if (intLogInfoFlag == 10) {
						log.info("理财报警REWARDINFO:【回本加成-区块奖励记录生成(1周区块)-生成】没有需要执行的任务，任务状态 = " + financialTask.getTaskFlag());
					}
					return;
				}
			}
            log.info("NewUserRewardProductWork taskflag:" + taskflag);
        } catch (Exception e1) {
            financialTask = new FinancialTask();
            financialTask.setTaskName("NewUserRewardProductWork");
            log.info("理财报警REWARDERROR:【回本加成-区块奖励记录生成(1周区块)-生成】定时任务启动失败,失败原因:", e1);
        } finally {
        	if (intLogInfoFlag == 10) {
        		intLogInfoFlag = 0;
        	}
        	intLogInfoFlag++;
        }
        
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
        
        if (workFlag && taskflag) {
            try {
            	workFlag = false;
                log.info("回本加成-区块奖励记录生成(1周区块)-生成，定时任务开始...");
                /**
                 * 每日记录检查是否已经生成过，没有生成的进行生成，已生成的不再生成
                 */
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String curDate = sdf.format(new Date());
                /*批次处理*/
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
                String curBatchNo = sdf2.format(new Date());
                /*回本加成 类型 8*/
                String distType = "8";
                String seqNo = curBatchNo + distType;
                log.info("curDate = " + curDate);
                /*判断记录是否已生成*/
                sql = "select * from fin_user_reward_status where distType = 8 and seqNo = '" + seqNo + "' ";
                log.info("sql = " + sql);
                
                /**
                 * 产出金额和分配金额
                 */
                BigDecimal distBalOriginal = BigDecimal.ZERO;
            	BigDecimal distBal = BigDecimal.ZERO;
            	BigDecimal yetAmt = BigDecimal.ZERO;
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
                    	/*8 = 回本加成*/
                    	int superNodeProfitCount = listFinSuperNodeMiningDetail.size();
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
                    		log.info("理财报警REWARDTASK:【回本加成-区块奖励记录生成(1周区块)-生成】统计分配区块异常，ids = " + ids);
                    		return;
                    	}
                    	
                    	/**
                    	 * 98%
                    	 */
                        distBal = distBalOriginal.multiply(BigDecimal.valueOf(0.98)).setScale(9, BigDecimal.ROUND_DOWN);
                        yetAmt = distBal.multiply(new BigDecimal(vdsUsdt)).setScale(9, BigDecimal.ROUND_DOWN);
                        /*区间开始时间*/
                        String distStartTime = sdf.format(TimeUtil.getBeforeTime(-7)) + " 04:00:00";
                    	/**
                    	 * 开启事务
                    	 */
                    	List<OneSql> sqls = new ArrayList<>();
            			TransactionObject txObj = new TransactionObject();
                    	
                    	sql = "insert into fin_user_reward_status (seqNo, distTime, superNodeProfitCount, distType, "
                    		+ "distBal, distBalOriginal, yetAmt, distStatus, distFlag, distStartTime, distEndTime, usdtPrice) "
                    		+ "values ('" + seqNo + "', '" + distTime + "', " + superNodeProfitCount + ", " + distType + ", " + distBal + ", "
                    		+ "" + distBalOriginal + ", " + yetAmt + ", 0, 1, '" + distStartTime + "', '" + distEndTime + "', " + vdsUsdt + " )";
                    	log.info("sql = " + sql);
                    	sqls.add(new OneSql(sql, 1, null, "vip_financial"));
                    	
                    	sql = "update fin_supernode_mining_detail set profitBatchNo = " + seqNo + " "
                    		+ "where sNodeBelType != 1 and profitBatchNo = 0 and sNodeShowFlag = 1 and id in (" + ids + ")";
                    	log.info("sql = " + sql);
                    	sqls.add(new OneSql(sql, superNodeProfitCount, null, "vip_financial"));
                    	/**
                    	 * ReturnUserOrderWork		更新开跑状态	回本用户顺序列表生成
                    	 * ReturnUserCapitalProductWork	更新下次启动时间		回本加成-区块奖励记录生成(1周区块)-生成
                    	 */
                    	/*回本加成-区块奖励记录生成(1周区块)-生成开始时间*/
                    	String nextDistStartDate = sdf.format(TimeUtil.getBeforeTime(7));
                    	/*下一个cal start 时间 */
//                    	String nextCallStartDate = curDate + " 04:00:00";
                    	String nextDistStartTime = nextDistStartDate + " 04:00:00";
                    	String nextDistEndTime = nextDistStartDate + " 18:00:00";
//                    	sql = "update fin_task set taskTime = '" + nextDistStartTime + "', "
//                    		+ "callStartDate = '" + nextCallStartDate + "', callEndDate = '" + nextDistStartTime + "', "
//                    		+ "taskFlag = 1 where taskName = 'ReturnUserOrderWork' ";
//                    	log.info("sql = " + sql);
//                    	sqls.add(new OneSql(sql, 1, null, "vip_financial"));
                    	
                    	/**
                    	 * ReturnUserCapitalProductWork		1周后的启动时间
                    	 */
                    	sql = "update fin_task set taskTime = '" + nextDistStartTime + "', "
                    		+ "distStartTime = '" + nextDistStartTime + "', "
                    		+ "distEndTime = '" + nextDistEndTime + "' "
                    		+ "where taskName = 'ReturnUserCapitalProductWork' ";
                    	log.info("sql = " + sql);
                    	sqls.add(new OneSql(sql, 1, null, "vip_financial"));
                    	
                    	/*结算 - 回本用户奖励支付*/
                    	String nextSettleDistStartTime = nextDistStartDate + " 06:00:00";
                    	sql = "update fin_task set taskFlag = 1, taskTime = '" + nextSettleDistStartTime + "', "
                    		+ "distStartTime = '" + nextDistStartTime + "', "
                    		+ "distEndTime = '" + nextDistEndTime + "' "
                    		+ "where taskName = 'ReturnUserCapitalPayWork' ";
                    	log.info("sql = " + sql);
                    	sqls.add(new OneSql(sql, 1, null, "vip_financial"));
                    	
                    	txObj.excuteUpdateList(sqls);
                        if (txObj.commit()) {
                        	
                        } else {
                        	log.info("理财报警REWARDERROR:【回本加成-区块奖励记录生成(1周区块)-生成】发生异常");
                        }
                    } else {
                    	if (intLogInfoFlag == 10) {
                    		log.info("理财报警REWARDINFO:【回本加成-区块奖励记录生成(1周区块)-生成】,产生的区块数量不足，当前产生数量 = " + listFinSuperNodeMiningDetail.size());
                    	}
                    	return;
                    }
                    showInfoFlag = true;
                } else {
                	if (intLogInfoFlag == 10) {
                		log.info("理财报警REWARDINFO:【回本加成-区块奖励记录生成(1周区块)-生成】,本次没有需要执行的分配资金记录任务 = " + sql);
                	}
                	return;
                }
                
                long endTime = System.currentTimeMillis();
                if (showInfoFlag) {
                	updateTask(financialTask, startTime, 0, 0);
                	log.info("理财报警REWARDTASK:【回本加成-区块奖励记录生成(1周区块)-生成】... 耗时：" + (endTime - startTime) + ", 分配数量 = " + distBal + ", 折算USDT = " + yetAmt);
                }
            } catch (Exception e) {
                log.info("理财报警REWARDERROR:【回本加成-区块奖励记录生成(1周区块)-生成】发生不控异常,异常信息", e);
                updateTask(financialTask, startTime, 1, 1);
            } finally {
                workFlag = true;
            }
        } else {
        	log.info("理财报警REWARDTASK:【回本加成-区块奖励记录生成(1周区块)-生成】,上一轮定时任务没有结束，本轮不需要执行");
        }
        
    }

    private void updateTask(FinancialTask financialTask, long startTime, int taskFlag, int taskError) {
        TransactionObject txObj = new TransactionObject();
        List<OneSql> sqls = new ArrayList<>();

        String taskInfo = "执行成功";
        if (taskFlag != 0) {
            taskInfo = "执行失败，请查看日志记录";
        }
        /* 更新任务执行状态 taskFlag = 0, */
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
    	ReturnUserCapitalProductWork snrpw = new ReturnUserCapitalProductWork("", "");
    	snrpw.run();
//    	FinThreadTest ft = new FinThreadTest();
//    	ft.start();
//    	FinThreadTest ft2 = new FinThreadTest();
//    	ft2.start();
    }
}
