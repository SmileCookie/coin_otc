package com.world.model.enums;

import com.world.model.entity.SysEnum;

import java.util.LinkedHashMap;
import java.util.Map;

public enum LoginStatus implements SysEnum{

	UNLOGIN(0, "未登录"),
	HAS_LOGIN(1, "正常登录"),
	NEED_GOOGLE_AUTHEN(2, "需要谷歌验证"),
	NEED_SMS_EMAIL_AUTHEN(3, "需要短信或邮件验证"),
	NEED_TWO_AUTHEN(4, "需要二次验证");

	private LoginStatus(int key, String value) {
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
