package com.world.model.loan.entity;

import com.world.model.entity.SysEnum;

/***
 * 默认设置的限制枚举类型
 */
public enum DefaultLimitType implements SysEnum{

	p2pOutLimit(1 , "p2pOutLimit"), //放贷的限额
	p2pOutRate(2 , "p2pOutRate"),   //放贷的利率
	p2pMinLoan(3, "p2pMinLoan");    //借贷的最小额度
	
	private DefaultLimitType(int key, String value) {
		this.key = key;
		this.value = value;
	}

	private int key;
	private String value;

	@Override
	public int getKey() {
		return key;
	}

	@Override
	public String getValue() {
		return value;
	}
}
