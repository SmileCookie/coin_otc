package com.world.web.defense.statistics;

import com.world.data.mongo.dao.LogMongoDao;

/***
 * 访问日志
 * @author apple
 *
 */
public class AccessStatisticsDao extends LogMongoDao<AccessStatistics, Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2179237068870523200L;

	public void inc(long _id , long times){
		
		//this.update(this.getQuery().filter("_id =", _id), this.createUpdateOperations().inc("times", 1));
		this.getDatastore().findAndModify(getQuery().filter("_id =", _id), createUpdateOperations().inc("times", times), false, true);
	}
}
