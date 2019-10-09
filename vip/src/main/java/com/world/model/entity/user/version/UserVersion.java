package com.world.model.entity.user.version;

import com.world.model.entity.SysEnum;

/****
 * 用户版本
 * @author Administrator
 *
 */
public enum UserVersion implements SysEnum {
	common(0 , "大众版") , 
	specialty(1 , "专业版"),
	vip1(2 , "贵宾版");

	private UserVersion(int key, String value) {
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
