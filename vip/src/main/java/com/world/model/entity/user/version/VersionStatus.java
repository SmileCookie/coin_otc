package com.world.model.entity.user.version;

import com.world.model.entity.SysEnum;

public enum VersionStatus implements SysEnum{
	
	commonToCancel(10, "用户要求取消"),
	commonTospecial_wait(11, "等待审核"),
	commonTospecial_pass(12, "通过审核"),
	commonTospecial_unpass(13, "不通过审核"),
	
	specialToguest_wait(16, "等待审核"),
	specialToguest_pass(17, "通过审核"),
	specialToguest_unpass(18, "不通过审核");
	
	private VersionStatus(int key, String value) {
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
