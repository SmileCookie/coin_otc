package com.world.model.dao.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.code.morphia.query.Query;
import com.world.data.database.DatabasesUtil;
import com.world.data.mongo.MongoDao;
import com.world.model.entity.app.MarketRemind;
import com.world.model.entity.coin.CoinProps;

public class MarketRemindDao extends MongoDao<MarketRemind, Long> {
	
/*	//根据某个字段获取用户
	public MarketRemind getMarketRemindBySymbol(String userId, String symbol){
		Query<MarketRemind> q=null;
		q = getQuery(MarketRemind.class).filter("userId", userId).filter("symbol", symbol);
//		q.field("isDeleted").notEqual(true);
//			q.or(
//					q.criteria("isDeleted").equal(null),
//					q.criteria("isDeleted").equal(false)
//			);
		return super.findOne(q);
	}
	

	public List<MarketRemind> search(Query<MarketRemind> q){
		//q.offset((pageIndex-1)*pageSize).limit(pageSize);
		return super.find(q).asList();
	}*/

	public boolean existRemindByUid(String userId, String rid) {
		Query<MarketRemind> query = null;
		query = getQuery(MarketRemind.class).filter("userId", userId).filter("_id", Long.parseLong(rid));
		MarketRemind marketRemind = super.findOne(query);

		if (marketRemind != null) {
			return true;
		}

		return false;
	}
	
	
	public List<MarketRemind> getList(String userId) {
//		completSetting(userId);
		List<MarketRemind> list = this.getListByField("userId", userId);
		MarketRemind r = new MarketRemind();
		/*MarketRemind[] reminds = {r,r,r,r};
		for (MarketRemind e : list) {
			if (e.getSymbol().equalsIgnoreCase("btc")) {
				reminds[0] = e;
			} else if (e.getSymbol().equalsIgnoreCase("ltc")) {
				reminds[1] = e;
			}else if (e.getSymbol().equalsIgnoreCase("eth")) {
				reminds[2] = e;
			} else if (e.getSymbol().equalsIgnoreCase("etc")) {
				reminds[3] = e;
			}
		}*/
		Map<String, CoinProps> types = DatabasesUtil.getCoinPropMaps();
		List<MarketRemind> reminds = new ArrayList<>();
		for (MarketRemind e : list) {
			if(types.containsKey(e.getCurrency())){
				reminds.add(e);
			}/*else{
				reminds.add(r);
			}*/
		}
		//List<MarketRemind> sortList = Arrays.asList(reminds);
		return reminds;
	}

	public void deleteMarketRemind(String id) {
		Query<MarketRemind> query = this.getQuery();
		query.filter("_id", Long.parseLong(id));
		this.deleteByQuery(query);
	}
	
	
	/*public void completSetting(String userId) {
		List<MarketRemind> list = this.getListByField("userId", userId);
		
		Map<String, CoinProps> types = DatabasesUtil.getCoinPropMaps();
		
		if (null != list && list.size() > types.size()) {
			Query<MarketRemind> query = this.getQuery();
			query.filter("userId", userId);
			this.deleteByQuery(query);
			list = this.getListByField("userId", userId);
		}
		
		Map<String, CoinProps> limitTypes = new HashMap<>();//剩余类型
		
		for(Map.Entry<String, CoinProps> enrty:types.entrySet()){
			limitTypes.put(enrty.getKey(), enrty.getValue());
		}
		
		if (null != list && list.size() < types.size()) {
			for (MarketRemind e : list) {
				String typeOfRemind =  e.getCurrency() ;
				if (!types.containsKey(typeOfRemind)) {
					limitTypes.remove(typeOfRemind);
				}
			}
			
			if (limitTypes.size()>0) {
				for (Map.Entry<String, CoinProps> e : limitTypes.entrySet()) {
					MarketRemind remind = new MarketRemind();
					
					remind.setStatus(0);
					remind.setHigh("");
					remind.setLow("");
					remind.setSymbol(e.getKey());
					remind.setUserId(userId);
					String nid = this.save(remind).getId().toString();
				}
			}
			
		}
		
	}*/
	
}
