package com.world.model.entity.user.authen;

import com.world.model.entity.SysEnum;

/**
 * 认证类型
 */
public enum AuditType implements SysEnum{
	individual(1 , "个人用户"),
	corporate(2 , "企业用户");

	private int key;
	private String value;

	private AuditType(int key, String value) {
		this.key = key;
		this.value = value;
	}

	public int getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
