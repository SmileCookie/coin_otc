package com.world.model.entity.msg;

import com.world.model.entity.SysEnum;

/******
 * 提示消息类别
 * @author Administrator
 *
 */
public enum AlarmType implements SysEnum{
	
	fundInsideRemind(1, "资金站内信提醒"),
	importantRemind(2, "重要站内信提醒"),
	otherInsideRemind(5, "其他站内信提醒"),
	;
	
	private AlarmType(int key, String value) {
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
