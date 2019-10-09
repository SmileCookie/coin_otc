package com.world.model.dao.app;

import java.util.List;

import com.google.code.morphia.query.Query;
import com.world.data.mongo.MongoDao;
import com.world.model.entity.app.PushSetting;

public class PushSettingDao extends MongoDao<PushSetting, Long> {
	
	//根据某个字段获取用户
	public PushSetting getPushSettingBySymbol(String userId, int type){
		Query<PushSetting> q = getQuery(PushSetting.class);
		q.filter("userId", userId).filter("type", type);
		return super.findOne(q);
	}
	
	public List<PushSetting> getList(String userId) {
		List<PushSetting> list = this.getListByField("userId", userId);
		return list;
	}
	
}
