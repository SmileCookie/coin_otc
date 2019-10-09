package com.world.model.loan.entity;

import com.world.model.entity.SysEnum;

public enum RepayOfQiStatus implements SysEnum{
	no(0, "未还"), 
	hasRepay(1 , "已还"), 
	yanshi(2 , "延时还"),	
	yuqiyihuan(3 , "逾期已还"), 
	yuqiweihuan(4 , "逾期未还");
	
	private RepayOfQiStatus(int key, String value) {
		this.key = key;
		this.value = value;
	}
	public int key;
	public String value;
	public int getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

}