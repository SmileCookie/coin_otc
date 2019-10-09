package com.world.web.defense;

import java.sql.Timestamp;

import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.LongIdEntity;

@Entity(value = "ip_defense_data" , noClassnameStored = true)
public class IpDefenseData  extends LongIdEntity {

	private static final long serialVersionUID = 1L;

	private String ip;
	private int type; 	//0:黑名单, 1:白名单
	private long expire; //过期时间,long类型时间
	
	
	public IpDefenseData() {
	}
	
	public IpDefenseData(String ip, int type, long expire) {
		super();
		this.ip = ip;
		this.type = type;
		this.expire = expire;
	}

	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getExpire() {
		return expire;
	}
	public void setExpire(long expire) {
		this.expire = expire;
	}
	
	public Timestamp getExpireDate(){
		return new Timestamp(expire);
	}
	
}
