package com.world.controller.combined;

import java.util.EnumSet;
import java.util.Iterator;

import com.world.model.entity.EnumUtils;
import com.world.model.entity.SysEnum;

/*****
 * 注册的方式
 * @author Administrator
 *
 */
public enum RegType implements SysEnum{
	
	email(1 , "email", "邮箱注册", "") , 
	mobile(2 , "mobile", "手机注册", "") , 
	qq(3 , "qq", "QQ", "") , 
	wb(4 , "wb", "微博", "") , 
	; 
	
	private RegType(int key, String value, String des, String domain) {
		this.key = key;
		this.value = value;
		this.des = des;
		this.domain = domain;//请求外网的域名
	}
	public int key;
	public String value;
	public String des;
	public String domain;
	
	public int getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public String getDes() {
		return des;
	}

	public String getDomain() {
		return domain;
	}

	@SuppressWarnings("unchecked")
	public static RegType getEnumByName(String name){
		EnumSet<RegType> types = EnumUtils.getAll(RegType.class);
		Iterator<RegType> it = types.iterator();
		while (it.hasNext()) {
			RegType regType = (RegType) it.next();
			if(regType.getValue().equals(name)){
				return regType;
			}
		}
		return null;
	}
}
