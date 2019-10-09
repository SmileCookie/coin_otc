package com.world.model.entity.pay.fee;

public enum FeeType {
	rmbIn(1 , "人民币充值"),
	rmbOut(2 , "人民币提现"),
	btcIn(3 , "比特币充值"),
	btcOut(4 , "比特币提币"),
	ltcIn(3 , "莱特币充值"),
	ltcOut(4 , "莱特币提币"),
	trade(5 , "交易"),
	loan(6 , "借贷"),
	dogeIn(8, "狗币充值"),
	dogeOut(9, "狗币提币"),
	ethIn(10, "以太坊充值"),
	ethOut(11, "以太坊提币"),
	ethcIn(12, "Ethereum Classic充值"),
	ethcOut(13, "Ethereum Classic提币")
	;
	
	private FeeType(int key, String value) {
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
