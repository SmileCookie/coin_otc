package com.world.model.financial.entity;

import com.world.model.entity.SysEnum;

public enum AccountType implements SysEnum{
	
	charge(1 , "充值账户"),
	withdraw(3 , "提现账户"),
	daily(4 , "日常开支"),
	reserve(2 , "储备账户"),
	other(5 , "其他")
	;
	
	private AccountType(int key, String value) {
		this.key = key;
		this.value = value;
	}

	private int key;
	private String value;
	
	public int getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

}
