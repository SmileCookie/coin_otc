package com.world.model.loan.entity;

import com.world.model.entity.SysEnum;

public enum Repayment implements SysEnum{

	interestByDay(1 , "按日付息到期还款"),
	daoQiHuanBx(2 , "到期还本还息");
	
	private int key;
	private String value;
	private Repayment(int key, String value) {
		this.key = key;
		this.value = value;
	}
	public int getKey() {
		return key;
	}
	public String getValue() {
		return value;
	}
}
