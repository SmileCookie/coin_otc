package com.world.data.database;

import java.io.Serializable;

import com.world.model.entity.coin.CoinProps;

public class DatabaseProp implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1452739436293950834L;
	public String database = "default";//数据库
	public CoinProps coint;
	public String lan;

	public void setLan(String lan){
		this.lan = lan;
	}




	public String getDatabase() {
		return database;
	}
	
	public void setCoint(CoinProps coint){
		this.coint = coint;
	}
	
	public void setDatabase(String database) {
		this.database = database;
	}
}
