package com.world.model.dao.app;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.google.code.morphia.query.Query;
import com.world.cache.Cache;
import com.world.data.mongo.MongoDao;
import com.world.model.entity.app.App;

public class AppDao extends MongoDao<App, Long> {
	
	private final String cacheKey = "last_app_";

	//查询最新版本
	//param:type   客户端类型
	public App findLastVesion(String type){
		Query<App> q = getQuery();
		q.field("type").equal(type);
		q.field("released").equal(true);
		q.order("-datetime");
		List<App> dataList =q.asList();
		if(dataList!=null&&dataList.size()>0){
			return dataList.get(0);
		}else{
			return null;
		}
	}
	
	public void setCache() {
		App app1 = findLastVesion("android");
		App app2 = findLastVesion("ios");
		Cache.Set(cacheKey + "android", JSONObject.toJSONString(app1));
		Cache.Set(cacheKey + "ios", JSONObject.toJSONString(app2));
	}

}
