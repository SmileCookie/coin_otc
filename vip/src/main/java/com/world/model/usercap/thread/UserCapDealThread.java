package com.world.model.usercap.thread;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.world.data.mysql.Query;
import com.world.model.dao.bill.BillDetailDao;
import com.world.model.entity.bill.BillDetails;
import com.world.model.entity.bill.BillType;
import com.world.model.financial.entity.BillFinancial;
import com.world.model.usercap.pool.UserCapPool;

/**
 * <p>标题: 用户资金处理</p>
 * <p>描述: 按每个用户每个币种进行处理核对</p>
 * <p>版权: Copyright (c) 2017</p>
 * @author flym
 * @version 
 */
public class UserCapDealThread extends Thread {
	
	/*需要检查的用户ID*/
	private int capUserId;
	private int fundsType;
	
	private BillFinancial billDetails;
	/*本次扫描的该资金的最大流水号*/
	private Long capLastBillId;
	/*用户资金监控编号*/
	private String ucmId;
	/*sql语句*/
	private String sql = "";
	private static Logger log = Logger.getLogger(UserCapDealThread.class.getName());
	
	public UserCapDealThread (BillFinancial billDetails, Long capLastBillId, String ucmId) {
		this.billDetails = billDetails;
		this.capLastBillId = capLastBillId;
		this.ucmId = ucmId;
	}
	
