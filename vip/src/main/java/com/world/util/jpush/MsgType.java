package com.world.util.jpush;

import java.util.EnumSet;
import java.util.Iterator;

import com.world.model.entity.SysEnum;

public enum MsgType implements SysEnum{
	
	broadcast(1,"广播信息"),
	priceRemind(2,"价格提醒"),
	entrustRemind(3,"委托交易提醒"),
	abnormalLogin(4,"更换登陆设备提醒"),
	loginRemind(5,"登陆平台提醒"),
	downloadOrUploadRemind(6,"充值提现成功提醒"),
	successOrFailToRealNameAuthentication(7,"实名认证成功/失败"),
	downloadAppReceiveEth(8,"下载APP领取以太币"),
	liquidationRiskRemind(9,"平仓风险提醒");
	
	private MsgType(int key,String description) {
		this.key = key;
		this.description = description;
	}

	private int key;
	private String description;

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static EnumSet<MsgType> getAll(){
		return EnumSet.allOf(MsgType.class);
	}
	
	public static MsgType getPlatformByKey(int Key){
		Iterator<MsgType> it= getAll().iterator();
		
		while(it.hasNext()){
			MsgType method = it.next();
			if(method.getKey() == Key){
				return method;
			}
		}
		return null;
	}

	public String getValue() {
		// TODO Auto-generated method stub
		return description;
	}
	
}
