package com.world.model.entity.reward;

import java.sql.Timestamp;


import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;
import com.world.model.entity.EnumUtils;

@Entity(noClassnameStored = true)
public class RewardRecord extends StrBaseLongIdEntity{
	
	public RewardRecord() {
		super();
	}
	
	public RewardRecord(Datastore ds) {
		super(ds);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 877813232377925399L;

	private String userId;
	private String userName;
	private String launchUserId;// 发起操作用户
	private String launchUserName;// 发起操作用户
	private Double launchRmb;// 发起操作资金
	private String ip;
	private int type;//奖励类型   和枚举类RewardRecord对应
	private long btc;
	private Double rmb;// 奖励
	private Timestamp date;
	
	private String other;
	
	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public RewardSource getRewardSource(){
		return (RewardSource) EnumUtils.getEnumByKey(type, RewardSource.class);
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
	public long getBtc() {
		return btc;
	}
	public void setBtc(long btc) {
		this.btc = btc;
	}
	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}

	public Double getRmb() {
		return rmb;
	}

	public void setRmb(Double rmb) {
		this.rmb = rmb;
	}

	public String getLaunchUserName() {
		return launchUserName;
	}

	public void setLaunchUserName(String launchUserName) {
		this.launchUserName = launchUserName;
	}

	public String getLaunchUserId() {
		return launchUserId;
	}

	public void setLaunchUserId(String launchUserId) {
		this.launchUserId = launchUserId;
	}

	public Double getLaunchRmb() {
		return launchRmb;
	}

	public void setLaunchRmb(Double launchRmb) {
		this.launchRmb = launchRmb;
	}


}
