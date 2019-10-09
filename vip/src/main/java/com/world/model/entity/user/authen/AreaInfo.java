package com.world.model.entity.user.authen;

import com.world.model.entity.SysEnum;

/****
 * 用户版本
 * @author Administrator
 *
 */
public enum AreaInfo implements SysEnum {
	dalu(1 , "大陆地区") , 
	gangao(2 , "港澳地区"),
	taiwan(3 , "台湾地区"),
	haiwai(4, "海外地区");

	private AreaInfo(int key, String value) {
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
