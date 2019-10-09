package com.world.data.mongo.dao;

import com.world.config.GlobalConfig;
import com.world.data.mongo.MongoDao;
import com.world.data.mongo.MorphiaMongo;
import com.world.data.mongo.MorphiaMongoUtil;
import com.world.model.dao.admin.competence.CompetenceMongoDao;

public class LogMongoDao<T, K> extends MongoDao<T, K>{
	
	private final static String daoBase = "log";
	/**
	 * 
	 */
	private static final long serialVersionUID = -8320505113380281150L;
	static String name = GlobalConfig.getValue(daoBase + "Db");
	static String tag = name == null ? CompetenceMongoDao.daoBase : daoBase;
	
	
	final static MorphiaMongo mm = MorphiaMongoUtil.getMorphiaMongo(GlobalConfig.getValue(tag + "Db") , GlobalConfig.getValue(tag + "DbIp") , 
										GlobalConfig.getValue(tag + "UserName") , GlobalConfig.getValue(tag + "Pwd"));
	final static String dbName = GlobalConfig.getValue(tag + "Db");
	protected LogMongoDao() {
		super(mm.getMongo(), mm.getMorphia() , dbName);
	}
}
