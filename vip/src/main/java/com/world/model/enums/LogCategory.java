package com.world.model.enums;

import com.world.model.entity.SysEnum;

import java.util.LinkedHashMap;
import java.util.Map;

public enum LogCategory implements SysEnum{

	OTHER_PLACE_LOGIN(1, "异地登录"),
	OTHER_PLACE_LOGIN_SWITCH(2, "异地登录验证开关"),
	USE_SAFE_PWD_SWITCH(3, "交易使用安全密码开关"),
	LOGIN_WITH_GOOGLE_CODE_SWITCH(4, "Google登录验证开关"),
	WITHDRAW_WITH_GOOGLE_CODE_SWITCH(5, "Google提现验证开关"),
	WITHDRAW_WITH_SMS_SWITCH(6, "短信提现验证开关"),
	RECEIVE_DEAL_SMS(7, "充值/提现短信提醒"),
	WEB_LOGIN_NOTICE_APP(8, "网页登录APP提醒"),
	LOGIN_AUTHEN_SETTING(9, "登录验证设置"),
	TRADE_AUTHEN_SETTING(10, "交易验证设置"),
	WITHDRAW_AUTHEN_SETTING(11, "提现验证设置"),
	RECEIVE_DEAL_EMAIL(12, "充值/提现邮件提醒"),
	RECEIVE_DEAL_PUSH(13, "充值/提现APP推送提醒"),
	LOGIN_ERROR_PWD_TOO_MANY_TIMES(14, "登录密码输入错误次数过多"),
	MODIFY_LOGIN_PWD(15, "修改登录密码"),
	MODIFY_SAFE_PWD(16, "修改资金安全密码"),
    WITHDRAW_ADDRESS_AUTHEN_SETTING(17, "提现地址验证设置"),

	LOGOUT(99, "退出登录");

	public static Map<Integer, String> MAP = null;
	static {
		LogCategory[] arr = values();
		MAP = new LinkedHashMap<>(arr.length);
		for(LogCategory logCategory: arr){
			MAP.put(logCategory.getKey(), logCategory.getValue());
		}
	}

	private LogCategory(int key, String value) {
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
