package com.world.model.loan.entity;

import com.world.model.entity.SysEnum;

public enum LoanRecordStatus implements SysEnum{

	no(0, "-", "#000000"),
	Returning(1, "还款中", "orange"), 
	hasEnd(2, "已还清", "green"),
	forceRepay(3, "需平仓", "red"),
	forceSuccess(4, "平仓还款", "green");
	
	private LoanRecordStatus(int key, String value , String color) {
		this.key = key;
		this.value = value;
		this.color = color;
	}
	public int key;
	public String value;
	public String color;
	public String getColor() {
		return color;
	}
	public int getKey() {
		return key;
	}
	public String getValue() {
		return value;
	}

}