	@SuppressWarnings("unused")
	@Override
	public void run() {
		/*异常标志和资金核对正确标志*/
		boolean tryFlag = true, correctFlag = true;
		/*记录核算开始时间*/
		long startTime = System.currentTimeMillis();
//		BigDecimal checkBillAmount, checkPayUserAmount, billTotalAmount;
		BigDecimal checkBillAmount = BigDecimal.ZERO;
		BigDecimal checkPayUserAmount = BigDecimal.ZERO;
		BigDecimal billTotalAmount = BigDecimal.ZERO;
		try {
			capUserId = billDetails.getUserId().intValue();
			fundsType = billDetails.getFundsType();
			log.info("用户ID:" + capUserId + ",资金类型:" + fundsType + "开始扫描对账");
			/*查询数据*/
			sql = "select id, userId, userName, type, amount, balance, fundsType from bill_financial "
				+ "where id >= 132928 and id <= 100000000 and userId = " + capUserId + " and fundsType = " + fundsType + " order by id asc ";
			BillDetailDao bdDao = new BillDetailDao();
			Query<BillDetails> query = bdDao.getQuery();
			query.setSql(sql);
			query.setDatabase("vip_main");
			query.setCls(BillDetails.class);
			
			/*保存到List中*/
			log.info("sql = " + sql);
			bdDao.setDatabase("vip_main");
			List<BillDetails> listUserBills = bdDao.find(sql, null, BillDetails.class);
			log.info("listBills.size = " + listUserBills.size());
			/*每次对比前后2条数据amount,除非用户只要1次操作，否则至少会有2条数据*/
			int type1 = 0, type2 = 0, isFinaAccount = 0,isFinaAccount1 = 0,isFinaAccount2 = 0;
			/*流水号1流水号2*/
			long billId1 = 0, billId2 = 0;
			/*发生额，余额，正常的余额*/
			BigDecimal amount1, amount2, balance1, balance2, corBlance;
			/*每次取2条记录进行核对*/
			BillDetails billDetails1, billDetails2;
			if (CollectionUtils.isNotEmpty(listUserBills) && listUserBills.size() < 2) {
				log.info("只要1条流水");
				/*用户只要1条流水*/
				if(listUserBills.get(0).getIsfinaaccount() !=3){
					billDetails1 = listUserBills.get(0);
					amount1 = billDetails1.getAmount();
					balance1 = billDetails1.getBalance();
					billId1 = billDetails1.getId();
				/*以前没有核对过是第一次核对,而且只有1条数据*/
					log.info("amount1 = " + amount1 + ", balance1 = " + balance1 + ", billId1 = " + billId1);
//				if(amount1.subtract(balance1).compareTo(BigDecimal.ZERO) != 0 && amount1.subtract(balance1).abs().compareTo(new BigDecimal("0.00000001")) > 0) {
					if(amount1.subtract(balance1).compareTo(BigDecimal.ZERO) != 0) {
					/*初始第1条记录，自身核对*/
						correctFlag = false;
					/*检查时点余额bill,流水合计金额*/
						checkBillAmount = amount1;
						billTotalAmount = balance1;
					}
				}
				billId1 = listUserBills.get(0).getId();
			} else if (null != listUserBills && listUserBills.size() > 1) {
				log.info("N条流水");
				for (int i = 0; i < listUserBills.size() - 1; i++) {
					billDetails1 = listUserBills.get(i);
					billDetails2 = listUserBills.get(i + 1);
					isFinaAccount = billDetails1.getIsfinaaccount();
					type1 = billDetails1.getType();
					type2 = billDetails2.getType();
					amount1 = billDetails1.getAmount();
					amount2 = billDetails2.getAmount();
					balance1 = billDetails1.getBalance();
					balance2 = billDetails2.getBalance();
					billId1 = billDetails1.getId();
					billId2 = billDetails2.getId();

					isFinaAccount1 = billDetails1.getIsfinaaccount();
					isFinaAccount2 = billDetails2.getIsfinaaccount();

					if (i == 0 && 3 != isFinaAccount) {
						/*以前没有核对过是第一次核对,否则是以前核对过，上次核对的最后1条状态更新成了3作为本次第一条核对数据*/
						log.info("amount1 = " + amount1 + ", balance1 = " + balance1 + ", billId1 = " + billId1);
//						if(amount1.subtract(balance1).compareTo(BigDecimal.ZERO) != 0 && amount1.subtract(balance1).abs().compareTo(new BigDecimal("0.00000001")) > 0) {
						if(amount1.subtract(balance1).compareTo(BigDecimal.ZERO) != 0) {
							/*初始第1条记录，自身核对*/
							correctFlag = false;
							/*检查时点余额bill,流水合计金额*/
							checkBillAmount = amount1;
							billTotalAmount = balance1;
							tryFlag = false;
						}
					}
					/*N和N+1进行核对校验*/
					corBlance = matchBlillBlance(balance1, amount2, type2);
//					if(corBlance.subtract(balance2).compareTo(BigDecimal.ZERO) != 0 && corBlance.subtract(balance2).abs().compareTo(new BigDecimal("0.00000001")) > 0) {
					if(corBlance.subtract(balance2).compareTo(BigDecimal.ZERO) != 0) {
						/*N和N+1核对不平*/
						log.info("balance1 = " + balance1 + ", amount2 = " + amount2 + ", type2 = " + type2 + ", type1 = " + type1);
						log.info("corBlance = " + corBlance + ", balance2 = " + balance2 + ", billId1 = " + billId1 + "i = " + i);
						correctFlag = false;
						/*检查时点余额bill,流水合计金额*/
						checkBillAmount = balance2;
						billTotalAmount = corBlance;
						tryFlag = false;
					}
					
				}
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		} finally {
			/*减少线程池中的统计个数*/
			UserCapPool.subCount();
			/*记录核算完成时间*/
			long endTime = System.currentTimeMillis();
			if (!tryFlag) {
				/*异常报错处理*/
				log.info("【用户：" + capUserId + ", 资金类型：" + fundsType + "】流水核对异常!!!【核算耗时：" + (endTime - startTime) + "】");
			} 
		}
		
	}
	
	/**
	 * 根据收支类型判断等式:corBlance = blance 加减 amount
	 * @param
	 * @param amount
	 * @param type
	 * @return
	 */
	public BigDecimal matchBlillBlance(BigDecimal balance, BigDecimal amount, int type) {
		BigDecimal corBlance;
		int expType = BillType.giveExpType(type);
//		log.info("expType = " + expType);
		if (1 == expType) {
			corBlance = balance.add(amount);
		} else {
			corBlance = balance.subtract(amount);
		}
		return corBlance;
	}
	
	
	public int getCapUserId() {
		return capUserId;
	}

	public void setCapUserId(int capUserId) {
		this.capUserId = capUserId;
	}

	public int getFundsType() {
		return fundsType;
	}

	public void setFundsType(int fundsType) {
		this.fundsType = fundsType;
	}

	public Long getCapLastBillId() {
		return capLastBillId;
	}

	public void setCapLastBillId(Long capLastBillId) {
		this.capLastBillId = capLastBillId;
	}

	public String getUcmId() {
		return ucmId;
	}

	public void setUcmId(String ucmId) {
		this.ucmId = ucmId;
	}
}
