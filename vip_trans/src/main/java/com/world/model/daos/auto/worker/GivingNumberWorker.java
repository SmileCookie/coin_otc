package com.world.model.daos.auto.worker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.api.level.LevelManager;
import com.api.user.UserManager;
import com.world.config.GlobalConfig;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.model.Market;
import com.world.model.dao.task.Worker;
import com.world.model.entitys.record.TransRecord;

public class GivingNumberWorker extends Worker{

	private Market m;
	public GivingNumberWorker(String name, String des,Market m) {
		super(name, des);
		this.m = m;
	}
	public static String tradeNumStr = GlobalConfig.getValue("tradeNum");
	public static int tradeNum = tradeNumStr != null && tradeNumStr.length() > 0 ? Integer.parseInt(tradeNumStr) : 0;

	@Override
	public void run() {
		/*super.run();
//		log.info("赠送中奖号");
		log.info("赠送积分");
//		
		List<Bean> tr = Data.Query(m.db,"select * from TransRecord where actStatus=? and types>=0 and iscount=1  ORDER BY transRecordId limit 0,200", new Object[]{1}, TransRecord.class);
		
		if(tr.size() > 0){
			for(Bean b : tr){
//				doOne((TransRecord)b);
				doJifen((TransRecord)b,m);
			}
			
		}*/
		
	}
	public void doOne(TransRecord tr,Market m){
		if(tradeNum <= 0){
			return;
		}
		
		double transNum = tr.getNumbers().doubleValue();
		
		if(transNum < tradeNum){
			log.error(tr.getNumbers().doubleValue() + " : " + tradeNum);
			Data.Update(m.db,"update TransRecord set actStatus=? where transRecordId=? and actStatus=?" , new Object[]{2 , tr.getTransRecordId() , 1});
			return;
		}
		
		List<OneSql> sqls = new ArrayList<OneSql>();
		sqls.add(new OneSql("update TransRecord set actStatus=? where transRecordId=? and actStatus=?" , 1 , new Object[]{3 , tr.getTransRecordId() , 1},m.db));
		int hostUserId = 0;
		int beUserId = 0;
		
		int beiShu = (int)(transNum / tradeNum);
		
		if(tr.getTypes() == 1){//主买
			hostUserId = tr.getUserIdBuy();
			beUserId = tr.getUserIdSell();
		}else if(tr.getTypes() == 0){//主卖
			hostUserId = tr.getUserIdSell();
			beUserId = tr.getUserIdBuy();
		}
		
		//UserManager.getInstance().givingNumber(hostUserId , beUserId , tr.getTimes() , beiShu);
		 
		if(Data.doTransWithHttp(sqls, UserManager.class, "givingNumber", new Object[]{hostUserId , beUserId , tr.getTimes() , beiShu})){
			log.info("成功处理编号为：" + tr.getTransRecordId() + ",hostUserId:" + hostUserId + ",beUserId:" + beUserId + ",赠送个数："+beiShu);
		}else{
			log.error("处理编号为：" + tr.getTransRecordId() + ",hostUserId:" + hostUserId + ",beUserId:" + beUserId + "处理失败了！");
		}
	}

	public void doJifen(TransRecord tr,Market m){
		
		List<OneSql> sqls = new ArrayList<OneSql>();
		sqls.add(new OneSql("update TransRecord set actStatus=? where transRecordId=? and actStatus=?" , 1 , new Object[]{3 , tr.getTransRecordId() , 1},m.db));
		int buyUserId = tr.getUserIdBuy();
		int sellUserId = tr.getUserIdSell();
		
		
		
//		int beiShu = (int)(transNum / tradeNum);
		BigDecimal meney =  tr.getTotalPrice().divide(new BigDecimal("100"), 0, RoundingMode.DOWN);
		
		//UserManager.getInstance().givingNumber(hostUserId , beUserId , tr.getTimes() , beiShu);
		
		if(Data.doTransWithHttp(sqls, LevelManager.class, "addTransJifen", new Object[]{buyUserId+"" , sellUserId +"", meney })){
			log.debug("成功处理编号为：" + tr.getTransRecordId() + ",buyUserId:" + buyUserId + ",sellUserId:" + sellUserId);
		}else{
			log.error("处理编号为：" + tr.getTransRecordId() + ",buyUserId:" + buyUserId + ",sellUserId:" + sellUserId + ",meney:" + meney+ "  处理失败了！");
		}
	}
	
	
}
