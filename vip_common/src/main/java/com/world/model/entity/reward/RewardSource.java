package com.world.model.entity.reward;

import com.world.model.entity.SysEnum;

/*******
 * 记录来源  此枚举类列举所有的信誉、币来源相关的操作
 * @author Administrator
 *
 */
public enum RewardSource implements SysEnum{
	PhoneCertification(1,"手机认证",0,0),
	EmailCertification(2,"邮箱认证",0,0),
	Report(3,"举报",0,0),
	Suggest(4,"提建议",0,0),
	recommendUserPhoneCertification(5,"推荐用户手机认证",0,0),
	Register(6,"注册",0,0),
	RecommendRegister(7,"推荐注册",0,15),
	Recharge(8,"用户充值",0,5),
	RecommendRecharge(9,"推荐用户充值",0,0),
	ActivityRecharge(10,"充值活动",0,0),
	;
	
	private RewardSource(int key, String value, long btc, double rmb) {
		this.key = key;
		this.value = value;
		this.btc = btc;
		this.rmb = rmb;
	}
	
	private RewardSource() {}

	private int key;
	private String value;
	private long btc;//默认赠送的比特币
	private double rmb;//默认赠送的人民币
	
	public long getBtc() {
		return btc;
	}

	public void setBtc(long btc) {
		this.btc = btc;
	}
	
	public double getRmb() {
		return rmb;
	}

	public void setRmb(double rmb) {
		this.rmb = rmb;
	}

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
}
