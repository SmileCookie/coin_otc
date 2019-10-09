package com.world.web.defense;

import java.util.List;

import com.google.code.morphia.Datastore;
import com.world.data.mongo.dao.LogMongoDao;

/***
 * 访问日志
 * @author apple
 *
 */
public class AccessLogDao extends LogMongoDao<AccessLog, String>{

	static boolean ensureIndexes = false;
	/**
	 * 
	 */
	private static final long serialVersionUID = -8235127664244963724L;

	/***
	 * 增加访问次数
	 * @param log
	 */
	public void incSave(AccessLog ac){
		Datastore ds = this.getDatastore();
		ds.ensureIndexes(); 
		ds.findAndModify(
				getQuery().filter("_id =", ac.get_id())
						  .filter("minuteFirst =", ac.getMinuteFirst())
						  .filter("ip =", ac.getIp())
						  .filter("urls =", ac.getUrls())
						  .filter("params =", ac.getParams())
						  .filter("adminName =", ac.getAdminName())
						  .filter("userName =", ac.getUserName())
						  ,
			    createUpdateOperations().inc("times", ac.getTimes()), false, true);
//		this.save(ac);
	}
	
	
	public void saveOne(AccessLog ac){
		if(!ensureIndexes){
			//this.getDatastore().ensureIndexes();
			ensureIndexes = true;
		}
		
		//long start = System.currentTimeMillis();
		this.save(ac);
		//long end1 = System.currentTimeMillis();
		
		//log.error("saveOne:" + (end1 - start));
	}
	
	
	public void saveList(List<AccessLog> acs){
		if(!ensureIndexes){
			//this.getDatastore().ensureIndexes();
			ensureIndexes = true;
		}
		
		long start = System.currentTimeMillis();
		
		//this.getDatastore().save(acs);
		
		this.saves(acs);
		
		long end1 = System.currentTimeMillis();
		log.error("saveList耗时：" + (end1 - start) + "共计保存了：" + acs.size());
	}
	
	
}
