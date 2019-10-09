package com.world.model.dao.app;

import com.world.data.mongo.MongoDao;
import com.world.model.entity.app.AppSetting;

public class AppSettingDao extends MongoDao<AppSetting, Long> {
	private static final long serialVersionUID = 5231963319023716774L;
	
	//查询最新版本
	//param:type   客户端类型
	/*public App findLastVesion(String type){
		Query<App> q = getQuery();
		q.field("type").equal(type);
		q.order("-datetime");
		List<App> dataList =q.asList();
		if(dataList!=null&&dataList.size()>0){
			return dataList.get(0);
		}else{
			return null;
		}
	}*/

}
