package com.world.model.enums;

import com.world.model.entity.SysEnum;

public enum CoinChargeStatus implements SysEnum{
	UNCONFIRM(0, "等待确认"),
	FAIL(1, "失败"),
	SUCCESS(2, "确认成功"),
	CANCEL(3, "取消");



	private CoinChargeStatus(int key, String value) {
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
