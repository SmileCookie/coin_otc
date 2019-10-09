package com.api.common;

import net.sf.json.JSONObject;

public enum BillType{
	
	recharge(1, "充值", 1),
	download(2, "提现", 2),
	sysRecharge(7, "系统充值", 1),
	sysDeduct(8, "系统扣除", 2),

	recommendReward(15 , "奖励", 1),
	
	buy(20, "买入", 1),
	sell(21, "卖出", 2),
	transReward(22, "交易奖励", 1),
	
	exchangeIn(30, "购买交易币", 2),
	exchangeOut(31, "售出交易币", 1),

	errorDeductReturn(50, "资金退回", 1),
	transferOut(55, "资金转出", 2),
	transferIn(56, "资金转入", 1),
	
	fromP2pIn(101 , "借入" , 1),
	outToP2p(102 , "借出" , 2),
	repaymentOutToP2p(103 , "还款" , 2),
	repaymentFromP2pIn(104 , "收款" , 1),
	repayInterestOutToP2p(105 , "还息" , 2),
	repayInterestFromP2pIn(106 , "收益" , 1),
	
	Register(107 , "注册奖励" , 1),
	RecommendRegister(108 , "推荐注册奖励" , 1),
	;
	private BillType(int key, String value, int inout) {
		this.key = key;
		this.value = value;
		this.inout = inout;
	}

	private int key;
	private String value;
	private int inout;//0无关联不变  1.收入  2.支出
	
	public int getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public int getInout() {
		return inout;
	}

	public void setInout(int inout) {
		this.inout = inout;
	}

	public static JSONObject getObjByType(BillType type){
		JSONObject obj = new JSONObject();
		obj.put("key", type.getKey());
		obj.put("value", type.getValue());
		return obj;
	}
}
