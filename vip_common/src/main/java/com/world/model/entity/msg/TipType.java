package com.world.model.entity.msg;

import com.world.model.entity.SysEnum;

/******
 * 十星网提示消息类别
 * @author Administrator
 *
 */
  
public enum TipType implements SysEnum{
	
	registerSuc(1,"恭喜您注册成为本站的会员", null, MsgType.SYSTEM_MSG),
	
	bandPhoneSuc(2,"系统提醒：您的手机认证成功", AlarmType.importantRemind, MsgType.SYSTEM_MSG),
	bandEmailSuc(3,"系统提醒：您的邮箱认证成功", AlarmType.importantRemind, MsgType.SYSTEM_MSG),
	
	withdrawSuccess(6,"系统提醒：您的提现成功", AlarmType.fundInsideRemind, MsgType.SYSTEM_MSG),
	withdrawFail(7,"系统提醒：您的提现未成功", AlarmType.fundInsideRemind, MsgType.SYSTEM_MSG),
	withdrawFailAfterSuc(8,"系统提醒：您的提现未成功", AlarmType.fundInsideRemind, MsgType.SYSTEM_MSG),
	
	recommendHasReply(10,"系统提醒：您的建议已经回复", null, MsgType.SYSTEM_MSG),
	askHasReply(11,"系统提醒：您的提问已经回复", null, MsgType.SYSTEM_MSG),
	
	chargeSuccess(12,"系统提醒：您的充值成功", null, MsgType.MONEY_MSG),
	chargeFaild(13,"系统提醒：您的充值失败", null, MsgType.MONEY_MSG),
	btcWithdrawSuccess(15 , "系统提醒：您的比特币提现成功", null, MsgType.MONEY_MSG),
	btcRechargeSuccess(16 , "系统提醒：您的比特币充值成功", null, MsgType.MONEY_MSG),
	btcBuySuccess(17 , "系统提醒：您的比特币买入成功", null, MsgType.MONEY_MSG),
	btcSellSuccess(18 , "系统提醒：您的比特币卖出成功", null, MsgType.MONEY_MSG),

	ltcWithdrawSuccess(30 , "系统提醒：您的莱特币提现成功", null, MsgType.MONEY_MSG),
	ltcRechargeSuccess(31 , "系统提醒：您的莱特币充值成功", null, MsgType.MONEY_MSG),
	ltcBuySuccess(32 , "系统提醒：您的莱特币买入成功", null, MsgType.MONEY_MSG),
	ltcSellSuccess(33 , "系统提醒：您的莱特币卖出成功", null, MsgType.MONEY_MSG),
	
	loginSuccess(34, "安全提醒，您已经登陆成功", null, MsgType.SECURITY_MSG),
	
	ethWithdrawSuccess(40 , "系统提醒：您的以太币提现成功", null, MsgType.MONEY_MSG),
	ethRechargeSuccess(41 , "系统提醒：您的以太币充值成功", null, MsgType.MONEY_MSG),
	ethBuySuccess(42 , "系统提醒：您的以太币买入成功", null, MsgType.MONEY_MSG),
	ethSellSuccess(43 , "系统提醒：您的以太币卖出成功", null, MsgType.MONEY_MSG),
	
	daoWithdrawSuccess(44 , "系统提醒：您的DAO币提现成功", null, MsgType.MONEY_MSG),
	daoRechargeSuccess(45 , "系统提醒：您的DAO币充值成功", null, MsgType.MONEY_MSG),
	daoBuySuccess(46 , "系统提醒：您的DAO币买入成功", null, MsgType.MONEY_MSG),
	daoSellSuccess(47 , "系统提醒：您的DAO币卖出成功", null, MsgType.MONEY_MSG),
	
	etcWithdrawSuccess(48 , "系统提醒：您的ETC提现成功", null, MsgType.MONEY_MSG),
	etcRechargeSuccess(49 , "系统提醒：您的ETC充值成功", null, MsgType.MONEY_MSG),
	etcBuySuccess(50 , "系统提醒：您的ETC买入成功", null, MsgType.MONEY_MSG),
	etcSellSuccess(51 , "系统提醒：您的ETC卖出成功", null, MsgType.MONEY_MSG),
	
	commonTospecialPass(20, "恭喜您成功成为专业版用户", null),
	commonTospecialUnPass(21, "申请成为专业版用户没有通过", null),
	specialToguestPass(22, "恭喜您成功成为贵宾版用户", null),
	specialToguestUnPass(23, "申请成为贵宾版用户没有通过", null),
	specialToCancel(24, "您的账户已成功恢复大众版", null),
	
	btcTransIn(101, "您有一笔BTC金额已转入", null, MsgType.MONEY_MSG),
	btcTransOut(102, "您有一笔BTC金额已转出", null, MsgType.MONEY_MSG),
	
	ltcTransIn(201, "您有一笔LTC金额已转入", null, MsgType.MONEY_MSG),
	ltcTransOut(202, "您有一笔LTC金额已转出", null, MsgType.MONEY_MSG)
	;
	 
	private TipType(int key, String value, AlarmType alarmType) { 
		this.key = key;
		this.value = value;
		this.alarmType = alarmType;
	}
	private TipType(int key, String value, AlarmType alarmType, MsgType msgType) { 
		this.key = key;
		this.value = value;
		this.alarmType = alarmType;
		this.msgType = msgType;
	}
	private int key;
	private String value;
	private AlarmType alarmType;
	private MsgType msgType;
	
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public AlarmType getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(AlarmType alarmType) {
		this.alarmType = alarmType;
	}
	public MsgType getMsgType() {
		return msgType;
	}
	public void setMsgType(MsgType msgType) {
		this.msgType = msgType;
	}
}
