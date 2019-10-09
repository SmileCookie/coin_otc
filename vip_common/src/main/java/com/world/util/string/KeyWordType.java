package com.world.util.string;

import com.world.model.entity.SysEnum;

/*****
 * 联合登录方式的枚举类
 * @author Administrator
 *
 */
public enum KeyWordType implements SysEnum{
	
	nick(1 , "用户名") , 
	domain(2 , "推荐人二级域名") , 
	; 
	
	private KeyWordType(int key, String value) {
		this.key = key;
		this.value = value;
	}
	public int key;
	public String value;
	
	public int getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

}
