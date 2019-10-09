package com.world.model.entity.msg;

import com.world.model.entity.SysEnum;

/******
 * 提示消息类别
 * @author Administrator
 *
 */
public enum MsgType implements SysEnum{
	
	SYSTEM_MSG(1, "系统消息"),
	SECURITY_MSG(2, "安全提醒"),
	MONEY_MSG(3, "资金消息"),
	OTHER_MSG(4, "其他")
	;
	
	private MsgType(int key, String value) {
		this.key = key;
		this.value = value;
	}
	
	private int key;
	private String value;
	
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
