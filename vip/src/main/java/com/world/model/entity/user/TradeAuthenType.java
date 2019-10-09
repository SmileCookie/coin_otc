package com.world.model.entity.user;

import com.world.model.entity.SysEnum;
import com.world.util.CommonUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/****
 * 交易验证类型
 * @author micheal cao
 *
 */
public enum TradeAuthenType implements SysEnum {
	NO_TRADE_PASSWORD(1, "永不输入资金密码") ,
	NO_TRADE_PASSWORD_FOR_6H(2 , "6小时内免输资金密码"),
	TRADE_PASSWORD(3 , "每次交易均验证资金密码")
	;

	private TradeAuthenType(int key, String value) {
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
