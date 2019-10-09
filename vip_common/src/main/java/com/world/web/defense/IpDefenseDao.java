package com.world.web.defense;

import com.google.code.morphia.Key;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.mongodb.WriteResult;
import com.world.model.dao.admin.competence.CompetenceMongoDao;
import com.world.util.date.TimeUtil;
import com.world.web.defense.IpDefense;

/***
 * 访问日志
 * 
 * @author jiahua
 */
public class IpDefenseDao extends CompetenceMongoDao<IpDefenseData, String> {

	@Override
	public Key<IpDefenseData> save(IpDefenseData entity) {
		Key<IpDefenseData> key = super.save(entity);
		return key;
	}

	/**
	 * 删除过期数据
	 */
	public int deleteExpireData() {
		long now = now().getTime();
		WriteResult result = deleteByQuery(this.getQuery().filter("expire <", now));

		// 发出通知版本已经更新
		int count = result.getN();
		if(count>0){
			IpDefense.updateVersion();
		}
		

		return count;
	}
	
	/***
	 * 添加黑白名单
	 * @param ip
	 * @param type  0黑名单  1白名单
	 * @param expire
	 * @return
	 */
	public boolean addHeibai(String ip,int type,long expire){
		boolean success = false;
		if(ip.length() > 0){
			boolean existIp = this.exists("ip", ip);
			if(existIp){
				UpdateOperations<IpDefenseData> operate = this.createUpdateOperations();
				operate.set("ip", ip);
				operate.set("expire", expire);
				operate.set("type", type);
				UpdateResults<IpDefenseData> ur = this.update(this.createQuery().filter("ip", ip), operate);
				if(!ur.getHadError()){
					success = true;
				}
			}else{
				Key<IpDefenseData> save = this.save(new IpDefenseData(ip,type,expire));
				if(save!=null){
					success = true;
				}
			}
			if(success){
				// 发出通知版本已经更新
				IpDefense.updateVersion();
			}
		}
		
		return success;
	}

}
