package com.world.model.entity.user;

import com.world.model.entity.SysEnum;
import com.world.util.CommonUtil;

import java.util.Map;

/****
 * 登录验证类型
 * @author micheal cao
 *
 */
public enum LoginAuthenType implements SysEnum {
	PASSWORD(1, "密码") ,
	PASSWORD_GOOGLE(2 , "密码+Google验证码"),
	PASSWORD_DIFFERENT_PLACE_SMS_OR_EMAIL(3 , "密码+异地登录验证（短信/邮件）"),
	PASSWORD_GOOGLE_DIFFERENT_PLACE_SMS_OR_EMAIL(4 , "密码+Google验证码+异地登录验证（短信/邮件）"),
	;

	private LoginAuthenType(int key, String value) {
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

	public static final Map<Integer, String> MAP = CommonUtil.enumToMap(values());
}
