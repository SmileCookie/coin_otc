package com.world.model.dao.reward;

import java.util.Iterator;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.world.data.mongo.MongoDao;
import com.world.model.entity.reward.RewardRecord;
import com.world.model.entity.reward.RewardSource;
import com.world.util.DigitalUtil;

public class RewardRecordDao extends MongoDao<RewardRecord, String>{

	/***
	 * 完成一次奖励
	 * @param rr
	 * @return
	 */
	public boolean reward(RewardRecord rr){
		RewardSource rs = rr.getRewardSource(); 
		switch (rs) {
			case PhoneCertification:
			case EmailCertification:
			case recommendUserPhoneCertification:
				/////////菲可重复奖励
//				List<RewardRecord> records = getHistory(rr.getUserId(), rr.getType());
//				if(records == null || records.size() == 0 || rs.equals(RewardSource.recommendUserPhoneCertification)){//满足条件开始奖励
//					try {
//						Key<RewardRecord> key = this.save(rr);
//						if(key != null){
//							User user = new UserDao().getUserById(rr.getUserId());//买家
//							if(user != null){
//								List<OneSql> sqls = new ArrayList<OneSql>();
//								
//								String des = user.getLanVal(rs.getValue()) + "奖励";
//								if(rs.equals(RewardSource.recommendUserPhoneCertification)){
//									des = "推荐[" + rr.getOther() + "]手机认证奖励";
//								}
//								
//								btcDetailsBean.addBtcSql(sqls,rs.getBtc(),Integer.parseInt(rr.getUserId()),rr.getUserName(),des , 4 , null ,0 , 0);
//								
//								BillDetailDao.injectInsertSql(BillType.btcReward, Integer.parseInt(rr.getUserId()) , rs.getBtc() , 0 , 0 , 0 , 0 , sqls);
//								return Data.doTrans(sqls);
//							}
//						}
//					} catch (Exception e) {
//						log.error(e.toString(), e);
//					}
//				}
				return true;
			case Register:
				save(rr);
			case RecommendRegister:
				save(rr);
			case Recharge:
				save(rr);
			case RecommendRecharge:
				save(rr);
			default:
				break;
		}
		return false;
	}
	
	public Double statisticsReward(String userId) {
		Double d = 0D;
		try {
		    DBObject key = new BasicDBObject("userId",true);
		    DBObject cond = new BasicDBObject("userId", userId);
		    DBObject initial = new BasicDBObject("rmb",0);
		    String reduce = "function(item,prev){prev.rmb+=item.rmb;}";

		    DBObject group = getCollection().group(key, cond, initial, reduce);
		    Iterator<String> it = group.keySet().iterator();
		    if (it.hasNext()) {
		    	BasicDBObject l = (BasicDBObject) group.get(it.next());
		    	d = DigitalUtil.round((Double) l.get("rmb"), 2);
		    }
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return d;
	}
	
	/***
	 * 获取某用户的历史奖励记录
	 * @return
	 */
	public List<RewardRecord> getHistory(String userId , int type){
		return this.find(this.getQuery().filter("userId =", userId).filter("type", type)).asList();
	}
}
