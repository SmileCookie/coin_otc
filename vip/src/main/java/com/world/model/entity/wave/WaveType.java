package com.world.model.entity.wave;

import com.world.model.entity.SysEnum;

public enum WaveType implements SysEnum{
	
	btc_autoCashToUser(1 , "免审额度内自动提BTC"),
	ltc_autoCashToUser(3 , "免审额度内自动提LTC"),
	tipsMobelNumber1(2 , "免审提币短信提醒手机号");

	private WaveType(int key, String value) {
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
