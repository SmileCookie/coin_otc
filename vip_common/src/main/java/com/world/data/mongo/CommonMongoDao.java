package com.world.data.mongo;

import com.world.model.entity.coin.CoinProps;

public class CommonMongoDao<T, K> extends MongoDao<T, K>{
	public String database = "default";//数据库
	public CoinProps coint;

	public String getDatabase() {
		return database;
	}
	
	public void setCoint(CoinProps coint){
		if(coint != null){
			setDatabase(coint.database);
		}
		this.coint = coint;
	}
	
	public void setDatabase(String database) {
		this.database = database;
	}
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8320505113380281150L;
	
	
	protected CommonMongoDao(String other) {
		
        
	}
}