package com.world.model.dao.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.world.data.mongo.MongoDao;
import com.world.model.entity.user.VerifyUserInfo;


public class VerifyUserInfoDao extends MongoDao<VerifyUserInfo, String>{
	
	/**
	 * 新增
	 * @param info
	 * @return
	 */
	public String add(VerifyUserInfo info){
		String id = super.save(info).getId().toString();
		return id;
	}
	
	/**
	 * 获取正在审核状态中的
	 * @param userId
	 * @param type
	 * @return
	 */
	public VerifyUserInfo getVerifyingInfo(String userId, int type){
		return this.findOne(this.getQuery().filter("userId", userId).filter("type", type).order("-addTime"));
	}
	
	public Map<Integer, VerifyUserInfo> getVerifyMap(String userId){
		Map<Integer, VerifyUserInfo> map = new HashMap<Integer, VerifyUserInfo>();
		List<VerifyUserInfo> list = find(this.getQuery().filter("userId", userId)).asList();
		if(list != null){
			for(int i = 0; i < list.size(); i ++){
				VerifyUserInfo verify = list.get(i);
				map.put(verify.getType(), verify);
			}
		}
		return map;
	}
	
	/**
	 * 用户撤消
	 * @param id
	 * @return
	 */
	public boolean cancel(String id){
		Datastore ds = super.getDatastore();
		Query<VerifyUserInfo> q = this.getQuery().filter("_id", id).filter("status", 0);  
		UpdateOperations<VerifyUserInfo> ops = ds.createUpdateOperations(VerifyUserInfo.class);
		ops.set("status", 3);
		return update(q, ops).getHadError();
	}
	
	/**
	 * 用户撤消
	 * @param id
	 * @return
	 */
	public boolean update(VerifyUserInfo info){
		Datastore ds = super.getDatastore();
		Query<VerifyUserInfo> q = this.getQuery().filter("_id", info.getId());  
		UpdateOperations<VerifyUserInfo> ops = ds.createUpdateOperations(VerifyUserInfo.class);
		ops.set("status", info.getStatus());
		ops.set("memo", info.getMemo());
		ops.set("adminId", info.getAdminId());
		ops.set("verifyTime", info.getVerifyTime());
		return update(q, ops).getHadError();
	}
}
