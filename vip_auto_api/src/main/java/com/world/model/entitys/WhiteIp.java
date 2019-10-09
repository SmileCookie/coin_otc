package com.world.model.entitys;

import com.world.data.mongo.id.LongIdEntity;

public class WhiteIp extends LongIdEntity {	
	private String ip;
	private int limit;
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
}
