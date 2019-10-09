package com.world.model.entity.bill;

import com.alibaba.fastjson.JSONObject;
import com.world.model.entity.SysEnum;

public enum BillType implements SysEnum{
	
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
	
	/*start by flym 20170710 ICO兑换使用*/
	icoExchangePlacingIn(61, "ICO配售兑入", 1),
	icoExchangePlacingOut(62, "ICO配售兑出", 2),
	icoExchangeApplyIn(63, "ICO申购兑入", 1),
	icoExchangeApplyOut(64, "ICO申购兑出", 2),
	/*交易手续费转换购买GBC回购使用*/
	transFeeToICO(71, "手续费回购充值", 1),
	/*end*/
	fromP2pIn(101 , "借入" , 1),
	outToP2p(102 , "借出" , 2),
	repaymentOutToP2p(103 , "还款" , 2),
	repaymentFromP2pIn(104 , "收款" , 1),
	repayInterestOutToP2p(105 , "还息" , 2),
	repayInterestFromP2pIn(106 , "收益" , 1),


	/*start by chendi 20171206 内部及外部调整金额*/
	InternalAdjustmentPositive (901, "内部调账正", 1),
	InternalAdjustmentNegative (902, "内部调账负", 2),
	ExternalAdjustmentPositive (903, "外部调账正", 1),
	ExternalAdjustmentNegative (904, "外部调账负", 2),
	/*end*/



	Register(107 , "注册奖励" , 1),
	RecommendRegister(108 , "推荐注册奖励" , 1),
	/*start by xzhang 20171028 比特币分叉需要将时点余额分发到用户的BTG账户。(兼容其他币)*/
	sysDistribute(201, "系统分发", 1),
	/*end*/

	/* add by buxianguan 20171128 GBC回购一期手续费转移回购账户使用*/
    backCapitalInAccount(210, "手续费转入回购账户", 1),
    backCapitalOutAccount(211, "手续费转出回购账户", 2),

	/*start by xzhang 20171111 抽奖获取*/
	luckyIn(221, "活动奖励", 1),
	luckyDouble(222, "奖励翻倍", 1),
	/*end*/

    /* add by buxianguan 20180319 内部账户之间转移*/
    InnerAccountIn(230, "内部账户转入", 1),
    InnerAccountOut(231, "内部账户转出", 2),
	/* add by gkl 20190214 币币账户划至我的钱包*/
	bibiToWalletIn(1220, "币币账户划至我的钱包", 1),
	bibiToWalletOut(2110, "币币账户划至我的钱包", 2),
	;
	
	/**
	 * 获取收支类型1收入，2支出
	 * @return
	 */
	public static int giveExpType(int type) {
		if(1 == type || 7 == type || 15 == type || 20 == type || 22 == type || 31 == type || 61 == type || 63 == type || 71== type) {
			return 1;
			/*start by xzhang 20171028 新增 201 == type JYPT1.1.5.1-开发-后端-billType新增账单类型*/
		} else if (50 == type || 56 == type || 101 == type || 104 == type || 106 == type || 107 == type || 108 == type || 201 == type|| 221 == type|| 222 == type) {
			/*end*/
			return 1;
		}else if (901 == type || 903 == type || 210 == type || 230 == type) {
			/*end*/
			return 1;
		} else if (2 == type || 8 == type || 21 == type || 30 == type || 55 == type || 102 == type || 103 == type || 105 == type) {
			return 2;
		} else if (62 == type || 64 == type || 902 == type || 904 == type || 211 == type || 231 == type) {
			return 2;
		} else if (5110 == type || 5120 == type || 5130 == type || 5301 == type || 5371 == type) {
			/*5110, 5120, 5130, 5210, 5220, 5230, 5301*/
			return 2;
		} else if (5210 == type || 5220 == type || 5230 == type || 5360 == type || 5370 == type) {
			return 1;
		} else if (5310 == type || 5320 == type || 5330 == type || 5372 == type || 5386 == type) {
			return 1;
		} else if (5350 == type || 5360 == type || 5370 == type || 5385 == type) {
			return 1;
		}
		return 0;
	}
	
	
	
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
