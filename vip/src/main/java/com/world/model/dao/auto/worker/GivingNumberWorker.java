package com.world.model.dao.auto.worker;

import com.alibaba.fastjson.JSONObject;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.connection;
import com.world.model.dao.jifen.JifenDao;
import com.world.model.dao.task.Worker;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.Market;
import com.world.model.entity.level.JifenType;
import com.world.model.entity.record.TransRecord;
import com.world.model.jifenmanage.JifenManage;
import com.world.model.service.BrushAccountService;
import com.world.model.singleton.SingletonSingleThreadPool;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class GivingNumberWorker extends Worker{

	
	public GivingNumberWorker(String name, String des) {
		super(name, des);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 307598938459227078L;
	JifenDao jifenDao =new JifenDao();
    BrushAccountService brushAccountService = new BrushAccountService();

	//定时缓存数据的Key
	//private static String marketNoPriceKey = "market_np_key";
	
	public static ExecutorService executorService;
	
	public static ExecutorService getExecutorService(){
		if(executorService == null){
			executorService = Executors.newCachedThreadPool();
		}
		return executorService;
	}

	@Override
	public void run() {
		super.run();
		try{

			Map<String, JSONObject> markets = Market.getMarketsMap();
			log.info("交易积分处理开始");
			if(markets!=null && !markets.isEmpty()){
				Iterator<Map.Entry<String, JSONObject>> entryIter = markets.entrySet().iterator();
				while(entryIter.hasNext()){
					Entry<String, JSONObject> entry = entryIter.next();
					JSONObject  market = entry.getValue();//市场配置信息
					String dbName = market.getString("db");//市场交易数据库

					if(connection.containsKey(dbName)){
						dealTransRecordAddJifen(market);
					}else{
						log.error("dbName："+dbName+" 在vip项目环境未配置数据库信息，无法处理该市场的交易积分处理！");
					}

				}
				/*for(String dbName :dbNameList){
					dealTransRecordAddJifen(dbName);
				}*/

			}else{
				log.error("获取不到盘口信息无法处理交易积分！");
			}
		}catch(Exception e){
			log.error(e.toString(), e);
		}
		
	}
	
	/**
	 * 处理成交记录的积分给用户，分不同币种处理，每个币种启用一个新线程进行处理
	 * 每次处理200条成交记录
	 * @param market 市场名称
	 * @author zhanglinbo 20161229
	 */
	public void dealTransRecordAddJifen(final JSONObject market){
			
		getExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				long t1=System.currentTimeMillis();
				String dbName = market.getString("db");//数据库名称
				String exChangeBi = market.getString("exchangeBi");//兑换币种
				//String marketName = market.getString("market");

				List<Bean> list = Data.Query(dbName,"select * from TransRecord where actStatus=? ORDER BY transRecordId limit 0,200", new Object[]{1}, TransRecord.class);
				
				if(list!=null && list.size() > 0){
					Map<String,UserTrans> userTransMap = new HashMap<>();
					
					String buyUserId ="";//买用户ID
					String sellUserId="";//卖用户ID
					BigDecimal money = BigDecimal.ZERO;
					Set<String> idSet = new HashSet<>();
					
					for(Bean b : list){
						//doJifen((TransRecord)b,dbName);
						TransRecord tr =(TransRecord) b;
						/**start by gkl**修改日常交易的积分等级不合并用户交易资金20190510**/
						//交易金额，如果非比特币，进行换算成BTC
						if(exChangeBi.equalsIgnoreCase("usdt")){//对USD交易
							money =  exChangeToUSD(tr.getTotalPrice(),exChangeBi);
						}else if(exChangeBi.equalsIgnoreCase("btc")){//对BTC交易
							money =  exChangeToBTC(tr.getTotalPrice(),exChangeBi);//折算成BTC
							money =  exChangeToUSD(money,"btc");//比特币折算成美元
						}else{
							// TODO: 发现一个坑 上线其他币种，还需加判断
							return;
						}
						/**end**/
						if(money.compareTo(BigDecimal.ZERO)<=0){
							continue;//金额为0不处理
						}
						buyUserId  = String.valueOf(tr.getUserIdBuy());//购买用户ID
						sellUserId = String.valueOf(tr.getUserIdSell());//卖出用户ID
						//合并计算用户交易资金
						/**start by gkl**修改日常交易的积分等级不合并用户交易资金20190510**/
						//将交易资金取整
						money = money.divide(new BigDecimal("100"),0,BigDecimal.ROUND_DOWN);
						log.info("日常交易每笔资金取整："+money);
						megerUserTransMoney(userTransMap,buyUserId,sellUserId,money,tr.getTransRecordId());
						/**end**/
						
					}
					
					//处理积分到数据库
					if(!userTransMap.isEmpty()){
						Iterator<Entry<String,UserTrans>> iter = userTransMap.entrySet().iterator();
						while(iter.hasNext()){
							Entry<String,UserTrans> entry = iter.next();
							UserTrans  userTrans = entry.getValue(); 
							
							boolean flag = doJifen(userTrans.getUserId(),userTrans.getTransMoney());
							if(flag){
								idSet.addAll(userTrans.getTransRecordList());
							}
						}
						//更新已处理的transRecord IDS
					    if(!idSet.isEmpty()){
					    	updateTransRecordStatus(dbName , idSet);
					    }
					}
					userTransMap = null;
					idSet=null;
					list=null;
				}
				long t2=System.currentTimeMillis();
				log.info("交易积分处理 "+dbName+" 数据耗时："+(t2-t1));
			}
		});
			
	}
	
	
	/**
	 * 处理用户交易积分
	 * @author zhanglinbo 20161228
	 * @param userId 用户ID
	 * @param money 交易金额
	 */
	public boolean doJifen(final String userId, final BigDecimal money){
		
		try{

			// update by suxinjie 此处存在并发，用队列处理
			SingletonSingleThreadPool.addJiFenThread(new Thread(){
				@Override
				public void run() {
					jifenDao.updateJifen(JifenType.trans,userId ,money);//日常交易
				}
			});
			//log.error("成功处理用户积分： userId:" + userId + ",money:" + money);

			/*start by xwz 20170626 交易加积分*/
			JifenManage jifenManager = new JifenManage(userId, 8, null, null, "VIP");//6：首次交易
			SingletonSingleThreadPool.addJiFenThread(jifenManager);
			/*end*/
			return true;
		}catch(Exception e){
			log.error("处理用户为：userId:" + userId + ",money:" + money+" 处理失败了！", e);
		}
		
		return false;
	}
	
	/**
	 * 更新已经处理完成的交易记录的积分处理状态
	 * @param dbName 币种数据库名称
	 * @param idSet 已处理的ID集合
	 */
	public void updateTransRecordStatus(String dbName ,Set<String> idSet){
		String transRecordIds = "";
		try{	
			StringBuffer bufferIds = new StringBuffer();
			for(String id :idSet){
				bufferIds.append(",").append(id);
			}
			if(bufferIds.length()>1){
				transRecordIds = bufferIds.substring(1);
			}
			Data.Update(dbName, "update TransRecord set actStatus=? where actStatus=? and transRecordId in ("+transRecordIds+") ", new Object[]{3 ,1});
			log.info(dbName+"成功处理数量为：" + idSet.size() + "");
		}catch(Exception e){
			log.error("处理编号为：" + transRecordIds + "  处理失败了！", e);
		}
	}
	
	/**
	 * 合并相同用户的成交记录操作，以便减少数据库的访问处理
	 * @param userTransMap  用户交易资金Map
	 * @param buyUserId 购买用户ID
	 * @param sellUserId 卖出用户ID
	 * @param money 成交金额
	 * @param transRecordId 成交记录ID
	 * @author zhanglinbo 20161229
	 */
	public void megerUserTransMoney(Map<String,UserTrans> userTransMap,String buyUserId,String sellUserId,BigDecimal money,long transRecordId){
		String[] userIdArr = new String[]{buyUserId,sellUserId};
		//计算交易金额，少于1000部分之间减掉。保留整数1000的倍数
		//money =	money.subtract(money.divideAndRemainder(BigDecimal.valueOf(1000))[1]);
		for(String userId:userIdArr){
		    //刷量账号不累计积分 add by buxianguan 20180321
            boolean isBrushAccount = brushAccountService.isBrushAccount(userId);
            if (isBrushAccount) {
                continue;
            }
            UserTrans userTrans = userTransMap.get(userId);
			List<String> transRecordList = new ArrayList<String>();
			if(userTrans==null){
				userTrans = new UserTrans();
				userTrans.setUserId(userId);
				userTrans.setTransMoney(money);
				
				transRecordList.add(transRecordId+"");
				userTrans.setTransRecordList(transRecordList);
				userTransMap.put(userId,userTrans);
			}else{
				userTrans.setTransMoney(userTrans.getTransMoney().add(money));
				transRecordList = userTrans.getTransRecordList();
				transRecordList.add(transRecordId+"");
				userTrans.setTransRecordList(transRecordList);
			}
		}
	}
	
	
	private BigDecimal exChangeToBTC(BigDecimal tradeMoney,String exChangeBi){
		if(!exChangeBi.equalsIgnoreCase("btc")){
			JSONObject  marketPrice = UserCache.getPrices();//(JSONObject)Cache.GetObj(marketNoPriceKey);
			if(marketPrice!=null && marketPrice.containsKey(exChangeBi.toLowerCase() + "_btc")){
				//modify by xwz  20170911 取对BTC价格
//				BigDecimal exchangeBiPrice = marketPrice.getBigDecimal(exChangeBi.toLowerCase());
				BigDecimal exchangeBiPrice = marketPrice.getBigDecimal(exChangeBi.toLowerCase()+"_btc");
					tradeMoney = tradeMoney.multiply(exchangeBiPrice);
			}else{
				tradeMoney = BigDecimal.ZERO;
				log.info("处理积分无法找到"+exChangeBi+" 兑BTC的价格！");
			}
		}
		
		return tradeMoney;
	}

	/**
	 * 折算成美元
	 * @autor xwz
	 * 20170911
	 * @param tradeMoney
	 * @param exChangeBi
     * @return
     */
	private BigDecimal exChangeToUSD(BigDecimal tradeMoney,String exChangeBi){
		if(!exChangeBi.equalsIgnoreCase("usdt")){
			JSONObject  marketPrice = UserCache.getPrices();//(JSONObject)Cache.GetObj(marketNoPriceKey);
			if(marketPrice!=null && marketPrice.containsKey(exChangeBi.toLowerCase() + "_usdt")){
				BigDecimal exchangeBiPrice = marketPrice.getBigDecimal(exChangeBi.toLowerCase()+ "_usdt");
				tradeMoney = tradeMoney.multiply(exchangeBiPrice);
			}else{
				tradeMoney = BigDecimal.ZERO;
				log.info("处理积分无法找到"+exChangeBi+" 兑USD的价格！");
			}
		}

		return tradeMoney;
	}



	/**
	 * 定义内部类实体 用于存放统计数据
	 * @author chbtc
	 *
	 */
	class UserTrans{
		private String userId;
		private BigDecimal transMoney;
		private List<String> transRecordList;
		
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public BigDecimal getTransMoney() {
			return transMoney;
		}
		public void setTransMoney(BigDecimal transMoney) {
			this.transMoney = transMoney;
		}
		public List<String> getTransRecordList() {
			return transRecordList;
		}
		public void setTransRecordList(List<String> transRecordList) {
			this.transRecordList = transRecordList;
		}
		
	}
}
