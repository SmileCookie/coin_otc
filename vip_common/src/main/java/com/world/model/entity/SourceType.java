package com.world.model.entity;

import java.util.Map;

/**
 * 来源类型
 */
public enum SourceType implements SysEnum{

	WEB(8, "网页"),
	APP(5, "手机APP"),
	API(6, "API");

	public static Map<Integer, String> MAP = EnumUtils.enumToMap(values());

	private SourceType(int key, String value) {
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
