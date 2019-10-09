package com.world.model.financialproift.userfininfo.thread;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import com.world.data.mysql.Data;
import com.world.model.entity.financialproift.UserFinancialInfo;

public class ReturnUserOrderThread extends Thread {
	/*sql语句*/
	private String sql = "";
	private static Logger log = Logger.getLogger(ReturnUserOrderThread.class.getName());
	private CountDownLatch countDownLatch;
	private String invitationUserName;
	private String batchNo;
	
	public ReturnUserOrderThread (CountDownLatch countDownLatch, String invitationUserName, String batchNo) {
		this.countDownLatch = countDownLatch;
		this.invitationUserName = invitationUserName;
		this.batchNo = batchNo;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		try {
			/*获取用户ID和用户名*/
			String userId = "";
			String userName = "";
			/**
			 * 1、获取筛选直推人员大于3个的VIP 
			 * 1-1、未复投过的：authPayFlag = 2，resetProfitTime is null
			 * 1-2、复投过的：authPayFlag = 2, resetProfitTime is not null
			 */
			sql = "select userId, userName, resetProfitTime, investAmount, investAvergPrice from fin_userfinancialinfo "
				+ "where userName = '" + invitationUserName + "' and authPayFlag = 2 ";
			log.info("sql = " + sql);
			UserFinancialInfo userFinancialInfo = (UserFinancialInfo) Data.GetOne("vip_financial", sql, null, UserFinancialInfo.class);
			/*复投时间*/
			Date resetProfitTime = null;
			/*投资金额*/
			BigDecimal investAmount = BigDecimal.ZERO;
			/*投资均价*/
			BigDecimal investAvergPrice = BigDecimal.ZERO;
			if (null != userFinancialInfo) {
				userId = userFinancialInfo.getUserId() + "";
				userName = userFinancialInfo.getUserName();
				resetProfitTime = userFinancialInfo.getResetProfitTime();
				investAmount = userFinancialInfo.getInvestAmount();
				investAvergPrice = userFinancialInfo.getInvestAvergPrice();
			} else {
				log.info("此用户已出局，不需要处理 = " + invitationUserName);
				return;
			}
			/*判断是否出局过*/
			boolean outReturnFlag = false;
			if (null != resetProfitTime) {
				outReturnFlag = true;
			}
			
			/*获取第三个直推用户的投资时间*/
			Date profitTime = null;
			sql = "select profitTime dateProfitTime from fin_userfinancialinfo "
				+ "where invitationUserName = '" + invitationUserName + "' and authPayFlag != 1 and profitTime > '2019-07-01' "
				+ "order by profitTime asc limit 2, 1";
			log.info("sql = " + sql);
			UserFinancialInfo userOrderProfitTime = (UserFinancialInfo) Data.GetOne("vip_financial", sql, null, UserFinancialInfo.class);
			if (null != userOrderProfitTime) {
				profitTime = userOrderProfitTime.getDateProfitTime();
			} else {
				log.info("理财报警REWARDERROR:【回本用户顺序列表生成】用户排位生成第三直推时间获取异常 = userName = " + userName + ": " + sql);
				return;
			}
			
			/**
			 * 获取用户已获得的静态收益
			 */
			sql = "select balance from pay_user_financial where userId = " + userId + " and fundsType = 10 ";
			log.info("userAvaTransferAmount sql = " + sql);
            List<BigDecimal> listPayUserFinancial = (List<BigDecimal>) Data.GetOne("vip_main", sql, null);
            BigDecimal staticProfitSumUsdt = BigDecimal.ZERO;
            if (null != listPayUserFinancial) {
            	if (null != listPayUserFinancial.get(0)) {
            		staticProfitSumUsdt = listPayUserFinancial.get(0);
                }
            }
			
			log.info("userId = " + userId + ", userName = " + userName + ", profitTime = " +profitTime + ", "
					+ "staticProfitSumUsdt = " + staticProfitSumUsdt + ", "
					+ "resetProfitTime = " + resetProfitTime + ", outReturnFlag = " + outReturnFlag);
			if (outReturnFlag) {
				sql = "insert into fin_userreturnorderinfotmp (userId, userName, profitTime, resetProfitTime, expectProfitUsdt, "
					+ "seqNo, batchNo, returnType, staticProfitSumUsdt, createTime, investAmount, investAvergPrice) "
					+ "select userId, userName, profitTime, resetProfitTime, expectProfitUsdt, "
					+ "9999999, '" + batchNo + "', 2, " + staticProfitSumUsdt + ", now(), " + investAmount + ", " + investAvergPrice + " "
					+ "from fin_userfinancialinfo where userId = " + userId + " and authPayFlag = 2 ";
			} else {
				sql = "insert into fin_userreturnorderinfotmp (userId, userName, profitTime, resetProfitTime, expectProfitUsdt, "
					+ "seqNo, batchNo, returnType, staticProfitSumUsdt, createTime, investAmount, investAvergPrice) "
					+ "select userId, userName, profitTime, profitTime, expectProfitUsdt, "
					+ "9999999, '" + batchNo + "', 1, " + staticProfitSumUsdt + ", now(), " + investAmount + ", " + investAvergPrice + " "
					+ "from fin_userfinancialinfo where userId = " + userId + " and authPayFlag = 2 ";
			}
			log.info("sql = " + sql);
			int intInsert = Data.Insert("vip_financial", sql, null);
			if (-1 == intInsert) {
				log.info("理财报警REWARDERROR:【回本用户顺序列表生成】此用户排位可能已经生成，不需要重复插入 = " + userId);
			} else {
				log.info("理财报警:【回本用户顺序列表生成】用户排位生成成功 = " + userId);
			}
		} catch (Exception e) {
			log.info("理财报警REWARDERROR:【回本用户顺序列表生成】", e);
		} finally {
    		countDownLatch.countDown();
    	}
	}
}
