package com.world.model.financialproift.userfininfo.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.entity.financialproift.FinProfitAssignDetail;

/**
 * @Author Ethan
 * @Date 2019-07-30 14:04
 * @Description 新增用户奖励线程类
 **/
public class NewUserRewardThread extends Thread {

	private static Logger log = Logger.getLogger(NewUserRewardThread.class);

	private final FinProfitAssignDetail rewardDetail;
	private final CountDownLatch countDownLatch;

	public NewUserRewardThread(FinProfitAssignDetail rewardDetail, CountDownLatch countDownLatch) {
		this.rewardDetail = rewardDetail;
		this.countDownLatch = countDownLatch;
	}

	@Override
	public void run() {
		long userId = rewardDetail.getProfituserid();
		//BigDecimal rewardAmount = rewardDetail.getProfitamount();
		try {

			// vip权重分得收益
			List<OneSql> sqls = new ArrayList<>();
			TransactionObject txObj = new TransactionObject();
			long currentTime = System.currentTimeMillis();
			String sql = "";
			
			/* vip_financial库 更新fin_ecorewardassign表 billstatus=1 */
			sql = "update fin_profit_newvip_detail set flag = 1 where id = " + rewardDetail.getId();
			log.info("HierarchyThread sql = " + sql);
			sqls.add(new OneSql(sql, 1, null, "vip_financial"));
			
			/* 1、vip_main库，更新 pay_user_financial 表 profit, realizedPnl字段 */
//			executesql = "update pay_user_financial set profit = profit + " + rewardDetail.getProfitamount()
//					+ ", realizedPnl = realizedPnl + " + rewardDetail.getProfitamount() + " " + "where userid = " + userId
//					+ " and fundstype = 51 ";
//			log.info("理财报警REWARDINFO:start2Profit-sql = " + executesql);
//			sqls.add(new OneSql(executesql, 1, null, "vip_main"));
			/**
             * 放到静态收益, 此处只需要加
             * curstaticProfit      当前静态收益每次复投清空
             * curstaticProfitUsdt    当前静态收益折算USDT每次复投清空
             * staticProfitSum      静态收益累积
             * staticProfitSumUsdt    静态收益累积USDT  
             */
			sql = "update pay_user_financial set "
				+ "curstaticProfit = curstaticProfit + " + rewardDetail.getProfitamount() + " , "
				+ "curstaticProfitUsdt = curstaticProfitUsdt + " + rewardDetail.getUsdtamount() + " , "
				+ "staticProfitSum = staticProfitSum +" + rewardDetail.getProfitamount() + " , "
				+ "staticProfitSumUsdt = staticProfitSumUsdt + " + rewardDetail.getUsdtamount() + ", "
				+ "newVipUserAmount = newVipUserAmount + " + rewardDetail.getProfitamount() + " , "
				+ "newVipUserAmountUsdt = newVipUserAmountUsdt + " + rewardDetail.getUsdtamount() 
			    + " where userid = " + userId + " and fundstype = 51 ";
			log.info("NewUserRewardThread:start2Profit-sql = " + sql);
			sqls.add(new OneSql(sql, 1, null, "vip_main"));
			
			/* 2. 更新vip_main.pay_user_financial 余额 balance*/
			sql = "update pay_user_financial set balance = balance + " + rewardDetail.getUsdtamount() + " "
				+ "where userid = " + userId + " and fundstype = 10 ";
            log.info("SuperNodeRewardThread-sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_main"));
			
			/* 2、vip_main库，记入流水bill_financial 流水类型为5370 */
            sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName,remark, "
            	+ "vdsUsdtPrice, investProPeriod, matrixLevel)"
				+ " select " + userId + ", userName, 5370 ," + rewardDetail.getUsdtamount() + ", "
				+ "" + currentTime + " , (balance), " + "10, '新人加成', '', 0, 0, 0 "
				+ " from pay_user_financial where userid = " + userId + " and fundstype = 10 for update";
			log.info("理财报警REWARDINFO:start2Profit-sql = " + sql);
			sqls.add(new OneSql(sql, 1, null, "vip_main"));
			
			txObj.excuteUpdateList(sqls);
			if (txObj.commit()) {
//				log.info("【新人加成-结算】用户【" + userId + "】新增用户奖励分配成功");
			} else {
				log.info("理财报警REWARDERROR:【新人加成-结算】用户【" + userId + "】结算失败");
			}
		} catch (Exception e) {
			log.info("理财报警REWARDERROR:【新人加成-结算】发生不可控异常,异常信息:{}", e);

		} finally {
			countDownLatch.countDown();
		}
	}
}
