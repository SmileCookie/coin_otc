package com.world.model.loan.worker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONObject;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.model.dao.task.Worker;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.loan.MarketPrices;
import com.world.model.loan.dao.LoanRecordDao;
import com.world.model.loan.dao.P2pUserDao;
import com.world.model.loan.dao.UserDetectDao;
import com.world.model.loan.entity.P2pUser;

public class UpdateUnwindPriceWorker extends Worker{
	public UpdateUnwindPriceWorker(String name,String des){
		this.name = name;
		this.des = des;
	}
	LoanRecordDao loanRecordDao = new LoanRecordDao();
	P2pUserDao p2pUserDao = new P2pUserDao();
	UserDetectDao detectDao = new UserDetectDao();
	
	@Override
	public void run() {
		super.run();
		log.info("## 定时刷新预计平仓价 ##");
		
		Query query = p2pUserDao.getQuery();
		query.setSql("select * from p2puser where repayLevel>0 ");
		query.setCls(P2pUser.class);
		
		List<P2pUser> dataList = query.getList();
		
		JSONObject prices = LoanAutoFactory.getPrices();
		if(!MarketPrices.isReturn(prices)){
			for (P2pUser user : dataList) {
				JSONObject dynamicUnwindPrices = detectDao.calUnwindPirce(user, null);
				p2pUserDao.initLoanUser(user);
				Map<String, PayUserBean> map = user.getFunds();
				JSONObject unwindPrices = new JSONObject();
				for(Entry<String, PayUserBean> entry : map.entrySet()){
					String key = entry.getKey();
					PayUserBean payUser = entry.getValue();
					unwindPrices.put(key, payUser.getUnwindPrice());
				}
				if (needUpdate(unwindPrices, dynamicUnwindPrices, unwindPrices)) {
					List<OneSql> sqls = new ArrayList<OneSql>();
					for(Entry<String, PayUserBean> entry : map.entrySet()){
						String key = entry.getKey();
						PayUserBean payUser = entry.getValue();
						BigDecimal unwindPrice = dynamicUnwindPrices.getBigDecimal(key);
						sqls.add(new OneSql("UPDATE pay_user SET unwindPrice = ? WHERE userId = ? AND fundsType = ?", 1, new Object[]{unwindPrice, user.getUserId(), payUser.getFundsType()}));
					}
					if(Data.doTrans(sqls)){
						//成功
//						log.info("定时刷新预计平仓价 成功 ##");
					}else{
						log.error("## 刷新预计平仓价失败, userID::" + user.getUserId() + ", username::" + user.getUserName() + " ##");
					}
				}
				
			}
		}
		
	}
	
	/**
	 * 价格进行比较，有一种价格符合条件，就需要全部记录更新。
	 * @param dynamicUnwindPrices
	 * @param unwindPrices
	 * @param prices
	 * @return
	 */
	private boolean needUpdate(JSONObject unwindPrices, JSONObject dynamicUnwindPrices, JSONObject prices){
		Iterator<String> it = unwindPrices.keySet().iterator();
		boolean needUpdate = false;//获取出来的lastPrices一定是有值的
		while (it.hasNext()) {
			String key = (String) it.next();
			BigDecimal unwindPrice = unwindPrices.getBigDecimal(key);
			BigDecimal price = prices.getBigDecimal(key);
			BigDecimal dynamicUnwindPrice = dynamicUnwindPrices.getBigDecimal(key);
			if(unwindPrice.subtract(dynamicUnwindPrice).abs().divide(price, 4, RoundingMode.DOWN).doubleValue() >= 0.001){
				needUpdate = true;
				break;
			}
		}
		return needUpdate;
	}
}
