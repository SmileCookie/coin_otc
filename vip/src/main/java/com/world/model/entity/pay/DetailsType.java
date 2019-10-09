package com.world.model.entity.pay;

import com.world.model.entity.SysEnum;

/****
 * 交易方式
 * @author Administrator
 *
 */
public enum DetailsType implements SysEnum{
	//0 提现 1 充值 2 买入 3 卖出 4 获赠 5手续费 6推荐人提成 7系统充值,8购买比特权,9卖出比特权 10.买入比特权剩余
	cash(0,"提现", 2),
	charge(1,"充值", 1),
	;
	
	private int id;
	private String value;
	private int key;
	private int inOut;//BTC是否收入支出    0无关联不变  1.收入  2.支出
	
	private DetailsType(int id, String value, int inOut) {
		this.id = id;
		this.key = id;
		this.value = value;
		this.inOut = inOut;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public int getInOut() {
		return inOut;
	}

	public int getKey() {
		return key;
	}
}
