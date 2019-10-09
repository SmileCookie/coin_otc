package com.world.model.entity.btc;

import com.world.model.entity.SysEnum;

public enum ChargeFinancialStatus implements SysEnum{
	no(0 , "无需处理"),
	wait(1 , "待处理"),
	hasBuy(2 , "已购买"),
	noBuy(3 , "未购买"),
	waitBuy(4 , "预定购买")
	;
	
	private ChargeFinancialStatus(int key, String value) {
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
