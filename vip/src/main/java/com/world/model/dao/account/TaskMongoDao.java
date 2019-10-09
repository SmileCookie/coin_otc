package com.world.model.dao.account;

import com.world.config.GlobalConfig;
import com.world.data.mongo.MongoDao;
import com.world.data.mongo.MorphiaMongo;
import com.world.data.mongo.MorphiaMongoUtil;

public class TaskMongoDao<T, K> extends MongoDao<T, K>{
	final static MorphiaMongo mm = MorphiaMongoUtil.getMorphiaMongo(GlobalConfig.getValue("moDbName") , GlobalConfig.getValue("moUrl") , 
										GlobalConfig.getValue("moUserName") , GlobalConfig.getValue("moPassword"));
	final static String dbName = GlobalConfig.getValue("moDbName");
	protected TaskMongoDao() {
		super(mm.getMongo(), mm.getMorphia() , dbName);
	}
}
