package com.world.model.entity.user;

import com.world.model.entity.SysEnum;
import com.world.util.CommonUtil;

import java.util.Map;

/****
 * 提现地址验证类型
 * @author buxianguan
 *
 */
public enum WithdrawAddressAuthenType implements SysEnum {
    ORIGINAL(1, "初级模式"),
    SECURITY(2 , "安全模式");

	private WithdrawAddressAuthenType(int key, String value) {
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
