package com.world.model.financialproift.thread;

import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.entity.financialproift.FinProfitAssignDetail;
import com.world.util.financialproift.FinancialProiftUtils;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author Ethan
 * @Date 2019-07-30 14:04
 * @Description 超级节点奖励线程类
 **/
public class SuperNodeRewardThread extends Thread {

    private static Logger log = Logger.getLogger(SuperNodeRewardThread.class);

    private final FinProfitAssignDetail rewardDetail;
    private final CountDownLatch countDownLatch;

    private String sql = "";

    public SuperNodeRewardThread(FinProfitAssignDetail rewardDetail, CountDownLatch countDownLatch) {
        this.rewardDetail = rewardDetail;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {

            /****************************************
             *             本线程业务逻辑
             * 1 更新vip_main.pay_user_financial 静态收益 curstaticProfit(当前静态收益每次复投清空) curstaticProfitUsdt(当前静态收益折算USDT每次复投清空) staticProfitSum(静态收益累积) staticProfitSumUsdt(静态收益累积USDT) superNodeAmount(超级主节点累积收益) superNodeAmountUsdt(超级主节点累积收益折算USDT)
             *
             * 2 更新vip_main.pay_user_financial 余额 balance
             *
             * 3 添加vip_main.bill_financial 超级节点奖励结算资金流水
             *
             * 4 更新vip_financial.fin_profit_assign_detail 更新执行完成标识 flag=1
             ***************************************/


            //vip权重分得收益
            List<OneSql> sqls = new ArrayList<>();
            TransactionObject txObj = new TransactionObject();
            long currentTime = System.currentTimeMillis();
            long userId = rewardDetail.getProfituserid();
            
            /*出局判断*/
			boolean outUserFlag = false;
			outUserFlag = FinancialProiftUtils.outUserJudge(userId + "");
			/*流水类型*/
			int fundsTypeSelf = 5360;
            String fundsNameSelf = "VIP分红奖励";
			if (outUserFlag) {
				log.info("VIP分红转入公司账号");
				/**
				 * 3、公司理财账户点位资金接收	pay_user_financial balance = balance + douProfitAmount
				 * lvwa1900@163.com
				 * 用户ID：1216832 
				 */
				sql = "update pay_user_financial set balance = balance + " + rewardDetail.getUsdtamount() + " "
					+ "where userid = 1216832 and fundstype = 10 ";
				log.info("理财报警:HierarchyThread sql = " + sql);
				sqls.add(new OneSql(sql, 1, null, "vip_main"));
				/*4、1216832 记录流水fundsTypeComp  fundsNameComp */
				sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, "
					+ "remark, vdsUsdtPrice, investProPeriod, matrixLevel) "
					+ "select 1216832, userName, " + fundsTypeSelf + ", " + rewardDetail.getUsdtamount() + ", "
					+ "" + currentTime + " , (balance + profit + insureInvestFreezeAmount), "
					+ "51, '" + fundsNameSelf + "', '" + userId + "', 0, 0, 0 "
					+ "from pay_user_financial where userid = 1216832 and fundstype = 10 for update";
				log.info("理财报警:HierarchyThread sql = " + sql);
				sqls.add(new OneSql(sql, 1, null, "vip_main"));
			} else {
				/* 1. 更新vip_main.pay_user_financial 静态收益 */
	            /**
	             * curstaticProfit			当前静态收益每次复投清空
	             * curstaticProfitUsdt		当前静态收益折算USDT每次复投清空
	             * staticProfitSum			静态收益累积
	             * staticProfitSumUsdt		静态收益累积USDT
	             * superNodeAmount          超级主节点累积收益
	             * superNodeAmountUsdt      超级主节点累积收益折算USDT
	             */
	            sql = "update pay_user_financial set "
	            	+ "curstaticProfit = curstaticProfit + " + rewardDetail.getProfitamount() + ", "
	            	+ "curstaticProfitUsdt = curstaticProfitUsdt + " + rewardDetail.getUsdtamount() + ", "
	            	+ "staticProfitSum = staticProfitSum +" + rewardDetail.getProfitamount() + ", "
	                + "staticProfitSumUsdt = staticProfitSumUsdt + " + rewardDetail.getUsdtamount() + ", "
	                + "superNodeAmount = superNodeAmount + " + rewardDetail.getProfitamount() + ", "
	                + "superNodeAmountUsdt = superNodeAmountUsdt + " + rewardDetail.getUsdtamount() + " "
	                + "where userid = " + userId + " and fundstype = 51 ";
	            log.info("SuperNodeRewardThread-sql = " + sql);
	            sqls.add(new OneSql(sql, 1, null, "vip_main"));
	            
	            /* 2. 更新vip_main.pay_user_financial 余额 balance*/
	            sql = "update pay_user_financial set balance = balance + " + rewardDetail.getUsdtamount()
	                + " where userid = " + userId + " and fundstype = 10 ";
	            log.info("SuperNodeRewardThread-sql = " + sql);
	            sqls.add(new OneSql(sql, 1, null, "vip_main"));
	            
	            /* 3. 添加vip_main.bill_financial 超级节点奖励结算资金流水*/
	            sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, "
	                + "remark, vdsUsdtPrice, investProPeriod, matrixLevel) "
	                + "select " + userId + ", userName, " + fundsTypeSelf + "," + rewardDetail.getUsdtamount() + ", "
	                + "" + currentTime + ", (balance), "
	                + "10, '" + fundsNameSelf + "', '', 0, 0, 0 "
	                + "from pay_user_financial where userid = " + userId + " and fundstype = 10 for update";
	            log.info("SuperNodeRewardThread-sql = " + sql);
	            sqls.add(new OneSql(sql, 1, null, "vip_main"));
			}
            
            /* 4. vip_financial库 fin_profit_assign_detail表 flag=1*/
            sql = "update fin_profit_supernode_detail set flag = 1 where id = " + rewardDetail.getId() + " and flag = 0 ";
            log.info("SuperNodeRewardThread-sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_financial"));
            
            txObj.excuteUpdateList(sqls);
            if (txObj.commit()) {
//                log.info("【VIP分红奖励-结算】用户【" + userId + "】VIP分红奖励分配成功");
            } else {
                log.info("理财报警REWARDERROR:【VIP分红奖励-结算】用户【" + userId + "】VIP分红奖励结算失败");
            }
        } catch (Exception e) {
            log.info("理财报警REWARDERROR:【VIP分红奖励-结算】发生不可控异常,异常信息:{}", e);

        } finally {
            countDownLatch.countDown();
        }
    }
}
