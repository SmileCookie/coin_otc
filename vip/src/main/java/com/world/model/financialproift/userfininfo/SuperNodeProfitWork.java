package com.world.model.financialproift.userfininfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.dao.task.Worker;
import com.world.model.entity.financialproift.FinSupernodeProfit;
import com.world.model.entity.financialproift.FinancialTask;

/**
 * @Author Ethan
 * @Date 2019-07-25 13:47
 * @Description 超级节点奖励记录定时任务
 **/

public class SuperNodeProfitWork extends Worker {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	private static final BigDecimal THRESHOLD = BigDecimal.valueOf(1E5);

    private String sql = "";
    private boolean workFlag = true;
//    private BigDecimal PROFIT_RATE = BigDecimal.valueOf(0.98);

    public SuperNodeProfitWork(String name, String des) {
        super(name, des);
    }

    @SuppressWarnings({ "unused", "null" })
	@Override
    public void run() {
        long startTime = System.currentTimeMillis();

        FinancialTask financialTask;

        boolean taskflag = false;
        try {
            /**
             * 取得类名称，作为任务名称
             * 【超级节点奖励分配记录】
             */
            sql = "select * from fin_task where taskName = 'SuperNodeProfitWork'";
            log.info("SuperNodeProfitWork sql = " + sql);
            financialTask = (FinancialTask) Data.GetOne("vip_financial", sql, null, FinancialTask.class);
            if (null == financialTask) {
				log.info("理财报警REWARDINFO:【超级节点奖励分配记录】没有需要执行的任务");
				return;
			} else {
				if(1 == financialTask.getTaskFlag()) {
					taskflag = true;
				} else {
					taskflag = false;
					log.info("理财报警REWARDINFO:【超级节点奖励分配记录】没有需要执行的任务，任务状态 = " + financialTask.getTaskFlag());
					return;
				}
			}
            log.info("SuperNodeProfitWork taskflag:" + taskflag);
        } catch (Exception e1) {
            financialTask = new FinancialTask();
            financialTask.setTaskName("SuperNodeProfitWork");
            log.info("理财报警REWARDERROR:【超级节点奖励分配记录】定时任务启动失败:SuperNodeProfitWork", e1);
        }
        if (workFlag && taskflag) {
            workFlag = false;
            try {
                log.info("【超级节点奖励分配记录】定时任务开始...");
                //1. 初始化分红奖励数据
                //获取累计收益
//                BigDecimal totalBalance = getTotalProfit();
//                //上次超级节点累计收益
//                BigDecimal preTotalBalance = getPreTotalProfit();
//                //本次分配收益
//                BigDecimal profit_original = totalBalance.subtract(preTotalBalance);
//
//                BigDecimal profit_real = profit_original.multiply(PROFIT_RATE).setScale(9, BigDecimal.ROUND_DOWN);

                sql = "select * from fin_supernode_profit and status = 0 ";
                log.info("SuperNodeProfitWork sql:" + sql);
                //获取所有未分配记录
                List<FinSupernodeProfit> noAssignList = Data.QueryT("vip_financial", sql, null, FinSupernodeProfit.class);
                
                boolean ifFirst = false;
                TransactionObject txObj = new TransactionObject();
                List<OneSql> sqls = new ArrayList<>();
                //2. 无未分配记录
                if (noAssignList == null && noAssignList.size() == 0) {
                    ifFirst = true;
                }
                
//                if (ifFirst) {
//                    if (totalBalance.compareTo(BigDecimal.ZERO) == 1) {
//                        sql = "INSERT INTO fin_supernode_profit(profit, profitoriginal, profittime, updatetime, status, profitUsers, vdsAmount_peruser, usdtAmount_peruser, usdtPrice, balance, balancepre) VALUES (" + profit_real.toPlainString() + ", " + profit_original.toPlainString() + ", now(), NULL, 0, NULL, NULL, NULL, NULL, " + totalBalance.toPlainString() + ", 0.000000000)";
//                        log.info("SuperNodeProfitWork sql =" + sql);
//                        sqls.add(new OneSql(sql, 1, null, "vip_financial"));
//                    }
//                } else {
//                    //分配金额是否满足阈值
//                    if (profit_original.compareTo(THRESHOLD) == 1) {
//                        sql = "INSERT INTO fin_supernode_profit(profit, profitoriginal, profittime, updatetime, status, profitUsers, vdsAmount_peruser, usdtAmount_peruser, usdtPrice, balance, balancepre) VALUES (" + profit_real.toPlainString() + ", " + profit_original.toPlainString() + ", now(), NULL, 0, NULL, NULL, NULL, NULL, " + totalBalance.toPlainString() + ", " + preTotalBalance.toPlainString() + ")";
//                        log.info("SuperNodeProfitWork sql =" + sql);
//                        sqls.add(new OneSql(sql, 1, null, "vip_financial"));
//                    }
//                }
                if (sqls.size()>0) {
                    txObj.excuteUpdateList(sqls);
                    if (txObj.commit()) {
                        log.info("理财报警REWARDINFO:任务【" + financialTask.getTaskName() + "】执行状态更新成功");
                    } else {
                        log.info("理财报警REWARDINFO:任务【" + financialTask.getTaskName() + "】执行状态更新失败");
                    }
                }

                updateTask(financialTask, startTime, 0);
                long endTime = System.currentTimeMillis();
                log.info("【超级节点奖励分配记录】定时任务结束... 耗时：" + (endTime - startTime));
            } catch (Exception e) {
                log.info("理财报警REWARDWARN:【超级节点奖励分配记录】发生不控异常,异常信息:", e);
                updateTask(financialTask, startTime, 1);
            } finally {
                workFlag = true;
            }
        }

    }

    /**
     * 累计产出收益
     *
     * @return
     */
    @SuppressWarnings({ "unchecked", "unused" })
	private BigDecimal getTotalProfit() {
        String totalProfitSQL = "select ifnull(sum(snodebalance)-sum(snodepayamount),0) as total from fin_supernode where snodestate = 1 ";
        List<BigDecimal> listTotalProfit = (List<BigDecimal>) Data.GetOne("vip_financial", totalProfitSQL, null);

        if (listTotalProfit != null && listTotalProfit.size() > 0) {
            return listTotalProfit.get(0).setScale(9, BigDecimal.ROUND_DOWN);
        }
        log.info("【超级节点奖励分配记录】 listTotalProfit.size=0");
        return BigDecimal.ZERO;

    }

    /**
     * 上一次分配金额
     *
     * @return
     */
    @SuppressWarnings({ "unused", "unchecked" })
	private BigDecimal getPreTotalProfit() {
        String sql = "select ifnull(sum(profitoriginal),0) as pretotal from fin_supernode_profit";
        List<BigDecimal> list = (List<BigDecimal>) Data.GetOne("vip_financial", sql, null);

        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return BigDecimal.ZERO;
    }

    private void updateTask(FinancialTask financialTask, long startTime, int taskFlag) {
        TransactionObject txObj = new TransactionObject();
        List<OneSql> sqls = new ArrayList<>();

        String taskInfo = "执行成功";
        if (taskFlag != 0)
            taskInfo = "执行失败，请查看日志记录";

        /* 更新任务执行状态 */
        sql = "update fin_task set handleTime=now(),nowStep = 1,taskFlag = " + taskFlag + ",taskError=" + taskFlag
                + " where taskName='" + financialTask.getTaskName() + "' ";
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
        SuperNodeProfitWork work=new SuperNodeProfitWork("","");
        work.run();
    }
}
