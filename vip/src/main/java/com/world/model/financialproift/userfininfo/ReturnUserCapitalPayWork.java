package com.world.model.financialproift.userfininfo;

import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.dao.task.Worker;
import com.world.model.entity.financialproift.FinUserReturnOrderInfo;
import com.world.model.entity.financialproift.FinUserRewardStatus;
import com.world.model.entity.financialproift.FinancialTask;
import com.world.util.financialproift.FinancialProiftUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ReturnUserCapitalPayWork extends Worker {
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
//    private boolean showInfoFlag = false;
    /*日志打印计数器执行10次打印一次*/
    private static int intLogInfoFlag = 1;

    public ReturnUserCapitalPayWork(String name, String des) {
        super(name, des);
    }

	@Override
    public void run() {
		if (intLogInfoFlag == 10) {
			log.info("理财报警REWARDINFO:【回本用户奖励支付】开始");
		}
        long startTime = System.currentTimeMillis();
        FinancialTask financialTask;
        boolean taskflag = false;
//        showInfoFlag = false;
        try {
            /**
             * 取得类名称，作为任务名称
             * 回本用户奖励支付
             */
            sql = "select * from fin_task where taskName = 'ReturnUserCapitalPayWork' and now() >= taskTime "
            	+ "and now() >= distStartTime and now() <= distEndTime and taskFlag = 1 ";
            log.info("NewUserRewardProductWork sql = " + sql);
            financialTask = (FinancialTask) Data.GetOne("vip_financial", sql, null, FinancialTask.class);
            if (null == financialTask) {
            	if (intLogInfoFlag == 10) {
            		log.info("理财报警REWARDINFO:【回本用户奖励支付】没有需要执行的任务");
            	}
				return;
			} else {
				if(1 == financialTask.getTaskFlag()) {
					/*可以执行*/
					taskflag = true;
				} else {
					taskflag = false;
					if (intLogInfoFlag == 10) {
						log.info("理财报警REWARDINFO:【回本用户奖励支付】没有需要执行的任务，任务状态 = " + financialTask.getTaskFlag());
					}
					return;
				}
			}
            log.info("ReturnUserCapitalPayWork taskflag:" + taskflag);
        } catch (Exception e1) {
            financialTask = new FinancialTask();
            financialTask.setTaskName("NewUserRewardProductWork");
            log.info("理财报警REWARDERROR:【回本用户奖励支付】定时任务启动失败,失败原因:", e1);
        } finally {
        	if (intLogInfoFlag == 10) {
        		intLogInfoFlag = 0;
        	}
        	intLogInfoFlag++;
        }
        
        if (workFlag && taskflag) {
            try {
            	workFlag = false;
                log.info("回本用户奖励支付，定时任务开始...");
                /**
                 * 每日记录检查是否已经生成过，没有生成的进行生成
                 */
                /*批次设置*/
    			SimpleDateFormat sdfBatch = new SimpleDateFormat("yyyyMMdd");
                String curDateBatch = sdfBatch.format(new Date());
                String distType = "8";
            	String batchNo = curDateBatch + distType;
                /*判断记录是否已生成*/
                sql = "select * from fin_user_reward_status where distType = 8 and seqNo = '" + batchNo + "' ";
                log.info("sql = " + sql);
                FinUserRewardStatus finUserRewardStatus = (FinUserRewardStatus) Data.GetOne("vip_financial", sql, null, FinUserRewardStatus.class);
                /**
                 * 产出金额和分配金额
                 */
//                BigDecimal distBalOriginal = BigDecimal.ZERO;
            	BigDecimal distBal = BigDecimal.ZERO;
            	BigDecimal yetAmt = BigDecimal.ZERO;
            	int distStatus = 0;
                if (null == finUserRewardStatus) {
                	log.info("理财报警REWARDERROR:【回本用户奖励支付】异常，超级节点资金没有准备好 = " + sql);
                	return;
                } else {
//                	distBalOriginal = finUserRewardStatus.getDistBalOriginal();
                	distBal = finUserRewardStatus.getDistBal();
                	yetAmt = finUserRewardStatus.getYetAmt();
                	distStatus = finUserRewardStatus.getDistStatus();
                }
                if (1 == distStatus) {
                	log.info("理财报警REWARDTASK:【回本用户奖励支付】已分配过 = " + distStatus);
                	return;
                }
                if (distBal.compareTo(BigDecimal.ZERO) <= 0) {
                	log.info("理财报警REWARDERROR:【回本用户奖励支付】异常，剩余分配资金不足 = " + yetAmt);
                	return;
                }
                /*获取待分配的用户记录*/
                sql = "select * from fin_userreturnorderinfo where batchNo = '" + batchNo + "' and dealFlag = 0 ";
                log.info("sql = " + sql);
    			List<Bean> listFinUserReturnOrderInfo = (List<Bean>) Data.Query("vip_financial", sql, null, FinUserReturnOrderInfo.class);
                if (null == listFinUserReturnOrderInfo || listFinUserReturnOrderInfo.size() < 1) {
                	log.info("理财报警REWARDERROR:【回本用户奖励支付】异常，用户回本清单没有准备好 = " + sql);
                	return;
                }
                
                /*定义循环接收变量*/
                FinUserReturnOrderInfo finUserReturnOrderInfo;
                int userId = 0;
//                String userName = "";
                BigDecimal expectProfitUsdt = BigDecimal.ZERO;
                /*累积发放金额*/
                BigDecimal sumPayUsdt = BigDecimal.ZERO;
                int cntPay = 0;
                /**
                 * 需要发送邮件的内容
                 */
                BigDecimal investAmount = BigDecimal.ZERO;
                BigDecimal investAvergPrice = BigDecimal.ZERO;
                BigDecimal staticProfitSumUsdt = BigDecimal.ZERO;
                Date profitTime = null;
                Date resetProfitTime = null;
                
                for (int i = 0; i < listFinUserReturnOrderInfo.size(); i++) {
                	finUserReturnOrderInfo = (FinUserReturnOrderInfo) listFinUserReturnOrderInfo.get(i);
                	/*应回本金额*/
                	userId = finUserReturnOrderInfo.getUserId();
                	expectProfitUsdt = finUserReturnOrderInfo.getExpectProfitUsdt();
                	investAmount = finUserReturnOrderInfo.getInvestAmount();
                	investAvergPrice = finUserReturnOrderInfo.getInvestAvergPrice();
                	staticProfitSumUsdt = finUserReturnOrderInfo.getStaticProfitSumUsdt();
                	profitTime = finUserReturnOrderInfo.getProfitTime();
                	resetProfitTime = finUserReturnOrderInfo.getResetProfitTime();
                	
                	expectProfitUsdt = expectProfitUsdt.subtract(staticProfitSumUsdt).setScale(4, BigDecimal.ROUND_DOWN);
                	if(expectProfitUsdt.compareTo(BigDecimal.ZERO) <= 0) {
                		log.info("理财报警REWARDTASK:【回本用户奖励支付】发放continue = " + yetAmt + ", expectProfitUsdt = " + expectProfitUsdt);
                		expectProfitUsdt = BigDecimal.ZERO;
                	}
                	if (yetAmt.compareTo(expectProfitUsdt) < 0) {
                		log.info("理财报警REWARDTASK:【回本用户奖励支付】发放break = " + yetAmt + ", expectProfitUsdt = " + expectProfitUsdt);
                		break;
                	}
                	/**
                	 * 开启事务
                	 * 回本状态更新
                	 * 回本状态更新all表
                	 * yetAmt 资金扣减
                	 * fin_userfinancialinfo 更新状态
                	 * pay_user_financial	回本资金  静态收益
                	 * bill_financial		回本流水
                	 */
                	List<OneSql> sqls = new ArrayList<>();
        			TransactionObject txObj = new TransactionObject();
        			long currentTime = System.currentTimeMillis();
        			
        			/*回本状态更新*/
        			sql = "update fin_userreturnorderinfo set dealFlag = 1, dealTime = now(), authPayFlag = 3 where userId = " + userId;
                	log.info("sql = " + sql);
                	sqls.add(new OneSql(sql, 1, null, "vip_financial"));
                	
                	/*回本状态更新all表*/
        			sql = "update fin_userreturnorderinfoall set dealFlag = 1, dealTime = now(), authPayFlag = 3 "
        				+ "where userId = " + userId + " and batchNo = '" + batchNo + "' ";
                	log.info("sql = " + sql);
                	sqls.add(new OneSql(sql, 1, null, "vip_financial"));
                	
                	/*资金扣减*/
        			sql = "update fin_user_reward_status set yetAmt = yetAmt - " + expectProfitUsdt + ", distStatus = 1, distFlag = 0 "
        				+ "where distType = 8 and seqNo = '" + batchNo + "' and yetAmt >= " + expectProfitUsdt + " ";
                	log.info("sql = " + sql);
                	sqls.add(new OneSql(sql, 1, null, "vip_financial"));
                	
                	/*更新支付状态*/
        			sql = "update fin_userfinancialinfo set authPayFlag = 3, outTime = now() where userId = " + userId + " and authPayFlag = 2 ";
                	log.info("sql = " + sql);
                	sqls.add(new OneSql(sql, 1, null, "vip_financial"));
                	
                	/**
                     * 放到静态收益, 此处只需要加
                     * curstaticProfit      当前静态收益每次复投清空
                     * curstaticProfitUsdt    当前静态收益折算USDT每次复投清空
                     * staticProfitSum      静态收益累积
                     * staticProfitSumUsdt    静态收益累积USDT  
                     */
        			sql = "update pay_user_financial set "
        				+ "curstaticProfitUsdt = 0, "
        				+ "staticProfitSumUsdt = 0 "
        			    + "where userId = " + userId + " and fundstype = 51 ";
        			log.info("NewUserRewardThread:start2Profit-sql = " + sql);
        			sqls.add(new OneSql(sql, 1, null, "vip_main"));
        			
        			/* 2. 更新vip_main.pay_user_financial 余额 balance*/
        			sql = "update pay_user_financial set balance = balance + " + expectProfitUsdt + " "
        				+ "where userid = " + userId + " and fundstype = 10 ";
                    log.info("SuperNodeRewardThread-sql = " + sql);
                    sqls.add(new OneSql(sql, 1, null, "vip_main"));
        			
        			/* 2、vip_main库，记入流水bill_financial 流水类型为 5305 回本加成*/
                    sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName,remark, "
                    	+ "vdsUsdtPrice, investProPeriod, matrixLevel)"
        				+ " select " + userId + ", userName, 5305 ," + expectProfitUsdt + ", "
        				+ "" + currentTime + " , (balance), 10, '回本加成', '', 0, 0, 0 "
        				+ " from pay_user_financial where userId = " + userId + " and fundstype = 10 for update";
        			log.info("理财报警REWARDINFO:start2Profit-sql = " + sql);
        			sqls.add(new OneSql(sql, 1, null, "vip_main"));
        			
        			txObj.excuteUpdateList(sqls);
        			if (txObj.commit()) {
        				log.info("理财报警REWARDINFO:【回本用户奖励支付】发放成功 = " + userId + ", expectProfitUsdt = " + expectProfitUsdt);
        			} else {
        				log.info("理财报警REWARDTASK:【回本用户奖励支付】发放break = " + userId);
        				break;
        			}
                	cntPay++;
                	sumPayUsdt = sumPayUsdt.add(expectProfitUsdt);
                	yetAmt = yetAmt.subtract(expectProfitUsdt);
                	
                	try {
                		FinancialProiftUtils.sendUserReturnCapitalMsg(userId + "", investAmount, investAvergPrice, 
                				finUserReturnOrderInfo.getExpectProfitUsdt(), 
                				staticProfitSumUsdt, profitTime, resetProfitTime, new Date(),"7");
                	} catch (Exception e) {
                		log.info("理财报警REWARDERROR:【回本用户奖励支付】邮件发送失败 = " + userId, e);
                	}
                }
                
                long endTime = System.currentTimeMillis();
                updateTask(financialTask, startTime, 0, 0);
                log.info("理财报警REWARDTASK:【回本用户奖励支付】... 耗时：" + (endTime - startTime) + ", "
                		+ "分配回本加成用户数量 = " + cntPay + ", 回本加成总金额 = " + sumPayUsdt + ", 剩余金额 = " + yetAmt);
            } catch (Exception e) {
                log.info("理财报警REWARDERROR:【回本用户奖励支付】发生不控异常,异常信息", e);
                updateTask(financialTask, startTime, 1, 1);
            } finally {
                workFlag = true;
            }
        } else {
        	log.info("理财报警REWARDTASK:【回本用户奖励支付】,上一轮定时任务没有结束，本轮不需要执行");
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
        sql = "update fin_task set handleTime = now(), nowStep = 1, taskFlag = 0, taskError = " + taskError + " "
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
    	ReturnUserCapitalPayWork snrpw = new ReturnUserCapitalPayWork("", "");
    	snrpw.run();
//    	FinThreadTest ft = new FinThreadTest();
//    	ft.start();
//    	FinThreadTest ft2 = new FinThreadTest();
//    	ft2.start();
    }
}
