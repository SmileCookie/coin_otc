package com.world.model.entity.user;

import com.world.model.entity.SysEnum;
import com.world.util.CommonUtil;

import java.util.Map;

/****
 * 提现验证类型
 * @author micheal cao
 *
 */
public enum WithdrawAuthenType implements SysEnum {
	TRADE_PASSWORD_NO_SET(0, "设置资金密码") ,
	TRADE_PASSWORD_SMS_OR_EMAIL_CODE(1, "资金密码+短信/邮件验证码") ,
	TRADE_PASSWORD_GOOGLE_CODE(2 , "资金密码+Google验证码"),
	TRADE_PASSWORD_SMS_OR_EMAIL_GOOGLE_CODE(3 , "资金密码+短信/邮件验证码+Google验证码")
	;

	private WithdrawAuthenType(int key, String value) {
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
