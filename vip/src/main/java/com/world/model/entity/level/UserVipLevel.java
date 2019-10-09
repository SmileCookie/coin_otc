package com.world.model.entity.level;

import com.google.code.morphia.Datastore;
import com.world.data.mongo.id.StrBaseLongIdEntity;

public class UserVipLevel extends StrBaseLongIdEntity{

	public UserVipLevel(){
		super(null);
	}
	
	public UserVipLevel(Datastore ds) {
		super(ds);
	}

	private int vipRate;//用户等级
	private int jifen;//等级对应积分
	private double discount;//手续费折扣
	private String memo;//备注
	
	
	public int getVipRate() {
		return vipRate;
	}
	public void setVipRate(int vipRate) {
		this.vipRate = vipRate;
	}
	public int getJifen() {
		return jifen;
	}
	public void setJifen(int jifen) {
		this.jifen = jifen;
	}
	public double getDiscount() {
		return discount;
	}
	public void setDiscount(double discount) {
		this.discount = discount;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	
}
