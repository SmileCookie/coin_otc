package com.world.model.entity.iplist;

import java.sql.Timestamp;

import com.google.code.morphia.Datastore;
import com.world.data.mongo.id.StrBaseLongIdEntity;

public class WhiteIp extends StrBaseLongIdEntity {	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8152337873863006120L;
	public WhiteIp() {
		super();
	}

	public WhiteIp(Datastore ds) {
		super(ds);
	}
	
	private String ip;//ip地址
	private int limit;//每分钟访问次数限制
	private Timestamp createTime;//记录最后操作时间
	private String opUserName;//记录操作用户

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
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getOpUserName() {
		return opUserName;
	}
	public void setOpUserName(String opUserName) {
		this.opUserName = opUserName;
	}
}
