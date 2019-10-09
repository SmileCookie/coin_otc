package com.yc.entity.msg;

import com.yc.prop.MsgSysProp;

public enum MsgType {

	email(1,"邮件",MsgSysProp.everyDayUserMaxEmailNum,MsgSysProp.everyEmailSendMaxTimes),
	sms(2,"短信",MsgSysProp.everyDayUserMaxSmsNum,MsgSysProp.everySmsSendMaxTimes),
	speech(3, "语音", MsgSysProp.everyDayUserMaxSpeechNum, MsgSysProp.everySpeechSendMaxTimes),
	weixin(4, "微信", MsgSysProp.everyDayUserMaxSpeechNum, MsgSysProp.everySmsSendMaxTimes);
	
	private MsgType(int key, String value,long maxNum,int maxSendTimes) {
		this.key = key;
		this.value = value;
		this.maxNum = maxNum;
		this.maxSendTimes=maxSendTimes;
	}
	
	private int key;
	private String value;
	private long maxNum;//每天每用户最大的发送量  防止刷服务
	private int maxSendTimes;//每条消息最多发送的次数
	
	private Object lock;
	

	public Object getLock() {
		return lock;
	}
	public void setLock(Object lock) {
		this.lock = lock;
	}
	public int getMaxSendTimes() {
		return maxSendTimes;
	}
	public void setMaxSendTimes(int maxSendTimes) {
		this.maxSendTimes = maxSendTimes;
	}
	public long getMaxNum() {
		return maxNum;
	}
	public void setMaxNum(long maxNum) {
		this.maxNum = maxNum;
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
