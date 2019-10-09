package com.world.model.dao.admin.competence;

import com.world.config.GlobalConfig;
import com.world.data.mongo.MongoDao;
import com.world.data.mongo.MorphiaMongo;
import com.world.data.mongo.MorphiaMongoUtil;

public class CompetenceMongoDao<T, K> extends MongoDao<T, K>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5073720723328608867L;
	public final static String daoBase = "competence";
	
	final static MorphiaMongo mm = MorphiaMongoUtil.getMorphiaMongo(GlobalConfig.getValue(daoBase + "Db") , GlobalConfig.getValue(daoBase + "DbIp") , 
			GlobalConfig.getValue(daoBase + "UserName") , GlobalConfig.getValue(daoBase + "Pwd"));
	
	final static String dbName = GlobalConfig.getValue("competenceDb");
	protected CompetenceMongoDao() {
		super(mm.getMongo(), mm.getMorphia() , dbName);
	}
}
