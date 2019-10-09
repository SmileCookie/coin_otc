package com.world.model.financialproift.userfininfo.thread;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.entity.financialproift.FinancialBonus;
import com.world.util.financialproift.FinancialProiftUtils;

public class DynamicBonusDisThread extends Thread {
	
	private FinancialBonus financialBonus;
	/*sql语句*/
	private String sql = "";
	private static Logger log = Logger.getLogger(DynamicBonusDisThread.class.getName());
	private CountDownLatch countDownLatch;
	private BigDecimal vdsUsdt;
	private Date calStart;
	private Date calEnd;
	
	public DynamicBonusDisThread (FinancialBonus financialBonus, CountDownLatch countDownLatch, BigDecimal vdsUsdt, Date calStart, Date calEnd) {
		this.financialBonus = financialBonus;
		this.countDownLatch = countDownLatch;
		this.vdsUsdt = vdsUsdt;
		this.calStart = calStart;
		this.calEnd = calEnd;
	}
	
	@Override
	public void run() {
		try {
			long userId = financialBonus.getUser_id();
			int bonusType = financialBonus.getBonus_type();
//			long id = financialBonus.getId();
			/*总奖励金额*/
			BigDecimal bonusPrice = financialBonus.getBonus_price();
			BigDecimal profitAmount = bonusPrice;
//			.multiply(BigDecimal.valueOf(0.8))
//			BigDecimal newVipUserAmount = bonusPrice.subtract(profitAmount);
			/*95%给用户，5%转入新增VIP增值点位*/
//			long id = financialBonus.getId();
			/*对应个人流水类型*/
			int fundsTypeSelf = 0;
			String fundsNameSelf = "";
			/*对应公司账号流水类型*/
//			int fundsTypeComp = 0;
//			String fundsNameComp = "";
			if (1 == bonusType) {
				fundsTypeSelf = 5310;
				fundsNameSelf = "层级建点奖励";
//				fundsTypeComp = 5381;
//				fundsNameComp = "层级建点奖励5%";
			} else if (2 == bonusType) {
				fundsTypeSelf = 5320;
				fundsNameSelf = "直推指导奖励";
//				fundsTypeComp = 5382;
//				fundsNameComp = "直推指导奖励5%";
			} else if (3 == bonusType) {
				fundsTypeSelf = 5330;
				fundsNameSelf = "级别晋升奖励";
//				fundsTypeComp = 5383;
//				fundsNameComp = "级别晋升奖励5%";
			} else if (4 == bonusType) {
				fundsTypeSelf = 5340;
				fundsNameSelf = "全球领袖分红奖励";
//				fundsTypeComp = 5384;
//				fundsNameComp = "全球领袖分红奖励5%";
			}
			log.info("userId = " + userId + ", bonusType = " + bonusType + ", bonusPrice = " + bonusPrice);
			/**
			 * 1、vip_main库，更新 pay_user_financial 表 profit, realizedPnl字段
			 * 2、vip_main库，记入流水bill_financial
			 * 3、pay_user_financial 表 理财 公司理财账号专有，用于VIP分红 增加金额
			 * 4、bill_financial 	表 理财 公司理财账号专有，用于VIP分红 增加流水
			 * 5、vdsapollo库，t_bonus表 dealflag 设置成1
			 * 以上在1个事务处理
			 * vdsUsdt
			 */
			List<OneSql> sqls = new ArrayList<>();
			TransactionObject txObj = new TransactionObject();
			long currentTime = System.currentTimeMillis();
			
			/*出局判断*/
			boolean outUserFlag = false;
			outUserFlag = FinancialProiftUtils.outUserJudge(userId + "");
			if (outUserFlag) {
				/**
				 * 3、公司理财账户点位资金接收	pay_user_financial balance = balance + douProfitAmount
				 * lvwa1900@163.com
				 * 用户ID：1216832 
				 */
				sql = "update pay_user_financial set balance = balance + " + profitAmount + " "
					+ "where userid = 1216832 and fundstype = 51 ";
				log.info("理财报警:HierarchyThread sql = " + sql);
				sqls.add(new OneSql(sql, 1, null, "vip_main"));
				/*4、1216832 记录流水fundsTypeComp  fundsNameComp */
				sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, "
					+ "remark, vdsUsdtPrice, investProPeriod, matrixLevel) "
					+ "select 1216832, userName, " + fundsTypeSelf + ", " + profitAmount + ", "
					+ "" + currentTime + " , (balance + profit + insureInvestFreezeAmount), "
					+ "51, '" + fundsNameSelf + "', '" + userId + "', 0, 0, 0 "
					+ "from pay_user_financial where userid = 1216832 and fundstype = 51 for update";
				log.info("理财报警:HierarchyThread sql = " + sql);
				sqls.add(new OneSql(sql, 1, null, "vip_main"));
			} else {
				/*1、vip_main库，更新 pay_user_financial 表 profit, realizedPnl 理财动态收益累积  realizedPnlUsdt*/
				sql = "update pay_user_financial set "
					+ "profit = profit + " + profitAmount + ", "
					+ "profitUsdt = profitUsdt + (" + profitAmount + " * " + vdsUsdt + "), "
					+ "realizedPnl = realizedPnl + " + profitAmount + ", "
					+ "realizedPnlUsdt = realizedPnlUsdt + (" + profitAmount + " * " + vdsUsdt + ") "
					+ "where userid = " + userId + " and fundstype = 51 ";
				log.info("理财报警:HierarchyThread sql = " + sql);
				sqls.add(new OneSql(sql, 1, null, "vip_main"));
				/*2、vip_main库，记入流水bill_financial 流水类型为5310*/
				sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, "
					+ "remark, vdsUsdtPrice, investProPeriod, matrixLevel) "
					+ "select " + userId + ", userName, " + fundsTypeSelf + ", " + profitAmount + ", "
					+ "" + currentTime + " , (balance + profit + insureInvestFreezeAmount), "
					+ "51, '" + fundsNameSelf + "', '', " + vdsUsdt + ", 0, 0 "
					+ "from pay_user_financial where userid = " + userId + " and fundstype = 51 for update";
				log.info("理财报警:HierarchyThread sql = " + sql);
				sqls.add(new OneSql(sql, 1, null, "vip_main"));
			}
			
			/*3、vdsapollo库，t_bonus表 dealflag 设置成1 Date calStart, Date calEnd*/
			sql = "update t_bonus set deal_flag = 1 where user_id = " + userId + " and bonus_type = " + bonusType +  " "
				+ "and bonus_time < '" + calEnd + "' and bonus_time >= '" + calStart + "' ";
			log.info("理财报警:HierarchyThread sql = " + sql);
			sqls.add(new OneSql(sql, -1, null, "vdsapollo"));
			
			txObj.excuteUpdateList(sqls);
            if (txObj.commit()) {
//            	log.info("理财报警:用户【" + userId + "】" + fundsNameSelf + "分配成功");
            } else {
            	log.info("理财报警REWARDERROR:用户【" + userId + "】" + fundsNameSelf + "分配失败");
            }
		} catch (Exception e) {
			log.info("理财报警REWARDERROR:HierarchyThread", e);
		} finally {
    		countDownLatch.countDown();
    	}
	}
}
