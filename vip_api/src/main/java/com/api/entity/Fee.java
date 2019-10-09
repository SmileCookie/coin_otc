package com.api.entity;

import java.math.BigDecimal;

/**
 * 手续费用
 * @author guosj
 */
public class Fee {
	
	private int id;
	/**
	 * 用户ID
	 */
	private String userId;
	/**
	 * 费用类型(1、交易手续费，2、借贷手续费，3、提现手续费)
	 */
	private int type;
	/**
	 * 货币类型(CNY、BTC、LTC、ETH、DAO)
	 */
	private String currency;
	/**
	 * 手续费金额
	 */
	private BigDecimal amount;
	/**
	 * 创建时间
	 */
	private long time;
	
	public Fee(){}
	public Fee(String userId, int type, String currency, BigDecimal amount, long time){
		this.userId = userId;
		this.type = type;
		this.currency = currency;
		this.amount = amount;
		this.time = time;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
}
